package app.vaygram.ui;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public final class VayCoachOverlay extends FrameLayout {
    public interface Callback {
        void onNext();
        void onSkip();
    }

    private final View target;
    private final RectF targetRect = new RectF();
    private final Paint scrimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final LinearLayout card;
    private final int[] targetLocation = new int[2];
    private final int[] overlayLocation = new int[2];
    private float lastCardTargetCenterY = Float.NaN;

    private VayCoachOverlay(
            Activity activity,
            View target,
            String step,
            String title,
            String body,
            String nextLabel,
            String skipLabel,
            Callback callback
    ) {
        super(activity);
        this.target = target;

        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setClickable(true);
        setFocusable(true);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);

        scrimPaint.setColor(0xD9000000);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(AndroidUtilities.dp(2));
        borderPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        borderPaint.setShadowLayer(
                AndroidUtilities.dp(12),
                0,
                0,
                Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)
        );

        card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        int cardPadding = AndroidUtilities.dp(18);
        card.setPadding(cardPadding, cardPadding, cardPadding, AndroidUtilities.dp(12));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        background.setCornerRadius(AndroidUtilities.dp(20));
        background.setStroke(
                AndroidUtilities.dp(1),
                Theme.getColor(Theme.key_divider)
        );
        card.setBackground(background);
        card.setElevation(AndroidUtilities.dp(12));

        TextView stepView = new TextView(activity);
        stepView.setText(step);
        stepView.setTextSize(12);
        stepView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        card.addView(stepView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        TextView titleView = new TextView(activity);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = AndroidUtilities.dp(6);
        card.addView(titleView, titleParams);

        TextView bodyView = new TextView(activity);
        bodyView.setText(body);
        bodyView.setTextSize(15);
        bodyView.setLineSpacing(AndroidUtilities.dp(2), 1f);
        bodyView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        bodyParams.topMargin = AndroidUtilities.dp(8);
        card.addView(bodyView, bodyParams);

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        TextView skip = actionText(activity, skipLabel, false);
        skip.setOnClickListener(v -> {
            detach();
            callback.onSkip();
        });
        actions.addView(skip);

        TextView next = actionText(activity, nextLabel, true);
        next.setOnClickListener(v -> {
            detach();
            callback.onNext();
        });
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                AndroidUtilities.dp(48)
        );
        nextParams.leftMargin = AndroidUtilities.dp(8);
        actions.addView(next, nextParams);

        LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        actionsParams.topMargin = AndroidUtilities.dp(8);
        card.addView(actions, actionsParams);

        addView(card, new FrameLayout.LayoutParams(
                Math.min(AndroidUtilities.dp(360), AndroidUtilities.displaySize.x - AndroidUtilities.dp(32)),
                LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT
        ));
    }

    private TextView actionText(Activity activity, String text, boolean primary) {
        TextView view = new TextView(activity);
        view.setText(text);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(14);
        view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        view.setTextColor(Theme.getColor(
                primary
                        ? Theme.key_windowBackgroundWhiteBlueText
                        : Theme.key_windowBackgroundWhiteGrayText2
        ));
        view.setPadding(
                AndroidUtilities.dp(12),
                0,
                AndroidUtilities.dp(12),
                0
        );
        return view;
    }

    public static VayCoachOverlay show(
            Activity activity,
            View target,
            String step,
            String title,
            String body,
            String nextLabel,
            String skipLabel,
            Callback callback
    ) {
        ViewGroup root = activity.findViewById(android.R.id.content);
        VayCoachOverlay overlay = new VayCoachOverlay(
                activity,
                target,
                step,
                title,
                body,
                nextLabel,
                skipLabel,
                callback
        );
        root.addView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        overlay.post(() -> {
            overlay.updateTargetRect();
            overlay.positionCard();
            overlay.invalidate();
        });
        return overlay;
    }

    public void detach() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        updateTargetRect();
        if (target != null
                && target.isShown()
                && targetRect.contains(event.getX(), event.getY())
                && !isInsideCard(event.getX(), event.getY())) {
            // Let the real highlighted control receive the gesture instead of
            // simulating a click. This preserves pressed/ripple/accessibility
            // behavior while the rest of the screen stays blocked by the coach.
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean isInsideCard(float x, float y) {
        return x >= card.getLeft()
                && x <= card.getRight()
                && y >= card.getTop()
                && y <= card.getBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateTargetRect();

        canvas.drawRect(0, 0, getWidth(), getHeight(), scrimPaint);
        float radius = AndroidUtilities.dp(16);
        canvas.drawRoundRect(targetRect, radius, radius, clearPaint);
        canvas.drawRoundRect(targetRect, radius, radius, borderPaint);

        float centerY = targetRect.centerY();
        if (Float.isNaN(lastCardTargetCenterY)
                || Math.abs(centerY - lastCardTargetCenterY) > AndroidUtilities.dp(2)) {
            lastCardTargetCenterY = centerY;
            post(this::positionCard);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(this::positionCard);
    }

    private void updateTargetRect() {
        if (target == null || !target.isShown()) {
            float width = Math.min(getWidth() - AndroidUtilities.dp(48), AndroidUtilities.dp(280));
            float left = (getWidth() - width) / 2f;
            float top = Math.max(AndroidUtilities.dp(72), getHeight() * 0.20f);
            targetRect.set(left, top, left + width, top + AndroidUtilities.dp(72));
            return;
        }

        getLocationOnScreen(overlayLocation);
        target.getLocationOnScreen(targetLocation);

        float pad = AndroidUtilities.dp(8);
        float left = targetLocation[0] - overlayLocation[0] - pad;
        float top = targetLocation[1] - overlayLocation[1] - pad;
        float right = left + target.getWidth() + pad * 2f;
        float bottom = top + target.getHeight() + pad * 2f;

        targetRect.set(
                Math.max(AndroidUtilities.dp(8), left),
                Math.max(AndroidUtilities.dp(8), top),
                Math.min(getWidth() - AndroidUtilities.dp(8), right),
                Math.min(getHeight() - AndroidUtilities.dp(8), bottom)
        );
    }

    private void positionCard() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        updateTargetRect();

        int width = Math.min(
                AndroidUtilities.dp(360),
                getWidth() - AndroidUtilities.dp(32)
        );
        card.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST)
        );
        int cardHeight = card.getMeasuredHeight();

        int left = (getWidth() - width) / 2;
        int gap = AndroidUtilities.dp(18);
        int top;

        if (targetRect.centerY() > getHeight() / 2f) {
            top = Math.round(targetRect.top) - cardHeight - gap;
        } else {
            top = Math.round(targetRect.bottom) + gap;
        }

        top = Math.max(
                AndroidUtilities.dp(16),
                Math.min(top, getHeight() - cardHeight - AndroidUtilities.dp(16))
        );

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
        params.width = width;
        params.leftMargin = left;
        params.topMargin = top;
        card.setLayoutParams(params);
    }
}

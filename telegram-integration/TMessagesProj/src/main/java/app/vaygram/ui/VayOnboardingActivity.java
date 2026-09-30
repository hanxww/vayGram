package app.vaygram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

import app.vaygram.core.settings.VayVisibilityLevel;

public final class VayOnboardingActivity extends BaseFragment {
    private int page;
    private VayVisibilityLevel selectedLevel = VayVisibilityLevel.BASIC;
    private boolean handedOffToCoach;

    private TextView stepView;
    private TextView titleView;
    private TextView bodyView;
    private LinearLayout modeContainer;
    private TextView backButton;
    private TextView nextButton;
    private OnboardingArtView artView;

    public static void maybePresent(BaseFragment host) {
        if (host == null
                || host.getParentActivity() == null
                || host.getVisibleDialog() != null
                || VayOnboardingState.isCompleted()
                || !VayOnboardingState.beginPresentation()) {
            return;
        }

        boolean presented = host.presentFragment(new VayOnboardingActivity());
        if (!presented) {
            VayOnboardingState.endPresentation();
        }
    }

    @Override
    public View createView(Context context) {
        selectedLevel = VayOnboardingState.preferredLevel();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("vayGram");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    skipOnboarding();
                }
            }
        });

        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        int horizontal = AndroidUtilities.dp(24);
        content.setPadding(horizontal, AndroidUtilities.dp(18), horizontal, AndroidUtilities.dp(24));

        artView = new OnboardingArtView(context);
        content.addView(artView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(230)
        ));

        stepView = new TextView(context);
        stepView.setTextSize(12);
        stepView.setGravity(Gravity.CENTER);
        stepView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        content.addView(stepView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        titleView = new TextView(context);
        titleView.setTextSize(28);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = AndroidUtilities.dp(10);
        content.addView(titleView, titleParams);

        bodyView = new TextView(context);
        bodyView.setTextSize(16);
        bodyView.setGravity(Gravity.CENTER);
        bodyView.setLineSpacing(AndroidUtilities.dp(3), 1f);
        bodyView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        bodyParams.topMargin = AndroidUtilities.dp(12);
        content.addView(bodyView, bodyParams);

        modeContainer = new LinearLayout(context);
        modeContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams modeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        modeParams.topMargin = AndroidUtilities.dp(20);
        content.addView(modeContainer, modeParams);

        View spacer = new View(context);
        content.addView(spacer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);

        TextView skip = button(context, LocaleController.getString(R.string.vay_onboarding_skip), false);
        skip.setOnClickListener(v -> skipOnboarding());
        buttons.addView(skip, new LinearLayout.LayoutParams(
                0,
                AndroidUtilities.dp(52),
                1f
        ));

        backButton = button(context, LocaleController.getString(R.string.vay_onboarding_back), false);
        backButton.setOnClickListener(v -> {
            if (page > 0) {
                page--;
                updatePage();
            }
        });
        buttons.addView(backButton, new LinearLayout.LayoutParams(
                0,
                AndroidUtilities.dp(52),
                1f
        ));

        nextButton = button(context, LocaleController.getString(R.string.vay_onboarding_continue), true);
        nextButton.setOnClickListener(v -> {
            if (page < 3) {
                page++;
                updatePage();
            } else {
                startGuidedTour();
            }
        });
        buttons.addView(nextButton, new LinearLayout.LayoutParams(
                0,
                AndroidUtilities.dp(52),
                1.25f
        ));

        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.topMargin = AndroidUtilities.dp(18);
        content.addView(buttons, buttonsParams);

        root.addView(content, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        fragmentView = root;
        updatePage();
        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        if (!handedOffToCoach && !VayOnboardingState.isCompleted()) {
            VayOnboardingState.endPresentation();
        }
        super.onFragmentDestroy();
    }

    private void updatePage() {
        stepView.setText(LocaleController.formatString(
                R.string.vay_onboarding_step,
                page + 1,
                4
        ));

        modeContainer.removeAllViews();
        modeContainer.setVisibility(page == 1 ? View.VISIBLE : View.GONE);

        if (page == 0) {
            titleView.setText(LocaleController.getString(R.string.vay_onboarding_title));
            bodyView.setText(LocaleController.getString(R.string.vay_onboarding_body));
        } else if (page == 1) {
            titleView.setText(LocaleController.getString(R.string.vay_onboarding_choose_level));
            bodyView.setText(LocaleController.getString(R.string.vay_onboarding_choose_level_body));
            addModeOption(VayVisibilityLevel.BASIC, R.string.vay_onboarding_basic);
            addModeOption(VayVisibilityLevel.ADVANCED, R.string.vay_onboarding_advanced);
            addModeOption(VayVisibilityLevel.INSANE, R.string.vay_onboarding_insane);
        } else if (page == 2) {
            titleView.setText(LocaleController.getString(R.string.vay_onboarding_scopes_title));
            bodyView.setText(LocaleController.getString(R.string.vay_onboarding_scopes_body));
        } else {
            titleView.setText(LocaleController.getString(R.string.vay_onboarding_preview_title));
            bodyView.setText(LocaleController.getString(R.string.vay_onboarding_preview_body));
        }

        backButton.setVisibility(page == 0 ? View.INVISIBLE : View.VISIBLE);
        nextButton.setText(LocaleController.getString(
                page == 3
                        ? R.string.vay_onboarding_start_tour
                        : R.string.vay_onboarding_continue
        ));
        artView.setPage(page);
    }

    private void addModeOption(VayVisibilityLevel level, int titleRes) {
        TextView view = new TextView(getContext());
        view.setText(LocaleController.getString(titleRes));
        view.setTextSize(17);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        view.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));

        int padding = AndroidUtilities.dp(18);
        view.setPadding(padding, 0, padding, 0);
        view.setBackground(modeBackground(level == selectedLevel));
        view.setOnClickListener(v -> {
            selectedLevel = level;
            updatePage();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AndroidUtilities.dp(54)
        );
        params.bottomMargin = AndroidUtilities.dp(8);
        modeContainer.addView(view, params);
    }

    private GradientDrawable modeBackground(boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(AndroidUtilities.dp(16));
        drawable.setColor(Theme.getColor(
                selected
                        ? Theme.key_windowBackgroundWhiteBlueText
                        : Theme.key_windowBackgroundGray
        ));
        if (selected) {
            drawable.setAlpha(38);
            drawable.setStroke(
                    AndroidUtilities.dp(2),
                    Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)
            );
        } else {
            drawable.setStroke(
                    AndroidUtilities.dp(1),
                    Theme.getColor(Theme.key_divider)
            );
        }
        return drawable;
    }

    private TextView button(Context context, String text, boolean primary) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        view.setTextColor(Theme.getColor(
                primary
                        ? Theme.key_windowBackgroundWhiteBlueText
                        : Theme.key_windowBackgroundWhiteGrayText2
        ));
        return view;
    }

    private void skipOnboarding() {
        VayOnboardingState.setPreferredLevel(selectedLevel);
        VayOnboardingState.markCompleted();
        finishFragment();
    }

    private void startGuidedTour() {
        VayOnboardingState.setPreferredLevel(selectedLevel);
        handedOffToCoach = true;
        boolean presented = presentFragment(VaySettingsActivity.forOnboarding(), true);
        if (!presented) {
            handedOffToCoach = false;
            VayOnboardingState.endPresentation();
        }
    }

    private static final class OnboardingArtView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private int page;

        private OnboardingArtView(Context context) {
            super(context);
        }

        void setPage(int page) {
            this.page = page;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            int accent = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
            int surface = Theme.getColor(Theme.key_windowBackgroundGray);

            paint.setColor(surface);
            rect.set(
                    cx - AndroidUtilities.dp(120),
                    cy - AndroidUtilities.dp(82),
                    cx + AndroidUtilities.dp(120),
                    cy + AndroidUtilities.dp(82)
            );
            canvas.drawRoundRect(rect, AndroidUtilities.dp(34), AndroidUtilities.dp(34), paint);

            paint.setColor(accent);
            paint.setAlpha(42);
            canvas.drawCircle(
                    cx + AndroidUtilities.dp((page - 1) * 18),
                    cy - AndroidUtilities.dp(8),
                    AndroidUtilities.dp(64),
                    paint
            );
            paint.setAlpha(255);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(4));
            paint.setColor(accent);

            if (page == 0) {
                rect.set(cx - AndroidUtilities.dp(44), cy - AndroidUtilities.dp(44),
                        cx + AndroidUtilities.dp(44), cy + AndroidUtilities.dp(44));
                canvas.drawRoundRect(rect, AndroidUtilities.dp(24), AndroidUtilities.dp(24), paint);
                canvas.drawLine(cx - AndroidUtilities.dp(24), cy,
                        cx - AndroidUtilities.dp(6), cy + AndroidUtilities.dp(24), paint);
                canvas.drawLine(cx - AndroidUtilities.dp(6), cy + AndroidUtilities.dp(24),
                        cx + AndroidUtilities.dp(30), cy - AndroidUtilities.dp(34), paint);
            } else if (page == 1) {
                for (int i = 0; i < 3; i++) {
                    float top = cy - AndroidUtilities.dp(46) + AndroidUtilities.dp(i * 34);
                    rect.set(cx - AndroidUtilities.dp(70), top,
                            cx + AndroidUtilities.dp(70), top + AndroidUtilities.dp(22));
                    canvas.drawRoundRect(rect, AndroidUtilities.dp(11), AndroidUtilities.dp(11), paint);
                }
            } else if (page == 2) {
                float y = cy - AndroidUtilities.dp(34);
                for (int i = 0; i < 3; i++) {
                    canvas.drawCircle(cx, y + AndroidUtilities.dp(i * 34), AndroidUtilities.dp(10), paint);
                    if (i < 2) {
                        canvas.drawLine(cx, y + AndroidUtilities.dp(i * 34 + 10),
                                cx, y + AndroidUtilities.dp(i * 34 + 24), paint);
                    }
                }
            } else {
                rect.set(cx - AndroidUtilities.dp(76), cy - AndroidUtilities.dp(50),
                        cx + AndroidUtilities.dp(76), cy + AndroidUtilities.dp(50));
                canvas.drawRoundRect(rect, AndroidUtilities.dp(20), AndroidUtilities.dp(20), paint);
                canvas.drawCircle(cx - AndroidUtilities.dp(42), cy - AndroidUtilities.dp(8), AndroidUtilities.dp(16), paint);
                canvas.drawLine(cx - AndroidUtilities.dp(14), cy - AndroidUtilities.dp(12),
                        cx + AndroidUtilities.dp(50), cy - AndroidUtilities.dp(12), paint);
                canvas.drawLine(cx - AndroidUtilities.dp(14), cy + AndroidUtilities.dp(12),
                        cx + AndroidUtilities.dp(32), cy + AndroidUtilities.dp(12), paint);
            }

            paint.setStyle(Paint.Style.FILL);
        }
    }
}

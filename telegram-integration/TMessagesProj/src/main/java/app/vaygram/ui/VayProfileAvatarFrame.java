package app.vaygram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;

import app.vaygram.core.theme.VayThemeTokens;
import app.vaygram.telegram.VayProfileAppearance;
import app.vaygram.theme.VayThemeBridge;

public class VayProfileAvatarFrame extends FrameLayout {
    private final int account;
    private final boolean ownProfile;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    public VayProfileAvatarFrame(Context context, int account, boolean ownProfile) {
        super(context);
        this.account = account;
        this.ownProfile = ownProfile;
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!ownProfile) {
            super.dispatchDraw(canvas);
            return;
        }

        int accent = VayThemeBridge.color(VayThemeTokens.ACCENT_PRIMARY);
        float inset = AndroidUtilities.dp(2);
        rect.set(inset, inset, getWidth() - inset, getHeight() - inset);
        float radius = VayProfileAppearance.avatarRadiusForSize(
                account,
                Math.min(getWidth(), getHeight())
        );

        if (VayProfileAppearance.avatarGlow(account)) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(3));
            paint.setColor(accent);
            paint.setShadowLayer(AndroidUtilities.dp(12), 0, 0, accent);
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.clearShadowLayer();
        }

        // Never suppress Telegram's avatar container here. Hiding only the
        // children keeps Telegram's original header geometry reserved and
        // creates a large blank header on real devices.
        super.dispatchDraw(canvas);

        if (VayProfileAppearance.statusRing(account)) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(2));
            paint.setColor(accent);
            canvas.drawRoundRect(rect, radius, radius, paint);
        }

        paint.setStyle(Paint.Style.FILL);
    }
}

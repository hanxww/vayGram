package app.vaygram.theme;

import android.graphics.drawable.GradientDrawable;

import app.vaygram.core.theme.VayGradientSpec;

public final class VayGradientBridge {
    private VayGradientBridge() {}

    public static void apply(
            GradientDrawable drawable,
            VayGradientSpec spec,
            float cornerRadiusPx,
            int alpha
    ) {
        drawable.setOrientation(orientation(spec.getAngleDegrees()));
        drawable.setColors(new int[]{spec.getStartColor(), spec.getEndColor()});
        drawable.setCornerRadius(cornerRadiusPx);
        drawable.setAlpha(Math.max(0, Math.min(255, alpha)));
    }

    public static GradientDrawable.Orientation orientation(int angleDegrees) {
        int normalized = ((angleDegrees % 360) + 360) % 360;
        switch (normalized) {
            case 45:
                return GradientDrawable.Orientation.BL_TR;
            case 90:
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case 135:
                return GradientDrawable.Orientation.BR_TL;
            case 180:
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case 225:
                return GradientDrawable.Orientation.TR_BL;
            case 270:
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case 315:
                return GradientDrawable.Orientation.TL_BR;
            case 0:
            default:
                return GradientDrawable.Orientation.LEFT_RIGHT;
        }
    }
}

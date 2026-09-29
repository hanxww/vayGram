package app.vaygram.core.theme;

public final class VayThemeGradients {
    public static final String NAVIGATION_BOTTOM = "navigation.bottom";

    private VayThemeGradients() {}

    public static VayGradientSpec navigationDefaults(int startColor, int endColor) {
        return new VayGradientSpec(
                NAVIGATION_BOTTOM,
                false,
                startColor,
                endColor,
                0
        );
    }
}

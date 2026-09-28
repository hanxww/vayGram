package app.vaygram.telegram;

import app.vaygram.core.settings.VayDefaults;

public final class VayAppearance {
    private static volatile boolean amoledSurfacesEnabled;
    private static volatile boolean compactModeEnabled;

    private VayAppearance() {}

    static void setAmoledSurfacesEnabled(boolean enabled) {
        amoledSurfacesEnabled = enabled;
    }

    static void setCompactModeEnabled(boolean enabled) {
        compactModeEnabled = enabled;
    }

    public static boolean isCompactModeEnabled() {
        return compactModeEnabled;
    }

    public static float chatBubbleRadiusDp() {
        return VayTelegram.settings().get(VayDefaults.CHAT_BUBBLE_RADIUS);
    }

    public static float chatMessageSpacingDp() {
        float value = VayTelegram.settings().get(VayDefaults.CHAT_MESSAGE_SPACING);
        return compactModeEnabled ? 0f : value;
    }

    public static int dialogRowHeightDp() {
        int value = VayTelegram.settings().get(VayDefaults.DIALOG_ROW_HEIGHT);
        return compactModeEnabled ? Math.min(value, 60) : value;
    }

    public static float avatarSizeDp() {
        float value = VayTelegram.settings().get(VayDefaults.AVATAR_SIZE);
        return compactModeEnabled ? Math.min(value, 46f) : value;
    }

    public static float avatarRoundnessPercent() {
        return VayTelegram.settings().get(VayDefaults.AVATAR_RADIUS);
    }

    public static float dialogAvatarSizeDp(float telegramBaseSizeDp) {
        float deltaFromTelegramDefault = avatarSizeDp() - 54f;
        float requested = telegramBaseSizeDp + deltaFromTelegramDefault;
        float maxForRow = Math.max(28f, dialogRowHeightDp() - 12f);
        return Math.max(28f, Math.min(requested, maxForRow));
    }

    public static int dialogMessagePaddingStartDp(int avatarStartDp) {
        return Math.max(52, Math.round(avatarStartDp + dialogAvatarSizeDp(52f) + 9f));
    }

    public static float dialogAvatarRadiusDp(float telegramBaseSizeDp) {
        return dialogAvatarSizeDp(telegramBaseSizeDp) * avatarRoundnessPercent() / 100f;
    }

    public static int bottomNavigationHeightDp() {
        return effectiveBottomNavigationHeightDp(
                VayTelegram.settings().get(VayDefaults.NAV_HEIGHT)
        );
    }

    static int effectiveBottomNavigationHeightDp(int configuredHeightDp) {
        return compactModeEnabled ? Math.min(configuredHeightDp, 52) : configuredHeightDp;
    }

    public static int bottomNavigationHeightWithMarginsDp() {
        return bottomNavigationHeightDp() + 16;
    }

    public static int bottomNavigationFloatingOffsetDp() {
        return bottomNavigationHeightDp() + 8;
    }

    public static boolean showBottomNavigationLabels() {
        return !compactModeEnabled
                && VayTelegram.settings().get(VayDefaults.NAV_SHOW_LABELS);
    }

    public static float animationScale() {
        return VayTelegram.settings().get(VayDefaults.MOTION_SCALE);
    }

    public static long animationDurationMs(long baseDurationMs) {
        if (baseDurationMs <= 0) {
            return 1L;
        }
        return Math.max(1L, Math.round(baseDurationMs * animationScale()));
    }

    public static boolean useAmoledSurfaces() {
        return amoledSurfacesEnabled;
    }
}

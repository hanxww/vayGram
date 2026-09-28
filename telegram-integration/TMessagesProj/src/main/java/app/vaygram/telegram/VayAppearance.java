package app.vaygram.telegram;

import app.vaygram.core.settings.VayDefaults;

public final class VayAppearance {
    private VayAppearance() {}

    public static float chatBubbleRadiusDp() {
        return VayTelegram.settings().get(VayDefaults.CHAT_BUBBLE_RADIUS);
    }

    public static float chatMessageSpacingDp() {
        return VayTelegram.settings().get(VayDefaults.CHAT_MESSAGE_SPACING);
    }

    public static int dialogRowHeightDp() {
        return VayTelegram.settings().get(VayDefaults.DIALOG_ROW_HEIGHT);
    }

    public static float avatarSizeDp() {
        return VayTelegram.settings().get(VayDefaults.AVATAR_SIZE);
    }

    public static float avatarRoundnessPercent() {
        return VayTelegram.settings().get(VayDefaults.AVATAR_RADIUS);
    }

    public static float dialogAvatarSizeDp() {
        float requested = avatarSizeDp();
        float maxForRow = Math.max(28f, dialogRowHeightDp() - 12f);
        return Math.max(28f, Math.min(requested, maxForRow));
    }

    public static int dialogMessagePaddingStartDp(int avatarStartDp) {
        return Math.max(52, Math.round(avatarStartDp + dialogAvatarSizeDp() + 9f));
    }

    public static float dialogAvatarRadiusDp() {
        return dialogAvatarSizeDp() * avatarRoundnessPercent() / 100f;
    }

    public static int bottomNavigationHeightDp() {
        return VayTelegram.settings().get(VayDefaults.NAV_HEIGHT);
    }

    public static boolean showBottomNavigationLabels() {
        return VayTelegram.settings().get(VayDefaults.NAV_SHOW_LABELS);
    }

    public static float animationScale() {
        return VayTelegram.settings().get(VayDefaults.MOTION_SCALE);
    }

    public static boolean useAmoledSurfaces() {
        return VayTelegram.settings().get(VayDefaults.THEME_AMOLED);
    }
}

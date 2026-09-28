package app.vaygram.telegram;

import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VaySetting;

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

    public static float chatBubbleRadiusDp(int account, long dialogId) {
        return resolvedForChat(VayDefaults.CHAT_BUBBLE_RADIUS, account, dialogId);
    }

    public static float chatMessageSpacingDp() {
        float value = VayTelegram.settings().get(VayDefaults.CHAT_MESSAGE_SPACING);
        return compactModeEnabled ? 0f : value;
    }

    public static float chatMessageSpacingDp(int account, long dialogId) {
        float value = resolvedForChat(VayDefaults.CHAT_MESSAGE_SPACING, account, dialogId);
        return compactModeEnabled ? 0f : value;
    }

    public static int dialogRowHeightDp() {
        int value = VayTelegram.settings().get(VayDefaults.DIALOG_ROW_HEIGHT);
        return compactModeEnabled ? Math.min(value, 60) : value;
    }

    public static int dialogRowHeightDp(int account, long dialogId) {
        int value = resolvedForChat(VayDefaults.DIALOG_ROW_HEIGHT, account, dialogId);
        return compactModeEnabled ? Math.min(value, 60) : value;
    }

    public static float avatarSizeDp() {
        float value = VayTelegram.settings().get(VayDefaults.AVATAR_SIZE);
        return compactModeEnabled ? Math.min(value, 46f) : value;
    }

    public static float avatarSizeDp(int account, long dialogId) {
        float value = resolvedForChat(VayDefaults.AVATAR_SIZE, account, dialogId);
        return compactModeEnabled ? Math.min(value, 46f) : value;
    }

    public static float avatarRoundnessPercent() {
        return VayTelegram.settings().get(VayDefaults.AVATAR_RADIUS);
    }

    public static float avatarRoundnessPercent(int account, long dialogId) {
        return resolvedForChat(VayDefaults.AVATAR_RADIUS, account, dialogId);
    }

    public static float dialogAvatarSizeDp(float telegramBaseSizeDp) {
        float deltaFromTelegramDefault = avatarSizeDp() - 54f;
        float requested = telegramBaseSizeDp + deltaFromTelegramDefault;
        float maxForRow = Math.max(28f, dialogRowHeightDp() - 12f);
        return Math.max(28f, Math.min(requested, maxForRow));
    }

    public static float dialogAvatarSizeDp(int account, long dialogId, float telegramBaseSizeDp) {
        float deltaFromTelegramDefault = avatarSizeDp(account, dialogId) - 54f;
        float requested = telegramBaseSizeDp + deltaFromTelegramDefault;
        float maxForRow = Math.max(28f, dialogRowHeightDp(account, dialogId) - 12f);
        return Math.max(28f, Math.min(requested, maxForRow));
    }

    public static int dialogMessagePaddingStartDp(int avatarStartDp) {
        return Math.max(52, Math.round(avatarStartDp + dialogAvatarSizeDp(52f) + 9f));
    }

    public static int dialogMessagePaddingStartDp(int account, long dialogId, int avatarStartDp) {
        return Math.max(
                52,
                Math.round(avatarStartDp + dialogAvatarSizeDp(account, dialogId, 52f) + 9f)
        );
    }

    public static float dialogAvatarRadiusDp(float telegramBaseSizeDp) {
        return dialogAvatarSizeDp(telegramBaseSizeDp) * avatarRoundnessPercent() / 100f;
    }

    public static float dialogAvatarRadiusDp(int account, long dialogId, float telegramBaseSizeDp) {
        return dialogAvatarSizeDp(account, dialogId, telegramBaseSizeDp)
                * avatarRoundnessPercent(account, dialogId)
                / 100f;
    }

    public static int bottomNavigationHeightDp() {
        return effectiveBottomNavigationHeightDp(
                VayTelegram.settings().get(VayDefaults.NAV_HEIGHT)
        );
    }

    public static int bottomNavigationHeightDp(int account) {
        return effectiveBottomNavigationHeightDp(
                resolvedForAccount(VayDefaults.NAV_HEIGHT, account)
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

    public static boolean showBottomNavigationLabels(int account) {
        return !compactModeEnabled
                && resolvedForAccount(VayDefaults.NAV_SHOW_LABELS, account);
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

    private static <T> T resolvedForAccount(VaySetting<T> setting, int account) {
        return VayTelegram.settings().getResolved(
                setting,
                VayTelegram.accountScope(account),
                app.vaygram.core.settings.VayScopeKey.GLOBAL
        );
    }

    private static <T> T resolvedForChat(VaySetting<T> setting, int account, long dialogId) {
        if (setting.getScopes().contains(app.vaygram.core.settings.VaySettingScope.CHAT)) {
            return VayTelegram.settings().getResolved(
                    setting,
                    VayTelegram.chatScope(account, dialogId),
                    VayTelegram.accountScope(account)
            );
        }
        if (setting.getScopes().contains(app.vaygram.core.settings.VaySettingScope.ACCOUNT)) {
            return resolvedForAccount(setting, account);
        }
        return VayTelegram.settings().get(setting);
    }
}

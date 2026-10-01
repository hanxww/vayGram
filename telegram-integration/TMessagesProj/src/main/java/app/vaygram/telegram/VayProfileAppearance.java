package app.vaygram.telegram;

import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;

public final class VayProfileAppearance {
    private VayProfileAppearance() {}

    private static <T> T get(int account, VaySetting<T> setting) {
        VayTelegram.ensureInitialized();
        return VayTelegram.settings().getResolved(
                setting,
                VayTelegram.accountScope(account),
                VayScopeKey.GLOBAL
        );
    }

    private static boolean hasExplicitValue(int account, VaySetting<?> setting) {
        VayTelegram.ensureInitialized();
        return VayTelegram.settings().hasStoredValue(setting, VayTelegram.accountScope(account))
                || VayTelegram.settings().hasStoredValue(setting, VayScopeKey.GLOBAL);
    }

    private static boolean telegramVisibleUnlessExplicitlyHidden(
            int account,
            VaySetting<Boolean> setting
    ) {
        return !hasExplicitValue(account, setting) || Boolean.TRUE.equals(get(account, setting));
    }

    /*
     * These three switches still drive the Vay Profile preview/model, but are
     * intentionally not used to hide Telegram's structural profile header.
     * Telegram reserves geometry for the avatar/name/status header, so making
     * those child views INVISIBLE leaves large blank or clipped areas.
     */
    public static boolean showAvatar(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_AVATAR));
    }

    public static boolean showName(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_NAME));
    }

    public static boolean showStatus(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_STATUS));
    }

    public static boolean showUsername(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_USERNAME));
    }

    public static boolean showBio(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_BIO));
    }

    public static boolean showPhone(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_PHONE));
    }

    public static boolean showBirthday(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_BIRTHDAY));
    }

    public static boolean showPersonalChannel(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_PERSONAL_CHANNEL));
    }

    public static boolean showMusic(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_MUSIC));
    }

    public static boolean showMedia(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_SHOW_MEDIA));
    }

    /*
     * Real Telegram rows use opt-out semantics. A Vay block default such as
     * "music=false" must not silently remove Telegram's native row on a fresh
     * install. Only an explicit account/global override may hide it.
     */
    public static boolean showTelegramUsername(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_USERNAME);
    }

    public static boolean showTelegramBio(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_BIO);
    }

    public static boolean showTelegramPhone(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_PHONE);
    }

    public static boolean showTelegramBirthday(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_BIRTHDAY);
    }

    public static boolean showTelegramPersonalChannel(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_PERSONAL_CHANNEL);
    }

    public static boolean showTelegramMusic(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_MUSIC);
    }

    public static boolean showTelegramMedia(int account) {
        return telegramVisibleUnlessExplicitlyHidden(account, VayDefaults.PROFILE_SHOW_MEDIA);
    }

    public static boolean avatarGlow(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_AVATAR_GLOW));
    }

    public static boolean statusRing(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_STATUS_RING));
    }

    public static int avatarRadiusForSize(int account, int avatarSize) {
        Number percent = get(account, VayDefaults.PROFILE_AVATAR_RADIUS);
        float value = percent == null ? 50f : percent.floatValue();
        value = Math.max(0f, Math.min(50f, value));
        return Math.round(avatarSize * value / 100f);
    }
}

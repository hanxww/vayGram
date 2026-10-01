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

    public static boolean avatarGlow(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_AVATAR_GLOW));
    }

    public static boolean statusRing(int account) {
        return Boolean.TRUE.equals(get(account, VayDefaults.PROFILE_STATUS_RING));
    }

    public static int avatarRadiusDp(int account, int avatarSizeDp) {
        Number percent = get(account, VayDefaults.PROFILE_AVATAR_RADIUS);
        float value = percent == null ? 50f : percent.floatValue();
        value = Math.max(0f, Math.min(50f, value));
        return Math.round(avatarSizeDp * value / 100f);
    }
}

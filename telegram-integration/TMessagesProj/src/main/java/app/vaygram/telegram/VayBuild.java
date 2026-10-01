package app.vaygram.telegram;

public final class VayBuild {
    public static final String APP_NAME = "vayGram";
    public static final String PACKAGE_ID = "app.vaygram.messenger";
    public static final String ACCOUNT_TYPE = PACKAGE_ID;

    public static final String CONTACT_PROFILE_MIME =
            "vnd.android.cursor.item/vnd.app.vaygram.messenger.android.profile";
    public static final String CONTACT_CALL_MIME =
            "vnd.android.cursor.item/vnd.app.vaygram.messenger.android.call";
    public static final String CONTACT_VIDEO_CALL_MIME =
            "vnd.android.cursor.item/vnd.app.vaygram.messenger.android.call.video";

    public static final String DEVELOPMENT_CHANNEL = "https://t.me/vayGram_app";
    public static final String SOURCE_REPOSITORY = "https://github.com/hanxww/vayGram";
    public static final String CLIENT_VERSION = "0.1-dev";
    public static final String TELEGRAM_BASE_VERSION = "12.10.5";
    public static final int TELEGRAM_BASE_VERSION_CODE = 7105;
    public static final String TELEGRAM_BASE_COMMIT = "dc780e81ed1261c369c27870e8e0999a1eb0b600";

    private VayBuild() {}
}

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
    public static final String CLIENT_VERSION = "0.1-dev";

    private VayBuild() {}
}

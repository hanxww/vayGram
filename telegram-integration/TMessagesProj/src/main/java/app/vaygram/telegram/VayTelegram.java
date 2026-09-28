package app.vaygram.telegram;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;

import app.vaygram.android.VayAndroid;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;

public final class VayTelegram {
    private static volatile boolean initialized;

    private VayTelegram() {}

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }
        synchronized (VayTelegram.class) {
            if (initialized) {
                return;
            }
            VayAndroid.initialize(ApplicationLoader.applicationContext);
            installTelegramBridges();
            initialized = true;
        }
    }

    private static void installTelegramBridges() {
        VaySettingsEngine engine = VayAndroid.settings();

        // Preserve an existing Telegram appearance choice on the first vayGram run.
        engine.seedIfAbsent(
                VayDefaults.CHAT_BUBBLE_RADIUS,
                VayScopeKey.GLOBAL,
                (float) SharedConfig.bubbleRadius
        );

        syncBubbleRadius(engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));
        syncMainTabsHeight(engine.get(VayDefaults.NAV_HEIGHT));

        engine.addListener(change -> {
            if (!VayScopeKey.GLOBAL.equals(change.getScope())) {
                return;
            }

            String settingId = change.getSettingId();
            if (VayDefaults.CHAT_BUBBLE_RADIUS.getId().equals(settingId)) {
                Object value = change.getNewValue();
                if (value instanceof Number) {
                    syncBubbleRadius(((Number) value).floatValue());
                }
            }

            if (VayDefaults.NAV_HEIGHT.getId().equals(settingId)) {
                Object value = change.getNewValue();
                if (value instanceof Number) {
                    syncMainTabsHeight(((Number) value).intValue());
                }
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(
                        NotificationCenter.updateInterfaces,
                        0
                );
            } else if (VayDefaults.NAV_SHOW_LABELS.getId().equals(settingId)) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(
                        NotificationCenter.updateInterfaces,
                        0
                );
            }

            if (VayDefaults.DIALOG_ROW_HEIGHT.getId().equals(settingId)
                    || VayDefaults.AVATAR_SIZE.getId().equals(settingId)
                    || VayDefaults.AVATAR_RADIUS.getId().equals(settingId)) {
                NotificationCenter.getGlobalInstance().postNotificationName(
                        NotificationCenter.dialogsNeedReload,
                        true
                );
            }
        });
    }

    private static void syncMainTabsHeight(int value) {
        DialogsActivity.vaySetMainTabsHeight(Math.max(48, Math.min(88, value)));
    }

    private static void syncBubbleRadius(float value) {
        int radius = Math.max(0, Math.round(value));
        if (SharedConfig.bubbleRadius == radius) {
            return;
        }

        SharedConfig.bubbleRadius = radius;
        MessagesController.getGlobalMainSettings()
                .edit()
                .putInt("bubbleRadius", radius)
                .apply();
    }

    public static VaySettingsEngine settings() {
        ensureInitialized();
        return VayAndroid.settings();
    }

    public static VaySettingsRegistry registry() {
        ensureInitialized();
        return VayAndroid.registry();
    }
}

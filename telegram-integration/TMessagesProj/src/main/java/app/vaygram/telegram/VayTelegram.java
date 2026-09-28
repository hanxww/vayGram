package app.vaygram.telegram;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.DialogsActivity;

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
        VayAppearance.setCompactModeEnabled(engine.get(VayDefaults.COMPACT_MODE));
        syncMainTabsHeight(VayAppearance.effectiveBottomNavigationHeightDp(
                engine.get(VayDefaults.NAV_HEIGHT)
        ));
        VayAppearance.setAmoledSurfacesEnabled(engine.get(VayDefaults.THEME_AMOLED));

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
                    syncMainTabsHeight(VayAppearance.effectiveBottomNavigationHeightDp(
                            ((Number) value).intValue()
                    ));
                }
                notifyMainTabsAppearanceChanged();
            } else if (VayDefaults.COMPACT_MODE.getId().equals(settingId)) {
                VayAppearance.setCompactModeEnabled(Boolean.TRUE.equals(change.getNewValue()));
                syncMainTabsHeight(VayAppearance.effectiveBottomNavigationHeightDp(
                        engine.get(VayDefaults.NAV_HEIGHT)
                ));
                notifyMainTabsAppearanceChanged();
                NotificationCenter.getGlobalInstance().postNotificationName(
                        NotificationCenter.dialogsNeedReload,
                        true
                );
            } else if (VayDefaults.NAV_SHOW_LABELS.getId().equals(settingId)
                    || VayDefaults.MOTION_SCALE.getId().equals(settingId)) {
                notifyMainTabsAppearanceChanged();
            } else if (VayDefaults.THEME_AMOLED.getId().equals(settingId)) {
                VayAppearance.setAmoledSurfacesEnabled(Boolean.TRUE.equals(change.getNewValue()));
                Theme.refreshThemeColors();
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

    private static void notifyMainTabsAppearanceChanged() {
        for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
            NotificationCenter.getInstance(account).postNotificationName(
                    NotificationCenter.updateInterfaces,
                    0
            );
        }
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

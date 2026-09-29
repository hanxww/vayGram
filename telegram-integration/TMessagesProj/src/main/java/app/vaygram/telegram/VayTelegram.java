package app.vaygram.telegram;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.DialogsActivity;

import app.vaygram.android.VayAndroid;
import app.vaygram.android.settings.VayPresetRepository;
import app.vaygram.android.theme.VayMaterialYouPalette;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;
import app.vaygram.core.theme.VayThemePalette;
import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokenRegistry;
import app.vaygram.theme.VayThemeBridge;

public final class VayTelegram {
    private static volatile boolean initialized;
    private static volatile boolean materialYouSupported;

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
        VayThemeBridge.refreshPalette(VayAndroid.themePalette());
        syncMaterialYou(engine);

        // Preserve an existing Telegram appearance choice on the first vayGram run.
        engine.seedIfAbsent(
                VayDefaults.CHAT_BUBBLE_RADIUS,
                VayScopeKey.GLOBAL,
                (float) SharedConfig.bubbleRadius
        );

        syncBubbleRadius(engine.get(VayDefaults.CHAT_BUBBLE_RADIUS));
        VayAppearance.setCompactModeEnabled(engine.get(VayDefaults.COMPACT_MODE));

        VayScopeKey selectedAccountScope = accountScope(UserConfig.selectedAccount);
        int selectedNavigationHeight = engine.getResolved(
                VayDefaults.NAV_HEIGHT,
                selectedAccountScope,
                VayScopeKey.GLOBAL
        );
        syncMainTabsHeight(VayAppearance.effectiveBottomNavigationHeightDp(
                selectedNavigationHeight
        ));

        VayAppearance.setAmoledSurfacesEnabled(engine.getResolved(
                VayDefaults.THEME_AMOLED,
                selectedAccountScope,
                VayScopeKey.GLOBAL
        ));

        engine.addListener(change -> {
            String settingId = change.getSettingId();
            if (VayDefaults.CHAT_BUBBLE_RADIUS.getId().equals(settingId)
                    && VayScopeKey.GLOBAL.equals(change.getScope())) {
                Object value = change.getNewValue();
                if (value instanceof Number) {
                    syncBubbleRadius(((Number) value).floatValue());
                }
            }

            if (VayDefaults.NAV_HEIGHT.getId().equals(settingId)) {
                notifyMainTabsAppearanceChanged();
            } else if (VayDefaults.COMPACT_MODE.getId().equals(settingId)) {
                VayAppearance.setCompactModeEnabled(Boolean.TRUE.equals(change.getNewValue()));
                VayScopeKey currentAccountScope = accountScope(UserConfig.selectedAccount);
                syncMainTabsHeight(VayAppearance.effectiveBottomNavigationHeightDp(
                        engine.getResolved(
                                VayDefaults.NAV_HEIGHT,
                                currentAccountScope,
                                VayScopeKey.GLOBAL
                        )
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
                VayScopeKey currentAccountScope = accountScope(UserConfig.selectedAccount);
                VayAppearance.setAmoledSurfacesEnabled(engine.getResolved(
                        VayDefaults.THEME_AMOLED,
                        currentAccountScope,
                        VayScopeKey.GLOBAL
                ));
                Theme.refreshThemeColors();
            } else if (VayDefaults.THEME_MATERIAL_YOU.getId().equals(settingId)) {
                syncMaterialYou(engine);
                Theme.refreshThemeColors(false, true);
            } else if (VayDefaults.CHAT_MESSAGE_SPACING.getId().equals(settingId)) {
                notifyMainTabsAppearanceChanged();
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

    private static void syncMaterialYou(VaySettingsEngine engine) {
        VayMaterialYouPalette.Snapshot snapshot = VayMaterialYouPalette.resolve(
                ApplicationLoader.applicationContext
        );
        materialYouSupported = snapshot.isSupported();

        VayScopeKey currentAccountScope = accountScope(UserConfig.selectedAccount);
        boolean enabled = materialYouSupported && engine.getResolved(
                VayDefaults.THEME_MATERIAL_YOU,
                currentAccountScope,
                VayScopeKey.GLOBAL
        );

        VayThemeBridge.refreshMaterialYou(
                snapshot.getLightColors(),
                snapshot.getDarkColors(),
                enabled
        );
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

    public static VayScopeKey accountScope(int account) {
        long userId = UserConfig.getInstance(account).getClientUserId();
        String subjectId = userId != 0
                ? "tg:" + userId
                : "slot:" + account;
        return new VayScopeKey(app.vaygram.core.settings.VaySettingScope.ACCOUNT, subjectId);
    }

    public static VayScopeKey chatScope(int account, long dialogId) {
        return new VayScopeKey(
                app.vaygram.core.settings.VaySettingScope.CHAT,
                accountScope(account).getSubjectId() + ":dialog:" + dialogId
        );
    }

    public static VaySettingsEngine settings() {
        ensureInitialized();
        return VayAndroid.settings();
    }

    public static VaySettingsRegistry registry() {
        ensureInitialized();
        return VayAndroid.registry();
    }

    public static VayPresetRepository presets() {
        ensureInitialized();
        return VayAndroid.presets();
    }

    public static VayThemeTokenRegistry themeTokens() {
        ensureInitialized();
        return VayAndroid.themeTokens();
    }

    public static VayThemePalette themePalette() {
        ensureInitialized();
        return VayAndroid.themePalette();
    }

    public static boolean isMaterialYouSupported() {
        ensureInitialized();
        return materialYouSupported;
    }

    public static boolean isMaterialYouEnabled() {
        ensureInitialized();
        return VayThemeBridge.isMaterialYouEnabled();
    }

    public static void refreshMaterialYou() {
        ensureInitialized();
        syncMaterialYou(VayAndroid.settings());
        Theme.refreshThemeColors(false, true);
    }


    public static void previewThemeColor(VayThemeToken token, int color) {
        themePalette().setColor(token, color);
        applyThemePalette();
    }

    public static void restoreThemeColorPreview(VayThemeToken token, Integer originalOverride) {
        if (originalOverride == null) {
            themePalette().reset(token);
        } else {
            themePalette().setColor(token, originalOverride);
        }
        applyThemePalette();
    }

    public static void commitThemeColor(VayThemeToken token, int color) {
        ensureInitialized();
        VayAndroid.saveThemeColor(token, color);
        applyThemePalette();
    }

    public static void resetThemeColor(VayThemeToken token) {
        ensureInitialized();
        VayAndroid.resetThemeColor(token);
        applyThemePalette();
    }

    public static void resetThemePalette() {
        ensureInitialized();
        VayAndroid.resetThemePalette();
        applyThemePalette();
    }

    private static void applyThemePalette() {
        VayThemeBridge.refreshPalette(VayAndroid.themePalette());
        syncMaterialYou(VayAndroid.settings());
        Theme.refreshThemeColors(false, true);
    }
}

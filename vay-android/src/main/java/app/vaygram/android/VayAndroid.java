package app.vaygram.android;

import android.content.Context;
import android.content.SharedPreferences;

import app.vaygram.android.settings.SharedPreferencesVaySettingsStore;
import app.vaygram.android.settings.VayPresetRepository;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;
import app.vaygram.core.theme.VayThemeTokenRegistry;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayAndroid {
    private static final String PREFS_NAME = "vaygram_settings_v1";
    private static final String PRESETS_PREFS_NAME = "vaygram_presets_v1";

    private static volatile VaySettingsRegistry registry;
    private static volatile VaySettingsEngine settings;
    private static volatile VayPresetRepository presets;
    private static volatile VayThemeTokenRegistry themeTokens;

    private VayAndroid() {}

    public static void initialize(Context context) {
        if (settings != null) {
            return;
        }
        synchronized (VayAndroid.class) {
            if (settings != null) {
                return;
            }
            Context appContext = context.getApplicationContext();
            SharedPreferences preferences = appContext.getSharedPreferences(
                    PREFS_NAME,
                    Context.MODE_PRIVATE
            );
            registry = VayDefaults.createRegistry();
            themeTokens = VayThemeTokens.createRegistry();
            settings = new VaySettingsEngine(
                    registry,
                    new SharedPreferencesVaySettingsStore(preferences)
            );
            presets = new VayPresetRepository(appContext.getSharedPreferences(
                    PRESETS_PREFS_NAME,
                    Context.MODE_PRIVATE
            ));
        }
    }

    public static VaySettingsEngine settings() {
        VaySettingsEngine result = settings;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }

    public static VaySettingsRegistry registry() {
        VaySettingsRegistry result = registry;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }

    public static VayPresetRepository presets() {
        VayPresetRepository result = presets;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }

    public static VayThemeTokenRegistry themeTokens() {
        VayThemeTokenRegistry result = themeTokens;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }
}

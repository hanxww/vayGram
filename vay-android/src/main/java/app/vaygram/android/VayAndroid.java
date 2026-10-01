package app.vaygram.android;

import android.content.Context;
import android.content.SharedPreferences;

import app.vaygram.android.settings.SharedPreferencesVaySettingsStore;
import app.vaygram.android.settings.VayPresetRepository;
import app.vaygram.android.theme.VayGradientRepository;
import app.vaygram.android.theme.VayThemePaletteRepository;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;
import app.vaygram.core.theme.VayThemePalette;
import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokenRegistry;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayAndroid {
    private static final String PREFS_NAME = "vaygram_settings_v1";
    private static final String PRESETS_PREFS_NAME = "vaygram_presets_v1";
    private static final String THEME_PREFS_NAME = "vaygram_theme_palette_v1";
    private static final String GRADIENT_PREFS_NAME = "vaygram_gradients_v1";
    private static final String RUNTIME_PREFS_NAME = "vaygram_runtime_v1";
    private static final String KEY_SAFE_MODE = "safe_mode";

    private static volatile VaySettingsRegistry registry;
    private static volatile VaySettingsEngine settings;
    private static volatile VayPresetRepository presets;
    private static volatile VayThemeTokenRegistry themeTokens;
    private static volatile VayThemePalette themePalette;
    private static volatile VayThemePaletteRepository themePaletteRepository;
    private static volatile VayGradientRepository gradients;
    private static volatile SharedPreferences runtimePreferences;

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
            themePalette = new VayThemePalette(themeTokens);
            themePaletteRepository = new VayThemePaletteRepository(
                    appContext.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE),
                    themeTokens
            );
            themePalette.loadColorOverrides(themePaletteRepository.loadColors());
            gradients = new VayGradientRepository(appContext.getSharedPreferences(
                    GRADIENT_PREFS_NAME,
                    Context.MODE_PRIVATE
            ));
            runtimePreferences = appContext.getSharedPreferences(
                    RUNTIME_PREFS_NAME,
                    Context.MODE_PRIVATE
            );
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

    public static VayThemePalette themePalette() {
        VayThemePalette result = themePalette;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }

    public static VayGradientRepository gradients() {
        VayGradientRepository result = gradients;
        if (result == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return result;
    }

    public static void saveThemeColor(VayThemeToken token, int color) {
        themePalette().setColor(token, color);
        VayThemePaletteRepository repository = themePaletteRepository;
        if (repository == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        repository.saveColor(token, color);
    }

    public static void resetThemeColor(VayThemeToken token) {
        themePalette().reset(token);
        VayThemePaletteRepository repository = themePaletteRepository;
        if (repository == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        repository.resetColor(token);
    }

    public static void resetThemePalette() {
        themePalette().loadColorOverrides(null);
        VayThemePaletteRepository repository = themePaletteRepository;
        if (repository == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        repository.resetAll();
    }

    public static boolean isSafeMode() {
        SharedPreferences preferences = runtimePreferences;
        if (preferences == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        return preferences.getBoolean(KEY_SAFE_MODE, false);
    }

    public static void setSafeMode(boolean enabled) {
        SharedPreferences preferences = runtimePreferences;
        if (preferences == null) {
            throw new IllegalStateException("VayAndroid.initialize(context) must be called first");
        }
        preferences.edit().putBoolean(KEY_SAFE_MODE, enabled).apply();
    }
}

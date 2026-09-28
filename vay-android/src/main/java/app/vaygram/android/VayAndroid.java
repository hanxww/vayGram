package app.vaygram.android;

import android.content.Context;
import android.content.SharedPreferences;

import app.vaygram.android.settings.SharedPreferencesVaySettingsStore;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VaySettingsEngine;
import app.vaygram.core.settings.VaySettingsRegistry;

public final class VayAndroid {
    private static final String PREFS_NAME = "vaygram_settings_v1";

    private static volatile VaySettingsRegistry registry;
    private static volatile VaySettingsEngine settings;

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
            settings = new VaySettingsEngine(
                    registry,
                    new SharedPreferencesVaySettingsStore(preferences)
            );
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
}

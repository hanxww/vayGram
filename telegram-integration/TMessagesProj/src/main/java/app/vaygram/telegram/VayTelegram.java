package app.vaygram.telegram;

import org.telegram.messenger.ApplicationLoader;

import app.vaygram.android.VayAndroid;
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
            initialized = true;
        }
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

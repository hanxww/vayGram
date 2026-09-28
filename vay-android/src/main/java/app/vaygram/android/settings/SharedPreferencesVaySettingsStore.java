package app.vaygram.android.settings;

import android.content.SharedPreferences;

import app.vaygram.core.settings.VaySettingsStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SharedPreferencesVaySettingsStore implements VaySettingsStore {
    private final SharedPreferences preferences;

    public SharedPreferencesVaySettingsStore(SharedPreferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    @Override
    public Object get(String key) {
        return preferences.getAll().get(key);
    }

    @Override
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported SharedPreferences value for " + key + ": "
                            + (value == null ? "null" : value.getClass().getName()));
        }
        editor.apply();
    }

    @Override
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

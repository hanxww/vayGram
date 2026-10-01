package app.vaygram.android.settings;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import app.vaygram.core.settings.VaySettingsPreset;

public final class VayPresetRepository {
    private static final String KEY_PREFIX = "preset:";

    private final SharedPreferences preferences;

    public VayPresetRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void save(VaySettingsPreset preset) {
        preferences.edit()
                .putString(KEY_PREFIX + preset.getName(), VayPresetJson.encode(preset))
                .apply();
    }

    public VaySettingsPreset find(String name) {
        String json = preferences.getString(KEY_PREFIX + name, null);
        if (json == null) {
            return null;
        }
        try {
            return VayPresetJson.decode(json);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public List<VaySettingsPreset> list() {
        Map<String, ?> all = preferences.getAll();
        ArrayList<VaySettingsPreset> presets = new ArrayList<>();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (!entry.getKey().startsWith(KEY_PREFIX) || !(entry.getValue() instanceof String)) {
                continue;
            }
            try {
                presets.add(VayPresetJson.decode((String) entry.getValue()));
            } catch (RuntimeException ignored) {
                // Ignore malformed entries without breaking the settings screen.
            }
        }
        Collections.sort(presets, Comparator.comparing(
                VaySettingsPreset::getName,
                String.CASE_INSENSITIVE_ORDER
        ));
        return presets;
    }

    public int count() {
        return list().size();
    }

    public void replaceAll(List<VaySettingsPreset> presets) {
        SharedPreferences.Editor editor = preferences.edit();
        for (String key : preferences.getAll().keySet()) {
            if (key.startsWith(KEY_PREFIX)) {
                editor.remove(key);
            }
        }
        editor.apply();

        if (presets == null) {
            return;
        }
        for (VaySettingsPreset preset : presets) {
            if (preset != null) {
                save(preset);
            }
        }
    }

    public boolean delete(String name) {
        String key = KEY_PREFIX + name;
        if (!preferences.contains(key)) {
            return false;
        }
        preferences.edit().remove(key).apply();
        return true;
    }
}

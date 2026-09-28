package app.vaygram.core.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VaySettingsRegistry {
    private final Map<String, VaySetting<?>> settings = new LinkedHashMap<>();

    public synchronized <T> VaySetting<T> register(VaySetting<T> setting) {
        if (settings.containsKey(setting.getId())) {
            throw new IllegalStateException("Duplicate setting id: " + setting.getId());
        }
        settings.put(setting.getId(), setting);
        return setting;
    }

    public synchronized VaySetting<?> find(String id) {
        return settings.get(id);
    }

    public synchronized Collection<VaySetting<?>> all() {
        return Collections.unmodifiableCollection(new ArrayList<>(settings.values()));
    }

    public synchronized List<VaySetting<?>> search(String query, VayVisibilityLevel level) {
        List<VaySetting<?>> result = new ArrayList<>();
        for (VaySetting<?> setting : settings.values()) {
            if (setting.getVisibilityLevel().isVisibleAt(level) && setting.matches(query)) {
                result.add(setting);
            }
        }
        return result;
    }

    public synchronized List<VaySetting<?>> byCategory(String category, VayVisibilityLevel level) {
        List<VaySetting<?>> result = new ArrayList<>();
        for (VaySetting<?> setting : settings.values()) {
            if (setting.getVisibilityLevel().isVisibleAt(level)
                    && setting.getCategory().equalsIgnoreCase(category)) {
                result.add(setting);
            }
        }
        return result;
    }

    public synchronized int size() {
        return settings.size();
    }
}

package app.vaygram.core.settings;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InMemoryVaySettingsStore implements VaySettingsStore {
    private final Map<String, Object> values = new LinkedHashMap<>();

    @Override
    public synchronized Object get(String key) {
        return values.get(key);
    }

    @Override
    public synchronized void put(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public synchronized void remove(String key) {
        values.remove(key);
    }

    @Override
    public synchronized Map<String, Object> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
}

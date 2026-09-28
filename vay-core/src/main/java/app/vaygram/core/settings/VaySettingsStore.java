package app.vaygram.core.settings;

import java.util.Map;

public interface VaySettingsStore {
    Object get(String key);
    void put(String key, Object value);
    void remove(String key);
    Map<String, Object> snapshot();
}

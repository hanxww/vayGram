package app.vaygram.core.settings;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class VaySettingsPreset {
    private final String name;
    private final int schemaVersion;
    private final Map<String, Object> values;

    public VaySettingsPreset(String name, int schemaVersion, Map<String, Object> values) {
        this.name = Objects.requireNonNull(name, "name");
        this.schemaVersion = schemaVersion;
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public String getName() {
        return name;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }
}

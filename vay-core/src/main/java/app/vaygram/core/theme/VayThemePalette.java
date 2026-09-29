package app.vaygram.core.theme;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class VayThemePalette {
    private final VayThemeTokenRegistry registry;
    private final Map<String, Integer> colorOverrides = new LinkedHashMap<>();

    public VayThemePalette(VayThemeTokenRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public synchronized Integer getColorOverride(VayThemeToken token) {
        assertColorToken(token);
        return colorOverrides.get(token.getId());
    }

    public synchronized boolean hasColorOverride(VayThemeToken token) {
        assertColorToken(token);
        return colorOverrides.containsKey(token.getId());
    }

    public synchronized void setColor(VayThemeToken token, int color) {
        assertColorToken(token);
        colorOverrides.put(token.getId(), color);
    }

    public synchronized void reset(VayThemeToken token) {
        assertColorToken(token);
        colorOverrides.remove(token.getId());
    }

    public synchronized int countOverrides() {
        return colorOverrides.size();
    }

    public synchronized Map<String, Integer> snapshotColors() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(colorOverrides));
    }

    public synchronized void loadColorOverrides(Map<String, Integer> values) {
        colorOverrides.clear();
        if (values == null) {
            return;
        }
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            VayThemeToken token = registry.find(entry.getKey());
            if (token == null
                    || token.getType() != VayThemeTokenType.COLOR
                    || entry.getValue() == null) {
                continue;
            }
            colorOverrides.put(token.getId(), entry.getValue());
        }
    }

    private void assertColorToken(VayThemeToken token) {
        Objects.requireNonNull(token, "token");
        VayThemeToken registered = registry.find(token.getId());
        if (registered == null) {
            throw new IllegalArgumentException("Unknown vayGram theme token: " + token.getId());
        }
        if (registered.getType() != VayThemeTokenType.COLOR) {
            throw new IllegalArgumentException("Theme token is not a color: " + token.getId());
        }
    }
}

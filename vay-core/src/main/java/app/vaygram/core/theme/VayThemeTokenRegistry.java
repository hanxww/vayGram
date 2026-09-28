package app.vaygram.core.theme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VayThemeTokenRegistry {
    private final Map<String, VayThemeToken> byId = new LinkedHashMap<>();

    public synchronized void register(VayThemeToken token) {
        if (byId.containsKey(token.getId())) {
            throw new IllegalArgumentException("Duplicate vayGram theme token: " + token.getId());
        }
        byId.put(token.getId(), token);
    }

    public synchronized VayThemeToken find(String id) {
        return byId.get(id);
    }

    public synchronized Collection<VayThemeToken> all() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }

    public synchronized int size() {
        return byId.size();
    }

    public synchronized List<VayThemeToken> search(String query) {
        ArrayList<VayThemeToken> result = new ArrayList<>();
        for (VayThemeToken token : byId.values()) {
            if (token.matches(query)) {
                result.add(token);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public synchronized Map<String, List<VayThemeToken>> byCategory() {
        LinkedHashMap<String, List<VayThemeToken>> result = new LinkedHashMap<>();
        for (VayThemeToken token : byId.values()) {
            List<VayThemeToken> category = result.get(token.getCategory());
            if (category == null) {
                category = new ArrayList<>();
                result.put(token.getCategory(), category);
            }
            category.add(token);
        }
        for (Map.Entry<String, List<VayThemeToken>> entry : result.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }
}

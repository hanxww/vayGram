package app.vaygram.android.theme;

import android.content.SharedPreferences;

import java.util.LinkedHashMap;
import java.util.Map;

import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokenRegistry;
import app.vaygram.core.theme.VayThemeTokenType;

public final class VayThemePaletteRepository {
    private static final String COLOR_PREFIX = "color:";

    private final SharedPreferences preferences;
    private final VayThemeTokenRegistry registry;

    public VayThemePaletteRepository(
            SharedPreferences preferences,
            VayThemeTokenRegistry registry
    ) {
        this.preferences = preferences;
        this.registry = registry;
    }

    public Map<String, Integer> loadColors() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            if (!entry.getKey().startsWith(COLOR_PREFIX)
                    || !(entry.getValue() instanceof Integer)) {
                continue;
            }

            String tokenId = entry.getKey().substring(COLOR_PREFIX.length());
            VayThemeToken token = registry.find(tokenId);
            if (token != null && token.getType() == VayThemeTokenType.COLOR) {
                result.put(tokenId, (Integer) entry.getValue());
            }
        }
        return result;
    }

    public void saveColor(VayThemeToken token, int color) {
        preferences.edit()
                .putInt(COLOR_PREFIX + token.getId(), color)
                .apply();
    }

    public void resetColor(VayThemeToken token) {
        preferences.edit()
                .remove(COLOR_PREFIX + token.getId())
                .apply();
    }

    public void resetAll() {
        SharedPreferences.Editor editor = preferences.edit();
        for (String key : preferences.getAll().keySet()) {
            if (key.startsWith(COLOR_PREFIX)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }
}

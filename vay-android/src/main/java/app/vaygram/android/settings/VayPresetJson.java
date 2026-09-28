package app.vaygram.android.settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import app.vaygram.core.settings.VaySettingsPreset;

public final class VayPresetJson {
    private VayPresetJson() {}

    public static String encode(VaySettingsPreset preset) {
        try {
            JSONObject root = new JSONObject();
            root.put("schema", preset.getSchemaVersion());
            root.put("name", preset.getName());
            JSONObject values = new JSONObject();
            for (Map.Entry<String, Object> entry : preset.getValues().entrySet()) {
                values.put(entry.getKey(), entry.getValue());
            }
            root.put("values", values);
            return root.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to encode vayGram preset", e);
        }
    }

    public static VaySettingsPreset decode(String json) {
        try {
            JSONObject root = new JSONObject(json);
            int schema = root.optInt("schema", 1);
            if (schema != 1) {
                throw new IllegalArgumentException("Unsupported vayGram preset schema: " + schema);
            }
            String name = root.optString("name", "Imported preset");
            JSONObject valuesJson = root.getJSONObject("values");
            Map<String, Object> values = new LinkedHashMap<>();
            Iterator<String> keys = valuesJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = valuesJson.get(key);
                if (value != JSONObject.NULL) {
                    values.put(key, value);
                }
            }
            return new VaySettingsPreset(name, schema, values);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid vayGram preset", e);
        }
    }
}

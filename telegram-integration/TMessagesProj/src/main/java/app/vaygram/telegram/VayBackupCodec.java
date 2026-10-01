package app.vaygram.telegram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import app.vaygram.android.settings.VayPresetJson;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySettingsPreset;
import app.vaygram.core.theme.VayGradientSpec;
import app.vaygram.core.theme.VayThemeToken;

/**
 * Portable vayGram-only backup.
 *
 * It deliberately excludes Telegram sessions, chats, messages, phone numbers,
 * account ids and per-chat overrides. Account settings are mapped to the
 * currently selected account when restored on another installation.
 */
public final class VayBackupCodec {
    public static final int SCHEMA = 1;
    private static final String KIND = "vaygram_portable_backup";

    private VayBackupCodec() {}

    public static String encode(int account) {
        try {
            JSONObject root = new JSONObject();
            root.put("kind", KIND);
            root.put("schema", SCHEMA);
            root.put("client_version", VayBuild.CLIENT_VERSION);
            root.put("telegram_data_included", false);
            root.put("chat_overrides_included", false);

            VaySettingsPreset global = VayTelegram.settings().capturePreset(
                    "global",
                    VayScopeKey.GLOBAL,
                    true
            );
            VaySettingsPreset currentAccount = VayTelegram.settings().capturePreset(
                    "current_account",
                    VayTelegram.accountScope(account),
                    true
            );
            root.put("global_settings", new JSONObject(VayPresetJson.encode(global)));
            root.put("account_settings", new JSONObject(VayPresetJson.encode(currentAccount)));

            JSONArray presets = new JSONArray();
            for (VaySettingsPreset preset : VayTelegram.presets().list()) {
                presets.put(new JSONObject(VayPresetJson.encode(preset)));
            }
            root.put("saved_profiles", presets);

            JSONObject palette = new JSONObject();
            for (Map.Entry<String, Integer> entry : VayTelegram.themePalette().snapshotColors().entrySet()) {
                palette.put(entry.getKey(), entry.getValue());
            }
            root.put("palette", palette);

            VayGradientSpec gradient = VayTelegram.storedNavigationGradient();
            if (gradient != null) {
                JSONObject gradientJson = new JSONObject();
                gradientJson.put("enabled", gradient.isEnabled());
                gradientJson.put("start", gradient.getStartColor());
                gradientJson.put("end", gradient.getEndColor());
                gradientJson.put("angle", gradient.getAngleDegrees());
                root.put("navigation_gradient", gradientJson);
            } else {
                root.put("navigation_gradient", JSONObject.NULL);
            }

            return root.toString();
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to encode vayGram backup", e);
        }
    }

    public static RestoreResult restore(int account, String json) {
        try {
            JSONObject root = new JSONObject(json);
            if (!KIND.equals(root.optString("kind"))) {
                throw new IllegalArgumentException("Not a vayGram portable backup");
            }
            int schema = root.optInt("schema", -1);
            if (schema != SCHEMA) {
                throw new IllegalArgumentException("Unsupported vayGram backup schema: " + schema);
            }

            VaySettingsPreset global = VayPresetJson.decode(
                    root.getJSONObject("global_settings").toString()
            );
            VaySettingsPreset accountPreset = VayPresetJson.decode(
                    root.getJSONObject("account_settings").toString()
            );

            int clearedGlobal = VayTelegram.settings().resetAll(VayScopeKey.GLOBAL);
            VayScopeKey accountScope = VayTelegram.accountScope(account);
            int clearedAccount = VayTelegram.settings().resetAll(accountScope);
            int appliedGlobal = VayTelegram.settings().applyPreset(global, VayScopeKey.GLOBAL);
            int appliedAccount = VayTelegram.settings().applyPreset(accountPreset, accountScope);

            JSONArray presets = root.optJSONArray("saved_profiles");
            java.util.ArrayList<VaySettingsPreset> restoredProfiles = new java.util.ArrayList<>();
            if (presets != null) {
                for (int i = 0; i < presets.length(); i++) {
                    JSONObject preset = presets.optJSONObject(i);
                    if (preset == null) {
                        continue;
                    }
                    try {
                        restoredProfiles.add(VayPresetJson.decode(preset.toString()));
                    } catch (RuntimeException ignored) {
                        // Ignore one malformed saved profile without aborting the backup.
                    }
                }
            }
            VayTelegram.presets().replaceAll(restoredProfiles);

            VayTelegram.resetThemePalette();
            int paletteCount = 0;
            JSONObject palette = root.optJSONObject("palette");
            if (palette != null) {
                Iterator<String> keys = palette.keys();
                while (keys.hasNext()) {
                    String id = keys.next();
                    VayThemeToken token = VayTelegram.themeTokens().find(id);
                    if (token == null) {
                        continue;
                    }
                    Object raw = palette.opt(id);
                    if (!(raw instanceof Number)) {
                        continue;
                    }
                    VayTelegram.commitThemeColor(token, ((Number) raw).intValue());
                    paletteCount++;
                }
            }

            if (root.isNull("navigation_gradient")) {
                VayTelegram.resetNavigationGradient();
            } else {
                JSONObject gradient = root.optJSONObject("navigation_gradient");
                if (gradient != null) {
                    VayTelegram.commitNavigationGradient(new VayGradientSpec(
                            app.vaygram.core.theme.VayThemeGradients.NAVIGATION_BOTTOM,
                            gradient.optBoolean("enabled", false),
                            gradient.optInt("start", 0),
                            gradient.optInt("end", 0),
                            gradient.optInt("angle", 0)
                    ));
                }
            }

            return new RestoreResult(
                    clearedGlobal + clearedAccount,
                    appliedGlobal + appliedAccount,
                    restoredProfiles.size(),
                    paletteCount
            );
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid vayGram backup", e);
        }
    }

    public static final class RestoreResult {
        private final int clearedSettings;
        private final int appliedSettings;
        private final int restoredProfiles;
        private final int paletteOverrides;

        RestoreResult(
                int clearedSettings,
                int appliedSettings,
                int restoredProfiles,
                int paletteOverrides
        ) {
            this.clearedSettings = clearedSettings;
            this.appliedSettings = appliedSettings;
            this.restoredProfiles = restoredProfiles;
            this.paletteOverrides = paletteOverrides;
        }

        public int getClearedSettings() { return clearedSettings; }
        public int getAppliedSettings() { return appliedSettings; }
        public int getRestoredProfiles() { return restoredProfiles; }
        public int getPaletteOverrides() { return paletteOverrides; }
    }
}

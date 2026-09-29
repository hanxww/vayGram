package app.vaygram.android.theme;

import android.content.SharedPreferences;

import app.vaygram.core.theme.VayGradientSpec;

public final class VayGradientRepository {
    private static final String PREFIX = "gradient:";

    private final SharedPreferences preferences;

    public VayGradientRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public VayGradientSpec find(String targetId) {
        String base = PREFIX + targetId + ":";
        if (!preferences.contains(base + "enabled")) {
            return null;
        }
        return new VayGradientSpec(
                targetId,
                preferences.getBoolean(base + "enabled", false),
                preferences.getInt(base + "start", 0),
                preferences.getInt(base + "end", 0),
                preferences.getInt(base + "angle", 0)
        );
    }

    public void save(VayGradientSpec spec) {
        String base = PREFIX + spec.getTargetId() + ":";
        preferences.edit()
                .putBoolean(base + "enabled", spec.isEnabled())
                .putInt(base + "start", spec.getStartColor())
                .putInt(base + "end", spec.getEndColor())
                .putInt(base + "angle", spec.getAngleDegrees())
                .apply();
    }

    public void delete(String targetId) {
        String base = PREFIX + targetId + ":";
        preferences.edit()
                .remove(base + "enabled")
                .remove(base + "start")
                .remove(base + "end")
                .remove(base + "angle")
                .apply();
    }
}

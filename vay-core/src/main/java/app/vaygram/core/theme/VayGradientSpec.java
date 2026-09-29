package app.vaygram.core.theme;

import java.util.Objects;

public final class VayGradientSpec {
    private final String targetId;
    private final boolean enabled;
    private final int startColor;
    private final int endColor;
    private final int angleDegrees;

    public VayGradientSpec(
            String targetId,
            boolean enabled,
            int startColor,
            int endColor,
            int angleDegrees
    ) {
        this.targetId = Objects.requireNonNull(targetId, "targetId");
        this.enabled = enabled;
        this.startColor = startColor;
        this.endColor = endColor;
        this.angleDegrees = normalizeAngle(angleDegrees);
    }

    public String getTargetId() { return targetId; }
    public boolean isEnabled() { return enabled; }
    public int getStartColor() { return startColor; }
    public int getEndColor() { return endColor; }
    public int getAngleDegrees() { return angleDegrees; }

    public VayGradientSpec withEnabled(boolean value) {
        return new VayGradientSpec(targetId, value, startColor, endColor, angleDegrees);
    }

    public VayGradientSpec withStartColor(int value) {
        return new VayGradientSpec(targetId, enabled, value, endColor, angleDegrees);
    }

    public VayGradientSpec withEndColor(int value) {
        return new VayGradientSpec(targetId, enabled, startColor, value, angleDegrees);
    }

    public VayGradientSpec withAngleDegrees(int value) {
        return new VayGradientSpec(targetId, enabled, startColor, endColor, value);
    }

    private static int normalizeAngle(int value) {
        int normalized = value % 360;
        if (normalized < 0) normalized += 360;
        return Math.round(normalized / 45f) * 45 % 360;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof VayGradientSpec)) return false;
        VayGradientSpec that = (VayGradientSpec) other;
        return enabled == that.enabled
                && startColor == that.startColor
                && endColor == that.endColor
                && angleDegrees == that.angleDegrees
                && targetId.equals(that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, enabled, startColor, endColor, angleDegrees);
    }
}

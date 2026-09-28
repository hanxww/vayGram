package app.vaygram.core.settings;

import java.util.Objects;

public final class VayScopeKey {
    public static final VayScopeKey GLOBAL = new VayScopeKey(VaySettingScope.GLOBAL, "global");

    private final VaySettingScope scope;
    private final String subjectId;

    public VayScopeKey(VaySettingScope scope, String subjectId) {
        this.scope = Objects.requireNonNull(scope, "scope");
        this.subjectId = Objects.requireNonNull(subjectId, "subjectId");
    }

    public VaySettingScope getScope() { return scope; }
    public String getSubjectId() { return subjectId; }

    public String storagePrefix() {
        return scope.name().toLowerCase() + ":" + subjectId + ":";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof VayScopeKey)) return false;
        VayScopeKey that = (VayScopeKey) other;
        return scope == that.scope && subjectId.equals(that.subjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, subjectId);
    }

    @Override
    public String toString() {
        return scope + "(" + subjectId + ")";
    }
}

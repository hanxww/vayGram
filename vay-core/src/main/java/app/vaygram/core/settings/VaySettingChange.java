package app.vaygram.core.settings;

public final class VaySettingChange {
    private final String settingId;
    private final VayScopeKey scope;
    private final Object oldValue;
    private final Object newValue;
    private final long timestampMs;

    public VaySettingChange(String settingId, VayScopeKey scope, Object oldValue, Object newValue, long timestampMs) {
        this.settingId = settingId;
        this.scope = scope;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestampMs = timestampMs;
    }

    public String getSettingId() { return settingId; }
    public VayScopeKey getScope() { return scope; }
    public Object getOldValue() { return oldValue; }
    public Object getNewValue() { return newValue; }
    public long getTimestampMs() { return timestampMs; }

    @Override
    public String toString() {
        return settingId + ": " + oldValue + " -> " + newValue + " @ " + scope;
    }
}

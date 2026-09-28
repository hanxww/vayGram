package app.vaygram.core.settings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class VaySettingsEngine {
    private static final int MAX_HISTORY = 200;

    private final VaySettingsRegistry registry;
    private final VaySettingsStore store;
    private final Deque<VaySettingChange> undoStack = new ArrayDeque<>();
    private final Deque<VaySettingChange> redoStack = new ArrayDeque<>();
    private final List<VaySettingsListener> listeners = new ArrayList<>();
    private boolean replayingHistory;

    public VaySettingsEngine(VaySettingsRegistry registry, VaySettingsStore store) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.store = Objects.requireNonNull(store, "store");
    }

    public synchronized void addListener(VaySettingsListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    public synchronized void removeListener(VaySettingsListener listener) {
        listeners.remove(listener);
    }

    public synchronized <T> T get(VaySetting<T> setting) {
        return get(setting, VayScopeKey.GLOBAL);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T get(VaySetting<T> setting, VayScopeKey scope) {
        assertScopeAllowed(setting, scope);
        Object stored = store.get(storageKey(setting, scope));
        if (stored == null) return setting.getDefaultValue();
        return setting.normalize((T) stored);
    }

    public synchronized <T> void set(VaySetting<T> setting, T value) {
        set(setting, VayScopeKey.GLOBAL, value);
    }

    public synchronized <T> void set(VaySetting<T> setting, VayScopeKey scope, T value) {
        assertScopeAllowed(setting, scope);
        T normalized = setting.normalize(value);
        T oldValue = get(setting, scope);
        if (Objects.equals(oldValue, normalized)) return;

        store.put(storageKey(setting, scope), normalized);
        VaySettingChange change = new VaySettingChange(
                setting.getId(), scope, oldValue, normalized, System.currentTimeMillis());
        record(change);
        notifyListeners(change);
    }

    public synchronized <T> void reset(VaySetting<T> setting) {
        reset(setting, VayScopeKey.GLOBAL);
    }

    public synchronized <T> void reset(VaySetting<T> setting, VayScopeKey scope) {
        assertScopeAllowed(setting, scope);
        T oldValue = get(setting, scope);
        store.remove(storageKey(setting, scope));
        T newValue = setting.getDefaultValue();

        if (!Objects.equals(oldValue, newValue)) {
            VaySettingChange change = new VaySettingChange(
                    setting.getId(), scope, oldValue, newValue, System.currentTimeMillis());
            record(change);
            notifyListeners(change);
        }
    }

    public synchronized boolean isModified(VaySetting<?> setting) {
        return isModified(setting, VayScopeKey.GLOBAL);
    }

    public synchronized boolean isModified(VaySetting<?> setting, VayScopeKey scope) {
        assertScopeAllowed(setting, scope);
        return !Objects.equals(getUnchecked(setting, scope), setting.getDefaultValue());
    }

    public synchronized int countModified(VayScopeKey scope) {
        int count = 0;
        for (VaySetting<?> setting : registry.all()) {
            if (setting.getScopes().contains(scope.getScope()) && isModified(setting, scope)) {
                count++;
            }
        }
        return count;
    }

    public synchronized int resetAll(VayScopeKey scope) {
        int resetCount = 0;
        for (VaySetting<?> setting : registry.all()) {
            if (!setting.getScopes().contains(scope.getScope()) || !isModified(setting, scope)) {
                continue;
            }
            resetUnchecked(setting, scope);
            resetCount++;
        }
        return resetCount;
    }

    public synchronized boolean canUndo() { return !undoStack.isEmpty(); }
    public synchronized boolean canRedo() { return !redoStack.isEmpty(); }

    public synchronized VaySettingChange undo() {
        if (undoStack.isEmpty()) return null;
        VaySettingChange change = undoStack.pop();
        replay(change, change.getOldValue());
        redoStack.push(change);
        return change;
    }

    public synchronized VaySettingChange redo() {
        if (redoStack.isEmpty()) return null;
        VaySettingChange change = redoStack.pop();
        replay(change, change.getNewValue());
        undoStack.push(change);
        return change;
    }

    public synchronized List<VaySettingChange> recentChanges() {
        return Collections.unmodifiableList(new ArrayList<>(undoStack));
    }

    private void replay(VaySettingChange change, Object value) {
        VaySetting<?> setting = requireSetting(change.getSettingId());
        replayingHistory = true;
        try {
            setUnchecked(setting, change.getScope(), value);
        } finally {
            replayingHistory = false;
        }
    }

    private void record(VaySettingChange change) {
        if (replayingHistory) return;
        undoStack.push(change);
        while (undoStack.size() > MAX_HISTORY) undoStack.removeLast();
        redoStack.clear();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setUnchecked(VaySetting setting, VayScopeKey scope, Object value) {
        assertScopeAllowed(setting, scope);
        Object normalized = setting.normalize(value);
        Object oldValue = get(setting, scope);
        if (Objects.equals(oldValue, normalized)) return;

        store.put(storageKey(setting, scope), normalized);
        notifyListeners(new VaySettingChange(
                setting.getId(), scope, oldValue, normalized, System.currentTimeMillis()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getUnchecked(VaySetting setting, VayScopeKey scope) {
        return get(setting, scope);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void resetUnchecked(VaySetting setting, VayScopeKey scope) {
        reset(setting, scope);
    }

    private VaySetting<?> requireSetting(String id) {
        VaySetting<?> setting = registry.find(id);
        if (setting == null) {
            throw new IllegalStateException("Setting disappeared from registry: " + id);
        }
        return setting;
    }

    private void assertScopeAllowed(VaySetting<?> setting, VayScopeKey scope) {
        if (!setting.getScopes().contains(scope.getScope())) {
            throw new IllegalArgumentException(
                    "Setting " + setting.getId() + " does not support scope " + scope.getScope());
        }
    }

    private String storageKey(VaySetting<?> setting, VayScopeKey scope) {
        return scope.storagePrefix() + setting.getId();
    }

    private void notifyListeners(VaySettingChange change) {
        for (VaySettingsListener listener : new ArrayList<>(listeners)) {
            listener.onSettingChanged(change);
        }
    }
}

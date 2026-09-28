package app.vaygram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import app.vaygram.android.settings.VayPresetJson;
import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingChange;
import app.vaygram.core.settings.VaySettingType;
import app.vaygram.core.settings.VaySettingScope;
import app.vaygram.core.settings.VaySettingsListener;
import app.vaygram.core.settings.VaySettingsPreset;
import app.vaygram.core.settings.VayVisibilityLevel;
import app.vaygram.telegram.VayTelegram;

public final class VaySettingsActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_VALUE = 2;
    private static final int TYPE_PREVIEW = 3;

    private static final int ACTION_UNDO = 1;
    private static final int ACTION_REDO = 2;
    private static final int ACTION_RESET_ALL = 3;
    private static final int ACTION_EXPORT_PRESET = 4;
    private static final int ACTION_IMPORT_PRESET = 5;
    private static final int ACTION_SAVE_PROFILE = 6;
    private static final int ACTION_SAVED_PROFILES = 7;
    private static final int ACTION_OPEN_PARENT_SCOPE = 8;
    private static final int ACTION_OPEN_ACCOUNT_SCOPE = 9;
    private static final int ACTION_SCOPE_INFO = 10;

    private static final int RECENT_LIMIT = 5;

    private final VayScopeKey scopeKey;
    private final VayScopeKey parentScopeKey;
    private final String scopeTitle;

    private RecyclerListView listView;
    private ListAdapter adapter;
    private VayPreviewView previewView;
    private final ArrayList<Row> rows = new ArrayList<>();
    private final VaySettingsListener previewListener = change ->
            AndroidUtilities.runOnUIThread(() -> {
                if (previewView != null) {
                    previewView.invalidate();
                }
            });

    private String query = "";
    private VayVisibilityLevel visibilityLevel = VayVisibilityLevel.ADVANCED;

    public VaySettingsActivity() {
        this(VayScopeKey.GLOBAL, null, "Global");
    }

    public VaySettingsActivity(VayScopeKey scopeKey) {
        this(scopeKey, null, scopeKey == null || VayScopeKey.GLOBAL.equals(scopeKey) ? "Global" : scopeKey.getScope().name());
    }

    private VaySettingsActivity(
            VayScopeKey scopeKey,
            VayScopeKey parentScopeKey,
            String scopeTitle
    ) {
        this.scopeKey = scopeKey == null ? VayScopeKey.GLOBAL : scopeKey;
        this.parentScopeKey = parentScopeKey;
        this.scopeTitle = TextUtils.isEmpty(scopeTitle) ? this.scopeKey.getScope().name() : scopeTitle;
    }

    public static VaySettingsActivity forAccount(int account) {
        return new VaySettingsActivity(
                VayTelegram.accountScope(account),
                VayScopeKey.GLOBAL,
                "This account"
        );
    }

    public static VaySettingsActivity forChat(int account, long dialogId) {
        return new VaySettingsActivity(
                VayTelegram.chatScope(account, dialogId),
                VayTelegram.accountScope(account),
                "This chat"
        );
    }

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();
        VayTelegram.settings().removeListener(previewListener);
        VayTelegram.settings().addListener(previewListener);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(VayScopeKey.GLOBAL.equals(scopeKey)
                ? "vayGram Settings"
                : "vayGram · " + scopeTitle);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenuItem searchItem = actionBar.createMenu()
                .addItem(0, R.drawable.outline_header_search)
                .setIsSearchField(true)
                .setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                    @Override
                    public void onTextChanged(EditText editText) {
                        query = editText.getText().toString();
                        rebuildRows();
                    }
                });
        searchItem.setSearchFieldHint(LocaleController.getString(R.string.Search));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        listView.setAdapter(adapter = new ListAdapter(context));
        listView.setOnItemClickListener((view, position) -> onRowClicked(position));
        listView.setOnItemLongClickListener((view, position) -> onRowLongClicked(view, position));

        fragmentView = listView;
        actionBar.setAdaptiveBackground(listView);
        rebuildRows();
        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        VayTelegram.settings().removeListener(previewListener);
        previewView = null;
        super.onFragmentDestroy();
    }

    private void rebuildRows() {
        rows.clear();

        if (TextUtils.isEmpty(query)) {
            rows.add(Row.header("Live Preview"));
            rows.add(Row.preview());
            addControlRows();
            addRecentRows();
        }

        List<VaySetting<?>> visible = VayTelegram.registry().search(query, visibilityLevel);
        Map<String, List<VaySetting<?>>> grouped = new LinkedHashMap<>();
        for (VaySetting<?> setting : visible) {
            if (!setting.getScopes().contains(scopeKey.getScope())) {
                continue;
            }
            List<VaySetting<?>> category = grouped.get(setting.getCategory());
            if (category == null) {
                category = new ArrayList<>();
                grouped.put(setting.getCategory(), category);
            }
            category.add(setting);
        }

        for (Map.Entry<String, List<VaySetting<?>>> entry : grouped.entrySet()) {
            rows.add(Row.header(entry.getKey()));
            for (VaySetting<?> setting : entry.getValue()) {
                rows.add(Row.setting(setting, false));
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void addControlRows() {
        rows.add(Row.header("Controls"));
        rows.add(Row.mode());
        rows.add(Row.action(ACTION_SCOPE_INFO, "Scope", scopeTitle));

        if (VayScopeKey.GLOBAL.equals(scopeKey)) {
            rows.add(Row.action(
                    ACTION_OPEN_ACCOUNT_SCOPE,
                    "This account",
                    "Account-specific overrides"
            ));
        } else if (parentScopeKey != null) {
            rows.add(Row.action(
                    ACTION_OPEN_PARENT_SCOPE,
                    parentScopeKey.getScope() == VaySettingScope.GLOBAL ? "Global defaults" : "Account defaults",
                    "View inherited settings"
            ));
        }

        if (VayTelegram.settings().canUndo()) {
            VaySettingChange last = firstRecentChange();
            rows.add(Row.action(
                    ACTION_UNDO,
                    "Undo",
                    last == null ? "Last change" : titleForSettingId(last.getSettingId())
            ));
        }
        if (VayTelegram.settings().canRedo()) {
            rows.add(Row.action(ACTION_REDO, "Redo", "Restore undone change"));
        }

        int modified = VayTelegram.settings().countCustomized(scopeKey);
        rows.add(Row.action(
                ACTION_EXPORT_PRESET,
                "Copy preset",
                modified > 0 ? modified + (modified == 1 ? " customized setting" : " customized settings") : "Current values"
        ));
        rows.add(Row.action(ACTION_IMPORT_PRESET, "Import preset", "Paste vayGram JSON"));
        rows.add(Row.action(ACTION_SAVE_PROFILE, "Save profile", "Save all current values"));
        int savedProfiles = VayTelegram.presets().count();
        rows.add(Row.action(
                ACTION_SAVED_PROFILES,
                "Saved profiles",
                savedProfiles == 0 ? "None yet" : savedProfiles + (savedProfiles == 1 ? " profile" : " profiles")
        ));

        if (modified > 0) {
            rows.add(Row.action(
                    ACTION_RESET_ALL,
                    "Reset customized values",
                    modified + (modified == 1 ? " setting" : " settings")
            ));
        }
    }

    private void addRecentRows() {
        List<VaySettingChange> changes = VayTelegram.settings().recentChanges();
        if (changes.isEmpty()) {
            return;
        }

        Set<String> seen = new LinkedHashSet<>();
        ArrayList<VaySetting<?>> recent = new ArrayList<>();
        for (VaySettingChange change : changes) {
            if (!scopeKey.equals(change.getScope()) || !seen.add(change.getSettingId())) {
                continue;
            }
            VaySetting<?> setting = VayTelegram.registry().find(change.getSettingId());
            if (setting != null
                    && setting.getScopes().contains(scopeKey.getScope())
                    && setting.getVisibilityLevel().isVisibleAt(visibilityLevel)) {
                recent.add(setting);
            }
            if (recent.size() >= RECENT_LIMIT) {
                break;
            }
        }

        if (recent.isEmpty()) {
            return;
        }

        rows.add(Row.header("Recently changed"));
        for (VaySetting<?> setting : recent) {
            rows.add(Row.setting(setting, true));
        }
    }

    private VaySettingChange firstRecentChange() {
        List<VaySettingChange> changes = VayTelegram.settings().recentChanges();
        return changes.isEmpty() ? null : changes.get(0);
    }

    private String titleForSettingId(String settingId) {
        VaySetting<?> setting = VayTelegram.registry().find(settingId);
        return setting == null ? settingId : setting.getTitle();
    }

    private void onRowClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return;
        }
        Row row = rows.get(position);

        if (row.mode) {
            visibilityLevel = nextLevel(visibilityLevel);
            rebuildRows();
            return;
        }

        if (row.action != 0) {
            performAction(row.action);
            return;
        }

        if (row.setting == null) {
            return;
        }

        if (row.setting.getType() == VaySettingType.BOOLEAN) {
            toggleBoolean(row.setting);
            rebuildRows();
        } else if ((row.setting.getType() == VaySettingType.INTEGER
                || row.setting.getType() == VaySettingType.FLOAT)
                && row.setting.hasNumericRange()) {
            showNumberEditor(row.setting);
        }
    }

    private boolean onRowLongClicked(View view, int position) {
        if (position < 0 || position >= rows.size()) {
            return false;
        }
        Row row = rows.get(position);
        if (row.setting == null || !isModified(row.setting)) {
            return false;
        }

        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        showResetSettingDialog(row.setting);
        return true;
    }

    private void performAction(int action) {
        if (action == ACTION_UNDO) {
            VayTelegram.settings().undo();
            rebuildRows();
        } else if (action == ACTION_REDO) {
            VayTelegram.settings().redo();
            rebuildRows();
        } else if (action == ACTION_RESET_ALL) {
            showResetAllDialog();
        } else if (action == ACTION_EXPORT_PRESET) {
            copyPreset();
        } else if (action == ACTION_IMPORT_PRESET) {
            showImportPresetDialog();
        } else if (action == ACTION_SAVE_PROFILE) {
            showSaveProfileDialog();
        } else if (action == ACTION_SAVED_PROFILES) {
            showSavedProfilesDialog();
        } else if (action == ACTION_OPEN_PARENT_SCOPE) {
            openParentScope();
        } else if (action == ACTION_OPEN_ACCOUNT_SCOPE) {
            presentFragment(VaySettingsActivity.forAccount(currentAccount));
        }
    }

    private void openParentScope() {
        if (parentScopeKey == null) {
            return;
        }
        if (parentScopeKey.getScope() == VaySettingScope.GLOBAL) {
            presentFragment(new VaySettingsActivity());
        } else if (parentScopeKey.getScope() == VaySettingScope.ACCOUNT) {
            presentFragment(new VaySettingsActivity(
                    parentScopeKey,
                    VayScopeKey.GLOBAL,
                    "This account"
            ));
        }
    }

    private void showSaveProfileDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        EditText input = new EditText(context);
        input.setSingleLine(true);
        input.setHint("Profile name");
        int padding = AndroidUtilities.dp(20);
        input.setPadding(padding, padding, padding, padding);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save vayGram profile");
        builder.setMessage("Profiles store the current values in this scope. Saving the same name again replaces it.");
        builder.setView(input);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, "Profile name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            VaySettingsPreset preset = VayTelegram.settings().capturePreset(
                    name,
                    scopeKey,
                    false
            );
            VayTelegram.presets().save(preset);
            Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void showSavedProfilesDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        List<VaySettingsPreset> presets = VayTelegram.presets().list();
        if (presets.isEmpty()) {
            Toast.makeText(context, "No saved vayGram profiles yet", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] names = new CharSequence[presets.size()];
        for (int i = 0; i < presets.size(); i++) {
            VaySettingsPreset preset = presets.get(i);
            names[i] = preset.getName() + "  ·  " + preset.size() + " values";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Saved vayGram profiles");
        builder.setItems(names, (dialog, which) -> showProfileActions(presets.get(which)));
        builder.setNegativeButton("Close", null);
        showDialog(builder.create());
    }

    private void showProfileActions(VaySettingsPreset preset) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(preset.getName());
        builder.setMessage(
                preset.size() + (preset.size() == 1 ? " saved value" : " saved values")
                        + "\n\nOnly settings compatible with the current scope are applied."
        );
        builder.setNegativeButton("Cancel", null);
        builder.setNeutralButton("Delete", (dialog, which) -> showDeleteProfileDialog(preset));
        builder.setPositiveButton("Apply", (dialog, which) -> {
            int changed = VayTelegram.settings().applyPreset(preset, scopeKey);
            Toast.makeText(
                    context,
                    changed + (changed == 1 ? " setting applied" : " settings applied"),
                    Toast.LENGTH_SHORT
            ).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void showDeleteProfileDialog(VaySettingsPreset preset) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete profile?");
        builder.setMessage(preset.getName());
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            VayTelegram.presets().delete(preset.getName());
            Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void copyPreset() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        boolean modifiedOnly = VayTelegram.settings().countCustomized(scopeKey) > 0;
        VaySettingsPreset preset = VayTelegram.settings().capturePreset(
                "vayGram preset",
                scopeKey,
                modifiedOnly
        );
        String json = VayPresetJson.encode(preset);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("vayGram preset", json));
            Toast.makeText(context, "vayGram preset copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImportPresetDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        EditText input = new EditText(context);
        input.setHint("{\"schema\":1,...}");
        input.setMinLines(5);
        input.setMaxLines(12);
        int padding = AndroidUtilities.dp(20);
        input.setPadding(padding, padding, padding, padding);

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip() && clipboard.getPrimaryClip() != null
                && clipboard.getPrimaryClip().getItemCount() > 0) {
            CharSequence clip = clipboard.getPrimaryClip().getItemAt(0).coerceToText(context);
            if (clip != null && clip.toString().trim().startsWith("{")) {
                input.setText(clip);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Import vayGram preset");
        builder.setMessage("Only known settings compatible with this scope will be applied.");
        builder.setView(input);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Import", (dialog, which) -> {
            try {
                VaySettingsPreset preset = VayPresetJson.decode(input.getText().toString());
                int changed = VayTelegram.settings().applyPreset(preset, scopeKey);
                Toast.makeText(
                        context,
                        changed + (changed == 1 ? " setting applied" : " settings applied"),
                        Toast.LENGTH_SHORT
                ).show();
                rebuildRows();
            } catch (RuntimeException e) {
                Toast.makeText(context, "Invalid vayGram preset", Toast.LENGTH_SHORT).show();
            }
        });
        showDialog(builder.create());
    }

    private void showResetSettingDialog(VaySetting<?> setting) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reset setting?");
        builder.setMessage(setting.getTitle() + "\n\nDefault: " + formatDefaultValue(setting));
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Reset", (dialog, which) -> {
            resetSetting(setting);
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void showResetAllDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        int modified = VayTelegram.settings().countCustomized(scopeKey);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reset vayGram settings?");
        builder.setMessage("Reset " + modified + (modified == 1 ? " customized setting" : " customized settings") + " in this scope.");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Reset", (dialog, which) -> {
            VayTelegram.settings().resetAll(scopeKey);
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private VayVisibilityLevel nextLevel(VayVisibilityLevel level) {
        if (level == VayVisibilityLevel.BASIC) {
            return VayVisibilityLevel.ADVANCED;
        } else if (level == VayVisibilityLevel.ADVANCED) {
            return VayVisibilityLevel.INSANE;
        }
        return VayVisibilityLevel.BASIC;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void toggleBoolean(VaySetting setting) {
        Object value = getValue(setting);
        VayTelegram.settings().set(setting, scopeKey, !(Boolean) value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getValue(VaySetting setting) {
        if (VayScopeKey.GLOBAL.equals(scopeKey)) {
            return VayTelegram.settings().get(setting, scopeKey);
        }
        return VayTelegram.settings().getResolved(setting, scopeKey, parentScopeKey);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setValue(VaySetting setting, Object value) {
        VayTelegram.settings().set(setting, scopeKey, value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void previewValue(VaySetting setting, Object value) {
        VayTelegram.settings().preview(setting, scopeKey, value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void commitPreviewValue(VaySetting setting, Object originalValue, Object finalValue) {
        VayTelegram.settings().commitPreview(setting, scopeKey, originalValue, finalValue);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void cancelPreviewValue(VaySetting setting, Object originalValue) {
        VayTelegram.settings().cancelPreview(setting, scopeKey, originalValue);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void clearStoredPreviewValue(VaySetting setting) {
        VayTelegram.settings().clearStoredValue(setting, scopeKey);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void resetSetting(VaySetting setting) {
        VayTelegram.settings().reset(setting, scopeKey);
    }

    private boolean isModified(VaySetting<?> setting) {
        return VayTelegram.settings().isCustomizedAtScope(setting, scopeKey);
    }

    private boolean isInherited(VaySetting<?> setting) {
        return !VayScopeKey.GLOBAL.equals(scopeKey)
                && !VayTelegram.settings().hasStoredValue(setting, scopeKey);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getPreviewValue(VaySetting setting) {
        if (setting.getScopes().contains(scopeKey.getScope())) {
            return getValue(setting);
        }
        if (parentScopeKey != null && setting.getScopes().contains(parentScopeKey.getScope())) {
            return VayTelegram.settings().getResolved(
                    setting,
                    parentScopeKey,
                    VayScopeKey.GLOBAL
            );
        }
        if (setting.getScopes().contains(VaySettingScope.ACCOUNT)
                && !VayScopeKey.GLOBAL.equals(scopeKey)) {
            return VayTelegram.settings().getResolved(
                    setting,
                    VayTelegram.accountScope(currentAccount),
                    VayScopeKey.GLOBAL
            );
        }
        return VayTelegram.settings().get(setting, VayScopeKey.GLOBAL);
    }

    private void showNumberEditor(VaySetting<?> setting) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        final double min = setting.getMinValue();
        final double max = setting.getMaxValue();
        final double step = setting.getStepValue();
        final int stepCount = Math.max(1, (int) Math.round((max - min) / step));
        final Number current = (Number) getValue(setting);
        final Object originalValue = current;
        final boolean hadStoredValue = VayTelegram.settings().hasStoredValue(setting, scopeKey);
        final boolean[] settled = {false};

        int currentProgress = (int) Math.round((current.doubleValue() - min) / step);
        currentProgress = Math.max(0, Math.min(stepCount, currentProgress));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = AndroidUtilities.dp(24);
        container.setPadding(padding, AndroidUtilities.dp(8), padding, AndroidUtilities.dp(8));

        TextView valueView = new TextView(context);
        valueView.setTextSize(20);
        valueView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        container.addView(valueView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(stepCount);
        seekBar.setProgress(currentProgress);
        container.addView(seekBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Runnable updateLabel = () -> {
            double raw = min + seekBar.getProgress() * step;
            valueView.setText(formatNumber(setting, raw));
        };
        updateLabel.run();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateLabel.run();
                if (fromUser) {
                    double raw = min + progress * step;
                    previewValue(setting, numericValue(setting, raw));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(setting.getTitle());
        if (!TextUtils.isEmpty(setting.getDescription())) {
            builder.setMessage(setting.getDescription());
        }
        builder.setView(container);
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            settled[0] = true;
            if (hadStoredValue || VayScopeKey.GLOBAL.equals(scopeKey)) {
                cancelPreviewValue(setting, originalValue);
            } else {
                clearStoredPreviewValue(setting);
            }
            rebuildRows();
        });
        builder.setNeutralButton(
                VayScopeKey.GLOBAL.equals(scopeKey) ? "Default" : "Inherit",
                (dialog, which) -> {
                    settled[0] = true;
                    if (VayScopeKey.GLOBAL.equals(scopeKey)) {
                        commitPreviewValue(setting, originalValue, setting.getDefaultValue());
                    } else {
                        resetSetting(setting);
                    }
                    rebuildRows();
                }
        );
        builder.setPositiveButton("Apply", (dialog, which) -> {
            settled[0] = true;
            double raw = min + seekBar.getProgress() * step;
            commitPreviewValue(setting, originalValue, numericValue(setting, raw));
            rebuildRows();
        });
        builder.setOnDismissListener(dialog -> {
            if (!settled[0]) {
                if (hadStoredValue || VayScopeKey.GLOBAL.equals(scopeKey)) {
                    cancelPreviewValue(setting, originalValue);
                } else {
                    clearStoredPreviewValue(setting);
                }
                rebuildRows();
            }
        });
        showDialog(builder.create());
    }

    private Object numericValue(VaySetting<?> setting, double raw) {
        if (setting.getType() == VaySettingType.INTEGER) {
            return (int) Math.round(raw);
        }
        return (float) raw;
    }

    private String formatValue(VaySetting<?> setting) {
        Object value = getValue(setting);
        if (value instanceof Number) {
            return formatNumber(setting, ((Number) value).doubleValue());
        }
        return String.valueOf(value);
    }

    private String formatDefaultValue(VaySetting<?> setting) {
        Object value = setting.getDefaultValue();
        if (value instanceof Number) {
            return formatNumber(setting, ((Number) value).doubleValue());
        }
        return String.valueOf(value);
    }

    private String formatNumber(VaySetting<?> setting, double value) {
        if (setting.getType() == VaySettingType.INTEGER) {
            return String.valueOf((int) Math.round(value));
        }
        double step = setting.getStepValue() == null ? 0.01 : setting.getStepValue();
        if (step >= 1d) {
            return String.format(Locale.US, "%.0f", value);
        } else if (step >= 0.1d) {
            return String.format(Locale.US, "%.1f", value);
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private String displayTitle(VaySetting<?> setting, boolean recent) {
        String title = setting.getTitle();
        if (isModified(setting)) {
            title = "• " + title;
        } else if (isInherited(setting)) {
            title = title + "  ·  inherited";
        }
        if (recent) {
            title = title + "  ·  recent";
        }
        return title;
    }

    private final class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;

        private ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            Row row = rows.get(holder.getAdapterPosition());
            return !row.preview && (row.mode || row.action != 0 || row.setting != null);
        }

        @Override
        public int getItemViewType(int position) {
            Row row = rows.get(position);
            if (row.header != null) {
                return TYPE_HEADER;
            }
            if (row.preview) {
                return TYPE_PREVIEW;
            }
            if (row.setting != null && row.setting.getType() == VaySettingType.BOOLEAN) {
                return TYPE_CHECK;
            }
            return TYPE_VALUE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_PREVIEW) {
                previewView = new VayPreviewView(context);
                previewView.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        AndroidUtilities.dp(260)
                ));
                return new RecyclerListView.Holder(previewView);
            } else if (viewType == TYPE_HEADER) {
                view = new HeaderCell(context);
            } else if (viewType == TYPE_CHECK) {
                view = new TextCheckCell(context);
            } else {
                view = new TextSettingsCell(context);
            }
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            ));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Row row = rows.get(position);
            int type = holder.getItemViewType();

            if (type == TYPE_HEADER) {
                ((HeaderCell) holder.itemView).setText(row.header);
                return;
            }

            if (type == TYPE_PREVIEW) {
                previewView = (VayPreviewView) holder.itemView;
                previewView.invalidate();
                return;
            }

            if (row.mode) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setTextAndValue("Customization level", visibilityLevel.name(), false);
                return;
            }

            if (row.action != 0) {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setTextAndValue(row.actionTitle, row.actionValue, false);
                return;
            }

            if (type == TYPE_CHECK) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                boolean checked = (Boolean) getValue(row.setting);
                cell.setTextAndCheck(displayTitle(row.setting, row.recent), checked, false);
            } else {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                String value = formatValue(row.setting);
                if (isModified(row.setting)) {
                    value += VayScopeKey.GLOBAL.equals(scopeKey)
                            ? "  ·  modified"
                            : "  ·  override";
                } else if (isInherited(row.setting)) {
                    value += "  ·  inherited";
                }
                cell.setTextAndValue(
                        displayTitle(row.setting, row.recent),
                        value,
                        false
                );
            }
        }
    }

    private static final class Row {
        private final String header;
        private final VaySetting<?> setting;
        private final boolean mode;
        private final boolean recent;
        private final boolean preview;
        private final int action;
        private final String actionTitle;
        private final String actionValue;

        private Row(
                String header,
                VaySetting<?> setting,
                boolean mode,
                boolean recent,
                boolean preview,
                int action,
                String actionTitle,
                String actionValue
        ) {
            this.header = header;
            this.setting = setting;
            this.mode = mode;
            this.recent = recent;
            this.preview = preview;
            this.action = action;
            this.actionTitle = actionTitle;
            this.actionValue = actionValue;
        }

        private static Row header(String title) {
            return new Row(title, null, false, false, false, 0, null, null);
        }

        private static Row setting(VaySetting<?> setting, boolean recent) {
            return new Row(null, setting, false, recent, false, 0, null, null);
        }

        private static Row mode() {
            return new Row(null, null, true, false, false, 0, null, null);
        }

        private static Row action(int action, String title, String value) {
            return new Row(null, null, false, false, false, action, title, value);
        }

        private static Row preview() {
            return new Row(null, null, false, false, true, 0, null, null);
        }
    }

    private final class VayPreviewView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        private VayPreviewView(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            final float left = AndroidUtilities.dp(16);
            final float top = AndroidUtilities.dp(8);
            final float right = getWidth() - AndroidUtilities.dp(16);
            final float bottom = getHeight() - AndroidUtilities.dp(8);

            final boolean amoled = Boolean.TRUE.equals(getPreviewValue(VayDefaults.THEME_AMOLED))
                    && Theme.isCurrentThemeDark();
            final boolean compact = Boolean.TRUE.equals(getPreviewValue(VayDefaults.COMPACT_MODE));

            int surfaceColor = amoled
                    ? Color.BLACK
                    : Theme.getColor(Theme.key_windowBackgroundWhite);
            paint.setColor(surfaceColor);
            rect.set(left, top, right, bottom);
            canvas.drawRoundRect(rect, AndroidUtilities.dp(18), AndroidUtilities.dp(18), paint);

            float rowHeight = ((Number) getPreviewValue(VayDefaults.DIALOG_ROW_HEIGHT)).floatValue();
            float avatarSize = ((Number) getPreviewValue(VayDefaults.AVATAR_SIZE)).floatValue();
            float avatarRoundness = ((Number) getPreviewValue(VayDefaults.AVATAR_RADIUS)).floatValue();
            if (compact) {
                rowHeight = Math.min(rowHeight, 60f);
                avatarSize = Math.min(avatarSize, 46f);
            }

            final float rowTop = top + AndroidUtilities.dp(14);
            final float avatarDp = Math.max(28f, Math.min(60f, avatarSize));
            final float avatarPx = AndroidUtilities.dp(avatarDp);
            final float avatarLeft = left + AndroidUtilities.dp(16);
            final float avatarTop = rowTop + Math.max(
                    0,
                    (AndroidUtilities.dp(Math.min(rowHeight, 72f)) - avatarPx) / 2f
            );
            final float avatarRadiusPx = avatarPx * Math.max(0f, Math.min(50f, avatarRoundness)) / 100f;

            paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            rect.set(avatarLeft, avatarTop, avatarLeft + avatarPx, avatarTop + avatarPx);
            canvas.drawRoundRect(rect, avatarRadiusPx, avatarRadiusPx, paint);

            final float textLeft = avatarLeft + avatarPx + AndroidUtilities.dp(12);
            paint.setColor(Theme.getColor(Theme.key_chats_name));
            rect.set(textLeft, rowTop + AndroidUtilities.dp(10), right - AndroidUtilities.dp(26), rowTop + AndroidUtilities.dp(16));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(3), AndroidUtilities.dp(3), paint);

            paint.setColor(Theme.getColor(Theme.key_chats_message));
            rect.set(textLeft, rowTop + AndroidUtilities.dp(28), right - AndroidUtilities.dp(62), rowTop + AndroidUtilities.dp(33));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(3), AndroidUtilities.dp(3), paint);

            final float bubbleRadius = ((Number) getPreviewValue(VayDefaults.CHAT_BUBBLE_RADIUS)).floatValue();
            final float messageSpacing = compact
                    ? 0f
                    : ((Number) getPreviewValue(VayDefaults.CHAT_MESSAGE_SPACING)).floatValue();
            final float bubbleHeight = AndroidUtilities.dp(34);
            final float bubbleRadiusPx = Math.min(
                    bubbleHeight / 2f,
                    AndroidUtilities.dp(Math.max(0f, bubbleRadius))
            );

            float incomingTop = top + AndroidUtilities.dp(86);
            paint.setColor(Theme.getColor(Theme.key_chat_inBubble));
            rect.set(
                    left + AndroidUtilities.dp(18),
                    incomingTop,
                    left + AndroidUtilities.dp(18 + 148),
                    incomingTop + bubbleHeight
            );
            canvas.drawRoundRect(rect, bubbleRadiusPx, bubbleRadiusPx, paint);

            paint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
            rect.set(
                    left + AndroidUtilities.dp(30),
                    incomingTop + AndroidUtilities.dp(15),
                    left + AndroidUtilities.dp(118),
                    incomingTop + AndroidUtilities.dp(19)
            );
            canvas.drawRoundRect(rect, AndroidUtilities.dp(2), AndroidUtilities.dp(2), paint);

            float outgoingTop = incomingTop + bubbleHeight
                    + AndroidUtilities.dp(8 + Math.min(24f, messageSpacing));
            paint.setColor(Theme.getColor(Theme.key_chat_outBubble));
            rect.set(
                    right - AndroidUtilities.dp(164),
                    outgoingTop,
                    right - AndroidUtilities.dp(18),
                    outgoingTop + bubbleHeight
            );
            canvas.drawRoundRect(rect, bubbleRadiusPx, bubbleRadiusPx, paint);

            paint.setColor(Theme.getColor(Theme.key_chat_messageTextOut));
            rect.set(
                    right - AndroidUtilities.dp(148),
                    outgoingTop + AndroidUtilities.dp(15),
                    right - AndroidUtilities.dp(58),
                    outgoingTop + AndroidUtilities.dp(19)
            );
            canvas.drawRoundRect(rect, AndroidUtilities.dp(2), AndroidUtilities.dp(2), paint);

            int navHeight = ((Number) getPreviewValue(VayDefaults.NAV_HEIGHT)).intValue();
            boolean navLabels = Boolean.TRUE.equals(getPreviewValue(VayDefaults.NAV_SHOW_LABELS));
            if (compact) {
                navHeight = Math.min(navHeight, 52);
                navLabels = false;
            }

            final float navTop = bottom - AndroidUtilities.dp(Math.min(88, Math.max(48, navHeight)));
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
            rect.set(left + AndroidUtilities.dp(8), navTop, right - AndroidUtilities.dp(8), bottom - AndroidUtilities.dp(8));
            canvas.drawRoundRect(
                    rect,
                    AndroidUtilities.dp(Math.min(28, navHeight / 2f)),
                    AndroidUtilities.dp(Math.min(28, navHeight / 2f)),
                    paint
            );

            final float navWidth = rect.width();
            final float iconY = navTop + AndroidUtilities.dp(navLabels ? 17 : Math.max(14, navHeight / 2f - 4));
            for (int i = 0; i < 4; i++) {
                float cx = rect.left + navWidth * (i + 0.5f) / 4f;
                paint.setColor(Theme.getColor(i == 0 ? Theme.key_glass_tabSelected : Theme.key_glass_tabUnselected));
                canvas.drawCircle(cx, iconY, AndroidUtilities.dp(i == 0 ? 5 : 4), paint);

                if (navLabels) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(AndroidUtilities.dp(8));
                    canvas.drawText(
                            i == 0 ? "Chats" : "Tab",
                            cx,
                            iconY + AndroidUtilities.dp(16),
                            paint
                    );
                }
            }
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}

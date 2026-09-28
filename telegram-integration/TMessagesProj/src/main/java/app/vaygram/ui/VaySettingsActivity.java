package app.vaygram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingChange;
import app.vaygram.core.settings.VaySettingType;
import app.vaygram.core.settings.VaySettingsPreset;
import app.vaygram.core.settings.VayVisibilityLevel;
import app.vaygram.telegram.VayTelegram;

public final class VaySettingsActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_VALUE = 2;

    private static final int ACTION_UNDO = 1;
    private static final int ACTION_REDO = 2;
    private static final int ACTION_RESET_ALL = 3;
    private static final int ACTION_EXPORT_PRESET = 4;
    private static final int ACTION_IMPORT_PRESET = 5;
    private static final int ACTION_SAVE_PROFILE = 6;
    private static final int ACTION_SAVED_PROFILES = 7;

    private static final int RECENT_LIMIT = 5;

    private final VayScopeKey scopeKey;

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<Row> rows = new ArrayList<>();

    private String query = "";
    private VayVisibilityLevel visibilityLevel = VayVisibilityLevel.ADVANCED;

    public VaySettingsActivity() {
        this(VayScopeKey.GLOBAL);
    }

    public VaySettingsActivity(VayScopeKey scopeKey) {
        this.scopeKey = scopeKey == null ? VayScopeKey.GLOBAL : scopeKey;
    }

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("vayGram Settings");
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

    private void rebuildRows() {
        rows.clear();

        if (TextUtils.isEmpty(query)) {
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

        int modified = VayTelegram.settings().countModified(scopeKey);
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
        boolean modifiedOnly = VayTelegram.settings().countModified(scopeKey) > 0;
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

        int modified = VayTelegram.settings().countModified(scopeKey);
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
        Object value = VayTelegram.settings().get(setting, scopeKey);
        VayTelegram.settings().set(setting, scopeKey, !(Boolean) value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getValue(VaySetting setting) {
        return VayTelegram.settings().get(setting, scopeKey);
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
    private void resetSetting(VaySetting setting) {
        VayTelegram.settings().reset(setting, scopeKey);
    }

    private boolean isModified(VaySetting<?> setting) {
        return VayTelegram.settings().isModified(setting, scopeKey);
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
            cancelPreviewValue(setting, originalValue);
            rebuildRows();
        });
        builder.setNeutralButton("Default", (dialog, which) -> {
            settled[0] = true;
            commitPreviewValue(setting, originalValue, setting.getDefaultValue());
            rebuildRows();
        });
        builder.setPositiveButton("Apply", (dialog, which) -> {
            settled[0] = true;
            double raw = min + seekBar.getProgress() * step;
            commitPreviewValue(setting, originalValue, numericValue(setting, raw));
            rebuildRows();
        });
        builder.setOnDismissListener(dialog -> {
            if (!settled[0]) {
                cancelPreviewValue(setting, originalValue);
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
            return row.mode || row.action != 0 || row.setting != null;
        }

        @Override
        public int getItemViewType(int position) {
            Row row = rows.get(position);
            if (row.header != null) {
                return TYPE_HEADER;
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
            if (viewType == TYPE_HEADER) {
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
                    value += "  ·  modified";
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
        private final int action;
        private final String actionTitle;
        private final String actionValue;

        private Row(
                String header,
                VaySetting<?> setting,
                boolean mode,
                boolean recent,
                int action,
                String actionTitle,
                String actionValue
        ) {
            this.header = header;
            this.setting = setting;
            this.mode = mode;
            this.recent = recent;
            this.action = action;
            this.actionTitle = actionTitle;
            this.actionValue = actionValue;
        }

        private static Row header(String title) {
            return new Row(title, null, false, false, 0, null, null);
        }

        private static Row setting(VaySetting<?> setting, boolean recent) {
            return new Row(null, setting, false, recent, 0, null, null);
        }

        private static Row mode() {
            return new Row(null, null, true, false, 0, null, null);
        }

        private static Row action(int action, String title, String value) {
            return new Row(null, null, false, false, action, title, value);
        }
    }
}

package app.vaygram.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingType;
import app.vaygram.core.settings.VayVisibilityLevel;
import app.vaygram.telegram.VayTelegram;

public final class VaySettingsActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_VALUE = 2;

    private RecyclerListView listView;
    private ListAdapter adapter;
    private final ArrayList<Row> rows = new ArrayList<>();

    private String query = "";
    private VayVisibilityLevel visibilityLevel = VayVisibilityLevel.ADVANCED;

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

        fragmentView = listView;
        actionBar.setAdaptiveBackground(listView);
        rebuildRows();
        return fragmentView;
    }

    private void rebuildRows() {
        rows.clear();
        rows.add(Row.mode());

        List<VaySetting<?>> visible = VayTelegram.registry().search(query, visibilityLevel);
        Map<String, List<VaySetting<?>>> grouped = new LinkedHashMap<>();
        for (VaySetting<?> setting : visible) {
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
                rows.add(Row.setting(setting));
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
        if (row.setting == null) {
            return;
        }

        if (row.setting.getType() == VaySettingType.BOOLEAN) {
            toggleBoolean(row.setting);
            adapter.notifyItemChanged(position);
        } else if ((row.setting.getType() == VaySettingType.INTEGER
                || row.setting.getType() == VaySettingType.FLOAT)
                && row.setting.hasNumericRange()) {
            showNumberEditor(row.setting);
        }
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
        Object value = VayTelegram.settings().get(setting);
        VayTelegram.settings().set(setting, !(Boolean) value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getValue(VaySetting setting) {
        return VayTelegram.settings().get(setting);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setValue(VaySetting setting, Object value) {
        VayTelegram.settings().set(setting, value);
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
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Apply", (dialog, which) -> {
            double raw = min + seekBar.getProgress() * step;
            if (setting.getType() == VaySettingType.INTEGER) {
                setValue(setting, (int) Math.round(raw));
            } else {
                setValue(setting, (float) raw);
            }
            adapter.notifyDataSetChanged();
        });
        showDialog(builder.create());
    }

    private String formatValue(VaySetting<?> setting) {
        Object value = getValue(setting);
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
            return row.mode || row.setting != null;
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

            if (type == TYPE_CHECK) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                boolean checked = (Boolean) getValue(row.setting);
                cell.setTextAndCheck(row.setting.getTitle(), checked, false);
            } else {
                TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                cell.setTextAndValue(
                        row.setting.getTitle(),
                        formatValue(row.setting),
                        false
                );
            }
        }
    }

    private static final class Row {
        private final String header;
        private final VaySetting<?> setting;
        private final boolean mode;

        private Row(String header, VaySetting<?> setting, boolean mode) {
            this.header = header;
            this.setting = setting;
            this.mode = mode;
        }

        private static Row header(String title) {
            return new Row(title, null, false);
        }

        private static Row setting(VaySetting<?> setting) {
            return new Row(null, setting, false);
        }

        private static Row mode() {
            return new Row(null, null, true);
        }
    }
}

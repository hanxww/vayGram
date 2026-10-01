package app.vaygram.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.vaygram.core.theme.VayThemeToken;
import app.vaygram.core.theme.VayThemeTokenType;
import app.vaygram.telegram.VayTelegram;
import app.vaygram.theme.VayThemeBridge;

public final class VayPaletteActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TOKEN = 1;
    private static final int TYPE_ACTION = 2;

    private static final int ACTION_RESET_ALL = 1;
    private static final int ACTION_REFRESH_MATERIAL_YOU = 2;

    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;
    private String query = "";

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.vay_palette_title));
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

        if (TextUtils.isEmpty(query)) {
            rows.add(Row.header(LocaleController.getString(R.string.vay_sources)));
            String materialValue;
            if (!VayTelegram.isMaterialYouSupported()) {
                materialValue = LocaleController.getString(R.string.vay_requires_android_12);
            } else if (VayTelegram.isMaterialYouEnabled()) {
                materialValue = LocaleController.getString(R.string.vay_enabled_tap_refresh);
            } else {
                materialValue = LocaleController.getString(R.string.vay_disabled_in_settings);
            }
            rows.add(Row.action(
                    ACTION_REFRESH_MATERIAL_YOU,
                    LocaleController.getString(R.string.vay_material_you_base),
                    materialValue
            ));
        }

        if (TextUtils.isEmpty(query) && VayTelegram.themePalette().countOverrides() > 0) {
            rows.add(Row.header(LocaleController.getString(R.string.vay_palette)));
            rows.add(Row.action(
                    ACTION_RESET_ALL,
                    LocaleController.getString(R.string.vay_reset_palette),
                    LocaleController.formatString(R.string.vay_override_count_suffix, VayTelegram.themePalette().countOverrides())
            ));
        }

        List<VayThemeToken> visible = VayTelegram.themeTokens().search(query);
        LinkedHashMap<String, List<VayThemeToken>> grouped = new LinkedHashMap<>();
        for (VayThemeToken token : visible) {
            if (token.getType() != VayThemeTokenType.COLOR) {
                continue;
            }
            List<VayThemeToken> category = grouped.get(token.getCategory());
            if (category == null) {
                category = new ArrayList<>();
                grouped.put(token.getCategory(), category);
            }
            category.add(token);
        }

        for (Map.Entry<String, List<VayThemeToken>> entry : grouped.entrySet()) {
            rows.add(Row.header(entry.getKey()));
            for (VayThemeToken token : entry.getValue()) {
                rows.add(Row.token(token));
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
        if (row.action == ACTION_RESET_ALL) {
            showResetAllDialog();
        } else if (row.action == ACTION_REFRESH_MATERIAL_YOU) {
            if (!VayTelegram.isMaterialYouSupported()) {
                Toast.makeText(
                        getParentActivity(),
                        LocaleController.getString(R.string.vay_material_requires_android),
                        Toast.LENGTH_SHORT
                ).show();
            } else if (!VayTelegram.isMaterialYouEnabled()) {
                Toast.makeText(
                        getParentActivity(),
                        LocaleController.getString(R.string.vay_material_enable_hint),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                VayTelegram.refreshMaterialYou();
                Toast.makeText(
                        getParentActivity(),
                        LocaleController.getString(R.string.vay_material_refreshed),
                        Toast.LENGTH_SHORT
                ).show();
                rebuildRows();
            }
        } else if (row.token != null) {
            showColorEditor(row.token);
        }
    }

    private void showResetAllDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_reset_palette_title));
        builder.setMessage(LocaleController.getString(R.string.vay_reset_palette_body));
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_reset), (dialog, which) -> {
            VayTelegram.resetThemePalette();
            Toast.makeText(context, LocaleController.getString(R.string.vay_palette_reset_done), Toast.LENGTH_SHORT).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void showColorEditor(VayThemeToken token) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        final Integer originalOverride = VayTelegram.themePalette().getColorOverride(token);
        final int originalEffective = VayThemeBridge.color(token);
        final boolean[] settled = {false};

        ColorEditorView editor = new ColorEditorView(context, originalEffective);
        editor.setOnColorChangedListener(color ->
                VayTelegram.previewThemeColor(token, color)
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(token.getTitle());
        builder.setMessage(token.getId());
        builder.setView(editor);
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), (dialog, which) -> {
            settled[0] = true;
            VayTelegram.restoreThemeColorPreview(token, originalOverride);
            rebuildRows();
        });
        builder.setNeutralButton(LocaleController.getString(R.string.vay_reset), (dialog, which) -> {
            settled[0] = true;
            VayTelegram.resetThemeColor(token);
            rebuildRows();
        });
        builder.setPositiveButton(LocaleController.getString(R.string.vay_apply), (dialog, which) -> {
            settled[0] = true;
            VayTelegram.commitThemeColor(token, editor.getColor());
            rebuildRows();
        });
        builder.setOnDismissListener(dialog -> {
            if (!settled[0]) {
                VayTelegram.restoreThemeColorPreview(token, originalOverride);
                rebuildRows();
            }
        });
        showDialog(builder.create());
    }

    private String colorValue(VayThemeToken token) {
        int color = VayThemeBridge.color(token);
        String value = String.format(Locale.US, "#%08X", color);
        if (VayTelegram.themePalette().hasColorOverride(token)) {
            value += "  ·  " + LocaleController.getString(R.string.vay_override_short);
        } else if (VayTelegram.isMaterialYouEnabled()) {
            value += "  ·  " + LocaleController.getString(R.string.vay_material_you_suffix);
        }
        return value;
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
            return row.token != null || row.action != 0;
        }

        @Override
        public int getItemViewType(int position) {
            Row row = rows.get(position);
            if (row.header != null) {
                return TYPE_HEADER;
            }
            if (row.action != 0) {
                return TYPE_ACTION;
            }
            return TYPE_TOKEN;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_HEADER) {
                view = new HeaderCell(context);
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
            if (row.header != null) {
                ((HeaderCell) holder.itemView).setText(row.header);
                return;
            }

            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            if (row.action != 0) {
                cell.setTextAndValue(row.actionTitle, row.actionValue, false);
                return;
            }

            String title = row.token.getTitle();
            if (VayTelegram.themePalette().hasColorOverride(row.token)) {
                title = "• " + title;
            }
            cell.setTextAndValue(title, colorValue(row.token), false);
        }
    }

    private interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    private static final class ColorEditorView extends LinearLayout {
        private final SeekBar[] channels = new SeekBar[4];
        private final TextView[] labels = new TextView[4];
        private final View swatch;
        private OnColorChangedListener listener;

        private ColorEditorView(Context context, int color) {
            super(context);
            setOrientation(VERTICAL);
            int horizontal = AndroidUtilities.dp(24);
            setPadding(horizontal, AndroidUtilities.dp(8), horizontal, AndroidUtilities.dp(8));

            swatch = new View(context);
            LinearLayout.LayoutParams swatchParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    AndroidUtilities.dp(64)
            );
            swatchParams.bottomMargin = AndroidUtilities.dp(12);
            addView(swatch, swatchParams);

            int[] values = {
                    Color.alpha(color),
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
            };
            String[] names = {"A", "R", "G", "B"};

            for (int i = 0; i < channels.length; i++) {
                final int index = i;
                labels[i] = new TextView(context);
                labels[i].setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                labels[i].setTextSize(14);
                addView(labels[i], new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                ));

                channels[i] = new SeekBar(context);
                channels[i].setMax(255);
                channels[i].setProgress(values[i]);
                channels[i].setContentDescription(names[i]);
                addView(channels[i], new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                ));
                channels[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        updateUi();
                        if (fromUser && listener != null) {
                            listener.onColorChanged(getColor());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            }

            updateUi();
        }

        private void setOnColorChangedListener(OnColorChangedListener listener) {
            this.listener = listener;
        }

        private int getColor() {
            return Color.argb(
                    channels[0].getProgress(),
                    channels[1].getProgress(),
                    channels[2].getProgress(),
                    channels[3].getProgress()
            );
        }

        private void updateUi() {
            int color = getColor();
            swatch.setBackgroundColor(color);
            String[] names = {
                    LocaleController.getString(R.string.vay_channel_alpha),
                    LocaleController.getString(R.string.vay_channel_red),
                    LocaleController.getString(R.string.vay_channel_green),
                    LocaleController.getString(R.string.vay_channel_blue)
            };
            for (int i = 0; i < labels.length; i++) {
                labels[i].setText(names[i] + "  " + channels[i].getProgress());
            }
        }
    }

    private static final class Row {
        private final String header;
        private final VayThemeToken token;
        private final int action;
        private final String actionTitle;
        private final String actionValue;

        private Row(
                String header,
                VayThemeToken token,
                int action,
                String actionTitle,
                String actionValue
        ) {
            this.header = header;
            this.token = token;
            this.action = action;
            this.actionTitle = actionTitle;
            this.actionValue = actionValue;
        }

        private static Row header(String title) {
            return new Row(title, null, 0, null, null);
        }

        private static Row token(VayThemeToken token) {
            return new Row(null, token, 0, null, null);
        }

        private static Row action(int action, String title, String value) {
            return new Row(null, null, action, title, value);
        }
    }
}

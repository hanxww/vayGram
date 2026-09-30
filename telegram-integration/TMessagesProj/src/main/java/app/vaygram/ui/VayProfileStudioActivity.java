package app.vaygram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.vaygram.core.settings.VayDefaults;
import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.core.settings.VaySetting;
import app.vaygram.core.settings.VaySettingsListener;
import app.vaygram.telegram.VayTelegram;
import app.vaygram.theme.VayThemeBridge;
import app.vaygram.core.theme.VayThemeTokens;

public final class VayProfileStudioActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_VALUE = 2;
    private static final int TYPE_PREVIEW = 3;

    private static final int ACTION_LAYOUT_MODE = 1;
    private static final int ACTION_RESET = 2;

    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;
    private ProfilePreviewView previewView;
    private VayScopeKey accountScope;

    private final VaySettingsListener listener = change -> {
        if (!change.getSettingId().startsWith("profile.")) {
            return;
        }
        AndroidUtilities.runOnUIThread(() -> {
            if (previewView != null) {
                previewView.invalidate();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    };

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();
        accountScope = VayTelegram.accountScope(currentAccount);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.vay_profile_studio));
        actionBar.setActionBarMenuOnItemClick(id -> {
            if (id == -1) {
                finishFragment();
            }
        });

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        listView.setAdapter(adapter = new ListAdapter(context));
        listView.setOnItemClickListener((view, position) -> onRowClicked(position));
        listView.setOnItemLongClickListener((view, position) -> onRowLongClicked(position));

        VayTelegram.settings().removeListener(listener);
        VayTelegram.settings().addListener(listener);

        rebuildRows();
        fragmentView = listView;
        actionBar.setAdaptiveBackground(listView);
        return fragmentView;
    }

    @Override
    public void onFragmentDestroy() {
        VayTelegram.settings().removeListener(listener);
        previewView = null;
        super.onFragmentDestroy();
    }

    private void rebuildRows() {
        rows.clear();

        rows.add(Row.header(LocaleController.getString(R.string.vay_profile_preview)));
        rows.add(Row.preview());

        rows.add(Row.header(LocaleController.getString(R.string.vay_profile_layout)));
        rows.add(Row.action(
                ACTION_LAYOUT_MODE,
                LocaleController.getString(R.string.vay_profile_layout_mode),
                layoutModeLabel()
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_profile_avatar)));
        rows.add(Row.setting(VayDefaults.PROFILE_AVATAR_SIZE,
                LocaleController.getString(R.string.vay_profile_avatar_size)));
        rows.add(Row.setting(VayDefaults.PROFILE_AVATAR_RADIUS,
                LocaleController.getString(R.string.vay_profile_avatar_roundness)));
        rows.add(Row.setting(VayDefaults.PROFILE_AVATAR_GLOW,
                LocaleController.getString(R.string.vay_profile_avatar_glow)));
        rows.add(Row.setting(VayDefaults.PROFILE_STATUS_RING,
                LocaleController.getString(R.string.vay_profile_status_ring)));

        rows.add(Row.header(LocaleController.getString(R.string.vay_profile_effects)));
        rows.add(Row.setting(VayDefaults.PROFILE_BACKGROUND_BLUR,
                LocaleController.getString(R.string.vay_profile_background_blur)));
        rows.add(Row.setting(VayDefaults.PROFILE_PARALLAX,
                LocaleController.getString(R.string.vay_profile_parallax)));

        rows.add(Row.header(LocaleController.getString(R.string.vay_profile_blocks)));
        rows.add(Row.setting(VayDefaults.PROFILE_SHOW_USERNAME,
                LocaleController.getString(R.string.vay_profile_show_username)));
        rows.add(Row.setting(VayDefaults.PROFILE_SHOW_BIO,
                LocaleController.getString(R.string.vay_profile_show_bio)));
        rows.add(Row.setting(VayDefaults.PROFILE_AUTHOR_STYLE,
                LocaleController.getString(R.string.vay_profile_author_style)));

        rows.add(Row.action(
                ACTION_RESET,
                LocaleController.getString(R.string.vay_profile_reset),
                customizedCount() == 0
                        ? LocaleController.getString(R.string.vay_profile_inherited)
                        : String.valueOf(customizedCount())
        ));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private int customizedCount() {
        int count = 0;
        for (VaySetting<?> setting : profileSettings()) {
            if (VayTelegram.settings().hasStoredValue(setting, accountScope)) {
                count++;
            }
        }
        return count;
    }

    private List<VaySetting<?>> profileSettings() {
        ArrayList<VaySetting<?>> settings = new ArrayList<>();
        settings.add(VayDefaults.PROFILE_LAYOUT_MODE);
        settings.add(VayDefaults.PROFILE_AVATAR_SIZE);
        settings.add(VayDefaults.PROFILE_AVATAR_RADIUS);
        settings.add(VayDefaults.PROFILE_AVATAR_GLOW);
        settings.add(VayDefaults.PROFILE_STATUS_RING);
        settings.add(VayDefaults.PROFILE_BACKGROUND_BLUR);
        settings.add(VayDefaults.PROFILE_PARALLAX);
        settings.add(VayDefaults.PROFILE_SHOW_USERNAME);
        settings.add(VayDefaults.PROFILE_SHOW_BIO);
        settings.add(VayDefaults.PROFILE_AUTHOR_STYLE);
        return settings;
    }

    private void onRowClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return;
        }
        Row row = rows.get(position);

        if (row.action == ACTION_LAYOUT_MODE) {
            String current = get(VayDefaults.PROFILE_LAYOUT_MODE);
            set(VayDefaults.PROFILE_LAYOUT_MODE, "free".equals(current) ? "grid" : "free");
            rebuildRows();
            return;
        }

        if (row.action == ACTION_RESET) {
            showResetDialog();
            return;
        }

        if (row.setting == null) {
            return;
        }

        if (row.setting == VayDefaults.PROFILE_AVATAR_GLOW
                || row.setting == VayDefaults.PROFILE_STATUS_RING
                || row.setting == VayDefaults.PROFILE_PARALLAX
                || row.setting == VayDefaults.PROFILE_SHOW_USERNAME
                || row.setting == VayDefaults.PROFILE_SHOW_BIO
                || row.setting == VayDefaults.PROFILE_AUTHOR_STYLE) {
            toggleBoolean(row.setting);
        } else if (row.setting.hasNumericRange()) {
            showNumberEditor(row);
        }
    }

    private boolean onRowLongClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return false;
        }
        Row row = rows.get(position);
        if (row.setting == null || !VayTelegram.settings().hasStoredValue(row.setting, accountScope)) {
            return false;
        }
        clear(row.setting);
        rebuildRows();
        return true;
    }

    @SuppressWarnings("unchecked")
    private void toggleBoolean(VaySetting<?> raw) {
        VaySetting<Boolean> setting = (VaySetting<Boolean>) raw;
        set(setting, !get(setting));
        rebuildRows();
    }

    private String layoutModeLabel() {
        return "free".equals(get(VayDefaults.PROFILE_LAYOUT_MODE))
                ? LocaleController.getString(R.string.vay_profile_free_layout)
                : LocaleController.getString(R.string.vay_profile_grid_layout);
    }

    private void showResetDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_profile_reset));
        builder.setMessage(LocaleController.getString(R.string.vay_profile_reset_message));
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_reset), (dialog, which) -> {
            for (VaySetting<?> setting : profileSettings()) {
                if (VayTelegram.settings().hasStoredValue(setting, accountScope)) {
                    clear(setting);
                }
            }
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_profile_reset_done),
                    Toast.LENGTH_SHORT
            ).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void showNumberEditor(Row row) {
        VaySetting setting = row.setting;
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        double min = setting.getMinValue();
        double max = setting.getMaxValue();
        double step = setting.getStepValue();
        Number current = (Number) get(setting);
        int steps = Math.max(1, (int) Math.round((max - min) / step));
        int progress = Math.max(0, Math.min(
                steps,
                (int) Math.round((current.doubleValue() - min) / step)
        ));

        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        int padding = AndroidUtilities.dp(20);
        box.setPadding(padding, padding, padding, padding);

        TextView label = new TextView(context);
        label.setTextSize(18);
        label.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        box.addView(label, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        SeekBar seek = new SeekBar(context);
        seek.setMax(steps);
        seek.setProgress(progress);
        box.addView(seek, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Runnable update = () -> {
            double raw = min + seek.getProgress() * step;
            label.setText(formatNumber(setting, raw));
        };
        update.run();
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                update.run();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(row.title);
        builder.setView(box);
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setNeutralButton(LocaleController.getString(R.string.vay_inherit), (dialog, which) -> {
            clear(setting);
            rebuildRows();
        });
        builder.setPositiveButton(LocaleController.getString(R.string.vay_apply), (dialog, which) -> {
            double raw = min + seek.getProgress() * step;
            if (setting.getDefaultValue() instanceof Integer) {
                set(setting, (int) Math.round(raw));
            } else {
                set(setting, (float) raw);
            }
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private String formatNumber(VaySetting<?> setting, double value) {
        if (setting.getDefaultValue() instanceof Integer) {
            return String.valueOf((int) Math.round(value));
        }
        return String.format(Locale.US, "%.0f%%", value);
    }

    private <T> T get(VaySetting<T> setting) {
        return VayTelegram.settings().getResolved(setting, accountScope, VayScopeKey.GLOBAL);
    }

    private <T> void set(VaySetting<T> setting, T value) {
        VayTelegram.settings().set(setting, accountScope, value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void clear(VaySetting setting) {
        VayTelegram.settings().clearStoredValue(setting, accountScope);
    }

    private String valueText(Row row) {
        if (row.setting == VayDefaults.PROFILE_AVATAR_SIZE
                || row.setting == VayDefaults.PROFILE_BACKGROUND_BLUR) {
            return String.valueOf(((Number) get(row.setting)).intValue())
                    + inheritanceSuffix(row.setting);
        }
        if (row.setting == VayDefaults.PROFILE_AVATAR_RADIUS) {
            return String.format(Locale.US, "%.0f%%",
                    ((Number) get(row.setting)).doubleValue()) + inheritanceSuffix(row.setting);
        }
        return "";
    }

    private String inheritanceSuffix(VaySetting<?> setting) {
        return VayTelegram.settings().hasStoredValue(setting, accountScope)
                ? " · " + LocaleController.getString(R.string.vay_profile_override)
                : " · " + LocaleController.getString(R.string.vay_profile_inherited);
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
            return row.action != 0 || row.setting != null;
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
            if (row.setting != null && row.setting.getDefaultValue() instanceof Boolean) {
                return TYPE_CHECK;
            }
            return TYPE_VALUE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_PREVIEW) {
                previewView = new ProfilePreviewView(context);
                previewView.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        AndroidUtilities.dp(330)
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
                previewView = (ProfilePreviewView) holder.itemView;
                previewView.invalidate();
                return;
            }
            if (row.action != 0) {
                ((TextSettingsCell) holder.itemView)
                        .setTextAndValue(row.title, row.value, false);
                return;
            }
            if (type == TYPE_CHECK) {
                boolean checked = Boolean.TRUE.equals(get(row.setting));
                ((TextCheckCell) holder.itemView)
                        .setTextAndCheck(row.title, checked, false);
            } else {
                ((TextSettingsCell) holder.itemView)
                        .setTextAndValue(row.title, valueText(row), false);
            }
        }
    }

    private static final class Row {
        private final String header;
        private final VaySetting<?> setting;
        private final String title;
        private final int action;
        private final String value;
        private final boolean preview;

        private Row(String header, VaySetting<?> setting, String title, int action, String value, boolean preview) {
            this.header = header;
            this.setting = setting;
            this.title = title;
            this.action = action;
            this.value = value;
            this.preview = preview;
        }

        static Row header(String title) {
            return new Row(title, null, null, 0, null, false);
        }

        static Row setting(VaySetting<?> setting, String title) {
            return new Row(null, setting, title, 0, null, false);
        }

        static Row action(int action, String title, String value) {
            return new Row(null, null, title, action, value, false);
        }

        static Row preview() {
            return new Row(null, null, null, 0, null, true);
        }
    }

    private final class ProfilePreviewView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        private ProfilePreviewView(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int surface = VayThemeBridge.color(VayThemeTokens.SURFACE_PRIMARY);
            int secondary = VayThemeBridge.color(VayThemeTokens.SURFACE_SECONDARY);
            int text = VayThemeBridge.color(VayThemeTokens.TEXT_PRIMARY);
            int textSecondary = VayThemeBridge.color(VayThemeTokens.TEXT_SECONDARY);
            int accent = VayThemeBridge.color(VayThemeTokens.ACCENT_PRIMARY);

            paint.setColor(Theme.isCurrentThemeDark() ? Color.rgb(10, 10, 14) : secondary);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

            float margin = AndroidUtilities.dp(18);
            rect.set(margin, AndroidUtilities.dp(18), getWidth() - margin, getHeight() - AndroidUtilities.dp(18));
            paint.setColor(surface);
            paint.setShadowLayer(
                    AndroidUtilities.dp(Math.min(24, get(VayDefaults.PROFILE_BACKGROUND_BLUR) / 2f)),
                    0,
                    AndroidUtilities.dp(4),
                    0x33000000
            );
            canvas.drawRoundRect(rect, AndroidUtilities.dp(26), AndroidUtilities.dp(26), paint);
            paint.clearShadowLayer();

            float avatarSize = AndroidUtilities.dp(Math.min(148, get(VayDefaults.PROFILE_AVATAR_SIZE)));
            float cx = "free".equals(get(VayDefaults.PROFILE_LAYOUT_MODE))
                    ? getWidth() * 0.36f
                    : getWidth() * 0.5f;
            float cy = AndroidUtilities.dp(92);
            float radiusPct = get(VayDefaults.PROFILE_AVATAR_RADIUS);
            float avatarRadius = avatarSize * Math.max(0f, Math.min(50f, radiusPct)) / 100f;

            if (Boolean.TRUE.equals(get(VayDefaults.PROFILE_AVATAR_GLOW))) {
                paint.setColor(accent);
                paint.setShadowLayer(AndroidUtilities.dp(22), 0, 0, accent);
                rect.set(cx - avatarSize / 2, cy - avatarSize / 2, cx + avatarSize / 2, cy + avatarSize / 2);
                canvas.drawRoundRect(rect, avatarRadius, avatarRadius, paint);
                paint.clearShadowLayer();
            }

            paint.setColor(accent);
            rect.set(cx - avatarSize / 2, cy - avatarSize / 2, cx + avatarSize / 2, cy + avatarSize / 2);
            canvas.drawRoundRect(rect, avatarRadius, avatarRadius, paint);

            if (Boolean.TRUE.equals(get(VayDefaults.PROFILE_STATUS_RING))) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(AndroidUtilities.dp(3));
                paint.setColor(Color.WHITE);
                canvas.drawRoundRect(rect, avatarRadius, avatarRadius, paint);
                paint.setStyle(Paint.Style.FILL);
            }

            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(text);
            paint.setTextSize(AndroidUtilities.dp(20));
            paint.setFakeBoldText(true);
            canvas.drawText("vayGram", cx, cy + avatarSize / 2 + AndroidUtilities.dp(32), paint);
            paint.setFakeBoldText(false);

            float cursorY = cy + avatarSize / 2 + AndroidUtilities.dp(56);
            if (Boolean.TRUE.equals(get(VayDefaults.PROFILE_SHOW_USERNAME))) {
                paint.setColor(accent);
                paint.setTextSize(AndroidUtilities.dp(13));
                canvas.drawText("@vaygram", cx, cursorY, paint);
                cursorY += AndroidUtilities.dp(24);
            }
            if (Boolean.TRUE.equals(get(VayDefaults.PROFILE_SHOW_BIO))) {
                paint.setColor(textSecondary);
                paint.setTextSize(AndroidUtilities.dp(12));
                canvas.drawText("Customize everything. Complicate nothing.", cx, cursorY, paint);
            }

            if (Boolean.TRUE.equals(get(VayDefaults.PROFILE_PARALLAX))) {
                paint.setColor(0x224F2CFF);
                canvas.drawCircle(getWidth() - AndroidUtilities.dp(52), AndroidUtilities.dp(62), AndroidUtilities.dp(34), paint);
                paint.setColor(0x22FF35CC);
                canvas.drawCircle(AndroidUtilities.dp(48), getHeight() - AndroidUtilities.dp(48), AndroidUtilities.dp(26), paint);
            }
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}

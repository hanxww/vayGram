package app.vaygram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

import app.vaygram.core.settings.VayScopeKey;
import app.vaygram.telegram.VayBuild;
import app.vaygram.telegram.VayTelegram;

public final class VayDiagnosticsActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_VALUE = 1;
    private static final int TYPE_ACTION = 2;

    private static final int ACTION_COPY = 1;
    private static final int ACTION_RESET_SCOPE = 2;
    private static final int ACTION_RESET_THEME = 3;

    private final VayScopeKey scopeKey;
    private final String scopeTitle;
    private final String levelTitle;
    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;

    public VayDiagnosticsActivity(
            VayScopeKey scopeKey,
            String scopeTitle,
            String levelTitle
    ) {
        this.scopeKey = scopeKey == null ? VayScopeKey.GLOBAL : scopeKey;
        this.scopeTitle = scopeTitle == null ? "" : scopeTitle;
        this.levelTitle = levelTitle == null ? "" : levelTitle;
    }

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.vay_diagnostics_title));
        actionBar.setActionBarMenuOnItemClick(new org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        rebuildRows();
    }

    private void rebuildRows() {
        rows.clear();

        rows.add(Row.header(LocaleController.getString(R.string.vay_diagnostics_build)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_client),
                VayBuild.CLIENT_VERSION
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_telegram_base),
                VayBuild.TELEGRAM_BASE_VERSION + " (" + VayBuild.TELEGRAM_BASE_VERSION_CODE + ")"
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_package),
                getContext() == null ? "" : getContext().getPackageName()
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_android),
                "SDK " + Build.VERSION.SDK_INT
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_diagnostics_state)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_scope),
                scopeTitle
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_customization_level),
                levelTitle
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_customized),
                String.valueOf(VayTelegram.settings().countCustomized(scopeKey))
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_palette_overrides),
                String.valueOf(VayTelegram.themePalette().countOverrides())
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_saved_profiles),
                String.valueOf(VayTelegram.presets().count())
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_material_you),
                materialYouStatus()
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_gradient),
                VayTelegram.storedNavigationGradient() != null
                        ? LocaleController.getString(R.string.vay_enabled)
                        : LocaleController.getString(R.string.vay_diagnostics_disabled)
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_diagnostics_recovery)));
        rows.add(Row.action(
                ACTION_COPY,
                LocaleController.getString(R.string.vay_diagnostics_copy),
                LocaleController.getString(R.string.vay_diagnostics_copy_hint)
        ));
        rows.add(Row.action(
                ACTION_RESET_SCOPE,
                LocaleController.getString(R.string.vay_diagnostics_reset_scope),
                LocaleController.getString(R.string.vay_diagnostics_reset_scope_hint)
        ));
        rows.add(Row.action(
                ACTION_RESET_THEME,
                LocaleController.getString(R.string.vay_diagnostics_reset_theme),
                LocaleController.getString(R.string.vay_diagnostics_reset_theme_hint)
        ));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private String materialYouStatus() {
        if (!VayTelegram.isMaterialYouSupported()) {
            return LocaleController.getString(R.string.vay_diagnostics_unsupported);
        }
        return VayTelegram.isMaterialYouEnabled()
                ? LocaleController.getString(R.string.vay_enabled)
                : LocaleController.getString(R.string.vay_diagnostics_disabled);
    }

    private void onRowClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return;
        }
        Row row = rows.get(position);
        if (row.action == ACTION_COPY) {
            copyReport();
        } else if (row.action == ACTION_RESET_SCOPE) {
            confirmResetScope();
        } else if (row.action == ACTION_RESET_THEME) {
            confirmResetTheme();
        }
    }

    private void copyReport() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        clipboard.setPrimaryClip(ClipData.newPlainText("vayGram diagnostics", buildReport()));
        Toast.makeText(
                context,
                LocaleController.getString(R.string.vay_diagnostics_copied),
                Toast.LENGTH_SHORT
        ).show();
    }

    private String buildReport() {
        Context context = getContext();
        String packageName = context == null ? "" : context.getPackageName();
        return "vayGram diagnostics\n"
                + "client=" + VayBuild.CLIENT_VERSION + "\n"
                + "telegram_base=" + VayBuild.TELEGRAM_BASE_VERSION
                + " (" + VayBuild.TELEGRAM_BASE_VERSION_CODE + ")\n"
                + "telegram_commit=" + VayBuild.TELEGRAM_BASE_COMMIT + "\n"
                + "package=" + packageName + "\n"
                + "android_sdk=" + Build.VERSION.SDK_INT + "\n"
                + "scope=" + scopeTitle + "\n"
                + "customization_level=" + levelTitle + "\n"
                + "customized_settings=" + VayTelegram.settings().countCustomized(scopeKey) + "\n"
                + "palette_overrides=" + VayTelegram.themePalette().countOverrides() + "\n"
                + "saved_profiles=" + VayTelegram.presets().count() + "\n"
                + "material_you=" + materialYouStatus() + "\n"
                + "navigation_gradient="
                + (VayTelegram.storedNavigationGradient() != null ? "enabled" : "disabled") + "\n"
                + "privacy=No account ids, phone numbers, chats, messages, tokens, or credentials are included.";
    }

    private void confirmResetScope() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_diagnostics_reset_scope_title));
        builder.setMessage(LocaleController.getString(R.string.vay_diagnostics_reset_scope_body));
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_reset), (dialog, which) -> {
            int count = VayTelegram.settings().resetAll(scopeKey);
            Toast.makeText(
                    context,
                    LocaleController.formatString(R.string.vay_diagnostics_reset_done, count),
                    Toast.LENGTH_SHORT
            ).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private void confirmResetTheme() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_diagnostics_reset_theme_title));
        builder.setMessage(LocaleController.getString(R.string.vay_diagnostics_reset_theme_body));
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_reset), (dialog, which) -> {
            VayTelegram.resetThemePalette();
            VayTelegram.resetNavigationGradient();
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_diagnostics_theme_reset_done),
                    Toast.LENGTH_SHORT
            ).show();
            rebuildRows();
        });
        showDialog(builder.create());
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
            return rows.get(holder.getAdapterPosition()).action != 0;
        }

        @Override
        public int getItemViewType(int position) {
            Row row = rows.get(position);
            if (row.header != null) {
                return TYPE_HEADER;
            }
            return row.action != 0 ? TYPE_ACTION : TYPE_VALUE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = viewType == TYPE_HEADER
                    ? new HeaderCell(context)
                    : new TextSettingsCell(context);
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
            } else {
                ((TextSettingsCell) holder.itemView).setTextAndValue(row.title, row.value, false);
            }
        }
    }

    private static final class Row {
        private final String header;
        private final String title;
        private final String value;
        private final int action;

        private Row(String header, String title, String value, int action) {
            this.header = header;
            this.title = title;
            this.value = value;
            this.action = action;
        }

        private static Row header(String title) {
            return new Row(title, null, null, 0);
        }

        private static Row value(String title, String value) {
            return new Row(null, title, value, 0);
        }

        private static Row action(int action, String title, String value) {
            return new Row(null, title, value, action);
        }
    }
}

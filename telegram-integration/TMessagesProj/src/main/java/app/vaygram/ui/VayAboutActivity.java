package app.vaygram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

import app.vaygram.telegram.VayBuild;

public final class VayAboutActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_VALUE = 1;
    private static final int TYPE_ACTION = 2;

    private static final int ACTION_SOURCE = 1;
    private static final int ACTION_CHANNEL = 2;
    private static final int ACTION_COPY_BUILD = 3;

    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.vay_about_title));
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

    private void rebuildRows() {
        rows.clear();

        rows.add(Row.header(LocaleController.getString(R.string.vay_about_client)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_client),
                VayBuild.CLIENT_VERSION
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_package),
                getContext() == null ? VayBuild.PACKAGE_ID : getContext().getPackageName()
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_android),
                "SDK " + Build.VERSION.SDK_INT
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_about_base)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_diagnostics_telegram_base),
                VayBuild.TELEGRAM_BASE_VERSION + " (" + VayBuild.TELEGRAM_BASE_VERSION_CODE + ")"
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_about_base_commit),
                VayBuild.TELEGRAM_BASE_COMMIT.substring(0, 12)
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_about_project)));
        rows.add(Row.action(
                ACTION_SOURCE,
                LocaleController.getString(R.string.vay_about_source),
                "GitHub"
        ));
        rows.add(Row.action(
                ACTION_CHANNEL,
                LocaleController.getString(R.string.vay_about_channel),
                "Telegram"
        ));
        rows.add(Row.action(
                ACTION_COPY_BUILD,
                LocaleController.getString(R.string.vay_about_copy_build),
                LocaleController.getString(R.string.vay_about_copy_build_hint)
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_about_principle)));
        rows.add(Row.value(
                "vayGram",
                LocaleController.getString(R.string.vay_about_slogan)
        ));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void onRowClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return;
        }
        int action = rows.get(position).action;
        if (action == ACTION_SOURCE) {
            openUrl(VayBuild.SOURCE_REPOSITORY);
        } else if (action == ACTION_CHANNEL) {
            openUrl(VayBuild.DEVELOPMENT_CHANNEL);
        } else if (action == ACTION_COPY_BUILD) {
            copyBuildInfo();
        }
    }

    private void openUrl(String url) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        try {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)), 511);
        } catch (RuntimeException ignored) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_about_open_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void copyBuildInfo() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        String value = "vayGram " + VayBuild.CLIENT_VERSION + "\n"
                + "Telegram " + VayBuild.TELEGRAM_BASE_VERSION
                + " (" + VayBuild.TELEGRAM_BASE_VERSION_CODE + ")\n"
                + "Base commit " + VayBuild.TELEGRAM_BASE_COMMIT + "\n"
                + "Android SDK " + Build.VERSION.SDK_INT;
        clipboard.setPrimaryClip(ClipData.newPlainText("vayGram build", value));
        Toast.makeText(
                context,
                LocaleController.getString(R.string.vay_about_build_copied),
                Toast.LENGTH_SHORT
        ).show();
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

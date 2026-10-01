package app.vaygram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import app.vaygram.telegram.VayBackupCodec;
import app.vaygram.telegram.VayTelegram;

public final class VayBackupActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_VALUE = 1;
    private static final int TYPE_ACTION = 2;

    private static final int ACTION_COPY = 1;
    private static final int ACTION_SHARE = 2;
    private static final int ACTION_RESTORE = 3;
    private static final int ACTION_EXPORT_FILE = 4;
    private static final int ACTION_IMPORT_FILE = 5;

    private static final int REQUEST_EXPORT_FILE = 520;
    private static final int REQUEST_IMPORT_FILE = 521;
    private static final int MAX_BACKUP_BYTES = 2 * 1024 * 1024;

    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.vay_backup_title));
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

        rows.add(Row.header(LocaleController.getString(R.string.vay_backup_contents)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_global_settings),
                String.valueOf(VayTelegram.settings().countCustomized(app.vaygram.core.settings.VayScopeKey.GLOBAL))
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_account_settings),
                String.valueOf(VayTelegram.settings().countCustomized(VayTelegram.accountScope(currentAccount)))
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_saved_profiles),
                String.valueOf(VayTelegram.presets().count())
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_palette),
                String.valueOf(VayTelegram.themePalette().countOverrides())
        ));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_gradient),
                VayTelegram.storedNavigationGradient() == null
                        ? LocaleController.getString(R.string.vay_diagnostics_disabled)
                        : LocaleController.getString(R.string.vay_enabled)
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_backup_actions)));
        rows.add(Row.action(
                ACTION_COPY,
                LocaleController.getString(R.string.vay_backup_copy),
                LocaleController.getString(R.string.vay_backup_copy_hint)
        ));
        rows.add(Row.action(
                ACTION_SHARE,
                LocaleController.getString(R.string.vay_backup_share),
                LocaleController.getString(R.string.vay_backup_share_hint)
        ));
        rows.add(Row.action(
                ACTION_RESTORE,
                LocaleController.getString(R.string.vay_backup_restore),
                LocaleController.getString(R.string.vay_backup_restore_hint)
        ));
        rows.add(Row.action(
                ACTION_EXPORT_FILE,
                LocaleController.getString(R.string.vay_backup_export_file),
                LocaleController.getString(R.string.vay_backup_export_file_hint)
        ));
        rows.add(Row.action(
                ACTION_IMPORT_FILE,
                LocaleController.getString(R.string.vay_backup_import_file),
                LocaleController.getString(R.string.vay_backup_import_file_hint)
        ));

        rows.add(Row.header(LocaleController.getString(R.string.vay_backup_privacy)));
        rows.add(Row.value(
                LocaleController.getString(R.string.vay_backup_excludes),
                LocaleController.getString(R.string.vay_backup_excludes_value)
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
        if (action == ACTION_COPY) {
            copyBackup();
        } else if (action == ACTION_SHARE) {
            shareBackup();
        } else if (action == ACTION_RESTORE) {
            showRestoreDialog();
        } else if (action == ACTION_EXPORT_FILE) {
            exportBackupFile();
        } else if (action == ACTION_IMPORT_FILE) {
            importBackupFile();
        }
    }

    private String buildBackup() {
        return VayBackupCodec.encode(currentAccount);
    }

    private void copyBackup() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        clipboard.setPrimaryClip(ClipData.newPlainText("vayGram backup", buildBackup()));
        Toast.makeText(
                context,
                LocaleController.getString(R.string.vay_backup_copied),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void shareBackup() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TEXT, buildBackup());
        try {
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            LocaleController.getString(R.string.vay_backup_share)
                    ),
                    510
            );
        } catch (RuntimeException e) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_share_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void exportBackupFile() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "vayGram-0.1-dev-backup.json");
        try {
            startActivityForResult(intent, REQUEST_EXPORT_FILE);
        } catch (RuntimeException e) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_file_picker_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void importBackupFile() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        String[] mimeTypes = {"application/json", "text/json", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        try {
            startActivityForResult(intent, REQUEST_IMPORT_FILE);
        } catch (RuntimeException e) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_file_picker_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        Uri uri = data.getData();
        if (requestCode == REQUEST_EXPORT_FILE) {
            writeBackupToUri(uri);
        } else if (requestCode == REQUEST_IMPORT_FILE) {
            readBackupFromUri(uri);
        }
    }

    private void writeBackupToUri(Uri uri) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        byte[] data = buildBackup().getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = context.getContentResolver().openOutputStream(uri, "wt")) {
            if (output == null) {
                throw new IOException("Unable to open backup destination");
            }
            output.write(data);
            output.flush();
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_file_exported),
                    Toast.LENGTH_SHORT
            ).show();
        } catch (IOException | RuntimeException e) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_file_write_failed),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void readBackupFromUri(Uri uri) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new IOException("Unable to open backup source");
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > MAX_BACKUP_BYTES) {
                    Toast.makeText(
                            context,
                            LocaleController.getString(R.string.vay_backup_file_too_large),
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                output.write(buffer, 0, read);
            }
            String json = new String(output.toByteArray(), StandardCharsets.UTF_8).trim();
            if (json.isEmpty() || !json.contains("vaygram_portable_backup")) {
                Toast.makeText(
                        context,
                        LocaleController.getString(R.string.vay_backup_invalid),
                        Toast.LENGTH_LONG
                ).show();
                return;
            }
            confirmRestore(json);
        } catch (IOException | RuntimeException e) {
            Toast.makeText(
                    context,
                    LocaleController.getString(R.string.vay_backup_file_read_failed),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void showRestoreDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(6);
        input.setMaxLines(14);
        input.setHint(LocaleController.getString(R.string.vay_backup_paste_hint));

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()
                && clipboard.getPrimaryClip() != null
                && clipboard.getPrimaryClip().getItemCount() > 0) {
            CharSequence text = clipboard.getPrimaryClip().getItemAt(0).coerceToText(context);
            if (text != null && text.toString().contains("vaygram_portable_backup")) {
                input.setText(text);
                input.setSelection(input.length());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_backup_restore_title));
        builder.setMessage(LocaleController.getString(R.string.vay_backup_restore_body));
        builder.setView(input);
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_backup_restore), (dialog, which) -> {
            String json = input.getText().toString().trim();
            if (json.isEmpty()) {
                Toast.makeText(
                        context,
                        LocaleController.getString(R.string.vay_backup_invalid),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            confirmRestore(json);
        });
        showDialog(builder.create());
    }

    private void confirmRestore(String json) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.vay_backup_confirm_title));
        builder.setMessage(LocaleController.getString(R.string.vay_backup_confirm_body));
        builder.setNegativeButton(LocaleController.getString(R.string.vay_cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.vay_apply), (dialog, which) -> {
            try {
                VayBackupCodec.RestoreResult result = VayBackupCodec.restore(currentAccount, json);
                Toast.makeText(
                        context,
                        LocaleController.formatString(
                                R.string.vay_backup_restored,
                                result.getAppliedSettings(),
                                result.getRestoredProfiles(),
                                result.getPaletteOverrides()
                        ),
                        Toast.LENGTH_LONG
                ).show();
                rebuildRows();
            } catch (RuntimeException e) {
                Toast.makeText(
                        context,
                        LocaleController.getString(R.string.vay_backup_invalid),
                        Toast.LENGTH_LONG
                ).show();
            }
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

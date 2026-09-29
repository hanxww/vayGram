package app.vaygram.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Locale;

import app.vaygram.core.theme.VayGradientSpec;
import app.vaygram.telegram.VayTelegram;
import app.vaygram.theme.VayGradientBridge;

public final class VayGradientActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_VALUE = 2;
    private static final int TYPE_PREVIEW = 3;

    private static final int ROW_ENABLED = 1;
    private static final int ROW_START_COLOR = 2;
    private static final int ROW_END_COLOR = 3;
    private static final int ROW_ANGLE = 4;
    private static final int ROW_RESET = 5;

    private final ArrayList<Row> rows = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter adapter;
    private GradientPreviewView previewView;

    @Override
    public View createView(Context context) {
        VayTelegram.ensureInitialized();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("vayGram Gradients");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        rows.add(Row.header("Live Preview"));
        rows.add(Row.preview());
        rows.add(Row.header("Bottom navigation"));
        rows.add(Row.check(ROW_ENABLED));
        rows.add(Row.value(ROW_START_COLOR));
        rows.add(Row.value(ROW_END_COLOR));
        rows.add(Row.value(ROW_ANGLE));

        if (VayTelegram.storedNavigationGradient() != null) {
            rows.add(Row.header("Reset"));
            rows.add(Row.value(ROW_RESET));
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (previewView != null) {
            previewView.refresh();
        }
    }

    private void onRowClicked(int position) {
        if (position < 0 || position >= rows.size()) {
            return;
        }

        Row row = rows.get(position);
        if (row.id == 0) {
            return;
        }

        VayGradientSpec current = VayTelegram.navigationGradient();
        if (row.id == ROW_ENABLED) {
            VayTelegram.commitNavigationGradient(current.withEnabled(!current.isEnabled()));
            rebuildRows();
        } else if (row.id == ROW_START_COLOR) {
            showColorEditor(true);
        } else if (row.id == ROW_END_COLOR) {
            showColorEditor(false);
        } else if (row.id == ROW_ANGLE) {
            showAnglePicker();
        } else if (row.id == ROW_RESET) {
            showResetDialog();
        }
    }

    private void showColorEditor(boolean start) {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        final VayGradientSpec original = VayTelegram.navigationGradient();
        final int initialColor = start ? original.getStartColor() : original.getEndColor();
        final boolean[] settled = {false};

        ColorEditorView editor = new ColorEditorView(context, initialColor);
        editor.setOnColorChangedListener(color -> {
            VayGradientSpec preview = start
                    ? original.withStartColor(color)
                    : original.withEndColor(color);
            VayTelegram.previewNavigationGradient(preview);
            if (previewView != null) {
                previewView.refresh();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(start ? "Gradient start" : "Gradient end");
        builder.setMessage("ARGB color · live preview");
        builder.setView(editor);
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            settled[0] = true;
            VayTelegram.restoreNavigationGradientPreview(original);
            rebuildRows();
        });
        builder.setPositiveButton("Apply", (dialog, which) -> {
            settled[0] = true;
            VayGradientSpec applied = start
                    ? original.withStartColor(editor.getColor())
                    : original.withEndColor(editor.getColor());
            VayTelegram.commitNavigationGradient(applied);
            rebuildRows();
        });
        builder.setOnDismissListener(dialog -> {
            if (!settled[0]) {
                VayTelegram.restoreNavigationGradientPreview(original);
                rebuildRows();
            }
        });
        showDialog(builder.create());
    }

    private void showAnglePicker() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        final int[] angles = {0, 45, 90, 135, 180, 225, 270, 315};
        CharSequence[] labels = new CharSequence[angles.length];
        for (int i = 0; i < angles.length; i++) {
            labels[i] = angleLabel(angles[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Gradient angle");
        builder.setItems(labels, (dialog, which) -> {
            VayGradientSpec current = VayTelegram.navigationGradient();
            VayTelegram.commitNavigationGradient(
                    current.withAngleDegrees(angles[which])
            );
            rebuildRows();
        });
        builder.setNegativeButton("Cancel", null);
        showDialog(builder.create());
    }

    private void showResetDialog() {
        Context context = getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reset navigation gradient?");
        builder.setMessage("Remove the saved gradient and return to the current vayGram theme colors.");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Reset", (dialog, which) -> {
            VayTelegram.resetNavigationGradient();
            Toast.makeText(context, "Gradient reset", Toast.LENGTH_SHORT).show();
            rebuildRows();
        });
        showDialog(builder.create());
    }

    private String colorValue(int color) {
        return String.format(Locale.US, "#%08X", color);
    }

    private String angleLabel(int angle) {
        switch (angle) {
            case 0: return "0° · left → right";
            case 45: return "45° · bottom-left → top-right";
            case 90: return "90° · bottom → top";
            case 135: return "135° · bottom-right → top-left";
            case 180: return "180° · right → left";
            case 225: return "225° · top-right → bottom-left";
            case 270: return "270° · top → bottom";
            case 315: return "315° · top-left → bottom-right";
            default: return angle + "°";
        }
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
            return row.id != 0;
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
            if (row.id == ROW_ENABLED) {
                return TYPE_CHECK;
            }
            return TYPE_VALUE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_PREVIEW) {
                previewView = new GradientPreviewView(context);
                previewView.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        AndroidUtilities.dp(132)
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
                previewView = (GradientPreviewView) holder.itemView;
                previewView.refresh();
                return;
            }

            VayGradientSpec spec = VayTelegram.navigationGradient();
            if (type == TYPE_CHECK) {
                ((TextCheckCell) holder.itemView).setTextAndCheck(
                        "Enabled",
                        spec.isEnabled(),
                        false
                );
                return;
            }

            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            if (row.id == ROW_START_COLOR) {
                cell.setTextAndValue("Start color", colorValue(spec.getStartColor()), false);
            } else if (row.id == ROW_END_COLOR) {
                cell.setTextAndValue("End color", colorValue(spec.getEndColor()), false);
            } else if (row.id == ROW_ANGLE) {
                cell.setTextAndValue("Angle", angleLabel(spec.getAngleDegrees()), false);
            } else if (row.id == ROW_RESET) {
                cell.setTextAndValue("Reset gradient", "Use theme colors", false);
            }
        }
    }

    private final class GradientPreviewView extends View {
        private final GradientDrawable drawable = new GradientDrawable();

        private GradientPreviewView(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        }

        private void refresh() {
            VayGradientSpec spec = VayTelegram.navigationGradient();
            int start = spec.isEnabled()
                    ? spec.getStartColor()
                    : VayTelegram.navigationGradient().getStartColor();
            int end = spec.isEnabled()
                    ? spec.getEndColor()
                    : start;

            VayGradientSpec preview = new VayGradientSpec(
                    spec.getTargetId(),
                    true,
                    start,
                    end,
                    spec.getAngleDegrees()
            );
            VayGradientBridge.apply(
                    drawable,
                    preview,
                    AndroidUtilities.dp(28),
                    255
            );
            setBackground(drawable);
            invalidate();
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
            String[] names = {"Alpha", "Red", "Green", "Blue"};
            for (int i = 0; i < labels.length; i++) {
                labels[i].setText(names[i] + "  " + channels[i].getProgress());
            }
        }
    }

    private static final class Row {
        private final String header;
        private final int id;
        private final boolean preview;

        private Row(String header, int id, boolean preview) {
            this.header = header;
            this.id = id;
            this.preview = preview;
        }

        private static Row header(String title) {
            return new Row(title, 0, false);
        }

        private static Row check(int id) {
            return new Row(null, id, false);
        }

        private static Row value(int id) {
            return new Row(null, id, false);
        }

        private static Row preview() {
            return new Row(null, 0, true);
        }
    }
}

package com.dctimer.adapter;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.activity.SessionActivity;
import com.dctimer.database.SessionManager;
import com.dctimer.model.Session;
import com.dctimer.util.StringUtils;
import com.dctimer.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meigen on 2016/8/11.
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.ViewHolder> {
    //private Context context;
    private APP app;
    private SessionManager sessionManager;
    private SessionActivity parent;
    private int select;
    private boolean mod;
    private boolean editMode;
    private EditText editText;
    private List<Boolean> checkItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        CheckBox checkBox;
        TextView textView;
        TextView detailView;
        ImageButton btnInfo;
        ImageView imageDrag;

        public ViewHolder(View view) {
            super(view);
            item = view;
            checkBox = view.findViewById(R.id.check);
            textView = view.findViewById(R.id.tv_name);
            detailView = view.findViewById(R.id.tv_detail);
            //infoView = view.findViewById(R.id.iv_option);
            btnInfo = view.findViewById(R.id.bt_info);
            imageDrag = view.findViewById(R.id.iv_drag);
        }
    }

    public SessionListAdapter(SessionActivity activity) {
        this.parent = activity;
        app = APP.getInstance();
        sessionManager = app.getSessionManager();
        checkItems = new ArrayList<>();
        for (int i = 0; i < sessionManager.getSessionLength(); i++)
            checkItems.add(false);
    }

    public void setSelect(int idx) {
        select = idx;
    }

    public int getSelect() {
        return select;
    }

    public boolean getMod() {
        return mod;
    }

    public void enableEditMode(boolean edit) {
        editMode = edit;
        notifyDataSetChanged();
    }

    public void addCheckItem(boolean check) {
        checkItems.add(check);
    }

    public boolean getChecked(int pos) {
        return checkItems.get(pos);
    }

    public void moveItem(int from, int to) {
        boolean check = checkItems.remove(from);
        checkItems.add(to, check);
    }

    public int getCheckedCount() {
        int count = 0;
        for (int i=0; i<checkItems.size(); i++) {
            if (checkItems.get(i))
                count++;
        }
        return count;
    }

    public void removeCheckItem(int i) {
        checkItems.remove(i);
        if (select >= checkItems.size()) {
            select = checkItems.size() - 1;
        }
        mod = true;
    }

    @Override
    public int getItemCount() {
        return sessionManager.getSessionLength();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int viewType) {
        View view = LayoutInflater.from(parent).inflate(R.layout.session_list_item, vg, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (editMode) {
                    if (pos < checkItems.size()) {
                        checkItems.set(pos, !checkItems.get(pos));
                        notifyItemChanged(pos);
                    } else Log.e("dct", "index超出范围");
                } else {
                    if (select == pos) return;
                    select = pos;
                    notifyDataSetChanged();
                    mod = true;
                }
            }
        });
        holder.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getAdapterPosition();
                LayoutInflater factory = LayoutInflater.from(parent);
                int layoutId = R.layout.dialog_session_name;
                View v = factory.inflate(layoutId, null);
                editText = v.findViewById(R.id.edit_name);
                final Session session = sessionManager.getSession(position);
                String sname = session.getName();
                if (sname.length() == 0) {
                    if (position == 0)
                        editText.setHint(R.string.default_session);
                    else editText.setHint(parent.getString(R.string.session) + (position + 1));
                } else {
                    editText.setText(sname);
                    editText.setSelection(sname.length());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(parent).setTitle(R.string.session_name).setView(v).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sname = editText.getText().toString();
                        sessionManager.setSessionName(position, sname);
                        notifyDataSetChanged();
                        mod = true;
                    }
                }).setNegativeButton(R.string.action_clear, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (app.getResult().length() != 0) {
                            new AlertDialog.Builder(parent).setTitle(R.string.confirm_clear_session)
                                    .setNegativeButton(R.string.btn_cancel, null)
                                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            app.getResult().clear();
                                            mod = true;
                                            sessionManager.getSession(position).setCount(0);
                                            notifyDataSetChanged();
                                        }
                                    }).show();
                        }
                    }
                });
                if (sessionManager.getSessionLength() > 1)
                    builder.setNeutralButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AlertDialog.Builder(parent).setTitle(R.string.confirm_delete_session).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sessionManager.removeSession(position);
                                    if (select >= sessionManager.getSessionLength()) {
                                        select = sessionManager.getSessionLength() - 1;
                                    } else if (position < select) {
                                        select--;
                                    }
                                    mod = true;
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton(R.string.btn_cancel, null).show();
                        }
                    });
                builder.show();
                Utils.showKeyboard(editText);
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                checkItems.set(position, holder.checkBox.isChecked());
            }
        });
        holder.imageDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    parent.startDragItem(holder);
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Session session = sessionManager.getSession(i);
        String name = session.getName();
        if (TextUtils.isEmpty(name)) {
            name = "(" + parent.getString(R.string.session) + (i + 1) + ")";
        }
        holder.textView.setText(name);
        if (select == i) {
            holder.textView.setTextColor(0xff0088ff);
        } else holder.textView.setTextColor(parent.getResources().getColor(R.color.colorText));
        int puzzle = session.getPuzzle();
        int idx = puzzle >> 5;
        int sub = puzzle & 31;
        int count = session.getCount();
        holder.detailView.setText(StringUtils.getScrambleName(idx, sub) + "\n" + parent.getString(R.string.num_of_solve) + count);
        holder.checkBox.setVisibility(editMode ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(checkItems.get(i));
        holder.imageDrag.setVisibility(editMode ? View.VISIBLE : View.GONE);
        holder.btnInfo.setVisibility(editMode ? View.GONE : View.VISIBLE);
    }
}

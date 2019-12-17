package com.dctimer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.adapter.CallbackItemTouch;
import com.dctimer.adapter.ItemTouchHelperCallback;
import com.dctimer.adapter.SessionListAdapter;
import com.dctimer.database.SessionManager;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import static com.dctimer.APP.screenOri;
import static com.dctimer.APP.SCREEN_ORIENTATION;

public class SessionActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private RecyclerView rvSession;
    private SessionListAdapter adapter;
    private EditText editText;
    private Menu mMenu;
    private boolean editMode;
    private boolean moved;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    //5.0
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_session);
        LinearLayout layout = findViewById(R.id.layout);
        layout.setBackgroundColor(APP.colors[0]);
        int grey = Utils.greyScale(APP.colors[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0
            if (grey > 200) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
            if (grey > 200) {
                getWindow().setStatusBarColor(0x44000000);
            } else {
                getWindow().setStatusBarColor(0);
            }
        }

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(APP.colors[0]);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setItemColor(APP.colors[1]);

        sessionManager = APP.getInstance().getSessionManager();

        rvSession = findViewById(R.id.rv_session);
        rvSession.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionListAdapter(this);
        adapter.setSelect(APP.sessionIdx);
        rvSession.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(new CallbackItemTouch() {
            @Override
            public void itemTouchOnMove(int oldPosition, int newPosition) {
                if (oldPosition != newPosition) {
                    Log.w("dct", oldPosition+"->"+newPosition);
                    sessionManager.move(oldPosition, newPosition);
                    adapter.moveItem(oldPosition, newPosition);
                    adapter.notifyItemMoved(oldPosition, newPosition);
                    moved = true;
                    if (adapter.getSelect() == oldPosition) {
                        adapter.setSelect(newPosition);
                    }
                }
            }
        });
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvSession);

//        lvSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != adapter.getSelect()) {
//                    adapter.setSelect(i);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });

        //屏幕方向
        setRequestedOrientation(SCREEN_ORIENTATION[screenOri]);
    }

    @Override
    public void onBackPressed() {
        if (moved) {
            //Log.w("dct", "保存分组排序");
            sessionManager.save();
        }
        Intent intent = new Intent();
        intent.putExtra("mod", adapter.getMod() | moved);
        intent.putExtra("select", adapter.getSelect());
        setResult(1, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.session, menu);
        mMenu = menu;
        //menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editMode = !editMode;
                //mMenu.getItem(0).setVisible(editMode);
                mMenu.getItem(1).setVisible(editMode);
                adapter.enableEditMode(editMode);
                return true;
            case R.id.action_add:
                LayoutInflater factory = LayoutInflater.from(this);
                int layoutId = R.layout.dialog_session_name;
                View view = factory.inflate(layoutId, null);
                editText = view.findViewById(R.id.edit_name);
                editText.setHint(getString(R.string.session) + (sessionManager.getSessionLength() + 1));
                new AlertDialog.Builder(this).setTitle(R.string.add_session).setView(view)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = editText.getText().toString();
                                sessionManager.addSession(name);
                                adapter.addCheckItem(false);
                                adapter.notifyDataSetChanged();
                            }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                Utils.showKeyboard(editText);
                return true;
            case R.id.action_delete:
                int count = adapter.getCheckedCount();
                if (count > 0) {
                    new AlertDialog.Builder(this).setTitle(R.string.delete_items).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            for (int i = sessionManager.getSessionLength() - 1; i > 0; i--) {
                                if (adapter.getChecked(i)) {
                                    sessionManager.removeSession(i);
                                    adapter.removeCheckItem(i);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton(R.string.btn_cancel, null).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startDragItem(RecyclerView.ViewHolder holder) {
        itemTouchHelper.startDrag(holder);
    }
}

package com.dctimer.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.adapter.StatAdapter;
import com.dctimer.dialog.FileSelectorDialog;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.dctimer.APP.*;

public class DetailActivity extends AppCompatActivity {
    private EditText editText, et2;
    FileSelectorDialog dialog;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    //private ProgressBar progress;
    private RecyclerView rvStat;
    private StatAdapter stAdapter;

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
        setContentView(R.layout.activity_detail);

        LinearLayout layout = findViewById(R.id.layout);
        layout.setBackgroundColor(colors[0]);
        int grey = Utils.greyScale(colors[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0
            if (grey > 200) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
            if (grey > 200) {
                window.setStatusBarColor(0x44000000);
            } else {
                window.setStatusBarColor(0);
            }
        }

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(colors[0]);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setItemColor(colors[1]);

        rvStat = findViewById(R.id.rv_detail);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rvStat.setLayoutManager(lm);
        //rvStat.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //屏幕方向
        setRequestedOrientation(SCREEN_ORIENTATION[screenOri]);

        //tvDetail.setText(MainActivity.statDetail);
        Intent i = getIntent();
        int avg = i.getIntExtra("avg", 0);
        int pos = i.getIntExtra("pos", 0);
        int len = i.getIntExtra("len", 0);
        ArrayList<Integer> trimIdx = i.getIntegerArrayListExtra("trim");
        String[] stat = i.getStringArrayExtra("detail");
        stAdapter = new StatAdapter(avg, pos, len, stat, trimIdx);
        rvStat.setAdapter(stAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_copy) {   //复制成绩
            // if (Build.VERSION.SDK_INT >= 11) {
                android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("text", APP.statDetail));
            //} else {
//                android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                clip.setText(tvDetail.getText().toString());
//            }
            Toast.makeText(DetailActivity.this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_save) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    return saveStat();
                } else {
                    ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                }
            } else return saveStat();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DetailActivity.this, R.string.permission_deny, Toast.LENGTH_SHORT).show();
                } else saveStat();
            }
        }
    }

    private boolean saveStat() {
        if (APP.defaultPath == null) {
            Toast.makeText(DetailActivity.this, getString(R.string.sdcard_not_exist), Toast.LENGTH_SHORT).show();
            return false;
        }
        final LayoutInflater factory = LayoutInflater.from(DetailActivity.this);
        int layoutId = R.layout.dialog_save_stat;
        View view = factory.inflate(layoutId, null);
        editText = view.findViewById(R.id.edit_scrpath);
        editText.setText(savePath);
        ImageButton btn = view.findViewById(R.id.btn_browse);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentPath = editText.getText().toString();
                File f = new File(currentPath);
                if (!f.exists()) currentPath = Environment.getExternalStorageDirectory().getPath() + File.separator;
                dialog = FileSelectorDialog.newInstance(currentPath, false);
                dialog.show(getSupportFragmentManager(), "FileSelector");
            }
        });
        et2 = view.findViewById(R.id.edit_scrfile);
        et2.requestFocus();
        et2.setText(String.format(getString(R.string.default_stats_name), formatter.format(new Date())));
        et2.setSelection(et2.getText().length());
        new AlertDialog.Builder(DetailActivity.this).setView(view).setTitle(R.string.stat_save)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        final String path = editText.getText().toString();
                        if (!path.equals(savePath)) {
                            savePath = path;
                            SharedPreferences sp = getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("scrpath", path);
                            edit.apply();
                        }
                        final String fileName=et2.getText().toString();
                        File file = new File(path+fileName);
                        if (file.isDirectory()) Toast.makeText(DetailActivity.this, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
                        else if (file.exists()) {
                            new AlertDialog.Builder(DetailActivity.this).setTitle(R.string.confirm_overwrite)
                                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int j) {
                                            Utils.saveStat(DetailActivity.this, path, fileName, APP.statDetail);
                                        }
                                    }).setNegativeButton(R.string.btn_cancel, null).show();
                        } else Utils.saveStat(DetailActivity.this, path, fileName, APP.statDetail);
                        Utils.hideKeyboard(editText);
                    }
                }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Utils.hideKeyboard(editText);
            }
        }).show();
        return true;
    }

    public void setFilePath(String path) {
        currentPath = path;
        editText.setText(path + File.separator);
    }

//    private void saveStat(String path, String fileName, String stat) {
//        File fPath = new File(path);
//        if (fPath.exists() || fPath.mkdir() || fPath.mkdirs()) {
//            try {
//                OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
//                byte [] bytes = stat.getBytes();
//                out.write(bytes);
//                out.close();
//                Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                Toast.makeText(this, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
//            }
//        }
//        else Toast.makeText(this, getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
//    }
}

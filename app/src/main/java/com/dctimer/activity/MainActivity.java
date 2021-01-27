package com.dctimer.activity;

import static com.dctimer.APP.*;
import static com.dctimer.adapter.SettingAdapter.ST_IMAGE_SIZE;
import static com.dctimer.adapter.SettingAdapter.ST_OPACITY;
import static com.dctimer.adapter.SettingAdapter.ST_SCR_FONT;
import static com.dctimer.adapter.SettingAdapter.ST_SENSITIVITY;
import static com.dctimer.adapter.SettingAdapter.ST_START_DELAY;
import static com.dctimer.adapter.SettingAdapter.ST_TIMER_SIZE;
import static scrambler.Scrambler.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.support.design.widget.NavigationView;
import android.widget.*;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.adapter.*;
import com.dctimer.database.SessionManager;
import com.dctimer.dialog.*;
import com.dctimer.model.SmartCube;
import com.dctimer.model.Result;
import com.dctimer.model.Stackmat;
import com.dctimer.model.Timer;
import com.dctimer.util.*;
import com.dctimer.view.*;
import com.dctimer.widget.*;
import com.dingmouren.colorpicker.ColorPickerDialog;
import com.dingmouren.colorpicker.OnColorPickerListener;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import cs.threephase.Util;
import scrambler.Scrambler;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private APP app;
    public Context context;
    private DrawerLayout drawer;
    private CustomToolbar toolbar;    //工具栏
    private NavigationView navigationView;
    private RelativeLayout frame;
    private TabHost tabHost;
    private AppCompatTextView tvScramble;
    private Button btnScramble;  //打乱按钮
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private TextView tvTimer;   //计时器
    private ImageView scrambleView; //打乱图案
    private Bitmap bmScrambleView;
    private TextView tvStat;    //统计简要
    private TextView tvMulPhase;
    private PopupWindow popupWindow;	//打乱弹出窗口
    private TextAdapter s1Adapter;
    private TextAdapter s2Adapter;
    //private ListView listView;
    private View view;
    private CenterRadioButton rbTimer, rbResult, rbSetting;
    private ProgressBar pbScramble;
    private ProgressBar pbScan;
    private TextView tvTest;
    private Button btnScan;

    private LinearLayout llSession;
    private LinearLayout llSearch;
    private LinearLayout llTitle;	//成绩标题
    private ListView lvResult;	//成绩列表
    //private RecyclerView rvResult;  //成绩列表
    //public ResultAdapter resAdapter;
    public TimesAdapter resAdapter;
    private Button btnSession;
    private Button btnSessionMean;
    private ImageButton btnNext, btnPrev;
    private ClearEditText editSearch;
    private EditText editText;
    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private FileSelectorDialog fileSelectorDialog;

    private SettingAdapter stAdapter;
    private RecyclerView rvSetting;    //设置列表
    private ColorSchemeView colorSchemeView;
    public Bitmap bitmap;

    private SharedPreferences sp; //保存配置
    //public static SharedPreferences.Editor edit;
    public Vibrator vibrator;
    public Scrambler currentScramble;
    public Scrambler nextScramble;
    public Timer timer;
    public Result result;
    public SessionManager sessionManager;
    private List<Integer> searchResult = new ArrayList<>();
    private int searchIndex;

    public boolean canStart;
    public int lastScrambleType = -64;
    private boolean isSwipe;
    private boolean touchDown;
    private boolean scrambleGenerating = false;
    private int curTab;
    private int dip40;
    private int mpCount;
    private int selectIdx, selectIdx2;
    private int startX, startY;
    private int gesture;
    private int version;
    private long exitTime = 0;
    //private List<String> nextScramble = new ArrayList<>();
    private String newVersion, updateCont;
    private TextToSpeech tts;
    private SensorManager sensorManager;
    private Sensor sensor;
    private double lastAcc;

    private Stackmat stackmat;
    private BluetoothTools bluetoothTools;
    private BluetoothDeviceAdapter adapter;
    //private SmartCube cube;

    private static final String[] PERMISSIONS = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final int[] VIBRATE_TIME = {30, 50, 80, 150, 240};
    private static final int[] ITEMS_ID = {R.array.opt_enter_time, R.array.opt_timer_update, R.array.opt_accuracy, R.array.opt_multi_phase,
            R.array.opt_average, R.array.opt_solve_333, R.array.opt_solve_222, R.array.opt_mega_scheme,
            R.array.opt_timer_font, R.array.opt_screen_ori, R.array.opt_vibrate, R.array.opt_vibrate_time,
            R.array.opt_sq_solver, R.array.opt_time_format, R.array.opt_average, R.array.opt_gesture, R.array.opt_decimal};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    //5.0
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(0);
            //window.setNavigationBarColor(0xff000000);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   //4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //setPrimaryDark();
        }
        setContentView(R.layout.activity_main);
        context = this;
        app = APP.getInstance();
        sp = super.getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
        //edit = sp.edit();
        dm = getResources().getDisplayMetrics();
        dpi = dm.density;
        fontScale = dm.scaledDensity;
        dip300 = Math.round(dpi * 300);
        dip40 = Math.round(dpi * 40);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //System.out.println(dpi+", "+dm.widthPixels);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            defaultPath = Environment.getExternalStorageDirectory().getPath() + "/DCTimer/";
        }
        dataPath = getFilesDir().getParent() + "/databases/";
        app.readPref(sp);
        StringUtils.scrambleItems = getResources().getStringArray(R.array.item_scr);
        StringUtils.scrambleSubitems = new String[StringUtils.scrambleItems.length][];
        int[] subid = {R.array.item_wca, R.array.item_222, R.array.item_333, R.array.item_444, R.array.item_555, R.array.item_666,
                R.array.item_666, R.array.item_mega, R.array.item_pyr, R.array.item_sq1, R.array.item_clk, R.array.item_skewb,
                R.array.item_mnl, R.array.item_cmt, R.array.item_gear, R.array.item_smc, R.array.item_15p, R.array.item_other,
                R.array.item_333_sub, R.array.item_bandage, R.array.item_minx_sub, R.array.item_relay};
        for (int i = 0; i < subid.length; i++)
            StringUtils.scrambleSubitems[i] = getResources().getStringArray(subid[i]);
        for (int i = 0; i < itemStr.length; i++)
            itemStr[i] = getResources().getStringArray(ITEMS_ID[i]);
        version = Utils.getVersion(context);
        if (screenOn) acquireWakeLock();
        toolbar = findViewById(R.id.toolbar); //工具栏
        toolbar.setTitle("");
        //toolbar.setBackgroundColor(0x10ffffff);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);  //导航栏
        navigationView.setNavigationItemSelectedListener(this);
        frame = findViewById(R.id.main_layout);
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        int[] ids = {R.id.tab_timer, R.id.tab_result, R.id.tab_settings};
        for (int i = 0; i < 3; i++) {
            TabHost.TabSpec myTab = tabHost.newTabSpec("tab" + i);
            myTab.setIndicator("tab");
            myTab.setContent(ids[i]);
            tabHost.addTab(myTab);
        }
        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                invalidateOptionsMenu();
            }
        });
        if (useBgcolor) {
            setBackground(colors[0]);
        } else setBackground();

        RadioGroup radioTab = findViewById(R.id.radio_tab);
        radioTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_timer:
                        curTab = 0;
                        break;
                    case R.id.rb_result:
                        curTab = 1;
                        break;
                    case R.id.rb_setting:
                        curTab = 2;
                        break;
                }
                tabHost.setCurrentTab(curTab);
            }
        });
        rbTimer = findViewById(R.id.rb_timer);
        rbResult = findViewById(R.id.rb_result);
        rbSetting = findViewById(R.id.rb_setting);

        //计时
        tvScramble = findViewById(R.id.tv_scramble);
        btnScramble = findViewById(R.id.bt_scramble);    //打乱按钮
        btnScramble.setOnClickListener(mOnClickListener);
        pbScramble = findViewById(R.id.progress);
        pbScramble.getIndeterminateDrawable().setColorFilter(colors[1], PorterDuff.Mode.SRC_IN);
        btnLeft = findViewById(R.id.bt_left);
        btnLeft.setOnClickListener(mOnClickListener);
        btnRight = findViewById(R.id.bt_right);
        btnRight.setOnClickListener(mOnClickListener);
        tvTimer = findViewById(R.id.tv_timer);
        tvTimer.setOnTouchListener(mOnTouchListener);
        scrambleView = findViewById(R.id.iv_scramble);
        int tvHeight = (int) (dm.heightPixels - 76 * dpi) / 2;
        tvScramble.setHeight(tvHeight);
        //tvScramble.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvStat = findViewById(R.id.tv_stat);
        tvMulPhase = findViewById(R.id.tv_multi_phase);
        //成绩
        llSession = findViewById(R.id.ll_session);
        llSearch = findViewById(R.id.ll_search);
        llTitle = findViewById(R.id.ll_title);
        //lvTimes = findViewById(R.id.lv_times);
        lvResult = findViewById(R.id.list_res);
//        rvResult = findViewById(R.id.rv_result);
//        LinearLayoutManager lm = new LinearLayoutManager(this);
//        lm.setOrientation(LinearLayoutManager.VERTICAL);
//        rvResult.setLayoutManager(lm);
        btnSession = findViewById(R.id.btn_session);
        btnSession.setOnClickListener(mOnClickListener);
        btnSession.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                sessionManager.updateSessionCount();
                Intent intent = new Intent(context, SessionActivity.class);
                startActivityForResult(intent, 2);
                return true;
            }
        });
        ImageButton btSearch = findViewById(R.id.btn_search);
        btSearch.setOnClickListener(mOnClickListener);
        ImageButton btClear = findViewById(R.id.btn_clear);
        btClear.getDrawable().setColorFilter(0xff007aff, PorterDuff.Mode.SRC_ATOP);
        btClear.setOnClickListener(mOnClickListener);
        editSearch = findViewById(R.id.edit_search);
        editSearch.addTextChangedListener(mTextWatcher);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(mOnClickListener);
        btnPrev = findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(mOnClickListener);
        Button btCancel = findViewById(R.id.btn_cancel);
        btCancel.setOnClickListener(mOnClickListener);
        btnSessionMean = findViewById(R.id.btn_session_mean);
        btnSessionMean.setOnClickListener(mOnClickListener);
        //StateListDrawable sldraw = (StateListDrawable) btnSessionMean.getBackground();
        //ShapeDrawable sdraw = (ShapeDrawable) btnSessionMean.getBackground();
        //btnSessionMean.setBackgroundDrawable(sdraw);
        //设置
        Map<Integer, String> headers = new HashMap<>();
        List<Map<String, Object>> cells = new ArrayList<>();
        Utils.addSection(headers, cells, getString(R.string.title_timer), getResources().getStringArray(R.array.item_timer),
                new int[] {1, 1, 0, 0, 0, 0, 0, 2, 0, 1, 1, 1, 2},
                new Object[] {wca, inspectionAlert, itemStr[13][timeFormat], itemStr[16][decimalMark], itemStr[0][enterTime], itemStr[1][timerUpdate], itemStr[2][timerAccuracy], String.format("%.02fs", freezeTime/20f), itemStr[3][multiPhase], simulateSS, showStat, dropToStop, ""},
                new int[] {0, 0, 0, 0, 0, 0, 0, 20<<16|freezeTime, 0, 0, 0, 0, 95<<16|((int) (sensitivity *100)-5)});
        Utils.addSection(headers, cells, getString(R.string.title_scramble), getResources().getStringArray(R.array.item_scramble),
                new int[] {2, 1, 1, 2, 0},
                new Object[] {String.valueOf(scrambleSize), monoFont, showImage, "", ""},
                new int[] {18<<16|(scrambleSize-12), 0, 0, 16<<16|(imageSize/10-16), 0});
        Utils.addSection(headers, cells, getString(R.string.title_stats), getResources().getStringArray(R.array.item_stats),
                new int[] {1, 0, 0, 0, 0, 1},
                new Object[] {promptToSave, itemStr[14][avg1Type], String.valueOf(avg1len), itemStr[4][avg2Type], String.valueOf(avg2len), selectSession},
                new int[6]);
        Utils.addSection(headers, cells, getString(R.string.title_tools), getResources().getStringArray(R.array.item_tools),
                new int[6], new Object[] {itemStr[5][solve333], itemStr[12][solveSq1], itemStr[6][solve222], ""}, new int[6]);
        Utils.addSection(headers, cells, getString(R.string.title_scheme), getResources().getStringArray(R.array.item_scheme),
                new int[5], new Object[] {"", "", "", "", itemStr[7][megaColorScheme]}, new int[5]);
        Utils.addSection(headers, cells, getString(R.string.title_interface), getResources().getStringArray(R.array.item_interface),
                new int[] {0, 2, 0, 0, 0, 1, 2, 0, 0, 0},
                new Object[] {itemStr[8][timerFont], String.valueOf(timerSize), "", "", "", !useBgcolor, "", "", "", ""},
                new int[] {0, 70<<16|(timerSize-50), 0, 0, 0, 0, 80<<16|(opacity-20), 0, 0, 0, 0});
        Utils.addSection(headers, cells, getString(R.string.title_gesture), getResources().getStringArray(R.array.item_gesture),
                new int[4], new Object[] {itemStr[15][swipeType[0]], itemStr[15][swipeType[1]], itemStr[15][swipeType[2]], itemStr[15][swipeType[3]]}, new int[4]);
        Utils.addSection(headers, cells, getString(R.string.title_hardware), getResources().getStringArray(R.array.item_hardware),
                new int[] {1, 0, 0, 0},
                new Object[] {screenOn, itemStr[10][vibrateType], itemStr[11][vibrateTime], itemStr[9][screenOri]},
                new int[4]);
        //Log.w("dct", ""+cells.size());
        stAdapter = new SettingAdapter(this, headers, cells);
        rvSetting = findViewById(R.id.lv_settings);
        rvSetting.setLayoutManager(new LinearLayoutManager(context));
        rvSetting.setAdapter(stAdapter);
        //rvSetting.setOnItemClickListener(mOnItemListener);

        currentScramble = new Scrambler(sp);
        timer = new Timer(this);

        //进度条
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        //震动器
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        app.initSession(context);
        result = app.getResult();
        sessionManager = app.getSessionManager();
        if (sessionIdx >= sessionManager.getSessionLength()) sessionIdx = 0;
        getResult();
        btnSession.setText(sessionManager.getSessionName(sessionIdx));
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        result.calcAvg();
        if (multiPhase != 0) {
            result.calcMpMean();
        }

        Utils.setEgOll();
        setViews();
        setTextsColor();
        setIconColor();
        setResultTitle();

        resAdapter = new TimesAdapter(this, result);
        lvResult.setAdapter(resAdapter);
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    //Log.w("tts", "tts init");
                } else Log.e("tts", "tts失败");
            }
        });
        setScramble();

        tvTest = findViewById(R.id.tv_test);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bluetoothTools = new BluetoothTools(this);
        bluetoothTools.setCubeStateChangeCallback(cubeStateChangeCallback);
        //getBluetoothAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && sensor != null) {
            sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null && sensor != null) {
            sensorManager.unregisterListener(mSensorEventListener, sensor);
        }
    }

    @Override
    protected void onDestroy() {
        Log.w("dct", "ondestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("sel", scrambleIdx >> 5);
        edit.putInt("sel2", scrambleIdx & 0x1f);
        edit.commit();
        //Log.w("dct", "on save instance1");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Log.w("dct", "旋转屏幕");
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int tvHeight = (int) (dm.heightPixels - 76 * dpi) / 2;
        tvScramble.setHeight(tvHeight);
        showScramble();
        if (!useBgcolor) try {
            setBackground();
        } catch (Exception e) {
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Error e) {
            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {   //按返回键
        if (timer.getTimerState() == Timer.RUNNING) {
            timer.timeEnd = SystemClock.uptimeMillis();
            timer.count();
            setVisibility(true);
            if (!wca || currentScramble.isBlindfoldScramble()) { penaltyTime = 0; isDNF = false; }
            save((int) timer.time);
            timer.setTimerState(0);
            if (!screenOn) releaseWakeLock();
        } else if (timer.getTimerState() == Timer.INSPECTING) {
            timer.stopInspect();
            setTimerText("0" + (decimalMark == 0 ? "." : ",") + (timerAccuracy == 0 ? "00" : "000"));
            setVisibility(true);
            if (!screenOn) releaseWakeLock();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (curTab != 0) {
            //toolbar.setTitle(R.string.tab_timer);
            curTab = 0;
            tabHost.setCurrentTab(0);
            rbTimer.setChecked(true);
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, getString(R.string.exit_tip), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("sel", scrambleIdx >> 5);
            edit.putInt("sel2", scrambleIdx & 0x1f);
            edit.commit();
            app.closeDb();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //System.out.println("onPrepareMenu "+curTab);
        if (curTab == 0) {
            menu.findItem(R.id.action_scramble).setVisible(true);
            menu.findItem(R.id.action_import_scramble).setVisible(true);
            menu.findItem(R.id.action_export_scramble).setVisible(true);
            menu.findItem(R.id.action_last).setVisible(true);
        } else {
            menu.findItem(R.id.action_scramble).setVisible(false);
            menu.findItem(R.id.action_import_scramble).setVisible(false);
            menu.findItem(R.id.action_export_scramble).setVisible(false);
            menu.findItem(R.id.action_last).setVisible(false);
        }
        if (curTab == 1) {
            menu.findItem(R.id.action_rename).setVisible(true);
            menu.findItem(R.id.action_sort).setVisible(true);
            menu.findItem(R.id.action_histogram).setVisible(true);
            menu.findItem(R.id.action_graph).setVisible(true);
            menu.findItem(R.id.action_daily).setVisible(true);
        } else {
            menu.findItem(R.id.action_rename).setVisible(false);
            menu.findItem(R.id.action_sort).setVisible(false);
            menu.findItem(R.id.action_histogram).setVisible(false);
            menu.findItem(R.id.action_graph).setVisible(false);
            menu.findItem(R.id.action_daily).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final LayoutInflater factory;
        switch (id) {
            case R.id.action_scramble:  //打乱详情
                ScrambleDetailDialog scrambleDialog = ScrambleDetailDialog.newInstance(currentScramble.getScramble(), currentScramble.getScrambleLen(), currentScramble.is333Scramble() ? 3 : 0);
                scrambleDialog.show(getSupportFragmentManager(), "ScrambleDetail");
                break;
            case R.id.action_import_scramble:   //导入打乱
                ImportScrambleDialog dialog = ImportScrambleDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "ImportScramble");
                break;
            case R.id.action_export_scramble:   //导出打乱
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //权限已授权 GRANTED:授权 DENIED:拒绝
                    if (ContextCompat.checkSelfPermission(context, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                        ExportScrambleDialog.newInstance(btnScramble.getText().toString()).show(getSupportFragmentManager(), "ExportScramble");
                    } else ActivityCompat.requestPermissions(this, PERMISSIONS, 4);
                } else ExportScrambleDialog.newInstance(btnScramble.getText().toString()).show(getSupportFragmentManager(), "ExportScramble");
                break;
            case R.id.action_last:  //上一次成绩
                if (result.length() != 0) {
                    showDetail(result.length() - 1);
                }
                break;
            case R.id.action_rename:    //分组命名
                factory = LayoutInflater.from(context);
                view = factory.inflate(R.layout.dialog_session_name, null);
                editText = view.findViewById(R.id.edit_name);
                String name = sessionManager.getSession(sessionIdx).getName();
                if (name.length() == 0) {
                    if (sessionIdx == 0)
                        editText.setHint(R.string.default_session);
                    else editText.setHint(getString(R.string.session) + (sessionIdx + 1));
                } else {
                    editText.setText(name);
                    editText.setSelection(name.length());
                }
                new AlertDialog.Builder(context).setTitle(R.string.session_name).setView(view)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = editText.getText().toString();
                                sessionManager.setSessionName(sessionIdx, name);
                                if (name.length() == 0) {
                                    if (sessionIdx == 0)
                                        btnSession.setText(R.string.default_session);
                                    else btnSession.setText(getString(R.string.session) + (sessionIdx + 1));
                                } else btnSession.setText(name);
                                Utils.hideKeyboard(editText);
                            }
                        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.hideKeyboard(editText);
                    }
                }).show();
                //editText.requestFocus();
                Utils.showKeyboard(editText);
                break;
            case R.id.action_sort:  //TODO 排序方式
                new AlertDialog.Builder(context).setTitle(R.string.action_sort).setSingleChoiceItems(multiPhase > 0 ? R.array.opt_sort_order2 : R.array.opt_sort_order, sortType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        llSearch.setVisibility(View.GONE);
                        llSession.setVisibility(View.VISIBLE);
                        Utils.hideKeyboard(editSearch);
                        sortType = i;
                        //Log.w("dct", "sort" + i);
                        if (sortType != 0) {
                            result.sortResult();
                        }
                        resAdapter.setHighlight(-1);
                        lvResult.setSelection(0);
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case R.id.action_histogram: //成绩分布直方图
                factory = LayoutInflater.from(context);
                view = factory.inflate(R.layout.dialog_graph, null);
                view.findViewById(R.id.layout).setVisibility(View.GONE);
                ImageView iv = view.findViewById(R.id.image_view);
                Bitmap bm = Bitmap.createBitmap(dip300, (int) (dip300 * 1.2), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bm);
                //c.drawColor(0);
                Paint p = new Paint();
                p.setAntiAlias(true);
                Graph.drawHist(result, dip300, p, c);
                iv.setImageBitmap(bm);
                new AlertDialog.Builder(context).setView(view).setNegativeButton(R.string.btn_close, null).show();
                break;
            case R.id.action_graph: //折线图
                factory = LayoutInflater.from(context);
                view = factory.inflate(R.layout.dialog_graph, null);
                iv = view.findViewById(R.id.image_view);
                TextView tv1 = view.findViewById(R.id.tv_trend1);
                if (avg1Type == 0) tv1.setText("ao" + avg1len);
                else tv1.setText("mo" + avg1len);
                TextView tv2 = view.findViewById(R.id.tv_trend2);
                if (avg2Type == 0) tv2.setText("ao" + avg2len);
                else tv2.setText("mo" + avg2len);
                bm = Bitmap.createBitmap(dip300, (int) (dip300 * 0.9), Bitmap.Config.ARGB_8888);
                c = new Canvas(bm);
                p = new Paint();
                p.setAntiAlias(true);
                Graph.drawGraph(result, dip300, p, c);
                iv.setImageBitmap(bm);
                new AlertDialog.Builder(context).setView(view)
                        .setNegativeButton(R.string.btn_close, null).show();
                break;
            case R.id.action_daily:
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share: //分享
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");	//纯文本
                intent.putExtra(Intent.EXTRA_SUBJECT, "SHARE");
                intent.putExtra(Intent.EXTRA_TEXT, Utils.getShareContent(this));
                startActivity(Intent.createChooser(intent, getTitle()));
                break;
            case R.id.action_exit:  //退出
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt("sel", scrambleIdx >> 5);
                edit.putInt("sel2", scrambleIdx & 0x1f);
                edit.commit();
                app.closeDb();
                this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_import_export:   //导入导出数据库
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                        new ImportExportDialog().newInstance().show(getSupportFragmentManager(), "ImportExport");
                    } else ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 3);
                } else new ImportExportDialog().newInstance().show(getSupportFragmentManager(), "ImportExport");
                break;
            case R.id.nav_algorithm:    //公式库
                Intent intent = new Intent(context, WebActivity.class);
                String web = "http://algdb.net";
                intent.putExtra("web", web);
                intent.putExtra("title", "AlgDb.net");
                startActivity(intent);
                break;
            case R.id.nav_algcubing: //alg.cubing
                intent = new Intent(context, WebActivity.class);
                web = "https://alg.cubing.net";
                intent.putExtra("web", web);
                intent.putExtra("title", "alg.cubing.net");
                startActivity(intent);
                break;
            case R.id.nav_test:
                //随机生成成绩
                result.insert(10000, 12000, 4000000, currentScramble.getScramble());
                btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                result.calcAvg();
                if (multiPhase > 0) result.calcMpMean();
                if (sortType != 0) result.sortResult();
                resAdapter.reload();
                lvResult.setSelection(resAdapter.getCount() - 1);
                newScramble();
                setStatsLabel();
                break;
            case R.id.nav_about:
                new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle(R.string.app_name).setMessage(String.format(getString(R.string.about_msg), Utils.getVersionName(context)))
                        .setNeutralButton(R.string.btn_upgrade, new DialogInterface.OnClickListener() { //检测更新
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread() {
                                    public void run() {
                                        handler.sendEmptyMessage(8);
                                        String url = "https://raw.githubusercontent.com/MeigenChou/DCTimer/master/release/version.txt";
                                        if (getString(R.string.language).equals("en")) url = "https://raw.githubusercontent.com/MeigenChou/DCTimer/master/release/version_en.txt";
                                        String ver = Utils.getContent(url);
                                        Log.w("DCT", ver);
                                        if (ver.startsWith("error")) {
                                            handler.sendEmptyMessage(9);
                                        } else {
                                            String[] vers = ver.split("\t");
                                            try {
                                                int v = Integer.parseInt(vers[0]);
                                                if (v > version) {
                                                    newVersion = vers[1];
                                                    StringBuilder sb = new StringBuilder(vers[2]);
                                                    for (int i = 3; i < vers.length; i++) sb.append("\n").append(vers[i]);
                                                    updateCont = sb.toString();
                                                    handler.sendEmptyMessage(16);
                                                }
                                                else handler.sendEmptyMessage(10);
                                            } catch (Exception e) {

                                            }
                                        }
                                    }
                                }.start();
                            }
                        })
                        .setNegativeButton(R.string.btn_close, null).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //背景图片
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picPath = cursor.getString(columnIndex);
                    //Log.w("pic", picPath);
                    setPref("picpath", picPath);
                    if (!useBgcolor)
                        setBackground();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2) {  //更改分组
            boolean mod = data.getBooleanExtra("mod", false);
            if (mod) {
                sortType = 0;
                sessionIdx = data.getIntExtra("select", 0);
                changeSession();
            }
        } else if (requestCode == 3) {    //显示详情
            statDetail = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.permission_deny, Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) {  //选择图片
                selectPic();
            } else if (requestCode == 3) {  //导入导出数据库
                new ImportExportDialog().newInstance().show(getSupportFragmentManager(), "ImportExport");
            } else if (requestCode == 4) {  //导出打乱
                ExportScrambleDialog.newInstance(btnScramble.getText().toString()).show(getSupportFragmentManager(), "ExportScramble");
            } else if (requestCode == 5) {  //下载
                download("DCTimer" + newVersion + ".apk");
            } else if (requestCode == 6) {  //蓝牙
                scanDevice();
            } else if (requestCode == 7) {  //Stackmat
                startStackmat();
            }
        }
    }

    private void startStackmat() {
        if (stackmat == null) {
            stackmat = new Stackmat(this);
        }
        setTimerText("---");
        stackmat.start();
    }

    private void scanDevice() {
        bluetoothTools.startScan();
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_device, null);
        btnScan = v.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(mOnClickListener);
        pbScan = v.findViewById(R.id.progress);
        RecyclerView rvDevice = v.findViewById(R.id.rv_device);
        adapter = new BluetoothDeviceAdapter(this, new ArrayList<SmartCube>());
        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        rvDevice.setAdapter(adapter);
        dialog = new AlertDialog.Builder(this).setView(v)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bluetoothTools.stopScan();
                    }
                }).setCancelable(false).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnScan.setVisibility(View.VISIBLE);
                pbScan.setVisibility(View.GONE);
                bluetoothTools.stopScan();
            }
        }, 20000);
    }

    public void refreshCubeList(List<SmartCube> list) {
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    public void refreshCubeList() {
        adapter.notifyDataSetChanged();
    }

    public void showScanButton() {
        pbScan.setVisibility(View.GONE);
        btnScan.setVisibility(View.VISIBLE);
    }

    public void connectCube(int pos) {
        bluetoothTools.connectCube(pos);
    }

    public void dismissDialog() {
        dialog.dismiss();
        adapter = null;
        canStart = false;
    }

    public void disconnectHint(final SmartCube cube) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                Toast.makeText(context, cube.getName() + getString(R.string.cube_not_connected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void moveCube(SmartCube cube, int move, int time) {
        cube.applyMove(move, time, currentScramble.getCubeState());
        if (timer.getTimerState() == Timer.READY) {
            if (canStart) {
                canStart = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.timeStart = SystemClock.uptimeMillis();
                        timer.count();
                        acquireWakeLock();
                        setVisibility(false);
                        //timer.setTimerState(Timer.RUNNING);
                    }
                });
            }
        }
    }

    public void markScrambled() {
        setTimerColor(0xff00ff00);
        timer.setTimerState(Timer.READY);
        canStart = true;
    }

    private SmartCube.StateChangedCallback cubeStateChangeCallback = new SmartCube.StateChangedCallback() {
        @Override
        public void onScrambled(SmartCube cube) {
            Log.w("dct", "已打乱");
            if (timer.getTimerState() != Timer.RUNNING) {
                cube.markScrambled();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTimerColor(0xff00ff00);
                        tvMulPhase.setText("");
                        timer.setTimerState(Timer.READY);
                        canStart = true;
                    }
                });
            }
        }

        @Override
        public void onSolved(final SmartCube cube) {
            if (timer.getTimerState() == Timer.RUNNING) {
                cube.calcResult();
                cube.markSolved();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.timeEnd = SystemClock.uptimeMillis();
                        timer.count();
                        setVisibility(true);
                        int timeRes = cube.getResult();
                        tvMulPhase.setText(String.format(Locale.getDefault(), "%d moves\n%.1f tps", cube.getMovesCount(), cube.getMovesCount() * 1000f / timeRes));
                        Log.w("dct", "成绩 "+timeRes);
                        if (!wca || currentScramble.isBlindfoldScramble()) { penaltyTime = 0; isDNF = false;}
                        timer.setTimerState(Timer.READY);
                        save(timeRes);
                    }
                });
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {    //按钮监听事件
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_scramble:	//选择打乱
                    selectIdx = scrambleIdx >> 5;
                    selectIdx2 = scrambleIdx & 0x1f;
                    if (selectIdx == -1 && selectIdx2 > 7) selectIdx2--;
                    int resId = R.layout.popup_window;
                    view = LayoutInflater.from(context).inflate(resId, null);
                    ListView listView = view.findViewById(R.id.list1);
                    s1Adapter = new TextAdapter(context, StringUtils.scrambleItems, selectIdx + 1, 1);
                    listView.setAdapter(s1Adapter);
                    listView.setSelection(selectIdx + 1);
                    listView.setOnItemClickListener(mOnItemListener);
                    listView = view.findViewById(R.id.list2);
                    s2Adapter = new TextAdapter(context, StringUtils.scrambleSubitems[selectIdx + 1], selectIdx2, 2);
                    listView.setAdapter(s2Adapter);
                    if (selectIdx2 > 5) listView.setSelection(selectIdx2 - 5);
                    //listView.setSelection(0);
                    listView.setOnItemClickListener(mOnItemListener);
                    popupWindow = new PopupWindow(view, dip300, dip300, true);
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg));
                    popupWindow.setTouchable(true);
                    popupWindow.showAsDropDown(v, (btnScramble.getWidth() - popupWindow.getWidth()) / 2, 0);
                    break;
                case R.id.btn_session_mean:	//分组平均
                    for (int i = 0; i < result.length(); i++)
                        if (!result.isDnf(i)) {
                            showAvgDetail(3, 0);
                            break;
                        }
                    break;
                case R.id.btn_session:  //分组列表
                    String[] list = sessionManager.getSessionNames();
                    new AlertDialog.Builder(context).setItems(list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sortType = 0;
                            sessionIdx = i;
                            changeSession();
                        }
                    }).setNeutralButton(R.string.title_activity_session, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sessionManager.updateSessionCount();
                            Intent intent = new Intent(context, SessionActivity.class);
                            startActivityForResult(intent, 2);
                        }
                    }).setNegativeButton(R.string.btn_cancel, null).show();
                    break;
                case R.id.btn_search:   //搜索成绩
                    llSearch.setVisibility(View.VISIBLE);
                    llSession.setVisibility(View.GONE);
                    editSearch.setText("");
                    btnNext.setVisibility(View.GONE);
                    btnPrev.setVisibility(View.GONE);
                    Utils.showKeyboard(editSearch);
                    break;
                case R.id.btn_cancel:   //取消搜索
                    llSearch.setVisibility(View.GONE);
                    llSession.setVisibility(View.VISIBLE);
                    resAdapter.setHighlight(-1);
                    Utils.hideKeyboard(editSearch);
                    break;
                case R.id.btn_next: //查找下一个
                    searchIndex++;
                    if (searchIndex >= searchResult.size()) searchIndex = 0;
                    resAdapter.setHighlight(searchResult.get(searchIndex));
                    lvResult.setSelection(searchResult.get(searchIndex));
                    break;
                case R.id.btn_prev: //查找上一个
                    searchIndex--;
                    if (searchIndex < 0) searchIndex = searchResult.size() - 1;
                    resAdapter.setHighlight(searchResult.get(searchIndex));
                    lvResult.setSelection(searchResult.get(searchIndex));
                    break;
                case R.id.btn_clear:    //清空分组
                    if (result.length() != 0) {
                        new AlertDialog.Builder(context).setTitle(R.string.confirm_clear_session)
                                .setNegativeButton(R.string.btn_cancel, null)
                                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        sortType = 0;
                                        deleteAll();
                                        setStatsLabel();
                                    }
                                }).show();
                    }
                    break;
                case R.id.bt_left: //上一个打乱
                    showLastScramble();
                    break;
                case R.id.bt_right:    //下一个打乱
                    showNextScramble();
                    break;
                case R.id.btn_scan: //扫描设备
                    btnScan.setVisibility(View.GONE);
                    pbScan.setVisibility(View.VISIBLE);
                    bluetoothTools.startScan();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener mOnItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ListView listView = (ListView) arg0;
            switch (listView.getId()) {
                case R.id.list1:
                    if (selectIdx != arg2 - 1) {
                        selectIdx = arg2 - 1;
                        s1Adapter.setSelectItem(selectIdx + 1);
                        s1Adapter.notifyDataSetChanged();
                        s2Adapter.setData(StringUtils.scrambleSubitems[selectIdx + 1]);
                        if (selectIdx == (scrambleIdx >> 5)) {
                            int idx2 = scrambleIdx & 0x1f;
                            if (selectIdx == -1 && idx2 > 7) idx2--;
                            s2Adapter.setSelectItem(idx2);
                        } else s2Adapter.setSelectItem(-1);
                        s2Adapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.list2:
                    if (selectIdx != (scrambleIdx >> 5) || selectIdx2 != arg2) {
                        selectIdx2 = arg2;
                        if (selectIdx == -1 && selectIdx2 >= 7) selectIdx2++;
                        scrambleIdx = selectIdx << 5 | selectIdx2;
                        setScramble();
                        if (selectSession) {    //自动选择分组
                            Log.w("dct", "选择分组");
                            for (int i = 0; i < sessionManager.getSessionLength(); i++) {
                                if (scrambleIdx == sessionManager.getPuzzle(i)) {
                                    Log.w("dct", "找到分组 "+i);
                                    sessionIdx = i;
                                    multiPhase = sessionManager.getMultiPhase(i);
                                    getResult();
                                    setResultTitle();
                                    btnSession.setText(sessionManager.getSessionName(sessionIdx));
                                    btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                                    result.calcAvg();
                                    if (multiPhase > 0) result.calcMpMean();
                                    resAdapter.reload();
                                    lvResult.setSelection(0);
                                    setPref("session", sessionIdx);
                                    setStatsLabel();
                                    break;
                                }
                            }
                        }
                    }
                    popupWindow.dismiss();
                    break;
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String key = charSequence.toString().trim().toLowerCase();
            if (TextUtils.isEmpty(key)) {
                btnNext.setVisibility(View.GONE);
                btnPrev.setVisibility(View.GONE);
                resAdapter.setHighlight(-1);
                return;
            }
            searchResult = result.search(key);
            //Log.w("dct", key+" 搜索:"+searchResult.size());
            if (searchResult.size() < 2) {
                btnNext.setVisibility(View.GONE);
                btnPrev.setVisibility(View.GONE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
                btnPrev.setVisibility(View.VISIBLE);
            }
            if (searchResult.size() > 0) {
                searchIndex = 0;
                resAdapter.setHighlight(searchResult.get(0));
                lvResult.setSelection(searchResult.get(0));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!simulateSS || enterTime == 1) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        isSwipe = false;
                        touchDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                    case MotionEvent.ACTION_CANCEL:
                        touchUp();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getX(), y = (int) event.getY();
                        if (timer.getTimerState() == Timer.READY) {
                            int delX = Math.abs(x - startX), delY = Math.abs(y - startY);
                            if (delX > dip40 || delY > dip40) {
                                setTimerColor(colors[1]);
                                isSwipe = true;
                                if (freezeTime > 0)
                                    timer.stopFreeze();
                                if (delX > delY) {  //左右滑动
                                    if (x > startX) {
                                        gesture = swipeType[1];
                                    } else {
                                        gesture = swipeType[0];
                                    }
                                } else if (delY > delX) {
                                    if (y > startY) {
                                        gesture = swipeType[3];
                                    } else {
                                        gesture = swipeType[2];
                                    }
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
//                  case MotionEvent.ACTION_CANCEL:
                        timer.stopFreeze();
                        setTimerColor(colors[1]);
                        break;
                }
            } else {
                int count = event.getPointerCount();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_POINTER_DOWN:
                    case 261:
                        if (count > 1) {
                            int x1 = (int) event.getX(0) * 2 / tvTimer.getWidth();
                            int x2 = (int) event.getX(1) * 2 / tvTimer.getWidth();
                            if ((x1 ^ x2) == 1) {
                                if (!touchDown) {
                                    touchDown();
                                    touchDown = true;
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case 262:
                        if (touchDown) {
                            touchDown = false;
                            touchUp();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        timer.stopFreeze();
                        setTimerColor(colors[1]);
                        break;
                }
            }
            return true;
        }
    };

    public void setPref(final int position) {
        switch (position) {
            case 1: //WCA观察
                wca = !wca;
                stAdapter.setCheck(position, wca);
                setPref("wca", wca);
                break;
            case 2: //观察提示
                inspectionAlert = !inspectionAlert;
                stAdapter.setCheck(position, inspectionAlert);
                setPref("wcainsp", inspectionAlert);
                break;
            case 3: //时间格式
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[13], timeFormat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (timeFormat == i) return;
                        timeFormat = i;
                        stAdapter.setText(position, itemStr[13][i]);
                        setPref("timeform", i);
                        if (result.length() > 0) {
                            resAdapter.notifyDataSetChanged();
                            btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 4: //小数点格式
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[16], decimalMark, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (decimalMark == i) return;
                        decimalMark = i;
                        stAdapter.setText(position, itemStr[16][i]);
                        setPref("decim", i);
                        if (enterTime == 0) setTimerText("0" + (decimalMark == 0 ? "." : ",") + (timerAccuracy == 0 ? "00" : "000"));
                        if (result.length() > 0) {
                            resAdapter.notifyDataSetChanged();
                            btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 5: //计时方式
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[0], enterTime, new DialogInterface.OnClickListener() {
                    @TargetApi(18)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (enterTime == i) return;
                        enterTime = i;
                        stAdapter.setText(position, itemStr[0][i]);
                        if (i < 2) {
                            bluetoothTools.disconnect();
                            if (stackmat != null) stackmat.stop();
                            if (i == 0)
                                setTimerText("0" + (decimalMark == 0 ? "." : ",") + (timerAccuracy == 0 ? "00" : "000"));
                            else setTimerText("IMPORT");
                            tvMulPhase.setText("");
                            timer.setTimerState(Timer.READY);
                        } else if (i < 4) {
                            if (Build.VERSION.SDK_INT > 22) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    //申请WRITE_EXTERNAL_STORAGE权限
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.RECORD_AUDIO },
                                            7);
                                } else startStackmat();
                            } else startStackmat();
                        }
                        else {
                            if (Build.VERSION.SDK_INT < 18 || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                                Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            bluetoothTools.disconnect();
                            setTimerText("0" + (decimalMark == 0 ? "." : ",") + (timerAccuracy == 0 ? "00" : "000"));
                            if (bluetoothTools.initBluetoothAdapter()) {
                                if (Build.VERSION.SDK_INT > 22) {
                                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //申请WRITE_EXTERNAL_STORAGE权限
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                                6);
                                    } else scanDevice();
                                } else scanDevice();
                            } else {
                                Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        //else
                        setPref("tiway", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 6: //更新方式
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[1], timerUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (timerUpdate == i) return;
                        timerUpdate = i;
                        stAdapter.setText(position, itemStr[1][i]);
                        setPref("timerupd", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 7: //计时精度
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[2], timerAccuracy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (timerAccuracy == i) return;
                        timerAccuracy = i;
                        stAdapter.setText(position, itemStr[2][i]);
                        setPref("prec", i != 0);
                        if (enterTime == 0) setTimerText("0" + (decimalMark == 0 ? "." : ",") + (i == 0 ? "00" : "000"));
                        if (result.length() > 0) {
                            btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                            result.calcAvg();
                            if (multiPhase > 0) result.calcMpMean();
                            resAdapter.notifyDataSetChanged();
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 9: //分段计时
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[3], multiPhase, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (multiPhase == i) return;
                        multiPhase = i;
                        stAdapter.setText(position, itemStr[3][i]);
                        result.calcAvg();
                        if (i == 0) {
                            result.clearMulTime();
                            tvMulPhase.setText("");
                            sortType = 0;
                        } else {
                            result.initMulTime();
                            if (result.length() > 0)
                                result.getMulTime();
                            sortType = 0;
                            result.calcMpMean();
                        }
                        setPref("multp", i);
                        sessionManager.setMultiPhase(sessionIdx, i);
                        resAdapter.reload();
                        setResultTitle();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 10: //模拟SS
                simulateSS = !simulateSS;
                stAdapter.setCheck(position, simulateSS);
                setPref("simss", simulateSS);
                break;
            case 11: //显示统计简要
                showStat = !showStat;
                tvStat.setVisibility(showStat ? View.VISIBLE : View.GONE);
                stAdapter.setCheck(position, showStat);
                setPref("showstat", showStat);
                break;
            case 12:    //拍桌子停表
                dropToStop = !dropToStop;
                stAdapter.setCheck(position, dropToStop);
                setPref("drop", dropToStop);
                break;
            case 16:    //等宽打乱字体
                monoFont = !monoFont;
                stAdapter.setCheck(position, monoFont);
                setPref("monoscr", monoFont);
                setScrambleFont();
                break;
            case 17:    //显示打乱状态
                showImage = !showImage;
                stAdapter.setCheck(position, showImage);
                setPref("showscr", showImage);
                if (showImage) {
                    scrambleView.setVisibility(View.VISIBLE);
                    showScrambleView();
                }
                else scrambleView.setVisibility(View.GONE);
                break;
            case 19:    //EG打乱
                new AlertDialog.Builder(context).setMultiChoiceItems(R.array.opt_eg_scramble, egIdx, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        egIdx[i] = b;
                        if (i < 3) {
                            if (b) egtype |= (4 >> i);
                            else egtype &= (-5 >> i);
                            setPref("egtype", egtype);
                        } else {
                            setPref("egoll", Utils.getEgOll());
                            Utils.setEgOll();
                        }
                    }
                }).setNegativeButton(R.string.btn_close, null).show();
                break;
            case 21:    //确认时间
                promptToSave = !promptToSave;
                stAdapter.setCheck(position, promptToSave);
                setPref("conft", promptToSave);
                break;
            case 22:	//滚动平均1类型
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[14], avg1Type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (avg1Type == i) return;
                        avg1Type = i;
                        stAdapter.setText(position, itemStr[14][i]);
                        setPref("l1tp", i);
                        int avg = (i * 1000 + avg1len - 1) * 2000 + (avg2Type * 1000 + avg2len - 1);
                        sessionManager.setAverage(sessionIdx, avg);
                        if (result.length() > 0) {
                            result.calcAvg();
                            setStatsLabel();
                        }
                        if (multiPhase == 0) {
                            setResultTitle();
                            if (result.length() > 0) {
                                if (sortType == 3 || sortType == 4)
                                    result.sortResult();
                                resAdapter.notifyDataSetChanged();
                            }
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 24:    //滚动平均2类型
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[4], avg2Type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (avg2Type == i) return;
                        avg2Type = i;
                        stAdapter.setText(position, itemStr[4][i]);
                        setPref("l2tp", i);
                        int avg = (avg1Type * 1000 + avg1len - 1) * 2000 + (i * 1000 + avg2len - 1);
                        sessionManager.setAverage(sessionIdx, avg);
                        if (result.length() > 0) {
                            result.calcAvg();
                            setStatsLabel();
                        }
                        if (multiPhase == 0) {
                            setResultTitle();
                            if (result.length() > 0) {
                                if (sortType == 5 || sortType == 6)
                                    result.sortResult();
                                resAdapter.notifyDataSetChanged();
                            }
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 23:    //平均1长度
            case 25:    //平均2长度
                LayoutInflater factory = LayoutInflater.from(context);
                int layoutId = R.layout.dialog_input;
                view = factory.inflate(layoutId, null);
                editText = view.findViewById(R.id.edit_text);
                editText.setText(String.valueOf(position==23 ? avg1len : avg2len));
                editText.setSelection(editText.getText().length());
                new AlertDialog.Builder(context).setTitle(R.string.enter_length).setView(view)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str = editText.getText().toString();
                                if (TextUtils.isEmpty(str)) return;
                                int len = Integer.parseInt(str);
                                if (len < 3 || len > 1000) {
                                    Toast.makeText(context, getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (position == 23) {
                                    avg1len = len;
                                    setPref("l1len", len);
                                } else {
                                    avg2len = len;
                                    setPref("l2len", len);
                                }
                                int avg = (avg1Type * 1000 + avg1len - 1) * 2000 + (avg2Type * 1000 + avg2len - 1);
                                sessionManager.setAverage(sessionIdx, avg);
                                stAdapter.setText(position, String.valueOf(len));
                                if (result.length() > 0) {
                                    result.calcAvg();
                                    setStatsLabel();
                                }
                                if (multiPhase == 0) {
                                    setResultTitle();
                                    if (sortType > 2)
                                        result.sortResult();
                                    resAdapter.notifyDataSetChanged();
                                }
                                Utils.hideKeyboard(editText);
                            }
                        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.hideKeyboard(editText);
                            }
                }).show();
                Utils.showKeyboard(editText);
                break;
            case 26:	//自动选择分组
                selectSession = !selectSession;
                stAdapter.setCheck(position, selectSession);
                setPref("selses", selectSession);
                break;
            case 28:    //三阶求解
                Cube333SolverDialog.newInstance(position).show(getSupportFragmentManager(), "333Solver");
                break;
            case 29:    //SQ1复形
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[12], solveSq1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (solveSq1 == i) return;
                        solveSq1 = i;
                        stAdapter.setText(position, itemStr[12][i]);
                        setPref("sq1s", i);
                        if (currentScramble.isSqScramble()) {
                            if (i > 0) {
                                final int sel = i;
                                new Thread() {
                                    public void run() {
                                        handler.sendEmptyMessage(4);
                                        currentScramble.updateHint(sel);
                                        showScramble();
                                        scrambleState = SCRAMBLING_NEXT;
                                        if (nextScramble != null)
                                            nextScramble.updateHint(sel);
                                        scrambleState = SCRAMBLE_DONE;
                                        handler.sendEmptyMessage(26);
                                    }
                                }.start();
                            } else {
                                currentScramble.updateHint(0);
                                tvScramble.setText(currentScramble.getScramble());
                                if (nextScramble != null)
                                    nextScramble.updateHint(0);
                            }
                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 30:    //二阶求解
                Cube222SolverDialog.newInstance(position).show(getSupportFragmentManager(), "222Solver");
                break;
            case 31:    //Pyraminx V求解
                final boolean[] chks = new boolean[4];
                for (int i=0; i<4; i++)
                    chks[i] = (((solvePyr >> i) & 1) != 0);
                new AlertDialog.Builder(context).setMultiChoiceItems(R.array.opt_solve_pyr, chks, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        int s = 0;
                        for (int j=0; j<4; j++)
                            if (chks[j]) s |= 1 << j;
                        setPref("pyrv", solvePyr = s);
                        if (currentScramble.isPyrScramble()) {
                            if (s > 0) {
                                final int sel = s;
                                new Thread() {
                                    public void run() {
                                        handler.sendEmptyMessage(4);
                                        currentScramble.updateHint(sel);
                                        showScramble();
                                        scrambleState = SCRAMBLING_NEXT;
                                        if (nextScramble != null)
                                            nextScramble.updateHint(sel);
                                        scrambleState = SCRAMBLE_DONE;
                                        handler.sendEmptyMessage(26);
                                    }
                                }.start();
                            } else {
                                currentScramble.updateHint(0);
                                tvScramble.setText(currentScramble.getScramble());
                                if (nextScramble != null)
                                    nextScramble.updateHint(0);
                            }
                        }
                    }
                }).setNegativeButton(R.string.btn_close, null).show();
                break;
            //配色设置
            case 33:    //n阶
                int[] cs = {sp.getInt("csn1", Color.YELLOW), sp.getInt("csn2", Color.BLUE), sp.getInt("csn3", Color.RED),
                        sp.getInt("csn4", Color.WHITE), sp.getInt("csn5", 0xff009900), sp.getInt("csn6", 0xffff9900)};
                colorSchemeView = new ColorSchemeView(this, (int) (dpi * 290), cs, 1);
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle(getString(R.string.scheme_cube)).setView(colorSchemeView)
                        .setNegativeButton(R.string.btn_close, null).setNeutralButton(R.string.scheme_reset, null).show();
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int) (dpi * 320);
                dialog.getWindow().setAttributes(params);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffff0000);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {    //配色复位
                        int[] color = {0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff9900};
                        for (int i=1; i<7; i++) {
                            delPref("csn" + i);
                        }
                        colorSchemeView.setColor(color);
                        colorSchemeView.invalidate();
                    }
                });
                break;
            case 34:    //金字塔
                cs = new int[] {sp.getInt("csp1", Color.RED), sp.getInt("csp2", 0xff009900),
                        sp.getInt("csp3", Color.BLUE), sp.getInt("csp4", Color.YELLOW)};
                colorSchemeView = new ColorSchemeView(this, (int) (dpi * 290), cs, 2);
                dialog = new AlertDialog.Builder(context).setTitle(getString(R.string.scheme_pyrm)).setView(colorSchemeView)
                        .setNegativeButton(R.string.btn_close, null).setNeutralButton(R.string.scheme_reset, null).show();
                params = dialog.getWindow().getAttributes();
                params.width = (int) (dpi * 320);
                dialog.getWindow().setAttributes(params);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffff0000);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int[] color = {0xffff0000, 0xff009900, 0xff0000ff, 0xffffff00};
                        for (int i=1; i<5; i++) delPref("csp"+i);
                        colorSchemeView.setColor(color);
                        colorSchemeView.invalidate();
                    }
                });
                break;
            case 35:    //SQ1
                cs = new int[] {sp.getInt("csq1", Color.WHITE), sp.getInt("csq2", 0xffff9900), sp.getInt("csq3", 0xff009900),
                        sp.getInt("csq4", Color.YELLOW), sp.getInt("csq5", Color.RED), sp.getInt("csq6", Color.BLUE)};
                colorSchemeView = new ColorSchemeView(this, (int) (dpi * 290), cs, 3);
                dialog = new AlertDialog.Builder(context).setTitle(getString(R.string.scheme_sq)).setView(colorSchemeView)
                        .setNegativeButton(R.string.btn_close, null).setNeutralButton(R.string.scheme_reset, null).show();
                params = dialog.getWindow().getAttributes();
                params.width = (int) (dpi * 320);
                dialog.getWindow().setAttributes(params);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffff0000);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int[] color = {0xffffffff, 0xffff9900, 0xff009900, 0xffffff00, 0xffff0000, 0xff0000ff};
                        for (int i=1; i<7; i++) delPref("csq" + i);
                        colorSchemeView.setColor(color);
                        colorSchemeView.invalidate();
                    }
                });
                break;
            case 36:    //skewb
                cs = new int[] {sp.getInt("csw1", Color.YELLOW), sp.getInt("csw2", Color.BLUE), sp.getInt("csw3", Color.RED),
                        sp.getInt("csw4", Color.WHITE), sp.getInt("csw5", 0xff009900), sp.getInt("csw6", 0xffff9900)};
                colorSchemeView = new ColorSchemeView(this, (int) (dpi * 290), cs, 4);
                dialog = new AlertDialog.Builder(context).setTitle(getString(R.string.scheme_skewb)).setView(colorSchemeView)
                        .setNegativeButton(R.string.btn_close, null).setNeutralButton(R.string.scheme_reset, null).show();
                params = dialog.getWindow().getAttributes();
                params.width = (int) (dpi * 320);
                dialog.getWindow().setAttributes(params);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffff0000);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int[] color = {0xffffff00, 0xff0000ff, 0xffff0000, 0xffffffff, 0xff009900, 0xffff9900};
                        for (int i=1; i<7; i++) delPref("csw"+i);
                        colorSchemeView.setColor(color);
                        colorSchemeView.invalidate();
                    }
                });
                break;
            case 37:    //五魔
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[7], megaColorScheme, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (megaColorScheme == i) return;
                        megaColorScheme = i;
                        stAdapter.setText(position, itemStr[7][i]);
                        setPref("minxc", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            //界面设置
            case 39:    //计时器字体
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[8], timerFont, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (timerFont == i) return;
                        timerFont = i;
                        stAdapter.setText(position, itemStr[8][i]);
                        setTimerFont();
                        setPref("tfont", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 41:    //背景颜色
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null);
                final LineColorPicker colorPicker = dialogLayout.findViewById(R.id.color_picker_primary);
                final LineColorPicker colorPicker2 = dialogLayout.findViewById(R.id.color_picker_primary_2);
                final TextView dialogTitle = dialogLayout.findViewById(R.id.dialog_title);
                dialogTitle.setText(R.string.background_color);
                //((CardView) dialogLayout.findViewById(R.id.dialog_card)).setCardBackgroundColor(-1);
                colorPicker2.setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int c) {
                        dialogTitle.setBackgroundColor(c);
                        if (Utils.greyScale(c) > 200)
                            dialogTitle.setTextColor(0xff212121);
                        else dialogTitle.setTextColor(-1);
                        //chooser.onColorChanged(c);
                    }
                });
                colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int c) {
                        colorPicker2.setColors(ColorPalette.getColors(context, colorPicker.getColor()));
                        colorPicker2.setSelectedColor(colorPicker.getColor());
                    }
                });
                int[] baseColors = ColorPalette.getBaseColors(context);
                colorPicker.setColors(baseColors);
                for (int i : baseColors) {
                    for (int i2 : ColorPalette.getColors(context, i)) {
                        if (i2 == colors[0]) {
                            colorPicker.setSelectedColor(i);
                            colorPicker2.setColors(ColorPalette.getColors(context, i));
                            colorPicker2.setSelectedColor(i2);
                            break;
                        }
                    }
                }
                dialogBuilder.setView(dialogLayout);
                dialogBuilder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //chooser.onDialogDismiss();
                    }
                });
                dialogBuilder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog alertDialog = (AlertDialog) dialog;
                        alertDialog.setOnDismissListener(null);
                        //Log.w("dct", "背景颜色 "+Integer.toHexString(colorPicker2.getColor()));
                        //chooser.onColorSelected(colorPicker2.getColor());
                        colors[0] = colorPicker2.getColor();
                        if (useBgcolor)
                            setBackground(colors[0]);
                        //useBgcolor = true;
                        setPref("cl0", colors[0]);
                        setPref("bgcolor", true);
                        dialog.dismiss();
                    }
                });
                dialogBuilder.show();
                break;
            case 42:    //文字颜色
                new ColorPickerDialog(context, colors[1], -1, new OnColorPickerListener() {
                    @Override
                    public void onColorCancel(ColorPickerDialog dialog) { }

                    @Override
                    public void onColorChange(ColorPickerDialog dialog, int color) { }

                    @Override
                    public void onColorConfirm(ColorPickerDialog dialog, int color) {
                        //Log.w("dct", "选择颜色 "+Integer.toHexString(color));
                        colors[1] = color;
                        setPref("cl1", color);
                        setTextsColor();
                        setIconColor();
                        pbScramble.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onColorReset(ColorPickerDialog dialog, int color) {
                        colors[1] = color;
                        delPref("cl1");
                        setTextsColor();
                        setIconColor();
                        pbScramble.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    }
                }).show();
                break;
            case 43:    //背景图片
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                2);
                    } else selectPic();
                } else selectPic();
                break;
            case 44:    //显示背景图
                if (useBgcolor) {
                    setBackground();
                } else setBackground(colors[0]);
                useBgcolor = !useBgcolor;
                //Log.w("dct", ""+useBgcolor);
                stAdapter.setCheck(position, !useBgcolor);
                setPref("bgcolor", useBgcolor);
                break;
            case 46:    //最快单次颜色
                new ColorPickerDialog(context, colors[2], 0xffff00ff, new OnColorPickerListener() {
                    @Override
                    public void onColorCancel(ColorPickerDialog dialog) { }

                    @Override
                    public void onColorChange(ColorPickerDialog dialog, int color) { }

                    @Override
                    public void onColorConfirm(ColorPickerDialog dialog, int color) {
                        //Log.w("dct", "选择颜色 "+Integer.toHexString(color));
                        colors[2] = color;
                        setPref("cl2", color);
                        resAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onColorReset(ColorPickerDialog dialog, int color) {
                        colors[2] = color;
                        delPref("cl2");
                        resAdapter.notifyDataSetChanged();
                    }
                }).show();
                break;
            case 47:    //最慢单次颜色
                new ColorPickerDialog(context, colors[3], 0xffff0000, new OnColorPickerListener() {
                    @Override
                    public void onColorCancel(ColorPickerDialog dialog) { }

                    @Override
                    public void onColorChange(ColorPickerDialog dialog, int color) { }

                    @Override
                    public void onColorConfirm(ColorPickerDialog dialog, int color) {
                        //Log.w("dct", "选择颜色 "+Integer.toHexString(color));
                        colors[3] = color;
                        setPref("cl3", color);
                        resAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onColorReset(ColorPickerDialog dialog, int color) {
                        colors[3] = color;
                        delPref("cl3");
                        resAdapter.notifyDataSetChanged();
                    }
                }).show();
                break;
            case 48:    //最快平均颜色
                new ColorPickerDialog(context, colors[4], 0xff009900, new OnColorPickerListener() {
                    @Override
                    public void onColorCancel(ColorPickerDialog dialog) { }

                    @Override
                    public void onColorChange(ColorPickerDialog dialog, int color) { }

                    @Override
                    public void onColorConfirm(ColorPickerDialog dialog, int color) {
                        //Log.w("dct", "选择颜色 "+Integer.toHexString(color));
                        colors[4] = color;
                        setPref("cl4", color);
                        resAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onColorReset(ColorPickerDialog dialog, int color) {
                        colors[4] = color;
                        delPref("cl4");
                        resAdapter.notifyDataSetChanged();
                    }
                }).show();
                break;
            //手势管理
            case 50:    //左
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[15], swipeType[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (swipeType[0] == i) return;
                        swipeType[0] = i;
                        stAdapter.setText(position, itemStr[15][i]);
                        setPref("gesturel", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 51:    //右
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[15], swipeType[1], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (swipeType[1] == i) return;
                        swipeType[1] = i;
                        stAdapter.setText(position, itemStr[15][i]);
                        setPref("gesturer", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 52:    //上
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[15], swipeType[2], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (swipeType[2] == i) return;
                        swipeType[2] = i;
                        stAdapter.setText(position, itemStr[15][i]);
                        setPref("gestureu", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 53:    //下
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[15], swipeType[3], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (swipeType[3] == i) return;
                        swipeType[3] = i;
                        stAdapter.setText(position, itemStr[15][i]);
                        setPref("gestured", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            //硬件设置
            case 55:    //屏幕常亮
                if (screenOn) {
                    if (timer.getTimerState() != 1) releaseWakeLock();
                } else acquireWakeLock();
                screenOn = !screenOn;
                stAdapter.setCheck(position, screenOn);
                setPref("scron", screenOn);
                break;
            case 56:    //触感反馈
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[10], vibrateType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (vibrateType == i) return;
                        vibrateType = i;
                        stAdapter.setText(position, itemStr[10][i]);
                        setPref("vibra", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 57:    //触感时间
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[11], vibrateTime, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (vibrateTime == i) return;
                        vibrateTime = i;
                        stAdapter.setText(position, itemStr[11][i]);
                        setPref("vibtime", i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
            case 58:    //屏幕方向
                new AlertDialog.Builder(context).setSingleChoiceItems(ITEMS_ID[9], screenOri, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (screenOri == i) return;
                        screenOri = i;
                        stAdapter.setText(position, itemStr[9][i]);
                        setPref("screenori", i);
                        dialogInterface.dismiss();
                        setRequestedOrientation(SCREEN_ORIENTATION[i]);
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
                break;
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double acc = Math.sqrt(x * x + y * y + z * z);
                //tvTest.setText(String.format(Locale.getDefault(), "%.3f", acc));
                if (lastAcc != 0) {
                    if (Math.abs(acc - lastAcc) > sensitivity && dropToStop) {
                        if (timer.getTimerState() == Timer.RUNNING && timer.time > 200) {   //停止计时
                            setVisibility(true);
                            timer.timeEnd = SystemClock.uptimeMillis();
                            timer.count();
                            if (!wca || currentScramble.isBlindfoldScramble()) { penaltyTime = 0; isDNF = false;}
                            save((int) timer.time);
                            timer.setTimerState(0);
                            if (!screenOn) releaseWakeLock();
                        }
                        //tvTest.setBackgroundColor(0x88dddddd);
                    } //else tvTest.setBackgroundColor(0);
                }
                lastAcc = acc;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void show333Hint(final int idx) {
        if (currentScramble.is333Scramble()) {
            if (idx == 0) {
                currentScramble.updateHint(0);
                tvScramble.setText(currentScramble.getScramble());
                if (nextScramble != null)
                    nextScramble.updateHint(0);
            } else new Thread() {
                public void run() {
                    handler.sendEmptyMessage(4);
                    currentScramble.updateHint(idx);
                    showScramble();
                    scrambleState = SCRAMBLING_NEXT;
                    if (nextScramble != null)
                        nextScramble.updateHint(idx);
                    scrambleState = SCRAMBLE_DONE;
                    handler.sendEmptyMessage(26);
                }
            }.start();
        }
    }

    public void show222Hint(final int idx) {
        if (currentScramble.is222Scramble()) {
            if (idx == 0) {
                currentScramble.updateHint(0);
                tvScramble.setText(currentScramble.getScramble());
                if (nextScramble != null)
                    nextScramble.updateHint(0);
            } else new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(4);
                    currentScramble.updateHint(idx);
                    showScramble();
                    scrambleState = SCRAMBLING_NEXT;
                    if (nextScramble != null)
                        nextScramble.updateHint(idx);
                    scrambleState = SCRAMBLE_DONE;
                    handler.sendEmptyMessage(26);
                }
            }).start();
        }
    }

    public void updateSettingList(int position, String text) {
        stAdapter.setText(position, text);
    }

    public void updatePref(int position, int progress) {
        switch (position) {
            case ST_START_DELAY: //启动延时
                freezeTime = progress;
                setPref("tapt", freezeTime);
                break;
            case ST_SENSITIVITY:    //灵敏度
                sensitivity = (progress + 5) / 100d;
                setPref("sensity", progress + 5);
                break;
            case ST_SCR_FONT: //打乱字体
                scrambleSize = progress + 12;
                setScrambleSize();
                setPref("stsize", scrambleSize);
                break;
            case ST_IMAGE_SIZE: //打乱状态
                imageSize = progress * 10 + 160;
                setImageSize();
                setPref("svsize", imageSize);
                break;
            case ST_TIMER_SIZE: //计时器大小
                timerSize = progress + 50;
                setTimerSize();
                setPref("ttsize", timerSize);
                break;
            case ST_OPACITY: //不透明度
                opacity = progress + 20;
                if (!useBgcolor)
                    setBackground();
                setPref("opac", opacity);
                break;
        }
    }

    public void updatePref(int position, String detail) {
        View v = rvSetting.getLayoutManager().findViewByPosition(position);
        if (v == null) {
            //Log.e("dct", "view为null");
            return;
        }
        TextView tv = v.findViewById(R.id.list_detail);
        tv.setText(detail);
//        ((TextView) v.findViewById(R.id.tv_detail)).setText(""+progress);
    }

    public void resetAll() {
        new AlertDialog.Builder(context).setTitle(R.string.confirm_reset)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        APP.resetPref();
                        removePref();
                        setBackground(colors[0]);
                        //setPrimaryDark();
                        setViews();
                        setTextsColor();
                        setIconColor();
                        resAdapter.notifyDataSetChanged();
                        stAdapter.reload();
                        releaseWakeLock();
                    }
                }).setNegativeButton(R.string.btn_cancel, null).show();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {   //TODO
        @Override
        public void handleMessage(Message msg) {
            int msw = msg.what;
            switch (msw) {
                case 0:
                    tvScramble.setText(currentScramble.getScramble());
                    //btnScramble.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    tvScramble.setText(getString(R.string.scrambling));
                    btnScramble.setVisibility(View.INVISIBLE);
                    pbScramble.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(false);
                    btnRight.setEnabled(false);
                    break;
                case 4: //打乱求解
                    //tvScramble.setText(currentScramble.getScramble() + "\n\n" + getString(R.string.solving));
                    btnScramble.setVisibility(View.INVISIBLE);
                    pbScramble.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(false);
                    btnRight.setEnabled(false);
                    break;
                case 5: Toast.makeText(context, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();	break;
                case 6: Toast.makeText(context, getString(R.string.file_error), Toast.LENGTH_SHORT).show();    break;
                case 7: Toast.makeText(context, getString(R.string.save_success), Toast.LENGTH_SHORT).show();	break;
                case 8: Toast.makeText(context, R.string.conning, Toast.LENGTH_SHORT).show();	break;
                case 9: Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();	break;
                case 10: Toast.makeText(context, R.string.lastest_version, Toast.LENGTH_LONG).show();	break;
                case 11:
                    Toast.makeText(context, getString(R.string.import_fail), Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
                case 12:
                    Toast.makeText(context, getString(R.string.import_success), Toast.LENGTH_SHORT).show(); //TODO
//                    APP.getInstance().initSession(context);
//                    if (sessionIdx >= sessionManager.getSessionLength()) sessionIdx = 0;
//                    multiPhase = sessionManager.getMultiPhase(sessionIdx);
//                    getResult();
//                    setResultTitle();
//                    result.calcAvg();
//                    if (multiPhase > 0) result.calcMpMean();
//                    resAdapter.reload();
//                    btnSession.setText(sessionManager.getSessionName(sessionIdx));
//                    btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
//                    setStatsLabel();
//                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
//                    intent.putExtra("REBOOT","reboot");
//                    startActivity(intent);
                    intent = getIntent();
                    finish();
                    startActivity(intent);
                    break;
                case 14: scrambleView.setVisibility(View.GONE); break;
                case 15:
                    scrambleView.setVisibility(View.VISIBLE);
                    scrambleView.setImageBitmap(bmScrambleView);
                    break;
                case 16:
                    if (defaultPath == null)
                        Toast.makeText(context, getString(R.string.sdcard_not_exist), Toast.LENGTH_SHORT).show();
                    else
                        new AlertDialog.Builder(context).setTitle(getString(R.string.new_version)+ newVersion).setMessage(updateCont)
                                .setPositiveButton(R.string.btn_download, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            //权限已授权 GRANTED:授权 DINIED:拒绝
                                            if (ContextCompat.checkSelfPermission(context, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                                                download("DCTimer"+ newVersion +".apk");
                                            } else ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 5);
                                        } else download("DCTimer"+ newVersion +".apk");
                                    }
                                }).setNegativeButton(R.string.btn_cancel, null).show();
                    break;
                case 21:
                    tvScramble.setText(getString(R.string.initializing) + " (0%) ...");
                    btnScramble.setVisibility(View.INVISIBLE);
                    pbScramble.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(false);
                    btnRight.setEnabled(false);
                    break;
                case 22: tvScramble.setText(getString(R.string.initializing) + " (10%) ..."); break;
                case 23: tvScramble.setText(getString(R.string.initializing) + " (20%) ..."); break;
                case 24: tvScramble.setText(getString(R.string.initializing) + " (30%) ..."); break;
                case 25: int prog = (int) msg.obj;
                    tvScramble.setText(getString(R.string.initializing) + " (" + (36 + prog / 44809) + "%) ..."); break;
                case 26:
                    if (timer.getTimerState() == Timer.STOP || timer.getTimerState() == Timer.READY)
                        btnScramble.setVisibility(View.VISIBLE);
                    pbScramble.setVisibility(View.GONE);
                    btnLeft.setEnabled(true);
                    btnRight.setEnabled(true);
                    break;
                default:
                    progressDialog.setProgress(msw - 100);
                    break;
            }
        }
    };

    private void removePref() { //移除配置
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("cl0");	edit.remove("cl1");	edit.remove("cl2");
        edit.remove("cl3");	edit.remove("cl4");	edit.remove("wca");
        edit.remove("cxe");
        edit.remove("l1am");	edit.remove("l2am");	edit.remove("mnxc");
        edit.remove("prec");	edit.remove("mulp");	edit.remove("invs");
        edit.remove("tapt");	edit.remove("intv");	edit.remove("opac");
        edit.remove("mclr");	edit.remove("prom");	edit.remove("sq1s");
        edit.remove("l1tp");	edit.remove("l2tp");    edit.remove("dark");
        edit.remove("c2fl");
        edit.remove("hidls");	edit.remove("conft");	edit.remove("list1");
        edit.remove("list2");	edit.remove("timmh");	edit.remove("tiway");
        edit.remove("cface");	edit.remove("cside");	edit.remove("srate");
        edit.remove("tfont");	edit.remove("vibra");	edit.remove("sqshp");
        edit.remove("fulls");	edit.remove("usess");	edit.remove("scron");
        edit.remove("multp");	edit.remove("minxc");	edit.remove("simss");
        edit.remove("l1len");	edit.remove("l2len");   edit.remove("sside");
        edit.remove("pside");   edit.remove("rside");   edit.remove("group");
        edit.remove("decim");
        edit.remove("hidscr");	edit.remove("ttsize");	edit.remove("stsize");
        edit.remove("cube2l");	edit.remove("scrgry");	edit.remove("selses");
        edit.remove("ismulp");	edit.remove("svsize");
        edit.remove("vibtime");	edit.remove("bgcolor");	edit.remove("ssvalue");
        edit.remove("sensity");	edit.remove("monoscr");	edit.remove("showscr");
        edit.remove("timerupd");	edit.remove("timeform");    edit.remove("showstat");
        edit.remove("screenori");
        edit.apply();
    }

    public void setPref(String key, int value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public void setPref(String key, String value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public void setPref(String key, boolean value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public void delPref(String key) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.apply();
    }

    //设置各种View、TextView颜色等 TODO
    private void setViews() {
        //打乱显示
        //tvScramble.setTextSize(scrambleSize);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tvScramble, 10, scrambleSize, 2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        tvScramble.setTextColor(colors[1]);
        if (monoFont) setScrambleFont();

        //打乱状态
        setImageSize();
        //计时器
        tvTimer.setTextSize(timerSize);
        setTimerFont();
        setTimerColor(colors[1]);
        if (enterTime == 0) {
            setTimerText("0" + (decimalMark == 0 ? "." : ",") + (timerAccuracy == 0 ? "00" : "000"));
        } else if (enterTime == 1)
            setTimerText("IMPORT");
        else if (enterTime == 2 || enterTime == 3) {  //TODO SS计时器
            startStackmat();
        }

        //屏幕方向
        setRequestedOrientation(SCREEN_ORIENTATION[screenOri]);
        //分组平均
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        //统计简要
        tvStat.setVisibility(showStat ? View.VISIBLE : View.GONE);
        //tvStat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        tvStat.setTextColor(colors[1]);
        setStatsLabel();
    }

    private void setStatsLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(result.getSolved()).append('/').append(result.length()).append('\n');
        sb.append(avg1Type == 0 ? "ao" : "mo").append(avg1len).append(": ");
        sb.append(result.getRollingAvg1(result.length() - 1)).append('\n');
        sb.append(avg2Type == 0 ? "ao" : "mo").append(avg2len).append(": ");
        sb.append(result.getRollingAvg2(result.length() - 1));
        tvStat.setText(sb.toString());
    }

    private void setVisibility(boolean v) {	//设置控件的隐藏 TODO
        int vi = v ? View.VISIBLE : View.GONE;
        tvScramble.setVisibility(vi);
        if (showStat)
            tvStat.setVisibility(vi);
        btnScramble.setVisibility(vi);
        if (currentScramble.getScrambleListSize() > 1) {
            btnLeft.setVisibility(vi);
            btnRight.setVisibility(vi);
        }
        toolbar.setVisibility(vi);
        if (showImage)
            scrambleView.setVisibility(vi);
    }

    public void setBackground(int color) {
        frame.setBackgroundColor(color);
        //tabHost.setBackgroundColor(color);
        //toolbar.setBackgroundColor(color);
        int grey = Utils.greyScale(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0
            if (grey > 200) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            //getWindow().setNavigationBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
            if (grey > 200) {
                getWindow().setStatusBarColor(0x44000000);
            } else {
                getWindow().setStatusBarColor(0);
            }
        }
    }

    public void setBackground() {
        try {
            Bitmap bm = Utils.getBitmap(picPath);
            bitmap = Utils.getBackgroundBitmap(bm);
            frame.setBackgroundDrawable(Utils.getBackgroundDrawable(context, bitmap, opacity));
        } catch (Exception e) {
            e.printStackTrace();
            setBackground(colors[0]);
            //Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (OutOfMemoryError e) {
            setBackground(colors[0]);
            //Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setTimerColor(int color) {  //设置计时器颜色
        tvTimer.setTextColor(color);
        tvMulPhase.setTextColor(color);
    }

    public void setTimerText(String text) { //设置计时器文字
        tvTimer.setText(text);
    }

    public void setTimerSize() {
        tvTimer.setTextSize(timerSize);
    }

    public void updateTime() {
        if (bluetoothTools.getCube() != null) {
            tvTimer.setText(StringUtils.timeToString(bluetoothTools.getCube().getResult()));
        }
    }

    public CharSequence getScrambleText() {
        return btnScramble.getText();
    }

    private void setTimerFont() {   //设置计时器字体
        switch (timerFont) {
            case 0:
                tvTimer.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                tvMulPhase.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                break;
            case 1:
                tvTimer.setTypeface(Typeface.create("serif", Typeface.NORMAL));
                tvMulPhase.setTypeface(Typeface.create("serif", Typeface.NORMAL));
                break;
            case 2:
                tvTimer.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                tvMulPhase.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                break;
            case 3:
                tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
                tvMulPhase.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
                break;
            case 4:
                tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
                tvMulPhase.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
                break;
            case 5:
                tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
                tvMulPhase.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
                break;
        }
    }

    private void setScrambleFont() {    //设置打乱字体
        if (monoFont)
            tvScramble.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        else tvScramble.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
    }

    public void setScrambleSize() {
        //tvScramble.setTextSize(scrambleSize);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tvScramble, 10, scrambleSize, 2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
    }

    public void setImageSize() {    //设置打乱图大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (imageSize * dpi), (int) (imageSize * 3 * dpi) / 4);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        scrambleView.setLayoutParams(params);
    }

    private void setTextsColor() {
        setTimerColor(colors[1]);
        tvScramble.setTextColor(colors[1]);
        tvStat.setTextColor(colors[1]);
        btnScramble.setTextColor(colors[1]);
        //toolbar.setTitleTextColor(colors[1]);
    }

    private void setIconColor() {
        rbTimer.setColor(colors[1]);
        rbResult.setColor(colors[1]);
        rbSetting.setColor(colors[1]);
        toolbar.setItemColor(colors[1]);
        btnLeft.getDrawable().setColorFilter(colors[1], PorterDuff.Mode.SRC_IN);
        btnRight.getDrawable().setColorFilter(colors[1], PorterDuff.Mode.SRC_IN);
    }

    private void setScramble() {
        if (scrambleIdx == -25)
            scrambleIdx = 33;
        int idx = scrambleIdx >> 5;
        int idx2 = scrambleIdx & 0x1f;
        if (idx == -1 && idx2 > 7) idx2--;
        btnScramble.setText(StringUtils.getScrambleName(idx, idx2));
        newScramble();
    }

    private void newScramble() {   //生成新打乱
        final boolean resetLen = lastScrambleType != scrambleIdx;
        int idx = scrambleIdx >> 5;
        int idx2 = scrambleIdx & 0x1f;
        currentScramble.setCategory(scrambleIdx);
        if (!resetLen && scrambleList !=null && importScrambleLen < scrambleList.size()) {
            if (!isImportScr) isImportScr = true;
            final String scr = scrambleList.get(importScrambleLen++);
            currentScramble.setScramble(scr);
            if (importType == 0)
                currentScramble.setImageType(StringUtils.getImageType(scr));
            else currentScramble.setImageType(StringUtils.getImageType(scr, importType));
            if (currentScramble.getImageType() == 3 && solve333 != 0) {
                new Thread() {
                    public void run() {
                        handler.sendEmptyMessage(4);
                        currentScramble.solve333(scr);
                        showScramble();
                        handler.sendEmptyMessage(26);
                    }
                }.start();
            } else {
                tvScramble.setText(scr);
                showScrambleView();
            }
        } else if ((idx == -1 && (idx2 < 2 || (idx2 > 2 && idx2 < 8) || idx2 == 10 || idx2 == 15 || idx2 == 17)) ||
                (idx == 0 && idx2 < 2) ||
                (idx == 1) ||
                (idx == 2 && idx2 == 5) ||
                idx == 8 ||
                (idx == 11 && (idx2 > 1 && idx2 < 5 || idx2 == 6 || idx2 == 8)) ||
                (idx == 16 && idx2 == 8) ||
                (idx == 17 && (idx2 < 3 || idx2 == 6)) ||
                idx == 20) {    //TODO
            if (isImportScr) isImportScr = false;
            if (resetLen) scrambleState = SCRAMBLE_NONE;
            if (scrambleState == SCRAMBLE_NONE || scrambleState == SCRAMBLE_DONE) {
                new Thread() {
                    public void run() {
                        if (scrambleState == SCRAMBLE_DONE) {
                            currentScramble = nextScramble;
                            //Log.w("dct", "scrdone "+nextScramble+"/"+extsol);
                        } else {
                            scrambleState = SCRAMBLING;
                            if (currentScramble.is444Scramble()) {
                                //Log.w("dct", "4阶初始化");
                                Util.init(handler);
                            }
                            handler.sendEmptyMessage(2);
                            currentScramble.generateScramble(scrambleIdx, resetLen);
                        }
                        if (scrambleIdx == lastScrambleType) {
                            showScramble();
                            scrambleState = SCRAMBLING_NEXT;
                            handler.sendEmptyMessage(4);
                            getNextScramble(resetLen);
                        }
                    }
                }.start();
            } else if (scrambleState == SCRAMBLING_NEXT) {
                if (!scrambleGenerating) {
                    scrambleGenerating = true;
                    btnScramble.setVisibility(View.INVISIBLE);
                    pbScramble.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(false);
                    btnRight.setEnabled(false);
                    tvScramble.setText(getString(R.string.scrambling));
                }
            }
        } else {
            scrambleState = SCRAMBLING;
            currentScramble.generateScramble(scrambleIdx, resetLen);
            showScramble();
            scrambleState = SCRAMBLE_DONE;
        }
        lastScrambleType = scrambleIdx;
    }

    private void showScramble() {   //显示打乱
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentScramble.getScrambleListSize() > 1) {
                    tvScramble.setText(currentScramble.getScrambleWithIndicator(dm.heightPixels < dpi * 376));
                    btnLeft.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(true);
                    btnRight.setVisibility(View.VISIBLE);
                    btnLeft.setEnabled(true);
                } else {
                    tvScramble.setText(currentScramble.getScrambleWithHint(dm.heightPixels < dpi * 376));
                    btnLeft.setVisibility(View.GONE);
                    btnRight.setVisibility(View.GONE);
                }
                showScrambleView();
            }
        });
    }

    private void showNextScramble() {
        tvScramble.setText(currentScramble.getNextScramble(dm.heightPixels < dpi * 376));
        showScrambleView();
    }

    private void showLastScramble() {
        tvScramble.setText(currentScramble.getLastScramble(dm.heightPixels < dpi * 376));
        showScrambleView();
    }

    private void showScrambleView() {   //显示打乱状态
        if (!showImage) return;
        //Log.w("dct", currentScramble.getCategory()+", "+currentScramble.getImageType());
        if (currentScramble.getImageType() > 0) {
            bmScrambleView = Bitmap.createBitmap(dip300, dip300 * 3 / 4, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmScrambleView);
            c.drawColor(0);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setStrokeWidth(dpi);
            currentScramble.drawScramble(dip300, p, c);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scrambleView.setVisibility(View.VISIBLE);
                    scrambleView.setImageBitmap(bmScrambleView);
                }
            });
        } else runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrambleView.setVisibility(View.GONE);
            }
        });
    }

    private void getNextScramble(boolean resetLen) {  //生成下一个打乱
        scrambleState = SCRAMBLING_NEXT;
        nextScramble = new Scrambler(sp);
        if (!resetLen) nextScramble.setScrambleLen(currentScramble.getScrambleLen());
        nextScramble.generateScramble(scrambleIdx, resetLen);
        Log.w("dct", "next scramble: "+ nextScramble.getScramble());
        scrambleState = SCRAMBLE_DONE;
        if (scrambleGenerating) {
            currentScramble = nextScramble;
            showScramble();
            scrambleGenerating = false;
            getNextScramble(resetLen);
        } else handler.sendEmptyMessage(26);
    }

    public void setScrambleLen(int len) {
        currentScramble.setScrambleLen(len);
        int idx = scrambleIdx >> 5;
        int idx2 = scrambleIdx & 0x1f;
        if ((idx==-1 && idx2==17)
                || (idx==0 && (idx2==1 || idx2==2))
                || (idx==1 && (idx2==0 || idx2==19))
                || (idx==20 && idx2==4))
            scrambleState = SCRAMBLE_NONE;
        newScramble();
    }

    public boolean isBLDScramble() {
        return currentScramble.isBlindfoldScramble();
    }

    private void changeSession() {
        btnSession.setText(sessionManager.getSessionName(sessionIdx));
        int mp = sessionManager.getMultiPhase(sessionIdx);
        if (mp != multiPhase) {
            multiPhase = mp;
            stAdapter.setText(9, itemStr[3][mp]);
            tvMulPhase.setText("");
            setPref("multp", mp);
            setResultTitle();
        }
        int avg = sessionManager.getAverage(sessionIdx);
        if (avg == 0) avg = 8011;
        int ra = (avg1Type * 1000 + avg1len - 1) * 2000 + (avg2Type * 1000 + avg2len - 1);
        if (avg != ra) {
            avg2len = (avg % 1000 + 1);
            avg2Type = (avg % 2000) / 1000;
            avg1len = (avg / 2000) % 1000 + 1;
            avg1Type = avg / 2000 / 1000;
            //Log.w("dct", avg1Type+"/"+avg1len+", "+avg2Type+"/"+avg2len);
            stAdapter.setText(22, itemStr[14][avg1Type]);
            stAdapter.setText(23, String.valueOf(avg1len));
            stAdapter.setText(24, itemStr[4][avg2Type]);
            stAdapter.setText(25, String.valueOf(avg2len));
            setPref("l1tp", avg1Type);
            setPref("l2tp", avg2Type);
            setPref("l1len", avg1len);
            setPref("l2len", avg2len);
            if (multiPhase == 0) {
                setResultTitle();
            }
        }
        getResult();
        result.calcAvg();
        if (mp > 0) {
            result.calcMpMean();
        }
        sortType = 0;
        resAdapter.reload();
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        lvResult.setSelection(0);
        setPref("session", sessionIdx);
        int puzzle = sessionManager.getPuzzle(sessionIdx);
        if (puzzle != scrambleIdx && scrambleState == SCRAMBLE_DONE) {
            if (puzzle == -25) puzzle = 33;
            scrambleIdx = puzzle;
            setScramble();
        }
        setStatsLabel();
    }

    private void touchDown() {
        if (enterTime >= 2) {
            return;
        }
        if (enterTime == 1) {
            setTimerColor(0xff00ff00);
            return;
        }
        if (timer.getTimerState() == Timer.RUNNING) {
            if (mpCount != 0) {
                if (vibrateType == 1 || vibrateType == 3)
                    vibrator.vibrate(VIBRATE_TIME[vibrateTime]);
                setTimerColor(0xff00ff00);
                int idx = multiPhase + 1 - mpCount;
                Result.multemp[idx] = SystemClock.uptimeMillis();
                int time = idx == 0 ? (int) Result.multemp[idx] : (int) (Result.multemp[idx] - Result.multemp[idx-1]);
                //if (idx == 0) tvAssist.setText(Stats.timeToString(time));
                //else tvAssist.setText(tvAssist.getText()+"\n"+Stats.timeToString(time));
                if (idx == 0) tvMulPhase.setText(StringUtils.timeToString(time));
                else tvMulPhase.setText(tvMulPhase.getText()+"\n"+ StringUtils.timeToString(time));
            } else {
                timer.timeEnd = SystemClock.uptimeMillis();
                if (vibrateType > 1)
                    vibrator.vibrate(VIBRATE_TIME[vibrateTime]);
                timer.count();
                if (multiPhase > 0) {
                    Result.multemp[multiPhase+1] = timer.timeEnd;
                    int time = (int) (Result.multemp[multiPhase+1] - Result.multemp[multiPhase]);
                    tvMulPhase.setText(tvMulPhase.getText() + "\n" + StringUtils.timeToString(time));
                    //tvAssist.setText(tvAssist.getText()+"\n"+ Stats.timeToString(time));
                }
                setVisibility(true);
            }
        } else if (timer.getTimerState() != Timer.STOP) {
//            if (enterTime == 1) {
//                setTimerColor(0xff00ff00);
//            }
            if (freezeTime == 0 || (wca && !currentScramble.isBlindfoldScramble() && timer.getTimerState() == Timer.READY)) {
                setTimerColor(0xff00ff00);
                canStart = true;
            } else {
                if (timer.getTimerState()==0) {
                    if (multiPhase > 0) tvMulPhase.setText("");
                    setTimerColor(0xffff0000);
                }
                else setTimerColor(0xffffff00);
                timer.startFreeze();
            }
        }
    }

    private void touchUp() {
        //Log.w("dct", "timer state "+timer.state);
        if (timer.getTimerState() == Timer.READY) {    //准备开始
            if (isSwipe) {
                //Log.w("dct", "is swipe");
                switch (gesture) {
                    case 2: //生成新打乱
                        newScramble();
                        break;
                    case 1: //删除上次成绩
                        if (result.length() != 0)
                            new AlertDialog.Builder(context).setTitle(getString(R.string.confirm_delete_last) + result.getTimeAt(result.length() - 1, false))
                                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        delete(result.length() - 1);
                                    }
                                }).setNegativeButton(R.string.btn_cancel, null).show();
                        break;
                    case 3: //修改惩罚
                        if (result.length() != 0) {
                            int penalty = result.getPenalty(result.length() - 1);
                            new AlertDialog.Builder(context).setTitle(getString(R.string.show_time) + result.getTimeAt(result.length() - 1, true))
                                    .setSingleChoiceItems(R.array.opt_penalty, penalty, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            boolean tag = false;
                                            switch (i) {
                                                case 0:
                                                    tag = result.update(result.length() - 1, (byte) 0);
                                                    break;
                                                case 1:
                                                    tag = result.update(result.length() - 1, (byte) 1);
                                                    break;
                                                case 2:
                                                    tag = result.update(result.length() - 1, (byte) 2);
                                                    break;
                                            }
                                            if (tag) {
                                                dialog.dismiss();
                                                result.calcAvg();
                                                if (multiPhase > 0) result.calcMpMean();
                                                if (sortType != 0) result.sortResult();
                                                btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
                                                resAdapter.notifyDataSetChanged();
                                                setTimerText(result.getTimeAt(result.length() - 1, false));
                                                setStatsLabel();
                                            }
                                        }
                                    }).setNegativeButton(getString(R.string.btn_cancel), null).show();
                        }
                        break;
                    case 4: //清空成绩
                        if (result.length() != 0)
                            new AlertDialog.Builder(context).setTitle(R.string.confirm_clear_session)
                                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        sortType = 0;
                                        deleteAll();
                                        setStatsLabel();
                                    }
                                }).setNegativeButton(R.string.btn_cancel, null).show();
                        break;
                    case 5: //手动输入时间
                        inputTime();
                        break;
                    case 6: //查看打乱详情
                        ScrambleDetailDialog scrambleDialog = ScrambleDetailDialog.newInstance(currentScramble.getScramble(), currentScramble.getScrambleLen(), currentScramble.is333Scramble() ? 3 : 0);
                        scrambleDialog.show(getSupportFragmentManager(), "ScrambleDetail");
                        break;
                    case 7: //切换分组
                        String[] list = sessionManager.getSessionNames();
                        new AlertDialog.Builder(context).setTitle(R.string.select_session).setItems(list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sortType = 0;
                                sessionIdx = i;
                                changeSession();
                            }
                        }).setNegativeButton(R.string.btn_cancel, null).show();
                        break;
                }
                isSwipe = false;
            } else if(enterTime > 3) { //蓝牙魔方
                if (bluetoothTools.getCube() != null) {
                    CubeStateDialog dialog = CubeStateDialog.newInstance(bluetoothTools.getCube());
                    dialog.show(getSupportFragmentManager(), "CubeState");
                }
            } else if (enterTime == 1) { //手动输入成绩
                tvTimer.setTextColor(colors[1]);
                inputTime();
            } else if (enterTime == 0) {
                if (freezeTime ==0 || canStart) {    //可以开始计时
                    //Log.w("dct", "freeze=0 & canstart");
                    timer.timeStart = SystemClock.uptimeMillis();
                    if (vibrateType == 1 || vibrateType == 3)
                        vibrator.vibrate(VIBRATE_TIME[vibrateTime]);
                    timer.count();
                    if (multiPhase > 0) {
                        tvMulPhase.setText("");
                        mpCount = multiPhase;
                        Result.multemp[0] = timer.timeStart;
                    }
                    else mpCount = 0;
                    acquireWakeLock();
                    setVisibility(false);
                } else {
                    //Log.w("dct", "other");
                    timer.stopFreeze();
                    setTimerColor(colors[1]);
                }
            }
        } else if (timer.getTimerState() == Timer.RUNNING) {
            if (mpCount !=0) {
                mpCount--;
                setTimerColor(colors[1]);
            }
        } else if (timer.getTimerState() == Timer.INSPECTING) {
            if (freezeTime ==0 || canStart) {
                //tvAssist.setText("");
                timer.timeStart = SystemClock.uptimeMillis();
                penaltyTime = timer.getPenaltyTime();
                isDNF = timer.isDNF();
                if (vibrateType ==1 || vibrateType ==3)
                    vibrator.vibrate(VIBRATE_TIME[vibrateTime]);
                timer.count();
                if (multiPhase > 0) Result.multemp[0] = timer.timeStart;
                acquireWakeLock();
                setVisibility(false);
            } else {
                timer.stopFreeze();
                setTimerColor(0xffff0000);
            }
        } else {
            if (!wca || currentScramble.isBlindfoldScramble()) { penaltyTime = 0; isDNF = false;}
            save((int) timer.time);
            timer.setTimerState(Timer.READY);
            if (!screenOn) releaseWakeLock();
        }
    }

    private void inputTime() {
        final KeypadDialog dialog = new KeypadDialog(this);
        dialog.getKeypad().setOnClickListener(new KeypadView.OnClickListener() {
            @Override
            public void onFinish(String time, int penalty) {
                dialog.dismiss();
                int value = StringUtils.parseTime(time);
                if (value <= 0) {
                    Toast.makeText(context, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                } else {
                    addTime(value, penalty);
                }
            }

            @Override
            public void onClose() {
                dialog.dismiss();
            }
        });
    }

    public void sayAlert(int id) {
        //if (!inspectionAlert) return;
        tts.speak(getString(id), TextToSpeech.QUEUE_FLUSH, null);
    }

    public void save(final int time) {
        if (!isDNF) {
            if (promptToSave) {
                new AlertDialog.Builder(context).setTitle(getString(R.string.show_time) + StringUtils.timeToString(time + penaltyTime))
                        .setItems(R.array.opt_penalty, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0: addTime(time + penaltyTime, 0); break;
                                    case 1: addTime(time + penaltyTime, 1); break;
                                    case 2: addTime(time + penaltyTime, 2); break;
                                }
                            }
                        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newScramble();
                    }
                }).show();
            } else addTime(time + penaltyTime, 0);
        } else if (promptToSave) {
            new AlertDialog.Builder(context).setTitle(getString(R.string.show_time) + "DNF("+ StringUtils.timeToString(time) + ")").setMessage(R.string.confirm_save)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addTime(time, 2);
                        }
                    }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    newScramble();
                }
            }).show();
        } else addTime(time, 2);
        isDNF = false;
    }

    public void addTime(int time, int penalty) {
        result.insert(time, penalty, currentScramble.getScramble(), multiPhase > 0, bluetoothTools.getCube());
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        result.calcAvg();
        if (multiPhase > 0) result.calcMpMean();
        if (sortType != 0) result.sortResult();
        resAdapter.reload();
        if (sortType == 0)
            //rvResult.scrollToPosition(resAdapter.getCount() - 1);
            lvResult.setSelection(resAdapter.getCount() - 1);
        sessionManager.setPuzzle(sessionIdx, scrambleIdx);
        newScramble();
        setStatsLabel();
    }

    private void getResult() {
        result.init(multiPhase > 0, sessionManager.getSession(sessionIdx).getId());
    }

    public void showDetail(int pos) {
        int p = sortType == 0 ? pos : result.getSortIdx(pos);
        String time = result.getTimeAt(p, true);
        int penalty = result.getPenalty(p);
        String scramble = result.getString(p, 4);
        String date = result.getString(5);
        String comment = result.getString(6);
        String solution = result.getString(13);
        if (date == null) date = "";
        if (multiPhase > 0) {   //TODO 显示各分段成绩

        }
        ResultDialog dialog = ResultDialog.newInstance(p, time, scramble, date, penalty, comment, solution, sessionManager.getPuzzle(sessionIdx));
        dialog.show(getSupportFragmentManager(), "result");
    }

    public void updateResult(int num, int penalty) {
        result.update(num, penalty);
        result.calcAvg();
        if (multiPhase > 0) result.calcMpMean();
        if (sortType != 0) result.sortResult();
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        resAdapter.notifyDataSetChanged();
        setStatsLabel();
    }

    public void updateResult(int num, String comment) {
        result.update(num, comment);
    }

    public void copyScramble(String scramble) {
        android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText("text", scramble));
    }

    public void showAvgDetail(final int type, int pos) {
        final int j;
        if (sortType == 0) j = pos;
        else j = result.getSortIdx(pos);
        if (type == 1 && result.getAvg1(j) == -2) return;
        if (type == 2 && result.getAvg2(j) == -2) return;
        String t = "";
        switch (type) {
            case 1:
                t = getString(avg1Type ==0 ? R.string.detail_avg : R.string.detail_mean, avg1len);//String.format(avg1Type ==0 ? getString(R.string.detail_avg) : getString(R.string.detail_mean), avg1len);
                statDetail = avg1Type ==0 ? StringUtils.averageOf(context, result, avg1len, j, null, new ArrayList<Integer>()) : StringUtils.meanOf(context, result, avg1len, j, null);
                break;
            case 2:
                t = getString(avg2Type ==0 ? R.string.detail_avg : R.string.detail_mean, avg2len);
                statDetail = avg2Type ==0 ? StringUtils.averageOf(context, result, avg2len, j, null, new ArrayList<Integer>()) : StringUtils.meanOf(context, result, avg2len, j, null);
                break;
            case 3:
                t = getString(R.string.detail_session_mean);
                statDetail = StringUtils.sessionMean(context, result, null);
                break;
        }
        new AlertDialog.Builder(context).setTitle(t).setMessage(statDetail)
                .setPositiveButton(R.string.btn_copy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        //if (Build.VERSION.SDK_INT >= 11) {
                            android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clip.setPrimaryClip(ClipData.newPlainText("text", statDetail));
//                        }
//                        else {
//                            android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                            clip.setText(statDetail);
//                        }
                        Toast.makeText(context, getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                    }
                }).setNeutralButton(R.string.btn_detail, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        String[] stats = new String[4];
                        ArrayList<Integer> trimIdx = new ArrayList<>();
                        switch (type) {
                            case 1: //滚动平均1
                                statDetail = avg1Type ==0 ? StringUtils.averageOf(context, result, avg1len, j, stats, trimIdx) : StringUtils.meanOf(context, result, avg1len, j, stats);
                                intent.putExtra("avg", avg1Type == 0 ? 1 : 2);
                                intent.putExtra("len", avg1len);
                                intent.putExtra("pos", j);
                                intent.putExtra("detail", stats);
                                intent.putIntegerArrayListExtra("trim", trimIdx);
                                break;
                            case 2: //滚动平均2
                                statDetail = avg2Type ==0 ? StringUtils.averageOf(context, result, avg2len, j, stats, trimIdx) : StringUtils.meanOf(context, result, avg2len, j, stats);
                                intent.putExtra("avg", avg2Type == 0 ? 1 : 2);
                                intent.putExtra("len", avg2len);
                                intent.putExtra("pos", j);
                                intent.putExtra("detail", stats);
                                intent.putIntegerArrayListExtra("trim", trimIdx);
                                break;
                            case 3: //分组平均
                                statDetail = StringUtils.sessionMean(context, result, stats);
                                intent.putExtra("avg", 3);
                                intent.putExtra("len", result.length());
                                intent.putExtra("detail", stats);
                                intent.putIntegerArrayListExtra("trim", trimIdx);
                                break;
                        }
                        startActivityForResult(intent, 3);
                    }
                }).setNegativeButton(R.string.btn_close, null).show();
    }

    public void delete(final int num, boolean prompt) {
        if (prompt) {
            new AlertDialog.Builder(context).setTitle(getString(R.string.confirm_delete_result) + result.getTimeAt(num, false)).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    delete(num);
                }
            }).setNegativeButton(R.string.btn_cancel, null).show();
        } else delete(num);
    }

    private void delete(int num) {
        result.delete(num);
        btnSessionMean.setText(getString(R.string.session_mean, result.getSessionMean()));
        result.calcAvg();
        if (multiPhase > 0) result.calcMpMean();
        if (sortType != 0) result.sortResult();
        resAdapter.reload();
        setStatsLabel();
    }

    private void deleteAll() {
        result.clear();
        btnSessionMean.setText(getString(R.string.session_mean) + "0/0): N/A (N/A)");
        sortType = 0;
        resAdapter.setLength(0);
        setStatsLabel();
        if (sessionManager.getPuzzle(sessionIdx) != 32) {
            sessionManager.setPuzzle(sessionIdx, 32);
            if (sessionIdx < 15) {
                delPref("sestype" + sessionIdx);
            }
        }
    }

    private void setResultTitle() {
        llTitle.removeAllViews();
        TextView tvNum = new TextView(context);
        tvNum.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
        tvNum.setMinWidth(Math.round(dpi * 44));
        tvNum.setText("#");
        tvNum.setTextSize(16);
        tvNum.setTextColor(0xff666666);
        tvNum.setGravity(Gravity.CENTER);
        llTitle.addView(tvNum);
        String[] title;
        if (multiPhase > 0) {
            title = new String[multiPhase + 2];
            title[0] = getString(R.string.time);
            for (int i=1; i<multiPhase+2; i++) title[i] = "P-" + i;
        } else {
            title = new String[] {getString(R.string.time),
                    (avg1Type ==0 ? "AO" : "MO") + avg1len,
                    (avg2Type ==0 ? "AO" : "MO") + avg2len};
        }
        for (String text : title) {
            //View v = new View(context);
            //v.setLayoutParams(new LinearLayout.LayoutParams(1, -1));
            //v.setBackgroundColor(0xddb2b2b2);
            //llTitle.addView(v);
            TextView tv = new TextView(context);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1));
            tv.setTextColor(0xff000000);
            tv.setGravity(Gravity.CENTER);
            tv.setText(text);
            tv.setTextSize(16);
            llTitle.addView(tv);
        }
    }

    private void download(final String fileName) {
        final File f = new File(defaultPath);
        if (!f.exists()) f.mkdirs();
        progressDialog.setTitle(getString(R.string.downloading));
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.show();
        new Thread() {
            public void run() {
                try {
                    URL url = new URL("https://raw.githubusercontent.com/MeigenChou/DCTimer/master/release/"+fileName);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    int filesum = conn.getContentLength();
                    if (filesum == 0) {
                        progressDialog.dismiss();
                        handler.sendEmptyMessage(6);
                        return;
                    }
                    progressDialog.setMax(filesum / 1024);
                    FileOutputStream fs = new FileOutputStream(defaultPath + fileName);
                    byte[] buffer = new byte[1024 * 8];
                    int byteread, bytesum = 0;
                    while ((byteread = is.read(buffer)) != -1) {
                        bytesum += byteread;
                        fs.write(buffer, 0, byteread);
                        handler.sendEmptyMessage(bytesum / 1024 + 100);
                    }
                    fs.close();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //Log.w("dct", defaultPath+fileName);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //File f = new File(defaultPath, fileName);
                        Uri contentUri = FileProvider.getUriForFile(context, "com.dctimer.provider", new File(defaultPath +fileName));
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + defaultPath +fileName), "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(9);
                }
                progressDialog.dismiss();
            }
        }.start();
    }

    //屏幕常亮
    private void acquireWakeLock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void releaseWakeLock() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public void setFilePath(String path, boolean listFiles) {
        currentPath = path;
        if (listFiles)
            editText.setText(currentPath);
        else editText.setText(currentPath + File.separator);
    }

    public void importDatabase(String path) {
        app.closeDb();
        Utils.importDB(path, handler);
    }

    public void exportDatabase(final String path) {
        File file = new File(path);
        if (file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
        else if (file.exists()) {
            new AlertDialog.Builder(context).setTitle(R.string.confirm_overwrite)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int j) {
                            Utils.exportDB(path, handler);
                        }
                    }).setNegativeButton(R.string.btn_cancel, null).show();
        } else Utils.exportDB(path, handler);
    }

    public void importScramble(String scramble) {
        scrambleList = new ArrayList<>();
        importScrambleLen = 0;
        Utils.addScramble(scramble, scrambleList);
        if (scrambleList.size() > 0) newScramble();
    }

    public void exportScramble(final int n, final String path, final String fileName) {
        if (!path.equals(savePath)) {
            savePath = path;
            setPref("scrpath", path);
        }
        File file = new File(path + fileName);
        if (file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
        else if (file.exists()) {
            new AlertDialog.Builder(context).setTitle(R.string.confirm_overwrite)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int j) {
                            Utils.saveScramble(context, progressDialog, handler, currentScramble, path + fileName, n);
                        }
                    }).setNegativeButton(R.string.btn_cancel, null).show();
        } else Utils.saveScramble(context, progressDialog, handler, currentScramble, path + fileName, n);
    }

    private void selectPic() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }
}

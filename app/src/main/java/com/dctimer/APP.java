package com.dctimer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.util.DisplayMetrics;

import com.dctimer.database.DBHelper;
import com.dctimer.database.SessionManager;
import com.dctimer.model.Result;

import java.util.List;

/**
 * Created by meigen on 2017/3/19.
 */

public class APP extends Application {
    private static APP instance;
    private DBHelper db;
    private SessionManager sessionManager;
    private Result result;

    public static final int[] SCREEN_ORIENTATION = {ActivityInfo.SCREEN_ORIENTATION_USER, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_SENSOR};

    public static DisplayMetrics dm;
    public static boolean isImportScr;
    public static boolean isDNF;
    public static int dip300;
    public static int importScrambleLen;
    public static int penaltyTime;
    public static int scrambleState;
    public static float dpi, fontScale;
    public static String currentPath;
    public static String defaultPath;// = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
    public static String dataPath;
    public static String[][] itemStr = new String[17][];

    public static int scrambleIdx;
    public static int[] colors = new int[5];
    public static boolean wca;
    public static boolean showImage;
    public static boolean monoFont;
    public static boolean promptToSave;
    public static int[] solverType = new int[6];
    public static int importType;
    public static int sessionIdx;
    public static int timerSize;
    public static int scrambleSize;
    public static boolean showStat;
    public static boolean inspectionAlert;
    public static int timeFormat;
    public static int decimalMark;
    public static int enterTime;
    public static int timerUpdate;
    public static int timerAccuracy;
    public static int timerFont;
    public static int multiPhase;
    public static int megaColorScheme;
    public static int avg1Type;
    public static int avg2Type;
    public static int solve333;
    public static int solveSq1;
    public static int solve222;
    public static int solvePyr;
    public static int screenOri;
    public static int vibrateType;
    public static int vibrateTime;
    public static int avg1len, avg2len;
    public static boolean useBgcolor;
    public static int opacity;
    public static boolean fullScreen;
    public static boolean screenOn;
    public static boolean selectSession;
    public static String picPath;
    public static int freezeTime;
    public static String savePath;
    public static int[] sesionType = new int[15];
    public static String[] sessionName = new String[15];
    public static int egtype;
    public static boolean[] egIdx = new boolean[10];
    public static String egolls;
    public static boolean simulateSS;
    public static int imageSize;
    public static int[] swipeType = new int[4];
    public static List<String> scrambleList = null;
    public static int sortType;
    public static String statDetail;
    public static boolean dropToStop;
    public static double sensitivity;
    public static boolean darkList;
    public static int samplingRate;
    public static int dataFormat;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static APP getInstance() {
        return instance;
    }

    public void initSession(Context context) {
        db = new DBHelper(context);
        sessionManager = new SessionManager(context, db);
        result = new Result(db);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public Result getResult() {
        return result;
    }

    public void closeDb() {
        result.close();
        db.close();
        db = null;
    }

    public void readPref(SharedPreferences sp) {	//读取配置 TODO
        int idx = sp.getInt("sel", 1);
        if (idx > 19 || idx < -1) idx = 1;
        int idx2 = sp.getInt("sel2", -1);
        if (idx2 < 0 || idx2 > 31) {
            if (idx == 1) idx2 = 1;
            else idx2 = 0;
        }
        scrambleIdx = idx << 5 | idx2;

        sessionIdx = sp.getInt("session", 0);	// 分组
        for (int i = 0; i < 15; i++) {
            sesionType[i] = sp.getInt("sestype" + i, 32);
            sessionName[i] = sp.getString("sesname" + i, "");
        }

        wca = sp.getBoolean("wca", false);	//WCA观察
        inspectionAlert = sp.getBoolean("wcainsp", false); //观察语音提示
        timeFormat = sp.getInt("timeform", 0);	//时间格式
        decimalMark = sp.getInt("decim", 0);
        enterTime = sp.getInt("tiway", 0);	// 计时方式
        if (enterTime > 1) enterTime = 0;
        timerUpdate = sp.getInt("timerupd", 0);	// 计时器更新
        timerAccuracy = sp.getBoolean("prec", true) ? 1 : 0;	// 计时精度
        freezeTime = sp.getInt("tapt", 0);	//启动延时
        multiPhase = sp.getInt("multp", 0);	//分段计时
        simulateSS = sp.getBoolean("simss", false);
        showStat = sp.getBoolean("showstat", true); //显示统计简要
        dropToStop = sp.getBoolean("drop", false);
        int level = sp.getInt("sensity", 47);
        if (level < 0) level = 0;
        sensitivity = (level + 5) / 100d;
        scrambleSize = sp.getInt("stsize", 18);	//打乱字体
        if (scrambleSize < 12) scrambleSize = 12;
        showImage = sp.getBoolean("showscr", true);	//显示打乱状态
        monoFont = sp.getBoolean("monoscr", false);	//等宽打乱字体
        imageSize = sp.getInt("svsize", 220);   //打乱图大小
        egtype = sp.getInt("egtype", 7);
        for (int i = 0; i < 3; i++) {
            if (((egtype << i) & 4) != 0)
                egIdx[i] = true;
        }
        int egoll = sp.getInt("egoll", 254);
        for (int i = 0; i < 7; i++) {
            if (((egoll << i) & 0x80) != 0)
                egIdx[i + 3] = true;
        }
        promptToSave = sp.getBoolean("conft", true);	//提示确认成绩
        avg1Type = sp.getInt("l1tp", 0);	//滚动平均1类型
        avg2Type = sp.getInt("l2tp", 0);	//滚动平均2类型
        avg1len = sp.getInt("l1len", 5);
        avg2len = sp.getInt("l2len", 12);
        selectSession = sp.getBoolean("selses", false);	//自动选择分组
        solve333 = sp.getInt("cxe", 0);	//三阶求解
        //solverType[0] = sp.getInt("cface", 0);	// 十字求解底面
        int cside = sp.getInt("cside", 1);
        if (cside > 6) cside = 1;
        solverType[1] = sp.getInt("sside", 1 << cside);	// 十字求解颜色
        solverType[2] = sp.getInt("pside", 1);    //Petrus求解
        solverType[3] = sp.getInt("rside", 1);    //Roux求解
        solveSq1 = sp.getInt("sq1s", 0);	//SQ1复形计算
        solve222 = sp.getInt("c2fl", 0);//sp.getInt("cube2l", 0);	// 二阶求解
        solvePyr = sp.getInt("pyrv", 0);    //Pyraminx V求解
        solverType[4] = sp.getInt("cface", 1);
        megaColorScheme = sp.getInt("minxc", 1);	//五魔配色
        darkList = sp.getBoolean("dark", false);
        timerFont = sp.getInt("tfont", 3);	// 计时器字体
        timerSize = sp.getInt("ttsize", 60);	//计时器大小
        if (timerSize < 50 || timerSize > 120) timerSize = 60;
        colors[0] = sp.getInt("cl0", 0xff2196F3);	// 背景颜色
        colors[1] = sp.getInt("cl1", 0xffffffff);	// 文字颜色
        colors[2] = sp.getInt("cl2", 0xffff00ff);	//最快单次颜色
        colors[3] = sp.getInt("cl3", 0xffff0000);	//最慢单次颜色
        colors[4] = sp.getInt("cl4", 0xff009900);	//最快平均颜色
        picPath = sp.getString("picpath", "");	//背景图片路径
        opacity = sp.getInt("opac", 35);	//背景不透明度
        if (opacity < 20) opacity = 20;
        useBgcolor = sp.getBoolean("bgcolor", true);	//使用背景颜色
        swipeType[0] = sp.getInt("gesturel", 1);
        swipeType[1] = sp.getInt("gesturer", 2);
        swipeType[2] = sp.getInt("gestureu", 3);
        swipeType[3] = sp.getInt("gestured", 4);
        fullScreen = sp.getBoolean("fulls", false);	// 全屏显示
        screenOn = sp.getBoolean("scron", false);	// 屏幕常亮
        vibrateType = sp.getInt("vibra", 0);	// 震动反馈
        vibrateTime = sp.getInt("vibtime", 2);	// 震动时长
        screenOri = sp.getInt("screenori", 0);	// 屏幕方向
        savePath = sp.getString("scrpath", defaultPath);
        samplingRate = sp.getInt("srate", 44100);
        dataFormat = sp.getInt("dform", AudioFormat.ENCODING_PCM_8BIT);
    }

    public static void resetPref() {
        wca = false; inspectionAlert = false; timeFormat = 0;
        decimalMark = 0; enterTime = 0; timerUpdate = 0;
        timerAccuracy = 1; freezeTime = 0; multiPhase = 0;
        simulateSS = false; showStat = true; scrambleSize = 18;
        showImage = true; monoFont = false; imageSize = 220;
        promptToSave = true; avg1Type = 0; avg2Type = 0;
        avg1len = 5; avg2len = 12; selectSession = false;
        solve333 = 0; solveSq1 = 0; solve222 = 0; solvePyr = 0;
        megaColorScheme = 1; darkList = false; timerFont = 3;
        timerSize = 60; useBgcolor = true; opacity = 35;
        fullScreen = false; screenOn = false; vibrateType = 0;
        vibrateTime = 2; screenOri = 0;
        colors[0] = 0xff2196F3;	colors[1] = 0xffffffff;	colors[2] = 0xffff00ff;
        colors[3] = 0xffff0000;	colors[4] = 0xff009900;
        for (int i = 0; i < 4; i++) swipeType[i] = i + 1;
        samplingRate = 44100; dataFormat = AudioFormat.ENCODING_PCM_8BIT;
    }

    public static int getPixel(int dp) {
        return (int) (dpi * dp);
    }
}

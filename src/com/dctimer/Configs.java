package com.dctimer;

import java.util.ArrayList;

import android.content.pm.ActivityInfo;

public class Configs {
	public final static int SCRNONE = 0;
	public final static int SCRING = 1;
	public final static int NEXTSCRING = 2;
	public final static int SCRDONE = 3;
	public final static int[] vibTime = {30, 50, 80, 150, 240};
	public final static int[] screenOri = {ActivityInfo.SCREEN_ORIENTATION_USER, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
		ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_SENSOR};
	public final static int[] staid = {R.array.tiwStr, R.array.tupdStr, R.array.preStr, R.array.mulpStr,
			R.array.avgStr, R.array.crsStr, R.array.c2lStr, R.array.mncStr,  
			R.array.fontStr, R.array.soriStr, R.array.vibraStr, R.array.vibTimeStr,
			R.array.sq1sStr, R.array.timeForm, R.array.avgStr};
	
	public static boolean isInScr;
	public static boolean idnf = true;
	public static int dip300;
	public static int inScrLen;
	public static int isp2;
	public static int crntScrType = -64;
	public static int scrState;
	public static float scale, fontScale;
	//public static String crntScr;	//当前打乱
	public static String selFilePath;
	public static String defPath;// = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
	public static String extsol;
	public static String nextScr;
	public static String[] scrAry;
	public static String[] scr2Ary;
	public static String[] sol31, sol32;
	public static String[] sesItems = new String[15];
	public static String[][] itemStr = new String[15][];
	
	public static int scrIdx, scr2idx;
	public static int[] colors = new int[5];
	public static boolean wca;
	public static boolean showscr;
	public static boolean monoscr;
	public static boolean hidls;
	public static boolean conft;
	public static int[] solSel = new int[2];
	public static int insType;
	public static int sesIdx;
	public static int timerSize;
	public static int scrambleSize;
	/*
	 * 13 时间格式
	 * 0 计时方式
	 * 1 计时更新
	 * 2 计时精度
	 * 3 分段计时
	 * 14 滚动平均1类型
	 * 4 滚动平均2类型
	 * 5 三阶求解
	 * 12 SQ1复形
	 * 6 二阶底层求解
	 * 7 五魔配色
	 * 8 计时器字体
	 * 9 屏幕方向
	 * 10 震动反馈
	 * 11 震动时长
	 */
	public static int[] stSel = new int[15];
	public static boolean isMulp;
	public static int l1len, l2len;
	public static boolean useBgcolor;
	public static int opacity;
	public static boolean fullScreen;
	public static boolean opnl;
	public static boolean selScr;
	public static String picPath;
	public static int freezeTime;
	public static int rowSpacing;
	public static String savePath;
	public static int[] sesType = new int[15];
	public static String[] sesnames = new String[15];
	public static int egtype;
	public static int egoll;
	public static String egolls;
	public static boolean simulateSS;
	public static int switchThreshold;
	public static int sviewSize;
	
	public static ArrayList<String> inScr = null;
}

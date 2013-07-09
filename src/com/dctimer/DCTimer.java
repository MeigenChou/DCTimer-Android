package com.dctimer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import solvers.*;

import com.weibo.net.*;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DCTimer extends Activity {
	private Context context;
	private TabHost tabHost;
	private Button buttonSst;	// 打乱状态
	public TextView tvTimer;
	private static TextView tvScr;	// 显示打乱
	private Spinner[] spinner = new Spinner[7];
	static byte[] spSel = new byte[7];
	private ArrayAdapter<String> adapter;
	public boolean wca;
	private boolean opnl, opnd, hidscr, conft, isShare, isLogin, isMulp,
			canScr = true, simss, touchDown = false;
	public static boolean hidls, clkform, l1am, l2am;
	private static int selold;
	public static int[] rest;	// 成绩列表
	public static byte[] resp;	// 惩罚
	protected static int[][] mulp = null;
	private static long[] multemp = null;
	public static int resl;
	public static String[] scrst;	// 打乱列表
	public static String crntScr;	// 当前打乱
	private static String nextScr = null;
	private static String extsol;
	private static boolean isNextScr = false;
	private Timer timer;
	private Stackmat stm;
	static int isp2 = 0;
	static boolean idnf = true;
	public int[] cl = new int[5];
	private ImageView iv;
	private boolean scrt = false, bgcolor, fulls, invs, usess, screenOn,
			selSes, isLongPress, isChScr;
	static boolean sqshp;
	private String picPath;
	private int dbLastId;
	public static short[] sestp = new short[15];
	private static String[] sesname = new String[15];
	private static int scrType;

	private GridView myGridView = null, gvTitle = null;
	private TimesAdapter aryAdapter;
	private String[] times = null;
	private Button seMean, clear, hist;	// 时间分布
	private static String slist;
	public static byte[] listnum = {3, 5, 12, 50, 100};
	private static char[] srate = {48000, 44100, 22050, 16000, 11025, 8000};

	private Button reset, rsauth;
	private ColorPicker dialog;
	private SeekBar[] skb = new SeekBar[6];
	private int ttsize, stsize, intv, insType;
	private TextView[] stt = new TextView[67];
	private int sttlen = stt.length;
	private String[][] itemStr = new String[12][];
	private TextView[] std = new TextView[12];
	private LinearLayout[] llay = new LinearLayout[22];
	static int[] stSel = new int[12];
	private int[] staid = {R.array.tiwStr, R.array.tupdStr, R.array.preStr, R.array.mulpStr, R.array.samprate, R.array.crsStr,
			R.array.c2lStr, R.array.mncStr, R.array.fontStr, R.array.soriStr, R.array.vibraStr, R.array.vibTimeStr};
	private TextView tvl;
	private CheckBox[] chkb = new CheckBox[13];
	protected static SharedPreferences share;
	protected static SharedPreferences.Editor edit;
	private DBHelper dbh;
	private Cursor cursor;

	protected boolean canStart;
	protected int frzTime;
	private String[] mItems;
	private Bitmap bitmap;
	private PowerManager.WakeLock wakeLock = null;
	private DisplayMetrics dm;
	private static String addstr = "/data/data/com.dctimer/databases/main.png";
	private static final String CONSUMER_KEY = "3318942954";	// 替换为开发者的appkey，例如"1646212960";
	private static final String CONSUMER_SECRET = "77d13c80e4a9861e4e7c497968c5d4e5";	// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";
	private int mulpCount;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ProgressDialog proDlg = null;
	private static String outPath;
	private static ArrayList<String> inScr = null;
	private static int inScrLen;
	protected static boolean isInScr = false;
	private long exitTime = 0;
	static int egtype;
	private int egoll;
	static String egolls;

	private List<String> items = null, paths = null;
	private ListView listView;
	private String selFilePath;
	private Vibrator vibrator;
	private long[] vibTime = new long[] {30, 50, 80, 150, 240};
	private int[] screenOri = new int[] {2, 0, 8, 1, 4};
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int msw = msg.what;
			switch(msw) {
			case 0: tvScr.setText(crntScr); break;
			case 1: tvScr.setText(crntScr + "\n\n" + getResources().getString(R.string.shape) + extsol); break;
			case 2: tvScr.setText(getResources().getString(R.string.scrambling)); break;
			case 3: tvScr.setText(crntScr + extsol); break;
			case 4: Toast.makeText(DCTimer.this, getResources().getString(R.string.outscr_failed), Toast.LENGTH_SHORT).show(); break;
			case 5: tvTimer.setText("IMPORT"); break;
			case 6: tvScr.setText(crntScr + "\n\n" + getResources().getString(R.string.solving)); break;
			case 7: Toast.makeText(DCTimer.this, getResources().getString(R.string.outscr_success), Toast.LENGTH_SHORT).show(); break;
			default: proDlg.setMessage(msw%100 + "/" + msw/100); break;
			}
		}
	};
	
	class TitleAdapter extends BaseAdapter {
		private Context context;
		private String[] times;
		private TextView tv;
		private int cl;
		public TitleAdapter(Context context, String[] times, int cl) {
			this.context = context;
			this.times = times;
			this.cl = cl;
		}
		public int getCount() {
			if(times != null) return times.length;
			return 0;
		}
		public Object getItem(int position) {return position;}
		public long getItemId(int position) {return position;}
		public View getView(int po, View convertView, ViewGroup parent) {
			if (convertView == null) {
				tv = new TextView(context);
				tv.setLayoutParams(new GridView.LayoutParams(-1, -2));
			}
			else tv = (TextView) convertView;
			tv.setTextSize(16);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(cl);
			tv.setText(times[po]);
			return tv;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(!bgcolor) {
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			bitmap = BitmapFactory.decodeFile(picPath);
			bitmap = getBgPic(bitmap);
			setBgPic(bitmap, share.getInt("opac", 35));
		}
//		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.tab);
		context = this;
		share = super.getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
		selold = spSel[0] = (byte) share.getInt("sel", 1);	//打乱种类
		cl[0] = share.getInt("cl0", 0xff66ccff);	// 背景颜色
		cl[1] = share.getInt("cl1", Color.BLACK);	// 文字颜色
		cl[2] = share.getInt("cl2", 0xffff00ff);	//最快单次颜色
		cl[3] = share.getInt("cl3", Color.RED);	//最慢单次颜色
		cl[4] = share.getInt("cl4", 0xff009900);	//最快平均颜色
		wca = share.getBoolean("wca", false);	//WCA观察
		hidscr = share.getBoolean("hidscr", true);	//隐藏打乱
		hidls = share.getBoolean("hidls", false);	//成绩列表隐藏打乱
		conft = share.getBoolean("conft", true);	//提示确认成绩
		l1am = share.getBoolean("l1am", true);
		l2am = share.getBoolean("l2am", true);
		spSel[1] = (byte) share.getInt("cface", 0);	// 十字求解底面
		spSel[2] = (byte) share.getInt("list2", 1);
		spSel[3] = (byte) share.getInt("cside", 1);	// 三阶求解颜色
		spSel[4] = (byte) share.getInt("list1", 1);
		spSel[5] = (byte) share.getInt("group", 0);	// 分组
		spSel[6] = (byte) share.getInt("sel2", 0);	// 二级打乱
		ttsize = share.getInt("ttsize", 60);	//计时器字体
		stsize = share.getInt("stsize", 18);	//打乱字体
		clkform = share.getBoolean("timmh", true);	//时间格式
		stSel[0] = share.getInt("tiway", 0);	// 计时方式
		stSel[1] = share.getInt("timerupd", 0);	// 计时器更新
		stSel[2] = share.getBoolean("prec", true) ? 1 : 0;	// 计时精度
		stSel[3] = share.getInt("multp", 0);	//分段计时
		stSel[4] = share.getInt("srate", 1);	// 采样频率
		Stackmat.samplingRate = srate[stSel[4]];
		stSel[5] = share.getInt("cxe", 0);	//三阶求解
		stSel[6] = share.getInt("cube2l", 0);	// 二阶底层求解
		stSel[7] = share.getInt("minxc", 1);	//五魔配色
		stSel[8] = share.getInt("tfont", 3);	// 计时器字体
		stSel[9] = share.getInt("screenori", 0);	// 屏幕方向
		stSel[10] = share.getInt("vibra", 0);	// 震动反馈
		stSel[11] = (byte) share.getInt("vibtime", 2);	// 震动时长
		bgcolor = share.getBoolean("bgcolor", true);
		sqshp = share.getBoolean("sqshp", false);	// SQ1复形计算
		fulls = share.getBoolean("fulls", false);	// 全屏显示
		usess = share.getBoolean("usess", false);	// ss计时器
		Stackmat.inv = invs = share.getBoolean("invs", false);	// 反转信号
		opnl = share.getBoolean("scron", false);	// 屏幕常亮
		opnd = share.getBoolean("scrgry", true);
		selSes = share.getBoolean("selses", false);	//自动选择分组
		picPath = share.getString("picpath", "");
		frzTime = share.getInt("tapt", 0);	//启动延时
		isMulp = stSel[3] != 0;
		intv = share.getInt("intv", 30);	//成绩列表行距
		outPath = share.getString("scrpath", Environment.getExternalStorageDirectory().getPath()+File.separator+"DCTimer/");
		edit = share.edit();
		for(int i=0; i<15; i++) {
			sestp[i] = (short) share.getInt("sestp" + i, -1);
			sesname[i] = share.getString("sesname" + i, "");
		}
		long sestype = share.getLong("sestype", -1);
		if(sestype != -1) {
			for(int i=0; i<9; i++) {
				int temp = Mi.getSessionType(sestype, i);
				if(temp != 0x7f) {
					edit.putInt("sestp" + i, temp);
				}
			}
			edit.remove("sestype");
			edit.commit();
		}
		egtype = share.getInt("egtype", 7);
		egoll = share.getInt("egoll", 254);
		simss = share.getBoolean("simss", false);
		setEgOll();
		
		if(fulls) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(opnl) {acquireWakeLock(); screenOn = true;}
		mItems = getResources().getStringArray(R.array.tabInd);
		tabHost = (TabHost) super.findViewById(R.id.tabhost);	//取得TabHost对象
		tabHost.setup();	//建立TabHost对象
		int[] ids = {R.id.tab_timer, R.id.tab_list, R.id.tab_setting};
		for (int x=0; x<3; x++) {	//循环取出所有布局标记
			TabSpec myTab = tabHost.newTabSpec("tab" + x);	//定义TabSpec
			if(x == 0) myTab.setIndicator(mItems[x], getResources().getDrawable(R.drawable.img1));
			else if(x == 1) myTab.setIndicator(mItems[x], getResources().getDrawable(R.drawable.img2));
			else myTab.setIndicator(mItems[x], getResources().getDrawable(R.drawable.img3));
			myTab.setContent(ids[x]);	//设置显示的组件
			tabHost.addTab(myTab);	//增加标签
		}
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(bgcolor) tabHost.setBackgroundColor(cl[0]);
		else {
			try {
				bitmap = BitmapFactory.decodeFile(picPath);
				bitmap = getBgPic(bitmap);
				setBgPic(bitmap, share.getInt("opac", 35));
			} catch (Exception e) {
				tabHost.setBackgroundColor(cl[0]);
				Toast.makeText(DCTimer.this, getResources().getString(R.string.not_exist), Toast.LENGTH_SHORT).show();
			}
		}
		tabHost.setCurrentTab(0);	// 设置开始索引
		
		tvScr = (TextView) findViewById(R.id.myTextView1);
		tvTimer = (TextView) findViewById(R.id.myTextView2);
		buttonSst = (Button) findViewById(R.id.myButtonSst);
		ids = new int[] {R.id.std01, R.id.std02, R.id.std03, R.id.std04, R.id.std05, R.id.std06, R.id.std07,
				R.id.std08, R.id.std09, R.id.std10, R.id.std11, R.id.std12};
		for(int i=0; i<12; i++) {
			itemStr[i] = getResources().getStringArray(staid[i]);
			std[i] = (TextView) findViewById(ids[i]);
			std[i].setText(itemStr[i][stSel[i]]);
			std[i].setTextColor(0x80000000|(cl[1]&0xffffff));
		}
		ids = new int[] {R.id.mySpinner, R.id.spinner2, R.id.spinner5, R.id.spinner3, R.id.spinner4, R.id.spinner6};
		for(int i=0; i<6; i++) {
			switch(i) {
			case 0: mItems = getResources().getStringArray(R.array.cubeStr); break;
			case 1: mItems = getResources().getStringArray(R.array.faceStr); break;
			case 2: mItems = getResources().getStringArray(R.array.list2Str); break;
			case 3: mItems = getResources().getStringArray(R.array.sideStr); break;
			case 4: mItems = getResources().getStringArray(R.array.list1Str); break;
			case 5:
				mItems = new String[15];
				for (int j = 0; j < 15; j++)
					mItems[j] = (j + 1) + (sesname[j].equals("") ? "　" : ": "+sesname[j]);
				break;
			}
			spinner[i] = (Spinner) findViewById(ids[i]);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner[i].setAdapter(adapter);
			spinner[i].setSelection(spSel[i]);
		}
		
		if(stSel[5] == 0) {spinner[1].setEnabled(false); spinner[3].setEnabled(false);}
		spinner[6] = (Spinner) findViewById(R.id.sndscr);
		set2ndsel();
		tvScr.setTextSize(stsize);
		tvTimer.setTextSize(ttsize);
		setTimerFont(stSel[8]);
		if(stSel[9] > 0) this.setRequestedOrientation(screenOri[stSel[9]]);
		
		stm = new Stackmat(this);
		if(usess) {
			tvTimer.setText("OFF");
			if(stm.creatAudioRecord((int)srate[stSel[4]]));
			else {
				edit.putInt("srate", 1);
				edit.commit();
			}
		} else {
			if(stSel[0] == 0) {
				if(stSel[2] == 0) tvTimer.setText("0.00");
				else tvTimer.setText("0.000");
			}
			else if(stSel[0] == 1) tvTimer.setText("IMPORT");
		}
		
		timer = new Timer(this);
		dbh = new DBHelper(this);

		myGridView = (GridView) findViewById(R.id.myGridView);
		gvTitle = (GridView) findViewById(R.id.gv_title);
		seMean = (Button) findViewById(R.id.mButtonoa);
		clear = (Button) findViewById(R.id.mButtonClr);
		hist = (Button) findViewById(R.id.mButtonHist);
		rsauth = (Button) findViewById(R.id.auth_sina);
		ids = new int[] {R.id.checkeg2, R.id.checkcll, R.id.lcheck2, R.id.lcheck1, R.id.checkeg1, R.id.checkegn,
				R.id.checkegs, R.id.checkega, R.id.checkegpi, R.id.checkegl, R.id.checkegt, R.id.checkegu, R.id.checkegh};
		for(int i=0; i<13; i++) chkb[i] = (CheckBox) findViewById(ids[i]);
		ids = new int[] {R.id.seekb1, R.id.seekb2, R.id.seekb3, R.id.seekb4, R.id.seekb5, R.id.seekb6};
		for(int i=0; i<6; i++) skb[i] = (SeekBar) findViewById(ids[i]);
		tvl = (TextView) findViewById(R.id.tv4);
		ids = new int[] {R.id.stt00, R.id.stt01, R.id.stt02, R.id.stt08, R.id.stt09, R.id.stt05, R.id.stt21,
				R.id.stt07, R.id.stt03, R.id.stt22, R.id.stt17, R.id.stt11, R.id.stt12, R.id.stt13, R.id.stt14,
				R.id.stt15, R.id.stt16, R.id.stt10, R.id.stt18, R.id.stt19, R.id.stt23, R.id.stt04, R.id.stt06,
				R.id.stt20, R.id.stt24, R.id.stt25, R.id.stt26, R.id.stt27, R.id.stt28, R.id.stt29, R.id.stt30,
				R.id.stt31, R.id.stt32, R.id.stt33, R.id.stt34, R.id.stt35, R.id.stt36, R.id.stt37, R.id.stt38,
				R.id.stt39, R.id.stt40, R.id.stt41, R.id.stcheck1, R.id.stt42, R.id.stcheck2, R.id.stt43,
				R.id.stcheck3, R.id.stt44, R.id.stcheck4, R.id.stt45, R.id.stcheck5, R.id.stt46, R.id.stcheck6,
				R.id.stt47, R.id.stcheck7, R.id.stt48, R.id.stcheck8, R.id.stt49, R.id.stcheck9, R.id.stt50,
				R.id.stcheck10, R.id.stt51, R.id.stcheck11, R.id.stt52, R.id.stcheck12, R.id.stt53,
				R.id.stcheck13};
		for(int i=0; i<sttlen; i++) stt[i] = (TextView) findViewById(ids[i]);
		ids = new int[] {R.id.lay01, R.id.lay02, R.id.lay03, R.id.lay04, R.id.lay05, R.id.lay06,
				R.id.lay07, R.id.lay08, R.id.lay09, R.id.lay10, R.id.lay11, R.id.lay12, R.id.lay13,
				R.id.lay14, R.id.lay15, R.id.lay16, R.id.lay17, R.id.lay18, R.id.lay19, R.id.lay20,
				R.id.lay21, R.id.lay22};
		for(int i=0; i<22; i++) llay[i] = (LinearLayout) findViewById(ids[i]);
		reset = (Button) findViewById(R.id.reset);
		for(int i=0; i<12; i++) llay[i].setOnTouchListener(touchListener);
		for(int i=12; i<22; i++) llay[i].setOnTouchListener(touchList2);
		ids = new int[] {95, 25, 41, 100, 20, 100};
		for(int i=0; i<6; i++) skb[i].setMax(ids[i]);
		int ssvalue = share.getInt("ssvalue", 50);
		ids = new int[] {share.getInt("ttsize", 60) - 50, share.getInt("stsize", 18) - 12, share.getInt("intv", 30) - 20,
				share.getInt("opac", 35), frzTime, ssvalue};
		for(int i=0; i<6; i++) skb[i].setProgress(ids[i]);
		stt[3].setText(getResources().getString(R.string.timer_size) + share.getInt("ttsize", 60));
		stt[4].setText(getResources().getString(R.string.scrsize) + share.getInt("stsize", 18));
		stt[10].setText(getResources().getString(R.string.row_spacing) + share.getInt("intv", 30));
		stt[31].setText(getResources().getString(R.string.time_tap) + frzTime/20D);
		stt[39].setText(getResources().getString(R.string.stt_ssvalue) + ssvalue);
		Stackmat.switchThreshold = ssvalue;
		for(int i=0; i<6; i++) skb[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		stt[42].setBackgroundResource(wca ? R.drawable.switchon : R.drawable.switchoff);
		stt[44].setBackgroundResource(clkform ? R.drawable.switchon : R.drawable.switchoff);
		stt[46].setBackgroundResource(simss ? R.drawable.switchon : R.drawable.switchoff);
		stt[48].setBackgroundResource(usess ? R.drawable.switchon : R.drawable.switchoff);
		stt[50].setBackgroundResource(invs ? R.drawable.switchon : R.drawable.switchoff);
		stt[52].setBackgroundResource(hidscr ? R.drawable.switchon : R.drawable.switchoff);
		stt[54].setBackgroundResource(conft ? R.drawable.switchon : R.drawable.switchoff);
		stt[56].setBackgroundResource(hidls ? R.drawable.switchoff : R.drawable.switchon);
		stt[58].setBackgroundResource(selSes ? R.drawable.switchon : R.drawable.switchoff);
		stt[60].setBackgroundResource(sqshp ? R.drawable.switchon : R.drawable.switchoff);
		stt[62].setBackgroundResource(fulls ? R.drawable.switchon : R.drawable.switchoff);
		stt[64].setBackgroundResource(opnl ? R.drawable.switchon : R.drawable.switchoff);
		stt[66].setBackgroundResource(opnd ? R.drawable.switchon : R.drawable.switchoff);
		for(int i=42; i<67; i+=2) stt[i].setOnClickListener(new OnClickListener());
		
		if(l1am) chkb[3].setChecked(true);
		if(l2am) chkb[2].setChecked(true);
		getSession(spSel[5]);
		seMean.setText(getResources().getString(R.string.session_average) + Mi.sesMean());
		setGvTitle();
		if(isMulp) multemp = new long[7];
		setGridView(true);
		
		if(usess) {
			if(!stm.isStart) stm.start();
		}
		if((egtype & 4) != 0) chkb[1].setChecked(true);
		if((egtype & 2) != 0) chkb[4].setChecked(true);
		if((egtype & 1) != 0) chkb[0].setChecked(true);
		if((egoll & 128) != 0) chkb[8].setChecked(true);
		if((egoll & 64) != 0) chkb[12].setChecked(true);
		if((egoll & 32) != 0) chkb[11].setChecked(true);
		if((egoll & 16) != 0) chkb[10].setChecked(true);
		if((egoll & 8) != 0) chkb[9].setChecked(true);
		if((egoll & 4) != 0) chkb[6].setChecked(true);
		if((egoll & 2) != 0) chkb[7].setChecked(true);
		if((egoll & 1) != 0) chkb[5].setChecked(true);
		for(int i=0; i<chkb.length; i++)
			chkb[i].setOnCheckedChangeListener(listener);
		
		String token=share.getString("token", null);
		String expires_in=share.getString("expin", null);
		if(token==null || expires_in==null ||
				(System.currentTimeMillis()-share.getLong("totime", 0))/1000 >= Integer.parseInt(expires_in)) {
			isLogin = false;
			rsauth.setText(getResources().getString(R.string.login));
		} else {
			isLogin = true;
			rsauth.setText(getResources().getString(R.string.logout));
		}
		
		tvl.setTextColor(cl[1]);
		for(int i=0; i<sttlen; i++) stt[i].setTextColor(cl[1]);
		for(int i=0; i<chkb.length; i++) chkb[i].setTextColor(cl[1]);
		tvScr.setTextColor(cl[1]);
		tvTimer.setTextColor(cl[1]);
		
		vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		
		//打乱类型
		spinner[0].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[0] = (byte) arg2;
				if(spSel[0] != selold){
					spSel[6] = 0; selold = spSel[0];
				}
				set2ndsel();
				setScrType();
				newScr(true);
				if(selSes) searchSesType();
				if(inScr != null && inScr.size() != 0) inScr = null;
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		spinner[6].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spSel[6] != arg2) {
					spSel[6] = (byte) arg2;
					//set2ndsel();
					setScrType();
					newScr(true);
					if (selSes) searchSesType();
					if (inScr != null && inScr.size() != 0) inScr = null;
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//滚动平均0
		spinner[4].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[4] != arg2) {
					spSel[4] = (byte) arg2;
					if(!isMulp) {
						setGvTitle();
						setGridView(false);
					}
					edit.putInt("list1", spSel[4]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//滚动平均1
		spinner[2].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[2] != arg2) {
					spSel[2] = (byte) arg2;
					if(!isMulp) {
						setGvTitle();
						setGridView(false);
					}
					edit.putInt("list2", spSel[2]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		//分组
		spinner[5].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[5] != arg2) {
					spSel[5] = (byte) arg2;
					getSession(arg2);
					seMean.setText(getResources().getString(R.string.session_average) + Mi.sesMean());
					setGridView(true);
					edit.putInt("group", spSel[5]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//十字底面
		spinner[1].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[1] != arg2) {
					spSel[1] = (byte) arg2;
					edit.putInt("cface", spSel[1]);
					edit.commit();
					if(spSel[0]==1 && (spSel[6]==0 || spSel[6]==1 || spSel[6]==5 || spSel[6]==19))
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(6);
								extsol = "\n"+Cross.cross(crntScr, spSel[1], spSel[3]);
								handler.sendEmptyMessage(3);
								isNextScr = false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], false);
								isNextScr = true;
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//颜色
		spinner[3].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[3] != arg2) {
					spSel[3] = (byte) arg2;
					edit.putInt("cside", spSel[3]);
					edit.commit();
					if(spSel[0]==1 && (spSel[6]==0 || spSel[6]==1 || spSel[6]==5 || spSel[6]==19))
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(6);
								switch(stSel[5]) {
								case 1: extsol="\n"+Cross.cross(crntScr, spSel[1], spSel[3]); break;
								case 2: extsol="\n"+Cross.xcross(crntScr, spSel[3]); break;
								case 3: extsol="\n"+EOline.eoLine(crntScr, spSel[3]); break;
								case 4: extsol="\n"+PetrusxRoux.roux(crntScr, spSel[3]); break;
								case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, spSel[3]); break;
								}
								handler.sendEmptyMessage(3);
								isNextScr=false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], false);
								isNextScr = true;
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//打乱状态
		buttonSst.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(Mi.viewType > 0) {
					int width = dm.widthPixels;
					LayoutInflater inflater = LayoutInflater.from(DCTimer.this);	// 取得LayoutInflater对象
					final View popView = inflater.inflate(R.layout.popwindow, null);	// 读取布局管理器
					popView.setBackgroundColor(0xaaece9d8);
					iv = (ImageView) popView.findViewById(R.id.ImageView1);
					Bitmap bm = Bitmap.createBitmap(width, width*3/4, Config.ARGB_8888);
					Canvas c = new Canvas(bm);
					c.drawColor(0);
					Paint p = new Paint();
					p.setAntiAlias(true);
					Mi.drawScr(spSel[6], width, p, c);
					iv.setImageBitmap(bm);
					new AlertDialog.Builder(DCTimer.this).setView(popView)
					.setNegativeButton(getResources().getString(R.string.btn_close), null).show();
				} else Toast.makeText(DCTimer.this, getResources().getString(R.string.not_support), Toast.LENGTH_SHORT).show();
			}
		});
		//打乱
		tvScr.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				scrt = true;
				setTouch(event);
				return timer.state != 0;
			}
		});
		tvScr.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(timer.state == 0) {
					isLongPress = true;
					LayoutInflater factory = LayoutInflater.from(DCTimer.this);
					final View view = factory.inflate(R.layout.scr_layout, null);
					final EditText editText = (EditText)view.findViewById(R.id.etslen);
					final TextView tvScr = (TextView)view.findViewById(R.id.cnt_scr);
					tvScr.setText(crntScr);
					editText.setText(""+Mi.scrLen);
					if(Mi.scrLen==0)editText.setEnabled(false);
					new AlertDialog.Builder(DCTimer.this).setView(view)
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String et = editText.getText().toString();
							int len = et.equals("")?0:Integer.parseInt(editText.getText().toString());
							if(editText.isEnabled() && len>0) {
								if(len>180) len=180;
								if(len != Mi.scrLen) {
									Mi.scrLen = len;
									if((spSel[0]==1 && spSel[6]==19) || (spSel[0]==20 && spSel[6]==4)) isChScr = true;
									newScr(false);
								}
							}
						}
					}).setNeutralButton(getResources().getString(R.string.copy_scr), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
							clip.setText(crntScr);
							Toast.makeText(DCTimer.this, getResources().getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
						}
					}).show();
				}
				return true;
			}
		});
		//计时器
		tvTimer.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				scrt = false;
				if(!usess) {
					if(stSel[0] == 0) setTouch(event);
					else if(stSel[0] == 1) inputTime(event.getAction());
				}
				return true;
			}
		});
		myGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int p, long arg3) {
				if(isMulp) {
					if(p/(stSel[3]+2)<resl && p%(stSel[3]+2)==0) singTime(p, stSel[3]+2);
				}
				else if(p%3 == 0)
					singTime(p, 3);
				else if(p%3==1 && p/3>listnum[spSel[4]]-2)
					showAlertDialog(1, p/3);
				else if(p%3==2 && p/3>listnum[spSel[2]+1]-2)
					showAlertDialog(2, p/3);
			}
		});
		//分组平均
		seMean.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				for(int i=0; i<resl; i++)
					if(resp[i] != 2) {
						showAlertDialog(3, 0);
						return;
					}
			}
		});
		//清空成绩
		clear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(resl == 0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
				else {
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_clear_session))
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j) {deleteAll();}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
				}
			}
		});
		//时间分布
		hist.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int width = dm.widthPixels;
				int height = dm.heightPixels;
				if(height<width) width = height;
				width = (int) (width*0.9);
				LayoutInflater inflater = LayoutInflater.from(DCTimer.this);
				final View popView = inflater.inflate(R.layout.popwindow, null);
				popView.setBackgroundColor(0xddf0f0f0);
				iv = (ImageView) popView.findViewById(R.id.ImageView1);
				Bitmap bm = Bitmap.createBitmap(width, (int)(width*1.2), Config.ARGB_8888);
				Canvas c = new Canvas(bm);
				c.drawColor(0);
				Paint p = new Paint();
				p.setAntiAlias(true);
				Mi.drawHist(width, p, c);
				iv.setImageBitmap(bm);
				new AlertDialog.Builder(DCTimer.this).setView(popView)
				.setNegativeButton(getResources().getString(R.string.btn_close), null).show();
			}
		});
		//恢复默认设置
		reset.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_reset))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j){
						//TODO
						wca=false; clkform=true; simss=false; usess=false; invs=Stackmat.inv=false;
						hidscr=true; conft=true; hidls=false; selSes=false; sqshp=false; fulls=false;
						l1am=true; l2am=true; bgcolor=true; opnl=false; opnd=true; isMulp=false;
						spSel[1]=0; spSel[2]=1; spSel[3]=1; spSel[4]=1;
						stSel[0]=0; stSel[1]=0; stSel[2]=1; stSel[3]=0; stSel[4]=1;
						stSel[5]=0; stSel[6]=0; stSel[7]=1; stSel[8]=3; stSel[9]=0;
						stSel[10]=0; stSel[11]=2; 
						tvTimer.setTextSize(60); tvScr.setTextSize(18);
						cl[0] = 0xff66ccff;	cl[1] = 0xff000000;	cl[2] = 0xffff00ff;
						cl[3] = 0xffff0000;	cl[4] = 0xff009900;
						int i;
						for(i=2;i<4;i++) chkb[i].setChecked(true);
						for(i=0; i<12; i++) std[i].setText(itemStr[i][stSel[i]]);
						for(i=1; i<5; i++) spinner[i].setSelection(spSel[i]);
						int is[] = {44, 52, 54, 56, 66};
						for(i=0; i<5; i++) stt[is[i]].setBackgroundResource(R.drawable.switchon);
						is = new int[] {42, 46, 48, 50, 58, 60, 62, 64};
						for(i=0; i<8; i++) stt[is[i]].setBackgroundResource(R.drawable.switchoff);
						is = new int[] {10, 6, 10, 35, 0, 50};
						for(i=0; i<6; i++) skb[i].setProgress(is[i]);
						intv = 30; frzTime = 0;
						tabHost.setBackgroundColor(cl[0]);
						tvl.setTextColor(cl[1]);
						for(i=0; i<sttlen; i++) stt[i].setTextColor(cl[1]);
						for(i=0; i<chkb.length; i++) chkb[i].setTextColor(cl[1]);
						tvScr.setTextColor(cl[1]);
						tvTimer.setTextColor(cl[1]);
						setGridView(false);
						releaseWakeLock();
						screenOn=false;
						edit.remove("cl0");	edit.remove("cl1");	edit.remove("cl2");
						edit.remove("cl3");	edit.remove("cl4");	edit.remove("wca");
						edit.remove("cxe");
						edit.remove("l1am");	edit.remove("l2am");	edit.remove("mnxc");
						edit.remove("prec");	edit.remove("mulp");	edit.remove("invs");
						edit.remove("tapt");	edit.remove("intv");	edit.remove("opac");
						edit.remove("mclr");	edit.remove("prom");
						edit.remove("hidls");	edit.remove("conft");	edit.remove("list1");
						edit.remove("list2");	edit.remove("timmh");	edit.remove("tiway");
						edit.remove("cface");	edit.remove("cside");	edit.remove("srate");
						edit.remove("tfont");	edit.remove("vibra");	edit.remove("sqshp");
						edit.remove("fulls");	edit.remove("usess");	edit.remove("scron");
						edit.remove("multp");	edit.remove("minxc");
						edit.remove("hidscr");	edit.remove("ttsize");	edit.remove("stsize");
						edit.remove("cube2l");	edit.remove("scrgry");	edit.remove("selses");
						edit.remove("ismulp");
						edit.remove("vibtime");	edit.remove("bgcolor");	edit.remove("ssvalue");
						edit.remove("timerupd");
						edit.remove("screenori");
						edit.commit();
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
			}
		});
		//微博授权
		rsauth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isLogin)
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.con_rsauth))
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j){
							edit.remove("token");
							edit.remove("expin");
							edit.remove("totime");
							edit.commit();
							isLogin=false;
							Toast.makeText(DCTimer.this, getResources().getString(R.string.rsauth), Toast.LENGTH_SHORT).show();
							rsauth.setText(getResources().getString(R.string.login));
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
				else {
					isShare = false;
					auth();
				}
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(opnd && screenOn) releaseWakeLock();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(opnd && screenOn) acquireWakeLock();
	}
	private class OnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.stcheck1:	//WCA观察
				stt[42].setBackgroundResource(wca ? R.drawable.switchoff : R.drawable.switchon);
				wca = !wca; edit.putBoolean("wca", wca);
				break;
			case R.id.stcheck2:	//时间格式
				stt[44].setBackgroundResource(clkform ? R.drawable.switchoff : R.drawable.switchon);
				clkform = !clkform; edit.putBoolean("timmh", clkform);
				if(resl>0) setGridView(false);
				break;
			case R.id.stcheck3:	//模拟ss计时
				stt[46].setBackgroundResource(simss ? R.drawable.switchoff : R.drawable.switchon);
				simss = !simss; edit.putBoolean("simss", simss);
				break;
			case R.id.stcheck4:	//使用ss计时
				stt[48].setBackgroundResource(usess ? R.drawable.switchoff : R.drawable.switchon);
				usess = !usess; edit.putBoolean("usess", usess);
				if(usess) {
					tvTimer.setText("OFF");
					if(!stm.isStart) stm.start();
				} else {
					if(stm.isStart) stm.stop();
					if(stSel[0]==0) tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
					else if(stSel[0]==1) tvTimer.setText("IMPORT");
				}
				break;
			case R.id.stcheck5:	//信号反转
				stt[50].setBackgroundResource(invs ? R.drawable.switchoff : R.drawable.switchon);
				invs = Stackmat.inv = !invs; edit.putBoolean("invs", invs);
				break;
			case R.id.stcheck6:	//隐藏打乱
				stt[52].setBackgroundResource(hidscr ? R.drawable.switchoff : R.drawable.switchon);
				hidscr = !hidscr; edit.putBoolean("hidscr", hidscr);
				break;
			case R.id.stcheck7:	//确认时间
				stt[54].setBackgroundResource(conft ? R.drawable.switchoff : R.drawable.switchon);
				conft = !conft; edit.putBoolean("conft", conft);
				break;
			case R.id.stcheck8:	//成绩列表隐藏打乱
				stt[56].setBackgroundResource(hidls ? R.drawable.switchon : R.drawable.switchoff);
				hidls = !hidls; edit.putBoolean("hidls", hidls);
				break;
			case R.id.stcheck9:	//自动选择分组
				stt[58].setBackgroundResource(selSes ? R.drawable.switchoff : R.drawable.switchon);
				selSes = !selSes; edit.putBoolean("selses", selSes);
				break;
			case R.id.stcheck10:	//SQ1复形求解
				stt[60].setBackgroundResource(sqshp ? R.drawable.switchoff : R.drawable.switchon);
				sqshp = !sqshp; edit.putBoolean("sqshp", sqshp);
				if(spSel[0]==8) {
					if(sqshp) {
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(6);
								extsol = " " + Sq1Shape.solve(crntScr);
								handler.sendEmptyMessage(1);
								isNextScr = false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], false);
								isNextScr = true;
							}
						}.start();
					}
					else tvScr.setText(crntScr);
				}
				break;
			case R.id.stcheck11:	//全屏显示
				stt[62].setBackgroundResource(fulls ? R.drawable.switchoff : R.drawable.switchon);
				if(fulls) getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				else getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				fulls = !fulls; edit.putBoolean("fulls", fulls);
				break;
			case R.id.stcheck12:	//屏幕常亮
				stt[64].setBackgroundResource(opnl ? R.drawable.switchoff : R.drawable.switchon);
				if(opnl) {
					if(timer.state != 1) releaseWakeLock();
				} else acquireWakeLock();
				opnl = !opnl; edit.putBoolean("scron", opnl);
				break;
			case R.id.stcheck13:
				stt[66].setBackgroundResource(opnd ? R.drawable.switchoff : R.drawable.switchon);
				if(screenOn)releaseWakeLock();
				opnd = !opnd;
				if(screenOn)acquireWakeLock();
				edit.putBoolean("scrgry", opnd);
				break;
			}
			edit.commit();
		}
	}
	private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getResources().getString(R.string.timer_size) + (seekBar.getProgress()+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getResources().getString(R.string.scrsize) + (seekBar.getProgress()+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getResources().getString(R.string.row_spacing) + (seekBar.getProgress()+20));
			else if(seekBar.getId()==R.id.seekb5)stt[31].setText(getResources().getString(R.string.time_tap) + (seekBar.getProgress()/20D));
			else if(seekBar.getId()==R.id.seekb6)stt[39].setText(getResources().getString(R.string.stt_ssvalue) + seekBar.getProgress());
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getResources().getString(R.string.timer_size)+ (seekBar.getProgress()+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getResources().getString(R.string.row_spacing)+ (seekBar.getProgress()+20));
			else if(seekBar.getId()==R.id.seekb5)stt[31].setText(getResources().getString(R.string.time_tap)+ (seekBar.getProgress()/20D));
			else if(seekBar.getId()==R.id.seekb6)stt[39].setText(getResources().getString(R.string.stt_ssvalue) + seekBar.getProgress());
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			switch (seekBar.getId()) {
			case R.id.seekb1:	//计时器字体
				stt[3].setText(getResources().getString(R.string.timer_size)+ (seekBar.getProgress()+50));
				edit.putInt("ttsize", seekBar.getProgress()+50);
				tvTimer.setTextSize(seekBar.getProgress()+50);
				break;
			case R.id.seekb2:	//打乱字体
				stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
				edit.putInt("stsize", seekBar.getProgress()+12);
				tvScr.setTextSize(seekBar.getProgress()+12);
				break;
			case R.id.seekb3:	//成绩列表行距
				intv=seekBar.getProgress()+20;
				stt[10].setText(getResources().getString(R.string.row_spacing)+ intv);
				if(resl!=0) setGridView(false);
				edit.putInt("intv", seekBar.getProgress()+20);
				break;
			case R.id.seekb4:	//背景图不透明度
				if(!bgcolor) setBgPic(bitmap, seekBar.getProgress());
				edit.putInt("opac", seekBar.getProgress());
				break;
			case R.id.seekb5:	//启动延时
				frzTime=seekBar.getProgress();
				stt[31].setText(getResources().getString(R.string.time_tap)+ (frzTime/20D));
				edit.putInt("tapt", frzTime);
				break;
			case R.id.seekb6:	//ss参数
				int ssvalue = seekBar.getProgress();
				stt[39].setText(getResources().getString(R.string.stt_ssvalue) + ssvalue);
				Stackmat.switchThreshold = ssvalue;
				edit.putInt("ssvalue", ssvalue);
				break;
			}
			edit.commit();
		}
	}
	private OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			selFilePath = paths.get(arg2);
			getFileDir(selFilePath);
		}
	};
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {	//TODO
			//RA0去尾统计
			case R.id.lcheck1:
				l1am = isChecked; edit.putBoolean("l1am", isChecked);
				if(!isMulp) setGvTitle();
				if(resl>0 && !isMulp) setGridView(false);
				break;
			case R.id.lcheck2:
				l2am = isChecked; edit.putBoolean("l2am", isChecked);
				if(!isMulp)setGvTitle();
				if(resl>0 && !isMulp) setGridView(false);
				break;
			//EG训练打乱
			case R.id.checkcll:
				if(isChecked) egtype |= 4;
				else egtype &= 3;
				edit.putInt("egtype", egtype);
				break;
			case R.id.checkeg1:
				if(isChecked) egtype |= 2;
				else egtype &= 5;
				edit.putInt("egtype", egtype);
				break;
			case R.id.checkeg2:
				if(isChecked) egtype |= 1;
				else egtype &= 6;
				edit.putInt("egtype", egtype);
				break;
			case R.id.checkegpi:
				if(isChecked) {
					egoll |= 128;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 127;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegh:
				if(isChecked) {
					egoll |= 64;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 191;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegu:
				if(isChecked) {
					egoll |= 32;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 223;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegt:
				if(isChecked) {
					egoll |= 16;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 239;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegl:
				if(isChecked) {
					egoll |= 8;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 247;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegs:
				if(isChecked) {
					egoll |= 4;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 251;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkega:
				if(isChecked) {
					egoll |= 2;
					if(chkb[5].isChecked()) {chkb[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 253;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegn:
				if(isChecked) {
					egoll |= 1;
					for(int i=6; i<13; i++)
						if(chkb[i].isChecked()) chkb[i].setChecked(false);
				}
				else egoll &= 254;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			}
			edit.commit();
		}
	};
	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int selt;
			switch (v.getId()) {
			case R.id.lay01: selt = 0; break;
			case R.id.lay02: selt = 1; break;
			case R.id.lay03: selt = 2; break;
			case R.id.lay04: selt = 3; break;
			case R.id.lay05: selt = 4; break;
			case R.id.lay06: selt = 5; break;
			case R.id.lay07: selt = 6; break;
			case R.id.lay08: selt = 7; break;
			case R.id.lay09: selt = 8; break;
			case R.id.lay10: selt = 9; break;
			case R.id.lay11: selt = 10; break;
			case R.id.lay12: selt = 11; break;
			default: selt = -1; break;
			}
			final int sel = selt;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llay[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				new AlertDialog.Builder(DCTimer.this).setSingleChoiceItems(staid[sel], stSel[sel], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(stSel[sel] != which) {
							stSel[sel] = which;
							switch (sel) {
							case 0:	//计时方式
								if (!usess) {
									if (which == 0) 
										tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
									else tvTimer.setText("IMPORT");
								}
								edit.putInt("tiway", which);
								break;
							case 1:	//计时器更新方式
								edit.putInt("timerupd", which);
								break;
							case 2:	//计时精确度
								if(which == 0) {edit.putBoolean("prec", false); if(stSel[0]==0) tvTimer.setText("0.00");}
								else {edit.putBoolean("prec", true); if(stSel[0]==0) tvTimer.setText("0.000");}
								if(resl != 0) {
									setGridView(false);
									seMean.setText(getResources().getString(R.string.session_average) + Mi.sesMean());
								}
								break;
							case 3:	//分段计时
								if(which == 0) {
									isMulp=false; mulp = null; multemp = null;
									System.gc();
									times = (resl!=0) ? new String[resl*3] : null;
								} else if(!isMulp) {
									isMulp=true;
									multemp = new long[7];
									mulp = new int[6][rest.length];
									if(resl>0){
										cursor = dbh.query(spSel[5]);
										for(int i=0; i<resl; i++) {
											cursor.moveToPosition(i);
											for(int j=0; j<6; j++)
												mulp[j][i] = cursor.getInt(7+j);
										}
										//cursor.close();
									}
									times = (resl!=0) ? new String[(which+2)*(resl+1)]:null;
								}
								else {
									if(resl != 0) {
										times = new String[(which+2)*(resl+1)];
									} else times = null;
								}
								edit.putInt("multp", which);
								setGridView(false);
								setGvTitle();
								break;
							case 4:	//采样频率
								if(stm.creatAudioRecord((int)srate[which]));
								else {
									Toast.makeText(DCTimer.this, getResources().getString(R.string.sr_not_support), Toast.LENGTH_SHORT).show();
								}
								edit.putInt("srate", which);
								break;
							case 5:	//三阶求解
								edit.putInt("cxe", which);
								if(which == 0) {spinner[1].setEnabled(false); spinner[3].setEnabled(false);}
								else if(which == 1) {spinner[1].setEnabled(true); spinner[3].setEnabled(true);}
								else {spinner[1].setEnabled(false); spinner[3].setEnabled(true);}
								if(spSel[0]==1 && (spSel[6]==0 || spSel[6]==1 || spSel[6]==5 || spSel[6]==19)) {
									if(which==0)tvScr.setText(crntScr);
									else new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											switch(stSel[5]) {
											case 1: extsol="\n"+Cross.cross(crntScr, spSel[1], spSel[3]); break;
											case 2: extsol="\n"+Cross.xcross(crntScr, spSel[3]); break;
											case 3: extsol="\n"+EOline.eoLine(crntScr, spSel[3]); break;
											case 4: extsol="\n"+PetrusxRoux.roux(crntScr, spSel[3]); break;
											case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, spSel[3]); break;
											}
											handler.sendEmptyMessage(3);
											isNextScr = false;
											nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], false);
											isNextScr = true;
										}
									}.start();
								}
								break;
							case 6:	//二阶底面
								edit.putInt("cube2l", which);
								if(spSel[0]==0) {
									if(which==0)tvScr.setText(crntScr);
									else if(spSel[6] < 3) new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											extsol = "\n"+Cube2bl.cube2layer(crntScr, stSel[6]);
											handler.sendEmptyMessage(3);
											isNextScr=false;
											nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], false);
											isNextScr = true;
										}
									}.start();
								}
								break;
							case 7:	//五魔配色
								edit.putInt("minxc", which);
								break;
							case 8:	//计时器字体
								setTimerFont(which);
								edit.putInt("tfont", which);
								break;
							case 9:	//屏幕方向
								DCTimer.this.setRequestedOrientation(screenOri[which]);
								edit.putInt("screenori", which);
								break;
							case 10:	//触感反馈
								edit.putInt("vibra", which);
								break;
							case 11:	//触感时间
								edit.putInt("vibtime", which);
								break;
							}
							edit.commit();
							std[sel].setText(itemStr[sel][which]);
						}
						dialog.dismiss();
					}
				}).show();
			case MotionEvent.ACTION_CANCEL:
				llay[sel].setBackgroundColor(0);
				break;
			}
			return false;
		}
	};
	
	private View.OnTouchListener touchList2 = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) { //TODO
			int sel;
			switch (v.getId()) {
			case R.id.lay13: sel = 12; break;
			case R.id.lay14: sel = 13; break;
			case R.id.lay15: sel = 14; break;
			case R.id.lay16: sel = 15; break;
			case R.id.lay17: sel = 16; break;
			case R.id.lay18: sel = 17; break;
			case R.id.lay19: sel = 18; break;
			case R.id.lay20: sel = 19; break;
			case R.id.lay21: sel = 20; break;
			case R.id.lay22: sel = 21; break;
			default: sel = -1; break;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llay[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				switch (sel) {
				case 12:	//背景颜色
					dialog = new ColorPicker(context, cl[0], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							tabHost.setBackgroundColor(color); cl[0]=color; bgcolor=true;
							edit.putInt("cl0", color); edit.putBoolean("bgcolor", true);
							edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.select_color));
					dialog.show();
					break;
				case 13:	//文字颜色
					dialog = new ColorPicker(context, cl[1], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							tvl.setTextColor(color);
							for(int i=0; i<sttlen; i++)stt[i].setTextColor(color);
							for(int i=0; i<chkb.length; i++)chkb[i].setTextColor(color);
							tvScr.setTextColor(color);
							tvTimer.setTextColor(color);
							cl[1]=color;
							if(resl!=0){
								setGridView(false);
							}
							setGvTitle();
							edit.putInt("cl1", color);edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.select_color));
					dialog.show();
					break;
				case 14:	//最快单次颜色
					dialog = new ColorPicker(context, cl[2], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[2]=color;
							if(resl!=0){
								setGridView(false);
							}
							edit.putInt("cl2", color);edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.select_color));
					dialog.show();
					break;
				case 15:	//最慢单次颜色
					dialog = new ColorPicker(context, cl[3], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[3]=color;
							if(resl!=0){
								setGridView(false);
							}
							edit.putInt("cl3", color);edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.select_color));
					dialog.show();
					break;
				case 16:	//最快平均颜色
					dialog = new ColorPicker(context, cl[4], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[4]=color;
							if(resl!=0 && !isMulp) setGridView(false);
							edit.putInt("cl4", color);edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.select_color));
					dialog.show();
					break;
				case 17:	//分组命名
					LayoutInflater factory = LayoutInflater.from(DCTimer.this);
					final View view = factory.inflate(R.layout.ses_name, null);
					final Spinner sp = (Spinner)view.findViewById(R.id.spin_ses);
					String[] items=new String[15];
					for(int i=0; i<15; i++)items[i]=""+(i+1);
					adapter = new ArrayAdapter<String>(DCTimer.this, android.R.layout.simple_spinner_item, items);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					final EditText et= (EditText)view.findViewById(R.id.edit_ses);
					sp.setAdapter(adapter);
					sp.setSelection(0);
					final int temp = -1;
					sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
						int lastSel = temp;
						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if(lastSel>=0)
								sesname[lastSel]=et.getText().toString();
							et.setText(sesname[arg2]);
							lastSel=arg2;
						}
						public void onNothingSelected(AdapterView<?> arg0) {} 
					});
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.sesname)).setView(view)
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							sesname[sp.getSelectedItemPosition()]=et.getText().toString();
							boolean change = false;
							for(int i=0; i<15; i++)
								if(!sesname[i].equals(share.getString("sesname"+i, ""))){
									edit.putString("sesname"+i, sesname[i]);
									change = true;
								}
							if(change){
								edit.commit();
								String[] mItems=new String[15];
								for(int j=0; j<15; j++)
									mItems[j]=j+1+(sesname[j].equals("")?"　":": "+sesname[j]);
								adapter = new ArrayAdapter<String>(DCTimer.this, android.R.layout.simple_spinner_item, mItems);
								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								spinner[5].setAdapter(adapter);
								spinner[5].setSelection(spSel[5], true);
							}
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
					break;
				case 18:	//配色设置
					int[] colors={share.getInt("csn1", Color.YELLOW), share.getInt("csn2", Color.BLUE), share.getInt("csn3", Color.RED),
							share.getInt("csn4", Color.WHITE), share.getInt("csn5", 0xff009900), share.getInt("csn6", 0xffff8026)};
					ColorScheme dialog = new ColorScheme(context, 1, colors, new ColorScheme.OnSchemeChangedListener(){
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csn"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.scheme_cube));
					dialog.show();
					break;
				case 19:	//金字塔配色
					colors = new int[] {share.getInt("csp1", Color.RED), share.getInt("csp2", 0xff009900),
							share.getInt("csp3", Color.BLUE), share.getInt("csp4", Color.YELLOW)};
					dialog = new ColorScheme(context, 2, colors, new ColorScheme.OnSchemeChangedListener(){
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csp"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.scheme_pyrm));
					dialog.show();
					break;
				case 20:	//SQ配色
					colors = new int[] {share.getInt("csq1", Color.YELLOW), share.getInt("csq2", Color.BLUE), share.getInt("csq3", Color.RED),
							share.getInt("csq4", Color.WHITE), share.getInt("csq5", 0xff009900), share.getInt("csq6", 0xffff8026)};
					dialog = new ColorScheme(context, 3, colors, new ColorScheme.OnSchemeChangedListener(){
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csq"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getResources().getString(R.string.scheme_sq));
					dialog.show();
					break;
				case 21:	//背景图片
					Intent intent = new Intent();
					intent.setType("image/*");	//开启Pictures画面Type设定为image
					intent.setAction(Intent.ACTION_GET_CONTENT);	//使用Intent.ACTION_GET_CONTENT这个Action
					startActivityForResult(intent, 1);	//取得相片后返回本画面
					break;
				}
			case MotionEvent.ACTION_CANCEL:
				llay[sel].setBackgroundColor(0);
				break;
			}
			return false;
		}
	};
	private void setEgOll() {
		String ego = "PHUTLSAN";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<8; i++) {
			if((egoll & (1<<(7-i))) != 0) sb.append(ego.charAt(i));
		}
		egolls = sb.toString();
	}
	
	private void viewsVisibility(boolean v) {
		int vi = v ? 0 : 8;
		tabHost.getTabWidget().setVisibility(vi);
		spinner[0].setVisibility(vi);
		spinner[6].setVisibility(vi);
		buttonSst.setVisibility(vi);
		if(hidscr)tvScr.setVisibility(vi);
	}
	
	private void set2ndsel() {
		String[] s = new String[0];
		switch(spSel[0]) {
		case 0:s=getResources().getStringArray(R.array.scr222);break;
		case 1:s=getResources().getStringArray(R.array.scr333);break;
		case 2:s=getResources().getStringArray(R.array.scr444);break;
		case 3:s=getResources().getStringArray(R.array.scr555);break;
		case 4:
		case 5:s=getResources().getStringArray(R.array.scr666);break;
		case 6:s=getResources().getStringArray(R.array.scrMinx);break;
		case 7:s=getResources().getStringArray(R.array.scrPrym);break;
		case 8:s=getResources().getStringArray(R.array.scrSq1);break;
		case 9:s=getResources().getStringArray(R.array.scrClk);break;
		case 10:s=getResources().getStringArray(R.array.scr15p);break;
		case 11:s=getResources().getStringArray(R.array.scrMxN);break;
		case 12:s=getResources().getStringArray(R.array.scrCmt);break;
		case 13:s=getResources().getStringArray(R.array.scrGear);break;
		case 14:s=getResources().getStringArray(R.array.scrSmc);break;
		case 15:s=getResources().getStringArray(R.array.scrSkw);break;
		case 16:s=getResources().getStringArray(R.array.scrOth);break;
		case 17:s=getResources().getStringArray(R.array.scr3sst);break;
		case 18:s=getResources().getStringArray(R.array.scrBdg);break;
		case 19:s=getResources().getStringArray(R.array.scrMsst);break;
		case 20:s=getResources().getStringArray(R.array.scrRly);break;
		}
		if(spSel[6] >= s.length) {
			spSel[6] = 0;
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner[6].setAdapter(adapter);
		spinner[6].setSelection(spSel[6], true);
	}
	private void setInScr(String scrs) {
		String[] scr = scrs.split("\n");
		for(int i=0; i<scr.length; i++) {
			String cscr = scr[i].replaceFirst("^\\s*((\\(?\\d+\\))|(\\d+\\.))\\s*", "");
			if(!cscr.equals(""))inScr.add(cscr);
		}
	}
	private void outScr(final String path, final String fileName, final int num) {
		File fPath = new File(path);
		if(fPath.exists() || fPath.mkdir() || fPath.mkdirs()) {
			proDlg = ProgressDialog.show(DCTimer.this, getResources().getString(R.string.menu_outscr), "");
			new Thread() {
				public void run() {
					try {
						OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
						for(int i=0; i<num; i++) {
							String scr=(i+1)+". "+Mi.SetScr((spSel[0]<<5)|spSel[6], false)+"\r\n";
							handler.sendEmptyMessage(num*100+i);
							byte [] bytes = scr.toString().getBytes();
							out.write(bytes);
						}
						out.close();
						handler.sendEmptyMessage(7);
					} catch (IOException e) {
						handler.sendEmptyMessage(4);
					}
					proDlg.dismiss();
				}
			}.start();
		}
		else Toast.makeText(DCTimer.this, getResources().getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
	}
	private void outStat(String path, String fileName, String stat) {
		File fPath = new File(path);
		if(fPath.exists() || fPath.mkdir() || fPath.mkdirs()) {
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
				byte [] bytes = stat.toString().getBytes();
				out.write(bytes);
				out.close();
				Toast.makeText(DCTimer.this, getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(DCTimer.this, getResources().getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
			}
		}
		else Toast.makeText(DCTimer.this, getResources().getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		menu.add(Menu.NONE, 0, 0, getResources().getString(R.string.menu_inscr));
		menu.add(Menu.NONE, 1, 1, getResources().getString(R.string.menu_outscr));
		menu.add(Menu.NONE, 2, 2, getResources().getString(R.string.menu_share));
		menu.add(Menu.NONE, 3, 3, getResources().getString(R.string.menu_weibo));
		menu.add(Menu.NONE, 4, 4, getResources().getString(R.string.menu_about));
		menu.add(Menu.NONE, 5, 5, getResources().getString(R.string.menu_exit));
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case 0:
			LayoutInflater factory = LayoutInflater.from(DCTimer.this);
			final View view0 = factory.inflate(R.layout.inscr_layout, null);
			final Spinner sp = (Spinner) view0.findViewById(R.id.spnScrType);
			String[] items = getResources().getStringArray(R.array.inscrStr);
			ArrayAdapter<String> adap = new ArrayAdapter<String>(DCTimer.this, android.R.layout.simple_spinner_item, items);
			adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adap);
			final EditText et0 = (EditText) view0.findViewById(R.id.edit_inscr);
			sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					insType = arg2;
				}
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			new AlertDialog.Builder(DCTimer.this).setView(view0).setTitle(getResources().getString(R.string.menu_inscr))
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i){
					final String scrs=et0.getText().toString();
					inScr = new ArrayList<String>();
					inScrLen = 0;
					setInScr(scrs);
					if(inScr.size()>0) newScr(false);
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case 1:
			final LayoutInflater factory1 = LayoutInflater.from(DCTimer.this);
			final View view1 = factory1.inflate(R.layout.outscr_layout, null);
			final EditText et1 = (EditText) view1.findViewById(R.id.edit_scrnum);
			final EditText et2 = (EditText) view1.findViewById(R.id.edit_scrpath);
			final Button btn = (Button)view1.findViewById(R.id.btn_browse);
			et2.setText(outPath);
			final EditText et3 = (EditText) view1.findViewById(R.id.edit_scrfile);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selFilePath = et2.getText().toString();
					final View viewb = factory1.inflate(R.layout.file_selector, null);
					listView = (ListView)viewb.findViewById(R.id.list);
					File f = new File(selFilePath);
					selFilePath = f.exists()?selFilePath:Environment.getExternalStorageDirectory().getPath()+File.separator;
					getFileDir(selFilePath);
					listView.setOnItemClickListener(itemListener);
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.sel_path)).setView(viewb)
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j){
							et2.setText(selFilePath+"/");
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
				}
			});
			new AlertDialog.Builder(DCTimer.this).setView(view1).setTitle(getResources().getString(R.string.menu_outscr)+"("+getScrName()+")")
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i){
					int numt = Integer.parseInt(et1.getText().toString());
					if(numt>100)numt=100;
					else if(numt<1)numt=5;
					final int num = numt;
					final String path=et2.getText().toString();
					if(!path.equals(outPath)){
						outPath=path;
						edit.putString("scrpath", path);
						edit.commit();
					}
					final String fileName=et3.getText().toString();
					File file = new File(path+fileName);
					if(file.isDirectory())Toast.makeText(DCTimer.this, getResources().getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
					else if(file.exists()){
						new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.path_dupl))
						.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j){
								outScr(path, fileName, num);
							}
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
					} else {
						outScr(path, fileName, num);
					}
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case 2:
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");	//纯文本
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, getShareContext());
			startActivity(Intent.createChooser(intent, getTitle()));
			break;
		case 3:
			savePic(takeScreenShot(DCTimer.this), addstr);
			String token = share.getString("token", null);
			String expires_in = share.getString("expin", null);
			if(token==null || expires_in==null || (System.currentTimeMillis()-share.getLong("totime", 0))/1000>=Integer.parseInt(expires_in)) {
				isShare = true;
				isLogin = false;
				auth();
			} else {
				try {
					Utility.setAuthorization(new Oauth2AccessTokenHeader());
					AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
					accessToken.setExpiresIn(expires_in);
					Weibo.getInstance().setAccessToken(accessToken);
					File picFile = new File(addstr);
					if (!picFile.exists())addstr = null;
					share2weibo(getShareContext(), addstr);
					Intent i = new Intent(DCTimer.this, ShareActivity.class);
					DCTimer.this.startActivity(i);
				} catch(Exception e) {isShare=true; auth();}
			}
			break;
		case 4:
			LayoutInflater factory2 = LayoutInflater.from(DCTimer.this);
			final View view = factory2.inflate(R.layout.dlg_about, null);
			new AlertDialog.Builder(DCTimer.this).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_close), null).show();
			break;
		case 5:
			cursor.close();
			dbh.close();
			edit.putInt("sel", spSel[0]);
			edit.putInt("sel2", spSel[6]);
			edit.commit();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return true;
	}
	private void getFileDir(String path) {
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(path);
		File[] fs = f.listFiles();
		if(fs!=null && fs.length>0) Arrays.sort(fs);
		if(!path.equals("/")) {
			items.add("..");
			paths.add(f.getParent());
		}
		if(fs != null)
			for(int i=0; i<fs.length; i++) {
				File file = fs[i];
				if(file.isDirectory()) {
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(fileList);
	}
	
	private void setGridView(boolean ch) {
		if(!isMulp) {
			aryAdapter = new TimesAdapter (DCTimer.this, times, new int[] {
					cl[1],cl[2],cl[3],cl[4]}, Mi.smax, Mi.smin, intv);
			myGridView.setNumColumns(3);
		} else {
			aryAdapter = new TimesAdapter(DCTimer.this,	times, new int[]{cl[1],
					cl[2], cl[3], Mi.smax, Mi.smin}, intv, stSel[3]+2);
			myGridView.setNumColumns(stSel[3]+2);
		}
		if(ch) myGridView.setStackFromBottom(false);
		else if(resl>40) myGridView.setStackFromBottom(true);
		else myGridView.setStackFromBottom(false);
		myGridView.setAdapter(aryAdapter);
	}
	private void setGvTitle() {
		if(isMulp) {
			String[] title = new String[stSel[3]+2];
			title[0] = getResources().getString(R.string.time);
			for(int i=1; i<stSel[3]+2; i++) title[i] = "P-"+i;
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(stSel[3]+2);
			gvTitle.setAdapter(ta);
		}
		else {
			String[] title = {getResources().getString(R.string.time),
					(l1am ? "avg of " : "mean of ") + listnum[spSel[4]],
					(l2am ? "avg of " : "mean of ") + listnum[spSel[2]+1]};
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(3);
			gvTitle.setAdapter(ta);
		}
	}
	private String getShareContext() {
		String s1 = getResources().getString(R.string.share_c1).replace("$len", ""+resl).replace("$scrtype", getScrName())
				.replace("$best", Mi.distime(Mi.smin, false)).replace("$mean", Mi.distime(Mi.sesMean));
		String s2 = (resl>listnum[spSel[4]])?getResources().getString(R.string.share_c2).replace("$flen", ""+listnum[spSel[4]]).
				replace("$favg", Mi.distime(Mi.bavg[0])):"";
		String s3 = (resl>listnum[spSel[2]+1])?getResources().getString(R.string.share_c2).replace("$flen", ""+listnum[spSel[2]+1]).
				replace("$favg", Mi.distime(Mi.bavg[1])):"";
		String s4 = getResources().getString(R.string.share_c3);
		return s1 + s2 + s3 + s4;
	}
	private String getScrName() {
		String[] mItems = getResources().getStringArray(R.array.cubeStr);
		String[] s = new String[0];
		switch(spSel[0]) {
		case 0:s=getResources().getStringArray(R.array.scr222);break;
		case 1:s=getResources().getStringArray(R.array.scr333);break;
		case 2:s=getResources().getStringArray(R.array.scr444);break;
		case 3:s=getResources().getStringArray(R.array.scr555);break;
		case 4:
		case 5:s=getResources().getStringArray(R.array.scr666);break;
		case 6:s=getResources().getStringArray(R.array.scrMinx);break;
		case 7:s=getResources().getStringArray(R.array.scrPrym);break;
		case 8:s=getResources().getStringArray(R.array.scrSq1);break;
		case 9:s=getResources().getStringArray(R.array.scrClk);break;
		case 10:s=getResources().getStringArray(R.array.scr15p);break;
		case 11:s=getResources().getStringArray(R.array.scrMxN);break;
		case 12:s=getResources().getStringArray(R.array.scrCmt);break;
		case 13:s=getResources().getStringArray(R.array.scrGear);break;
		case 14:s=getResources().getStringArray(R.array.scrSmc);break;
		case 15:s=getResources().getStringArray(R.array.scrSkw);break;
		case 16:s=getResources().getStringArray(R.array.scrOth);break;
		case 17:s=getResources().getStringArray(R.array.scr3sst);break;
		case 18:s=getResources().getStringArray(R.array.scrBdg);break;
		case 19:s=getResources().getStringArray(R.array.scrMsst);break;
		case 20:s=getResources().getStringArray(R.array.scrRly);break;
		}
		return mItems[spSel[0]] + "-" + s[spSel[6]];
	}
	private void searchSesType() {
		int type=0, idx=-1;
		for(int i=0; i<15; i++) {
			int s = sestp[i];
			if(type==0 && s==-1) {
				idx = i;
				type = 1;
			}
			if(s == scrType) {
				idx = i;
				type = 2;
				break;
			}
		}
		if(type==2 || (sestp[spSel[5]] != -1 && type == 1)) {
			spinner[5].setSelection(idx, true);
			spSel[5] = (byte) idx;
			//getSession(idx);
			edit.putInt("group", idx);
			edit.commit();
		}
	}
	private void setScrType() {
		switch(spSel[0]) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			scrType = spSel[0]; break;
		case 11:
			if(spSel[6]<3) scrType=11;
			else if(spSel[6]<5) scrType=12;
			else if(spSel[6]<12) scrType=spSel[6]+8;
			else scrType = spSel[6]+37;
			break;
		case 12:
		case 13:
		case 14:
		case 15:
			scrType = spSel[0]+8; break;
		case 16:
			scrType = spSel[6]+24; break;
		case 17:
			scrType = 1; break;
		case 18:
			scrType = spSel[6]+30; break;
		case 19:
			scrType = 6; break;
		case 20:
			scrType = spSel[6]+32; break;
		}
	}
	private void setTimerFont(int f) {
		switch (f) {
		case 0: tvTimer.setTypeface(Typeface.create("monospace", 0)); break;
		case 1: tvTimer.setTypeface(Typeface.create("serif", 0)); break;
		case 2: tvTimer.setTypeface(Typeface.create("sans-serif", 0)); break;
		case 3: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf")); break;
		case 4: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf")); break;
		case 5: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf")); break;
		}
	}
	private void auth() {
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
		weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");	// 此处回调页内容应该替换为与appkey对应的应用回调页
		weibo.authorize(DCTimer.this, new AuthDialogListener());
	}
	private void share2weibo(String content, String picPath) throws WeiboException {
		Weibo weibo = Weibo.getInstance();
		weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo.getAccessToken()
				.getSecret(), content, picPath);
	}
	private void setTouch(MotionEvent e) {	//TODO
		if(!simss || scrt) {
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown();
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				break;
			}
		} else {
			int x1 = (int)e.getX(0)*2/tvTimer.getWidth(), x2 = (int)e.getX(1)*2/tvTimer.getWidth();
			switch (e.getAction()) {
			case MotionEvent.ACTION_POINTER_1_DOWN:
			case MotionEvent.ACTION_POINTER_2_DOWN:
				if(e.getPointerCount()>1 && (x1^x2)==1) {
					touchDown();
					touchDown = true;
				}
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
			case MotionEvent.ACTION_POINTER_2_UP:
			case MotionEvent.ACTION_UP:
				if(touchDown) {
					touchDown = false;
					touchUp();
				}
				break;
			}
		}
	}
	
	private void touchDown() {
		if(timer.state == 1) {
			if(mulpCount != 0) {
				if(stSel[10]==1 || stSel[10]==3)
					vibrator.vibrate(vibTime[stSel[11]]);
				tvTimer.setTextColor(Color.GREEN);
				multemp[stSel[3]+1-mulpCount] = System.currentTimeMillis();
			}
			else {
				if(stSel[10]>1)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) multemp[stSel[3]+1]=timer.time1;
				viewsVisibility(true);
			}
		} else if(timer.state != 3) {
			if(!scrt || timer.state==2) {
				if(frzTime == 0 || (wca && timer.state==0)) {
					tvTimer.setTextColor(Color.GREEN);
					canStart = true;
				} else {
					if(timer.state==0) tvTimer.setTextColor(Color.RED);
					else tvTimer.setTextColor(Color.YELLOW);
					timer.freeze();
				}
			}
		}
	}
	private void touchUp() {
		if(timer.state == 0) {
			if(isLongPress) isLongPress = false;
			else if(scrt) newScr(false);
			else {
				if(frzTime ==0 || canStart) {
					if(stSel[10]==1 || stSel[10]==3)
						vibrator.vibrate(vibTime[stSel[11]]);
					timer.count();
					if(isMulp) {
						mulpCount = stSel[3];
						multemp[0] = timer.time0;
					}
					else mulpCount = 0;
					acquireWakeLock(); screenOn=true;
					viewsVisibility(false);
				} else {
					timer.stopf();
					tvTimer.setTextColor(cl[1]);
				}
			}
		} else if(timer.state == 1) {	//TODO
			if(isLongPress) isLongPress = false;
			if(mulpCount!=0) {
				mulpCount--;
				tvTimer.setTextColor(cl[1]);
			}
		} else if(timer.state == 2) {
			if(isLongPress) isLongPress = false;
			if(frzTime ==0 || canStart) {
				isp2 = timer.insp==2 ? 2000 : 0;
				idnf = timer.insp != 3;
				if(stSel[10]==1 || stSel[10]==3)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) multemp[0] = timer.time0;
				acquireWakeLock(); screenOn=true;
				viewsVisibility(false);
			} else {
				timer.stopf();
				tvTimer.setTextColor(Color.RED);
			}
		} else {
			if(isLongPress) isLongPress = false;
			if(!wca) {isp2=0; idnf=true;}
			//newScr(false);
			//mTextView2.setText(Mi.distime((int)timer.time));
			confirmTime((int)timer.time);
			timer.state = 0;
			if(!opnl) {releaseWakeLock(); screenOn=false;}
		}
	}
	private void inputTime(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			tvTimer.setTextColor(Color.GREEN);
			break;
		case MotionEvent.ACTION_UP:
			tvTimer.setTextColor(cl[1]);
			LayoutInflater factory = LayoutInflater.from(DCTimer.this);
			final View view = factory.inflate(R.layout.editbox_layout, null);
			final EditText editText = (EditText) view.findViewById(R.id.editText1);
			editText.setFocusable(true);
			editText.requestFocus();
			new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.enter_time)).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String time = Mi.convStr(editText.getText().toString());
					if(time.equals("Error") || Mi.convTime(time)==0)
						Toast.makeText(DCTimer.this, getResources().getString(R.string.illegal), Toast.LENGTH_SHORT).show();
					else save(Mi.convTime(time), (byte) 0);
					//newScr(false);
				}
			}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
		}
	}
	private void save(int time, int p) {
		if(resl >= rest.length) {
			String[] scr2 = new String[scrst.length+12];
			byte[] rep2 = new byte[resp.length+12];
			int res2[] = new int[rest.length+12];
			for(int i=0; i<resl; i++) {
				scr2[i]=scrst[i]; rep2[i]=resp[i]; res2[i]=rest[i];
			}
			scrst=scr2; resp=rep2; rest=res2;
			if(isMulp) {
				int[][] mulp2 = new int[6][rest.length];
				for(int i=0;i<resl;i++) {
					for(int j=0; j<6; j++) {
						mulp2[j][i] = mulp[j][i];
					}
				}
				mulp = mulp2;
			}
			System.gc();
		}
		scrst[resl]=crntScr; resp[resl]=(byte) p; rest[resl++]=time;
		if(isMulp) {
			boolean temp = true;
			for(int i=0; i<stSel[3]+1; i++) {
				if(temp)
					mulp[i][resl-1] = (int)(multemp[i+1]-multemp[i]);
				else mulp[i][resl-1] = 0;
				if(mulp[i][resl-1]<0 || mulp[i][resl-1]>rest[resl-1]) {
					mulp[i][resl-1]=0; temp=false;
				}
			}
		}
		int d = 1;
		if(p==2) {
			p=0; d=0;
		}
		ContentValues cv = new ContentValues();
		cv.put("id", ++dbLastId);
		cv.put("rest", time);
		cv.put("resp", p);
		cv.put("resd", d);
		cv.put("scr", crntScr);
		cv.put("time", formatter.format(new Date()));
		if(isMulp)
			for(int i=0; i<6; i++)
				cv.put("p"+(i+1), mulp[i][resl-1]);
		dbh.insert(spSel[5], cv);
		if(isMulp) times = new String[(stSel[3]+2)*(resl+1)];
		else times = new String[resl*3];
		seMean.setText(getResources().getString(R.string.session_average) + Mi.sesMean());
		setGridView(false);
		if(selSes && sestp[spSel[5]] != scrType) {
			sestp[spSel[5]] = (short) scrType;
			edit.putInt("sestp"+spSel[5], scrType);
			edit.commit();
		}
		newScr(false);
	}
	private void update(int idx, byte p) {
		if(resp[idx] != p) {
			resp[idx] = p;
			byte d = 1;
			if(p==2) {
				p=0; d=0;
			}
			cursor = dbh.query(spSel[5]);
			cursor.moveToPosition(idx);
			int id=cursor.getInt(0);
			//cursor.close();
			dbh.update(spSel[5], id, p, d);
			seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
			setGridView(false);
		}
	}
	private void delete(int idx, int col) {
		int delId;
		if(idx != resl-1) {
			for(int i=idx; i<resl-1; i++) {
				rest[i]=rest[i+1]; resp[i]=resp[i+1]; scrst[i]=scrst[i+1];
				if(isMulp) {
					for(int j=0; j<stSel[3]+1; j++) {
						mulp[j][i] = mulp[j][i+1];
					}
				}
			}
			cursor.moveToPosition(idx);
			delId = cursor.getInt(0);
		} else {
			delId = dbLastId;
			if(resl > 1) {
				cursor.moveToPosition(resl-2);
				dbLastId = cursor.getInt(0);
			} else dbLastId = 0;
		}
		dbh.del(spSel[5], delId);
		//cursor.close();
		resl--;
		if(resl > 0) {
			if(isMulp) times = new String[(resl+1)*col];
			else times = new String[resl*col];
		}
		else {
			times = null;
			sestp[spSel[5]] = -1;
			edit.remove("sestp"+spSel[5]);
			edit.commit();
		}
		seMean.setText(getResources().getString(R.string.session_average) + Mi.sesMean());
		setGridView(false);
	}
	private void deleteAll() {
		dbh.clear(spSel[5]);
		resl = dbLastId = 0;
		times = null;
		seMean.setText(getResources().getString(R.string.session_average) + "0/0): N/A (N/A)");
		Mi.smax = Mi.smin = -1;
		setGridView(false);
		if(sestp[spSel[5]] != -1) {
			sestp[spSel[5]] = -1;
			edit.remove("sestp"+spSel[5]);
			edit.commit();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(timer.state == 1) {
				timer.count();
				viewsVisibility(true);
				if(!wca) {isp2=0; idnf=true;}
				//newScr(false);
				confirmTime((int)timer.time);
				timer.state = 0;
				if(!opnl) {releaseWakeLock(); screenOn=false;}
			} else if(timer.state == 2) {
				timer.stopi();
				tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
				viewsVisibility(true);
				if(!opnl) {releaseWakeLock(); screenOn=false;}
			} else if(event.getRepeatCount() == 0) {
				if((System.currentTimeMillis()-exitTime) > 2000) {
					Toast.makeText(DCTimer.this, getResources().getString(R.string.again_exit), Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					edit.putInt("sel", spSel[0]);
					edit.putInt("sel2", spSel[6]);
					edit.commit();
		            finish();
		        }
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_Q) chScr(8, 2);
		else if(keyCode == KeyEvent.KEYCODE_W) chScr(0, 0);
		else if(keyCode == KeyEvent.KEYCODE_E) chScr(1, 1);
		else if(keyCode == KeyEvent.KEYCODE_R) chScr(2, 0);
		else if(keyCode == KeyEvent.KEYCODE_T) chScr(3, 0);
		else if(keyCode == KeyEvent.KEYCODE_Y) chScr(4, 0);
		else if(keyCode == KeyEvent.KEYCODE_U) chScr(5, 0);
		else if(keyCode == KeyEvent.KEYCODE_M) chScr(6, 0);
		else if(keyCode == KeyEvent.KEYCODE_P) chScr(7, 0);
		else if(keyCode == KeyEvent.KEYCODE_K) chScr(9, 0);
		else if(keyCode == KeyEvent.KEYCODE_N) newScr(false);
		else if(keyCode == KeyEvent.KEYCODE_Z) {
			if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.confirm_del_last))
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					cursor = dbh.query(spSel[5]);
					delete(resl-1, isMulp ? stSel[3]+2 : 3);
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_A) {
			if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.confirm_clear_session))
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {deleteAll();}
			}).setNegativeButton(R.string.btn_cancel, null).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_D) {
			if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.show_time)+Mi.distime(resl-1, true)).setItems(R.array.rstcon,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: update(resl-1, (byte) 0); break;
					case 1: update(resl-1, (byte) 1); break;
					case 2: update(resl-1, (byte) 2); break;
					}
				}
			}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
		}
		return false;
	}
	private void chScr(int s1, int s2) {
		boolean c1=false, c2=false;
		if(spSel[0]!=s1){
			c1=true;
			spinner[0].setSelection(s1);
			spSel[0] = (byte) s1;
			if(spSel[0]!=selold)selold=spSel[0];
		}
		if(spSel[6]!=s2){
			c2=true;
			spSel[6]=(byte) s2;
		}
		if(c1||c2) {
			set2ndsel();
			setScrType();
			if(selSes)searchSesType();
			if(inScr!=null && inScr.size()!=0)inScr=null;
		}
	}
	private void showAlertDialog(int i, int j) {
		String t=null;
		switch(i) {
		case 1:
			t=(l1am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spSel[4]]);
			slist=l1am?ao(listnum[spSel[4]], j):mo(listnum[spSel[4]], j);
			break;
		case 2:
			t=(l2am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spSel[2]+1]);
			slist=l2am?ao(listnum[spSel[2]+1], j):mo(listnum[spSel[2]+1], j);
			break;
		case 3:
			t=getResources().getString(R.string.sta_session_mean);
			slist=sesMean();
			break;
		}
		new AlertDialog.Builder(DCTimer.this).setTitle(t).setMessage(slist)
		.setPositiveButton(getResources().getString(R.string.btn_copy), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i){
				ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(slist);
				Toast.makeText(DCTimer.this, getResources().getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
			}
		}).setNeutralButton(getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final LayoutInflater factory = LayoutInflater.from(DCTimer.this);
				final View view = factory.inflate(R.layout.save_stat, null);
				final EditText et1 = (EditText) view.findViewById(R.id.edit_scrpath);
				final Button btn = (Button)view.findViewById(R.id.btn_browse);
				et1.setText(outPath);
				final EditText et2 = (EditText) view.findViewById(R.id.edit_scrfile);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
				et2.setText(getResources().getString(R.string.def_sname).replace("$datetime", formatter.format(new Date())));
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						selFilePath = et1.getText().toString();
						final View viewb = factory.inflate(R.layout.file_selector, null);
						listView = (ListView)viewb.findViewById(R.id.list);
						File f = new File(selFilePath);
						selFilePath = f.exists()?selFilePath:Environment.getExternalStorageDirectory().getPath()+File.separator;
						getFileDir(selFilePath);
						listView.setOnItemClickListener(itemListener);
						new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.sel_path)).setView(viewb)
						.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j){
								et1.setText(selFilePath+"/");
							}
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
					}
				});
				new AlertDialog.Builder(DCTimer.this).setView(view).setTitle(getResources().getString(R.string.stat_save))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						final String path=et1.getText().toString();
						if(!path.equals(outPath)){
							outPath=path;
							edit.putString("scrpath", path);
							edit.commit();
						}
						final String fileName=et2.getText().toString();
						File file = new File(path+fileName);
						if(file.isDirectory())Toast.makeText(DCTimer.this, getResources().getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
						else if(file.exists()){
							new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.path_dupl))
							.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int j){
									outStat(path, fileName, slist);
								}
							}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
						} else {
							outStat(path, fileName, slist);
						}
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
			}
		}).setNegativeButton(getResources().getString(R.string.btn_close), null).show();
	}
	protected void newScr(final boolean ch) {
		if(!ch && inScr!=null && inScrLen<inScr.size()){
			if(!isInScr)isInScr=true;
			crntScr=inScr.get(inScrLen++);
			switch (insType) {
			case 0:
				if(crntScr.matches("([FRU][2']?\\s*)+"))Mi.viewType=2;
				else if(crntScr.matches("([ULRBulrb]'?\\s*)+"))Mi.viewType=17;
				else if(crntScr.matches("([xFRUBLDMfrubld][2']?\\s*)+"))Mi.viewType=3;
				else if(crntScr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+"))Mi.viewType=4;
				else if(crntScr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+"))Mi.viewType=5;
				else if(crntScr.matches("(((2?[FRUBLD])|(3[FRU]w))[2']?\\s*)+"))Mi.viewType=6;
				else if(crntScr.matches("(((2|3)?[FRUBLD])[2']?\\s*)+"))Mi.viewType=7;
				else Mi.viewType=0;
				break;
			case 1:
				if(crntScr.matches("([FRUBLD][2']?\\s*)+"))Mi.viewType=2;
				else Mi.viewType=0;
				break;
			case 2:
				if(crntScr.matches("([xFRUBLDMfrubld][2']?\\s*)+"))Mi.viewType=3;
				else Mi.viewType=0;
				break;
			case 3:
				if(crntScr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+"))Mi.viewType=4;
				else Mi.viewType=0;
				break;
			case 4:
				if(crntScr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+"))Mi.viewType=5;
				else Mi.viewType=0;
				break;
			case 5:
				if(crntScr.matches("([ULRBulrb]'?\\s*)+"))Mi.viewType=17;
				else Mi.viewType=0;
			}
			
			if(Mi.viewType==3 && stSel[5]!=0) {
				new Thread() {
					public void run() {
						handler.sendEmptyMessage(6);
						if(stSel[5]==1)extsol="\n"+Cross.cross(crntScr, DCTimer.spSel[1], DCTimer.spSel[3]);
						else if(stSel[5]==2)extsol="\n"+Cross.xcross(crntScr, DCTimer.spSel[3]);
						else if(stSel[5]==3)extsol="\n"+EOline.eoLine(crntScr, DCTimer.spSel[3]);
						else if(stSel[5]==4)extsol="\n"+PetrusxRoux.roux(crntScr, DCTimer.spSel[3]);
						else if(stSel[5]==5)extsol="\n"+PetrusxRoux.petrus(crntScr, DCTimer.spSel[3]);
						handler.sendEmptyMessage(3);
					}
				}.start();
			}
			else tvScr.setText(crntScr);
		}
		else if((spSel[0]==0 && spSel[6]<3 && stSel[6]!=0) ||
			(spSel[0]==1 && (spSel[6]!=0 || (stSel[5]!=0 && (spSel[6]==0 || spSel[6]==1 || spSel[6]==5 || spSel[6]==19)))) ||
			(spSel[0]==8 && (spSel[6]==2 || sqshp)) ||
			(spSel[0]==11 && (spSel[6]>3 && spSel[6]<7)) ||
			(spSel[0]==17 && (spSel[6]<3 || spSel[6]==6)) ||
			spSel[0]==20) {
			if(isInScr)isInScr=false;
			if(ch)canScr=true;
			if(canScr){
				new Thread() {
					public void run() {
						canScr=false;
						handler.sendEmptyMessage(2);
						if(!ch) {
							//TODO
							if(nextScr==null || isChScr) {
								crntScr = Mi.SetScr((spSel[0]<<5)|spSel[6], ch);
								isChScr=false;
								nextScr="";
							} else {
								while (!isNextScr) {
									try {
										sleep(50);
									} catch (InterruptedException e) {}
								}
								crntScr = nextScr;
							}
						}
						else {
							crntScr = Mi.SetScr((spSel[0]<<5)|spSel[6], ch);
						}
						extsol = Mi.sc;
						//crntScr=(!ch && isNextScr)?nextScr:Mi.SetScr((spSel[0]<<5)|spSel[6]);
						if((spSel[0]==0 && stSel[6]!=0) ||
								(stSel[5]!=0 && spSel[0]==1 && (spSel[6]==0 || spSel[6]==1 || spSel[6]==5 || spSel[6]==19)))
							handler.sendEmptyMessage(3);
						else if(spSel[0]==8 && sqshp)handler.sendEmptyMessage(1);
						else handler.sendEmptyMessage(0);
						canScr=true;
						isNextScr = false;
						nextScr = Mi.SetScr((spSel[0]<<5)|spSel[6], ch);
						isNextScr = true;
						System.out.println(nextScr);
					}
				}.start();
			}
		} else {
			crntScr=Mi.SetScr(spSel[0]<<5|spSel[6], ch);
			tvScr.setText(crntScr);
		}
	}
	public void confirmTime(final int time) {
		if(idnf) {
			if(conft)
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.show_time)+Mi.distime(time + isp2)).
						setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:save(time + isp2, 0);break;
						case 1:save(time + isp2, 1);break;
						case 2:save(time + isp2, 2);break;
						}
					}
				})
				.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						d.dismiss();
						newScr(false);
					}
				}).show();
			else save(time + isp2, 0);
		}
		else {
			if(conft)
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.time_dnf)).setMessage(getResources().getString(R.string.confirm_adddnf))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j){
						save((int)timer.time, 2);
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						d.dismiss();
						newScr(false);
					}
				}).show();
			else save((int)timer.time, 2);
		}
	}
	
	public String sesMean() {
		StringBuffer sb=new StringBuffer();
		int n=resl;
		for(int i=0;i<resl;i++) {
			if(resp[i]==2) n--;
		}
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_solve)+n+"/"+resl+"\n");
		sb.append(getResources().getString(R.string.ses_mean)+Mi.distime(Mi.sesMean)+" ");
		sb.append("(σ = "+Mi.standDev(Mi.sesSD)+")\n");
		sb.append(getResources().getString(R.string.ses_avg)+Mi.sesAvg()+"\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(Mi.smin, false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(Mi.smax, false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		cursor = dbh.query(spSel[5]);
		for(int i=0;i<resl;i++){
			if(!hidls)sb.append("\n"+(i+1)+". ");
			sb.append(Mi.distime(i, true));
			cursor.moveToPosition(i);
			String s = cursor.getString(6);
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(hidls && i<resl-1)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[i]);
		}
		return sb.toString();
	}
	public String ao(int n, int i) {
		int cavg=0, csdv=-1, ind=1;
		int trim = (int) Math.ceil(n/20.0);
		int max, min;
		ArrayList<Integer> dnfIdx=new ArrayList<Integer>();
		ArrayList<Integer> midx = new ArrayList<Integer>();
		for(int j=i-n+1;j<=i;j++)
			if(resp[j]==2)
				dnfIdx.add(j);
		int dnf = dnfIdx.size();
		int[] data=new int[n-dnf];
		int[] idx=new int[n-dnf];
		int len=0;
		for(int j=i-n+1;j<=i;j++)
			if(resp[j]!=2) {
				data[len]=rest[j]+resp[j]*2000;
				idx[len++]=j;
			}
		quickSort(data, idx, 0, n-dnf-1);
		if(n-dnf >= trim) {
			for(int j=0; j<trim; j++) midx.add(idx[j]);
		} else {
			for(int j=0; j<data.length; j++) midx.add(idx[j]);
			for(int j=0; j<trim-n+dnf; j++) midx.add(dnfIdx.get(j));
		}
		boolean m = dnf>trim;
		min = midx.get(0);
		if(m) {
			for(int j=dnfIdx.size()-trim; j<dnfIdx.size(); j++) midx.add(dnfIdx.get(j));
		} else {
			for(int j=n-trim; j<n-dnf; j++) midx.add(idx[j]);
			for(int j=0; j<dnfIdx.size(); j++) midx.add(dnfIdx.get(j));
			double sum=0, sum2=0;
			for(int j=trim;j<n-trim;j++) {
				if(stSel[2]==1)sum+=data[j];
				else sum+=(data[j]+5)/10;
				if(stSel[2]==1)sum2+=Math.pow(data[j], 2);
				else sum2+=Math.pow((data[j]+5)/10, 2);
			}
			cavg=(int) (sum/(n-trim*2)+0.5);
			csdv=(int) Math.sqrt(sum2/(n-trim*2)-sum*sum/Math.pow(n-trim*2, 2));
			if(stSel[2]==0)cavg*=10;
		}
		max = midx.get(midx.size()-1);
		StringBuffer sb=new StringBuffer();
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_avg)+(m?"DNF":Mi.distime(cavg))+" ");
		sb.append("(σ = "+Mi.standDev(csdv)+")\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(min,false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(max,false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		cursor = dbh.query(spSel[5]);
		for(int j=i-n+1;j<=i;j++) {
			cursor.moveToPosition(j);
			String s = cursor.getString(6);
			if(!hidls)sb.append("\n"+(ind++)+". ");
			if(midx.indexOf(j)>-1)sb.append("(");
			sb.append(Mi.distime(j, false));
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(midx.indexOf(j)>-1)sb.append(")");
			if(hidls && j<i)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[j]);
		}
		return sb.toString();
	}
	private void quickSort(int[] a, int[] i, int lo0, int hi0) {
		int lo = lo0, hi = hi0;
		if (lo >= hi) return;
		boolean transfer = true;
		while (lo != hi) {
			if (a[lo] > a[hi]) {
				int temp = a[lo], itemp=i[lo];
				a[lo] = a[hi]; i[lo]=i[hi];
				a[hi] = temp; i[hi]=itemp;
				transfer = !transfer;
			}
			if(transfer) hi--;
			else lo++;
		}
		lo--; hi++;
		quickSort(a, i, lo0, lo);
		quickSort(a, i, hi, hi0);
	}
	public String mo(int n, int i) {
		StringBuffer sb=new StringBuffer();
		int max,min,dnf=0;
		int cavg=0, csdv=-1,ind=1;
		double sum=0,sum2=0;
		max=min=i-n+1;
		boolean m=false;
		for(int j=i-n+1;j<=i;j++){
			if(resp[j]!=2 && !m){min=j;m=true;}
			if(resp[j]==2){max=j;dnf++;}
		}
		m = (dnf>0);
		if(!m) {
			for (int j=i-n+1;j<=i;j++) {
				if(rest[j]+resp[j]*2000>rest[max]+resp[max]*2000)max=j;
				if(rest[j]+resp[j]*2000<=rest[min]+resp[min]*2000)min=j;
				if(stSel[2]==1)sum+=(double)(rest[j]+resp[j]*2000);
				else sum+=(rest[j]+resp[j]*2000+5)/10;
				if(stSel[2]==1)sum2+=Math.pow(rest[j]+resp[j]*2000, 2);
				else sum2+=Math.pow((rest[j]+resp[j]*2000+5)/10, 2);
			}
			cavg=(int) (sum/n+0.5);
			csdv=(int) (Math.sqrt(sum2/n-sum*sum/n/n)+(stSel[2]==1?0:0.5));
		}
		if(stSel[2]==0)cavg*=10;
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_avg)+(m?"DNF":Mi.distime(cavg))+" ");
		sb.append("(σ = "+Mi.standDev(csdv)+")\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(min,false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(max,false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		cursor = dbh.query(spSel[5]);
		for(int j=i-n+1;j<=i;j++) {
			cursor.moveToPosition(j);
			if(!hidls)sb.append("\n"+(ind++)+". ");
			sb.append(Mi.distime(j, false));
			String s = cursor.getString(6);
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(hidls && j<i)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[j]);
		}
		return sb.toString();
	}
	private Bitmap getBgPic(Bitmap bitmap) {
		int width = dm.widthPixels;
		int height=dm.heightPixels;
		float scaleWidth=(float)bitmap.getWidth()/width;
		float scaleHeight=(float)bitmap.getHeight()/height;
		float scale = Math.min(scaleWidth, scaleHeight);
		Matrix matrix = new Matrix();
		matrix.postScale(1/scale, 1/scale);
		return Bitmap.createBitmap(bitmap, (int)((bitmap.getWidth()-width*scale)/2),
				(int)((bitmap.getHeight()-height*scale)/2), (int)(width*scale), (int)(height*scale), matrix, true);
	}
	private void setBgPic(Bitmap scaleBitmap, int opa){
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(0);
		Paint paint = new Paint();
		canvas.drawBitmap(scaleBitmap, 0, 0, paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255-255*opa/100);
		canvas.drawRect(0, 0, width, height, paint);
		//return newBitmap;
		Drawable drawable =new BitmapDrawable(newBitmap);
		tabHost.setBackgroundDrawable(drawable);
	}
	private void acquireWakeLock() {
		if(!opnd) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		else if (wakeLock ==null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}
	private void releaseWakeLock() {
		if(!opnd) getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (wakeLock != null&& wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
	private void getSession(int i) {
		cursor = dbh.query(i);
		resl = cursor.getCount();
		rest = new int[resl+12];
		resp = new byte[resl+12];
		scrst = new String[resl+12];
		if(isMulp)mulp = new int[6][resl+12];
		if(resl!=0) {
			cursor.moveToFirst();
			for(int k=0; k<resl; k++){
				rest[k]=cursor.getInt(1);
				resp[k]=(byte)cursor.getInt(2);
				if(cursor.getInt(3)==0)resp[k]=2;
				scrst[k]=cursor.getString(4);
				if(isMulp){
					for(int j=0; j<6; j++){
						mulp[j][k] = cursor.getInt(7+j);
					}
				}
				cursor.moveToNext();
			}
			cursor.moveToLast();
			dbLastId=cursor.getInt(0);
			times=(isMulp)?new String[(stSel[3]+2)*(resl+1)]:new String[resl*3];
		} else {
			times=null;
			dbLastId=0;
		}
		//cursor.close();
	}
	private void singTime(final int p, final int col) {
		cursor = dbh.query(spSel[5]);
		cursor.moveToPosition(p/col);
		final int id = cursor.getInt(0);
		String time=cursor.getString(5);
		String n=cursor.getString(6);
		if(n==null)n="";
		final String note = n;
		if(time!=null)time="\n("+time+")";
		else time = "";
		LayoutInflater factory = LayoutInflater.from(DCTimer.this);
		final View view = factory.inflate(R.layout.singtime, null);
		final EditText editText=(EditText)view.findViewById(R.id.etnote);
		final TextView tvTime=(TextView)view.findViewById(R.id.st_time);
		final TextView tvScr=(TextView)view.findViewById(R.id.st_scr);
		tvTime.setText(getResources().getString(R.string.show_time)+Mi.distime(p/col,true)+time);
		tvScr.setText(scrst[p/col]);
		if(resp[p/col]==2) {
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(resp[p/col]==1) {
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe2);
			rb.setChecked(true);
		} else {
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe1);
			rb.setChecked(true);
		}
		if(!note.equals("")) editText.setText(note);
		new AlertDialog.Builder(DCTimer.this).setView(view)
		.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup rg = (RadioGroup)view.findViewById(R.id.st_penalty);
				int rgid = rg.getCheckedRadioButtonId();
				switch(rgid){
				case R.id.st_pe1: update(p/col, (byte)0);break;
				case R.id.st_pe2: update(p/col, (byte)1);break;
				case R.id.st_pe3: update(p/col, (byte)2);break;
				}
				String text = editText.getText().toString();
				if(!text.equals(note)){
					dbh.update(spSel[5], id, text);
					//myGridView.setAdapter(aryAdapter);
				}
			}
		}).setNeutralButton(getResources().getString(R.string.copy_scr), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(scrst[p/col]);
				Toast.makeText(DCTimer.this, getResources().getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
			}
		}).setNegativeButton(getResources().getString(R.string.delete_time), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int which) {
				delete(p/col, col);
				d.dismiss();
			}
		}).show();
	}
	private Bitmap takeScreenShot(Activity activity) {
		//View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		//获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		//System.out.println(statusBarHeight);
		//获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		//去掉标题栏
		//Bitmap bm = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap bm = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bm;
	}
	private void savePic(Bitmap b, String strFileName) {
		try {
			FileOutputStream fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();
					Cursor c = getContentResolver().query(uri, null, null, null, null);
					c.moveToFirst();
					picPath = c.getString(1);
					ContentResolver cr = this.getContentResolver();
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					bitmap = getBgPic(bitmap);
					setBgPic(bitmap, share.getInt("opac", 35));
					bgcolor = false;
					edit.putString("picpath", picPath);
					edit.putBoolean("bgcolor", false); edit.commit();
					c.close();
				} catch (Exception e) {
				} catch (OutOfMemoryError e) {Toast.makeText(DCTimer.this, "Out of memory error: bitmap size exceeds VM budget", Toast.LENGTH_SHORT).show();}
			}
		}
	}
	class AuthDialogListener implements WeiboDialogListener {
		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			edit.putString("token", token);
			edit.putString("expin", expires_in);
			edit.putLong("totime", System.currentTimeMillis());
			edit.commit();
			AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			Toast.makeText(DCTimer.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
			isLogin = true;
			rsauth.setText(getResources().getString(R.string.logout));
			if(isShare) {
				File picFile = new File(addstr);
				if (!picFile.exists()) {
					//Toast.makeText(TestActivity.this, "图片" + addstr + "不存在！", Toast.LENGTH_SHORT).show();
					addstr = null;
				}
				try {
					share2weibo(getShareContext(), addstr);
					Intent i = new Intent(DCTimer.this, ShareActivity.class);
					DCTimer.this.startActivity(i);
				} catch (WeiboException e) {} finally {}
			}
		}
		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}
		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
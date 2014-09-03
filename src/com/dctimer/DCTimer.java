package com.dctimer;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dctimer.ui.*;
import com.dctimer.ui.CustomDialog.Builder;
import com.sina.weibo.sdk.auth.*;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.*;

import sina.weibo.*;
import solvers.*;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.*;
import android.hardware.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DCTimer extends Activity implements SensorEventListener {
	private Context context;
	private TabHost tabHost;
	private LayoutInflater layoutInflater;
	private Button btScr, bt2Scr, btScrv;	// 打乱状态
	public TextView tvTimer;	//计时器
	private static TextView tvScr;	// 显示打乱
	private ImageView iv;
	private GridView gvTimes = null, gvTitle = null;
	private Button seMean, sesOpt, sesName;	//分组平均, 分组选项, 分组名称
	private Button[] btnSol3 = new Button[2];	//三阶求解
	private Button reset;	//设置复位
	private SeekBar[] skb = new SeekBar[7];	//拖动条
	private TextView[] stt = new TextView[59];	//设置
	private int sttlen = stt.length;
	private TextView[] stSwitch = new TextView[13];
	private TextView[] std = new TextView[16];
	private TextView[] stdn = new TextView[2];
	private LinearLayout[] llay = new LinearLayout[28];
	private CheckBox[] chkb = new CheckBox[11];
	
	private Timer timer;
	private Stackmat stm;
	private ColorPicker cpDialog;
	private DBHelper dbh;
	private TimesAdapter adapter;
	private WeiboAuth mWeiboAuth;
	private Oauth2AccessToken mAccessToken;
	private SsoHandler mSsoHandler;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected static SharedPreferences share;
	public static SharedPreferences.Editor edit;
	private Cursor cursor;
	private Bitmap bitmap;
	private PowerManager.WakeLock wakeLock = null;
	public static DisplayMetrics dm;
	private ProgressDialog proDlg = null;
	private ProgressDialog dlProg = null;
	private ProgressDialog impDlg = null;
	private Vibrator vibrator;
	private SensorManager sensor;

	private boolean opnl, opnd, hidscr, conft, isMulp, canScr = true, simss, touchDown = false, isLogin, isShare,
			scrt = false, bgcolor, fulls, invs, usess, screenOn, selSes, isLongPress, isChScr, drop;
	static boolean isNextScr = false, nextScrUsed = false;
	protected boolean canStart;
	protected static boolean isInScr = false;
	public boolean wca;
	public static boolean hidls;
	static boolean idnf = true;
	public static byte[] resp;	// 惩罚
	private static char[] srate = {48000, 44100, 22050, 16000, 11025, 8000};
	private float lowZ = 9.8f;
	static float fontScale;
	private int dbLastId, ttsize, stsize, intv, insType, mulpCount, egoll, sensity, listLen, opac;
	private int verc = 21, dbCount, scrIdx, scr2idx, sesIdx;
	private int[] staid = {R.array.tiwStr, R.array.tupdStr, R.array.preStr, R.array.mulpStr,
			R.array.samprate, R.array.crsStr, R.array.c2lStr, R.array.mncStr,  
			R.array.fontStr, R.array.soriStr, R.array.vibraStr, R.array.vibTimeStr,
			R.array.sq1sStr, R.array.timeForm, R.array.avgStr, R.array.avgStr};
	private int[] screenOri = new int[] {2, 0, 8, 1, 4};
	private static int selold, scrType, inScrLen, bytesum;
	static int l1len, l2len;
	public static int dip300;
	protected int frzTime;
	protected static int[][] mulp = null;
	public int[] cl = new int[5];
	private int[] vibTime = new int[] {30, 50, 80, 150, 240};
	public static int resl;
	public static int[] rest, stSel = new int[16], solSel = new int[2];
	static int isp2 = 0, egtype;
	public static long time;
	private long exitTime = 0;
	private static long[] multemp = null;
	public static short[] sesType = new short[15];

	private String picPath, selFilePath, newver, newupd;
	private String defPath = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
	private String[] mItems, scrStr, sesItems, sol31, sol32;
	private String[][] itemStr = new String[16][];
	private static String nextScr = null, extsol, slist, outPath;
	private static String[] sesnames = new String[15];
	public static String crntScr;	// 当前打乱
	public static String[] scrst;	// 打乱列表
	static String egolls;
	//private static String addstr = "/data/data/com.dctimer/databases/main.png";
	public static final String APP_KEY = "3318942954";	// 替换为开发者的appkey，例如"1646212960";
	private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	private static ArrayList<String> inScr = null;
	private List<String> items = null, paths = null;
	private ListView listView;
	private TextView tvFile;
	private Scrambler scrThread;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int msw = msg.what;
			switch(msw) {	//TODO
			case 0: tvScr.setText(crntScr); break;
			case 1: tvScr.setText(crntScr + "\n\n" + getString(R.string.shape) + extsol);	break;
			case 2: tvScr.setText(getString(R.string.scrambling));	break;
			case 3: tvScr.setText(crntScr + extsol);	break;
			case 4: Toast.makeText(context, getString(R.string.outscr_failed), Toast.LENGTH_SHORT).show();	break;
			case 5: tvTimer.setText("IMPORT");	break;
			case 6: tvScr.setText(crntScr + "\n\n" + getString(R.string.solving));	break;
			case 7: Toast.makeText(context, getString(R.string.outscr_success), Toast.LENGTH_SHORT).show();	break;
			case 8: Toast.makeText(context, getString(R.string.conning), Toast.LENGTH_SHORT).show();	break;
			case 9: Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_LONG).show();	break;
			case 10: Toast.makeText(context, getString(R.string.lastest), Toast.LENGTH_LONG).show();	break;
			case 11:
				new CustomDialog.Builder(context).setTitle(getString(R.string.havenew)+newver)
				.setMessage(newupd)
				.setPositiveButton(R.string.btn_download, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						download("DCTimer"+newver+".apk");
					}
				}).setNegativeButton(R.string.btn_cancel, null).create().show();
			case 12: dlProg.setProgress(bytesum / 1024);	break;
			case 13: Toast.makeText(context, getString(R.string.file_error), Toast.LENGTH_LONG).show();
			case 14: seMean.setText(getString(R.string.session_average) + Mi.sesMean());
				setGridView();	break;
			case 15: impDlg.setMessage(""+dbCount); break;
			case 16: Toast.makeText(context, getString(R.string.import_failed), Toast.LENGTH_SHORT).show(); break;
			case 17: Toast.makeText(context, getString(R.string.import_success), Toast.LENGTH_SHORT).show(); break;
			case 18: 
				adapter.notifyDataSetChanged();
				gvTimes.setAdapter(adapter);
				gvTimes.setSelection(gvTimes.getCount()-1);
				break;
			case 19: tvScr.setText(getString(R.string.initing) + " (" + (13 + threephase.Util.prog / 2597) + "%) ..."); break;
			case 20: tvScr.setText(getString(R.string.initing) + " (0%) ..."); break;
			case 21: tvScr.setText(getString(R.string.initing) + " (19%) ..."); break;
			case 22: tvScr.setText(getString(R.string.initing) + " (26%) ..."); break;
			case 23: tvScr.setText(getString(R.string.initing) + " (" + (33 + threephase.Util.prog / 44098) + "%) ..."); break;
			case 24: tvScr.setText(getString(R.string.initing) + " (21%) ..."); break;
			case 25: tvScr.setText(getString(R.string.initing) + " (" + (23 + threephase.Util.prog / 150150) + "%) ..."); break;
			case 26: tvScr.setText(getString(R.string.initing) + " (" + (26 + threephase.Util.prog / 4200) + "%) ..."); break;
			case 27: tvScr.setText(getString(R.string.initing) + " (" + (95 + threephase.Util.prog / 252) + "%) ..."); break;
			case 28: tvScr.setText(getString(R.string.initing) + " (" + (33 + threephase.Util.prog / 28923) + "%) ..."); break;
			case 29: tvScr.setText(getString(R.string.initing) + " (" + threephase.Util.prog / 1198 + "%) ..."); break;
			case 30: tvScr.setText(getString(R.string.initing) + " (2%) ..."); break;
			default: proDlg.setProgress(msw%100);	break;//Message(msw%100 + "/" + msw/100);
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
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(!bgcolor) {
			bitmap = BitmapFactory.decodeFile(picPath);
			bitmap = getBgPic(bitmap);
			setBgPic(bitmap, opac);
		}
		new Thread() {
			public void run() {
				try {
					sleep(200);
					handler.sendEmptyMessage(18);
				} catch (Exception e) { }
			}
		}.start();
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
		readConf();
		edit = share.edit();
		fontScale = getResources().getDisplayMetrics().scaledDensity;
		long sestype = share.getLong("sestype", -1);
		if(sestype != -1) {
			for(int i=0; i<9; i++) {
				int temp = Mi.getSessionType(sestype, i);
				if(temp != 0x7f)
					edit.putInt("sestp" + i, temp);
			}
			edit.remove("sestype");
			edit.commit();
		}
		setEgOll();
		scrStr = getResources().getStringArray(R.array.cubeStr);
		sol31 = getResources().getStringArray(R.array.faceStr);
		sol32 = getResources().getStringArray(R.array.sideStr);

		if(fulls) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(opnl) {acquireWakeLock(); screenOn = true;}
		mItems = getResources().getStringArray(R.array.tabInd);
		tabHost = (TabHost) super.findViewById(R.id.tabhost);
		tabHost.setup();
		layoutInflater = LayoutInflater.from(this);
		int[] ids = {R.id.tab_timer, R.id.tab_list, R.id.tab_setting};
		int[] imgs = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
		for (int x=0; x<3; x++) {
			TabSpec myTab = tabHost.newTabSpec("tab" + x);
			//myTab.setIndicator(mItems[x], getResources().getDrawable(imgs[x]));
			myTab.setIndicator(getTabItemView(x, imgs));
			myTab.setContent(ids[x]);
			tabHost.addTab(myTab);
		}

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(bgcolor) tabHost.setBackgroundColor(cl[0]);
		else {
			try {
				bitmap = BitmapFactory.decodeFile(picPath);
				bitmap = getBgPic(bitmap);
				setBgPic(bitmap, opac);
			} catch (Exception e) {
				tabHost.setBackgroundColor(cl[0]);
				Toast.makeText(context, getString(R.string.not_exist), Toast.LENGTH_SHORT).show();
			}
		}
		tabHost.setCurrentTab(0);

		tvScr = (TextView) findViewById(R.id.myTextView1);
		tvTimer = (TextView) findViewById(R.id.myTextView2);
		btScr = (Button) findViewById(R.id.spScr);
		bt2Scr = (Button) findViewById(R.id.sp2ndScr);
		btScrv = (Button) findViewById(R.id.myButtonSst);
		ids = new int[] {R.id.std01, R.id.std02, R.id.std03, R.id.std04, R.id.std05, R.id.std06, R.id.std07, R.id.std08, 
				R.id.std09, R.id.std10, R.id.std11, R.id.std12, R.id.std13, R.id.std14, R.id.std15, R.id.std16};
		for(int i=0; i<std.length; i++) {
			itemStr[i] = getResources().getStringArray(staid[i]);
			std[i] = (TextView) findViewById(ids[i]);
			std[i].setText(itemStr[i][stSel[i]]);
		}
		stdn[0] = (TextView) findViewById(R.id.std17);
		stdn[0].setText(""+l1len);
		stdn[1] = (TextView) findViewById(R.id.std18);
		stdn[1].setText(""+l2len);
		
		btnSol3[0] = (Button) findViewById(R.id.solve1);
		btnSol3[1] = (Button) findViewById(R.id.solve2);
		btnSol3[0].setText(sol31[solSel[0]]);
		btnSol3[1].setText(sol32[solSel[1]]);
		
		sesItems = new String[15];
		for (int j = 0; j < 15; j++)
			sesItems[j] = (j + 1) + ". " + sesnames[j];
		sesName = (Button) findViewById(R.id.sesname);
		sesName.setText(getString(R.string.session) + (sesIdx+1) + (sesnames[sesIdx].equals("") ? "" : " - "+sesnames[sesIdx]));
		
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

		gvTimes = (GridView) findViewById(R.id.myGridView);
		gvTitle = (GridView) findViewById(R.id.gv_title);
		seMean = (Button) findViewById(R.id.mButtonoa);
		sesOpt = (Button) findViewById(R.id.mButtonOpt);
		//rsauth = (Button) findViewById(R.id.auth_sina);
		ids = new int[] {R.id.checkeg2, R.id.checkcll, R.id.checkegu, R.id.checkegh, R.id.checkeg1, R.id.checkegn,
				R.id.checkegs, R.id.checkega, R.id.checkegpi, R.id.checkegl, R.id.checkegt};
		for(int i=0; i<chkb.length; i++) chkb[i] = (CheckBox) findViewById(ids[i]);
		ids = new int[] {R.id.seekb1, R.id.seekb2, R.id.seekb3, R.id.seekb4, R.id.seekb5, R.id.seekb6, R.id.seekb7};
		for(int i=0; i<ids.length; i++) skb[i] = (SeekBar) findViewById(ids[i]);
		ids = new int[] {
				R.id.stt00, R.id.stt01, R.id.stt02, R.id.stt08, R.id.stt09, R.id.stt05, R.id.stt21, R.id.stt07,
				R.id.stt03, R.id.stt22, R.id.stt17, R.id.stt11, R.id.stt12, R.id.stt13, R.id.stt14, R.id.stt15,
				R.id.stt16, R.id.stt10, R.id.stt18, R.id.stt19, R.id.stt23, R.id.stt04, R.id.stt06, R.id.stt20,
				R.id.stt26, R.id.stt27, R.id.stt28, R.id.stt29, R.id.stt30, R.id.stt31, R.id.stt33, R.id.stt33,
				R.id.stt34, R.id.stt35, R.id.stt36, R.id.stt37, R.id.stt38, R.id.stt39, R.id.stt40, R.id.stt41,
				R.id.stt42, R.id.stt43, R.id.stt44, R.id.stt45, R.id.stt46, R.id.stt47, R.id.stt48, R.id.stt49,
				R.id.stt50, R.id.stt51, R.id.stt52, R.id.stt53, R.id.stt54, R.id.stt55, R.id.stt24, R.id.stt25,
				R.id.stt56, R.id.stt57, R.id.stt58, 
		};	//DOTO
		for(int i=0; i<sttlen; i++) stt[i] = (TextView) findViewById(ids[i]);
		
		ids = new int[] {R.id.stcheck1, R.id.stcheck14, R.id.stcheck3, R.id.stcheck4, R.id.stcheck5, R.id.stcheck6, R.id.stcheck7, 
				R.id.stcheck8, R.id.stcheck9, R.id.stcheck11, R.id.stcheck12, R.id.stcheck13, R.id.stcheck10};
		for(int i=0; i<ids.length; i++) stSwitch[i] = (TextView) findViewById(ids[i]);
		ids = new int[] {
				R.id.lay01, R.id.lay02, R.id.lay03, R.id.lay04, R.id.lay05, R.id.lay06,
				R.id.lay07, R.id.lay08, R.id.lay09, R.id.lay10, R.id.lay11, R.id.lay12,
				R.id.lay23, R.id.lay18, R.id.lay25, R.id.lay26, 
				R.id.lay16, R.id.lay17, R.id.lay22, R.id.lay19, R.id.lay20, R.id.lay21,
				R.id.lay13, R.id.lay24, R.id.lay14, R.id.lay15, R.id.lay27, R.id.lay28,
		};//TODO
		for(int i=0; i<llay.length; i++) llay[i] = (LinearLayout) findViewById(ids[i]);
		reset = (Button) findViewById(R.id.reset);
		for(int i=0; i<16; i++) llay[i].setOnTouchListener(comboListener);
		for(int i=16; i<28; i++) llay[i].setOnTouchListener(touchListener);
		ids = new int[] {95, 25, 41, 100, 20, 100, 50};
		for(int i=0; i<ids.length; i++) skb[i].setMax(ids[i]);
		int ssvalue = share.getInt("ssvalue", 50);
		ids = new int[] {ttsize - 50, stsize - 12, intv - 20,
				opac, frzTime, ssvalue, sensity};
		for(int i=0; i<ids.length; i++) skb[i].setProgress(ids[i]);
		stt[3].setText(getString(R.string.timer_size) + ttsize);
		stt[4].setText(getString(R.string.scrsize) + stsize);
		stt[10].setText(getString(R.string.row_spacing) + intv);
		stt[29].setText(getString(R.string.time_tap) + frzTime/20D);
		stt[37].setText(getString(R.string.stt_ssvalue) + ssvalue);
		stt[53].setText(getString(R.string.sensitivity) + (sensity<15 ? getString(R.string.sen_low) :
			(sensity<30 ? getString(R.string.sen_mid) :
				(sensity<45 ? getString(R.string.sen_high) : getString(R.string.sen_ultra)))));
		Stackmat.switchThreshold = ssvalue;
		for(int i=0; i<7; i++) skb[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		stSwitch[0].setBackgroundResource(wca ? R.drawable.switch_on : R.drawable.switch_off);
		//stSwitch[1].setBackgroundResource(clkform ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[2].setBackgroundResource(simss ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[3].setBackgroundResource(usess ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[4].setBackgroundResource(invs ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[5].setBackgroundResource(hidscr ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[6].setBackgroundResource(conft ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[7].setBackgroundResource(hidls ? R.drawable.switch_off : R.drawable.switch_on);
		stSwitch[8].setBackgroundResource(selSes ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[9].setBackgroundResource(fulls ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[10].setBackgroundResource(opnl ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[11].setBackgroundResource(opnd ? R.drawable.switch_on : R.drawable.switch_off);
		stSwitch[12].setBackgroundResource(drop ? R.drawable.switch_on : R.drawable.switch_off);
		for(int i=0; i<13; i++) stSwitch[i].setOnClickListener(new OnClickListener());

		getSession(sesIdx);
		seMean.setText(getString(R.string.session_average) + Mi.sesMean());
		setGvTitle();
		if(isMulp) multemp = new long[7];
		setGridView();

		if(usess && !stm.isStart) stm.start();
		if((egtype & 4) != 0) chkb[1].setChecked(true);
		if((egtype & 2) != 0) chkb[4].setChecked(true);
		if((egtype & 1) != 0) chkb[0].setChecked(true);
		if((egoll & 128) != 0) chkb[8].setChecked(true);
		if((egoll & 64) != 0) chkb[3].setChecked(true);
		if((egoll & 32) != 0) chkb[2].setChecked(true);
		if((egoll & 16) != 0) chkb[10].setChecked(true);
		if((egoll & 8) != 0) chkb[9].setChecked(true);
		if((egoll & 4) != 0) chkb[6].setChecked(true);
		if((egoll & 2) != 0) chkb[7].setChecked(true);
		if((egoll & 1) != 0) chkb[5].setChecked(true);
		for(int i=0; i<chkb.length; i++)
			chkb[i].setOnCheckedChangeListener(listener);

		setTextsColor();

		vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		
		sensor = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		proDlg = new ProgressDialog(this);
		proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDlg.setTitle(getString(R.string.menu_outscr));
		proDlg.setCancelable(false);
		dlProg = new ProgressDialog(this);
		dlProg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dlProg.setTitle(getString(R.string.downloading));
		dlProg.setCancelable(false);
		impDlg = new ProgressDialog(this);
		impDlg.setTitle(getString(R.string.importing));
		impDlg.setCancelable(false);
		
		mWeiboAuth = new WeiboAuth(this, APP_KEY, REDIRECT_URL, SCOPE);
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if(!mAccessToken.isSessionValid()) stSwitch[1].setBackgroundResource(R.drawable.switch_off);//wbAuth.setText(R.string.login);
		else {
			isLogin = true;
			stSwitch[1].setBackgroundResource(R.drawable.switch_on);
			//wbAuth.setText(R.string.logout);
		}
		dip300 = (int) (getResources().getDisplayMetrics().density * 300 + 0.5);
		
		//打乱类型
		btScr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CustomDialog dialog =
				new CustomDialog.Builder(context).setSingleChoiceItems(R.array.cubeStr, scrIdx+1, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dia, int which) {
						if(scrIdx != which - 1) {	//TODO
							scrIdx = which - 1;
							if(scrIdx != selold) {scr2idx = 0; selold = scrIdx;}
							set2ndsel();
							setScrType();
							if(selSes) searchSesType();
							if(inScr != null && inScr.size() != 0) inScr = null;
						}
					}
				}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			}
		});
		bt2Scr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CustomDialog dialog =
				new CustomDialog.Builder(context).setSingleChoiceItems(get2ndScr(scrIdx), scr2idx, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (scr2idx != which) {
							scr2idx = which;
							set2ndsel();
							setScrType();
							if (selSes) searchSesType();
							if (inScr != null && inScr.size() != 0) inScr = null;
						}
					}
				}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			}
		});

		//分组
		sesName.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				CustomDialog dialog =
				new CustomDialog.Builder(context).setSingleChoiceItems(sesItems, sesIdx, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(sesIdx != which) {
							sesIdx = (byte) which;
							getSession(which);
							seMean.setText(getString(R.string.session_average) + Mi.sesMean());
							setGridView();
							edit.putInt("group", sesIdx);
							edit.commit();
							sesName.setText(getString(R.string.session) + (sesIdx+1) + (sesnames[sesIdx].equals("") ? "" : " - "+sesnames[sesIdx]));
						}
					}
				}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			}
		});

		//十字底面
		btnSol3[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CustomDialog dialog =
					new CustomDialog.Builder(context).setSingleChoiceItems(R.array.faceStr, solSel[0], new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dia, int which) {
							if(solSel[0] != which) {
								solSel[0] = which;
								btnSol3[0].setText(sol31[solSel[0]]);
								edit.putInt("cface", solSel[0]);
								edit.commit();
								if(scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))
									new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											extsol = "\n"+Cross.cross(crntScr, solSel[0], solSel[1]);
											handler.sendEmptyMessage(3);
											isNextScr = false;
											nextScr = Mi.setScr((scrIdx<<5)|scr2idx, false);
											isNextScr = true;
										}
									}.start();
							}
						}
					}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			}
		});
		//颜色
		btnSol3[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CustomDialog dialog =
					new CustomDialog.Builder(context).setSingleChoiceItems(R.array.sideStr, solSel[1], new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dia, int which) {
							if(solSel[1] != which) {
								solSel[1] = which;
								btnSol3[1].setText(sol32[solSel[1]]);
								edit.putInt("cside", solSel[1]);
								edit.commit();
								if(scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))
									new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											switch(stSel[5]) {
											case 1: extsol="\n"+Cross.cross(crntScr, solSel[0], solSel[1]); break;
											case 2: extsol="\n"+Cross.xcross(crntScr, solSel[1]); break;
											case 3: extsol="\n"+EOline.eoLine(crntScr, solSel[1]); break;
											case 4: extsol="\n"+PetrusxRoux.roux(crntScr, solSel[1]); break;
											case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, solSel[1]); break;
											}
											handler.sendEmptyMessage(3);
											isNextScr=false;
											nextScr = Mi.setScr((scrIdx<<5)|scr2idx, false);
											isNextScr = true;
										}
									}.start();
							}
						}
					}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			}
		});

		//打乱状态
		btScrv.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(Mi.viewType > 0) {
					LayoutInflater inflater = LayoutInflater.from(context);	// 取得LayoutInflater对象
					final View popView = inflater.inflate(R.layout.popwindow, null);	// 读取布局管理器
					iv = (ImageView) popView.findViewById(R.id.ImageView1);
					Bitmap bm = Bitmap.createBitmap(dip300, dip300*3/4, Config.ARGB_8888);
					Canvas c = new Canvas(bm);
					c.drawColor(0);
					Paint p = new Paint();
					p.setAntiAlias(true);
					Mi.drawScr(scr2idx, dip300, p, c);
					iv.setImageBitmap(bm);
					CustomDialog.Builder builder = new Builder(context);
					builder.setContentView(popView).setNegativeButton(R.string.btn_close, null);
					builder.create().show();
				} else Toast.makeText(context, getString(R.string.not_support), Toast.LENGTH_SHORT).show();
			}
		});

		//打乱
		tvScr.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				scrt = true;
				setTouch(event);
				return timer.state != 0;
			}
		});
		tvScr.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if(timer.state == 0) {
					isLongPress = true;
					LayoutInflater factory = LayoutInflater.from(context);
					final View view = factory.inflate(R.layout.scr_layout, null);
					final EditText editText = (EditText) view.findViewById(R.id.etslen);
					final TextView tvScr = (TextView) view.findViewById(R.id.cnt_scr);
					tvScr.setMaxWidth((int) (dm.widthPixels * 0.95));
					tvScr.setText(crntScr);
					editText.setText(""+Mi.scrLen);
					if(Mi.scrLen==0) editText.setEnabled(false);
					else editText.setSelection(editText.getText().length());
					new CustomDialog.Builder(context).setContentView(view)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String et = editText.getText().toString();
							int len = et.equals("")?0:Integer.parseInt(editText.getText().toString());
							if(editText.isEnabled() && len>0) {
								if(len>180) len=180;
								if(len != Mi.scrLen) {
									Mi.scrLen = len;
									if((scrIdx==-1 && scr2idx==17) || (scrIdx==1 && scr2idx==19) || (scrIdx==20 && scr2idx==4)) isChScr = true;
									newScr(false);
								}
							}
							hideKeyboard(editText);
						}
					}).setNegativeButton(R.string.copy_scr, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
							clip.setText(crntScr);
							Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
							hideKeyboard(editText);
						}
					}).create().show();
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

		gvTimes.setOnItemClickListener(new GridView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int p, long arg3) {
				if(isMulp) {
					if(p/(stSel[3]+2)<resl && p%(stSel[3]+2)==0) singTime(p, stSel[3]+2);
				}
				else if(p%3 == 0)
					singTime(p, 3);
				else if(p%3==1 && p/3>l1len-2)
					showAlertDialog(1, p/3);
				else if(p%3==2 && p/3>l2len-2)
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

		//分组选项
		sesOpt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				CustomDialog cdialog =
				new CustomDialog.Builder(context).
				setItems(R.array.optStr, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CustomDialog.Builder builder;
						switch (which) {
						case 0:	//分组命名
							LayoutInflater factory = LayoutInflater.from(context);
							final View view = factory.inflate(R.layout.ses_name, null);
							final EditText et= (EditText) view.findViewById(R.id.edit_ses);
							et.setText(sesnames[sesIdx]);
							et.setSelection(sesnames[sesIdx].length());
							builder = new Builder(context);
							builder.setTitle(R.string.sesname).setContentView(view)
							.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									sesnames[sesIdx]=et.getText().toString();
									edit.putString("sesname" + sesIdx, sesnames[sesIdx]);
									edit.commit();
									sesItems[sesIdx] = (sesIdx + 1) + ". " + sesnames[sesIdx];
									sesName.setText(getString(R.string.session)+(sesIdx + 1) + " - " + sesnames[sesIdx]);
									hideKeyboard(et);
								}
							}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									hideKeyboard(et);
								}
							}).create().show();
							showKeyboard(et);
							break;
						case 1:	//清空成绩
							if(resl == 0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
							else {
								builder = new Builder(context);
								builder.setTitle(R.string.confirm_clear_session)
								.setNegativeButton(R.string.btn_cancel, null)
								.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										deleteAll();
									}
								}).create().show();
							}
							break;
						case 2:	//时间分布直方图
							int width = dip300;
							LayoutInflater inflater = LayoutInflater.from(context);
							final View popView = inflater.inflate(R.layout.popwindow, null);
							iv = (ImageView) popView.findViewById(R.id.ImageView1);
							Bitmap bm = Bitmap.createBitmap(width, (int)(width*1.2), Config.ARGB_8888);
							Canvas c = new Canvas(bm);
							c.drawColor(0);
							Paint p = new Paint();
							p.setAntiAlias(true);
							Mi.drawHist(width, p, c);
							iv.setImageBitmap(bm);
							builder = new Builder(context);
							builder.setContentView(popView)
							.setNegativeButton(R.string.btn_close, null).create().show();
							break;
						case 3:	//折线图
							int wid = dip300;
							inflater = LayoutInflater.from(context);
							final View pView = inflater.inflate(R.layout.popwindow, null);
							iv = (ImageView) pView.findViewById(R.id.ImageView1);
							bm = Bitmap.createBitmap(wid, (int)(wid*0.8), Config.ARGB_8888);
							c = new Canvas(bm);
							//c.drawColor(0);
							p = new Paint();
							p.setAntiAlias(true);
							Mi.drawGraph(wid, p, c);
							iv.setImageBitmap(bm);
							builder = new Builder(context);
							builder.setContentView(pView)
							.setNegativeButton(R.string.btn_close, null).create().show();
							break;
						case 4:	//导出数据库
							try {
								File f = new File(defPath);
								if(!f.exists()) f.mkdirs();
								BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(defPath+"Database.dat"), "UTF-8"));
								for(int i=0; i<15; i++) {
									Cursor cur = dbh.query(i);
									int count = cur.getCount();
									if(count == 0) continue;
									writer.write(i+1+"\r\n");
									cur.moveToFirst();
									for(int j=0; j<count; j++) {
										writer.write(cur.getInt(1)+"\t"+cur.getInt(2)+"\t"+cur.getInt(3)+"\t");
										writer.write(cur.getString(4).replace("\n", "\\n")+"\t"+cur.getString(5)+"\t");
										if(cur.getString(6) != null) writer.write(cur.getString(6).replace("\t", "\\t")+"\t");
										else writer.write(cur.getString(6)+"\t");
										writer.write(cur.getInt(7)+"\t"+cur.getInt(8)+"\t"+cur.getInt(9)+"\t"
												+cur.getInt(10)+"\t"+cur.getInt(11)+"\t"+cur.getInt(12)+"\r\n");
										cur.moveToNext();
									}
									cur.close();
								}
								writer.close();
								Toast.makeText(context, getString(R.string.saved)+"sdcard/DCTimer/Database.dat", Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context, getString(R.string.save_failed), Toast.LENGTH_LONG).show();
							}
							break;
						case 5:	//导入数据库
							impDlg.show();
							dbCount = 0;
							new Thread() {
								public void run() {
									try {
										int table = 1;
										BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(defPath+"Database.dat"), "UTF-8"));
										String line = "";
										ContentValues cv = new ContentValues();
										int count = 1;
										while (((line = reader.readLine()) != null)) {
											if(!line.contains("\t")) {
												table = Integer.parseInt(line);
												count = 1;
											} else {
												String[] ts = line.split("\t");
												cv.put("id", count++);
												cv.put("rest", Integer.parseInt(ts[0]));
												cv.put("resp", Integer.parseInt(ts[1]));
												cv.put("resd", Integer.parseInt(ts[2]));
												cv.put("scr", ts[3].replace("\\n", "\n"));
												if(ts[4].equals("null")) cv.put("time", "");
												else cv.put("time", ts[4]);
												if(ts[5].equals("null")) cv.put("note", "");
												else cv.put("note", ts[5].replace("\\t", "\t"));
												for(int i=0; i<6; i++)
													cv.put("p"+(i+1), Integer.parseInt(ts[i+6]));
												dbh.insert(table-1, cv);
												dbCount++;
												handler.sendEmptyMessage(15);
											}
										}
										reader.close();
										handler.sendEmptyMessage(17);
									} catch (Exception e) {
										e.printStackTrace();
										handler.sendEmptyMessage(16);
									}
									getSession(sesIdx);
									handler.sendEmptyMessage(14);
									impDlg.dismiss();
								}
							}.start();
						}
					}
				})
				.setNegativeButton(getString(R.string.btn_cancel), null).create();
				showDialog(cdialog);
			}
		});

		//恢复默认设置
		reset.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new CustomDialog.Builder(context)
				.setTitle(R.string.confirm_reset)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j) {
						//TODO
						wca=false; simss=false; usess=false; invs=Stackmat.inv=false;
						hidscr=true; conft=true; hidls=false; selSes=false; fulls=false;
						bgcolor=true; opnl=false; opnd=true; isMulp=false;
						solSel[0]=0; solSel[1]=1;
						stSel[0]=0; stSel[1]=0; stSel[2]=1; stSel[3]=0; stSel[4]=1;
						stSel[5]=0; stSel[6]=0; stSel[7]=1; stSel[8]=3; stSel[9]=0;
						stSel[10]=0; stSel[11]=2; stSel[12]=0;
						tvTimer.setTextSize(60); tvScr.setTextSize(18);
						cl[0] = 0xff66ccff;	cl[1] = 0xff000000;	cl[2] = 0xffff00ff;
						cl[3] = 0xffff0000;	cl[4] = 0xff009900;
						int i;
						for(i=2;i<4;i++) chkb[i].setChecked(true);
						for(i=0; i<std.length; i++) std[i].setText(itemStr[i][stSel[i]]);
						btnSol3[0].setText(sol31[solSel[0]]);
						btnSol3[1].setText(sol32[solSel[1]]);
						int is[] = {1, 5, 6, 7, 11};
						for(i=0; i<is.length; i++) stSwitch[is[i]].setBackgroundResource(R.drawable.switch_on);
						is = new int[] {0, 2, 3, 4, 8, 9, 10, 12};
						for(i=0; i<is.length; i++) stSwitch[is[i]].setBackgroundResource(R.drawable.switch_off);
						is = new int[] {10, 6, 10, 35, 0, 50, 10};
						for(i=0; i<7; i++) skb[i].setProgress(is[i]);
						intv = 25; frzTime = 0;
						tabHost.setBackgroundColor(cl[0]);
						setTextsColor();
						updateGrid();
						releaseWakeLock();
						screenOn=false;
						edit.remove("cl0");	edit.remove("cl1");	edit.remove("cl2");
						edit.remove("cl3");	edit.remove("cl4");	edit.remove("wca");
						edit.remove("cxe");
						edit.remove("l1am");	edit.remove("l2am");	edit.remove("mnxc");
						edit.remove("prec");	edit.remove("mulp");	edit.remove("invs");
						edit.remove("tapt");	edit.remove("intv");	edit.remove("opac");
						edit.remove("mclr");	edit.remove("prom");	edit.remove("sq1s");
						edit.remove("l1tp");	edit.remove("l2tp");
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
						edit.remove("timerupd");	edit.remove("timeform");
						edit.remove("screenori");
						edit.commit();
					}
				}).setNegativeButton(R.string.btn_cancel, null).create().show();
			}
		});
	}
	
	private void setTextsColor() {
		// TODO
		for(int i=0; i<sttlen; i++) stt[i].setTextColor(cl[1]);
		for(int i=0; i<chkb.length; i++) chkb[i].setTextColor(cl[1]);
		tvScr.setTextColor(cl[1]);
		tvTimer.setTextColor(cl[1]);
		btScr.setTextColor(cl[1]);
		bt2Scr.setTextColor(cl[1]);
		btScrv.setTextColor(cl[1]);
		sesName.setTextColor(cl[1]);
		seMean.setTextColor(cl[1]);
		sesOpt.setTextColor(cl[1]);
		for(int i=0; i<btnSol3.length; i++) 
			btnSol3[i].setTextColor(cl[1]);
		for(int i=0; i<std.length; i++)
			std[i].setTextColor(0x80000000 | (cl[1] & 0xffffff));
		for(int i=0; i<stdn.length; i++)
			stdn[i].setTextColor(0x80000000 | (cl[1] & 0xffffff));
	}

	private View getTabItemView(int index, int[] imgs) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
		TextView textView = (TextView) view.findViewById(R.id.tab_name);		
		textView.setText(mItems[index]);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_img);
		imageView.setImageResource(imgs[index]);
		return view;
	}
	
	private void readConf() {	//TODO
		selold = scrIdx = (byte) share.getInt("sel", 1);	//打乱种类
		cl[0] = share.getInt("cl0", 0xff66ccff);	// 背景颜色
		cl[1] = share.getInt("cl1", Color.BLACK);	// 文字颜色
		cl[2] = share.getInt("cl2", 0xffff00ff);	//最快单次颜色
		cl[3] = share.getInt("cl3", Color.RED);	//最慢单次颜色
		cl[4] = share.getInt("cl4", 0xff009900);	//最快平均颜色
		wca = share.getBoolean("wca", false);	//WCA观察
		hidscr = share.getBoolean("hidscr", true);	//隐藏打乱
		hidls = share.getBoolean("hidls", false);	//成绩列表隐藏打乱
		conft = share.getBoolean("conft", true);	//提示确认成绩
		solSel[0] = (byte) share.getInt("cface", 0);	// 十字求解底面
		solSel[1] = (byte) share.getInt("cside", 1);	// 三阶求解颜色
		sesIdx = (byte) share.getInt("group", 0);	// 分组
		scr2idx = (byte) share.getInt("sel2", 0);	// 二级打乱
		ttsize = share.getInt("ttsize", 60);	//计时器字体
		stsize = share.getInt("stsize", 18);	//打乱字体
		stSel[0] = share.getInt("tiway", 0);	// 计时方式
		stSel[1] = share.getInt("timerupd", 0);	// 计时器更新
		stSel[2] = share.getBoolean("prec", true) ? 1 : 0;	// 计时精度
		stSel[3] = share.getInt("multp", 0);	//分段计时
		isMulp = stSel[3] != 0;
		stSel[4] = share.getInt("srate", 1);	// 采样频率
		Stackmat.samplingRate = srate[stSel[4]];
		stSel[5] = share.getInt("cxe", 0);	//三阶求解
		stSel[6] = share.getInt("cube2l", 0);	// 二阶底层求解
		stSel[7] = share.getInt("minxc", 1);	//五魔配色
		stSel[8] = share.getInt("tfont", 3);	// 计时器字体
		stSel[9] = share.getInt("screenori", 0);	// 屏幕方向
		stSel[10] = share.getInt("vibra", 0);	// 震动反馈
		stSel[11] = share.getInt("vibtime", 2);	// 震动时长
		stSel[12] = share.getInt("sq1s", 0);	//SQ1复形计算
		stSel[13] = share.getInt("timeform", 0);	//时间格式
		stSel[14] = share.getInt("l1tp", 0);	//滚动平均1类型
		stSel[15] = share.getInt("l2tp", 0);	//滚动平均2类型
		l1len = share.getInt("l1len", 5);
		l2len = share.getInt("l2len", 12);
		bgcolor = share.getBoolean("bgcolor", true);	//使用背景颜色
		opac = share.getInt("opac", 35);	//背景图不透明度
		fulls = share.getBoolean("fulls", false);	// 全屏显示
		usess = share.getBoolean("usess", false);	// ss计时器
		Stackmat.inv = invs = share.getBoolean("invs", false);	// 反转信号
		opnl = share.getBoolean("scron", false);	// 屏幕常亮
		opnd = share.getBoolean("scrgry", true);	//允许暗屏
		selSes = share.getBoolean("selses", false);	//自动选择分组
		picPath = share.getString("picpath", "");	//背景图片路径
		frzTime = share.getInt("tapt", 0);	//启动延时
		intv = share.getInt("intv", 25);	//成绩列表行距
		drop = share.getBoolean("drop", false);	//拍桌子停表
		sensity = share.getInt("sensity", 25);	//灵敏度
		outPath = share.getString("scrpath", defPath);
		for(int i=0; i<15; i++) {
			sesType[i] = (short) share.getInt("sestp" + i, -1);
			sesnames[i] = share.getString("sesname" + i, "");
		}
		egtype = share.getInt("egtype", 7);
		egoll = share.getInt("egoll", 254);
		simss = share.getBoolean("simss", false);
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
		List<Sensor> ss = sensor.getSensorList(Sensor.TYPE_ACCELEROMETER);
		for(Sensor s : ss)
			sensor.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		super.onStop();
		sensor.unregisterListener(this);
	}
	
	private class OnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.stcheck1:	//WCA观察
				stSwitch[0].setBackgroundResource(wca ? R.drawable.switch_off : R.drawable.switch_on);
				wca = !wca; edit.putBoolean("wca", wca);
				break;
			case R.id.stcheck3:	//模拟ss计时
				stSwitch[2].setBackgroundResource(simss ? R.drawable.switch_off : R.drawable.switch_on);
				simss = !simss; edit.putBoolean("simss", simss);
				break;
			case R.id.stcheck4:	//使用ss计时
				stSwitch[3].setBackgroundResource(usess ? R.drawable.switch_off : R.drawable.switch_on);
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
				stSwitch[4].setBackgroundResource(invs ? R.drawable.switch_off : R.drawable.switch_on);
				invs = Stackmat.inv = !invs; edit.putBoolean("invs", invs);
				break;
			case R.id.stcheck6:	//隐藏打乱
				stSwitch[5].setBackgroundResource(hidscr ? R.drawable.switch_off : R.drawable.switch_on);
				hidscr = !hidscr; edit.putBoolean("hidscr", hidscr);
				break;
			case R.id.stcheck7:	//确认时间
				stSwitch[6].setBackgroundResource(conft ? R.drawable.switch_off : R.drawable.switch_on);
				conft = !conft; edit.putBoolean("conft", conft);
				break;
			case R.id.stcheck8:	//成绩列表隐藏打乱
				stSwitch[7].setBackgroundResource(hidls ? R.drawable.switch_on : R.drawable.switch_off);
				hidls = !hidls; edit.putBoolean("hidls", hidls);
				break;
			case R.id.stcheck9:	//自动选择分组
				stSwitch[8].setBackgroundResource(selSes ? R.drawable.switch_off : R.drawable.switch_on);
				selSes = !selSes; edit.putBoolean("selses", selSes);
				break;
			case R.id.stcheck10:	//拍桌子停表
				stSwitch[12].setBackgroundResource(drop ? R.drawable.switch_off : R.drawable.switch_on);
				drop = !drop; edit.putBoolean("drop", drop);
				break;
			case R.id.stcheck11:	//全屏显示
				stSwitch[9].setBackgroundResource(fulls ? R.drawable.switch_off : R.drawable.switch_on);
				if(fulls) getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				else getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				fulls = !fulls; edit.putBoolean("fulls", fulls);
				break;
			case R.id.stcheck12:	//屏幕常亮
				stSwitch[10].setBackgroundResource(opnl ? R.drawable.switch_off : R.drawable.switch_on);
				if(opnl) {
					if(timer.state != 1) releaseWakeLock();
				} else acquireWakeLock();
				opnl = !opnl; edit.putBoolean("scron", opnl);
				break;
			case R.id.stcheck13:	//允许变暗
				stSwitch[11].setBackgroundResource(opnd ? R.drawable.switch_off : R.drawable.switch_on);
				if(screenOn)releaseWakeLock();
				opnd = !opnd;
				if(screenOn)acquireWakeLock();
				edit.putBoolean("scrgry", opnd);
				break;
			case R.id.stcheck14:	//新浪微博授权
				if(isLogin) {
					new CustomDialog.Builder(context).setTitle(R.string.con_rsauth)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j) {
							AccessTokenKeeper.clear(context);
							isLogin = false;
							Toast.makeText(context, getString(R.string.rsauth), Toast.LENGTH_SHORT).show();
							stSwitch[1].setBackgroundResource(R.drawable.switch_off);
							//wbAuth.setText(getString(R.string.login)); TODO
						}
					}).setNegativeButton(R.string.btn_cancel, null)
					.create().show();
				}
				else {
					isShare = false;
					auth();
				}
				break;
			}
			edit.commit();
		}
	}
	

	private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			int prg = seekBar.getProgress();
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getString(R.string.timer_size) + (prg+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getString(R.string.scrsize) + (prg+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getString(R.string.row_spacing) + (prg+20));
			else if(seekBar.getId()==R.id.seekb5)stt[29].setText(getString(R.string.time_tap) + (prg/20D));
			else if(seekBar.getId()==R.id.seekb6)stt[37].setText(getString(R.string.stt_ssvalue) + prg);
			else if(seekBar.getId()==R.id.seekb7)stt[53].setText(getString(R.string.sensitivity) + (prg<15 ? getString(R.string.sen_low) :
				prg<30 ? getString(R.string.sen_mid) :
					prg < 45 ? getString(R.string.sen_high) : getString(R.string.sen_ultra)));
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			int prg = seekBar.getProgress();
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getString(R.string.timer_size)+ (prg+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getString(R.string.scrsize)+ (prg+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getString(R.string.row_spacing)+ (prg+20));
			else if(seekBar.getId()==R.id.seekb5)stt[29].setText(getString(R.string.time_tap)+ (prg/20D));
			else if(seekBar.getId()==R.id.seekb6)stt[37].setText(getString(R.string.stt_ssvalue) + prg);
			else if(seekBar.getId()==R.id.seekb7)stt[53].setText(getString(R.string.sensitivity) + (prg<15 ? getString(R.string.sen_low) :
				prg<30 ? getString(R.string.sen_mid) :
					prg < 45 ? getString(R.string.sen_high) : getString(R.string.sen_ultra)));
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			switch (seekBar.getId()) {
			case R.id.seekb1:	//计时器字体
				stt[3].setText(getString(R.string.timer_size)+ (seekBar.getProgress()+50));
				edit.putInt("ttsize", seekBar.getProgress()+50);
				tvTimer.setTextSize(seekBar.getProgress()+50);
				break;
			case R.id.seekb2:	//打乱字体
				stt[4].setText(getString(R.string.scrsize)+ (seekBar.getProgress()+12));
				edit.putInt("stsize", seekBar.getProgress()+12);
				tvScr.setTextSize(seekBar.getProgress()+12);
				break;
			case R.id.seekb3:	//成绩列表行距
				intv=seekBar.getProgress()+20;
				stt[10].setText(getString(R.string.row_spacing)+ intv);
				if(resl!=0) {
					new Thread() {
						public void run() {
							adapter.setHeight(intv);
							handler.sendEmptyMessage(18);
						}
					}.start();
				}
				edit.putInt("intv", seekBar.getProgress()+20);
				break;
			case R.id.seekb4:	//背景图不透明度
				if(!bgcolor) setBgPic(bitmap, seekBar.getProgress());
				opac = seekBar.getProgress();
				edit.putInt("opac", opac);
				break;
			case R.id.seekb5:	//启动延时
				frzTime=seekBar.getProgress();
				stt[29].setText(getString(R.string.time_tap)+ (frzTime/20D));
				edit.putInt("tapt", frzTime);
				break;
			case R.id.seekb6:	//ss参数
				int ssvalue = seekBar.getProgress();
				stt[37].setText(getString(R.string.stt_ssvalue) + ssvalue);
				Stackmat.switchThreshold = ssvalue;
				edit.putInt("ssvalue", ssvalue);
				break;
			case R.id.seekb7:	//灵敏度
				sensity = seekBar.getProgress();
				edit.putInt("sensity", sensity);
				stt[53].setText(getString(R.string.sensitivity) + (sensity<15 ? getString(R.string.sen_low) :
					sensity<30 ? getString(R.string.sen_mid) :
						sensity < 45 ? getString(R.string.sen_high) : getString(R.string.sen_ultra)));
				break;
			}
			edit.commit();
		}
	}

	private OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			selFilePath = paths.get(arg2);
			tvFile.setText(selFilePath);
			getFileDir(selFilePath);
		}
	};

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
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

	private View.OnTouchListener comboListener = new View.OnTouchListener() {
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
			case R.id.lay23: selt = 12; break;
			case R.id.lay18: selt = 13; break;
			case R.id.lay25: selt = 14; break;
			case R.id.lay26: selt = 15; break;
			default: selt = -1; break;
			}
			final int sel = selt;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llay[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				CustomDialog dialog = 
				new CustomDialog.Builder(context).setSingleChoiceItems(staid[sel], stSel[sel], new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(stSel[sel] != which) {
							stSel[sel] = which;
							switch (sel) {
							case 0:	//计时方式
								if (!usess) {
									if (which == 0) tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
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
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
									seMean.setText(getString(R.string.session_average) + Mi.sesMean());
								}
								break;
							case 3:	//分段计时
								if(which == 0) {
									isMulp=false; mulp = null; multemp = null;
									System.gc();
									listLen = (resl!=0) ? resl*3 : 0;
								} else if(!isMulp) {
									isMulp=true;
									multemp = new long[7];
									mulp = new int[6][rest.length];
									if(resl>0) {
										cursor = dbh.query(sesIdx);
										for(int i=0; i<resl; i++) {
											cursor.moveToPosition(i);
											for(int j=0; j<6; j++)
												mulp[j][i] = cursor.getInt(7+j);
										}
										//cursor.close();
									}
									listLen = resl!=0 ? (which+2)*(resl+1) : 0;
								}
								else {
									listLen = resl!=0 ? (which+2)*(resl+1) : 0;
								}
								edit.putInt("multp", which);
								setGridView();
								setGvTitle();
								break;
							case 4:	//采样频率
								if(stm.creatAudioRecord((int)srate[which]));
								else Toast.makeText(context, getString(R.string.sr_not_support), Toast.LENGTH_SHORT).show();
								edit.putInt("srate", which);
								break;
							case 5:	//三阶求解
								edit.putInt("cxe", which);
								if(scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)) {
									if(which==0)tvScr.setText(crntScr);
									else new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											switch(stSel[5]) {
											case 1: extsol="\n"+Cross.cross(crntScr, solSel[0], solSel[1]); break;
											case 2: extsol="\n"+Cross.xcross(crntScr, solSel[1]); break;
											case 3: extsol="\n"+EOline.eoLine(crntScr, solSel[1]); break;
											case 4: extsol="\n"+PetrusxRoux.roux(crntScr, solSel[1]); break;
											case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, solSel[1]); break;
											}
											handler.sendEmptyMessage(3);
											isNextScr = false;
											nextScr = Mi.setScr((scrIdx<<5)|scr2idx, false);
											isNextScr = true;
										}
									}.start();
								}
								break;
							case 6:	//二阶底面
								edit.putInt("cube2l", which);
								if(scrIdx==0) {
									if(which==0)tvScr.setText(crntScr);
									else if(scr2idx < 3) new Thread() {
										public void run() {
											handler.sendEmptyMessage(6);
											extsol = "\n"+Cube2bl.cube2layer(crntScr, stSel[6]);
											handler.sendEmptyMessage(3);
											isNextScr=false;
											nextScr = Mi.setScr((scrIdx<<5)|scr2idx, false);
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
							case 12:	//SQ复形求解
								edit.putInt("sq1s", which);
								if(scrIdx==8 && scr2idx<3) {
									if(stSel[12] > 0) {
										new Thread() {
											public void run() {
												handler.sendEmptyMessage(6);
												extsol = " " + (stSel[12]==1 ? Sq1Shape.solveTrn(crntScr) : Sq1Shape.solveTws(crntScr));
												handler.sendEmptyMessage(1);
												isNextScr = false;
												nextScr = Mi.setScr((scrIdx<<5)|scr2idx, false);
												isNextScr = true;
											}
										}.start();
									}
									else tvScr.setText(crntScr);
								}
								break;
							case 13:	//时间格式
								edit.putInt("timeform", which);
								if(resl>0) {
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
								}
								break;
							case 14:	//滚动平均1类型
								edit.putInt("l1tp", which);
								if(!isMulp) setGvTitle();
								if(resl>0 && !isMulp) {
									setGvTitle();
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
								}
								break;
							case 15:	//滚动平均2类型
								edit.putInt("l2tp", which);
								if(!isMulp) setGvTitle();
								if(resl>0 && !isMulp) {
									setGvTitle();
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
								}
								break;
							}
							edit.commit();
							std[sel].setText(itemStr[sel][which]);
						}
					}
				}).setNegativeButton(R.string.btn_close, null).create();
				showDialog(dialog);
			case MotionEvent.ACTION_CANCEL:
				llay[sel].setBackgroundColor(0);
				break;
			}
			return false;
		}
	};

	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) { //TODO
			int sel;
			switch (v.getId()) {
			case R.id.lay16: sel = 16; break;
			case R.id.lay17: sel = 17; break;
			case R.id.lay22: sel = 18; break;
			case R.id.lay19: sel = 19; break;
			case R.id.lay20: sel = 20; break;
			case R.id.lay21: sel = 21; break;
			case R.id.lay13: sel = 22; break;
			case R.id.lay24: sel = 23; break;
			case R.id.lay14: sel = 24; break;
			case R.id.lay15: sel = 25; break;
			case R.id.lay27: sel = 26; break;
			case R.id.lay28: sel = 27; break;
			default: sel = -1; break;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llay[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				switch (sel) {
				case 16:	//最慢单次颜色
					cpDialog = new ColorPicker(context, cl[3], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[3]=color;
							new Thread() {
								public void run() {
									adapter.setWorstColor(cl[3]);
									handler.sendEmptyMessage(18);
								}
							}.start();
							edit.putInt("cl3", color);
							edit.commit();
						}
					});
					cpDialog.show();
					break;
				case 17:	//最快平均颜色
					cpDialog = new ColorPicker(context, cl[4], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[4]=color;
							if(!isMulp) 
								new Thread() {
									public void run() {
										adapter.setBestAvgColor(cl[4]);
										handler.sendEmptyMessage(18);
									}
								}.start();
							edit.putInt("cl4", color);
							edit.commit();
						}
					});
					cpDialog.show();
					break;
				case 18:	//背景图片
					Intent intent = new Intent();
					intent.setType("image/*");	//开启Pictures画面Type设定为image
					intent.setAction(Intent.ACTION_GET_CONTENT);	//使用Intent.ACTION_GET_CONTENT这个Action
					startActivityForResult(intent, 1);	//取得相片后返回本画面
					break;
				case 19:	//配色设置
					int[] colors={share.getInt("csn1", Color.YELLOW), share.getInt("csn2", Color.BLUE), share.getInt("csn3", Color.RED),
							share.getInt("csn4", Color.WHITE), share.getInt("csn5", 0xff009900), share.getInt("csn6", 0xffff8026)};
					ColorScheme dialog = new ColorScheme(context, 1, colors, new ColorScheme.OnSchemeChangedListener() {
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csn"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getString(R.string.scheme_cube));
					dialog.show();
					break;
				case 20:	//金字塔配色
					colors = new int[] {share.getInt("csp1", Color.RED), share.getInt("csp2", 0xff009900),
							share.getInt("csp3", Color.BLUE), share.getInt("csp4", Color.YELLOW)};
					dialog = new ColorScheme(context, 2, colors, new ColorScheme.OnSchemeChangedListener() {
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csp"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getString(R.string.scheme_pyrm));
					dialog.show();
					break;
				case 21:	//SQ配色
					colors = new int[] {share.getInt("csq1", Color.YELLOW), share.getInt("csq2", Color.BLUE), share.getInt("csq3", Color.RED),
							share.getInt("csq4", Color.WHITE), share.getInt("csq5", 0xff009900), share.getInt("csq6", 0xffff8026)};
					dialog = new ColorScheme(context, 3, colors, new ColorScheme.OnSchemeChangedListener() {
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csq"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getString(R.string.scheme_sq));
					dialog.show();
					break;
				case 22:	//背景颜色
					cpDialog = new ColorPicker(context, cl[0], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							tabHost.setBackgroundColor(color); cl[0]=color; bgcolor=true;
							edit.putInt("cl0", color); edit.putBoolean("bgcolor", true);
							edit.commit();
						}
					});
					cpDialog.show();
					break;
				case 23:	//Skewb配色
					colors = new int[] {share.getInt("csw1", Color.YELLOW), share.getInt("csw2", Color.BLUE), share.getInt("csw3", Color.RED),
							share.getInt("csw4", Color.WHITE), share.getInt("csw5", 0xff009900), share.getInt("csw6", 0xffff8026)};
					dialog = new ColorScheme(context, 4, colors, new ColorScheme.OnSchemeChangedListener() {
						@Override
						public void schemeChanged(int idx, int color) {
							edit.putInt("csw"+idx, color);
							edit.commit();
						}
					});
					dialog.setTitle(getString(R.string.scheme_skewb));
					dialog.show();
					break;
				case 24:	//文字颜色
					cpDialog = new ColorPicker(context, cl[1], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[1]=color;
							setTextsColor();
							new Thread() {
								public void run() {
									adapter.setTextColor(cl[1]);
									handler.sendEmptyMessage(18);
								}
							}.start();
							setGvTitle();
							edit.putInt("cl1", color);edit.commit();
						}
					});
					cpDialog.show();
					break;
				case 25:	//最快单次颜色
					cpDialog = new ColorPicker(context, cl[2], new ColorPicker.OnColorChangedListener() {
						@Override
						public void colorChanged(int color) {
							cl[2] = color;
							new Thread() {
								public void run() {
									adapter.setBestColor(cl[2]);
									handler.sendEmptyMessage(18);
								}
							}.start();
							edit.putInt("cl2", color);
							edit.commit();
						}
					});
					cpDialog.show();
					break;
				case 26:	//滚动平均1长度
					LayoutInflater factory = LayoutInflater.from(context);
					final View view = factory.inflate(R.layout.number_input, null);
					final EditText editText = (EditText) view.findViewById(R.id.editText1);
					editText.setText(""+l1len);
					editText.setSelection(editText.getText().length());
					new CustomDialog.Builder(context).setTitle(R.string.enter_len).setContentView(view)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int len = Integer.parseInt(editText.getText().toString());
							if(len < 3 || len > 1000)
								Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_LONG).show();
							else {
								l1len = len;
								edit.putInt("l1len", len);
								edit.commit();
								stdn[0].setText("" + len);
								if(resl>0 && !isMulp) {
									setGvTitle();
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
								}
							}
							hideKeyboard(editText);
						}
					}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							hideKeyboard(editText);
						}
					}).create().show();
					showKeyboard(editText);
					break;
				case 27:	//滚动平均2长度
					LayoutInflater fact = LayoutInflater.from(context);
					final View vw = fact.inflate(R.layout.number_input, null);
					final EditText edt = (EditText) vw.findViewById(R.id.editText1);
					edt.setText(""+l2len);
					edt.setSelection(edt.getText().length());
					new CustomDialog.Builder(context).setTitle(R.string.enter_len).setContentView(vw)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int len = Integer.parseInt(edt.getText().toString());
							if(len < 3 || len > 1000)
								Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_LONG).show();
							else {
								l2len = len;
								edit.putInt("l2len", len);
								edit.commit();
								stdn[1].setText("" + len);
								if(resl>0 && !isMulp) {
									setGvTitle();
									new Thread() {
										public void run() {
											adapter.refresh(listLen);
											handler.sendEmptyMessage(18);
										}
									}.start();
								}
							}
							hideKeyboard(edt);
						}
					}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							hideKeyboard(edt);
						}
					}).create().show();
					showKeyboard(edt);
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
		for(int i=0; i<8; i++)
			if((egoll & (1<<(7-i))) != 0)
				sb.append(ego.charAt(i));
		egolls = sb.toString();
	}

	private void viewsVisibility(boolean v) {
		int vi = v ? 0 : 8;
		tabHost.getTabWidget().setVisibility(vi);
		btScr.setVisibility(vi);
		bt2Scr.setVisibility(vi);
		btScrv.setVisibility(vi);
		if(hidscr)tvScr.setVisibility(vi);
	}

	private void set2ndsel() {
		String[] s = getResources().getStringArray(get2ndScr(scrIdx));
		if(scr2idx >= s.length) scr2idx = 0;
		bt2Scr.setText(s[scr2idx]);
		btScr.setText(scrStr[scrIdx+1]);
		newScr(true);
	}
	
	private int get2ndScr(int s) {
		switch (s) {
		case -1: return R.array.scrwca;
		case 0: return R.array.scr222;
		case 1: return R.array.scr333;
		case 2: return R.array.scr444;
		case 3: return R.array.scr555;
		case 4:
		case 5: return R.array.scr666;
		case 6: return R.array.scrMinx;
		case 7: return R.array.scrPrym;
		case 8: return R.array.scrSq1;
		case 9: return R.array.scrClk;
		case 15: return R.array.scr15p;
		case 11: return R.array.scrMxN;
		case 12: return R.array.scrCmt;
		case 13: return R.array.scrGear;
		case 14: return R.array.scrSmc;
		case 10: return R.array.scrSkw;
		case 16: return R.array.scrOth;
		case 17: return R.array.scr3sst;
		case 18: return R.array.scrBdg;
		case 19: return R.array.scrMsst;
		default: return R.array.scrRly;
		}
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
			proDlg.setMax(num);
			proDlg.show();
			//proDlg = ProgressDialog.show(context, getString(R.string.menu_outscr), "");
			new Thread() {
				public void run() {
					try {
						OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
						for(int i=0; i<num; i++) {
							String scr=(i+1)+". "+Mi.setScr((scrIdx<<5)|scr2idx, false)+"\r\n";
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
		else Toast.makeText(context, getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
	}

	private void outStat(String path, String fileName, String stat) {
		File fPath = new File(path);
		if(fPath.exists() || fPath.mkdir() || fPath.mkdirs()) {
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
				byte [] bytes = stat.toString().getBytes();
				out.write(bytes);
				out.close();
				Toast.makeText(context, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(context, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
			}
		}
		else Toast.makeText(context, getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		menu.add(Menu.NONE, 0, 0, getString(R.string.menu_inscr));
		menu.add(Menu.NONE, 1, 1, getString(R.string.menu_outscr));
		menu.add(Menu.NONE, 2, 2, getString(R.string.menu_share));
		menu.add(Menu.NONE, 3, 3, getString(R.string.menu_weibo));
		menu.add(Menu.NONE, 4, 4, getString(R.string.menu_about));
		menu.add(Menu.NONE, 5, 5, getString(R.string.menu_exit));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		CustomDialog.Builder builder;
		switch(item.getItemId()) {
		case 0:
			LayoutInflater factory = LayoutInflater.from(context);
			final View view0 = factory.inflate(R.layout.inscr_layout, null);
			final Spinner sp = (Spinner) view0.findViewById(R.id.spnScrType);
			String[] items = getResources().getStringArray(R.array.inscrStr);
			ArrayAdapter<String> adap = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
			adap.setDropDownViewResource(R.layout.spinner_dropdown_item);
			sp.setAdapter(adap);
			final EditText et0 = (EditText) view0.findViewById(R.id.edit_inscr);
			sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					insType = arg2;
				}
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			builder = new Builder(context);
			builder.setContentView(view0).setTitle(R.string.menu_inscr)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i) {
					hideKeyboard(et0);
					final String scrs=et0.getText().toString();
					inScr = new ArrayList<String>();
					inScrLen = 0;
					setInScr(scrs);
					if(inScr.size()>0) newScr(false);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					hideKeyboard(et0);
				}
			}).create().show();
			showKeyboard(et0);
			break;
		case 1:
			final LayoutInflater factory1 = LayoutInflater.from(context);
			final View view1 = factory1.inflate(R.layout.outscr_layout, null);
			final EditText et1 = (EditText) view1.findViewById(R.id.edit_scrnum);
			final EditText et2 = (EditText) view1.findViewById(R.id.edit_scrpath);
			final Button btn = (Button) view1.findViewById(R.id.btn_browse);
			et1.setText("5");
			et1.setSelection(1);
			et2.setText(outPath);
			final EditText et3 = (EditText) view1.findViewById(R.id.edit_scrfile);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selFilePath = et2.getText().toString();
					final View viewb = factory1.inflate(R.layout.file_selector, null);
					listView = (ListView) viewb.findViewById(R.id.list);
					File f = new File(selFilePath);
					selFilePath = f.exists()?selFilePath:Environment.getExternalStorageDirectory().getPath()+File.separator;
					tvFile = (TextView) viewb.findViewById(R.id.text);
					tvFile.setText(selFilePath);
					getFileDir(selFilePath);
					listView.setOnItemClickListener(itemListener);
					CustomDialog cdialog =
					new CustomDialog.Builder(context).setTitle(R.string.sel_path).setContentView(viewb)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j) {
							et2.setText(selFilePath+"/");
						}
					}).setNegativeButton(R.string.btn_cancel, null).create();
					showDialog(cdialog);
				}
			});
			CustomDialog dialog =
			new CustomDialog.Builder(context)
			.setContentView(view1).setTitle(getString(R.string.menu_outscr)+"("+getScrName()+")")
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i) {
					int numt = Integer.parseInt(et1.getText().toString());
					if(numt>100)numt=100;
					else if(numt<1)numt=5;
					final int num = numt;
					final String path=et2.getText().toString();
					if(!path.equals(outPath)) {
						outPath=path;
						edit.putString("scrpath", path);
						edit.commit();
					}
					final String fileName=et3.getText().toString();
					File file = new File(path+fileName);
					if(file.isDirectory())Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
					else if(file.exists()) {
						new CustomDialog.Builder(context).setTitle(R.string.path_dupl)
						.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j) {
								outScr(path, fileName, num);
							}
						}).setNegativeButton(R.string.btn_cancel, null).create().show();
					} else {
						outScr(path, fileName, num);
					}
					hideKeyboard(et1);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(et1);
				}
			}).create();
			showDialog(dialog);
			showKeyboard(et1);
			break;
		case 2:
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");	//纯文本
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, getShareContext());
			startActivity(Intent.createChooser(intent, getTitle()));
			break;
		case 3:
			isShare = true;
			if(!isLogin) {
				auth();
			} else {
				WBShareActivity.text = getShareContext();
				WBShareActivity.bitmap = takeScreenShot(DCTimer.this);
				Intent it = new Intent(context, WBShareActivity.class);
				startActivity(it);
			}
			break;
		case 4:
			//LayoutInflater factory2 = LayoutInflater.from(context);
			//final View view = factory2.inflate(R.layout.dlg_about, null);
			builder = new Builder(context);
			builder.setTitle(R.string.abt_title).setMessage(R.string.abt_msg)
			.setPositiveButton(R.string.btn_upgrade, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new Thread() {
						public void run() {
							handler.sendEmptyMessage(8);
							String ver = getContent("https://raw.github.com/MeigenChou/DCTimer/master/release/version.txt");
							if(ver.startsWith("error")) {
								handler.sendEmptyMessage(9);
							} else {
								String[] vers = ver.split("\t");
								int v = Integer.parseInt(vers[0]);
								if(v > verc) {
									newver = vers[1];
									StringBuilder sb = new StringBuilder(vers[2]);
									if(vers.length > 3)
										for(int i=3; i<vers.length; i++) sb.append("\n"+vers[i]);
									newupd = sb.toString();
									handler.sendEmptyMessage(11);
								}
								else handler.sendEmptyMessage(10);
							}
						}
					}.start();
				}
			})
			.setNegativeButton(R.string.btn_close, null).create().show();
			break;
		case 5:
			cursor.close();
			dbh.close();
			edit.putInt("sel", scrIdx);
			edit.putInt("sel2", scr2idx);
			edit.commit();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return true;
	}

	private String getContent(String strUrl) {
        try {
            URL url = new URL(strUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "GB2312"));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while((line = br.readLine()) != null) {
            	sb.append(line+"\t");
            }
            br.close();
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            return "error open url:" + strUrl;
        }
    }
	
	private void download(final String fileName) {
		bytesum = 0;
		final File f = new File(defPath);
    	if(!f.exists()) f.mkdirs();
    	dlProg.show();
        new Thread() {
        	public void run() {
        		try {
                	URL url = new URL("https://raw.github.com/MeigenChou/DCTimer/master/release/"+fileName);
                	URLConnection conn = url.openConnection();
                	conn.connect();
                	InputStream is = conn.getInputStream();
                	int filesum = conn.getContentLength();
                	if(filesum == 0) {
                		handler.sendEmptyMessage(13);
                		dlProg.dismiss();
                		return;
                	}
                	dlProg.setMax(filesum / 1024);
                	FileOutputStream fs = new FileOutputStream(defPath+fileName);
                	byte[] buffer = new byte[2096];
                	int byteread;
                	while ((byteread = is.read(buffer)) != -1) {
                		bytesum += byteread;
                		fs.write(buffer, 0, byteread);
                		handler.sendEmptyMessage(12);
                	}
                	fs.close();
        		} catch (Exception e) {
        			handler.sendEmptyMessage(9);
        			dlProg.dismiss();
            		return;
        		}
        		dlProg.dismiss();
        		Intent intent = new Intent();
        		intent.setAction(android.content.Intent.ACTION_VIEW);
        		intent.setDataAndType(Uri.parse("file://"+defPath+fileName), "application/vnd.android.package-archive");
        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		startActivity(intent);
        	}
        }.start();
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
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.list_item, items);
		listView.setAdapter(fileList);
	}

	private void setGridView() {	//TODO
		if(!isMulp) {
			adapter = new TimesAdapter (context, listLen, new int[] {
					cl[1],cl[2],cl[3],cl[4]}, intv);
			gvTimes.setNumColumns(3);
		} else {
			adapter = new TimesAdapter(context,	listLen, new int[]{cl[1],
					cl[2], cl[3]}, intv, stSel[3]+2);
			gvTimes.setNumColumns(stSel[3]+2);
		}
		gvTimes.setStackFromBottom(false);
		gvTimes.setAdapter(adapter);
	}

	private void setGvTitle() {
		if(isMulp) {
			String[] title = new String[stSel[3]+2];
			title[0] = getString(R.string.time);
			for(int i=1; i<stSel[3]+2; i++) title[i] = "P-"+i;
			TitleAdapter ta = new TitleAdapter(context, title, cl[1]);
			gvTitle.setNumColumns(stSel[3]+2);
			gvTitle.setAdapter(ta);
		}
		else {
			String[] title = {getString(R.string.time),
					(stSel[14]==0 ? "avg of " : "mean of ") + l1len,
					(stSel[15]==0 ? "avg of " : "mean of ") + l2len};
			TitleAdapter ta = new TitleAdapter(context, title, cl[1]);
			gvTitle.setNumColumns(3);
			gvTitle.setAdapter(ta);
		}
	}

	private String getShareContext() {
		String s1 = getString(R.string.share_c1).replace("$len", ""+resl).replace("$scrtype", getScrName())
				.replace("$best", Mi.distime(Mi.minIdx, false)).replace("$mean", Mi.distime(Mi.sesMean));
		String s2 = (resl>l1len)?getString(R.string.share_c2).replace("$flen", ""+l1len).
				replace("$favg", Mi.distime(Mi.bavg[0])):"";
		String s3 = (resl>l2len)?getString(R.string.share_c2).replace("$flen", ""+l2len).
				replace("$favg", Mi.distime(Mi.bavg[1])):"";
		String s4 = getString(R.string.share_c3);
		return s1 + s2 + s3 + s4;
	}

	private String getScrName() {
		String[] mItems = getResources().getStringArray(R.array.cubeStr);
		String[] s = getResources().getStringArray(get2ndScr(scrIdx));
		return mItems[scrIdx+1] + "-" + s[scr2idx];
	}

	private void searchSesType() {
		int type=0, idx=-1;
		for(int i=0; i<15; i++) {
			int s = sesType[i];
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
		if(type==2 || (sesType[sesIdx] != -1 && type == 1)) {
			sesIdx = (byte) idx;
			sesName.setText(getString(R.string.session) + sesItems[idx]);
			getSession(idx);
			seMean.setText(getString(R.string.session_average) + Mi.sesMean());
			setGridView();
			sesIdx = (byte) idx;
			edit.putInt("group", idx);
			edit.commit();
		}
	}

	private void setScrType() {
		switch(scrIdx) {
		case -1:
			switch (scr2idx) {
			case 0: scrType = 1; break;
			case 1: scrType = 2; break;
			case 2: scrType = 3; break;
			case 3: scrType = 0; break;
			case 4: scrType = 37; break;
			case 5: scrType = 38; break;
			case 6: scrType = 39; break;
			case 7: scrType = 40; break;
			case 8: scrType = 6; break;
			case 9: scrType = 7; break;
			case 10: scrType = 8; break;
			case 11: scrType = 9; break;
			case 12: scrType = 10; break;
			case 13: scrType = 4; break;
			case 14: scrType = 5; break;
			case 15: scrType = 41; break;
			case 16: scrType = 42; break;
			case 17: scrType = 43; break;
			}
			break;
		case 0:	//2阶
		case 1:	//3阶
		case 2:	//4阶
		case 3:	//5阶
		case 4:	//6阶
		case 5:	//7阶
		case 6:	//五魔
		case 7:	//金字塔
		case 8:	//sq1
		case 9:	//魔表
		case 10:	//斜转
			scrType = scrIdx; break;	//0~10
		case 11:
			if(scr2idx<3) scrType = 11;	//1x3x3
			else if(scr2idx<5) scrType = 12;	//2x3x3
			else if(scr2idx<12) scrType = scr2idx + 8;	//13~19
			else scrType = scr2idx + 37;	//49~
			break;
		case 12:	//cmetrick
		case 13:	//齿轮
		case 14:	//siamese cube
		case 15:	//15puzzle
			scrType = scrIdx + 8; break;	//20~23
		case 16:	//其他
			scrType = scr2idx + 24; break;	//24~29
		case 17:	//3阶子集
			scrType = 1; break;
		case 18:	//bandaged cube
			scrType = scr2idx + 30; break;	//30~31
		case 19:	//五魔子集
			scrType = 6; break;
		case 20:	//连拧
			scrType = scr2idx + 32; break;	//32~36
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
		mSsoHandler = new SsoHandler(DCTimer.this, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener());
	}

	private void setTouch(MotionEvent e) {
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
			int x1=0, x2=0;
			try {
				x1 = (int)e.getX(0)*2/tvTimer.getWidth();
				x2 = (int)e.getX(1)*2/tvTimer.getWidth();
			} catch (Exception ex) { }
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
					acquireWakeLock();
					screenOn = true;
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
			LayoutInflater factory = LayoutInflater.from(context);
			final View view = factory.inflate(R.layout.editbox_layout, null);
			final EditText editText = (EditText) view.findViewById(R.id.editText1);
			new CustomDialog.Builder(context).setTitle(R.string.enter_time).setContentView(view)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String time = Mi.convStr(editText.getText().toString());
					if(time.equals("Error") || Mi.convTime(time)==0)
						Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_SHORT).show();
					else save(Mi.convTime(time), (byte) 0);
					//setGridView(false);
					//seMean.setText(getString(R.string.session_average) + Mi.sesMean());
					//newScr(false);
					hideKeyboard(editText);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(editText);
				}
			}).create().show();
			showKeyboard(editText);
			break;
		}
	}

	private void updateGrid() {
		new Thread() {
			public void run() {
				try {
					sleep(200);
					adapter.refresh(listLen);
					handler.sendEmptyMessage(18);
				} catch (InterruptedException e) { }
			}
		}.start();
	}
	
	private void save(int time, int p) {
		if(resl >= rest.length) {
			String[] scr2 = new String[scrst.length*2];
			byte[] rep2 = new byte[resp.length*2];
			int res2[] = new int[rest.length*2];
			for(int i=0; i<resl; i++) {
				scr2[i]=scrst[i]; rep2[i]=resp[i]; res2[i]=rest[i];
			}
			scrst=scr2; resp=rep2; rest=res2;
			if(isMulp) {
				int[][] mulp2 = new int[6][rest.length];
				for(int i=0;i<resl;i++)
					for(int j=0; j<6; j++)
						mulp2[j][i] = mulp[j][i];
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
		if(p==2) p=d=0;
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
		dbh.insert(sesIdx, cv);
		listLen = isMulp ? (stSel[3]+2)*(resl+1) : resl*3;
		seMean.setText(getString(R.string.session_average) + Mi.sesMean());
		updateGrid();
		if(selSes && sesType[sesIdx] != scrType) {
			sesType[sesIdx] = (short) scrType;
			edit.putInt("sestp"+sesIdx, scrType);
			edit.commit();
		}
		newScr(false);
	}
	private void update(int idx, byte p) {
		if(resp[idx] != p) {
			resp[idx] = p;
			byte d = 1;
			if(p==2) p=d=0;
			cursor = dbh.query(sesIdx);
			cursor.moveToPosition(idx);
			int id=cursor.getInt(0);
			//cursor.close();
			dbh.update(sesIdx, id, p, d);
			seMean.setText(getString(R.string.session_average)+Mi.sesMean());
			updateGrid();
		}
	}
	private void delete(int idx, int col) {
		int delId;
		if(idx != resl-1) {
			for(int i=idx; i<resl-1; i++) {
				rest[i]=rest[i+1]; resp[i]=resp[i+1]; scrst[i]=scrst[i+1];
				if(isMulp)
					for(int j=0; j<stSel[3]+1; j++)
						mulp[j][i] = mulp[j][i+1];
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
		dbh.del(sesIdx, delId);
		//cursor.close();
		resl--;
		if(resl > 0) {
			listLen = isMulp ? (resl+1)*col : resl*col;
		}
		else {
			listLen = 0;
			sesType[sesIdx] = -1;
			edit.remove("sestp"+sesIdx);
			edit.commit();
		}
		seMean.setText(getString(R.string.session_average) + Mi.sesMean());
		updateGrid();
	}
	private void deleteAll() {
		dbh.clear(sesIdx);
		resl = dbLastId = 0;
		listLen = 0;
		seMean.setText(getString(R.string.session_average) + "0/0): N/A (N/A)");
		Mi.maxIdx = Mi.minIdx = -1;
		updateGrid();
		if(sesType[sesIdx] != -1) {
			sesType[sesIdx] = -1;
			edit.remove("sestp"+sesIdx);
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
					Toast.makeText(context, getString(R.string.again_exit), Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					edit.putInt("sel", scrIdx);
					edit.putInt("sel2", scr2idx);
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
			if(resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_del_last)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					cursor = dbh.query(sesIdx);
					delete(resl-1, isMulp ? stSel[3]+2 : 3);
				}
			}).setNegativeButton(R.string.btn_cancel, null).create().show();
		}
		else if(keyCode == KeyEvent.KEYCODE_A) {
			if(resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_clear_session)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {deleteAll();}
			}).setNegativeButton(R.string.btn_cancel, null).create().show();
		}
		else if(keyCode == KeyEvent.KEYCODE_D) {
			if(resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else {
				CustomDialog cdialog = 
						new CustomDialog.Builder(context).setTitle(getString(R.string.show_time)+Mi.distime(resl-1, true))
						.setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0: update(resl-1, (byte) 0); break;
								case 1: update(resl-1, (byte) 1); break;
								case 2: update(resl-1, (byte) 2); break;
								}
							}
						}).setNegativeButton(getString(R.string.btn_cancel), null).create();
				showDialog(cdialog);
			}
		}
		return false;
	}

	private void chScr(int s1, int s2) {
		boolean c1 = false, c2 = false;
		if(scrIdx != s1) {
			c1 = true;
			btScr.setText(scrStr[s1+1]);
			scrIdx = (byte) s1;
			if(scrIdx != selold) selold = scrIdx;
		}
		if(scr2idx != s2) {
			c2 = true;
			scr2idx = (byte) s2;
		}
		if(c1 || c2) {
			set2ndsel();
			setScrType();
			if(selSes)searchSesType();
			if(inScr != null && inScr.size() != 0) inScr = null;
		}
	}

	private void showAlertDialog(int i, int j) {
		String t = null;
		switch(i) {
		case 1:
			t = (stSel[14]==0 ? getString(R.string.sta_avg) : getString(R.string.sta_mean)).replace("len", ""+l1len);
			slist=stSel[14]==0 ? ao(l1len, j):mo(l1len, j);
			break;
		case 2:
			t = (stSel[15]==0 ? getString(R.string.sta_avg) : getString(R.string.sta_mean)).replace("len", ""+l2len);
			slist=stSel[15]==0 ? ao(l2len, j):mo(l2len, j);
			break;
		case 3:
			t=getString(R.string.sta_session_mean);
			slist=sesMean();
			break;
		}
		new CustomDialog.Builder(context).setTitle(t).setMessage(slist)
		.setPositiveButton(R.string.btn_copy, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(slist);
				Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
			}
		}).setNeutralButton(R.string.btn_save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final LayoutInflater factory = LayoutInflater.from(context);
				final View view = factory.inflate(R.layout.save_stat, null);
				final EditText et1 = (EditText) view.findViewById(R.id.edit_scrpath);
				final Button btn = (Button) view.findViewById(R.id.btn_browse);
				et1.setText(outPath);
				final EditText et2 = (EditText) view.findViewById(R.id.edit_scrfile);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
				et2.requestFocus();
				et2.setText(getString(R.string.def_sname).replace("$datetime", formatter.format(new Date())));
				et2.setSelection(et2.getText().length());
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						selFilePath = et1.getText().toString();
						final View viewb = factory.inflate(R.layout.file_selector, null);
						listView = (ListView) viewb.findViewById(R.id.list);
						File f = new File(selFilePath);
						selFilePath = f.exists() ? selFilePath : Environment.getExternalStorageDirectory().getPath()+File.separator;
						tvFile = (TextView) viewb.findViewById(R.id.text);
						tvFile.setText(selFilePath);
						getFileDir(selFilePath);
						listView.setOnItemClickListener(itemListener);
						CustomDialog cdialog =
						new CustomDialog.Builder(context).setTitle(R.string.sel_path).setContentView(viewb)
						.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j) {
								et1.setText(selFilePath+"/");
							}
						}).setNegativeButton(R.string.btn_cancel, null).create();
						showDialog(cdialog);
					}
				});
				CustomDialog cdialog =
				new CustomDialog.Builder(context).setContentView(view).setTitle(R.string.stat_save)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						final String path=et1.getText().toString();
						if(!path.equals(outPath)) {
							outPath=path;
							edit.putString("scrpath", path);
							edit.commit();
						}
						final String fileName=et2.getText().toString();
						File file = new File(path+fileName);
						if(file.isDirectory())Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
						else if(file.exists()) {
							new CustomDialog.Builder(context).setMessage(R.string.path_dupl)
							.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int j) {
									outStat(path, fileName, slist);
								}
							}).setNegativeButton(R.string.btn_cancel, null).create().show();
						} else outStat(path, fileName, slist);
						hideKeyboard(et1);
					}
				}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						hideKeyboard(et1);
					}
				}).create();
				showDialog(cdialog);
			}
		}).setNegativeButton(R.string.btn_close, null).create().show();
	}

	protected void newScr(final boolean ch) {
		if(!ch && inScr!=null && inScrLen<inScr.size()) {
			if(!isInScr) isInScr = true;
			crntScr = inScr.get(inScrLen++);
			switch (insType) {
			case 0:
				if(crntScr.matches("([FRU][2']?\\s*)+"))
					Mi.viewType = 2;
				else if(crntScr.matches("([ULRBulrb]'?\\s*)+"))
					Mi.viewType = Mi.TYPE_PYRAM;
				else if(crntScr.matches("([xFRUBLDMfrubld][2']?\\s*)+"))
					Mi.viewType = 3;
				else if(crntScr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+"))
					Mi.viewType = 4;
				else if(crntScr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+"))
					Mi.viewType = 5;
				else if(crntScr.matches("(((2?[FRUBLD])|(3[FRU]w))[2']?\\s*)+"))
					Mi.viewType = 6;
				else if(crntScr.matches("(((2|3)?[FRUBLD])[2']?\\s*)+"))
					Mi.viewType = 7;
				else Mi.viewType = 0;
				break;
			case 1:
				if(crntScr.matches("([FRUBLD][2']?\\s*)+"))
					Mi.viewType = 2;
				else Mi.viewType = 0;
				break;
			case 2:
				if(crntScr.matches("([xFRUBLDMfrubld][2']?\\s*)+"))
					Mi.viewType = 3;
				else Mi.viewType = 0;
				break;
			case 3:
				if(crntScr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+"))
					Mi.viewType = 4;
				else Mi.viewType = 0;
				break;
			case 4:
				if(crntScr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+"))
					Mi.viewType = 5;
				else Mi.viewType = 0;
				break;
			case 5:
				if(crntScr.matches("([ULRBulrb]'?\\s*)+"))
					Mi.viewType = Mi.TYPE_PYRAM;
				else Mi.viewType = 0;
			}
			if(Mi.viewType==3 && stSel[5]!=0) {
				new Thread() {
					public void run() {
						handler.sendEmptyMessage(6);
						if(stSel[5]==1)extsol="\n"+Cross.cross(crntScr, DCTimer.solSel[0], DCTimer.solSel[1]);
						else if(stSel[5]==2)extsol="\n"+Cross.xcross(crntScr, DCTimer.solSel[1]);
						else if(stSel[5]==3)extsol="\n"+EOline.eoLine(crntScr, DCTimer.solSel[1]);
						else if(stSel[5]==4)extsol="\n"+PetrusxRoux.roux(crntScr, DCTimer.solSel[1]);
						else if(stSel[5]==5)extsol="\n"+PetrusxRoux.petrus(crntScr, DCTimer.solSel[1]);
						handler.sendEmptyMessage(3);
					}
				}.start();
			}
			else tvScr.setText(crntScr);
		} else if((scrIdx==-1 && (scr2idx==0 || scr2idx==1 || (scr2idx>3 && scr2idx<8) || scr2idx==10 || scr2idx==15 || scr2idx==17)) ||
				(scrIdx==0 && scr2idx<3 && stSel[6]!=0) ||
				(scrIdx==1 && (scr2idx!=0 || (stSel[5]!=0 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)))) ||
				(scrIdx==2 && scr2idx==5) ||
				(scrIdx==8 && (scr2idx>1 || (scr2idx<3 && stSel[12]>0))) ||
				(scrIdx==11 && (scr2idx>3 && scr2idx<7)) ||
				(scrIdx==17 && (scr2idx<3 || scr2idx==6)) ||
				scrIdx==20) {	//TODO
			if(isInScr) isInScr = false;
			if(ch) canScr = true;
			if(canScr) {
				if(ch) {
//					if(scrThread != null && scrThread.isAlive()) 
//						scrThread.interrupt();
					scrThread = new Scrambler(ch);
					scrThread.start();
				} else if(!isNextScr) {
					nextScrUsed = true;
					tvScr.setText(getString(R.string.scrambling));
				} else {
					scrThread = new Scrambler(ch);
					scrThread.start();
				}
			}
		} else {
//			if(ch && scrThread != null && scrThread.isAlive()) 
//				scrThread.interrupt();
			crntScr = Mi.setScr(scrIdx<<5|scr2idx, ch);
			tvScr.setText(crntScr);
		}
	}

	public void confirmTime(final int time) {
		if(idnf) {
			if(conft) {
				CustomDialog cdialog = 
				new CustomDialog.Builder(context).setTitle(getString(R.string.show_time)+Mi.distime(time + isp2))
						.setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:save(time + isp2, 0);break;
						case 1:save(time + isp2, 1);break;
						case 2:save(time + isp2, 2);break;
						}
					}
				})
				.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						newScr(false);
					}
				}).create();
				showDialog(cdialog);
			}
			else save(time + isp2, 0);
		}
		else {
			if(conft)
				new CustomDialog.Builder(context).setTitle(R.string.time_dnf).setMessage(R.string.confirm_adddnf)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j) {
						save((int)timer.time, 2);
					}
				}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						newScr(false);
					}
				}).create().show();
			else save((int)timer.time, 2);
		}
	}

	public String sesMean() {
		StringBuffer sb=new StringBuffer();
		int n=resl;
		for(int i=0;i<resl;i++)
			if(resp[i]==2) n--;
		sb.append(getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\r\n");
		sb.append(getString(R.string.stat_solve)+n+"/"+resl+"\r\n");
		sb.append(getString(R.string.ses_mean)+Mi.distime(Mi.sesMean)+" ");
		sb.append("(σ = "+Mi.standDev(Mi.sesSD)+")\r\n");
		sb.append(getString(R.string.ses_avg)+Mi.sesAvg()+"\r\n");
		if(resl >= l1len && Mi.bidx[0] != -1) 
			sb.append((stSel[14]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean)).replace("len", ""+l1len)+Mi.distime(Mi.bavg[0])+"\r\n");
		if(resl >= l2len && Mi.bidx[1] != -1) 
			sb.append((stSel[15]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean)).replace("len", ""+l2len)+Mi.distime(Mi.bavg[1])+"\r\n");
		sb.append(getString(R.string.stat_best)+Mi.distime(Mi.minIdx, false)+"\r\n");
		sb.append(getString(R.string.stat_worst)+Mi.distime(Mi.maxIdx, false)+"\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls)sb.append("\r\n");
		cursor = dbh.query(sesIdx);
		for(int i=0;i<resl;i++) {
			if(!hidls)sb.append("\r\n"+(i+1)+". ");
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
			for(int j=dnf-trim; j<dnf; j++) midx.add(dnfIdx.get(j));
		} else {
			for(int j=n-trim; j<n-dnf; j++) midx.add(idx[j]);
			for(int j=0; j<dnf; j++) midx.add(dnfIdx.get(j));
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
		sb.append(getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\r\n");
		sb.append(getString(R.string.stat_avg)+(m?"DNF":Mi.distime(cavg))+" ");
		sb.append("(σ = "+Mi.standDev(csdv)+")\r\n");
		sb.append(getString(R.string.stat_best)+Mi.distime(min,false)+"\r\n");
		sb.append(getString(R.string.stat_worst)+Mi.distime(max,false)+"\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls)sb.append("\r\n");
		cursor = dbh.query(sesIdx);
		for(int j=i-n+1;j<=i;j++) {
			cursor.moveToPosition(j);
			String s = cursor.getString(6);
			if(!hidls)sb.append("\r\n"+(ind++)+". ");
			if(midx.indexOf(j)>-1)sb.append("(");
			sb.append(Mi.distime(j, false));
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(midx.indexOf(j)>-1)sb.append(")");
			if(hidls && j<i)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[j]);
		}
		return sb.toString();
	}

	private void quickSort(int[] a, int[] idx, int lo, int hi) {
		if(lo >= hi) return;
		int pivot = a[lo], i = lo, j = hi;
		int temp = idx[lo];
		while(i < j) {
			while(i<j && a[j]>=pivot) j--;
			a[i] = a[j];
			idx[i] = idx[j];
			while(i<j && a[i]<=pivot) i++;
			a[j] = a[i];
			idx[j] = idx[i];
		}
		a[i] = pivot;
		idx[i] = temp;
		quickSort(a, idx, lo, i-1);
		quickSort(a, idx, i+1, hi);
	}

	public String mo(int n, int i) {
		StringBuffer sb=new StringBuffer();
		int max, min, dnf=0;
		int cavg=0, csdv=-1, ind=1;
		double sum=0, sum2=0;
		max=min=i-n+1;
		boolean m=false;
		for(int j=i-n+1; j<=i; j++) {
			if(resp[j]!=2 && !m) {min=j; m=true;}
			if(resp[j]==2) {max=j; dnf++;}
		}
		m = dnf > 0;
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
		sb.append(getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\r\n");
		sb.append(getString(R.string.stat_mean)+(m?"DNF":Mi.distime(cavg))+" ");
		sb.append("(σ = "+Mi.standDev(csdv)+")\r\n");
		sb.append(getString(R.string.stat_best)+Mi.distime(min,false)+"\r\n");
		sb.append(getString(R.string.stat_worst)+Mi.distime(max,false)+"\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls)sb.append("\r\n");
		cursor = dbh.query(sesIdx);
		for(int j=i-n+1;j<=i;j++) {
			cursor.moveToPosition(j);
			if(!hidls)sb.append("\r\n"+(ind++)+". ");
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

	private void setBgPic(Bitmap scaleBitmap, int opa) {
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
		if(isMulp) mulp = new int[6][resl+12];
		if(resl != 0) {
			cursor.moveToFirst();
			for(int k=0; k<resl; k++) {
				rest[k] = cursor.getInt(1);
				resp[k] = (byte) cursor.getInt(2);
				if(cursor.getInt(3) == 0) resp[k]=2;
				scrst[k] = cursor.getString(4);
				if(isMulp)
					for(int j=0; j<6; j++)
						mulp[j][k] = cursor.getInt(7+j);
				cursor.moveToNext();
			}
			cursor.moveToLast();
			dbLastId = cursor.getInt(0);
			listLen = isMulp ? (stSel[3]+2)*(resl+1) : resl*3;
		} else {
			listLen = 0;
			dbLastId = 0;
		}
		//cursor.close();
	}

	private void singTime(final int p, final int col) {
		cursor = dbh.query(sesIdx);
		cursor.moveToPosition(p/col);
		final int id = cursor.getInt(0);
		String time=cursor.getString(5);
		String n=cursor.getString(6);
		if(n==null) n="";
		final String comment = n;
		if(time!=null) time="\n("+time+")";
		else time = "";
		LayoutInflater factory = LayoutInflater.from(context);
		final View view = factory.inflate(R.layout.singtime, null);
		final EditText editText=(EditText) view.findViewById(R.id.etnote);
		final TextView tvTime=(TextView) view.findViewById(R.id.st_time);
		final TextView tvScr=(TextView) view.findViewById(R.id.st_scr);
		tvTime.setText(getString(R.string.show_time)+Mi.distime(p/col,true)+time);
		tvScr.setText(scrst[p/col]);
		if(resp[p/col]==2) {
			RadioButton rb = (RadioButton) view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(resp[p/col]==1) {
			RadioButton rb = (RadioButton) view.findViewById(R.id.st_pe2);
			rb.setChecked(true);
		} else {
			RadioButton rb = (RadioButton) view.findViewById(R.id.st_pe1);
			rb.setChecked(true);
		}
		if(!comment.equals("")) {
			editText.setText(comment);
			editText.setSelection(comment.length());
		}
		CustomDialog cdialog =
		new CustomDialog.Builder(context).setContentView(view)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup rg = (RadioGroup) view.findViewById(R.id.st_penalty);
				int rgid = rg.getCheckedRadioButtonId();
				switch(rgid) {
				case R.id.st_pe1: update(p/col, (byte)0); break;
				case R.id.st_pe2: update(p/col, (byte)1); break;
				case R.id.st_pe3: update(p/col, (byte)2); break;
				}
				String text = editText.getText().toString();
				if(!text.equals(comment)) {
					dbh.update(sesIdx, id, text);
					new Thread() {
						public void run() {
							try {
								adapter.refresh(listLen);
								handler.sendEmptyMessage(18);
							} catch (Exception e) { }
						}
					}.start();
				}
				hideKeyboard(editText);
			}
		}).setNeutralButton(R.string.copy_scr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(scrst[p/col]);
				Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
				hideKeyboard(editText);
			}
		}).setNegativeButton(R.string.delete_time, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int which) {
				delete(p/col, col);
				hideKeyboard(editText);
			}
		}).create();
		showDialog(cdialog);
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
		//获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		//去掉标题栏
		//Bitmap bm = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap bm = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bm;
	}

	void savePic(Bitmap b, String strFileName) {
		try {
			FileOutputStream fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {}
	}
	
	private void showKeyboard(final EditText et) {
		new Thread() {
			public void run() {
				try {
					sleep(300);
				} catch (Exception e) { }
				InputMethodManager inm = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				inm.showSoftInput(et, 0);
			}
		}.start();
	}
	
	private void hideKeyboard(EditText et) {
		InputMethodManager inm = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		inm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	private void showDialog(CustomDialog dialog) {
		Window dw = dialog.getWindow();
		WindowManager.LayoutParams p = dw.getAttributes();
		p.width = dip300;
		dw.setAttributes(p);
		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("request " + requestCode);
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
					setBgPic(bitmap, opac);
					bgcolor = false;
					edit.putString("picpath", picPath);
					edit.putBoolean("bgcolor", false); edit.commit();
					c.close();
				} catch (Exception e) {
				} catch (OutOfMemoryError e) {Toast.makeText(context, "Out of memory error: bitmap size exceeds VM budget", Toast.LENGTH_SHORT).show();}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
	        // SSO 授权回调
	        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
	        if (mSsoHandler != null) {
	            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	        }
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent e) {
		float z = e.values[SensorManager.DATA_Z];
		lowZ = 0.8f * lowZ + 0.2f * z;
		float highZ = z - lowZ;
		//testView.setText(String.format("%.1f", highZ));
		if(drop && timer.time > 200 && (highZ-0.1)*20 > (50-sensity) && timer.state == 1) {
			timer.count();
			viewsVisibility(true);
			if(!wca) {isp2=0; idnf=true;}
			confirmTime((int) timer.time);
			timer.state = 0;
			if(!opnl) {releaseWakeLock(); screenOn = false;}
		}
	}
	
	class Scrambler extends Thread {	//TODO
		boolean ch;
		public Scrambler(boolean c) {
			ch = c;
		}
		public void showScramble() {
			if((scrIdx==0 && stSel[6]!=0) ||
					(stSel[5]!=0 && scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)))
				handler.sendEmptyMessage(3);
			else if(scrIdx==8 && scr2idx<3 && stSel[12]>0)
				handler.sendEmptyMessage(1);
			else handler.sendEmptyMessage(0);
		}
		public void run() {
			canScr = false;
			if(scrIdx==-1 && (scr2idx==1 || scr2idx == 15)) {
				threephase.Util.init(handler);
//				handler.sendEmptyMessage(20);
//				time = System.currentTimeMillis();
//				Center1.init(handler);
//				handler.sendEmptyMessage(21);
//				Center2.init(handler);
//				handler.sendEmptyMessage(22);
//				Center3.init(handler);
//				handler.sendEmptyMessage(23);
//				Edge3.init(handler);
//				time = System.currentTimeMillis() - time;
//				System.out.println("init4: "+time);
//				ini4 = true;
			}
			handler.sendEmptyMessage(2);
			if(!ch) {
				if(nextScr==null || isChScr) {
					crntScr = Mi.setScr((scrIdx<<5)|scr2idx, ch);
					isChScr = false;
					nextScr = "";
				} else {
					crntScr = nextScr;
				}
			}
			else {
				crntScr = Mi.setScr((scrIdx<<5)|scr2idx, ch);
			}
			extsol = Mi.sc;
			showScramble();
			canScr=true;
			getNextScr();
		}
		public void getNextScr() {
			System.out.println("get next scramble...");
			isNextScr = false;
			nextScr = Mi.setScr((scrIdx<<5)|scr2idx, ch);
			isNextScr = true;
			System.out.println("next scr: " + nextScr);
			System.out.println("isNextScr "+isNextScr+" nextScrUsed "+nextScrUsed);
			if(nextScrUsed) {
				crntScr = nextScr;
				extsol = Mi.sc;
				showScramble();
				nextScrUsed = false;
				getNextScr();
			}
		}
	}

	/*
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
            	String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(mAccessToken.getExpiresTime()));
                String format = "Token：%1$s \n有效期：%2$s";
                Toast.makeText(context, String.format(format, mAccessToken.getToken(), date), Toast.LENGTH_LONG).show();
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(context, mAccessToken);
                Toast.makeText(context, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
                isLogin = true;
                stSwitch[1].setBackgroundResource(R.drawable.switch_on);
                if(isShare) {
                	WBShareActivity.text = getShareContext();
    				WBShareActivity.bitmap = takeScreenShot(DCTimer.this);
    				Intent it = new Intent(context, WBShareActivity.class);
    				startActivity(it);
                }
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = getString(R.string.auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message += "\nObtained the code: " + code;
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, getString(R.string.auth_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(context, "Auth exception: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
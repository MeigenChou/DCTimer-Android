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
	private TabHost myTabHost;
	private int[] layRes = {R.id.tab_timer, R.id.tab_list, R.id.tab_setting};
	private Button mButtonSst;	//打乱状态
	public TextView tvTimer;
	private static TextView tvScr; //显示打乱
	private Spinner[] spinner=new Spinner[19];
	public static byte[] spSel=new byte[19];
	private ArrayAdapter<String> adapter;
	public boolean wca;
	private boolean wcat, opnl, opnd, hidscr, conft, isShare, isLogin, isMulp, canScr=true;
	public static boolean hidls, timmh, l1am, l2am;
	private static int selold;//,spSel[18];
	public static int[] rest;	//成绩列表
	public static byte[] resp;	//惩罚
	protected static int[][] mulp = null;
	private static long[] multemp=null;
	public static int resl;
	public static String[] scrst;	//打乱列表
	public static String crntScr;	//当前打乱
	private static String nextScr = null;
	private static String extsol;
	private static boolean isNextScr = false;
	private Timer timer;
	private Stackmat stm;
	static int isp2 = 0;
	static boolean idnf = true;
	private static int timk = 0;	//0-无 1-计时中 2-观察中
	public int[] cl = new int[5];
	private ImageView iv;
	private boolean scrt = false, bgcolor, fulls, invs, usess, screenOn, selSes, isLongPress, isChScr;
	static boolean sqshp;
	private String picPath;
	private int dbLastId;
	public static short[] sestp = new short[15];
	private static String[] sesname = new String[15];
	private static int scrType;
	
	private GridView myGridView = null, gvTitle = null;
	private TimesAdapter aryAdapter;
	private String[] times = null;
	private Button seMean, clear, hist;	//时间分布
	private static String slist;
	public static byte[] listnum = {3,5,12,50,100};
	private static char[] srate = {48000,44100,22050,16000,11025,8000};

	private Button bagc, bgpic, txtc, orbc, orwc, avbc, reset, rsauth;
	private Button csCube, csPyrm, csSQ1, sesMang;
	private ColorPicker dialog;
	private SeekBar skb1, skb2, skb3, skb4, skb5;
	private int ttsize, stsize, intv, insType;
	private TextView[] stt = new TextView[38];
	private int sttlen=stt.length;
	private TextView tvl;
	private CheckBox[] chkb = new CheckBox[15];
	protected static SharedPreferences share;
	protected static SharedPreferences.Editor edit;
	private DBHelper dbh;
	private Cursor cursor;
	
	protected boolean canStart;
	protected int tapTime;
	private String[] mItems;
	private Bitmap bitmap;
	private PowerManager.WakeLock wakeLock = null;
	private DisplayMetrics dm;
	private static String addstr="/data/data/com.dctimer/databases/main.png";
	private static final String CONSUMER_KEY = "3318942954";// 替换为开发者的appkey，例如"1646212960";
	private static final String CONSUMER_SECRET = "77d13c80e4a9861e4e7c497968c5d4e5";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";
	private int mulpCount;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ProgressDialog proDlg = null;
	private static String outPath;
	private static ArrayList<String> inScr = null;
	private static int inScrLen;
	protected static boolean isInScr = false;
	private long exitTime = 0;
	
	private List<String> items = null, paths = null;
	private ListView listView;
	private String selFilePath;
	private Vibrator vibrator;
	private long[] vibTime = new long[]{30, 50, 80, 150, 240};
	private int[] scrOri = new int[]{2, 0, 8, 1, 4};
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int msw = msg.what;
			switch(msw){
			case 0: tvScr.setText(crntScr); break;
			case 1:
				tvScr.setText(crntScr+"\n\n"+getResources().getString(R.string.shape)+extsol);
				break;
			case 2:
				tvScr.setText(getResources().getString(R.string.scrambling)); break;
			case 3: tvScr.setText(crntScr+extsol); break;
			case 4: tvTimer.setText(Mi.distime((int)timer.time)); break;
			case 5: tvTimer.setText("IMPORT"); break;
			case 6: tvTimer.setText(spSel[6]==0?"0.00":"0.000"); break;
			case 7: Toast.makeText(DCTimer.this, getResources().getString(R.string.outscr_success), Toast.LENGTH_SHORT).show(); break;
			case 8: tvScr.setText(crntScr+"\n\n"+getResources().getString(R.string.solving)); break;
			case 9: Toast.makeText(DCTimer.this, getResources().getString(R.string.outscr_failed), Toast.LENGTH_SHORT).show(); break;
			default: proDlg.setMessage(msw%100+"/"+msw/100); break;
			}
		}
	};
	
	class TitleAdapter extends BaseAdapter {
		private Context context;
		private String[] times;
		private TextView tv;
		private int cl;
		public TitleAdapter(Context context, String[] times, int cl) {
			this.context=context;
			this.times=times;
			this.cl = cl;
		}
		public int getCount() {
			if(times!=null)return times.length;
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
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(!bgcolor){
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				bitmap=BitmapFactory.decodeFile(picPath);
				bitmap=getBgPic(bitmap);
				setBgPic(bitmap, share.getInt("opac", 35));
				setGridView(false);
			}
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			if(!bgcolor){
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				bitmap=BitmapFactory.decodeFile(picPath);
				bitmap=getBgPic(bitmap);
				setBgPic(bitmap, share.getInt("opac", 35));
				setGridView(false);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.tab);
		context=this;
		share = super.getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
		selold=spSel[0]=(byte) share.getInt("sel", 1);	//打乱种类
		//spSel[18]=share.getInt("sel2", 0);
		cl[0]=share.getInt("cl0", Color.rgb(102, 204, 255));
		cl[1]=share.getInt("cl1", Color.BLACK);
		cl[2]=share.getInt("cl2", Color.rgb(255, 0, 255));
		cl[3]=share.getInt("cl3", Color.RED);
		cl[4]=share.getInt("cl4", Color.rgb(0, 153, 0));
		wca=share.getBoolean("wca", false);
		wcat=wca;
		hidscr=share.getBoolean("hidscr", true);
		hidls=share.getBoolean("hidls", false);
		conft=share.getBoolean("conft", true);
		l1am=share.getBoolean("l1am", true);
		l2am=share.getBoolean("l2am", true);
		spSel[1]=(byte) share.getInt("cxe", 0);
		ttsize=share.getInt("ttsize", 60);
		stsize=share.getInt("stsize", 18);
		if(share.getBoolean("mnxc", true))spSel[3]=1;
		else spSel[3]=0;	//五魔配色
		spSel[4]=(byte) share.getInt("list1", 1);
		spSel[5]=(byte) share.getInt("list2", 1);
		timmh=share.getBoolean("timmh", true);
		if(share.getBoolean("prec", true))spSel[6]=1;
		else spSel[6]=0;	//精度设置
		spSel[7]=(byte)share.getInt("tiway", 0);	//计时方式
		spSel[8]=(byte)share.getInt("group", 0);	//分组
		spSel[9]=(byte)share.getInt("cface", 0);	//十字计算底面
		spSel[10]=(byte)share.getInt("cside", 1);	//以及颜色
		spSel[11]=(byte)share.getInt("srate", 1);	//采样频率
		Stackmat.samplingRate=srate[spSel[11]];
		spSel[2]=(byte)share.getInt("cube2l", 0);	//二阶底层求解
		spSel[12]=(byte)share.getInt("tfont", 3);	//计时器字体
		spSel[13]=(byte)share.getInt("mulp", 0);
		spSel[14]=(byte)share.getInt("vibra", 0);	//震动反馈
		spSel[15]=(byte)share.getInt("vibtime", 2);	//震动时长
		spSel[16]=(byte)share.getInt("timerupd", 0);	//计时器更新
		spSel[17]=(byte)share.getInt("screenori", 0);	//屏幕方向
		spSel[18]=(byte)share.getInt("sel2", 0);	//二级打乱
		bgcolor=share.getBoolean("bgcolor", true);
		sqshp=share.getBoolean("sqshp", false);	//SQ1复形计算
		fulls=share.getBoolean("fulls", false);	//全屏显示
		usess=share.getBoolean("usess", false);
		invs=share.getBoolean("invs", false);	//反转信号
		opnl=share.getBoolean("scron", false);
		opnd=share.getBoolean("scrgry", true);
		selSes=share.getBoolean("selses", false);
		Stackmat.inv=invs;
		picPath=share.getString("picpath", null);
		tapTime=share.getInt("tapt", 0);
		isMulp=share.getBoolean("ismulp", false);
		intv=share.getInt("intv", 30);
		outPath=share.getString("scrpath", "/sdcard/DCTimer/");
		edit = share.edit();
		for(int i=0; i<15; i++){
			sestp[i]=(short) share.getInt("sestp"+i, -1);
			sesname[i]=share.getString("sesname"+i, "");
		}
		long sestype = share.getLong("sestype", -1);
		if(sestype!=-1){
			for(int i=0; i<9; i++){
				int temp = Mi.getSessionType(sestype, i);
				if(temp!=0x7f){
					edit.putInt("sestp"+i, temp);
				}
			}
			edit.remove("sestype");
			edit.commit();
		}
		
		if(fulls)getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(opnl){acquireWakeLock();screenOn=true;}
		mItems=getResources().getStringArray(R.array.tabInd);
		myTabHost = (TabHost) super.findViewById(R.id.tabhost);	//取得TabHost对象
		myTabHost.setup();	//建立TabHost对象
		for (int x = 0; x < this.layRes.length; x++) {	//循环取出所有布局标记
			TabSpec myTab = myTabHost.newTabSpec("tab" + x);	//定义TabSpec
			if(x==0)myTab.setIndicator(mItems[x],getResources().getDrawable(R.drawable.img1));
			else if(x==1)myTab.setIndicator(mItems[x],getResources().getDrawable(R.drawable.img2));
			else myTab.setIndicator(mItems[x],getResources().getDrawable(R.drawable.img3));
			myTab.setContent(this.layRes[x]);	//设置显示的组件
			myTabHost.addTab(myTab);	//增加标签
		}
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(bgcolor)myTabHost.setBackgroundColor(cl[0]);
		else {
			try{
				bitmap=BitmapFactory.decodeFile(picPath);
				bitmap=getBgPic(bitmap);
				setBgPic(bitmap, share.getInt("opac", 35));
			} catch (Exception e) {
				myTabHost.setBackgroundColor(cl[0]);
				Toast.makeText(DCTimer.this, getResources().getString(R.string.not_exist), Toast.LENGTH_SHORT).show();
			}
		}
		myTabHost.setCurrentTab(0);	// 设置开始索引
		
		tvScr = (TextView) findViewById(R.id.myTextView1);
		tvTimer = (TextView)findViewById(R.id.myTextView2);
		//mButtonScr = (Button) findViewById(R.id.myButtonScr);
		mButtonSst = (Button) findViewById(R.id.myButtonSst);
		for(int i=0;i<18;i++){
			switch(i){
			case 0:
				mItems=getResources().getStringArray(R.array.cubeStr);
				spinner[i]=(Spinner) findViewById(R.id.mySpinner);break;
			case 1:
				mItems=getResources().getStringArray(R.array.crsStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner);break;
			case 2:
				mItems=getResources().getStringArray(R.array.c2lStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner12);break;
			case 3:
				mItems=getResources().getStringArray(R.array.mncStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner3);break;
			case 4:
				mItems=getResources().getStringArray(R.array.list1Str);
				spinner[i]=(Spinner) findViewById(R.id.spinner4);break;
			case 5:
				mItems=getResources().getStringArray(R.array.list2Str);
				spinner[i]=(Spinner) findViewById(R.id.spinner5);break;
			case 6:
				mItems=getResources().getStringArray(R.array.preStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner6);break;
			case 7:
				mItems=getResources().getStringArray(R.array.tiwStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner7);break;
			case 8:
				mItems=new String[15];
				for(int j=0; j<15; j++)
					mItems[j]=j+1+(sesname[j].equals("")?"　":": "+sesname[j]);
				spinner[i]=(Spinner) findViewById(R.id.spinner8);break;
			case 9:
				mItems=getResources().getStringArray(R.array.faceStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner9);break;
			case 10:
				mItems=getResources().getStringArray(R.array.sideStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner10);break;
			case 11:
				mItems=getResources().getStringArray(R.array.samprate);
				spinner[i]=(Spinner) findViewById(R.id.spinner11);break;
			case 12:
				mItems=getResources().getStringArray(R.array.fontStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner13);break;
			case 13:
				mItems=getResources().getStringArray(R.array.mulpStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner14);break;
			case 14:
				mItems=getResources().getStringArray(R.array.vibraStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner15);break;
			case 15:
				mItems=getResources().getStringArray(R.array.vibTimeStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner16);break;
			case 16:
				mItems=getResources().getStringArray(R.array.tupdStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner17);break;
			case 17:
				mItems=getResources().getStringArray(R.array.soriStr);
				spinner[i]=(Spinner) findViewById(R.id.spinner18);break;
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner[i].setAdapter(adapter);
			spinner[i].setSelection(spSel[i]);
		}
		
		if(spSel[1]==0){spinner[9].setEnabled(false);spinner[10].setEnabled(false);}
		if(spSel[14]==0) spinner[15].setEnabled(false);
		spinner[18] = (Spinner) findViewById(R.id.sndscr);
		//spinner[18] = (Button) findViewById(R.id.spinner[18]);
		set2ndsel();
		tvScr.setTextSize(stsize);
		tvTimer.setTextSize(ttsize);
		switch(spSel[12]){
		case 0:
			tvTimer.setTypeface(Typeface.create("monospace", 0));break;
		case 1:
			tvTimer.setTypeface(Typeface.create("serif", 0));break;
		case 2:
			tvTimer.setTypeface(Typeface.create("sans-serif", 0));break;
		case 3:
			tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
			break;
		case 4:
			tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
			break;
		case 5:
			tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
			break;
		}
		if(spSel[17]>0)
			this.setRequestedOrientation(scrOri[spSel[17]]);
		
		stm=new Stackmat(this);
		if(usess){
			spinner[11].setEnabled(false);
			tvTimer.setText("OFF");
			if(stm.creatAudioRecord((int)srate[spSel[11]]));
			else {
				spinner[11].setSelection(1);
				spSel[11]=1;
				edit.putInt("srate", 1);
				edit.commit();
			}
			stm.start();
			spinner[7].setEnabled(false);
		} else {
			if(spSel[7]==0 && spSel[6]==0)tvTimer.setText("0.00");
			else if(spSel[7]==1)tvTimer.setText("IMPORT");
		}
		
		timer = new Timer(this);
		dbh = new DBHelper(this);

		myGridView=(GridView)findViewById(R.id.myGridView);
		gvTitle=(GridView)findViewById(R.id.gv_title);
		seMean=(Button)findViewById(R.id.mButtonoa);
		clear=(Button)findViewById(R.id.mButtonClr);
		hist=(Button)findViewById(R.id.mButtonHist);
		bagc = (Button) findViewById(R.id.selbagc);
		bgpic = (Button) findViewById(R.id.selbgpic);
		txtc=(Button)findViewById(R.id.seltxtc);
		orbc=(Button)findViewById(R.id.selorbc);
		orwc=(Button)findViewById(R.id.selorwc);
		avbc=(Button)findViewById(R.id.selavbc);
		rsauth=(Button)findViewById(R.id.auth_sina);
		csCube=(Button)findViewById(R.id.btn_csn);
		csPyrm=(Button)findViewById(R.id.btn_csp);
		csSQ1=(Button)findViewById(R.id.btn_csq);
		sesMang=(Button)findViewById(R.id.btn_ses);
		chkb[0]=(CheckBox)findViewById(R.id.check1);
		chkb[1]=(CheckBox)findViewById(R.id.check2);
		chkb[2]=(CheckBox)findViewById(R.id.check3);
		chkb[3]=(CheckBox)findViewById(R.id.check4);
		chkb[4]=(CheckBox)findViewById(R.id.check5);
		chkb[5]=(CheckBox)findViewById(R.id.lcheck1);
		chkb[6]=(CheckBox)findViewById(R.id.lcheck2);
		chkb[7]=(CheckBox)findViewById(R.id.check6);
		chkb[8]=(CheckBox)findViewById(R.id.check7);
		chkb[9]=(CheckBox)findViewById(R.id.check8);
		chkb[10]=(CheckBox)findViewById(R.id.check9);
		chkb[11]=(CheckBox)findViewById(R.id.check10);
		chkb[12]=(CheckBox)findViewById(R.id.check11);
		chkb[13]=(CheckBox)findViewById(R.id.check12);
		chkb[14]=(CheckBox)findViewById(R.id.check13);
		
		skb1=(SeekBar)findViewById(R.id.seekb1);
		skb2=(SeekBar)findViewById(R.id.seekb2);
		skb3=(SeekBar)findViewById(R.id.seekb3);
		skb4=(SeekBar)findViewById(R.id.seekb4);
		skb5=(SeekBar)findViewById(R.id.seekb5);
		tvl=(TextView) findViewById(R.id.tv4);
		stt[0]=(TextView) findViewById(R.id.stt00);
		stt[1]=(TextView) findViewById(R.id.stt01);
		stt[2]=(TextView) findViewById(R.id.stt02);
		stt[3]=(TextView) findViewById(R.id.stt08);
		stt[4]=(TextView) findViewById(R.id.stt09);
		stt[5]=(TextView) findViewById(R.id.stt05);
		stt[6]=(TextView) findViewById(R.id.stt21);
		stt[7]=(TextView) findViewById(R.id.stt07);
		stt[8]=(TextView) findViewById(R.id.stt03);
		stt[9]=(TextView) findViewById(R.id.stt22);
		stt[10]=(TextView) findViewById(R.id.stt17);
		stt[11]=(TextView) findViewById(R.id.stt11);
		stt[12]=(TextView) findViewById(R.id.stt12);
		stt[13]=(TextView) findViewById(R.id.stt13);
		stt[14]=(TextView) findViewById(R.id.stt14);
		stt[15]=(TextView) findViewById(R.id.stt15);
		stt[16]=(TextView) findViewById(R.id.stt16);
		stt[17]=(TextView) findViewById(R.id.stt10);
		stt[18]=(TextView) findViewById(R.id.stt18);
		stt[19]=(TextView) findViewById(R.id.stt19);
		stt[20]=(TextView) findViewById(R.id.stt23);
		stt[21]=(TextView) findViewById(R.id.stt04);
		stt[22]=(TextView) findViewById(R.id.stt06);
		stt[23]=(TextView) findViewById(R.id.stt20);
		stt[24]=(TextView) findViewById(R.id.stt24);
		stt[25]=(TextView) findViewById(R.id.stt25);
		stt[26]=(TextView) findViewById(R.id.stt26);
		stt[27]=(TextView) findViewById(R.id.stt27);
		stt[28]=(TextView) findViewById(R.id.stt28);
		stt[29]=(TextView) findViewById(R.id.stt29);
		stt[30]=(TextView) findViewById(R.id.stt30);
		stt[31]=(TextView) findViewById(R.id.stt31);
		stt[32]=(TextView) findViewById(R.id.stt32);
		stt[33]=(TextView) findViewById(R.id.stt33);
		stt[34]=(TextView) findViewById(R.id.stt34);
		stt[35]=(TextView) findViewById(R.id.stt35);
		stt[36]=(TextView) findViewById(R.id.stt36);
		stt[37]=(TextView) findViewById(R.id.stt37);
		reset=(Button)findViewById(R.id.reset);
		
		skb1.setMax(95);
		stt[3].setText(getResources().getString(R.string.timer_size)+share.getInt("ttsize", 60));
		skb1.setProgress(share.getInt("ttsize", 60)-50);
		skb1.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		skb2.setMax(25);
		stt[4].setText(getResources().getString(R.string.scrsize)+share.getInt("stsize", 18));
		skb2.setProgress(share.getInt("stsize", 18)-12);
		skb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		skb3.setMax(41);
		skb3.setProgress(share.getInt("intv", 30)-20);
		stt[10].setText(getResources().getString(R.string.row_spacing)+share.getInt("intv", 30));
		skb3.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		skb4.setProgress(share.getInt("opac", 35));
		skb4.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		skb5.setMax(20);
		skb5.setProgress(tapTime);
		stt[31].setText(getResources().getString(R.string.time_tap)+tapTime/20D);
		skb5.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
		if(wca)chkb[0].setChecked(true);
		if(opnl)chkb[1].setChecked(true);
		if(hidscr)chkb[2].setChecked(true);
		if(conft)chkb[3].setChecked(true);
		if(!hidls)chkb[4].setChecked(true);
		if(l1am)chkb[5].setChecked(true);
		if(l2am)chkb[6].setChecked(true);
		if(timmh)chkb[7].setChecked(true);
		if(sqshp)chkb[8].setChecked(true);
		if(fulls)chkb[9].setChecked(true);
		if(invs)chkb[10].setChecked(true);
		if(opnd)chkb[14].setChecked(true);
		getSession(spSel[8]);
		seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
		setGvTitle();
		if(isMulp){
			chkb[13].setChecked(true);
			multemp = new long[7];
		}
		else {
			spinner[13].setEnabled(false);
		}
		setGridView(true);
		
		if(usess){
			chkb[11].setChecked(true);
			if(!stm.isStart)stm.start();
		}
		if(selSes)chkb[12].setChecked(true);
		for(int i=0;i<15;i++)
		chkb[i].setOnCheckedChangeListener(listener);
		
		String token=share.getString("token", null);
		String expires_in=share.getString("expin", null);
		if(token==null || expires_in==null ||
				(System.currentTimeMillis()-share.getLong("totime", 0))/1000>=Integer.parseInt(expires_in)) {
			isLogin=false;
			rsauth.setText(getResources().getString(R.string.login));
		} else {
			isLogin=true;
			rsauth.setText(getResources().getString(R.string.logout));
		}
		
		tvl.setTextColor(cl[1]);
		for(int i=0;i<sttlen;i++)stt[i].setTextColor(cl[1]);
		for(int i=0;i<15;i++)chkb[i].setTextColor(cl[1]);
		tvScr.setTextColor(cl[1]);
		tvTimer.setTextColor(cl[1]);
		
		vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		
		//打乱类型
		spinner[0].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[0]=(byte) arg2;
				if(spSel[0]!=selold){
					spSel[18]=0;selold=spSel[0];
				}
				set2ndsel();
				setScrType();
				newScr(true);
				if(selSes)searchSesType();
				if(inScr!=null && inScr.size()!=0)inScr=null;
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		spinner[18].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[18]!=arg2) {
					spSel[18]=(byte) arg2;
					//set2ndsel();
					setScrType();
					newScr(true);
					if(selSes)searchSesType();
					if(inScr!=null && inScr.size()!=0)inScr=null;
				}
//				final String[] s=set2ndsel();
//				new AlertDialog.Builder(DCTimer.this).setItems(s, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						if(spSel[18]!=which){
//							spSel[18]=(byte) which;
//							setScrType();
//							newScr(true);
//							if(selSes)searchSesType();
//							if(inScr!=null && inScr.size()!=0)inScr=null;
//						}
//					}
//				}).show();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//3阶求解
		spinner[1].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[1]!=arg2) {
					spSel[1]=(byte) arg2;
					edit.putInt("cxe", spSel[1]);
					edit.commit();
					if(spSel[0]==1 && (spSel[18]==0 || spSel[18]==1 || spSel[18]==5 || spSel[18]==19)) {
						if(spSel[1]==0)tvScr.setText(crntScr);
						else new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								switch(spSel[1]) {
								case 1: extsol="\n"+Cross.cross(crntScr, spSel[9], spSel[10]); break;
								case 2: extsol="\n"+Cross.xcross(crntScr, spSel[10]); break;
								case 3: extsol="\n"+EOline.eoLine(crntScr, spSel[10]); break;
								case 4: extsol="\n"+PetrusxRoux.roux(crntScr, spSel[10]); break;
								case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, spSel[10]); break;
								}
								handler.sendEmptyMessage(3);
								isNextScr=false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[18], false);
								isNextScr = true;
							}
						}.start();
					}
				}
				if(spSel[1]==0){spinner[9].setEnabled(false);spinner[10].setEnabled(false);}
				else if(spSel[1]==1){spinner[9].setEnabled(true);spinner[10].setEnabled(true);}
				else {spinner[9].setEnabled(false);spinner[10].setEnabled(true);}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//五魔配色
		spinner[3].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[3]=(byte)arg2;
				if(spSel[3]==0)edit.putBoolean("mnxc", false);
				else edit.putBoolean("mnxc", true);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//滚动平均0
		spinner[4].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[4]!=arg2) {
					spSel[4]=(byte)arg2;
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
		spinner[5].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[5]!=arg2) {
					spSel[5]=(byte)arg2;
					if(!isMulp) {
						setGvTitle();
						setGridView(false);
					}
					edit.putInt("list2", spSel[5]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		//精度设置
		spinner[6].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[6]=(byte)arg2;
				if(spSel[6]==0){edit.putBoolean("prec", false);if(spSel[7]==0)tvTimer.setText("0.00");}
				else {edit.putBoolean("prec", true);if(spSel[7]==0)tvTimer.setText("0.000");}
				edit.commit();
				if(resl!=0){
					setGridView(false);
					seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//计时方式
		spinner[7].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[7]=(byte)arg2;
				if(spSel[7]==0){
					if(spSel[6]==0)tvTimer.setText("0.00");
					else tvTimer.setText("0.000");
				} else if(spSel[7]==1){
					if(timk==1){
						timer.count();
						new Thread(new Runnable(){
							public void run(){
								try {
									Thread.sleep(25);
								} catch (InterruptedException e) {e.printStackTrace();}
								handler.sendEmptyMessage(5);
							}
						}).start();
						viewsVisibility(true);
						if(isMulp)spinner[13].setEnabled(true);
						wca=wcat;
						timk=0;
						if(!opnl){releaseWakeLock();screenOn=false;}
					} else if(timk==2){
						timer.stopi();
						new Thread(new Runnable(){
							public void run(){
								try {
									Thread.sleep(25);
								} catch (InterruptedException e) {e.printStackTrace();}
								handler.sendEmptyMessage(5);
							}
						}).start();
						viewsVisibility(true);
						if(isMulp)spinner[13].setEnabled(true);
						wca=wcat;
						timk=0;
						if(!opnl){releaseWakeLock();screenOn=false;}
					} else tvTimer.setText("IMPORT");
				}
				edit.putInt("tiway", spSel[7]);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//分组
		spinner[8].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[8]!=arg2) {
					spSel[8]=(byte)arg2;
					getSession(arg2);
					seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
					setGridView(true);
					edit.putInt("group", spSel[8]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//十字底面
		spinner[9].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[9]!=arg2) {
					spSel[9]=(byte)arg2;
					edit.putInt("cface", spSel[9]);
					edit.commit();
					if(spSel[0]==1 && (spSel[18]==0 || spSel[18]==1 || spSel[18]==5 || spSel[18]==19))
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								extsol="\n"+Cross.cross(crntScr, spSel[9], spSel[10]);
								handler.sendEmptyMessage(3);
								isNextScr=false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[18], false);
								isNextScr = true;
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//颜色
		spinner[10].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[10]!=arg2) {
					spSel[10]=(byte)arg2;
					edit.putInt("cside", spSel[10]);
					edit.commit();
					if(spSel[0]==1 && (spSel[18]==0 || spSel[18]==1 || spSel[18]==5 || spSel[18]==19))
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								switch(spSel[1]) {
								case 1: extsol="\n"+Cross.cross(crntScr, spSel[9], spSel[10]); break;
								case 2: extsol="\n"+Cross.xcross(crntScr, spSel[10]); break;
								case 3: extsol="\n"+EOline.eoLine(crntScr, spSel[10]); break;
								case 4: extsol="\n"+PetrusxRoux.roux(crntScr, spSel[10]); break;
								case 5: extsol="\n"+PetrusxRoux.petrus(crntScr, spSel[10]); break;
								}
								handler.sendEmptyMessage(3);
								isNextScr=false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[18], false);
								isNextScr = true;
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//采样频率
		spinner[11].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spSel[11]=(byte)arg2;
				if(stm.creatAudioRecord((int)srate[spSel[11]]));
				else {
					spinner[11].setSelection(1);
					spSel[11]=1;
					edit.putInt("srate", 1);
					Toast.makeText(DCTimer.this, getResources().getString(R.string.sr_not_support), Toast.LENGTH_SHORT).show();
				}
				edit.putInt("srate", spSel[11]);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//二阶底层
		spinner[2].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[2]!=arg2) {
					spSel[2]=(byte)arg2;
					edit.putInt("cube2l", spSel[2]);
					edit.commit();
					if(spSel[0]==0) {
						if(spSel[2]==0)tvScr.setText(crntScr);
						else new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								extsol = "\n"+Cube2layer.cube2layer(crntScr, spSel[2]);
								handler.sendEmptyMessage(3);
								isNextScr=false;
								nextScr = Mi.SetScr((spSel[0]<<5)|spSel[18], false);
								isNextScr = true;
							}
						}.start();
					}
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//计时器字体
		spinner[12].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[12]!=arg2) {
					spSel[12]=(byte)arg2;
					switch(spSel[12]){
					case 0:
						tvTimer.setTypeface(Typeface.create("monospace", 0));break;
					case 1:
						tvTimer.setTypeface(Typeface.create("serif", 0));break;
					case 2:
						tvTimer.setTypeface(Typeface.create("sans-serif", 0));break;
					case 3:
						tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
						break;
					case 4:
						tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
						break;
					case 5:
						tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
						break;
					}
					edit.putInt("tfont", spSel[12]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//分段计时
		spinner[13].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[13]!=arg2) {
					spSel[13]=(byte)arg2;
					setGvTitle();
					if(resl!=0) {
						times = new String[(arg2+3)*(resl+1)];
					} else times = null;
					setGridView(false);
					edit.putInt("mulp", arg2);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		//触感反馈
		spinner[14].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[14]!=arg2) {
					spSel[14]=(byte)arg2;
					if(arg2!=0)spinner[15].setEnabled(true);
					else spinner[15].setEnabled(false);
					edit.putInt("vibra", arg2);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		//触感反馈时间
		spinner[15].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[15]!=arg2) {
					spSel[15]=(byte)arg2;
					edit.putInt("vibtime", arg2);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		//计时器更新
		spinner[16].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[16]!=arg2) {
					spSel[16]=(byte)arg2;
					edit.putInt("timerupd", arg2);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		spinner[17].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(spSel[17]!=arg2) {
					spSel[17]=(byte)arg2;
					DCTimer.this.setRequestedOrientation(scrOri[arg2]);
					edit.putInt("screenori", arg2);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		//打乱状态
		mButtonSst.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(Mi.viewType>0){
					int width = dm.widthPixels;
					int height=dm.heightPixels;
					width=(int) (width*0.9);
					if(width*0.75>(height-30))width=height-30;
					LayoutInflater inflater = LayoutInflater.from(DCTimer.this);	// 取得LayoutInflater对象
					final View popView = inflater.inflate(R.layout.popwindow, null);	// 读取布局管理器
					popView.setBackgroundColor(0xaaece9d8);
					iv=(ImageView) popView.findViewById(R.id.ImageView1);
					Bitmap bm=Bitmap.createBitmap(width+7, (int)(width*0.75)+7, Config.ARGB_8888);
					Canvas c=new Canvas(bm);
					c.drawColor(Color.TRANSPARENT);
					Paint p=new Paint();
					p.setAntiAlias(true);
					Mi.drawScr(spSel[18], width, p, c);
					iv.setImageBitmap(bm);
					new AlertDialog.Builder(DCTimer.this)
					.setView(popView).setNegativeButton(getResources().getString(R.string.btn_close), null).show();
				} else Toast.makeText(DCTimer.this, getResources().getString(R.string.not_support), Toast.LENGTH_SHORT).show();
			}
		});
		//打乱
		tvScr.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				scrt=true;
				setTouch(event.getAction());
				return timk!=0;
			}
		});
		tvScr.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {	//TODO
				if(timk==0) {
					isLongPress = true;
					LayoutInflater factory = LayoutInflater.from(DCTimer.this);
					final View view = factory.inflate(R.layout.scr_layout, null);
					final EditText editText=(EditText)view.findViewById(R.id.etslen);
					final TextView tvScr=(TextView)view.findViewById(R.id.cnt_scr);
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
									if((spSel[0]==1 && spSel[18]==19) || (spSel[0]==20 && spSel[18]==4)) isChScr = true;
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
			public boolean onTouch(View v, MotionEvent event){
				scrt=false;
				if(!usess){
					if(spSel[7]==0)setTouch(event.getAction());
					else if(spSel[7]==1)inputTime(event.getAction());
				}
				return true;
			}
		});
		myGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int p, long arg3) {
				if(isMulp) {
					if(p/(spSel[13]+3)<resl && p%(spSel[13]+3)==0) singTime(p, spSel[13]+3);
				}
				else if(p%3==0)
					singTime(p, 3);
				else if(p%3==1 && p/3>listnum[spSel[4]]-2)
					showAlertDialog(1, p/3);
				else if(p%3==2 && p/3>listnum[spSel[5]+1]-2)
					showAlertDialog(2, p/3);
			}
		});
		//分组平均
		seMean.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				boolean m=false;
				for(int i=0;i<resl;i++)
					if(resp[i]!=2){m=true;break;}
				if(m)showAlertDialog(3, 0);
			}
		});
		//清空成绩
		clear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
				else {
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_clear_session))
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j){
							dbh.clear(spSel[8]);
							resl=dbLastId=0;
							times=null;
							seMean.setText(getResources().getString(R.string.session_average)+"0/0): N/A (N/A)");
							Mi.smax=Mi.smin=-1;
							setGridView(false);
							if(sestp[spSel[8]]!=-1){
								sestp[spSel[8]]=-1;
								edit.remove("sestp"+spSel[8]);
								edit.commit();
							}
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
				}
			}
		});
		//时间分布
		hist.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int width = dm.widthPixels;
				int height = dm.heightPixels;
				if(height<width)width=height;
				width=(int) (width*0.9);
				LayoutInflater inflater = LayoutInflater.from(DCTimer.this);
				final View popView = inflater.inflate(R.layout.popwindow, null);
				popView.setBackgroundColor(0xddf0f0f0);
				iv=(ImageView) popView.findViewById(R.id.ImageView1);
				Bitmap bm=Bitmap.createBitmap(width, (int)(width*1.2), Config.ARGB_8888);
				Canvas c=new Canvas(bm);
				c.drawColor(Color.TRANSPARENT);
				Paint p=new Paint();
				p.setAntiAlias(true);
				Mi.drawHist(width, p, c);
				iv.setImageBitmap(bm);
				new AlertDialog.Builder(DCTimer.this)
				.setView(popView).setNegativeButton(getResources().getString(R.string.btn_close), null).show();
			}
		});
		//恢复默认设置
		reset.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_reset))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j){
						//TODO
						if(timk==0) wca=false;wcat=false;hidscr=true;conft=true;
						timmh=true;hidls=false;l1am=true;l2am=true;bgcolor=true;
						sqshp=false;fulls=false;invs=false;usess=false;opnl=false;
						Stackmat.inv=false;
						spSel[1]=0;spSel[2]=0;spSel[3]=1;spSel[4]=1;spSel[5]=1;
						spSel[6]=1;spSel[7]=0;spSel[11]=1;spSel[12]=3;spSel[13]=0;
						spSel[14]=0;spSel[15]=2;spSel[16]=0;spSel[17]=0;
						tvTimer.setTextSize(60);tvScr.setTextSize(18);
						cl[0]=Color.rgb(102, 204, 255);cl[1]=Color.BLACK;
						cl[2]=Color.rgb(255, 0, 255);cl[3]=Color.RED;
						cl[4]=Color.rgb(0, 153, 0);
						for(int i=0;i<3;i++)chkb[i].setChecked(false);
						for(int i=3;i<8;i++)chkb[i].setChecked(true);
						for(int i=8;i<14;i++)chkb[i].setChecked(false);
						chkb[14].setChecked(true);
						for(int i=0;i<8;i++)spinner[i].setSelection(spSel[i]);
						for(int i=11;i<18;i++)spinner[i].setSelection(1);
						skb1.setProgress(10);
						skb2.setProgress(6);
						skb3.setProgress(10);
						skb4.setProgress(35);
						myTabHost.setBackgroundColor(cl[0]);
						tvl.setTextColor(cl[1]);
						for(int i=0;i<sttlen;i++)stt[i].setTextColor(cl[1]);
						for(int i=0;i<13;i++)chkb[i].setTextColor(cl[1]);
						tvScr.setTextColor(cl[1]);
						tvTimer.setTextColor(cl[1]);
						intv=30;
						setGridView(false);
						releaseWakeLock();
						screenOn=false;
						edit.remove("wca");
						edit.putBoolean("hidscr", true);
						edit.remove("mclr");
						edit.putBoolean("mnxc", true);
						edit.remove("conft");
						edit.remove("hidls");
						edit.remove("l1am");
						edit.remove("l2am");
						edit.remove("invs");
						edit.remove("prom");
						edit.putBoolean("prec", true);
						edit.remove("bgcolor");
						edit.remove("sqshp");
						edit.remove("fulls");
						edit.remove("usess");
						edit.remove("scron");
						edit.putBoolean("timmh", true);
						edit.remove("ttsize");
						edit.remove("stsize");
						edit.remove("cl0");
						edit.remove("cl1");
						edit.remove("cl2");
						edit.remove("cl3");
						edit.remove("cl4");
						edit.putInt("cxe", 0);
						edit.remove("intv");
						edit.remove("opac");
						edit.putInt("list1", 1);
						edit.putInt("list2", 1);
						edit.putInt("tiway", 0);
						edit.putInt("srate", 1);
						edit.remove("tfont");
						edit.remove("mulp");
						edit.remove("vibra");
						edit.remove("vibtime");
						edit.remove("timerupd");
						edit.remove("screenori");
						edit.remove("scrgry");
						edit.commit();
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i){ }
				}).show();
			}
		});
		//背景颜色
		bagc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[0], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						myTabHost.setBackgroundColor(color);cl[0]=color;bgcolor=true;
						edit.putInt("cl0", color);edit.putBoolean("bgcolor", true);
						edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
		//背景图片
		bgpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");	//开启Pictures画面Type设定为image
				intent.setAction(Intent.ACTION_GET_CONTENT);//使用Intent.ACTION_GET_CONTENT这个Action
				startActivityForResult(intent, 1);//取得相片后返回本画面
			}
		});
		//文字颜色
		txtc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[1], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						tvl.setTextColor(color);
						for(int i=0;i<sttlen;i++)stt[i].setTextColor(color);
						for(int i=0;i<13;i++)chkb[i].setTextColor(color);
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
			}
		});
		//最快单次颜色
		orbc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
			}
		});
		//最慢单次颜色
		orwc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
			}
		});
		//最快平均颜色
		avbc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i){ }
					}).show();
				else {
					isShare=false;
					auth();
				}
			}
		});
		//配色NxN
		csCube.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] colors={share.getInt("csn1", 0xffffff00), share.getInt("csn2", 0xff0000ff), share.getInt("csn3", 0xffff0000),
						share.getInt("csn4", 0xffffffff), share.getInt("csn5", 0xff009900), share.getInt("csn6", 0xffff8026)};
				ColorScheme dialog = new ColorScheme(context, 1, colors, new ColorScheme.OnSchemeChangedListener(){
					@Override
					public void schemeChanged(int idx, int color) {
						edit.putInt("csn"+idx, color);
						edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.scheme_cube));
				dialog.show();
			}
		});
		//配色金字塔
		csPyrm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] colors={share.getInt("csp1", 0xffff0000), share.getInt("csp2", 0xff009900),
						share.getInt("csp3", 0xff0000ff), share.getInt("csp4", 0xffffff00)};
				ColorScheme dialog = new ColorScheme(context, 2, colors, new ColorScheme.OnSchemeChangedListener(){
					@Override
					public void schemeChanged(int idx, int color) {
						edit.putInt("csp"+idx, color);
						edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.scheme_pyrm));
				dialog.show();
			}
		});
		//配色SQ
		csSQ1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] colors={share.getInt("csq1", 0xffffff00), share.getInt("csq2", 0xff0000ff), share.getInt("csq3", 0xffff0000),
						share.getInt("csq4", 0xffffffff), share.getInt("csq5", 0xff009900), share.getInt("csq6", 0xffff8026)};
				ColorScheme dialog = new ColorScheme(context, 3, colors, new ColorScheme.OnSchemeChangedListener(){
					@Override
					public void schemeChanged(int idx, int color) {
						edit.putInt("csq"+idx, color);
						edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.scheme_sq));
				dialog.show();
			}
		});
		//分组管理
		sesMang.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
							spinner[8].setAdapter(adapter);
							spinner[8].setSelection(spSel[8], true);
						}
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(opnd && screenOn)releaseWakeLock();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(opnd && screenOn)acquireWakeLock();
	}
	private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getResources().getString(R.string.timer_size)+(seekBar.getProgress()+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getResources().getString(R.string.row_spacing)+ (seekBar.getProgress()+20));
			else if(seekBar.getId()==R.id.seekb5)stt[31].setText(getResources().getString(R.string.time_tap)+ (seekBar.getProgress()/20D));
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(seekBar.getId()==R.id.seekb1)stt[3].setText(getResources().getString(R.string.timer_size)+ (seekBar.getProgress()+50));
			else if(seekBar.getId()==R.id.seekb2)stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
			else if(seekBar.getId()==R.id.seekb3)stt[10].setText(getResources().getString(R.string.row_spacing)+ (seekBar.getProgress()+20));
			else if(seekBar.getId()==R.id.seekb5)stt[31].setText(getResources().getString(R.string.time_tap)+ (seekBar.getProgress()/20D));
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			//switch(seekBar.getId()) {
			//计时器字体
			if(seekBar.getId()==R.id.seekb1){
				stt[3].setText(getResources().getString(R.string.timer_size)+ (seekBar.getProgress()+50));
				edit.putInt("ttsize", seekBar.getProgress()+50);
				tvTimer.setTextSize(seekBar.getProgress()+50);
			}
			//打乱字体
			else if(seekBar.getId()==R.id.seekb2){
				stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
				edit.putInt("stsize", seekBar.getProgress()+12);
				tvScr.setTextSize(seekBar.getProgress()+12);
			}
			//成绩列表行距
			else if(seekBar.getId()==R.id.seekb3){
				intv=seekBar.getProgress()+20;
				stt[10].setText(getResources().getString(R.string.row_spacing)+ intv);
				if(resl!=0){
					setGridView(false);
				}
				edit.putInt("intv", seekBar.getProgress()+20);
			}
			//背景图不透明度
			else if(seekBar.getId()==R.id.seekb4){
				if(!bgcolor){
					setBgPic(bitmap, seekBar.getProgress());
				}
				edit.putInt("opac", seekBar.getProgress());
			}
			//启动延时
			else if(seekBar.getId()==R.id.seekb5){
				tapTime=seekBar.getProgress();
				stt[31].setText(getResources().getString(R.string.time_tap)+ (tapTime/20D));
				edit.putInt("tapt", tapTime);
			}
			edit.commit();
		}
	}
	private OnItemClickListener listl = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selFilePath=paths.get(arg2);
			getFileDir(selFilePath);
		}
	};
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			//WCA观察
			if(buttonView.getId()==R.id.check1) {
				if(isChecked) {if(timk==0) wca=true;wcat=true;edit.putBoolean("wca", true);}
				else {if(timk==0) wca=false;wcat=false;edit.putBoolean("wca", false);}
			}
			//屏幕常亮
			else if(buttonView.getId()==R.id.check2) {
				if(isChecked){opnl=true;acquireWakeLock();screenOn=true;edit.putBoolean("scron", true);}
				else {opnl=false;if(timk!=1)releaseWakeLock();screenOn=false;edit.putBoolean("scron", false);}
			}
			//隐藏打乱
			else if(buttonView.getId()==R.id.check3) {
				if(isChecked){hidscr=true;edit.putBoolean("hidscr", true);}
				else {hidscr=false;edit.putBoolean("hidscr", false);}
			}
			//确认成绩
			else if(buttonView.getId()==R.id.check4) {
				if(isChecked){conft=true;edit.putBoolean("conft", true);}
				else {conft=false;edit.putBoolean("conft", false);}
			}
			//详细成绩隐藏打乱
			else if(buttonView.getId()==R.id.check5) {
				if(isChecked){hidls=false;edit.putBoolean("hidls", false);}
				else {hidls=true;edit.putBoolean("hidls", true);}
			}
			//时间格式
			else if(buttonView.getId()==R.id.check6) {
				if(isChecked){timmh=true;edit.putBoolean("timmh", true);}
				else {timmh=false;edit.putBoolean("timmh", false);}
				if(resl>0){
					setGridView(false);
				}
			}
			//SQ复形求解
			else if(buttonView.getId()==R.id.check7) {
				if(isChecked) {
					sqshp=true;edit.putBoolean("sqshp", true);
					if(spSel[0]==8)
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								extsol=" "+Sq1Shape.solve(crntScr);
								handler.sendEmptyMessage(1);
								isNextScr=false;
								nextScr=Mi.SetScr((spSel[0]<<5)|spSel[18], false);
								isNextScr=true;
							}
						}.start();
				} else {
					sqshp=false;edit.putBoolean("sqshp", false);
					if(spSel[0]==8) tvScr.setText(crntScr);
				}
			}
			//全屏
			else if(buttonView.getId()==R.id.check8) {
				if(isChecked){
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fulls=true;edit.putBoolean("fulls", true);
				} else {
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fulls=false;edit.putBoolean("fulls", false);
				}
			}
			//信号反转
			else if(buttonView.getId()==R.id.check9) {
				if(isChecked){invs=true;Stackmat.inv=true;edit.putBoolean("invs", true);}
				else {invs=false;Stackmat.inv=false;edit.putBoolean("invs", false);}
			}
			//使用ss计时
			else if(buttonView.getId()==R.id.check10) {
				if(isChecked){
					usess=true;edit.putBoolean("usess", true);
					spinner[11].setEnabled(false);
					spinner[7].setEnabled(false);
					tvTimer.setText("OFF");
					if(!stm.isStart)stm.start();
				} else {
					usess=false;edit.putBoolean("usess", false);
					spinner[11].setEnabled(true);
					spinner[7].setEnabled(true);
					if(stm.isStart)stm.stop();
					if(spSel[7]==0){
						if(spSel[6]==0)tvTimer.setText("0.00");
						else tvTimer.setText("0.000");
					} else if(spSel[7]==1){
						tvTimer.setText("IMPORT");
					}
				}
			}
			//选择分组
			else if(buttonView.getId()==R.id.check11) {
				if(isChecked){selSes=true;edit.putBoolean("selses", true);}
				else {selSes=false;edit.putBoolean("selses", false);}
			}
			//分段计时
			else if(buttonView.getId()==R.id.check12) {
				if(isChecked){
					isMulp=true; spinner[13].setEnabled(true);
					edit.putBoolean("ismulp", true);
					multemp = new long[7];
					mulp = new int[6][rest.length];
					if(resl>0){
						cursor = dbh.query(spSel[8]);
						for(int i=0; i<resl; i++) {
							cursor.moveToPosition(i);
							for(int j=0; j<6; j++)
								mulp[j][i] = cursor.getInt(7+j);
						}
						//cursor.close();
					}
					times = (resl!=0)?new String[(spSel[13]+3)*(resl+1)]:null;
					setGridView(false);
				} else {
					isMulp=false; spinner[13].setEnabled(false);
					edit.putBoolean("ismulp", false);
					mulp = null; multemp = null;
					System.gc();
					times = (resl!=0)?new String[resl*3]:null;
					setGridView(false);
				}
				setGvTitle();
			}
			//屏幕变暗
			else if(buttonView.getId()==R.id.check13) {
				if(isChecked){
					if(screenOn)releaseWakeLock();
					opnd=true; if(screenOn)acquireWakeLock();
					edit.putBoolean("scrgry", true);
				} else {
					if(screenOn)releaseWakeLock();
					opnd=false; if(screenOn)acquireWakeLock();
					edit.putBoolean("scrgry", false);
				}
			}
			//RA0去尾统计
			else if(buttonView.getId()==R.id.lcheck1) {
				if(isChecked){
					l1am=true;edit.putBoolean("l1am", true);
					if(!isMulp)setGvTitle();
				} else {
					l1am=false;edit.putBoolean("l1am", false);
					if(!isMulp)setGvTitle();
				}
				if(resl>0 && !isMulp) setGridView(false);
			} else if(buttonView.getId()==R.id.lcheck2) {
				if(isChecked){
					l2am=true;edit.putBoolean("l2am", true);
					if(!isMulp)setGvTitle();
				} else {
					l2am=false;edit.putBoolean("l2am", false);
					if(!isMulp)setGvTitle();
				}
				if(resl>0 && !isMulp) setGridView(false);
			}
			edit.commit();
		}
	};
	private void viewsVisibility(boolean v) {	//TODO
		if(v) {
			myTabHost.getTabWidget().setVisibility(0);
			spinner[0].setVisibility(0);
			spinner[18].setVisibility(0);
			mButtonSst.setVisibility(0);
			if(hidscr)tvScr.setVisibility(0);
			chkb[11].setEnabled(true);
			chkb[11].setTextColor(cl[1]);
			chkb[13].setEnabled(true);
			chkb[13].setTextColor(cl[1]);
		} else {
			myTabHost.getTabWidget().setVisibility(8);
			spinner[0].setVisibility(8);
			spinner[18].setVisibility(8);
			mButtonSst.setVisibility(8);
			if(hidscr)tvScr.setVisibility(8);
			chkb[11].setEnabled(false);
			chkb[11].setTextColor((cl[1]&0xffffff)|(127<<24));
			chkb[13].setEnabled(false);
			chkb[13].setTextColor((cl[1]&0xffffff)|(127<<24));
		}
	}
	private String[] set2ndsel() {
		String[] s = null;
		switch(spSel[0]){
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
		if(spSel[18]>=s.length){
			//spinner[18].setText(s[0]);
			spSel[18] = 0;
		} //else spinner[18].setText(s[spSel[18]]);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner[18].setAdapter(adapter);
		spinner[18].setSelection(spSel[18], true);
		return s;
	}
	private void setInScr(String scrs) {
		String[] scr = scrs.split("\n");
		for(int i=0; i<scr.length; i++){
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
							String scr=(i+1)+". "+Mi.SetScr((spSel[0]<<5)|spSel[18], false)+"\r\n";
							handler.sendEmptyMessage(num*100+i);
							byte [] bytes = scr.toString().getBytes();
							out.write(bytes);
						}
						out.close();
						handler.sendEmptyMessage(7);
					} catch (IOException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(9);
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
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int which) {d.dismiss();}
			}).show();
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
					selFilePath = f.exists()?selFilePath:"/sdcard/";
					getFileDir(selFilePath);
					listView.setOnItemClickListener(listl);
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.sel_path)).setView(viewb)
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j){
							et2.setText(selFilePath+"/");
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i){ }
					}).show();
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
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i){ }
						}).show();
					} else {
						outScr(path, fileName, num);
					}
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int which) {d.dismiss();}
			}).show();
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
			String token=share.getString("token", null);
			String expires_in=share.getString("expin", null);
			if(token==null || expires_in==null || (System.currentTimeMillis()-share.getLong("totime", 0))/1000>=Integer.parseInt(expires_in)){
				isShare=true;
				isLogin=false;
				auth();
			} else {
				try {
					Utility.setAuthorization(new Oauth2AccessTokenHeader());
					AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
					accessToken.setExpiresIn(expires_in);
					Weibo.getInstance().setAccessToken(accessToken);
					File picFile = new File(addstr);
					if (!picFile.exists()) {
						addstr = null;
					}
					share2weibo(getShareContext(), addstr);
					Intent i = new Intent(DCTimer.this, ShareActivity.class);
					DCTimer.this.startActivity(i);
				} catch(Exception e){isShare=true;auth();}
			}
			break;
		case 4:
			LayoutInflater factory2 = LayoutInflater.from(DCTimer.this);
			final View view = factory2.inflate(R.layout.dlg_about, null);
			new AlertDialog.Builder(DCTimer.this).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i){}
			}).show();
			break;
		case 5:
			cursor.close();
			dbh.close();
			edit.putInt("sel", spSel[0]);
			if(spSel[0]==11 && spSel[18]==4)edit.putInt("sel2", 3);
			else edit.putInt("sel2", spSel[18]);
			edit.commit();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return true;
	}
	private void getFileDir(String path) {
		items=new ArrayList<String>();
		paths=new ArrayList<String>();
		File f = new File(path);
		File[] fs = f.listFiles();
		if(fs!=null && fs.length>0)Arrays.sort(fs);
		if(!path.equals("/")) {
			items.add("..");
			paths.add(f.getParent());
		}
		if(fs!=null)
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
		if(!isMulp){
			aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{
					cl[1],cl[2],cl[3],cl[4]}, Mi.smax, Mi.smin, intv);
			myGridView.setNumColumns(3);
		} else {
			aryAdapter = new TimesAdapter(DCTimer.this,	times, new int[]{cl[1],
					cl[2], cl[3], Mi.smax, Mi.smin}, intv, spSel[13]+3);
			myGridView.setNumColumns(spSel[13]+3);
		}
		if(ch)myGridView.setStackFromBottom(false);
		else if(resl>30)myGridView.setStackFromBottom(true);
		else myGridView.setStackFromBottom(false);
		myGridView.setAdapter(aryAdapter);
	}
	private void setGvTitle() {
		if(isMulp){
			String[] title = new String[spSel[13]+3];
			title[0] = getResources().getString(R.string.time);
			for(int i=1; i<spSel[13]+3; i++) title[i] = "P"+i;
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(spSel[13]+3);
			gvTitle.setAdapter(ta);
		}
		else {
			String[] title = {getResources().getString(R.string.time),
					(l1am?"avg of ":"mean of ")+listnum[spSel[4]],
					(l2am?"avg of ":"mean of ")+listnum[spSel[5]+1]};
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(3);
			gvTitle.setAdapter(ta);
		}
	}
	private String getShareContext(){
		String s1=getResources().getString(R.string.share_c1).replace("$len", ""+resl).replace("$scrtype", getScrName())
				.replace("$best", Mi.distime(Mi.smin, false)).replace("$mean", Mi.distime(Mi.sesMean));
		String s2=(resl>listnum[spSel[4]])?getResources().getString(R.string.share_c2).replace("$flen", ""+listnum[spSel[4]]).
				replace("$favg", Mi.distime(Mi.bavg[0])):"";
		String s3=(resl>listnum[spSel[5]+1])?getResources().getString(R.string.share_c2).replace("$flen", ""+listnum[spSel[5]+1]).
				replace("$favg", Mi.distime(Mi.bavg[1])):"";
		String s4=getResources().getString(R.string.share_c3);
		return s1+s2+s3+s4;
	}
	private String getScrName(){
		String[] mItems=getResources().getStringArray(R.array.cubeStr);
		String[] s = null;
		switch(spSel[0]){
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
		return mItems[spSel[0]]+"-"+s[spSel[18]];
	}
	private void searchSesType(){
		int type=0, idx=-1;
		for(int i=0; i<15; i++){
			int s=sestp[i];
			if(type==0 && s==-1){
				idx=i;
				type=1;
			}
			if(s==scrType){
				idx=i;
				type=2;
				break;
			}
		}
		if(type==2 || (sestp[spSel[8]] != -1 && type == 1)){
			System.out.println(type);
			spinner[8].setSelection(idx, true);
			spSel[8] = (byte) idx;
			//getSession(idx);
			edit.putInt("group", idx);
			edit.commit();
		}
	}
	private void setScrType(){
		switch(spSel[0]){
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
			scrType=spSel[0];break;
		case 11:
			if(spSel[18]<3)scrType=11;
			else if(spSel[18]<5)scrType=12;
			else if(spSel[18]<12)scrType=spSel[18]+8;
			else scrType=spSel[18]+37;
			break;
		case 12:
		case 13:
		case 14:
		case 15:
			scrType=spSel[0]+8;break;
		case 16:
			scrType=spSel[18]+24;break;
		case 17:
			scrType=1;break;
		case 18:
			scrType=spSel[18]+30;break;
		case 19:
			scrType=6;break;
		case 20:
			scrType=spSel[18]+32;break;
		}
	}
	private void auth(){
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
		weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");// 此处回调页内容应该替换为与appkey对应的应用回调页
		weibo.authorize(DCTimer.this, new AuthDialogListener());
	}
	private void share2weibo(String content, String picPath) throws WeiboException {
		Weibo weibo = Weibo.getInstance();
		weibo.share2weibo(this, weibo.getAccessToken().getToken(), weibo.getAccessToken()
				.getSecret(), content, picPath);
	}
	private void setTouch(int a) {
		switch (a) {
		case MotionEvent.ACTION_DOWN:
			if(timk==1) {
				if(mulpCount!=0) {
					if(spSel[14]==1 || spSel[14]==3)
						vibrator.vibrate(vibTime[spSel[15]]);
					tvTimer.setTextColor(Color.GREEN);
					multemp[spSel[13]+2-mulpCount] = System.currentTimeMillis();
				}
				else {
					if(spSel[14]>1)
						vibrator.vibrate(vibTime[spSel[15]]);
					timer.count();
					new Thread(new Runnable(){
						public void run(){
							try {
								Thread.sleep(25);
							} catch (InterruptedException e) {e.printStackTrace();}
							handler.sendEmptyMessage(4);
						}
					}).start();
					if(isMulp)multemp[spSel[13]+2]=timer.time1;
					viewsVisibility(true);
					if(isMulp)spinner[13].setEnabled(true);
				}
			} else {
				if(!scrt || timk==2) {
					if(tapTime == 0 || (wca && timk==0)) {
						tvTimer.setTextColor(Color.GREEN);
						canStart=true;
					} else {
						timer.isTapped = true;
						if(timk==0)tvTimer.setTextColor(Color.RED);
						else tvTimer.setTextColor(Color.YELLOW);
						timer.tap();
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(timk==0){
				if(isLongPress) {
					isLongPress = false;
				}
				else if(scrt)newScr(false);
				else {
					if(tapTime ==0 || canStart) {
						if(spSel[14]==1 || spSel[14]==3)
							vibrator.vibrate(vibTime[spSel[15]]);
						if(wca)timer.v=1;
						timer.count();
						if(wca)timk=2;
						else timk=1;
						if(isMulp){
							mulpCount=spSel[13]+1;
							multemp[0]=timer.time0;
						}
						else mulpCount=0;
						acquireWakeLock();screenOn=true;
						viewsVisibility(false);
						if(isMulp)spinner[13].setEnabled(false);
					} else {
						timer.isTapped = false;
						timer.stopt();
						tvTimer.setTextColor(cl[1]);
					}
				}
			} else if(timk==1){	//TODO
				if(isLongPress) isLongPress = false;
				if(mulpCount!=0) {
					mulpCount--;
					tvTimer.setTextColor(cl[1]);
				} else {
					wca=wcat;
					if(!wca){isp2=0;idnf=true;}
					//newScr(false);
					//mTextView2.setText(Mi.distime((int)timer.time));
					if(idnf) confirmTime((int)timer.time);
					else {
						if(conft)
							new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.time_dnf)).setMessage(getResources().getString(R.string.confirm_adddnf))
							.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int j){
									record((int)timer.time,(byte)2);
								}
							}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int which) {
									d.dismiss();
								}
							}).show();
						else record((int)timer.time,(byte)2);
					}
					timk=0;
					if(!opnl){releaseWakeLock();screenOn=false;}
				}
			} else {
				if(isLongPress) isLongPress = false;
				if(tapTime ==0 || canStart) {
					if(timer.v==1){isp2=0;idnf=true;}
					else if(timer.v==2){isp2=2000;idnf=true;}
					else if(timer.v==3){isp2=0;idnf=false;}
					if(spSel[14]==1 || spSel[14]==3)
						vibrator.vibrate(vibTime[spSel[15]]);
					timer.v=0;
					timer.count();
					timk=1;
					if(isMulp){
						multemp[0]=timer.time0;
					}
					acquireWakeLock();screenOn=true;
					viewsVisibility(false);
					if(isMulp)spinner[13].setEnabled(false);
				} else {
					timer.isTapped = false;
					timer.stopt();
					tvTimer.setTextColor(Color.RED);
				}
			}
		}
	}
	private void inputTime(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			tvTimer.setTextColor(Color.GREEN);
			break;
		case MotionEvent.ACTION_UP:
			tvTimer.setTextColor(Color.BLACK);
			LayoutInflater factory = LayoutInflater.from(DCTimer.this);
			final View view = factory.inflate(R.layout.editbox_layout, null);
			final EditText editText=(EditText)view.findViewById(R.id.editText1);
			editText.setFocusable(true);
			editText.requestFocus();
			new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.enter_time)).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String time = Mi.convStr(editText.getText().toString());
					if(time.equals("Error") || Mi.convTime(time)==0)
						Toast.makeText(DCTimer.this, getResources().getString(R.string.illegal), Toast.LENGTH_SHORT).show();
					else record(Mi.convTime(time), (byte) 0);
					//newScr(false);
				}
			}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
		}
	}
	private void record(int time, byte p) {
		if(resl>=rest.length) {
			String[] scr2=new String[scrst.length+12];
			byte[] rep2=new byte[resp.length+12];//, rd2=new byte[resd.length+12];
			int res2[]=new int[rest.length+12];
			for(int i=0;i<resl;i++) {
				scr2[i]=scrst[i];rep2[i]=resp[i];res2[i]=rest[i];//rd2[i]=resd[i];
			}
			scrst=scr2;resp=rep2;rest=res2;
			if(isMulp) {
				int[][] mulp2 = new int[6][rest.length];
				for(int i=0;i<resl;i++) {
					for(int j=0; j<6; j++){
						mulp2[j][i]=mulp[j][i];
					}
				}
				mulp=mulp2;
			}
			System.gc();
		}
		scrst[resl]=crntScr;resp[resl]=p;rest[resl++]=time;
		if(isMulp) {
			boolean temp = true;
			for(int i=0; i<spSel[13]+2; i++){
				if(temp)
					mulp[i][resl-1]=(int)(multemp[i+1]-multemp[i]);
				else mulp[i][resl-1]=0;
				if(mulp[i][resl-1]<0 || mulp[i][resl-1]>rest[resl-1]){
					mulp[i][resl-1]=0; temp=false;
				}
			}
		}
		byte d = 1;
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
		dbh.insert(spSel[8], cv);
		if(isMulp)times = new String[(spSel[13]+3)*(resl+1)];
		else times=new String[resl*3];
		seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
		setGridView(false);
		if(selSes && sestp[spSel[8]] != scrType) {
			sestp[spSel[8]]=(short) scrType;
			edit.putInt("sestp"+spSel[8], scrType);
			edit.commit();
		}
		newScr(false);
	}
	private void change(int idx, byte p) {
		if(resp[idx]!=p) {
			resp[idx]=p;
			byte d = 1;
			if(p==2) {
				p=0; d=0;
			}
			cursor = dbh.query(spSel[8]);
			cursor.moveToPosition(idx);
			int id=cursor.getInt(0);
			//cursor.close();
			dbh.update(spSel[8], id, p, d);
			seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
			setGridView(false);
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(timk==1){
				timer.count();
				new Thread(new Runnable(){
					public void run(){
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {e.printStackTrace();}
						handler.sendEmptyMessage(4);
					}
				}).start();
				viewsVisibility(true);
				if(isMulp)spinner[13].setEnabled(true);
				wca=wcat;
				if(!wca){isp2=0;idnf=true;}
				//newScr(false);
				if(idnf) confirmTime((int)timer.time);
				else {
					if(conft)
						new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.time_dnf)).setMessage(getResources().getString(R.string.confirm_adddnf))
						.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j){
								record((int)timer.time,(byte)2);
							}
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface d, int which) {
								d.dismiss();
							}
						}).show();
					else record((int)timer.time,(byte)2);
				}
				timk=0;
				if(!opnl){releaseWakeLock();screenOn=false;}
			} else if(timk==2){
				timer.stopi();
				new Thread(new Runnable(){
					public void run(){
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {e.printStackTrace();}
						handler.sendEmptyMessage(6);
					}
				}).start();
				viewsVisibility(true);
				if(isMulp)spinner[13].setEnabled(true);
				wca=wcat;
				timk=0;
				if(!opnl){releaseWakeLock();screenOn=false;}
			} else if(event.getRepeatCount() == 0) {
				if((System.currentTimeMillis()-exitTime) > 2000){
					Toast.makeText(DCTimer.this, getResources().getString(R.string.again_exit), Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					edit.putInt("sel", spSel[0]);
					if(spSel[0]==11 && spSel[18]==4)edit.putInt("sel2", 3);
					else edit.putInt("sel2", spSel[18]);
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
					cursor = dbh.query(spSel[8]);
					int delId=dbLastId;
					if(resl>1){
						cursor.moveToPosition(resl-2);
						dbLastId=cursor.getInt(0);
					} else dbLastId=0;
					dbh.del(spSel[8], delId);
					resl--;
					if(resl>0){
						if(isMulp)times=new String[(resl+1)*(spSel[13]+3)];
						else times=new String[resl*3];
					}
					else {
						times=null;
						sestp[spSel[8]]=-1;
						edit.remove("sestp"+spSel[8]);
						edit.commit();
					}
					seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
					setGridView(false);
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_A) {
			if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.confirm_clear_session))
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dbh.clear(spSel[8]);
					resl=dbLastId=0;
					times=null;
					seMean.setText(getResources().getString(R.string.session_average)+"0/0): N/A (N/A)");
					Mi.smax=Mi.smin=-1;
					setGridView(false);
					if(sestp[spSel[8]]!=-1){
						sestp[spSel[8]]=-1;
						edit.remove("sestp"+spSel[8]);
						edit.commit();
					}
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_D) {
			if(resl==0) Toast.makeText(DCTimer.this, getResources().getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.show_time)+Mi.distime(resl-1, true)).setItems(R.array.rstcon,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:change(resl-1, (byte) 0);break;
					case 1:change(resl-1, (byte) 1);break;
					case 2:change(resl-1, (byte) 2);break;
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
		if(spSel[18]!=s2){
			c2=true;
			spSel[18]=(byte) s2;
		}
		if(c1||c2) {
			set2ndsel();
			setScrType();
			if(selSes)searchSesType();
			if(inScr!=null && inScr.size()!=0)inScr=null;
		}
	}
	private void showAlertDialog(int i, int j){
		String t=null;
		switch(i){
		case 1:
			t=(l1am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spSel[4]]);
			slist=l1am?ao(listnum[spSel[4]], j):mo(listnum[spSel[4]], j);
			break;
		case 2:
			t=(l2am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spSel[5]+1]);
			slist=l2am?ao(listnum[spSel[5]+1], j):mo(listnum[spSel[5]+1], j);
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
						selFilePath = f.exists()?selFilePath:"/sdcard/";
						getFileDir(selFilePath);
						listView.setOnItemClickListener(listl);
						new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.sel_path)).setView(viewb)
						.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j){
								et1.setText(selFilePath+"/");
							}
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i){ }
						}).show();
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
							}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int i){ }
							}).show();
						} else {
							outStat(path, fileName, slist);
						}
					}
				}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int which) {d.dismiss();}
				}).show();
			}
		}).setNegativeButton(getResources().getString(R.string.btn_close), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i){}
		}).show();
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
			
			if(Mi.viewType==3 && spSel[1]!=0) {
				new Thread() {
					public void run() {
						handler.sendEmptyMessage(8);
						if(spSel[1]==1)extsol="\n"+Cross.cross(crntScr, DCTimer.spSel[9], DCTimer.spSel[10]);
						else if(spSel[1]==2)extsol="\n"+Cross.xcross(crntScr, DCTimer.spSel[10]);
						else if(spSel[1]==3)extsol="\n"+EOline.eoLine(crntScr, DCTimer.spSel[10]);
						else if(spSel[1]==4)extsol="\n"+PetrusxRoux.roux(crntScr, DCTimer.spSel[10]);
						else if(spSel[1]==5)extsol="\n"+PetrusxRoux.petrus(crntScr, DCTimer.spSel[10]);
						handler.sendEmptyMessage(3);
					}
				}.start();
			}
			else tvScr.setText(crntScr);
		}
		else if((spSel[0]==0 && spSel[2]!=0) ||
			(spSel[0]==1 && (spSel[18]!=0 || (spSel[1]!=0 && (spSel[18]==0 || spSel[18]==1 || spSel[18]==5 || spSel[18]==19)))) ||
			(spSel[0]==8 && (spSel[18]==2 || sqshp)) ||
			(spSel[0]==11 && (spSel[18]>3 && spSel[18]<7)) ||
			(spSel[0]==17 && (spSel[18]<3 || spSel[18]==6)) ||
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
								crntScr = Mi.SetScr((spSel[0]<<5)|spSel[18], ch);
								isChScr=false;
								nextScr="";
							} else {
								while (!isNextScr) {
									try {
										sleep(50);
									} catch (InterruptedException e) { }
								}
								crntScr = nextScr;
							}
						}
						else {
							crntScr = Mi.SetScr((spSel[0]<<5)|spSel[18], ch);
						}
						extsol = Mi.sc;
						//crntScr=(!ch && isNextScr)?nextScr:Mi.SetScr((spSel[0]<<5)|spSel[18]);
						if((spSel[0]==0 && spSel[2]!=0) ||
								(spSel[1]!=0 && spSel[0]==1 && (spSel[18]==0 || spSel[18]==1 || spSel[18]==5 || spSel[18]==19)))
							handler.sendEmptyMessage(3);
						else if(spSel[0]==8 && sqshp)handler.sendEmptyMessage(1);
						else handler.sendEmptyMessage(0);
						canScr=true;
						isNextScr = false;
						nextScr = Mi.SetScr((spSel[0]<<5)|spSel[18], ch);
						isNextScr = true;
					}
				}.start();
			}
		} else {
			crntScr=Mi.SetScr((spSel[0]<<5)|spSel[18], ch);
			tvScr.setText(crntScr);
		}
	}
	public void confirmTime(int utime){
		final int time=utime;
		if(conft)
			new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.show_time)+Mi.distime(time + isp2)).
					setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:record(time + isp2, (byte) 0);break;
					case 1:record(time + isp2, (byte) 1);break;
					case 2:record(time + isp2, (byte) 2);break;
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.btn_cancel),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d,int which) {
					d.dismiss();
					newScr(false);
				}
			}).show();
		else record(time + isp2, (byte)0);
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
		cursor = dbh.query(spSel[8]);
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
				if(spSel[6]==1)sum+=data[j];
				else sum+=(data[j]+5)/10;
				if(spSel[6]==1)sum2+=Math.pow(data[j], 2);
				else sum2+=Math.pow((data[j]+5)/10, 2);
			}
			cavg=(int) (sum/(n-trim*2)+0.5);
			csdv=(int) Math.sqrt(sum2/(n-trim*2)-sum*sum/Math.pow(n-trim*2, 2));
			if(spSel[6]==0)cavg*=10;
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
		cursor = dbh.query(spSel[8]);
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
				if(spSel[6]==1)sum+=(double)(rest[j]+resp[j]*2000);
				else sum+=(rest[j]+resp[j]*2000+5)/10;
				if(spSel[6]==1)sum2+=Math.pow(rest[j]+resp[j]*2000, 2);
				else sum2+=Math.pow((rest[j]+resp[j]*2000+5)/10, 2);
			}
			cavg=(int) (sum/n+0.5);
			csdv=(int) (Math.sqrt(sum2/n-sum*sum/n/n)+(spSel[6]==1?0:0.5));
		}
		if(spSel[6]==0)cavg*=10;
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_avg)+(m?"DNF":Mi.distime(cavg))+" ");
		sb.append("(σ = "+Mi.standDev(csdv)+")\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(min,false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(max,false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		cursor = dbh.query(spSel[8]);
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
		int height=dm.heightPixels;
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		canvas.drawBitmap(scaleBitmap, 0, 0, paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255-255*opa/100);
		canvas.drawRect(0, 0, width, height, paint);
		//return newBitmap;
		Drawable drawable =new BitmapDrawable(newBitmap);
		myTabHost.setBackgroundDrawable(drawable);
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
			times=(isMulp)?new String[(spSel[13]+3)*(resl+1)]:new String[resl*3];
		} else {
			times=null;
			dbLastId=0;
		}
		//cursor.close();
	}
	private void singTime(final int p, final int col) {
		cursor = dbh.query(spSel[8]);
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
		if(resp[p/col]==2){
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(resp[p/col]==1){
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
				case R.id.st_pe1: change(p/col, (byte)0);break;
				case R.id.st_pe2: change(p/col, (byte)1);break;
				case R.id.st_pe3: change(p/col, (byte)2);break;
				}
				String text = editText.getText().toString();
				if(!text.equals(note)){
					dbh.update(spSel[8], id, text);
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
				int delId;
				if(p/col!=resl-1) {
					for(int i=p/col;i<resl-1;i++) {
						rest[i]=rest[i+1];resp[i]=resp[i+1];scrst[i]=scrst[i+1];
						if(isMulp){
							for(int j=0; j<spSel[13]+2; j++){
								mulp[j][i]=mulp[j][i+1];
							}
						}
					}
					cursor.moveToPosition(p/col);
					delId=cursor.getInt(0);
				} else {
					delId=dbLastId;
					if(resl>1){
						cursor.moveToPosition(resl-2);
						dbLastId=cursor.getInt(0);
					} else dbLastId=0;
				}
				dbh.del(spSel[8], delId);
				//cursor.close();
				resl--;
				if(resl>0){
					if(isMulp)times=new String[(resl+1)*col];
					else times=new String[resl*col];
				}
				else {
					times=null;
					sestp[spSel[8]]=-1;
					edit.remove("sestp"+spSel[8]);
					edit.commit();
				}
				seMean.setText(getResources().getString(R.string.session_average)+Mi.sesMean());
				setGridView(false);
				d.dismiss();
			}
		}).show();
	}
	private Bitmap takeScreenShot(Activity activity){
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
		//Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}
	private void savePic(Bitmap b,String strFileName){
		try {
			FileOutputStream fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					bitmap=getBgPic(bitmap);
					setBgPic(bitmap, share.getInt("opac", 35));
					bgcolor=false;
					edit.putString("picpath", picPath);
					edit.putBoolean("bgcolor", false);edit.commit();
					c.close();
				} catch (Exception e) {
				} catch (OutOfMemoryError e){Toast.makeText(DCTimer.this, "Out of memory error: bitmap size exceeds VM budget", Toast.LENGTH_SHORT).show();}
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
			isLogin=true;
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
				} catch (WeiboException e) {
					e.printStackTrace();
				} finally {}
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
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
import android.widget.TabHost.TabSpec;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DCTimer extends Activity {
	private Context context;
	private TabHost myTabHost;
	private int[] layRes = {R.id.tab_timer, R.id.tab_list, R.id.tab_setting};
	private Button sndscr, mButtonSst;	//打乱状态
	public TextView mTextView2;
	private static TextView mTextView1; //显示打乱
	private Spinner[] spinner=new Spinner[14];
	public static byte[] spinSel=new byte[14];
	private ArrayAdapter<String> adapter;
	public boolean wca;
	private boolean wcat, opnl, hidscr, conft, isShare, isLogin, isMulp;
	public static boolean hidls, timmh, l1am, l2am;
	private static int selold,sel2;
	public static int[] rest;	//成绩列表
	public static byte[] resp, resd;	//+2, dnf
	protected static int[][] mulp = null;
	private static long[] multemp=null;
	public static int resl;
	public static String[] scrst;	//打乱列表
	public static String cscrs;	//当前打乱
	private Timer timer;
	private Stackmat stm;
	static int isp2 = 0;
	static boolean idnf = true;
	private static int timk = 0;
	public int[] cl = new int[5];
	private ImageView iv;
	private boolean scrt = false, bgcolor, fulls, invs, usess, screenOn, selSes;
	static boolean sqshp;
	private String picPath;
	private int dbLastId;
	public static short[] sestp = new short[15];
	private static String[] sesname = new String[15];
	private static int scrType;
	
	private GridView myGridView = null, gvTitle = null;
	private TimesAdapter aryAdapter;
	private String[] times = null;
	private Button oriavg, clear, hist;	//时间分布
	private static String slist;
	public static byte[] listnum = {3,5,12,50,100};
	private static char[] srate = {48000,44100,22050,16000,11025,8000};

	private Button bagc, bgpic, txtc, orbc, orwc, avbc, reset, rsauth;
	private Button csCube, csPyrm, csSQ1, sesMang;
	private ColorPicker dialog;
	private SeekBar skb1, skb2, skb3, skb4, skb5;
	private int ttsize, stsize;
	private TextView[] stt = new TextView[33];
	private int sttlen=stt.length;
	private TextView tvl;
	private CheckBox[] chkb = new CheckBox[14];
	protected static SharedPreferences share;
	protected static SharedPreferences.Editor edit;
	private DBHelper dbh;
	
	protected boolean canStart;
	protected int tapTime;
	private String[] mItems;
	private Bitmap bitmap;
	private PowerManager.WakeLock wakeLock = null;
	private DisplayMetrics dm;
	private String addstr="/data/data/com.dctimer/databases/main.png";
	private static final String CONSUMER_KEY = "3318942954";// 替换为开发者的appkey，例如"1646212960";
	private static final String CONSUMER_SECRET = "77d13c80e4a9861e4e7c497968c5d4e5";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";
	private int mulpCount;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0: mTextView1.setText(cscrs); break;
			case 1:
				mTextView1.setText(cscrs+"\n\n"+getResources().getString(R.string.shape)+Mi.sc);
				break;
			case 2:
				mTextView1.setText(getResources().getString(R.string.scrambling)); break;
			case 3: mTextView1.setText(cscrs+Mi.sc); break;
			case 4: mTextView2.setText(Mi.distime((int)timer.time)); break;
			case 5: mTextView2.setText("IMPORT"); break;
			case 6: mTextView2.setText(spinSel[6]==0?"0.00":"0.000"); break;
			case 7:
				mTextView1.setText(getResources().getString(R.string.initializing)); break;
			case 8: mTextView1.setText(cscrs+"\n\n"+getResources().getString(R.string.solving)); break;
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
		@Override
		public int getCount() {
			if(times!=null)return times.length;
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
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
			}
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			if(!bgcolor){
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				bitmap=BitmapFactory.decodeFile(picPath);
				bitmap=getBgPic(bitmap);
				setBgPic(bitmap, share.getInt("opac", 35));
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
		selold=spinSel[0]=(byte) share.getInt("sel", 1);	//打乱种类
		sel2=share.getInt("sel2", 0);
		cl[0]=share.getInt("cl0", Color.rgb(102, 204, 255));
		cl[1]=share.getInt("cl1", Color.BLACK);
		cl[2]=share.getInt("cl2", Color.rgb(255, 0, 255));
		cl[3]=share.getInt("cl3", Color.RED);
		cl[4]=share.getInt("cl4", Color.rgb(0, 153, 0));
		wca=share.getBoolean("wca", false);
		wcat=wca;
		hidscr=share.getBoolean("hidscr", false);
		hidls=share.getBoolean("hidls", false);
		conft=share.getBoolean("conft", true);
		l1am=share.getBoolean("l1am", true);
		l2am=share.getBoolean("l2am", true);
		spinSel[1]=(byte) share.getInt("cxe", 0);
		ttsize=share.getInt("ttsize", 60);
		stsize=share.getInt("stsize", 18);
		if(share.getBoolean("mnxc", true))spinSel[3]=1;
		else spinSel[3]=0;	//五魔配色
		spinSel[4]=(byte) share.getInt("list1", 1);
		spinSel[5]=(byte) share.getInt("list2", 1);
		timmh=share.getBoolean("timmh", true);
		if(share.getBoolean("prec", true))spinSel[6]=1;
		else spinSel[6]=0;	//精度设置
		spinSel[7]=(byte)share.getInt("tiway", 0);	//计时方式
		spinSel[8]=(byte)share.getInt("group", 0);	//分组
		spinSel[9]=(byte)share.getInt("cface", 0);	//十字计算底面
		spinSel[10]=(byte)share.getInt("cside", 1);	//以及颜色
		spinSel[11]=(byte)share.getInt("srate", 1);	//采样频率
		Stackmat.samplingRate=srate[spinSel[11]];
		spinSel[2]=(byte)share.getInt("cube2l", 0);	//二阶底层求解
		spinSel[12]=(byte)share.getInt("tfont", 3);	//计时器字体
		spinSel[13]=(byte)share.getInt("mulp", 0);
		bgcolor=share.getBoolean("bgcolor", true);
		sqshp=share.getBoolean("sqshp", false);	//SQ1复形计算
		fulls=share.getBoolean("fulls", false);	//全屏显示
		usess=share.getBoolean("usess", false);
		invs=share.getBoolean("invs", false);	//反转信号
		opnl=share.getBoolean("scron", false);
		selSes=share.getBoolean("selses", false);
		Stackmat.inv=invs;
		picPath=share.getString("picpath", null);
		tapTime=share.getInt("tapt", 0);
		isMulp=share.getBoolean("ismulp", false);
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
		
		mTextView1 = (TextView) findViewById(R.id.myTextView1);
		mTextView2 = (TextView)findViewById(R.id.myTextView2);
		//mButtonScr = (Button) findViewById(R.id.myButtonScr);
		mButtonSst = (Button) findViewById(R.id.myButtonSst);
		for(int i=0;i<14;i++){
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
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner[i].setAdapter(adapter);
			spinner[i].setSelection(spinSel[i]);
		}
		
		if(spinSel[1]==0){spinner[9].setEnabled(false);spinner[10].setEnabled(false);}
		
		sndscr = (Button) findViewById(R.id.sndscr);
		set2ndsel();
		mTextView1.setTextSize(stsize);
		mTextView2.setTextSize(ttsize);
		switch(spinSel[12]){
		case 0:
			mTextView2.setTypeface(Typeface.create("monospace", 0));break;
		case 1:
			mTextView2.setTypeface(Typeface.create("serif", 0));break;
		case 2:
			mTextView2.setTypeface(Typeface.create("sans-serif", 0));break;
		case 3:
			mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
			break;
		case 4:
			mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
			break;
		case 5:
			mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
			break;
		}
		
		stm=new Stackmat(this);
		
		if(usess){
			spinner[11].setEnabled(false);
			mTextView2.setText("OFF");
			if(stm.creatAudioRecord((int)srate[spinSel[11]]));
			else {
				spinner[11].setSelection(1);
				spinSel[11]=1;
				edit.putInt("srate", 1);
				edit.commit();
			}
			stm.start();
		} else {
			if(spinSel[7]==0 && spinSel[6]==0)mTextView2.setText("0.00");
			else if(spinSel[7]==1)mTextView2.setText("IMPORT");
		}
		
		timer = new Timer(this);
		dbh = new DBHelper(this);

		myGridView=(GridView)findViewById(R.id.myGridView);
		gvTitle=(GridView)findViewById(R.id.gv_title);
		oriavg=(Button)findViewById(R.id.mButtonoa);
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
		getSession(spinSel[8]);
		oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
		if(isMulp){
			chkb[13].setChecked(true);
			String[] temp = null;
			mulp = new int[spinSel[13]+2][rest.length];
			multemp = new long[spinSel[13]+3];
			if(resl!=0){
				Cursor c = dbh.query(spinSel[8]);
				for(int i=0; i<resl; i++) {
					c.moveToPosition(i);
					for(int j=0; j<spinSel[13]+2; j++){
						mulp[j][i] = c.getInt(7+j);
					}
				}
				c.close();
				temp = new String[(spinSel[13]+3)*(resl+1)];
			}
			aryAdapter = new TimesAdapter(DCTimer.this, temp, new int[]{cl[1],
					cl[2],cl[3],Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
			myGridView.setNumColumns(spinSel[13]+3);
			myGridView.setAdapter(aryAdapter);
		}
		else {
			spinner[13].setEnabled(false);
			aryAdapter=new TimesAdapter (this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
					Mi.omax, Mi.omin, share.getInt("intv", 30));
			myGridView.setAdapter(aryAdapter);
		}
		setGvTitle();
		if(usess){
			chkb[11].setChecked(true);spinner[10].setEnabled(false);
			if(!stm.isStart)stm.start();
		}
		if(selSes)chkb[12].setChecked(true);
		for(int i=0;i<14;i++)
		chkb[i].setOnCheckedChangeListener(listener);
		
		File file = new File(addstr);
		if(!file.exists()){
			//Toast.makeText(TestActivity.this, "图片" + addstr + "不存在", Toast.LENGTH_SHORT).show();
			try {
				InputStream assetsPic = this.getAssets().open("main.png");
				OutputStream dbOut = new FileOutputStream(addstr);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = assetsPic.read(buffer)) > 0) {
					dbOut.write(buffer, 0, length);
				}
				dbOut.flush();
				dbOut.close();
				assetsPic.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
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
		for(int i=0;i<14;i++)chkb[i].setTextColor(cl[1]);
		mTextView1.setTextColor(cl[1]);
		mTextView2.setTextColor(cl[1]);
		spinner[0].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spinSel[0]=(byte) arg2;
				if(spinSel[0]!=selold){
					sel2=0;selold=spinSel[0];
				}
				set2ndsel();
				setScrType();
				newScr();
				if(selSes)searchSesType();
				arg0.setVisibility(View.VISIBLE); 
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		sndscr.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				final String[] s=set2ndsel();
				new AlertDialog.Builder(DCTimer.this).setItems(s, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						sel2=which;
						setScrType();
						newScr();
						sndscr.setText(s[sel2]);
						if(selSes)searchSesType();
					}
				}).show();
			}
		});
		//十字
		spinner[1].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[1]!=arg2) {
					spinSel[1]=(byte) arg2;
					edit.putInt("cxe", spinSel[1]);
					edit.commit();
					if(spinSel[0]==1 && (sel2==0 || sel2==1 || sel2==5)) {
						if(spinSel[1]==0)mTextView1.setText(cscrs);
						else new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								switch(spinSel[1]) {
								case 1:
									Mi.sc="\n"+Cross.cross(cscrs, spinSel[9], spinSel[10]); break;
								case 2:
									Mi.sc="\n"+Cross.xcross(cscrs, spinSel[10]); break;
								case 3:
									Mi.sc="\n"+EOline.eoLine(cscrs, spinSel[10]); break;
								case 4:
									Mi.sc="\n"+PetrusxRoux.roux(cscrs, spinSel[10]); break;
								case 5:
									Mi.sc="\n"+PetrusxRoux.petrus(cscrs, spinSel[10]); break;
								}
								handler.sendEmptyMessage(3);
							}
						}.start();
					}
				}
				if(spinSel[1]==0){spinner[9].setEnabled(false);spinner[10].setEnabled(false);}
				else if(spinSel[1]==1){spinner[9].setEnabled(true);spinner[10].setEnabled(true);}
				else {spinner[9].setEnabled(false);spinner[10].setEnabled(true);}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//五魔配色
		spinner[3].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spinSel[3]=(byte)arg2;
				arg0.setVisibility(View.VISIBLE); 
				if(spinSel[3]==0)edit.putBoolean("mnxc", false);
				else edit.putBoolean("mnxc", true);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//滚动平均0
		spinner[4].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[4]!=arg2) {
					spinSel[4]=(byte)arg2;
					if(!isMulp) {
						aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
								Mi.omax, Mi.omin, share.getInt("intv", 30));
						myGridView.setAdapter(aryAdapter);
						setGvTitle();
					}
					edit.putInt("list1", spinSel[4]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//滚动平均1
		spinner[5].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[5]!=arg2) {
					spinSel[5]=(byte)arg2;
					if(!isMulp) {
						aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
								Mi.omax, Mi.omin, share.getInt("intv", 30));
						setGvTitle();
					}
					edit.putInt("list2", spinSel[5]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//精度设置
		spinner[6].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spinSel[6]=(byte)arg2;
				arg0.setVisibility(View.VISIBLE); 
				if(spinSel[6]==0){edit.putBoolean("prec", false);if(spinSel[7]==0)mTextView2.setText("0.00");}
				else {edit.putBoolean("prec", true);if(spinSel[7]==0)mTextView2.setText("0.000");}
				edit.commit();
				if(resl!=0){
					if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{cl[1],cl[2],cl[3], 
							Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
					else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
						Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
					oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//计时方式
		spinner[7].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spinSel[7]=(byte)arg2;
				arg0.setVisibility(View.VISIBLE); 
				if(spinSel[7]==0){
					if(spinSel[6]==0)mTextView2.setText("0.00");
					else mTextView2.setText("0.000");
				} else if(spinSel[7]==1){
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
						spinner[0].setEnabled(true);
						sndscr.setEnabled(true);
						mButtonSst.setEnabled(true);
						chkb[11].setEnabled(true);
						chkb[11].setTextColor(cl[1]);
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
						spinner[0].setEnabled(true);
						sndscr.setEnabled(true);
						mButtonSst.setEnabled(true);
						chkb[11].setEnabled(true);
						chkb[11].setTextColor(cl[1]);
						wca=wcat;
						timk=0;
						if(!opnl){releaseWakeLock();screenOn=false;}
					} else mTextView2.setText("IMPORT");
				}
				edit.putInt("tiway", spinSel[7]);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//分组
		spinner[8].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[8]!=arg2) {
					spinSel[8]=(byte)arg2;
					//dbh = new DBHelper(DCTimer.this);
					getSession(spinSel[8]);
					oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
					if(isMulp) {
						if(resl!=0){
							mulp = new int[spinSel[13]+2][rest.length];
							Cursor c = dbh.query(spinSel[8]);
							for(int i=0; i<resl; i++) {
								c.moveToPosition(i);
								for(int j=0; j<spinSel[13]+2; j++){
									mulp[j][i] = c.getInt(7+j);
								}
							}
							c.close();
						}
						aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{cl[1],cl[2],cl[3], 
								Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
					}
					else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
							Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
					edit.putInt("group", spinSel[8]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//十字底面
		spinner[9].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[9]!=arg2) {
					spinSel[9]=(byte)arg2;
					edit.putInt("cface", spinSel[9]);
					edit.commit();
					if(spinSel[0]==1 && (sel2==0 || sel2==1 || sel2==5) && spinSel[1]==1)
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								Mi.sc="\n"+Cross.cross(cscrs, spinSel[9], spinSel[10]);
								handler.sendEmptyMessage(3);
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//颜色
		spinner[10].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[10]!=arg2) {
					spinSel[10]=(byte)arg2;
					edit.putInt("cside", spinSel[10]);
					edit.commit();
					if(spinSel[0]==1 && (sel2==0 || sel2==1 || sel2==5))
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								switch(spinSel[1]) {
								case 1:
									Mi.sc="\n"+Cross.cross(cscrs, spinSel[9], spinSel[10]); break;
								case 2:
									Mi.sc="\n"+Cross.xcross(cscrs, spinSel[10]); break;
								case 3:
									Mi.sc="\n"+EOline.eoLine(cscrs, spinSel[10]); break;
								case 4:
									Mi.sc="\n"+PetrusxRoux.roux(cscrs, spinSel[10]); break;
								case 5:
									Mi.sc="\n"+PetrusxRoux.petrus(cscrs, spinSel[10]); break;
								}
								handler.sendEmptyMessage(3);
							}
						}.start();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//采样频率
		spinner[11].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				spinSel[11]=(byte)arg2;
				arg0.setVisibility(View.VISIBLE);
				if(stm.creatAudioRecord((int)srate[spinSel[11]]));
				else {
					spinner[11].setSelection(1);
					spinSel[11]=1;
					edit.putInt("srate", 1);
					Toast.makeText(DCTimer.this, getResources().getString(R.string.sr_not_support), Toast.LENGTH_SHORT).show();
				}
				edit.putInt("srate", spinSel[11]);
				edit.commit();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//二阶底层
		spinner[2].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() { 
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[2]!=arg2) {
					spinSel[2]=(byte)arg2;
					edit.putInt("cube2l", spinSel[2]);
					edit.commit();
					if(spinSel[0]==0) {
						if(spinSel[2]==0)mTextView1.setText(cscrs);
						else new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								Mi.sc = "\n"+Cube2layer.cube2layer(cscrs, spinSel[2]);
								handler.sendEmptyMessage(3);
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
				arg0.setVisibility(View.VISIBLE); 
				if(spinSel[12]!=arg2) {
					spinSel[12]=(byte)arg2;
					switch(spinSel[12]){
					case 0:
						mTextView2.setTypeface(Typeface.create("monospace", 0));break;
					case 1:
						mTextView2.setTypeface(Typeface.create("serif", 0));break;
					case 2:
						mTextView2.setTypeface(Typeface.create("sans-serif", 0));break;
					case 3:
						mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf"));
						break;
					case 4:
						mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf"));
						break;
					case 5:
						mTextView2.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf"));
						break;
					}
					edit.putInt("tfont", spinSel[12]);
					edit.commit();
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		//分段计时
		spinner[13].setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
				if(spinSel[13]!=arg2) {
					spinSel[13]=(byte)arg2;
					myGridView.setNumColumns(arg2+3);
					if(resl!=0){
						mulp = new int[arg2+2][rest.length];
						multemp = new long[arg2+3];
						Cursor c = dbh.query(spinSel[8]);
						for(int i=0; i<resl; i++) {
							c.moveToPosition(i);
							for(int j=0; j<arg2+2; j++){
								mulp[j][i] = c.getInt(7+j);
							}
						}
						c.close();
						aryAdapter = new TimesAdapter(DCTimer.this, new String[(arg2+3)*(resl+1)], new int[]{cl[1],
								cl[2],cl[3],Mi.omax, Mi.omin}, share.getInt("intv", 30), arg2+3);
					} else aryAdapter = new TimesAdapter(DCTimer.this, null, new int[]{cl[1],
							cl[2],cl[3],Mi.omax, Mi.omin}, share.getInt("intv", 30), arg2+3);
					myGridView.setAdapter(aryAdapter);
					setGvTitle();
					edit.putInt("mulp", arg2);
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
					popView.setBackgroundColor(0xddece9d8);
					iv=(ImageView) popView.findViewById(R.id.ImageView1);
					Bitmap bm=Bitmap.createBitmap(width+7, (int)(width*0.75)+7, Config.ARGB_8888);
					Canvas c=new Canvas(bm);
					c.drawColor(Color.TRANSPARENT);
					Paint p=new Paint();
					p.setAntiAlias(true);
					Mi.drawScr(sel2, width, p, c);
					iv.setImageBitmap(bm);
					new AlertDialog.Builder(DCTimer.this)
					.setView(popView).setNegativeButton(getResources().getString(R.string.btn_close), null).show();
				} else Toast.makeText(DCTimer.this, getResources().getString(R.string.not_support), Toast.LENGTH_SHORT).show();
			}
		});
		mTextView1.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				scrt=true;
				setTouch(event.getAction());
				return true;
			}
		});
		mTextView2.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				scrt=false;
				if(!usess){
					if(spinSel[7]==0)setTouch(event.getAction());
					else if(spinSel[7]==1)inputTime(event.getAction());
				}
				return true;
			}
		});
		myGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int p, long arg3) {
				if(isMulp) {
					if(p/(spinSel[13]+3)<resl && p%(spinSel[13]+3)==0) singTime(p, spinSel[13]+3);
				}
				else if(p%3==0) {
					singTime(p, 3);
				} else if(p%3==1) {
					if(p/3>listnum[spinSel[4]]-2)showAlertDialog(1, p/3);
				} else if(p%3==2){
					if(p/3>listnum[spinSel[5]+1]-2)showAlertDialog(2, p/3);
				}
			}
		});
		oriavg.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				boolean m=false;
				for(int i=0;i<resl;i++)
					if(resd[i]!=0){m=true;break;}
				if(m)showAlertDialog(3, 0);
			}
		});
		clear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(resl==0) {
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.no_times)).
					setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i){}
					}).show();
				} else {
					new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_clear_session))
					.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j){
							//DBHelper helper = new DBHelper(getApplicationContext());
							dbh.clear(spinSel[8]);
							//dbh.close();
							resl=dbLastId=0;
							times=null;
							oriavg.setText(getResources().getString(R.string.session_average)+"0/0): N/A (N/A)");
							if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, null,
									new int[]{cl[1],cl[2],cl[3],-1,-1}, share.getInt("intv", 30), spinSel[13]+3);
							else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],
									cl[2],cl[3],cl[4]}, -1, -1, share.getInt("intv", 30));
							myGridView.setAdapter(aryAdapter);
							if(sestp[spinSel[8]]!=-1){
								sestp[spinSel[8]]=-1;
								edit.remove("sestp"+spinSel[8]);
								edit.commit();
							}
						}
					}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i){ }
					}).show();
				}
			}
		});
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
		reset.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_reset))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j){
						if(timk==0) wca=false;wcat=false;hidscr=false;conft=true;
						timmh=true;hidls=false;l1am=true;l2am=true;bgcolor=true;
						sqshp=false;fulls=false;invs=false;usess=false;opnl=false;
						Stackmat.inv=false;
						spinSel[1]=0;spinSel[2]=0;spinSel[3]=1;spinSel[4]=1;spinSel[5]=1;
						spinSel[6]=1;spinSel[7]=0;spinSel[11]=1;
						mTextView2.setTextSize(60);mTextView1.setTextSize(18);
						cl[0]=Color.rgb(102, 204, 255);cl[1]=Color.BLACK;
						cl[2]=Color.rgb(255, 0, 255);cl[3]=Color.RED;
						cl[4]=Color.rgb(0, 153, 0);
						for(int i=0;i<3;i++)chkb[i].setChecked(false);
						for(int i=3;i<8;i++)chkb[i].setChecked(true);
						for(int i=8;i<14;i++)chkb[i].setChecked(false);
						for(int i=0;i<8;i++)spinner[i].setSelection(spinSel[i]);
						spinner[11].setSelection(1);
						skb1.setProgress(10);
						skb2.setProgress(6);
						skb3.setProgress(10);
						skb4.setProgress(35);
						myTabHost.setBackgroundColor(cl[0]);
						tvl.setTextColor(cl[1]);
						for(int i=0;i<sttlen;i++)stt[i].setTextColor(cl[1]);
						for(int i=0;i<13;i++)chkb[i].setTextColor(cl[1]);
						mTextView1.setTextColor(cl[1]);
						mTextView2.setTextColor(cl[1]);
						if(resl!=0){
							if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)],
									new int[]{cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, 30, spinSel[13]+3);
							else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{
									cl[1],cl[2],cl[3],cl[4]}, Mi.omax, Mi.omin, 30);
							myGridView.setAdapter(aryAdapter);
						}
						releaseWakeLock();
						screenOn=false;
						edit.remove("wca");
						edit.remove("hidscr");
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
						edit.commit();
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i){ }
				}).show();
			}
		});
		bagc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[0], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						myTabHost.setBackgroundColor(color);cl[0]=color;
						edit.putInt("cl0", color);edit.putBoolean("bgcolor", true);
						edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
		bgpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");	//开启Pictures画面Type设定为image
				intent.setAction(Intent.ACTION_GET_CONTENT);//使用Intent.ACTION_GET_CONTENT这个Action
				startActivityForResult(intent, 1);//取得相片后返回本画面
			}
		});
		txtc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[1], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						tvl.setTextColor(color);
						for(int i=0;i<sttlen;i++)stt[i].setTextColor(color);
						for(int i=0;i<13;i++)chkb[i].setTextColor(color);
						mTextView1.setTextColor(color);
						mTextView2.setTextColor(color);
						cl[1]=color;
						if(resl!=0){
							if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
									cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
							else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{color,cl[2],cl[3],cl[4]}, 
									Mi.omax, Mi.omin, share.getInt("intv", 30));
							myGridView.setAdapter(aryAdapter);
						}
						setGvTitle();
						edit.putInt("cl1", color);edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
		orbc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[2], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						cl[2]=color;
						if(resl!=0){
							if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
									cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
							else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
									Mi.omax, Mi.omin, share.getInt("intv", 30));
							myGridView.setAdapter(aryAdapter);
						}
						edit.putInt("cl2", color);edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
		orwc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[3], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						cl[3]=color;
						if(resl!=0){
							if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
									cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
							else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],color,cl[4]}, 
									Mi.omax, Mi.omin, share.getInt("intv", 30));
							myGridView.setAdapter(aryAdapter);
						}
						edit.putInt("cl3", color);edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
		avbc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ColorPicker(context, cl[4], new ColorPicker.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						cl[4]=color;
						if(resl!=0 && !isMulp){
							aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],
									cl[2],cl[3],color}, Mi.omax, Mi.omin, share.getInt("intv", 30));
							myGridView.setAdapter(aryAdapter);
						}
						edit.putInt("cl4", color);edit.commit();
					}
				});
				dialog.setTitle(getResources().getString(R.string.select_color));
				dialog.show();
			}
		});
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
		csPyrm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] colors={share.getInt("csp1", 0xff009900), share.getInt("csp2", 0xffff0000),
						share.getInt("csp3", 0xffffff00), share.getInt("csp4", 0xff0000ff)};
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
							spinner[8].setSelection(spinSel[8]);
						}
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(screenOn)releaseWakeLock();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(screenOn)acquireWakeLock();
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
			if(seekBar.getId()==R.id.seekb1){
				stt[3].setText(getResources().getString(R.string.timer_size)+ (seekBar.getProgress()+50));
				edit.putInt("ttsize", seekBar.getProgress()+50);
				mTextView2.setTextSize(seekBar.getProgress()+50);
			} else if(seekBar.getId()==R.id.seekb2){
				stt[4].setText(getResources().getString(R.string.scrsize)+ (seekBar.getProgress()+12));
				edit.putInt("stsize", seekBar.getProgress()+12);
				mTextView1.setTextSize(seekBar.getProgress()+12);
			} else if(seekBar.getId()==R.id.seekb3){
				stt[10].setText(getResources().getString(R.string.row_spacing)+ (seekBar.getProgress()+20));
				if(resl!=0){
					if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
							cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, seekBar.getProgress()+20, spinSel[13]+3);
					else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
							Mi.omax, Mi.omin, seekBar.getProgress()+20);
					myGridView.setAdapter(aryAdapter);
				}
				edit.putInt("intv", seekBar.getProgress()+20);
			} else if(seekBar.getId()==R.id.seekb4){
				if(!bgcolor){
					setBgPic(bitmap, seekBar.getProgress());
				}
				edit.putInt("opac", seekBar.getProgress());
			} else if(seekBar.getId()==R.id.seekb5){
				tapTime=seekBar.getProgress();
				stt[31].setText(getResources().getString(R.string.time_tap)+ (tapTime/20D));
				edit.putInt("tapt", tapTime);
			}
			edit.commit();
		}
	}
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			if(buttonView.getId()==R.id.check1) {
				if(isChecked) {if(timk==0) wca=true;wcat=true;edit.putBoolean("wca", true);}
				else {if(timk==0) wca=false;wcat=false;edit.putBoolean("wca", false);}
			} else if(buttonView.getId()==R.id.check2) {
				if(isChecked){opnl=true;acquireWakeLock();screenOn=true;edit.putBoolean("scron", true);}
				else {opnl=false;if(timk!=1)releaseWakeLock();screenOn=false;edit.putBoolean("scron", false);}
			} else if(buttonView.getId()==R.id.check3) {
				if(isChecked){hidscr=true;edit.putBoolean("hidscr", true);}
				else {hidscr=false;edit.putBoolean("hidscr", false);}
			} else if(buttonView.getId()==R.id.check4) {
				if(isChecked){conft=true;edit.putBoolean("conft", true);}
				else {conft=false;edit.putBoolean("conft", false);}
			} else if(buttonView.getId()==R.id.check5) {
				if(isChecked){hidls=false;edit.putBoolean("hidls", false);}
				else {hidls=true;edit.putBoolean("hidls", true);}
			} else if(buttonView.getId()==R.id.check6) {
				if(isChecked){timmh=true;edit.putBoolean("timmh", true);}
				else {timmh=false;edit.putBoolean("timmh", false);}
				if(resl>0){
					if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
							cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
					else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
							Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
				}
			} else if(buttonView.getId()==R.id.check7) {
				if(isChecked) {
					sqshp=true;edit.putBoolean("sqshp", true);
					if(spinSel[0]==8)
						new Thread() {
							public void run() {
								handler.sendEmptyMessage(8);
								Mi.sc=" "+Sq1Shape.solve(cscrs);
								handler.sendEmptyMessage(1);
							}
						}.start();
				} else {
					sqshp=false;edit.putBoolean("sqshp", false);
					if(spinSel[0]==8) mTextView1.setText(cscrs);
				}
			} else if(buttonView.getId()==R.id.check8) {
				if(isChecked){
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fulls=true;edit.putBoolean("fulls", true);
				} else {
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fulls=false;edit.putBoolean("fulls", false);
				}
			} else if(buttonView.getId()==R.id.check9) {
				if(isChecked){invs=true;Stackmat.inv=true;edit.putBoolean("invs", true);}
				else {invs=false;Stackmat.inv=false;edit.putBoolean("invs", false);}
			} else if(buttonView.getId()==R.id.check10) {
				if(isChecked){
					usess=true;edit.putBoolean("usess", true);
					spinner[11].setEnabled(false);
					mTextView2.setText("OFF");
					if(!stm.isStart)stm.start();
				} else {
					usess=false;edit.putBoolean("usess", false);
					spinner[11].setEnabled(true);
					if(stm.isStart)stm.stop();
					if(spinSel[7]==0){
						if(spinSel[6]==0)mTextView2.setText("0.00");
						else mTextView2.setText("0.000");
					} else if(spinSel[7]==1){
						mTextView2.setText("IMPORT");
					}
				}
			} else if(buttonView.getId()==R.id.check11) {
				if(isChecked){selSes=true;edit.putBoolean("selses", true);}
				else {selSes=false;edit.putBoolean("selses", false);}
			} else if(buttonView.getId()==R.id.check12) {
				if(isChecked){
					isMulp=true; spinner[13].setEnabled(true);
					edit.putBoolean("ismulp", true);
					myGridView.setNumColumns(spinSel[13]+3);
					multemp = new long[spinSel[13]+3];
					if(resl>0){
						mulp = new int[spinSel[13]+2][rest.length];
						Cursor c = dbh.query(spinSel[8]);
						for(int i=0; i<resl; i++) {
							c.moveToPosition(i);
							for(int j=0; j<spinSel[13]+2; j++){
								mulp[j][i] = c.getInt(7+j);
							}
						}
						c.close();
						aryAdapter = new TimesAdapter(DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{cl[1],
								cl[2],cl[3],Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
					} else aryAdapter = new TimesAdapter(DCTimer.this, null, new int[]{cl[1],
							cl[2],cl[3],Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
					myGridView.setAdapter(aryAdapter);
				} else {
					isMulp=false; spinner[13].setEnabled(false);
					edit.putBoolean("ismulp", false);
					myGridView.setNumColumns(3);
					mulp = null;
					if(resl>0){
						aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
								Mi.omax, Mi.omin, share.getInt("intv", 30));
					} else aryAdapter=new TimesAdapter (DCTimer.this, null, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
							Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
				}
				setGvTitle();
			} else if(buttonView.getId()==R.id.lcheck1) {
				if(isChecked){
					l1am=true;edit.putBoolean("l1am", true);
					if(!isMulp)setGvTitle();
				} else {
					l1am=false;edit.putBoolean("l1am", false);
					if(!isMulp)setGvTitle();
				}
				if(resl>0 && !isMulp){
					aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],
							cl[2],cl[3],cl[4]}, Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
				}
			} else if(buttonView.getId()==R.id.lcheck2) {
				if(isChecked){
					l2am=true;edit.putBoolean("l2am", true);
					if(!isMulp)setGvTitle();
				} else {
					l2am=false;edit.putBoolean("l2am", false);
					if(!isMulp)setGvTitle();
				}
				if(resl>0 && !isMulp){
					aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
							Mi.omax, Mi.omin, share.getInt("intv", 30));
					myGridView.setAdapter(aryAdapter);
				}
			}
			edit.commit();
		}
	};
	private String[] set2ndsel() {
		String[] s = null;
		switch(spinSel[0]){
		case 0:s=getResources().getStringArray(R.array.sel21);break;
		case 1:s=getResources().getStringArray(R.array.sel22);break;
		case 2:s=getResources().getStringArray(R.array.sel23);break;
		case 3:s=getResources().getStringArray(R.array.sel24);break;
		case 4:s=getResources().getStringArray(R.array.sel25);break;
		case 5:s=getResources().getStringArray(R.array.sel25);break;
		case 6:s=getResources().getStringArray(R.array.sel26);break;
		case 7:s=getResources().getStringArray(R.array.sel27);break;
		case 8:s=getResources().getStringArray(R.array.sel28);break;
		case 9:s=getResources().getStringArray(R.array.sel29);break;
		case 10:s=getResources().getStringArray(R.array.sel210);break;
		case 11:s=getResources().getStringArray(R.array.sel211);break;
		case 12:s=getResources().getStringArray(R.array.sel212);break;
		case 13:s=getResources().getStringArray(R.array.sel218);break;
		case 14:s=getResources().getStringArray(R.array.sel213);break;
		case 15:s=getResources().getStringArray(R.array.sel219);break;
		case 16:s=getResources().getStringArray(R.array.sel214);break;
		case 17:s=getResources().getStringArray(R.array.sel215);break;
		case 18:s=getResources().getStringArray(R.array.sel216);break;
		case 19:s=getResources().getStringArray(R.array.sel217);break;
		case 20:s=getResources().getStringArray(R.array.sel220);break;
		}
		if(sel2>=s.length){
			sndscr.setText(s[0]);
			sel2 = 0;
		} else sndscr.setText(s[sel2]);
		return s;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		menu.add(Menu.NONE, 0, 0, getResources().getString(R.string.menu_share));
		menu.add(Menu.NONE, 1, 1, getResources().getString(R.string.menu_weibo));
		menu.add(Menu.NONE, 2, 2, getResources().getString(R.string.menu_about));
		menu.add(Menu.NONE, 3, 3, getResources().getString(R.string.menu_exit));
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case 0:
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");	//纯文本
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, getShareContext());
			startActivity(Intent.createChooser(intent, getTitle()));
			break;
		case 1:
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
		case 2:
			LayoutInflater factory = LayoutInflater.from(DCTimer.this);
			final View view = factory.inflate(R.layout.dlg_about, null);
			new AlertDialog.Builder(DCTimer.this).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i){}
			}).show();
			break;
		case 3:
			new AlertDialog.Builder(DCTimer.this).setMessage(getResources().getString(R.string.confirm_exit))
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int j){
					dbh.close();
					edit.putInt("sel", spinSel[0]);
					if(spinSel[0]==11 && sel2==4)edit.putInt("sel2", 3);
					else edit.putInt("sel2", sel2);
					edit.commit();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i){ }
			}).show();
		}
		return true;
	}
	private void setGvTitle() {
		if(isMulp){
			String[] title = new String[spinSel[13]+3];
			title[0] = getResources().getString(R.string.time);
			for(int i=1; i<spinSel[13]+3; i++) title[i] = "P"+i;
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(spinSel[13]+3);
			gvTitle.setAdapter(ta);
		}
		else {
			String[] title = {getResources().getString(R.string.time),
					(l1am?"avg of ":"mean of ")+listnum[spinSel[4]],
					(l2am?"avg of ":"mean of ")+listnum[spinSel[5]+1]};
			TitleAdapter ta = new TitleAdapter(DCTimer.this, title, cl[1]);
			gvTitle.setNumColumns(3);
			gvTitle.setAdapter(ta);
		}
	}
	private String getShareContext(){
		String s1=getResources().getString(R.string.share_c1).replace("$len", ""+resl).replace("$scrtype", getScrType())
				.replace("打乱打乱", "打乱").replace("$best", Mi.distime(Mi.omin, false)).replace("$mean", spinSel[6]==0?Mi.distime(Mi.oravg*10):Mi.distime(Mi.oravg));
		String s2=(resl>listnum[spinSel[4]])?getResources().getString(R.string.share_c2).replace("$flen", ""+listnum[spinSel[4]]).replace("$favg", Mi.distime(Mi.bavg[0])):"";
		String s3=getResources().getString(R.string.share_c3);
		return s1+s2+s3;
	}
	private String getScrType(){
		String[] mItems=getResources().getStringArray(R.array.cubeStr);
		String[] s = null;
		switch(spinSel[0]){
		case 0:s=getResources().getStringArray(R.array.sel21);break;
		case 1:s=getResources().getStringArray(R.array.sel22);break;
		case 2:s=getResources().getStringArray(R.array.sel23);break;
		case 3:s=getResources().getStringArray(R.array.sel24);break;
		case 4:s=getResources().getStringArray(R.array.sel25);break;
		case 5:s=getResources().getStringArray(R.array.sel25);break;
		case 6:s=getResources().getStringArray(R.array.sel26);break;
		case 7:s=getResources().getStringArray(R.array.sel27);break;
		case 8:s=getResources().getStringArray(R.array.sel28);break;
		case 9:s=getResources().getStringArray(R.array.sel29);break;
		case 10:s=getResources().getStringArray(R.array.sel210);break;
		case 11:s=getResources().getStringArray(R.array.sel211);break;
		case 12:s=getResources().getStringArray(R.array.sel212);break;
		case 13:s=getResources().getStringArray(R.array.sel218);break;
		case 14:s=getResources().getStringArray(R.array.sel213);break;
		case 15:s=getResources().getStringArray(R.array.sel219);break;
		case 16:s=getResources().getStringArray(R.array.sel214);break;
		case 17:s=getResources().getStringArray(R.array.sel215);break;
		case 18:s=getResources().getStringArray(R.array.sel216);break;
		case 19:s=getResources().getStringArray(R.array.sel217);break;
		}
		return mItems[spinSel[0]]+"-"+s[sel2];
	}
	private void searchSesType(){
		int type=0, idx=-1;
		for(int i=0; i<9; i++){
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
		if(type==2 || (sestp[spinSel[8]] != -1 && type == 1)){
			spinner[8].setSelection(idx);
			//dbh = new DBHelper(DCTimer.this);
			getSession(idx);
			edit.putInt("group", idx);
			edit.commit();
		}
	}
	private void setScrType(){
		switch(spinSel[0]){
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
			scrType=spinSel[0];break;
		case 11:
			if(sel2<3)scrType=11;
			else if(sel2<5)scrType=12;
			else scrType=sel2+8;
			break;
		case 12:
		case 13:
		case 14:
		case 15:
			scrType=spinSel[0]+8;break;
		case 16:
			scrType=sel2+24;break;
		case 17:
			scrType=1;break;
		case 18:
			scrType=sel2+30;break;
		case 19:
			scrType=6;break;
		case 20:
			scrType=sel2+32;break;
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
					mTextView2.setTextColor(Color.GREEN);
					multemp[spinSel[13]+2-mulpCount] = System.currentTimeMillis();
				}
				else {
					timer.count();
					new Thread(new Runnable(){
						public void run(){
							try {
								Thread.sleep(25);
							} catch (InterruptedException e) {e.printStackTrace();}
							handler.sendEmptyMessage(4);
						}
					}).start();
					if(isMulp)multemp[spinSel[13]+2]=timer.time1;
					spinner[0].setEnabled(true);
					sndscr.setEnabled(true);
					mButtonSst.setEnabled(true);
					chkb[11].setEnabled(true);
					chkb[11].setTextColor(cl[1]);
				}
			} else {
				if(!scrt || timk==2) {
					if(tapTime == 0 || (wca && timk==0)) {
						mTextView2.setTextColor(Color.GREEN);
						canStart=true;
					} else {
						timer.isTapped = true;
						if(timk==0)mTextView2.setTextColor(Color.RED);
						else mTextView2.setTextColor(Color.YELLOW);
						timer.tap();
					}
					if(hidscr)mTextView1.setText("");
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(timk==0){
				if(scrt)newScr();
				else {
					if(tapTime ==0 || canStart) {
						if(wca)timer.v=1;
						timer.count();
						if(wca)timk=2;
						else timk=1;
						if(isMulp){
							mulpCount=spinSel[13]+1;
							multemp[0]=timer.time0;
						}
						else mulpCount=0;
						acquireWakeLock();screenOn=true;
						spinner[0].setEnabled(false);
						sndscr.setEnabled(false);
						mButtonSst.setEnabled(false);
						chkb[11].setEnabled(false);
						chkb[11].setTextColor((cl[1]&0xffffff)|(127<<24));
					} else {
						timer.isTapped = false;
						timer.stopt();
						mTextView2.setTextColor(cl[1]);
					}
				}
			} else if(timk==1){
				if(mulpCount!=0) {
					mulpCount--;
					mTextView2.setTextColor(cl[1]);
				} else {
					wca=wcat;
					if(!wca){isp2=0;idnf=true;}
					//mTextView2.setText(Mi.distime((int)timer.time));
					if(idnf) confirmTime((int)timer.time);
					else {
						if(conft)
							new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.time_dnf)).setMessage(getResources().getString(R.string.confirm_adddnf))
							.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int j){
									record((int)timer.time,(byte)0,(byte)0);
								}
							}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int which) {
									d.dismiss();newScr();
								}
							}).show();
						else record((int)timer.time,(byte)0,(byte)0);
					}
					timk=0;
					if(!opnl){releaseWakeLock();screenOn=false;}
				}
			} else {
				if(tapTime ==0 || canStart) {
					if(timer.v==1){isp2=0;idnf=true;}
					else if(timer.v==2){isp2=2000;idnf=true;}
					else if(timer.v==3){isp2=0;idnf=false;}
					timer.v=0;
					timer.count();
					timk=1;
					if(isMulp){
						multemp[0]=timer.time0;
					}
					acquireWakeLock();screenOn=true;
					spinner[0].setEnabled(false);
					sndscr.setEnabled(false);
					mButtonSst.setEnabled(false);
					chkb[11].setEnabled(false);
					chkb[11].setTextColor((cl[1]&0xffffff)|(127<<24));
				} else {
					timer.isTapped = false;
					timer.stopt();
					mTextView2.setTextColor(Color.RED);
				}
			}
		}
	}
	void inputTime(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mTextView2.setTextColor(Color.GREEN);
			if(hidscr)mTextView1.setText("");
			break;
		case MotionEvent.ACTION_UP:
			mTextView2.setTextColor(Color.BLACK);
			LayoutInflater factory = LayoutInflater.from(DCTimer.this);
			final View view = factory.inflate(R.layout.editbox_layout, null);
			final EditText editText=(EditText)view.findViewById(R.id.editText1);
			//editText.setInputType(InpuType.);
			new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.enter_time)).setView(view)
			.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String time = Mi.convStr(editText.getText().toString());
					if(time.equals("Error") || Mi.convTime(time)==0)Toast.makeText(DCTimer.this, getResources().getString(R.string.illegal), Toast.LENGTH_SHORT).show();
					else record(Mi.convTime(time), (byte) 0, (byte) 1);
					newScr();
				}
			}).setNegativeButton(getResources().getString(R.string.btn_cancel), null).show();
		}
	}
	void record(int time, byte p, byte d) {
		if(resl>=rest.length) {
			String[] scr2=new String[scrst.length+12];
			byte[] rp2=new byte[resp.length+12], rd2=new byte[resd.length+12];
			int res2[]=new int[rest.length+12];
			for(int i=0;i<resl;i++) {
				scr2[i]=scrst[i];rp2[i]=resp[i];rd2[i]=resd[i];res2[i]=rest[i];
			}
			scrst=scr2;resp=rp2;resd=rd2;rest=res2;
			if(isMulp) {
				int[][] mulp2 = new int[mulp.length][rest.length];
				for(int i=0;i<resl-1;i++) {
					for(int j=0; j<mulp.length; j++){
						mulp2[j][i]=mulp[j][i];
					}
				}
				mulp=mulp2;
			}
			System.gc();
		}
		scrst[resl]=cscrs;resp[resl]=p;resd[resl]=d;rest[resl++]=time;
		if(isMulp) {
			boolean temp = true;
			for(int i=0; i<spinSel[13]+2; i++){
				if(temp)
					mulp[i][resl-1]=(int)(multemp[i+1]-multemp[i]);
				else mulp[i][resl-1]=0;
				if(mulp[i][resl-1]<0 || mulp[i][resl-1]>rest[resl-1]){
					mulp[i][resl-1]=0; temp=false;
				}
			}
		}
		ContentValues cv = new ContentValues();
		cv.put("id", ++dbLastId);
		cv.put("rest", time);
		cv.put("resp", p);
		cv.put("resd", d);
		cv.put("scr", cscrs);
		cv.put("time", formatter.format(new Date()));
		if(isMulp){
			if(spinSel[13]>3)cv.put("p6", mulp[5][resl-1]);
			if(spinSel[13]>2)cv.put("p5", mulp[4][resl-1]);
			if(spinSel[13]>1)cv.put("p4", mulp[3][resl-1]);
			if(spinSel[13]>0)cv.put("p3", mulp[2][resl-1]);
			if(spinSel[13]>=0){
				cv.put("p2", mulp[1][resl-1]);
				cv.put("p1", mulp[0][resl-1]);
			}
		}
		DBHelper helper = new DBHelper(getApplicationContext());
		helper.insert(spinSel[8], cv);
		helper.close();
		if(times==null) times=new String[3];
		else times=new String[times.length+3];
		oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
		if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{
				cl[1],cl[2],cl[3], Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
		else aryAdapter=new TimesAdapter (this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
				Mi.omax, Mi.omin, share.getInt("intv", 30));
		myGridView.setAdapter(aryAdapter);
		if(selSes && sestp[spinSel[8]] != scrType) {
			sestp[spinSel[8]]=(short) scrType;
			edit.putInt("sestp"+spinSel[8], scrType);
			edit.commit();
		}
		newScr();
	}
	void change(int idx, byte p, byte d) {
		if(resp[idx]==p && resd[idx]==d);
		else {
			resp[idx]=p;
			resd[idx]=d;
			//DBHelper helper=new DBHelper(getApplicationContext());
			Cursor c = dbh.query(spinSel[8]);
			c.moveToPosition(idx);
			int id=c.getInt(0);
			c.close();
			dbh.update(spinSel[8], id, p, d);
			//dbh.close();
			oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
			if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, new String[(spinSel[13]+3)*(resl+1)], new int[]{cl[1],cl[2],cl[3], 
					Mi.omax, Mi.omin}, share.getInt("intv", 30), spinSel[13]+3);
			else aryAdapter=new TimesAdapter (this, times, new int[]{cl[1],cl[2],cl[3],cl[4]},
					Mi.omax, Mi.omin, share.getInt("intv", 30));
			myGridView.setAdapter(aryAdapter);
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
				spinner[0].setEnabled(true);
				sndscr.setEnabled(true);
				mButtonSst.setEnabled(true);
				chkb[11].setEnabled(true);
				chkb[11].setTextColor(cl[1]);
				wca=wcat;
				if(!wca){isp2=0;idnf=true;}
				if(idnf) confirmTime((int)timer.time);
				else {
					if(conft)
						new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.time_dnf)).setMessage(getResources().getString(R.string.confirm_adddnf))
						.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j){
								record((int)timer.time,(byte)0,(byte)0);
							}
						}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface d, int which) {
								d.dismiss();newScr();
							}
						}).show();
					else record((int)timer.time,(byte)0,(byte)0);
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
				spinner[0].setEnabled(true);
				sndscr.setEnabled(true);
				mButtonSst.setEnabled(true);
				chkb[11].setEnabled(true);
				chkb[11].setTextColor(cl[1]);
				wca=wcat;
				timk=0;
				if(!opnl){releaseWakeLock();screenOn=false;}
			} else if(event.getRepeatCount() == 0) {
				new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.confirm_exit))
				.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j){
						dbh.close();
						edit.putInt("sel", spinSel[0]);
						if(spinSel[0]==11 && sel2==4)edit.putInt("sel2", 3);
						else edit.putInt("sel2", sel2);
						edit.commit();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}).setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface d, int i){d.dismiss();}
				}).show();
			}
		}
		return false;
	}
	private void showAlertDialog(int i, int j){
		String t=null;
		switch(i){
		case 1:
			t=(l1am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spinSel[4]]);
			slist=l1am?ao(listnum[spinSel[4]], j):mo(listnum[spinSel[4]], j);
			break;
		case 2:
			t=(l2am?getResources().getString(R.string.sta_avg):getResources().getString(R.string.sta_mean)).replace("len", ""+listnum[spinSel[5]+1]);
			slist=l2am?ao(listnum[spinSel[5]+1], j):mo(listnum[spinSel[5]+1], j);
			break;
		case 3:
			t=getResources().getString(R.string.sta_session_mean);
			slist=oriAverage();
			break;
		}
		new AlertDialog.Builder(DCTimer.this).setTitle(t).setMessage(slist)
		.setPositiveButton(getResources().getString(R.string.btn_copy), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i){
				ClipboardManager clip=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(slist);
				Toast.makeText(DCTimer.this, getResources().getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
			}
		}).setNegativeButton(getResources().getString(R.string.btn_close), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i){}
		}).show();
	}
	private void newScr() {
		if((spinSel[0]==0 && spinSel[2]!=0) || (spinSel[0]==1 && (sel2!=0 || (spinSel[1]!=0 && (sel2==0 || sel2==1 || sel2==5)))) ||
				(spinSel[0]==8 && (sel2==1 || sqshp)) ||
				(spinSel[0]==11 && (sel2==4 || sel2==5)) ||
				(spinSel[0]==17 && (sel2==0 || sel2==1 || sel2==6)) ||
				spinSel[0]==20) {
			new Thread() {
				public void run() {
					handler.sendEmptyMessage(2);
					cscrs=Mi.SetScr((spinSel[0]<<4)|sel2);
					if((spinSel[0]==0 && spinSel[2]!=0) ||
							(spinSel[1]!=0 && spinSel[0]==1 && (sel2==0 || sel2==1 || sel2==5)))
						handler.sendEmptyMessage(3);
					else if(spinSel[0]==8 && sqshp)handler.sendEmptyMessage(1);
					else handler.sendEmptyMessage(0);
				}
			}.start();
		} else {
			cscrs=Mi.SetScr((spinSel[0]<<4)|sel2);
			mTextView1.setText(cscrs);
		}
	}
	public void confirmTime(int utime){
		final int time=utime;
		if(conft)
			new AlertDialog.Builder(DCTimer.this).setTitle(getResources().getString(R.string.show_time)+Mi.distime(time)).setItems(R.array.rstcon,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:record(time + isp2, (byte) 0, (byte) 1);break;
					case 1:record(time + isp2, (byte) 1, (byte) 1);break;
					case 2:record(time, (byte) 0, (byte) 0);break;
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.btn_cancel),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d,int which) {
					d.dismiss();newScr();
				}
			}).show();
		else record(time+isp2, (byte)0, (byte)1);
	}
	
	public String oriAverage() {
		StringBuffer sb=new StringBuffer();
		int n=resl;
		for(int i=0;i<resl;i++) {
			if(resd[i]==0) n--;
		}
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_solve)+n+"/"+resl+"\n");
		sb.append(getResources().getString(R.string.stat_mean)+(spinSel[6]==0?Mi.distime(Mi.oravg*10):Mi.distime(Mi.oravg))+"\n");
		sb.append(getResources().getString(R.string.stat_sd)+Mi.standDev(Mi.orsdv)+"\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(Mi.omin, false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(Mi.omax, false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		Cursor c = dbh.query(spinSel[8]);
		for(int i=0;i<resl;i++){
			if(!hidls)sb.append("\n"+(i+1)+". ");
			sb.append(Mi.distime(i, true));
			c.moveToPosition(i);
			String s = c.getString(6);
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(hidls && i<resl-1)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[i]);
		}
		return sb.toString();
	}
	public String ao(int n, int i) {
		StringBuffer sb=new StringBuffer();
		int max,min,dnf=0,dnidx;
		int[] idx=null;
		int cavg, csdv=-1,ind=1;
		int trim=n/20;if(n%20!=0)trim++;
		double sum=0,sum2=0;
		max=min=dnidx=i-n+1;
		boolean m=false;
		for(int j=i-n+1;j<=i;j++){
			if(resd[j]!=0 && !m){min=j;m=true;}
			if(resd[j]==0){max=j;dnidx=j;dnf++;}
		}
		if(dnf>trim)cavg=0;
		else {
			if(n<20){
				for (int j=i-n+2;j<=i;j++) {
					if(dnf==0 && rest[j]+resp[j]*2000>rest[max]+resp[max]*2000)max=j;
					if(j!=dnidx && rest[j]+resp[j]*2000<=rest[min]+resp[min]*2000)min=j;
				}
				for(int j=i-n+1;j<=i;j++) {
					if(j!=max && j!=min){
						if(spinSel[6]==1)sum+=rest[j]+resp[j]*2000;
						else sum+=(rest[j]+resp[j]*2000+5)/10;
						if(spinSel[6]==1)sum2+=Math.pow(rest[j]+resp[j]*2000, 2);
						else sum2+=Math.pow((rest[j]+resp[j]*2000+5)/10, 2);
					}
				}
				cavg=(int) (sum/(n-2)+0.5);
				csdv=(int) (Math.sqrt(sum2/(n-2)-sum*sum/(n-2)/(n-2))+(spinSel[6]==1?0:0.5));
			} else {
				int[] data=new int[n-dnf];idx=new int[n-dnf];int len=0;
				ArrayList<Integer> dnfIdx=new ArrayList<Integer>(trim);
				for(int j=i-n+1;j<=i;j++) {
					if(resd[j]!=0) {
						data[len]=rest[j]+resp[j]*2000;
						idx[len++]=j;
					} else dnfIdx.add(j);
				}
				quickSort(data, idx, 0, n-dnf-1);
				min=idx[0];max=dnf==0?idx[idx.length-1]:dnfIdx.get(0);
				for(int j=trim;j<n-trim;j++) {
					if(spinSel[6]==1)sum+=data[j];
					else sum+=(data[j]+5)/10;
					if(spinSel[6]==1)sum2+=Math.pow(data[j], 2);
					else sum2+=Math.pow((data[j]+5)/10, 2);
				}
				for(int j=trim;j<trim*2-dnf;j++){
					idx[j]=idx[n-(trim*2-j)];
				}
				if(dnf>1)
					for(int j=0;j<dnfIdx.size()-1;j++)
						idx[trim*2-dnf+j]=dnfIdx.get(j+1);
				cavg=(int) (sum/(n-trim*2)+0.5);
				csdv=(int) Math.sqrt(sum2/(n-trim*2)-sum*sum/Math.pow(n-trim*2, 2));
			}
		}
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_avg)+(spinSel[6]==0?Mi.distime(cavg*10):Mi.distime(cavg))+"\n");
		sb.append(getResources().getString(R.string.stat_sd)+Mi.standDev(csdv)+"\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(min,false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(max,false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		Cursor c = dbh.query(spinSel[8]);
		for(int j=i-n+1;j<=i;j++) {
			c.moveToPosition(j);
			String s = c.getString(6);
			if(!hidls)sb.append("\n"+(ind++)+". ");
			if(j==min||j==max || (n>20&&(idx[1]==j||idx[2]==j||idx[3]==j||idx[4]==j)) || (n==100&&(idx[5]==j||idx[6]==j||idx[7]==j||idx[8]==j)))sb.append("(");
			sb.append(Mi.distime(j, false));
			if(s!=null && !s.equals(""))sb.append("["+s+"]");
			if(j==min||j==max || (n>20&&(idx[1]==j||idx[2]==j||idx[3]==j||idx[4]==j)) || (n==100&&(idx[5]==j||idx[6]==j||idx[7]==j||idx[8]==j)))sb.append(")");
			if(hidls && j<i)sb.append(", ");
			if(!hidls)sb.append("  "+scrst[j]);
		}
		return sb.toString();
	}
	private static void quickSort(int[] a, int[] i, int lo0, int hi0) {
		int lo = lo0, hi = hi0;
		if (lo >= hi) return;
		boolean transfer=true;
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
		quickSort(a,i, lo0, lo);
		quickSort(a,i, hi, hi0);
	}
	public String mo(int n, int i) {
		StringBuffer sb=new StringBuffer();
		int max,min,dnf=0;
		int cavg, csdv=-1,ind=1;
		double sum=0,sum2=0;
		max=min=i-n+1;
		boolean m=false;
		for(int j=i-n+1;j<=i;j++){
			if(resd[j]!=0 && !m){min=j;m=true;}
			if(resd[j]==0){max=j;dnf++;}
		}
		if(dnf>0)cavg=0;
		else {
			for (int j=i-n+1;j<=i;j++) {
				if(rest[j]+resp[j]*2000>rest[max]+resp[max]*2000)max=j;
				if(rest[j]+resp[j]*2000<=rest[min]+resp[min]*2000)min=j;
				if(spinSel[6]==1)sum+=(double)(rest[j]+resp[j]*2000);
				else sum+=(rest[j]+resp[j]*2000+5)/10;
				if(spinSel[6]==1)sum2+=Math.pow(rest[j]+resp[j]*2000, 2);
				else sum2+=Math.pow((rest[j]+resp[j]*2000+5)/10, 2);
			}
			cavg=(int) (sum/n+0.5);
			csdv=(int) (Math.sqrt(sum2/n-sum*sum/n/n)+(spinSel[6]==1?0:0.5));
		}
		sb.append(getResources().getString(R.string.stat_title)+new java.sql.Date(new Date().getTime())+"\n");
		sb.append(getResources().getString(R.string.stat_avg)+(spinSel[6]==0?Mi.distime(cavg*10):Mi.distime(cavg))+"\n");
		sb.append(getResources().getString(R.string.stat_sd)+Mi.standDev(csdv)+"\n");
		sb.append(getResources().getString(R.string.stat_best)+Mi.distime(min,false)+"\n");
		sb.append(getResources().getString(R.string.stat_worst)+Mi.distime(max,false)+"\n");
		sb.append(getResources().getString(R.string.stat_list));
		if(hidls)sb.append("\n");
		Cursor c = dbh.query(spinSel[8]);
		for(int j=i-n+1;j<=i;j++) {
			c.moveToPosition(j);
			if(!hidls)sb.append("\n"+(ind++)+". ");
			sb.append(Mi.distime(j, false));
			String s = c.getString(6);
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
		if (wakeLock ==null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.acquire();
		}
	}
	private void releaseWakeLock() {
		if (wakeLock !=null&& wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock =null;
		}
	}
	private void getSession(int i) {
		Cursor c = dbh.query(i);
		resl = c.getCount();
		rest = new int[resl+12];
		resp = new byte[resl+12];
		resd = new byte[resl+12];
		scrst = new String[resl+12];
		if(resl!=0) {
			c.moveToFirst();
			for(int k=0; k<resl; k++){
				rest[k]=c.getInt(1);
				resp[k]=(byte)c.getInt(2);
				resd[k]=(byte)c.getInt(3);
				scrst[k]=c.getString(4);
				c.moveToNext();
			}
			c.moveToLast();
			dbLastId=c.getInt(0);
			times=new String[resl*3];
		} else {
			times=null;
			dbLastId=0;
		}
		c.close();
	}
	private void singTime(final int p, final int col) {
		final Cursor c = dbh.query(spinSel[8]);
		c.moveToPosition(p/col);
		final int id = c.getInt(0);
		String time=c.getString(5);
		final String note=c.getString(6);
		if(time!=null)time="\n("+time+")";
		else time = "";
		LayoutInflater factory = LayoutInflater.from(DCTimer.this);
		final View view = factory.inflate(R.layout.singtime, null);
		final EditText editText=(EditText)view.findViewById(R.id.etnote);
		final TextView tvTime=(TextView)view.findViewById(R.id.st_time);
		final TextView tvScr=(TextView)view.findViewById(R.id.st_scr);
		tvTime.setText(getResources().getString(R.string.show_time)+Mi.distime(p/col,true)+time);
		tvScr.setText(scrst[p/col]);
		if(resd[p/col]==0){
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(resp[p/col]==1){
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe2);
			rb.setChecked(true);
		} else {
			RadioButton rb = (RadioButton)view.findViewById(R.id.st_pe1);
			rb.setChecked(true);
		}
		if(note!=null && !note.equals("")) editText.setText(note);
		new AlertDialog.Builder(DCTimer.this).setView(view)
		.setPositiveButton(getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup rg = (RadioGroup)view.findViewById(R.id.st_penalty);
				int rgid = rg.getCheckedRadioButtonId();
				switch(rgid){
				case R.id.st_pe1: change(p/col, (byte)0, (byte)1);break;
				case R.id.st_pe2: change(p/col, (byte)1, (byte)1);break;
				case R.id.st_pe3: change(p/col, (byte)0, (byte)0);break;
				}
				String text = editText.getText().toString();
				if(!text.equals(note)){
					dbh.update(spinSel[8], id, text);
					myGridView.setAdapter(aryAdapter);
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
						rest[i]=rest[i+1];resp[i]=resp[i+1];resd[i]=resd[i+1];scrst[i]=scrst[i+1];
					}
					c.moveToPosition(p/col);
					delId=c.getInt(0);
				} else {
					delId=dbLastId;
					if(resl>1){
						c.moveToPosition(resl-2);
						dbLastId=c.getInt(0);
					} else dbLastId=0;
				}
				dbh.del(spinSel[8], delId);
				resl--;
				if(resl>0){
					if(isMulp)times=new String[(resl+1)*col];
					else times=new String[resl*col];
				}
				else {
					times=null;
					sestp[spinSel[8]]=-1;
					edit.remove("sestp"+spinSel[8]);
					edit.commit();
				}
				oriavg.setText(getResources().getString(R.string.session_average)+Mi.average());
				if(isMulp)aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3], 
						Mi.omax, Mi.omin}, share.getInt("intv", 30), col);
				else aryAdapter=new TimesAdapter (DCTimer.this, times, new int[]{cl[1],cl[2],cl[3],cl[4]}, 
						Mi.omax, Mi.omin, share.getInt("intv", 30));
				myGridView.setAdapter(aryAdapter);
				d.dismiss();
			}
		}).show();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();
					Cursor cursor = getContentResolver().query(uri, null, null, null, null);
					cursor.moveToFirst();
					picPath = cursor.getString(1);
					ContentResolver cr = this.getContentResolver();
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					bitmap=getBgPic(bitmap);
					setBgPic(bitmap, share.getInt("opac", 35));
					bgcolor=false;
					edit.putString("picpath", picPath);
					edit.putBoolean("bgcolor", false);edit.commit();
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
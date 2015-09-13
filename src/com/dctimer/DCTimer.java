package com.dctimer;

import static com.dctimer.Configs.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dctimer.adapter.*;
import com.dctimer.db.*;
import com.dctimer.ui.CustomDialog;
import com.dctimer.util.*;
import com.dctimer.view.ColorPickerView;
import com.dctimer.view.ColorSchemeView;

import scrambler.Scrambler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressLint("NewApi")
public class DCTimer extends Activity {
	private Context context;
	private TabHost tabHost;
	private RadioButton[] rbTab = new RadioButton[3];
	private RadioGroup rGroup;
	private Button btScramble;	//打乱按钮
	public TextView tvTimer;	//计时器
	private TextView tvScramble;	// 显示打乱
	private ImageView scrambleView;	//打乱状态图
	private GridView gvTimes, gvTitle;	//成绩列表
	private Button btSesMean, btSesOptn, btSession;	//分组平均, 分组选项, 分组
	private TextView tvSesName;	//分组名称
	private Button[] btSolver3 = new Button[2];	//三阶求解
	private Button btReset;	//设置复位
	private SeekBar[] seekBar = new SeekBar[5];	//拖动条
	private TextView[] tvSettings = new TextView[50];	//设置
	private ImageButton[] ibSwitch = new ImageButton[9];	//开关
	private TextView[] std = new TextView[15];
	private TextView[] stdn = new TextView[2];
	private LinearLayout[] llayout = new LinearLayout[27];
	private LinearLayout[] lborder = new LinearLayout[7];
	private CheckBox[] checkBox = new CheckBox[11];	//EG打乱设置
	private PopupWindow popupWindow;	//打乱弹出窗口
	private TextView tvPathName;
	private View view;
	private ListView listView;
	private ProgressDialog progressDialog;
	private EditText editText;
	private Bitmap bitmap;
	private Bitmap bmScrView;
	public SharedPreferences share;
	public static SharedPreferences.Editor edit;
	public static DisplayMetrics dm;
	private Vibrator vibrator;
	
	private Timer timer;
	private Scrambler scrambler;
	private Session session;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
	private TimesAdapter timesAdapter;
	private TextAdapter scr1Adapter;
	private TextAdapter scr2Adapter;
	
	final static int SCRNONE = 0;
	final static int SCRING = 1;
	final static int NEXTSCRING = 2;
	final static int SCRDONE = 3;
	
	public boolean isInScr = false;
	private boolean nextScrWaiting = false;
	private boolean scrTouch = false;
	private boolean isLongPress;
	protected boolean canStart;
	private boolean idnf = true;
	private boolean touchDown;
	static int scrState = 0;
	private static int inScrLen;
	private static int crntScrType = -64;
	private int selScr1, selScr2;
	private int crntProgress;
	private int insType;
	private int mulpCount;
	private int listLen;
	private int version;
	private int[] vibTime = new int[] {30, 50, 80, 150, 240};
	private int[] screenOri = new int[] {2, 0, 8, 1, 4};
	private int[] resId = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img1_selected, R.drawable.img2_selected, R.drawable.img3_selected};
	static int isp2 = 0;
	public static int dip300;
	private int[] staid = {R.array.tiwStr, R.array.tupdStr, R.array.preStr, R.array.mulpStr,
			R.array.avgStr, R.array.crsStr, R.array.c2lStr, R.array.mncStr,  
			R.array.fontStr, R.array.soriStr, R.array.vibraStr, R.array.vibTimeStr,
			R.array.sq1sStr, R.array.timeForm, R.array.avgStr};
	private long exitTime = 0;
	public static float scale, fontScale;
	private String selFilePath;
	private String newVersion, updateCont;
	private String defPath = null;// = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
	private String[] sesItems, sol31, sol32;
	private String[][] itemStr = new String[15][];
	public static String crntScr;	// 当前打乱
	private static String nextScr = null, extsol, slist;
	private String[] scrStr;
	private static ArrayList<String> inScr = null;
	private List<String> items = null, paths = null;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int msw = msg.what;
			switch (msw) {
			case 0: tvScramble.setText(crntScr); break;
			case 1: tvScramble.setText(crntScr + "\n\n" + getString(R.string.shape) + extsol);	break;
			case 2: tvScramble.setText(getString(R.string.scrambling));	break;
			case 3: tvScramble.setText(crntScr + extsol);	break;
			case 4: tvScramble.setText(crntScr + "\n\n" + getString(R.string.solving));	break;
			case 5: Toast.makeText(context, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();	break;
			case 6: Toast.makeText(context, getString(R.string.file_error), Toast.LENGTH_LONG).show();
			case 7: Toast.makeText(context, getString(R.string.save_success), Toast.LENGTH_SHORT).show();	break;
			case 8: Toast.makeText(context, getString(R.string.conning), Toast.LENGTH_SHORT).show();	break;
			case 9: Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_LONG).show();	break;
			case 10: Toast.makeText(context, getString(R.string.lastest), Toast.LENGTH_LONG).show();	break;
			case 11: Toast.makeText(context, getString(R.string.import_failed), Toast.LENGTH_SHORT).show(); break;
			case 12: Toast.makeText(context, getString(R.string.import_success), Toast.LENGTH_SHORT).show(); break;
			case 13:
				btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
				setGridView();
				break;
			case 14: scrambleView.setVisibility(View.GONE); break;
			case 15:
				scrambleView.setVisibility(View.VISIBLE);
				scrambleView.setImageBitmap(bmScrView);
				break;
			case 16:
				if(defPath == null)
					Toast.makeText(context, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
				else
					new CustomDialog.Builder(context).setTitle(getString(R.string.havenew)+newVersion).setMessage(updateCont)
					.setPositiveButton(R.string.btn_download, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							download("DCTimer"+newVersion+".apk");
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
				break;
			case 17: 
				timesAdapter.notifyDataSetChanged();
				gvTimes.setAdapter(timesAdapter);
				gvTimes.setSelection(gvTimes.getCount()-1);
				break;
			case 18: tvScramble.setText(getString(R.string.initing) + " (2%) ..."); break;
			case 19: tvScramble.setText(getString(R.string.initing) + " (" + (13 + threephase.Util.prog / 2597) + "%) ..."); break;
			case 20: tvScramble.setText(getString(R.string.initing) + " (0%) ..."); break;
			case 21: tvScramble.setText(getString(R.string.initing) + " (19%) ..."); break;
			case 22: tvScramble.setText(getString(R.string.initing) + " (26%) ..."); break;
			case 23: tvScramble.setText(getString(R.string.initing) + " (" + (33 + threephase.Util.prog / 44098) + "%) ..."); break;
			case 24: tvScramble.setText(getString(R.string.initing) + " (21%) ..."); break;
			case 25: tvScramble.setText(getString(R.string.initing) + " (" + (23 + threephase.Util.prog / 150150) + "%) ..."); break;
			case 26: tvScramble.setText(getString(R.string.initing) + " (" + (26 + threephase.Util.prog / 4200) + "%) ..."); break;
			case 27: tvScramble.setText(getString(R.string.initing) + " (" + (95 + threephase.Util.prog / 252) + "%) ..."); break;
			case 28: tvScramble.setText(getString(R.string.initing) + " (" + (33 + threephase.Util.prog / 28923) + "%) ..."); break;
			case 29: tvScramble.setText(getString(R.string.initing) + " (" + threephase.Util.prog / 1198 + "%) ..."); break;
			default:
				progressDialog.setProgress(msw - 100);	break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
		super.setContentView(R.layout.tab);
		context = this;
		share = super.getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
		edit = share.edit();
		dm = getResources().getDisplayMetrics();
		scale = dm.density;
		fontScale = dm.scaledDensity;
		dip300 = (int) (scale * 300 + 0.5);
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			defPath = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
			//System.out.println("SD卡 "+defPath);
		}
		readConf();
		scrStr = getResources().getStringArray(R.array.cubeStr);
		sol31 = getResources().getStringArray(R.array.faceStr);
		sol32 = getResources().getStringArray(R.array.sideStr);
		
		scrambler = new Scrambler(this);
		timer = new Timer(this);
		session = new Session(context);
		
		if(fulls) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(opnl) acquireWakeLock();
		
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		int[] ids = {R.id.tab_timer, R.id.tab_list, R.id.tab_setting};
		int[] rbIds = {R.id.radio_1, R.id.radio_2, R.id.radio_3};
		for (int x=0; x<3; x++) {
			TabSpec myTab = tabHost.newTabSpec("tab" + x);
			myTab.setIndicator("tab");
			myTab.setContent(ids[x]);
			tabHost.addTab(myTab);
			
			rbTab[x] = (RadioButton) findViewById(rbIds[x]);
			rbTab[x].setOnCheckedChangeListener(mOnTabChangeListener);
			if(x == 0) {
				rbTab[x].setTextColor(0xff3d9ee8);
				Drawable dr = getResources().getDrawable(resId[x + 3]);
				rbTab[x].setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
			} else {
				rbTab[x].setTextColor(0xff747474);
				Drawable dr = getResources().getDrawable(resId[x]);
				rbTab[x].setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
			}
		}
		tabHost.setCurrentTab(0);
		if(bgcolor) tabHost.setBackgroundColor(colors[0]);
		else try {
			Bitmap bm = Utils.getBitmap(picPath);
			bitmap = Utils.getBackgroundBitmap(bm);
			tabHost.setBackground(Utils.getBackgroundDrawable(context, bitmap, opac));
			bm.recycle();
		} catch (Exception e) {
			tabHost.setBackgroundColor(colors[0]);
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (OutOfMemoryError e) {
			tabHost.setBackgroundColor(colors[0]);
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		rGroup = (RadioGroup) findViewById(R.id.main_radio);
		
		tvScramble = (TextView) findViewById(R.id.tv_scr);	//打乱显示
		tvScramble.setOnTouchListener(mOnTouchListener);
		tvScramble.setOnLongClickListener(mOnLongClickListener);
		tvTimer = (TextView) findViewById(R.id.tv_timer);	//计时器
		tvTimer.setOnTouchListener(mOnTouchListener);
		btScramble = (Button) findViewById(R.id.bt_scr);	//打乱按钮
		btScramble.setOnClickListener(mOnClickListener);
		scrambleView = (ImageView) findViewById(R.id.iv_scr);	//打乱状态图
		//设置选项
		ids = new int[] {R.id.std01, R.id.std02, R.id.std03, R.id.std04, R.id.std16, R.id.std06, R.id.std07, R.id.std08, 
				R.id.std09, R.id.std10, R.id.std11, R.id.std12, R.id.std13, R.id.std14, R.id.std15};
		for(int i=0; i<std.length; i++) {
			itemStr[i] = getResources().getStringArray(staid[i]);
			std[i] = (TextView) findViewById(ids[i]);
		}
		stdn[0] = (TextView) findViewById(R.id.std17);	//平均1长度
		stdn[1] = (TextView) findViewById(R.id.std18);	//平均2长度
		btSolver3[0] = (Button) findViewById(R.id.solve1);	//十字底面
		btSolver3[0].setOnClickListener(mOnClickListener);
		btSolver3[1] = (Button) findViewById(R.id.solve2);	//颜色
		btSolver3[1].setOnClickListener(mOnClickListener);
		//分组名称
		sesItems = new String[15];
		for (int j = 0; j < 15; j++)
			sesItems[j] = (j + 1) + ". " + sesnames[j];
		tvSesName = (TextView) findViewById(R.id.sesname);
		tvSesName.setText(sesnames[sesIdx].equals("") ? getString(R.string.session)+(sesIdx+1) : sesnames[sesIdx]);
		//成绩列表
		gvTimes = (GridView) findViewById(R.id.myGridView);
		gvTimes.setOnItemClickListener(new GridView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				if(isMulp) {
					if(pos/(stSel[3]+2)<Session.resl && pos%(stSel[3]+2)==0)
						showTime(pos, stSel[3] + 2);
				}
				else if(pos % 3 == 0)
					showTime(pos, 3);
				else if(pos%3==1 && pos/3>l1len-2)
					showAlertDialog(1, pos / 3);
				else if(pos%3==2 && pos/3>l2len-2)
					showAlertDialog(2, pos / 3);
			}
		});
		gvTitle = (GridView) findViewById(R.id.gv_title);
		btSesMean = (Button) findViewById(R.id.bt_ses_mean);	//分组平均
		getSession(sesIdx);
		btSesMean.setOnClickListener(mOnClickListener);
		setGvTitle();
		setGridView();
		btSession = (Button) findViewById(R.id.bt_session);	//分组按钮
		btSession.setOnClickListener(mOnClickListener);
		btSesOptn = (Button) findViewById(R.id.bt_optn);	//分组选项
		btSesOptn.setOnClickListener(mOnClickListener);
		//EG打乱
		setEgOll();
		ids = new int[] {R.id.checkeg2, R.id.checkcll, R.id.checkegu, R.id.checkegh, R.id.checkeg1, R.id.checkegn,
				R.id.checkegs, R.id.checkega, R.id.checkegpi, R.id.checkegl, R.id.checkegt};
		for(int i=0; i<checkBox.length; i++) {
			checkBox[i] = (CheckBox) findViewById(ids[i]);
			checkBox[i].setOnCheckedChangeListener(mOnCheckedChangeListener);
		}
		if((egtype & 4) != 0) checkBox[1].setChecked(true);
		if((egtype & 2) != 0) checkBox[4].setChecked(true);
		if((egtype & 1) != 0) checkBox[0].setChecked(true);
		if((egoll & 128) != 0) checkBox[8].setChecked(true);
		if((egoll & 64) != 0) checkBox[3].setChecked(true);
		if((egoll & 32) != 0) checkBox[2].setChecked(true);
		if((egoll & 16) != 0) checkBox[10].setChecked(true);
		if((egoll & 8) != 0) checkBox[9].setChecked(true);
		if((egoll & 4) != 0) checkBox[6].setChecked(true);
		if((egoll & 2) != 0) checkBox[7].setChecked(true);
		if((egoll & 1) != 0) checkBox[5].setChecked(true);
		//拖动条
		ids = new int[] {R.id.seekb1, R.id.seekb2, R.id.seekb3, R.id.seekb4, R.id.seekb5};
		for(int i=0; i<ids.length; i++) seekBar[i] = (SeekBar) findViewById(ids[i]);
		ids = new int[] {95, 25, 41, 100, 20};
		for(int i=0; i<ids.length; i++) seekBar[i].setMax(ids[i]);
		for(int i=0; i<seekBar.length; i++) seekBar[i].setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		//设置TextView
		ids = new int[] {
				R.id.stt00, R.id.stt01, R.id.stt02, R.id.stt08, R.id.stt09, R.id.stt05, R.id.stt21, R.id.stt07,
				R.id.stt03, R.id.stt22, R.id.stt17, R.id.stt11, R.id.stt12, R.id.stt13, R.id.stt14, R.id.stt15,
				R.id.stt16, R.id.stt10, R.id.stt18, R.id.stt19, R.id.stt23, R.id.stt04, R.id.stt52, R.id.stt56,
				R.id.stt26, R.id.stt27, R.id.stt28, R.id.stt29, R.id.stt30, R.id.stt31, R.id.stt33, R.id.stt33,
				R.id.stt34, R.id.stt35, R.id.stt36, R.id.stt37, R.id.stt38, R.id.stt59, R.id.stt40, R.id.stt41,
				R.id.stt42, R.id.stt43, R.id.stt57, R.id.stt58, R.id.stt46, R.id.stt47, R.id.stt48, R.id.stt49,
				R.id.stt50, R.id.stt51,
		};
		for(int i=0; i<ids.length; i++) tvSettings[i] = (TextView) findViewById(ids[i]);
		//设置开关
		ids = new int[] {R.id.stcheck1, R.id.stcheck15, R.id.stcheck3, R.id.stcheck12, R.id.stcheck11, R.id.stcheck6,
				R.id.stcheck7, R.id.stcheck8, R.id.stcheck9};
		for(int i=0; i<ids.length; i++) {
			ibSwitch[i] = (ImageButton) findViewById(ids[i]);
			ibSwitch[i].setOnClickListener(mOnClickListener);
		}
		//设置Layout
		ids = new int[] {
				R.id.lay01, R.id.lay02, R.id.lay03, R.id.lay04, R.id.lay26, R.id.lay06,
				R.id.lay07, R.id.lay08, R.id.lay09, R.id.lay10, R.id.lay11, R.id.lay12,
				R.id.lay23, R.id.lay18, R.id.lay25,
				R.id.lay16, R.id.lay17, R.id.lay22, R.id.lay19, R.id.lay20, R.id.lay21,
				R.id.lay13, R.id.lay24, R.id.lay14, R.id.lay15, R.id.lay27, R.id.lay28,
		};
		for(int i=0; i<ids.length; i++)
			llayout[i] = (LinearLayout) findViewById(ids[i]);
		for(int i=0; i<15; i++) llayout[i].setOnTouchListener(comboListener);
		for(int i=15; i<27; i++) llayout[i].setOnTouchListener(touchListener);
		ids = new int[] {
				R.id.sl01, R.id.sl02, R.id.sl03, R.id.sl04, R.id.sl05, R.id.sl06, R.id.sl07, 
		};
		for(int i=0; i<ids.length; i++)
			lborder[i] = (LinearLayout) findViewById(ids[i]);
		//复位按钮
		btReset = (Button) findViewById(R.id.reset);
		btReset.setOnClickListener(mOnClickListener);
		//震动器
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		//进度条
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		
		version = Utils.getVersion(context);
		setBorders();
		setViews();
		setTextsColor();
		set2ndsel();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//System.out.println("旋转屏幕");
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(!bgcolor) try {
			Bitmap bm = Utils.getBitmap(picPath);
			bitmap = Utils.getBackgroundBitmap(bm);
			tabHost.setBackground(Utils.getBackgroundDrawable(context, bitmap, opac));
			bm.recycle();
		} catch (Exception e) {
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (OutOfMemoryError e) {
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		updateGridView(0);
	}
	
	@Override
	protected void onDestroy() {
		//System.out.println("onDestroy");
		if(session != null)
			session.closeDB();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.clear();
		menu.add(Menu.NONE, 0, 0, getString(R.string.menu_inscr));
		menu.add(Menu.NONE, 1, 1, getString(R.string.menu_outscr));
		menu.add(Menu.NONE, 2, 2, getString(R.string.menu_share));
		//menu.add(Menu.NONE, 3, 3, getString(R.string.menu_weibo));
		menu.add(Menu.NONE, 4, 4, getString(R.string.menu_about));
		menu.add(Menu.NONE, 5, 5, getString(R.string.menu_exit));
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final LayoutInflater factory;
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:	//导入打乱
			factory = LayoutInflater.from(context);
			int layoutId = R.layout.import_scramble;
			view = factory.inflate(layoutId, null);
			final Spinner sp = (Spinner) view.findViewById(R.id.spnScrType);
			String[] items = getResources().getStringArray(R.array.inscrStr);
			ArrayAdapter<String> adap = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
			adap.setDropDownViewResource(R.layout.spinner_dropdown_item);
			sp.setAdapter(adap);
			editText = (EditText) view.findViewById(R.id.edit_inscr);
			sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					insType = position;
				}
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			new CustomDialog.Builder(context).setView(view).setTitle(R.string.menu_inscr)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i) {
					hideKeyboard(editText);
					final String scrs = editText.getText().toString();
					inScr = new ArrayList<String>();
					inScrLen = 0;
					setInScr(scrs);
					if(inScr.size()>0) newScramble(crntScrType);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					hideKeyboard(editText);
				}
			}).show();
			showKeyboard(editText);
			break;
		case 1:	//导出打乱
			if(defPath == null) {
				Toast.makeText(context, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
				break;
			}
			factory = LayoutInflater.from(context);
			layoutId = R.layout.export_scramble;
			view = factory.inflate(layoutId, null);
			final EditText et1 = (EditText) view.findViewById(R.id.edit_scrnum);
			final EditText et2 = (EditText) view.findViewById(R.id.edit_scrpath);
			final ImageButton btn = (ImageButton) view.findViewById(R.id.btn_browse);
			et1.setText("5");
			et1.setSelection(1);
			et2.setText(outPath);
			final EditText et3 = (EditText) view.findViewById(R.id.edit_scrfile);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selFilePath = et2.getText().toString();
					int lid = R.layout.file_selector;
					final View viewb = factory.inflate(lid, null);
					listView = (ListView) viewb.findViewById(R.id.list);
					File f = new File(selFilePath);
					selFilePath = f.exists() ? selFilePath : defPath + File.separator;
					tvPathName = (TextView) viewb.findViewById(R.id.text);
					tvPathName.setText(selFilePath);
					getFileDirs(selFilePath, false);
					listView.setOnItemClickListener(itemListener);
					new CustomDialog.Builder(context).setTitle(R.string.sel_path).setView(viewb)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int j) {
							et2.setText(selFilePath + "/");
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
				}
			});
			new CustomDialog.Builder(context).setView(view).setTitle(getString(R.string.menu_outscr)+"("+btScramble.getText()+")")
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int i) {
					int numt = Integer.parseInt(et1.getText().toString());
					if(numt > 100) numt = 100;
					else if(numt < 1) numt = 5;
					final int num = numt;
					final String path = et2.getText().toString();
					if(!path.equals(outPath)) {
						outPath = path;
						edit.putString("scrpath", path);
						edit.commit();
					}
					final String fileName = et3.getText().toString();
					File file = new File(path+fileName);
					if(file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
					else if(file.exists()) {
						new CustomDialog.Builder(context).setTitle(R.string.path_dupl)
						.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j) {
								outScr(path, fileName, num);
							}
						}).setNegativeButton(R.string.btn_cancel, null).show();
					} else {
						outScr(path, fileName, num);
					}
					hideKeyboard(et1);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(et1);
				}
			}).show();
			showKeyboard(et1);
			break;
		case 2:	//分享
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");	//纯文本
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, getShareContent());
			startActivity(Intent.createChooser(intent, getTitle()));
			break;
		case 4:	//关于
			new CustomDialog.Builder(context).setIcon(R.drawable.ic_launcher).setTitle(R.string.abt_title).setMessage(R.string.abt_msg)
			.setPositiveButton(R.string.btn_upgrade, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new Thread() {
						public void run() {
							handler.sendEmptyMessage(8);
							String ver = Utils.getContent("https://raw.github.com/MeigenChou/DCTimer/master/release/version.txt");
							if(ver.startsWith("error")) {
								handler.sendEmptyMessage(9);
							} else {
								String[] vers = ver.split("\t");
								int v = Integer.parseInt(vers[0]);
								if(v > version) {
									newVersion = vers[1];
									StringBuilder sb = new StringBuilder(vers[2]);
									if(vers.length > 3)
										for(int i=3; i<vers.length; i++) sb.append("\n"+vers[i]);
									updateCont = sb.toString();
									handler.sendEmptyMessage(16);
								}
								else handler.sendEmptyMessage(10);
							}
						}
					}.start();
				}
			})
			.setNegativeButton(R.string.btn_close, null).show();
			break;
		case 5:	//退出
			if(session != null)
				session.closeDB();
			edit.putInt("sel", scrIdx);
			edit.putInt("sel2", scr2idx);
			edit.commit();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {	//设置背景图片
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				//System.out.println(uri);
				if("file".equalsIgnoreCase(uri.getScheme())) {
					picPath = uri.getPath();
				} else {
					ContentResolver cr = getContentResolver();
					Cursor c = cr.query(uri, null, null, null, null);
					c.moveToFirst();
					picPath = c.getString(1);
					c.close();
				}
				//System.out.println("文件路径 " + picPath);
				bgcolor = false;
				edit.putString("picpath", picPath);
				edit.putBoolean("bgcolor", false);
				edit.commit();
				try {
					Bitmap bm = Utils.getBitmap(picPath);
					bitmap = Utils.getBackgroundBitmap(bm);
					tabHost.setBackground(Utils.getBackgroundDrawable(context, bitmap, opac));
					bm.recycle();
				} catch (Exception e) {
					Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
				} catch (OutOfMemoryError e) {
					Toast.makeText(context, "Out of memory error: bitmap size exceeds VM budget", Toast.LENGTH_SHORT).show();
				}
				setBorders();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(timer.state == 1) {
				timer.count();
				viewsVisibility(true);
				if(!wca) {isp2=0; idnf=true;}
				confirmTime((int)timer.time);
				timer.state = 0;
				if(!opnl) releaseWakeLock();
			} else if(timer.state == 2) {
				timer.stopi();
				tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
				viewsVisibility(true);
				if(!opnl) releaseWakeLock();
			} else if(event.getRepeatCount() == 0) {
				if((System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(context, getString(R.string.again_exit), Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					edit.putInt("sel", scrIdx);
					edit.putInt("sel2", scr2idx);
					edit.commit();
		            finish();
		        }
			}
			return false;
		case KeyEvent.KEYCODE_Q:	setScramble(-1, 10);	break;	//SQ1
		case KeyEvent.KEYCODE_W:	setScramble(-1, 3);	break;	//二阶
		case KeyEvent.KEYCODE_E:	setScramble(-1, 0);	break;	//三阶
		case KeyEvent.KEYCODE_R:	setScramble(2, 0);	break;	//四阶
		case KeyEvent.KEYCODE_T:	setScramble(-1, 2);	break;	//五阶
		case KeyEvent.KEYCODE_Y:	setScramble(-1, 13);	break;	//六阶
		case KeyEvent.KEYCODE_U:	setScramble(-1, 14);	break;	//七阶
		case KeyEvent.KEYCODE_M:	setScramble(-1, 8);	break;	//五魔
		case KeyEvent.KEYCODE_P:	setScramble(-1, 9);	break;	//金字塔
		case KeyEvent.KEYCODE_K:	setScramble(-1, 11);	break;	//魔表
		case KeyEvent.KEYCODE_S:	setScramble(-1, 12);	break;	//斜转
		case KeyEvent.KEYCODE_N:	newScramble(crntScrType);	break;	//新打乱
		case KeyEvent.KEYCODE_Z:	//删除最后成绩
			if(Session.resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_del_last)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					delete(Session.resl-1, isMulp ? stSel[3]+2 : 3);
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case KeyEvent.KEYCODE_A:	//删除所有成绩
			if(Session.resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_clear_session)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {deleteAll();}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case KeyEvent.KEYCODE_D:	//最近一次成绩
			if(Session.resl==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(getString(R.string.show_time) + Statistics.distime(Session.resl-1, true))
			.setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: update(Session.resl-1, (byte) 0); break;
					case 1: update(Session.resl-1, (byte) 1); break;
					case 2: update(Session.resl-1, (byte) 2); break;
					}
				}
			}).setNegativeButton(getString(R.string.btn_cancel), null).show();
		}
		return super.onKeyDown(keyCode, event);
	};
	
	private OnCheckedChangeListener mOnTabChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked) {
				int pos = 0;
				switch (buttonView.getId()) {
				case R.id.radio_1:
					pos = 0;
					break;
				case R.id.radio_2:
					pos = 1;
					break;
				case R.id.radio_3:
					pos = 2;
					break;
				}
				tabHost.setCurrentTab(pos);
				for(int i=0; i<3; i++) {
					if(i == pos) {
						rbTab[i].setTextColor(0xff3d9ee8);
						Drawable dr = getResources().getDrawable(resId[i + 3]);
						rbTab[i].setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
					} else {
						rbTab[i].setTextColor(0xff747474);
						Drawable dr = getResources().getDrawable(resId[i]);
						rbTab[i].setCompoundDrawablesWithIntrinsicBounds(null, dr, null, null);
					}
				}
			}
		}
	};
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			switch (v.getId()) {
			case R.id.tv_scr:
				scrTouch = true;
				setTouch(event);
				return timer.state != 0;
			case R.id.tv_timer:
				scrTouch = false;
				if(stSel[0] == 0) setTouch(event);
				else if(stSel[0] == 1) inputTime(event.getAction());
				return true;
			}
			return false;
		}
	};
	
	private OnTouchListener comboListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int sel;
			switch (v.getId()) {
			case R.id.lay01: sel = 0; break;
			case R.id.lay02: sel = 1; break;
			case R.id.lay03: sel = 2; break;
			case R.id.lay04: sel = 3; break;
			case R.id.lay26: sel = 4; break;
			case R.id.lay06: sel = 5; break;
			case R.id.lay07: sel = 6; break;
			case R.id.lay08: sel = 7; break;
			case R.id.lay09: sel = 8; break;
			case R.id.lay10: sel = 9; break;
			case R.id.lay11: sel = 10; break;
			case R.id.lay12: sel = 11; break;
			case R.id.lay23: sel = 12; break;
			case R.id.lay18: sel = 13; break;
			case R.id.lay25: sel = 14; break;
			default: sel = -1; break;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llayout[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				v.performClick();
				new CustomDialog.Builder(context).setSingleChoiceItems(staid[sel], stSel[sel], new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(stSel[sel] != which) {
							stSel[sel] = which;
							switch (sel) {
							case 0:	//计时方式
								if (which == 0) tvTimer.setText(stSel[2]==0 ? "0.00" : "0.000");
								else tvTimer.setText("IMPORT");
								edit.putInt("tiway", which);
								break;
							case 1:	//计时器更新方式
								edit.putInt("timerupd", which);
								break;
							case 2:	//计时精确度
								edit.putBoolean("prec", which != 0);
								if(stSel[0]==0) tvTimer.setText(which==0 ? "0.00" : "0.000");
								if(Session.resl != 0) {
									btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
									updateGridView(1);
								}
								break;
							case 3:	//分段计时
								if(which == 0) {
									isMulp = false;
									Session.mulp = null;
									listLen = Session.resl * 3;
								} else {
									listLen = Session.resl!=0 ? (which+2)*(Session.resl+1) : 0;
									if(!isMulp) {
										isMulp = true;
										Session.mulp = new int[6][Session.result.length];
										if(Session.resl > 0)
											session.getMultData();
									}
								}
								edit.putInt("multp", which);
								setGridView();
								setGvTitle();
								break;
							case 4:	//滚动平均2类型
								edit.putInt("l2tp", which);
								if(!isMulp) setGvTitle();
								if(Session.resl>0 && !isMulp) {
									setGvTitle();
									updateGridView(1);
								}
								break;
							case 5:	//三阶求解
								edit.putInt("cxe", which);
								if(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
										scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)) {
									if(which == 0) tvScramble.setText(crntScr);
									else new Thread() {
										public void run() {
											handler.sendEmptyMessage(4);
											scrambler.extSol3(stSel[5], crntScr);
											extsol = scrambler.sc;
											handler.sendEmptyMessage(3);
											scrState = NEXTSCRING;
											nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, false);
											scrState = SCRDONE;
										}
									}.start();
								}
								break;
							case 6:	//二阶底面
								edit.putInt("cube2l", which);
								if(scrIdx == 0) {
									if(which == 0) tvScramble.setText(crntScr);
									else if(scr2idx < 3) new Thread() {
										public void run() {
											handler.sendEmptyMessage(4);
											extsol = "\n"+solver.Cube2bl.cube2layer(crntScr, stSel[6]);
											handler.sendEmptyMessage(3);
											scrState = NEXTSCRING;
											nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, false);
											scrState = SCRDONE;
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
												handler.sendEmptyMessage(4);
												extsol = " " + (stSel[12]==1 ? solver.Sq1Shape.solveTrn(crntScr) : solver.Sq1Shape.solveTws(crntScr));
												handler.sendEmptyMessage(1);
												scrState = NEXTSCRING;
												nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, false);
												scrState = SCRDONE;
											}
										}.start();
									}
									else tvScramble.setText(crntScr);
								}
								break;
							case 13:	//时间格式
								edit.putInt("timeform", which);
								if(Session.resl > 0) {
									updateGridView(1);
								}
								break;
							case 14:	//滚动平均1类型
								edit.putInt("l1tp", which);
								if(!isMulp) setGvTitle();
								if(Session.resl>0 && !isMulp) {
									setGvTitle();
									updateGridView(1);
								}
								break;
							}
							edit.commit();
							std[sel].setText(itemStr[sel][which]);
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
			case MotionEvent.ACTION_CANCEL:
				llayout[sel].setBackgroundColor(0);
				break;
			}
			return false;
		}
	};
	
	private OnTouchListener touchListener = new OnTouchListener() {
		private ColorPickerView colorPickerView;
		private ColorSchemeView colorSchemeView;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int sel;
			v.performClick();
			switch (v.getId()) {
			case R.id.lay16: sel = 15; break;
			case R.id.lay17: sel = 16; break;
			case R.id.lay22: sel = 17; break;
			case R.id.lay19: sel = 18; break;
			case R.id.lay20: sel = 19; break;
			case R.id.lay21: sel = 20; break;
			case R.id.lay13: sel = 21; break;
			case R.id.lay24: sel = 22; break;
			case R.id.lay14: sel = 23; break;
			case R.id.lay15: sel = 24; break;
			case R.id.lay27: sel = 25; break;
			case R.id.lay28: sel = 26; break;
			default: sel = -1;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				llayout[sel].setBackgroundColor(0x80ffffff);
				break;
			case MotionEvent.ACTION_UP:
				switch (sel) {
				case 15:	//最慢单次颜色
					colorPickerView = new ColorPickerView(context, dip300, colors[3]);
					new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int color = colorPickerView.getColor();
							colors[3]=color;
							edit.putInt("cl3", color);
							edit.commit();
							updateGridView(0);
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
					break;
				case 16:	//最快平均颜色
					colorPickerView = new ColorPickerView(context, dip300, colors[4]);
					new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int color = colorPickerView.getColor();
							colors[4]=color;
							edit.putInt("cl4", color);
							edit.commit();
							updateGridView(0);
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
					break;
				case 17:	//背景图片
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					//intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, 1);
					break;
				case 18:	//n阶配色设置
					int[] cs = {share.getInt("csn1", Color.YELLOW), share.getInt("csn2", Color.BLUE), share.getInt("csn3", Color.RED),
							share.getInt("csn4", Color.WHITE), share.getInt("csn5", 0xff009900), share.getInt("csn6", 0xffff8026)};
					colorSchemeView = new ColorSchemeView(context, dip300, cs, 1);
					new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_cube)).setView(colorSchemeView)
						.setNegativeButton(R.string.btn_close, null).show();
					break;
				case 19:	//金字塔配色
					cs = new int[] {share.getInt("csp1", Color.RED), share.getInt("csp2", 0xff009900),
							share.getInt("csp3", Color.BLUE), share.getInt("csp4", Color.YELLOW)};
					colorSchemeView = new ColorSchemeView(context, dip300, cs, 2);
					new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_pyrm)).setView(colorSchemeView)
						.setNegativeButton(R.string.btn_close, null).show();
					break;
				case 20:	//SQ配色
					cs = new int[] {share.getInt("csq1", Color.YELLOW), share.getInt("csq2", Color.BLUE), share.getInt("csq3", Color.RED),
							share.getInt("csq4", Color.WHITE), share.getInt("csq5", 0xff009900), share.getInt("csq6", 0xffff8026)};
					colorSchemeView = new ColorSchemeView(context, dip300, cs, 3);
					new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_sq)).setView(colorSchemeView)
						.setNegativeButton(R.string.btn_close, null).show();
					break;
				case 21:	//背景颜色
					colorPickerView = new ColorPickerView(context, dip300, colors[0]);
					new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int color = colorPickerView.getColor();
							tabHost.setBackgroundColor(color); colors[0]=color; bgcolor=true;
							edit.putInt("cl0", color);
							edit.putBoolean("bgcolor", true);
							edit.commit();
							setBorders();
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
					break;
				case 22:	//Skewb配色
					cs = new int[] {share.getInt("csw1", Color.YELLOW), share.getInt("csw2", Color.BLUE), share.getInt("csw3", Color.RED),
							share.getInt("csw4", Color.WHITE), share.getInt("csw5", 0xff009900), share.getInt("csw6", 0xffff8026)};
					colorSchemeView = new ColorSchemeView(context, dip300, cs, 4);
					new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_skewb)).setView(colorSchemeView)
						.setNegativeButton(R.string.btn_close, null).show();
					break;
				case 23:	//文字颜色
					colorPickerView = new ColorPickerView(context, dip300, colors[1]);
					new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int color = colorPickerView.getColor();
							colors[1] = color;
							edit.putInt("cl1", color);
							edit.commit();
							setTextsColor();
							setGvTitle();
							updateGridView(0);
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
					break;
				case 24:	//最快单次颜色
					colorPickerView = new ColorPickerView(context, dip300, colors[2]);
					new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int color = colorPickerView.getColor();
							colors[2] = color;
							edit.putInt("cl2", color);
							edit.commit();
							updateGridView(0);
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
					break;
				case 25:	//滚动平均1长度
				case 26:	//滚动平均2长度
					LayoutInflater factory = LayoutInflater.from(context);
					int layoutId = R.layout.number_input;
					view = factory.inflate(layoutId, null);
					editText = (EditText) view.findViewById(R.id.edit_text);
					editText.setText(String.valueOf(sel==25 ? l1len : l2len));
					editText.setSelection(editText.getText().length());
					new CustomDialog.Builder(context).setTitle(R.string.enter_len).setView(view)
					.setPositiveButton(R.string.btn_ok, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int len = Integer.parseInt(editText.getText().toString());
							if(len < 3 || len > 1000)
								Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_LONG).show();
							else {
								if(sel == 25) {
									l1len = len;
									edit.putInt("l1len", len);
								} else {
									l2len = len;
									edit.putInt("l2len", len);
								}
								edit.commit();
								stdn[sel - 25].setText("" + len);
								if(Session.resl>0 && !isMulp) {
									setGvTitle();
									updateGridView(1);
								}
							}
							hideKeyboard(editText);
						}
					}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							hideKeyboard(editText);
						}
					}).show();
					showKeyboard(editText);
					break;
				}
			case MotionEvent.ACTION_CANCEL:
				llayout[sel].setBackgroundColor(0);
				break;
			}
			return false;
		}
	};
	
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			switch (seekBar.getId()) {
			case R.id.seekb1:	//计时器字体
				tvSettings[3].setText(getString(R.string.timer_size) + (seekBar.getProgress()+50));
				edit.putInt("ttsize", seekBar.getProgress() + 50);
				tvTimer.setTextSize(seekBar.getProgress() + 50);
				break;
			case R.id.seekb2:	//打乱字体
				tvSettings[4].setText(getString(R.string.scrsize) + (seekBar.getProgress()+12));
				edit.putInt("stsize", seekBar.getProgress() + 12);
				tvScramble.setTextSize(seekBar.getProgress() + 12);
				break;
			case R.id.seekb3:	//成绩列表行距
				intv = seekBar.getProgress() + 20;
				tvSettings[10].setText(getString(R.string.row_spacing) + intv);
				if(Session.resl != 0)
					updateGridView(2);
				edit.putInt("intv", seekBar.getProgress() + 20);
				break;
			case R.id.seekb4:	//背景图不透明度
				opac = seekBar.getProgress();
				if(!bgcolor)
					tabHost.setBackground(Utils.getBackgroundDrawable(context, bitmap, opac));
				edit.putInt("opac", opac);
				setBorders();
				break;
			case R.id.seekb5:	//启动延时
				frzTime=seekBar.getProgress();
				tvSettings[29].setText(getString(R.string.time_tap) + (frzTime/20D));
				edit.putInt("tapt", frzTime);
				break;
			}
			edit.commit();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			setProgress(seekBar);
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			setProgress(seekBar);
		}
		
		private void setProgress(SeekBar seekBar) {
			int prg = seekBar.getProgress();
			switch (seekBar.getId()) {
			case R.id.seekb1:
				tvSettings[3].setText(getString(R.string.timer_size) + (prg+50));
				break;
			case R.id.seekb2:
				tvSettings[4].setText(getString(R.string.scrsize) + (prg+12));
				break;
			case R.id.seekb3:
				tvSettings[10].setText(getString(R.string.row_spacing) + (prg+20));
				break;
			case R.id.seekb5:
				tvSettings[29].setText(getString(R.string.time_tap) + (prg/20.0));
				break;
			}
		}
	};
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.stcheck1:	//WCA观察
				wca = !wca;
				setSwitchOn(ibSwitch[0], wca);
				edit.putBoolean("wca", wca);
				edit.commit();
				break;
			case R.id.stcheck3:	//模拟ss计时
				simss = !simss;
				setSwitchOn(ibSwitch[2], simss);
				edit.putBoolean("simss", simss);
				edit.commit();
				break;
			case R.id.stcheck6:	//显示打乱状态
				showscr = !showscr;
				setSwitchOn(ibSwitch[5], showscr);
				edit.putBoolean("showscr", showscr);
				if (showscr) {
					scrambleView.setVisibility(View.VISIBLE);
					showScrView(false);
				}
				else scrambleView.setVisibility(View.GONE);
				edit.commit();
				break;
			case R.id.stcheck7:	//确认时间
				conft = !conft;
				setSwitchOn(ibSwitch[6], conft);
				edit.putBoolean("conft", conft);
				edit.commit();
				break;
			case R.id.stcheck8:	//成绩详情隐藏打乱
				setSwitchOn(ibSwitch[7], hidls);
				hidls = !hidls;
				edit.putBoolean("hidls", hidls);
				edit.commit();
				break;
			case R.id.stcheck9:	//自动选择打乱
				selScr = !selScr;
				setSwitchOn(ibSwitch[8], selScr);
				edit.putBoolean("selses", selScr);
				edit.commit();
				break;
			case R.id.stcheck11:	//全屏显示
				fulls = !fulls;
				setFullScreen(fulls);
				setSwitchOn(ibSwitch[4], fulls);
				edit.putBoolean("fulls", fulls);
				edit.commit();
				break;
			case R.id.stcheck12:	//屏幕常亮
				if(opnl) {
					if(timer.state != 1) releaseWakeLock();
				} else acquireWakeLock();
				opnl = !opnl;
				setSwitchOn(ibSwitch[3], opnl);
				edit.putBoolean("scron", opnl);
				edit.commit();
				break;
			case R.id.stcheck15:	//等宽打乱字体
				monoscr = !monoscr;
				setSwitchOn(ibSwitch[1], monoscr);
				edit.putBoolean("monoscr", monoscr);
				edit.commit();
				setScrambleFont(monoscr ? 0 : 1);
				break;
			case R.id.bt_scr:	//选择打乱
				selScr1 = scrIdx;
				selScr2 = scr2idx;
				int resId = R.layout.pop_window;
				view = LayoutInflater.from(context).inflate(resId, null);
				listView = (ListView) view.findViewById(R.id.list1);
				scr1Adapter = new TextAdapter(context, scrStr, selScr1+1, 1);
				listView.setAdapter(scr1Adapter);
				listView.setSelection(selScr1 + 1);
				listView.setOnItemClickListener(itemListener);
				listView = (ListView) view.findViewById(R.id.list2);
				scr2Adapter = new TextAdapter(context, getResources().getStringArray(get2ndScr(scrIdx)), selScr2, 2);
				listView.setAdapter(scr2Adapter);
				listView.setSelection(selScr2);
				listView.setOnItemClickListener(itemListener);
				popupWindow = new PopupWindow(view, dip300, dip300, true);
				popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_mid));
				popupWindow.setTouchable(true);
				popupWindow.showAsDropDown(v, (btScramble.getWidth()-popupWindow.getWidth())/2, 0);
				break;
			case R.id.bt_session:	//选择分组
				new CustomDialog.Builder(context).setSingleChoiceItems(sesItems, sesIdx, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(sesIdx != which) {
							sesIdx = (byte) which;
							getSession(which);
							btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
							setGridView();
							edit.putInt("group", sesIdx);
							edit.commit();
							tvSesName.setText((sesnames[sesIdx].equals("") ? getString(R.string.session) + (sesIdx+1) : sesnames[sesIdx]));
							if(selScr && sesType[which] != crntScrType) {
								scrIdx = sesType[which] >> 5;
								scr2idx = sesType[which] & 31;
								set2ndsel();
							}
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.bt_optn:	//分组选项
				new CustomDialog.Builder(context).setItems(R.array.optStr, new OnClickListener() {
					ImageView iv;
					LayoutInflater factory;
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:	//分组命名
							factory = LayoutInflater.from(context);
							int layoutId = R.layout.ses_name;
							view = factory.inflate(layoutId, null);
							editText = (EditText) view.findViewById(R.id.edit_ses);
							editText.setText(sesnames[sesIdx]);
							editText.setSelection(sesnames[sesIdx].length());
							new CustomDialog.Builder(context).setTitle(R.string.sesname).setView(view)
							.setPositiveButton(R.string.btn_ok, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									sesnames[sesIdx] = editText.getText().toString();
									edit.putString("sesname" + sesIdx, sesnames[sesIdx]);
									edit.commit();
									sesItems[sesIdx] = (sesIdx + 1) + ". " + sesnames[sesIdx];
									tvSesName.setText((sesnames[sesIdx].equals("")) ? getString(R.string.session) + (sesIdx+1) : sesnames[sesIdx]);
									hideKeyboard(editText);
								}
							}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									hideKeyboard(editText);
								}
							}).show();
							showKeyboard(editText);
							break;
						case 1:	//清空成绩
							if(Session.resl == 0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
							else {
								new CustomDialog.Builder(context).setTitle(R.string.confirm_clear_session)
								.setNegativeButton(R.string.btn_cancel, null)
								.setPositiveButton(R.string.btn_ok, new OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										deleteAll();
									}
								}).show();
							}
							break;
						case 2:	//时间分布直方图
							factory = LayoutInflater.from(context);
							layoutId = R.layout.graph;
							view = factory.inflate(layoutId, null);
							iv = (ImageView) view.findViewById(R.id.image_view);
							Bitmap bm = Bitmap.createBitmap(dip300, (int)(dip300*1.2), Config.ARGB_8888);
							Canvas c = new Canvas(bm);
							c.drawColor(0);
							Paint p = new Paint();
							p.setAntiAlias(true);
							Graph.drawHist(dip300, p, c);
							iv.setImageBitmap(bm);
							new CustomDialog.Builder(context).setView(view)
								.setNegativeButton(R.string.btn_close, null).show();
							break;
						case 3:	//折线图
							factory = LayoutInflater.from(context);
							layoutId = R.layout.graph;
							view = factory.inflate(layoutId, null);
							iv = (ImageView) view.findViewById(R.id.image_view);
							bm = Bitmap.createBitmap(dip300, (int)(dip300*0.9), Config.ARGB_8888);
							c = new Canvas(bm);
							//c.drawColor(0);
							p = new Paint();
							p.setAntiAlias(true);
							Graph.drawGraph(dip300, p, c);
							iv.setImageBitmap(bm);
							new CustomDialog.Builder(context).setView(view)
								.setNegativeButton(R.string.btn_close, null).show();
							break;
						case 4:	//导出数据库
							if(defPath == null) {
								Toast.makeText(context, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
								break;
							}
							factory = LayoutInflater.from(context);
							layoutId = R.layout.save_stat;
							view = factory.inflate(layoutId, null);
							editText = (EditText) view.findViewById(R.id.edit_scrpath);
							ImageButton btn = (ImageButton) view.findViewById(R.id.btn_browse);
							editText.setText(outPath);
							final EditText et2 = (EditText) view.findViewById(R.id.edit_scrfile);
							et2.requestFocus();
							et2.setText("database.db");
							et2.setSelection(et2.getText().length());
							btn.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									selFilePath = editText.getText().toString();
									int lid = R.layout.file_selector;
									final View viewb = factory.inflate(lid, null);
									listView = (ListView) viewb.findViewById(R.id.list);
									File f = new File(selFilePath);
									selFilePath = f.exists() ? selFilePath : defPath + File.separator;
									tvPathName = (TextView) viewb.findViewById(R.id.text);
									tvPathName.setText(selFilePath);
									getFileDirs(selFilePath, false);
									listView.setOnItemClickListener(itemListener);
									new CustomDialog.Builder(context).setTitle(R.string.sel_path).setView(viewb)
									.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialoginterface, int j) {
											editText.setText(selFilePath+"/");
										}
									}).setNegativeButton(R.string.btn_cancel, null).show();
								}
							});
							new CustomDialog.Builder(context).setView(view).setTitle(R.string.out_db)
							.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface di, int i) {
									final String path = editText.getText().toString();
									if(!path.equals(outPath)) {
										outPath = path;
										edit.putString("scrpath", path);
										edit.commit();
									}
									final String fileName = et2.getText().toString();
									File file = new File(path+fileName);
									if(file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
									else if(file.exists()) {
										new CustomDialog.Builder(context).setTitle(R.string.path_dupl)
										.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialoginterface, int k) {
												exportDB(path+fileName);
											}
										}).setNegativeButton(R.string.btn_cancel, null).show();
									} else exportDB(path+fileName);
									hideKeyboard(editText);
								}
							}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									hideKeyboard(editText);
								}
							}).show();
							break;
						case 5:	//导入数据库
							if(defPath == null) {
								Toast.makeText(context, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
								break;
							}
							factory = LayoutInflater.from(context);
							layoutId = R.layout.import_db;
							view = factory.inflate(layoutId, null);
							editText = (EditText) view.findViewById(R.id.edit_scrpath);
							btn = (ImageButton) view.findViewById(R.id.btn_browse);
							editText.setText(outPath+"database.db");
							editText.setSelection(editText.getText().length());
							btn.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									selFilePath = editText.getText().toString();
									selFilePath = selFilePath.substring(0, selFilePath.lastIndexOf('/'));
									int lid = R.layout.file_selector;
									final View viewb = factory.inflate(lid, null);
									listView = (ListView) viewb.findViewById(R.id.list);
									File f = new File(selFilePath);
									selFilePath = f.exists() ? selFilePath : Environment.getExternalStorageDirectory().getPath()+File.separator;
									tvPathName = (TextView) viewb.findViewById(R.id.text);
									tvPathName.setText(selFilePath);
									getFileDirs(selFilePath, true);
									final CustomDialog fdialog = new CustomDialog.Builder(context).setTitle(R.string.sel_path).setView(viewb)
											.setNegativeButton(R.string.btn_close, null).create();
									listView.setOnItemClickListener(new OnItemClickListener() {
										@Override
										public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
											selFilePath = paths.get(arg2);
											File f = new File(selFilePath);
											if(f.isDirectory()) {
												tvPathName.setText(selFilePath);
												getFileDirs(selFilePath, true);
											} else {
												editText.setText(selFilePath);
												fdialog.dismiss();
											}
										}
									});
									fdialog.show();
								}
							});
							new CustomDialog.Builder(context).setView(view).setTitle(R.string.in_db)
							.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface di, int i) {
									hideKeyboard(editText);
									final String path = editText.getText().toString();
									File file = new File(path);
									if(file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
									else if(file.exists()) {
										importDB(path);
									} else Toast.makeText(context, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
								}
							}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									hideKeyboard(editText);
								}
							}).show();
						}
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.solve1:
				new CustomDialog.Builder(context).setSingleChoiceItems(R.array.faceStr, solSel[0], new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(solSel[0] != which) {
							solSel[0] = which;
							btSolver3[0].setText(sol31[solSel[0]]);
							edit.putInt("cface", solSel[0]);
							edit.commit();
							if(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
									scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))
								new Thread() {
									public void run() {
										handler.sendEmptyMessage(4);
										scrambler.extSol3(1, crntScr);
										extsol = "\n" + scrambler.sc;
										handler.sendEmptyMessage(3);
										scrState = NEXTSCRING;
										nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, false);
										scrState = SCRDONE;
									}
								}.start();
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.solve2:
				new CustomDialog.Builder(context).setSingleChoiceItems(R.array.sideStr, solSel[1], new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(solSel[1] != which) {
							solSel[1] = which;
							btSolver3[1].setText(sol32[solSel[1]]);
							edit.putInt("cside", solSel[1]);
							edit.commit();
							if(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
									scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))
								new Thread() {
									public void run() {
										handler.sendEmptyMessage(4);
										scrambler.extSol3(stSel[5], crntScr);
										extsol = "\n" + scrambler.sc;
										handler.sendEmptyMessage(3);
										scrState = NEXTSCRING;
										nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, false);
										scrState = SCRDONE;
									}
								}.start();
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.bt_ses_mean:	//分组平均
				for(int i=0; i<Session.resl; i++)
					if(Session.penalty[i] != 2) {
						showAlertDialog(3, 0);
						break;
					}
				break;
			case R.id.reset:	//恢复默认设置 TODO
				new CustomDialog.Builder(context).setTitle(R.string.confirm_reset)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(fulls) setFullScreen(false);
						wca=false; simss=false; monoscr = false;
						showscr=true; conft=true; hidls=false; selScr=false; fulls=false;
						bgcolor=true; opnl=false; isMulp=false;
						solSel[0]=0; solSel[1]=1;
						stSel[0]=0; stSel[1]=0; stSel[2]=1; stSel[3]=0; stSel[4]=0;
						stSel[5]=0; stSel[6]=0; stSel[7]=1; stSel[8]=3; stSel[9]=0;
						stSel[10]=0; stSel[11]=2; stSel[12]=0;
						stsize = 18; ttsize = 60;
						l1len = 5; l2len = 12;
						intv = 25; frzTime = 0;
						colors[0] = 0xff66ccff;	colors[1] = 0xff000000;	colors[2] = 0xffff00ff;
						colors[3] = 0xffff0000;	colors[4] = 0xff009900;
						tabHost.setBackgroundColor(colors[0]);
						setViews();
						setTextsColor();
						updateGridView(1);
						releaseWakeLock();
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
						edit.remove("multp");	edit.remove("minxc");	edit.remove("simss");
						edit.remove("l1len");	edit.remove("l2len");
						edit.remove("hidscr");	edit.remove("ttsize");	edit.remove("stsize");
						edit.remove("cube2l");	edit.remove("scrgry");	edit.remove("selses");
						edit.remove("ismulp");
						edit.remove("vibtime");	edit.remove("bgcolor");	edit.remove("ssvalue");
						edit.remove("sensity");	edit.remove("monoscr");	edit.remove("showscr");
						edit.remove("timerupd");	edit.remove("timeform");
						edit.remove("screenori");
						edit.commit();
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
			}
		}
	};
	
	//EG训练打乱
	private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
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
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 127;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegh:
				if(isChecked) {
					egoll |= 64;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 191;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegu:
				if(isChecked) {
					egoll |= 32;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 223;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegt:
				if(isChecked) {
					egoll |= 16;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 239;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegl:
				if(isChecked) {
					egoll |= 8;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 247;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegs:
				if(isChecked) {
					egoll |= 4;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 251;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkega:
				if(isChecked) {
					egoll |= 2;
					if(checkBox[5].isChecked()) {checkBox[5].setChecked(false); egoll &= 254;}
				}
				else egoll &= 253;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			case R.id.checkegn:
				if(isChecked) {
					egoll |= 1;
					for(int i=6; i<13; i++)
						if(checkBox[i].isChecked()) checkBox[i].setChecked(false);
				}
				else egoll &= 254;
				edit.putInt("egoll", egoll);
				setEgOll();
				break;
			}
			edit.commit();
		}
	};
	
	private OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			ListView listView = (ListView) arg0;
			switch (listView.getId()) {
			case R.id.list:
				selFilePath = paths.get(arg2);
				tvPathName.setText(selFilePath);
				getFileDirs(selFilePath, false);
				break;
			case R.id.list1:
				if(selScr1 != arg2 - 1) {
					selScr1 = arg2 - 1;
					scr1Adapter.setSelectItem(selScr1 + 1);
					scr1Adapter.notifyDataSetChanged();
					scr2Adapter.setData(getResources().getStringArray(get2ndScr(arg2-1)));
					if(selScr1 == scrIdx) scr2Adapter.setSelectItem(scr2idx);
					else scr2Adapter.setSelectItem(-1);
					scr2Adapter.notifyDataSetChanged();
				}
				break;
			case R.id.list2:
				if(selScr1 != scrIdx || selScr2 != arg2) {
					scrIdx = selScr1;
					scr2idx = selScr2 = arg2;
					set2ndsel();
				}
				popupWindow.dismiss();
				break;
			}
		}
	};
	
	private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if(timer.state == 0) {
				isLongPress = true;
				LayoutInflater factory = LayoutInflater.from(context);
				int layoutId = R.layout.scr_layout;
				view = factory.inflate(layoutId, null);
				editText = (EditText) view.findViewById(R.id.etslen);
				TextView tvScr = (TextView) view.findViewById(R.id.cnt_scr);
				tvScr.setText(crntScr);
				editText.setText(""+Scrambler.scrLen);
				if(Scrambler.scrLen == 0) editText.setEnabled(false);
				else editText.setSelection(editText.getText().length());
				new CustomDialog.Builder(context).setView(view)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String text = editText.getText().toString();
						int len = text.equals("") ? 0:Integer.parseInt(text);
						if(editText.isEnabled() && len>0) {
							if(len>180) len = 180;
							if(len != Scrambler.scrLen) {
								Scrambler.scrLen = len;
								if((scrIdx==-1 && scr2idx==17) || (scrIdx==1 && scr2idx==19) || (scrIdx==20 && scr2idx==4))
									scrState = SCRNONE;
								newScramble(crntScrType);
							}
						}
						hideKeyboard(editText);
					}
				}).setNegativeButton(R.string.copy_scr, new OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int which) {
						if(VERSION.SDK_INT >= 11) {
							android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clip.setPrimaryClip(ClipData.newPlainText("text", crntScr));
						}
						else {
							android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							clip.setText(crntScr);
						}
						Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
						hideKeyboard(editText);
					}
				}).show();
			}
			return true;
		}
	};
	
	//设置各种View、TextView颜色、边框等 TODO
	private void setViews() {
		//打乱显示
		tvScramble.setTextSize(stsize);
		setScrambleFont(monoscr ? 0 : 1);
		//计时器
		tvTimer.setTextSize(ttsize);
		setTimerFont(stSel[8]);
		if(stSel[0] == 0) {
			if(stSel[2] == 0) tvTimer.setText("0.00");
			else tvTimer.setText("0.000");
		} else if(stSel[0] == 1) tvTimer.setText("IMPORT");
		//设置选项
		for(int i=0; i<std.length; i++)
			std[i].setText(itemStr[i][stSel[i]]);
		stdn[0].setText(""+l1len);
		stdn[1].setText(""+l2len);
		btSolver3[0].setText(sol31[solSel[0]]);
		btSolver3[1].setText(sol32[solSel[1]]);
		//屏幕方向
		this.setRequestedOrientation(screenOri[stSel[9]]);
		//拖动条
		int[] ids = {ttsize - 50, stsize - 12, intv - 20, opac, frzTime};
		for(int i=0; i<ids.length; i++) seekBar[i].setProgress(ids[i]);
		//设置TextView
		tvSettings[3].setText(getString(R.string.timer_size) + ttsize);
		tvSettings[4].setText(getString(R.string.scrsize) + stsize);
		tvSettings[10].setText(getString(R.string.row_spacing) + intv);
		tvSettings[29].setText(getString(R.string.time_tap) + frzTime/20D);
		//设置开关
		setSwitchOn(ibSwitch[0], wca);
		setSwitchOn(ibSwitch[1], monoscr);
		setSwitchOn(ibSwitch[2], simss);
		setSwitchOn(ibSwitch[3], opnl);
		setSwitchOn(ibSwitch[4], fulls);
		setSwitchOn(ibSwitch[5], showscr);
		setSwitchOn(ibSwitch[6], conft);
		setSwitchOn(ibSwitch[7], !hidls);
		setSwitchOn(ibSwitch[8], selScr);
		//分组平均
		btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
	}
	
	private void setTextsColor() {
		for(int i=0; i<tvSettings.length; i++) tvSettings[i].setTextColor(colors[1]);
		for(int i=0; i<checkBox.length; i++) checkBox[i].setTextColor(colors[1]);
		tvScramble.setTextColor(colors[1]);
		tvTimer.setTextColor(colors[1]);
		btScramble.setTextColor(colors[1]);
		btSesMean.setTextColor(colors[1]);
		for(int i=0; i<btSolver3.length; i++) 
			btSolver3[i].setTextColor(colors[1]);
		for(int i=0; i<std.length; i++)
			std[i].setTextColor(0x80000000 | (colors[1] & 0xffffff));
		for(int i=0; i<stdn.length; i++)
			stdn[i].setTextColor(0x80000000 | (colors[1] & 0xffffff));
	}
	
	private void setBorders() {
		boolean tag;
		if(bgcolor) tag = Utils.greyLevel(colors[0]) > 220;
		else tag = opac < 25;
		if(tag) {
			btScramble.setBackgroundResource(R.drawable.button_grey_selector);
			btSesMean.setBackgroundResource(R.drawable.button_grey_selector);
			for(int i=0; i<lborder.length; i++) 
				lborder[i].setBackgroundResource(R.drawable.button_grey);
			for(int i=0; i<2; i++)
				btSolver3[i].setBackgroundResource(R.drawable.spinner_grey_style);
		} else {
			btScramble.setBackgroundResource(R.drawable.button_white_selector);
			btSesMean.setBackgroundResource(R.drawable.button_white_selector);
			for(int i=0; i<lborder.length; i++) 
				lborder[i].setBackgroundResource(R.drawable.button_white);
			for(int i=0; i<2; i++)
				btSolver3[i].setBackgroundResource(R.drawable.spinner_white_style);
		}
	}
	
	private void readConf() {	//读取配置 TODO
		scrIdx = (byte) share.getInt("sel", 1);	//打乱种类
		colors[0] = share.getInt("cl0", 0xff66ccff);	// 背景颜色
		colors[1] = share.getInt("cl1", 0xff000000);	// 文字颜色
		colors[2] = share.getInt("cl2", 0xffff00ff);	//最快单次颜色
		colors[3] = share.getInt("cl3", 0xffff0000);	//最慢单次颜色
		colors[4] = share.getInt("cl4", 0xff009900);	//最快平均颜色
		wca = share.getBoolean("wca", false);	//WCA观察
		showscr = share.getBoolean("showscr", true);	//显示打乱状态
		monoscr = share.getBoolean("monoscr", false);	//等宽打乱字体
		hidls = share.getBoolean("hidls", false);	//成绩列表隐藏打乱
		conft = share.getBoolean("conft", true);	//提示确认成绩
		solSel[0] = (byte) share.getInt("cface", 0);	// 十字求解底面
		solSel[1] = (byte) share.getInt("cside", 1);	// 三阶求解颜色
		if(solSel[1] == 6) solSel[1] = 1;
		sesIdx = (byte) share.getInt("group", 0);	// 分组
		scr2idx = (byte) share.getInt("sel2", 0);	// 二级打乱
		ttsize = share.getInt("ttsize", 60);	//计时器字体
		stsize = share.getInt("stsize", 18);	//打乱字体
		stSel[0] = share.getInt("tiway", 0);	// 计时方式
		stSel[1] = share.getInt("timerupd", 0);	// 计时器更新
		stSel[2] = share.getBoolean("prec", true) ? 1 : 0;	// 计时精度
		stSel[3] = share.getInt("multp", 0);	//分段计时
		isMulp = stSel[3] != 0;
		stSel[4] = share.getInt("l2tp", 0);	//滚动平均2类型
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
		l1len = share.getInt("l1len", 5);
		l2len = share.getInt("l2len", 12);
		bgcolor = share.getBoolean("bgcolor", true);	//使用背景颜色
		opac = share.getInt("opac", 35);	//背景图不透明度
		fulls = share.getBoolean("fulls", false);	// 全屏显示
		opnl = share.getBoolean("scron", false);	// 屏幕常亮
		selScr = share.getBoolean("selses", false);	//自动选择分组
		picPath = share.getString("picpath", "");	//背景图片路径
		frzTime = share.getInt("tapt", 0);	//启动延时
		intv = share.getInt("intv", 25);	//成绩列表行距
		outPath = share.getString("scrpath", defPath);
		for(int i=0; i<15; i++) {
			sesType[i] = share.getInt("sestype" + i, 32);
			sesnames[i] = share.getString("sesname" + i, "");
		}
		egtype = share.getInt("egtype", 7);
		egoll = share.getInt("egoll", 254);
		simss = share.getBoolean("simss", false);
	}
	
	private void setEgOll() {
		String ego = "PHUTLSAN";
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<8; i++)
			if((egoll & (1<<(7-i))) != 0)
				sb.append(ego.charAt(i));
		egolls = sb.toString();
	}
	
	private void viewsVisibility(boolean v) {	//TODO
		int vi = v ? 0 : 8;
		btScramble.setVisibility(vi);
		tvScramble.setVisibility(vi);
		if(!v)
			scrambleView.setVisibility(vi);
		rGroup.setVisibility(vi);
		if(!fulls) {
			setFullScreen(!v);
		}
	}
	
	private void set2ndsel() {
		String[] s = getResources().getStringArray(get2ndScr(scrIdx));
		if(scr2idx >= s.length || scr2idx < 0) scr2idx = 0;
		btScramble.setText(scrStr[scrIdx+1] + " - " + s[scr2idx]);
		newScramble(scrIdx << 5 | scr2idx);
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
			if(!cscr.equals("")) inScr.add(cscr);
		}
	}
	
	private void outScr(final String path, final String fileName, final int num) {
		File fPath = new File(path);
		if(fPath.exists() || fPath.mkdirs()) {
			progressDialog.setTitle(getString(R.string.menu_outscr));
			progressDialog.setMax(num);
			progressDialog.show();
			new Thread() {
				public void run() {
					try {
						OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
						for(int i=0; i<num; i++) {
							handler.sendEmptyMessage(100 + i);
							String s=(i+1)+". "+scrambler.getScramble((scrIdx<<5)|scr2idx, false)+"\r\n";
							byte [] bytes = s.toString().getBytes();
							out.write(bytes);
						}
						out.close();
						handler.sendEmptyMessage(7);
					} catch (IOException e) {
						handler.sendEmptyMessage(5);
					}
					progressDialog.dismiss();
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
	
	private void download(final String fileName) {
		final File f = new File(defPath);
    	if(!f.exists()) f.mkdirs();
    	progressDialog.setTitle(getString(R.string.downloading));
    	progressDialog.setMax(100);
    	progressDialog.setProgress(0);
    	progressDialog.show();
        new Thread() {
        	public void run() {
        		try {
                	URL url = new URL("https://raw.github.com/MeigenChou/DCTimer/master/release/"+fileName);
                	URLConnection conn = url.openConnection();
                	conn.connect();
                	InputStream is = conn.getInputStream();
                	int filesum = conn.getContentLength();
                	if(filesum == 0) {
                		progressDialog.dismiss();
                		handler.sendEmptyMessage(6);
                		return;
                	}
                	progressDialog.setMax(filesum / 1024);
                	FileOutputStream fs = new FileOutputStream(defPath+fileName);
                	byte[] buffer = new byte[4096];
                	int byteread, bytesum = 0;
                	while ((byteread = is.read(buffer)) != -1) {
                		bytesum += byteread;
                		fs.write(buffer, 0, byteread);
                		handler.sendEmptyMessage(bytesum / 1024 + 100);
                	}
                	fs.close();
                	Intent intent = new Intent();
                	intent.setAction(android.content.Intent.ACTION_VIEW);
                	intent.setDataAndType(Uri.parse("file://"+defPath+fileName), "application/vnd.android.package-archive");
                	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                	startActivity(intent);
        		} catch (Exception e) {
        			handler.sendEmptyMessage(9);
        		}
        		progressDialog.dismiss();
        	}
        }.start();
	}
	
	private void getFileDirs(String path, boolean listFiles) {
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(path);
		File[] fs = f.listFiles();
		if(fs!=null && fs.length>0) Arrays.sort(fs);
		if(!path.equals("/")) {
			items.add("..");
			paths.add(f.getParent());
		}
		if(fs != null) {
			for(int i=0; i<fs.length; i++) {
				File file = fs[i];
				if(file.isDirectory()) {
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}
			if(listFiles) {
				for(int i=0; i<fs.length; i++) {
					File file = fs[i];
					if(!file.isDirectory()) {
						items.add(file.getName());
						paths.add(file.getPath());
					}
				}
			}
		}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.list_item, items);
		listView.setAdapter(fileList);
	}
	
	private void setGridView() {	//TODO
		if(!isMulp) {
			timesAdapter = new TimesAdapter (context, listLen, intv);
			gvTimes.setNumColumns(3);
		} else {
			timesAdapter = new TimesAdapter(context, listLen, intv, stSel[3]+2);
			gvTimes.setNumColumns(stSel[3] + 2);
		}
		gvTimes.setAdapter(timesAdapter);
	}
	
	private void setGvTitle() {
		if(isMulp) {
			String[] title = new String[stSel[3]+2];
			title[0] = getString(R.string.time);
			for(int i=1; i<stSel[3]+2; i++) title[i] = "P-"+i;
			TitleAdapter ta = new TitleAdapter(context, title, colors[1]);
			gvTitle.setNumColumns(stSel[3]+2);
			gvTitle.setAdapter(ta);
		}
		else {
			String[] title = {getString(R.string.time),
					(stSel[14]==0 ? "avg of " : "mean of ") + l1len,
					(stSel[4]==0 ? "avg of " : "mean of ") + l2len};
			TitleAdapter ta = new TitleAdapter(context, title, colors[1]);
			gvTitle.setNumColumns(3);
			gvTitle.setAdapter(ta);
		}
	}
	
	private String getShareContent() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(getString(R.string.share_c1), Session.resl, btScramble.getText(),
				Statistics.distime(Statistics.minIdx, false), Statistics.distime(Statistics.mean)));
		if(Session.resl>l1len) sb.append(String.format(getString(R.string.share_c2), l1len,
				Statistics.distime(Statistics.bestAvg[0])));
		if(Session.resl>l2len) sb.append(String.format(getString(R.string.share_c2), l2len,
				Statistics.distime(Statistics.bestAvg[1])));
		sb.append(getString(R.string.share_c3));
		return sb.toString();
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
	
	private void setScrambleFont(int f) {
		if (f == 0)
			tvScramble.setTypeface(Typeface.create("monospace", 0));
		else tvScramble.setTypeface(Typeface.create("sans-serif", 0));
	}
	
	private void setTouch(MotionEvent e) {
		if(!simss || scrTouch) {
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown();
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				break;
			}
		} else {
			int count = e.getPointerCount();
			System.out.println(count+", "+e.getAction());
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_POINTER_DOWN:
			case 261:
				if(count > 1) {
					int x1 = (int)e.getX(0)*2/tvTimer.getWidth();
					int x2 = (int)e.getX(1)*2/tvTimer.getWidth();
					if((x1 ^ x2) == 1) {
						if(!touchDown) {
							touchDown();
							touchDown = true;
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case 262:
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
				tvTimer.setTextColor(0xff00ff00);
				Session.multemp[stSel[3]+1-mulpCount] = System.currentTimeMillis();
			}
			else {
				if(stSel[10]>1)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) Session.multemp[stSel[3]+1]=timer.time1;
				viewsVisibility(true);
			}
		} else if(timer.state != 3) {
			if(!scrTouch || timer.state==2) {
				if(frzTime == 0 || (wca && timer.state==0)) {
					tvTimer.setTextColor(0xff00ff00);
					canStart = true;
				} else {
					if(timer.state==0) tvTimer.setTextColor(0xffff0000);
					else tvTimer.setTextColor(0xffffff00);
					timer.freeze();
				}
			}
		}
	}

	private void touchUp() {
		if(timer.state == 0) {
			if(isLongPress) isLongPress = false;
			else if(scrTouch) newScramble(crntScrType);
			else {
				if(frzTime ==0 || canStart) {
					if(stSel[10]==1 || stSel[10]==3)
						vibrator.vibrate(vibTime[stSel[11]]);
					timer.count();
					if(isMulp) {
						mulpCount = stSel[3];
						Session.multemp[0] = timer.time0;
					}
					else mulpCount = 0;
					acquireWakeLock();
					viewsVisibility(false);
				} else {
					timer.stopf();
					tvTimer.setTextColor(colors[1]);
				}
			}
		} else if(timer.state == 1) {
			if(isLongPress) isLongPress = false;
			if(mulpCount!=0) {
				mulpCount--;
				tvTimer.setTextColor(colors[1]);
			}
		} else if(timer.state == 2) {
			if(isLongPress) isLongPress = false;
			if(frzTime ==0 || canStart) {
				isp2 = timer.insp==2 ? 2000 : 0;
				idnf = timer.insp != 3;
				if(stSel[10]==1 || stSel[10]==3)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) Session.multemp[0] = timer.time0;
				acquireWakeLock();
				viewsVisibility(false);
			} else {
				timer.stopf();
				tvTimer.setTextColor(0xffff0000);
			}
		} else {
			if(isLongPress) isLongPress = false;
			if(!wca) {isp2=0; idnf=true;}
			confirmTime((int)timer.time);
			timer.state = 0;
			if(!opnl) releaseWakeLock();
		}
	}
	
	private void inputTime(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			tvTimer.setTextColor(0xff00ff00);
			break;
		case MotionEvent.ACTION_UP:
			tvTimer.setTextColor(colors[1]);
			LayoutInflater factory = LayoutInflater.from(context);
			int layoutId = R.layout.editbox_layout;
			view = factory.inflate(layoutId, null);
			editText = (EditText) view.findViewById(R.id.edit_text);
			new CustomDialog.Builder(context).setTitle(R.string.enter_time).setView(view)
			.setPositiveButton(R.string.btn_ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String time = Utils.convertStr(editText.getText().toString());
					if(time.equals("Error") || Utils.parseTime(time)==0)
						Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_SHORT).show();
					else save(Utils.parseTime(time), (byte) 0);
					hideKeyboard(editText);
				}
			}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					hideKeyboard(editText);
				}
			}).show();
			showKeyboard(editText);
			break;
		}
	}
	
	private void updateGridView(final int type) {
		new Thread() {
			public void run() {
				try {
					sleep(200);
				} catch (Exception e) { }
				switch (type) {
				case 1:
					timesAdapter.setData(listLen);
					break;
				case 2:
					timesAdapter.setHeight(intv);
					break;
				}
				handler.sendEmptyMessage(17);
			}
		}.start();
	}
	
	private void save(int time, int p) {
		session.insert(time, p, crntScr, isMulp);
		listLen = isMulp ? (stSel[3]+2)*(Session.resl+1) : Session.resl*3;
		btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
		updateGridView(1);
		if(sesType[sesIdx] != crntScrType) {
			sesType[sesIdx] = crntScrType;
			edit.putInt("sestype"+sesIdx, crntScrType);
			edit.commit();
		}
		newScramble(crntScrType);
	}
	
	private boolean update(int idx, byte p) {
		if(Session.penalty[idx] != p) {
			session.update(idx, p);
			btSesMean.setText(getString(R.string.session_average)+Statistics.sesMean());
			return true;
		}
		return false;
	}
	
	private void delete(int idx, int col) {
		session.delete(idx, isMulp);
		if(Session.resl > 0) {
			listLen = isMulp ? (Session.resl+1)*col : Session.resl*col;
		} else {
			listLen = 0;
			sesType[sesIdx] = 32;
			edit.remove("sestype"+sesIdx);
			edit.commit();
		}
		btSesMean.setText(getString(R.string.session_average) + Statistics.sesMean());
		updateGridView(1);
	}
	
	private void deleteAll() {
		session.clear();
		listLen = 0;
		btSesMean.setText(getString(R.string.session_average) + "0/0): N/A (N/A)");
		Statistics.maxIdx = Statistics.minIdx = -1;
		updateGridView(1);
		if(sesType[sesIdx] != 32) {
			sesType[sesIdx] = 32;
			edit.remove("sestype"+sesIdx);
			edit.commit();
		}
	}
	
	private void setScramble(int s1, int s2) {
		boolean changed = false;
		if(scrIdx != s1) {
			changed = true;
			scrIdx = s1;
		}
		if(scr2idx != s2) {
			changed = true;
			scr2idx = s2;
		}
		if(changed) {
			set2ndsel();
			if(inScr != null && inScr.size() != 0) inScr = null;
		}
	}
	
	private void showAlertDialog(int i, int j) {
		String t = null;
		switch(i) {
		case 1:
			t = String.format(stSel[14]==0 ? getString(R.string.sta_avg) : getString(R.string.sta_mean), l1len);
			slist = stSel[14]==0 ? averageOf(l1len, j) : meanOf(l1len, j);
			break;
		case 2:
			t = String.format(stSel[4]==0 ? getString(R.string.sta_avg) : getString(R.string.sta_mean), l2len);
			slist = stSel[4]==0 ? averageOf(l2len, j) : meanOf(l2len, j);
			break;
		case 3:
			t = getString(R.string.sta_session_mean);
			slist = sesMean();
			break;
		}
		new CustomDialog.Builder(context).setTitle(t).setMessage(slist)
		.setPositiveButton(R.string.btn_copy, new DialogInterface.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialoginterface, int i) {
				if(VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(ClipData.newPlainText("text", slist));
				}
				else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clip.setText(slist);
				}
				Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
			}
		}).setNeutralButton(R.string.btn_save, new DialogInterface.OnClickListener() {
			LayoutInflater factory;
			EditText et1, et2;
			ImageButton btn;
			public void onClick(DialogInterface dialog, int which) {
				if(defPath == null) {
					Toast.makeText(context, getString(R.string.sd_not_exist), Toast.LENGTH_SHORT).show();
					return;
				}
				factory = LayoutInflater.from(context);
				int layoutId = R.layout.save_stat;
				view = factory.inflate(layoutId, null);
				et1 = (EditText) view.findViewById(R.id.edit_scrpath);
				btn = (ImageButton) view.findViewById(R.id.btn_browse);
				et1.setText(outPath);
				et2 = (EditText) view.findViewById(R.id.edit_scrfile);
				et2.requestFocus();
				et2.setText(String.format(getString(R.string.def_sname), formatter.format(new Date())));
				et2.setSelection(et2.getText().length());
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						selFilePath = et1.getText().toString();
						int lid = R.layout.file_selector;
						final View viewb = factory.inflate(lid, null);
						listView = (ListView) viewb.findViewById(R.id.list);
						File f = new File(selFilePath);
						selFilePath = f.exists() ? selFilePath : Environment.getExternalStorageDirectory().getPath()+File.separator;
						tvPathName = (TextView) viewb.findViewById(R.id.text);
						tvPathName.setText(selFilePath);
						getFileDirs(selFilePath, false);
						listView.setOnItemClickListener(itemListener);
						new CustomDialog.Builder(context).setTitle(R.string.sel_path).setView(viewb)
						.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int j) {
								et1.setText(selFilePath+"/");
							}
						}).setNegativeButton(R.string.btn_cancel, null).show();
					}
				});
				new CustomDialog.Builder(context).setView(view).setTitle(R.string.stat_save)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int i) {
						final String path=et1.getText().toString();
						if(!path.equals(outPath)) {
							outPath = path;
							edit.putString("scrpath", path);
							edit.commit();
						}
						final String fileName=et2.getText().toString();
						File file = new File(path+fileName);
						if(file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
						else if(file.exists()) {
							new CustomDialog.Builder(context).setTitle(R.string.path_dupl)
							.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int j) {
									outStat(path, fileName, slist);
								}
							}).setNegativeButton(R.string.btn_cancel, null).show();
						} else outStat(path, fileName, slist);
						hideKeyboard(et1);
					}
				}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						hideKeyboard(et1);
					}
				}).show();
			}
		}).setNegativeButton(R.string.btn_close, null).show();
	}
	
	private int getViewType(String scr) {
		if(scr.matches("([FRU][2']?\\s*)+")) return 2;
		if(scr.matches("([ULRB]'?\\s*)+")) return Scrambler.TYPE_SKW;
		if(scr.matches("([ULRBulrb]'?\\s*)+")) return Scrambler.TYPE_PYR;
		if(scr.matches("([xFRUBLDMfrubld][2']?\\s*)+")) return 3;
		if(scr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+")) return 4;
		if(scr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+")) return 5;
		if(scr.matches("(((2?[FRUBLD])|(3[FRU]w))[2']?\\s*)+")) return 6;
		if(scr.matches("(((2|3)?[FRUBLD])[2']?\\s*)+")) return 7;
		return 0;
	}
	
	private void newScramble(final int scrType) {
		final boolean ch = crntScrType != scrType;
		crntScrType = scrType;
		if(!ch && inScr!=null && inScrLen<inScr.size()) {
			if(!isInScr) isInScr = true;
			crntScr = inScr.get(inScrLen++);
			scrambler.viewType = getViewType(crntScr);
			switch (insType) {
			case 1:
				if(scrambler.viewType != 2) scrambler.viewType = 0;
				break;
			case 2:
				if(scrambler.viewType != 3) scrambler.viewType = 0;
				break;
			case 3:
				if(scrambler.viewType != 4) scrambler.viewType = 0;
				break;
			case 4:
				if(scrambler.viewType != 5) scrambler.viewType = 0;
				break;
			case 5:
				if(scrambler.viewType != Scrambler.TYPE_PYR) scrambler.viewType = 0;
			case 6:
				if(scrambler.viewType != Scrambler.TYPE_SKW) scrambler.viewType = 0;
			}
			if(scrambler.viewType==3 && stSel[5]!=0) {
				new Thread() {
					public void run() {
						handler.sendEmptyMessage(4);
						scrambler.extSol3(stSel[5], crntScr);
						extsol = scrambler.sc;
						handler.sendEmptyMessage(3);
					}
				}.start();
			}
			else {
				tvScramble.setText(crntScr);
				showScrView(false);
			}
		} else if((scrIdx==-1 && (scr2idx<2 || (scr2idx>3 && scr2idx<8) || scr2idx==10 || scr2idx==15 || scr2idx==17)) ||
				(scrIdx==0 && scr2idx<3 && stSel[6]!=0) ||
				(scrIdx==1 && (scr2idx!=0 || (stSel[5]!=0 && (scr2idx<2 || scr2idx==5 || scr2idx==19)))) ||
				(scrIdx==8 && (scr2idx>1 || (scr2idx<3 && stSel[12]>0))) ||
				(scrIdx==11 && scr2idx>3 && scr2idx<7) ||
				(scrIdx==17 && (scr2idx<3 || scr2idx==6)) ||
				scrIdx==20) {	//TODO
			if(isInScr) isInScr = false;
			if(ch) scrState = SCRNONE;
			if(scrState == SCRNONE || scrState == SCRDONE) {
				new Thread() {
					public void run() {
						if(scrState == SCRDONE) {
							crntScr = nextScr;
							extsol = scrambler.sc;
						} else {
							scrState = SCRING;
							if(scrIdx==-1 && (scr2idx==1 || scr2idx == 15)) {
								threephase.Util.init(handler);
							}
							handler.sendEmptyMessage(2);
							crntScr = scrambler.getScramble((scrIdx<<5)|scr2idx, ch);
							extsol = scrambler.sc;
						}
						if(scrType == crntScrType) {
							showScramble();
							scrState = NEXTSCRING;
							getNextScramble(ch);
						}
					}
				}.start();
			} else if(scrState == NEXTSCRING) {
				if(!nextScrWaiting) {
					nextScrWaiting = true;
					tvScramble.setText(getString(R.string.scrambling));
				}
			}
		} else {
			scrState = SCRING;
			crntScr = scrambler.getScramble(scrIdx<<5|scr2idx, ch);
			tvScramble.setText(crntScr);
			showScrView(false);
			scrState = SCRDONE;
		}
	}
	
	public void showScramble() {
		if((scrIdx==0 && stSel[6]!=0) ||
				(stSel[5]!=0 && (scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
						scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))))
			handler.sendEmptyMessage(3);
		else if(scrIdx==8 && scr2idx<3 && stSel[12]>0)
			handler.sendEmptyMessage(1);
		else handler.sendEmptyMessage(0);
		showScrView(true);
	}
	
	public void getNextScramble(boolean ch) {
		System.out.println("get next scramble...");
		scrState = NEXTSCRING;
		nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, ch);
		System.out.println("next scr: " + nextScr);
		//System.out.println("next solve: " + Scramble.sc);
		scrState = SCRDONE;
		if(nextScrWaiting) {
			crntScr = nextScr;
			extsol = scrambler.sc;
			showScramble();
			nextScrWaiting = false;
			getNextScramble(ch);
		}
	}
	
	public void showScrView(boolean isThread) {
		if (!showscr) return;
		//if(bmScrView != null) bmScrView.recycle();
		if(scrambler.viewType > 0) {
			bmScrView = Bitmap.createBitmap(dip300, dip300*3/4, Config.ARGB_8888);
			Canvas c = new Canvas(bmScrView);
			c.drawColor(0);
			Paint p = new Paint();
			p.setAntiAlias(true);
			scrambler.drawScr(scr2idx, dip300, p, c);
			if(isThread) handler.sendEmptyMessage(15);
			else {
				scrambleView.setVisibility(View.VISIBLE);
				scrambleView.setImageBitmap(bmScrView);
			}
		} else if(isThread) handler.sendEmptyMessage(14);
		else scrambleView.setVisibility(View.GONE);
	}
	
	public void confirmTime(final int time) {
		if(idnf) {
			if(conft) {
				new CustomDialog.Builder(context).setTitle(getString(R.string.show_time)+Statistics.distime(time + isp2))
						.setItems(R.array.rstcon, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:save(time + isp2, 0);break;
						case 1:save(time + isp2, 1);break;
						case 2:save(time + isp2, 2);break;
						}
					}
				})
				.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						newScramble(crntScrType);
					}
				}).show();
			}
			else save(time + isp2, 0);
		}
		else {
			if(conft)
				new CustomDialog.Builder(context).setTitle(R.string.time_dnf).setMessage(R.string.confirm_adddnf)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int j) {
						save((int)timer.time, 2);
					}
				}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						newScramble(crntScrType);
					}
				}).show();
			else save((int)timer.time, 2);
		}
	}
	
	public String sesMean() {
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_solve) + Statistics.solved + "/" + Session.resl + "\r\n");
		sb.append(getString(R.string.ses_mean) + Statistics.distime(Statistics.mean) + " ");
		sb.append("(σ = " + Statistics.standardDeviation(Statistics.sd) + ")\r\n");
		sb.append(getString(R.string.ses_avg) + Statistics.sesAvg() + "\r\n");
		if(Session.resl >= l1len && Statistics.bestIdx[0] != -1) 
			sb.append(String.format(stSel[14]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean), l1len)
					+ Statistics.distime(Statistics.bestAvg[0]) + "\r\n");
		if(Session.resl >= l2len && Statistics.bestIdx[1] != -1) 
			sb.append(String.format(stSel[4]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean), l2len)
					+ Statistics.distime(Statistics.bestAvg[1]) + "\r\n");
		sb.append(getString(R.string.stat_best) + Statistics.distime(Statistics.minIdx, false) + "\r\n");
		sb.append(getString(R.string.stat_worst) + Statistics.distime(Statistics.maxIdx, false) + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		for(int i=0; i<Session.resl; i++) {
			if(!hidls) sb.append("\r\n" + (i+1) + ". ");
			sb.append(Statistics.distime(i, true));
			String s = session.getString(i, 6);
			if(s!=null && !s.equals("")) sb.append("[" + s + "]");
			if(hidls && i<Session.resl-1) sb.append(", ");
			if(!hidls) sb.append("  " + session.getString(i, 4));
		}
		return sb.toString();
	}
	
	public String averageOf(int n, int i) {
		int cavg = 0, csdv = -1, ind = 1;
		int trim = (int) Math.ceil(n/20.0);
		int max, min;
		ArrayList<Integer> dnfIdx = new ArrayList<Integer>();
		ArrayList<Integer> midx = new ArrayList<Integer>();
		for(int j=i-n+1; j<=i; j++)
			if(Session.penalty[j] == 2)
				dnfIdx.add(j);
		int dnf = dnfIdx.size();
		long[] data = new long[n - dnf];
		int len = 0;
		for(int j=i-n+1; j<=i; j++)
			if(Session.penalty[j] != 2) {
				data[len++] = (long)Session.getTime(j) << 32 | j;
			}
		Arrays.sort(data);
		if(n-dnf >= trim) {
			for(int j=0; j<trim; j++) midx.add((int)data[j]);
		} else {
			for(int j=0; j<data.length; j++) midx.add((int)data[j]);
			for(int j=0; j<trim-n+dnf; j++) midx.add(dnfIdx.get(j));
		}
		boolean m = dnf > trim;
		min = midx.get(0);
		if(m) {
			for(int j=dnf-trim; j<dnf; j++) midx.add(dnfIdx.get(j));
		} else {
			for(int j=n-trim; j<n-dnf; j++) midx.add((int)data[j]);
			for(int j=0; j<dnf; j++) midx.add(dnfIdx.get(j));
			long sum = 0;
			double sum2 = 0;
			for(int j=trim; j<n-trim; j++) {
				data[j] >>= 32;
				if(stSel[2] == 0) data[j] /= 10;
				sum += data[j];
				sum2 += (double)data[j] * data[j];
			}
			int num = n-trim*2;
			cavg = (int) (sum/num+0.5);
			csdv = (int) (Math.sqrt((sum2-sum*sum/num)/num)+0.5);
			if(stSel[2] == 0) cavg *= 10;
		}
		max = midx.get(midx.size() - 1);
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_avg) + (m?"DNF":Statistics.distime(cavg)) + " ");
		sb.append("(σ = " + Statistics.standardDeviation(csdv) + ")\r\n");
		sb.append(getString(R.string.stat_best) + Statistics.distime(min,false) + "\r\n");
		sb.append(getString(R.string.stat_worst) + Statistics.distime(max,false) + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		for(int j=i-n+1; j<=i; j++) {
			String s = session.getString(j, 6);
			if(!hidls) sb.append("\r\n" + (ind++) + ". ");
			if(midx.indexOf(j) > -1) sb.append("(");
			sb.append(Statistics.distime(j, false));
			if(s!=null && !s.equals("")) sb.append("[" + s + "]");
			if(midx.indexOf(j) > -1) sb.append(")");
			if(hidls && j<i) sb.append(", ");
			if(!hidls) sb.append("  " + session.getString(j, 4));
		}
		return sb.toString();
	}
	
	public String meanOf(int n, int i) {
		int max, min, dnf = 0;
		int cavg = 0, csdv = -1, ind = 1;
		long sum = 0;
		double sum2 = 0;
		max = min = i-n+1;
		boolean m = false;
		for(int j=i-n+1; j<=i; j++) {
			if(Session.penalty[j] != 2 && !m) { min = j; m = true; }
			if(Session.penalty[j] == 2) { max = j; dnf++; }
		}
		m = dnf > 0;
		if(!m) {
			for (int j=i-n+1; j<=i; j++) {
				int time = Session.getTime(j);
				if(time > Session.getTime(max)) max = j;
				if(time <= Session.getTime(min)) min = j;
				if(stSel[2] == 0) time /= 10;
				sum += time;
				sum2 += (long)time * time;
			}
			cavg = (int) (sum/n+0.5);
			csdv = (int) (Math.sqrt((sum2-sum*sum/n)/n)+0.5);
		}
		if(stSel[2] == 0) cavg *= 10;
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_mean) + (m ? "DNF" : Statistics.distime(cavg)) + " ");
		sb.append("(σ = " + Statistics.standardDeviation(csdv) + ")\r\n");
		sb.append(getString(R.string.stat_best) + Statistics.distime(min, false) + "\r\n");
		sb.append(getString(R.string.stat_worst) + Statistics.distime(max, false) + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		for(int j=i-n+1; j<=i; j++) {
			if(!hidls) sb.append("\r\n" + (ind++) + ". ");
			sb.append(Statistics.distime(j, false));
			String s = session.getString(j, 6);
			if(s!=null && !s.equals("")) sb.append("[" + s + "]");
			if(hidls && j<i) sb.append(", ");
			if(!hidls) sb.append("  " + session.getString(j, 4));
		}
		return sb.toString();
	}
	
	private void acquireWakeLock() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	private void releaseWakeLock() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	private void getSession(int i) {
		session.getSession(i, isMulp);
		if(Session.resl != 0) {
			listLen = isMulp ? (stSel[3]+2)*(Session.resl+1) : Session.resl*3;
		} else {
			listLen = 0;
		}
	}
	
	private void showTime(final int p, final int col) {
		session.move(p/col);
		final String scr = session.getString(4);
		String time = session.getString(5);
		String n = session.getString(6);
		if(n==null) n="";
		final String comment = n;
		if(time!=null) time="\n("+time+")";
		else time = "";
		LayoutInflater factory = LayoutInflater.from(context);
		int layoutId = R.layout.singtime;
		view = factory.inflate(layoutId, null);
		editText = (EditText) view.findViewById(R.id.etnote);
		TextView tvTime=(TextView) view.findViewById(R.id.st_time);
		final TextView tvScr=(TextView) view.findViewById(R.id.st_scr);
		tvTime.setText(getString(R.string.show_time)+Statistics.distime(p/col,true)+time);
		tvScr.setText(scr);
		if(Session.penalty[p/col]==2) {
			RadioButton rb = (RadioButton) view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(Session.penalty[p/col]==1) {
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
		new CustomDialog.Builder(context).setView(view)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean tag = false;
				RadioGroup rg = (RadioGroup) view.findViewById(R.id.st_penalty);
				int rgid = rg.getCheckedRadioButtonId();
				switch(rgid) {
				case R.id.st_pe1: tag = update(p/col, (byte)0); break;
				case R.id.st_pe2: tag = update(p/col, (byte)1); break;
				case R.id.st_pe3: tag = update(p/col, (byte)2); break;
				}
				String text = editText.getText().toString();
				if(!text.equals(comment)) {
					session.update(text);
					tag = true;
				}
				hideKeyboard(editText);
				if(tag) updateGridView(1);
			}
		}).setNeutralButton(R.string.copy_scr, new DialogInterface.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialog, int which) {
				if(VERSION.SDK_INT > 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setPrimaryClip(ClipData.newPlainText("text", scr));
				}
				else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clip.setText(scr);
				}
				Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
				hideKeyboard(editText);
			}
		}).setNegativeButton(R.string.delete_time, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int which) {
				delete(p/col, col);
				hideKeyboard(editText);
			}
		}).show();
	}
	
	private void showKeyboard(final EditText et) {
		new Thread() {
			public void run() {
				try {
					sleep(200);
				} catch (Exception e) { }
				InputMethodManager inm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				inm.showSoftInput(et, 0);
			}
		}.start();
	}
	
	private void hideKeyboard(EditText et) {
		InputMethodManager inm = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		inm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	private void setSwitchOn(ImageButton bt, boolean isOn) {
		if(isOn) bt.setImageResource(R.drawable.switch_on);
		else bt.setImageResource(R.drawable.switch_off);
	}
	
	private void setFullScreen(boolean fs) {
		if (fs)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	private void exportDB(final String path) {
		File f = new File(defPath);
		if(!f.exists()) f.mkdirs();
		progressDialog.setTitle(getString(R.string.out_db));
		progressDialog.setMax(15);
		progressDialog.show();
		new Thread() {
			public void run() {
				try {
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
					for(int i=0; i<15; i++) {
						handler.sendEmptyMessage(100 + i);
						Cursor cur = session.getCursor(i);
						int count = cur.getCount();
						if(count == 0) continue;
						writer.write(i+1+"\r\n");
						cur.moveToFirst();
						for(int j=0; j<count; j++) {
							writer.write(cur.getInt(1)+"\t"+cur.getInt(2)+"\t"+cur.getInt(3)+"\t");
							writer.write(cur.getString(4).replace("\n", "\\n")+"\t"+cur.getString(5)+"\t");
							if(cur.getString(6) != null)
								writer.write(cur.getString(6).replace("\t", "\\t")+"\t");
							else writer.write("\t");
							writer.write(cur.getInt(7)+"\t"+cur.getInt(8)+"\t"+cur.getInt(9)+"\t"
									+cur.getInt(10)+"\t"+cur.getInt(11)+"\t"+cur.getInt(12)+"\r\n");
							cur.moveToNext();
						}
						cur.close();
					}
					writer.close();
					handler.sendEmptyMessage(7);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
				}
				progressDialog.dismiss();
			}
		}.start();
	}
	
	private void importDB(final String path) {
		progressDialog.setTitle(getString(R.string.in_db));
		progressDialog.setMax(15);
		progressDialog.show();
		new Thread() {
			public void run() {
				int dbCount = 0;
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
					String line = "";
					int count = 1;
					DBHelper dbh = new DBHelper(context);
					SQLiteDatabase db = dbh.getWritableDatabase();
					db.beginTransaction();
					SQLiteStatement stmt = null;// = db.compileStatement("insert into commodity(commodity_icon, commodity_photo, category_id, commodity_name, commodity_price) values(?, ?, ?, ?, ?)");
					while (((line = reader.readLine()) != null)) {
						if(!line.contains("\t")) {
							crntProgress = Integer.parseInt(line);
							Cursor cur = db.query(DBHelper.TBL_NAME[crntProgress-1], null, null, null, null, null, null);
							if(cur.getCount() > 0) {
								cur.moveToLast();
								count = cur.getInt(0) + 1;
							} else count = 1;
							cur.close();
							stmt = db.compileStatement("insert into "+DBHelper.TBL_NAME[crntProgress-1]+" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
							handler.sendEmptyMessage(100+crntProgress);
						} else {
							String[] ts = line.split("\t");
							stmt.bindLong(1, count++);
							stmt.bindLong(2, Integer.parseInt(ts[0]));
							stmt.bindLong(3, Integer.parseInt(ts[1]));
							stmt.bindLong(4, Integer.parseInt(ts[2]));
							stmt.bindString(5, ts[3].replace("\\n", "\n"));
							if(ts[4].equals("null")) stmt.bindString(6, "");
							else stmt.bindString(6, ts[4]);
							if(ts[5].equals("null")) stmt.bindString(7, "");
							else stmt.bindString(7, ts[5].replace("\\t", "\t"));
							for(int i=0; i<6; i++)
								stmt.bindLong(i+8, Integer.parseInt(ts[i+6]));
							stmt.execute();
							stmt.clearBindings();
							dbCount++;
						}
					}
					db.setTransactionSuccessful();
					db.endTransaction();
					reader.close();
					progressDialog.dismiss();
					handler.sendEmptyMessage(12);
					if(dbCount > 0) {
						getSession(sesIdx);
						handler.sendEmptyMessage(13);
					}
				} catch (Exception e) {
					progressDialog.dismiss();
					handler.sendEmptyMessage(11);
				}
			}
		}.start();
	}
}

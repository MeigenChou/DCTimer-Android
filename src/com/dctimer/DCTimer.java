package com.dctimer;

import static com.dctimer.Configs.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dctimer.adapter.ListAdapter;
import com.dctimer.adapter.TextAdapter;
import com.dctimer.db.*;
import com.dctimer.model.Stackmat;
import com.dctimer.model.Timer;
import com.dctimer.ui.CustomDialog;
import com.dctimer.util.*;
import com.dctimer.view.ColorPickerView;
import com.dctimer.view.ColorSchemeView;

import scrambler.Scrambler;
import solver.Sq1Shape;
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
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.TabSpec;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class DCTimer extends Activity {
	public Context context;
	private TabHost tabHost;
	private RadioButton[] rbTab = new RadioButton[3];
	private RadioGroup rGroup;
	
	private Button btScramble;	//打乱按钮
	private PopupWindow popupWindow;	//打乱弹出窗口
	private TextAdapter scr1Adapter;
	private TextAdapter scr2Adapter;
	private TextView tvTimer;	//计时器
	private TextView tvScramble;	// 显示打乱
	private Bitmap bmScrView;
	private ImageView scrambleView;	//打乱状态图
	
	private ListView lvTimes;	//成绩列表
	public ListAdapter timeAdapter;
	private LinearLayout layoutTitle;	//成绩标题
	private Button btSesMean, btSesOptn, btSession;	//分组平均, 分组选项, 分组
	private TextView tvSesName;	//分组名称
	
	private TextView tvTitle;	//设置
	private TextView[] tvSettings = new TextView[5];
	private TextView[] std = new TextView[15];
	private TextView[] stdn = new TextView[2];
	private SeekBar[] seekBar = new SeekBar[7];	//拖动条
	private ImageButton[] ibSwitch = new ImageButton[9];	//开关
	private Button[] btSolver3 = new Button[2];	//三阶求解
	private RelativeLayout[] llayout = new RelativeLayout[27];
	private CheckBox[] checkBox = new CheckBox[11];	//EG打乱设置
	private Button btReset;	//设置复位
	
	private TextView tvPathName;
	private View tabLine;
	private View view;
	private ListView listView;
	private ProgressDialog progressDialog;
	private EditText editText;
	public Bitmap bitmap;
	
	public SharedPreferences share;
	public static SharedPreferences.Editor edit;
	public static DisplayMetrics dm;
	
	public Scrambler scrambler;
	public Timer timer;
	public Session session;
	public Stackmat stm;
	public Vibrator vibrator;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
	
	private boolean nextScrWaiting = false;
	private boolean scrTouch = false;
	private boolean isLongPress;
	private boolean touchDown;
	public static boolean canStart;
	private int mulpCount;
	private int version;
	private int[] resId = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img1_selected, R.drawable.img2_selected, R.drawable.img3_selected};
	private int selScr1, selScr2;
	private int crntProgress;
	private long exitTime = 0;
	private String newVersion, updateCont;
	public static String dataPath;
	private static String slist;
	public List<String> items = null, paths = null;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int msw = msg.what;
			switch (msw) {
			case 0: tvScramble.setText(scrambler.crntScr); break;
			case 1: tvScramble.setText(scrambler.crntScr + "\n\n" + getString(R.string.shape) + extsol);	break;
			case 2: tvScramble.setText(getString(R.string.scrambling));	break;
			case 3: tvScramble.setText(scrambler.crntScr + extsol);	break;
			case 4: tvScramble.setText(scrambler.crntScr + "\n\n" + getString(R.string.solving));	break;
			case 5: Toast.makeText(context, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();	break;
			case 6: Toast.makeText(context, getString(R.string.file_error), Toast.LENGTH_LONG).show();
			case 7: Toast.makeText(context, getString(R.string.save_success), Toast.LENGTH_SHORT).show();	break;
			case 8: Toast.makeText(context, getString(R.string.conning), Toast.LENGTH_SHORT).show();	break;
			case 9: Toast.makeText(context, getString(R.string.net_error), Toast.LENGTH_LONG).show();	break;
			case 10: Toast.makeText(context, getString(R.string.lastest), Toast.LENGTH_LONG).show();	break;
			case 11: Toast.makeText(context, getString(R.string.import_failed), Toast.LENGTH_SHORT).show(); break;
			case 12: Toast.makeText(context, getString(R.string.import_success), Toast.LENGTH_SHORT).show(); break;
			case 13:
				btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
				setListView();
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
					.setPositiveButton(R.string.btn_download, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							download("DCTimer"+newVersion+".apk");
						}
					}).setNegativeButton(R.string.btn_cancel, null).show();
				break;
			case 17: 
				timeAdapter.notifyDataSetChanged();
				break;
			case 18: tvScramble.setText(getString(R.string.initing) + " (0%) ..."); break;
			case 19: tvScramble.setText(getString(R.string.initing) + " (19%) ..."); break;
			case 20: tvScramble.setText(getString(R.string.initing) + " (26%) ..."); break;
			case 21: tvScramble.setText(getString(R.string.initing) + " (34%) ..."); break;
			case 22: tvScramble.setText(getString(R.string.initing) + " (" + (36 + cs.threephase.Util.c4prog / 44809) + "%) ..."); break;
			case 30: timeAdapter.setData();	break;
			case 31: timeAdapter.setHeight((int) Math.round(rowSpacing * scale));	break;
			case 32: lvTimes.setSelection(timeAdapter.getCount()-1); break;
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
		if(VERSION.SDK_INT >= 19) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		super.setContentView(R.layout.tab);
		context = this;
		share = super.getSharedPreferences("dctimer", Activity.MODE_PRIVATE);
		edit = share.edit();
		dm = getResources().getDisplayMetrics();
		scale = dm.density;
		fontScale = dm.scaledDensity;
		//System.out.println(scale+", "+fontScale);
		dip300 = (int) Math.round(scale * 300);
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			defPath = Environment.getExternalStorageDirectory().getPath()+"/DCTimer/";
			//System.out.println("SD卡 "+defPath);
		}
		dataPath = getFilesDir().getParent() + "/databases/";
		//System.out.println("data path: "+dataPath);
		
		readConf();
		scrAry = getResources().getStringArray(R.array.cubeStr);
		sol31 = getResources().getStringArray(R.array.faceStr);
		sol32 = getResources().getStringArray(R.array.sideStr);
		for(int i=0; i<15; i++)
			itemStr[i] = getResources().getStringArray(staid[i]);
		scr2Ary = getResources().getStringArray(Utils.get2ndScr(scrIdx));
		if(scr2idx >= scr2Ary.length || scr2idx < 0) scr2idx = 0;
		Utils.setEgOll();
		if(fullScreen) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(opnl) acquireWakeLock();
		
		scrambler = new Scrambler(this);
		timer = new Timer(this);
		session = new Session(this);
		stm = new Stackmat(this);
		
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
		if(useBgcolor) tabHost.setBackgroundColor(colors[0]);
		else try {
			Bitmap bm = Utils.getBitmap(picPath);
			bitmap = Utils.getBackgroundBitmap(bm);
			tabHost.setBackgroundDrawable(Utils.getBackgroundDrawable(context, bitmap, opacity));
			bm.recycle();
		} catch (Exception e) {
			tabHost.setBackgroundColor(colors[0]);
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (OutOfMemoryError e) {
			tabHost.setBackgroundColor(colors[0]);
			Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		rGroup = (RadioGroup) findViewById(R.id.main_radio);
		tabLine = (View) findViewById(R.id.tab_line);
		//计时
		btScramble = (Button) findViewById(R.id.bt_scr);	//打乱按钮
		btScramble.setOnClickListener(mOnClickListener);
		tvScramble = (TextView) findViewById(R.id.tv_scr);	//打乱显示
		tvScramble.setOnTouchListener(mOnTouchListener);
		tvScramble.setOnLongClickListener(mOnLongClickListener);
		tvScramble.setTextColor(colors[1]);
		//tvScramble.setHeight((int)(DCTimer.dm.heightPixels*0.3));
		tvTimer = (TextView) findViewById(R.id.tv_timer);	//计时器
		tvTimer.setOnTouchListener(mOnTouchListener);
		scrambleView = (ImageView) findViewById(R.id.iv_scr);	//打乱状态图
		
		//分组名称
		for (int j = 0; j < 15; j++)
			sesItems[j] = (j + 1) + ". " + sesnames[j];
		//成绩
		tvSesName = (TextView) findViewById(R.id.sesname);
		tvSesName.setText(sesnames[sesIdx].equals("") ? getString(R.string.session)+(sesIdx+1) : sesnames[sesIdx]);
		tvSesName.setTextColor(colors[1]);
		//成绩列表
		layoutTitle = (LinearLayout) findViewById(R.id.layout_title);
		lvTimes = (ListView) findViewById(R.id.lv_times);
		btSesMean = (Button) findViewById(R.id.bt_ses_mean);	//分组平均
		session.getSession(sesIdx, isMulp);
		btSesMean.setOnClickListener(mOnClickListener);
		btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
		setResultTitle();
		setListView();
		btSession = (Button) findViewById(R.id.bt_session);	//分组按钮
		btSession.setOnClickListener(mOnClickListener);
		btSesOptn = (Button) findViewById(R.id.bt_optn);	//分组选项
		btSesOptn.setOnClickListener(mOnClickListener);
		tvTitle = (TextView) findViewById(R.id.tv_setting);	//设置
		tvTitle.setTextColor(colors[1]);
		//设置TextView
		ids = new int[] {R.id.stt08, R.id.stt09, R.id.stt17, R.id.stt31, R.id.stt05};
		for(int i=0; i<ids.length; i++) tvSettings[i] = (TextView) findViewById(ids[i]);
		
		//设置选项
		ids = new int[] {R.id.std01, R.id.std02, R.id.std03, R.id.std04, R.id.std16, R.id.std06, R.id.std07, R.id.std08, 
				R.id.std09, R.id.std10, R.id.std11, R.id.std12, R.id.std13, R.id.std14, R.id.std15};
		for(int i=0; i<std.length; i++) {
			std[i] = (TextView) findViewById(ids[i]);
		}
		stdn[0] = (TextView) findViewById(R.id.std17);	//平均1长度
		stdn[1] = (TextView) findViewById(R.id.std18);	//平均2长度
		//拖动条
		ids = new int[] {R.id.seekb1, R.id.seekb2, R.id.seekb3, R.id.seekb4, R.id.seekb5, R.id.seekb6, R.id.seekb7};
		for(int i=0; i<ids.length; i++) {
			seekBar[i] = (SeekBar) findViewById(ids[i]);
			seekBar[i].setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		}
		//设置开关
		ids = new int[] {R.id.stcheck1, R.id.stcheck15, R.id.stcheck3, R.id.stcheck12, R.id.stcheck11, R.id.stcheck6,
				R.id.stcheck7, R.id.stcheck8, R.id.stcheck9};
		for(int i=0; i<ids.length; i++) {
			ibSwitch[i] = (ImageButton) findViewById(ids[i]);
			ibSwitch[i].setOnClickListener(mOnClickListener);
		}
		
		btSolver3[0] = (Button) findViewById(R.id.solve1);	//十字底面
		btSolver3[0].setOnClickListener(mOnClickListener);
		btSolver3[1] = (Button) findViewById(R.id.solve2);	//颜色
		btSolver3[1].setOnClickListener(mOnClickListener);
		//设置Layout
		ids = new int[] {
				R.id.lay01, R.id.lay02, R.id.lay03, R.id.lay04, R.id.lay26, R.id.lay06,
				R.id.lay07, R.id.lay08, R.id.lay09, R.id.lay10, R.id.lay11, R.id.lay12,
				R.id.lay23, R.id.lay18, R.id.lay25,
				R.id.lay16, R.id.lay17, R.id.lay22, R.id.lay19, R.id.lay20, R.id.lay21,
				R.id.lay13, R.id.lay24, R.id.lay14, R.id.lay15, R.id.lay27, R.id.lay28,
		};
		for(int i=0; i<ids.length; i++)
			llayout[i] = (RelativeLayout) findViewById(ids[i]);
		for(int i=0; i<15; i++) llayout[i].setOnClickListener(comboListener);
		for(int i=15; i<27; i++) llayout[i].setOnClickListener(mOnClickListener);
		//EG打乱
		ids = new int[] {R.id.checkcll, R.id.checkeg1, R.id.checkeg2,
				R.id.checkegpi, R.id.checkegh, R.id.checkegu, R.id.checkegt, R.id.checkegl,
				R.id.checkegs, R.id.checkega, R.id.checkegn};
		for(int i=0; i<ids.length; i++) {
			checkBox[i] = (CheckBox) findViewById(ids[i]);
			checkBox[i].setOnCheckedChangeListener(mOnCheckedChangeListener);
		}
		if((egtype & 4) != 0) checkBox[0].setChecked(true);
		if((egtype & 2) != 0) checkBox[1].setChecked(true);
		if((egtype & 1) != 0) checkBox[2].setChecked(true);
		if((egoll & 128) != 0) checkBox[3].setChecked(true);
		if((egoll & 64) != 0) checkBox[4].setChecked(true);
		if((egoll & 32) != 0) checkBox[5].setChecked(true);
		if((egoll & 16) != 0) checkBox[6].setChecked(true);
		if((egoll & 8) != 0) checkBox[7].setChecked(true);
		if((egoll & 4) != 0) checkBox[8].setChecked(true);
		if((egoll & 2) != 0) checkBox[9].setChecked(true);
		if((egoll & 1) != 0) checkBox[10].setChecked(true);
		//复位按钮
		btReset = (Button) findViewById(R.id.reset);
		btReset.setOnClickListener(mOnClickListener);
		//进度条
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		//震动器
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		version = Utils.getVersion(context);
		//timerFrag.set2ndsel();
		setViews();
		set2ndsel();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//System.out.println("旋转屏幕");
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(!useBgcolor) try {
			Bitmap bm = Utils.getBitmap(picPath);
			bitmap = Utils.getBackgroundBitmap(bm);
			tabHost.setBackgroundDrawable(Utils.getBackgroundDrawable(context, bitmap, opacity));
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
					Utils.hideKeyboard(editText);
					final String scrs = editText.getText().toString();
					inScr = new ArrayList<String>();
					inScrLen = 0;
					Utils.setInScr(scrs, inScr);
					if(inScr.size()>0) newScramble(crntScrType);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					Utils.hideKeyboard(editText);
				}
			}).show();
			Utils.showKeyboard(editText);
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
			et2.setText(savePath);
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
					if(!path.equals(savePath)) {
						savePath = path;
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
					Utils.hideKeyboard(et1);
				}
			}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Utils.hideKeyboard(et1);
				}
			}).show();
			Utils.showKeyboard(et1);
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
				useBgcolor = false;
				edit.putString("picpath", picPath);
				edit.putBoolean("bgcolor", false);
				edit.commit();
				try {
					Bitmap bm = Utils.getBitmap(picPath);
					bitmap = Utils.getBackgroundBitmap(bm);
					tabHost.setBackgroundDrawable(Utils.getBackgroundDrawable(context, bitmap, opacity));
					bm.recycle();
				} catch (Exception e) {
					Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
				} catch (OutOfMemoryError e) {
					Toast.makeText(context, "Out of memory error: bitmap size exceeds VM budget", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(Timer.state == 1) {
				timer.count();
				setVisibility(true);
				if(!wca) {isp2 = 0; idnf = true;}
				confirmTime((int) timer.time);
				Timer.state = 0;
				if(!opnl) releaseWakeLock();
			} else if(Timer.state == 2) {
				timer.stopInspect();
				setTimerText(stSel[2]==0 ? "0.00" : "0.000");
				setVisibility(true);
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
		case KeyEvent.KEYCODE_Q:	changeScramble(-1, 10);	break;	//SQ1
		case KeyEvent.KEYCODE_W:	changeScramble(-1, 3);	break;	//二阶
		case KeyEvent.KEYCODE_E:	changeScramble(-1, 0);	break;	//三阶
		case KeyEvent.KEYCODE_R:	changeScramble(2, 0);	break;	//四阶
		case KeyEvent.KEYCODE_T:	changeScramble(-1, 2);	break;	//五阶
		case KeyEvent.KEYCODE_Y:	changeScramble(-1, 13);	break;	//六阶
		case KeyEvent.KEYCODE_U:	changeScramble(-1, 14);	break;	//七阶
		case KeyEvent.KEYCODE_M:	changeScramble(-1, 8);	break;	//五魔
		case KeyEvent.KEYCODE_P:	changeScramble(-1, 9);	break;	//金字塔
		case KeyEvent.KEYCODE_K:	changeScramble(-1, 11);	break;	//魔表
		case KeyEvent.KEYCODE_S:	changeScramble(-1, 12);	break;	//斜转
		case KeyEvent.KEYCODE_N:	newScramble(crntScrType);	break;	//新打乱
		case KeyEvent.KEYCODE_Z:	//删除最后成绩
			if(Session.length==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_del_last)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					delete(Session.length-1);
				}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case KeyEvent.KEYCODE_A:	//删除所有成绩
			if(Session.length==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(R.string.confirm_clear_session)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {deleteAll();}
			}).setNegativeButton(R.string.btn_cancel, null).show();
			break;
		case KeyEvent.KEYCODE_D:	//最近一次成绩
			if(Session.length==0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
			else new CustomDialog.Builder(context).setTitle(getString(R.string.show_time) + Statistics.timeAt(Session.length-1, true))
			.setItems(R.array.rstcon, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0: update(Session.length-1, (byte) 0); break;
					case 1: update(Session.length-1, (byte) 1); break;
					case 2: update(Session.length-1, (byte) 2); break;
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
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		ColorPickerView colorPickerView;
		ColorSchemeView colorSchemeView;
		@Override
		public void onClick(View v) {
			final int sel = v.getId();
			switch (sel) {
			case R.id.bt_scr:	//选择打乱
				selScr1 = scrIdx;
				selScr2 = scr2idx;
				int resId = R.layout.pop_window;
				view = LayoutInflater.from(context).inflate(resId, null);
				listView = (ListView) view.findViewById(R.id.list1);
				scr1Adapter = new TextAdapter(context, scrAry, selScr1+1, 1);
				listView.setAdapter(scr1Adapter);
				listView.setSelection(selScr1 + 1);
				listView.setOnItemClickListener(itemListener);
				listView = (ListView) view.findViewById(R.id.list2);
				scr2Adapter = new TextAdapter(context, getResources().getStringArray(Utils.get2ndScr(scrIdx)), selScr2, 2);
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
							sesIdx = which;
							session.getSession(which, isMulp);
							btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
							setListView();
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
									Utils.hideKeyboard(editText);
								}
							}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Utils.hideKeyboard(editText);
								}
							}).show();
							Utils.showKeyboard(editText);
							break;
						case 1:	//清空成绩
							if(Session.length == 0) Toast.makeText(context, getString(R.string.no_times), Toast.LENGTH_SHORT).show();
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
							editText.setText(savePath);
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
									if(!path.equals(savePath)) {
										savePath = path;
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
									Utils.hideKeyboard(editText);
								}
							}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Utils.hideKeyboard(editText);
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
							editText.setText(savePath+"database.db");
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
									Utils.hideKeyboard(editText);
									final String path = editText.getText().toString();
									File file = new File(path);
									if(file.isDirectory()) Toast.makeText(context, getString(R.string.path_illegal), Toast.LENGTH_SHORT).show();
									else if(file.exists()) {
										importDB(path);
									} else Toast.makeText(context, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
								}
							}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Utils.hideKeyboard(editText);
								}
							}).show();
						}
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.bt_ses_mean:	//分组平均
				for(int i=0; i<Session.length; i++)
					if(Session.penalty[i] != 2) {
						showAlertDialog(3, 0);
						break;
					}
				break;
			case R.id.stcheck1:	//WCA观察
				wca = !wca;
				setSwitchOn(ibSwitch[0], wca);
				edit.putBoolean("wca", wca);
				edit.commit();
				break;
			case R.id.stcheck3:	//模拟ss计时
				simulateSS = !simulateSS;
				setSwitchOn(ibSwitch[2], simulateSS);
				edit.putBoolean("simss", simulateSS);
				edit.commit();
				break;
			case R.id.stcheck6:	//显示打乱状态
				showscr = !showscr;
				setSwitchOn(ibSwitch[5], showscr);
				edit.putBoolean("showscr", showscr);
				if (showscr) {
					scrambleView.setVisibility(View.VISIBLE);
					showScramble();
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
				fullScreen = !fullScreen;
				setFullScreen(fullScreen);
				setSwitchOn(ibSwitch[4], fullScreen);
				edit.putBoolean("fulls", fullScreen);
				edit.commit();
				break;
			case R.id.stcheck12:	//屏幕常亮
				if(opnl) {
					if(Timer.state != 1) releaseWakeLock();
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
			case R.id.solve1:	//三阶求解底面
				new CustomDialog.Builder(context).setSingleChoiceItems(R.array.faceStr, solSel[0], new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(solSel[0] != which) {
							solSel[0] = which;
							btSolver3[0].setText(sol31[solSel[0]]);
							edit.putInt("cface", solSel[0]);
							edit.commit();
							if(stSel[5] != 0 &&
									(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
									scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)))
								new Thread() {
								public void run() {
									handler.sendEmptyMessage(4);
									scrambler.extSol3(1, scrambler.crntScr);
									extsol = scrambler.sc;
									handler.sendEmptyMessage(3);
									scrState = NEXTSCRING;
									scrambler.extSol3(1, nextScr);
									scrState = SCRDONE;
								}
							}.start();
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.solve2:	//三阶求解底色
				new CustomDialog.Builder(context).setSingleChoiceItems(R.array.sideStr, solSel[1], new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(solSel[1] != which) {
							solSel[1] = which;
							btSolver3[1].setText(sol32[solSel[1]]);
							edit.putInt("cside", solSel[1]);
							edit.commit();
							if(stSel[5] != 0 &&
									(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
									scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)))
								new Thread() {
								public void run() {
									handler.sendEmptyMessage(4);
									scrambler.extSol3(stSel[5], scrambler.crntScr);
									extsol = scrambler.sc;
									handler.sendEmptyMessage(3);
									scrState = NEXTSCRING;
									scrambler.extSol3(stSel[5], nextScr);
									scrState = SCRDONE;
								}
							}.start();
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.reset:	//恢复默认设置 TODO
				new CustomDialog.Builder(context).setTitle(R.string.confirm_reset)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(fullScreen) setFullScreen(false);
						wca=false; simulateSS=false; monoscr = false;
						showscr=true; conft=true; hidls=false; selScr=false; fullScreen=false;
						useBgcolor=true; opnl=false; isMulp=false;
						solSel[0]=0; solSel[1]=1;
						stSel[0]=0; stSel[1]=0; stSel[2]=1; stSel[3]=0; stSel[4]=0;
						stSel[5]=0; stSel[6]=0; stSel[7]=1; stSel[8]=3; stSel[9]=0;
						stSel[10]=0; stSel[11]=2; stSel[12]=0;
						scrambleSize = 18; timerSize = 60;
						l1len = 5; l2len = 12;
						rowSpacing = 25; freezeTime = 0;
						colors[0] = 0xff66ccff;	colors[1] = 0xff000000;	colors[2] = 0xffff00ff;
						colors[3] = 0xffff0000;	colors[4] = 0xff009900;
						tabHost.setBackgroundColor(colors[0]);
						setViews();
						setTextsColor();
						updateGridView(1);
						releaseWakeLock();
						removeConf();
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
				break;
			case R.id.lay16:	//最慢单次颜色
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
			case R.id.lay17:	//最快平均颜色
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
			case R.id.lay22:	//背景图片
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				//intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 1);
				break;
			case R.id.lay19:
				//n阶配色设置
				int[] cs = {share.getInt("csn1", Color.YELLOW), share.getInt("csn2", Color.BLUE), share.getInt("csn3", Color.RED),
						share.getInt("csn4", Color.WHITE), share.getInt("csn5", 0xff009900), share.getInt("csn6", 0xffff8026)};
				colorSchemeView = new ColorSchemeView(context, dip300, cs, 1);
				new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_cube)).setView(colorSchemeView)
				.setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.lay20:	//金字塔配色
				cs = new int[] {share.getInt("csp1", Color.RED), share.getInt("csp2", 0xff009900),
						share.getInt("csp3", Color.BLUE), share.getInt("csp4", Color.YELLOW)};
				colorSchemeView = new ColorSchemeView(context, dip300, cs, 2);
				new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_pyrm)).setView(colorSchemeView)
				.setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.lay21:	//SQ配色
				cs = new int[] {share.getInt("csq1", Color.YELLOW), share.getInt("csq2", Color.BLUE), share.getInt("csq3", Color.RED),
						share.getInt("csq4", Color.WHITE), share.getInt("csq5", 0xff009900), share.getInt("csq6", 0xffff8026)};
				colorSchemeView = new ColorSchemeView(context, dip300, cs, 3);
				new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_sq)).setView(colorSchemeView)
				.setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.lay13:	//背景颜色
				colorPickerView = new ColorPickerView(context, dip300, colors[0]);
				new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int color = colorPickerView.getColor();
						tabHost.setBackgroundColor(color);
						colors[0] = color;
						useBgcolor = true;
						edit.putInt("cl0", color);
						edit.putBoolean("bgcolor", true);
						edit.commit();
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
				break;
			case R.id.lay24:	//Skewb配色
				cs = new int[] {share.getInt("csw1", Color.YELLOW), share.getInt("csw2", Color.BLUE), share.getInt("csw3", Color.RED),
						share.getInt("csw4", Color.WHITE), share.getInt("csw5", 0xff009900), share.getInt("csw6", 0xffff8026)};
				colorSchemeView = new ColorSchemeView(context, dip300, cs, 4);
				new CustomDialog.Builder(context).setTitle(getString(R.string.scheme_skewb)).setView(colorSchemeView)
				.setNegativeButton(R.string.btn_close, null).show();
				break;
			case R.id.lay14:	//文字颜色
				colorPickerView = new ColorPickerView(context, dip300, colors[1]);
				new CustomDialog.Builder(context).setTitle(R.string.select_color).setView(colorPickerView).setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int color = colorPickerView.getColor();
						colors[1] = color;
						edit.putInt("cl1", color);
						edit.commit();
						setTextsColor();
						setListView();
						setResultTitle();
						updateGridView(0);
					}
				}).setNegativeButton(R.string.btn_cancel, null).show();
				break;
			case R.id.lay15:	//最快单次颜色
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
			case R.id.lay27:
			case R.id.lay28:	//滚动平均长度
				LayoutInflater factory = LayoutInflater.from(context);
				int layoutId = R.layout.number_input;
				view = factory.inflate(layoutId, null);
				editText = (EditText) view.findViewById(R.id.edit_text);
				editText.setText(String.valueOf(sel==R.id.lay27 ? l1len : l2len));
				editText.setSelection(editText.getText().length());
				new CustomDialog.Builder(context).setTitle(R.string.enter_len).setView(view)
				.setPositiveButton(R.string.btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int len = Integer.parseInt(editText.getText().toString());
						if(len < 3 || len > 1000)
							Toast.makeText(context, getString(R.string.illegal), Toast.LENGTH_LONG).show();
						else {
							int idx;
							if(sel == R.id.lay27) {
								idx = 0;
								l1len = len;
								edit.putInt("l1len", len);
							} else {
								idx = 1;
								l2len = len;
								edit.putInt("l2len", len);
							}
							edit.commit();
							stdn[idx].setText("" + len);
							if(Session.length>0 && !isMulp) {
								setResultTitle();
								updateGridView(1);
							}
						}
						Utils.hideKeyboard(editText);
					}
				}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Utils.hideKeyboard(editText);
					}
				}).show();
				Utils.showKeyboard(editText);
				break;
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
				return Timer.state != 0;
			case R.id.tv_timer:
				scrTouch = false;
				if(stSel[0] == 0) setTouch(event);
				else if(stSel[0] == 1) inputTime(event.getAction());
				return true;
			}
			return false;
		}
	};
	
	private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if(Timer.state == 0) {
				isLongPress = true;
				LayoutInflater factory = LayoutInflater.from(context);
				int layoutId = R.layout.scr_layout;
				view = factory.inflate(layoutId, null);
				editText = (EditText) view.findViewById(R.id.etslen);
				TextView tvScr = (TextView) view.findViewById(R.id.cnt_scr);
				tvScr.setText(scrambler.crntScr);
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
						Utils.hideKeyboard(editText);
					}
				}).setNegativeButton(R.string.copy_scr, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(VERSION.SDK_INT >= 11) {
							android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							clip.setPrimaryClip(ClipData.newPlainText("text", scrambler.crntScr));
						}
						else {
							android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							clip.setText(scrambler.crntScr);
						}
						Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
						Utils.hideKeyboard(editText);
					}
				}).show();
			}
			return true;
		}
	};
	
	private OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			ListView listView = (ListView) arg0;
			switch (listView.getId()) {
			case R.id.list1:
				if(selScr1 != arg2 - 1) {
					selScr1 = arg2 - 1;
					scr1Adapter.setSelectItem(selScr1 + 1);
					scr1Adapter.notifyDataSetChanged();
					scr2Adapter.setData(getResources().getStringArray(Utils.get2ndScr(arg2-1)));
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
			default:
				selFilePath = paths.get(arg2);
				tvPathName.setText(selFilePath);
				getFileDirs(selFilePath, false);
			}
		}
	};
	
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			switch (seekBar.getId()) {
			case R.id.seekb1:	//计时器字体
				timerSize = seekBar.getProgress() + 50;
				tvSettings[0].setText(String.valueOf(timerSize));
				edit.putInt("ttsize", timerSize);
				tvTimer.setTextSize(timerSize);
				break;
			case R.id.seekb2:	//打乱字体
				scrambleSize = seekBar.getProgress() + 12;
				tvSettings[1].setText(String.valueOf(scrambleSize));
				edit.putInt("stsize", scrambleSize);
				tvScramble.setTextSize(scrambleSize);
				break;
			case R.id.seekb3:	//成绩列表行距
				rowSpacing = seekBar.getProgress() + 20;
				tvSettings[2].setText(String.valueOf(rowSpacing));
				if(Session.length != 0)
					updateGridView(2);
				edit.putInt("intv", rowSpacing);
				break;
			case R.id.seekb4:	//背景图不透明度
				opacity = seekBar.getProgress();
				if(!useBgcolor)
					tabHost.setBackgroundDrawable(Utils.getBackgroundDrawable(context, bitmap, opacity));
				edit.putInt("opac", opacity);
				break;
			case R.id.seekb5:	//启动延时
				freezeTime = seekBar.getProgress();
				tvSettings[3].setText(String.format("%.02fs", freezeTime/20f));
				edit.putInt("tapt", freezeTime);
				break;
			case R.id.seekb6:	//SS参数值
				switchThreshold = seekBar.getProgress();
				tvSettings[2].setText(String.valueOf(switchThreshold));
				edit.putInt("ssvalue", switchThreshold);
				break;
			case R.id.seekb7: 	//打乱状态大小
				sviewSize = seekBar.getProgress() * 10 + 160;
				setScrambleViewSize();
				edit.putInt("svsize", sviewSize);
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
				tvSettings[0].setText(String.valueOf(prg+50));
				break;
			case R.id.seekb2:
				tvSettings[1].setText(String.valueOf(prg+12));
				break;
			case R.id.seekb3:
				tvSettings[2].setText(String.valueOf(prg+20));
				break;
			case R.id.seekb5:
				tvSettings[3].setText(String.format("%.02fs", prg / 20f));
				break;
			case R.id.seekb6:
				tvSettings[4].setText(String.valueOf(prg));
				break;
			}
		}
	};
	
	private View.OnClickListener comboListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
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
			new CustomDialog.Builder(context).setSingleChoiceItems(staid[sel], stSel[sel], new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, final int which) {
					if(stSel[sel] != which) {
						switch (sel) {
						case 0:	//计时方式
							if(stSel[0] == 2) stm.stop();
							if (which == 0) setTimerText(stSel[2]==0 ? "0.00" : "0.000");
							else if(which == 1) setTimerText("IMPORT");
							else {
								setTimerText("OFF");
								stm.start();
							}
							edit.putInt("tiway", which);
							break;
						case 1:	//计时器更新方式
							edit.putInt("timerupd", which);
							break;
						case 2:	//计时精确度
							edit.putBoolean("prec", which != 0);
							if(stSel[0] == 0) setTimerText(which==0 ? "0.00" : "0.000");
							if(Session.length != 0) {
								btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
								updateGridView(1);
							}
							break;
						case 3:	//分段计时
							stSel[3] = which;
							if(which == 0) {
								isMulp = false;
								Session.mulp = null;
							} else {
								if(!isMulp) {
									isMulp = true;
									Session.mulp = new int[6][Session.result.length];
									if(Session.length > 0)
										session.getMultData();
								}
							}
							edit.putInt("multp", which);
							setListView();
							setResultTitle();
							break;
						case 4:	//滚动平均2类型
							stSel[4] = which;
							edit.putInt("l2tp", which);
							if(!isMulp) setResultTitle();
							if(Session.length>0 && !isMulp) {
								setResultTitle();
								updateGridView(1);
							}
							break;
						case 5:	//三阶求解
							edit.putInt("cxe", which);
							if(scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
									scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19)) {
								if(which == 0) tvScramble.setText(scrambler.crntScr);
								else new Thread() {
									public void run() {
										handler.sendEmptyMessage(4);
										scrambler.extSol3(which, scrambler.crntScr);
										extsol = scrambler.sc;
										handler.sendEmptyMessage(3);
										scrState = NEXTSCRING;
										scrambler.extSol3(which, nextScr);
										scrState = SCRDONE;
									}
								}.start();
							}
							break;
						case 6:	//二阶底面
							edit.putInt("cube2l", which);
							if(scrIdx == 0) {
								if(which == 0) tvScramble.setText(scrambler.crntScr);
								else if(scr2idx < 3) new Thread() {
									public void run() {
										handler.sendEmptyMessage(4);
										extsol = "\n"+solver.Cube2bl.cube2layer(scrambler.crntScr, which);
										handler.sendEmptyMessage(3);
										scrState = NEXTSCRING;
										scrambler.sc = "\n"+solver.Cube2bl.cube2layer(nextScr, which);
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
							setRequestedOrientation(screenOri[which]);
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
								if(which > 0) {
									new Thread() {
										public void run() {
											handler.sendEmptyMessage(4);
											extsol = " " + (which==1 ? Sq1Shape.solveTrn(scrambler.crntScr) : Sq1Shape.solveTws(scrambler.crntScr));
											handler.sendEmptyMessage(1);
											scrState = NEXTSCRING;
											scrambler.sc = " " + (which==1 ? Sq1Shape.solveTrn(nextScr) : Sq1Shape.solveTws(nextScr));
											scrState = SCRDONE;
										}
									}.start();
								}
								else tvScramble.setText(scrambler.crntScr);
							}
							break;
						case 13:	//时间格式
							stSel[13] = which;
							edit.putInt("timeform", which);
							if(Session.length > 0) {
								updateGridView(1);
							}
							break;
						case 14:	//滚动平均1类型
							stSel[14] = which;
							edit.putInt("l1tp", which);
							if(!isMulp) setResultTitle();
							if(Session.length>0 && !isMulp) {
								setResultTitle();
								updateGridView(1);
							}
							break;
						}
						stSel[sel] = which;
						edit.commit();
						std[sel].setText(itemStr[sel][which]);
					}
					dialog.dismiss();
				}
			}).setNegativeButton(R.string.btn_close, null).show();
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
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 127;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegh:
				if(isChecked) {
					egoll |= 64;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 191;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegu:
				if(isChecked) {
					egoll |= 32;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 223;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegt:
				if(isChecked) {
					egoll |= 16;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 239;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegl:
				if(isChecked) {
					egoll |= 8;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 247;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegs:
				if(isChecked) {
					egoll |= 4;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 251;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkega:
				if(isChecked) {
					egoll |= 2;
					if(checkBox[10].isChecked()) {checkBox[10].setChecked(false); egoll &= 254;}
				}
				else egoll &= 253;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			case R.id.checkegn:
				if(isChecked) {
					egoll |= 1;
					for(int i=3; i<10; i++)
						if(checkBox[i].isChecked()) checkBox[i].setChecked(false);
				}
				else egoll &= 254;
				edit.putInt("egoll", egoll);
				Utils.setEgOll();
				break;
			}
			edit.commit();
		}
	};

	//设置各种View、TextView颜色等 TODO
	private void setViews() {
		//打乱显示
		tvScramble.setTextSize(scrambleSize);
		setScrambleFont(monoscr ? 0 : 1);
		//打乱状态
		setScrambleViewSize();
		//计时器
		tvTimer.setTextSize(timerSize);
		setTimerFont(stSel[8]);
		setTimerColor(colors[1]);
		if(stSel[0] == 0) {
			if(stSel[2] == 0) setTimerText("0.00");
			else setTimerText("0.000");
		} else if(stSel[0] == 1) setTimerText("IMPORT");
		else {
			setTimerText("OFF");
			stm.start();
		}
		//设置选项
		for(int i=0; i<std.length; i++)
			std[i].setText(itemStr[i][stSel[i]]);
		stdn[0].setText(""+l1len);
		stdn[1].setText(""+l2len);
		btSolver3[0].setText(sol31[solSel[0]]);
		btSolver3[1].setText(sol32[solSel[1]]);
		//屏幕方向
		setRequestedOrientation(screenOri[stSel[9]]);
		//拖动条
		int[] ids = {timerSize - 50, scrambleSize - 12, rowSpacing - 20, opacity, freezeTime, switchThreshold, sviewSize / 10 - 16};
		for(int i=0; i<ids.length; i++) seekBar[i].setProgress(ids[i]);
		//设置TextView
		tvSettings[0].setText(String.valueOf(timerSize));
		tvSettings[1].setText(String.valueOf(scrambleSize));
		tvSettings[2].setText(String.valueOf(rowSpacing));
		tvSettings[3].setText(String.format("%.02fs", freezeTime/20f));
		tvSettings[4].setText(String.valueOf(switchThreshold));
		//设置开关
		setSwitchOn(ibSwitch[0], wca);
		setSwitchOn(ibSwitch[1], monoscr);
		setSwitchOn(ibSwitch[2], simulateSS);
		setSwitchOn(ibSwitch[3], opnl);
		setSwitchOn(ibSwitch[4], fullScreen);
		setSwitchOn(ibSwitch[5], showscr);
		setSwitchOn(ibSwitch[6], conft);
		setSwitchOn(ibSwitch[7], !hidls);
		setSwitchOn(ibSwitch[8], selScr);
		//分组平均
		btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
	}
	
	private void setTextsColor() {
		setTimerColor(colors[1]);
		tvScramble.setTextColor(colors[1]);
		tvSesName.setTextColor(colors[1]);
		tvTitle.setTextColor(colors[1]);
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
		//if(solSel[1] == 6) solSel[1] = 1;
		sesIdx = (byte) share.getInt("group", 0);	// 分组
		scr2idx = (byte) share.getInt("sel2", 0);	// 二级打乱
		timerSize = share.getInt("ttsize", 60);	//计时器字体
		scrambleSize = share.getInt("stsize", 18);	//打乱字体
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
		useBgcolor = share.getBoolean("bgcolor", true);	//使用背景颜色
		opacity = share.getInt("opac", 35);	//背景图不透明度
		fullScreen = share.getBoolean("fulls", false);	// 全屏显示
		opnl = share.getBoolean("scron", false);	// 屏幕常亮
		selScr = share.getBoolean("selses", false);	//自动选择分组
		picPath = share.getString("picpath", "");	//背景图片路径
		freezeTime = share.getInt("tapt", 0);	//启动延时
		rowSpacing = share.getInt("intv", 30);	//成绩列表行距
		savePath = share.getString("scrpath", defPath);
		for(int i=0; i<15; i++) {
			sesType[i] = share.getInt("sestype" + i, 32);
			sesnames[i] = share.getString("sesname" + i, "");
		}
		egtype = share.getInt("egtype", 7);
		egoll = share.getInt("egoll", 254);
		simulateSS = share.getBoolean("simss", false);
		switchThreshold = share.getInt("ssvalue", 50);
		sviewSize = share.getInt("svsize", 220);
	}
	
	private void removeConf() {
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
		edit.remove("ismulp");	edit.remove("svsize");
		edit.remove("vibtime");	edit.remove("bgcolor");	edit.remove("ssvalue");
		edit.remove("sensity");	edit.remove("monoscr");	edit.remove("showscr");
		edit.remove("timerupd");	edit.remove("timeform");
		edit.remove("screenori");
		edit.commit();
	}
	
	private void setVisibility(boolean v) {	//TODO
		int vi = v ? View.VISIBLE : View.GONE;
		rGroup.setVisibility(vi);
		tabLine.setVisibility(vi);
		btScramble.setVisibility(vi);
		tvScramble.setVisibility(vi);
		if(!v)
			scrambleView.setVisibility(vi);
		if(!fullScreen) {
			setFullScreen(!v);
		}
	}
	
	private void set2ndsel() {
		if(scrIdx < -1 || scrIdx > 20) scrIdx = 1;
		scr2Ary = getResources().getStringArray(Utils.get2ndScr(scrIdx));
		if(scr2idx >= scr2Ary.length || scr2idx < 0) scr2idx = 0;
		btScramble.setText(scrAry[scrIdx+1] + " - " + scr2Ary[scr2idx]);
		newScramble(scrIdx << 5 | scr2idx);
	}
	
	private void newScramble(final int scrType) {
		final boolean ch = crntScrType != scrType;
		crntScrType = scrType;
		if(!ch && inScr!=null && inScrLen<inScr.size()) {
			if(!isInScr) isInScr = true;
			scrambler.crntScr = inScr.get(inScrLen++);
			scrambler.viewType = Utils.getViewType(scrambler.crntScr);
			switch (Configs.insType) {
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
						scrambler.extSol3(stSel[5], scrambler.crntScr);
						extsol = scrambler.sc;
						handler.sendEmptyMessage(3);
						showScrambleView(true);
					}
				}.start();
			} else {
				tvScramble.setText(scrambler.crntScr);
				showScrambleView(false);
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
							scrambler.crntScr = nextScr;
							extsol = scrambler.sc;
						} else {
							scrState = SCRING;
							if(scrIdx==-1 && (scr2idx==1 || scr2idx == 15)) {
								cs.threephase.Util.init(handler);
							}
							handler.sendEmptyMessage(2);
							scrambler.crntScr = scrambler.getScramble((scrIdx<<5)|scr2idx, ch);
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
			scrambler.crntScr = scrambler.getScramble(scrIdx<<5|scr2idx, ch);
			tvScramble.setText(scrambler.crntScr);
			showScrambleView(false);
			scrState = SCRDONE;
		}
	}
	
	private void showScramble() {
		if((scrIdx==0 && stSel[6]!=0) ||
				(stSel[5]!=0 && (scrIdx==-1 && (scr2idx==0 || scr2idx==5 || scr2idx==6 || scr2idx==7) ||
						scrIdx==1 && (scr2idx==0 || scr2idx==1 || scr2idx==5 || scr2idx==19))))
			handler.sendEmptyMessage(3);
		else if(scrIdx==8 && scr2idx<3 && stSel[12]>0)
			handler.sendEmptyMessage(1);
		else handler.sendEmptyMessage(0);
		showScrambleView(true);
	}
	
	private void showScrambleView(boolean isThread) {
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
	
	private void getNextScramble(boolean ch) {
		System.out.println("get next scramble...");
		scrState = NEXTSCRING;
		nextScr = scrambler.getScramble((scrIdx<<5)|scr2idx, ch);
		System.out.println("next scr: " + nextScr);
		//System.out.println("next solve: " + Scramble.sc);
		scrState = SCRDONE;
		if(nextScrWaiting) {
			scrambler.crntScr = nextScr;
			extsol = scrambler.sc;
			showScramble();
			nextScrWaiting = false;
			getNextScramble(ch);
		}
	}
	
	private void changeScramble(int s1, int s2) {
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
	
	private void setScrambleFont(int font) {
		if (font == 0)
			tvScramble.setTypeface(Typeface.create("monospace", 0));
		else tvScramble.setTypeface(Typeface.create("sans-serif", 0));
	}
	
	private void setScrambleViewSize() {
		LayoutParams params = new LayoutParams((int)(sviewSize*scale), (int)(sviewSize*3*scale)/4);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		scrambleView.setLayoutParams(params);
	}
	
	private void setTimerFont(int font) {
		switch (font) {
		case 0: tvTimer.setTypeface(Typeface.create("monospace", 0)); break;
		case 1: tvTimer.setTypeface(Typeface.create("serif", 0)); break;
		case 2: tvTimer.setTypeface(Typeface.create("sans-serif", 0)); break;
		case 3: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Ds.ttf")); break;
		case 4: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "Df.ttf")); break;
		case 5: tvTimer.setTypeface(Typeface.createFromAsset(getAssets(), "lcd.ttf")); break;
		}
	}
	
	public void setTimerText(String str) {
		tvTimer.setText(str);
	}
	
	public void setTimerColor(int color) {
		tvTimer.setTextColor(color);
	}
	
	private void setTouch(MotionEvent e) {
		if(!simulateSS || scrTouch) {
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown();
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				break;
			case MotionEvent.ACTION_CANCEL:
				timer.stopFreeze();
				setTimerColor(colors[1]);
				break;
			}
		} else {
			int count = e.getPointerCount();
			//System.out.println(count+", "+e.getAction());
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_POINTER_DOWN:
			case 261:
				//System.out.println(e.getX()+", "+e.getY());
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
			case MotionEvent.ACTION_CANCEL:
				timer.stopFreeze();
				setTimerColor(colors[1]);
				break;
			}
		}
	}
	
	private void touchDown() {
		if(Timer.state == 1) {
			if(mulpCount != 0) {
				if(stSel[10]==1 || stSel[10]==3)
					vibrator.vibrate(vibTime[stSel[11]]);
				setTimerColor(0xff00ff00);
				Session.multemp[stSel[3]+1-mulpCount] = System.currentTimeMillis();
			} else {
				timer.timeEnd = System.currentTimeMillis();
				if(stSel[10] > 1)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) Session.multemp[stSel[3]+1] = timer.timeEnd;
				setVisibility(true);
			}
		} else if(Timer.state != 3) {
			if(!scrTouch || Timer.state==2) {
				if(freezeTime == 0 || (wca && Timer.state==0)) {
					setTimerColor(0xff00ff00);
					canStart = true;
				} else {
					if(Timer.state==0) setTimerColor(0xffff0000);
					else setTimerColor(0xffffff00);
					timer.startFreeze();
				}
			}
		}
	}

	private void touchUp() {
		if(Timer.state == 0) {
			if(isLongPress) isLongPress = false;
			else if(scrTouch) newScramble(crntScrType);
			else {
				if(freezeTime ==0 || canStart) {
					timer.timeStart = System.currentTimeMillis();
					if(stSel[10]==1 || stSel[10]==3)
						vibrator.vibrate(vibTime[stSel[11]]);
					timer.count();
					if(isMulp) {
						mulpCount = stSel[3];
						Session.multemp[0] = timer.timeStart;
					}
					else mulpCount = 0;
					acquireWakeLock();
					setVisibility(false);
				} else {
					timer.stopFreeze();
					setTimerColor(colors[1]);
				}
			}
		} else if(Timer.state == 1) {
			if(isLongPress) isLongPress = false;
			if(mulpCount!=0) {
				mulpCount--;
				setTimerColor(colors[1]);
			}
		} else if(Timer.state == 2) {
			if(isLongPress) isLongPress = false;
			if(freezeTime ==0 || canStart) {
				timer.timeStart = System.currentTimeMillis();
				isp2 = timer.insp==2 ? 2000 : 0;
				idnf = timer.insp != 3;
				if(stSel[10]==1 || stSel[10]==3)
					vibrator.vibrate(vibTime[stSel[11]]);
				timer.count();
				if(isMulp) Session.multemp[0] = timer.timeStart;
				acquireWakeLock();
				setVisibility(false);
			} else {
				timer.stopFreeze();
				setTimerColor(0xffff0000);
			}
		} else {
			if(isLongPress) isLongPress = false;
			if(!wca) {isp2=0; idnf=true;}
			confirmTime((int)timer.time);
			Timer.state = 0;
			if(!opnl) releaseWakeLock();
		}
	}
	
	private void inputTime(int action) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setTimerColor(0xff00ff00);
			break;
		case MotionEvent.ACTION_UP:
			setTimerColor(colors[1]);
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
					Utils.hideKeyboard(editText);
				}
			}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Utils.hideKeyboard(editText);
				}
			}).show();
			Utils.showKeyboard(editText);
			break;
		}
	}
	
	public void confirmTime(final int time) {
		if(idnf) {
			if(conft) {
				new CustomDialog.Builder(context).setTitle(getString(R.string.show_time)+Statistics.timeToString(time + isp2))
						.setItems(R.array.rstcon, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:save(time + isp2, 0);break;
						case 1:save(time + isp2, 1);break;
						case 2:save(time + isp2, 2);break;
						}
					}
				}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
					public void onClick(DialogInterface d,int which) {
						newScramble(crntScrType);
					}
				}).show();
			} else save(time + isp2, 0);
		} else if(conft)
			new CustomDialog.Builder(context).setTitle(getString(R.string.show_time)+"DNF("+Statistics.timeToString(time)+")").setMessage(R.string.confirm_adddnf)
			.setPositiveButton(R.string.btn_ok, new OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int j) {
					save(time, 2);
				}
			}).setNegativeButton(R.string.btn_cancel, new OnClickListener() {
				public void onClick(DialogInterface d,int which) {
					newScramble(crntScrType);
				}
			}).show();
		else save(time, 2);
	}
	
	private void save(int time, int p) {
		session.insert(time, p, scrambler.crntScr, isMulp);
		btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
		updateGridView(3);
		if(sesType[sesIdx] != crntScrType) {
			sesType[sesIdx] = crntScrType;
			edit.putInt("sestype"+sesIdx, crntScrType);
			edit.commit();
		}
		newScramble(crntScrType);
	}
	
	private void getSession(int i) {
		session.getSession(i, isMulp);
	}
	
	private void setResultTitle() {
		layoutTitle.removeAllViews();
		TextView tvNum = new TextView(context);
		tvNum.setLayoutParams(new LinearLayout.LayoutParams((int) Math.round(scale * 50), -1));
		layoutTitle.addView(tvNum);
		String[] title;
		if(isMulp) {
			title = new String[stSel[3] + 2];
			title[0] = getString(R.string.time);
			for(int i=1; i<stSel[3]+2; i++) title[i] = "P-" + i;
		} else {
			title = new String[] {getString(R.string.time),
					(stSel[14]==0 ? "avg of " : "mean of ") + l1len,
					(stSel[4]==0 ? "avg of " : "mean of ") + l2len};
		}
		for(int i=0; i<title.length; i++) {
			//View v = new View(context);
			//v.setLayoutParams(new LinearLayout.LayoutParams(1, -1));
			//v.setBackgroundColor(0xddb2b2b2);
			//layoutTitle.addView(v);
			TextView tv = new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1));
			tv.setTextColor(0xff000000);
			tv.setGravity(Gravity.CENTER);
			tv.setText(title[i]);
			tv.setTextSize(16);
			layoutTitle.addView(tv);
		}
	}
	
	private void setListView() {	//TODO
		if(!isMulp) {
			timeAdapter = new ListAdapter(this, (int) Math.round(rowSpacing * scale));
		} else {
			timeAdapter = new ListAdapter(this, (int) Math.round(rowSpacing * scale), stSel[3]+3);
		}
		lvTimes.setAdapter(timeAdapter);
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
	
	private void outScr(final String path, final String fileName, final int num) {
		File fPath = new File(path);
		if(fPath.exists() || fPath.mkdirs()) {
			progressDialog.setTitle(getString(R.string.menu_outscr));
			progressDialog.setMax(num);
			progressDialog.show();
			new Thread() {
				public void run() {
					try {
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+fileName), "utf-8"));
						for(int i=0; i<num; i++) {
							handler.sendEmptyMessage(100 + i);
							String s = (i+1) + ". " + scrambler.getScramble((scrIdx<<5)|scr2idx, false) + "\r\n";
							writer.write(s);
						}
						writer.close();
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
				byte [] bytes = stat.getBytes();
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
		if(fs!=null && fs.length>0) Arrays.sort(fs, new Comparator<File>() {
			@Override
			public int compare(File arg0, File arg1) {
				String fn1 = arg0.getName();
				String fn2 = arg1.getName();
				return fn1.compareToIgnoreCase(fn2);
			}
		});
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
	
	public void showAlertDialog(int i, int j) {
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
			slist = sessionMean();
			break;
		}
		new CustomDialog.Builder(context).setTitle(t).setMessage(slist)
		.setPositiveButton(R.string.btn_copy, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				if(VERSION.SDK_INT >= 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
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
				et1.setText(savePath);
				et2 = (EditText) view.findViewById(R.id.edit_scrfile);
				et2.requestFocus();
				et2.setText(String.format(getString(R.string.def_sname), formatter.format(new Date())));
				et2.setSelection(et2.getText().length());
				btn.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						selFilePath = et1.getText().toString();
						int lid = R.layout.file_selector;
						final View view2 = factory.inflate(lid, null);
						listView = (ListView) view2.findViewById(R.id.list);
						File f = new File(selFilePath);
						selFilePath = f.exists() ? selFilePath : Environment.getExternalStorageDirectory().getPath()+File.separator;
						tvPathName = (TextView) view2.findViewById(R.id.text);
						tvPathName.setText(selFilePath);
						getFileDirs(selFilePath, false);
						listView.setOnItemClickListener(itemListener);
						new CustomDialog.Builder(context).setTitle(R.string.sel_path).setView(view2)
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
						if(!path.equals(savePath)) {
							savePath = path;
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
						Utils.hideKeyboard(et1);
					}
				}).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Utils.hideKeyboard(et1);
					}
				}).show();
			}
		}).setNegativeButton(R.string.btn_close, null).show();
	}
	
	private String getShareContent() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(getString(R.string.share_c1), Session.length, btScramble.getText(),
				Statistics.timeAt(Statistics.minIdx, false), Statistics.timeToString(Statistics.mean)));
		if(Session.length>l1len) sb.append(String.format(getString(R.string.share_c2), l1len,
				Statistics.timeToString(Statistics.bestAvg[0])));
		if(Session.length>l2len) sb.append(String.format(getString(R.string.share_c2), l2len,
				Statistics.timeToString(Statistics.bestAvg[1])));
		sb.append(getString(R.string.share_c3));
		return sb.toString();
	}
	
	private void updateGridView(final int type) {
		new Thread() {
			public void run() {
				try {
					sleep(200);
				} catch (Exception e) { }
				switch (type) {
				case 1:
				case 3:
					handler.sendEmptyMessage(30);
					break;
				case 2:
					handler.sendEmptyMessage(31);
					break;
				}
				handler.sendEmptyMessage(17);
				if(type == 3) {
					handler.sendEmptyMessage(32);
				}
			}
		}.start();
	}
	
	private boolean update(int idx, int p) {
		if(Session.penalty[idx] != p) {
			session.update(idx, p);
			btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
			return true;
		}
		return false;
	}
	
	private void delete(int idx) {
		session.delete(idx, isMulp);
		btSesMean.setText(getString(R.string.session_average) + Statistics.sessionMean());
		updateGridView(1);
	}
	
	private void deleteAll() {
		session.clear();
		btSesMean.setText(getString(R.string.session_average) + "0/0): N/A (N/A)");
		Statistics.maxIdx = Statistics.minIdx = -1;
		updateGridView(1);
		if(sesType[sesIdx] != 32) {
			sesType[sesIdx] = 32;
			edit.remove("sestype"+sesIdx);
			edit.commit();
		}
	}
	
	private String sessionMean() {
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_solve) + Statistics.solved + "/" + Session.length + "\r\n");
		sb.append(getString(R.string.ses_mean) + Statistics.timeToString(Statistics.mean) + " ");
		sb.append("(σ = " + Statistics.standardDeviation(Statistics.sd) + ")\r\n");
		sb.append(getString(R.string.ses_avg) + Statistics.sessionAvg() + "\r\n");
		if(Session.length >= l1len && Statistics.bestIdx[0] != -1) 
			sb.append(String.format(stSel[14]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean), l1len)
					+ Statistics.timeToString(Statistics.bestAvg[0]) + "\r\n");
		if(Session.length >= l2len && Statistics.bestIdx[1] != -1) 
			sb.append(String.format(stSel[4]==0 ? getString(R.string.stat_best_avg) : getString(R.string.stat_best_mean), l2len)
					+ Statistics.timeToString(Statistics.bestAvg[1]) + "\r\n");
		sb.append(getString(R.string.stat_best) + Statistics.timeAt(Statistics.minIdx, false) + "\r\n");
		sb.append(getString(R.string.stat_worst) + Statistics.timeAt(Statistics.maxIdx, false) + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		for(int i=0; i<Session.length; i++) {
			if(!hidls) sb.append("\r\n" + (i+1) + ". ");
			sb.append(Statistics.timeAt(i, true));
			String s = session.getString(i, 6);
			if(s!=null && !s.equals("")) sb.append("[" + s + "]");
			if(hidls && i<Session.length-1) sb.append(", ");
			if(!hidls) sb.append("  " + session.getString(i, 4));
		}
		return sb.toString();
	}
	
	private String averageOf(int n, int i) {
		ArrayList<Integer> midx = new ArrayList<Integer>();
		String[] avg = Statistics.getAvgDetail(n, i, midx);
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_avg) + avg[0] + " ");
		sb.append("(σ = " + avg[1] + ")\r\n");
		sb.append(getString(R.string.stat_best) + avg[2] + "\r\n");
		sb.append(getString(R.string.stat_worst) + avg[3] + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		int num = 1;
		for(int j=i-n+1; j<=i; j++) {
			String s = session.getString(j, 6);
			if(!hidls) sb.append("\r\n" + (num++) + ". ");
			if(midx.indexOf(j) > -1) sb.append("(");
			sb.append(Statistics.timeAt(j, false));
			if(s!=null && !s.equals("")) sb.append("[" + s + "]");
			if(midx.indexOf(j) > -1) sb.append(")");
			if(hidls && j<i) sb.append(", ");
			if(!hidls) sb.append("  " + session.getString(j, 4));
		}
		return sb.toString();
	}
	
	private String meanOf(int n, int i) {
		String[] avg = Statistics.getMeanDetail(n, i);
		StringBuffer sb = new StringBuffer();
		sb.append(getString(R.string.stat_title) + new java.sql.Date(new Date().getTime()) + "\r\n");
		sb.append(getString(R.string.stat_mean) + avg[0] + " ");
		sb.append("(σ = " + avg[1] + ")\r\n");
		sb.append(getString(R.string.stat_best) + avg[2] + "\r\n");
		sb.append(getString(R.string.stat_worst) + avg[3] + "\r\n");
		sb.append(getString(R.string.stat_list));
		if(hidls) sb.append("\r\n");
		int num = 1;
		for(int j=i-n+1; j<=i; j++) {
			if(!hidls) sb.append("\r\n" + (num++) + ". ");
			sb.append(Statistics.timeAt(j, false));
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
	
	public void showTime(final int p) {
		session.move(p);
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
		TextView tvTime = (TextView) view.findViewById(R.id.st_time);
		final TextView tvScr = (TextView) view.findViewById(R.id.st_scr);
		tvTime.setText(getString(R.string.show_time) + Statistics.timeAt(p, true) + time);
		tvScr.setText(scr);
		if(Session.penalty[p] == 2) {
			RadioButton rb = (RadioButton) view.findViewById(R.id.st_pe3);
			rb.setChecked(true);
		} else if(Session.penalty[p] == 1) {
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
				case R.id.st_pe1: tag = update(p, 0); break;
				case R.id.st_pe2: tag = update(p, 1); break;
				case R.id.st_pe3: tag = update(p, 2); break;
				}
				String text = editText.getText().toString();
				if(!text.equals(comment)) {
					session.update(text);
					tag = true;
				}
				Utils.hideKeyboard(editText);
				if(tag) updateGridView(1);
			}
		}).setNeutralButton(R.string.copy_scr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(VERSION.SDK_INT > 11) {
					android.content.ClipboardManager clip = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clip.setPrimaryClip(ClipData.newPlainText("text", scr));
				}
				else {
					android.text.ClipboardManager clip = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clip.setText(scr);
				}
				Toast.makeText(context, getString(R.string.copy_to_clip), Toast.LENGTH_SHORT).show();
				Utils.hideKeyboard(editText);
			}
		}).setNegativeButton(R.string.delete_time, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int which) {
				delete(p);
				Utils.hideKeyboard(editText);
			}
		}).show();
	}
	
	private void setFullScreen(boolean fs) {
		if (fs)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	private void setSwitchOn(ImageButton bt, boolean isOn) {
		if(isOn) bt.setImageResource(R.drawable.switch_on);
		else bt.setImageResource(R.drawable.switch_off);
	}
}

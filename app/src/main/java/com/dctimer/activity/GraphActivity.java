package com.dctimer.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.model.Result;
import com.dctimer.util.Stats;
import com.dctimer.util.StringUtils;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.dctimer.APP.colors;
import static com.dctimer.APP.dm;

public class GraphActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ImageButton btnLeft, btnRight;
    private Button btnStart;
    private Button btnEnd;
    private ImageView graph;
    private TextView tvHyphen;
    private TextView tvNums;
    private TextView tvMean;
    private TextView tvBest;
    private TextView tvWorst;
    private SimpleDateFormat dateFormat;// = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
    private int dateRangeType;
    private Calendar dateStart;
    private Calendar dateEnd;
    private Result result;
    private String[] dateList;
    private String[] weekText;
    private String[] monthText;
    private Bitmap bitmap;
    private Canvas canvas;
    private int[] data;
    private int bins;
    private int select = -1;

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
        setContentView(R.layout.activity_graph);

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

        //Intent intent = getIntent();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());//new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_daily);
//        if (graphType == 1) toolbar.setTitle(R.string.action_histogram);
//        else if (graphType == 2) toolbar.setTitle(R.string.action_graph);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(colors[0]);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setItemColor(colors[1]);
        tabLayout = findViewById(R.id.tablayout);
        String[] items = getResources().getStringArray(R.array.item_date_range);
        weekText = getResources().getStringArray(R.array.item_week);
        monthText = getResources().getStringArray(R.array.item_month);
        for (int i = 0; i < items.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(items[i]));
        }
        tvHyphen = findViewById(R.id.tv_hyphen);
        tvNums = findViewById(R.id.tv_nums);
        tvMean = findViewById(R.id.tv_mean);
        tvBest = findViewById(R.id.tv_best);
        tvWorst = findViewById(R.id.tv_worst);
        btnLeft = findViewById(R.id.bt_left);
        btnLeft.setOnClickListener(mOnClickListener);
        btnRight = findViewById(R.id.bt_right);
        btnRight.setOnClickListener(mOnClickListener);
        btnStart = findViewById(R.id.bt_start);
        btnStart.setOnClickListener(mOnClickListener);
        btnEnd = findViewById(R.id.bt_end);
        btnEnd.setOnClickListener(mOnClickListener);
        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        setDateStart();
        graph = findViewById(R.id.graph);
        graph.setOnTouchListener(mOnTouchListener);
        graph.post(new Runnable() {
            @Override
            public void run() {
                int width = graph.getWidth();
                Log.w("dct", "image wid "+width);
            }
        });
        ViewGroup.LayoutParams lp = graph.getLayoutParams();
        lp.width = dm.widthPixels;
        lp.height = dm.widthPixels > dm.heightPixels ? dm.widthPixels / 3 : dm.widthPixels * 3 / 4;
        graph.setLayoutParams(lp);
        result = APP.getInstance().getResult();
        dateList = result.getDates();
        bitmap = Bitmap.createBitmap(lp.width, lp.height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        graph.setImageBitmap(bitmap);
        //Log.w("dct", "draw graph...");
        drawGraph(lp.width, lp.height);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_left).mutate();
        drawable.setColorFilter(0xff007aff, PorterDuff.Mode.SRC_ATOP);
        btnLeft.setImageDrawable(drawable);
        drawable = getResources().getDrawable(R.drawable.ic_right).mutate();
        drawable.setColorFilter(0xff007aff, PorterDuff.Mode.SRC_ATOP);
        btnRight.setImageDrawable(drawable);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                dateRangeType = tab.getPosition();
                dateEnd.setTime(new Date());
                setDateStart();
                drawGraph(graph.getWidth(), graph.getHeight());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams lp = graph.getLayoutParams();
        lp.width = dm.widthPixels;
        lp.height = dm.widthPixels > dm.heightPixels ? dm.widthPixels / 3 : dm.widthPixels * 3 / 4;
        graph.setLayoutParams(lp);
        bitmap = Bitmap.createBitmap(lp.width, lp.height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        graph.setImageBitmap(bitmap);
        refresh(lp.width, lp.height);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_start:
                    DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            dateStart.set(Calendar.YEAR, year);
                            dateStart.set(Calendar.MONTH, monthOfYear);
                            dateStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            setDateEnd();
                            drawGraph(graph.getWidth(), graph.getHeight());
                        }
                    }, dateStart.get(Calendar.YEAR), dateStart.get(Calendar.MONTH), dateStart.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                    break;
                case R.id.bt_end:
                    dialog = new DatePickerDialog(GraphActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            dateEnd.set(Calendar.YEAR, year);
                            dateEnd.set(Calendar.MONTH, monthOfYear);
                            dateEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            setDateStart();
                            drawGraph(graph.getWidth(), graph.getHeight());
                        }
                    }, dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                    break;
                case R.id.bt_left:
                    dateEnd.setTime(dateStart.getTime());
                    dateEnd.add(Calendar.DATE, -1);
                    setDateStart();
                    drawGraph(graph.getWidth(), graph.getHeight());
                    break;
                case R.id.bt_right:
                    dateStart.setTime(dateEnd.getTime());
                    dateStart.add(Calendar.DATE, 1);
                    setDateEnd();
                    drawGraph(graph.getWidth(), graph.getHeight());
                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float startX = event.getX();
                    float startY = event.getY();
                    //Log.w("dct", "down "+startX+", "+startY);
                    if (graph.getWidth() != 0) {
                        int i = (int) (startX * bins / graph.getWidth());
                        if (i == select) select = -1;
                        else select = i;
                        refresh(graph.getWidth(), graph.getHeight());
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
            }
            return true;
        }
    };

    private void setDateEnd() {
        switch (dateRangeType) {
            case 0: //日
                dateEnd.setTime(dateStart.getTime());
                btnEnd.setVisibility(View.GONE);
                tvHyphen.setVisibility(View.GONE);
                break;
            case 1: //周
                dateEnd.setTime(dateStart.getTime());
                dateEnd.add(Calendar.DATE, 6);
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 2: //月
                dateEnd.setTime(dateStart.getTime());
                dateEnd.add(Calendar.MONTH, 1);
                dateEnd.add(Calendar.DATE, -1);
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 3: //年
                dateEnd.setTime(dateStart.getTime());
                dateEnd.add(Calendar.YEAR, 1);
                if (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH)) {
                    dateEnd.set(Calendar.DAY_OF_MONTH, 1);
                    dateEnd.add(Calendar.DATE, -1);
                }
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 4: //范围
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
        }
        btnStart.setText(dateFormat.format(dateStart.getTime()));
        btnEnd.setText(dateFormat.format(dateEnd.getTime()));
    }

    private void setDateStart() {
        switch (dateRangeType) {
            case 0: //日
                dateStart.setTime(dateEnd.getTime());
                btnEnd.setVisibility(View.GONE);
                tvHyphen.setVisibility(View.GONE);
                break;
            case 1: //周
                dateStart.setTime(dateEnd.getTime());
                dateStart.add(Calendar.DATE, -6);
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 2: //月
                dateStart.setTime(dateEnd.getTime());
                dateStart.add(Calendar.MONTH, -1);
                dateStart.add(Calendar.DATE, 1);
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 3: //年
                dateStart.setTime(dateEnd.getTime());
                dateStart.add(Calendar.YEAR, -1);
                if (dateStart.get(Calendar.MONTH) == dateEnd.get(Calendar.MONTH)) {
                    dateStart.set(Calendar.DAY_OF_MONTH, 1);
                    dateStart.add(Calendar.MONTH, 1);
                }
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
            case 4: //范围
                btnEnd.setVisibility(View.VISIBLE);
                tvHyphen.setVisibility(View.VISIBLE);
                break;
        }
        btnStart.setText(dateFormat.format(dateStart.getTime()));
        btnEnd.setText(dateFormat.format(dateEnd.getTime()));
    }

    private void drawGraph(int width, int height) {
        bins = 1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar endd = Calendar.getInstance();
        endd.setTime(dateEnd.getTime());
        endd.add(Calendar.DATE, 1);
        String start = format.format(dateStart.getTime()), end = format.format(endd.getTime());
        int datedif = (int) dateDifference(dateStart, dateEnd);
        switch (dateRangeType) {
            case 0: //日
                bins = 24;
                break;
            case 1:
                bins = 7;
                break;
            case 2:
                bins = datedif + 1;
                break;
            case 3:
                bins = 12;
                break;
            case 4:
        }
        data = new int[bins];
        int count = 0, nSolve = 0, max = 0, min = Integer.MAX_VALUE;
        double sum = 0;
        for (int i=0; i<dateList.length; i++) {
            if (TextUtils.isEmpty(dateList[i])) continue;
            if (dateList[i].compareTo(start) >= 0 && dateList[i].compareTo(end) < 0) {
                count++;
                if (!result.isDnf(i)) {
                    nSolve++;
                    int time = result.getTime(i);
                    if (time > max) max = time;
                    if (time < min) min = time;
                    sum += time;
                }
                try {
                    Calendar curDate = Calendar.getInstance();
                    curDate.setTime(StringUtils.formatter.parse(dateList[i]));
                    if (dateRangeType == 0)
                        datedif = hourDifferenct(dateList[i]);
                    else if (dateRangeType == 3)
                        datedif = monthDifference(dateStart, curDate);
                    else datedif = (int) dateDifference(dateStart, curDate);
                    data[datedif]++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.w("dct", "count "+count);
        tvNums.setText(String.valueOf(nSolve));
        if (nSolve > 0) {
            int avg = (int) (sum / nSolve + 0.5);
            tvMean.setText(StringUtils.timeToString(avg));
            tvBest.setText(StringUtils.timeToString(min));
            tvWorst.setText(StringUtils.timeToString(max));
        } else {
            tvMean.setText("N/A");
            tvBest.setText("-");
            tvWorst.setText("-");
        }
        select = -1;
        refresh(width, height);
    }

    private void refresh(int width, int height) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        p.setColor(0xffffffff);
        canvas.drawRect(0, 0, width, height, p);
        p.setColor(0xffdddddd);
        p.setStrokeWidth(APP.dpi);
        int fontSize = APP.getPixel(15);
        canvas.drawLine(0, height - fontSize, width, height - fontSize, p);
        for (int i=0; i<bins; i++) {
            float x = (float) i * width / bins;
            canvas.drawLine(x, height - fontSize, x, height, p);
        }
        int rectHeight = height - 2 * fontSize;
        int maxValue = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > maxValue)
                maxValue = data[i];
        }
        if (maxValue == 0) maxValue = 1;
        if (select >= 0 && data[select] > 0) {
            int h = rectHeight * data[select] / maxValue;
            canvas.drawRect((float) select * width / bins, height - fontSize - h, (float) (select + 1) * width / bins, height - fontSize, p);
        }
        p.setColor(0xff333333);
        p.setStyle(Paint.Style.STROKE);
        for (int i=0; i<bins; i++) {
            int h = rectHeight * data[i] / maxValue;
            //float x = (float) i * width / bin;
            canvas.drawRect((float) i * width / bins, height - fontSize - h, (float) (i + 1) * width / bins, height - fontSize, p);
        }
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(fontSize);
        for (int i=0; i<bins; i++) {
            float x = (float) i * width / bins;
            canvas.drawText(getDateText(i), x + APP.dpi, height - APP.getPixel(2), p);
        }
        if (select >= 0 && data[select] > 0) {
            p.setTextAlign(Paint.Align.CENTER);
            int h = rectHeight * data[select] / maxValue;
            canvas.drawText(String.valueOf(data[select]), (select + 0.5f) * width / bins, height - fontSize - h - APP.getPixel(2), p);
        }

        graph.invalidate();
    }

    private long dateDifference(Calendar start, Calendar end) {
        long timeStart = start.getTimeInMillis() / (1000 * 3600 * 24);
        long timeEnd = end.getTimeInMillis() / (1000 * 3600 * 24);
        return timeEnd - timeStart;
    }

    private int monthDifference(Calendar start, Calendar end) {
        int monthStart = start.get(Calendar.MONTH);
        int monthEnd = end.get(Calendar.MONTH);
        int diff = monthEnd - monthStart;
        if (diff < 0) diff += 12;
        return diff;
    }

    private int hourDifferenct(String date) {
        String hour = date.substring(11, 13);
        int diff = 0;
        try {
            diff = Integer.parseInt(hour);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    private String getDateText(int i) {
        switch (dateRangeType) {
            case 0:
                if (i % 3 == 0)
                    return String.valueOf(i);
                return "";
            case 1:
                Calendar c = Calendar.getInstance();
                c.setTime(dateStart.getTime());
                c.add(Calendar.DATE, i);
                int week = c.get(Calendar.DAY_OF_WEEK);
                return weekText[week - 1];
            case 2:
                c = Calendar.getInstance();
                c.setTime(dateStart.getTime());
                c.add(Calendar.DATE, i);
                week = c.get(Calendar.DAY_OF_WEEK);
                if (week == 1) {
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    return String.valueOf(day);
                }
                return "";
            case 3:
                c = Calendar.getInstance();
                c.setTime(dateStart.getTime());
                c.add(Calendar.MONTH, i);
                return monthText[c.get(Calendar.MONTH)];
        }
        return "";
    }
}

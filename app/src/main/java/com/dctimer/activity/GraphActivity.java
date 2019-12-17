package com.dctimer.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dctimer.R;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.dctimer.APP.colors;

public class GraphActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ImageButton btnLeft, btnRight;
    private Button btnDate;
    private SimpleDateFormat dateFormat;// = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
    private SimpleDateFormat weekFormat;// = new SimpleDateFormat(getString(R.string.week_format), Locale.getDefault());
    private SimpleDateFormat monthFormat;// = new SimpleDateFormat(getString(R.string.month_format), Locale.getDefault());
    private SimpleDateFormat yearFormat;// = new SimpleDateFormat(getString(R.string.year_format), Locale.getDefault());
    private int graphType;
    private int dateRangeType;
    private Calendar dateStart;
    private Calendar dateEnd;

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

        Intent intent = getIntent();
        graphType = intent.getIntExtra("type", 0);
        dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        weekFormat = new SimpleDateFormat(getString(R.string.week_format), Locale.getDefault());
        monthFormat = new SimpleDateFormat(getString(R.string.month_format), Locale.getDefault());
        yearFormat = new SimpleDateFormat(getString(R.string.year_format), Locale.getDefault());

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        if (graphType == 1) toolbar.setTitle(R.string.action_histogram);
        else if (graphType == 2) toolbar.setTitle(R.string.action_graph);
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
        for (int i = 0; i < items.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(items[i]));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                dateRangeType = tab.getPosition();
                setDateText();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        btnLeft = findViewById(R.id.bt_left);
        btnRight = findViewById(R.id.bt_right);
        btnDate = findViewById(R.id.bt_date);
        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        setDateText();
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateRangeType == 0) {
                    DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            dateEnd.set(Calendar.YEAR, year);
                            dateEnd.set(Calendar.MONTH, monthOfYear);
                            dateEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            setDateText();
                        }
                    }, dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
//                    DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                            dateEnd.set(Calendar.YEAR, year);
//                            dateEnd.set(Calendar.MONTH, monthOfYear);
//                            dateEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                            setDateText();
//                        }
//                    }, dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH), dateEnd.get(Calendar.DAY_OF_MONTH));
//                    dialog.show(getFragmentManager(), "DatePicker");
                } else if (dateRangeType == 2) {
//                    MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(GraphActivity.this, new MonthPickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(int selectedMonth, int selectedYear) {
//                            dateEnd.set(Calendar.YEAR, selectedYear);
//                            dateEnd.set(Calendar.MONTH, selectedMonth);
//                            setDateText();
//                        }
//                    }, dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH));
//                    builder.setMinYear(2012).setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
//                        @Override
//                        public void onMonthChanged(int selectedMonth) {
//
//                        }
//                    }).build().show();
                } else if (dateRangeType == 4) {
//                    MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(GraphActivity.this, new MonthPickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(int selectedMonth, int selectedYear) {
//                            dateEnd.set(Calendar.YEAR, selectedYear);
//                            setDateText();
//                        }
//                    }, dateEnd.get(Calendar.YEAR), dateEnd.get(Calendar.MONTH));
//                    builder.showYearOnly().setMinYear(2012).build().show();
                }
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.ic_left).mutate();
        drawable.setColorFilter(0xff007aff, PorterDuff.Mode.SRC_ATOP);
        btnLeft.setImageDrawable(drawable);
        drawable = getResources().getDrawable(R.drawable.ic_right).mutate();
        drawable.setColorFilter(0xff007aff, PorterDuff.Mode.SRC_ATOP);
        btnRight.setImageDrawable(drawable);
    }

    private void setDateText() {
        switch (dateRangeType) {
            case 0: //日
                btnDate.setText(dateFormat.format(dateEnd.getTime()));
                break;
            case 1: //周
                btnDate.setText(weekFormat.format(dateEnd.getTime()));
                break;
            case 2: //月
                btnDate.setText(monthFormat.format(dateEnd.getTime()));
                break;
            case 3: //季度
                int quarter = dateEnd.get(Calendar.MONTH) / 3 + 1;
                btnDate.setText(getString(R.string.quarter_format, dateEnd.get(Calendar.YEAR), quarter));
                break;
            case 4: //年
                btnDate.setText(yearFormat.format(dateEnd.getTime()));
                break;
            case 5: //范围
        }
    }
}

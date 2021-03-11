package com.dctimer.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.model.Stackmat;
import com.dctimer.util.Utils;
import com.dctimer.widget.CustomToolbar;

import java.util.Arrays;

public class TestActivity extends AppCompatActivity {
    private Stackmat stackmat;
    private SharedPreferences sp;
    private Spinner spRate;
    private Spinner spFormat;
    private ImageView image;
    private TextView tvTime;
    private TextView tvL;
    private TextView tvR;
    private TextView tvRed;
    private TextView tvGreen;
    private static int[] samplingRates = {8000, 11025, 16000, 22050, 24000, 32000, 44100};
    private Bitmap bitmap;
    private Canvas canvas;

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
        setContentView(R.layout.activity_test);
        LinearLayout layout = findViewById(R.id.layout);
        layout.setBackgroundColor(APP.colors[0]);
        int grey = Utils.greyScale(APP.colors[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //6.0
            if (grey > 200) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0
            if (grey > 200) {
                getWindow().setStatusBarColor(0x44000000);
            } else {
                getWindow().setStatusBarColor(0);
            }
        }

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(APP.colors[0]);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setItemColor(APP.colors[1]);
        sp = getSharedPreferences("dctimer", Activity.MODE_PRIVATE);

        spRate = findViewById(R.id.sp_rate);
        int idx = Arrays.binarySearch(samplingRates, APP.samplingRate);
        if (idx >= 0) spRate.setSelection(idx);
        spRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int rate = samplingRates[i];
                if (rate != APP.samplingRate) {
                    APP.samplingRate = rate;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("srate", rate);
                    editor.commit();
                    if (stackmat != null) {
                        stackmat.stop();
                        stackmat.setSamplingRate(rate);
                    } else stackmat = new Stackmat(TestActivity.this, rate, APP.dataFormat);
                    stackmat.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        spFormat = findViewById(R.id.sp_format);
        if (APP.dataFormat == AudioFormat.ENCODING_PCM_8BIT)
            spFormat.setSelection(0);
        else spFormat.setSelection(1);
        spFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("dct", "data sel "+i);
                int format = i == 0 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;
                if (APP.dataFormat != format) {
                    APP.dataFormat = format;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("dform", format);
                    editor.commit();
                    if (stackmat != null) {
                        stackmat.stop();
                        stackmat.setDataFormat(format);
                    }
                    stackmat = new Stackmat(TestActivity.this, APP.samplingRate, format);
                    stackmat.start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        image = findViewById(R.id.img_wave);
        tvTime = findViewById(R.id.tv_time);
        tvL = findViewById(R.id.tv_l);
        tvR = findViewById(R.id.tv_r);
        tvRed = findViewById(R.id.tv_red);
        tvGreen = findViewById(R.id.tv_green);
        bitmap = Bitmap.createBitmap(APP.dm.widthPixels, APP.getPixel(120), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        stackmat = new Stackmat(this, APP.samplingRate, APP.dataFormat);
        stackmat.start();
    }

    @Override
    public void onBackPressed() {
        if (stackmat != null)
            stackmat.stop();
        super.onBackPressed();
    }

    public void drawWave(Integer[] data) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(2);
        p.setColor(-1);
        int wid = APP.dm.widthPixels;
        int hei = APP.getPixel(120);
        canvas.drawRect(0, 0, wid, hei, p);
        p.setColor(0xff333333);
        int last = 0;
        int len = data.length;
        for (int i=0; i<len; i++) {
            int d = data[i];
            if (i != 0) {
                canvas.drawLine((float) i * wid / len, last * hei / 256f, (float) (i + 1) * wid / len, d * hei / 256f, p);
            }
            last = d;
        }
        image.setImageBitmap(bitmap);
    }

    public void displayTime(int status, String time) {
        if (status == 'L') {
            tvL.setVisibility(View.VISIBLE);
            tvR.setVisibility(View.GONE);
            tvRed.setVisibility(View.INVISIBLE);
            tvGreen.setVisibility(View.INVISIBLE);
        } else if (status == 'R') {
            tvL.setVisibility(View.GONE);
            tvR.setVisibility(View.VISIBLE);
            tvRed.setVisibility(View.INVISIBLE);
            tvGreen.setVisibility(View.INVISIBLE);
        } else if (status == 'C') {
            tvL.setVisibility(View.GONE);
            tvR.setVisibility(View.GONE);
            tvRed.setVisibility(View.VISIBLE);
            tvGreen.setVisibility(View.INVISIBLE);
        } else if (status == 'A') {
            tvL.setVisibility(View.GONE);
            tvR.setVisibility(View.GONE);
            tvRed.setVisibility(View.VISIBLE);
            tvGreen.setVisibility(View.VISIBLE);
        } else {
            tvL.setVisibility(View.GONE);
            tvR.setVisibility(View.GONE);
            tvRed.setVisibility(View.INVISIBLE);
            tvGreen.setVisibility(View.INVISIBLE);
        }
        tvTime.setText(time);
    }
}

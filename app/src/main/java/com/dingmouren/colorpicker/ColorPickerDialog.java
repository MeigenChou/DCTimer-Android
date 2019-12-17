package com.dingmouren.colorpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.R;

/**
 * Created by dingmouren on 2017/5/4.
 */

public class ColorPickerDialog {
    private static final String TAG = ColorPickerDialog.class.getName();

    private  AlertDialog mAlertDialog;
    private final boolean mIsSupportAlpha;
    private final OnColorPickerListener mListener;
    private final ViewGroup mViewContainer;
    private final ColorPlateView mViewPlate;
    private final View mViewHue;
    private final ImageView mViewAlphaBottom;
    private final View mViewAlphaOverlay;
    private final ImageView mPalteCursor;
    private final ImageView mHueCursor;
    private final ImageView mAlphaCursor;
    private TextView tvColor;
    private final float[] mCurrentHSV = new float[3];
    private int mAlpha;
    private int defColor = 0xff000000;
    private Button btnRed;
    private Button btnPurple;
    private Button btnBlue;
    private Button btnGreen;
    private Button btnYellow;
    private Button btnOrange;

    /**
     * 创建不支持透明度的取色器
     * @param context
     * @param color 默认颜色
     * @param listener
     */
    public ColorPickerDialog(final Context context, int color, int defColor, OnColorPickerListener listener) {
        this(context, color, defColor, false, listener);
    }

    /**
     * 创建支持透明度的取色器
     * @param context 宿主Activity
     * @param color 当前颜色
     * @param defColor 默认颜色
     * @param isSupportAlpha 颜色是否支持透明度
     * @param listener 取色器的监听器
     */
    public ColorPickerDialog(final Context context, int color, int defColor, boolean isSupportAlpha, OnColorPickerListener listener) {
        this.mIsSupportAlpha = isSupportAlpha;
        this.mListener = listener;
        this.defColor = defColor;

        if (!isSupportAlpha) {
            color = color | 0xff000000;
        }

        Color.colorToHSV(color, mCurrentHSV);
        mAlpha = Color.alpha(color);

        final View view = LayoutInflater.from(context).inflate(R.layout.color_picker_dialog,null);
        mViewHue = view.findViewById(R.id.img_hue);
        mViewPlate = view.findViewById(R.id.color_plate);
        mHueCursor = view.findViewById(R.id.hue_cursor);
        mPalteCursor = view.findViewById(R.id.plate_cursor);
        mViewContainer = view.findViewById(R.id.container);
        mViewAlphaOverlay = view.findViewById(R.id.view_overlay);
        mAlphaCursor = view.findViewById(R.id.alpha_Cursor);
        mViewAlphaBottom = view.findViewById(R.id.img_alpha_bottom);
        tvColor = view.findViewById(R.id.tv_color);
        tvColor.setText(getColorString(color));
        btnRed = view.findViewById(R.id.btn_red);
        btnRed.setOnClickListener(mOnClickListener);
        btnPurple = view.findViewById(R.id.btn_purple);
        btnPurple.setOnClickListener(mOnClickListener);
        btnBlue = view.findViewById(R.id.btn_blue);
        btnBlue.setOnClickListener(mOnClickListener);
        btnGreen = view.findViewById(R.id.btn_green);
        btnGreen.setOnClickListener(mOnClickListener);
        btnYellow = view.findViewById(R.id.btn_yellow);
        btnYellow.setOnClickListener(mOnClickListener);
        btnOrange = view.findViewById(R.id.btn_orange);
        btnOrange.setOnClickListener(mOnClickListener);

        {
            mViewAlphaBottom.setVisibility(mIsSupportAlpha ? View.VISIBLE : View.GONE);
            mViewAlphaOverlay.setVisibility(mIsSupportAlpha ? View.VISIBLE : View.GONE);
            mAlphaCursor.setVisibility(mIsSupportAlpha ? View.VISIBLE : View.GONE);
        }

        mViewPlate.setHue(getHue());

        initOnTouchListener();
        initAlerDialog(context, view);
        initGlobalLayoutListener(view);
    }

    /**
     * 触摸监听
     */
    private void initOnTouchListener() {
        //色彩板的触摸监听
        mViewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                    //float x = event.getX();
                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > mViewHue.getMeasuredHeight()) y = mViewHue.getMeasuredHeight();
                    float colorHue = 360f - y * 360 / mViewHue.getMeasuredHeight();
                    //if (colorHue == 360) colorHue = 0.f;
                    //if (action == MotionEvent.ACTION_UP) Log.w("dct", "hue "+colorHue);
                    setHue(colorHue);
                    mViewPlate.setHue(colorHue);

                    moveHueCursor();
                    int color = getColor();
                    if (mListener != null) {
                        mListener.onColorChange(ColorPickerDialog.this, color);
                    }
                    tvColor.setText(getColorString(color));
                    //updateAlphaView();

                    return true;
                }
                return false;
            }
        });


        //支持透明度时的触摸监听
        if (mIsSupportAlpha) mViewAlphaBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                    float y = event.getY();
                    if (y < 0) y = 0;
                    if (y > mViewHue.getMeasuredHeight()) y = mViewHue.getMeasuredHeight() - 0.001f;
                    final  int alpha = Math.round(255.f - (255.f / mViewAlphaBottom.getMeasuredHeight() * y));
                    ColorPickerDialog.this.setAlpha(alpha);
                    moveAlphaCursor();
                    int color = ColorPickerDialog.this.getColor();
                    int alphaColor = alpha << 24 | color & 0x00ffffff;
                    if (mListener != null) {
                        mListener.onColorChange(ColorPickerDialog.this, getColor());
                    }
                    return true;
                }
                return false;
            }
        });

        //颜色样板的触摸监听
        mViewPlate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getY();
                    if (x < 0) x = 0;
                    if (x > mViewPlate.getMeasuredWidth()) x = mViewPlate.getMeasuredWidth();
                    if (y < 0) y = 0;
                    if (y > mViewPlate.getMeasuredHeight()) y = mViewPlate.getMeasuredHeight();

                    setColorSat(x / mViewPlate.getMeasuredWidth());//颜色深浅
                    setColorVal(1f - (y / mViewPlate.getMeasuredHeight()));//颜色明暗
                    movePlateCursor();
                    int color = getColor();
                    if (mListener != null) {
                        mListener.onColorChange(ColorPickerDialog.this, color);
                    }
                    tvColor.setText(getColorString(color));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 初始化AlerDialog
     */
    private void initAlerDialog(Context context, View view) {
        mAlertDialog = new AlertDialog.Builder(context).create();
        mAlertDialog.setTitle(context.getResources().getString(R.string.select_color));
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onColorConfirm(ColorPickerDialog.this, getColor());
                }
            }
        });
        mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onColorCancel(ColorPickerDialog.this);
                }
            }
        });
        mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getResources().getString(R.string.scheme_reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (mListener != null) {
                    mListener.onColorReset(ColorPickerDialog.this, defColor);
                }
            }
        });
        //mAlertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(0xffff0000);
        mAlertDialog.setView(view,0,0,0,0);
    }

    /**
     * 全局布局状态监听
     */
    private void initGlobalLayoutListener(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)//api 16
            @Override
            public void onGlobalLayout() {
                moveHueCursor();
                movePlateCursor();
                if (ColorPickerDialog.this.mIsSupportAlpha) {
                    moveAlphaCursor();
                    updateAlphaView();
                }
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    /**
     * 移动色彩样板指针
     */
    private void moveHueCursor() {//ConstraintLayout$LayoutParams
        float y = mViewHue.getMeasuredHeight() - (getHue() * mViewHue.getMeasuredHeight() / 360.f);
        //if (y == mViewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mHueCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (mViewHue.getLeft() - APP.dpi);
        layoutParams.topMargin = (int) (mViewHue.getTop() + y - APP.dpi * 3.5 + APP.dpi);
        mHueCursor.setLayoutParams(layoutParams);
    }

    /**
     * 移动透明度板的指针
     */
    private void moveAlphaCursor() {
        final float y = mViewAlphaBottom.getMeasuredHeight() - (this.getAlpha() * mViewAlphaBottom.getMeasuredHeight() / 255.f);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAlphaCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (mViewAlphaBottom.getLeft() - Math.floor(mAlphaCursor.getMeasuredWidth() / 3) -  mViewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (mViewAlphaBottom.getTop() + y - Math.floor(mAlphaCursor.getMeasuredHeight() / 2) - mViewContainer.getPaddingTop());
        mAlphaCursor.setLayoutParams(layoutParams);
    }

    /**
     * 移动最终颜色样板指针
     */
    private void movePlateCursor() {
        final float x = getColorSat() * mViewPlate.getMeasuredWidth();
        final float y = (1.f - getColorVal()) * mViewPlate.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPalteCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (mViewPlate.getLeft() + x - Math.floor(mPalteCursor.getMeasuredWidth() / 2));
        layoutParams.topMargin = (int) (mViewPlate.getTop() + y - Math.floor(mPalteCursor.getMeasuredHeight() / 2));
        mPalteCursor.setLayoutParams(layoutParams);

    }

    /**
     * 设置色彩
     * @param color
     */
    private void setHue(float color) {
        mCurrentHSV[0] = color;
    }

    private float getHue() {
        return mCurrentHSV[0];
    }

    /**
     * 设置颜色深浅
     */
    private void setColorSat(float color) {
        this.mCurrentHSV[1] = color;
    }

    private float getColorSat() {
        return this.mCurrentHSV[1];
    }

    /**
     * 设置颜色明暗
     */
    private void setColorVal(float color) {
        this.mCurrentHSV[2] = color;
    }

    private float getColorVal() {
        return mCurrentHSV[2];
    }

    /**
     * 获取int颜色
     */
    private int getColor() {
        final int argb = Color.HSVToColor(mCurrentHSV);
        return mAlpha << 24 | (argb & 0x00ffffff);
    }

    /**
     * 更新透明度UI
     */
    private void updateAlphaView() {
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {Color.HSVToColor(mCurrentHSV),0x0});
        mViewAlphaOverlay.setBackgroundDrawable(gd);
    }

    public void setButtonTextColor(int color) {
        if (mAlertDialog != null) {
            Button btnPositive = mAlertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            Button btnNegative = mAlertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
            btnPositive.setTextColor(color);
            btnNegative.setTextColor(color);
        }
    }

    private void setAlpha(int alpha) {
        this.mAlpha = alpha;
    }

    private int getAlpha() {
        return mAlpha;
    }

    public ColorPickerDialog show() {
        mAlertDialog.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffff0000);
        return this;
    }

    public AlertDialog getDialog() {
        return mAlertDialog;
    }

    private String getColorString(int color) {
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;
        return String.format("#%02X%02X%02X", r, g, b);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int color = 0;
            switch (view.getId()) {
                case R.id.btn_red:
                    color = 0xffff0000;
                    break;
                case R.id.btn_purple:
                    color = 0xffff00ff;
                    break;
                case R.id.btn_blue:
                    color = 0xff0000ff;
                    break;
                case R.id.btn_green:
                    color = 0xff009900;
                    break;
                case R.id.btn_yellow:
                    color = 0xffffff00;
                    break;
                case R.id.btn_orange:
                    color = 0xffff9900;
                    break;
            }
            tvColor.setText(getColorString(color));
            Color.colorToHSV(color, mCurrentHSV);
            mViewPlate.setHue(getHue());
            moveHueCursor();
            movePlateCursor();
        }
    };
}

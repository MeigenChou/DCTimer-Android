package com.dctimer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.dctimer.R;
import com.dctimer.view.KeypadView;

public class KeypadDialog {
    private AlertDialog mDialog;
    private Window window;
    private Context mContext;
    private int mThemeResId;
    private View mDialogLayout;

    public KeypadDialog(Context context) {
        this.mContext = context;
        this.mThemeResId= R.style.DialogTheme;
        this.mDialogLayout =  LayoutInflater.from(mContext).inflate(R.layout.dialog_keypad,null);
        mDialog = new AlertDialog.Builder(mContext, mThemeResId).create();
        mDialog.setCancelable(true);
        mDialog.show();

        mDialog.getWindow().setDimAmount(0.4f);//设置透明度0.4
        window = mDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setContentView(mDialogLayout);//设置弹框布局
        mDialog.setCanceledOnTouchOutside(true);
        window.setWindowAnimations(R.style.dialogOpenAnimation);  //添加动画
        window.setGravity(Gravity.BOTTOM);//底部
    }

    public KeypadDialog(Context context, int themeResId) {
        this.mContext = context;
        this.mThemeResId = themeResId;
        this.mDialogLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_keypad,null);
    }

    public KeypadView getKeypad() {
        return mDialogLayout.findViewById(R.id.keypad);
    }

    public KeypadDialog setAlertDialog() {
        mDialog = new AlertDialog.Builder(mContext, mThemeResId).create();
        mDialog.setCancelable(true);//按返回键退出
        mDialog.show();
        return this;
    }

    public KeypadDialog setWindowSize(int width, int height,float amount) {
        mDialog.getWindow().setDimAmount(amount);//设置透明度
        window = mDialog.getWindow();
        window.setLayout(width, height);
        window.setContentView(mDialogLayout);//设置弹框布局
        return this;
    }

    public KeypadDialog setWindowSize(int width, int height,int custom,float amount) {
        if (custom == 2) {
            mDialog.getWindow().setDimAmount(amount);//设置透明度
            window = mDialog.getWindow();
            window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setContentView(mDialogLayout);//设置弹框布局
            return this;
        }
        else {
            mDialog.getWindow().setDimAmount(amount);//设置透明度
            window = mDialog.getWindow();
            window.setLayout(width, height);
            window.setContentView(mDialogLayout);//设置弹框布局
            return this;
        }
    }

    public KeypadDialog setOutColse(boolean isOut) {
        if (isOut) {
            mDialog.setCanceledOnTouchOutside(true);
        }
        else {
            mDialog.setCanceledOnTouchOutside(false);
        }
        return this;
    }

    public KeypadDialog setGravity(int animation, int gravity) {
        window.setWindowAnimations(animation);  //添加动画
        window.setGravity(gravity);             //底部
        return this;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;//清空对象
            window = null;
        }
    }
}

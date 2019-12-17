package com.dctimer.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.dctimer.R;

public class KeypadView extends RelativeLayout {
    private Activity context;
    private EditText editText;
    private ImageView imgClose;
    private ImageButton btnBackspace;
    private Button btnClear;
    private Button btnDone;
    private Button[] btnKey = new Button[12];
    private RadioGroup radioGroup;
    private OnClickListener mOnClickListener;
    private View mLayout;

    public KeypadView(Context context) {
        super(context);
        this.context = (Activity) context;
        initView();
    }

    public KeypadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        initView();
    }

    public KeypadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
        initView();
    }

    private void initView() {
        mLayout = LayoutInflater.from(context).inflate(R.layout.layout_keypad, null);
        imgClose  = mLayout.findViewById(R.id.iv_close);//关闭
        editText = mLayout.findViewById(R.id.edit_text);
        radioGroup = mLayout.findViewById(R.id.rg_penalty);
        btnDone = mLayout.findViewById(R.id.bt_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rgid = radioGroup.getCheckedRadioButtonId();
                int penalty = 0;
                switch (rgid) {
                    case R.id.rb_no_penalty: penalty = 0; break;
                    case R.id.rb_plus2: penalty = 1; break;
                    case R.id.rb_dnf: penalty = 2; break;
                }
                mOnClickListener.onFinish(editText.getText().toString(), penalty);
            }
        });
        btnClear = mLayout.findViewById(R.id.bt_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        btnBackspace = mLayout.findViewById(R.id.bt_bs);
        btnBackspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int len = editText.getText().length();
                if (len > 0)
                    editText.getText().delete(len - 1, len);
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClose();
            }
        });
        int[] keyIds = {R.id.bt_0, R.id.bt_1, R.id.bt_2, R.id.bt_3, R.id.bt_4, R.id.bt_5, R.id.bt_6, R.id.bt_7, R.id.bt_8, R.id.bt_9, R.id.bt_colon, R.id.bt_dot};
        for (int i = 0; i < keyIds.length; i++) {
            btnKey[i] = mLayout.findViewById(keyIds[i]);
            btnKey[i].setTag(i);
            btnKey[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int button = (int) view.getTag();
                    if (button < 10)
                        editText.getText().append((char) ('0' + button));
                    else if (button == 10)
                        editText.getText().append(':');
                    else if (button == 11)
                        editText.getText().append('.');
                }
            });
        }
        this.addView(mLayout);
    }

    public interface OnClickListener {
        void onFinish(String time, int penalty);
        void onClose();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }
}

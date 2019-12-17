package com.dctimer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.StringUtils;

public class InputTimeDialog extends DialogFragment {
    private EditText editText;
    //private CustomKeyboardView keyboardView;
    private int format;
    private int penalty;
    private boolean mFormatting = false;
    RadioGroup rgFormat;
    RadioGroup rgPenalty;

    public static InputTimeDialog newInstance(int format) {
        InputTimeDialog dialog = new InputTimeDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("format", format);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        format = getArguments().getInt("format", 0);
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_input_time, null);
        editText = view.findViewById(R.id.edit_text);
        editText.addTextChangedListener(mTextWatcher);
        //keyboardView = view.findViewById(R.id.keyboard);
//        Keyboard keyboard = new Keyboard(editText.getContext(), R.xml.custom_keyboard);
//        keyboardView.setKeyboard(keyboard);
//        keyboardView.setPreviewEnabled(false);
//        keyboardView.setOnKeyListener(new CustomKeyboardView.OnKeyListener() {
//            @Override
//            public void onInput(String text) {
//                editText.getText().append(text);
//            }
//
//            @Override
//            public void onDelete() {
//                String text = editText.getText().toString();
//                if (text.length() > 0) editText.getText().delete(text.length() - 1, text.length());
//            }
//        });
        rgFormat = view.findViewById(R.id.rg_format);
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = rgFormat.getCheckedRadioButtonId();
                if (id == R.id.rb_format4)
                    format = 0;
                else if (id == R.id.rb_format5)
                    format = 1;
            }
        });
        rgPenalty = view.findViewById(R.id.rg_penalty);
        buidler.setTitle(R.string.enter_time).setView(view).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rgid = rgPenalty.getCheckedRadioButtonId();
                switch (rgid) {
                    case R.id.rb_no_penalty: penalty = 0; break;
                    case R.id.rb_plus2: penalty = 1; break;
                    case R.id.rb_dnf: penalty = 2; break;
                }
                int time = StringUtils.parseTime(editText.getText().toString());
                if (time <= 0) {
                    Toast.makeText(getActivity(), getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                } else if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).addTime(time, penalty);
                }
            }
        }).setNegativeButton(R.string.btn_cancel, null);
//        try {
//            editText.requestFocus();
//            editText.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(editText, 0);
//                }
//            }, 300);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return buidler.create();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            if (format == 0) return;
            if (mFormatting) return;
            mFormatting = true;
            String origin = editable.toString().replaceAll("^0+|:|\\.", "");
            Log.w("dct", "replce "+origin);
            editable.clear();
            int len = origin.length();
            if (len == 0 || origin.equals("0")) {
                mFormatting = false;
                return;
            }
            editable.append(origin);
            if (len == 1)
                editable.insert(0, "0.0");
            else if (len == 2)
                editable.insert(0, "0.");
            else if (len < 5)
                editable.insert(len - 2, ".");
            else if (len < 7) {
                editable.insert(len - 4, ":");
                editable.insert(len - 1, ".");
            } else {
                editable.insert(len - 6, ":");
                editable.insert(len - 3, ":");
                editable.insert(len, ".");
            }
            mFormatting = false;
        }
    };
}

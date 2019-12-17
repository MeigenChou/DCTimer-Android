package com.dctimer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.Utils;

import static com.dctimer.APP.importType;

public class ImportScrambleDialog extends DialogFragment {
    private EditText editText;

    public static ImportScrambleDialog newInstance() {
        return new ImportScrambleDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_import_scramble, null);
        Spinner spinner = view.findViewById(R.id.sp_type);
        editText = view.findViewById(R.id.et_scramble);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                importType = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        buidler.setView(view).setTitle(R.string.action_import_scramble)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        Utils.hideKeyboard(editText);
                        String s = editText.getText().toString();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).importScramble(s);
                        }
                    }
                }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Utils.hideKeyboard(editText);
            }
        });
        try {
            editText.requestFocus();
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                }
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buidler.create();
    }
}

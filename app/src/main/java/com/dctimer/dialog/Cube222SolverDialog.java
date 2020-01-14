package com.dctimer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;

import static com.dctimer.APP.itemStr;
import static com.dctimer.APP.solve222;
import static com.dctimer.APP.solverType;
import static com.dctimer.util.Utils.getCheck;
import static com.dctimer.util.Utils.setCheck;

public class Cube222SolverDialog extends DialogFragment {
    private int position;

    public static Cube222SolverDialog newInstance(int position) {
        Cube222SolverDialog dialog = new Cube222SolverDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("position", -1);
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_222_solver, null);
        RadioGroup rg = view.findViewById(R.id.rgroup);
        RadioButton[] rbs = new RadioButton[3];
        int[] rids = {R.id.btn_none, R.id.btn_face, R.id.btn_layer};
        for (int i=0; i<rids.length; i++) rbs[i] = view.findViewById(rids[i]);
        rbs[solve222].setChecked(true);
        final CheckBox[] chks = new CheckBox[6];
        int[] chkids = {R.id.chk_d, R.id.chk_u, R.id.chk_l, R.id.chk_r, R.id.chk_f, R.id.chk_b};
        for (int i=0; i<chks.length; i++) {
            chks[i] = view.findViewById(chkids[i]);
            chks[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solverType[4] = getCheck(chks);
                    if (getActivity() instanceof MainActivity) {
                        MainActivity dct = (MainActivity) getActivity();
                        dct.setPref("cface", solverType[4]);
                        dct.show222Hint(solve222);
                    }
                }
            });
        }
        setCheck(solverType[4], chks);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.btn_none:
                        solve222 = 0;
                        break;
                    case R.id.btn_face:
                        solve222 = 1;
                        break;
                    case R.id.btn_layer:
                        solve222 = 2;
                        break;
                }
                if (getActivity() instanceof MainActivity) {
                    MainActivity dct = (MainActivity) getActivity();
                    dct.updateSettingList(position, itemStr[6][solve222]);
                    dct.setPref("c2fl", solve222);
                    dct.show222Hint(solve222);
                }
            }
        });
        buidler.setView(view).setNegativeButton(R.string.btn_close, null);
        return buidler.create();
    }
}

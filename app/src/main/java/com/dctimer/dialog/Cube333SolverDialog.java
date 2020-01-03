package com.dctimer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.Utils;

import static com.dctimer.APP.itemStr;
import static com.dctimer.APP.solve333;
import static com.dctimer.APP.solverType;

public class Cube333SolverDialog extends DialogFragment {
    private int position;

    public static Cube333SolverDialog newInstance(int position) {
        Cube333SolverDialog dialog = new Cube333SolverDialog();
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_333_solver, null);
        RadioGroup rg = view.findViewById(R.id.rgroup);
        RadioButton[] rbs = new RadioButton[7];
        int[] rids = {R.id.btn_none, R.id.btn_cross, R.id.btn_xcross, R.id.btn_eoline, R.id.btn_roux, R.id.btn_petrus, R.id.btn_eofc};
        for (int i=0; i<rids.length; i++) rbs[i] = view.findViewById(rids[i]);
        rbs[solve333].setChecked(true);
        final LinearLayout llCross = view.findViewById(R.id.layout_cross);
        final LinearLayout llRoux = view.findViewById(R.id.layout_roux);
        final LinearLayout llPetrus = view.findViewById(R.id.layout_petrus);
        final CheckBox[] chks = new CheckBox[6];
        int[] chkids = {R.id.chk_d, R.id.chk_u, R.id.chk_l, R.id.chk_r, R.id.chk_f, R.id.chk_b};
        for (int i=0; i<chks.length; i++) {
            chks[i] = view.findViewById(chkids[i]);
            chks[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solverType[1] = getCheck(chks);
                    //Log.w("dct", solverType[1]+"");
                    if (getActivity() instanceof MainActivity) {
                        MainActivity dct = (MainActivity) getActivity();
                        dct.setPref("sside", solverType[1]);
                        dct.show333Hint(solve333);
                    }
                }
            });
        }
        final CheckBox[] chkp = new CheckBox[8];
        chkids = new int[] {R.id.chk_ulf, R.id.chk_ulb, R.id.chk_urf, R.id.chk_urb, R.id.chk_dlf, R.id.chk_dlb, R.id.chk_drf, R.id.chk_drb};
        for (int i=0; i<chkp.length; i++) {
            chkp[i] = view.findViewById(chkids[i]);
            chkp[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solverType[2] = getCheck(chkp);
                    if (getActivity() instanceof MainActivity) {
                        MainActivity dct = (MainActivity) getActivity();
                        dct.setPref("pside", solverType[2]);
                        dct.show333Hint(solve333);
                    }
                }
            });
        }
        final CheckBox[] chkr = new CheckBox[8];
        chkids = new int[] {R.id.chk_lu, R.id.chk_ld, R.id.chk_fu, R.id.chk_fd, R.id.chk_ru, R.id.chk_rd, R.id.chk_bu, R.id.chk_bd};
        for (int i=0; i<chkr.length; i++) {
            chkr[i] = view.findViewById(chkids[i]);
            chkr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    solverType[3] = getCheck(chkr);
                    if (getActivity() instanceof MainActivity) {
                        MainActivity dct = (MainActivity) getActivity();
                        dct.setPref("rside", solverType[3]);
                        dct.show333Hint(solve333);
                    }
                }
            });
        }
        if (solve333 == 0 || solve333 == 4 || solve333 == 5) llCross.setVisibility(View.GONE);
        else setCheck(solverType[1], chks);
        if (solve333 != 5) llPetrus.setVisibility(View.GONE);
        else setCheck(solverType[2], chkp);
        if (solve333 == 4)
            setCheck(solverType[3], chkr);
        else llRoux.setVisibility(View.GONE);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.btn_none:
                        solve333 = 0;
                        break;
                    case R.id.btn_cross:
                        solve333 = 1;
                        break;
                    case R.id.btn_xcross:
                        solve333 = 2;
                        break;
                    case R.id.btn_eoline:
                        solve333 = 3;
                        break;
                    case R.id.btn_roux:
                        solve333 = 4;
                        break;
                    case R.id.btn_petrus:
                        solve333 = 5;
                        break;
                    case R.id.btn_eofc:
                        solve333 = 6;
                        break;
                }
                if (solve333 == 0 || solve333 == 4 || solve333 == 5)
                    llCross.setVisibility(View.GONE);
                else {
                    llCross.setVisibility(View.VISIBLE);
                    setCheck(solverType[1], chks);
                }
                if (solve333 == 5) {
                    llPetrus.setVisibility(View.VISIBLE);
                    setCheck(solverType[2], chkp);
                } else llPetrus.setVisibility(View.GONE);
                if (solve333 == 4) {
                    llRoux.setVisibility(View.VISIBLE);
                    setCheck(solverType[3], chkr);
                } else llRoux.setVisibility(View.GONE);
                if (getActivity() instanceof MainActivity) {
                    MainActivity dct = (MainActivity) getActivity();
                    dct.updateSettingList(position, itemStr[5][solve333]);
                    dct.setPref("cxe", solve333);
                    dct.show333Hint(solve333);
                }
            }
        });
        buidler.setView(view).setNegativeButton(R.string.btn_close, null);
        return buidler.create();
    }

    private void setCheck(int ch, CheckBox[] chks) {
        for (int i = 0; i < chks.length; i++) {
            chks[i].setChecked(((ch >> i) & 1) != 0);
        }
    }

    private int getCheck(CheckBox[] chks) {
        int ch = 0;
        for (int i = 0; i < chks.length; i++) {
            if (chks[i].isChecked())
                ch |= (1 << i);
        }
        return ch;
    }
}

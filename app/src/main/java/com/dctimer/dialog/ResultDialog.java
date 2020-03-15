package com.dctimer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.activity.WebActivity;
import com.dctimer.util.Utils;

public class ResultDialog extends DialogFragment {
    private EditText etComment;
    private int num;
    private String time;
    private String scramble;
    private String date;
    private int penalty;
    private String comment;
    private String solution;

    public static ResultDialog newInstance(int num, String time, String scramble, String date, int penalty, String comment, String solution) {
        ResultDialog dialog = new ResultDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        bundle.putString("time", time);
        bundle.putString("scramble", scramble);
        bundle.putString("date", date);
        bundle.putInt("penalty", penalty);
        bundle.putString("comment", comment);
        bundle.putString("solution", solution);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        num = getArguments().getInt("num", 0);
        time = getArguments().getString("time");
        scramble = getArguments().getString("scramble");
        date = getArguments().getString("date");
        penalty = getArguments().getInt("penalty", 0);
        comment = getArguments().getString("comment");
        solution = getArguments().getString("solution");
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
        TextView tvNum = view.findViewById(R.id.tv_num);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvScramble = view.findViewById(R.id.tv_scramble);
        etComment = view.findViewById(R.id.et_comment);
        Button btnSolution = view.findViewById(R.id.bt_solution);
        TextView tvSolution = view.findViewById(R.id.tv_solution);
        tvNum.setText("#" + (num + 1));
        tvTime.setText(time);
        tvScramble.setText(scramble);
        tvDate.setText(date);
        if (penalty == 2) {
            RadioButton rb = view.findViewById(R.id.rb_dnf);
            rb.setChecked(true);
        } else if (penalty == 1) {
            RadioButton rb = view.findViewById(R.id.rb_plus2);
            rb.setChecked(true);
        } else {
            RadioButton rb = view.findViewById(R.id.rb_no_penalty);
            rb.setChecked(true);
        }
        if (!TextUtils.isEmpty(comment)) {
            etComment.setText(comment);
            etComment.setSelection(comment.length());
        }
        if (!TextUtils.isEmpty(solution))
            tvSolution.setText(solution);
        else {
            btnSolution.setVisibility(View.GONE);
            tvSolution.setVisibility(View.GONE);
        }
        btnSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                String web = "https://alg.cubing.net/?alg=" + solution.trim().replace('\'', '-').replace(' ', '_')
                        + "&setup=" + scramble.trim().replace('\'', '-').replace(' ', '_');
                intent.putExtra("web", web);
                startActivity(intent);
            }
        });
        buidler.setView(view).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean mod = false;
                RadioGroup rg = view.findViewById(R.id.rg_penalty);
                int id = rg.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.rb_no_penalty:
                        mod = penalty != 0;
                        penalty = 0;
                        break;
                    case R.id.rb_plus2:
                        mod = penalty != 1;
                        penalty = 1;
                        break;
                    case R.id.rb_dnf:
                        mod = penalty != 2;
                        penalty = 2;
                        break;
                }
                String text = etComment.getText().toString();
                if (!text.equals(comment)) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateResult(num, text);
                    }
                    //result.update(num, text);
                }
                Utils.hideKeyboard(etComment);
                if (mod) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateResult(num, penalty);
                    }
                }
            }
        }).setNegativeButton(R.string.delete_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.hideKeyboard(etComment);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).delete(num);
                }
            }
        }).setNeutralButton(R.string.btn_copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).copyScramble(scramble);
                }
            }
        });
        return buidler.create();
    }
}

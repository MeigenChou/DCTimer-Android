package com.dctimer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.activity.WebActivity;
import com.dctimer.util.Utils;

import scrambler.Scrambler;

public class ResultDialog extends DialogFragment {
    private EditText etComment;
    private TextView tvSolution;
    private ImageView imgArrow;
    private int num;
    private String time;
    private String scramble;
    private String date;
    private int penalty;
    private String comment;
    private String solution;
    private int puzzle;
    private boolean expandSol;

    public static ResultDialog newInstance(int num, String time, String scramble, String date, int penalty, String comment, String solution, int puzzle) {
        ResultDialog dialog = new ResultDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        bundle.putString("time", time);
        bundle.putString("scramble", scramble);
        bundle.putString("date", date);
        bundle.putInt("penalty", penalty);
        bundle.putString("comment", comment);
        bundle.putString("solution", solution);
        bundle.putInt("puzzle", puzzle);
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
        puzzle = getArguments().getInt("puzzle", 0);
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
        TextView tvNum = view.findViewById(R.id.tv_num);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvScramble = view.findViewById(R.id.tv_scramble);
        etComment = view.findViewById(R.id.et_comment);
        Button btnCopy = view.findViewById(R.id.btn_copy);
        //Button btnSolution = view.findViewById(R.id.bt_solution);
        LinearLayout llSolution = view.findViewById(R.id.ll_sol);
        tvSolution = view.findViewById(R.id.tv_solution);
        imgArrow = view.findViewById(R.id.iv_arrow);
        tvNum.setText("#" + (num + 1));
        tvTime.setText(time);
        tvScramble.setText(scramble);
        ImageView ivScramble = view.findViewById(R.id.img_scramble);
        Scrambler scrambler = new Scrambler(getActivity().getSharedPreferences("dctimer", Activity.MODE_PRIVATE));
        scrambler.parseScramble(puzzle, scramble);
        if (scrambler.getImageType() == 0) ivScramble.setVisibility(View.GONE);
        else {
            int dip240 = APP.getPixel(240);
            Bitmap bitmap = Bitmap.createBitmap(dip240, dip240 * 3 / 4, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.drawColor(0);
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setStrokeWidth(APP.dpi);
            scrambler.drawScramble(dip240, p, c);
            ivScramble.setImageBitmap(bitmap);
        }
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
            //btnSolution.setVisibility(View.GONE);
            llSolution.setVisibility(View.GONE);
        }
        tvSolution.setVisibility(View.GONE);
        llSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandSol = !expandSol;
                if (expandSol) {
                    tvSolution.setVisibility(View.VISIBLE);
                    imgArrow.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    tvSolution.setVisibility(View.GONE);
                    imgArrow.setImageResource(R.drawable.ic_arrow_down);
                }
            }
        });
        /*btnSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebActivity.class);
                String web = "https://alg.cubing.net/?alg=" + solution.trim().replace('\'', '-').replace(' ', '_')
                        + "&setup=" + scramble.trim().replace('\'', '-').replace(' ', '_');
                intent.putExtra("web", web);
                startActivity(intent);
            }
        });*/
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).copyScramble(scramble);
                }
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
        }).setNeutralButton(R.string.delete_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                Utils.hideKeyboard(etComment);
                if (getActivity() instanceof MainActivity) {
                    //Log.w("dct", "num "+num+", "+getActivity());
                    ((MainActivity) getActivity()).delete(num, true);
                }
            }
        }).setNegativeButton(R.string.btn_close, null);
        return buidler.create();
    }
}

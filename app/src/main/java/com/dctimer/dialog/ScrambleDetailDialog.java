package com.dctimer.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;

import solver.Cross;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ScrambleDetailDialog extends DialogFragment {
    private EditText editText;
    private String scramble;
    private LinearLayout llHintc;
    private LinearLayout llHintx;
    private ImageView ivArrowc;
    private ImageView ivArrowx;
    private TextView tvHintc;
    private TextView tvHintx;
    private int hintType;
    private int scrambleLen;
    private boolean expandc;
    private boolean expandx;

    public static ScrambleDetailDialog newInstance(String scramble, int scrambleLen, int hintType) {
        ScrambleDetailDialog dialog = new ScrambleDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putString("scramble", scramble);
        bundle.putInt("len", scrambleLen);
        bundle.putInt("type", hintType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        scramble = getArguments().getString("scramble", "");
        scrambleLen = getArguments().getInt("len", 0);
        hintType = getArguments().getInt("type", 0);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_scramble, null);
        editText = view.findViewById(R.id.edit_len);
        editText.setText(String.valueOf(Math.abs(scrambleLen)));
        if (scrambleLen <= 0) editText.setEnabled(false);
        TextView tv = view.findViewById(R.id.text_scramble);
        tv.setText(scramble);
        llHintc = view.findViewById(R.id.ll_hintc);
        llHintx = view.findViewById(R.id.ll_hintx);
        ivArrowc = view.findViewById(R.id.iv_arrowc);
        ivArrowx = view.findViewById(R.id.iv_arrowx);
        tvHintc = view.findViewById(R.id.text_hintc);
        tvHintx = view.findViewById(R.id.text_hintx);
        llHintc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expandc) {
                    //tvHint.setVisibility(View.VISIBLE);
                    tvHintc.setText(Cross.solveCrossf(scramble));
                    ivArrowc.setImageResource(R.drawable.ic_arrow_up);
                    expandc = true;
                } else {
                    tvHintc.setText(Cross.solveCross(scramble));
                    //tvHint.setVisibility(View.GONE);
                    ivArrowc.setImageResource(R.drawable.ic_arrow_down);
                    expandc = false;
                }
            }
        });
        llHintx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expandx) {
                    tvHintx.setText(Cross.solveXcrossf(scramble));
                    ivArrowx.setImageResource(R.drawable.ic_arrow_up);
                    expandx = true;
                } else {
                    tvHintx.setText(Cross.solveXcross(scramble));
                    ivArrowx.setImageResource(R.drawable.ic_arrow_down);
                    expandx = false;
                }
            }
        });
        if (hintType == 3) {
            //tvHint.setVisibility(View.GONE);
            tvHintc.setText(Cross.solveCross(scramble));
            tvHintx.setText(Cross.solveXcross(scramble));
        } else {
            llHintc.setVisibility(View.GONE);
            tvHintc.setVisibility(View.GONE);
            llHintx.setVisibility(View.GONE);
            tvHintx.setVisibility(View.GONE);
        }
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        buidler.setTitle(R.string.action_scramble).setView(view)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editText.getText().toString();
                        if (TextUtils.isEmpty(text)) return;
                        int len = Integer.parseInt(text);
                        if (editText.isEnabled() && len > 0) {
                            if (len > 180) len = 180;
                            if (len != scrambleLen && getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).setScrambleLen(len);
                            }
                        }
                    }
                }).setNegativeButton(R.string.copy_scramble, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                android.content.ClipboardManager clip = (android.content.ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("text", scramble));
                Toast.makeText(getActivity(), getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
            }
        });
        return buidler.create();
    }
}

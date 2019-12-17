package com.dctimer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.Utils;

import java.io.File;

import static com.dctimer.APP.currentPath;
import static com.dctimer.APP.defaultPath;
import static com.dctimer.APP.savePath;

public class ExportScrambleDialog extends DialogFragment {
    private EditText editNumber;
    private EditText editPath;
    private EditText editName;
    private String path;
    private String scramble;

    public static ExportScrambleDialog newInstance(String scramble) {
        ExportScrambleDialog dialog = new ExportScrambleDialog();
        Bundle bundle = new Bundle();
        bundle.putString("scramble", scramble);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        scramble = getArguments().getString("scramble", "");
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_export_scramble, null);
        editNumber = view.findViewById(R.id.edit_scrnum);
        editNumber.setText("5");
        editNumber.setSelection(1);
        editPath = view.findViewById(R.id.edit_scrpath);
        editPath.setText(savePath);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setEditText(editPath);
        }
        editName = view.findViewById(R.id.edit_scrfile);
        ImageButton btnPath = view.findViewById(R.id.btn_browse);
        btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPath = editPath.getText().toString();
                File f = new File(currentPath);
                if (!f.exists()) currentPath = defaultPath + File.separator;
                FileSelectorDialog fileSelectorDialog = FileSelectorDialog.newInstance(currentPath, false);
                fileSelectorDialog.show(getActivity().getSupportFragmentManager(), "FileSelector");
            }
        });
        buidler.setView(view).setTitle(getString(R.string.action_export_scramble) + "(" + scramble + ")")
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        String str = editNumber.getText().toString();
                        if (TextUtils.isEmpty(str)) return;
                        int n = Integer.parseInt(str);
                        if (n > 500) n = 500;
                        else if (n < 1) n = 5;
                        path = editPath.getText().toString();
                        String fileName = editName.getText().toString();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).exportScramble(n, path, fileName);
                        }
                    }
                }).setNegativeButton(R.string.btn_cancel, null);
        return buidler.create();
    }
}

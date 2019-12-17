package com.dctimer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.Utils;

import java.io.File;

import static com.dctimer.APP.currentPath;
import static com.dctimer.APP.savePath;

public class ImportExportDialog extends DialogFragment {
    private EditText editText;
    private RadioGroup radioGroup;
    private String path;

    public ImportExportDialog newInstance() {
        ImportExportDialog dialog = new ImportExportDialog();
        return new ImportExportDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_import_export, null);
        editText = view.findViewById(R.id.edit_scrpath);
        editText.setText(savePath + "database.db");
        editText.setSelection(editText.getText().length());
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setEditText(editText);
        }
        radioGroup = view.findViewById(R.id.rgroup);
        ImageButton btnPath = view.findViewById(R.id.btn_browse);
        btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPath = editText.getText().toString();
                currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                File f = new File(currentPath);
                if (!f.exists()) currentPath = Environment.getExternalStorageDirectory().getPath() + File.separator;
                FileSelectorDialog fileSelectorDialog = FileSelectorDialog.newInstance(currentPath, true);
                fileSelectorDialog.show(getActivity().getSupportFragmentManager(), "FileSelector");
            }
        });
        buidler.setView(view).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                Utils.hideKeyboard(editText);
                path = editText.getText().toString();
                int rgid = radioGroup.getCheckedRadioButtonId();
                if (rgid == R.id.rbt_in) {  //导入数据库
                    if (getActivity() instanceof MainActivity)
                        ((MainActivity) getActivity()).importDatabase(path);
                } else if (rgid == R.id.rbt_out) {  //导出数据库
                    if (getActivity() instanceof MainActivity)
                        ((MainActivity) getActivity()).exportDatabase(path);
                }
            }
        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Utils.hideKeyboard(editText);
            }
        });
        return buidler.create();
    }
}

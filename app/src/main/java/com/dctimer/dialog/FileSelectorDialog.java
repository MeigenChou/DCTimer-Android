package com.dctimer.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.dctimer.R;
import com.dctimer.activity.DetailActivity;
import com.dctimer.activity.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileSelectorDialog extends DialogFragment {
    private ListView listView;
    private TextView tvPath;
    private String path;
    private boolean listFiles;
    public List<String> items, pathList;

    public static FileSelectorDialog newInstance(String path, boolean listFiles) {
        FileSelectorDialog dialog = new FileSelectorDialog();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putBoolean("list_files", listFiles);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        path = getArguments().getString("path");
        listFiles = getArguments().getBoolean("list_files", false);
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_file_selector, null);
        listView = view.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                path = pathList.get(i);
                File f = new File(path);
                if (f.isDirectory()) {
                    tvPath.setText(path);
                    getFileDirs();
                } else {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setFilePath(path, listFiles);
                    } else if (getActivity() instanceof DetailActivity) {
                        ((DetailActivity) getActivity()).setFilePath(path);
                    }
                    dismiss();
                }
            }
        });
        tvPath = view.findViewById(R.id.text);
        tvPath.setText(path);
        getFileDirs();
        buidler.setView(view);
        if (!listFiles)
            buidler.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).setFilePath(path, listFiles);
                    } else if (getActivity() instanceof DetailActivity) {
                        ((DetailActivity) getActivity()).setFilePath(path);
                    }
                }
            });
        buidler.setNegativeButton(R.string.btn_cancel, null);
        return buidler.create();
    }

    private void getFileDirs() {
        items = new ArrayList<>();
        pathList = new ArrayList<>();
        File f = new File(path);
        File[] fs = f.listFiles();
        if (fs != null && fs.length > 0) Arrays.sort(fs, new Comparator<File>() {
            @Override
            public int compare(File arg0, File arg1) {
                String fn1 = arg0.getName();
                String fn2 = arg1.getName();
                return fn1.compareToIgnoreCase(fn2);
            }
        });
        if (!path.equals("/")) {
            items.add("d..");
            pathList.add(f.getParent());
        }
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                File file = fs[i];
                if (file.isDirectory()) {
                    items.add("d" + file.getName());
                    pathList.add(file.getPath());
                }
            }
            if (listFiles) {
                for (int i = 0; i < fs.length; i++) {
                    File file = fs[i];
                    if (!file.isDirectory()) {
                        items.add("f" + file.getName());
                        pathList.add(file.getPath());
                    }
                }
            }
        }
        //ArrayAdapter<String> fileList = new ArrayAdapter<>(getActivity(), R.layout.file_list_item, items);
        FileListAdapter adapter = new FileListAdapter();
        listView.setAdapter(adapter);
    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    class FileListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.file_list_item, viewGroup, false);
                holder.textView = view.findViewById(R.id.text);
                holder.imageView = view.findViewById(R.id.icon);
                view.setTag(holder);
            } else holder = (ViewHolder) view.getTag();
            String item = items.get(i);
            if (item.charAt(0) == 'd') {
                holder.imageView.setImageResource(R.drawable.ic_folder);
            } else holder.imageView.setImageResource(R.drawable.ic_file);
            holder.textView.setText(item.substring(1));
            return view;
        }
    }
}

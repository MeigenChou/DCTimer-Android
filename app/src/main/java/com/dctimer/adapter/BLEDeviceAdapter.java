package com.dctimer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.model.BLEDevice;

import java.util.List;

public class BLEDeviceAdapter extends RecyclerView.Adapter<BLEDeviceAdapter.ViewHolder> {
    private MainActivity context;
    private List<BLEDevice> list;

    public BLEDeviceAdapter(MainActivity context, List<BLEDevice> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<BLEDevice> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        TextView tvName;
        TextView tvStatus;
        ProgressBar progress;

        private ViewHolder(View view) {
            super(view);
            item = view;
            tvName = view.findViewById(R.id.tv_name);
            tvStatus = view.findViewById(R.id.tv_status);
            progress = view.findViewById(R.id.progress);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_list, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                context.connectCube(pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        BLEDevice cube = list.get(i);
        holder.tvName.setText(cube.getName());
        int connect = cube.getConnected();
        if (connect == 0) {
            holder.progress.setVisibility(View.GONE);
            //holder.tvStatus.setText(R.string.not_connected);
        } else if (connect == 1) {
            holder.progress.setVisibility(View.GONE);
            //holder.tvStatus.setText(R.string.connected);
        } else {
            holder.progress.setVisibility(View.VISIBLE);
            //holder.tvStatus.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}

package com.dctimer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.activity.MainActivity;
import com.dctimer.R;
import com.dctimer.model.Result;
import com.dctimer.util.Stats;
import com.dctimer.util.StringUtils;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private MainActivity dct;
    private int column;
    private int length;
    //private ArrayList<int[]> times;
    private Result result;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        TextView textView;
        Button[] button = new Button[7];
        View divider;
        int[] btids = {R.id.bt_time1, R.id.bt_time2, R.id.bt_time3, R.id.bt_time4, R.id.bt_time5, R.id.bt_time6, R.id.bt_time7};

        private ViewHolder(View view) {
            super(view);
            item = view;
            textView = view.findViewById(R.id.tv_num);
            for (int i = 0; i < button.length; i++) {
                button[i] = view.findViewById(btids[i]);
            }
            divider = view.findViewById(R.id.divider);
        }
    }

    public ResultAdapter(MainActivity dct, Result result) {
        this.dct = dct;
        this.result = result;
        if (APP.multiPhase > 0) {
            this.column = APP.multiPhase + 3;
            length = result.length() + 1;
        } else {
            this.column = 4;
            length = result.length();
        }
    }

    public void reload() {
        this.column = APP.multiPhase > 0 ? APP.multiPhase + 3 : 4;
        length = APP.multiPhase > 0 ? result.length() + 1 : result.length();
        notifyDataSetChanged();
    }

    public void setLength(int length) {
        this.length = length;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(dct).inflate(R.layout.times_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.button[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos < result.length())
                    dct.showDetail(pos);
            }
        });
        holder.button[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (APP.multiPhase == 0)
                    dct.showAvgDetail(1, pos);
            }
        });
        holder.button[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (APP.multiPhase == 0)
                    dct.showAvgDetail(2, pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= result.length()) {
            if (APP.multiPhase > 0) {
                holder.textView.setTextColor(0x99000000);
                holder.textView.setText(R.string.multi_phase_mean);
                holder.button[0].setText("");
                for (int i=1; i<7; i++) {
                    if (i < column - 1) {
                        holder.button[i].setVisibility(View.VISIBLE);
                        holder.button[i].setText(result.getMpMean(i - 1));
                        holder.button[i].setTextColor(0x99000000);
                    } else holder.button[i].setVisibility(View.GONE);
                }
            }
        } else {
            int pos = APP.sortType == 0 ? position : result.getSortIdx(position);
            //序号
            holder.textView.setText(String.valueOf(pos + 1));
            if (pos == result.getMinIdx()) holder.textView.setTextColor(APP.colors[2]);
            else if (pos == result.getMaxIdx()) holder.textView.setTextColor(APP.colors[3]);
            else holder.textView.setTextColor(0xff000000);
            //单次成绩
            holder.button[0].setText(result.getTimeAt(pos, false));
            if (pos == result.getMinIdx()) //单次最快
                holder.button[0].setTextColor(APP.colors[2]);
            else if (pos == result.getMaxIdx())    //单次最慢
                holder.button[0].setTextColor(APP.colors[3]);
            else holder.button[0].setTextColor(0xff000000);
            //平均或分段
            for (int i=1; i<7; i++) {
                if (i < column - 1) {
                    holder.button[i].setVisibility(View.VISIBLE);
                    if (APP.multiPhase > 0) {
                        holder.button[i].setText(result.getMulTime(i-1, pos) == 0 ? "-" : StringUtils.timeToString(result.getMulTime(i-1, pos)));
                        if (pos == result.getMpMinIdx(i - 1)) {
                            holder.button[i].setTextColor(APP.colors[2]);
                        } else if (pos == result.getMpMinIdx(i - 1)) {
                            holder.button[i].setTextColor(APP.colors[3]);
                        } else holder.button[i].setTextColor(0xff000000);
                    } else {
                        if (i == 1) {
                            holder.button[i].setText(StringUtils.timeToString(result.getAvg1(pos)));
                            if (pos == result.getBestAvgIdx(0)) holder.button[i].setTextColor(APP.colors[4]);
                            else holder.button[i].setTextColor(0xff000000);
                        } else if (i == 2) {
                            holder.button[i].setText(StringUtils.timeToString(result.getAvg2(pos)));
                            if (pos == result.getBestAvgIdx(1)) holder.button[i].setTextColor(APP.colors[4]);
                            else holder.button[i].setTextColor(0xff000000);
                        }
                    }
                } else {
                    holder.button[i].setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return length;
    }
}

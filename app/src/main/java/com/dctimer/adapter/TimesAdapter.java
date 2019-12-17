package com.dctimer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dctimer.APP;
import com.dctimer.activity.MainActivity;
import com.dctimer.R;
import com.dctimer.model.Result;
import com.dctimer.util.StringUtils;

public class TimesAdapter extends BaseAdapter {
    private MainActivity dct;
    private Result result;
    private int column;
    private int length;
    private int highlight = -1;
    private static int[] btnId = {R.id.bt_time1, R.id.bt_time2, R.id.bt_time3, R.id.bt_time4, R.id.bt_time5, R.id.bt_time6, R.id.bt_time7};

    class ViewHolder {
        RelativeLayout item;
        TextView textView;
        Button[] button = new Button[7];
        View divider;
    }

    public TimesAdapter(MainActivity dct, Result result) {
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

    public void setHighlight(int highlight) {
        this.highlight = highlight;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(dct).inflate(R.layout.times_item, viewGroup, false);
            holder.item = view.findViewById(R.id.rl_item);
            holder.textView = view.findViewById(R.id.tv_num);
            for (int i = 0; i < holder.button.length; i++) {
                holder.button[i] = view.findViewById(btnId[i]);
            }
            holder.divider = view.findViewById(R.id.divider);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();
        if (position >= result.length()) {
            if (APP.multiPhase > 0) {
                holder.textView.setTextColor(0xff666666);
                holder.textView.setText(R.string.multi_phase_mean);
                holder.button[0].setText("");
                holder.button[0].setTag(-1);
                holder.button[0].setBackgroundColor(-1);
                for (int i=1; i<7; i++) {
                    if (i < column - 1) {
                        holder.button[i].setVisibility(View.VISIBLE);
                        holder.button[i].setBackgroundColor(-1);
                        holder.button[i].setText(result.getMpMean(i - 1));
                        holder.button[i].setTextColor(0xff666666);
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
            if (position == highlight) holder.button[0].setBackgroundColor(0xffeeeeee);
            else holder.button[0].setBackgroundResource(R.drawable.item_background);
            holder.button[0].setTag(position);
            holder.button[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    if (pos >= 0)
                        dct.showDetail(pos);
                }
            });
            //平均或分段
            for (int i=1; i<7; i++) {
                if (i < column - 1) {
                    holder.button[i].setVisibility(View.VISIBLE);
                    if (APP.multiPhase > 0) {
                        holder.button[i].setBackgroundColor(-1);
                        holder.button[i].setText(result.getMulTime(i-1, pos) == 0 ? "-" : StringUtils.timeToString(result.getMulTime(i - 1, pos)));
                        if (pos == result.getMpMinIdx(i - 1)) {
                            holder.button[i].setTextColor(APP.colors[2]);
                        } else if (pos == result.getMpMaxIdx(i - 1)) {
                            holder.button[i].setTextColor(APP.colors[3]);
                        } else holder.button[i].setTextColor(0xff000000);
                    } else {
                        holder.button[i].setBackgroundResource(R.drawable.item_background);
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
                    holder.button[i].setTag(position);
                    if (i == 1)
                        holder.button[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (APP.multiPhase == 0)
                                    dct.showAvgDetail(1, (int) view.getTag());
                            }
                        });
                    else if (i == 2)
                        holder.button[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (APP.multiPhase == 0)
                                    dct.showAvgDetail(2, (int) view.getTag());
                            }
                        });
                } else {
                    holder.button[i].setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}

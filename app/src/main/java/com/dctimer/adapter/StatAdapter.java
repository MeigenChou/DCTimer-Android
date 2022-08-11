package com.dctimer.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dctimer.R;
import com.dctimer.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int len;
    private int type;
    private List<String[]> detailList;
    private String[] stat;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        TextView textView;
        TextView textScr;
        TextView textDate;
        TextView textComment;

        public ViewHolder(View view) {
            super(view);
            item = view;
            textView = view.findViewById(R.id.tv_time);
            textScr = view.findViewById(R.id.tv_scr);
            textDate = view.findViewById(R.id.tv_date);
            textComment = view.findViewById(R.id.tv_comment);
        }
    }

    static class Header extends RecyclerView.ViewHolder {
        View item;
        TextView textTitle;
        TextView textAcc;
        View divider;

        public Header(View view) {
            super(view);
            item = view;
            textTitle = view.findViewById(R.id.text_title);
            textAcc = view.findViewById(R.id.text_acc);
            divider = view.findViewById(R.id.divider);
        }
    }

    static class Divider extends RecyclerView.ViewHolder {
        View item;
        TextView textView;

        public Divider(View view) {
            super(view);
            item = view;
            textView = view.findViewById(R.id.text_header);
        }
    }

    public StatAdapter(int avg, int pos, int len, String[] stat, List<Integer> trim) {
        this.len = len;
        this.type = avg;
        this.stat = stat;
        //this.trim = trim;
        //if (len == -1) len = Sessions.length;
        detailList = new ArrayList<>(len);
        if (avg == 1) { //去尾平均
            Utils.averageDetail(detailList, len, pos, trim);
        } else if (avg == 2) {  //平均
            Utils.meanDetail(detailList, len, pos);
        } else if (avg == 3) {  //分组平均
            Utils.sessionMeanDetail(detailList);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;// = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_header, parent, false);
                Header header = new Header(view);
                return header;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_divider, parent, false);
                Divider divider = new Divider(view);
                return divider;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
                ViewHolder holder = new ViewHolder(view);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (type == 3) {
            if (position < 5) {
                Header head = (Header) holder;
                if (position == 0) {
                    head.textTitle.setText(R.string.stat_solve);
                } else if (position == 1) {
                    head.textTitle.setText(R.string.stat_session_mean);
                } else if (position == 2) {
                    head.textTitle.setText(R.string.stat_session_avg);
                } else if (position == 3) {
                    head.textTitle.setText(R.string.stat_best);
                } else {
                    head.textTitle.setText(R.string.stat_worst);
                }
                head.textAcc.setText(stat[position]);
                head.divider.setVisibility(position == 4 ? View.GONE : View.VISIBLE);
            } else if (position == 5) {
                //divider
            } else {
                position -= 6;
                ViewHolder vh = (ViewHolder) holder;
                String[] details = detailList.get(position);
                vh.textView.setText(details[0]);
                vh.textScr.setText(details[1]);
                vh.textDate.setText(details[2]);
                String comment = details[3];
                if (!TextUtils.isEmpty(comment))
                    vh.textComment.setText("["+comment+"]");
                else vh.textComment.setText("");
            }
        } else if (position < 3) {
            Header head = (Header) holder;
            if (position == 0) {
                head.textTitle.setText(type == 1 ? R.string.stat_avg : R.string.stat_mean);
            } else if (position == 1) {
                head.textTitle.setText(R.string.stat_best);
            } else {
                head.textTitle.setText(R.string.stat_worst);
            }
            head.textAcc.setText(stat[position]);
            head.divider.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
        } else if (position == 3) {
            //divider
        } else {
            position -= 4;
            ViewHolder vh = (ViewHolder) holder;
            String[] details = detailList.get(position);
            vh.textView.setText(details[0]);
            vh.textScr.setText(details[1]);
            vh.textDate.setText(details[2]);
            String comment = details[3];
            if (!TextUtils.isEmpty(comment))
                vh.textComment.setText("["+comment+"]");
            else vh.textComment.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (type == 3) return len + 6;
        return len + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (type == 3) {
            if (position < 5) return 0;
            else if (position == 5) return 1;
            return 2;
        } else if (position < 3) return 0;
        else if (position == 3) return 1;
        else return 2;
    }
}

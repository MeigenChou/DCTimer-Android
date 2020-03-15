package com.dctimer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.dctimer.activity.MainActivity;
import com.dctimer.R;

import java.util.List;
import java.util.Map;

import static com.dctimer.APP.*;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private MainActivity dct;
    private Map<Integer, String> headers;
    private List<Map<String, Object>> cells;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        RelativeLayout rlCell;
        LinearLayout layoutCell;
        TextView textView;
        TextView detailView;
        SeekBar seekAccessory;
        Switch checkAccessory;
        View divider;

        public ViewHolder(View view) {
            super(view);
            item = view;
            rlCell = view.findViewById(R.id.rl_item);
            layoutCell = view.findViewById(R.id.layout_cell);
            textView = view.findViewById(R.id.list_text);
            detailView = view.findViewById(R.id.list_detail);
            seekAccessory = view.findViewById(R.id.seek_accessory);
            checkAccessory = view.findViewById(R.id.check_accessory);
            divider = view.findViewById(R.id.divider);
        }
    }

    static class Header extends RecyclerView.ViewHolder {
        TextView tvHead;
        //View divider;
        View view;
        public Header(View v) {
            super(v);
            tvHead = v.findViewById(R.id.list_header_title);
            view = v.findViewById(R.id.view);
            //divider = v.findViewById(R.id.divider);
        }
    }

    static class Footer extends RecyclerView.ViewHolder {
        Button btnReset;
        public Footer(View v) {
            super(v);
            btnReset = v.findViewById(R.id.btn_reset);
        }
    }

    public SettingAdapter(MainActivity dct, Map<Integer, String> headers, List<Map<String, Object>> cells) {
        this.dct = dct;
        this.headers = headers;
        this.cells = cells;
    }

    public void reload() {
        for (int i = 0; i < cells.size(); i++) {
            Map<String, Object> map = cells.get(i);
            switch (i) {
                case 1: //wca
                    map.put("detail", wca);
                    break;
                case 2: //观察语音
                    map.put("detail", inspectionAlert);
                    break;
                case 3: //时间格式
                    map.put("detail", itemStr[13][timeFormat]);
                    break;
                case 4: //小数点格式
                    map.put("detail", itemStr[16][decimalMark]);
                    break;
                case 5: //成绩输入方式
                    map.put("detail", itemStr[0][enterTime]);
                    break;
                case 6: //更新方式
                    map.put("detail", itemStr[1][timerUpdate]);
                    break;
                case 7: //计时器精度
                    map.put("detail", itemStr[2][timerAccuracy]);
                    break;
                case 8: //启动延时
                    map.put("detail", String.format("%.02fs", freezeTime/20f));
                    map.put("value", freezeTime);
                case 9: //分段计时
                    map.put("detail", itemStr[3][multiPhase]);
                    break;
                case 10: //模拟ss计时
                    map.put("detail", simulateSS);
                    break;
                case 11:    //显示统计
                    map.put("detail", showStat);
                    break;
                case 12:    //拍桌子停表
                    map.put("detail", dropToStop);
                    break;
                case 13:    //灵敏度

                    break;
                case 15:    //打乱字体大小
                    map.put("detail", String.valueOf(scrambleSize));
                    map.put("value", scrambleSize - 12);
                case 16:    //显示打乱
                    map.put("detail", showImage);
                    break;
                case 17:    //等宽打乱
                    map.put("detail", monoFont);
                    break;
                case 21:    //确认成绩
                    map.put("detail", promptToSave);
                    break;
                case 22:    //滚动平均1
                    map.put("detail", itemStr[14][avg1Type]);
                    break;
                case 23:
                    map.put("detail", String.valueOf(avg1len));
                    break;
                case 24:    //滚动平均2
                    map.put("detail", itemStr[4][avg2Type]);
                    break;
                case 25:
                    map.put("detail", String.valueOf(avg2len));
                    break;
                case 26:    //更改分组
                    map.put("detail", selectSession);
                    break;
                case 28:    //三阶求解
                    map.put("detail", itemStr[5][solve333]);
                    break;
                case 29:    //SQ1求解
                    map.put("detail", itemStr[12][solveSq1]);
                    break;
                case 30:    //二阶求解
                    map.put("detail", itemStr[6][solve222]);
                    break;
                case 37:    //五魔配色
                    map.put("detail", itemStr[7][megaColorScheme]);
                    break;
                case 39:    //计时器字体
                    map.put("detail", itemStr[8][timerFont]);
                    break;
                case 40:    //计时器大小
                    map.put("detail", String.valueOf(timerSize));
                    map.put("value", timerSize - 50);
                case 44:    //显示背景图
                    map.put("detail", !useBgcolor);
                    break;
                case 50:    //左
                    map.put("detail", itemStr[15][swipeType[0]]);
                    break;
                case 51:    //右
                    map.put("detail", itemStr[15][swipeType[1]]);
                    break;
                case 52:    //上
                    map.put("detail", itemStr[15][swipeType[2]]);
                    break;
                case 53:    //下
                    map.put("detail", itemStr[15][swipeType[3]]);
                    break;
                case 55:    //屏幕常亮
                    map.put("detail", screenOn);
                    break;
                case 56:    //触感反馈
                    map.put("detail", itemStr[10][vibrateType]);
                    break;
                case 57:    //持续时间
                    map.put("detail", itemStr[11][vibrateTime]);
                    break;
                case 58:    //屏幕方向
                    map.put("detail", itemStr[9][screenOri]);
                    break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public @NonNull RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(dct).inflate(R.layout.setting_list_header, parent, false);
            return new Header(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(dct).inflate(R.layout.setting_list_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    //HashMap<String, Object> map = cells.get(pos);
                    dct.setPref(pos);
                }
            });
            holder.checkAccessory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    dct.setPref(pos);
                }
            });
            holder.seekAccessory.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    int pos = holder.getAdapterPosition();
                    Map<String, Object> map = cells.get(pos);
                    //Log.w("seek", pos+"/"+i);
                    switch (pos) {
                        case 8: //启动延时
                            //map.put("value", i);
                            String detail = String.format("%.02fs", i/20f);
                            map.put("detail", detail);
                            dct.updatePref(pos, detail);
                            //notifyItemChanged(pos, 1);
                            break;
                        case 13:    //拍桌子停表
                            //map.put("detail", detail);
                            break;
                        case 15:    //打乱字体
                            //map.put("value", i);
                            detail = String.valueOf(i + 12);
                            map.put("detail", detail);
                            dct.updatePref(pos, detail);
                            break;
                        case 18:    //打乱状态
                            //map.put("value", i);
                            break;
                        case 40:    //计时器大小
                            //map.put("value", i);
                            detail = String.valueOf(i + 50);
                            map.put("detail", detail);
                            dct.updatePref(pos, detail);
                            break;
                        case 45:    //不透明度
                            //map.put("value", i);
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int pos = holder.getAdapterPosition();
                    int progress = seekBar.getProgress();
                    Map<String, Object> map = cells.get(pos);
                    map.put("value", progress);
                    dct.updatePref(pos, progress);
                }
            });
            return holder;
        } else {
            view = LayoutInflater.from(dct).inflate(R.layout.setting_list_footer, parent, false);
            final Footer footer = new Footer(view);
            footer.btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dct.resetAll();
                }
            });
            return footer;
        }
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
//        if (payloads.isEmpty()) {
//            Log.w("dct", "payload null");
//            onBindViewHolder(holder, position);
//        } else {
//            Log.w("dct", "payload "+payloads.get(0).toString());
//        }
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Header) {
            Header hh = (Header) holder;
            hh.tvHead.setText(headers.get(position));
            if (position == 0) {
                if (hh.view == null) Log.e("dct", "view为Null0");
                else hh.view.setVisibility(View.GONE);
                //hh.divider.setVisibility(View.GONE);
            } else {
                if (hh.view == null) Log.e("dct", "view为Null1");
                else hh.view.setVisibility(View.VISIBLE);
                //hh.divider.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            Map<String, Object> map = cells.get(position);
            String title = (String) map.get("title");
            //vh.layoutCell.setOnClickListener(null);
            vh.textView.setText(title);
            int type = (int) map.get("type");
            if (type == 0) {
                vh.layoutCell.setBackgroundResource(R.drawable.item_background);
                vh.checkAccessory.setVisibility(View.GONE);
                vh.seekAccessory.setVisibility(View.GONE);
                String detail = (String) map.get("detail");
                if (detail.length() == 0) vh.detailView.setVisibility(View.GONE);
                else {
                    vh.detailView.setVisibility(View.VISIBLE);
                    vh.detailView.setText(detail);
                }
            } else if (type == 1) {
                vh.layoutCell.setBackgroundResource(R.drawable.item_background);
                vh.checkAccessory.setVisibility(View.VISIBLE);
                vh.checkAccessory.setChecked((boolean) map.get("detail"));
                vh.seekAccessory.setVisibility(View.GONE);
                vh.detailView.setVisibility(View.GONE);
            } else {
                //Log.w("setting", "seek");
                vh.layoutCell.setBackgroundResource(R.color.item_background);
                vh.checkAccessory.setVisibility(View.GONE);
                vh.seekAccessory.setVisibility(View.VISIBLE);
                int max = (int) map.get("max");
                vh.seekAccessory.setMax(max);
                int progress = (int) map.get("value");
                //Log.w("seek", position+"/"+max+"/"+progress);
                vh.seekAccessory.setProgress(progress);
                String detail = (String) map.get("detail");
                if (detail.length() == 0) vh.detailView.setVisibility(View.GONE);
                else {
                    vh.detailView.setVisibility(View.VISIBLE);
                    vh.detailView.setText(detail);
                }
            }
            if (headers.containsKey(position + 1)) {
                vh.divider.setVisibility(View.GONE);
            } else vh.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return cells.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == cells.size()) return 2;
        if (headers.containsKey(position))
            return 0;
        return 1;
    }

    public void setCheck(int pos, boolean chk) {
        Map<String, Object> map = cells.get(pos);
        map.put("detail", chk);
        notifyItemChanged(pos);
    }

    public void setText(int pos, String text) {
        if (pos < 0) return;
        Map<String, Object> map = cells.get(pos);
        map.put("detail", text);
        notifyItemChanged(pos);
    }
}

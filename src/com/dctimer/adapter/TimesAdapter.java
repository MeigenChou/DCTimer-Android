package com.dctimer.adapter;

import java.util.ArrayList;

import com.dctimer.Configs;
import com.dctimer.DCTimer;
import com.dctimer.R;
import com.dctimer.db.Session;
import com.dctimer.util.Statistics;

import android.content.Context;
import android.view.*;
import android.widget.*;

public class TimesAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> times;
	private TextView tv;
	private int h, col;
	private boolean isMulp;

	public TimesAdapter(Context context, int len, int h) {
		this.context = context;
		this.h = fontHeight(h);
		col = 3;
		isMulp = false;
		setData(len);
	}
	
	private int fontHeight(int h) {
		return (int) (DCTimer.fontScale * h + 0.5);
	}
	
	public void setData(int len) {
		times = new ArrayList<String>(len);
		if(isMulp) {
			for (int i = 0; i < len / col - 1; i++) {
				times.add(Statistics.distime(i, false));
				for (int j = 1; j < col; j++) {
					int temp = Session.mulp[j - 1][i];
					times.add(temp == 0 ? "-" : Statistics.distime(temp));
				}
			}
			times.add(context.getResources().getString(R.string.mulp_mean));
			for (int j = 1; j < col; j++) {
				times.add(Statistics.mulMean(j - 1));
			}
		} else {
			for (int i = 0; i < len/col; i++) {
				times.add(Statistics.distime(i, false));
				times.add(Statistics.average(Configs.stSel[14], Configs.l1len, i, 0));
				times.add(Statistics.average(Configs.stSel[4], Configs.l2len, i, 1));
			}
		}
	}

	public TimesAdapter(Context context, int len, int h, int col) {
		this.context = context;
		this.h = fontHeight(h);
		this.col = col;
		isMulp = true;
		setData(len);
	}
	
	@Override
	public int getCount() {
		if(times != null) return times.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return times.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setHeight(int h) {
		this.h = fontHeight(h);
	}
	
	public void setColumn(int col) {
		this.col = col;
	}
	
	@Override
	public View getView(int po, View convertView, ViewGroup parent) {
		if (convertView == null) {
			tv = new TextView(context);
			tv.setLayoutParams(new GridView.LayoutParams(-1, h));
			tv.setTextSize(16);
			tv.setGravity(Gravity.CENTER);
		} else tv = (TextView) convertView;
		if (po / col == Statistics.minIdx && po % col == 0)
			tv.setTextColor(Configs.colors[2]);
		else if (po / col == Statistics.maxIdx && po % col == 0)
			tv.setTextColor(Configs.colors[3]);
		else if (!isMulp) {
			if (po / col == Statistics.bestIdx[0] && po % col == 1)
				tv.setTextColor(Configs.colors[4]);
			else if (po / col == Statistics.bestIdx[1] && po % col == 2)
				tv.setTextColor(Configs.colors[4]);
			else
				tv.setTextColor(Configs.colors[1]);
		} else if (po / col >= Session.resl)
			tv.setTextColor((Configs.colors[1] & 0xffffff) | (153 << 24));
		else
			tv.setTextColor(Configs.colors[1]);
		tv.setText(times.get(po));
		return tv;
	}
}

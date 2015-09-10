package com.dctimer.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;

public class TitleAdapter extends BaseAdapter {
	private Context context;
	private String[] times;
	private static TextView tv;
	private int cl;
	
	public TitleAdapter(Context context, String[] times, int cl) {
		this.context = context;
		this.times = times;
		this.cl = cl;
	}

	@Override
	public int getCount() {
		if(times != null) return times.length;
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		if(times != null) return times[arg0];
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			tv = new TextView(context);
			tv.setLayoutParams(new GridView.LayoutParams(-1, -2));
			tv.setTextSize(16);
			tv.setGravity(Gravity.CENTER);
		} else {
			tv = (TextView) convertView;
		}
		tv.setTextColor(cl);
		tv.setText(times[position]);
		return tv;
	}
}

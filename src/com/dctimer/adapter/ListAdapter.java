package com.dctimer.adapter;

import java.util.ArrayList;

import com.dctimer.*;
import com.dctimer.db.Session;
import com.dctimer.util.Statistics;

import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class ListAdapter extends BaseAdapter {
	private DCTimer dct;
	private ArrayList<String[]> times;
	private int column;
	private int height;
	private boolean isMulp;
	
	private static int[] bids = {R.id.bt_time1, R.id.bt_time2, R.id.bt_time3, R.id.bt_time4, R.id.bt_time5, R.id.bt_time6, R.id.bt_time7};
	//private static int[] vids = {R.id.view1, R.id.view2, R.id.view3, R.id.view4, R.id.view5, R.id.view6, R.id.view7};
	
	static class ViewHolder {
		public TextView textView;
		public Button[] button = new Button[7];
	}

	public ListAdapter(DCTimer dct, int height) {
		this.dct = dct;
		this.column = 4;
		this.isMulp = false;
		setHeight(height);
		setData();
	}
	
	public ListAdapter(DCTimer dct, int height, int col) {
		this.dct = dct;
		this.column = col;
		this.isMulp = true;
		setHeight(height);
		setData();
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setData() {
		int size = isMulp ? Session.length+1 : Session.length;
		times = new ArrayList<String[]>(size);
		if(isMulp) {
			for (int i = 0; i < Session.length; i++) {
				String[] data = new String[column];
				data[0] = String.valueOf(i+1);
				data[1] = Statistics.timeAt(i, false);
				for(int j=2; j<column; j++) {
					int time = Session.mulp[j - 2][i];
					data[j] = time == 0 ? "-" : Statistics.timeToString(time);
				}
				times.add(data);
			}
			String[] mean = new String[column];
			mean[1] = dct.getString(R.string.mulp_mean);
			for(int j=2; j<column; j++) {
				mean[j] = Statistics.mulMean(j - 2);
			}
			times.add(mean);
		} else {
			//System.out.println("¿ªÊ¼¼ÆËã");
			for (int i = 0; i < size; i++) {
				String[] data = {String.valueOf(i+1), Statistics.timeAt(i, false),
						Configs.stSel[14] == 0 ? Statistics.average(Configs.l1len, i, 0) : Statistics.mean(Configs.l1len, i, 0),
						Configs.stSel[4] == 0 ? Statistics.average(Configs.l2len, i, 1) : Statistics.mean(Configs.l2len, i, 1)};
				times.add(data);
			}
			//System.out.println("OK");
		}
	}
	
	@Override
	public int getCount() {
		return times.size();
	}

	@Override
	public Object getItem(int arg0) {
		return times.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			int resId = R.layout.times_item;
			convertView = LayoutInflater.from(dct).inflate(resId, null);
			holder = new ViewHolder();
			holder.textView = (TextView) convertView.findViewById(R.id.tv_num);
			for(int i=0; i<column-1; i++) {
				holder.button[i] = (Button) convertView.findViewById(bids[i]);
				holder.button[i].setVisibility(View.VISIBLE);
				holder.button[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						int idx = (Integer)view.getTag();
						switch (view.getId()) {
						case R.id.bt_time1:
							if(idx < Session.length)
								dct.showTime((Integer)view.getTag());
							break;
						case R.id.bt_time2:
							if(!isMulp && idx>Configs.l1len-2)
								dct.showAlertDialog(1, idx);
							break;
						case R.id.bt_time3:
							if(!isMulp && idx>Configs.l2len-2)
								dct.showAlertDialog(2, idx);
							break;
						}
					}
				});
				//convertView.findViewById(vids[i]).setVisibility(View.VISIBLE);
			}
			for(int i=column-1; i<7; i++) {
				holder.button[i] = (Button) convertView.findViewById(bids[i]);
				holder.button[i].setVisibility(View.GONE);
			}
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();
		//holder.textView.setLayoutParams(new LinearLayout.LayoutParams((int)(DCTimer.scale*52), height));
		for(int i=0; i<column-1; i++) {
			holder.button[i].setTag(pos);
			holder.button[i].setLayoutParams(new LinearLayout.LayoutParams(0, height, 1));
		}
		holder.textView.setText(times.get(pos)[0]);
		if(pos == Statistics.minIdx)
			holder.textView.setTextColor(Configs.colors[2]);
		else if(pos == Statistics.maxIdx)
			holder.textView.setTextColor(Configs.colors[3]);
		else holder.textView.setTextColor(0xff000000);
		for(int i=1; i<column; i++) {
			holder.button[i-1].setText(times.get(pos)[i]);
			if(pos == Statistics.minIdx && i == 1)
				holder.button[i-1].setTextColor(Configs.colors[2]);
			else if(pos == Statistics.maxIdx && i == 1)
				holder.button[i-1].setTextColor(Configs.colors[3]);
			else if (!isMulp) {
				if(pos == Statistics.bestIdx[0] && i == 2)
					holder.button[i-1].setTextColor(Configs.colors[4]);
				else if(pos == Statistics.bestIdx[1] && i == 3)
					holder.button[i-1].setTextColor(Configs.colors[4]);
				else holder.button[i-1].setTextColor(0xff000000);
			} else if(pos >= Session.length)
				holder.button[i-1].setTextColor(153 << 24);
			else holder.button[i-1].setTextColor(0xff000000);
		}
		return convertView;
	}
}

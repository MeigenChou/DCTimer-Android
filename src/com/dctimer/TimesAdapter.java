package com.dctimer;

import android.content.Context;
import android.view.*;
import android.widget.*;

public class TimesAdapter extends BaseAdapter {
	private Context context;
	private String[] times;
	private TextView tv;
	private int[] cl;
	private int omax, omin;
	private int h;
	public TimesAdapter(Context context, String[] times, int[] cl, int omax, int omin, int h) {
		this.context=context;
		this.times=times;
		this.cl=cl;
		this.omax=omax;
		this.omin=omin;
		this.h=h;
		if(times!=null)
			for(int i=0;i<times.length/3;i++) {
				times[i*3]=Mi.distime(i, false);
				times[i*3+1]=DCTimer.l1am?Mi.avg(DCTimer.listnum[DCTimer.spinSel[4]], i, 0):Mi.mean(DCTimer.listnum[DCTimer.spinSel[4]], i, 0);
				times[i*3+2]=DCTimer.l2am?Mi.avg(DCTimer.listnum[DCTimer.spinSel[5]+1], i, 1):Mi.mean(DCTimer.listnum[DCTimer.spinSel[5]+1], i, 1);
			}
	}

	@Override
	public int getCount() {
		if(times!=null)return times.length;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int po, View convertView, ViewGroup parent) {
		if (convertView == null) {
			tv = new TextView(context);
			tv.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, h));
		}
		else tv = (TextView) convertView;
		tv.setTextSize(16);
		tv.setGravity(Gravity.CENTER);
		if(po/3==omin && po%3==0)tv.setTextColor(cl[1]);
		else if(po/3==omax && po%3==0)tv.setTextColor(cl[2]);
		else if(po/3==Mi.bidx[0] && po%3==1)tv.setTextColor(cl[3]);
		else if(po/3==Mi.bidx[1] && po%3==2)tv.setTextColor(cl[3]);
		else tv.setTextColor(cl[0]);
		tv.setText(times[po]);
		return tv;
	}
}

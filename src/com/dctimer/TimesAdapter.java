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
	private int h, col;
	private boolean isMulp;
	public TimesAdapter(Context context, String[] times, int[] cl, int omax, int omin, int h) {
		this.context=context;
		this.times=times;
		this.cl=cl;
		this.omax=omax;
		this.omin=omin;
		this.h=h;
		col = 3;
		isMulp = false;
		if(times!=null) {
			int len = times.length/3;
			for(int i=0;i<len;i++) {
				times[i*3]=Mi.distime(i, false);
				times[i*3+1]=DCTimer.l1am?Mi.avg(DCTimer.listnum[DCTimer.spSel[4]], i, 0):Mi.mean(DCTimer.listnum[DCTimer.spSel[4]], i, 0);
				times[i*3+2]=DCTimer.l2am?Mi.avg(DCTimer.listnum[DCTimer.spSel[2]+1], i, 1):Mi.mean(DCTimer.listnum[DCTimer.spSel[2]+1], i, 1);
			}
		}
	}

	public TimesAdapter(Context context, String[] times, int[] para, int h, int col) {
		this.context=context;
		this.times=times;
		this.cl=para;
		this.omax=para[3];
		this.omin=para[4];
		this.h=h;
		this.col = col;
		isMulp = true;
		if(times!=null) {
			for(int i=0;i<times.length/col-1;i++) {
				times[i*col]=Mi.distime(i, false);
				for(int j=1; j<col; j++) {
					int temp = DCTimer.mulp[j-1][i];
					times[i*col+j]=temp==0?"-":Mi.distime(temp);
				}
			}
			int temp = times.length/col-1;
			times[temp*col]=context.getResources().getString(R.string.mulp_mean);
			for(int j=1; j<col; j++) {
				times[temp*col+j]=Mi.mulMean(j-1);
			}
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
		if(po/col==omin && po%col==0)tv.setTextColor(cl[1]);
		else if(po/col==omax && po%col==0)tv.setTextColor(cl[2]);
		else if(!isMulp) {
			if(po/col==Mi.bidx[0] && po%col==1)tv.setTextColor(cl[3]);
			else if(po/col==Mi.bidx[1] && po%col==2)tv.setTextColor(cl[3]);
			else tv.setTextColor(cl[0]);
		}
		else if(po/col>=DCTimer.resl){
			tv.setTextColor((cl[0]&0xffffff)|(153<<24));
		}
		else tv.setTextColor(cl[0]);
		tv.setText(times[po]);
		return tv;
	}
}

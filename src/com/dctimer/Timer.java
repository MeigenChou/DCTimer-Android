package com.dctimer;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class Timer {
	public int v;	//0-计时中 1-观察中 2-观察中(+2) 3-观察中(DNF)
	 int state = 0;	//0-停止 1-计时中 2-观察中
	
	long time, time0, time1;
	protected java.util.Timer myTimer;
	protected TimerTask timerTask = null;
	DCTimer ct;
	TimeHandler timeh;
	private int sec = 0;

	protected boolean isFreezed;
	
	public Timer(DCTimer parent){
		this.ct = parent;
		timeh = new TimeHandler();
		myTimer = new java.util.Timer();
	}
	
	public void freeze() {
		timerTask = new FreezeTask();
		ct.canStart = false;
		myTimer.schedule(timerTask, ct.frzTime*50);
	}
	
	public void stopf() {
		if(timerTask != null) timerTask.cancel();
		timerTask = null;
	}
	
	public void stopi() {
		if(state == 2) {
			if(timerTask != null) timerTask.cancel();
			timerTask = null;
			ct.tvTimer.setTextColor(ct.cl[1]);
		}
		state = v = 0;
	}
	
	public void count() {
		if(state==0 || state==2) {
			if(state==0 && ct.wca) state = 2;
			else state = 1;
			if(v == 0) {
				ct.tvTimer.setTextColor(ct.cl[1]);
				if(timerTask != null) timerTask.cancel();
				timerTask = new ClockTask();
				time0 = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 17);
			}
			else if(v == 1) {
				ct.tvTimer.setTextColor(0xffff0000);
				timerTask = new InspectTask();
				time0 = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 100);
			}
		}
		else {
			state = 0;
			if(v == 0){
				time1 = System.currentTimeMillis();
				time = time1 - time0;
				timerTask.cancel();
				timerTask = null;
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {e.printStackTrace();}
						timeh.sendEmptyMessage(2);
					}
				}).start();
			}
		}
	}
	
	private class ClockTask extends TimerTask {
		@Override
		public void run() {
			v = 0;
			time = System.currentTimeMillis() - time0;
			timeh.sendEmptyMessage(0);
		}
	}
	
	private class InspectTask extends TimerTask {
		@Override
		public void run() {
			time = System.currentTimeMillis() - time0;
			if(time/1000 < 15) {
				sec = (int) (15-time/1000);
				v = 1;
			}
			else if(time/1000 < 17) {
				sec = 0;
				v = 2;
			}
			else v = 3;
			timeh.sendEmptyMessage(0);
		}
	}
	
	private class FreezeTask extends TimerTask {
		@Override
		public void run() {
			ct.canStart = true;
			timeh.sendEmptyMessage(1);
		}
	}
	
	private String contime(int i) {
		boolean m = i<0;
		if(m) i = -i;
		i /= 1000;
		int sec = DCTimer.clkform ? i%60 : i;
		int min = DCTimer.clkform ? (i/60)%60 : 0;
		int hour = DCTimer.clkform ? i/3600 : 0;
		StringBuilder time = new StringBuilder();
		if(hour == 0) {
			if(min == 0) time.append(""+sec);
			else {
				if(sec<10) time.append(min+":0"+sec);
				else time.append(min+":"+sec);
			}
		}
		else {
			time.append(""+hour);
			if(min<10) time.append(":0"+min);
			else time.append(":"+min);
			if(sec<10) time.append(":0"+sec);
			else time.append(":"+sec);
		}
		return time.toString();
	}
	
	private class TimeHandler extends Handler {
		public void handleMessage (Message msg) {
			if(msg.what==1) ct.tvTimer.setTextColor(0xff00ff00);
			else if(msg.what==2) ct.tvTimer.setText(Mi.distime((int)time));
			else if(v==0) {
				if(DCTimer.stSel[1]==0) ct.tvTimer.setText(Mi.distime((int)time));
				else if(DCTimer.stSel[1]==1)ct.tvTimer.setText(contime((int)time));
				else ct.tvTimer.setText(ct.getResources().getString(R.string.solve));
			}
			else if(v==1) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText(""+sec);
				else ct.tvTimer.setText(ct.getResources().getString(R.string.inspect));
			}
			else if(v==2) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText("+2");
			}
			else if(v==3) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText("DNF");
			}
		}
	}
}

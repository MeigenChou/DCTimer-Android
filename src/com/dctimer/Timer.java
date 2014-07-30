package com.dctimer;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class Timer {
	public int insp;	//1-正常 2-+2 3-DNF
	public int state = 0;	//0-就绪 1-计时中 2-观察中 3-停止
	
	long time, time0, time1;
	protected java.util.Timer myTimer;
	protected TimerTask timerTask = null;
	FreezeThread threadf = null;
	DCTimer ct;
	TimeHandler timeh;
	private int sec = 0;
	
	public Timer(DCTimer parent) {
		this.ct = parent;
		timeh = new TimeHandler();
		myTimer = new java.util.Timer();
	}
	
	public void freeze() {
		threadf = new FreezeThread();
		threadf.start();
	}
	
	public void stopf() {
		if(threadf != null && threadf.isAlive()) threadf.interrupt();
		threadf = null;
	}
	
	public void stopi() {
		if(state == 2) {
			timerTask.cancel();
			timerTask = null;
			ct.tvTimer.setTextColor(ct.cl[1]);
		}
		state = 0;
	}
	
	public void count() {
		if(state==0 || state==2) {
			time = 0;
			if(state==0 && ct.wca) {
				state = 2;
				ct.tvTimer.setTextColor(0xffff0000);
				timerTask = new InspectTask();
				time0 = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 100);
			}
			else {
				if(timerTask != null) {
					timerTask.cancel();
				}
				state = 1;
				ct.tvTimer.setTextColor(ct.cl[1]);
				timerTask = new ClockTask();
				time0 = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 17);
			}
		}
		else if(state == 1) {
			state = 3;
			time1 = System.currentTimeMillis();
			time = time1 - time0;
			timerTask.cancel();
			timerTask = null;
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) { }
					timeh.sendEmptyMessage(2);
				}
			}).start();
		}
	}
	
	private class ClockTask extends TimerTask {
		@Override
		public void run() {
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
				insp = 1;
			}
			else if(time/1000 < 17) {
				sec = 0;
				insp = 2;
			}
			else insp = 3;
			timeh.sendEmptyMessage(0);
		}
	}
	
	private class FreezeThread extends Thread {
		public void run() {
			try {
				ct.canStart = false;
				sleep(ct.frzTime*50);
				ct.canStart = true;
				timeh.sendEmptyMessage(1);
				return;
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private String contime(int i) {
		boolean m = i<0;
		if(m) i = -i;
		i /= 1000;
		int sec = i, min = 0, hour = 0;
		if(DCTimer.stSel[13] < 2) {
			min = sec / 60;
			sec = sec % 60;
			if(DCTimer.stSel[13] < 1) {
				hour = sec / 60;
				sec = sec % 60;
			}
		}
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
			else if(state==1) {
				if(DCTimer.stSel[1]==0) ct.tvTimer.setText(Mi.distime((int)time));
				else if(DCTimer.stSel[1]==1)ct.tvTimer.setText(contime((int)time));
				else ct.tvTimer.setText(ct.getResources().getString(R.string.solve));
			}
			else if(insp==1) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText(""+sec);
				else ct.tvTimer.setText(ct.getResources().getString(R.string.inspect));
			}
			else if(insp==2) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText("+2");
			}
			else if(insp==3) {
				if(DCTimer.stSel[1]<3)ct.tvTimer.setText("DNF");
			}
		}
	}
}

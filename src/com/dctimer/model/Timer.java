package com.dctimer.model;

import java.util.TimerTask;

import com.dctimer.*;
import com.dctimer.util.Statistics;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class Timer {
	public int insp;	//1-正常 2-+2 3-DNF
	public static int state = 0;	//0-就绪 1-计时中 2-观察中 3-停止
	
	public long time, timeStart, timeEnd;
	protected java.util.Timer myTimer;
	protected TimerTask timerTask = null;
	FreezeThread freezeThread = null;
	DCTimer dct;
	TimeHandler timeh;
	static int inspSec = 0;
	
	public Timer(DCTimer dct) {
		this.dct = dct;
		timeh = new TimeHandler();
		myTimer = new java.util.Timer();
	}
	
	public void startFreeze() {
		freezeThread = new FreezeThread();
		freezeThread.start();
	}
	
	public void stopFreeze() {
		if(freezeThread != null)
			freezeThread.canStart = false;
	}
	
	public void stopInspect() {
		if(state == 2) {
			timerTask.cancel();
			timerTask = null;
			dct.setTimerColor(Configs.colors[1]);
		}
		state = 0;
	}
	
	public void count() {
		if(state==0 || state==2) {
			time = 0;
			if(state==0 && Configs.wca) {
				state = 2;
				dct.setTimerColor(0xffff0000);
				timerTask = new InspectTask();
				//timeStart = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 100);
			} else {
				if(Configs.wca && timerTask != null) {
					timerTask.cancel();
					timerTask = null;
				}
				state = 1;
				dct.setTimerColor(Configs.colors[1]);
				timerTask = new ClockTask();
				//timeStart = System.currentTimeMillis();
				myTimer.schedule(timerTask, 0, 17);
			}
		}
		else if(state == 1) {
			state = 3;
			//timeEnd = System.currentTimeMillis();
			time = timeEnd - timeStart;
			timerTask.cancel();
			timerTask = null;
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(23);
					} catch (InterruptedException e) { }
					timeh.sendEmptyMessage(2);
				}
			}).start();
		}
	}
	
	private class ClockTask extends TimerTask {
		@Override
		public void run() {
			time = System.currentTimeMillis() - timeStart;
			timeh.sendEmptyMessage(0);
		}
	}
	
	private class InspectTask extends TimerTask {
		@Override
		public void run() {
			time = System.currentTimeMillis() - timeStart;
			if(time < 15000) {
				inspSec = (int) (time / 1000);
				insp = 1;
			}
			else if(time < 17000) insp = 2;
			else insp = 3;
			timeh.sendEmptyMessage(0);
		}
	}
	
	private class FreezeThread extends Thread {
		public boolean canStart = true;
		public void run() {
			long start = System.currentTimeMillis();
			DCTimer.canStart = false;
			while (canStart) {
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				long time = System.currentTimeMillis() - start;
				if(time >= Configs.freezeTime * 50) {
					DCTimer.canStart = true;
					timeh.sendEmptyMessage(1);
					return;
				}
			}
		}
	}
	
	private String contime(int i) {
		boolean m = i<0;
		if(m) i = -i;
		i /= 1000;
		int sec = i, min = 0, hour = 0;
		if(Configs.stSel[13] < 2) {
			min = sec / 60;
			sec = sec % 60;
			if(Configs.stSel[13] < 1) {
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
	
	@SuppressLint("HandlerLeak")
	class TimeHandler extends Handler {
		public void handleMessage (Message msg) {
			if(msg.what == 1) dct.setTimerColor(0xff00ff00);
			else if(msg.what == 2) dct.setTimerText(Statistics.timeToString((int)time));
			else if(state == 1) {
				if(Configs.stSel[1] == 0) dct.setTimerText(Statistics.timeToString((int)time));
				else if(Configs.stSel[1] == 1) dct.setTimerText(contime((int)time));
				else dct.setTimerText(dct.getString(R.string.solve));
			}
			else if(insp == 1) {
				if(Configs.stSel[1] < 3) {
					dct.setTimerText(String.valueOf(inspSec));
//					if(inspSec == 8 || inspSec == 12)
//						dct.tvTimer.setTextColor(0xffff8080);
//					else dct.tvTimer.setTextColor(0xffff0000);
				}
				else dct.setTimerText(dct.getResources().getString(R.string.inspect));
			}
			else if(insp == 2) {
				if(Configs.stSel[1] < 3) dct.setTimerText("+2");
			}
			else if(insp == 3) {
				if(Configs.stSel[1] < 3) dct.setTimerText("DNF");
			}
		}
	}
}

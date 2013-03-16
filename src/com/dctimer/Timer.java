package com.dctimer;

import android.os.Handler;
import android.os.Message;

public class Timer {
	public int v;	//0-计时中 1-观察中 2-观察中(+2) 3-观察中(DNF)
	private int state = 0;	//0-停止 1-计时中 2-观察中
	
	long time,time0,time1;
	ClockThread threadS;
	CountDownThread threadC;
	TapThread threadT;
	DCTimer ct;
	TimeHandler timeh;
	private int sec = 0;

	protected boolean isTapped;
	
	public Timer(DCTimer parent){
		this.ct = parent;
		timeh = new TimeHandler();
	}
	
	public void tap(){
		threadT = new TapThread();
		threadT.start();
	}
	public void stopt(){
		if(threadT != null && threadT.isAlive())threadT.interrupt();
	}
	
	public void stopi(){
		if(state==2){
			state=0;
			if(ct.wca && threadC.isAlive()) threadC.interrupt();
			ct.tvTimer.setTextColor(ct.cl[1]);
		}
	}
	public void count(){
		if(state==0 || state==2){
			if(state==0 && ct.wca) state = 2;
			else state=1;
			if(v == 0){
				ct.tvTimer.setTextColor(ct.cl[1]);
				if(ct.wca && threadC.isAlive()) threadC.interrupt();
				threadS = new ClockThread();
				time0=System.currentTimeMillis();
				threadS.start();
			}
			else if(v==1){
				ct.tvTimer.setTextColor(0xffff0000);
				threadC = new CountDownThread();
				time0=System.currentTimeMillis();
				threadC.start();
			}
		}
		else{
			state =0;
			if(v == 0){
				if(threadS.isAlive()) {
					time1=System.currentTimeMillis();
					time=time1-time0;
					threadS.interrupt();
				}
			}
		}
	}
//	public void cancel(){
//		if(state==1 || state==2){
//			state=0;
//			if(v==0 && threadS.isAlive())threadS.interrupt();
//			if(v>0 && ct.wca && threadC.isAlive())threadC.interrupt();
//		}
//	}
	private class ClockThread extends Thread {
		public void run(){
			while(true){
				v=0;
				time=System.currentTimeMillis()-time0;
				timeh.sendEmptyMessage(0);
				try {
					sleep(17);
				}
				catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	private class CountDownThread extends Thread {
		public void run(){
			while(true){
				time=System.currentTimeMillis()-time0;
				if(time/1000<15) {
					sec=(int) (15-time/1000);
					v=1;
				}
				else if(time/1000<17) {
					sec=0;
					v=2;
				}
				else v=3;
				timeh.sendEmptyMessage(0);
				try {
					sleep(100);
				}
				catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	private class TapThread extends Thread {
		public void run(){
			while(isTapped) {
				try {
					ct.canStart = false;
					sleep(ct.tapTime*50);
					ct.canStart = true;
					timeh.sendEmptyMessage(1);
					return;
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
	private String contime(int i) {
		boolean m = i<0;
		if(m)i = -i;
		i/=1000;
		int sec=DCTimer.timmh?i%60:i;
		int min=DCTimer.timmh?(i/60)%60:0;
		int hour=DCTimer.timmh?i/3600:0;
		StringBuilder time=new StringBuilder();
		if(hour==0) {
			if(min==0) time.append(""+sec);
			else {
				if(sec<10)time.append(min+":0"+sec);
				else time.append(min+":"+sec);
			}
		}
		else {
			time.append(""+hour);
			if(min<10)time.append(":0"+min);
			else time.append(":"+min);
			if(sec<10)time.append(":0"+sec);
			else time.append(":"+sec);
		}
		return time.toString();
	}
	
	private class TimeHandler extends Handler{
		public void handleMessage (Message msg){
			if(msg.what==1)ct.tvTimer.setTextColor(0xff00ff00);
			else if(v==0) {
				if(DCTimer.spSel[16]==0) ct.tvTimer.setText(Mi.distime((int)time));
				else if(DCTimer.spSel[16]==1)ct.tvTimer.setText(contime((int)time));
				else ct.tvTimer.setText(ct.getResources().getString(R.string.solve));
			}
			else if(v==1) {
				if(DCTimer.spSel[16]<3)ct.tvTimer.setText(""+sec);
				else ct.tvTimer.setText(ct.getResources().getString(R.string.inspect));
			}
			else if(v==2) {
				if(DCTimer.spSel[16]<3)ct.tvTimer.setText("+2");
			}
			else if(v==3) {
				if(DCTimer.spSel[16]<3)ct.tvTimer.setText("DNF");
			}
		}
	}
}

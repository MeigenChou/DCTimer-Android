package com.dctimer;

import android.graphics.Color;
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
	private int msec = 0;
	private int sec = 0;
	private int min = 0;
	private int hour = 0;
	//String timeshow="";

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
				ct.tvTimer.setTextColor(Color.RED);
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
				msec=(int) time%1000;
				if(DCTimer.spSel[6]==0)msec=((msec+5)/10)%100;
				sec=(int) (DCTimer.timmh?(time/1000)%60:time/1000);
				if(DCTimer.spSel[6]==0 && time%1000>994)sec++;
				min=(int) (DCTimer.timmh?(time/60000)%60:0);
				hour=(int) (DCTimer.timmh?time/3600000:0);
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
					timeh.sendEmptyMessage(4);
				} catch (InterruptedException e) {
					return;
				}
				return;
			}
		}
	}
	private class TimeHandler extends Handler{
		public void handleMessage (Message msg){
			if(msg.what==4)ct.tvTimer.setTextColor(Color.GREEN);
			else if(v==0)ct.tvTimer.setText(Mi.contime(hour, min, sec, msec));
			else if(v==1)ct.tvTimer.setText(""+sec);
			else if(v==2)ct.tvTimer.setText("+2");
			else if(v==3)ct.tvTimer.setText("DNF");
		}
	}
}

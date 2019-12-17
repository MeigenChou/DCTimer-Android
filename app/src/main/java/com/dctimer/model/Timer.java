package com.dctimer.model;

import java.util.TimerTask;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.util.StringUtils;

import android.annotation.SuppressLint;
import android.os.*;

public class Timer {
    private int inspectionState;	//1-观察中 2-+2 3-DNF
    private int timerState = 0;	//0-就绪 1-计时中 2-观察中 3-停止
    public static final int READY = 0;
    public static final int RUNNING = 1;
    public static final int INSPECTING = 2;
    public static final int STOP = 3;

    public long time, timeStart, timeEnd;
    private java.util.Timer myTimer;
    private TimerTask timerTask = null;
    private FreezeThread freezeThread = null;
    private MainActivity dct;
    private TimeHandler handler;
    private int inspectionTime = 0;

    private boolean eightSec;
    private boolean twelveSec;

    public Timer(MainActivity dct) {
        this.dct = dct;
        handler = new TimeHandler();
        myTimer = new java.util.Timer();
    }

    public int getTimerState() {
        return timerState;
    }

    public void setTimerState(int timerState) {
        this.timerState = timerState;
    }

    public int getPenaltyTime() {
        return inspectionState == 2 ? 2000 : 0;
    }

    public boolean isDNF() {
        return inspectionState == 3;
    }

    public void startFreeze() {
        freezeThread = new FreezeThread();
        freezeThread.start();
    }

    public void stopFreeze() {
        if (freezeThread != null)
            freezeThread.canStart = false;
    }

    public void stopInspect() {
        if (timerState == INSPECTING) {
            timerTask.cancel();
            timerTask = null;
            dct.setTimerColor(APP.colors[1]);
        }
        timerState = READY;
    }

    public void count() {
        if (timerState == READY || timerState == INSPECTING) {
            time = 0;
            if (timerState == READY && APP.wca) {
                timerState = INSPECTING;
                dct.setTimerColor(0xffff0000);
                timerTask = new InspectTask();
                eightSec = twelveSec = false;
                myTimer.schedule(timerTask, 0, 100);
            } else {
                if (APP.wca && timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                timerState = RUNNING;
                dct.setTimerColor(APP.colors[1]);
                timerTask = new ClockTask();
                myTimer.schedule(timerTask, 0, 17);
            }
        } else if (timerState == RUNNING) {
            timerState = STOP;
            time = timeEnd - timeStart;
            if (time < 100) time = 100;
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(23);
                    } catch (Exception e) { }
                    handler.sendEmptyMessage(2);
                }
            }).start();
        }
    }

    private class ClockTask extends TimerTask {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - timeStart;
            handler.sendEmptyMessage(0);
        }
    }

    private class InspectTask extends TimerTask {
        @Override
        public void run() {
            time = SystemClock.uptimeMillis() - timeStart;
            if (APP.inspectionAlert && time >= 7700 && !eightSec) {
                eightSec = true;
                dct.sayAlert(R.string.eight_sec);
            }
            if (APP.inspectionAlert && time >= 11800 && !twelveSec) {
                twelveSec = true;
                dct.sayAlert(R.string.twelve_sec);
            }
            if (time < 15000) {
                inspectionTime = (int) (time / 1000);
                inspectionState = 1;
            }
            else if (time < 17000) inspectionState = 2;
            else inspectionState = 3;
            handler.sendEmptyMessage(0);
        }
    }

    private class FreezeThread extends Thread {
        private boolean canStart = true;
        public void run() {
            long start = SystemClock.uptimeMillis();
            dct.canStart = false;
            while (canStart) {
                try {
                    sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long time = SystemClock.uptimeMillis() - start;
                if (time >= APP.freezeTime * 50) {
                    dct.canStart = true;
                    handler.sendEmptyMessage(1);
                    break;
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class TimeHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == 1) dct.setTimerColor(0xff00ff00);
            else if (msg.what == 2) {
                if (APP.enterTime == 0)
                    dct.setTimerText(StringUtils.timeToString((int) time));
                else if (APP.enterTime >= 2)
                    dct.updateTime();
            } else if (timerState == 1) {
                if (APP.timerUpdate == 0) dct.setTimerText(StringUtils.timeToString((int) time));
                else if (APP.timerUpdate == 1) dct.setTimerText(StringUtils.timeToString((int) time, false));
                else dct.setTimerText(dct.getString(R.string.solving));
            } else if (inspectionState == 1) {
                if (APP.timerUpdate < 3) {
                    dct.setTimerText(String.valueOf(inspectionTime));
//					if (inspectionTime == 8 || inspectionTime == 12)
//						dct.tvTimer.setTextColor(0xffff8080);
//					else dct.tvTimer.setTextColor(0xffff0000);
                }
                else dct.setTimerText(dct.getResources().getString(R.string.inspecting));
            } else if (inspectionState == 2) {
                if (APP.timerUpdate < 3) dct.setTimerText("+2");
            } else if (inspectionState == 3) {
                if (APP.timerUpdate < 3) dct.setTimerText("DNF");
            }
        }
    }
}

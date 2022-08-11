package com.dctimer.model;

public class SmartTimer {
    //private int currentTime;
    //private int lastTime = 0;
    private TimeChangedCallback callback;

    public void setTimeChangedCallback(TimeChangedCallback callback) {
        this.callback = callback;
    }

    public void updateTime(byte[] data) {
        if (data.length < 8) return;
        int time = ((data[0] & 0xff) * 60 + (data[1] & 0xff)) * 1000 + (data[2] & 0xff) + (data[3] & 0xff) * 256;
        int lastTime = ((data[4] & 0xff) * 60 + (data[5] & 0xff)) * 1000 + (data[6] & 0xff) + (data[7] & 0xff) * 256;
        callback.onTimeChanged(time, lastTime);
        //lastTime = currentTime;
    }

    public interface TimeChangedCallback {
        void onTimeChanged(int time, int lastTime);
    }
}

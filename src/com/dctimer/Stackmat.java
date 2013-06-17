package com.dctimer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class Stackmat {
	protected RecordTask recorder;
	public AudioRecord record;
	private boolean isRecording = false;
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;   
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 
	private int bufferReadResult;
	public static int samplingRate;
	public static int switchThreshold;
	private int noiseSpikeThreshold;
	private double signalLengthPerBit;
	private int newPeriod;
	public static boolean inv;
	public boolean isStart;
	private boolean isValid = false;
	private boolean isMsec = false;
	DCTimer ct;
	private byte state = 0;	//0-off, 1-not ready, 2-running, 3-stop
	private int lasttime = 0;
	
	public Stackmat(DCTimer ct) {
		this.ct = ct;
	}
	
	public void start() {
		isStart = true;
		recorder = new RecordTask();
		recorder.execute();
	}
	
	public void stop() {
		isStart = false;
		isRecording = false;
	}
	
	public boolean creatAudioRecord(int sampRate) {
		try {
			int bufferSize = AudioRecord.getMinBufferSize(sampRate, channelConfig, audioEncoding);    
			new AudioRecord(MediaRecorder.AudioSource.MIC, sampRate, channelConfig, audioEncoding, bufferSize);
			samplingRate = sampRate;
			return true;
		} catch (Exception e) {
			samplingRate = 44100;
			return false;
		}
	}

//	private byte[] readPackage2(byte[] samples){
//		isValid = true; isMsec = false;
//		int offset = 0, sum = 0;
//    	byte[] data=new byte[9];
//    	for(int i=0; i<9; i++){
//    		data[i]=(byte) parseData(samples, i, inv);
//    		if(i==0 && !" ACILRS".contains(String.valueOf((char) data[i]))){
//    			isValid = false; return data;
//    		}
//    		if(i>0 && i<6){
//    			if(!Character.isDigit(data[i])){
//    				isValid = false; return data;
//    			}
//    			data[i] -= 48; sum += data[i];
//    		}
//    		if(i==6 && Character.isDigit(data[i])){
//    			offset = 1; isMsec = true; data[i] -= 48; sum += data[i];
//    		}
//    		if(i==6+offset && data[i]!=sum+64){
//    			isValid = false; return data;
//    		}
//    		if(i==7+offset && data[i]!= '\n'){
//    			isValid = false; return data;
//    		}
//    	}
//    	return data;
//    }
	private byte[] readPackage(byte[] samples) {
		isValid = true;
		int sum = 0;
    	byte[] data = new byte[9];
    	for(int i=0; i<9; i++) {
    		if(i>0 && i<7) {
    			data[i] = (byte) parseDigit(samples, i, inv);
    			sum += data[i];
    		}
    		else data[i] = (byte) parseData(samples, i, inv);
    	}
    	if(!" ACILRS".contains(String.valueOf((char) data[0]))) isValid = false;
    	//if(data[6]!=sum-data[7]+64 && data[7]!=sum+64) isValid = false;
    	if(data[7] == sum+64) isMsec = true;
    	if(data[7]!='\n' && data[8]!='\n') isValid = false;
    	return data;
    }
	private int parseData(byte[] periodData, int pos, boolean inv) {
		int temp = 0;
		for(int i = 1; i < 9; i++) temp |= periodData[pos * 10 + i] << (i - 1);
		return inv ? ~temp : temp;
	}
	private int parseDigit(byte[] periodData, int pos, boolean inv) {
		int temp = 0;
		for(int i = 1; i < 5; i++) temp |= periodData[pos * 10 + i] << (i - 1);
		return inv ? 15 - temp : temp;
	}
	
	class RecordTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			isRecording = true;
			state = 0;
			isMsec = false;
			try {
				newPeriod = samplingRate / 44;
				signalLengthPerBit = samplingRate * 36.75 / 44100;
				noiseSpikeThreshold = samplingRate * 25 / 44100;
				int bufferSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioEncoding);    
				record = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRate, channelConfig, audioEncoding, bufferSize);    
				short[] buffer = new short[bufferSize];
				record.startRecording();
				byte[] temp = new byte[90];
				int tlen = 0;
				byte sample = 0, lastSample = 0;
				byte lastBit = 0;
                int count = 0;
                
				while(isRecording) {
					bufferReadResult = record.read(buffer, 0, bufferSize);
					for(int c=0; c<bufferReadResult; c++) {
						sample = (byte) (buffer[c]>>8);
						if(count < newPeriod * 4) count++;
						else if(count == newPeriod * 4) {
							count++;
							publishProgress(-2);
							tlen = 0;
							state = 0;
							//Log.v("data", "off");
						}
						if(Math.abs(lastSample - sample) > switchThreshold && count > noiseSpikeThreshold) {
							if(count > newPeriod) {
								if(tlen < 1) {
									lastBit = bitValue(sample - lastSample);
									count = 0;
									continue;
								}
								if(tlen>88) {
									//readPackage2(temp);
									byte[] data = readPackage(temp);
									int time=(int)data[5]*10+data[4]*100+data[3]*1000+data[2]*10000+data[1]*60000+(isMsec?data[6]:0);
									if(isValid) {
	                					switch(state) {
	                					case 0:	//off
	                						if(time==0) state=1;
	                						else state=3;
	                						break;
	                					case 1:	//not ready
	                						lasttime = 0;
	                						if(time!=0) state = 2;
	                						break;
	                					case 2:	//running
	                						if(time==0) state=1;
	                						else if(time == lasttime) {
	                							lasttime = 0;
	                							publishProgress(-1, time);
	                							state = 3;
	                						}
	                						else lasttime = time;
	                						break;
	                					case 3:	//stop
	                						if(time==0) state=1;
	                						break;
	                					}
									}
									publishProgress(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2]*10+data[3]), Integer.valueOf(data[4]*10+data[5]), Integer.valueOf(data[6]));
									//System.out.println((char)data[0]+" "+data[1]+":"+data[2]+" "+data[3]+"."+data[4]+" "+data[5]);
								}
								tlen=0;
							}
							else {
								for(int i = 0; i < Math.round(count/signalLengthPerBit); i++)
									if(tlen<89)temp[tlen++]=lastBit;
							}
							lastBit = bitValue(sample - lastSample);
							count = 0;
						}
                    	lastSample=sample;
					}
				}
				record.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(Integer...pr) {
			if(pr[0]==-2) ct.tvTimer.setText("OFF");
			else if(pr[0]==-1) ct.confirmTime(pr[1]);
			else {
				if(pr[0]==65) ct.tvTimer.setTextColor(0xff00ff00);
				else if(pr[0]==67) ct.tvTimer.setTextColor(0xffff0000);
				else ct.tvTimer.setTextColor(ct.cl[1]);
				ct.tvTimer.setText((pr[1]==0?"":pr[1]+":")+((pr[1]>0 && pr[2]<10)?"0":"")+pr[2]+"."+(pr[3]<10?"0":"")+pr[3]+(isMsec?pr[4]:""));
			}
		}
		protected void onPostExecute(Void result) { }
		protected void onPreExecute() { }
	}
	private static byte bitValue(int x) {
		return (byte) ((x > 0) ? 1 : 0);
	}
}

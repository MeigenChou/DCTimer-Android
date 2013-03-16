package com.dctimer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class Stackmat {
	protected RecordTask recorder;
	public AudioRecord record;
	private boolean isRecording=false;
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;   
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 
	private int bufferReadResult;
	public static int samplingRate;
	private int switchThreshold = 50;
	private int noiseSpikeThreshold;
	private double signalLengthPerBit;
	private int newPeriod;
	public static boolean inv;
	public boolean isStart;
	private boolean isValid = false;
	private boolean isMsec = false;
	DCTimer ct;
	private byte state=0;	//0-off, 1-not ready, 2-running, 4-stop
	private int lasttime = 0;
	
	public Stackmat(DCTimer ct){
		this.ct=ct;
	}
	
	public void start(){
		isStart=true;
		recorder = new RecordTask();
		recorder.execute();
	}
	
	public void stop(){
		isStart=false;
		isRecording = false;
	}
	
	public boolean creatAudioRecord(int sampRate){
		try {
			int bufferSize = AudioRecord.getMinBufferSize(sampRate, channelConfig, audioEncoding);    
			new AudioRecord(MediaRecorder.AudioSource.MIC, sampRate, channelConfig, audioEncoding, bufferSize);
			samplingRate=sampRate;
			return true;
		} catch (Exception e) {
			samplingRate=44100;
			return false;
		}
	}

//	private void readPackage2(byte[] samples){
//		String s="";
//		for(int c=0;c<90;c++){
//			s+=samples[c];
//		}
//		Log.v("data", s);
//	}
	private byte[] readPackage(byte[] samples){
		isValid = true; isMsec = false;
		int offset = 0, sum = 0;
    	byte[] data=new byte[9];
    	for(int i=0; i<9; i++){
    		data[i]=(byte) parseData(samples, i, inv);
    		if(i==0 && !" ACILRS".contains(String.valueOf((char) data[i]))){
    			isValid = false; return data;
    		}
    		if(i>0 && i<6){
    			if(!Character.isDigit(data[i])){
    				isValid = false; return data;
    			}
    			data[i] -= 48; sum += data[i];
    		}
    		if(i==6 && Character.isDigit(data[i])){
    			offset = 1; isMsec = true; data[i] -= 48; sum += data[i];
    		}
    		if(i==6+offset && data[i]!=sum+64){
    			isValid = false; return data;
    		}
    		if(i==7+offset && data[i]!= '\n'){
    			isValid = false; return data;
    		}
    	}
    	return data;
    }
	private int parseData(byte[] periodData, int pos, boolean inv){
		int temp = 0;
		for(int i = 1; i < 9; i++) temp |= periodData[pos * 10 + i] << (i - 1);
		return inv ? ~temp : temp;
	}
	
	class RecordTask extends AsyncTask<Void, Integer, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			isRecording = true;
			state=0;
			try {
				int mul = samplingRate > 40000 ? 2 : 1;
				newPeriod = samplingRate / 44 / mul;
				signalLengthPerBit = samplingRate * 36.75 / 44100 / mul;
				noiseSpikeThreshold = samplingRate * 25 / 44100 / mul;
				int bufferSize = AudioRecord.getMinBufferSize(samplingRate, channelConfig, audioEncoding);    
				record = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRate, channelConfig, audioEncoding, bufferSize);    
				short[] buffer = new short[bufferSize];
				record.startRecording();
				byte[] temp=new byte[90];	//=new byte[230];
				int tlen=0;
				byte sample=0, lastSample=0;
				byte lastBit = 0;
                int count=0;
                
				while(isRecording){
					bufferReadResult = record.read(buffer, 0, bufferSize);
					for(int c=0; c < bufferReadResult; c+=mul){
						sample=(byte)(buffer[c]>>8);
						if(count < newPeriod * 4) count++;
						else if(count == newPeriod * 4){
							count++;
							publishProgress(-2);
							tlen=0;
							//Log.v("data", "off");
						}
						if(Math.abs(lastSample - sample) > switchThreshold && count > noiseSpikeThreshold) {
							if(count > newPeriod) {
								if(tlen < 1) {
									lastBit = bitValue(sample - lastSample);
									count = 0;
									continue;
								}
								if(tlen>88){
									//readPackage2(temp);
									byte[] data=readPackage(temp);
									if(isValid){
										//System.out.println(data[0]);
										int time=(int)data[5]*10+data[4]*100+data[3]*1000+data[2]*10000+data[1]*60000+(isMsec?data[6]:0);
	                					switch(state){
	                					case 0:	//off
	                						if(time==0)state=1;
	                						else state=3;
	                						break;
	                					case 1:	//not ready
	                						if(time>0 &&(data[0]==' ' || data[0]=='L' || data[0]=='R'))state=2;
	                						if(time>0 && (data[0]=='C' || data[0]=='S')){
	                							publishProgress(-1, time); state=3;
	                						}
	                						break;
	                					case 2:	//running
	                						if(time==0)state=1;
	                						if(data[0]=='C' || data[0]=='S'){
	                							if(time == lasttime) {
	                								lasttime = 0;
	                								publishProgress(-1, time);
	                								state=3;
	                							}
	                							else lasttime = time;
	                						}
	                						break;
	                					case 3:	//stop
	                						if(time==0)state=1;
	                						break;
	                					}
										publishProgress(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2]*10+data[3]), Integer.valueOf(data[4]*10+data[5]), Integer.valueOf(data[6]));
									}
									//Log.v("data", data[1]+":"+data[2]+" "+data[3]+"."+data[4]+" "+data[5]);
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
//						if(mode){
//							temp[tlen++]=bitValue(sample);
//                			if(tlen>100){
//                				byte[] data = readPackage(temp);
//                				if(isValid){
//                					int time=(int)data[5]*10+data[4]*100+data[3]*1000+data[2]*10000+data[1]*60000+(pro?data[6]:0);
//                					
//                					publishProgress((int)data[0], (int)data[1], (int)data[2], (int)data[3], (int)data[4], (int)data[5], (int)data[6]);
//                					//Log.v("data", data[1]+":"+data[2]+data[3]+"."+data[4]+data[5]);
//                				}
//                				tlen=0; mode=false;
//                			}
//						} else if(Math.abs(sample - lastSample) < 5) {
//                    		count++;
//                    	} else {
//                    		if(count>33)mode=true; count=0;
//                    	}
                    	lastSample=sample;
//						offset+=period;
//						c=(int)(offset);
//						if(offset>=bufferReadResult)offset-=bufferReadResult;
					}
				}
				record.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onProgressUpdate(Integer...pr){
			if(pr[0]==-2)ct.tvTimer.setText("OFF");
			else if(pr[0]==-1){
				ct.newScr(false);
				ct.confirmTime(pr[1]);
			}
			else {
				if(pr[0]==65)ct.tvTimer.setTextColor(0xff00ff00);
				else if(pr[0]==67)ct.tvTimer.setTextColor(0xffff0000);
				else ct.tvTimer.setTextColor(ct.cl[1]);
				ct.tvTimer.setText((pr[1]==0?"":pr[1]+":")+((pr[1]>0 && pr[2]<10)?"0":"")+pr[2]+"."+(pr[3]<10?"0":"")+pr[3]+(isMsec?pr[4]:""));
			}
		}
		protected void onPostExecute(Void result){

		}
		protected void onPreExecute(){

		}
	}
	private static byte bitValue(int x) {
		return (byte) ((x > 0) ? 1 : 0);
	}
}

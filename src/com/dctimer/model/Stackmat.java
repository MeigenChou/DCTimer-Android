package com.dctimer.model;

import com.dctimer.Configs;
import com.dctimer.DCTimer;

import android.media.*;
import android.os.AsyncTask;

public class Stackmat {
	private RecordTask recorder;
	public AudioRecord record;
	private boolean isRecording;
	private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT; 
	public static int samplingRate = 44100;
	//public static int switchThreshold = 50;
	private int noiseSpikeThreshold;
	private double signalLengthPerBit;
	private int newPeriod;
	private int invSign;
	private boolean milliTime;
	DCTimer dct;
	private byte state;	//0-off, 1-not ready, 2-running, 3-stop
	private int lasttime = 0;

	public Stackmat(DCTimer dct) {
		this.dct = dct;
		newPeriod = samplingRate / 44;
		signalLengthPerBit = samplingRate / 1200.0;
		noiseSpikeThreshold = samplingRate * 25 / 44100;
	}

	public void start() {
		recorder = new RecordTask();
		recorder.execute();
	}

	public void stop() {
		isRecording = false;
	}

	class RecordTask extends AsyncTask<Void, Integer, Void> {
		private int bufferLen;
		@Override
		protected Void doInBackground(Void... params) {
			isRecording = true;
			state = 0;
			invSign = -1;
			try {
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
					bufferLen = record.read(buffer, 0, bufferSize);
					for(int c=0; c<bufferLen; c++) {
						sample = (byte) (buffer[c] >> 8);
						if(count < newPeriod * 4) count++;
						else if(count == newPeriod * 4) {
							count++;
							publishProgress(-2);
							tlen = 0;
							state = 0;
							System.out.println("off");
						}
						if(Math.abs(lastSample - sample) > Configs.switchThreshold && count > noiseSpikeThreshold) {
							if(count > newPeriod) {
								if(tlen < 1) {
									lastBit = bitValue(sample - lastSample);
									count = 0;
									continue;
								}
								if(tlen > 88) {
									//print(temp);
									byte[] data = readPackage(temp);
									if(data != null) {
										int time = (data[1]-48)*60000 + (data[2]-48)*10000 + (data[3]-48)*1000
												+ (data[4]-48)*100 + (data[5]-48)*10 + (milliTime ? data[6]-48 : 0);
										switch (state) {
										case 0:	//off
											if(time == 0) state = 1;
	                						else state = 3;
	                						break;
										case 1:	//not ready
											lasttime = 0;
	                						if(time != 0) state = 2;
	                						break;
										case 2:	//running
											if(time == 0) state = 1;
	                						else if(time == lasttime) {
	                							lasttime = 0;
	                							publishProgress(-1, time);
	                							state = 3;
	                						} else lasttime = time;
	                						break;
										case 3:	//stop
											if(time==0) state = 1;
	                						break;
										}
										publishProgress((int)data[0], data[1] - 48, data[2] - 48, data[3] - 48,
												data[4] - 48, data[5] - 48, data[6] - 48);
									};
								}
								tlen = 0;
							} else for(int i = 0; i < Math.round(count/signalLengthPerBit); i++) {
								if(tlen < 89) temp[tlen++] = lastBit;
								else break;
							}
							lastBit = bitValue(sample - lastSample);
							count = 0;
						}
						lastSample = sample;
					}
				}
				record.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if(values[0] == -2) dct.setTimerText("OFF");
			else if(values[0] == -1) dct.confirmTime(values[1]);
			else {
				if(values[0] == 'A') dct.setTimerColor(0xff00ff00);
				else if(values[0] == 'C') dct.setTimerColor(0xffff0000);
				else dct.setTimerColor(Configs.colors[1]);
				StringBuilder sb = new StringBuilder();
				if(values[1] > 0) sb.append(values[1] + ":");
				if(values[1] > 0 || values[2] > 0) sb.append(values[2]);
				sb.append(values[3] + "." + values[4] + values[5]);
				if(milliTime) sb.append(values[6]);
				dct.setTimerText(sb.toString());
			}
		}
		
		private byte[] readPackage(byte[] samples) {
			int sum = 64;
	    	byte[] data = new byte[9];
	    	if(invSign == -1) {
	    		invSign = 0;
	    		for(int i=1; i<6; i++) {
	    			data[i] = (byte) parseData(samples, i, false);
	    			if(data[i] < '0' || data[i] > '9') {
	    				invSign = -1;
	    				break;
	    			}
	    			sum += data[i] - '0';
	    		}
	    		if(invSign == -1) {
	    			invSign = 1;
	    			for(int i=1; i<6; i++) {
	    				data[i] = (byte) parseData(samples, i, true);
	    				if(data[i] < '0' || data[i] > '9') {
	        				invSign = -1;
	        				return null;
	        			}
	    				sum += data[i] - '0';
	    			}
	    		}
	    		data[6] = (byte) parseData(samples, 6, invSign == 1);
				data[7] = (byte) parseData(samples, 7, invSign == 1);
				if(sum == data[6]) milliTime = false;
				else if(sum + data[6] - 48 == data[7]) milliTime = true;
				else {
					invSign = -1;
					return null;
				}
	    	} else {
	    		for(int i=0; i<9; i++) {
	    			data[i] = (byte) parseData(samples, i, invSign == 1);
	    			if((i > 0 && i < 6) || (milliTime && i == 6)) {
	    				if(data[i] < '0' || data[i] > '9')
	    					return null;
	    				sum += data[i] - '0';
	    			}
	    		}
	    		if(!milliTime && sum != data[6]) return null;
	    		if(milliTime && sum != data[7]) return null;
	    	}
	    	byte head = data[0];
	    	if(head != ' ' && head != 'A' && head != 'C' && head != 'I'
	    			&& head != 'L' && head != 'R' && head != 'S') return null;
	    	//if(!milliTime && data[7] != '\n') return null;
	    	//if(milliTime && data[8]!='\n') return null;
	    	return data;
	    }
		
		private int parseData(byte[] periodData, int pos, boolean inv) {
			int temp = 0;
			for(int i = 0; i < 8; i++) temp |= periodData[pos * 10 + i + 1] << i;
			return inv ? ~temp : temp;
		}

		private byte bitValue(int x) {
			return (byte) ((x > 0) ? 1 : 0);
		}
		
//		private void print(byte[] signal) {
//			StringBuilder sb = new StringBuilder();
//			for(int i=0; i<70; i++) {
//				sb.append(signal[i]);
//				if(i%10 == 9) sb.append(' ');
//			}
//			System.out.println(sb.toString());
//		}
	}
}

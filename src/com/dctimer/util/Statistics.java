package com.dctimer.util;

import static com.dctimer.Configs.stSel;

import java.util.ArrayList;
import java.util.Arrays;

import com.dctimer.Configs;
import com.dctimer.db.Session;

public class Statistics {
	public static int[] bestAvg = {0, 0};
	public static int[] bestIdx = {0, 0};
	public static int mean = -1;
	public static int sd;
	public static int minIdx, maxIdx;
	public static int solved;
	
	public static String mean(int n, int i, int l) {
		if(i<n-1) { bestIdx[l] = -1; return "N/A"; }
		double sum = 0;
		for(int j=i-n+1; j<=i; j++)
			if(Session.penalty[j] == 2) {
				if(i < n) bestAvg[l] = Integer.MAX_VALUE;
				return "DNF";
			} else {
				int time = Session.getTime(j);
				if(Configs.stSel[2] == 0) time /= 10;
				sum += time;
			}
		int mean = (int) (sum / n + 0.5);
		if(Configs.stSel[2] == 0) mean *= 10;
		if(i == n - 1) { bestAvg[l] = mean; bestIdx[l] = i; }
		if(mean <= bestAvg[l]) { bestAvg[l] = mean; bestIdx[l] = i; }
		return timeToString(mean);
	}
	
	public static String average(int n, int i, int l) {
		if(i<n-1) { bestIdx[l] = -1; return "N/A"; }
		int nDnf = 0, avg;
		int trim = (int) Math.ceil(n / 20.0);
		double sum = 0;
		
		if(n <= 20) {
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			for (int j=i-n+1; j<=i; j++)
				if(Session.penalty[j] == 2) {
					nDnf++;
					if(nDnf > trim) {
						if(i < n) bestAvg[l] = Integer.MAX_VALUE;
						return "DNF";
					}
				} else {
					int time = Session.getTime(j);
					if(time > max) max = time;
					if(time < min) min = time;
					if(Configs.stSel[2] == 0) time /= 10;
					sum += time;
				}
			if(nDnf != 0) max = 0;
			if(Configs.stSel[2] == 0) {
				min /= 10;
				max /= 10;
			}
			sum -= min + max;
			avg = (int) (sum / (n - 2) + 0.5);
		} else {
			int[] max = new int[trim];
			int[] min = new int[trim];
			int size = 0;
			for(int j=i-n+1; j<=i; j++) {
				if(Session.penalty[j] == 2) {
					nDnf++;
					if(nDnf > trim) {
						if(i < n) bestAvg[l] = Integer.MAX_VALUE;
						return "DNF";
					}
					if(size < trim) {
						Utils.addMinQueue(min, Integer.MAX_VALUE, size);
						Utils.addMaxQueue(max, Integer.MAX_VALUE, size++);
					} else if(max[0] < Integer.MAX_VALUE) {
						Utils.pollMax(max, trim);
						Utils.addMaxQueue(max, Integer.MAX_VALUE, trim-1);
					}
				} else {
					int time = Session.getTime(j);
					if(Configs.stSel[2] == 0) time /= 10;
					sum += time;
					if(size < trim) {
						Utils.addMinQueue(min, time, size);
						Utils.addMaxQueue(max, time, size++);
					} else {
						if(time < min[0]) {
							Utils.pollMin(min, trim);
							Utils.addMinQueue(min, time, trim-1);
						}
						if(time > max[0]) {
							Utils.pollMax(max, trim);
							Utils.addMaxQueue(max, time, trim-1);
						}
					}
				}
			}
			for(int j=0; j<trim; j++) {
				sum -= min[j];
				if(max[j] != Integer.MAX_VALUE) sum -= max[j];
			}
			avg = (int) (sum / (n - 2 * trim) + 0.5);
		}
		if(Configs.stSel[2] == 0) avg *= 10;
		if(i == n - 1) { bestAvg[l] = avg; bestIdx[l] = i; }
		if(avg <= bestAvg[l]) { bestAvg[l] = avg; bestIdx[l] = i; }
		return timeToString(avg);
	}
	
	public static String sessionMean() {
		long sum = 0;
		double sum2 = 0;
		maxIdx = minIdx = mean = -1;
		solved = Session.length;
		if(solved == 0) return "0/0): N/A (N/A)";
		for(int i=0; i<Session.length; i++) {
			if(Session.penalty[i] == 2) solved--;
			else {
				int time = Session.getTime(i);
				if(maxIdx == -1) maxIdx = i;
				else if(time > Session.getTime(maxIdx)) maxIdx = i;
				if(minIdx == -1) minIdx = i;
				else if(time <= Session.getTime(minIdx)) minIdx = i;
				if(Configs.stSel[2] == 0) time /= 10;
				sum += time;
				sum2 += (long)time * time;
			}
		}
		if(solved == 0) return "0/" + Session.length + "): N/A (N/A)";
		mean = (int) (sum / solved + 0.5);
		if(Configs.stSel[2] == 0) mean *= 10;
		sd = (int) (Math.sqrt((sum2-sum*sum/solved)/solved) + 0.5);
		return solved + "/" + Session.length + "): " + timeToString(mean) + " (" + standardDeviation(sd) + ")";
	}
	
	public static String sessionAvg() {
		int n = Session.length;
		if(n < 3) return "N/A";
		int[] data = new int[n];
		int count = 0;
		int trim = (int) Math.ceil(n / 20.0);
		for(int i=0; i<Session.length; i++) {
			if(Session.penalty[i] == 2) {
				n--;
				if(n < Session.length - trim) return "DNF";
			} else data[count++] = Session.getTime(i);
		}
		long sum = 0;
		double sum2 = 0;
		Arrays.sort(data);
		for(int j=trim; j<Session.length-trim; j++) {
			if(Configs.stSel[2] == 0) data[j] /= 10;
			sum += data[j];
			sum2 += (long)data[j] * data[j];
		}
		int num = Session.length - 2 * trim;
		int savg = (int) (sum / num + 0.5);
		if(Configs.stSel[2] == 0) savg *= 10;
		int ssd = (int) (Math.sqrt((sum2-sum*sum/num)/num) + 0.5);
		return timeToString(savg) + " (¦Ò = " + standardDeviation(ssd) + ")";
	}
	
	public static String[] getAvgDetail(int nSolves, int idx, ArrayList<Integer> ary) {
		int cavg = 0, csdv = -1;
		int trim = (int) Math.ceil(nSolves/20.0);
		int max, min;
		ArrayList<Integer> dnfIdx = new ArrayList<Integer>();
		for(int j=idx-nSolves+1; j<=idx; j++)
			if(Session.penalty[j] == 2)
				dnfIdx.add(j);
		int dnf = dnfIdx.size();
		long[] data = new long[nSolves - dnf];
		int len = 0;
		for(int j=idx-nSolves+1; j<=idx; j++)
			if(Session.penalty[j] != 2) {
				data[len++] = (long)Session.getTime(j) << 32 | j;
			}
		Arrays.sort(data);
		if(nSolves-dnf >= trim) {
			for(int j=0; j<trim; j++) ary.add((int)data[j]);
		} else {
			for(int j=0; j<data.length; j++) ary.add((int)data[j]);
			for(int j=0; j<trim-nSolves+dnf; j++) ary.add(dnfIdx.get(j));
		}
		boolean m = dnf > trim;
		min = ary.get(0);
		if(m) {
			for(int j=dnf-trim; j<dnf; j++) ary.add(dnfIdx.get(j));
		} else {
			for(int j=nSolves-trim; j<nSolves-dnf; j++) ary.add((int)data[j]);
			for(int j=0; j<dnf; j++) ary.add(dnfIdx.get(j));
			double sum = 0, sum2 = 0;
			for(int j=trim; j<nSolves-trim; j++) {
				data[j] >>= 32;
				if(stSel[2] == 0) data[j] /= 10;
				sum += data[j];
				sum2 += (double)data[j] * data[j];
			}
			int num = nSolves-trim*2;
			cavg = (int) (sum/num+0.5);
			csdv = (int) (Math.sqrt((sum2-sum*sum/num)/num)+0.5);
			if(stSel[2] == 0) cavg *= 10;
		}
		max = ary.get(ary.size() - 1);
		return new String[] {m?"DNF":timeToString(cavg), standardDeviation(csdv),
				timeAt(min, false), timeAt(max, false)};
	}
	
	public static String[] getMeanDetail(int n, int i) {
		int max, min, dnf = 0;
		int cavg = 0, csdv = -1;
		double sum = 0, sum2 = 0;
		max = min = i-n+1;
		boolean m = false;
		for(int j=i-n+1; j<=i; j++) {
			if(Session.penalty[j] != 2 && !m) { min = j; m = true; }
			if(Session.penalty[j] == 2) { max = j; dnf++; }
		}
		m = dnf > 0;
		if(!m) {
			for (int j=i-n+1; j<=i; j++) {
				int time = Session.getTime(j);
				if(time > Session.getTime(max)) max = j;
				if(time <= Session.getTime(min)) min = j;
				if(stSel[2] == 0) time /= 10;
				sum += time;
				sum2 += (long)time * time;
			}
			cavg = (int) (sum/n+0.5);
			csdv = (int) (Math.sqrt((sum2-sum*sum/n)/n)+0.5);
		}
		if(stSel[2] == 0) cavg *= 10;
		return new String[] {m ? "DNF" : timeToString(cavg), standardDeviation(csdv),
				timeAt(min, false), timeAt(max, false)};
	}
	
	public static String mulMean(int p) {
		long sum = 0;
		int n = 0;
		if(n == Session.length) return "-";
		for(int i=0; i<Session.length; i++) {
			int time = Session.mulp[p][i];
			if(time != 0) {
				if(Configs.stSel[2] == 0) time /= 10;
				sum += time;
				n++;
			}
		}
		if(n == 0) return "-";
		int m = (int) (sum / n + 0.5);
		if(Configs.stSel[2] == 0) m *= 10;
		return timeToString(m);
	}
	
	public static String standardDeviation(int i) {
		if(i < 0) return "N/A";
		if(Configs.stSel[2] == 1) i = (i + 5) / 10;
		StringBuffer s = new StringBuffer(i/100+".");
		s.append(String.format("%02d", i%100));
		return s.toString();
	}

	static String timeToString(int hour, int min, int sec, int msec) {
		StringBuilder time = new StringBuilder();
		if(hour == 0) {
			if(min==0) time.append(""+sec);
			else {
				if(sec<10)time.append(""+min+":0"+sec);
				else time.append(""+min+":"+sec);
			}
		}
		else {
			time.append(""+hour);
			if(min<10)time.append(":0"+min);
			else time.append(":"+min);
			if(sec<10)time.append(":0"+sec);
			else time.append(":"+sec);
		}
		if(Configs.stSel[2]==1) {
			if(msec<10)time.append(".00"+msec);
			else if(msec<100)time.append(".0"+msec);
			else time.append("."+msec);}
		else {
			if(msec<10)time.append(".0"+msec);
			else time.append("."+msec);
		}
		return time.toString();
	}
	
	public static String timeToString(int t) {
		boolean m = t < 0;
		if(m) t = -t;
		int msec = t % 1000;
		if(Configs.stSel[2] == 0) msec /= 10;
		int sec = t / 1000, min = 0, hour = 0;//DCTimer.clkform?(i/1000)%60:i/1000;
		if(Configs.stSel[13] < 2) {
			min = sec / 60;
			sec %= 60;
			if(Configs.stSel[13] < 1) {
				hour = min / 60;
				min %= 60;
			}
		}
		return (m ? "-" : "") + timeToString(hour, min, sec, msec);
	}
	
	public static String timeAt(int idx, boolean showTime) {
		if(idx < 0) return "N/A";
		if(idx >= Session.length) return "";
		int i = Session.result[idx];
		if(Session.penalty[idx] == 2) {
			if(showTime) return "DNF (" + timeToString(i) + ")";
			else return "DNF";
		}
		else if(Session.penalty[idx] == 1)
			return timeToString(i + 2000) + "+";
		else return timeToString(i);
	}
}

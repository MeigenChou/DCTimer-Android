package com.dctimer.util;

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
	
	public static String average(int type, int n, int i, int l) {
		if(i<n-1) { bestIdx[l] =- 1; return "N/A"; }
		int nDnf = 0, cavg;
		int trim = type==1 ? 0 : (int) Math.ceil(n/20.0);
		long sum = 0;
		for(int j=i-n+1; j<=i; j++)
			if(Session.penalty[j] == 2) {
				nDnf++;
				if(nDnf > trim) {
					cavg = Integer.MAX_VALUE;
					if(i < n) bestAvg[l] = Integer.MAX_VALUE;
					return "DNF";
				}
			} else if(type == 1) {
				int time = Session.getTime(j);
				if(Configs.stSel[2] == 0) time /= 10;
				sum += time;
			}
		if(type == 1) {
			cavg = (int) (sum / n + 0.5);
			if(Configs.stSel[2] == 0) cavg *= 10;
			if(i == n - 1) { bestAvg[l] = cavg; bestIdx[l] = i; }
			if(cavg <= bestAvg[l]) { bestAvg[l] = cavg; bestIdx[l] = i; }
			return distime(cavg);
		}
		if(n < 20) {
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			for (int j=i-n+1; j<=i; j++)
				if(Session.penalty[j] != 2) {
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
			cavg = (int) (sum / (n - 2) + 0.5);
		} else {
			int[] data = new int[n - nDnf];
			int len = 0;
			for(int j=i-n+1; j<=i; j++)
				if(Session.penalty[j] != 2)
					data[len++] = Session.getTime(j);
			Arrays.sort(data);
			for(int j=trim; j<n-trim; j++) {
				if(Configs.stSel[2] == 0) data[j] /= 10;
				sum += data[j];
			}
			cavg = (int) (sum / (n - 2 * trim) + 0.5);
		}
		if(Configs.stSel[2] == 0) cavg *= 10;
		if(i == n - 1) { bestAvg[l] = cavg; bestIdx[l] = i; }
		if(cavg <= bestAvg[l]) { bestAvg[l] = cavg; bestIdx[l] = i; }
		return distime(cavg);
	}
	
	public static String sesMean() {
		long sum = 0;
		double sum2 = 0;
		maxIdx = minIdx = mean = -1;
		solved = Session.resl;
		if(solved == 0) return "0/0): N/A (N/A)";
		for(int i=0; i<Session.resl; i++) {
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
		if(solved == 0) return "0/" + Session.resl + "): N/A (N/A)";
		mean = (int) (sum / solved + 0.5);
		if(Configs.stSel[2] == 0) mean *= 10;
		sd = (int) (Math.sqrt((sum2-sum*sum/solved)/solved) + 0.5);
		return solved + "/" + Session.resl + "): " + distime(mean) + " (" + standardDeviation(sd) + ")";
	}
	
	public static String sesAvg() {
		int n = Session.resl;
		if(n < 3) return "N/A";
		int[] data = new int[n];
		int count = 0;
		int trim = (int) Math.ceil(n / 20.0);
		for(int i=0; i<Session.resl; i++) {
			if(Session.penalty[i] == 2) {
				n--;
				if(n < Session.resl - trim) return "DNF";
			} else data[count++] = Session.getTime(i);
		}
		long sum = 0;
		double sum2 = 0;
		Arrays.sort(data);
		for(int j=trim; j<Session.resl-trim; j++) {
			if(Configs.stSel[2] == 0) data[j] /= 10;
			sum += data[j];
			sum2 += (long)data[j] * data[j];
		}
		int num = Session.resl - 2 * trim;
		int savg = (int) (sum / num + 0.5);
		if(Configs.stSel[2] == 0) savg *= 10;
		int ssd = (int) (Math.sqrt((sum2-sum*sum/num)/num) + 0.5);
		return distime(savg) + " (¦Ò = " + standardDeviation(ssd) + ")";
	}
	
	public static String mulMean(int p) {
		long sum = 0;
		int n = 0;
		if(n == Session.resl) return "-";
		for(int i=0; i<Session.resl; i++) {
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
		return distime(m);
	}
	
	public static String standardDeviation(int i) {
		if(i < 0) return "N/A";
		if(Configs.stSel[2] == 1) i = (i + 5) / 10;
		StringBuffer s = new StringBuffer(i/100+".");
		s.append(String.format("%02d", i%100));
		return s.toString();
	}

	static String contime(int hour, int min, int sec, int msec) {
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
	
	public static String distime(int i) {
		boolean m = i < 0;
		if(m) i = -i;
		//if(i==0)return "DNF";
		//if(DCTimer.stSel[2]==0)i+=5;
		int msec = i % 1000;
		if(Configs.stSel[2] == 0) msec /= 10;
		int sec = i / 1000, min = 0, hour = 0;//DCTimer.clkform?(i/1000)%60:i/1000;
		if(Configs.stSel[13] < 2) {
			min = sec / 60;
			sec %= 60;
			if(Configs.stSel[13] < 1) {
				hour = min / 60;
				min %= 60;
			}
		}
		return (m ? "-" : "") + contime(hour, min, sec, msec);
	}
	
	public static String distime(int idx, boolean showTime) {
		if(idx < 0) return "N/A";
		if(idx >= Session.resl) return "";
		int i = Session.result[idx];
		if(Session.penalty[idx] == 2) {
			if(showTime) return "DNF (" + distime(i) + ")";
			else return "DNF";
		}
		else if(Session.penalty[idx] == 1)
			return distime(i + 2000) + "+";
		else return distime(i);
	}
}

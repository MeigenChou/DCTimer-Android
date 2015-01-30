package com.dctimer.db;

import java.util.Arrays;

import com.dctimer.DCTimer;

public class Statistics {
	public static int[] bavg = {0, 0};
	public static int[] bidx = {0, 0};
	public static int sesMean = -1;
	public static int sesSD;
	public static int minIdx, maxIdx;
	public static int solved;
	
	public static String average(int type, int n, int i, int l) {
		if(i<n-1) {bidx[l]=-1; return "N/A";}
		int nDnf = 0, cavg;
		int trim = type==1 ? 0 : (int) Math.ceil(n/20.0);
		double sum = 0;
		for(int j=i-n+1; j<=i; j++)
			if(Session.resp[j]==2) {
				nDnf++;
				if(nDnf>trim) {
					cavg=Integer.MAX_VALUE;
					if(i<n)bavg[l]=Integer.MAX_VALUE;
					return "DNF";
				}
			} else if(type == 1) {
				int time = Session.rest[j]+Session.resp[j]*2000;
				if(DCTimer.stSel[2]==1)sum += time;
				else sum+=time/10;
			}
		if(type == 1) {
			cavg=(int) (sum/n+0.5);
			if(DCTimer.stSel[2]==0)cavg*=10;
			if(i==n-1) {bavg[l]=cavg;bidx[l]=i;}
			if(cavg<=bavg[l]) {bavg[l]=cavg;bidx[l]=i;}
			return distime(cavg);
		}
		if(n<20) {
			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;
			for (int j=i-n+1;j<=i;j++)
				if(Session.resp[j]!=2) {
					int time = Session.rest[j]+Session.resp[j]*2000;
					if(time>max) max = time;
					if(time<min) min = time;
					if(DCTimer.stSel[2]==1) sum+=time;
					else sum+=time/10;
				}
			if(nDnf!=0) max = 0;
			if(DCTimer.stSel[2]==1)sum-=min+max;
			else sum-=min/10+max/10;
			cavg=(int) (sum/(n-2)+0.5);
		}
		else {
			int[] data=new int[n-nDnf];
			int len=0;
			for(int j=i-n+1;j<=i;j++)
				if(Session.resp[j]!=2) data[len++]=Session.rest[j]+Session.resp[j]*2000;
			quickSort(data, 0, n-nDnf-1);
			for(int j=trim;j<n-trim;j++) {
				if(DCTimer.stSel[2]==1)sum+=data[j];
				else sum+=data[j]/10;
			}
			cavg=(int) (sum/(n-2*trim)+0.5);
		}
		if(DCTimer.stSel[2]==0)cavg*=10;
		if(i==n-1) {bavg[l]=cavg;bidx[l]=i;}
		if(cavg<=bavg[l]) {bavg[l]=cavg;bidx[l]=i;}
		return distime(cavg);
	}
	
	private static void quickSort(int[] a, int lo, int hi) {
		if(lo >= hi) return;
		int pivot = a[lo], i = lo, j = hi;
		while(i < j) {
			while(i<j && a[j]>=pivot) j--;
			a[i] = a[j];
			while(i<j && a[i]<=pivot) i++;
			a[j] = a[i];
		}
		a[i] = pivot;
		quickSort(a, lo, i-1);
		quickSort(a, i+1, hi);
	}
	
	public static String sesMean() {
		double sum = 0, sum2 = 0;
		maxIdx=-1; minIdx=-1; sesMean=-1;
		solved = Session.resl;
		if(solved==0) return "0/0): N/A (N/A)";
		for(int i=0; i<Session.resl; i++) {
			if(Session.resp[i]==2) solved--;
			else {
				int time = Session.rest[i]+Session.resp[i]*2000;
				if(maxIdx==-1)maxIdx=i;
				else if(time>Session.rest[maxIdx]+Session.resp[maxIdx]*2000)maxIdx=i;
				if(minIdx==-1)minIdx=i;
				else if(time<=Session.rest[minIdx]+Session.resp[minIdx]*2000)minIdx=i;
				if(DCTimer.stSel[2]==1)sum+=time;
				else sum+=time/10;
				if(DCTimer.stSel[2]==1)sum2+=Math.pow(time, 2);
				else sum2+=Math.pow(time/10, 2);
			}
		}
		if(solved == 0) return "0/" + Session.resl + "): N/A (N/A)";
		sesMean = (int)(sum/solved+0.5);
		if(DCTimer.stSel[2] == 0) sesMean *= 10;
		sesSD = (int)(Math.sqrt((sum2-sum*sum/solved)/solved)+(DCTimer.stSel[2]==1?0:0.5));
		return solved+"/"+Session.resl+"): "+distime(sesMean)+" ("+standDev(sesSD)+")";
	}
	
	public static String sesAvg() {
		int n = Session.resl;
		if(n < 3) return "N/A";
		int[] data = new int[n];
		int count = 0;
		int trim = (int) Math.ceil(n / 20.0);
		for(int i=0; i<Session.resl; i++) {
			if(Session.resp[i] == 2) {
				n--;
				if(n < Session.resl-trim) return "DNF";
			}
			else data[count++] = Session.rest[i] + Session.resp[i] * 2000;
		}
		double sum = 0, sum2 = 0;
		Arrays.sort(data);
		//heapsort(data, n);
		for(int j=trim; j<Session.resl-trim; j++) {
			if(DCTimer.stSel[2] == 1) sum += data[j];
			else sum += data[j] / 10;
			if(DCTimer.stSel[2] == 1) sum2 += Math.pow(data[j], 2);
			else sum2 += Math.pow(data[j] / 10, 2);
		}
		int num = Session.resl - 2 * trim;
		int savg = (int) (sum / num + 0.5);
		if(DCTimer.stSel[2] == 0) savg *= 10;
		int ssd = (int)(Math.sqrt((sum2-sum*sum/num)/num)+(DCTimer.stSel[2]==1?0:0.5));
		return distime(savg) + " (σ = " + standDev(ssd) + ")";
	}
	
//	static void adjust(int[] num, int s, int t) {
//	int i = s;
//	int x = num[s];
//	for (int j = 2 * i; j <= t; j = 2 * j) {
//		if (j < t && num[j] < num[j + 1])
//			j = j + 1;// 找出较大者把较大者给num[i]
//		if (x > num[j])
//			break;
//		num[i] = num[j];
//		i = j;
//	}
//	num[i] = x;
//}
//static void heapsort(int[] num, int n) {
//	// 初始建堆从n/2开始向根调整
//	int i;
//	for (i = n / 2; i >= 1; i--) {
//		adjust(num, i, n);//初始堆过程
//	}
//	for (i = n; i > 1; i--) {
//		num[0] = num[i];// 将堆顶元素与第n,n-1,.....2个元素相交换
//		num[i] = num[1];
//		num[1] = num[0];// 从num[1]到num[i-1]调整成新堆
//		adjust(num, 1, i - 1);
//	}
//}
	
	public static String mulMean(int p) {
		double sum=0;
		int n=0;
		if(n==Session.resl)return "-";
		for(int i=0;i<Session.resl;i++) {
			if(Session.mulp[p][i]!=0) {
				if(DCTimer.stSel[2]==1)sum+=(double)Session.mulp[p][i];
				else sum+=Session.mulp[p][i]/10;
				n++;
			}
		}
		if(n==0)return "-";
		int m=(int)(sum/n+0.5);
		if(DCTimer.stSel[2]==0)m*=10;
		return distime(m);
	}
	
	public static String standDev(int i) {
		if(i<0)return "N/A";
		if(DCTimer.stSel[2]==1)i=(i+5)/10;
		StringBuffer s=new StringBuffer(i/100+".");
		if(i%100<10)s.append("0");
		s.append(""+i%100);
		return s.toString();
	}

	static String contime(int hour, int min, int sec, int msec) {
		StringBuilder time=new StringBuilder();
		if(hour==0) {
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
		if(DCTimer.stSel[2]==1) {
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
		if(DCTimer.stSel[2] == 0) msec /= 10;
		int sec = i / 1000, min = 0, hour = 0;//DCTimer.clkform?(i/1000)%60:i/1000;
		if(DCTimer.stSel[13] < 2) {
			min = sec / 60;
			sec %= 60;
			if(DCTimer.stSel[13] < 1) {
				hour = min / 60;
				min %= 60;
			}
		}
		return (m ? "-" : "") + contime(hour, min, sec, msec);
	}
	
	public static String distime(int idx, boolean b) {
		if(idx<0)return "N/A";
		if(idx >= Session.resl) return "";
		int i = Session.rest[idx];
		if(Session.resp[idx]==2) {
			if(b)return "DNF ("+distime(i)+")";
			else return "DNF";
		}
		else if(Session.resp[idx]==1)
			return distime(i+2000)+"+";
		else return distime(i);
	}
}

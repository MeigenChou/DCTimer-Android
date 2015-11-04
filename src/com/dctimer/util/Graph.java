package com.dctimer.util;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

import com.dctimer.Configs;
import com.dctimer.db.Session;
import com.dctimer.util.Statistics;

public class Graph {
	private static int getDivision(int dv) {
		int[] ds = {100, 200, 500, 1000, 2000, 5000, 10000, 20000, 30000, 60000, 90000, 120000, 300000, 600000, 1200000, 1800000, 3600000};
		if(dv < ds[0]) return 100;
		for(int i=1; i<17; i++)
			if(dv < ds[i]) return ds[i];
		return (dv/1000+1)*1000;
	}
	
	private static String distime(int i) {
		boolean m = i < 0;
		i = Math.abs(i) + 5;
		int ms = (i % 1000) / 100;
		int s = i / 1000;
		int mi = 0, h = 0;
		if(Configs.stSel[13] < 2) {
			mi = s / 60;
			s %= 60;
			if(Configs.stSel[13] < 1) {
				h = mi / 60;
				mi %= 60;
			}
		}
		return (m?"-":"")+(h>0?h+":":"")+(h>0?(mi<10?"0"+mi+":":mi+":"):(mi>0?mi+":":""))+(((h>0 || mi>0) && s<10)?"0":"")+s+"."+ms;
	}
	
	public static void drawHist(int width, Paint p, Canvas c) {
		int[] bins = new int[14];
		int start;
		int end;
		if(Session.length==0 || Statistics.minIdx==-1 || Statistics.maxIdx==-1) {
			start = 13000;
			end = 27000;
		} else {
			int max = Session.getTime(Statistics.maxIdx);
			int min = Session.getTime(Statistics.minIdx);
			int divi = getDivision((max - min) / 14);
			int mean = (min + max) >>> 1;
			mean = ((mean + divi / 2) / divi) * divi;
			start = mean - divi * 7;
			end = mean + divi * 7;
		}
		for (int i = 0; i < bins.length; i++)
			bins[i] = 0;
		for (int i = 0; i < Session.length; i++) {
			if(Session.penalty[i]!=2) {
				int time = Session.getTime(i);
				if(time >= start && time < end) {
					int bin = (int) (bins.length * (time - start) / (end - start));
					bins[bin]++;
				}
			}
		}
		int wBase = 60*width/288;
		c.drawLine(wBase, 0, wBase, (int)(width*1.2), p);
		float wBar = (float) (width*1.2 / (bins.length+1));
		for (int i = 0; i < bins.length+1; i++) {
			float y = (float) ((i + 0.5) * wBar);
			c.drawLine(wBase - 4, y, wBase + 4, y, p);
		}
		float binInterval = (float)(end - start) / bins.length;
		p.setTextSize(wBase*2/9);
		p.setTextAlign(Align.RIGHT);
		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		for (int i = 0; i < bins.length+1; i++) {
			int value = (int)(start + i * binInterval);
			float y = (float) ((i + 0.5) * wBar + fontHeight / 2 - fm.bottom);
			c.drawText(distime(value), wBase-5, y, p);
		}
		int maxValue = 0;
		for (int i = 0; i < bins.length; i++) {
			if (bins[i] > maxValue)
				maxValue = bins[i];
		}
		if (maxValue > 0) {
			for (int i = 0; i < bins.length; i++) {
				float y1 = (float) ((i + 0.5) * wBar);
				float y2 = (float) ((i + 1.5) * wBar);
				int height = (int) (bins[i] * (width - wBase - 4) / maxValue);
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.WHITE);
				c.drawRect(wBase + height, y1, wBase, y2, p);
				p.setStyle(Paint.Style.STROKE);
				p.setColor(Color.BLACK);
				c.drawRect(wBase + height, y1, wBase, y2, p);
			}
		}
	}

	public static void drawGraph(int width, Paint p, Canvas c) {
		int up, down, mean;
		int blk, divi;
		if(Session.length==0 || Statistics.minIdx==-1 || Statistics.minIdx==Statistics.maxIdx) {
			up = 20000;
			down = 12000;
			mean = 16000;
			blk = 8;
			divi = 1000;
		} else {
			int max = Session.getTime(Statistics.maxIdx);
			int min = Session.getTime(Statistics.minIdx);
			divi = getDivision((max-min)/8);
			mean = (min + max) >>> 1;
			mean = ((mean + divi / 2) / divi) * divi;
			up = down = mean;
			while (up < max) {
				up += divi;
			}
			while (down > min) {
				down -= divi;
			}
			mean = Statistics.mean;
			blk = (up - down) / divi;
		}
		int wBase = 45 * width / 288;
		p.setTextSize(wBase *2 / 9);
		p.setTextAlign(Align.RIGHT);
		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		float wBar = (float) ((width * 0.9 - wBase / 4.5) / blk);
		p.setColor(0xff808080);
		for (int i = 0; i < blk+1; i++) {
			float y = (float) (i * wBar + wBase/9.);
			c.drawLine(wBase, y, width, y, p);
		}
		c.drawLine(wBase, (float)(wBase/9.), wBase, (float)(width*0.9-wBase/9.), p);
		p.setColor(0xffff0000);
		float y = (float) ((double)(up - mean) / divi * wBar + wBase/9.);
		c.drawLine(wBase, y, width, y, p);
		p.setColor(0xff000000);
		for (int i = 0; i < blk+1; i++) {
			int value = (int)(up - i * divi);
			y = (float) (i * wBar + wBase/9. + fontHeight / 2 - fm.bottom);
			c.drawText(distime(value), wBase-4, y, p);
		}
		int count = 0;
		for(int i=0; i<Session.length; i++) 
			if(Session.penalty[i] != 2) count++;
		float rsp = (float) ((double)(width - 8 - wBase) / (count-1));
		count = 0;
		float lastx = -1, lasty = -1;
		for(int i=0; i<Session.length; i++) {
			if(Session.penalty[i] != 2) {
				int time = Session.getTime(i);
				float x = (float) (wBase + 4.0 + (count++) * rsp);
				y = (float) ((double)(up - time) / divi * wBar + wBase/9.);
				//c.drawRect(x-2, y-2, x+2, y-2, p);
				c.drawCircle(x, y, 3, p);
				if(lastx!=-1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
	}
}

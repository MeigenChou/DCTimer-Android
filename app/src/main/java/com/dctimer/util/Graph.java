package com.dctimer.util;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.model.Result;

import java.util.ArrayList;

import static com.dctimer.APP.avg1len;
import static com.dctimer.APP.avg2len;

public class Graph {

	private static int getDivision(int dv) {
		int[] ds = {100, 200, 500, 1000, 2000, 5000, 10000, 20000, 30000, 60000, 90000, 120000, 300000, 600000, 1200000, 1800000, 3600000};
		if (dv < ds[0]) return 100;
		for (int i = 1; i < ds.length; i++)
			if (dv < ds[i]) return ds[i];
		return (dv / 1000 + 1) * 1000;
	}
	
	private static String timeToString(long i, boolean showMillisecond) {
		boolean m = i < 0;
		i = Math.abs(i) + 5;
		int ms = (int) ((i % 1000) / 100);
		long s = i / 1000;
		int mi = 0, h = 0;
		if (APP.timeFormat < 2) {
			mi = (int) (s / 60);
			s %= 60;
			if (APP.timeFormat < 1) {
				h = mi / 60;
				mi %= 60;
			}
		}
		StringBuilder sb = new StringBuilder();
		if (m) sb.append('-');
		if (h > 0) {
			sb.append(h).append(':');
			if (mi < 10) sb.append('0');
			sb.append(mi).append(':');
			if (s < 10) sb.append('0');
		} else if (mi > 0) {
			sb.append(mi).append(':');
			if (s < 10) sb.append('0');
		}
		sb.append(s);
		if (showMillisecond)
			sb.append(APP.decimalMark == 0 ? '.' : ',').append(ms);
		return sb.toString();
	}
	
	public static void drawHist(Context context, Result result, int width, Paint p, Canvas c) {
		int[] bins = new int[14];
		long start;
		long end;
		int divi;
		if (result.length() == 0 || result.getMinIdx() == -1 || result.getMaxIdx() == -1) {
			start = 11000;
			end = 25000;
			divi = 1000;
		} else {
			int max = result.getTime(result.getMaxIdx());
			int min = result.getTime(result.getMinIdx());
			divi = getDivision((max - min) / 14);
			long mean = (min + max) >>> 1;
			mean = ((mean + divi / 2) / divi) * divi;
			start = mean - divi * 7;
			end = mean + divi * 7;
		}
		//Log.w("dct", start + ", " + end);
		for (int i = 0; i < bins.length; i++)
			bins[i] = 0;
		for (int i = 0; i < result.length(); i++) {
			if (!result.isDnf(i)) {
				int time = result.getTime(i);
				if (time >= start && time < end) {
					int bin = (int) (bins.length * (time - start) / (end - start));
					bins[bin]++;
				} else if (time == end)
					bins[13]++;
			}
		}
		p.setTextSize(width / 24f);
		p.setTextAlign(Align.RIGHT);
		p.setStrokeWidth(APP.dpi);
		p.setColor(context.getResources().getColor(R.color.colorText));
		int wBase = getWidth(p, timeToString(end, divi < 1000)) + 8;
		c.drawLine(wBase, 0, wBase, width * 1.2f, p);
		float wBar = width * 1.2f / (bins.length + 1);
		for (int i = 0; i <= bins.length; i++) {
			float y = (i + 0.5f) * wBar;
			c.drawLine(wBase - 4, y, wBase + 4, y, p);
		}
		float binInterval = (float) (end - start) / bins.length;

		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		for (int i = 0; i <= bins.length; i++) {
			long value = (long) (start + i * binInterval);
			float y = (i + 0.5f) * wBar + fontHeight / 2 - fm.bottom;
			c.drawText(timeToString(value, divi < 1000), wBase - 5, y, p);
		}
		int maxValue = 0;
		for (int i = 0; i < bins.length; i++) {
			if (bins[i] > maxValue)
				maxValue = bins[i];
		}
		for (int i = 0; i < bins.length; i++) {
			if (bins[i] > 0) {
				float y1 = (i + 0.5f) * wBar;
				float y2 = (i + 1.5f) * wBar;
				int height = bins[i] * (width - wBase - 4) / maxValue;
				//p.setStyle(Paint.Style.FILL);
				//p.setColor(context.getResources().getColor(R.color.colorBackground));
				//c.drawRect(wBase + height, y1, wBase, y2, p);
				p.setStyle(Paint.Style.STROKE);
				c.drawRect(wBase + height, y1, wBase, y2, p);
				p.setStyle(Paint.Style.FILL);
				float y = (i + 1f) * wBar + fontHeight / 2 - fm.bottom;
				String text = String.valueOf(bins[i]);
				int textWid = getWidth(p, text) + 13;
				if (textWid > height)
					c.drawText(text, wBase + height + textWid, y, p);
				else c.drawText(String.valueOf(bins[i]), wBase + height - 5, y, p);
			}
		}
	}

	public static void drawGraph(Context context, Result result, int width, Paint p, Canvas c) {
		long up, down, mean;
		int blk, divi;
		if (result.length() == 0 || result.getMinIdx() == -1 || result.getMinIdx() == result.getMaxIdx()) {
			up = 20000;
			//down = 12000;
			mean = 16000;
			blk = 8;
			divi = 1000;
		} else {
			int max = result.getTime(result.getMaxIdx());
			int min = result.getTime(result.getMinIdx());
			divi = getDivision((max - min) / 8);
			mean = (min + max) >>> 1;
			mean = ((mean + divi / 2) / divi) * divi;
			up = down = mean;
			while (up < max) {
				up += divi;
			}
			while (down > min) {
				down -= divi;
			}
			mean = result.sessionMean();
			blk = (int) ((up - down) / divi);
		}
		p.setTextSize(width / 24f);
		p.setTextAlign(Align.RIGHT);
		int wBase = getWidth(p, timeToString(up, divi < 1000)) + 8;
		FontMetrics fm = p.getFontMetrics();
		float fontHeight = fm.bottom - fm.top;
		float wBar = (width * 0.9f - width / 24f) / blk;
		p.setColor(0xff999999);
		for (int i = 1; i < blk; i++) {
			float y = i * wBar + width / 48f;
			c.drawLine(wBase, y, width - 1, y, p);
		}

		p.setColor(context.getResources().getColor(R.color.colorText));
		for (int i = 0; i <= blk; i++) {
			int value = (int) (up - i * divi);
			float y = i * wBar + width / 48f + fontHeight / 2 - fm.bottom;
			c.drawText(timeToString(value, divi < 1000), wBase - 4, y, p);
		}
		p.setColor(0xff999999);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(APP.dpi);
		c.drawRect(wBase, width / 48f, width - 1, width * 0.9f - width / 48f, p);
		//c.drawLine(wBase, wBase/9f, wBase, (float)(width*0.9-wBase/9.), p);

		//画单次
		//int count = 0;
//		for (int i = 0; i < result.length(); i++)
//			if (!Sessions.isDNF(i)) count++;
		float rsp = (width - 8f - wBase) / (result.length() - 1);
		//count = 0;
		float lastx = -1, lasty = -1;
		p.setColor(0xff779977);
		p.setStyle(Paint.Style.FILL);
		p.setStrokeWidth(APP.dpi * 1.5f);
		ArrayList<float[]> best = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < result.length(); i++) {
			if (!result.isDnf(i)) {
				int time = result.getTime(i);
				float x = wBase + 4f + i * rsp;
				float y = (float) (up - time) / divi * wBar + width / 48f;
				if (time <= min) {
					float[] item = {x, y};
					best.add(item);
					min = time;
				}
				//c.drawRect(x-2, y-2, x+2, y-2, p);
				//c.drawCircle(x, y, 2, p);
				if (lastx != -1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
		for (int i = 0; i < best.size(); i++) {
			float[] item = best.get(i);
			c.drawCircle(item[0], item[1], p.getStrokeWidth(), p);
		}

		//平均1
		p.setColor(0xdd0033ff);
		lastx = lasty = -1;
		best = new ArrayList<>();
		min = Integer.MAX_VALUE;
		for (int i = avg1len - 1; i < result.length(); i++) {
			int avg = result.getAvg1(i);
//			if (avg1Type == 0) avg = Stats.averageOf(avg1len, i, -1);
//			else avg = Stats.meanOf(avg1len, i, -1);
			//Log.w("dct", i+":"+avg);
			if (avg > 0) {
				float x = wBase + 4f + i * rsp;
				float y = (float) (up - avg) / divi * wBar + width / 48f;
				if (avg <= min) {
					float[] item = {x, y};
					best.add(item);
					min = avg;
				}
				if (lastx != -1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
		for (int i = 0; i < best.size(); i++) {
			float[] item = best.get(i);
			c.drawCircle(item[0], item[1], p.getStrokeWidth(), p);
		}

		//平均2
		p.setColor(0xddff3300);
		lastx = lasty = -1;
		best = new ArrayList<>();
		min = Integer.MAX_VALUE;
		for (int i = avg2len - 1; i < result.length(); i++) {
			int avg = result.getAvg2(i);
//			if (avg2Type == 0) avg = Stats.averageOf(avg2len, i, -1);
//			else avg = Stats.meanOf(avg2len, i, -1);
			if (avg > 0) {
				float x = wBase + 4f + i * rsp;
				float y = (float) (up - avg) / divi * wBar + width / 48f;
				if (avg <= min) {
					float[] item = {x, y};
					best.add(item);
					min = avg;
				}
				if (lastx != -1) c.drawLine(lastx, lasty, x, y, p);
				lastx = x; lasty = y;
			}
		}
		for (int i = 0; i < best.size(); i++) {
			float[] item = best.get(i);
			c.drawCircle(item[0], item[1], p.getStrokeWidth(), p);
		}

		//总平均
		p.setColor(0xbbee3333);
		//float y = (float) ((double)(up - mean) / divi * wBar + wBase/9.);
		float y = ((float) (up - mean) / divi * wBar + width / 48f);
		p.setPathEffect(new DashPathEffect(new float[] {4 * APP.dpi, 4 * APP.dpi}, 0));
		c.drawLine(wBase, y, width - 1, y, p);
	}

	private static int getWidth(Paint p, String text) {
		Rect rect = new Rect();
		p.getTextBounds(text, 0, text.length(), rect);
		return rect.width();
	}
}

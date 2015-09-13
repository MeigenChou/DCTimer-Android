package com.dctimer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.dctimer.DCTimer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
	public static int greyLevel(int color) {
		int red = (color >>> 16) & 0xff;
		int green = (color >>> 8) & 0xff;
		int blue = color & 0xff;
		int grey = red * 299 + green * 587 + blue * 114;
		return grey / 1000;
	}
	
	public static String convertStr(String s) {
		if(s.length()==0 || s.equals("0")) return "Error";
		StringBuffer sb = new StringBuffer();
		byte dot = 0, colon = 0;
		int num = 0;
		boolean dbc = false;
		for(int i=0; i<s.length(); i++) {
			if(Character.isDigit(s.charAt(i))) { sb.append(s.charAt(i)); num++; }
			if(s.charAt(i)=='.' && dot<1) { sb.append('.'); dot++; dbc=true; }
			if(s.charAt(i)==':' && colon<2 && !dbc) { sb.append(':'); colon++; }
		}
		if(num == 0) return "Error";
		sb.insert(0, dot + "" + colon);
		return sb.toString();
	}
	
	public static int parseTime(String s) {
		if(s.charAt(1) == '0')
			return (int) (Double.parseDouble(s.substring(2))*1000+0.5);
		int hour, min;
		double sec;
		String[] time = s.substring(2).split(":");
		if(s.charAt(1) == '1') {
			hour = 0;
			min = time[0].length()==0 ? 0 : Integer.parseInt(time[0]);
			if(time.length == 1) sec = 0;
			else sec = time[1].length()==0 ? 0 : Double.parseDouble(time[1]);
		}
		else {
			hour = time[0].length()==0 ? 0 : Integer.parseInt(time[0]);
			if(time.length == 1) min = 0;
			else min = time[1].length()==0 ? 0 : Integer.parseInt(time[1]);
			if(time.length < 3) sec = 0;
			else sec = time[2].length()==0 ? 0 : Double.parseDouble(time[2]);
		}
		return (int) ((hour*3600+min*60+sec)*1000+0.5);
	}
	
	public static int getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			return 23;
		}
	}
	
	public static String getContent(String strUrl) {
        try {
            URL url = new URL(strUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "GB2312"));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while((line = br.readLine()) != null) {
            	sb.append(line + "\t");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "error open url:" + strUrl;
        }
    }
	
	public static int hslToRgb(int h, double s, double l) {
		double r, g, b;
		if(s == 0) r = g = b = l;
		else {
			double q, p, tr, tg, tb;
			if(l<0.5) q = l * (1 + s);
			else q = l + s - l * s;
			p = 2 * l - q;
			double H = h/360D;
			tr = H + 1/3D;
			tg = H;
			tb = H - 1/3D;
			r = toRGB(tr, q, p, H);
			g = toRGB(tg, q, p, H);
			b = toRGB(tb, q, p, H);
		}
		r = r * 255 + 0.5;
		g = g * 255 + 0.5;
		b = b * 255 + 0.5;
		return Color.rgb((int)r, (int)g, (int)b);
	}
	
	public static double[] rgbToHSL(int rgb) {
		double R = ((rgb >> 16) & 0xff) / 255D;
		double G = ((rgb >> 8) & 0xff) / 255D;
		double B = (rgb & 0xff) / 255D;
		double h = 0, s = 0, l;
		double max = Math.max(Math.max(R, G), B);
		double min = Math.min(Math.min(R, G), B);
		if(max == min) h = 0;
		else if(max == R && G >= B) h = 60 * ((G - B) / (max - min));
		else if(max == R && G < B) h = 60 * ((G - B) / (max - min)) + 360;
		else if(max == G) h = 60 * ((B - R) / (max - min)) + 120;
		else if(max == B) h = 60 * ((R - G) / (max - min)) + 240;
		l = (max + min) / 2;
		if(l == 0 || max == min) s = 0;
		else if(l > 0 && l <= 0.5)s = (max - min) / (max + min);
		else if(l > 0.5) s = (max - min) / (2 - (max + min));
		return new double[] {h, s, l};
	}
	
	public static double toRGB(double tc, double q, double p, double H) {
		if(tc < 0) tc += 1;
		if(tc > 1) tc -= 1;
		if(tc < 1/6D)
			return p + (q - p) * 6 * tc;
		else if(tc < 0.5)
			return q;
		else if(tc < 2/3D)
			return p + (q - p) * 6 * (2/3D - tc);
		else return p;
	}
	
	public static Bitmap getBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int width = DCTimer.dm.widthPixels;
		int height = DCTimer.dm.heightPixels;
		int scaleWidth = opts.outWidth / width;
		int scaleHeight = opts.outHeight / height;
		int scale = Math.min(scaleWidth, scaleHeight);
		opts.inJustDecodeBounds = false;
		if(scale > 1) {
			opts.inSampleSize = scale;
		} else opts.inSampleSize = 1;
		return BitmapFactory.decodeFile(path, opts);
	}
	
	public static Bitmap getBackgroundBitmap(Bitmap bitmap) {
		int width = DCTimer.dm.widthPixels;
		int height = DCTimer.dm.heightPixels;
		float scaleWidth = (float) bitmap.getWidth() / width;
		float scaleHeight = (float) bitmap.getHeight() / height;
		float scale = Math.min(scaleWidth, scaleHeight);
		Matrix matrix = new Matrix();
		matrix.postScale(1/scale, 1/scale);
		return Bitmap.createBitmap(bitmap, (int)((bitmap.getWidth()-width*scale)/2),
				(int)((bitmap.getHeight()-height*scale)/2), (int)(width*scale), (int)(height*scale), matrix, true);
	}
	
	public static Drawable getBackgroundDrawable(Context context, Bitmap scaleBitmap, int opac) {
		int width = DCTimer.dm.widthPixels;
		int height = DCTimer.dm.heightPixels;
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(0);
		Paint paint = new Paint();
		canvas.drawBitmap(scaleBitmap, 0, 0, paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255*(100-opac)/100);
		canvas.drawRect(0, 0, width, height, paint);
		return new BitmapDrawable(context.getResources(), newBitmap);
	}
}

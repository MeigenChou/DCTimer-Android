package com.dctimer.util;

import static com.dctimer.Configs.egoll;
import static com.dctimer.Configs.egolls;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import scrambler.Scrambler;

import com.dctimer.DCTimer;
import com.dctimer.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {
	static String ego = "PHUTLSAN";
	
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
			return 24;
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
	
	public static void addMaxQueue(int[] data, int x, int size) {
		if(size == 0) {
			data[0] = x;
		} else {
			int k = size;
			while (k > 0) {
				int parent = (k - 1) >>> 1;
				int e = data[parent];
				if(x >= e) break;
				data[k] = e;
				k = parent;
			}
			data[k] = x;
		}
	}
	
	static void pollMax(int[] data, int size) {
		int s = --size;
		int x = data[s];
		int half = s >>> 1;
		int k = 0;
		while(half > k) {
			int child = (k << 1) + 1;
			int c = data[child];
			int right = child + 1;
			if (right < s && c > data[right]) 
				c = data[child = right];
			if (x <= c) break;
			data[k] = c;
			k = child;
		}
		data[k] = x;
	}
	
	static void addMinQueue(int[] data, int x, int size) {
		if(size == 0) {
			data[0] = x;
		} else {
			int k = size;
			while (k > 0) {
				int parent = (k - 1) >>> 1;
				int e = data[parent];
				if(x <= e) break;
				data[k] = e;
				k = parent;
			}
			data[k] = x;
		}
	}
	
	static void pollMin(int[] data, int size) {
		int s = --size;
		int x = data[s];
		int half = s >>> 1;
		int k = 0;
		while(half > k) {
			int child = (k << 1) + 1;
			int c = data[child];
			int right = child + 1;
			if (right < s && c < data[right]) 
				c = data[child = right];
			if (x >= c) break;
			data[k] = c;
			k = child;
		}
		data[k] = x;
	}
	
	public static void setEgOll() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<8; i++)
			if(((egoll >> 7-i) & 1) != 0)
				sb.append(ego.charAt(i));
		egolls = sb.toString();
	}
	
	public static void setInScr(String text, ArrayList<String> list) {
		String[] scrs = text.split("\n");
		for(int i=0; i<scrs.length; i++) {
			String scr = scrs[i].replaceFirst("^\\s*((\\(?\\d+\\))|(\\d+\\.))\\s*", "");
			if(!scr.equals("")) list.add(scr);
		}
	}
	
	public static int getViewType(String scr) {
		if(scr.matches("([FRU][2']?\\s*)+")) return 2;
		if(scr.matches("([ULRB]'?\\s*)+")) return Scrambler.TYPE_SKW;
		if(scr.matches("([ULRBulrb]'?\\s*)+")) return Scrambler.TYPE_PYR;
		if(scr.matches("([xFRUBLDMfrubld][2']?\\s*)+")) return 3;
		if(scr.matches("(([FRUBLDfru]|[FRU]w)[2']?\\s*)+")) return 4;
		if(scr.matches("(([FRUBLDfrubld]|([FRUBLD]w?))[2']?\\s*)+")) return 5;
		if(scr.matches("(((2?[FRUBLD])|(3[FRU]w))[2']?\\s*)+")) return 6;
		if(scr.matches("(((2|3)?[FRUBLD])[2']?\\s*)+")) return 7;
		return 0;
	}
	
	public static void showKeyboard(final EditText et) {
		new Thread() {
			public void run() {
				try {
					sleep(200);
				} catch (Exception e) { }
				InputMethodManager inm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				inm.showSoftInput(et, 0);
			}
		}.start();
	}
	
	public static void hideKeyboard(EditText et) {
		InputMethodManager inm = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		inm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	public static int get2ndScr(int s) {
		switch (s) {
		case -1: return R.array.scrwca;
		case 0: return R.array.scr222;
		case 1: return R.array.scr333;
		case 2: return R.array.scr444;
		case 3: return R.array.scr555;
		case 4:
		case 5: return R.array.scr666;
		case 6: return R.array.scrMinx;
		case 7: return R.array.scrPrym;
		case 8: return R.array.scrSq1;
		case 9: return R.array.scrClk;
		case 15: return R.array.scr15p;
		case 11: return R.array.scrMxN;
		case 12: return R.array.scrCmt;
		case 13: return R.array.scrGear;
		case 14: return R.array.scrSmc;
		case 10: return R.array.scrSkw;
		case 16: return R.array.scrOth;
		case 17: return R.array.scr3sst;
		case 18: return R.array.scrBdg;
		case 19: return R.array.scrMsst;
		default: return R.array.scrRly;
		}
	}
}

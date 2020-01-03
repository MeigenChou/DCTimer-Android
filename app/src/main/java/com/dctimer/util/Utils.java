package com.dctimer.util;

import static com.dctimer.APP.egIdx;
import static com.dctimer.APP.egolls;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import cs.min2phase.CubieCube;
import scrambler.Scrambler;

import com.dctimer.APP;
import com.dctimer.activity.MainActivity;
import com.dctimer.R;
import com.dctimer.model.Result;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
    static String ego = "012PHUTLSA";

    public static int greyScale(int color) {
        int red = (color >>> 16) & 0xff;
        int green = (color >>> 8) & 0xff;
        int blue = color & 0xff;
        int grey = red * 299 + green * 587 + blue * 114;
        return grey / 1000;
    }

    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            return 29;
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.1";
        }
    }

    public static String getContent(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);     //设置连接超时时间
            conn.setReadTimeout(30000);
            //conn.setRequestMethod("POST");     //设置以Post方式提交数据
            //conn.setDoOutput(true);
            //conn.setDoInput(true);
            //conn.setUseCaches(false);
            conn.connect();
            int res = conn.getResponseCode();
            if (res == HttpURLConnection.HTTP_OK) {
                InputStreamReader is = new InputStreamReader(conn.getInputStream(), "GB2312");
                BufferedReader reader = new BufferedReader(is);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\t');
                }
                reader.close();
                return sb.toString();
            } else return "error code: " + res;
        } catch (Exception e) {
            return "error open url:" + urlStr;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int hslToRgb(int h, double s, double l) {
        double r, g, b;
        if (s == 0) r = g = b = l;
        else {
            double q, p, tr, tg, tb;
            if (l < 0.5) q = l * (1 + s);
            else q = l + s - l * s;
            p = 2 * l - q;
            double H = h / 360D;
            tr = H + 1 / 3D;
            tg = H;
            tb = H - 1 / 3D;
            r = toRGB(tr, q, p);
            g = toRGB(tg, q, p);
            b = toRGB(tb, q, p);
        }
        r = r * 255 + 0.5;
        g = g * 255 + 0.5;
        b = b * 255 + 0.5;
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public static double[] rgbToHSL(int rgb) {
        double R = ((rgb >> 16) & 0xff) / 255D;
        double G = ((rgb >> 8) & 0xff) / 255D;
        double B = (rgb & 0xff) / 255D;
        double h = 0, s = 0, l;
        double max = Math.max(Math.max(R, G), B);
        double min = Math.min(Math.min(R, G), B);
        if (max == min) h = 0;
        else if (max == R && G >= B) h = 60 * ((G - B) / (max - min));
        else if (max == R && G < B) h = 60 * ((G - B) / (max - min)) + 360;
        else if (max == G) h = 60 * ((B - R) / (max - min)) + 120;
        else if (max == B) h = 60 * ((R - G) / (max - min)) + 240;
        l = (max + min) / 2;
        if (l == 0 || max == min) s = 0;
        else if (l > 0 && l <= 0.5)s = (max - min) / (max + min);
        else if (l > 0.5) s = (max - min) / (2 - (max + min));
        return new double[] {h, s, l};
    }

    private static double toRGB(double tc, double q, double p) {
        if (tc < 0) tc += 1;
        if (tc > 1) tc -= 1;
        if (tc < 1/6D)
            return p + (q - p) * 6 * tc;
        else if (tc < 0.5)
            return q;
        else if (tc < 2/3D)
            return p + (q - p) * 6 * (2/3D - tc);
        else return p;
    }

    public static Bitmap getBitmap(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int width = APP.dm.widthPixels;
        int height = APP.dm.heightPixels;
        int scaleWidth = opts.outWidth / width;
        int scaleHeight = opts.outHeight / height;
        int scale = Math.min(scaleWidth, scaleHeight);
        opts.inJustDecodeBounds = false;
        if (scale > 1) {
            opts.inSampleSize = scale;
        } else opts.inSampleSize = 1;
        return BitmapFactory.decodeFile(path, opts);
    }

    public static Bitmap getBackgroundBitmap(Bitmap bitmap) {
        int width = APP.dm.widthPixels;
        int height = APP.dm.heightPixels;
        float scaleWidth = (float) bitmap.getWidth() / width;
        float scaleHeight = (float) bitmap.getHeight() / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(1 / scale, 1 / scale);
        return Bitmap.createBitmap(bitmap, (int)((bitmap.getWidth() - width * scale) / 2),
                (int) ((bitmap.getHeight() - height * scale) / 2), (int) (width * scale), (int) (height * scale), matrix, true);
    }

    public static Drawable getBackgroundDrawable(Context context, Bitmap scaleBitmap, int opacity) {
        int width = APP.dm.widthPixels;
        int height = APP.dm.heightPixels;
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(0);
        Paint paint = new Paint();
        canvas.drawBitmap(scaleBitmap, 0, 0, paint);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255 * (100 - opacity) / 100);
        canvas.drawRect(0, 0, width, height, paint);
        return new BitmapDrawable(context.getResources(), newBitmap);
    }

    public static void addMaxQueue(int[] data, int x, int size) {
        if (size == 0) {
            data[0] = x;
        } else {
            int k = size;
            while (k > 0) {
                int parent = (k - 1) >>> 1;
                int e = data[parent];
                if (x >= e) break;
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
        while (half > k) {
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
        if (size == 0) {
            data[0] = x;
        } else {
            int k = size;
            while (k > 0) {
                int parent = (k - 1) >>> 1;
                int e = data[parent];
                if (x <= e) break;
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
        while (half > k) {
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
        for (int i = 0; i < 7; i++)
            if (egIdx[i + 3])
                sb.append(ego.charAt(i));
        egolls = sb.toString();
    }

    public static int getEgOll() {
        int egoll = 0;
        for (int i = 0; i < 7; i++)
            if (egIdx[i + 3])
                egoll |= (0x80 >> i);
        return egoll;
    }

    public static void addScramble(String text, List<String> list) {
        String[] arr = text.split("\n");
        for (int i = 0; i < arr.length; i++) {
            String scramble = arr[i].replaceFirst("^\\s*((\\(?\\d+\\))|(\\d+\\.))\\s*", "");
            if (!TextUtils.isEmpty(scramble)) list.add(scramble);
        }
    }

    public static void showKeyboard(final EditText et) {
        et.requestFocus();
        new Thread() {
            public void run() {
                try {
                    sleep(300);
                    InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, 0);
                } catch (Exception e) { }
            }
        }.start();
    }

    public static void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager)et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static int getScrambleArrayId(int s) {
        switch (s) {
            case -1: return R.array.item_wca;
            case 0: return R.array.item_222;
            case 1: return R.array.item_333;
            case 2: return R.array.item_444;
            case 3: return R.array.item_555;
            case 4:
            case 5: return R.array.item_666;
            case 6: return R.array.item_mega;
            case 7: return R.array.item_pyr;
            case 8: return R.array.item_sq1;
            case 9: return R.array.item_clk;
            case 15: return R.array.item_15p;
            case 11: return R.array.item_mnl;
            case 12: return R.array.item_cmt;
            case 13: return R.array.item_gear;
            case 14: return R.array.item_smc;
            case 10: return R.array.item_skewb;
            case 16: return R.array.item_other;
            case 17: return R.array.item_333_sub;
            case 18: return R.array.item_bandage;
            case 19: return R.array.item_minx_sub;
            default: return R.array.item_relay;
        }
    }

    public static void saveScramble(Context context, final ProgressDialog progress, final Handler handler, final Scrambler scramble, final String path, final int num) {
        File fPath = new File(path);
        if (fPath.getParentFile().exists()) {
            progress.setTitle(context.getString(R.string.action_export_scramble));
            progress.setMax(num);
            progress.show();
            new Thread() {
                public void run() {
                    try {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
                        for (int i = 0; i < num; i++) {
                            handler.sendEmptyMessage(100 + i);
                            scramble.generateScramble(APP.scrambleIdx, false);
                            String s = (i + 1) + ". " + scramble.getScramble() + "\r\n";
                            writer.write(s);
                        }
                        writer.close();
                        handler.sendEmptyMessage(7);
                    } catch (IOException e) {
                        handler.sendEmptyMessage(5);
                    }
                    progress.dismiss();
                }
            }.start();
        } else Toast.makeText(context, context.getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
    }

    public static void saveStat(Context context, String path, String fileName, String stat) {
        File fPath = new File(path);
        if (fPath.exists() || fPath.mkdirs()) {
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(path+fileName));
                byte [] bytes = stat.getBytes();
                out.write(bytes);
                out.close();
                Toast.makeText(context, context.getString(R.string.save_success), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, context.getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
            }
        }
        else Toast.makeText(context, context.getString(R.string.path_not_exist), Toast.LENGTH_SHORT).show();
    }

    public static void exportDB(final String path, final Handler handler) {
        //File f = new File(path);
        //pd.setTitle(context.getString(R.string.export_db));
        //pd.show();
        new Thread() {
            public void run() {
                try {
                    InputStream is = new FileInputStream(APP.dataPath + "spdcube.db");
                    OutputStream os = new FileOutputStream(path);
                    byte[] buf = new byte[1024 * 8];
                    int bytesRead;
                    while ((bytesRead = is.read(buf)) != -1) {
                        os.write(buf, 0, bytesRead);
                    }
                    os.close();
                    handler.sendEmptyMessage(7);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(5);
                }
                //pd.dismiss();
            }
        }.start();
    }

    public static void importDB(final String path, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = new FileInputStream(path);
                    byte[] buf = new byte[1024 * 8];
                    int bytesRead = is.read(buf, 0, 16);
                    if (bytesRead < 16) handler.sendEmptyMessage(11);
                    else if (checkHeader(buf)) {
                        OutputStream os = new FileOutputStream(APP.dataPath + "spdcube.db");
                        os.write(buf, 0, 16);
                        while ((bytesRead = is.read(buf)) != -1) {
                            os.write(buf, 0, bytesRead);
                        }
                        os.close();
                        handler.sendEmptyMessage(12);
                    } else handler.sendEmptyMessage(11);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(11);
                }
            }
        }).start();
    }

    private static boolean checkHeader(byte[] buf) {
        if (buf.length < 16) return false;
        byte[] head = "SQLite format 3".getBytes();
        for (int i = 0; i < 15; i++) {
            if (buf[i] != head[i]) return false;
        }
        if (buf[15] != 0) return false;
        return true;
    }

    public static void addSection(Map<Integer, String> header, List<Map<String, Object>> cells, String head, String[] title, int[] type, Object[] detail, int[] progress) {
        header.put(cells.size(), head);
        cells.add(new HashMap<String, Object>());
        for (int i = 0; i < title.length; i++) {
            //Log.w("set", cells.size()+"/"+(progress[i] >> 16)+"/"+(progress[i] & 0xffff));
            Map<String, Object> map = new HashMap<>();
            map.put("title", title[i]);
            map.put("type", type[i]);
            map.put("detail", detail[i]);
            map.put("max", progress[i] >> 16);
            map.put("value", progress[i] & 0xffff);
            cells.add(map);
        }
    }

    public static String getShareContent(MainActivity context) {
        StringBuilder sb = new StringBuilder();
        Result result = APP.getInstance().getResult();
        int len = result.length();
        if (len == 0)
            sb.append(context.getString(R.string.share_c0));
        else {
            sb.append(String.format(context.getString(R.string.share_c1), len, context.getScrambleText(),
                    result.getTimeAt(result.getMinIdx(), false), StringUtils.timeToString(result.sessionMean())));
            if (len > APP.avg1len) sb.append(String.format(context.getString(R.string.share_c2), APP.avg1len, result.getBestAvg1()));
            if (len > APP.avg2len) sb.append(String.format(context.getString(R.string.share_c2), APP.avg2len, result.getBestAvg2()));
        }
        sb.append(context.getString(R.string.share_c3));
        return sb.toString();
    }

    public static void averageDetail(List<String[]> detailList, int n, int i, List<Integer> trim) {
        Result result = APP.getInstance().getResult();
        if (result == null) return;
        int num = 1;
        for (int j = i - n + 1; j <= i; j++) {
            String[] details = new String[4];
            if (trim.contains(j))
                details[0] = num + ". (" + result.getTimeAt(j, true) + ")";
            else details[0] = num + ". " + result.getTimeAt(j, true);
            details[1] = result.getString(j, 4);
            details[2] = result.getString(j, 5);
            details[3] = result.getString(j, 6);
            detailList.add(details);
            num++;
        }
    }

    public static void meanDetail(List<String[]> detailList, int n, int i) {
        Result result = APP.getInstance().getResult();
        if (result == null) return;
        int num = 1;
        for (int j = i - n + 1; j <= i; j++) {
            String[] details = new String[4];
            details[0] = num + ". " + result.getTimeAt(j, true);
            details[1] = result.getString(j, 4);
            details[2] = result.getString(j, 5);
            details[3] = result.getString(j, 6);
            detailList.add(details);
            num++;
        }
    }

    public static void sessionMeanDetail(List<String[]> detailList) {
        Result result = APP.getInstance().getResult();
        if (result == null) return;
        for (int i = 0; i < result.length(); i++) {
            String[] details = new String[4];
            details[0] = (i + 1) + ". " + result.getTimeAt(i, true);
            details[1] = result.getString(i, 4);
            details[2] = result.getString(i, 5);
            details[3] = result.getString(i, 6);
            detailList.add(details);
        }
    }

    public static String getCubeState(byte[] value) {
        //byte[] state = new byte[54];
        int f = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length - 2; i += 3) {
            int face = (value[i ^ 1] & 0xff) << 16 | (value[i + 1 ^ 1] & 0xff) << 8 | (value[i + 2 ^ 1] & 0xff);
            for (int j = 21; j >= 0; j -= 3) {
                int x = face >> j & 0x7;
                sb.append("URFDLB".charAt(x));
                //state[f++] = (byte) x;
                if (j == 12) {
                    x = i / 3;
                    sb.append("URFDLB".charAt(x));
                    //state[f++] = (byte) x;
                }
            }
        }
        //Log.w("dct", "face "+sb.toString());
        return sb.toString();
    }

    private static byte[][] cornerFacelet = {
            {26, 15, 29},
            {20, 8, 9},
            {18, 38, 6},
            {24, 27, 44},
            {51, 35, 17},
            {45, 11, 2},
            {47, 0, 36},
            {53, 42, 33}
    };

    private static byte[][] edgeFacelet = {
            {25, 28},
            {23, 12},
            {19, 7},
            {21, 41},
            {32, 16},
            {5, 10},
            {3, 37},
            {30, 43},
            {52, 34},
            {48, 14},
            {46, 1},
            {50, 39}
    };

    public static String parseGiikerState(byte[] value) {
        //byte[] valhex = toHexValue(value);
        int[] eo = new int[12];
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int mask = 8; mask != 0; mask >>= 1) {
                eo[count++] = (value[i + 28] & mask) == 0 ? 0 : 1;
            }
        }
        int[] coMask = {-1, 1, -1, 1, 1, -1, 1, -1};
        int[] cp = new int[8], co = new int[8];
        for (int i = 0; i < 8; i++) {
            cp[i] = value[i] - 1;
            co[i] = (3 + value[i + 8] * coMask[i]) % 3;
        }
        int[] ep = new int[12];
        for (int i = 0; i < 12; i++) {
            ep[i] = value[i + 16] - 1;
        }
        CubieCube cc = new CubieCube(cp, co, ep, eo);
        return cc.toFaceCube(cornerFacelet, edgeFacelet);
    }

    public static Bitmap drawCubeState(String facelets) {
        if (facelets == null || facelets.length() < 54) return null;
        String center = new String(
                new char[] {
                        facelets.charAt(4),
                        facelets.charAt(13),
                        facelets.charAt(22),
                        facelets.charAt(31),
                        facelets.charAt(40),
                        facelets.charAt(49)
                }
        );
        byte[] f = new byte[54];
        for (int i = 0; i < 54; i++) {
            f[i] = (byte) center.indexOf(facelets.charAt(i));
            if (f[i] == -1) {
                f[i] = 6;
            }
        }
        int[] color = {Color.WHITE, Color.RED, 0xff009900, Color.YELLOW, 0xffff9900, Color.BLUE, Color.GRAY};
        Bitmap bitmap = Bitmap.createBitmap((int) (APP.dpi * 245), (int) (APP.dpi * 183), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(0);
        Paint p = new Paint();
        p.setAntiAlias(true);
        float a = APP.dpi * 19;
        int[] dx = {1, 2, 1, 1, 0, 3}, dy = {0, 1, 1, 2, 1, 1};
        for (int i = 0; i < 6; i++) {
            p.setStrokeWidth(1);
            float offx = APP.dpi * (1 + dx[i] * 5);
            float offy = APP.dpi * (1 + dy[i] * 5);
            for (int j = 0; j < 9; j++) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(color[f[i * 9 + j] & 0xff]);
                c.drawRect(a * (3 * dx[i] + j % 3) + offx, a * (3 * dy[i] + j / 3) + offy, a * (3 * dx[i] + j % 3 + 1) + offx, a * (3 * dy[i] + j / 3 + 1) + offy, p);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(0xff000000);
                c.drawRect(a * (3 * dx[i] + j % 3) + offx, a * (3 * dy[i] + j / 3) + offy, a * (3 * dx[i] + j % 3 + 1) + offx, a * (3 * dy[i] + j / 3 + 1) + offy, p);
            }
            p.setStrokeWidth(APP.dpi);
            p.setColor(0xff000000);
            c.drawRect(a * 3 * dx[i] + offx, a * 3 * dy[i] + offy, a * 3 * (dx[i] + 1) + offx, a * 3 * (dy[i] + 1) + offy, p);
        }
        return bitmap;
    }
}

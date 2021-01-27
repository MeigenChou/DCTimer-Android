package com.dctimer.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.dctimer.APP;
import com.dctimer.R;
import com.dctimer.model.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import scrambler.Scrambler;

import static com.dctimer.APP.decimalMark;
import static com.dctimer.APP.avg1Type;
import static com.dctimer.APP.avg1len;
import static com.dctimer.APP.avg2Type;
import static com.dctimer.APP.avg2len;
import static com.dctimer.APP.scrambleIdx;

public class StringUtils {
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static String[] scrambleItems;
    public static String[][] scrambleSubitems;

    public static String getDate() {
        return formatter.format(new Date());
    }

    public static String timeToString(int t) {
        return timeToString(t, true);
    }

    public static String timeToString(int t, boolean showMsec) {
        if (t == -2) return "N/A";
        if (t == -1) return "DNF";
        if (t < 0) return "-";
        int msec = t % 1000;
        if (APP.timerAccuracy == 0) msec /= 10;
        int sec = t / 1000, min = 0, hour = 0;
        if (APP.timeFormat < 2) {
            min = sec / 60;
            sec %= 60;
            if (APP.timeFormat < 1) {
                hour = min / 60;
                min %= 60;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (hour > 0) sb.append(hour).append(":");
        if (min > 0) {
            if (hour > 0 && min < 10) sb.append("0");
            sb.append(min).append(":");
        }
        if (sec < 10 && (hour > 0 || min > 0)) sb.append("0");
        sb.append(sec);
        if (showMsec) {
            if (APP.decimalMark == 0)
                sb.append(".");
            else sb.append(",");
            if (APP.timerAccuracy == 1) {
                if (msec < 10) sb.append("00");
                else if (msec < 100) sb.append("0");
                sb.append(msec);
            } else {
                if (msec < 10) sb.append("0");
                sb.append(msec);
            }
        }
        return sb.toString();
    }

    public static int parseTime(String s) {
        if (TextUtils.isEmpty(s)) return -1;
        String[] arr = s.split(":");
        if (arr.length > 3) return -1;
        int hour = 0, min = 0;
        double sec = 0;
        try {
            if (arr.length > 2) {
                hour = Integer.parseInt(arr[0]);
                min = Integer.parseInt(arr[1]);
                sec = Double.parseDouble(arr[2]);
            } else if (arr.length > 1) {
                min = Integer.parseInt(arr[0]);
                sec = Double.parseDouble(arr[1]);
            } else {
                sec = Double.parseDouble(arr[0]);
            }
            return (int) ((hour * 3600 + min * 60 + sec) * 1000 + 0.5);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getImageType(String scramble) {
        if (scramble.matches("([URF][2']?\\s*)+")) return 2;
        if (scramble.matches("([URLB]'?\\s*)+")) return Scrambler.TYPE_SKW;
        if (scramble.matches("([URLBurlb]'?\\s*)+")) return Scrambler.TYPE_PYR;
        if (scramble.matches("([xyzURFDLBMurfdlb][2']?\\s*)+")) return 3;
        if (scramble.matches("(([xyzURFDLBurf]|[URF]w)[2']?\\s*)+")) return 4;
        if (scramble.matches("(([URFDLBurfdlb]|[URFDLB]w)[2']?\\s*)+")) return 5;
        if (scramble.matches("((2?[URFDLB]w?|[urfdlb]|3[URF]w?|3[urf])[2']?\\s*)+")) return 6;
        if (scramble.matches("(([23]?[URFDLB]w?|3?[urfdlb])[2']?\\s*)+")) return 7;
        return 0;
    }

    public static int getImageType(String scramble, int type) {
        switch (type) {
            case 1: //二阶
                return scramble.matches("([URFDLB][2']?\\s*)+") ? 2 : 0;
            case 2: //三阶
                return scramble.matches("([xyzURFDLBMurfdlb]w?[2']?\\s*)+") ? 3 : 0;
            case 3: //四阶
                return scramble.matches("(([xyzURFDLBurf]|[URF]w)[2']?\\s*)+") ? 4 : 0;
            case 4: //五阶
                return scramble.matches("(([URFDLBurfdlb]|[URFDLB]w)[2']?\\s*)+") ? 5 : 0;
            case 5: //六阶
                return scramble.matches("((2?[URFDLB]w?|[urfdlb]|3[URF]w?|3[urf])[2']?\\s*)+") ? 6 : 0;
            case 6: //七阶
                return scramble.matches("(([23]?[URFDLB]w?|3?[urfdlb])[2']?\\s*)+") ? 7 : 0;
            case 7: //金字塔
                return scramble.matches("([URLBulrb]'?\\s*)+") ? Scrambler.TYPE_PYR : 0;
            case 8: //斜转
                return scramble.matches("([URLB]'?\\s*)+") ? Scrambler.TYPE_SKW : 0;
            case 9: //SQ1
                return scramble.matches("((/|\\(-?\\d+,-?\\d+\\))\\s*)+") ? Scrambler.TYPE_SQ1 : 0;
            case 10:    //五魔
                return scramble.matches("((R\\+\\+|R--|D\\+\\+|D--|U'?)\\s*)+") ? Scrambler.TYPE_MEGA : 0;
            case 11:    //魔表
                if (scramble.matches("((UR|DR|DL|UL|U|R|D|L|ALL|y2)(\\d[+-])?\\s*)+")) return Scrambler.TYPE_CLK;
            default:
                return 0;
        }
    }

    public static String getScrambleName(int idx, int sub) {
        //String[] subitems;// = getResources().getStringArray(Utils.getScrambleArrayId(idx));
        String[] subitems = scrambleSubitems[idx + 1];
        if (sub >= subitems.length) sub = 0;
        return scrambleItems[idx + 1] + " - " + subitems[sub];
    }

    public static String meanOf(Context context, Result result, int n, int i, String[] detail) {
        String[] avg = result.getMeanDetail(n, i);
        StringBuilder sb = new StringBuilder();
        if (detail != null) sb.append(context.getString(R.string.stat_title)).append(new java.sql.Date(new Date().getTime())).append("\r\n");
        sb.append(context.getString(R.string.stat_mean)).append(avg[0]).append(' ');
        sb.append("(σ = ").append(avg[1]).append(')').append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_best)).append(avg[2]).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_worst)).append(avg[3]).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_list));
        if (detail != null) {
            detail[0] = avg[0] + " (σ = " + avg[1] + ")";
            detail[1] = avg[2];
            detail[2] = avg[3];
        }
        int num = 1;
        for (int j = i - n + 1; j <= i; j++) {
            if (detail != null) sb.append("\r\n").append(num++).append(". ");
            sb.append(result.getTimeAt(j, false));
            if (detail != null) {
                String s = result.getString(j, 6);
                if (!TextUtils.isEmpty(s)) sb.append('[').append(s).append(']');
            }
            if (detail == null && j < i) sb.append(decimalMark == 0 ? ", " : "; ");
            if (detail != null) sb.append("  ").append(result.getString(j, 4));
        }
        return sb.toString();
    }

    public static String averageOf(Context context, Result result, int n, int i, String[] detail, ArrayList<Integer> trim) {
        //ArrayList<Integer> midx = new ArrayList<>();
        String[] avg = result.getAvgDetail(n, i, trim);
        //Log.w("dct", "平均 "+avg[0]);
        StringBuilder sb = new StringBuilder();
        if (detail != null) sb.append(context.getString(R.string.stat_title)).append(new java.sql.Date(new Date().getTime())).append("\r\n");
        sb.append(context.getString(R.string.stat_avg)).append(avg[0]).append(' ');
        sb.append("(σ = ").append(avg[1]).append(')').append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_best)).append(avg[2]).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_worst)).append(avg[3]).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_list));
        if (detail != null) {
            detail[0] = avg[0] + " (σ = " + avg[1] + ")";
            detail[1] = avg[2];
            detail[2] = avg[3];
        }
        int num = 1;
        for (int j = i - n + 1; j <= i; j++) {
            if (detail != null) sb.append("\r\n").append(num++).append(". ");
            if (trim.contains(j)) sb.append("(");
            sb.append(result.getTimeAt(j, false));
            if (detail != null) {
                String s = result.getString(j, 6);
                if (!TextUtils.isEmpty(s)) sb.append('[').append(s).append(']');
            }
            if (trim.contains(j)) sb.append(")");
            if (detail == null && j < i) sb.append(decimalMark == 0 ? ", " : "; ");
            if (detail != null) sb.append("  ").append(result.getString(j, 4));
        }
        return sb.toString();
    }

    public static String sessionMean(Context context, Result result, String[] detail) {
        StringBuilder sb = new StringBuilder();
        String sessionAvg = result.getSessionAvg();
        if (detail != null) sb.append(context.getString(R.string.stat_title)).append(new java.sql.Date(new Date().getTime())).append("\r\n");
        sb.append(context.getString(R.string.stat_solve)).append(result.getSolved()).append('/').append(result.length()).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_session_mean)).append(timeToString(result.sessionMean())).append(' ');
        sb.append("(σ = ").append(result.getSessionSD()).append(')').append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_session_avg)).append(sessionAvg).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_best)).append(result.getTimeAt(result.getMinIdx(), false)).append(detail != null ? "\r\n" : "\n");
        sb.append(context.getString(R.string.stat_worst)).append(result.getTimeAt(result.getMaxIdx(), false)).append(detail != null ? "\r\n" : "\n");
        if (result.length() >= avg1len)
            sb.append(String.format(avg1Type == 0 ? context.getString(R.string.stat_best_avg) : context.getString(R.string.stat_best_mean), avg1len))
                    .append(result.getBestAvg1()).append("\r\n");
        if (result.length() >= avg2len)
            sb.append(String.format(avg2Type == 0 ? context.getString(R.string.stat_best_avg) : context.getString(R.string.stat_best_mean), avg2len))
                    .append(result.getBestAvg2()).append("\r\n");
        if (detail != null) {
            detail[0] = timeToString(result.sessionMean()) + " (σ = " + result.getSessionSD() + ")";
            detail[1] = sessionAvg;
            detail[2] = result.getTimeAt(result.getMinIdx(), false);
            detail[3] = result.getTimeAt(result.getMaxIdx(), false);
        }
        sb.append(context.getString(R.string.stat_list));
        int maxLen = Math.min(result.length(), 10000);
        for (int i = 0; i < maxLen; i++) {
            if (detail != null) sb.append("\r\n").append(i + 1).append(". ");
            sb.append(result.getTimeAt(i, true));
            if (detail != null) {
                String s = result.getString(i, 6);
                if (!TextUtils.isEmpty(s)) sb.append('[').append(s).append(']');
            }
            if (detail == null && i < result.length() - 1) sb.append(decimalMark == 0 ? ", " : "; ");
            if (detail != null) sb.append("  ").append(result.getString(i, 4));
        }
        return sb.toString();
    }

    public static String binaryArray(byte[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i != 0) sb.append(", ");
            sb.append(a[i] & 0xff);
        }
        sb.append("]");
        return sb.toString();
    }
}

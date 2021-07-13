package com.dctimer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.dctimer.APP;
import com.dctimer.database.DBHelper;
import com.dctimer.util.Stats;
import com.dctimer.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.dctimer.util.StringUtils.timeToString;

public class Result {
    private DBHelper db;
    private Cursor cursor;
    private int sessionId;
    private int[] result;
    private byte[] penalty;
    private int[][] multiPhase;
    private int length;
    public static long[] multemp = new long[7];
    private boolean mp;
    private int offset;
    //private int pos = -1;
    private boolean mod;
    private Stats stats;

    public Result(DBHelper db) {
        this.db = db;
        //stats = new Stats(this);
    }

    public int length() {
        return length;
    }

    public void init(boolean mp, int sid) {
        sessionId = sid;
        this.mp = mp;
        if (sid >= 15) offset = 1;
        else offset = 0;
        cursor = db.getResult(sid);
        length = cursor.getCount();
        Log.w("dct", "成绩 "+length);
        if (length == 0) {
            result = new int[24];
            penalty = new byte[24];
            if (mp) multiPhase = new int[6][24];
            db.lastId = 0;
        } else {
            if (length < 500) {
                result = new int[length * 2];
                penalty = new byte[length * 2];
                if (mp) multiPhase = new int[6][length * 2];
            } else {
                result = new int[length * 3 / 2];
                penalty = new byte[length * 3 / 2];
                if (mp) multiPhase = new int[6][length * 3 / 2];
            }
            cursor.moveToFirst();
            for (int i = 0; i < length; i++) {
                result[i] = cursor.getInt(1 + offset);
                penalty[i] = (byte) cursor.getInt(2 + offset);
                int d = cursor.getInt(3 + offset);
                if (d == 0) penalty[i] = 2;
                if (mp)
                    for (int j = 0; j < 6; j++)
                        multiPhase[j][i] = cursor.getInt(7 + j + offset);
                cursor.moveToNext();
            }
            if (sid < 15) {
                cursor.moveToLast();
                db.lastId = cursor.getInt(0);
            } else db.getLastId();
        }
        stats = new Stats(this);
    }

    public void setMod(boolean mod) {
        this.mod = mod;
    }

    private void expand() {
        byte[] pen;
        int[] res;
        if (length < 1000) {
            pen = new byte[penalty.length * 2];
            res = new int[result.length * 2];
        } else {
            pen = new byte[penalty.length * 3 / 2];
            res = new int[result.length * 3 / 2];
        }
        for (int i = 0; i < length; i++) {
            pen[i] = penalty[i];
            res[i] = result[i];
        }
        penalty = pen;
        result = res;
        if (mp) {
            int[][] mul = new int[6][result.length];
            for (int i = 0; i < length; i++)
                for (int j = 0; j < 6; j++)
                    mul[j][i] = multiPhase[j][i];
            multiPhase = mul;
        }
    }

    private void expand(int num) {
        byte[] pen2 = new byte[penalty.length + num];
        int[] res2 = new int[result.length + num];
        for (int i = 0; i < length; i++) {
            pen2[i] = penalty[i];
            res2[i] = result[i];
        }
        penalty = pen2;
        result = res2;
        if (mp) {
            int[][] mul = new int[6][result.length];
            for (int i = 0; i < length; i++)
                for (int j = 0; j < 6; j++)
                    mul[j][i] = multiPhase[j][i];
            multiPhase = mul;
        }
    }

    public int getPenalty(int i) {
        if (i < 0 || i >= length) return 0;
        return penalty[i];
    }

//    public int getPenaltyTime(int i) {
//        return penalty[i] == 1 ? 2000 : 0;
//    }

    public boolean isDnf(int i) {
        if (i < 0 || i >= length) return false;
        return penalty[i] == 2;
    }

    public int[] getResult() {
        return result;
    }

    public int getResult(int i) {
        if (i < 0 || i >= length)
            return 0;
        return result[i];
    }

    public int getTime(int i) {
        return result[i] + penalty[i] * 2000;
    }

    public int getMulTime(int p, int i) {
        return multiPhase[p][i];
    }

    public void initMulTime() {
        multiPhase = new int[6][result.length];
    }

    public void clearMulTime() {
        multiPhase = null;
    }

    private void addResult(int time, int p, boolean mp) {
        if (length >= result.length) {
            expand();
        }
        penalty[length] = (byte) p;
        result[length] = time;
        if (mp) {
            boolean tag = true;
            for (int i = 0; i < APP.multiPhase + 1; i++) {
                if (tag)
                    multiPhase[i][length] = (int) (multemp[i + 1] - multemp[i]);
                else multiPhase[i][length] = 0;
                if (multiPhase[i][length] < 0 || multiPhase[i][length] > result[length]) {
                    multiPhase[i][length] = 0;
                    tag = false;
                }
            }
        }
        length++;
    }

    public void checkExpand(int num) {
        if (length + num >= result.length) {
            expand(num);
        }
    }

    public void getMulTime() {
        cursor = db.getResult(sessionId);
        for (int i = 0; i < length; i++) {
            cursor.moveToPosition(i);
            for (int j = 0; j < 6; j++)
                multiPhase[j][i] = cursor.getInt(7 + j);
        }
    }

    public String getString(int i, int column) {
        if (mod) {
            cursor = db.getResult(sessionId);
            mod = false;
        }
        cursor.moveToPosition(i);
        return cursor.getString(column + offset);
    }

    public int getId(int i) {
        if (mod) {
            cursor = db.getResult(sessionId);
            mod = false;
        }
        cursor.moveToPosition(i);
        return cursor.getInt(0);
    }

    public String getString(int column) {
        return cursor.getString(column + offset);
    }

    public void insert(int time, int penalty, String scramble, boolean mp, SmartCube cube) {
        addResult(time, penalty, mp);
        int d = 1;
        if (penalty == 2) penalty = d = 0;
        ContentValues cv = new ContentValues();
        //cv.put("id", ++lastId);
        cv.put("rest", time);
        cv.put("resp", penalty);
        cv.put("resd", d);
        cv.put("scr", scramble);
        cv.put("time", StringUtils.getDate());
        if (cube != null) {
            String move = cube.getMoveSequence();
            if (!TextUtils.isEmpty(move))
            cv.put("moves", move);
        }
        if (mp && multiPhase != null)
            for (int i = 0; i < 6; i++)
                cv.put("p" + (i + 1), multiPhase[i][length - 1]);
        db.addResult(sessionId, cv);
        mod = true;
    }

    public void insert(int num, int u, int v, String scr) {
        Random r = new Random();
        checkExpand(num);
        SQLiteDatabase sqliteDb = db.getWritableDatabase();
        sqliteDb.beginTransaction();
        SQLiteStatement stmt = sqliteDb.compileStatement("insert into " + db.getTableName(sessionId) + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (int i = 0; i < num; i++) {
            int time = (int) (Math.sqrt(v) * r.nextGaussian() + u);
            //penalty[length] = (byte) 0;
            result[length++] = time;
            stmt.bindLong(1, ++db.lastId);
            stmt.bindLong(2, time);
            stmt.bindLong(3, 0);
            stmt.bindLong(4, 1);
            stmt.bindString(5, scr);
            stmt.bindString(6, StringUtils.getDate());
            stmt.bindString(7, "");
            for (int j = 8; j < 14; j++)
                stmt.bindLong(j, 0);
            stmt.bindString(14, "");
            stmt.execute();
            stmt.clearBindings();
        }
        sqliteDb.setTransactionSuccessful();
        sqliteDb.endTransaction();
        mod = true;
    }

    public boolean update(int i, int p) {
        if (i < 0 || i >= penalty.length) return false;
        if (penalty[i] == p) return false;
        if (mod) {
            cursor = db.getResult(sessionId);
        }
        penalty[i] = (byte) p;
        int d = 1;
        if (p == 2) p = d = 0;
        cursor.moveToPosition(i);
        int id = cursor.getInt(0);
        db.updateResult(sessionId, id, p, d);
        mod = true;
        return true;
    }

    public void update(int i, String text) {
        if (mod) {
            cursor = db.getResult(sessionId);
        }
        cursor.moveToPosition(i);
        int id = cursor.getInt(0);
        db.updateResult(sessionId, id, text);
        mod = true;
    }

    public int delete(int idx) {
        if (mod) {
            cursor = db.getResult(sessionId);
        }
        if (idx >= cursor.getCount()) {
            return -1;
        }
        cursor.moveToPosition(idx);
        int id = cursor.getInt(0);
        Log.w("dct", "id: "+id);
        if (idx != length - 1) {
            for (int i = idx; i < length - 1; i++) {
                result[i] = result[i + 1];
                penalty[i] = penalty[i + 1];
                if (APP.multiPhase > 0)
                    for (int j = 0; j <= APP.multiPhase; j++)
                        multiPhase[j][i] = multiPhase[j][i + 1];
            }
        }
        length--;
        db.deleteResult(sessionId, id);
        mod = true;
        return 0;
    }

    public void clear() {
        db.clearSession(sessionId);
        length = 0;
        mod = true;
        stats.maxIdx = stats.minIdx = -1;
        stats.mpMean = new int[6];
    }

    public String[] getDates() {
        if (mod) {
            cursor = db.getResult(sessionId);
        }
        String[] date = new String[length];
        cursor.moveToFirst();
        for (int i = 0; i < length; i++) {
            date[i] = cursor.getString(5 + offset);
            cursor.moveToNext();
        }
        return date;
    }

    public List<Integer> search(String key) {
        int i = 0;
        while (i < key.length() && key.charAt(i) == '0') i++;
        int j = key.length() - 1;

        if (i > 0) key = key.substring(i);
        List<Integer> list = new ArrayList<>();
        for (i = 0; i < length; i++) {
            int pos = APP.sortType == 0 ? i : stats.sortIdx[i];
            boolean tag = false;
            if (penalty[pos] != 2) {
                String time = StringUtils.timeToString(getTime(pos));
                if (time.startsWith(key)) {
                    list.add(i);
                    tag = true;
                }
            } else if ("dnf".startsWith(key)) {
                list.add(i);
                tag = true;
            }
            String comment = getString(pos, 6);
            if (!TextUtils.isEmpty(comment) && comment.contains(key) && !tag) {
                list.add(i);
            }
        }
        return list;
    }

    public String getTimeAt(int idx, boolean showTime) {
        if (idx < 0 || idx >= length) return "error";
        int i = result[idx];
        int penalty = getPenalty(idx);
        if (penalty == 2) {
            if (showTime) return "DNF (" + timeToString(i) + ")";
            else return "DNF";
        } else if (penalty == 1)
            return timeToString(i + 2000) + "+";
        else return timeToString(i);
    }

    public int getSolved() {
        return stats.solved;
    }

    public int getMinIdx() {
        return stats.minIdx;
    }

    public String getBestTime() {
        if (getMaxIdx() < 0) return "-";
        return getTimeAt(getMinIdx(), false);
    }

    public boolean isSessionBest() {
        if (length < 2) return false;
        return getMinIdx() == length - 1;
    }

    public int getMaxIdx() {
        return stats.maxIdx;
    }

    public String getWorstTime() {
        if (getMaxIdx() < 0) return "-";
        return getTimeAt(getMaxIdx(), false);
    }

    public int sessionMean() {
        return stats.mean;
    }

    public String getSessionMean() {
        return stats.sessionMean();
    }

    public String getSessionAvg() {
        return stats.sessionAvg();
    }

    public String getSessionSD() {
        return stats.getSD(stats.sd);
    }

    public void calcAvg() {
        stats.calcAvg();
    }

    public int getAvg1(int idx) {
        return stats.avg1[idx];
    }

    public int getAvg2(int idx) {
        return stats.avg2[idx];
    }

    public String getRollingAvg1(int i) {
        if (i < 0 || i >= length) return "-";
        return timeToString(stats.avg1[i]);
    }

    public String getRollingAvg2(int i) {
        if (i < 0 || i >= length) return "-";
        return timeToString(stats.avg2[i]);
    }

    public String[] getAvgDetail(int n, int i, ArrayList<Integer> trim) {
        return stats.getAvgDetail(n, i, trim);
    }

    public String[] getMeanDetail(int n, int i) {
        return stats.getMeanDetail(n, i);
    }


    public int getBestAvgIdx(int i) {
        return stats.bestAvgIdx[i];
    }

    public String getBestAvg1() {
        if (stats.bestAvgIdx[0] == -1) return "N/A";
        return StringUtils.timeToString(stats.bestAvg[0]);
    }

    public String getBestAvg2() {
        if (stats.bestAvgIdx[1] == -1) return "N/A";
        return StringUtils.timeToString(stats.bestAvg[1]);
    }

    public boolean isAvgBest(int i) {
        if (length == 0) return false;
        return stats.bestAvgIdx[i] == length - 1;
    }

    public int getMpMinIdx(int idx) {
        return stats.mpMin[idx];
    };

    public int getMpMaxIdx(int idx) {
        return stats.mpMax[idx];
    }

    public String getMpMean(int idx) {
        return StringUtils.timeToString(stats.mpMean[idx]);
    }

    public void calcMpMean() {
        stats.calcMpMean();
    }

    public void sortResult() {
        stats.sortResult();
    }

    public int getSortIdx(int i) {
        return stats.sortIdx[i];
    }

    public void close() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }
}

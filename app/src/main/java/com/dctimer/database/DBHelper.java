package com.dctimer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dctimer.APP;
import com.dctimer.model.Session;
import com.dctimer.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "spdcube.db";
    public static final String[] TBL_NAME = {"resulttb", "result2", "result3", "result4", "result5", "result6",
            "result7", "result8", "result9", "result10", "result11", "result12", "result13", "result14", "result15", "resultstb"};
    public static final String TBS_NAME = "sessiontb";
    private SQLiteDatabase db;
    public int lastId;
    public int sessionId;

    public DBHelper(Context c) {
        super(c, DB_NAME, null, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        for (int i=0; i<15; i++)
            db.execSQL("create table " + TBL_NAME[i] + "(id integer not null, "
                    + "rest integer not null, resp integer not null, resd integer not null, "
                    + "scr text not null, time text, note text, "
                    + "p1 integer, p2 integer, p3 integer, p4 integer, p5 integer, p6 integer, moves text);");

        db.execSQL("create table sessiontb(id integer not null, name text, type integer, mulp integer, ra integer, sorting integer);");
        db.execSQL("create table resultstb(id integer not null, sid integer not null, "
                + "rest integer not null, resp integer not null, resd integer not null, scr text, time text, note text, "
                + "p1 integer, p2 integer, p3 integer, p4 integer, p5 integer, p6 integer, moves text);");
    }

    public Cursor querySession() {
        if (db == null) db = getWritableDatabase();
        return db.query(TBS_NAME, null, null, null, null, null, null);
    }

    public void getSession(List<Session> sessionList) {
        if (db == null) db = getWritableDatabase();
        Cursor c = db.query(TBS_NAME, null, null, null, null, null, null);
        int count = c.getCount();
        if (count == 0) {
            for (sessionId = 0; sessionId < 15; sessionId++) {
                Session session = new Session(sessionId, APP.sessionName[sessionId], APP.sesionType[sessionId], 0, 8011, sessionId + 1);
                sessionList.add(session);
                ContentValues cv = new ContentValues();
                cv.put("id", sessionId);
                cv.put("name", APP.sessionName[sessionId]);
                cv.put("type", APP.sesionType[sessionId]);
                cv.put("sorting", sessionId + 1);
                db.insert(TBS_NAME, null, cv);
            }
        } else {
            c.moveToFirst();
            for (int i = 0; i < count; i++) {
                int avg = c.getInt(4);
                if (avg == 0) {
                    avg = 8011;
                }
                int sorting = c.getInt(5);
                if (sorting == 0) sorting = sessionList.size() + 1;
                Session session = new Session(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), avg, sorting);
//                id = c.getInt(0);
//                name = c.getString(1);
//                type = c.getInt(2);
                sessionList.add(session);
                c.moveToNext();
            }
            c.moveToLast();
            sessionId = c.getInt(0);
            if (sessionId < 14) sessionId = 14;
        }
        c.close();
    }

    public int getSessionCount(int id) {
        if (db == null) db = getWritableDatabase();
        String sql;
        if (id < 15) sql = "select count(*) from " + TBL_NAME[id];
        else sql = "select count(*) from resultstb where sid=" + id;
        Cursor c = db.rawQuery(sql, null);
        int count = 0;
        if (c.getCount() > 0) {
            c.moveToFirst();
            count = c.getInt(0);
        }
        c.close();
        return count;
    }

    public int addSession(String name) {
        if (db == null) db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", ++sessionId);
        cv.put("name", name);
        cv.put("type", 33);
        cv.put("mulp", 0);
        cv.put("ra", 8011);
        db.insert(TBS_NAME, null, cv);
        return sessionId;
    }

    public void saveSession(List<Session> sessionList) {
        if (db == null) db = getWritableDatabase();
        db.beginTransaction();
        //SQLiteStatement stmt = db.compileStatement("update " + TBS_NAME + " set sorting=? where id=?");
        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            int sorting = session.getSorting();
            int id = session.getId();
            //Log.w("dct", "("+id+") "+session.getName()+"/"+sorting);
            //db.insertWithOnConflict(TBS_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            db.execSQL("UPDATE " + TBS_NAME + " SET sorting=? WHERE id=?", new Object[] {sorting, id});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateSession(int id, ContentValues values) {
        if (db == null) db = getWritableDatabase();
        db.update(TBS_NAME, values, "id=?", new String[] {String.valueOf(id)});
    }

    public void updateName(int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        updateSession(id, cv);
    }

    public void updatePuzzle(int id, int puzzle) {
        ContentValues cv = new ContentValues();
        cv.put("type", puzzle);
        updateSession(id, cv);
    }

    public void updateMultiPhase(int id, int mp) {
        ContentValues cv = new ContentValues();
        cv.put("mulp", mp);
        updateSession(id, cv);
    }

    public void updateAverage(int id, int avg) {
        ContentValues cv = new ContentValues();
        cv.put("ra", avg);
        updateSession(id, cv);
    }

    public void deleteSession(int id) {
        if (db == null) db = getWritableDatabase();
        db.delete(TBS_NAME, "id=?", new String[] {String.valueOf(id)});
        if (id < 15) {
            db.delete(TBL_NAME[id], null, null);
        } else db.delete(TBL_NAME[15], "sid=?", new String[] {String.valueOf(id)});
    }

    public void addResult(int sid, ContentValues values) {
        if (db == null) db = getWritableDatabase();
        if (sid >= 15) {
            values.put("sid", sid);
            sid = 15;
        }
        values.put("id", ++lastId);
        db.insert(TBL_NAME[sid], null, values);
    }

    public Cursor getResult(int id) {
        if (db == null) db = getWritableDatabase();
        if (id < 15)
            return db.query(TBL_NAME[id], null, null, null, null, null, null);
        return db.query(TBL_NAME[15], null, "sid=?", new String[] {String.valueOf(id)}, null, null, null);
    }

    public void getLastId() {
        Cursor c = db.query(TBL_NAME[15], new String[] {"id"}, null, null, null, null, null);
        lastId = 1;
        if (c.getCount() != 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                int id = c.getInt(0);
                if (id > lastId) lastId = id;
                c.moveToNext();
            }
            //c.moveToLast();
            //lastId = c.getInt(0);
        }
        Log.w("dct", "lastid "+lastId);
        c.close();
    }

    public String getTableName(int sessionId) {
        if (sessionId >= 15) return TBL_NAME[15];
        return TBL_NAME[sessionId];
    }

    public void deleteResult(int sid, int id) {
        if (db == null) db = getWritableDatabase();
        if (sid > 15) sid = 15;
        db.delete(TBL_NAME[sid], "id=?", new String[] {String.valueOf(id)});
    }

    public void clearSession(int id) {
        if (db == null) db = getWritableDatabase();
        if (id < 15)
            db.delete(TBL_NAME[id], null, null);
        else db.delete(TBL_NAME[15], "sid=?", new String[] {String.valueOf(id)});
    }

    public void updateResult(int sid, int id, int resp, int resd) {
        if (db == null) db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("resp", resp);
        cv.put("resd", resd);
        if (sid >= 15) sid = 15;
        db.update(TBL_NAME[sid], cv, "id=?", new String[] {String.valueOf(id)});
    }

    private void updateResult(int id, int penalty) {
        if (db == null) db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("penalty", penalty);
        db.update(TBL_NAME[15], cv, "id=?",new String[] {String.valueOf(id)});
    }

    public void updateResult(int sid, int id, String note) {
        if (db == null) db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note", note);
        if (sid > 15) sid = 15;
        db.update(TBL_NAME[sid], cv, "id=?", new String[] {String.valueOf(id)});
    }

    public void close() {
        if (db != null) db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        Log.w("dct", "db upgrade " + oldVer + " -> " + newVer);
        this.db = db;
        if (oldVer < 4) {
            db.execSQL("alter table resulttb add time text;");
            db.execSQL("alter table resulttb add note text;");
            for (int i = 1; i < 7; i++) db.execSQL("alter table resulttb add p" + i + " integer;");
            if (oldVer < 3) {
                for (int i = 2; i < 16; i++) db.execSQL("create table result" + i + "(id integer not null, rest integer not null, resp integer not null, resd integer not null, scr text not null, time text, note text, p1 integer, p2 integer, p3 integer, p4 integer, p5 integer, p6 integer);");
            } else {
                for (int i = 2; i < 10; i++) {
                    db.execSQL("alter table result" + i + " add time text;");
                    db.execSQL("alter table result" + i + " add note text;");
                    for (int j = 1; j < 7; j++) db.execSQL("alter table result" + i + " add p" + j + " integer;");
                }
                for (int i = 10; i < 16; i++) db.execSQL("create table result" + i + "(id integer not null, rest integer not null, resp integer not null, resd integer not null, scr text not null, time text, note text, p1 integer, p2 integer, p3 integer, p4 integer, p5 integer, p6 integer);");
            }
        }
        if (oldVer < 5) {
            db.execSQL("create table sessiontb(id integer not null, name text, type integer, mulp integer, ra integer);");
            db.execSQL("create table resultstb(id integer not null, sid integer not null, result integer not null, penalty integer not null, scramble text, time text, note text, p1 integer, p2 integer, p3 integer, p4 integer, p5 integer, p6 integer);");
        }
        if (oldVer < 6) {
            for (int i = 0; i < TBL_NAME.length; i++) {
                db.execSQL("alter table " + TBL_NAME[i] + " add moves text");
            }
        }
        if (oldVer < 7) {
            db.execSQL("alter table sessiontb add sorting integer");
        }
    }
}
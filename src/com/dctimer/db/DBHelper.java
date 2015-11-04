package com.dctimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "spdcube.db";
	public static final String[] TBL_NAME = {"resulttb","result2","result3","result4","result5","result6",
		"result7","result8","result9","result10","result11","result12","result13","result14","result15"};
	private SQLiteDatabase db;
	
	public DBHelper(Context c) {
		super(c, DB_NAME, null, 4);
	}
	
	@Override  
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL("create table resulttb(id integer not null,rest integer not null,resp integer not null,resd integer not null,scr text not null,time text,note text,p1 integer,p2 integer,p3 integer,p4 integer,p5 integer,p6 integer);");
		for(int i=2; i<16; i++)db.execSQL("create table result"+i+"(id integer not null,rest integer not null,resp integer not null,resd integer not null,scr text not null,time text,note text,p1 integer,p2 integer,p3 integer,p4 integer,p5 integer,p6 integer);");
	}
	
	public void insert(int i, ContentValues values) {
		if (db == null) db = getWritableDatabase();
		db.insert(TBL_NAME[i], null, values);
	}
	
	public Cursor query(int i) {
		if (db == null) db = getWritableDatabase();
		Cursor c = db.query(TBL_NAME[i], null, null, null, null, null, null);
		return c;
	}
	
	public void del(int i, int id) {
		if (db == null) db = getWritableDatabase();
		db.delete(TBL_NAME[i], "id=?", new String[] {String.valueOf(id)});
	}
	
	public void clear(int i) {
		this.getWritableDatabase().delete(TBL_NAME[i], null, null);
	}
	
	public void update(int i, int id, int resp, int resd) {
		if (db == null) db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("resp", resp);
		cv.put("resd", resd);
		db.update(TBL_NAME[i], cv, "id=?",new String[] {String.valueOf(id)});
	}
	
	public void update(int i, int id, String note) {
		if (db == null) db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("note", note);
		db.update(TBL_NAME[i], cv, "id=?",new String[] {String.valueOf(id)});
	}
	
	public void close() {
		if (db != null) db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		if(oldVer > newVer) return;
		this.db = db;
		db.execSQL("alter table resulttb add time text;");
		db.execSQL("alter table resulttb add note text;");
		for(int i=1; i<7; i++)db.execSQL("alter table resulttb add p"+i+" integer;");
		if(oldVer < 3) {
			for(int i=2; i<16; i++) db.execSQL("create table result"+i+"(id integer not null,rest integer not null,resp integer not null,resd integer not null,scr text not null,time text,note text,p1 integer,p2 integer,p3 integer,p4 integer,p5 integer,p6 integer);");
		}
		else if(oldVer < 4){
			for(int i=2; i<10; i++) {
				db.execSQL("alter table result"+i+" add time text;");
				db.execSQL("alter table result"+i+" add note text;");
				for(int j=1; j<7; j++)db.execSQL("alter table result"+i+" add p"+j+" integer;");
			}
			for(int i=10; i<16; i++) db.execSQL("create table result"+i+"(id integer not null,rest integer not null,resp integer not null,resd integer not null,scr text not null,time text,note text,p1 integer,p2 integer,p3 integer,p4 integer,p5 integer,p6 integer);");
		}
		//for(int i=1;i<9;i++)db.execSQL(CREATE_TBL[i]);
	}
}
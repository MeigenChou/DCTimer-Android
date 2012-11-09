package com.dctimer;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
public class DBHelper extends SQLiteOpenHelper {  
	private static final String DB_NAME = "spdcube.db";  
	private static final String[] TBL_NAME = {"resulttb","result2","result3","result4",
		"result5","result6","result7","result8","result9"};
	private static final String[] CREATE_TBL = {"create table resulttb(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result2(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result3(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result4(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result5(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result6(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result7(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result8(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);",
		"create table result9(id integer not null,rest integer not null,resp integer not null,resd integer not null, scr text not null);"};  

	private SQLiteDatabase db;  
	DBHelper(Context c) {
		super(c, DB_NAME, null, 3);  
	}  
	@Override  
	public void onCreate(SQLiteDatabase db) { 
		this.db = db;  
		for(int i=0;i<9;i++)db.execSQL(CREATE_TBL[i]);
	}  
	public void insert(int i, ContentValues values) {  
		SQLiteDatabase db = getWritableDatabase();  
		db.insert(TBL_NAME[i], null, values);  
		db.close();  
	}
	public Cursor query(int i) {  
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TBL_NAME[i], null, null, null, null, null, null);
		return c;
	}  
	public void del(int i, int id) {  
		if (db == null)  
			db = getWritableDatabase();  
		db.delete(TBL_NAME[i], "id=?", new String[] { String.valueOf(id) });  
	}  
	public void clear(int i) {
		this.getWritableDatabase().delete(TBL_NAME[i], null, null);
	}
	public void update(int i, int id, int rest, byte resp, byte resd, String scr) {  
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("rest", rest);
		cv.put("resp", resp);
		cv.put("resd", resd);
		cv.put("scr", scr);
		db.update(TBL_NAME[i], cv, "id=?",new String[] {String.valueOf(id)});  
		db.close();  
	}  
	public void close() {  
		if (db != null)  
			db.close();  
	}  
	@Override  
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		this.db = db;  
		for(int i=1;i<9;i++)db.execSQL(CREATE_TBL[i]);
	}  
}  
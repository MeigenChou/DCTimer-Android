package com.dctimer.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dctimer.DCTimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Session {
	private Cursor cursor;
	private DBHelper dbh;
	public int dbLastId;
	public static int[] rest;
	public static int resl;
	public static byte[] resp;
	public static int[][] mulp = null;
	public static long[] multemp = new long[7];
	private static boolean mark = false;
	private static int crntSes = 0;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Session(Context c) {
		dbh = new DBHelper(c);
	}
	
	public void closeDB() {
		cursor.close();
		dbh.close();
	}
	
	public void getSession(int i, boolean isMulp) {
		crntSes = i;
		cursor = dbh.query(i);
		resl = cursor.getCount();
		if(resl == 0) {
			rest = new int[24];
			resp = new byte[24];
			DCTimer.scrst = new String[24];
			if(isMulp) mulp = new int[6][24];
			dbLastId = 0;
		} else {
			if(resl < 1000) {
				rest = new int[resl*2];
				resp = new byte[resl*2];
				DCTimer.scrst = new String[resl*2];
				if(isMulp) mulp = new int[6][resl*2];
			} else {
				rest = new int[resl*3/2];
				resp = new byte[resl*3/2];
				DCTimer.scrst = new String[resl*3/2];
				if(isMulp) mulp = new int[6][resl*3/2];
			}
			cursor.moveToFirst();
			for(int k=0; k<resl; k++) {
				rest[k] = cursor.getInt(1);
				resp[k] = (byte) cursor.getInt(2);
				if(cursor.getInt(3) == 0) resp[k]=2;
				DCTimer.scrst[k] = cursor.getString(4);
				if(isMulp)
					for(int j=0; j<6; j++)
						mulp[j][k] = cursor.getInt(7+j);
				cursor.moveToNext();
			}
			cursor.moveToLast();
			dbLastId = cursor.getInt(0);
		}
	}
	
	public void move(int pos) {
		if(mark) {
			cursor = dbh.query(crntSes);
			mark = false;
		}
		cursor.moveToPosition(pos);
	}
	
	public int getInt(int i) {
		return cursor.getInt(i);
	}
	
	public String getString(int pos, int i) {
		if(mark) {
			cursor = dbh.query(crntSes);
			mark = false;
		}
		cursor.moveToPosition(pos);
		return cursor.getString(i);
	}
	
	public String getString(int i) {
		return cursor.getString(i);
	}
	
	public void insert(int time, int p, String scr, boolean isMulp) {
		if(resl >= rest.length) {
			expand(isMulp);
		}
		DCTimer.scrst[resl] = scr;
		resp[resl] = (byte) p;
		rest[resl++]=time;
		if(isMulp) {
			boolean temp = true;
			for(int i=0; i<DCTimer.stSel[3]+1; i++) {
				if(temp)
					mulp[i][resl-1] = (int)(multemp[i+1]-multemp[i]);
				else mulp[i][resl-1] = 0;
				if(mulp[i][resl-1]<0 || mulp[i][resl-1]>rest[resl-1]) {
					mulp[i][resl-1]=0; temp=false;
				}
			}
		}
		int d = 1;
		if(p==2) p=d=0;
		ContentValues cv = new ContentValues();
		cv.put("id", ++dbLastId);
		cv.put("rest", time);
		cv.put("resp", p);
		cv.put("resd", d);
		cv.put("scr", scr);
		cv.put("time", formatter.format(new Date()));
		if(isMulp)
			for(int i=0; i<6; i++)
				cv.put("p"+(i+1), mulp[i][resl-1]);
		dbh.insert(crntSes, cv);
		mark = true;
	}
	
	public void expand(boolean isMulp) {
		String[] scr2;
		byte[] rep2;
		int[] res2;
		if(resl < 1000) {
			scr2 = new String[DCTimer.scrst.length*2];
			rep2 = new byte[resp.length*2];
			res2 = new int[rest.length*2];
		} else {
			scr2 = new String[DCTimer.scrst.length*3/2];
			rep2 = new byte[resp.length*3/2];
			res2 = new int[rest.length*3/2];
		}
		for(int i=0; i<resl; i++) {
			scr2[i] = DCTimer.scrst[i];
			rep2[i] = resp[i];
			res2[i] = rest[i];
		}
		DCTimer.scrst = scr2;
		resp = rep2;
		Session.rest = res2;
		if(isMulp) {
			int[][] mulp2 = new int[6][rest.length];
			for(int i=0; i<resl; i++)
				for(int j=0; j<6; j++)
					mulp2[j][i] = mulp[j][i];
			mulp = mulp2;
		}
	}
	
	public void update(int idx, byte p) {
		resp[idx] = p;
		byte d = 1;
		if(p==2) p=d=0;
		cursor.moveToPosition(idx);
		int id = cursor.getInt(0);
		dbh.update(crntSes, id, p, d);
		mark = true;
	}
	
	public void update(String text) {
		int id = cursor.getInt(0);
		dbh.update(crntSes, id, text);
		mark = true;
	}
	
	public void delete(int idx, boolean isMulp) {
		int delId;
		if(idx != resl-1) {
			for(int i=idx; i<resl-1; i++) {
				rest[i]=rest[i+1]; resp[i]=resp[i+1]; DCTimer.scrst[i]=DCTimer.scrst[i+1];
				if(isMulp)
					for(int j=0; j<DCTimer.stSel[3]+1; j++)
						mulp[j][i] = mulp[j][i+1];
			}
			cursor.moveToPosition(idx);
			delId = cursor.getInt(0);
		} else {
			delId = dbLastId;
			if(resl > 1) {
				cursor.moveToPosition(resl-2);
				dbLastId = cursor.getInt(0);
			} else dbLastId = 0;
		}
		dbh.del(crntSes, delId);
		resl--;
		mark = true;
	}
	
	public void clear() {
		dbh.clear(crntSes);
		resl = dbLastId = 0;
		mark = true;
	}
	
	public void getMultData() {
		cursor = dbh.query(crntSes);
		for(int i=0; i<resl; i++) {
			cursor.moveToPosition(i);
			for(int j=0; j<6; j++)
				mulp[j][i] = cursor.getInt(7+j);
		}
	}
}

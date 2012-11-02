package com.reefangel.evolution;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EvolutionDB
{
	private static final  String DATABASE_NAME = "evolution.db";
	private static final int DATABASE_VERSION = 1;
	static final String TABLE_NAME = "params";
	private static Context context;
	static SQLiteDatabase db;

	private SQLiteStatement insertStmt;

	private static final String INSERT = "insert into " + TABLE_NAME + " (logdate,t1,t2,t3,ph,sal,orp,phe,wl) values (?,?,?,?,?,?,?,?,?)";

	public EvolutionDB(Context context) {
		EvolutionDB.context = context;
		OpenHelper openHelper = new OpenHelper(EvolutionDB.context);
		EvolutionDB.db = openHelper.getWritableDatabase();
		this.insertStmt = EvolutionDB.db.compileStatement(INSERT);
	}
	
	public long insert(String t1,String t2,String t3,String ph,String sal,String orp,String phe,String wl) {
		this.insertStmt.bindLong(1, new Date().getTime()/1000);
		this.insertStmt.bindString(2, t1);
		this.insertStmt.bindString(3, t2);
		this.insertStmt.bindString(4, t3);
		this.insertStmt.bindString(5, ph);
		this.insertStmt.bindString(6, sal);
		this.insertStmt.bindString(7, orp);
		this.insertStmt.bindString(8, phe);
		this.insertStmt.bindString(9, wl);
		return this.insertStmt.executeInsert();
	}

	public void deleteAll() {
		db.delete(TABLE_NAME, null, null);
	}

	public List<String[]> selectAll()
	{
		List<String[]> list = new ArrayList<String[]>();
		Cursor cursor = db.query(TABLE_NAME, new String[] { "logdate","t1","t2","t3","ph","sal","orp","phe","wl" }, null, null, null, null, null); 
		int x=0;
		if (cursor.moveToFirst()) {
			do {
				String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8)};
				list.add(b1);
				x=x+1;
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		} 
		cursor.close();
		return list;
	}

	public void delete(int rowId) {
		db.delete(TABLE_NAME, null, null); 
	}

	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY, logdate INTEGER , t1 INTEGER, t2 INTEGER, t3 INTEGER, ph INTEGER, sal INTEGER, orp INTEGER, phe INTEGER, wl INTEGER)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
	
	public void finalize() {
		db.close();
	}
}
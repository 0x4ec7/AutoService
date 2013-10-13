package edu.bistu.hich.autoservice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper{
	private static final String DATABASENAME = "collection.db";
	private static final int DATABASEVERSION = 1;
	public DbOpenHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE collection (" +
				"id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
				"spot_pguid VARCHAR(15) NOT NULL," +
				"spot_name VARCHAR(50) NOT NULL," +
				"spot_addr VARCHAR(100), " +
				"spot_lat DOUBLE NOT NULL," +
				"spot_lon DOUBLE NOT NULL," +
				"spot_phone VARCHAR(80)," +
				"add_time TIMPSTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}

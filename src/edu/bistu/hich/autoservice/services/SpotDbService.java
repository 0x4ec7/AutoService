package edu.bistu.hich.autoservice.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.bistu.hich.autoservice.db.DbOpenHelper;
import edu.bistu.hich.autoservice.entity.Spot;

public class SpotDbService {
	private DbOpenHelper dbOpenHelper;

	public SpotDbService(Context context) {
		super();
		this.dbOpenHelper = new DbOpenHelper(context);
	}

	public boolean insertSpot(Spot spot) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			db.execSQL("INSERT INTO collection(id, spot_pguid, spot_name, spot_addr, spot_lat, spot_lon, spot_phone) VALUES(null, ?, ?, ?, ?, ?, ?)",
					new Object[] { spot.getSpotPguid(), spot.getSpotName(), spot.getSpotAddr(), spot.getSpotLat(), spot.getSpotLon(), spot.getSpotPhone() });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			db.close();
		}
		return true;
	}

	public boolean isAdded(String spotPguid) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) AS count FROM collection WHERE spot_pguid = '" + spotPguid + "'", null);
		if (cursor.moveToNext()) {
			int count = cursor.getInt(cursor.getColumnIndex("count"));
			if (count > 0) {
				return true;
			}
		}
		cursor.close();
		db.close();
		return false;
	}

	public List<Spot> getScrollData() {
		List<Spot> spots = new ArrayList<Spot>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM collection ORDER BY add_time DESC", null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String spotPguid = cursor.getString(cursor.getColumnIndex("spot_pguid"));
			String spotName = cursor.getString(cursor.getColumnIndex("spot_name"));
			String spotAddr = cursor.getString(cursor.getColumnIndex("spot_addr"));
			double spotLat = cursor.getDouble(cursor.getColumnIndex("spot_lat"));
			double spotLon = cursor.getDouble(cursor.getColumnIndex("spot_lon"));
			String spotPhone = cursor.getString(cursor.getColumnIndex("spot_phone"));
			Timestamp addTime = Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("add_time")));
			spots.add(new Spot(id, spotPguid, spotName, spotAddr, spotLat, spotLon, spotPhone, addTime));
		}
		cursor.close();
		db.close();
		return spots;
	}

	public boolean deleteSpot(String spotPguid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			db.execSQL("DELETE FROM collection WHERE spot_pguid=?", new Object[] { spotPguid });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			db.close();
		}
		return true;
	}
}

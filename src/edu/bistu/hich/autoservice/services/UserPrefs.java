package edu.bistu.hich.autoservice.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPrefs {
	private int type;
	private int bound;
	private Context context;
	private SharedPreferences prefs;
	
	private static UserPrefs instance = null;
	
	private UserPrefs(Context context){
		this.context = context;
		this.prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
		this.type = prefs.getInt("type", 0);
		this.bound = prefs.getInt("bound", 0);
	}
	
	public static UserPrefs getInstance(Context context){
		if (instance == null) {
			instance = new UserPrefs(context);
		}
		return instance;
	}
	
	public void savePrefs(int type, int bound){
		prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt("type", type);
		editor.putInt("bound", bound);
		editor.commit();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getBound() {
		return bound;
	}

	public void setBound(int bound) {
		this.bound = bound;
	}
	
}

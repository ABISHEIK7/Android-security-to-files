package com.example.appa;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreference {
	public static final String PREFS_NAME = "pref";
	public static SharedPreferences prefrence;
	public static Editor editor;

	public SharedPreference() {
		super();
	}

	public static void putString(Context context, String text, String text1) {
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.putString(text, text1);
		editor.commit();

	}

	public static String getString(Context context, String PREFS_KEY) {
		String text;
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = prefrence.getString(PREFS_KEY, "");
		return text;
	}

	public static void removeString(Context context, String PREFS_KEY) {

		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.remove(PREFS_KEY);
		editor.commit();
	}

	public static void putInt(Context context, String text, int text1) {
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.putInt(text, text1);
		editor.commit();
	}

	public static int getInt(Context context, String PREFS_KEY) {
		int text;
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = prefrence.getInt(PREFS_KEY, 0);
		return text;
	}

	public static void removeInt(Context context, String PREFS_KEY) {

		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.remove(PREFS_KEY);
		editor.commit();
	}

	public static void putBoolean(Context context, String text, Boolean text1) {
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.putBoolean(text, text1);
		editor.commit();
	}

	public static Boolean getBoolean(Context context, String PREFS_KEY) {
		boolean text;
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		text = prefrence.getBoolean(PREFS_KEY, false);
		return text;
	}

	public static void removeBoolean(Context context, String PREFS_KEY) {

		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.remove(PREFS_KEY);
		editor.commit();
	}

	public static void clearSharedPreference(Context context) {
		prefrence = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = prefrence.edit();

		editor.clear();
		editor.commit();
	}
}

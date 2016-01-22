package com.example.readobffile;

import android.util.Log;

/**
 * ¥Ú”°Log–≈œ¢
 * @author weijiang204321
 *
 */
public class AppListYPLog {

	private final static String TAG = "APPLIST";
	private static boolean SHOW_LOG = true;

	private AppListYPLog() {
	}

	public static void closeLog() {
		SHOW_LOG = false;
	}

	public static void i(String msg) {
		if (SHOW_LOG) {
			Log.i(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (SHOW_LOG) {
			Log.d(TAG, msg);
		}
	}

	public static void w(Exception ex) {
		if (SHOW_LOG) {
			ex.printStackTrace();
		}
	}

	public static void e(String msg) {
		if (SHOW_LOG) {
			Log.e(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (SHOW_LOG) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (SHOW_LOG) {
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (SHOW_LOG) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (SHOW_LOG) {
			Log.e(tag, msg);
		}
	}

}

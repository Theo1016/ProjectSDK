package com.theo.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * preference������
 * @author Theo
 *
 */
public class PreferenceUtils {

	private final static String SHARE_NAME = "calabar_preference";

	static SharedPreferences sPreferences;

	/**
	 * �洢���ݵ���ʱ�ļ���
	 * @param context
	 * @param key
	 * @param share_name  ��ʱ�ļ���  ��Ϊnull,Ĭ��calabar_preference
	 * @param content
	 */
	public static void saveData(Context context, String key, String share_name, String content) {
		initPreference(context, share_name);
		Editor editor = sPreferences.edit();
		editor.putString(key, content);
		editor.commit();
		editor.clear();
	}

	public static String getStringData(Context context, String key, String share_name) {
		initPreference(context, share_name);
		return sPreferences.getString(key, "");
	}

	public static int getIntData(Context context, String key, String share_name) {
		initPreference(context, share_name);
		return sPreferences.getInt(key, 0);
	}

	private static void initPreference(Context context, String share_name) {
		sPreferences = context.getSharedPreferences(TextUtils.isEmpty(share_name) ? SHARE_NAME : share_name,
				Context.MODE_PRIVATE);
	}
}

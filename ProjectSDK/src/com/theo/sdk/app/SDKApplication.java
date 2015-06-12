package com.theo.sdk.app;

import com.theo.sdk.manager.ImageManager;
import com.theo.sdk.thread.CacheSizeThread;
import com.theo.sdk.utils.AppUtils;
import android.app.Application;
import android.content.Context;

/**
 * Application����
 * @author Theo
 *
 */
public class SDKApplication extends Application{
	public static String logSwitch;
	public static Context appContext ;
	private static CacheSizeThread myCacheSizeThread;
	@Override
	public void onCreate() {
		super.onCreate();
		getLogSwitch();
		appContext = getApplicationContext();
		ImageManager.initImageLoader(appContext);
		// ʵʱ����ڴ�
		myCacheSizeThread = new CacheSizeThread();
		myCacheSizeThread.start();
	}

	/**
	 * ��ȡ��־����
	 */
	private void getLogSwitch() {
		try {
			logSwitch = (String) AppUtils.getMetaData(getApplicationContext(), "log.switch");
		} catch (Exception e) {
		}
	}
}

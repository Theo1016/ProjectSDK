package com.theo.sdk.control;

import java.util.Collection;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theo.sdk.constant.Const;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * »º´æ»Øµ÷
 * @author Theo
 *
 */
@SuppressLint("NewApi") 
public class MemeryCallBack implements ComponentCallbacks2{
	@Override
	public void onConfigurationChanged(Configuration arg0) {
	
		
	}

	@Override
	public void onLowMemory() {
		
		
	}

	@Override
	public void onTrimMemory(int arg0) {
		releaseCache();
	}

	/**
	 * Çå³ýËùÓÐÍ¼Æ¬»º´æ
	 */
	public static void cleanCache() {
		ImageLoader.getInstance().clearMemoryCache();
	}

	/**
	 * ÊÍ·Å»º´æ
	 */
	public static void releaseCache() {
		MemoryCacheAware<String, Bitmap> memoryCacheAware = ImageLoader.getInstance().getMemoryCache();
		Collection<String> listStrings = ImageLoader.getInstance().getMemoryCache().keys();
		int i = 0;
		for (String string : listStrings) {
			memoryCacheAware.remove(string);
			Log.i(Const.LogTag, "=========> ÊÍ·Å»º´ækey" + string);
			i++;
			if (i == listStrings.size() / 2) {
				return;
			}
		}
	}

	
}

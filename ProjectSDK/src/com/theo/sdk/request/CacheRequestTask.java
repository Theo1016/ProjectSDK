package com.theo.sdk.request;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.theo.sdk.constant.Const;
import android.text.TextUtils;
import android.util.Log;

/**
 * ���洦��Task
 * 
 * @author Theo
 */
public class CacheRequestTask implements Runnable {

	/** log tag. */
	private static final String TAG = CacheRequestTask.class.getSimpleName();

	/** if enabled, logcat will output the log. */
	private static final boolean DEBUG = true & Const.DEBUG;

	/**
	 * �̳߳�
	 */
	private static final ExecutorService THREAD_POOL = Executors
			.newFixedThreadPool(5,
					new NamingThreadFactory("CacheRequestTask"));

	/** cache ���� */
	private DataCache mDataCache;

	/**
	 * ��ȡ���ݽ����Listener
	 */
	private OnCacheRequestListener mOnHttpRequestListener;

	/**
	 * ���캯��
	 * 
	 * @param dataCache
	 *            dataCache
	 * @param listener
	 *            �ص�Listener
	 */
	public CacheRequestTask(DataCache dataCache, OnCacheRequestListener listener) {
		mDataCache = dataCache;
		mOnHttpRequestListener = listener;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public final void run() {

		if (mDataCache == null || !mDataCache.exist()) {
			return;
		}

		if (DEBUG) {
			Log.d(Const.LogTag,
					"---- start cache request time:"
							+ System.currentTimeMillis());
		}

		if (mOnHttpRequestListener == null) {
			return;
		}

		String ret = mDataCache.load();

		if (TextUtils.isEmpty(ret)) {
			mOnHttpRequestListener
					.onFailed(IRequestErrorCode.ERROR_CODE_NET_FAILED);
		} else {
			mOnHttpRequestListener.onSuccess(ret);
		}

	}

	/**
	 * ����ִ��
	 */
	public void execute() {
		THREAD_POOL.execute(this);
	}

	/**
	 * ��ȡ���ݽ����Listener
	 */
	public interface OnCacheRequestListener {
		/**
		 * ��ȡ���ݳɹ�
		 * 
		 * @param result
		 *            ��ȡ����String����
		 */
		void onSuccess(String result);

		/**
		 * ��ȡ����ʧ��
		 * 
		 * @param errorCode
		 *            ������
		 */
		void onFailed(int errorCode);
	}

	
}

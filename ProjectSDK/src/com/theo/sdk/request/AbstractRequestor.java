package com.theo.sdk.request;

import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import com.theo.sdk.constant.Const;
import com.theo.sdk.request.CacheRequestTask.OnCacheRequestListener;
import com.theo.sdk.request.HttpRequestHandler.OnHttpRequestHandlerListener;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * 
 * Requestor��������ȡ���������Ϣ���������õ������� Requestor�Ļ��࣬���еĽӿ�Requestor�����϶������̳�
 * ִ��doRequest()������ʼ�첽��ȡ���ݣ�����onReqeustListener�����ص����״̬
 * 
 * @author Theo
 */
public abstract class AbstractRequestor {

	/** context */
	protected Context mContext;
	/**
	 * ���߳�Handler
	 */
	private Handler mHandler;

	/** Cache ���� */
	private DataCache mDataCache;
	
	/** �Ƿ��������� */
	private boolean mReadCacheFlag;
	
	/** �Ƿ���д���� */
	private boolean mWriteCacheFlag;

	/** �������ݽӿ� */
	private ParseDataInterface parseDateInterface;

	/** �������ݽӿ� */
	private RequestInterface requestInterface;

	/** �Ƿ���ʱ��ȡ�������ص������Է�ֹ����UI */
	private boolean mParseNetDataDelayed = false;
	
	/** ��ȡ���ػ�������ϻ������Сʱ���� */
	private static final long TIME_INTERVAL = 2 * DateUtils.SECOND_IN_MILLIS;
	/**
	 * �����������ص�Listener
	 */
	protected OnRequestListener mOnRequestListener;
	
	/**
	 * Http�������ص�Listener
	 */
	protected OnHttpRequestHandlerListener mOnHttpRequestHandlerListener;

	/** ��ȡcache listener */
	private OnCacheLoadListener mCacheLoadListener;

	/** ������ */
	private int mErrorCode = IRequestErrorCode.ERROR_CODE_UNKNOW;

	/** DEBUG */
	public static final boolean DEBUG = true & Const.DEBUG;

	/**
	 * �Ƿ������Ѿ�ȡ��
	 */
	private boolean mIsCanceled;

	/**
	 * �������
	 * 
	 * @return Parameters
	 */
	protected abstract List<NameValuePair> getRequestParams();

	/**
	 * �����ַ
	 * 
	 * @return Url
	 */
	protected abstract String getRequestUrl();

	/**
	 * ���캯��
	 * 
	 * @param context
	 *            Context
	 */
	public AbstractRequestor(Context context) {
		mContext = context.getApplicationContext();
	}

	/**
	 * ���ý�����ʽ
	 * 
	 * @param parseDataInterface
	 */
	public void setParseData(ParseDataInterface parseData) {
		this.parseDateInterface = parseData;
	}
	
	/**
	 * ��������ʽ
	 * @param request
	 */
	public void setRequestMethod(RequestInterface request){
		this.requestInterface = request;
	}

	/**
	 * �Ƿ��п��û�������
	 * 
	 * @return �Ƿ��п��û�������
	 */
	boolean canUseCache() {
		return mDataCache != null && mReadCacheFlag;
	}

	/**
	 * �����Ƿ���ʱ��ȡ�������ص������Է�ֹ����UI��sleep�߳�
	 * 
	 * @param parseNetDataDelayed
	 *            true:��ʱ����
	 */
	public void setParseNetDataDelayed(boolean parseNetDataDelayed) {
		this.mParseNetDataDelayed = parseNetDataDelayed;
	}

	/**
	 * ��ȡ���̵߳�handler
	 * 
	 * @return ���̵߳�handler
	 */
	public synchronized Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		return mHandler;
	}

	/**
	 * �Ƿ���Ҫ������������
	 * 
	 * @return �Ƿ���Ҫ������������
	 */
	boolean needCacheData() {
		return mDataCache != null && mWriteCacheFlag;
	}

	/**
	 * ��д���湦��
	 * 
	 * @param cacheId
	 *            �����ļ�����
	 */
	public void turnOnWriteCache(String cacheId) {
		turnOnCache(cacheId, null, mReadCacheFlag, true);
	}

	/**
	 * �򿪶����湦��
	 * 
	 * @param cacheId
	 *            �����ļ�����
	 * @param cacheLoadListener
	 *            �����ȡ�ɹ��Ļص�
	 */
	public void turnOnReadCache(String cacheId,
			OnCacheLoadListener cacheLoadListener) {
		turnOnCache(cacheId, cacheLoadListener, true, mWriteCacheFlag);
	}

	/**
	 * ִ�д򿪻��湦�ܵķ�����˽�в����⿪��
	 * 
	 * @param cacheId
	 *            �����ļ���
	 * @param cacheLoadListener
	 *            Listener���ڶ�ȡ����ɹ�ʱ�����
	 * @param readFlag
	 *            �Ƿ��������湦��
	 * @param writeFlag
	 *            �Ƿ��������湦��
	 */
	private void turnOnCache(String cacheId,
			OnCacheLoadListener cacheLoadListener, boolean readFlag,
			boolean writeFlag) {
		if (TextUtils.isEmpty(cacheId)) {
			return;
		}
		mCacheLoadListener = cacheLoadListener;
		mDataCache = new DataCache(cacheId, mContext.getCacheDir(),
				mContext.getAssets());
		mReadCacheFlag = readFlag;
		mWriteCacheFlag = writeFlag;
	}

	/**
	 * �ر�cache,Ĭ��Ϊ�ر�״̬
	 */
	public void turnOffCache() {
		mDataCache = null;
		mCacheLoadListener = null;

		mReadCacheFlag = false;
		mWriteCacheFlag = false;
	}

	/**
	 * ������������
	 * 
	 * @param listener
	 *            ����������Listener
	 */
	public void doRequest(OnRequestListener listener) {
		// ��������,��ʹ�û���
		useCacheIfCould();
		//��ʼ����������ص�
		init(mOnRequestListener);
		// ��������
		requestInterface.request(mOnHttpRequestHandlerListener);
		
	}

	/**
	 * ��ʼ������Ļص�����������Ҫ����
	 */
	private void init(OnRequestListener listener) {
		mOnRequestListener = listener;
		// ��������ʱ��Ҫ��ص�
		if (mOnRequestListener != null) {
			getHandler();
			mOnHttpRequestHandlerListener = new HttpRequestHandler.OnHttpRequestHandlerListener() {
				@Override
				public void onSuccess(String result) {
					// �������Ѿ���Cancel
					if (mIsCanceled) {
						return;
					}
					// ��������
					boolean parseResult = false;
					try {
						if (DEBUG) {
							Log.d(Const.LogTag, "abs requestor result:"
									+ result);
						}
						parseResult = parseDateInterface.parseResult(result);
						if (parseResult) {
							responseRequestSuccess();
						} else {
							responseRequestFailed(mErrorCode);
						}
					} catch (JSONException je) {
						responseRequestFailed(IRequestErrorCode.ERROR_CODE_RESULT_IS_NOT_JSON_STYLE);
						je.printStackTrace();
					} catch (Exception e) {
						responseRequestFailed(IRequestErrorCode.ERROR_CODE_PARSE_DATA_ERROR);
						e.printStackTrace();
					}

					// �����ɹ��ٽ��д洢
					if (parseResult) {
						cacheDataIfNeed(result);
					}
				}

				@Override
				public void onFailed(final int errorCode) {

					// �������Ѿ���Cancel
					if (mIsCanceled) {
						return;
					}
					responseRequestFailed(errorCode);
				}
			};
		}
	}

	/**
	 * �����Ҫ,��������
	 * 
	 * @param data
	 *            ����
	 */
	private void cacheDataIfNeed(String data) {
		if (DEBUG) {
			Log.d(Const.LogTag, "cacheDataIfNeed data:" + data);
		}
		if (mDataCache == null) {
			return;
		}

		if (needCacheData()) {
			mDataCache.save(data);
		}
	}

	/**
	 * ����������ȡ�ɹ��Ľ����Listener
	 */
	private void responseRequestSuccess() {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mOnRequestListener.onSuccess(AbstractRequestor.this);
				}
			});
		} else {
			mOnRequestListener.onSuccess(AbstractRequestor.this);
		}
	}

	/**
	 * ����������ȡʧ�ܵĽ����Listener
	 * 
	 * @param errorCode
	 *            ������
	 */
	private void responseRequestFailed(final int errorCode) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mOnRequestListener.onFailed(AbstractRequestor.this,
							errorCode);
				}
			});
		} else {
			mOnRequestListener.onFailed(AbstractRequestor.this, errorCode);
		}
	}

	/**
	 * ��ο���,ʹ�û�������
	 */
	private void useCacheIfCould() {
		if (mDataCache == null) {
			return;
		}
		if (canUseCache()) {
			new CacheRequestTask(mDataCache, new OnCacheRequestListener() {
				@Override
				public void onSuccess(String result) {
					// ��������
					boolean isParseSuccess = false;
					try {
						isParseSuccess = parseDateInterface.parseResult(result);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (isParseSuccess) {
						// �ص�
						if (mHandler != null) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if (mCacheLoadListener != null) {
										mCacheLoadListener
												.onCacheLoaded(AbstractRequestor.this);
									}
								}
							});
						} else {
							if (mCacheLoadListener != null) {
								mCacheLoadListener
										.onCacheLoaded(AbstractRequestor.this);
							}
						}
					}
				}

				@Override
				public void onFailed(int errorCode) {
				}
			}).execute();
		}
	}


	/**
	 * ����ص�
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnCacheLoadListener {

		/**
		 * cache��ȡ�ɹ�
		 * 
		 * @param requestor
		 *            requestor
		 */
		void onCacheLoaded(AbstractRequestor requestor);
	}

	/**
	 * ǰ�����ݻص�
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnRequestListener {

		/**
		 * ��ȡ�ɹ�
		 * 
		 * @param requestor
		 *            requestor
		 */
		void onSuccess(AbstractRequestor requestor);

		/**
		 * ��ȡʧ��
		 * 
		 * @param requestor
		 *            requestor
		 * @param errorCode
		 *            ������
		 */
		void onFailed(AbstractRequestor requestor, int errorCode);

	}

}

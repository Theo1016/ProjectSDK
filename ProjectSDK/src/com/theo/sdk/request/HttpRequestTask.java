package com.theo.sdk.request;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import com.theo.sdk.constant.Const;
import android.content.Context;
import android.os.Process;
import android.util.Log;

/**
 * 
 * ��������
 * 
 * @author Theo
 * 
 */
public class HttpRequestTask implements Runnable {
	/** log tag. */
	private final String TAG = Const.LogTag;

	/** if enabled, logcat will output the log. */
	private final boolean DEBUG = true & Const.DEBUG;

	/** ��������ʧ��ʱ�����Լ��ʱ�� */
	private final long SLEEP_TIME_WHILE_REQUEST_FAILED = 1000L;

	private AbstractHttpClient client;
	
	private HttpUriRequest request;
	
	private HttpContext context;
	
	private int executionCount;
	
    private boolean isCancelled;
    
    private boolean isFinished;
    
    private boolean cancelIsNotified;

	/**
	 * �̳߳�
	 */
	private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(5,
			new NamingThreadFactory("HttpRequestTask"));

	/**
	 * �����ύ��ʽ
	 */
	public enum RequestType {
		/** GET��ʽ�ύ */
		GET,
		/** POST��ʽ�ύ */
		POST;
	}

	/**
	 * �����ύ�ԣ�Ĭ��Post�ύ
	 */
	private RequestType mRequestType = RequestType.POST;

	/**
	 * �����Ƿ��Ѿ�ɾ��
	 */
	private AtomicBoolean mIsCancel = new AtomicBoolean();

	/**
	 * �߳����ȼ�
	 */
	private int mPriority;

	/**
	 * ����Url
	 */
	private String mUrl;

	/**
	 * ����
	 */
	private List<NameValuePair> mParams;

	/**
	 * ��������������
	 */
	private OnHttpRequestTaskListener mOnHttpRequestTaskListener;
	
	/**
	 * 
	 */
	private ResponseHandlerInterface responseHandler;
	
	/** context */
	private Context mContext;

	/** Url������ */
	private URLFilter mURLFilter;

	/**
	 * ���캯��
	 * 
	 * @param context
	 *            Context
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param listener
	 *            �ص�Listener
	 */
	public HttpRequestTask(Context context, String url,
			List<NameValuePair> params, ResponseHandlerInterface listener) {
		this(context, url, params, Process.THREAD_PRIORITY_DEFAULT, listener);
	}
	
	/**
	 * ���캯��
	 * 
	 * @param context
	 *            Context
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param priority
	 *            �߳����ȼ�
	 * @param responseHandlerInterface
	 *            �ص�responseHandlerInterface
	 */
	public HttpRequestTask(Context context, String url,
			List<NameValuePair> params, int priority,
			ResponseHandlerInterface responseHandlerInterface) {
		mContext = context.getApplicationContext();
		mPriority = priority;
		mUrl = url;
		mParams = params;
		responseHandler = responseHandlerInterface;
	}

	/**
	 * @throws Exception
	 */
	@Override
	public final void run() {
		if (DEBUG) {
			Log.d(TAG,
					"---- start web request time:" + System.currentTimeMillis());
		}
		// �߳��Ż���
		Process.setThreadPriority(mPriority);
		if (mIsCancel.get()) {
			// �����Ѿ�����
			return;
		}
		if (mUrl == null) {
			if (mOnHttpRequestTaskListener != null) {
				mOnHttpRequestTaskListener
						.onFailed(IRequestErrorCode.ERROR_CODE_NO_URL);
			}
			return;
		}
		// ����Url
		if (mURLFilter != null) {
			mUrl = mURLFilter.filter(mUrl, mParams);
		}
		try {
			makeRequestWithRetries();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mOnHttpRequestTaskListener != null) {
			mOnHttpRequestTaskListener
					.onFailed(IRequestErrorCode.ERROR_CODE_NET_FAILED);
		}
	}

	private void makeRequestWithRetries() throws IOException {
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		try {
			while (retry) {
				try {
					makeRequest();
					return;
				} catch (UnknownHostException e) {
					cause = new IOException("UnknownHostException exception: "
							+ e.getMessage());
					retry = (executionCount > 0)
							&& retryHandler.retryRequest(cause,
									++executionCount, context);
				} catch (NullPointerException e) {
					cause = new IOException("NPE in HttpClient: "
							+ e.getMessage());
					retry = retryHandler.retryRequest(cause, ++executionCount,
							context);
				} catch (IOException e) {
					if (isCancelled()) {
						return;
					}
					cause = e;
					retry = retryHandler.retryRequest(cause, ++executionCount,
							context);
				}
				if (retry && (responseHandler != null)) {
					responseHandler.sendRetryMessage(executionCount);
				}
			}
		} catch (Exception e) {
			// catch anything else to ensure failure message is propagated
			Log.e("AsyncHttpRequest", "Unhandled exception origin cause", e);
			cause = new IOException("Unhandled exception: " + e.getMessage());
		}
		// cleaned up to throw IOException
        throw (cause);
	}
	
	private void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }

        // Fixes #115
        if (request.getURI().getScheme() == null) {
            // subclass of IOException so processed in the caller
            throw new MalformedURLException("No valid URI scheme was provided");
        }

        HttpResponse response = client.execute(request, context);

        //��ȡsessionid�����ý�header
        Header h = response.getFirstHeader("Set-Cookie");
        if(h != null){
        	String sessionid = h.getValue();
        	
        }
        
        if (isCancelled() || responseHandler == null) {
            return;
        }

        // Carry out pre-processing for this response.
        responseHandler.onPreProcessResponse(responseHandler, response);

        if (isCancelled()) {
            return;
        }

        // The response is ready, handle it.
        responseHandler.sendResponseMessage(response);

        if (isCancelled()) {
            return;
        }

        // Carry out post-processing for this response.
        responseHandler.onPostProcessResponse(responseHandler, response);
    }
	
	public boolean isCancelled() {
        if (isCancelled) {
            sendCancelNotification();
        }
        return isCancelled;
    }

    private synchronized void sendCancelNotification() {
        if (!isFinished && isCancelled && !cancelIsNotified) {
            cancelIsNotified = true;
            if (responseHandler != null)
                responseHandler.sendCancelMessage();
        }
    }

	public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled = true;
        request.abort();
        return isCancelled();
    }
	/**
	 * ����ִ��
	 */
	public void execute() {
		THREAD_POOL.execute(this);
	}

	/**
	 * @return �Ƿ��Ѿ�����
	 */
	public boolean isCancel() {
		return mIsCancel.get();
	}

	/**
	 * @param ��������ִ��
	 */
	public void cancel() {
		mIsCancel.set(true);
	}

	/**
	 * @return the mRequestType
	 */
	public RequestType getRequestType() {
		return mRequestType;
	}

	/**
	 * @param requestType
	 *            the mRequestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.mRequestType = requestType;
	}

	/**
	 * ���ù�����
	 * 
	 * @param urlFilter
	 *            ������
	 */
	public void setURLFilter(URLFilter urlFilter) {
		this.mURLFilter = urlFilter;
	}

	/**
	 * ��ȡ���ݽ����Listener
	 */
	public interface OnHttpRequestTaskListener {
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

	/**
	 * Url������
	 */
	public interface URLFilter {
		/**
		 * ����Url
		 * 
		 * @param requestUrl
		 *            �����Url
		 * @param params
		 *            �������
		 * @return ���˺��Url
		 */
		String filter(String requestUrl, List<NameValuePair> params);
	}
}

package com.theo.sdk.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import com.theo.sdk.constant.Const;

public class HttpRequestHandler implements RequestInterface{
	
	/**
	 *  context 
	 */
	protected Context mContext;
	
	/** 
	 * ����Url
	 */
	private String requestUrl;
	
	/** 
	 * �������
	 */
	private List<NameValuePair> requestParam;
	
	/**
	 * �Ƿ������Ѿ�ȡ��
	 */
	private boolean mIsCanceled;
	
	/**
	 * �����������ص�Listener
	 */
	private OnHttpRequestHandlerListener mOnHttpRequestHandlerListener;
	
	/**
	 * �첽Http�ص�
	 */
	private AsyncHttpResponseHandler mAsyncHttpResponseHandler;
	
	/**
	 * ������������Task
	 */
	private HttpRequestTask mHttpRequestTask;
	
	/**
	 * �߳����ȼ�
	 */
	private int mPriority = Process.THREAD_PRIORITY_DEFAULT;
	
	/** 
	 * DEBUG
	 */
	public static final boolean DEBUG = true & Const.DEBUG;
	
	/**
	 * �����ύ�ԣ�Ĭ��Post�ύ
	 */
	private HttpRequestTask.RequestType mRequestType = HttpRequestTask.RequestType.POST;
	
	
	
	public HttpRequestHandler(Context ctx,String url,List<NameValuePair> param){
		this.mContext= ctx;
		this.requestUrl = url;
		this.requestParam = param;
	}

	
	/**
	 * ������������
	 * 
	 * @param listener
	 *            ����������Listener
	 */
	@Override
	public void request(final OnHttpRequestHandlerListener listener) {
		//��ʼ������Ļص�����������Ҫ����
		init();
		mOnHttpRequestHandlerListener = listener;
		mHttpRequestTask = new HttpRequestTask(mContext, requestUrl,
				requestParam, mPriority, mAsyncHttpResponseHandler);
		mHttpRequestTask.setRequestType(mRequestType);
		mHttpRequestTask.setURLFilter(new HttpRequestTask.URLFilter() {
			@Override
			public String filter(String requestUrl, List<NameValuePair> params) {
				// ���˲����ͳһ��Ҫ��Url����
				String url = filterParams(requestUrl, params);
				return url;
			}
		});
	}
	
	/**
	 * ��ʼ������Ļص�����������Ҫ����
	 */
	private void init() {
		// ��������ʱ��Ҫ��ص�
		if (mOnHttpRequestHandlerListener != null) {
			mAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					mOnHttpRequestHandlerListener.onSuccess(responseBody.toString());
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					// �������Ѿ���Cancel
					if (mIsCanceled) {
						return;
					}
					mOnHttpRequestHandlerListener.onFailed(statusCode);
				}
			};
		}
	}
	
	/**
	 * ���˲�������Url����Щ��Param�г��ֵĲ������˵�
	 * 
	 * @param orginalUrl
	 *            ԭʼ����Url
	 * @param params
	 *            �������
	 * @return ���˺��Url
	 */
	private String filterParams(String orginalUrl, List<NameValuePair> params) {

		String url = orginalUrl;

		if (params != null) {
			for (NameValuePair param : params) {
				Pattern pattern = Pattern.compile("[\\?\\&]" + param.getName()
						+ "\\=[^\\&\\?]*");
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {
					String group = matcher.group();
					if (group.startsWith("?")) {
						url = matcher.replaceAll("?");
					} else {
						url = matcher.replaceAll("");
					}
				}
			}
		}

		return url;
	}
	
	/**
	 * ���ݻص�
	 * 
	 * @author Theo
	 * 
	 */
	public interface OnHttpRequestHandlerListener {
		
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

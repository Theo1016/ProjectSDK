package com.theo.sdk.request;

import com.theo.sdk.request.HttpRequestHandler.OnHttpRequestHandlerListener;

public interface RequestInterface {
	/**
     * ������������
     * @param listener ����������Listener
     */
    public void request(final OnHttpRequestHandlerListener listener);
}

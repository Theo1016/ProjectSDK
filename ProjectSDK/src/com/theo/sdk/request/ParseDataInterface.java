package com.theo.sdk.request;

import org.json.JSONException;

public interface ParseDataInterface {
	
	/**
     * ������ȡ����JSON����
     * @param result result
     * @return �Ƿ�����ɹ���ע�⣺�������ʧ�ܣ������ô�����mErrorCode
     * @throws JSONException JSONException
     * @throws Exception Exception
     */
	public boolean parseResult(String result) throws JSONException, Exception;

}

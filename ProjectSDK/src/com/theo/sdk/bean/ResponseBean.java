package com.theo.sdk.bean;

import java.util.Arrays;

import org.apache.http.Header;

/**
 * ����BEAN
 * @author Theo
 *
 */
public class ResponseBean {
	// ��Ӧ��
	public int code;
	// ������ķ�������
	public Object data;
	// ������Ϣ
	public String errorMsg;
	// Ͷͷ��
	public Header[] headers;
	// ÿһ�������Ψһ��־��
	public String flagId;
	// �����Ƿ����Ի���
	public int isFromCache;
	// �����쳣��Ϣ
	public Throwable throwable;

	@Override
	public String toString() {
		return "ResponseBean [code=" + code + ", data=" + data + ", errorMsg="
				+ errorMsg + ", headers=" + Arrays.toString(headers)
				+ ", flagId=" + flagId + ", isFromCache=" + isFromCache + "]";
	}

}

package com.theo.sdk.constant;

/**
 * ����
 * 
 * @author Theo
 * 
 */
public class Const {
	/** ͼƬ���� */
	public static final int memeryCacheSize = 20 * 1024 * 1024;
	public static final int disCacheSize = 100 * 1024 * 1024;
	public static final int disCacheCount = 500;
	/** �ַ����ݻ��� */
	public static final int DISCACHESIZESTRING = 50 * 1024 * 1024;
	/** �ַ����ݻ���ʱ�� */
	public static final long CACHEALIVETIME = 5 * 60 * 1000;
	/** �������� */
	public static final String CACHENAME = "TEMP_CACHE";
	public static final int CACHE_TIME_DEFAULT = 60 * 1000;
	public static final int TIME_HOUR = 60 * 60;
	public static final int TIME_DAY = TIME_HOUR * 24;
	/** �Զ�����ʱ�䣬Ĭ�ϻ���ʱ��ΪCACHE_TIME_DEFAULT */
	public static final boolean isAutoTime = true;
	/** ��־ͳһTag */
	public static final String LogTag = "Calabar";
	/** DEBUG���� */
	public static final boolean DEBUG = false;
}

package com.theo.sdk.constant;

/**
 * 常量
 * 
 * @author Theo
 * 
 */
public class Const {
	/** 图片缓存 */
	public static final int memeryCacheSize = 20 * 1024 * 1024;
	public static final int disCacheSize = 100 * 1024 * 1024;
	public static final int disCacheCount = 500;
	/** 字符数据缓存 */
	public static final int DISCACHESIZESTRING = 50 * 1024 * 1024;
	/** 字符数据缓存时间 */
	public static final long CACHEALIVETIME = 5 * 60 * 1000;
	/** 缓存名称 */
	public static final String CACHENAME = "TEMP_CACHE";
	public static final int CACHE_TIME_DEFAULT = 60 * 1000;
	public static final int TIME_HOUR = 60 * 60;
	public static final int TIME_DAY = TIME_HOUR * 24;
	/** 自动缓存时间，默认缓存时间为CACHE_TIME_DEFAULT */
	public static final boolean isAutoTime = true;
	/** 日志统一Tag */
	public static final String LogTag = "Calabar";
	/** DEBUG开关 */
	public static final boolean DEBUG = false;
}

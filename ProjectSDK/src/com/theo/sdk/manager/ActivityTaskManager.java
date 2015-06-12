package com.theo.sdk.manager;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;

/**
 * Activity�����ջ
 * @author Theo
 *
 */
public class ActivityTaskManager {
	private static ActivityTaskManager instance;
	/** Activity���� */
	private static List<Activity> mList = new LinkedList<Activity>();

	/**
	 * ��ȡ��ʵ�� ����
	 * 
	 * @return ActivityTaskManager
	 */
	public static synchronized ActivityTaskManager getInstance() {
		if (null == instance) {
			instance = new ActivityTaskManager();
		}
		return instance;
	}
	
	/**
	 * ���Activity
	 * 
	 * @param activity
	 *            ����ӵ�Activity
	 */
	public static final void addActivity2Task(Activity mActivity) {
		if (!mList.contains(mActivity))
			mList.add(mActivity);
	}

	/**
	 * �Ƴ�Activity
	 * 
	 * @param activity
	 *            ���Ƴ���Activity
	 */
	public static final void destoryActivity4Task(Activity mActivity) {
		if (mList.contains(mActivity))
			mList.remove(mActivity);
	}

	/**
	 * �Ƴ�ָ��Activity���������Activity
	 * 
	 * @param mActivity
	 *            ָ����Activity
	 */
	public static final void finishOtherActity(Activity mActivity) {
		for (Activity activity : mList) {
			if (activity != null && !(mActivity.getClass().getName().equals(activity.getClass().getName())))
				activity.finish();
		}
	}

	/**
	 * ����Activity����������Activity
	 * 
	 * @introduce һ���˳����Activity�������Ʒ��������湦���ǿ�ʹ��
	 */
	public static final void removeAllActivity4Task() {
		for (Activity activity : mList) {
			if (activity != null) {
				activity.finish();
			}
		}
		mList.clear();
	}

}

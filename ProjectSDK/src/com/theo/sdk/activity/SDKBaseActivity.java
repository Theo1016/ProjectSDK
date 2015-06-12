package com.theo.sdk.activity;

import com.theo.sdk.bean.ResponseBean;
import com.theo.sdk.callback.HttpCallBack;
import com.theo.sdk.control.MemeryCallBack;
import com.theo.sdk.manager.ActivityTaskManager;
import com.theo.sdk.widget.CommonProgressDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity����
 * @author Theo
 * 
 */
public class SDKBaseActivity extends Activity implements HttpCallBack {

	/**
	 * ������
	 */
	private CommonProgressDialog mProgressDialog;

	protected Context mContext;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		ActivityTaskManager.addActivity2Task(this);
		mContext.registerComponentCallbacks(new MemeryCallBack());
	}

	@Override
	protected void onDestroy() {
		ActivityTaskManager.destoryActivity4Task(this);
		super.onDestroy();
	}

	/**
	 * ��ʼ������
	 */
	protected void initParams() {
		mContext = getApplicationContext();

	}

	/**
	 * ��ʼ������
	 */
	protected void initViews() {

	}

	/**
	 * ��ʼ��������
	 */
	protected void initListeners() {

	}

	@Override
	public void onSuccess(ResponseBean responseBeah) {
		removeProgressDialog();
	}

	@Override
	public void onError(ResponseBean responseBeah) {
		removeProgressDialog();
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	public final void showProgressDialog(
			DialogInterface.OnCancelListener mCancel) {
		if (mProgressDialog == null) {
			mProgressDialog = new CommonProgressDialog(this);
		}
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnCancelListener(mCancel);

		if (!isFinishing())
			mProgressDialog.show();
	}

	public void setMsg(String content) {
		mProgressDialog.setMsg(content);
	}

	/**
	 * ���ȶԻ����Ƿ���ʾ
	 * 
	 * @return
	 */
	public final boolean isProgressShowing() {
		if (mProgressDialog != null) {
			return mProgressDialog.isShowing();
		} else {
			return false;
		}
	}

	/**
	 * ���ؽ��ȶԻ���
	 */
	public final void removeProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * activity��ת
	 * 
	 * @param toActivity
	 */
	public void gotoActivity(Class<?> toActivity) {

		Intent intent = new Intent(this, toActivity);
		startActivity(intent);
	}

	/**
	 * activity��ת
	 * 
	 * @param action
	 */
	public void gotoActivity(String action) {
		Intent intent = new Intent(action);
		startActivity(intent);
	}

}

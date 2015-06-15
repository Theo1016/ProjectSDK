package com.theo.sdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.theo.sdk.bean.ResponseBean;
import com.theo.sdk.callback.HttpCallBack;
import com.theo.sdk.constant.Const;
import com.theo.sdk.control.MemeryCallBack;
import com.theo.sdk.manager.ActivityTaskManager;
import com.theo.sdk.widget.CommonProgressDialog;

/**
 * Activity基类
 * @author Theo
 * 
 */
public abstract class SDKBaseActivity extends Activity implements HttpCallBack {

	/**
	 * 进度条
	 */
	private CommonProgressDialog mProgressDialog;

	protected Context mContext;

    private BroadcastReceiver mMessageReceiver;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		ActivityTaskManager.addActivity2Task(this);
		mContext.registerComponentCallbacks(new MemeryCallBack());
        initParams();
        initViews();
        initListeners();
	}


    /**
     *
     * 设置本地广播
     */
    private void setLocalReceiver(BroadcastReceiver receiver){
        this.mMessageReceiver = receiver;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Const.LocalBroadCastTag));
    }

    /**
     * 发送本地广播消息
     * @param value
     */
	private void sendMessage(String value) {
		Log.d(Const.LogTag, "Broadcasting message");
		Intent intent = new Intent(Const.LocalBroadCastTag);
		intent.putExtra("message", value);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	protected void onDestroy() {
		ActivityTaskManager.destoryActivity4Task(this);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	/**
	 * 初始化参数
	 */
	protected abstract void initParams();

	/**
	 * 初始化界面
	 */
	protected abstract void initViews();

	/**
	 * 初始化监听器
	 */
	protected abstract void initListeners();

	@Override
	public void onSuccess(ResponseBean responseBeah) {
		removeProgressDialog();
	}

	@Override
	public void onError(ResponseBean responseBeah) {
		removeProgressDialog();
	}

	/**
	 * 显示进度对话框
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
	 * 进度对话框是否显示
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
	 * 隐藏进度对话框
	 */
	public final void removeProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * activity跳转
	 * 
	 * @param toActivity
	 */
	public void gotoActivity(Class<?> toActivity) {

		Intent intent = new Intent(this, toActivity);
		startActivity(intent);
	}

	/**
	 * activity跳转
	 * 
	 * @param action
	 */
	public void gotoActivity(String action) {
		Intent intent = new Intent(action);
		startActivity(intent);
	}

}

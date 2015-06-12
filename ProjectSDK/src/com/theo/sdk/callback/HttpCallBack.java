package com.theo.sdk.callback;

import com.theo.sdk.bean.ResponseBean;

/**
 * ÇëÇó»Øµ÷
 * @author Theo
 *
 */
public interface HttpCallBack {

	void onSuccess(ResponseBean responseBeah);
	
	void onError(ResponseBean responseBeah);
}

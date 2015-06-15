package com.theo.sdk.request;

import org.json.JSONException;

public class JsonParseData extends JsonHttpResponseHandler implements ParseDataInterface {

	public static final String UTF8_BOM = "\uFEFF";

	@Override
	public boolean parseResult(String result) throws JSONException, Exception {
//		if (null == result)
//			return false;
//		Object JsonObj = null;
//		result = result.trim();
//		if (result.startsWith(UTF8_BOM)) {
//			result = result.substring(1);
//		}
//		if (result.startsWith("{") || result.startsWith("[")) {
//			JsonObj = new JSONTokener(result).nextValue();
//		}
//		if (JsonObj == null) {
//			JsonObj = result;
//		}
		return false;
	}

}

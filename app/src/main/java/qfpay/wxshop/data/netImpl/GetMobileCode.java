package qfpay.wxshop.data.netImpl;

import qfpay.wxshop.utils.MobAgentTools;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
/**
 * 获取注册码网络请求
 * */
public class GetMobileCode extends AbstractNet {


	
	public GetMobileCode(Activity act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {

			String mobile = parameter2.getString("mobile");
			String count = parameter2.getString("count");
			map.put("mobile", mobile);
			map.put("count", count);
			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance().getRegVerifyCodeURL(ConstValue.HTTP_POST));

			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_POST);
			/** 参数正确 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);
		} catch (Exception e) {
			T.e(e);
			/** 请求参数错误 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
		}
		return map;
	}

	@Override
	protected Bundle jsonParse(String jsonStr) {

		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (resultState.equals("0000")) {
					bundle.putString(ConstValue.ERROR_MSG, ConstValue.SUCCESS);
					
				} else if (resultState.equals("2300")) {

					String errMsg = root.getString("resperr");

					bundle.putString(ConstValue.ERROR_MSG, errMsg);

				}  else if (resultState.equals("2301")) {

//					String errMsg = root.getString("resperr");

					bundle.putString(ConstValue.ERROR_MSG, activity.getResources().getString(R.string.existing_phonenum));

				}else if (!resultState.equals("0000")) {

                    String errorMsg = root.getString("resperr");
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}

				Long key = System.currentTimeMillis();
				/** 界面上展示的时候直接根据key取存储类的数据 */
				bundle.putString(ConstValue.CACHE_KEY, key + "");
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_SUCCESS);
			} catch (Exception e) {
				T.e(e);
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			}
		} else {
			bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			T.i("jsonStr is null or jsonStr.length is 0");
		}
		return bundle;
	}
}

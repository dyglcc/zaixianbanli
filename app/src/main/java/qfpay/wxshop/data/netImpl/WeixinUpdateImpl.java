package qfpay.wxshop.data.netImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.RetrofitWrapper.CommonJsonBean;
import qfpay.wxshop.utils.T;
import android.app.Activity;
import android.os.Bundle;
/**
 * 保存微信信息
 * */
public class WeixinUpdateImpl extends AbstractNet {

	public WeixinUpdateImpl(Activity act) {
		super(act);
		setNoNeedShowDialog();
	}
	private String contract;
	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("intro")) {
				String intro = (String) parameter2.get("intro");
				map.put("weixinid", intro);
				contract = intro;
			}

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getWeixinHaoUpdateURl(ConstValue.HTTP_POST));

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
				Gson gson = new Gson();
				CommonJsonBean cjb= gson.fromJson(jsonStr, CommonJsonBean.class);
				if (cjb.getRespcd().equals("0000")) {

					if(contract!=null){
						WxShopApplication.dataEngine.setContract(contract);
					}

				} else if (cjb.getRespcd().startsWith("21")) {
					bundle.putString(ConstValue.ERROR_MSG,
							cjb.getResperr());

				} else {
                    String errorMsg =cjb.getResperr();
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

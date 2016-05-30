package qfpay.wxshop.data.netImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SSNListResponseWrapper;
import qfpay.wxshop.data.beans.SSNListResponseWrapper.MsgsWrapper;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
/**
 * 碎碎念列表
 *
 * */
public class SSNGetNetImpl extends AbstractNet {

	public SSNGetNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (parameter2.containsKey("page")) {
				map.put("page", parameter2.getString("page"));
			}
			// 买家秀 1
			map.put("msg_type", 0);

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.suisuiNianDatalist(ConstValue.HTTP_GET));
			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_GET);
			/** 参数正确 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);

		} catch (Exception e) {
			T.e(e);
			/** 请求参数错误 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
		}
		return map;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Bundle jsonParse(String jsonStr) {
		ArrayList<HashMap<String, Object>> list = null;
		HashMap<String, Object> map = null;
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				Gson gosn = new Gson();
				SSNListResponseWrapper fromJson = gosn.fromJson(jsonStr,
						SSNListResponseWrapper.class);
				if (!fromJson.getRespcd().equals("0000")) {
                    String errorMsg = fromJson.getResperr();
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}else{
                    MsgsWrapper data = fromJson.getData();
                    list = new ArrayList<HashMap<String, Object>>();
                    map = new HashMap<String, Object>();
                    // 2014-04-24 14:52:31
                    // 处理日期
                    map.put("orderList", data);
                    list.add(map);

                    Long key = System.currentTimeMillis();
                    CacheData.getInstance().setData(key + "", list);
                    /** 界面上展示的时候直接根据key取存储类的数据 */
                    bundle.putString(ConstValue.CACHE_KEY, key + "");
                }

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

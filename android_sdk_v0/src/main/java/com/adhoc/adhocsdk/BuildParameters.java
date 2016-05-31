/**
 * BuildParameters class is used to build a JSON object containing client's information such as
 * ClientId, App's TrackingId, timestamp of event, Event type.
 */

package com.adhoc.adhocsdk;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.adhoc.utils.T;

public class BuildParameters {
    private static BuildParameters mInstance;

    private String mClientId;
    private Context mContext;
    private JSONObject mSummary;
    private JSONObject customPara;

    public static BuildParameters getInstance(Context context) {
        try {
            if (mInstance == null) {
                mInstance = new BuildParameters(context);
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return mInstance;
    }

    public void makeCustomPara(HashMap<String, String> map) {

        customPara = new JSONObject();
        for (Entry<String, String> entry : map.entrySet()) {
            try {
                customPara.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                T.w("custormPara error");
            } catch (Throwable tr) {
                T.e(tr);
            }
        }
    }

    /*
     * Load all <key-value> pairs here.
     * 1) All given <key-value> pairs in the map.
     * 2) Basic information there.
     *
     * Returns null if there is no Client ID or Adhoc App ID.
     */
    public JSONObject buildParametersBasic() {
        // If the precondition does not exists.
        JSONObject json = new JSONObject();
        try {
            if (mClientId == null) {
                mClientId = AdhocClientIDHandler.getInstance(mContext).getClientId();
            }

            try {
                json.put(KeyFields.CLIENT_ID, mClientId);
                json.put(KeyFields.APP_KEY, AdhocTracker.APPKEY);
                // Includes other information.
                if (mSummary != null) {
                    json.put(KeyFields.SUMMARY, mSummary);
                }

                if (customPara == null) {
                    customPara = new JSONObject();
                }
                json.put(AdhocConstants.CUSTOM_PARA, customPara);
            } catch (JSONException e) {
                T.w("JSONException when filling basic key-value parameters.");
                return null;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return json;
    }


    public synchronized JSONObject getTrackparaJson(Context context, JSONObject obj, HashMap stats) {

        if (obj == null) {
            T.w("adhoc basic request para is null");
            return null;
        }

        String key = (String) stats.get(KeyFields.STAT_KEY);
        Object currentValue = stats.get(KeyFields.STAT_VALUE);
        // stats
        JSONArray array = new JSONArray();
        JSONArray arrayExperiments = (JSONArray) stats.get(KeyFields.EXPERIMENTS);
        for (int i = 0; i < arrayExperiments.length(); i++) {
            JSONObject statusOjb = new JSONObject();
            String expId = arrayExperiments.optString(i);
            // 每个试验对应的key的总的value是不同的.
            Double currentAllvalue = ExperimentUtils.getInstance().getAllValue(context, key, expId);
            Double count = ExperimentUtils.getInstance().saveAllvalue(context, key, expId, currentValue, currentAllvalue);
            try {

                statusOjb.put(KeyFields.STAT_KEY, key);
                statusOjb.put(KeyFields.STAT_VALUE, currentValue);
                statusOjb.put(KeyFields.TIMESTAMP, stats.get(KeyFields.TIMESTAMP));
                statusOjb.put(KeyFields.STAT_VALUE_ALL, count);
                statusOjb.put(KeyFields.EXPERIMENTS, new JSONArray().put(expId));
            } catch (Throwable e) {
                T.e(e);
                continue;
            }
            array.put(statusOjb);
        }

        try {
            obj.put(KeyFields.STATS, array);
        } catch (JSONException e) {
            T.e(e);
        } catch (Throwable throwable) {
            T.e(throwable);
        }
        return obj;
    }

    private BuildParameters(Context context) {
        try {
            mContext = context;
            getBasicParameters();
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private void getBasicParameters() {

        try {
            mClientId = AdhocClientIDHandler.getInstance(mContext).getClientId();
            mSummary = ParameterUtils.getSummary(mContext);
        } catch (JSONException e) {
            mSummary = null;
            T.w("json mSummary is error!");
        } catch (Throwable tt) {
            T.e(tt);
        }
        T.d(" aptext id : " + AdhocTracker.APPKEY);
    }
}

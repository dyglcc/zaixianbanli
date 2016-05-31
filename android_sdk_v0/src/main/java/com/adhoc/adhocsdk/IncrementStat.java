package com.adhoc.adhocsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.adhoc.http.Response;
import com.adhoc.http.Callback;
import com.adhoc.http.MediaType;
import com.adhoc.http.Request;
import com.adhoc.http.RequestBody;
import com.adhoc.http.ResponseBody;
import com.adhoc.utils.T;
import com.adhoc.utils.Utils;
import com.adhoc.net.AdhocNet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class
        IncrementStat {
    private static IncrementStat ourInstance = new IncrementStat();

    public static IncrementStat getInstance() {
        return ourInstance;
    }

    // default moble type send message
    private static boolean onlyWifiSend = false;
    private long GAP_SEND_2_SERVER = 1000;

    public void setOnlyWifiSend(boolean value) {
        onlyWifiSend = value;
    }

    public void setGAP_SEND_2_SERVER(long gap) {

        GAP_SEND_2_SERVER = gap;

    }

    public void registerBrocast(Context context) {
        try {
            //注册网络监听
            if (context != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                context.registerReceiver(receiver, filter);
            }
        } catch (Throwable e) {
            T.w("init reg receiver error!");
        }

    }

    enum Type {
        String,
        Integer,
        Double,
        Float,
        Long
    }

    ;

    private IncrementStat() {
    }

    /*
     * Increment experiment stats.
     */
    public void incrementStatObj(Context context, final String key, Object inc, long time, JSONArray experiments) {
        try {
            time = time == 0 ? System.currentTimeMillis() / 1000 : time / 1000;
            HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
            keyValueCampaign.put(KeyFields.TIMESTAMP, time);
            keyValueCampaign.put(KeyFields.STAT_KEY, key);
            keyValueCampaign.put(KeyFields.STAT_VALUE, inc);
            // deal with all value

//            keyValueCampaign.put(KeyFields.STAT_VALUE_ALL, allvalue);
            keyValueCampaign.put(KeyFields.EXPERIMENTS, experiments);
            // T.i("report key : " +key+" value : " + inc );
            T.i("send key - value :" + key + " " + inc + " experiments " + experiments);

            JSONObject objectBaic = BuildParameters.getInstance(context).buildParametersBasic();

            JSONObject reqObj = BuildParameters.getInstance(context).getTrackparaJson(context, objectBaic, keyValueCampaign);

            Utils.NetWorkState state = Utils.getNetworkInfo(context);

            switch (state) {
                case wifi:
                    T.i("report message..");
                    send(reqObj, context);
                    break;
                case mobileType:

                    T.i("mobile net type");
                    if (onlyWifiSend) {
                        saveCache(context, reqObj);
                    } else {
                        send(reqObj, context);
                    }
                    break;
                case unknow:
                    T.i("unknow net workstate");
                    saveCache(context, reqObj);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private void saveCache(Context context, JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return;
            }
            FileHandler.getInstance().writeCacheLines(context, FileHandler.FILENAME, jsonObject.toString() + "\n");
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private boolean sending;

    /**
     * 实现：每一条缓存的请求是将 值的类型+key+value+timestamp 以字符串的形式写入数据文件
     */
    public void sendCacheReqeust(final Context context) {
        try {

            if (sending) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> list = FileHandler.getInstance().readCacheLines(context, FileHandler.FILENAME);
                    // 清理文件
                    if (list.size() > 0) {
                        FileHandler.getInstance().clearFileContent(context, FileHandler.FILENAME);
                    }
                    for (String cacheStr : list) {

                        try {
                            sending = true;
                            try {
                                Thread.sleep(GAP_SEND_2_SERVER);
                            } catch (InterruptedException e) {
                                T.e(e);
                            }
                            send(new JSONObject(cacheStr), context);
                        } catch (Throwable e) {
                            T.w("unknown error");
                            continue;
                        }
                    }
                    sending = false;
                }
            }).start();
        } catch (Throwable e) {
            T.e(e);
        }

    }

    //    有个nullpoint excption 和一个4g下一直发送数据
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            T.i("网络状态改变");
            //获得网络连接服务
            try {
                if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (null != parcelableExtra) {
                        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                        NetworkInfo.State state = networkInfo.getState();
                        boolean isConnected = state == NetworkInfo.State.CONNECTED;//当然，这边可以更精确的确定状态
                        if (isConnected) {
                            // 发送缓存数据
                            getInstance().sendCacheReqeust(context);
                        }
                        T.i("isConnected : " + isConnected);
                    }
                }
            } catch (Throwable e) {
                T.w("reg receiver wrong");
            }

        }
    };

//    // send cache 不会判断当前使用的网络状况 因为在wifi下并且启动app的时候触发
//    private void sendCache(Context context, final String key, Object inc, long time, String experiments) {
//        try {
//            HashMap<String, Object> keyValueCampaign = new HashMap<String, Object>();
//            keyValueCampaign.put(KeyFields.EVENT_TYPE, String.valueOf(EventType.REPORT_STAT));
//            keyValueCampaign.put(KeyFields.TIMESTAMP, time == 0 ? System.currentTimeMillis() / 1000 : time / 1000);
//            keyValueCampaign.put(KeyFields.STAT_KEY, key);
//            keyValueCampaign.put(KeyFields.STAT_VALUE, inc);
//            if (experiments != null) {
//                try {
//                    keyValueCampaign.put(KeyFields.EXPERIMENTS, new JSONArray(experiments));
//                } catch (JSONException e) {
//                    T.e(e);
//                }
//            }
//            send(keyValueCampaign);
//        } catch (Throwable e) {
//            T.e(e);
//        }
//    }

    private void send(final JSONObject reqobj,final Context context) {

        try {
            if (reqobj == null) {
                return;
            }

            RequestBody rb = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqobj.toString());

            Request request = new Request.Builder()
                    .url(AdhocConstants.ADHOC_SERVER_URL)
                    .post(rb)
                    .build();


            T.i("发送json :" + reqobj.toString());

            AdhocNet.getInstance().enqueue(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    try {
                        T.w(request.urlString() + " error");
                        T.e(e);
                        IncrementStat.getInstance().saveCache(context, reqobj);
                    } catch (Throwable ex) {
                        T.e(ex);
                    }

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        ResponseBody body = response.body();
                        String resString = body.string();
                        T.i(resString);
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            });
        } catch (Throwable e) {
            T.e(e);
        }
    }
}

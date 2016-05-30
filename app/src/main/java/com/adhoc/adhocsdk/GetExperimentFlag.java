package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.adhoc.http.Callback;
import com.adhoc.http.MediaType;
import com.adhoc.http.MultipartBuilder;
import com.adhoc.http.OkHttpClient;
import com.adhoc.http.Request;
import com.adhoc.http.RequestBody;
import com.adhoc.http.Response;
import com.adhoc.http.ResponseBody;
import com.adhoc.net.AdhocNet;
import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class GetExperimentFlag {
    private static final String JSON_ERROR_STR = "Failed to get experiment flags.";
    private static GetExperimentFlag ourInstance;
    private static int GAPTIME = 180;
    private long t1_last_getFlagfrom_network;
    public boolean neeedRefreshRightNow;

    public void setGapTime(int t) {
        GAPTIME = t;
    }

    private Context mContext;

    public static GetExperimentFlag getInstance(Context context) {
        try {
            if (ourInstance == null) {
                ourInstance = new GetExperimentFlag(context);
            }

        } catch (Throwable e) {
            T.e(e);
        }
        return ourInstance;
    }

    /**
     * 取本地数据先
     * 没有通过回调方式取数据
     * 事件触发取回服务器最新的json
     */
    public void getAutoExperiment(Context context, final HttpCallBack callBack) {
        try {
            JSONObject autoJson = getLocalAutoExperiment();

            // 如果本地有数据取本地数据
            if (autoJson != null) {

                T.i("local json is " + autoJson.toString());
                callBack.onSuccess(autoJson);

                T.i("取本地 auto flags 结果：" + autoJson.toString());

                boolean isEditing = RealScreen.getInstance(context).isEditing;

                boolean isRequestFast = (System.currentTimeMillis() - t1_last_getFlagfrom_network) < GAPTIME * 1000;

                T.i("isRequestFast :" + isRequestFast);
                T.i("is Tester device : " + isEditing);
                // 是测试手机

                if (!isEditing && isRequestFast) {
                    T.i("网络获取AUTO EXPERIMENT FLAG 失败，距离上次取flag不足" + GAPTIME + "秒" + "duration is : "
                            + (System.currentTimeMillis() - t1_last_getFlagfrom_network));
                } else {
                    // 获取flag
                    getNewestFlagsFromServer(context);
                }
                return;
            }

            if (!Utils.isCanConnectionNetWork(context)) {

                return;

            }
            JSONObject object = BuildParameters.getInstance(mContext).buildParametersBasic();

            RequestBody rb = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());

            Request request = new Request.Builder()
                    .url(AdhocConstants.ADHOC_SERVER_GETFLAGS)
                    .post(rb)
                    .build();

            AdhocNet.getInstance().enqueue(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    try {
                        callBack.onFailure(e);
                        T.w(request.urlString() + " error");
                        T.e(e);
                    } catch (Throwable ex) {
                        T.e(ex);
                    }
                }

                @Override
                public void onResponse(Response response) throws IOException, JSONException {
                    if (response != null) {
                        try {
                            if (!response.isSuccessful()) {
                                String errMesg = null;
                                errMesg = response.message().toString();
                                T.w(errMesg);
                                return;
                            } else {
                                ResponseBody body = response.body();
                                JSONObject result = null;

                                String resString = body.string();

                                if (!resString.equals("")) {
                                    result = new JSONObject(resString);
                                } else {
                                    result = new JSONObject();
                                    T.i("result is null :" + resString);
                                }

                                T.i("网络获取 auto experiment flag :" + resString);
//                            JSONObject flags = new JSONObject(result.toString());
//                            flags.remove(AdhocConstants.FLAG_ABTEST_AUTO_KEY);
//                            mFlags = new ExperimentFlags(flags);
                                // 将mFlags 保存sharepreference;
                                JSONObject returnResult = saveSharePrefFlags(result);

                                // update experiment
                                ExperimentUtils.getInstance().updateExperiments(returnResult);

                                AdhocTracker.incrementStat(mContext, AdhocConstants.EVENT_GET_EXPERIMENT, 1);

                                // send result back
//                            T.i("call back from net work result : " + result.optJSONObject(AdhocConstants.FLAG_ABTEST_AUTO_KEY));
                                JSONObject object1 = result.optJSONObject(ExperimentUtils.FLAGS);
                                if (object1 != null) {
                                    String str = object1.optString(AdhocConstants.FLAG_ABTEST_AUTO_KEY);
                                    if (str != null && !str.equals("")) {
                                        try {
                                            callBack.onSuccess(new JSONObject(str));
                                        } catch (JSONException e) {
                                            T.e(e);
                                        } catch (Throwable ez) {
                                            T.e(ez);
                                        }
                                    }
                                }
                                //todo send pv report
                                result = null;
                            }
                        } catch (Throwable ex) {
                            T.e(ex);
                        }
                    }
                }

            });
        } catch (Throwable e) {
            T.e(e);
        }
    }

    public void getExperimentFlagsTimeOut(int timeoutMillis) {

        getExperimentFlagsTimeOut(timeoutMillis, null);
    }

    private ExperimentFlags mFlags = null;
    private Handler handler = null;

    /*
     * Get experiment flags.
     */
    public void getExperimentFlagsTimeOut(final int timeout, final OnAdHocReceivedData listener) {
        OkHttpClient client;
        try {
            // 检查网络如果网络未联通返回本地保存
            if (!Utils.isCanConnectionNetWork(mContext)) {

                mFlags = getLocalFlags();

                T.w("取本地flags 结果：" + mFlags);

            }
            T.i("超时时间:" + timeout);
            long timeoutMill = timeout / 3;

            String object =
                    BuildParameters.getInstance(mContext).buildParametersBasic().toString();

            client = new OkHttpClient();
            client.setConnectTimeout(timeoutMill, TimeUnit.MILLISECONDS);
            client.setWriteTimeout(timeoutMill, TimeUnit.MILLISECONDS);
            client.setReadTimeout(timeoutMill, TimeUnit.MILLISECONDS);

            RequestBody rb = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());

            Request request = new Request.Builder()
                    .url(AdhocConstants.ADHOC_SERVER_GETFLAGS)
                    .post(rb)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    try {
                        T.w("请求" + timeout + "内超时,取本地flag");
                        mFlags = getLocalFlags();
                        sendCallBack(listener, handler, mFlags);

                    } catch (Throwable ex) {
                        T.e(ex);
                    }
                }

                @Override
                public void onResponse(Response response) throws IOException, JSONException {
                    neeedRefreshRightNow = false;
                    if (response != null) {
                        try {
                            if (!response.isSuccessful()) {
                                String errMesg = null;
                                errMesg = response.message().toString();
                                T.w(errMesg);
                                T.w("请求回调获取flag失败,返回本地数据");
                                mFlags = getLocalFlags();
                            } else {
                                ResponseBody body = response.body();
                                JSONObject result = null;

                                String resString = body.string();

                                if (!resString.equals("")) {
                                    result = new JSONObject(resString);
                                } else {
                                    result = new JSONObject();
                                    T.i("result is null :" + resString);
                                }
                                T.i("网络获取 listener :" + resString);
                                mFlags = new ExperimentFlags(result);
                                mFlags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());

                                // 保存sharepreference;
                                JSONObject jsonObject = saveSharePrefFlags(result);

                                ExperimentUtils.getInstance().updateExperiments(jsonObject);

                                AdhocTracker.incrementStat(mContext, AdhocConstants.EVENT_GET_EXPERIMENT, 1);

                                result = null;
                            }
                            sendCallBack(listener, handler, mFlags);
                        } catch (Throwable ex) {
                            T.e(ex);
                        }
                    }
                }
            });
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Looper.prepare();
//
//                        int timeoutMillis = timeout;
//                        do {
//                            try {
//                                Thread.sleep(200);
//
//                                T.i(timeoutMillis + mAdhocClient.getLastResponse());
//
//                                timeoutMillis -= 200;
//
//                            } catch (InterruptedException e) {
//                                T.d("Failed to sleep.");
//                            } catch (Throwable ex) {
//                                T.e(ex);
//                            }
//                        } while (timeoutMillis > 0 && mAdhocClient.getLastResponse() == null);
//
//                        if (mAdhocClient.getLastResponse() != null && !mAdhocClient.getLastResponse().equals("UNKNOWN")) {
//                            try {
//
//                                neeedRefreshRightNow = false;
//
//                                JSONObject object = new JSONObject(mAdhocClient.getLastResponse());
//
//
//                                mFlags = new ExperimentFlags(object);
//                                mFlags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());
//
//                                // 保存sharepreference;
//                                JSONObject jsonObject = saveSharePrefFlags(object);
//
//                                ExperimentUtils.getInstance().updateExperiments(jsonObject);
//
//                                AdhocTracker.incrementStat(mContext, AdhocConstants.EVENT_GET_EXPERIMENT, 1);
//
//                            } catch (Throwable e) {
//                                T.i(JSON_ERROR_STR);
//                            }
//                        } else {
//
//                            T.w("请求" + timeout + "内超时,取本地flag");
//                            mFlags = getLocalFlags();
//
//                        }
//
//                        if (listener != null) {
//
//                            handler.post(new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        listener.onReceivedData(mFlags);
//                                    } catch (Throwable e) {
//                                        T.e(e);
//                                    }
//                                }
//                            }));
//
//                        }
//
//                        Looper.loop();
//
//
//                    } catch (Throwable e) {
//                        T.e(e);
//                    }
//                }
//            }).start();

        } catch (Throwable e) {
            T.e(e);
        } finally {
            T.i("client set null");
            client = null;
        }
    }

    private void sendCallBack(final OnAdHocReceivedData listener, Handler handler, final ExperimentFlags flags) {

        if (listener != null) {

            handler.post(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.onReceivedData(flags);
                    } catch (Throwable e) {
                        T.e(e);
                    }
                }
            }));

        }

    }
     /*
     * 取服务器模块开关，访问网络一次
     */

    public void getExperimentFlags(final OnAdHocReceivedData listener) {

        getExperimentFlagsTimeOut(ClientImpl.TIMEOUT, listener);
    }


    /*
     * 取本地flags，并访问服务器
     */
    public ExperimentFlags getExperimentFlags(final Context context) {

        // 检查网络如果网络未联通返回本地保存

        ExperimentFlags mFlags = getLocalFlags();

        String result = "null";

        if (mFlags != null && mFlags.getRawFlags() != null) {

            result = mFlags.getRawFlags().toString();
        }

        T.i("取本地flags 结果：" + result);


//        boolean isTesterDevice = Utils.checkIsTesterDevice(mContext, AdhocConstants.TEST_PACKAGE_NAME);

        boolean isRequestFast = (System.currentTimeMillis() - t1_last_getFlagfrom_network) < GAPTIME * 1000;

        T.i("isRequestFast :" + isRequestFast);
//        T.i("is Tester device : " + isTesterDevice);
        // 是测试手机

//        if (!isTesterDevice && isRequestFast) {
        if (!neeedRefreshRightNow && isRequestFast) {
            T.i("网络获取flag失败，距离上次取flag不足" + GAPTIME + "秒" + "duration is : "

                    + (System.currentTimeMillis() - t1_last_getFlagfrom_network));
        } else {
            // 获取flag
            getNewestFlagsFromServer(context);
        }

        return mFlags;

    }

    private void getNewestFlagsFromServer(final Context context) {
        try {

            // 查看是否需要获取flag
            if (!Utils.isCanConnectionNetWork(context)) {

                return;

            }
            // 保存网络中取flag的时间
            t1_last_getFlagfrom_network = System.currentTimeMillis();

            T.i("last_request : " + t1_last_getFlagfrom_network);

            JSONObject object = BuildParameters.getInstance(mContext).buildParametersBasic();
            RequestBody rb = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());

            Request request = new Request.Builder()
                    .url(AdhocConstants.ADHOC_SERVER_GETFLAGS)
                    .post(rb)
                    .build();

            AdhocNet.getInstance().enqueue(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    try {
                        T.w(request.urlString() + " error");
                    } catch (Throwable ex) {
                        T.e(ex);
                    }

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        if (response != null) {
                            if (!response.isSuccessful()) {
                                String errMesg = null;
                                errMesg = response.message().toString();
                                T.w(errMesg);
                                return;
                            } else {
                                neeedRefreshRightNow = false;
                                ResponseBody body = response.body();
                                JSONObject result = null;

                                String resString = body.string();
                                try {
                                    if (!resString.equals("")) {
                                        result = new JSONObject(resString);
                                    } else {
                                        result = new JSONObject();
                                        T.i("result is null :" + resString);
                                    }

                                    T.i("网络获取flag :" + resString);
//                            JSONObject flags = new JSONObject(result.toString());

//                            flags.remove(AdhocConstants.FLAG_ABTEST_AUTO_KEY);
//                            mFlags = new ExperimentFlags(flags);
                                    // before return setExperment
                                    JSONObject jsonObject = saveSharePrefFlags(result);
                                    ExperimentUtils.getInstance().updateExperiments(jsonObject);
                                    AdhocTracker.incrementStat(mContext, AdhocConstants.EVENT_GET_EXPERIMENT, 1);
                                    result = null;
                                } catch (JSONException e) {
                                    T.w("response json exception");
                                } catch (Throwable th) {
                                    T.e(th);
                                }
                                // do not delete use for upload pic
//                        if (context instanceof Activity) {
//                            checkNeedUploadFile((Activity) context);
//                        }
                            }
                        }
                    } catch (Throwable ex) {
                        T.e(ex);
                    }
                }

            });
        } catch (Throwable e) {
            T.e(e);
        }

    }


    private GetExperimentFlag(Context context) {

        try {
            mContext = context;
            handler = new Handler(mContext.getMainLooper());
            getNewestFlagsFromServer(mContext);

        } catch (Throwable e) {
            T.e(e);
        }

    }

    // 从本地去模块开关
    private ExperimentFlags getLocalFlags() {

        ExperimentFlags flags = new ExperimentFlags(new JSONObject());
        // init flag state
        flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_NULL.toString());
        try {

            String str = getlocalFlagsString();

            if (!str.equals("")) {
                try {
                    JSONObject jsObj = new JSONObject(str);

                    flags = new ExperimentFlags(jsObj);

                    flags.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_OK.toString());

                    return flags;
                } catch (JSONException e) {

                    T.i(JSON_ERROR_STR);

                }
            }

            return flags;
        } catch (Throwable e) {
            T.e(e);
        }
        return flags;
    }

    // 从本地取自动实验
    private JSONObject getLocalAutoExperiment() {

        String str = Utils.getStringShareData(Utils.getSharePreference(mContext),
                AdhocConstants.PREFS_ABTEST_AUTO_EXPERMENT);
        JSONObject jsObj = null;
        if (!str.equals("")) {
            try {
                jsObj = new JSONObject(str);
            } catch (JSONException e) {
                T.e(e);
            }
        }
        return jsObj;
    }

    private String getlocalFlagsString() {
        return Utils.getStringShareData(Utils.getSharePreference(mContext),
                AdhocConstants.PREFS_ABTEST_FLAGS);
    }

    private JSONObject saveSharePrefFlags(JSONObject obj_src) throws JSONException {
        JSONObject obj = new JSONObject(obj_src.toString());
        JSONObject flags = null;
        // 处理 auto experiment flag
        if (obj != null) {

            flags = obj.optJSONObject("flags");
            if (flags != null) {
                Utils.saveStringShareData(Utils.getSharePreference(mContext),
                        AdhocConstants.PREFS_ABTEST_AUTO_EXPERMENT,
                        flags.optString(AdhocConstants.FLAG_ABTEST_AUTO_KEY, "{}"));
            }
        }

        if (flags == null) return null;

        flags.remove(AdhocConstants.FLAG_ABTEST_AUTO_KEY);

        JSONArray array = obj.optJSONArray(ExperimentUtils.EXPERIMENTS);
        for (int i = 0; i < array.length(); i++) {
            JSONObject experiment = array.optJSONObject(i);

            JSONArray arrayFlags = experiment.optJSONArray(ExperimentUtils.FLAGS);
            JSONArray newFlags = new JSONArray();
            for (int x = 0; x < arrayFlags.length(); x++) {
                JSONObject flag = new JSONObject();
                if (AdhocConstants.FLAG_ABTEST_AUTO_KEY.equals(arrayFlags.getString(x))) {
                    flag.put(AdhocConstants.FLAG_ABTEST_AUTO_KEY, true);
                } else {
                    flag.put(arrayFlags.getString(x), false);
                }
                newFlags.put(flag);
            }
            experiment.put("flags", newFlags);
        }
        // do not delete 上传图片
//        String str = Utils.getStringShareData(Utils.getSharePreference(mContext), AdhocConstants.PREFS_ABTEST_FLAGS);

//         changed
//        if (!str.equals(obj.toString())) {
//            T.i("flag changed save change flag true");
//            Utils.saveBooleanShareData(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED, true);
//        }
        Utils.saveStringShareData(Utils.getSharePreference(mContext),
                AdhocConstants.PREFS_ABTEST_FLAGS, obj.toString());

        return obj;

    }


    private void checkNeedUploadFile(final Activity context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    // 没有安装tester
                    PackageInfo packageInfo = null;

                    try {
                        packageInfo = mContext.getPackageManager().getPackageInfo(
                                AdhocConstants.SCANNER_PACKAGE_NAME, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        T.i("不上传截图");
                    }

                    if (packageInfo == null) {
                        T.i("不上传截图 : 没有 tester ");
                        return;
                    }
                    // 查看test sao 标记
//                boolean sao = SharePrefHandler.getInstance(mContext).getTesterPreferenceSaoFlag();\

                    int count = ProviderHandler.getInstance(mContext).searchScan();

                    // tester 扫描加入实验成功
                    if (count == 0) {
                        T.i("不上传截图:未扫描并加入实验");
                        return;

                    }

                    // flag 有改变，上传截屏
                    boolean flag_changed = Utils.getBooleanValue(Utils.getSharePreference(mContext), AdhocConstants.FLAG_CHANGED);

                    if (!flag_changed) {

                        T.i("不上传截图 ： 开关字符串没有改变: " + flag_changed);
                        return;

                    }

                    T.i("do not upload SDK_Version_ int " + Build.VERSION.SDK_INT);

                    uploadScreenShotFile(context);

                    T.i("upload files ");
                } catch (Throwable e) {
                    T.e(e);
                }
            }

        }).start();
    }

    public interface OnShotFile {
        void onShotFile(Object obj);

    }

    private void uploadScreenShotFile(final Activity context) {

        // handler 处理图片
        ScreenShot.getInstance().takeScreenShot(context, new OnShotFile() {

            @Override
            public void onShotFile(Object obj) {
                sendFile2Server(context);
            }
        });

    }

    private void sendFile2Server(final Context context) {


        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        File file = ScreenShot.getInstance().getScreenShotFile(context);
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addFormDataPart("adhoc_app_track_id", AdhocTracker.APPKEY)
                .addFormDataPart("client_id", AdhocClientIDHandler.getInstance(context).getClientId())
                .build();

        Request request = new Request.Builder()
                .url(AdhocConstants.
                        ADHOC_PIC_SERVER)
                .post(requestBody)
                .build();

        AdhocNet.getInstance().enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                try {
                    T.w(request.httpUrl() + ": req error ! ");
                    T.e(e);
                } catch (Throwable ex) {

                    T.e(ex);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            Utils.saveBooleanShareData(Utils.getSharePreference(context), AdhocConstants.FLAG_CHANGED, false);
                            try {
                                ProviderHandler.getInstance(context).delScan();
                            } catch (Throwable e) {
                                T.e(e);
                            }
                        }
                    }
                } catch (Throwable ex) {
                    T.e(ex);
                }
            }
        });

    }

}

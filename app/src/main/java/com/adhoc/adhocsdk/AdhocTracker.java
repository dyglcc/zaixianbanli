package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class AdhocTracker {

    private static String msg = "ADHOC_SDK仅支持 Android SDK API level 9及以上,level 8及以下版本client将不加入试验";
    public static String client_id = null;
    private static int current_api_level = Build.VERSION.SDK_INT;

    /**
     * 得到用户模块开关 ExperimentFlags。
     */
    public static ExperimentFlags getExperimentFlags(Context context) {
        try {
            if (context == null) {
                T.w("get experiment flag context is null");
                return getNullExperimentflag();
            }
            T.i("need refresh right now " + GetExperimentFlag.getInstance(context.getApplicationContext()).neeedRefreshRightNow);
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return getNullExperimentflag();
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return getNullExperimentflag();

            }
            return GetExperimentFlag.getInstance(context.getApplicationContext()).getExperimentFlags(context.getApplicationContext());
        } catch (Throwable e) {
            T.e(e);
        }
        return getNullExperimentflag();
    }

    private static ExperimentFlags getNullExperimentflag() {
        ExperimentFlags nullFlag = new ExperimentFlags(new JSONObject());
        nullFlag.setFlagState(ExperimentFlags.ExperimentFlagsState.EXPERIMENT_NULL.toString());
        return nullFlag;
    }

    /**
     * 设置用户实验变量,注意需要在init()方法之前调用
     */

    public static void setCustomStatParameter(Context context, HashMap<String, String> map) {
        if (context == null) {
            return;
        }
        if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
            return;
        }
        try {
            BuildParameters.getInstance(context.getApplicationContext()).makeCustomPara(map);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 得到用户模块开关，在Activity的onStart()方法中调用。
     */
    public static void getExperimentFlags(Context context, OnAdHocReceivedData listener) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            T.i("need refresh right now " + GetExperimentFlag.getInstance(context.getApplicationContext()).neeedRefreshRightNow);
            GetExperimentFlag.getInstance(context.getApplicationContext()).getExperimentFlags(listener);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 得到用户模块开关，可以设置毫秒超时时间，UI线程将会等待返回。
     */
    public static void getExperimentFlagsTimeOut(Context context, int timeoutMillis) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }

            T.i("need refresh right now " + GetExperimentFlag.getInstance(context.getApplicationContext()).neeedRefreshRightNow);

            GetExperimentFlag.getInstance(context.getApplicationContext()).getExperimentFlagsTimeOut(timeoutMillis);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 接口从网络取模块开关。
     * 得到用户模块开关，在指定的响应时间内，并回调listener。
     */
    public static void getExperimentFlagsTimeOut(Context context, int timeoutMillis, OnAdHocReceivedData listener) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            T.i("need refresh right now " + GetExperimentFlag.getInstance(context.getApplicationContext()).neeedRefreshRightNow);
            GetExperimentFlag.getInstance(context.getApplicationContext()).getExperimentFlagsTimeOut(timeoutMillis, listener);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 统计优化指标值是double型的数据。
     */
    public static void incrementStat(final Context context, final String key, final double inc) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IncrementStat.getInstance().incrementStatObj(context.getApplicationContext(), key, inc, 0, ExperimentUtils.getInstance().getExperimetnsStrs());
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            }).start();

        } catch (Throwable e) {
            T.e(e);
        }

    }

    /**
     * 统计优化指标值是int类型的数据。
     */
    public static void incrementStat(final Context context, final String key, final int inc) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }

            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IncrementStat.getInstance().incrementStatObj(context.getApplicationContext(), key, inc, 0, ExperimentUtils.getInstance().getExperimetnsStrs());
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            }).start();

        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 统计优化指标值是long类型的数据。
     */
    public static void incrementStat(final Context context, final String key, final long inc) {

        try {
            if (context == null) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IncrementStat.getInstance().incrementStatObj(context.getApplicationContext(), key, inc, 0, ExperimentUtils.getInstance().getExperimetnsStrs());
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            }).start();

        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 统计优化指标值是float类型的数据。
     */
    public static void incrementStat(final Context context, final String key, final float inc) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IncrementStat.getInstance().incrementStatObj(context.getApplicationContext(), key, inc, 0, ExperimentUtils.getInstance().getExperimetnsStrs());
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            }).start();

        } catch (Throwable e) {
            T.e(e);
        }
    }


    /**
     * 自动统计view点击事件。
     */
    @Deprecated
    public static void autoTracking(Context context, MotionEvent event) {
        if (context == null) {
            return;
        }
        if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
            return;
        }
        if (current_api_level < 9) {
//            T.w(msg);
            return;
        }
        AutoStatClickEvent.getInstance().autoTracking(context.getApplicationContext(), event);

    }

    public static String APPKEY = null;

    /**
     * 初始化ADHOC_SDK，打开崩溃次数统计，并发送缓存请求数据到服务器。
     */
    public static void init(Context context, String appkey) {

        try {
            if (context == null) {
                return;
            }
            APPKEY = appkey;

            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                T.i("设备没有sdcard,将不进入实验!");
                return;
            }

            if (Build.VERSION.SDK_INT >= 9) {

                ExperimentUtils.getInstance().setContext(context.getApplicationContext());
                ExperimentUtils.getInstance().loadLocalExperiments();
                // 统计crash
                CrashHandler.getInstance().run(context.getApplicationContext());
                // 获取服务器新的flag
                GetExperimentFlag.getInstance(context.getApplicationContext());

                // 检测网络情况的变化
                IncrementStat.getInstance().registerBrocast(context.getApplicationContext());

                Utils.NetWorkState state = Utils.getNetworkInfo(context.getApplicationContext());

                switch (state) {
                    case wifi:
                        // 检查是否有缓存的数据
                        try {
                            IncrementStat.getInstance().sendCacheReqeust(context.getApplicationContext());
                        } catch (Throwable e) {
                            T.e(e);
                        }
                        break;
                    default:
                        break;
                }
                if (Build.VERSION.SDK_INT >= 14) {
                    // connect 2 server
                    RealScreen.getInstance(context.getApplicationContext());
                    // Rander view
                    Rendering.getInstance(context.getApplicationContext()).init();
                } else {
                    T.w("ADHOC_SDK可视化编辑仅支持 Android SDK API level 14及以上,API level 14以下设备将不会加入试验");
                }
            } else {
                T.w("ADHOC_SDK仅支持 Android SDK API level 9及以上,level 8及以下版本client将不加入试验");
            }
        } catch (Throwable e) {
            T.e(e);
        }

    }

    /**
     * 获取当前设备进入试验情况
     * 返回 JsonArray
     */
    public static JSONArray getCurrentExperiments() {
        if (current_api_level < 9) {
            return new JSONArray();
        }
        try {
            return ExperimentUtils.getInstance().getCurrentExperimetnsStrs();

        } catch (Throwable e) {
            T.e(e);
        }
        return new JSONArray();
    }

    /**
     * 统计fragment页面时长、点击顺序。在被统计Fragment类的onCreate()方法中调用。
     */
    public static void onFragmentCreate(Activity context, Object obj) {
        if (context == null) {
            return;
        }
        if (current_api_level < 9) {
//            T.w(msg);
            return;
        }
        try {
            StatFragment.getInstance().add(context, obj);
//            MLog.getInstance().createLogCollector(context);
//            MLog.getInstance().enableDebug();

        } catch (IllegalAccessException e) {
            T.e(e);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 统计fragment页面时长、点击顺序，配合OnFragmentCreate方法。在Fragment的onDestory()中调用。
     */
    public static void onFragmentDestory(Activity context, Object fragment) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            StatFragment.getInstance().onFragmentDestory(fragment);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 统计Activity时长、访问路径顺序。在被统计Activity的onResume()方法中调用。
     */
    public static void onResume(Activity context) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }

            T.i("onResume : " + context.getClass().getName());
            PageActivityStat.getInstance().OnResume(context);
            if (StatFragment.getInstance().resumeForeground) {
                Log.d("FragmentManager", " Operations:");
                T.i("resume Opertions");
            }
        } catch (Throwable e) {
            T.e(e);
        }

    }

    /**
     * 统计Activity时长，访问路径顺序。在被统计的Activity的onPause()方法中。
     */
    public static void onPause(final Activity context) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }

            T.i("onPause : " + context.getClass().getName());
            PageActivityStat.getInstance().OnPause(context);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        if (context == null) {
                            return;
                        }
                        boolean isForeground = Utils.isAppOnForeground(context.getApplicationContext());
                        if (!isForeground) {

                            // 结束统计信息。
                            StatFragment.getInstance().onBack2Menu(context.getApplicationContext());
                            T.i("back 2 menu");
                            StatFragment.getInstance().resumeForeground = true;
                        } else {
                            StatFragment.getInstance().resumeForeground = false;
                        }

                        if (PageActivityStat.run) {

                            // 回到主屏
                            if (!isForeground) {
                                //
                                PageActivityStat.getInstance().onDestory(context.getApplicationContext());
                            }
                        }
                    } catch (InterruptedException e) {
                        T.e(e);
                    } catch (Throwable ex) {
                        T.e(ex);
                    }

                }
            }).start();
        } catch (Throwable e) {
            T.e(e);
        }

    }

    /**
     * 统计App崩溃信息开关。默认true,用户需要关闭上报错误信息，设置false即可。
     */
    public static void reportCrashEnable(boolean enable) {
        try {
            if (current_api_level < 9) {
//            T.w(msg);
                return;
            }
            CrashHandler.getInstance().setEnable(enable);
        } catch (Throwable e) {
            T.e(e);
        }

    }

//    /**
//     * 开启debug模式，设置true，将会会打印"ADHOC_SDK"标签的log日志。
//     */
//    public static void setEnableDebugMode(boolean value) {
//        T.DEBUG = value;
//    }

    /**
     * 设置是否在移动网络环境上报数据。默认true，仅在wifi网络下发送统计数据，
     * 4g/3g/2g/无网络情况下，实验数据将缓存到本地。需要允许移动网络下发送数据，设置false即可。
     * 在Application的OnCreate()方法中调用
     */
    public static void setOnlyWifiReport(boolean value) {
        try {
            IncrementStat.getInstance().setOnlyWifiSend(value);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 设置模块开关的获取间隔时间，默认180秒。在application的OnCreate()方法中调用。
     */
    public static void setGapTimeGetFlag(Context context, int sec) {
        try {
            if (context == null) {
                return;
            }
            if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
                return;
            }
            GetExperimentFlag.getInstance(context.getApplicationContext()).setGapTime(sec);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 设置发送缓存数据的间隔时间,在Application的OnCreate()方法中调用。毫秒为单位
     */
    public static void setGapTimeSendCacheData(long value) {
        try {
            IncrementStat.getInstance().setGAP_SEND_2_SERVER(value);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 获取实验的client_id debug 使用
     */
    public static String getClientId(Context context) {
        if (context == null) {
            return null;
        }
        if (!SDcardHandler.getInstance(context.getApplicationContext()).hasSdcard()) {
            return null;
        }
        return AdhocClientIDHandler.getInstance(context.getApplicationContext()).getClientId();
    }

    /**
     * 允许用户设置设备id
     */
    private static void setClientId(final String client_id, final Context context) {

        if (client_id != null && !client_id.equals("")) {
            T.i("设置client_id" + client_id);
            AdhocTracker.client_id = client_id;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String sdcard_clientid = AdhocClientIDHandler.getInstance(context).getFromSDCARD();
                        if (!AdhocTracker.client_id.equals(sdcard_clientid)) {
                            AdhocClientIDHandler.getInstance(context).saveSaveSDcard(client_id);
                        }
                    } catch (Throwable e) {
                        T.e(e);
                    }

                }
            }).start();

        } else {
            T.w("client_id is null");
        }
    }

    /**
     * 如果需要自定义clientId(设备唯一id)请使用该方法初始化sdk,注意避免重复调用init();
     */
    public static void initWithClientId(Context applicationContext, String appkey, String client_id) {
        setClientId(client_id, applicationContext);
        init(applicationContext, appkey);
    }

}

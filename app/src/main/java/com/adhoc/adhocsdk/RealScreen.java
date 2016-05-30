package com.adhoc.adhocsdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;

import com.adhoc.beans.ChangeBean;
import com.adhoc.nkzawa.socketio.client.Socket;
import com.adhoc.utils.T;
import com.adhoc.utils.Toaster;
import com.adhoc.utils.Utils;
import com.adhoc.nkzawa.emitter.Emitter;
import com.adhoc.nkzawa.socketio.client.IO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by dongyuangui on 15-5-21.
 */
public class RealScreen {
    public static final long DELAY_SCREEN_SHOT = 1500;
    private static RealScreen ourInstance = null;
    public static boolean EDIT_MODE = false;
    private Socket mSocket;
    private Context context;
    private Activity currentActivity;
    private boolean uping = true;
    private JSONObject jsonObject;
    private JSONObject oldJsonObject;
    private static long uploadGapTime = 200;
    private long lastcaptureTime;
    // 最后截屏
    private String lastScreen = null;
    private static int reconnectTimes = 3;

    private HashMap<String, Drawable> drawables = null;
    public boolean reset;

    public void saveDrawable(ChangeBean cb, String property, Drawable drawable) {

        if (drawables == null) {
            drawables = new HashMap<String, Drawable>();
        }
        if (!drawables.containsKey(cb.getMapKey() + property)) {
            drawables.put(cb.getMapKey() + property, drawable);
        }
    }

    public Drawable getDrawable(String mapkey) {
        if (drawables != null) {
            return drawables.get(mapkey);
        }
        return null;
    }

    private String getPositionString(JSONArray arrayPostions) throws JSONException {
        StringBuilder sb = new StringBuilder();
        int size = arrayPostions.length();
        for (int i = 0; i < size; i++) {
            sb.append(arrayPostions.getString(i));
        }
        return sb.toString();
    }

    private ViewTreeObserver.OnGlobalLayoutListener viewDrawChange = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (login) {
                sendPicRealTime(currentActivity, ScreenShot.GET_VIEW_TREE);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class lifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
//            boolean switchVersion = SharePrefHandler.getInstance(activity).getBoolean(AdhocConstants.SHARE_PREF_SWITCH_DEFAULT);
//            if (switchVersion) {
//                openTargetActivty();
//            }
//            T.i("realscreen Oncreate");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            currentActivity = activity;
            startConnect();
            T.i("realscreen OnStart");

        }

//        private long t = System.currentTimeMillis();

        // sendpic 触发暂定为 onlayoutchange
        @Override
        public void onActivityResumed(final Activity activity) {
            View view = activity.getWindow().getDecorView();
            view.getViewTreeObserver().addOnGlobalLayoutListener(viewDrawChange);
            // 记录activity状态，是否是reStart


//            View[] views = ReflectView.getRootView(activity.getWindowManager());
//            if (views != null) {
//                T.i("window is " + views.length);
//            }

            //---
//            StatusBean statsBean = statusActivity.get(code);

            if (oldJsonObject != null) {
                reset(oldJsonObject);
            }

            if (jsonObject != null) {

                Rendering.RenderHandler handler = new Rendering.RenderHandler(currentActivity, jsonObject);
                handler.sendEmptyMessage(Rendering.RENDER);
            }

//            if (statsBean == null) {
//                T.i("onResume 新的activity code " + code);
//                statsBean = new StatusBean();
//                statsBean.resumed = true;
//                statusActivity.put(code, statsBean);
//            } else {
//                T.i("restart activity id " + code);
//
//                if (statsBean.resumed) {
////                    sendPicRealTime(activity);
//                }
//                // restart activity code here
//
//            }

            T.i("realscreen Onresume");

        }

        @Override
        public void onActivityPaused(Activity activity) {


            T.i("realscreen OnPause");
        }


        @Override
        public void onActivityStopped(final Activity activity) {

            View view = activity.getWindow().getDecorView();
            view.getViewTreeObserver().removeGlobalOnLayoutListener(viewDrawChange);
            T.i("remove global view listener");
            if (!Utils.isAppOnForeground(activity)) {
                if (isTesterDevices) {
                    closeConnection();
                    recycle();
                    killapp(activity);
                }
            }

            T.i("realscreen OnStopped");
        }


        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            T.i("realscreen onActivitySaveInstancestate");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            int code = activity.hashCode();
//            StatusBean statsBean = statusActivity.get(code);
//            if (statsBean != null) {
//                T.i("删除旧的activity" + activity.getClass().getName() + " id : " + code);
//                statusActivity.remove(code);
//            }
            T.i("realscreen OnDestory");
        }
    }

    // 改变
    private void killapp(Activity activity) {
//        Intent intent = new Intent("com.appadhoc.chageedit");
//        activity.sendBroadcast(intent);
        SDcardHandler.getInstance(activity).writeEditString("0");
        T.i("send brocast end change edit flag");
//        killPid();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private RealScreen(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            T.w("设备sdk版本不支持可视AB测试功能，已经默认定向测试API Level 14及已上机型");
            return;
        }
        readTesterSetting(context);
        // init data
        initInstance();

        this.context = context;
//        AnalyticsConfigLoader mConfigLoader = new AnalyticsConfigLoader(context);
//        appkey = mConfigLoader.getString(AdhocConstants.APP_KEY);
        if (context instanceof Activity) {
            T.i(" is Activity Context");
            Application application = ((Activity) (context)).getApplication();
            application.registerActivityLifecycleCallbacks(new lifecycleCallbacks());

        } else if (context instanceof Application) {
            T.i(" is Application Context");
            Application application1 = (Application) (context);
            application1.registerActivityLifecycleCallbacks(new lifecycleCallbacks());

        } else {
            T.i("type :" + context.getClass().getName());
        }
    }

    private void initInstance() {
        reconnectTimes = 0;
        login = false;
        EDIT_MODE = false;
        uping = false;
//        statusActivity.clear();
        isEditing = false;
    }

    private void startConnect() {

        if (!login) {
            connect2Sever();
        }
    }

//    private void sendPic(final Activity activity) {
//        if (!login) {
//            return;
//        }
//        if (uping) {
//            T.i("is uploading ,server not response!");
//            return;
//        }
////        long current = System.currentTimeMillis();
////        T.i("gap time : " + (current - lastScreenShot));
////        if (current - lastScreenShot < uploadGapTime) {
////            T.i("is in Gap time stop sending pic");
////            return;
////        }
//        uping = true;
//        // handler 处理图片
//        ScreenShot.getInstance().sendDataPicRealTime(activity, new GetDrawingCache() {
//
//
//            @Override
//            public void onGetDrawingCache(JSONObject obj, final Bitmap compressedmap) {
//
//                final JSONObject screenData = obj;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        long t1 = System.currentTimeMillis();
//                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//                        compressedmap.compress(Bitmap.CompressFormat.JPEG, 50, bStream);
//                        String str = Base64.encodeToString(bStream.toByteArray(), Base64.DEFAULT);
//                        try {
//                            screenData.put("screenShot", str);
//                        } catch (JSONException e) {
//                            T.e(e);
//                        }
//                        // websocket send pic
//                        execSendPic(screenData.toString());
//                        T.i("screenStr" + screenData.toString());
//                        T.i("t2 deal with pic time " + (System.currentTimeMillis() - t1));
//                        compressedmap.recycle();
//
//                    }
//                }).start();
//
//
//            }
//        });
//    }

    private void execSendPic(String json) {

        T.i("uping : " + uping);
        if (!uping) {
            uping = true;
            if (mSocket != null) {
                mSocket.emit("picture_", json);
            }
        } else {
            // 保存最后一张
            lastScreen = json;
            T.i("not send picture_");
        }
    }

    public void sendPicRealTime(final Activity activity, final int type) {
        if (!login) {
            return;
        }
//        if (uping) {
//            T.i("is uploading ,server not response!");
//            return;
//        }
        long current = System.currentTimeMillis();
        T.i("gap time : " + (current - lastcaptureTime));
        if (current - lastcaptureTime < uploadGapTime) {
            T.i("is in Gap time stop sending pic");
            return;
        }
        lastcaptureTime = System.currentTimeMillis();
        // handler 处理图片
        ScreenShot.getInstance().sendDataPicRealTime(activity, new GetDrawingCache() {

            @Override
            public void onGetDrawingCache(JSONObject obj, final Bitmap compressedmap) {

                final JSONObject screenData = obj;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long t1 = System.currentTimeMillis();
                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                            compressedmap.compress(Bitmap.CompressFormat.JPEG, 50, bStream);
                            String str = Base64.encodeToString(bStream.toByteArray(), Base64.DEFAULT);

                            // todo delete it
//                        byte[] decodedByte = Base64.decode(str, 0);
//                        testbitmap.add(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));
                            // ====

                            screenData.put("screenShot", str);

                            execSendPic(screenData.toString());
                            T.i(" screenStr" + screenData.toString());
                            T.i(" t2 deal with pic time " + (System.currentTimeMillis() - t1));
                            compressedmap.recycle();
                        } catch (JSONException e) {
                            T.e(e);
                        } catch (Throwable ex) {
                            T.e(ex);
                        }
                    }
                }).start();
            }
        }, type);
    }

    // todo delete it
//    public ArrayList<Bitmap> testbitmap = new ArrayList<Bitmap>();
    private boolean isTesterDevices = false;

    public boolean isEditing = false;

    public void connect2Sever() {
        try {
            // 判断是否是tester
            isTesterDevices = Utils.checkIsTesterDevice(context, AdhocConstants.TEST_PACKAGE_NAME);
            if (!isTesterDevices) {
                return;
            }
            if (currentActivity != null) {
                writePackage(currentActivity);
            }

            readTesterSetting(currentActivity);
            // test delete
//            isEditing = true;
            if (!isEditing) {
                return;
            }
            try {
                IO.Options opts = new IO.Options();
                opts.forceNew = false;
                opts.reconnection = false;
                mSocket = IO.socket(AdhocConstants.mServer, opts);
                connect();
            } catch (URISyntaxException e) {
                T.e(e);
            } catch (Throwable ex) {
                T.e(ex);
            }

        } catch (Throwable e) {
            T.e(e);
        }
    }

    public void readTesterSetting(Context currentActivity) {


//        SharedPreferences ps = getTesterShare(currentActivity);
//        if (ps != null) {
//            isEditing = ps.getBoolean("editing", false);
//        }
//        T.i("share editing is: " + isEditing);
        String str = SDcardHandler.getInstance(context).readEditString();
        if (str != null && str.equals("1")) {
            isEditing = true;
        } else {
            isEditing = false;
        }


    }

    private SharedPreferences getTesterShare(Context currentActivity) {
        Context useContext = null;
        try {
            // 获取其他程序对应的Context
            useContext = currentActivity.createPackageContext(AdhocConstants.SCANNER_PACKAGE_NAME,
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (Throwable e) {
            T.e(e);
        }
        if (useContext != null) {
            return useContext.getSharedPreferences("setting", Context.MODE_WORLD_READABLE);
        }
        return null;

    }

    private void connect() {
        if (login == true) {
            T.i("已经连接server");
            return;
        }
//        if (appkey == null) {
//            AnalyticsConfigLoader mConfigLoader = new AnalyticsConfigLoader(context);
//            appkey = mConfigLoader.getString(AdhocConstants.APP_KEY);
//        }
        JSONObject object = new JSONObject();
        try {
            object.put("appkey", AdhocTracker.APPKEY);
            object.put("client_id", AdhocClientIDHandler.getInstance(context).getClientId());
            object.put("device_name", ParameterUtils.getDeviceName());
            object.put(AdhocConstants.APP_VERSION_CODE, ParameterUtils.getAppVersionCode(context));
        } catch (JSONException e) {
            T.e(e);
        }
        // 建立实时通信
        mSocket.on("props", onRealTime);
//        mSocket.on("mobile_login", onMobileLogin);
        mSocket.on("refresh", onRefresh);
        mSocket.on("login-fail", loginFail);
        mSocket.on("login-success", loginSuccess);
        mSocket.on("picture_ok", picture_ok);
        //
        mSocket.on("picture_reset", picture_reset);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.w("connect webserver error!");
                if (args != null) {
                    String value = ((Exception) args[0]).getMessage();
                    if (value != null) {
                        T.w("error msg :" + value);
                    }
                }
            }
        });
        mSocket.on("switch_version", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.i("start switch default version!");
                if (currentActivity != null) {
                    // 切换默认版本
                    switchVersion((String) args[0]);
                }
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.w("connect webserver Timeout error!");
                if (reconnectTimes < 3) {
                    reconnectTimes++;
                    T.w("sdk 开始第" + reconnectTimes + "次重连");
                    connect2Sever();
                } else {
                    T.w("重新连接 webserver fail");
                }

            }
        });
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.w("receive server disconnect webserver!");
            }
        });
        mSocket.on("del_change", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.w("receive server delete webserver!");
                switchVersion((String) args[0]);
            }
        });
        mSocket.on("web_disconnet", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                T.i("web disconnect !");
                closeConnection();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(200);
                            if (currentActivity != null) {
                                killapp(currentActivity);
                            }
                        } catch (Throwable e) {
                            T.e(e);
                        }
                    }
                }).start();
            }
        });
        T.i("send app_key success:" + AdhocTracker.APPKEY);
        mSocket.emit("login", object.toString());
        mSocket.connect();
    }

    private void switchVersion(String args) {
//        T.i("switch version");
//        Intent intent = new Intent(AdhocConstants.ADHOC_TESTER_RESTART_ACTION);
//
//        SharePrefHandler.getInstance(currentActivity).saveBoolean(AdhocConstants.SHARE_PREF_SWITCH_DEFAULT, true);
//        SharePrefHandler.getInstance(currentActivity).saveString(AdhocConstants.SHARE_PREF_TARGET_ACTIVITY,
//                currentActivity.getClass().getName());
//        Bundle bun = new Bundle();
//        bun.putString("package", currentActivity.getPackageName());
//        bun.putString("class", currentActivity.getClass().getName());
//        intent.putExtra("bun", bun);
//
//        T.i("classname :" + currentActivity.getClass().getName());
//        currentActivity.sendBroadcast(intent);
//        killPid();

        // 以上代码是kill pid的方式实现恢复默认值，有问题。
        // 启动页面需要发送startactivityforresult并且需要传递参数，而且需要初始化一些状态的时候，该方法会导致app的异常
        // 新方法使用按控件oldvalue来恢复默认值。
        // 首先保存当前使用的json为old json，然后保存新的json为当前json
        JSONObject value = null;
        try {
            value = new JSONObject(args);
        } catch (JSONException e) {
            T.e(e);
        }
        if (value == null) {
            T.w("props json is null");
            return;
        }
        oldJsonObject = jsonObject;
        jsonObject = value;
        // 恢复界面
        reset(oldJsonObject);
        // 执行新版本
        randerNewVersion(jsonObject);


    }

    private void reset(JSONObject oldJsonObject) {
        if (oldJsonObject == null) {
            return;
        }
        JSONObject copyObj = null;
        try {
            copyObj = new JSONObject(oldJsonObject.toString());
        } catch (JSONException e) {
            T.e(e);
        }
        T.i("before roll:" + oldJsonObject);

        rollingOverOldValue(copyObj);

        T.i("after roll:" + copyObj);
        RealScreen.getInstance(currentActivity).reset = true;

        Rendering.RenderHandler handler = new Rendering.RenderHandler(currentActivity, copyObj);
        handler.sendEmptyMessage(Rendering.RENDER);

    }

    private void rollingOverOldValue(JSONObject copyObj) {
        try {
            JSONArray arraychanges = copyObj.getJSONArray("changes");
            int size = arraychanges.length();
            for (int i = 0; i < size; i++) {
                JSONObject childChange = arraychanges.getJSONObject(i);
                JSONArray arrayProperties = childChange.getJSONArray("properties");
                int propterSize = arrayProperties.length();
                for (int j = 0; j < propterSize; j++) {
                    JSONObject obj = arrayProperties.getJSONObject(j);
                    // 执行交换
                    String tmp = obj.getString("value");
                    obj.put("value", obj.getString("old_value"));
                    obj.put("old_value", tmp);
                }
            }
        } catch (JSONException e) {
            T.e(e);
        }
    }

    private void randerNewVersion(JSONObject jsonObject) {
        Rendering.RenderHandler handler = new Rendering.RenderHandler(currentActivity, jsonObject);
        handler.sendEmptyMessage(Rendering.RENDER);
    }

    private void killPid() {
//        android.os.Process.killProcess(android.os.Process.myPid());
        T.i(android.os.Process.myPid() + "");
        System.exit(0);
        // 两种退出方法效果一样
    }

    public static RealScreen getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RealScreen(context);
        }
        return ourInstance;
    }


    // todo 如果关闭了链接怎么设置为false呢
    public static boolean login = false;

    private Emitter.Listener loginFail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            T.w("webserver login fail");
            T.w(AdhocConstants.onlyOneDevice);
            if (currentActivity != null) {
                new Handler(currentActivity.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Toaster.toast(currentActivity, AdhocConstants.onlyOneDevice);
                    }
                }.sendEmptyMessage(1);
            }
            initInstance();
        }

    };

    private void closeConnection() {
        if (mSocket != null) {
            initInstance();
            mSocket.disconnect();
            mSocket.close();
            mSocket.off();
            mSocket = null;
        }
    }

    private Emitter.Listener loginSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            T.i("login success u can edit it now");
            login = true;
            EDIT_MODE = true;
        }

    };
    private Emitter.Listener picture_ok = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            T.i("picture_ok");
            uping = false;
            if (lastScreen != null) {
//                uping = true;
                T.i("sending last screen ");
                execSendPic(lastScreen);
                lastScreen = null;
            }
            T.i("picture_ok .." + uping);
        }

    };
    // app 已经打开，web再打开发送pictrue reset
    private Emitter.Listener picture_reset = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            T.i("picture_reset");
            uping = false;
        }

    };
    private Emitter.Listener onRealTime = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // 执行改动
            if (currentActivity == null) {
                EDIT_MODE = true;
                T.w("currenActivity is null");
                return;
            }
            JSONObject value = (JSONObject) args[0];
            if (value == null) {
                T.w("props json is null");
                return;
            }
            Rendering.RenderHandler handler = new Rendering.RenderHandler(currentActivity, value);
            handler.sendEmptyMessage(Rendering.RENDER);
            jsonObject = value;
        }

    };

    private void recycle() {
        jsonObject = null;
        oldJsonObject = null;
        if (drawables != null) {
            drawables.clear();
        }
        lastScreen = null;
    }

    private Emitter.Listener onRefresh = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            T.i("onRefresh");
            // 执行改动
            if (currentActivity == null) {
                T.w("currenActivity is null");
                return;
            }
            uping = false;
            sendPicRealTime(currentActivity, ScreenShot.GET_VIEW_TREE);
        }

    };

    public void reConnectJustForTest() {
        if (currentActivity != null) {
            Toaster.toast(currentActivity, "pre connect 2 server :" + AdhocConstants.mServer);
        }
        closeConnection();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(800);
                    connect2Sever();
                } catch (Throwable e) {
                    T.e(e);
                }
            }
        }).start();
    }

    final class StatusBean {
        boolean resumed = false;
    }

    private void writePackage(Activity currentActivity) {

        ArrayList<String> list = SDcardHandler.getInstance(currentActivity).readApps(AdhocConstants.
                FILE_CLIENT_APPS);
        String appname = currentActivity.getPackageName();
        boolean exist = false;
        if (list != null) {
            int count = list.size();
            for (int i = 0; i < count; i++) {
                String packageName = list.get(i);
                if (appname.equals(packageName)) {
                    exist = true;
                }
            }
        }

        if (!exist) {
            SDcardHandler.getInstance(currentActivity).writeFileAppend(AdhocConstants.FILE_CLIENT_APPS,
                    currentActivity.getPackageName() + "\n");
        }

    }

}

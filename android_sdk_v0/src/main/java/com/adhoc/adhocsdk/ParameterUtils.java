/**
 * The class is a utility class to collect necessary information for better targeting.
 */

package com.adhoc.adhocsdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;

import com.adhoc.utils.T;
import com.adhoc.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class ParameterUtils {

    public static JSONObject getSummary(final Context context) throws JSONException {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        WifiInfo wifiInfo = getWifiInfo(context);
        JSONObject obj = new JSONObject();
        getOsPlatform(obj);
        // set device_name
        String device_name = getDeviceName();
        obj.put(AdhocConstants.DEVICE_NAME, device_name);
        // model name
        getDeviceModel(obj, device_name);

        getOSSdkVersion(obj);
        getScreenSize(config, obj);

        getLanguage(config, obj);
        getCountry(config, obj);
        getlocale(config, obj);

        getDisplayWidth(displayMetrics, obj);
        getDisplayHeight(displayMetrics, obj);

        getAndroidId(context, obj);
        getWifiMac(wifiInfo, obj);

        getNetworkConnectionInfo(context, obj);

        getOSVersionName(context, obj);
        getOSVersion(obj);
        getAdhocVersion(obj);
        getPackageName(context, obj);
        getAppVersion(context, obj);
        return obj;
    }

    private static void getAdhocVersion(JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.SDK_VERSION, AdhocConstants.ADHOC_SDK_VERSION);
    }

    private static void getOSVersionName(Context context, JSONObject obj) throws JSONException {
        String os_version = Utils.getOSVerison();
        obj.put(AdhocConstants.OS_VERSION_NAME, os_version);
    }


    private static void getOSVersion(JSONObject obj) throws JSONException {
        String os_version = Utils.getOSVerison();
        String os_ver = exec(os_version);
        obj.put(AdhocConstants.OS_VERSION, os_ver);
    }


    private static String exec(String str) {
        String os_version = null;
        if (str != null) {
            String[] strs = str.split("\\.");
            if (strs.length >= 2) {
                os_version = strs[0] + "." + strs[1];
            } else {
                return str;
            }
        }
        return os_version;
    }

    // Returns true if the permission is granted.
    private static boolean checkPermission(final Context context, final String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void getOsPlatform(JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.OS_PLATFORM, AdhocConstants.ANDROID_PLATFORM);
    }

    private static void getPackageName(final Context context, JSONObject obj) throws JSONException {
        String packageName = context.getPackageName();
        obj.put(AdhocConstants.PACKAGE_NAME, packageName);
    }

    private static void getAppVersion(final Context context, JSONObject obj) throws JSONException {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            obj.put(AdhocConstants.APP_VERSION, pInfo.versionName);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    public static int getAppVersionCode(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            T.e(e);
        }
        if (pInfo != null) {
            return pInfo.versionCode;
        } else {
            return -1;
        }
    }

    private static void getScreenSize(final Configuration config, JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.SCREEN_SIZE,
                config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
    }

    public static String getDeviceName() throws JSONException {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String getDeviceOwnerName() throws JSONException {
        return Build.DEVICE;
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static void getDeviceOsName(JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.DEVICE_OS_NAME, Build.DEVICE);
    }

    private static void getDeviceModel(JSONObject obj, String name) throws JSONException {
        obj.put(AdhocConstants.DEVICE_MODEL, name);
    }

    private static void getOSSdkVersion(JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.OS_SDK_VERSION, Build.VERSION.SDK_INT);
    }

    private static void getDisplayWidth(final DisplayMetrics displayMetrics, JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.DISPLAY_WIDTH,
                displayMetrics.widthPixels);
    }

    private static void getDisplayHeight(final DisplayMetrics displayMetrics, JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.DISPLAY_HEIGHT,
                displayMetrics.heightPixels);
    }

    private static void getLanguage(final Configuration config, JSONObject obj) throws JSONException {
        // test
        obj.put(AdhocConstants.LANGUAGE, config.locale.getLanguage());
    }

//    private static void getloale(final Configuration config, JSONObject obj) throws JSONException {
//        obj.put(AdhocConstants.LOCALE, config.locale.toString());
//    }

    private static void getCountry(final Configuration config, JSONObject obj) throws JSONException {
        obj.put(AdhocConstants.COUNTRY, config.locale.getCountry());
    }

    private static void getlocale(final Configuration config, JSONObject obj) throws JSONException {
        if (config != null && config.locale != null) {
            obj.put(AdhocConstants.LOCALE, config.locale.toString());
        }
    }


    private static void getAndroidId(final Context context, JSONObject obj) throws JSONException {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        obj.put(AdhocConstants.ANDROID_ID, android_id);
    }

    private static WifiInfo getWifiInfo(final Context context) {
        try {
            if (!checkPermission(context, AdhocConstants.P_ACCESS_WIFI_STATE)) return null;
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.getConnectionInfo();
        } catch (Throwable e) {
            T.e(e);
            return null;
        }
    }

//    private static void getWifiMac(final WifiInfo wifiInfo, JSONObject obj) throws JSONException {
//        String info = null;
//        if (wifiInfo == null) {
//            info = "";
//        } else {
//            info = wifiInfo.getMacAddress();
//        }
//        obj.put(AdhocConstants.WIFI_MAC, info);
//    }

    public static void getNetworkConnectionInfo(final Context context, JSONObject obj) throws JSONException {
        try {
            obj.put(AdhocConstants.NETWORK_STATE, getNetworkConnectionState(context));
        } catch (Throwable e) {
            T.e(e);
            obj.put(AdhocConstants.NETWORK_STATE, "");
        }
    }

    private static void getWifiMac(final WifiInfo wifiInfo, JSONObject obj) throws JSONException {
        String info = null;
        if (wifiInfo == null) {
            info = "";
        } else {
            info = wifiInfo.getMacAddress();
        }
        obj.put(AdhocConstants.WIFI_MAC, info);
    }
    public static String getNetworkConnectionState(final Context context) {
        try {
            if (!checkPermission(context, AdhocConstants.P_ACCESS_NETWORK_STATE)) {
                return AdhocConstants.NETWORK_STATE_UNKNOWN;
            }
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netInfo != null && netInfo.isConnected()) {
                return AdhocConstants.WIFI_CONNECTED;
            } else {
                netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (netInfo != null && netInfo.isConnected()) {
                    return AdhocConstants.MOBILE_CONNECTED;
                } else {
                    return AdhocConstants.NETWORK_UNCONNECTED;
                }
            }
        } catch (Throwable e) {
            T.e(e);
            return AdhocConstants.NETWORK_STATE_UNKNOWN;
        }
    }
}
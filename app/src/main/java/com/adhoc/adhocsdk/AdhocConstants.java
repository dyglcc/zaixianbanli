package com.adhoc.adhocsdk;

public final class AdhocConstants {
    public static final int TIME_OUT = 30 * 1000;
    public static final String FILE_EDITING = "ADHOC_EDITING";

    private AdhocConstants() {
    }

    public static final String APP_KEY = "AdhocAppKey";
    public static final String ADHOC_SDK_VERSION = "2.0";
    public static final String SDK_VERSION = "sdk_api_version";
    public static final String EVENT_GET_EXPERIMENT = "Event-GET_EXPERIMENT_FLAGS";
    public static final String ADHOC_TRACK_ACTIVITY = "adhoc_autoActivityTracking";
    public static final String ADHOC_TESTER_RESTART_ACTION = "com.appadhoc.tester.restart";
    public static final String SHARE_PREF_SWITCH_DEFAULT = "switch_default";
    public static final String SHARE_PREF_TARGET_ACTIVITY = "target_activity";
    public static final String SHARE_PREF_TARGET_PACKAGE = "target_package";

    public static final String mServer_online = "http://h5.appadhoc.com";
    public static final String mServer_test = "http://192.168.1.68:3000";
    public static String mServer = mServer_online;
    // Adhoc SDK server URL. The port is generated from telephone keypad.
    public static final String ADHOC_SERVER_URL = "https://tracker.appadhoc.com/tracker";
    //	public static final String ADHOC_SERVER_URL = "http://tracking.appadhoc.com:23462";
//    public static final String ADHOC_SERVER_GETFLAGS = "http://api.appadhoc.com/optimizer/api/getflags.php";
    public static final String onlyOneDevice = "进入编辑模式失败！已经有一台设备正在编辑App,同时只能有一台设备编辑应用";
//        public static final String ADHOC_SERVER_GETFLAGS = "https://experiment.appadhoc.com/get_flags";
//    public static final String ADHOC_SERVER_GETFLAGS = "http://192.168.1.88/get_flags_async";
        public static final String ADHOC_SERVER_GETFLAGS = "https://experiment.appadhoc.com/get_flags_async";
    public static final String ADHOC_PIC_SERVER = "http://api.appadhoc.com/optimizer/api/screenshot.php";

    // SDK stored files.
    public static final String FILE_UTM_INFO = "ADHOC_FILE_UTM_INFO";
    public static final String FILE_CLIENT_ID = "ADHOC_CLIENT_ID";
    public static final String FILE_CLIENT_APPS = "ADHOC_CLIENT_APP";
    public static final String SHARE_PREF_CLIENT_ID = "ADHOC_CLIENT_ID";

    public static final String COARSE_LAST_SENT_TIMESTAMP = "COARSE_LAST_SENT_TIMESTAMP";

    // Key-value's key for summary information.
    public static final String ANDROID_ID = "device_id";
    public static final String APP_VERSION = "app_version";
    public static final String APP_VERSION_CODE = "app_ver_code";
    public static final String COUNTRY = "country";
    public static final String LOCALE = "locale";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MODEL = "device_model";
    public static final String DEVICE_OS_NAME = "device_os_name";
    public static final String DISPLAY_WIDTH = "display_width";
    public static final String DISPLAY_HEIGHT = "display_height";
    public static final String EMAIL = "email";
    public static final String FACEBOOK_ATTR_ID = "facebook_attr_id";
    public static final String LANGUAGE = "language";
    public static final String NETWORK_STATE = "network_state";
    // The platforms (Android) applied to this Adhoc SDK.
    public static final String OS_VERSION = "os_version";
    public static final String OS_VERSION_NAME = "os_version_name";
    public static final String OS_PLATFORM = "OS";
    public static final String PACKAGE_NAME = "package_name";
    public static final String TEST_PACKAGE_NAME = "com.example.scannertest";
    public static final String PHONE_ID = "phone_id";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String SCREEN_SIZE = "screen_size";
    // This is Google's Android SDK.
    public static final String OS_SDK_VERSION = "sdk_version";
    public static final String WIFI_MAC = "wifi_mac";

    // SDK Platform. It can be iOS or customized android etc.
    public static final String ANDROID_PLATFORM = "google_android";

    // Facebook AttributionID URI.
    public static final String FACEBOOK_ATTR_ID_URI = "content://com.facebook.katana.provider.AttributionIdProvider";

    // Permissions
    public static final String P_ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";
    public static final String P_ACCESS_WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
    public static final String P_GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
    public static final String P_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    // Network State
    public static final String WIFI_CONNECTED = "WIFI_CONNECTED";
    public static final String MOBILE_CONNECTED = "MOBILE_CONNECTED";
    public static final String NETWORK_UNCONNECTED = "NETWORK_UNCONNECTED";
    public static final String NETWORK_STATE_UNKNOWN = "NETWORK_STATE_UNKNOWN";

    // share prefrence flags key
    public static final String PREFS_ABTEST_FLAGS = "adhoc_abtest_flags";
    // Auto Experment
    public static final String PREFS_ABTEST_AUTO_EXPERMENT = "adhoc_abtest_flags_auto";
    public static final String FLAG_ABTEST_AUTO_KEY = "__autoexperiment__";
    // share prefrence flags key
    public static final String ADHOC_FILE_PATH = "Adhoc";
    public static final String ADHOC_SCREEN_SHOT_FILE_SUFFIX = ".jpg";
    // screen shot quality
    public static final int QUALITY_SCREEN_SHOT = 10;

    public static final String SHARED_PREFERENCE = "ADHOC_SHARED_PREFERENCE";
    // scanner 的package name
    public static final String SCANNER_PACKAGE_NAME = "com.example.scannertest";
    // diff flag
    public static final String FLAG_CHANGED = "flag_changed";
    // custom para
    public static final String CUSTOM_PARA = "custom";

    public static final String fgf = "&&&";
    // tracker type
    public static final int INTEGER_ = 0;
    public static final int FLOAT_ = 1;
    public static final int DOUBLE_ = 2;
    public static final int LONG_ = 3;

}

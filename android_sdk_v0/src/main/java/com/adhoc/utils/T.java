package com.adhoc.utils;

import android.util.Log;

public class T {
    static String AppName = "ADHOC_SDK";

    public static final Boolean DEBUG = true;

    public static void i(String string) {
        if (string == null) {
            return;
        }
        if (DEBUG) {
            Log.i(AppName, string);
        }
    }

    public static void i(String tag, String string) {
        if (string == null) {
            return;
        }
        if (DEBUG) {
            Log.i(tag, string);
        }
    }

    public static void w(String string) {
        if (string == null) {
            return;
        }
        if (DEBUG) {
            Log.e(AppName, string);
        } else {
            System.err.println(string);
        }
    }

//    public static void a(int num) {
//        if (DEBUG) {
//            Log.d(AppName, Integer.toString(num));
//        }
//    }

    public static void e(Exception exception) {
        if (exception == null || exception.toString() == null) {
            return;
        }
        if (DEBUG) {
            if (exception != null) {
                exception.printStackTrace();
            }
            Log.e(AppName, exception.toString());
        } else {
            System.err.println(exception.toString());
        }
    }

    public static void e(Error error) {
        if (error == null || error.toString() == null) {
            return;
        }
        if (DEBUG) {
            error.printStackTrace();
            Log.e(AppName, error.toString());
        } else {
            System.err.println(error.toString());
        }
    }

    public static void e(Throwable throwable) {
        if (throwable == null || throwable.toString() == null) {
            return;
        }
        if (DEBUG) {
            throwable.printStackTrace();
            Log.e(AppName, throwable.toString());
        } else {
            System.err.println(throwable.toString());
        }
    }

    public static void d(String msg) {
        if (msg == null) {
            return;
        }
        if (DEBUG) {
            Log.d(AppName, msg);
        }
    }
}

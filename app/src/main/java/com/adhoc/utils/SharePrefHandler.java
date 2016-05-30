package com.adhoc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.adhoc.adhocsdk.AdhocConstants;

/**
 * Created by dongyuangui on 15-4-16.
 */
public class SharePrefHandler {
    // SharedPreference's key

    private static SharePrefHandler handler ;
    protected Context mContext;
    private SharePrefHandler(Context context){
        this.mContext = context;
    }

    public static SharePrefHandler getInstance(Context context){
        if(handler == null){
            handler = new SharePrefHandler(context);
        }
        return handler;
    }

    // 保存String
    public void saveString(String key, String value){
        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key,value);
        editor.commit();
    }
//    // 得到String
    public String getString(String key){

        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        return share.getString(key,"");

    }
    // 保存boolean
    public void saveBoolean(String key,boolean value){

        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 得到boolean
    public boolean getBoolean(String key){

        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        return share.getBoolean(key, false);

    }
    // 保存int
    protected void saveInt(String key,int value){

        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    // 得到int
    public int getInt(String key){

        SharedPreferences share = mContext.getSharedPreferences(AdhocConstants.SHARED_PREFERENCE, 0);
        return share.getInt(key,0);

    }

//    public boolean getTesterPreferenceSaoFlag(){
//        boolean tester_scan = false;
//        try {
//            Context context = mContext.createPackageContext(AdhocConstants.SCANNER_PACKAGE_NAME,Context.CONTEXT_IGNORE_SECURITY);
//            SharedPreferences otherShare = context.getSharedPreferences("tester",Context.MODE_MULTI_PROCESS);
//            tester_scan = otherShare.getBoolean("scan",false);
//            T.i("tester_scan flag is " + tester_scan);
//        } catch (PackageManager.NameNotFoundException e) {
//            T.e(e);
//        }
//        return tester_scan;
//    }


//    public void setTesterScanFalse() {
//        try {
//            Context context = mContext.createPackageContext(AdhocConstants.SCANNER_PACKAGE_NAME,Context.CONTEXT_IGNORE_SECURITY);
//            SharedPreferences otherShare = context.getSharedPreferences("tester",Context.MODE_MULTI_PROCESS);
//            SharedPreferences.Editor editor = otherShare.edit();
//            editor.putBoolean("scan",false);
//            editor.commit();
//        } catch (PackageManager.NameNotFoundException e) {
//            T.e(e);
//        }
//    }
}

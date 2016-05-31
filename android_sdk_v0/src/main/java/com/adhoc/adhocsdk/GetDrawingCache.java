package com.adhoc.adhocsdk;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by dongyuangui on 15/11/27.
 */
public interface GetDrawingCache {

    void onGetDrawingCache(JSONObject obj, Bitmap compressedmap);
}

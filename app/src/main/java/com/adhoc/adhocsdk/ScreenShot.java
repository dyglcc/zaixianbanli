package com.adhoc.adhocsdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.os.Handler;

import com.adhoc.utils.T;
import com.adhoc.viewtool.ReflectView;
import com.adhoc.viewtool.ViewTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ScreenShot {

    private ScreenShot() {

    }

    private static ScreenShot instance;
    public static final int GET_VIEW_TREE = 2;
    public static final int GET_VIEW_TREE_DIALOG = 3;

    public static ScreenShot getInstance() {
        if (instance == null) {
            instance = new ScreenShot();
        }
        return instance;

    }

    public File getScreenShotFile(Context context) {
        return new File(context.getCacheDir() + "/" +
                AdhocConstants.ADHOC_FILE_PATH + "/" + "android_" + context.getClass().getSimpleName() +
                AdhocConstants.ADHOC_SCREEN_SHOT_FILE_SUFFIX);
    }

    public Bitmap compressByQuality(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = AdhocConstants.QUALITY_SCREEN_SHOT;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        T.i("图片压缩前大小：" + bitmap.getRowBytes() + "byte");
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length);
        T.i("图片压缩后大小：" + compressedBitmap.getRowBytes() + "byte");
        return compressedBitmap;
    }


    private void shot(Context context, Bitmap map) {

        File file = getScreenShotFile(context);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 清理
        if (!file.exists()) {
            System.err.print("文件不存在");
            try {
                // 删除原来文件
                file.delete();
                // 创建新文件
                file.createNewFile();
            } catch (IOException e) {
                T.e(e);
            } catch (Throwable ex) {
                T.e(ex);
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            if (null != fos) {
                if (map != null) {
                    map.compress(Bitmap.CompressFormat.JPEG, AdhocConstants.QUALITY_SCREEN_SHOT, fos);
                }
            }
        } catch (Throwable e) {
            T.e(e);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Throwable e) {
                    T.e(e);
                }
            }
        }
    }

    static class IncomingHandler extends Handler {

        private Activity activity;
        private GetExperimentFlag.OnShotFile onShotFileListener;

        IncomingHandler(Looper looper, Activity activity, GetExperimentFlag.OnShotFile onShotFile) {
            super(looper);
            this.activity = activity;
            this.onShotFileListener = onShotFile;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                shotFile(null);
            }
        }

        private void shotFile(JSONObject obj) {
            try {
                long t1 = System.currentTimeMillis();
                View view = activity.getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
                Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                int width = activity.getWindowManager().getDefaultDisplay().getWidth();
                int height = activity.getWindowManager().getDefaultDisplay()
                        .getHeight();
//            Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width,
//                    height - statusBarHeight);
                if (bitmap == null) {
                    return;
                }
//                if (height > bitmap.getHeight() || width > bitmap.getWidth()) {
//                    return;
//                }
//                Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, width,
//                        height);
                ScreenShot.getInstance().shot(activity, bitmap);
                T.i((System.currentTimeMillis() - t1) + " getDrawing cache pic time");
                if (onShotFileListener != null) {
                    onShotFileListener.onShotFile(obj);
                }
                view.destroyDrawingCache();
            } catch (Exception e) {
                T.e(e);
            }
        }

    }

    static class RealScreenHandler extends Handler {

        private Activity activity;
        private GetDrawingCache getDrawingCache;

        RealScreenHandler(Looper looper, Activity activity, GetDrawingCache getDrawingCache) {
            super(looper);
            this.activity = activity;
            this.getDrawingCache = getDrawingCache;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GET_VIEW_TREE) {
                JSONObject screenData = getViewTree(activity.getWindow().getDecorView());
                captureScreen(screenData);
            } else if (msg.what == GET_VIEW_TREE_DIALOG) {

                View[] views = ReflectView.getRootView(activity.getWindowManager());
                JSONObject screenData = getViewTree(views);
//                JSONObject screenData = getViewTree(views[views.length - 1]);
//                captureScreen(screenData);
                captureScreen(views, screenData);
            }
        }

        private JSONObject getViewTree(View[] views) {
            long t1 = System.currentTimeMillis();
            JSONArray arrayTrees = new JSONArray();
            for (int i = 0; i < views.length; i++) {
                JSONObject viewTreeJson = new JSONObject();
                try {
                    ViewTool.getInstance().getViewJson(viewTreeJson, 0, views[i], activity);
                    arrayTrees.put(viewTreeJson);
                } catch (JSONException e) {
                    T.e(e);
                }
            }

            T.i((System.currentTimeMillis() - t1) + " getViewTree time");
            JSONObject screenData = new JSONObject();
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            float density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            try {
                screenData.put("dpi", density);
                screenData.put("screenWidth", screenWidth);
                screenData.put("screenHeight", screenHeight);
                screenData.put("windows", arrayTrees);
                screenData.put("activity", activity.getClass().getName());

            } catch (JSONException e) {
                T.i(e.toString());
            }
            return screenData;
        }

        private JSONObject getViewTree(View view) {
            long t1 = System.currentTimeMillis();
            JSONObject viewTreeJson = new JSONObject();
            try {
                ViewTool.getInstance().getViewJson(viewTreeJson, 0, view, activity);
            } catch (JSONException e) {
                T.e(e);
            }

            T.i((System.currentTimeMillis() - t1) + " getViewTree time");
            JSONObject screenData = new JSONObject();
            JSONArray arrayWindows = new JSONArray();
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            float density = dm.density;        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            try {
                screenData.put("dpi", density);
                screenData.put("screenWidth", screenWidth);
                screenData.put("screenHeight", screenHeight);
                screenData.put("windows", arrayWindows.put(viewTreeJson));
                screenData.put("activity", activity.getClass().getName());

            } catch (JSONException e) {
                T.i(e.toString());
            }
            return screenData;
        }

        private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap, int x, int y) {
            Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                    firstBitmap.getConfig());
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(firstBitmap, new Matrix(), null);
            canvas.drawBitmap(secondBitmap, x, y, null);
            return bitmap;
        }

        private void captureScreen(View[] views, JSONObject obj) {
            Bitmap compressed = null;
            for (int i = 0; i < views.length; i++) {
                Bitmap layer = null;
                try {
                    long t1 = System.currentTimeMillis();
                    views[i].setDrawingCacheEnabled(true);
                    views[i].buildDrawingCache();
                    int[] x = new int[2];
                    views[i].getLocationOnScreen(x);
                    Bitmap bitmap = views[i].getDrawingCache();
//                Rect frame = new Rect();
//                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//                int statusBarHeight = frame.top;
//                int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//                int height = activity.getWindowManager().getDefaultDisplay()
//                        .getHeight();
                    if (bitmap == null) {
                        return;
                    }
                    layer = ScreenShot.getInstance().compressByQuality(bitmap);
                    T.i((System.currentTimeMillis() - t1) + " getDrawing cache pic time");

                    if (i > 0) {
                        compressed = mergeBitmap(compressed, layer, x[0], x[1]);
                    } else {
                        compressed = layer;
                    }

//                view.destroyDrawingCache();
                    views[i].setDrawingCacheEnabled(false);
                } catch (Exception e) {
                    T.e(e);
                }
            }
            if (getDrawingCache != null) {
                getDrawingCache.onGetDrawingCache(obj, compressed);
            }
        }


        private void captureScreen(JSONObject obj) {
            try {
                long t1 = System.currentTimeMillis();
                View view = activity.getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
//                Rect frame = new Rect();
//                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//                int statusBarHeight = frame.top;
//                int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//                int height = activity.getWindowManager().getDefaultDisplay()
//                        .getHeight();
                if (bitmap == null) {
                    return;
                }
                Bitmap compressed = ScreenShot.getInstance().compressByQuality(bitmap);
                T.i((System.currentTimeMillis() - t1) + " getDrawing cache pic time");
                if (getDrawingCache != null) {
                    getDrawingCache.onGetDrawingCache(obj, compressed);
                }
//                view.destroyDrawingCache();
                view.setDrawingCacheEnabled(false);
            } catch (Throwable e) {
                T.e(e);
            }
        }

        private void captureScreen(View view, JSONObject obj) {
            try {
                long t1 = System.currentTimeMillis();
//                View view = activity.getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap bitmap = view.getDrawingCache();
//                Rect frame = new Rect();
//                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//                int statusBarHeight = frame.top;
//                int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//                int height = activity.getWindowManager().getDefaultDisplay()
//                        .getHeight();
                if (bitmap == null) {
                    return;
                }
                Bitmap compressed = ScreenShot.getInstance().compressByQuality(bitmap);
                T.i((System.currentTimeMillis() - t1) + " getDrawing cache pic time");
                if (getDrawingCache != null) {
                    getDrawingCache.onGetDrawingCache(obj, compressed);
                }
//                view.destroyDrawingCache();
                view.setDrawingCacheEnabled(false);
            } catch (Exception e) {
                T.e(e);
            }
        }

    }

    public void takeScreenShot(final Activity activity, final GetExperimentFlag.OnShotFile onShotFile) {
        if (!(activity instanceof Activity)) {
            T.w("截屏出错：只截屏activity");
            return;
        }
        IncomingHandler handler = new IncomingHandler(activity.getMainLooper(), activity, onShotFile);
        handler.sendEmptyMessageDelayed(1, RealScreen.DELAY_SCREEN_SHOT);

    }

    public void sendDataPic(final Activity activity, final GetExperimentFlag.OnShotFile onShotFile) {
        if (!(activity instanceof Activity)) {
            T.w("截屏出错：只截屏activity");
            return;
        }
        IncomingHandler handler = new IncomingHandler(activity.getMainLooper(), activity, onShotFile);
        handler.sendEmptyMessageDelayed(GET_VIEW_TREE, RealScreen.DELAY_SCREEN_SHOT);

    }

    public void sendDataPicRealTime(final Activity activity, final GetDrawingCache onShotFile, int get_view_tree) {
        if (!(activity instanceof Activity)) {
            T.w("截屏出错：只截屏activity");
            return;
        }
        RealScreenHandler handler = new RealScreenHandler(activity.getMainLooper(), activity, onShotFile);
        handler.sendEmptyMessage(get_view_tree);
//         todo test delete
//        handler.sendEmptyMessageDelayed(GET_VIEW_TREE, 1000);

    }

//    public Bitmap getScreenShotBimap(Context context) {
//        Bitmap bimap = null;
//        File file = getScreenShotFile(context);
//        try {
//            bimap = BitmapFactory.decodeStream(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            T.e(e);
//        }
//        return bimap;
//    }

}

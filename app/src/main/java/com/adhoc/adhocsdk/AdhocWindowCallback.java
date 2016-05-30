package com.adhoc.adhocsdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.adhoc.utils.T;
import com.adhoc.viewtool.ReflectView;

import java.util.ArrayList;


/**
 * Created by dongyuangui on 16/3/30.
 */
public class AdhocWindowCallback implements Window.Callback {
    private Window.Callback callback;
    private WindowManager windowManager;
    private Activity context;

    public Context getContext() {
        return context;
    }

    private boolean dialogIsOnscreen = false;

    public void setContext(Activity context) {
        this.context = context;
    }


    public void destory() {
        callback = null;
        context = null;
        windowManager = null;
    }

    public AdhocWindowCallback(Window.Callback callback) {
        this.callback = callback;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (callback != null) {
            return callback.dispatchKeyEvent(event);
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (callback != null) {
            return callback.dispatchKeyShortcutEvent(event);
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (callback != null) {
            return callback.dispatchTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (callback != null) {
            return callback.dispatchTouchEvent(event);
        }
        return false;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (callback != null) {
            return callback.dispatchGenericMotionEvent(event);
        }
        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (callback != null) {
            return callback.dispatchPopulateAccessibilityEvent(event);
        }
        return false;
    }

    @Override
    public View onCreatePanelView(int featureId) {
        if (callback != null) {
            return callback.onCreatePanelView(featureId);
        }
        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (callback != null) {
            return callback.onCreatePanelMenu(featureId, menu);
        }
        return false;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (callback != null) {
            return callback.onPreparePanel(featureId, view, menu);
        }
        return false;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (callback != null) {
            return callback.onMenuOpened(featureId, menu);
        }
        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (callback != null) {
            return callback.onMenuItemSelected(featureId, item);
        }
        return false;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        if (callback != null) {
            callback.onWindowAttributesChanged(attrs);
        }

    }

    @Override
    public void onContentChanged() {
        if (callback != null) {
            callback.onContentChanged();
        }

    }

    // hasFocus is false 说明activity之上有window
    // 可以通过
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        // 当activity的失去窗口焦点,说明有其他window出现.判断出现的是window否是dialog.
        if (callback != null) {
            if (hasFocus == false) {
                T.i("adhoc sdk get onfocus value : " + hasFocus);
                View[] views = ReflectView.getRootView(windowManager);
                ArrayList<View> list = isDialog(views);
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Rendering.RenderHandler handler = new Rendering.RenderHandler(
                                Rendering.getInstance(context).getmCurrentActivity(),
                                Rendering.getInstance(context).getJsonObject());
                        handler.setView(list.get(i));
                        handler.sendEmptyMessage(Rendering.RENDER_DIALOG);
                    }
                    dialogIsOnscreen = true;
                }
            } else {// activity hasfocus
                // dialog is onscreen
                if (dialogIsOnscreen) {

                    if (RealScreen.getInstance(context).login) {
                        RealScreen.getInstance(context).sendPicRealTime(context, ScreenShot.GET_VIEW_TREE);
                    }
                }
            }
            callback.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (callback != null) {
            callback.onAttachedToWindow();
        }

    }

    @Override
    public void onDetachedFromWindow() {
        if (callback != null) {
            callback.onDetachedFromWindow();
        }

    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        if (callback != null) {
            callback.onPanelClosed(featureId, menu);
        }

    }

    @Override
    public boolean onSearchRequested() {
        if (callback != null) {
            callback.onSearchRequested();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {

        if (this.callback != null) {
            this.callback.onWindowStartingActionMode(callback);
        }
        return null;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (callback != null) {
            callback.onActionModeStarted(mode);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActionModeFinished(ActionMode mode) {
        if (context != null) {
            context.onActionModeFinished(mode);
        }
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    private ArrayList<View> isDialog(View[] views) {
        ArrayList<View> ls = new ArrayList();
        if (views != null) {
            T.i("window is onwindowfocus " + views.length);
            for (int i = 0; i < views.length; i++) {
                if (views[i].getContext() instanceof Activity) {
                    continue;
                } else {
                    ls.add(views[i]);
                }
            }
        }
        return ls;

    }
}

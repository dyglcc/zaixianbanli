package com.adhoc.adhocsdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adhoc.beans.ChangeBean;
import com.adhoc.beans.PositionBean;
import com.adhoc.beans.PropertyBean;
import com.adhoc.beans.Rowbean;
import com.adhoc.beans.StatsBean;
import com.adhoc.ninepatch.NinePatchChunk;
import com.adhoc.pic.Picasso;
import com.adhoc.pic.Target;
import com.adhoc.property.Properties;
import com.adhoc.property.Types;
import com.adhoc.utils.T;
import com.adhoc.utils.Utils;
import com.adhoc.viewtool.ReflectView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by dongyuangui on 15-5-21.
 */
public class Rendering {
    public static final int RENDER = 1;
    public static final int RENDER_NORMAL = RENDER + 1;
    public static final int RENDER_DIALOG = RENDER_NORMAL + 1;
    public static final int NOTIFY_ABSLIST_VIEW = 2;
    private static final boolean IS_DIALOG = true;
    private static final boolean IS_ACTIVITY = false;
    private static Rendering ourInstance = null;
    private static HashMap<Integer, AdhocWindowCallback> windowsCallbacks = new HashMap<Integer, AdhocWindowCallback>();

    private Activity mCurrentActivity;
    private static final String render = "rander";
    private Context context;
    private DisplayMetrics dm;
    public String experimentId = null;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    private HashMap<String, View.OnClickListener> srcClickListener = null;
    private JSONObject jsonObject;
    private HashMap<String, AbsListBean> map;
    private static HashMap<Integer, AdhocWindowCallback> statusActivity = new HashMap<Integer, AdhocWindowCallback>();

    public Activity getmCurrentActivity() {
        return mCurrentActivity;
    }

    // realscreen(编辑模式) 使用 RenderHandler
    static class RenderHandler extends Handler {

        private Activity context;
        private JSONObject real;
        private View dialogView;

        RenderHandler(Activity activity, JSONObject real) {
            super(activity.getMainLooper());
            this.context = activity;
            this.real = real;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RENDER) {
                Rendering.getInstance(context).rendering(real, context.getClass().getName(), context.getWindow().getDecorView(), IS_ACTIVITY);

                RealScreen.getInstance(context).reset = false;
                if (RealScreen.EDIT_MODE) {
                    RealScreen.getInstance(context).sendPicRealTime(context, ScreenShot.GET_VIEW_TREE);
                }
//                Rendering.getInstance(context).jsonObject = real;

            } else if (msg.what == RENDER_DIALOG) {

                if (dialogView != null) {
                    Rendering.getInstance(context).rendering(real, context.getClass().getName(), dialogView, IS_DIALOG);
                }
                if (RealScreen.EDIT_MODE) {
                    RealScreen.getInstance(context).sendPicRealTime(context, ScreenShot.GET_VIEW_TREE_DIALOG);
                }

            }


        }

        public void setView(View view) {
            this.dialogView = view;
        }
    }

    // rendering 类中使用,RenderHandlerNormal
    static class RenderHandlerNormal extends Handler {

        private Activity context;
        private JSONObject real;

        RenderHandlerNormal(Activity activity, JSONObject real) {
            super(activity.getMainLooper());
            this.context = activity;
            this.real = real;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RENDER_NORMAL) {
                if (context != null) {
                    Rendering.getInstance(context).rendering(real, context.getClass().getName(),
                            context.getWindow().getDecorView(), IS_ACTIVITY);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void rendering(JSONObject jsonObject, String activityName, View view, boolean isDialog) {

        if (jsonObject == null) {
            T.i("jsonObject is null");
            return;
        }

        // 检查编辑版本和运行版本是否一致
        int versionCode = jsonObject.optInt(AdhocConstants.APP_VERSION_CODE, versionCodeApp);
        if (versionCodeApp != versionCode) {
            T.w("当前应用版本号和线上版本不一致,用户将不进入试验.");
            return;
        }
        ChangeBean[] cb = null;
        try {
            cb = getChangeBeans(activityName, jsonObject, isDialog);

            if (cb != null) {
                // 设置normal view
                setNormalViews(activityName, cb, view);
                // 设置listView
                setListViewItems(activityName, cb, view, isDialog);
            } else {
                T.i("have no changes!");
            }

            // 添加自动统计优化指标
            StatsBean[] statsBeans = getStatBean(activityName, jsonObject);
            if (statsBeans != null) {
                // 优化指标统计
                setStats(activityName, statsBeans, view);
            } else {

                T.i("have no stat current page");
            }

        } catch (JSONException e) {
            T.e(e);
        }
        this.jsonObject = jsonObject;
    }

    /***
     * @param statsBean
     * @param listener
     */
    public void saveClickListener(StatsBean statsBean, View.OnClickListener listener) {
        if (srcClickListener == null) {
            srcClickListener = new HashMap<String, View.OnClickListener>();
        }
        if (!srcClickListener.containsKey(statsBean.getMapKey())) {
            srcClickListener.put(statsBean.getMapKey(), listener);
        }
    }

    private boolean hasAlreadySetListener(String mapKey) {
        if (srcClickListener == null) {
            srcClickListener = new HashMap<String, View.OnClickListener>();
            return false;
        }
        return srcClickListener.containsKey(mapKey);
    }

    private void setStats(String name, StatsBean[] statsBeans, View rootview) {
        for (int i = 0; i < statsBeans.length; i++) {

            if (statsBeans[i] != null && !hasAlreadySetListener(statsBeans[i].getMapKey())) {
                View view = getTargetView(name, rootview, statsBeans[i].getPositions());
                if (view != null) {
                    T.i("set listener " + view.getClass().getName());
                    // 自动统计的优化指标
                    View.OnClickListener listener_src = ReflectView.invokeListener(mCurrentActivity, view, statsBeans[i].getStatsKey());
                    saveClickListener(statsBeans[i], listener_src);
                } else {
                    T.i("can not find view");
                    continue;
                }

            }
        }
    }

    private void setNormalViews(String name, ChangeBean[] cbs, View view) {
        for (int i = 0; i < cbs.length; i++) {
            if (cbs[i] != null && !cbs[i].isAbsListView()) {
                dealRenderNormarlView(name, cbs[i], view);
            }
        }
    }

    private void setListViewItems(String name, ChangeBean[] cbs, View rootview, boolean isDialog) {

        if (map == null) {
            map = new HashMap<String, AbsListBean>();
        }
        // 合并成一个listview设置一次Onscroll listener，避免多个item 设置多次Listenter
        // key是listView,value是分组的 changebean 相同的listView
        combine(cbs);

        for (Map.Entry<String, AbsListBean> entry : map.entrySet()) {

            AbsListBean ab = entry.getValue();
            final ArrayList<ChangeBean> changeBeans = ab.getList();

            if (changeBeans != null && !changeBeans.isEmpty()) {

                ChangeBean cb = changeBeans.get(0);

                View view = getListView(name, cb.getPositions(), rootview);

                // 为多个listView设置
                if (view != null && view instanceof AbsListView) {

                    final AbsListView listView = (AbsListView) view;
                    // 注册数据集观察者，当界面已经randering完成，但是还没有加载完成数据。会导致view不能渲染。
                    setAbsListObser(ab, listView, isDialog);

                    // 设置listView的OnscrollListener setChangeBeans
                    OnscrollListener listener = new OnscrollListener(mCurrentActivity);
                    listener.setChangeBeans(changeBeans);
                    ReflectView.invokeListViewOnscrollListener(listView, listener);
                    // 初始化listview
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            T.i("init listview change ");
                            setListValues(changeBeans, listView, mCurrentActivity);
                        }
                    }, 200);
                }
            }
        }
    }


    private void setAbsListObser(AbsListBean ab, AbsListView listView, final boolean isDialog) {
        if (!ab.isObsered()) {
            Adapter adapter = listView.getAdapter();
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if (mCurrentActivity != null) {
                        Rendering.this.rendering(jsonObject, mCurrentActivity.getClass().getName(),
                                mCurrentActivity.getWindow().getDecorView(), isDialog);
                    }
                }
            });
            ab.setIsObsered(true);
        }
    }

    class OnscrollListener extends ScrollAbslistview implements AbsListView.OnScrollListener {

        private ArrayList<ChangeBean> changeBeans;

        public ArrayList<ChangeBean> getChangeBeans() {
            return changeBeans;
        }

        public void setChangeBeans(ArrayList<ChangeBean> changeBeans) {
            this.changeBeans = changeBeans;
        }

        OnscrollListener(Activity activity) {
            super(activity);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (oldListener != null) {
                oldListener.onScrollStateChanged(view, scrollState);
            }
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                setListValues(changeBeans, view, activity);
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (oldListener != null) {
                oldListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }

        }
    }

    private void setListValues(ArrayList<ChangeBean> changeBeans, AbsListView view, Activity activity) {
        if (changeBeans != null) {
            int first_visiable = view.getFirstVisiblePosition();
            int last_visiable = view.getLastVisiblePosition();

            for (int i = 0; i < changeBeans.size(); i++) {
                ChangeBean cb = changeBeans.get(i);
                Rowbean row = getRowbyChangeBean(cb);
                if (row.getRow() >= first_visiable && row.getRow() <= last_visiable) {
                    T.i("找到ListView的item row number : " + row);

                    dealRenderListView(view.getChildAt(row.getRow() - first_visiable), activity, cb, row);
                }
            }
        }
    }

    private Rowbean getRowbyChangeBean(ChangeBean cb) {
        Rowbean rowbean = new Rowbean();
        PositionBean[] pbs = cb.getPositions();
        for (int i = 2; i < pbs.length; i++) {
            if (pbs[i].getRow() != -1) {
                rowbean.setIndex(i);
                rowbean.setRow(pbs[i].getRow());
                break;
            }
        }
        return rowbean;
    }

    private void combine(ChangeBean[] cbs) {
        for (int i = 0; i < cbs.length; i++) {
            ChangeBean cb = cbs[i];
            if (cb == null) {
                continue;
            }
            if (cb.isAbsListView()) {
                PositionBean[] pbs = cb.getPositions();
                StringBuilder sb = new StringBuilder();
                for (int p = 2; p < pbs.length; p++) {

                    PositionBean pb = pbs[p];
                    if (pb.getRow() == -1) {
                        // 区分listView
                        sb.append(pb.getView() + "#" + pb.getPos() + "#");
                    } else {
                        break;
                    }
                }
                AbsListBean ab = map.get(sb.toString());
                if (ab == null) {
                    ab = new AbsListBean();
                    ArrayList<ChangeBean> cbSameAbsListview = new ArrayList<ChangeBean>();
                    cbSameAbsListview.add(cb);
                    ab.setList(cbSameAbsListview);
                    map.put(sb.toString(), ab);
                } else {
                    ArrayList<ChangeBean> cbSameAbsListview = ab.getList();
                    if (cbSameAbsListview == null) {
                        cbSameAbsListview = new ArrayList<ChangeBean>();
                    }
                    cbSameAbsListview.add(cb);
                }
            }

        }
    }

    private void dealRenderNormarlView(String name, ChangeBean cb, View rootview) {

        if (cb != null) {
            final View view = getTargetView(name, rootview, cb.getPositions());
            exec(view, cb);
        } else {
            T.w("找不到View");
        }
    }

    private void dealRenderListView(View view, Activity activity, ChangeBean cb, Rowbean rowbean) {
        if (cb != null) {
            PositionBean[] positionBean = cb.getPositions();
            // 从item View 开始
            for (int i = rowbean.getIndex() + 1; i < positionBean.length; i++) {
                PositionBean pb = positionBean[i];
                view = getChildView_v1(view, pb);
                current_item = pb.getCuritem();
            }
            exec(view, cb);
        } else {
            T.w("找不到View");
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void exec(View view, ChangeBean cb) {
        if (view != null) {

            try {

                PropertyBean[] pbs = cb.getProperties();
                for (int i = 0; i < pbs.length; i++) {
                    PropertyBean pb = pbs[i];
                    String property = pb.getProperty();
//                Toaster.toast(mCurrentActivity, "ready Rander");
                    T.i("ready Rendering");
                    switch (Properties.valueOf(property)) {

                        case text:
                            String value = pb.getValue();
                            if (view instanceof TextView) {
                                String valueText = ((TextView) view).getText().toString();
                                if (valueText.equals(pb.getOld_value())) {
                                    ((TextView) view).setText(value);
                                }
                            }
                            break;
                        case ellipsize:
                            // 1\2\3\4
                            String ellipsize = pb.getValue();
                            if (Utils.checkInteger(ellipsize)) {

                                if (view instanceof TextView) {

                                    switch (TextUtils.TruncateAt.valueOf(ellipsize)) {
                                        case START:
                                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.START);
                                            break;
                                        case MIDDLE:
                                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.MIDDLE);
                                            break;
                                        case END:
                                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                                            break;
                                        case MARQUEE:
                                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                            break;
                                    }
                                }
                            }
                            break;
                        case alpha:
                            String alpha = pb.getValue();
                            if (Utils.checkFloat(alpha)) {
                                view.setAlpha(Float.parseFloat(alpha));
                            }
                            break;
                        case maxlines:

                            if (view instanceof TextView) {
                                String maxline = pb.getValue();
                                if (Utils.checkInteger(maxline)) {
                                    ((TextView) view).setMaxLines(Integer.parseInt(maxline));
                                }
                            }
                            break;
                        case text_color:
                            if (view instanceof TextView) {
                                String text_color = pb.getValue();
                                if (Utils.checkColor(text_color)) {
                                    ((TextView) view).setTextColor(Color.parseColor(text_color));
                                }
                            }
                            break;
                        case visibility:
                            String visibility_int = pb.getValue();

                            if (Utils.checkInteger(visibility_int)) {
                                switch (Integer.parseInt(visibility_int)) {
                                    case View.INVISIBLE:
                                        view.setVisibility(View.INVISIBLE);
                                        break;
                                    case View.VISIBLE:
                                        view.setVisibility(View.VISIBLE);
                                        break;
                                    case View.GONE:
                                        view.setVisibility(View.GONE);
                                        break;
                                }
                            }

                            break;
                        case text_size:
                            String text_size = pb.getValue();
                            if (Utils.checkFloat(text_size)) {
                                if (view instanceof TextView) {
                                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(text_size));
                                }
                            }
                        case padding:
                            String padding = pb.getValue();
                            // left ,top,right,bottom
                            String[] paddings = padding.split("-");
                            if (paddings.length == 4
                                    && Utils.checkInteger(paddings[0])
                                    && Utils.checkInteger(paddings[1])
                                    && Utils.checkInteger(paddings[2])
                                    && Utils.checkInteger(paddings[3])) {
                                view.setPadding(Integer.parseInt(paddings[0]),
                                        Integer.parseInt(paddings[1]),
                                        Integer.parseInt(paddings[2]),
                                        Integer.parseInt(paddings[3]));
                            }
                            break;
                        case gravity:
                            if (view instanceof LinearLayout) {
                                String gravity = pb.getValue();
                                if (Utils.checkInteger(gravity)) {
                                    ((LinearLayout) view).setGravity(Integer.parseInt(gravity));
                                }
                            }
                            break;
                        case text_gravity:
                            //            int x = Gravity.BOTTOM;80
                            //            int x1 =Gravity.CENTER;17
                            //            int x2 = Gravity.CENTER_HORIZONTAL;1-->
                            //            int x3 = Gravity.CENTER_VERTICAL;16
                            //            int x10 = Gravity.LEFT;3 __>51
                            //            int x11 = Gravity.RIGHT;5
                            //            int x13 = Gravity.TOP;48

                            if (view instanceof TextView) {
                                String textGrivity = pb.getValue();
                                if (Utils.checkInteger(textGrivity)) {
                                    // left ,top,right,bottom
                                    ((TextView) view).setGravity(Integer.parseInt(textGrivity));
                                }
                            }

                            break;
                        case height:
                            String text_height = pb.getValue();
                            if (Utils.checkInteger(text_height)) {

                                int old_height = view.getHeight();
                                int new_height = Utils.dip2px(context, Integer.parseInt(text_height));
                                if (old_height == new_height) {
                                    T.i("height is same ,do not change view height");
                                    break;
                                }
                                ViewGroup.LayoutParams param = view.getLayoutParams();

                                if (param != null) {

                                    param.height = new_height;

                                } else {
                                    param = new ViewGroup.LayoutParams(view.getWidth(), new_height);
                                }
                                view.setLayoutParams(param);

                            }
                            break;
                        case width:
                            String text_width = pb.getValue();

                            if (Utils.checkInteger(text_width)) {

                                int old_width = view.getWidth();
                                int new_width = Utils.dip2px(context, Integer.parseInt(text_width));
                                T.i("old_width : " + old_width);
                                if (old_width == new_width) {
                                    T.i("width is same ,do not change view width");
                                    break;
                                }
                                ViewGroup.LayoutParams param = view.getLayoutParams();


                                if (param != null) {

                                    param.width = new_width;

                                } else {
                                    param = new ViewGroup.LayoutParams(new_width, view.getHeight());
                                }
                                view.setLayoutParams(param);
                                T.i("change Width " + text_width);
                            }

                            break;
                        case text_style:
//                                    public static final int NORMAL = 0;
//                                    public static final int BOLD = 1;
//                                    public static final int ITALIC = 2;
//                                    public static final int BOLD_ITALIC = 3;
                            if (view instanceof TextView) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((TextView) view).setTypeface(null, Integer.parseInt(text_style));
                                }
                            }
                            break;
                        case background:
                            setViewImage(pb, view, property, cb);
                            break;
                        case image:
                            setViewImage(pb, view, property, cb);
                            break;
                        case numpicker_max:
                            if (view instanceof NumberPicker) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((NumberPicker) view).setMaxValue(Integer.parseInt(text_style));
                                }
                            }
                            break;
                        case numpicker_min:
                            if (view instanceof NumberPicker) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((NumberPicker) view).setMinValue(Integer.parseInt(text_style));
                                }
                            }
                            break;
                        // rating
                        case numberstars:
                            if (view instanceof RatingBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((RatingBar) view).setNumStars(Integer.parseInt(text_style));
                                }
                            }
                            break;
                        case rating:
                            if (view instanceof RatingBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkFloat(text_style)) {
                                    ((RatingBar) view).setRating(Float.parseFloat(text_style));
                                }
                            }
                            break;
                        case stepsize:
                            if (view instanceof RatingBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkFloat(text_style)) {
                                    ((RatingBar) view).setStepSize(Float.parseFloat(text_style));
                                }
                            }
                            break;
                        case progress:
                            if (view instanceof ProgressBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((ProgressBar) view).setProgress(Integer.parseInt(text_style));
                                }
                            }
                            break;
                        case progress_max:
                            if (view instanceof ProgressBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkInteger(text_style)) {
                                    ((ProgressBar) view).setMax(Integer.parseInt(text_style));
                                }
                            }
                            break;
                        case checked:
                            if (view instanceof CheckBox) {
                                String text_style = pb.getValue();

                                if (Utils.checkBoolean(text_style)) {
                                    ((CheckBox) view).setChecked(Boolean.parseBoolean(text_style));
                                }

                            }
                            break;
                        case indeterminate:
                            if (view instanceof ProgressBar) {
                                String text_style = pb.getValue();
                                if (Utils.checkBoolean(text_style)) {
                                    ((ProgressBar) view).setIndeterminate(Boolean.parseBoolean(text_style));
                                }
                            }
                            break;
                        case orientation:
                            String orientation = pb.getValue();
                            if (view instanceof LinearLayout) {
                                if (Utils.checkInteger(orientation)) {
                                    int orientation_int = Integer.parseInt(orientation);
                                    switch (orientation_int) {
                                        case LinearLayout.VERTICAL:
                                            ((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
                                            break;
                                        case LinearLayout.HORIZONTAL:
                                            ((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
                                            break;
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (Throwable e) {
                T.w("render error!");
            }
        }
    }


    @SuppressLint("NewApi")
    private class lifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        private RenderingViewChange changeListener = null;

        @Override
        public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            mCurrentActivity = activity;
        }

        @Override
        public void onActivityResumed(final Activity activity) {
            int code = activity.hashCode();
            //----
            AdhocWindowCallback callback = windowsCallbacks.get(code);

            if (callback == null) {
                Window window = activity.getWindow();
                callback = ReflectView.setAdhocWindowCallback(window, activity);
                windowsCallbacks.put(code, callback);
            }
            //-------------
            changeListener = new RenderingViewChange(activity);
            View view = activity.getWindow().getDecorView();
            view.getViewTreeObserver().addOnGlobalLayoutListener(changeListener);
            T.i("normal rendering !!!");
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            View view = activity.getWindow().getDecorView();

            if (Build.VERSION.SDK_INT < 16) {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(changeListener);
            } else {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(changeListener);
            }
            if (changeListener != null) {
                changeListener.onDestory();
            }
            int code = activity.hashCode();
            AdhocWindowCallback callback = statusActivity.get(code);
            if (callback != null) {
                callback.destory();
                statusActivity.remove(callback);
            }

        }
    }

    /**
     * 普通用户渲染改变
     */
    private void normalRender(final Activity activity) {
        T.i("normal rendring>>>>>>>>>>>");
        GetExperimentFlag.getInstance(activity).getAutoExperiment(activity, new HttpCallBack() {
            @Override
            public void onFailure(Throwable throwable) {
                if (throwable != null) {
                    T.w(throwable.toString());
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                if (response != null) {
                    T.i("render -----" + response.toString());
//                        rendering(response, activity);
                    if (activity == null || activity.isFinishing()) {
                        T.i("activity is null or activity isFinishing stop rendering");
                        return;
                    }
                    RenderHandlerNormal handler = new RenderHandlerNormal(activity, response);
                    handler.sendEmptyMessage(Rendering.RENDER_NORMAL);
                } else {
                    T.i("call back response is null");
                }
            }
        });

    }

    private class RenderingViewChange implements ViewTreeObserver.OnGlobalLayoutListener {
        private Activity activity;

        public RenderingViewChange(Activity activity) {
            this.activity = activity;
        }

        public void onDestory() {
            activity = null;
        }

        @Override
        public void onGlobalLayout() {

            T.i("global view Rendering complete!");

            // 线上获取jsonObject
////            todo test
//            try {
//                String str = getAssets(activity);
//                JSONObject object = new JSONObject(str);
//
//                RenderHandlerNormal handler = new RenderHandlerNormal(activity, object);
//                handler.sendEmptyMessage(Rendering.RENDER_NORMAL);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


//--------------------------------todo online code
            try {
                if (!RealScreen.getInstance(activity).isEditing) {
                    T.i("normal rendering");
                    normalRender(activity);
                }
            } catch (Throwable e) {

                T.w("normal render error!");
            }
//-----------------------------------

        }
    }

    // todo recycle
    static Target target;

    private void setViewImage(PropertyBean pb, final View view, final String property, ChangeBean cb) {

        try {

            final String valueBackground = pb.getValue();
            String typeBack = pb.getType();
            switch (Types.valueOf(typeBack)) {
                case image:
                    T.i("start get image file .... + " + valueBackground);
                    if (property.equals(Properties.background.toString())) {
                        // 替换新的图片之前，在编辑模式下需要保存drawable

                        if (RealScreen.getInstance(context).reset) {

//                        todo 处理异常 Integer.parse 空字符串。
                            view.setBackgroundDrawable(RealScreen.getInstance(mCurrentActivity).
                                    getDrawable(cb.getMapKey() + Properties.background.toString()));

                        } else {
                            if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.background.toString(), view.getBackground());
                            }
                            target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    T.i("set background image w is :" + bitmap.getWidth());
                                    Drawable drawable = null;
                                    if (valueBackground.endsWith(".9.png")) {
                                        drawable = NinePatchChunk.create9PatchDrawable(context, bitmap, "adhoc-ninepatch");
                                    } else {
                                        drawable = new BitmapDrawable(bitmap);
                                    }
                                    view.setBackgroundDrawable(drawable);
                                    view.invalidate();
                                    target = null;
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    T.w("load pic error");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    T.i("preload image");
                                }
                            };
                            Picasso.with(context).load(valueBackground).into(target);
                        }

                    } else if (property.equals(Properties.image.toString())) {

                        if (view instanceof ImageView) {


                            if (RealScreen.getInstance(context).reset) {

                                ((ImageView) view).setImageDrawable(RealScreen.getInstance(mCurrentActivity).
                                        getDrawable(cb.getMapKey() + Properties.image.toString()));

                            } else {
                                // save drawable when editing
                                if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                    RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.image.toString(), ((ImageView) view).getDrawable());
                                }
                                Picasso.with(context).load(valueBackground).into((ImageView) view);
                            }
                        }
                    }
                    break;
                case color:
                    if (property.equals(Properties.background.toString())) {

                        if (RealScreen.getInstance(mCurrentActivity).reset) {

                            view.setBackgroundDrawable(RealScreen.getInstance(mCurrentActivity).
                                    getDrawable(cb.getMapKey() + Properties.background.toString()));

                        } else {
                            // 替换新的图片之前，在编辑模式下需要保存drawable
                            if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.background.toString(), view.getBackground());
                            }
                            if (valueBackground != null) {
                                if (!valueBackground.equals("")) {
                                    view.setBackgroundColor(Color.parseColor(valueBackground));
                                }
                            }
                        }

                    } else if (property.equals(Properties.image.toString())) {
                        if (view instanceof ImageView) {

                            if (RealScreen.getInstance(mCurrentActivity).reset) {

                                ((ImageView) view).setImageDrawable(RealScreen.getInstance(mCurrentActivity).
                                        getDrawable(cb + Properties.image.toString()));

                            } else {
                                // save drawable when editing
                                if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                    RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.image.toString(), ((ImageView) view).getDrawable());
                                }
                                ((ImageView) view).setImageDrawable(new ColorDrawable(Integer.parseInt(valueBackground)));
                            }

                        }
                    }
                    break;
                case none_bg:
                    if (property.equals(Properties.background.toString())) {

                        if (RealScreen.getInstance(mCurrentActivity).reset) {

                            view.setBackgroundDrawable(RealScreen.getInstance(mCurrentActivity).
                                    getDrawable(cb.getMapKey() + Properties.background.toString()));

                        } else {
                            // 替换新的图片之前，在编辑模式下需要保存drawable
                            if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.background.toString(), view.getBackground());
                            }
                            view.setBackgroundDrawable(null);
                        }


                    } else if (property.equals(Properties.image.toString())) {
                        if (view instanceof ImageView) {

                            if (RealScreen.getInstance(mCurrentActivity).reset) {

                                ((ImageView) view).setImageDrawable(RealScreen.getInstance(mCurrentActivity).
                                        getDrawable(cb.getMapKey() + Properties.image.toString()));

                            } else {
                                // save drawable when editing
                                if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                                    RealScreen.getInstance(mCurrentActivity).saveDrawable(cb, Properties.image.toString(), ((ImageView) view).getDrawable());
                                }
                                ((ImageView) view).setImageDrawable(null);
                            }

                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            T.e(e);
        }
    }

    private View getTargetView(String actName, View view, PositionBean[] beans) {
        if (beans == null) {
            return null;
        }
        PositionBean pbActivity = beans[0];
        String activityName = pbActivity.getActivity();
        if (!actName.equals(activityName)) {
            return null;
        }
        if (beans.length <= 2) {
            return null;
        }
        for (int i = 2; i < beans.length; i++) {

            PositionBean pb = beans[i];
            view = getChildView_v1(view, pb);
            current_item = pb.getCuritem();
        }
        return view;
    }

    private View getListView(String name, PositionBean[] beans, View view) {
        if (beans == null) {
            return null;
        }
        PositionBean pbActivity = beans[0];
        String activityName = pbActivity.getActivity();
        if (name.equals(activityName)) {
            return null;
        }
        if (beans.length <= 2) {
            return null;
        }
        int position = 0;
        for (int i = 2; i < beans.length; i++) {

            PositionBean pb = beans[i];
            int row = pb.getRow();
            if (row != -1) {
                position = i;
                break;
            }
        }
        for (int i = 2; i < position; i++) {
            PositionBean pb = beans[i];
            view = getChildView_v1(view, pb);
            current_item = pb.getCuritem();
            T.i("set current item " + current_item);
        }
        return view;
    }

    private View getChildView(View view, PositionBean pb) {
        // position is base on 可视控件
        if (pb != null && view != null) {
            int pos = pb.getPos();
            if (view instanceof ViewGroup) {
                return ((ViewGroup) view).getChildAt(pos);
            }
        }
        return null;
    }

    private int current_item = -1;

    private View getChildView_v1(View view, PositionBean pb) {
        if (view == null) {
            return null;
        }
        try {
            if (view instanceof ViewGroup) {

                // 有限处理cur_item如果有这样的字段的话。
                if (current_item != -1) {
                    ArrayList<String> list = new ArrayList<String>();
                    ReflectView.getViewParents(view.getClass(), list);
                    for (int i = 0; i < list.size(); i++) {
                        String className = list.get(i);
                        if (className.equals("android.support.v4.view.ViewPager")) {

                            T.i("try to find current item and return View");
                            if (ReflectView.getViewPagerCurrentItemPos(view) == current_item) {
                                Object obj = ReflectView.getPagerView(view, current_item);
                                T.i("find pager view");
                                if (obj != null) {
                                    return (View) obj;
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }

                        }
                    }
                }
                // position is base on 可视控件
                if (pb != null && view != null) {
                    int pos = pb.getPos();
                    return ((ViewGroup) view).getChildAt(pos);
                }

            }
        } catch (Throwable e) {
            T.e(e);
        }
        return null;
    }

    private View getChildViewOfVisiable(View view, PositionBean pb) {
        if (view == null) {
            return null;
        }
        try {
            if (view instanceof ViewGroup) {

                // 有限处理cur_item如果有这样的字段的话。
                if (pb.getCuritem() != -1) {
                    ArrayList<String> list = new ArrayList<String>();
                    ReflectView.getViewParents(view.getClass(), list);
                    for (int i = 0; i < list.size(); i++) {
                        String className = list.get(i);
                        if (className.equals("android.support.v4.view.ViewPager")) {

                            T.i("try to find current item and return View");
                            if (ReflectView.getViewPagerCurrentItemPos(view) == pb.getCuritem()) {
                                Object obj = ReflectView.getPagerView(view, pb.getCuritem());
                                T.i("find pager view");
                                return (View) obj;
                            }

                        }
                    }

                }

                int counter = 0;
                int offset = pb.getOffset();
                ViewGroup group = ((ViewGroup) view);
                int count = group.getChildCount();
//                if (offset > count) {
//                    T.w("error when get getChildInvisiCount " + view.getClass().getName());
//                    return null;
//                }
                for (int i = 0; i < count; i++) {
                    View viewChild = group.getChildAt(i);
                    if (viewChild.getVisibility() > 0) {
                        counter++;
                    }
                    // 当前view是可视位置的view
                    if (i - counter == offset) {
                        return ((ViewGroup) view).getChildAt(i);
                    }
                }
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return null;
    }

    private ChangeBean[] getChangeBeans(String className, JSONObject object, boolean isDialog) throws JSONException {
        // todo 如果修改了目录结构 那么找不到这个类了。所有实验将会不能分配
        // 当前activity的全名
//        String className = activity.getClass().getName();

        if (object == null) {
//            Toaster.toast(activity,"Json obj is null");
            return null;
        }
        T.i("rendering json is : " + object.toString());
        // 取到json changes 查看有没有当前页面的修改
        JSONArray array = object.optJSONArray("changes");
        if (array == null) {
            return null;
        }
        ChangeBean[] changeBeans = null;
        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);
            boolean dialog = obj.optBoolean("dialog", false);
            if (isDialog) {
                if (!dialog) {
                    continue;
                }
            }
            JSONArray positionsJSON = obj.optJSONArray("position");
            if (positionsJSON == null) {
                return null;
            }
            if (positionsJSON.length() > 0) {
                JSONObject objActivity = positionsJSON.getJSONObject(0);
                if (objActivity.optString("activity", "").equals(className)) {
                    ChangeBean cb = new ChangeBean();
                    // 解析property
                    JSONArray arrayProperties = obj.getJSONArray("properties");
                    PropertyBean[] pbs = getPropertiesBean(arrayProperties);
                    cb.setProperties(pbs);
                    // 位置在哪里 位置数组标示一个确定的View位置，逐层定位到最后修改的View
                    PositionBean[] ps = getPositionsBeans(cb, positionsJSON);
                    cb.setPositions(ps);

                    // set dialog
                    cb.setIsDialog(dialog);


                    // 编辑模式下需要知道mapKey
                    if (RealScreen.getInstance(mCurrentActivity).isEditing) {
                        cb.setMapKey(positionsJSON.toString());
                    }
                    if (changeBeans == null) {
                        changeBeans = new ChangeBean[array.length()];
                    }
                    changeBeans[i] = cb;
                }
            }
//            todo if has return  changebean
        }
        return changeBeans;
    }

    private PropertyBean[] getPropertiesBean(JSONArray arrayProperties) throws JSONException {
        PropertyBean[] pbs = new PropertyBean[arrayProperties.length()];
        for (int x = 0; x < arrayProperties.length(); x++) {
            JSONObject propertyObj = arrayProperties.getJSONObject(x);
            if (propertyObj != null) {
                PropertyBean pb = new PropertyBean();
                // 保存新属性的值
//                JSONObject old = propertyObj.optJSONObject("old_value");
                pb.setProperty(propertyObj.optString("property", ""));
                pb.setType(propertyObj.optString("type", ""));
                pb.setValue(propertyObj.optString("value", ""));
                pb.setOld_value(propertyObj.optString("old_value", ""));
                pbs[x] = pb;
            }
        }
        return pbs;
    }

    private PositionBean[] getPositionsBeans(ChangeBean cb, JSONArray positions) throws JSONException {

        PositionBean[] ps = new PositionBean[positions.length()];
        JSONObject posObj = null;
        for (int j = 0; j < positions.length(); j++) {

            posObj = positions.getJSONObject(j);

            PositionBean p1 = new PositionBean();
            if (j == 0) {
                p1.setActivity(posObj.optString("activity", ""));
            } else {
                p1.setView(posObj.optString("view", ""));
            }

            p1.setPos(posObj.optInt("index", -1));
            p1.setOffset(posObj.optInt("offset", -1));
            // view 属性
            p1.setCuritem(posObj.optInt("cur_item", -1));
            int row = posObj.optInt("row", -1);
            if (cb != null) {
                if (row != -1) {
                    cb.setIsAbsListView(true);
                }
            }
            p1.setRow(row);
            ps[j] = p1;
        }
        return ps;
    }

    private StatsBean[] getStatBean(String className, JSONObject object) throws JSONException {
        // 当前activity的全名

        if (object == null) {
            return null;
        }

        // 取到优化指标
        JSONArray array = object.optJSONArray("stats");
        if (array == null) {
            return null;
        }
        StatsBean[] statsBeans = new StatsBean[array.length()];
        for (int i = 0; i < array.length(); i++) {

            JSONObject obj = array.getJSONObject(i);
            JSONArray positions = obj.getJSONArray("position");
            if (positions.length() > 0) {
                JSONObject objActivity = positions.getJSONObject(0);
//                判断activity
                if (objActivity.optString("activity", "").equals(className)) {
                    StatsBean statsBean = new StatsBean();
                    String statKey = obj.optString("stat_key", "");
                    boolean dialog = obj.optBoolean("dialog", false);
                    statsBean.setStatsKey(statKey);
                    statsBean.setDialog(dialog);
                    // 生成unique key
                    statsBean.setMapKey(positions.toString());
                    // 位置在哪里
                    PositionBean[] ps = getPositionsBeans(null, positions);
                    statsBean.setPositions(ps);
                    statsBeans[i] = statsBean;
                }
            }
        }
        return statsBeans;
    }

    private int versionCodeApp;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Rendering(Context context) {
        versionCodeApp = ParameterUtils.getAppVersionCode(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            T.w("设备的SDKlevel不支持可视化编辑版本");
            return;
        }
        this.context = context;
        if (context instanceof Activity) {
            T.i(render, " is Activity Context");
            Application application = ((Activity) (context)).getApplication();
            application.registerActivityLifecycleCallbacks(new lifecycleCallbacks());
        } else if (context instanceof Application) {
            T.i(render, " is Application Context");
            Application application1 = (Application) (context);
            application1.registerActivityLifecycleCallbacks(new lifecycleCallbacks());
        }
    }

    public void init() {
        // todo

    }

    public static Rendering getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new Rendering(context);
        }
        return ourInstance;
    }

    private String getAssets(Activity context) throws IOException {

        StringBuilder sb = new StringBuilder();
        InputStream ins = context.getResources().getAssets().open("changes");

        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        String str = "";

        while ((str = reader.readLine()) != null) {
            sb.append(str.trim());
        }

        reader.close();
        T.i(sb.toString());
//        Toaster.toast(context, sb.toString());
        T.i("length is : " + sb.toString().length());
        return sb.toString();

    }

//    final class StatusBean {
//        boolean resumed = false;
//    }

}

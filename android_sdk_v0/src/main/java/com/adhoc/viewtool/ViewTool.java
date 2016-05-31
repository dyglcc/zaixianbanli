package com.adhoc.viewtool;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.adhoc.adhocsdk.ScrollAbsViewListener;
import com.adhoc.property.ViewToolProperty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dongyuangui on 15/8/6.
 */
public class ViewTool {

    private static ViewTool instance = null;

    private ArrayList<Integer> keylist;

    public static ViewTool getInstance() {
        if (instance == null) {
            instance = new ViewTool();
        }
        return instance;
    }

    private ViewTool() {

        if (keylist != null) {
            keylist.clear();
        } else {
            keylist = new ArrayList<Integer>();
        }
    }


    private ArrayList<String> parents = null;

    public JSONObject getViewJson(JSONObject obj, int position, View view, Activity activity) throws JSONException {
        parents = new ArrayList<String>();
        obj.put("position", position);
        ReflectView.getViewParents(view.getClass(), parents);
        JSONObject objectProperties = new JSONObject();
        // 每一个view的json是按照继承属性加入
        for (int i = parents.size() - 1; i >= 0; i--) {
            String name = parents.get(i);
            if (name.equals("android.view.View")) {
                ViewToolProperty.getInstance().getJsonView(view, objectProperties);
            } else if (name.equals("android.widget.Button")) {
//                AdhocView.getInstance().getJsonButton((Button) (view), objectProperties);
            } else if (name.equals("android.widget.TextView")) {
                ViewToolProperty.getInstance().getJsonTextView((TextView) (view), objectProperties);
                // view Group
            } else if (name.equals("android.widget.AbsListView")) {
                // 编辑模式 对
                ViewToolProperty.getInstance().getAbsListView(view, new ScrollAbsViewListener(activity), keylist);

            } else if (name.equals("android.widget.RelativeLayout")) {
//                AdhocView.getInstance().getJsonViewGroup(view, objectProperties);

            } else if (name.equals("android.widget.EditText")) {
                ViewToolProperty.getInstance().getEditJson(view, objectProperties);
            } else if (name.equals("android.widget.FrameLayout")) {
//                AdhocView.getInstance().getJsonViewGroup(view, objectProperties);

            } else if (name.equals("android.widget.LinearLayout")) {
                ViewToolProperty.getInstance().getJsonLinearLayout(view, objectProperties);
                // imageView
            } else if (name.equals("android.widget.ImageView")) {
                ViewToolProperty.getInstance().getJsonImageView(view, objectProperties);

            } else if (name.equals("android.widget.ImageButton")) {
//                AdhocView.getInstance().getJsonImageView(view, objectProperties);
            } else if (name.equals("android.support.v4.view.ViewPager")) {
                ViewToolProperty.getInstance().getViewPagerJson(view, obj);
            } else if (name.equals("android.widget.ProgressBar")) {
                ViewToolProperty.getInstance().getJsonProgressBar(view, objectProperties);
            } else if (name.equals("android.widget.RatingBar")) {
                ViewToolProperty.getInstance().getJsonRatingBar(view, objectProperties);
            } else if (name.equals("android.widget.ZoomButton")) {
                ViewToolProperty.getInstance().getZoomButton(view, objectProperties);
            } else if (name.equals("android.widget.SeekBar")) {
                ViewToolProperty.getInstance().getSeekBar(view, objectProperties);
            } else if (name.equals("android.widget.CompoundButton")) {
                ViewToolProperty.getInstance().getCompundButton(view, objectProperties);
            } else if (name.equals("android.widget.CheckBox")) {
                ViewToolProperty.getInstance().getCheckBox(view, objectProperties);
            } else if (name.equals("android.widget.Switch")) {
                ViewToolProperty.getInstance().getSwitch(view, objectProperties);
            } else if (name.equals("android.widget.RadioGroup")) {
                ViewToolProperty.getInstance().getRadioGroup(view, objectProperties);
            } else if (name.equals("android.widget.RadioButton")) {
                ViewToolProperty.getInstance().getRadioButton(view, objectProperties);
            } else if (name.equals("android.widget.ToggleButton")) {
                ViewToolProperty.getInstance().getToggleButton(view, objectProperties);
            } else if (name.equals("android.widget.ScrollView")) {
                ViewToolProperty.getInstance().getScrollview(view, objectProperties);
            } else if (name.equals("android.widget.TabHost")) {
                ViewToolProperty.getInstance().getTabhost(view, objectProperties);
            } else if (name.equals("android.widget.VideoView")) {
                ViewToolProperty.getInstance().getVideoView(view, objectProperties);
            } else if (name.equals("android.widget.StackView")) {
                ViewToolProperty.getInstance().getStackView(view, objectProperties);
            } else if (name.equals("android.widget.AdapterViewFlipper")) {
                ViewToolProperty.getInstance().getAdapterViewFliper(view, objectProperties);
            } else if (name.equals("android.widget.AnalogClock")) {
                ViewToolProperty.getInstance().getAnalogclock(view, objectProperties);
            } else if (name.equals("android.widget.Chronometer")) {
                ViewToolProperty.getInstance().getChonometer(view, objectProperties);
            } else if (name.equals("android.widget.Spinner")) {
                ViewToolProperty.getInstance().getSpinnerJson(view, objectProperties);
            } else if (name.equals("android.widget.CheckedTextView")) {
                ViewToolProperty.getInstance().getCheckedTextView(view, objectProperties);
            } else if (name.equals("android.widget.NumberPicker")) {
                ViewToolProperty.getInstance().getNumberPicker(view, objectProperties);
            } else if (name.equals("android.widget.QuickContactBadge")) {
                ViewToolProperty.getInstance().getQuickContractBadge(view, objectProperties);
            } else if (name.equals("android.widget.DialerFilter")) {
                ViewToolProperty.getInstance().getDialerFilter(view, objectProperties);
            } else if (name.equals("android.view.SurfaceView")) {
                ViewToolProperty.getInstance().getSurfaceView(view, objectProperties);
            }
        }
        obj.put("properties", objectProperties);
        obj.put("class_name", view.getClass().getName());
        obj.put("children", getChildren(view, activity));
        // 检查并添加row
        if (view.getParent() instanceof AbsListView) {
            int firstVisiblePosition = ((AbsListView) (view.getParent())).getFirstVisiblePosition();
            obj.put("row", position + firstVisiblePosition);
        }
        // 得到兄弟隐藏view的count
        obj.put("invicount", getInvisiableCountViews(position, view));

        return obj;
    }

    // 获取view哥哥的所有隐藏view
    private int getInvisiableCountViews(int position, View view) {
        int sum = 0;

        if (view.getParent() instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view.getParent();
//            int count = group.getChildCount();
            for (int i = 0; i < position; i++) {

                View viewChild = group.getChildAt(i);

                // 判断兄弟view是否是invisiable
                if (viewChild != null && viewChild.getVisibility() > 0) {
                    sum++;
                }
            }
        }

        return sum;

    }


    private JSONArray getChildren(View view, Activity activity) throws JSONException {
        JSONArray array = new JSONArray();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View viewChild = group.getChildAt(i);
                // 不上传不显示的view
                if (viewChild.getVisibility() == View.VISIBLE) {
                    JSONObject objectChild = getViewJson(new JSONObject(), i, viewChild, activity);
                    array.put(objectChild);
                }

            }

        }
        return array;
    }

}

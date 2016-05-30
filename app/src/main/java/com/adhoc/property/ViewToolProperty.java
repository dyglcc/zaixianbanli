package com.adhoc.property;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adhoc.adhocsdk.ScrollAbslistview;
import com.adhoc.utils.T;
import com.adhoc.viewtool.ReflectView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dongyuangui on 15/8/6.
 */
public class ViewToolProperty {

    /**
     * 生成json 在编辑的时候。
     **/
    // alpha
    // background
    // visibility
    // top padding
    // bottom padding
    // left padding
    // right padding

    // height
    // width
    // x
    // y
    private static ViewToolProperty instance = null;

    private ViewToolProperty() {

    }

    public static ViewToolProperty getInstance() {

        if (instance == null) {
            instance = new ViewToolProperty();
        }
        return instance;
    }

    @SuppressLint("NewApi")
    public void getJsonView(View view, JSONObject jsonObject) {
//        Drawable drawable = view.getBackground();

        int[] location = getLocations(view);
        float alpha = view.getAlpha();
        int visibility = view.getVisibility();

        try {
            jsonObject.put("alpha", alpha);
            // 修改background传递drable的id
//            if (drawable != null) {
////                if (drawable instanceof BitmapDrawable) {
////                    jsonObject.put("bg_bitmap", true);
////                } else if (drawable instanceof ColorDrawable) {
////                    jsonObject.put("bg_color", ((ColorDrawable) (drawable)).getColor());
////                } else if (drawable instanceof StateListDrawable) {
////                    // 当成有pic
////                    jsonObject.put("bg_bitmap", true);
////                }
//                jsonObject.put("bg_bitmap", drawable.hashCode());
//            } else {
            jsonObject.put("bg_bitmap", "");
//                jsonObject.put("bg_color", "");
//            }
            jsonObject.put("visibility", visibility);
            jsonObject.put("paddingTop", view.getPaddingTop());
            jsonObject.put("paddingBottom", view.getPaddingBottom());
            jsonObject.put("paddingLeft", view.getPaddingLeft());
            jsonObject.put("paddingRight", view.getPaddingRight());

            //
            jsonObject.put("x", location[0]);
            jsonObject.put("y", location[1]);
            jsonObject.put("width", view.getWidth());
            jsonObject.put("height", view.getHeight());

        } catch (JSONException e) {
            T.e(e);
        }
//        T.i(jsonObject.toString());
    }

    @SuppressLint("NewApi")
    public void getJsonButton(Button view, JSONObject objectProperties) {
    }

    @SuppressLint("NewApi")
    public void getJsonTextView(TextView view, JSONObject jsonObject) {
        try {
            // content
            jsonObject.put("text", view.getText().toString());
            // style
            jsonObject.put("textColor", view.getCurrentTextColor());
            jsonObject.put("textSize", view.getTextSize());
//             NORMAL = 0;
//             BOLD = 1;
//             ITALIC = 2;
//             BOLD_ITALIC = 3;
            int textStyle = Typeface.NORMAL;
            Typeface typeface = view.getTypeface();
            if (typeface != null) {
                textStyle = typeface.getStyle();
                jsonObject.put("textStyle", textStyle);
            } else {
                jsonObject.put("textStyle", textStyle);
            }
//            START,
//            MIDDLE,
//            END,
//            MARQUEE,
            TextUtils.TruncateAt trun = view.getEllipsize();
            if (trun != null) {
                jsonObject.put("ellipsize", trun.toString());
            } else {
                jsonObject.put("ellipsize", "none");
            }
            int maxline = view.getLineCount();
            jsonObject.put("maxlines", maxline);

            int gravity = view.getGravity();
            jsonObject.put("gravity", gravity);

        } catch (JSONException e) {
            T.e(e);
        }
    }

    @SuppressLint("NewApi")
    public void getJsonImageView(View view1, JSONObject jsonObject) {
        ImageView view = (ImageView) (view1);
//        Drawable drawable = view.getBackground();
//        Drawable drawsrc = view.getDrawable();
        try {
//            if (drawsrc != null) {

            jsonObject.put("src_bitmap", "");
//                if (drawsrc instanceof BitmapDrawable) {
//                } else if (drawsrc instanceof ColorDrawable) {
//                    jsonObject.put("src_color", ((ColorDrawable) (drawable)).getColor());
//                } else if (drawsrc instanceof StateListDrawable) {
//            } else {
//                jsonObject.put("src_bitmap", "");
//            }
//            drawsrc = null;
//
        } catch (JSONException e) {
            T.e(e);
        }
//        T.i(jsonObject.toString());
    }

    private int[] getLocations(View view1) {
        int[] location = new int[2];
        view1.getLocationOnScreen(location);
        return location;
    }

    public void getJsonLinearLayout(View view, JSONObject objectProperties) {

        // linearlayout 没有办法getGrivity
        LinearLayout layout = (LinearLayout) (view);
        // LinearLayout.VERTICAL linearlayout.hor
        int orientation = layout.getOrientation();
        try {
            objectProperties.put("orientation", orientation);
        } catch (JSONException e) {
            T.e(e);
        }
    }

    public void getEditJson(View view, JSONObject objectProperties) {

        EditText editText = (EditText) (view);
//         android.widget.EditText
        String hint = editText.getHint() == null ? "" : editText.getHint().toString();
        try {
            objectProperties.put("hint", hint);
        } catch (JSONException e) {
            T.e(e);
        }
    }

    public void getListViewJson(View view, JSONObject objectProperties) {

//        extends view;

    }

    public void getViewPagerJson(View view, JSONObject object) {

//        extends view;
        int curItem;
        try {
            curItem = ReflectView.getViewPagerCurrentItemPos(view);
            object.put("cur_item", curItem);
            T.i("viewPager current position : " + curItem);
        } catch (IllegalAccessException e) {
            T.e(e);
        } catch (Throwable e) {
            T.e(e);
        }


    }

    public void getCheckedTextView(View view, JSONObject objectProperties) {

//        extends TextView;

    }

    public void getSpinnerJson(View view, JSONObject objectProperties) {

//        extends View;
        objectProperties.remove("bg_bitmap");
        objectProperties.remove("bg_color");

    }

    public void getJsonProgressBar(View view, JSONObject objectProperties) {

        // linearlayout 没有办法getGrivity
        ProgressBar pb = (ProgressBar) (view);

        boolean indeterminate = pb.isIndeterminate();

        int progress_max = pb.getMax();
        int progress = pb.getProgress();
        try {
            objectProperties.put("indeterminate", indeterminate);
            objectProperties.put("progress_max", progress_max);
            objectProperties.put("progress", progress);
        } catch (JSONException e) {
            T.e(e);
        }
    }

    public void getJsonRatingBar(View view, JSONObject objectProperties) {

        // linearlayout 没有办法getGrivity
        RatingBar ratingBar = (RatingBar) (view);

        int numberStar = ratingBar.getNumStars();
        float rating = ratingBar.getRating();
        float stepsize = ratingBar.getStepSize();

        try {
            objectProperties.put("numberstars", numberStar);
            objectProperties.put("rating", rating);
            objectProperties.put("stepsize", stepsize);
        } catch (JSONException e) {
            T.e(e);
        }
    }

    public void getZoomButton(View view, JSONObject objectProperties) {

        // extends ImageView
    }

    public void getRadioGroup(View view, JSONObject objectProperties) {
//        android.widget.LinearLayout
    }

    public void getCompundButton(View view, JSONObject objectProperties) {
        CompoundButton compoundButton = (CompoundButton) (view);

//        android.widget.CompoundButton
        boolean checked = compoundButton.isChecked();

        try {
            objectProperties.put("checked", checked);
        } catch (JSONException e) {
            T.e(e);
        }

    }

    //    todo 新api的支持
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void getNumberPicker(View view, JSONObject objectProperties) {
        NumberPicker numberPicker = (NumberPicker) (view);

//        android.widget.NumberPicker
        int max_value = numberPicker.getMaxValue();
        int min_value = numberPicker.getMinValue();

        try {
            objectProperties.put("numpicker_max", max_value);
            objectProperties.put("numpicker_min", min_value);
        } catch (JSONException e) {
            T.e(e);
        }

    }

    public void getQuickContractBadge(View view, JSONObject objectProperties) {
//        QuickContactBadge quickContactBadge = (QuickContactBadge) (view);

//        android.widget.QuickContactBadge
//        extends imageView;

    }

    public void getStackView(View view, JSONObject objectProperties) {
//        StackView quickContactBadge = (StackView) (view);

//        android.widget.StackView
//        extends View;
    }

    public void getAdapterViewFliper(View view, JSONObject objectProperties) {

//        android.widget.AdapterViewFlipper
//        extends View;
    }

    public void getAnalogclock(View view, JSONObject objectProperties) {

//        android.widget.AnalogClock
//        extends View;
    }

    public void getChonometer(View view, JSONObject objectProperties) {

//        android.widget.Chronometer
//        extends TextView;
    }

    public void getRadioButton(View view, JSONObject objectProperties) {
//        extends CompundButton
    }

    public void getSwitch(View view, JSONObject objectProperties) {
//        extends CompundButton
    }

    public void getToggleButton(View view, JSONObject objectProperties) {
//        extends CompundButton
    }

    public void getCheckBox(View view, JSONObject objectProperties) {
//        extends CompundButton
    }

    public void getSeekBar(View view, JSONObject objectProperties) {

        // extends Progressbar
    }

    public void getScrollview(View view, JSONObject objectProperties) {
//        android.widget.ScrollView

    }

    public void getDialerFilter(View view, JSONObject objectProperties) {
//        android.widget.DialerFilter

    }

    public void getTabhost(View view, JSONObject objectProperties) {
//        android.widget.TabHost
//        extends FrameLayout

    }

    public void getVideoView(View view, JSONObject objectProperties) {
//        android.widget.

//        android.widget.VideoView
    }

    public void getSurfaceView(View view, JSONObject objectProperties) {
//        android.widget.SurfaceView
    }

    /**
     * this method will not response any property but set OnscrollListener
     * when on scroll send pic to webserver
     **/
    // todo 会不会多次设置listener
    public void getAbsListView(View view, ScrollAbslistview listener, ArrayList<Integer> list) {

        // need test view'id  when not set id at last id is what
        if (view.getId() == -1) {
            T.w("id : " + view.getId());

            // todo debug this function
            return;
        }
        int id = getId(list, view.getId());
        if (id == -1) {
            // set listView onScrollListener sendpic
            ReflectView.invokeListViewOnscrollListener((AbsListView) view, listener);
            T.i("set onScroll listener :" + view.getClass().getName());
            list.add(view.getId());
        }
    }

    private int getId(ArrayList<Integer> list, int srcid) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == srcid) {
                return srcid;
            }
        }
        return -1;
    }
}

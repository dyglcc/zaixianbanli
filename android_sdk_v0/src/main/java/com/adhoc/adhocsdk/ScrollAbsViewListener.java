package com.adhoc.adhocsdk;

import android.app.Activity;
import android.widget.AbsListView;

/**
 * Created by dongyuangui on 15/11/26.
 */
public  class ScrollAbsViewListener extends ScrollAbslistview implements AbsListView.OnScrollListener {


    public ScrollAbsViewListener(Activity activity) {
        super(activity);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (oldListener != null) {
            oldListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            // 当有listView滑动的时候
            RealScreen.getInstance(activity).sendPicRealTime(activity,ScreenShot.GET_VIEW_TREE);

        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (oldListener != null) {
            oldListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }
}
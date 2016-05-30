package com.adhoc.adhocsdk;

import android.app.Activity;
import android.widget.AbsListView;

/**
 * Created by dongyuangui on 15/11/26.
 */
public class ScrollAbslistview {

    protected Activity activity;
    // old listener
    protected AbsListView.OnScrollListener oldListener;

    public void setOldListener(AbsListView.OnScrollListener oldListener) {
        this.oldListener = oldListener;
    }

    public ScrollAbslistview(Activity activity) {
        this.activity = activity;
    }
}

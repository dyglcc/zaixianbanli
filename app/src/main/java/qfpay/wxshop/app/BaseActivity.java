package qfpay.wxshop.app;

import org.androidannotations.api.BackgroundExecutor;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import qfpay.wxshop.utils.ConstValue;

import com.adhoc.utils.T;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
//        AdhocTracker.onPause(this);
        super.onPause();
        T.i("wwd","activity Pause:" + getClass().getName());
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
//        AdhocTracker.onResume(this);
        super.onResume();
        T.i("wwd", "activity Resume:" + getClass().getName());
    }

    @Override
    protected void onDestroy() {
        BackgroundExecutor.cancelAll(ConstValue.THREAD_CANCELABLE, true);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}

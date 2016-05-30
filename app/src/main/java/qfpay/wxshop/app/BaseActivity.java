package qfpay.wxshop.app;

import org.androidannotations.api.BackgroundExecutor;

import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

//import cn.sharesdk.framework.ShareSDK;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;

import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.adhoc.adhocsdk.AdhocTracker;
//import com.adhoc.adhocsdk.AdhocTracker;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WxShopApplication.app.addActivity(this);
//        ShareSDK.initSDK(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if(ev.getAction() == MotionEvent.ACTION_DOWN){//触摸屏幕关闭软键盘
//            if(this.getCurrentFocus()!=null){
//                if(this.getCurrentFocus() instanceof EditText){
//                    System.out.println("如果是编辑框，不关闭软键盘------------");
//                }else{
//                    System.out.println("关闭软键盘------------");
//                    CloseSoftInput(getCurrentFocus());
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    protected void CloseSoftInput(View view) { // 关闭软键盘
//        if (view != null) {
//            if (view.getWindowToken() != null) {
//                InputMethodManager imm;
//                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }
//    }
}

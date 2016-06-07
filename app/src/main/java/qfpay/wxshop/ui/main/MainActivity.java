package qfpay.wxshop.ui.main;

import android.os.Handler;
import android.view.Menu;
import android.widget.Toast;

import com.adhoc.utils.T;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import banli.jinniu.com.R;
import qfpay.wxshop.app.BaseActivity;

/**
 * 主界面
 */
@EActivity(R.layout.main_myshop)
public class MainActivity extends BaseActivity {
    private PushAgent mPushAgent = null;
    private Handler handler;

    @AfterViews
    void initData() {
        mPushAgent = PushAgent.getInstance(this);

        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
        // 通知声音由服务端控制

        //应用程序启动统计
        //参考集成文档的1.5.1.2
        //http://dev.umeng.com/push/android/integration#1_5_1
        mPushAgent.onAppStart();

        //开启推送并设置注册的回调处理
        mPushAgent.enable(mRegisterCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

        @Override
        public void onRegistered(String registrationId) {
            // TODO Auto-generated method stub
            handler.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainActivity.this, "sdkfsdk", Toast.LENGTH_LONG).show();
                    T.i("sdfsdddddddddddddddddd");
                }
            });
        }
    };
}
package qfpay.wxshop.ui.main;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jinniu.animations.FadeInAnimation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import jiafen.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.utils.Utils;

/**
 * 欢迎页
 */

@WindowFeature({ Window.FEATURE_NO_TITLE , Window.FEATURE_INDETERMINATE_PROGRESS})
@EActivity(R.layout.welcome_layout)
public class WelcomeActivity extends BaseActivity {

    @ViewById
    TextView tv_version;
    @ViewById
    TextView tv_online;

    @AfterViews
    void init() {
        tv_online.setVisibility(View.INVISIBLE);
        initApp();
        initImg();
        delayDisappear(2500);
    }

    void initApp() {

        tv_version.setText("" + Utils.getAppVersionString(WelcomeActivity.this));
    }


    void afterSplash() {
        if (!this.isFinishing()) {
            MainActivity_.intent(WelcomeActivity.this).start();
            finish();
            return;
        }

        // 检查服务器地址更改
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("mUrl");
            int mServerType = intent.getIntExtra("mServerType", 0);
            if (url != null) {
                WDConfig.getInstance().init(url, mServerType);
                WxShopApplication.app.mTesterUrl = url;
                WxShopApplication.app.mServerType = mServerType;
            } else {
                if (WxShopApplication.app.mTesterUrl != null) {
                    WDConfig.getInstance().init(
                            WxShopApplication.app.mTesterUrl,
                            WxShopApplication.app.mServerType);
                }
            }
        }

    }

    void initImg() {

    }


    void delayDisappear(long time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                afterSplash();
            }
        }, time);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new FadeInAnimation(tv_online).setDuration(1000).animate();
            }
        },1000);
    }
}

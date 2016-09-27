package qfpay.wxshop.ui.main;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.benben.mall.R;
import com.jinniu.animations.FadeInAnimation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.ConstValue;
import qfpay.wxshop.utils.Utils;

/**
 * 欢迎页
 */

@WindowFeature({Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS})
@EActivity(R.layout.welcome_layout)
public class WelcomeActivity extends BaseActivity {

    @ViewById
    TextView tv_version;
    @ViewById
    ImageView tv_online;

    @AfterViews
    void init() {
        tv_online.setVisibility(View.INVISIBLE);
        initApp();
        initImg();
        delayDisappear(2500);
    }

    void initApp() {

        tv_version.setText("v" + Utils.getAppVersionString(WelcomeActivity.this));
    }


    void afterSplash() {
        if (!this.isFinishing()) {
            boolean isnew = Utils.getValue(getSharedPreferences(ConstValue.PREFS_NAME, 0), ConstValue.NEWINTRO);
            if (isnew) {
                MainActivity_.intent(WelcomeActivity.this).start();
            } else {
                startActivity(new Intent(WelcomeActivity.this, NewIntroductionActivity.class));
            }
            finish();
            return;
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
        }, 1000);
    }
}

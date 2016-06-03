package qfpay.wxshop.ui.main;

import android.view.Menu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import banli.jinniu.com.R;
import qfpay.wxshop.app.BaseActivity;

/**
 * 主界面
 */
@EActivity(R.layout.main_myshop)
public class MainActivity extends BaseActivity {
    @AfterViews
    void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
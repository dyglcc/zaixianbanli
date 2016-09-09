package qfpay.wxshop.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;

import jiafen.jinniu.com.R;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.tab.FragmentPage2;
import qfpay.wxshop.tab.FragmentPage3;
import qfpay.wxshop.tab.HistorySegment;
import qfpay.wxshop.tab.MineSegment;
import qfpay.wxshop.tab.NumberSegment;
import qfpay.wxshop.utils.Utils;

//import qfpay.wxshop.ui.view.ProgressDialog;

/**
 * 主界面
 */
@EActivity(R.layout.main_myshop)
public class MainActivity extends BaseActivity implements Handler.Callback {
    private Handler handler;
    private FragmentTabHost mTabHost;

    private LayoutInflater layoutInflater;

    private Class fragmentArray[] = {NumberSegment.class, FragmentPage2.class, FragmentPage3.class, HistorySegment.class, MineSegment.class};

    private int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_selfinfo_btn,
            R.drawable.tab_square_btn, R.drawable.tab_more_btn};

    private String mTextviewArray[] = {"page1", "page2", "page3", "page4", "page5"};

    @AfterViews
    void initData() {

        initView();

//        mPushAgent = PushAgent.getInstance(this);
//
//        //sdk开启通知声音
//        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
//        // sdk关闭通知声音
//        // 通知声音由服务端控制
//
//        //应用程序启动统计
//        //参考集成文档的1.5.1.2
//        //http://dev.umeng.com/push/android/integration#1_5_1
//        mPushAgent.onAppStart();
//
//        //开启推送并设置注册的回调处理
//        mPushAgent.enable(mRegisterCallback);

        // testcode

//        Request request = new Request.Builder().url()
//
//        AdhocNet.getInstance().enqueue();
        handler = new Handler(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    ProgressDialog dialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
//            Utils.deleteAllContract(getApplicationContext());
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setIndeterminate(true);
            dialog.setMessage("正在处理...请稍等");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList list = Utils.TestContact(MainActivity.this, "加粉");
                        Utils.testDelete(MainActivity.this, list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(1);
                }
            }).start();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

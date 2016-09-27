package qfpay.wxshop.ui.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.benben.mall.R;
import com.indicator.CirclePageIndicator;
import com.indicator.PageIndicator;

import qfpay.wxshop.utils.ConstValue;
import qfpay.wxshop.utils.Utils;

/**
 * 引导滑屏页面
 */
public class NewIntroductionActivity extends AppCompatActivity {


    public static NewIntroductionActivity INSTANCE = null;
    private PageIndicator mIndicator;

    private LayoutInflater inflater;

    //	private View page1, page2, page3, page5;
    private View page1, page2, page3, page4, page5;
    private ViewPager pager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_new_introduction_activity);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.hide();
        INSTANCE = this;
        init();

        SharedPreferences sharedPreferences = getSharedPreferences(ConstValue.PREFS_NAME, 0);
        Utils.saveBooleanShareData(sharedPreferences, ConstValue.NEWINTRO, true);

        initUi();

//        addShortcut();

    }

    /**
     * 为程序创建桌面快捷方式
     */
    private void addShortcut() {
        // 先删除之前的快捷方式
        delOldShortcut();
        delShortcutMainActivity();
        Intent shortcut = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        // 指定当前的Activity为快捷方式启动的对象
        // 注意: ComponentName的第二个参数必须是带着包名的类
        ComponentName comp = new ComponentName(this.getPackageName(),
                this.getPackageName() + ".ui.main.WelcomeActivity_");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
                Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(comp));

        // 快捷方式的图标
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(
                this, R.drawable.ic_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        sendBroadcast(shortcut);
    }

    /**
     * 删除程序的快捷方式
     */
    private void delShortcutMainActivity() {
        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));

        // 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
        // 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
        String appClass = this.getPackageName() + "."
                + ".ui.main.WelcomeActivity_";
        ComponentName comp = new ComponentName(this.getPackageName(), appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
                Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(comp));

        sendBroadcast(shortcut);

    }

    /**
     * 删除程序的快捷方式
     */
    private void delOldShortcut() {
        Intent shortcut = new Intent(
                "com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));

        // 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
        // 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
        String appClass = this.getPackageName() + "."
                + ".ui.main.WelcomeActivity_";
        ComponentName comp = new ComponentName(this.getPackageName(), appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(
                Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(comp));

        sendBroadcast(shortcut);

    }

    private void initUi() {

        // 进入商城
        page5.findViewById(R.id.parent).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        finish();
                        Intent intent = new Intent(
                                NewIntroductionActivity.this,
                                MainActivity_.class);
                        startActivity(intent);
                    }
                });
    }

    private void init() {

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyViewPagerAdapter());

        inflater = LayoutInflater.from(this);
        page1 = inflater.inflate(R.layout.layout_page1, null);
        page2 = inflater.inflate(R.layout.layout_page2, null);
//        page3 = inflater.inflate(R.layout.layout_page3, null);
//        page4 = inflater.inflate(R.layout.layout_page4, null);
        page5 = inflater.inflate(R.layout.main_login_preview, null);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View new_view = null;

            if (position == 0) {
                new_view = page1;
            } else if (position == 1) {
                new_view = page2;
            }
//            else if (position == 2) {
//                new_view = page3;
//            } else if (position == 3) {
//                new_view = page4;
//            }
            else if (position == 2) {
                new_view = page5;
            }

            container.addView(new_view);

            return new_view;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIndicator != null) {
            mIndicator = null;
        }
        if (page1 != null) {
            page1 = null;
        }
        if (page2 != null) {
            page2 = null;
        }
        if (page3 != null) {
            page3 = null;
        }
        if (page4 != null) {
            page4 = null;
        }
        if (page5 != null) {
            page5 = null;
        }
    }
}

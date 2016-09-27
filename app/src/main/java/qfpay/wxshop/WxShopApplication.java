package qfpay.wxshop;

import android.app.Application;
import android.content.Context;
import org.androidannotations.annotations.EApplication;

import java.util.LinkedList;

import qfpay.wxshop.ui.main.MainActivity;

@EApplication
public class WxShopApplication extends Application {
	public static  boolean IS_NEED_REFRESH_ONE_KEY_BEFALLF = false;

	public String mTesterUrl;
	public int mServerType;
    public MainActivity main;

	@Override
	public void onCreate() {
		super.onCreate();

	}

    public static WxShopApplication get(Context context) {
        return (WxShopApplication) context.getApplicationContext();
    }

	public static int crop_w = 300;
	public static int crop_h = 300;
	public static int aspectX = 1;
	public static int aspectY = 1;


	public static WxShopApplication app = null;
	public String DOMAIN_MMWD_URL = null;
	public static boolean MAIN_IS_RUNNING;

	public WxShopApplication() {
		app = this;
	}

}

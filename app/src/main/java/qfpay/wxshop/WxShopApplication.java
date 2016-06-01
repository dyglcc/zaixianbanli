package qfpay.wxshop;

import android.app.Application;
import android.content.Context;

import com.adhoc.adhocsdk.AdhocTracker;
import com.adhoc.utils.T;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.api.BackgroundExecutor;

import java.util.LinkedList;
import java.util.concurrent.Executors;

import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.ui.main.MainActivity;
import qfpay.wxshop.ui.selectpic.ImageItem;

@EApplication
public class WxShopApplication extends Application {
	public static IWXAPI api;
	public static  boolean IS_NEED_REFRESH_ONE_KEY_BEFALLF = false;


	public static ShareBean shareBean;


	public static LinkedList<ImageItem> paths = new LinkedList<ImageItem>();
	// testTester
	public String mTesterUrl;
	public int mServerType;
    public MainActivity main;

	@Override
	public void onCreate() {
		super.onCreate();
		AdhocTracker.init(this, "ADHOC_e4dff3eb-ffb3-42f6-988e-e13f4de61085");

		boolean b = AdhocTracker.getExperimentFlags(this.getApplicationContext()).getBooleanFlag("mzd_enable_https", false);
		if (b) {
			T.i("hello0000000000000000000000000000000000000000000000000");
		}

		BackgroundExecutor.setExecutor(Executors.newScheduledThreadPool(8));
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

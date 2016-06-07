package qfpay.wxshop;

import android.app.Application;
import android.content.Context;

import com.adhoc.adhocsdk.AdhocTracker;
import com.adhoc.utils.T;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.PlatformConfig;

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

		PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
		//微信 appid appsecret
//		PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");
//		//新浪微博 appkey appsecret
		PlatformConfig.setQQZone("100515642", "b4b049680837497595df378cdea7b0fb");
		// QQ和Qzone appid appkey
//		PlatformConfig.setAlipay("2015111700822536");
//		//支付宝 appid
//		PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//		//易信 appkey
//		PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//		//Twitter appid appkey
//		PlatformConfig.setPinterest("1439206");
//		//Pinterest appid
//		PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//		//来往 appid appkey
		AdhocTracker.init(this, "ADHOC_e4dff3eb-ffb3-42f6-988e-e13f4de61085");

		// bugly
		CrashReport.initCrashReport(getApplicationContext(), "900033658", false);
//		CrashReport.testJavaCrash();
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

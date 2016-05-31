package qfpay.wxshop;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;

import java.util.ArrayList;
import java.util.LinkedList;

import com.adhoc.adhocsdk.AdhocTracker;
import com.adhoc.utils.Toaster;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

import dagger.ObjectGraph;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.app.dependencies.RootModule;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.BusinessCommunityMyNotificationBean;
import qfpay.wxshop.data.beans.PromoStatus;
import qfpay.wxshop.data.beans.ShareBean;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.net.DataEngine;
import qfpay.wxshop.data.netImpl.BusinessCommunityService;
import qfpay.wxshop.listener.MaijiaxiuUploadListener;
import qfpay.wxshop.ui.main.*;
import qfpay.wxshop.ui.main.MoreActivity;
import qfpay.wxshop.ui.selectpic.ImageItem;
import qfpay.wxshop.utils.Utils;
import android.os.Handler;

@EApplication
public class WxShopApplication extends Application {
	public static IWXAPI api;
	public static  boolean IS_NEED_REFRESH_MINE_HUOYUAN = false;
	public static  boolean IS_NEED_REFRESH_ONE_KEY_BEFALLF = false;


	public byte[] aeskey;
	public String cookie;
	public static String DEFAULT_KEY = "ffffffffffffffffffffffffffffffff";
	public static ShareBean shareBean;

	private ArrayList<Activity> actList;
	public MaijiaxiuUploadListener maijiaxiuListener;

	public static LinkedList<ImageItem> paths = new LinkedList<ImageItem>();
	public static PromoStatus psb = new PromoStatus();
	// testTester
	public String mTesterUrl;
	public int mServerType;
	// 碎碎念活动参数
	public String SSN_ACTIVITY_TEXT = "";
	public String SSN_ACTIVITY_URL="http://www.qianmiaomiao.com/explore2/";
    public MainActivity main;

	@Override
	public void onCreate() {
		super.onCreate();
		AdhocTracker.init(this, "ADHOC_e4dff3eb-ffb3-42f6-988e-e13f4de61085");
		AdhocTracker.setOnlyWifiReport(true);

		boolean b = AdhocTracker.getExperimentFlags(this.getApplicationContext()).getBooleanFlag("mzd_enable_https", false);
		if (b) {
			T.i("hello0000000000000000000000000000000000000000000000000");
		}
        objectGraph = ObjectGraph.create(new RootModule(this));
        objectGraph.inject(this);

		BackgroundExecutor.setExecutor(Executors.newScheduledThreadPool(8));
//		if(!T.isTesting){
//			ACRA.init(this);
//		}
		api = WXAPIFactory.createWXAPI(this, ConstValue.APP_ID, true);
        api.registerApp(ConstValue.APP_ID);

		actList = new ArrayList<Activity>();
		dataEngine = new DataEngine(this.getApplicationContext());
		MobclickAgent.updateOnlineConfig(this);
		initDataEngin();
		getUmenDomain();
        getDisplayHuoyuanKefuButton();
		getSSNactivityPara();
		MobclickAgent
				.setOnlineConfigureListener(new UmengOnlineConfigureListener() {
					@Override
					public void onDataReceived(JSONObject data) {
						if (data == null) {
							return;
						}
						if (data.isNull(ConstValue.ONLINE_UPLOAD_PIC)) {
							if (data.has(ConstValue.ONLINE_UPLOAD_PIC)) {
								String uploadPara;
								try {
									uploadPara = data
											.getString(ConstValue.ONLINE_UPLOAD_PIC);
									JSONObject obj = new JSONObject(uploadPara);
									if (!obj.isNull("useQiniuServer")) {
										if (obj.has("useQiniuServer")) {
											String flag = obj
													.getString("useQiniuServer");
											if (flag.equals("no")) {
												WxShopApplication.app.useQiniu = false;
												if (!obj.isNull("server")) {
													if (obj.has("server")) {
														WxShopApplication.app.miaomiaoUploadServer = obj
																.getString("server");
													}
												}
											} else {
												WxShopApplication.app.useQiniu = true;
											}
										}
									}
								} catch (JSONException e) {
									T.e(e);
								}

							}
						}
					}
				});

//		EMChat.getInstance().init(this);
	}

    public static WxShopApplication get(Context context) {
        return (WxShopApplication) context.getApplicationContext();
    }

	public boolean useQiniu = true;
	public String miaomiaoUploadServer;
	public static int crop_w = 300;
	public static int crop_h = 300;
	public static int aspectX = 1;
	public static int aspectY = 1;

	private void initDataEngin() {
		String str = "QMMWD/" + Utils.getAppVersionString(this) + " Android/"
				+ Utils.getOSVerison(this) + " Device/" + Utils.getDeviceName();
		dataEngine.setUserAgent(str);
        initBusinessCommunityAboutMyNotification();
	}

	public static WxShopApplication app = null;
	public static String UPDATE_APK_URL = null;
	public String DOMAIN_MMWD_URL = null;
	public String HUOYUAN_KEFU_DISPLAY = "yes";
	public static String locationString;
	public static boolean MAIN_IS_RUNNING;

	public WxShopApplication() {
		app = this;
	}

	public String getDomainMMWDUrl() {
		if (DOMAIN_MMWD_URL == null || DOMAIN_MMWD_URL.equals("")) {
			return WDConfig.DEFAULT_MMWD_URL;
		}
		return DOMAIN_MMWD_URL;
	}

	public void closeAllActivity() {
		for (int i = 0; i < actList.size(); i++) {
			Activity act = actList.get(i);
			if (!act.isFinishing()) {
				act.finish();
			}
		}
	}

	public void addActivity(BaseActivity baseActivity) {
		actList.add(baseActivity);
	}


	public void checkUpdate(final Context context, final Handler handler) {
		if (context != null) {
			if (context instanceof MainActivity_) {
				if (WxShopApplication.dataEngine.isShownUpdate()) {
					return;
				}
			}
		}
		getUmenBsStaticUrl(context);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					T.e(e);
				}
				while (true) {
					if (WxShopApplication.UPDATE_APK_URL != null) {
						String[] values = WxShopApplication.UPDATE_APK_URL
								.split(";");
						if (!values[0].equals("")) {
							boolean needUpdate = checkNeedUpdateByVersion(values[0]);
							if (needUpdate) {
								Bundle data = new Bundle();
								data.putString("updateUrl", values[1]);
								data.putString("updateComment", values[2]);
								data.putString("needForce", values[3]);
								Message msg = handler.obtainMessage();
								msg.what = MoreActivity.SHOW_UPDATE_DIALOG;
								msg.setData(data);
								handler.sendMessage(msg);
							} else {
								if (context instanceof MoreActivity) {
									handler.sendEmptyMessage(MoreActivity.SHOW_UPDATE_DIALOG_AlEADY_NEWSEST);
								}
							}
						}
						break;
					}
				}
			}
		}).start();
	}

	/**
	 * true needVersion false noNeed
	 * */
	protected boolean checkNeedUpdateByVersion(String string) {
		int serverCode = Integer.parseInt(string);
		int localVersionCode = serverCode;
		try {
			localVersionCode = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			T.e(e);
		}
		if (serverCode > localVersionCode) {
			return true;
		}
		return false;
	}

	public void getUmenBsStaticUrl(final Context context) {
		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (WxShopApplication.UPDATE_APK_URL == null|| WxShopApplication.UPDATE_APK_URL.equals("")) {
						WxShopApplication.UPDATE_APK_URL = MobclickAgent
								.getConfigParams(context,
										ConstValue.ONLINE_APK_URLS);
					}
				}
			}).start();
		}
	}

	public void getUmenDomain() {
		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (DOMAIN_MMWD_URL == null) {
						DOMAIN_MMWD_URL = MobclickAgent.getConfigParams(
								getApplicationContext(), ConstValue.DOMAIN_URL);
					}
				}
			}).start();
		}

	}
	public void getDisplayHuoyuanKefuButton() {
		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
                        HUOYUAN_KEFU_DISPLAY = MobclickAgent.getConfigParams(
								getApplicationContext(), ConstValue.HuoYuan_kefu_display);
				}
			}).start();
		}

	}
    @Background(delay = 1000)
	public void getSSNactivityPara() {
		if (Utils.isCanConnectionNetWork(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String strpara = MobclickAgent.getConfigParams(
							getApplicationContext(),
							ConstValue.SUISUINIAN_ACTIVITY);
					try {
						JSONObject jsonObject = new JSONObject(strpara);
						if (jsonObject.has("text")) {
							SSN_ACTIVITY_TEXT = jsonObject.getString("text");
						}
						if (jsonObject.has("url")) {
							SSN_ACTIVITY_URL = jsonObject.getString("url");
						}
					} catch (JSONException e) {
						T.e(e);
					}
				}
			}).start();
		}

	}

	public static String getLocationLat() {

		if (locationString == null || locationString.equals("")) {
			if (!dataEngine.getLocationString().equals("")) {
				locationString = dataEngine.getLocationString();
			} else {
				locationString = "0,0";
			}
		}
		return locationString.split(",")[0];

	}

	public static String getLocationLng() {

		if (locationString == null || locationString.equals("")) {
			if (!dataEngine.getLocationString().equals("")) {
				locationString = dataEngine.getLocationString();
			} else {
				locationString = "0,0";
			}
		}
		return locationString.split(",")[1];

	}

	public boolean checkDupMainactivity(String checkName) {
		for (int i = 0; i < actList.size(); i++) {
			Activity act = actList.get(i);
			if (act.getClass().getName().equals(MainActivity_.class.getName())) {
				return true;
			}
		}
		return false;
	}
	// test

    /**
     * 初始化本地存储的消息通知
     */
    public static void initBusinessCommunityAboutMyNotification(){
        BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper businessCommmunityMyNotificationDataWrapper
                = new BusinessCommunityService.BusinessCommmunityMyNotificationDataWrapper();
        BusinessCommunityService.BusinessCommmunityMyNotificationListWrapper businessCommmunityMyNotificationListWrapper
                = new BusinessCommunityService.BusinessCommmunityMyNotificationListWrapper();
        businessCommmunityMyNotificationDataWrapper
                .data = businessCommmunityMyNotificationListWrapper;
        businessCommmunityMyNotificationDataWrapper
                .data.tag="0";
        businessCommmunityMyNotificationDataWrapper
                .data.has_new="0";
        businessCommmunityMyNotificationDataWrapper.data.count="0";
        businessCommmunityMyNotificationDataWrapper.data.items = new ArrayList<BusinessCommunityMyNotificationBean>();
        WxShopApplication.dataEngine.setBusinessCommmunityMyNotificationData(businessCommmunityMyNotificationDataWrapper);
		
	}

    public void inject(Object object) {
        objectGraph.inject(object);
    }


}

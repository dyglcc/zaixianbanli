package qfpay.wxshop.utils;

import android.os.Environment;

import com.adhoc.utils.T;

public class ConstValue {
	public static final String PREFS_MEMBER = "qianfang.member";
	public static String getPICTURE_DIR() {
		return getSdcardPath() + "/" +T.AppName +"/" + "pictures/";
	}
	//    操作的数据是联系人信息Uri
	public static String people = "content://com.android.contacts/contacts";
	//    联系人电话Uri
	public static String phone = "content://com.android.contacts/data/phones";
	//    联系人Email Uri
	public static String email = "content://com.android.contacts/data/emails";
	// 签约实体
	public static final String name = "name";
	public static final String src = "src";
	public static String getWebView_cache() {
		return getPICTURE_DIR() + "webview/cache";
	}
	/**
	 * 取sdcard的路径
	 * 
	 * @return 如果Sdcard没有，或者不可写，返回null
	 */
	public static String getSdcardPath() {
		if (haveSdcard()) {
			return Environment.getExternalStorageDirectory()
					.getPath();
		} else {
			return null;
		}
	}

	/**
	 * 是否有SDCARD
	 * 
	 * @return 有SDCARD,返回true,否则返回false
	 */
	public static boolean haveSdcard() {
		return (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED));
	}


	public static final String TEMP_USER = "QMM_U";

	public static final String TEMPNAME_FILE_NAME = "qmm";

	public static final String QQ_APP_ID = "100515599";
	public static final String QQ_ZONE_ID = "101004970";

	// 替换
	public static final String APP_WEIBO_KEY = "1071841130";

	public static final String WEI_BO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog";

	// 替换为开发者REDIRECT_URL


	public static final String URL = "url";
	public static final String TITLE = "title";
	public static final int MSG_ERROR_FROM_MAINHANDLER= 67;
	public static final int PAGE_SIZE_MANAGE= 30;
	

	private static final int UPLOAD_FILES = 4;
	public static final int CHECK_UPLOAD_STATUS = UPLOAD_FILES + 1;

	public static final long minute5 = 30 * 1000;
	public static final long oneDay = 1* 24 * 60 * 60 * 1000; 
	public static final long twoDay = 2* 24 * 60 * 60 * 1000; 
	public static final long threeDay = 3* 24 * 60 * 60 * 1000;
	public static final long fourDay = 4* 24 * 60 * 60 * 1000;
	public static final int shareBigPic = 500;
	public static final int shareSmallPic = 120;


	public static final String THREAD_CANCELABLE = "cancelable";
	
	public static final String 	SHARE_NAME_FINDMIAO = "发现个喵";
}

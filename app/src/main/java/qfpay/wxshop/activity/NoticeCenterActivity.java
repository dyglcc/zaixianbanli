package qfpay.wxshop.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import m.framework.utils.UIHandler;
import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.NoticeItemBean;
import qfpay.wxshop.data.beans.SsnContentBean;
import qfpay.wxshop.data.handler.MainHandler;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.data.netImpl.NoticeListNetImpl;
import qfpay.wxshop.dialogs.ISimpleDialogListener;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.main.fragment.MaijiaxiuFragment;
import qfpay.wxshop.ui.view.CustomProgressDialog;
import qfpay.wxshop.ui.view.EditorView;
import qfpay.wxshop.ui.view.NoticeItem;
import qfpay.wxshop.ui.view.*;
import qfpay.wxshop.ui.view.NoticeListView;
import qfpay.wxshop.ui.view.TopCloseAnimation;
import qfpay.wxshop.ui.view.TopExpandAnimation;
import qfpay.wxshop.ui.web.*;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.framework.utils.UIHandler;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qzone.QZone;
//import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.actionbarsherlock.app.ActionBar;
import com.squareup.okhttp.internal.Platform;

/**
 * 消息中心 页面
 */
@SuppressLint("HandlerLeak")
@EActivity(R.layout.main_list_notice_center)
public class NoticeCenterActivity extends BaseActivity implements
		ISimpleDialogListener, Callback {
	public static final String SP_NAME_MANAGE = "config";
	public static final String SP_ITEN_ISNEW = "copy_isnew";
	public static final String SP_HEADER_ISNEW = "header_header_img_isnew";

	@ViewById
	Button btn_share;
	@ViewById
	Button btn_add;

	@ViewById
	Button btn_empty_see;
	@ViewById
	TextView tv_link;

	public static final float BILI = 1f;

	private LayoutInflater mInflater;
	@ViewById(R.id.list_manage_ssn)
	NoticeListView listView;
	@ViewById(R.id.iv_share_image_onload)
	ImageView shareImg;
	@ViewById
	ImageView iv_nodata;

	@ViewById(R.id.layer_iv_share_result)
	ImageView ivImagelayer2;

	public NoticeItem sharebean;



	// 已经没有数据了吗？
	public static boolean nodata;

	public static ArrayList<NoticeItemBean> data = new ArrayList<NoticeItemBean>();
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, String> dateStrs = new HashMap<Integer, String>();
	@ViewById(R.id.layout_1)
	View view11;

	@ViewById(R.id.layout_2)
	View view12;
	@ViewById(R.id.layout_3)
	View viewLoading;

	@ViewById(R.id.layout_4)
	View viewFaild;

	@ViewById(R.id.progressBar1)
	ProgressBar progressBar1;

	@ViewById(R.id.ib_close)
	Button ibClose;
	@ViewById
	Button ib_close_2;

	@ViewById(R.id.layout_friend)
	View friendGoon;

	@ViewById(R.id.layout_moment)
	View momentGoon;
	@ViewById(R.id.btn_retry)
	View btn_retry;

	private boolean isloadding;
	private int pageIndex = 1;

	public static int clickedPos;

	TopExpandAnimation expand_animation;
	TopCloseAnimation closeAnima;
	Animation leftAnima, leftanima2;

	private int selectType;

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private ImageButton btn_back;
	@AfterViews
	void init() {

		if (initSuccess) {
			notifyData1();
			return;
		}

		dateStrs.clear();
		mInflater = LayoutInflater.from(this);

		data = new ArrayList<NoticeItemBean>();

		initHeader();

		initListView();

		getData();

		if (!initShare) {
//			ShareSDK.initSDK(this);
			initShare = true;
		}

		initSuccess = true;
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.common_menuitem_notice_title);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = actionBar.getCustomView();
		btn_back = (ImageButton) view.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});
	}

	@ViewById
	TextView tv_day, tv_month;
	@ViewById
	View layout_float;
//	@ViewById
//	View ib_search_sub;
	private boolean initSuccess;

	@Click
	void layout_moment() {
		friendShop(bsb);
	}

	@Click
	void layout_friend() {
		// momentsShop(bsb);
	}

//	@Click
//	void ib_search_sub() {
//		String[] stringArray = this.getResources().getStringArray(
//				R.array.official_category);
//		QMMAlert.showAlertWithListView(this,
//				getString(R.string.miaomiaotishi), R.drawable.about,
//				stringArray, selectType, new OnAlertSelectId() {
//					@Override
//					public void onClick(int whichButton) {
//
//						Toaster.s(NoticeCenterActivity.this, whichButton + "");
//						selectType = whichButton;
//					}
//				});
//	}

	View headerViewInfo;

	private void initHeader() {
		headerViewInfo = mInflater.inflate(
				R.layout.list_header_empty, null);
		if (listView.getHeaderViewsCount() == 0) {
			listView.addHeaderView(headerViewInfo);
		}
	}

	private MyAdatpter adapter;

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		int code = data.getIntExtra("result", -1);
		switch (code) {
		case MaijiaxiuFragment.ACTION_ADD_SSN:

			NoticeItem item = (NoticeItem) data
					.getSerializableExtra("bean");
			if (item != null) {
				bsb = item;
				isAdd = true;
				handler.sendEmptyMessage(upload_success);
			} else {
				Toaster.l(this, "数据异常！");
			}

			break;
		case MaijiaxiuFragment.ACTION_EDIT_SSN:
			NoticeItem itemEdit = (NoticeItem) data
					.getSerializableExtra("bean");
			int pos = data.getIntExtra("editpos", -1);
			if (itemEdit != null) {
				if (pos != -1) {
				}
				bsb = itemEdit;
				isAdd = false;
				handler.sendEmptyMessage(upload_success);
			} else {
				Toaster.l(this, "数据异常！");
			}
			break;

		default:
			break;
		}

	};

	String displayImage;

	@OnActivityResult(MaijiaxiuFragment.ACTION_EDIT_SSN)
	void onEdit(Intent intent, int resultCode) {
		T.d("ACTION_EDIT_GOOD");
		if (resultCode == Activity.RESULT_OK) {

		}
	}

	private void initListView() {
		adapter = new MyAdatpter();
		listView.setAdapter(adapter);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				T.i(System.currentTimeMillis()+" data change ");
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				T.i(System.currentTimeMillis() + "data  success");
			}
		});
		View getmFooterView = listView.getmFooterView();
		getmFooterView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getData();
			}
		});
	}

	String cacheKey = null;

	void getData() {
		if (isloadding == true) {
			return;
		}
		if (nodata) {

			setMoreButtonText(getResources().getString(R.string.haveNoData),
					false);
			return;
		}

		isloadding = true;

		setMoreButtonText("加载中...", true);

		AbstractNet net = new NoticeListNetImpl(this);
		Bundle bun = new Bundle();
		if (pageIndex == 0) {
			bun.putInt("page", 1);
		} else {
			bun.putInt("page", pageIndex);
		}
		net.request(bun, new MainHandler(this, handler) {

			@Override
			public void onSuccess(Bundle bundle) {

				if (bundle != null
						&& bundle.getString(ConstValue.CACHE_KEY) != null) {
					cacheKey = bundle.getString(ConstValue.CACHE_KEY);
					HashMap<String, Object> list = CacheData.getInstance()
							.getData(cacheKey).get(0);
					
					@SuppressWarnings("unchecked")
					List<NoticeItemBean> datas = (List<NoticeItemBean>) list.get("orderList");
					if (datas.size() < ConstValue.PAGE_SIZE) {
						nodata = true;
					}
					if (data == null) {
						data = new ArrayList<NoticeItemBean>();
					}
					if (pageIndex == 0) {
						data.clear();
						pageIndex = 1;
					}
					data.addAll(datas);
					notifyData();
					isLoadingData = false;
					stopOldProgress();
					pageIndex++;
				}
			}

			@Override
			public void onFailed(Bundle bundle) {
				isLoadingData = false;
				loadFail = true;
				stopOldProgress();
			}
		});
	}

	private boolean loadFail = false;

	private void setMoreButtonText(String str, boolean loading) {

		View view = listView.getmFooterView();
		if (view == null) {
			return;
		}
		View progress = view.findViewById(R.id.pb_loading);
		if (loading) {
			progress.setVisibility(View.VISIBLE);
		} else {
			progress.setVisibility(View.GONE);
		}
		TextView textview = (TextView) view.findViewById(R.id.tv_more);
		if (textview == null) {
			return;
		}
		textview.setText(str);
	}

	protected void notifyData() {
		listView.checkFooterView();
		setFooterText();
		adapter.notifyDataSetChanged();
	}


	protected void notifyData1() {

		initListView();
		initHeader();
		listView.checkFooterView();
		setFooterText();

		adapter.notifyDataSetChanged();
	}

	@UiThread(delay = 300)
	void setFooterText() {

		if(data == null ){
			return;
		}
		if (nodata) {
			setMoreButtonText(getResources().getString(R.string.haveNoData),
					false);
		} else if (data.size() == 0) {

			getData();

		} else {
			setMoreButtonText(getResources().getString(R.string.show_more),
					false);
		}
	}

	private class MyAdatpter extends BaseAdapter implements Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}

		@Override
		public NoticeItemBean getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int pos, View convertview, ViewGroup arg2) {

			if(pos == data.size()-1){
				getData();
			}
			if (convertview == null) {
				convertview = NoticeItem_.build(NoticeCenterActivity.this);
			}
			((NoticeItem_) convertview).setValues(data.get(pos), NoticeCenterActivity.this,handler);
			return convertview;
		}

	}

	public final static int POPUP_GOODITEM = ConstValue.MSG_ERROR_FROM_MAINHANDLER + 1;
	public final static int SSN_DEL = POPUP_GOODITEM + 1;
	public final static int ACTION_GET_DATA = SSN_DEL + 1;
	public final static int upload_faild = ACTION_GET_DATA + 1;
	public final static int upload_retry = upload_faild + 1;
	public final static int CHANGE_DATA = upload_retry + 1;
	public final static int NOTIFY_DATA = CHANGE_DATA + 1;
	private static final int upload_success = NOTIFY_DATA + 1;
	public static final int SSN_SHARE = upload_success + 1;

    public static final int CHANGETAB =SSN_SHARE +1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
				isloadding = false;
				loadFail = true;
				// layout_progress_load.setVisibility(View.INVISIBLE);

				if (nodata) {
					setMoreButtonText(
							getResources().getString(R.string.haveNoData),
							false);
				} else {
					setMoreButtonText("显示更多", false);
				}
				break;
			case SSN_DEL:
				Bundle closeBun = msg.getData();
				NoticeItem showbean = (NoticeItem) closeBun
						.getSerializable(SSNPublishActivity.SSN_DEL_BEAN);
				dateStrs.clear();
				data.remove(showbean);
				notifyData();
				break;

			case 1:
				Toaster.l(NoticeCenterActivity.this, "启动分享...");
				break;
			case ACTION_GET_DATA:
				getData();
				break;
			case 102:
				progressBar1.setProgress(j++);
				break;
			case upload_success:
				break;
			case upload_faild:
				viewLoading.setVisibility(View.GONE);
				viewFaild.setVisibility(View.VISIBLE);
				break;
			case upload_retry:
				viewLoading.setVisibility(View.VISIBLE);
				viewFaild.setVisibility(View.GONE);
				break;
			case NOTIFY_DATA:
				notifyData();
				break;
			case SSN_SHARE:
				popUpDialog();
				break;
			case CHANGETAB:
                WxShopApplication.app.main.showOneKeybehalf();
				break;
			default:
				break;
			}

		};
	};
	private boolean isAdd = true;
	private int j = 1;
	@ViewById(R.id.btn_back)
	ImageButton btnBack;


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (data != null) {
			data.clear();
		}
		nodata = false;
		data = null;
		if (initShare) {
//			ShareSDK.stopSDK(this);
		}
		pageIndex = 1;
		initSuccess = false;
		isloadding = false;
	}

	private boolean initShare;

	@UiThread
	public void refreshListView() {

		pageIndex = 1;
		getData();
	}

	boolean isLoadingData = false;

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 1) {
            CommonWebActivity_.intent(NoticeCenterActivity.this).url(WDConfig.getInstance().getShopUrl()
                    + WxShopApplication.dataEngine.getShopId()).title("店铺预览").start();

		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}

	public boolean isLoadingData() {
		return isLoadingData;
	}

	private CustomProgressDialog progressDialog;

	@UiThread
	void startOldProgress() {
		if (this != null && !this.isFinishing()) {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog4BBS(this);
				progressDialog.setMessage("加载中...");
			}
			progressDialog.show();
		}
	}

	@UiThread
	void stopOldProgress() {
		if (this != null && !this.isFinishing()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	public void removeItem(int position) {
		data.remove(position);
		adapter.notifyDataSetChanged();
	}

	@UiThread
	void showErrorMsg(String msg) {
		Toaster.s(this, msg);
	}

	private NoticeItem bsb;

	private static final int SHARE_MOMENT = 0;
	private static final int SHARE_FRIENT = 1;
	private static final int ONE_KEY_SHARE = SHARE_FRIENT + 1;
	private static final int COPY = ONE_KEY_SHARE + 1;

	private void popUpDialog() {
		String[] items = getResources().getStringArray(
				R.array.share_friends_ssn);
		if (sharebean == null) {
			Toaster.l(this, "失败，分享数据空");
			return;
		}
		QMMAlert.showAlert(this, getString(R.string.share2), items,
				null, new QMMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case SHARE_FRIENT:
							// 显示启动分享文字
							handler.sendEmptyMessage(1);
							// momentsShop(sharebean);
							break;
						case SHARE_MOMENT:
							// 显示启动分享文字

							handler.sendEmptyMessage(1);
							friendShop(sharebean);
							break;
						}
					}

				});
	}

	private void momentsShop(NoticeItemBean gb) {
		// String image_url = gb.getImg();
		// WeiXinDataBean wdb = new WeiXinDataBean();
		// if (image_url != null && !image_url.equals("")) {
		// wdb.imgUrl = Utils.getThumblePic(image_url, 120);
		// } else {
		// Toaster.l(this, "分享图片丢失,分享失败");
		// return;
		// }
		// wdb.title = gb.getTitle();
		// String desc = getContentStr(gb.getContent());
		// if (desc != null) {
		// desc = desc.replace(" ", "");
		// desc = desc.replaceAll("\\n", "");
		// }
		// wdb.description = desc;
		// wdb.scope = ConstValue.circle_share;
		// wdb.url = Utils.getSSNurl(gb);
		// wdb.imgUrl = gb.getImg_url();
		//
		// UtilsWeixinShare.shareWeb(wdb, null, this);
	}

	private void friendShop(NoticeItem gb) {
		// String image_url = gb.getImg_url();
		// WeiXinDataBean wdb = new WeiXinDataBean();
		// if (image_url != null && !image_url.equals("")) {
		// wdb.imgUrl = Utils.getThumblePic(image_url, 120);
		// } else {
		// Toaster.l(this, "分享图片丢失,分享失败");
		// return;
		// }
		// String desc = getContentStr(gb.getContent());
		// if (desc != null) {
		// desc = desc.replace(" ", "");
		// desc = desc.replaceAll("\\n", "");
		// }
		// wdb.description = desc;
		// wdb.title = gb.getTitle();
		// wdb.url = Utils.getSSNurl(gb);
		// wdb.scope = ConstValue.friend_share;
		// UtilsWeixinShare.shareWeb(wdb, null, this);
	}

	private String getContentStr(List<SsnContentBean> content) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < content.size(); i++) {
			SsnContentBean sdb = content.get(i);
			if (sdb.getType().equals(EditorView.FLAG_EDIT)) {
				sb.append(sdb.getContent());
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		String text = Utils.actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: {
			// 成功
			Platform plat = (Platform) msg.obj;
//			text = plat.getName() + "分享成功";
		}
			break;
		case 2: {
			// 失败
			if ("WechatClientNotExistException".equals(msg.obj.getClass()
					.getSimpleName())) {
				text = this.getString(
						R.string.wechat_client_inavailable);
			} else if ("WechatTimelineNotSupportedException".equals(msg.obj
					.getClass().getSimpleName())) {
				text = this.getString(
						R.string.wechat_client_inavailable);
			} else {
				text = getString(R.string.fail_share2);
			}
		}
			break;
		case 3: {
			// 取消
			Platform plat = (Platform) msg.obj;
//			text = plat.getName() + "取消分享";
		}
			break;
		}

		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		return false;
	}

//	@Override
	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);

//		if (plat.getName().equals(SinaWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"sina_share_success_sharesdk");
//		} else if (plat.getName().equals(QZone.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"qzone_share_success_sharesdk");
//		} else if (plat.getName().equals(TencentWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"qqweibo_share_success_sharesdk");
//		}

	}

	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();
		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);
//		if (plat.getName().equals(SinaWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"sina_share_faill_sharesdk");
//		} else if (plat.getName().equals(QZone.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"qzone_share_fail_sharesdk");
//		} else if (plat.getName().equals(TencentWeibo.NAME)) {
//			MobAgentTools.OnEventMobOnDiffUser(this,
//					"qqweibo_share_fail_sharesdk");
//		}
	}

}

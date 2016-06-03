package qfpay.wxshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adhoc.utils.T;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import banli.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.dialogs.SimpleDialogFragment;
import qfpay.wxshop.listener.onScrollviewFocusListener;
import qfpay.wxshop.takepicUtils.PictureBean;
import qfpay.wxshop.takepicUtils.TakePicUtils;
import qfpay.wxshop.ui.view.EditorView;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
//import com.adhoc.adhocsdk.AdhocTracker;

/**
 * 碎碎念发布页面
 * */
@EActivity(R.layout.main_suisuinian_pub)
public class SSNPublishActivity extends BaseActivity implements
		onScrollviewFocusListener {

	@ViewById(R.id.parent)
	EditorView mScrollview;
	public static String FENGEFU_PIC_SIZE = "_";

	public static final String SSN_DEL_BEAN = "suisuininadelbean";
	public static final String IV_DELETELINK_LINK = "link";
	public static final String IV_DELETELINK_DELETE = "delete";
	public static final int REQUEST_TAKE_PIC = 1;
	Button btn_save, btn_back;
	@ViewById
	EditText et_title;
	@ViewById
	LinearLayout layout_link, layout_add_pic, layout_link_close;

	@ViewById
	ImageView iv_deletelink, iv_linkicon, iv_title, iv_content;

	@ViewById
	TextView tv_link;
	@ViewById
	TextView layout_addpic_spaceing;
	private Map<String, String> mapFileSize = new HashMap<String, String>();

	@Click
	void layout_add_pic() {
		if(!Utils.isCanConnectionNetWork(SSNPublishActivity.this)){
			Toaster.l(this, "喵~没有联网");
			return;
		}
		if (!onEditorFocus) {
			Toaster.l(SSNPublishActivity.this, "选择添加位置");
			return;
		}
		MobAgentTools.OnEventMobOnDiffUser(this, "Click_HybridText_addphoto");
		TakePicUtils.getInstance().init(SSNPublishActivity.this);
		TakePicUtils.getInstance().takePic(
				TakePicUtils.TAKE_PIC_MODE_ONLY_SELECT_MUL_PICS);
	}


	@AfterViews
	void inits() {
//		AdhocTracker.reportCrashEnable(false);
//		mScrollview = null;
		mScrollview.init();
		mScrollview.addOnFocusListener(this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.common_menuitem_suisuinian);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = actionBar.getCustomView();
		btn_back = (Button) view.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showDialogConfirm();

			}
		});
		btn_save = (Button) view.findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobAgentTools.OnEventMobOnDiffUser(SSNPublishActivity.this, "Click_HybridText_save");
				T.i(mScrollview.getContent(mapFileSize));
				save2Server(mScrollview.getContent(mapFileSize));
			}
		});

		et_title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (et_title.getText().toString().length() == 0) {
					et_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							getResources()
									.getDimension(R.dimen.text_size_small));
				} else {
					et_title.setTextSize(
							TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(
									R.dimen.text_size_normal));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		et_title.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					onEditorFocus = false;
					// editor view 设置hint
					mScrollview.setHint();
					// 设置选中背景图片
					iv_title.setImageResource(R.drawable.icon_ssl_title_p);
					iv_content.setImageResource(R.drawable.icon_ssl_text);
				} else {
					iv_title.setImageResource(R.drawable.icon_ssl_title_p);
				}
			}
		});
		tv_link.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (tv_link.getText().toString().length() == 0) {
					tv_link.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							getResources()
									.getDimension(R.dimen.text_size_small));
				} else {
					tv_link.setTextSize(
							TypedValue.COMPLEX_UNIT_PX,
							getResources().getDimension(
									R.dimen.text_size_normal));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		mapFileSize.put(WDConfig.MAIJIAXIU_SHARE_PIC,
				getPicSizeStr(this, R.drawable.ic_launcher));

		item = new SSNItemBean();
//		layout_addpic_spaceing.setText(WxShopApplication.app.SSN_ACTIVITY_TEXT
//				.equals("") ? getString(R.string.ssn_have_look)
//				: WxShopApplication.app.SSN_ACTIVITY_TEXT);
		

		MobAgentTools.OnEventMobOnDiffUser(SSNPublishActivity.this, "click_HybridText_add");
		

	}

	public static String getPicSizeStr(String fileDir) {
		File file = new File(fileDir);
		if (!file.exists()) {
			return "100" + FENGEFU_PIC_SIZE + "100";
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		BitmapFactory.decodeFile(fileDir, options);
		options.inJustDecodeBounds = true;
		return options.outWidth + FENGEFU_PIC_SIZE + options.outHeight;
	}

	public static String getPicSizeStr(Context context, int maijiaxiuSharePic) {
		Drawable drawable = context.getResources().getDrawable(
				maijiaxiuSharePic);
		return drawable.getIntrinsicWidth() + FENGEFU_PIC_SIZE
				+ drawable.getIntrinsicHeight();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.btn_back) {
			Toaster.l(SSNPublishActivity.this, "back");
		} else if (item.getItemId() == R.id.btn_save) {
			Toaster.l(SSNPublishActivity.this, "save");
		}
		return super.onOptionsItemSelected(item);
	}

	@Click
	void btn_back() {
		// showDialog
		showDialogConfirm();
	}

	@Override
	public void onBackPressed() {

		showDialogConfirm();
	}

	protected void showDialogConfirm() {
		if(TextUtils.isEmpty(et_title.getText()) && mScrollview.isEmpty() && tv_link.getTag() == null){
			finish();
			return;
		}

		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(getString(R.string.mm_hint)).setMessage("亲，退出编辑吗")
				.setNegativeButtonText("继续编辑").setPositiveButtonText("确定放弃")
				.setCancelable(true).setRequestCode(-1)
				.setPositiveClick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				}).show();
	}


	private SSNItemBean item;

	private void save2Server(String content) {

	}

	private boolean isCan() {
		boolean checkUploadSuccess = mScrollview.checkUploadSuccess();
		if (!checkUploadSuccess) {
			Toaster.l(SSNPublishActivity.this, "正在上传图片..");
			return false;
		}
		String image_url = mScrollview.getImageUrl();
		if (image_url == null) {
			Toaster.l(SSNPublishActivity.this, "亲，上传一张图片吧");
			return false;
		}
		if (TextUtils.isEmpty(et_title.getText().toString())) {
			Toaster.l(SSNPublishActivity.this, "没有标题，填写一个标题吧");
			et_title.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TakePicUtils.TAKE_PIC_REQUEST_CODE) {

			if (resultCode != RESULT_OK) {
				return;
			}

			PictureBean fb = TakePicUtils.getInstance().receivePics(
					requestCode, resultCode, data);

			if (fb != null) {

				uploadFile2Server(fb);

			}

		} else if (requestCode == TakePicUtils.SELECT_PIC_REQUEST_CODE) {

			if (WxShopApplication.paths.size() != 0) {

			}
		}

	}

	private void uploadFile2Server(final PictureBean pb) {

		MobAgentTools.OnEventMobOnDiffUser(SSNPublishActivity.this, "Click_HybridText_Camera");

	}


	private static final int UPLOAD_FILES = 4;
	public static final int CHECK_UPLOAD_STATUS = UPLOAD_FILES + 1;
	// 重复上传bug1.调用两次js--解决方法：1。定义标志只调用一次。

	@UiThread
	void setGoodName(String name, String imgPath, int goodId) {
		if (imgPath == null && name == null) {
			tv_link.setHint(getString(R.string.ssn_publish_goodlink));
			tv_link.setText("");
			iv_deletelink.setTag(IV_DELETELINK_LINK);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_in);
		} else {
			tv_link.setText("已链接商品：" + name);
			iv_deletelink.setTag(IV_DELETELINK_DELETE);
			iv_deletelink.setImageResource(R.drawable.shopedit_icon_delete);
		}
	}


	private boolean onEditorFocus = false;

	@Override
	public void onfocus() {
		onEditorFocus = true;
		iv_content.setImageResource(R.drawable.icon_ssl_text_p);
		iv_title.setImageResource(R.drawable.icon_ssl_title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapFileSize.clear();
		mapFileSize = null;
	}

}

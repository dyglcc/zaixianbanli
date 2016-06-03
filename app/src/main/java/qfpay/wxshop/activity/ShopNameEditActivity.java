package qfpay.wxshop.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import banli.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.ConstValue;
import qfpay.wxshop.utils.Toaster;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 店铺名称编辑页面
 */
@EActivity(R.layout.main_edit_shopname)
public class ShopNameEditActivity extends BaseActivity {

	@ViewById ImageButton btn_back;
	@ViewById Button      btn_share;
	@ViewById TextView    tv_title, tv_count, tv_go2shopview;
	@ViewById EditText    et_text;
	@ViewById View        layout_progress_load;
	@ViewById ImageView  iv_progress_load;

	@AfterViews
	void init() {
		tv_title.setText("编辑店铺名称");
		btn_share.setText("完成");
		btn_share.setVisibility(View.VISIBLE);
		et_text.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override public void afterTextChanged(Editable arg0) {
				tv_count.setText(arg0.length() + "/15");
				if (arg0.length() == 15) {
					tv_count.setTextColor(getResources().getColor(R.color.title_bg_color));
				} else {
					tv_count.setTextColor(getResources().getColor(R.color.grey));
				}
			}
		});
//		String shopName = WxShopApplication.dataEngine.getShopName();
//		if(shopName!=null && !shopName.equals("")){
//			et_text.setText(shopName);
//		}
	}

	@Click void btn_back() {
		finish();
	}

	@Click void btn_share() {
		saveSever();
	}

	@Click void tv_go2shopview() {
		Intent intent = new Intent(ShopNameEditActivity.this, FunctionNoticeActivity.class);
		startActivity(intent);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ConstValue.MSG_ERROR_FROM_MAINHANDLER:
				layout_progress_load.setVisibility(View.INVISIBLE);
				btn_share.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};

	private void saveSever() {
		final String content = et_text.getText().toString().trim();
		if (content.equals("")) {
			Toaster.l(ShopNameEditActivity.this, "请输入店铺名称");
			return;
		}
		btn_share.setVisibility(View.GONE);
		layout_progress_load.setVisibility(View.VISIBLE);
//		ShopNameUpdateImpl net = new ShopNameUpdateImpl(ShopNameEditActivity.this);
//		Bundle bun = new Bundle();
//		bun.putString("intro", content);
//		net.request(bun, new MainHandler(ShopNameEditActivity.this, handler) {
//			@Override public void onSuccess(Bundle bundle) {
//				setResultOK();
//			}
//			@Override public void onFailed(Bundle bundle) { }
//		});

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			AnimationDrawable animation = (AnimationDrawable) iv_progress_load.getBackground();
			animation.start();
		}
		super.onWindowFocusChanged(hasFocus);
	}

//	protected void setResultOK() {
//		Intent intent = new Intent(ShopNameEditActivity.this,
//				ShopInfoActivity.class);
//		setResult(Activity.RESULT_OK, intent);
//		finish();
//	}
}

package qfpay.wxshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import jiafen.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseActivity;
/**
 * 买家秀空列表引导界面
 */
@EActivity(R.layout.main_maijiaxiu_empty)
public class MaijiaxiuEmptyActivity extends BaseActivity {

	@ViewById Button   btn_empty_see;
//	@ViewById Button   btn_share;
	@ViewById TextView tv_link;

	@AfterViews
	void init() {
//		btn_share.setVisibility(View.INVISIBLE);
	}

	private void finishDoNothing() {
		Intent intent = new Intent();
		intent.putExtra("finish", "en");
		setResult(Activity.RESULT_OK, intent);
		finish();
	}





	@Click
	void btn_share() {

	}

	@Override
	public void onBackPressed() {
		finishDoNothing();
	}
}

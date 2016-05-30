package qfpay.wxshop.ui.main.fragment;

import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.app.BaseFragment;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.ui.web.*;
import qfpay.wxshop.utils.MobAgentTools;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 订单货源列表
 * */
@EFragment(R.layout.main_publish_goods)
public class OrderFragment extends BaseFragment {
	@ViewById(R.id.load_fail) View failView;
	@ViewById(R.id.contact_webview) WebView webView;
	@ViewById View layout_progress_load;
	@ViewById(R.id.iv_progress_load) ImageView ivProgress;
	@ViewById TextView tv_title;
	@ViewById View btn_back;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
//		onFragmentRefresh();
		super.onResume();
	}
	private Map<String, String> header = new HashMap<String, String>();
	@Override
	public void onFragmentRefresh() {
//		Utils.setCookies(WDConfig.getInstance().getOrderListURL(""), getActivity());
////		Toaster.l(getActivity(), WDConfig.getInstance().getOrderListURL(""));
//		webView.loadUrl(WDConfig.getInstance().getOrderListURL(""));
		 new WebViewTask().execute();
	}

	@AfterViews @SuppressLint("SetJavaScriptEnabled") void init() {
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "Order management");
		tv_title.setText(getString(R.string.order_manage));

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);

		header.put("QFCOOKIE", "sessionid=" + WxShopApplication.dataEngine.getcid());
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity())
						.setTitle(getString(R.string.hint))
						.setMessage(message)
						.setPositiveButton("ok",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								});

				b2.setCancelable(false);
				b2.create();
				b2.show();
				return true;
			}
		});
//		Utils.setCookies(WDConfig.getInstance().getOrderListURL(""), getActivity());
//		webView.loadUrl(WDConfig.getInstance().getOrderListURL(""));
		new WebViewTask().execute();
	}

	@Click
	void btn_back() {
		if (webView.canGoBack()) {
			webView.goBack();
		}
	}

	private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
		String sessionCookie ="sessionid="
				+ WxShopApplication.dataEngine.getcid();
		CookieManager cookieManager;
 
	        @Override
		protected void onPreExecute() {
			CookieSyncManager.createInstance(getActivity());
			cookieManager = CookieManager.getInstance();
                        
			if (sessionCookie != null) {
                                /* delete old cookies */
				cookieManager.removeSessionCookie(); 
			}
			super.onPreExecute();
		}
		protected Boolean doInBackground(Void... param) {
                        /* this is very important - THIS IS THE HACK */
//			SystemClock.sleep(100);
			return false;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (sessionCookie != null) {
				cookieManager.setCookie(WDConfig.getInstance().getOrderListURL(""), sessionCookie);
				CookieSyncManager.getInstance().sync();
			}
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setBuiltInZoomControls(true);
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					startWebActivity(url);
					return true;
				}
				
			});
			webView.loadUrl(WDConfig.getInstance().getOrderListURL(""),header);
		}
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int code = data.getIntExtra("result", -1);
		if(code == REFRESH){
			new WebViewTask().execute();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public static final int REFRESH = 1;
	private void startWebActivity(String url) {
		if (webView == null) {
			return;
		}
		if(getActivity()!=null){
			WebActivity_.intent(getActivity()).url(url).startForResult(OrderFragment_.REFRESH);
		}
	}
}

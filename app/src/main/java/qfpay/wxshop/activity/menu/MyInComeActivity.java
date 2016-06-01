package qfpay.wxshop.activity.menu;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.WeixinQmmActivity;
import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.utils.JsToJavaInteface;
import qfpay.wxshop.utils.Utils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 *
 * 我的收入activity
 */
@SuppressLint("SetJavaScriptEnabled")
public class MyInComeActivity extends BaseActivity {

	private View failView;
	private WebView webView = null;
	@SuppressLint("JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_publish_income);


		getSupportActionBar().setTitle("我的收入");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		failView = findViewById(R.id.load_fail);

		webView = (WebView) this.findViewById(R.id.contact_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(WDConfig.ERROR_PAGE)) {
					return true;
				}
				Utils.setCookies(WDConfig.getInstance().getIncomeURL(""), MyInComeActivity.this);
				webView.loadUrl(url);
				return true;
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
			}

			public void onPageFinished(WebView view, String url) {
				if (!Utils.isCanConnectionNetWork(MyInComeActivity.this)) {
					webView.setVisibility(View.INVISIBLE);
					failView.setVisibility(View.VISIBLE);
				} else {
					webView.setVisibility(View.VISIBLE);
					failView.setVisibility(View.INVISIBLE);
				}
			}


			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				webView.setVisibility(View.INVISIBLE);
				failView.setVisibility(View.VISIBLE);
			}
		});
		

		webView.addJavascriptInterface(new weixinJavaScriptInterface(), "contactwx");
	    webView.addJavascriptInterface(new GoToTiXian(),
	            "extraction_cash_android");
		Utils.setCookies(WDConfig.getInstance().getIncomeURL(""), MyInComeActivity.this);
		webView.loadUrl(WDConfig.getInstance().getIncomeURL(""));
	}
	


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.title_myincom, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	private final class GoToTiXian implements JsToJavaInteface {

		@JavascriptInterface 
	    @Override
	    public void clickOnAndroid(String str) {

	        AlertDialog.Builder buider = new Builder(MyInComeActivity.this);
	        buider.setTitle("提示")
	                .setMessage("亲~尚未添加银行卡哦")
	                .setNegativeButton("稍后",
	                        new DialogInterface.OnClickListener() {

	                            @Override
	                            public void onClick(DialogInterface dialog,
	                                    int which) {

	                                dialog.dismiss();

	                            }
	                        })
	                .setPositiveButton("立即添加",
	                        new DialogInterface.OnClickListener() {

	                            @Override
	                            public void onClick(DialogInterface dialog,
	                                    int which) {
	                                dialog.dismiss();
	                            }
	                        }).create().show();

	    }

	}


	private final class weixinJavaScriptInterface {
		public void clickOnAndroid() {
			startActivity(new Intent(MyInComeActivity.this, WeixinQmmActivity.class));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

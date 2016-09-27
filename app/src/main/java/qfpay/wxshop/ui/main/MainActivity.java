package qfpay.wxshop.ui.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benben.mall.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineScalePulseOutRapidIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.app.BaseActivity;

/**
 * 订单管理内部打开app页面
 */
@EActivity(R.layout.main_web_activity)
public class MainActivity extends BaseActivity {
    @ViewById
    WebView webView;

//    @Extra
//    String url = "http://wxpay.weixin.qq.com/pub_v2/pay/wap.v2.php";
//            String url = "http://192.168.3.4/jinyi/mobile/";
//    @Extra
//    String url = "http://www.chinaycys.com/mobile/";


    private Map<String, String> header = new HashMap<String, String>();

//    private AVLoadingIndicatorView avi;

    private ProgressDialog progressBar;

    @AfterViews
    void init() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.hide();
        String url = getString(R.string.mobile_url);
//        tv_title.setText("订单管理");
//        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
//        avi.setIndicator(new LineScalePulseOutRapidIndicator());
        // test
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setBuiltInZoomControls(false);
        String ua = webSettings.getUserAgentString();
        ua += " webview_browser";
        webSettings.setUserAgentString(ua);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

//        progressBar = ProgressDialog.show(MainActivity.this, "", "加载中...");
        progressBar = new ProgressDialog(MainActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
//        progressBar.setTitle("");
        progressBar.setCanceledOnTouchOutside(true);
        progressBar.setMessage("加载中...");
        progressBar.show();

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!progressBar.isShowing()){
                    progressBar.show();
                }
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                webView.loadUrl(url);
            return true;
        }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "错误:" + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("错误");
                alertDialog.setMessage(description);
                alertDialog.setButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                if(!isFinishing()){
                    if(alertDialog!=null){
                        alertDialog.show();
                    }
                }
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

}

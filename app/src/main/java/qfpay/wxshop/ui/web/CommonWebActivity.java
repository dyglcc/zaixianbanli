package qfpay.wxshop.ui.web;

import qfpay.wxshop.app.BaseActivity;
/**
 * 通用webview activity 传参title,url,
 */
//@EActivity(R.layout.web_common_activity)
public class CommonWebActivity extends BaseActivity
{
//	@ViewById
//    WebViewSavePic webView;
//	@ViewById LinearLayout ll_fail;
//
//	@FragmentById CommonWebFragment webFragment;
//
//	@Extra String url = "";
//	@Extra String title = "";
//	@Extra String push = "";
//	@Extra String pointName = ""; // 用于统计分享次数
//    @Extra String shareTitle = "", shareName = "", shareDescript = "", shareIconUrl = ""; // 分享有关字段, 分享包括gam_medium的情况只需要在预览的时候就携带ga_medium
//
//
//	@AfterViews void init() {
//		getSupportActionBar().setTitle(title);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//	}
//
//
//    @Override
//    public void onBackPressed() {
//        if(webView != null){
//            if(webView.canGoBack()){
//                webView.goBack();
//            }else{
//                exePush();
//            }
//        }
//    }
//
//	private void exePush(){
//		if(push!=null && !push.equals("")){
//		}
//        finish();
//	}
//
//
////    @DebugLog
//    private String generateShareWebUrl(String url, String platformName) {
//        String medium = "ga_medium"; // GA统计字段的参数名,会包含在url里面
//
//        String processedUrl = "";
//        if (url.contains("ga_medium")) {
//            int start = url.indexOf(medium) + medium.length() + 1;
//            String mediumName = "";
//            String temp = url.substring(start);
//            if (temp.contains("&")) {
//                temp = temp.substring(0, temp.indexOf("&"));
//            }
//            if (temp.lastIndexOf("_") == (temp.length() - 1)) {
//                mediumName = temp.concat(platformName);
//            }
//            processedUrl = url.replace(temp, mediumName);
//        }
//        return processedUrl;
//    }
}

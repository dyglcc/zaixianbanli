package qfpay.wxshop.ui.web;

import android.widget.LinearLayout;

import com.adhoc.pic.Picasso;
import com.adhoc.utils.T;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;

import qfpay.wxshop.R;
import qfpay.wxshop.share.OnShareLinstener;
import qfpay.wxshop.share.SharedPlatfrom;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.app.BaseActivity;
import qfpay.wxshop.ui.view.WebViewSavePic;
import qfpay.wxshop.utils.BitmapUtil;
import qfpay.wxshop.utils.ConstValue;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.Toaster;
import qfpay.wxshop.utils.Utils;
/**
 * 通用webview activity 传参title,url,
 */
@EActivity(R.layout.web_common_activity)
public class CommonWebActivity extends BaseActivity implements OnShareLinstener {
	@ViewById
    WebViewSavePic webView;
	@ViewById LinearLayout ll_fail;
	
	@FragmentById CommonWebFragment webFragment;

	@Extra String url = "";
	@Extra String title = "";
	@Extra String push = "";
	@Extra String pointName = ""; // 用于统计分享次数
    @Extra
	ArrayList<SharedPlatfrom> platFroms;// 需要分享到的平台
    @Extra String shareTitle = "", shareName = "", shareDescript = "", shareIconUrl = ""; // 分享有关字段, 分享包括gam_medium的情况只需要在预览的时候就携带ga_medium


	@AfterViews void init() {
		getSupportActionBar().setTitle(title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		webFragment.init(url, false,title);
	}


    @Override
    public void onBackPressed() {
        if(webView != null){
            if(webView.canGoBack()){
                webView.goBack();
            }else{
                exePush();
            }
        }
    }

	private void exePush(){
		if(push!=null && !push.equals("")){
		}
        finish();
	}

	@Override
	public void onShare(SharedPlatfrom which) {
		switch (which) {
		case WXFRIEND:
			shareWxFriend();
			break;
		case WXMOMENTS:
			shareWxMoments();
			break;
		case COPY:
			String copyStr = webView.getUrl();
			Utils.saveClipBoard(this, copyStr);
			Toaster.l(this, shareName + "地址复制成功");
			break;
		default:
			break;
		}
	}

	@Override
	public String getShareFromName() {
		return shareName;
	}

    @Background(id = ConstValue.THREAD_CANCELABLE)
	void shareWxFriend() {
        WeiXinDataBean wdb = getShareData("wcfriend");
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb, null, this);
	}

    @Background(id = ConstValue.THREAD_CANCELABLE)
	void shareWxMoments() {
		WeiXinDataBean wdb = getShareData("wctimeline");
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb, null, this);
	}

    private WeiXinDataBean getShareData(String medium) {
        WeiXinDataBean bean = new WeiXinDataBean();
        bean.url = generateShareWebUrl(url, medium);
        bean.title = shareTitle;
        bean.description = shareDescript;
        try {
            String iconUrl = shareIconUrl;
            if (iconUrl == null || iconUrl.equals("")) {
                iconUrl = "http://qmmwx.u.qiniudn.com/icon.png";
            }

            bean.thumbData = BitmapUtil.bmpToByteArray(
                    Picasso.with(this).
                            load(Utils.getThumblePic(iconUrl, 120)).
                            resize(100, 100).
                            centerCrop().
                            get(),
                    true);
        } catch (IOException e) {
            T.e(e);
        }
        return bean;
    }

//    @DebugLog
    private String generateShareWebUrl(String url, String platformName) {
        String medium = "ga_medium"; // GA统计字段的参数名,会包含在url里面

        String processedUrl = "";
        if (url.contains("ga_medium")) {
            int start = url.indexOf(medium) + medium.length() + 1;
            String mediumName = "";
            String temp = url.substring(start);
            if (temp.contains("&")) {
                temp = temp.substring(0, temp.indexOf("&"));
            }
            if (temp.lastIndexOf("_") == (temp.length() - 1)) {
                mediumName = temp.concat(platformName);
            }
            processedUrl = url.replace(temp, mediumName);
        }
        return processedUrl;
    }
}

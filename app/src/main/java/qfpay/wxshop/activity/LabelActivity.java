package qfpay.wxshop.activity;import java.util.ArrayList;import java.util.List;import org.androidannotations.annotations.AfterInject;import org.androidannotations.annotations.AfterViews;import org.androidannotations.annotations.Click;import org.androidannotations.annotations.EActivity;import org.androidannotations.annotations.Extra;import org.androidannotations.annotations.UiThread;import org.androidannotations.annotations.ViewById;import jiafen.jinniu.com.R;import qfpay.wxshop.WxShopApplication;import qfpay.wxshop.data.beans.LabelBean;import qfpay.wxshop.app.BaseActivity;import qfpay.wxshop.ui.view.FilterView;import qfpay.wxshop.ui.view.FilterViewHis;import qfpay.wxshop.ui.view.Tag;import qfpay.wxshop.utils.MobAgentTools;import qfpay.wxshop.utils.Toaster;import android.app.Activity;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.view.animation.Animation;import android.view.animation.Animation.AnimationListener;import android.view.animation.AnimationUtils;import android.view.inputmethod.InputMethodManager;import android.widget.Button;import android.widget.EditText;import android.widget.TextView;import com.google.gson.Gson;import com.google.gson.reflect.TypeToken;/** * 商品添加完成添加标签页面 * */@EActivity(R.layout.main_label)public class LabelActivity extends BaseActivity {	private static final int maxLabel = 5;	List<LabelBean> ps;	List<LabelBean> hisBean;	LabelBean selected;	@ViewById(R.id.layout_bq_parent)	FilterView allParent;	@ViewById(R.id.layout_bq_parent_history)	FilterViewHis hisParent;	@ViewById	View lyt_about, view_ancor2;	@ViewById	Button btn_close;	@ViewById	View tv_apply;	@ViewById	TextView tv_apply_0, tv_apply_1, tv_apply_2;	@ViewById	View layout_apply;	@ViewById	Button btn_apply;	@ViewById	EditText et_apply;	@Extra	boolean add;	@Click	void tv_apply() {		tv_apply.setVisibility(View.GONE);		layout_apply.setVisibility(View.VISIBLE);		MobAgentTools.OnEventMobOnDiffUser(this, "click_category_wxcust_post");	}//	@Click//	void btn_apply() {//		save2Server();//		hideSoftKeyboard(LabelActivity.this);//	}//	private void save2Server() {////		String apply = et_apply.getText().toString().trim();//		if (apply.equals("")) {//			Toaster.l(LabelActivity.this, "亲，填写一个标签~");//			return;//		}//		AbstractNet net = new LabelApplyNetImpl(LabelActivity.this);//		Bundle bun = new Bundle();//		bun.putString("catename", apply);//		net.request(bun, new MainHandler(LabelActivity.this) {////			@Override//			public void onSuccess(Bundle bundle) {//				// Toaster.l(LabelActivity.this, "喵喵已经收到数据了");//				tv_apply.setVisibility(View.VISIBLE);//				tv_apply_0.setText("喵喵已经收到数据了");//				tv_apply_1.setVisibility(View.GONE);//				tv_apply_2.setVisibility(View.GONE);//				layout_apply.setVisibility(View.GONE);//			}////			@Override//			public void onFailed(Bundle bundle) {////			}//		});//	}	@Click	void btn_close() {		finishAct();		MobAgentTools.OnEventMobOnDiffUser(this, "click_category_exit_post");	}	@Click	void view_ancor2() {		MobAgentTools.OnEventMobOnDiffUser(this, "click_category_blank_post");		finishAct();	}	@ViewById	View layout_show_label;	@Click	void layout_show_label() {		showAll();		MobAgentTools.OnEventMobOnDiffUser(this, "click_category_more_post");	}	private void showAll() {		hisParent.setVisibility(View.GONE);		allParent.setVisibility(View.VISIBLE);		tv_apply.setVisibility(View.VISIBLE);		layout_show_label.setVisibility(View.GONE);		layout_apply.setVisibility(View.GONE);	}		@AfterInject	void initData(){		initJSONARRAY();		initTags();				initHisArrays();		initHisTags();	}		@AfterViews	void init() {				initAllParent();		initChildParent();				if (!add) {			showAll();		}	}	private void initHisArrays() {		hisBean = new ArrayList<LabelBean>();		String string = "";		if (string == null || string.equals("")) {			return;		}		String[] hisStrings = string.split(",");		for (int i = 0; i < hisStrings.length; i++) {			String str1 = hisStrings[i];			LabelBean bean = new LabelBean();			String[] strS = str1.split("_");			bean.setId(strS[0]);			bean.setName(strS[1]);			hisBean.add(bean);		}	}	private void initHisTags() {		hisList = new ArrayList<Tag>();		if (hisBean == null) {			return;		}		for (int i = 0; i < hisBean.size(); i++) {			LabelBean bean = hisBean.get(i);			hisList.add(new Tag(bean.getName(), false));		}	}	private void initAllParent() {		allParent.setTextSize(getResources().getDimensionPixelSize(				R.dimen.tag_text_size));		allParent.setTextColor(getResources().getColor(				R.color.tag_default_color));		allParent.setTextTouchColor(getResources().getColor(R.color.white));		allParent.setHorizontalMargin((int) getResources().getDimension(				R.dimen.tag_horizontalMargin));		allParent.setVerticalMargin((int) getResources().getDimension(				R.dimen.tag_verticalMargin));		allParent.setOnItemClickListener(new FilterView.OnItemClickListener() {			@Override			public void onItemClick(View view, List<Tag> data, int position) {				Tag tag = data.get(position);				if (tag == null) {					Toaster.l(LabelActivity.this, "选择的分类是空的！");					return;				}				onSelected(tag, ps);				MobAgentTools.OnEventMobOnDiffUser(LabelActivity.this,						"click_category_label_post");			}		});		allParent.setData(list);	}	private void initChildParent() {		hisParent.setTextSize(getResources()				.getDimension(R.dimen.tag_text_size));		hisParent.setTextColor(getResources().getColor(				R.color.tag_default_color));		hisParent.setTextTouchColor(getResources().getColor(R.color.white));		hisParent.setHorizontalMargin((int) getResources().getDimension(				R.dimen.tag_horizontalMargin));		hisParent.setVerticalMargin((int) getResources().getDimension(				R.dimen.tag_verticalMargin));		hisParent				.setOnItemClickListener(new FilterViewHis.OnItemClickListener() {					@Override					public void onItemClick(View view, List<Tag> data,							int position) {						Tag tag = data.get(position);						if (tag == null) {							Toaster.l(LabelActivity.this, "选择的分类是空的！");							return;						}						onSelected(tag, hisBean);						MobAgentTools.OnEventMobOnDiffUser(LabelActivity.this,								"click_category_label_post");					}				});		hisParent.setData(hisList);	}	@UiThread(delay = 400)	void onSelected(Tag tag, List<LabelBean> ps) {		String str = tag.getText();		for (int i = 0; i < ps.size(); i++) {			LabelBean lb = ps.get(i);			if (lb.getName().equals(str)) {				selected = lb;				break;			}		}		finishAct();	}	ArrayList<Tag> list;	ArrayList<Tag> hisList;	private void initTags() {		list = new ArrayList<Tag>();		if (ps == null) {			return;		}		for (int i = 0; i < ps.size(); i++) {			LabelBean bean = ps.get(i);			list.add(new Tag(bean.getName(), false));		}	}	private void initJSONARRAY() {//		String labelHis = WxShopApplication.dataEngine.getLabels();		Gson gson = new Gson();//		ps = gson.fromJson(labelHis, new TypeToken<List<LabelBean>>() {//		}.getType());//		if (ps == null || ps.equals("")) {//			Toaster.l(LabelActivity.this, "初始化标签失败~");//		}	}	@Override	public void onBackPressed() {		// TODO Auto-generated method stub		finishAct();	}	private void finishAct() {		if (selected == null) {			closeActAnimation();			return;		}		// 更新历史选择		String tmpstr = selected.getId() + "_" + selected.getName();		String labelHis = "";		labelHis = labelHis.replaceAll(tmpstr, "");		String[] historiese = labelHis.split(",");		StringBuilder builder = new StringBuilder();		int addcount = 1;		builder.append(selected.getId() + "_" + selected.getName() + ",");		for (int i = 0; i < historiese.length; i++) {			if (addcount >= LabelActivity.maxLabel) {				break;			}			if (!historiese[i].equals("")) {				addcount++;				builder.append(historiese[i]).append(",");			}		}		String newString = builder.substring(0, builder.length() - 1);//		WxShopApplication.dataEngine.setHistoryLabels(newString);		closeActAnimation();	}	private void closeActAnimation() {		Animation anima = AnimationUtils				.loadAnimation(this, R.anim.label_close);		lyt_about.startAnimation(anima);		anima.setAnimationListener(new AnimationListener() {			@Override			public void onAnimationStart(Animation arg0) {				// TODO Auto-generated method stub				lyt_about.setVisibility(View.VISIBLE);			}			@Override			public void onAnimationRepeat(Animation arg0) {				// TODO Auto-generated method stub			}			@Override			public void onAnimationEnd(Animation arg0) {				lyt_about.setVisibility(View.GONE);				sendIntent();			}		});	}	public void sendIntent() {	}	@UiThread	void startAnima() {		Animation animation = AnimationUtils.loadAnimation(this,				R.anim.push_down_in);		animation.setAnimationListener(new AnimationListener() {			@Override			public void onAnimationStart(Animation arg0) {				lyt_about.setVisibility(View.VISIBLE);			}			@Override			public void onAnimationRepeat(Animation arg0) {			}			@Override			public void onAnimationEnd(Animation arg0) {			}		});		lyt_about.startAnimation(animation);	}	@Override	public void onWindowFocusChanged(boolean hasFocus) {		// TODO Auto-generated method stub		super.onWindowFocusChanged(hasFocus);		startAnima();	}	public void hideSoftKeyboard(Activity act) {				View view = getWindow().peekDecorView();        if (view != null) {            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);        }//		InputMethodManager manager = (InputMethodManager) act//				.getSystemService(Context.INPUT_METHOD_SERVICE);//		if (manager == null) {//			return;//		}//		if (act.getCurrentFocus() == null) {//			return;//		}//		if (act.getCurrentFocus().getWindowToken() == null) {//			return;//		}//		manager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),//				1);	}}
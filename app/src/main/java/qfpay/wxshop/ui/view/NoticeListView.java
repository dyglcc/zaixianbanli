package qfpay.wxshop.ui.view;

import qfpay.wxshop.R;
import qfpay.wxshop.activity.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class NoticeListView extends ListView {

	private View mFooterMOREView;

	private View mEmptyFotterView;
	private LayoutInflater mInflater;
	private Context mContext;
	public boolean isaddedEmptyFooter = false;


	public NoticeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public NoticeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public NoticeListView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		mInflater = LayoutInflater.from(mContext);
		mFooterMOREView = mInflater.inflate(R.layout.more_view_maijiaxiu, null);

	}


	public View getmFooterView() {
		return mFooterMOREView;
	}

	public void removeFooterView() {
		if (getFooterViewsCount() > 0) {
			removeFooterView(mFooterMOREView);
		}
		removeFooterView(mEmptyFotterView);
	}

	private void addFooter() {

		if (mEmptyFotterView != null) {
			removeFooterView(mEmptyFotterView);
		}

		if (getFooterViewsCount() == 0) {
			addFooterView(mFooterMOREView);
		}
	}

	@SuppressLint("InflateParams")
	private void addEmptyFooterView() {
		if (mInflater == null) {
			mInflater = LayoutInflater.from(mContext);
		}
		mEmptyFotterView = mInflater.inflate(R.layout.main_notice_empty,
				null);
		mEmptyFotterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		this.addFooterView(mEmptyFotterView);
		isaddedEmptyFooter = true;

	}

	private Handler handler;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void removeEmptyfooterView() {
		this.removeFooterView(mEmptyFotterView);
	}

	public void setVisiAble() {
		if (mEmptyFotterView != null) {
			mEmptyFotterView.setVisibility(View.VISIBLE);
		}
	}

	public View getEmptyFooterView() {
		return mEmptyFotterView;
	}

}

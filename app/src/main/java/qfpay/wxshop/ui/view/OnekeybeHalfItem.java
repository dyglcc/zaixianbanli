package qfpay.wxshop.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jiafen.jinniu.com.R;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.beans.OnekeybehalfItemBean;
import qfpay.wxshop.utils.MobAgentTools;

import android.os.Handler;

import com.adhoc.pic.Picasso;


@EViewGroup(R.layout.onkeybehalf_list_item)
public class OnekeybeHalfItem extends LinearLayout {
    private static final int SIZE_MENU_HEIGHT_DP = 82;
    private static final String DATE_FORMATSTR = "MM-dd";

    @ViewById
    ImageView iv_icon, iv_indicator;
    @ViewById
    TextView tv_name, tv_time,
            tv_price,
            tv_commission,
            tv_salesvolume,
            tv_time_text,
            tv_price_text,
            tv_commission_text,
            tv_salesvolume_text;
    @ViewById
    TextView tv_menu_share, tv_menu_preview, tv_menu_offshelf;
    @ViewById
    LinearLayout ll_name, ll_menu;
    @ViewById
    View v_line;
    @ViewById
    ImageView iv_carriage;

    @ViewById
    View ll_menu_share,ll_menu_offshelf,ll_menu_preview;

    private Context context;
    private Handler handler;
    private OnekeybehalfItemBean data;

    public OnekeybeHalfItem(Context context) {
        super(context);
    }

    public OnekeybeHalfItem setData(OnekeybehalfItemBean data,Handler handler,Context context) {
        this.data = data;

        this.context = context;

        this.handler = handler;

        if (data.isMenuOpened) {
            v_line.setVisibility(View.INVISIBLE);
        } else {
            closeMenu();
            v_line.setVisibility(View.VISIBLE);
        }

        tv_name.setText(data.getTitle() + "");
        tv_price.setText(data.getPrice() );
        tv_commission.setText("￥" +data.getCps_value() + "");
        tv_salesvolume.setText( data.getSales() + "");
        tv_time.setText(getDateStr(data.getCps_created()));
        Picasso.with(getContext()).load("url").fit().centerCrop().placeholder(R.drawable.list_item_default).
                error(R.drawable.list_item_default).into(iv_icon);


        settextColor(data.getIs_agent_actived());

        return this;
    }



    private void settextColor(int status) {
        if(0 == status){
            tv_name.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_price.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_commission.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_time.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_salesvolume.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_price_text.setTextColor(getResources().getColor(R.color.text_color_cancel));

            tv_commission_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_time_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            tv_salesvolume_text.setTextColor(getResources().getColor(R.color.text_color_cancel));
            iv_carriage.setVisibility(View.VISIBLE);

            ll_menu_preview.setVisibility(View.GONE);
            ll_menu_share.setVisibility(View.GONE);

        }else{
            tv_name.setTextColor(getResources().getColor(R.color.text_content));
            tv_price.setTextColor(getResources().getColor(R.color.text_content));

            tv_commission.setTextColor(getResources().getColor(R.color.common_text_red));
            tv_time.setTextColor(getResources().getColor(R.color.text_content));
            tv_salesvolume.setTextColor(getResources().getColor(R.color.common_text_red));

            tv_price_text.setTextColor(getResources().getColor(R.color.text_content));

            tv_commission_text.setTextColor(getResources().getColor(R.color.text_content));
            tv_time_text.setTextColor(getResources().getColor(R.color.text_content));
            tv_salesvolume_text.setTextColor(getResources().getColor(R.color.text_content));
            iv_carriage.setVisibility(View.GONE);

            ll_menu_preview.setVisibility(View.VISIBLE);
            ll_menu_share.setVisibility(View.VISIBLE);
        }
    }


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format1 = new SimpleDateFormat(DATE_FORMATSTR);

    private String getDateStr(String cps_created) {

        if (cps_created == null || cps_created.equals("")) {
            return null;
        }
        try {
            Date date = format.parse(cps_created);
            return format1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Click
    void ll_menu_share() {

        Message msg = handler.obtainMessage();
        Bundle bun = new Bundle();
        bun.putSerializable("share",data);
        msg.setData(bun);
        msg.sendToTarget();

        MobAgentTools.OnEventMobOnDiffUser(getContext(), "click_share_distgood");

    }





    @SuppressLint("NewApi")
    private void closeMenu() {
        if (!data.isAni) {
            changeMenuHeight(0);
            iv_indicator.setRotation(0);
            return;
        }


        data.isAni = false;
    }

    private void changeMenuHeight(float height) {
        android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) ll_menu.getLayoutParams();
        lp.height = (int) height;
        ll_menu.requestLayout();
    }

}


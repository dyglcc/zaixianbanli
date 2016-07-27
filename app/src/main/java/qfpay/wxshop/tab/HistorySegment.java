package qfpay.wxshop.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jiafen.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;


public class HistorySegment extends Fragment {

//    https://github.com/Frank-Zhu/AndroidRecyclerViewDemo/blob/master/app/src/main/java/com/frankzhu/recyclerviewdemo/adapter/BaseAbstractRecycleCursorAdapter.java
    ArrayList<HistoryBean> list = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflater1 = (LayoutInflater) WxShopApplication.
                instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater1.inflate(R.layout.fragment_history, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(WxShopApplication.instance));

        return view;
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) WxShopApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item_history, null);
            return new Vholder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(list!=null){
                HistoryBean hb = list.get(position);
                ((Vholder) holder).city.setText(hb.city);
                ((Vholder) holder).time.setText(hb.time +"");
                ((Vholder) holder).city.setText(hb.count+"个");
            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        final class Vholder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView city, time, count;

            public Vholder(View itemView) {
                super(itemView);
                city = (TextView) itemView.findViewById(R.id.city);
                time = (TextView) itemView.findViewById(R.id.tv_time);
                count = (TextView) itemView.findViewById(R.id.count);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    class HistoryBean{
        long time;
        String city;
        int count;
    }
}
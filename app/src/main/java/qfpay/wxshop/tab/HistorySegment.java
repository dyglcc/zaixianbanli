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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jiafen.jinniu.com.Add_history;
import jiafen.jinniu.com.Add_historyDao;
import jiafen.jinniu.com.DaoSession;
import jiafen.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.recylerView.SpaceItemDecoration;


public class HistorySegment extends Fragment {

    //    https://github.com/Frank-Zhu/AndroidRecyclerViewDemo/blob/master/app/src/main/java/com/frankzhu/recyclerviewdemo/adapter/BaseAbstractRecycleCursorAdapter.java
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflater1 = LayoutInflater.from(WxShopApplication.
                instance);
        View view = inflater1.inflate(R.layout.fragment_history, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recyler_space_history);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recyclerView.setLayoutManager(new LinearLayoutManager(WxShopApplication.instance));
        recyclerView.setAdapter(new Myadapter());
        return view;
    }

    private Add_historyDao hisDao;
    List<Add_history> list;
    private LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        DaoSession session = WxShopApplication.instance.getDaoSession();
        hisDao = session.getAdd_historyDao();
        list = hisDao.queryBuilder().orderDesc(Add_historyDao.Properties.Time).list();

        inflater = LayoutInflater.from(WxShopApplication.instance);
        super.onCreate(savedInstanceState);
    }

    class Myadapter extends RecyclerView.Adapter<Myadapter.Vholder> {

        @Override
        public Myadapter.Vholder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Vholder(inflater.inflate(R.layout.list_item_history, null));
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Calendar calendar = Calendar.getInstance();
        @Override
        public void onBindViewHolder(Myadapter.Vholder holder, int position) {
            Add_history ah = list.get(position);
            holder.city.setText(ah.getPlace());
            holder.count.setText(ah.getCount() + "个号码");
            long t = ah.getTime();
            calendar.setTimeInMillis(t);
            holder.time.setText(formatter.format(calendar.getTime()));
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        final class Vholder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView time, count, city;

            public Vholder(View itemView) {
                super(itemView);
                city = (TextView) itemView.findViewById(R.id.city);
                time = (TextView) itemView.findViewById(R.id.tv_time);
                count = (TextView) itemView.findViewById(R.id.tv_count);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
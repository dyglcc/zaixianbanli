package qfpay.wxshop.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhoc.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jiafen.jinniu.com.R;
import qfpay.wxshop.utils.Utils;


public class NumberSegment extends Fragment {

    private String arrayStr = null;
    private String[] data;
    private String[] datacities;
    private HashMap<String, JSONObject> citys = new HashMap<>();
    private MyAdapterCity adapterCity = null;
    private String[] haoduans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_number_segment, null);
        //找到 RecyclerView
        RecyclerView recylcer = (RecyclerView) view.findViewById(R.id.recyclerView);
        //ListView效果的 LinearLayoutManager
//        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());
//        //VERTICAL纵向，类似ListView，HORIZONTAL<span style="font-family: Arial, Helvetica, sans-serif;">横向，类似Gallery</span>
//        mgr.setOrientation(LinearLayoutManager.VERTICAL);
//        recylcer.setLayoutManager(mgr);
//        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 8);
//        recylcer.setLayoutManager(mgr);
        //ListView效果的 LinearLayoutManager
        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 8);
//        mgr.setOrientation(LinearLayoutManager.VERTICAL);
        recylcer.setLayoutManager(mgr);
        //设置适配器
        recylcer.setAdapter(new MyAdapter());


        // init cities
        RecyclerView recylcerCity = (RecyclerView) view.findViewById(R.id.recyclerView_city);
        GridLayoutManager mgrCity = new GridLayoutManager(getActivity(), 8);
        recylcerCity.setLayoutManager(mgrCity);
        //设置适配器
        adapterCity = new MyAdapterCity();
        recylcerCity.setAdapter(adapterCity);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            arrayStr = Utils.inputStreamToString(getActivity(), "area.json");
            JSONArray array = new JSONArray(arrayStr);
            int len = array.length();
            data = new String[len];
            for (int i = 0; i < len; i++) {
                JSONObject province = array.getJSONObject(i);
                Iterator iterator = province.keys();
                String provinceName = (String) iterator.next();
                data[i] = provinceName;
                citys.put(provinceName, province);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        //RecyclerView显示的子View
        //该方法返回是ViewHolder，当有可复用View时，就不再调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_province, null);
            return new ViewHolder(v);
        }

        //将数据绑定到子View，会自动复用View
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.textView.setText(data[i]);
        }

        //RecyclerView显示数据条数
        @Override
        public int getItemCount() {
            return data.length;
        }


        //自定义的ViewHolder,减少findViewById调用次数
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView textView;

            //            ImageView imageView;
            //在布局中找到所含有的UI组件
            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv_content);
//                imageView = (ImageView) itemView.findViewById(R.id.imageView);

                textView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                displayCities(((TextView) v).getText().toString());
            }
        }
    }

    private void displayCities(String province) {
//        try {
//            String content = Utils.inputStreamToString(getActivity(),province);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Json
        JSONObject obj = citys.get(province);
        JSONArray array = obj.optJSONArray(province);
        int len = array.length();
        datacities = new String[len];
        for (int i = 0; i < len; i++) {

            try {
                datacities[i] = array.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapterCity.notifyDataSetChanged();
        }

    }

    class MyAdapterCity extends RecyclerView.Adapter<MyAdapterCity.ViewHolderCity> {

        //RecyclerView显示的子View
        //该方法返回是ViewHolder，当有可复用View时，就不再调用
        @Override
        public ViewHolderCity onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_province, null);
            return new ViewHolderCity(v);
        }

        //将数据绑定到子View，会自动复用View
        @Override
        public void onBindViewHolder(ViewHolderCity viewHolder, int i) {
            viewHolder.textView.setText(datacities[i]);
        }

        //RecyclerView显示数据条数
        @Override
        public int getItemCount() {
            return datacities == null ? 0 : datacities.length;
        }


        //自定义的ViewHolder,减少findViewById调用次数
        class ViewHolderCity extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView textView;

            //            ImageView imageView;
            //在布局中找到所含有的UI组件
            public ViewHolderCity(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv_content);
                textView.setOnClickListener(this);
//                imageView = (ImageView) itemView.findViewById(R.id.imageView);
            }

            @Override
            public void onClick(View v) {

                String city = ((TextView) (v)).getText().toString();
                try {
                    String pinyin_name = Utils.converterToPinyin(city);
                    String strs = Utils.inputStreamToString(getActivity(), pinyin_name+".txt");
                    String[] array = strs.split("\n");
                    T.i("sssssssssssssssssss" + array.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

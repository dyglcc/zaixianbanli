package qfpay.wxshop.tab;

import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adhoc.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import jiafen.jinniu.com.Add_history;
import jiafen.jinniu.com.Add_historyDao;
import jiafen.jinniu.com.DaoSession;
import jiafen.jinniu.com.PhoneNumber;
import jiafen.jinniu.com.PhoneNumberDao;
import jiafen.jinniu.com.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.beans.Tb_contacts;
import qfpay.wxshop.recylerView.SpaceItemDecoration;
import qfpay.wxshop.utils.Utils;


public class NumberSegment extends Fragment {

    private String arrayStr = null;
    private String[] data;
    private String[] datacities;
    private HashMap<String, JSONObject> citys = new HashMap<>();
    private MyAdapterCity adapterCity = null;
    private String[] haoduans;
    private int pos_pro;
    private int pos_city;
    private Button btn_daoru;
    private EditText edt_count;
    private String[] numbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_number_segment, null);
        btn_daoru = (Button) view.findViewById(R.id.btn_daoru);
        edt_count = (EditText) view.findViewById(R.id.edt_count);
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
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recyler_space);
        recylcer.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 6);
//        mgr.setOrientation(LinearLayoutManager.VERTICAL);
        recylcer.setLayoutManager(mgr);

        //设置适配器
        recylcer.setAdapter(new MyAdapter());


        // init cities
        RecyclerView recylcerCity = (RecyclerView) view.findViewById(R.id.recyclerView_city);
        int spacingInPixels_city = getResources().getDimensionPixelSize(R.dimen.recyler_space);
        recylcerCity.addItemDecoration(new SpaceItemDecoration(spacingInPixels_city));
        GridLayoutManager mgrCity = new GridLayoutManager(getActivity(), 4);
        recylcerCity.setLayoutManager(mgrCity);
        //设置适配器
        adapterCity = new MyAdapterCity();
        recylcerCity.setAdapter(adapterCity);
        displayCities(data[0]);


        btn_daoru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countStr = edt_count.getText().toString();
                if (!TextUtils.isEmpty(countStr)) {

                    int count = Integer.parseInt(countStr);
                    addHisHaoduan(count, datacities[pos_city]);
                }
            }
        });

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

        // 初始化数据库
        createDao();
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
            if (pos_pro == i) {
                viewHolder.textView.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
            } else {
                viewHolder.textView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            }
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
                if (pos_pro == getPosition()) {
                    return;
                }
                pos_pro = getPosition();
                pos_city = 0;
                MyAdapter.this.notifyDataSetChanged();
                displayCities(((TextView) v).getText().toString());

            }
        }
    }

    private void displayCities(String province) {

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
            if (i == pos_city) {
                viewHolder.textView.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
            } else {
                viewHolder.textView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            }
        }

        //RecyclerView显示数据条数
        @Override
        public int getItemCount() {
            return datacities == null ? 0 : datacities.length;
        }


        //自定义的ViewHolder,减少findViewById调用次数
        class ViewHolderCity extends RecyclerView.ViewHolder implements View.OnClickListener {

            //            private MyItemClickListener mListener;
            TextView textView;

            public ViewHolderCity(View rootView) {

                super(rootView);
                textView = (TextView) itemView.findViewById(R.id.tv_content);
                textView.setOnClickListener(this);

//                this.mListener = listener;

//                rootView.setOnClickListener(this);


            }

            @Override
            public void onClick(View v) {
                String city = ((TextView) (v)).getText().toString();
                try {
                    String pinyin_name = Utils.converterToPinyin(city);
                    String strs = Utils.readPhoneNumber(getActivity(), pinyin_name + ".txt");
                    numbers = strs.split(",");
                    T.i("sssssssssssssssssss" + numbers.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                if (mListener != null) {
//
//                    mListener.onItemClick(v, getPosition());
//
//                }
                pos_city = getPosition();
                T.i("the position is : " + pos_city);
                adapterCity.notifyDataSetChanged();

            }
        }
    }

    private Add_historyDao hisDao;
    private PhoneNumberDao haoduanDao;

    public void createDao() {

        DaoSession daoSession = ((WxShopApplication) getActivity().getApplication()).getDaoSession();
        hisDao = daoSession.getAdd_historyDao();
        haoduanDao = daoSession.getPhoneNumberDao();


    }

    private void addHisHaoduan(int count, String name) {
        Add_history history = new Add_history(null, count, name, System.currentTimeMillis());
        long id = hisDao.insert(history);
        // create numbers
        Random randome = new Random(numbers.length - 1);
        int x = randome.nextInt();
        String haoduan = numbers[x];
        T.i("号段:" + haoduan);
        ArrayList<Tb_contacts> list = new ArrayList<Tb_contacts>();
        Random r2 = new Random(9999);
        for (int i = 0; i < count; i++) {

            int x1 = r2.nextInt();

            String number = x1 + "";
            int buwei = 4 - number.length();
            for (int b = 0; b < buwei; b++) {
                number = "0" + number;
            }
            String newOne = haoduan + "" + number;
            list.add(new Tb_contacts("加粉" + i, newOne));

        }

        try {
            Utils.BatchAddContact(getActivity(),list);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        addHaoduan(id,list);


    }

    private void addHaoduan(long id,ArrayList<Tb_contacts> list) {

        for(int i=0;i<list.size();i++){
            Tb_contacts tb = list.get(i);
            PhoneNumber haoduan = new PhoneNumber(null,id,Integer.parseInt(tb.getNumber()));
            haoduanDao.insert(haoduan);
        }
    }

}

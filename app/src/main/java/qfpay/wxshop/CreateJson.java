package qfpay.wxshop;

import com.adhoc.http.Callback;
import com.adhoc.http.Request;
import com.adhoc.http.Response;
import com.adhoc.net.AdhocNet;
import com.adhoc.utils.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dongyuangui on 16/7/13.
 */
public class CreateJson {


    public static void main(String[] args){
//        JSONArray array = new JSONArray();
////        JSONObject bj=
//        Province beijingProvince = new Province();
//
//        beijingProvince.setName("北京");
//        City[] city = new City[1];
//        City bjcity = new City();
//        bjcity.setName("北京");
//        // 电信号码
//        String[] dianxins = new String[1];
//
//
//
//        // 联通号码
//        String[] liantongs = new String[1];
//
//        // 移动号码
//        String[] yidongs = new String[1];
//
//        bjcity.setDianxin(dianxins);
//        bjcity.setLiantong(liantongs);
//        bjcity.setYidong(yidongs);
//        city[0] = bjcity;
//        beijingProvince.setCities(city);


        // 获取页面内如


        Request request = new Request.Builder().url("http://www.0597zp.com/city/tianjin/tianjin.php").build();
        AdhocNet.getInstance().enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException, JSONException {
                if(response== null){
                    return;
                }
                if(response.isSuccessful()){
                    String string = response.body().string();
                    System.out.println(string);
                }

            }
        });


    }




}

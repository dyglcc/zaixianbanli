package com.adhoc.net;

import android.content.Context;

import com.adhoc.http.Callback;
import com.adhoc.http.OkHttpClient;
import com.adhoc.http.Request;
import com.adhoc.http.Response;
import com.adhoc.utils.T;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by dongyuangui on 15-5-21.
 */
public class AdhocNet {
    private static AdhocNet ourInstance = null;

    public static AdhocNet getInstance() {

        try {
            if (ourInstance == null) {
                ourInstance = new AdhocNet();
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return ourInstance;
    }

    public OkHttpClient getVolley() {
        return okHttpClient;
    }

    private OkHttpClient okHttpClient;


    private AdhocNet() {
        try {
            okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException {
        try {
            return okHttpClient.newCall(request).execute();
        } catch (Throwable e) {
            T.e(e);
        }
        return null;
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, Callback responseCallback) {
        try {
            okHttpClient.newCall(request).enqueue(responseCallback);
        } catch (Throwable e) {
            T.e(e);
        }
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public void enqueue(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    public String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response == null) return "";
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 这里使用了HttpClinet的API。只是为了方便
     *
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, CHARSET_NAME);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }
}

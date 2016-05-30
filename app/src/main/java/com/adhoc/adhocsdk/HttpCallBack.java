package com.adhoc.adhocsdk;

import org.json.JSONObject;

/**
 * Created by dongyuangui on 16/1/13.
 */
public interface HttpCallBack {

    /**
     * called when the server response was not 2xx or when an exception was thrown in the process
     *                 in case of IO exception this is null
     * @param throwable - contains the exception. in case of server error (4xx, 5xx) this is null
     */
    void onFailure(Throwable throwable);

    /**
     * contains the server response
     * @param response
     */
    void onSuccess(JSONObject response);
}

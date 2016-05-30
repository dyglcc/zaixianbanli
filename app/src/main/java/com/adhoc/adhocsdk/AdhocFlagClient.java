/**
 * AdhocClient is facing our SDK server. It sends analysis information back to SDK server.
 * It is for a general purpose RESTFUL client side implementation using HTTP.
 * TODO(jtx): Make it more secure.
 */

package com.adhoc.adhocsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.adhoc.utils.T;


/**
 * A dedicated client used to send tracking flags data to the server.
 */
public class AdhocFlagClient {
    private static final String TAG = AdhocTracker.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static AdhocFlagClient mAdhocClient = null;
    private Context mContext;
    // The last successful event sent timestamp. We do not need it to be accurate.
    // This is because we don't want to write it into SharedPreference too often.
//    private long mCoarseLastSentTimestamp;
    private long TIME_GAP = 1000 * 3600;  // one hour.
//    private SharedPreferences mSharedPref;
    private ClientImpl net;
    //    private String mCachedInstallReferrer;
    private String mLastResponse = null;

    public AdhocFlagClient(Context context) {
        mContext = context.getApplicationContext();
//        mSharedPref = mContext.getSharedPreferences(SharePrefHandler.SHARED_PREFERENCE, Context.MODE_PRIVATE);
//        mCoarseLastSentTimestamp = mSharedPref.getLong(AdhocConstants.COARSE_LAST_SENT_TIMESTAMP, 0);
//      mCachedInstallReferrer = mSharedPref.getString(AdhocConstants.CACHED_INSTALL_REFERRER, null);
        net = new ClientImpl();
    }

    public String getLastResponse() {
        return mLastResponse;
    }

//    private void WriteCoarseLastSendTimestamp(Long timestamp) {
//        SharedPreferences.Editor editor = mSharedPref.edit();
//        if (timestamp == null) {
//            timestamp = System.currentTimeMillis();
//        }
//        editor.putLong(AdhocConstants.COARSE_LAST_SENT_TIMESTAMP, timestamp);
//        editor.commit();
//        mCoarseLastSentTimestamp = timestamp;
//    }


    private class SendTaskFlag extends AsyncTask<String, Void, String> {

        private OnAdHocReceivedData listener;
        private int timeout;

        public SendTaskFlag(int timeout, OnAdHocReceivedData listener) {
            this.listener = listener;
            this.timeout = timeout;
        }

        @Override
        protected String doInBackground(String... args) {
            return net.send(args[0], args[1], listener, timeout);
        }

        @Override
        protected void onPostExecute(String result) {
            // NOTE: we only keep the last response returned from server.
            // Do not use this for stateless concurrent requests.
            if (result == null) {
                result = ClientImpl.UNKNOWN;
            }
            mLastResponse = result;
        }
    }


    public void sendToServer(final String targetURL, OnAdHocReceivedData listener, int timeout) {
        // Prepare for sending. Create a key-value pair.
        try {
            String parameterString =
                    BuildParameters.getInstance(mContext).buildParametersBasic().toString();

            String networkState = ParameterUtils.getNetworkConnectionState(mContext);
            if (networkState == AdhocConstants.NETWORK_UNCONNECTED) {
                if (DEBUG) {
                    Log.d(TAG, "No network connection.");
                }
                return;

            }

            SendTaskFlag sendTaskFlag = new SendTaskFlag(timeout, listener);
            sendTaskFlag.execute(targetURL, parameterString);
        } catch (Throwable e) {
            T.e(e);
        }


    }

}

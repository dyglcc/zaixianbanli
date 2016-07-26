package qfpay.wxshop.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adhoc.utils.T;
import com.qiniu.upload.tool.AuthException;
import com.qiniu.upload.tool.Mac;
import com.qiniu.upload.tool.PutPolicy;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import jiafen.jinniu.com.R;
import qfpay.wxshop.data.beans.OfficialGoodItemBean;
import qfpay.wxshop.data.beans.SSNItemBean;
import qfpay.wxshop.data.beans.Tb_contacts;
import qfpay.wxshop.dialogs.BaseDialogFragment;
import qfpay.wxshop.dialogs.SimpleDialogFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Utils {

    public final static int CHECK_STATUS = 1;

    public static final int INIT = 1;

    public static String CLOSE_ACTION = "CLOSE";

    public static String keyString = "unKnow";

    public static String macString = "unKnow";

    public static String appIDString = "unKnow";

    public static HashMap<String, String> data;

    public static ProgressDialog progressDialog;

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isCanConnectionNetWork(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.isConnectedOrConnecting() == false) {
            return false;
        }
        return true;
    }

    public static String toMd5(String data) {
        MessageDigest md5;
        byte[] m = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            m = md5.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArray2Hex(m);
    }

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    static final String HEXES = "0123456789ABCDEF";

    private static byte toByte(char c) {
        byte b = (byte) HEXES.indexOf(c);
        return b;
    }

    public static String byteArray2Hex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
                    HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public static byte[] Xor8(byte[] inputData) {
        byte[] outputData = {0, 0, 0, 0, 0, 0, 0, 0};
        int len = inputData.length;
        int iNum = len / 8;
        int i, j;
        if (len <= 9) {
            outputData = inputData.clone();
            return outputData;
        } else {
            for (i = 0; i < iNum; i++) {
                for (j = 0; j < 8; j++) {
                    outputData[j] ^= inputData[i * 8 + j];
                }
            }
        }
        return outputData;
    }

    public static String getDateStringByYMD(int year, int month, int day) {

        StringBuffer sb = new StringBuffer(10);
        sb.append(year).append("-")
                .append((month < 10) ? "0" + month : month + "").append("-")
                .append((day < 10) ? "0" + day : day + "");

        return sb.toString();
    }

    public static String getDotNumber(String numberString) {

        long number = Long.parseLong(numberString);
        boolean isNegative = false;
        if (number < 0) {
            isNegative = true;
            number *= -1;
            numberString = String.valueOf(number);
        }
        if (numberString.length() > 2) {
            String brforeString = numberString.substring(0,
                    numberString.length() - 2);

            String afterString = numberString.substring(
                    numberString.length() - 2, numberString.length());

            numberString = brforeString + "." + afterString;
        } else if (numberString.length() == 2) {
            numberString = "0." + numberString;
        } else if (numberString.length() == 1) {
            numberString = "0.0" + numberString;
        }

        return isNegative ? "-" + numberString : numberString;
    }

    public static String getClientNum(String sn) {
        return frontZeroFill(Integer.parseInt(sn), 6);
    }

    public static String frontZeroFill(int sourceStr, int formatLength) {
        /*
         * 0 指前面补充零 formatLength 字符总长度为 formatLength d 代表为正数。
		 */
        String newString = String.format("%0" + formatLength + "d", sourceStr);
        return newString;
    }

    public static String getLastThreeNumberOfClientSN(String clientSN) {
        String newString = clientSN.substring(clientSN.length() - 3,
                clientSN.length());
        return newString;
    }

    public static String getNetworkInfo(Context context) {
        if (context == null) {
            return "Unknown";
        }

        String typeName = "Unknown";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            typeName = info.getTypeName().toLowerCase();
            if (typeName.equals("wifi")) {
            } else {
                typeName = info.getExtraInfo().toLowerCase();
                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
            }
            if (typeName == null || typeName.equals("")) {
                typeName = "Unknown";
            }

        } catch (Exception e) {
            T.e(e);
        }
        return typeName;
    }

    public static boolean isRightPatternPhone(String str) {
        Pattern pattern = Pattern.compile("^[1,6]+[3,4,5,8,7,0]+\\d{9}");
        return pattern.matcher(str).find();
    }

    public static String cut2moneyAmount(String str) {
        if (str.indexOf(".") != -1) {
            // 小数点两位
            String temStr = str.substring(str.indexOf("."));
            if (temStr.length() > 2) {
                str = str.substring(0, str.indexOf(".") + 3);
            }
        }
        return str;
    }


    /**
     * 卡号前六后四显示
     *
     * @param cardNum
     * @return
     */
    public static String showMaskCardNum(String cardNum) {

        StringBuilder sb = new StringBuilder(20);
        if (cardNum != null && cardNum.length() > 10) {
            String before = cardNum.substring(0, 6);
            String after = cardNum.substring(cardNum.length() - 4);
            sb.append(before);
            for (int i = 0; i < 9; i++) {
                sb.append("*");
            }
            sb.append(after);
            return sb.toString();
        }
        return cardNum;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（重当参:cdc,zds,cds,zdc）
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToPinyin(String chines) {
        StringBuilder pinyinName = new StringBuilder();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    // 取得当前汉字的所有全拼
                    String[] strs = PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat);
                    if (strs != null && strs.length > 0) {

                        pinyinName.append(strs[0]);

                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        return pinyinName.toString();
//        return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));
    }

    public static String inputStreamToString(Context context, String metadataFileName) throws IOException {
        InputStream in = context.getAssets().open(metadataFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String str = "";
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        reader.close();
        in.close();
        return sb.toString();
    }

    public static String readPhoneNumber(Context context, String metadataFileName) throws IOException {
        InputStream in = context.getAssets().open(metadataFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String str = "";
        while ((str = reader.readLine()) != null) {
            sb.append(str).append(",");
        }
        reader.close();
        in.close();
        return sb.toString();
    }
//    http://lichangsong.blog.51cto.com/7997447/1306033
    public static void testDelete(Context context,String name)throws Exception{
        //根据姓名求id
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.RawContacts.Data._ID}, "display_name like '" + name + "%'", null, null);
        while(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            //根据id删除data中的相应数据
            resolver.delete(uri, "display_name like '"+name+"%'", null);
            uri = Uri.parse("content://com.android.contacts/data");
            resolver.delete(uri, "raw_contact_id=?", new String[]{id+""});
        }
    }
    public static void deleteAllContract(Context context) {

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur.moveToNext()) {
            try {
                String lookupKey = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.
                        Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());

                cr.delete(uri, null, null);//删除所有的联系人
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            } finally {
                cur = null;
            }
        }
    }

    //查询所有联系人的姓名，电话，邮箱
    public static void TestContact(Context context, String name) throws Exception {
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()) {
            int contractID = cursor.getInt(0);
            StringBuilder sb = new StringBuilder("contractID=");
            sb.append(contractID);
            // ---------

            // ----------
            uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            boolean needDel = false;
            while (cursor1.moveToNext()) {
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
//                    sb.append(",name=" + data1);
                        if (data1.startsWith(name)) {
                            needDel = true;
                            cursor1.close();
                            break;
                        }

                }
            }
//            if(needDel){
//                String lookupKey = cursor.getString(cursor.getColumnIndex(
//                        ContactsContract.Contacts.LOOKUP_KEY));
//                Uri urid = URIUtil(ContactsContract.
//                        Contacts.CONTENT_LOOKUP_URI, lookupKey);
//                System.out.println("The uri is " + uri.toString());
//                resolver.delete(urid, null, null);//删除所有的联系人
//            }
            cursor1.close();
        }
        cursor.close();
    }

    /**
     * 批量添加通讯录
     *
     * @throws OperationApplicationException
     * @throws RemoteException
     */
    public static void BatchAddContact(Context context, List<Tb_contacts> list)
            throws RemoteException, OperationApplicationException {
//        GlobalConstants.PrintLog_D("[GlobalVariables->]BatchAddContact begin");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = 0;
        for (Tb_contacts contact : list) {
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true).build());

            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(
                            ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
                    .withYieldAllowed(true).build());
            // 添加号码
            ops.add(ContentProviderOperation
                    .newInsert(
                            ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
        }
        if (ops != null) {
            // 真正添加
            ContentProviderResult[] results = context.getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
            // for (ContentProviderResult result : results) {
            // GlobalConstants
            // .PrintLog_D("[GlobalVariables->]BatchAddContact "
            // + result.uri.toString());
            // }
        }
    }

    public static boolean isRightIdNum(String idString) {
        // TODO Auto-generated method stub

        if (idString == null || idString.length() < 15) {
            return false;
        }
        if (idString.length() == 15) {
            String s;
            try {
                s = IdNumberUtil.Convert(idString);
                if (s == null) {
                    return false;
                }
                return IdNumberUtil.getInfof(s);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (idString.length() == 18) {
            return IdNumberUtil.getInfoe(idString);
        } else if (idString.length() != 15 && idString.length() != 18) {
            return false;
        }
        return true;
    }

    /**
     * 校验信用卡和借记卡是否正确
     */
    public static boolean isRightBankCard(String curNum) {
        if (curNum == null) {
            return false;
        }

        int len = curNum.length();
        // 银行卡最少14位
        if (len < 14) {
            return false;
        }

        int weightStart = len % 2 == 0 ? 2 : 1;
        int nextWeight = weightStart;
        int sum = 0;
        int tempSum;
        for (int i = 0; i < len; i++) {
            int charInt = Integer.parseInt(curNum.charAt(i) + "");
            // System.out.println(charInt);
            if (i == 0) {
                tempSum = charInt * weightStart;
            } else {
                nextWeight = nextWeight == 2 ? 1 : 2;
                tempSum = charInt * nextWeight;
            }
            if (tempSum > 9) {
                tempSum = tempSum - 9;
            }
            sum += tempSum;
        }
        return sum % 10 == 0 ? true : false;
    }

    /**
     * 将元为单位的价格转成分为单位的价格
     *
     * @param price
     * @return
     */
    public static int convertToCent(String price) {

        String costPercent = price;
        int amount = 0;
        if (costPercent.indexOf(".") == -1) {

            amount = Integer.parseInt(costPercent) * 100;
        } else {
            // 小数点前的数值
            String beforePoint = costPercent.substring(0,
                    costPercent.indexOf("."));
            amount = Integer.parseInt(beforePoint) * 100;
            String afterPointFirst = null;

            try {
                // 小数点后第一位的数值
                afterPointFirst = costPercent.substring(
                        costPercent.indexOf(".") + 1,
                        costPercent.indexOf(".") + 2);
                amount = amount + Integer.parseInt(afterPointFirst) * 10;
            } catch (Exception e) {
            }

            try {
                // 小数点后第二位的数值
                String afterPointTwice = costPercent.substring(
                        costPercent.indexOf(".") + 2,
                        costPercent.indexOf(".") + 3);
                amount = amount + Integer.parseInt(afterPointTwice);
            } catch (Exception e) {
            }

        }

        return amount;
    }

    // 从share中得到boolean
    public static boolean getValue(SharedPreferences share, String key) {
        return share.getBoolean(key, false);
    }

    // 从share中得到整形数
    public static int getNums(SharedPreferences share, String tradeSuccessCount) {
        return share.getInt(tradeSuccessCount, 0);
    }

    // 保存boolean值
    public static void saveBooleanShareData(SharedPreferences share,
                                            String key, boolean value) {
        Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static String getMd5(String m_szLongID) {
        // TODO Auto-generated method stub
        String m_szUniqueID = "";
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        } // hex string to uppercase
        return m_szUniqueID;
    }

    public static SharedPreferences getMemberShare(Context context) {

        return context.getSharedPreferences(ConstValue.PREFS_MEMBER,
                Context.MODE_PRIVATE);
    }

    /**
     * save share中的String value
     */
    public static void saveStringShareData(SharedPreferences share, String key,
                                           String value) {
        Editor editor = share.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 得到 share中的String value
     */
    public static String getStringShareData(SharedPreferences share, String key) {
        return share.getString(key, "");
    }

    /**
     * 将时间转换为0.0s格式的字符串
     *
     * @param data
     * @return
     */
    public static String convertToSecond(String data) {

        String returnString = null;

        if (data != null && (data.length() > 0)) {

            int length = data.length();

            switch (length) {

                case 1:
                case 2:
                    returnString = "0.0";
                    break;
                case 3:
                    returnString = "0." + data.substring(0, 1);
                    break;
                case 4:
                    returnString = data.substring(0, 1) + "."
                            + data.substring(1, 2);
                    break;
                case 5:
                    returnString = data.substring(0, 2) + "."
                            + data.substring(2, 3);
                    break;

            }
        }

        return returnString;

    }

    public static String getDataFromNative(String fileName, Context context) {

        ByteArrayOutputStream baos = null;

        BufferedInputStream bis = null;

        String returnString = null;
        // 初始化错误代码
        try {

            baos = new ByteArrayOutputStream();
            bis = new BufferedInputStream(context.getAssets().open(fileName));
            int CHUNK_SIZE = 32 * 1024;
            byte[] mFileIOBuffer = new byte[CHUNK_SIZE];
            while (true) {
                int bytesRead = bis.read(mFileIOBuffer);

                if (bytesRead < 0) {
                    break;
                }

                baos.write(mFileIOBuffer, 0, bytesRead);
            }
            byte[] buffer = baos.toByteArray();

            returnString = new String(buffer);

            baos.flush();
            baos.close();
            baos = null;

            bis.close();
            bis = null;

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.flush();
                    baos.close();
                } catch (Exception e) {

                }
            }

            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {

                }
            }

        }

        return returnString;
    }

    private static Dialog dialog = null;
    private static AlertDialog alertDialog = null;

    public static Dialog showDialog(final Context context,
                                    OnClickListener clickRightListener, String titleStr,
                                    String messStr, String leftButtonStr, String rightStr,
                                    boolean cancleAble, boolean isFragment) {
        if (context == null) {
            return null;
        }
        if (dialog != null && dialog.isShowing()) {
            android.app.Activity activity = (android.app.Activity) context;
            if (activity != null && !activity.isFinishing()) {
                // dialog.dismiss();
            }
        }
        View customView = LayoutInflater.from(context).inflate(
                R.layout.dialog_qmm, null);
        TextView tvMessage = (TextView) customView.findViewById(R.id.tv_mess);
        tvMessage.setText(messStr);
        TextView tvTitle = (TextView) customView.findViewById(R.id.tv_title);

        tvTitle.setText(titleStr);
        Button btnLeft = (Button) customView.findViewById(R.id.btn_left);
        btnLeft.setText(leftButtonStr);
        Button btnRight = (Button) customView.findViewById(R.id.btn_right);
        btnRight.setText(rightStr);
        if (rightStr == null || rightStr.equals("")) {
            btnRight.setVisibility(View.GONE);
        }
        if (leftButtonStr == null || leftButtonStr.equals("")) {
            btnLeft.setVisibility(View.GONE);
        }
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.setContentView(customView);
        if (!isFragment) {
            dialog.setCancelable(cancleAble);
        }
        dialog.setCanceledOnTouchOutside(false);
        btnLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (context instanceof TimeBarActivity) {
                // // Toaster.l(context, "isTimebarActivity");
                // TimeBarActivity timeBarAct = (TimeBarActivity) context;
                // // destory window view
                // // destoryWindowView(timeBarAct);
                // createWindowView(timeBarAct);
                // }
                dialog.dismiss();

            }
        });

        btnRight.setOnClickListener(clickRightListener);

        dialog.show();

        return dialog;
    }

    public static DialogFragment showNativeDialog(FragmentActivity context,
                                                  String titleStr, String msgStr, String leftButtonStr,
                                                  String rightStr, boolean cancleAble, int requestCode,
                                                  OnClickListener positiveListener) {
        BaseDialogFragment dialogFragment = (BaseDialogFragment) SimpleDialogFragment
                .createBuilder(context, context.getSupportFragmentManager())
                .setTitle(titleStr).setMessage(msgStr)
                .setNegativeButtonText(leftButtonStr)
                .setPositiveButtonText(rightStr).setCancelable(cancleAble)
                .setRequestCode(requestCode).setPositiveClick(positiveListener)
                .show();
        return dialogFragment;
    }

    public static AlertDialog showDialog(final Context context, String msg,
                                         View view) {
        if (context == null) {
            return null;
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            android.app.Activity activity = (android.app.Activity) context;
            if (activity != null && !activity.isFinishing()) {
                alertDialog.dismiss();
            }
        }
        alertDialog = new AlertDialog.Builder(context).setMessage(msg)
                .setCancelable(false).create();

        alertDialog.show();
        return alertDialog;
    }

    public static Dialog showFragmentDialog(Context context,
                                            OnClickListener clickRightListener, String titleStr,
                                            String messStr, String leftButtonStr, String rightStr) {
        if (context == null) {
            return null;
        }
        if (dialog != null && dialog.isShowing()) {
            android.app.Activity activity = (android.app.Activity) context;
            if (activity != null && !activity.isFinishing()) {
                dialog.dismiss();
            }
        }
        View customView = LayoutInflater.from(context).inflate(
                R.layout.dialog_qmm, null);
        TextView tvMessage = (TextView) customView.findViewById(R.id.tv_mess);
        tvMessage.setText(messStr);
        TextView tvTitle = (TextView) customView.findViewById(R.id.tv_title);

        tvTitle.setText(titleStr);
        Button btnLeft = (Button) customView.findViewById(R.id.btn_left);
        btnLeft.setText(leftButtonStr);
        Button btnRight = (Button) customView.findViewById(R.id.btn_right);
        btnRight.setText(rightStr);
        if (rightStr == null || rightStr.equals("")) {
            btnRight.setVisibility(View.GONE);
        }
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.setContentView(customView);
        btnLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnRight.setOnClickListener(clickRightListener);
        dialog.show();
        return dialog;
    }

    // 获取AppKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    public static void hideSoftKeyboard(Activity act) {
        if (act == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        if (act.getCurrentFocus() == null) {
            return;
        }
        if (act.getCurrentFocus().getWindowToken() == null) {
            return;
        }
        manager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
                0);
    }

    public static void saveClipBoard(Activity act, String str) {
        if (act == null) {
            return;
        }

        if (VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            android.text.ClipboardManager cmb = (android.text.ClipboardManager) act
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            cmb.setText(str);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) act
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("wdAddress", str);
            clipboard.setPrimaryClip(clip);
        }

    }

    public static void setCookies(String url, Context context) {
        if (url == null || url.equals("")) {
            return;
        }
        if (context == null) {
            return;
        }
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        // cookieManager.removeSessionCookie();//移除
        String pre = url.replace("http://", "");
        String domain = pre;
        if (pre.indexOf("/") != -1) {
            domain = pre.substring(0, pre.indexOf("/"));
        }
//		cookieManager.setCookie(domain, cookieString);
        CookieSyncManager.getInstance().sync();
    }

    public static void setCookiesHuoyuan(String url, Context context) {
        if (url == null || url.equals("")) {
            return;
        }
        if (context == null) {
            return;
        }
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//		// cookieManager.removeSessionCookie();//移除
        String pre = url.replace("http://", "");
        String domain = pre;
        if (pre.indexOf("/") != -1) {
            domain = pre.substring(0, pre.indexOf("/"));
        }
        CookieSyncManager.getInstance().sync();
    }
    // public static void setCookiesOrderList(String url, Context context) {
    // 	if(url== null || url.equals("")){
    // 		return;
    // 	}
    // 	if (context == null) {
    // 		return;
    // 	}
    // 	CookieSyncManager.createInstance(context);
    // 	CookieManager cookieManager = CookieManager.getInstance();
    // 	cookieManager.setAcceptCookie(true);
    // 	String cookieString = "sessionid="
    // 			+ WxShopApplication.dataEngine.getcid();
    // 	// cookieManager.removeSessionCookie();//移除
    // 	cookieManager.removeSessionCookie();//移除
    // 	cookieManager.setCookie("wx.qfpay.com", cookieString);
    // 	CookieSyncManager.getInstance().sync();
    // }
    // public static void setCookiesHUOYUAN(String url, Context context) {
    // 	if (context == null) {
    // 		return;
    // 	}
    // 	CookieSyncManager.createInstance(context);
    // 	CookieManager cookieManager = CookieManager.getInstance();
    // 	cookieManager.setAcceptCookie(true);
    // 	String cookieString = "sessionid="
    // 			+ WxShopApplication.dataEngine.getcid() + ";qf_uid="
    // 			+ WxShopApplication.dataEngine.getUserId();
    // 	// cookieManager.removeSessionCookie();//移除
    // 	cookieManager.removeSessionCookie();//移除
    // 	cookieManager.setCookie(url, cookieString);
    // 	CookieSyncManager.getInstance().sync();
    // }

    public static String getChannel(Context context) {
        if (context == null) {
            return "";
        }
        ApplicationInfo info = null;

        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String channel = info.metaData.getString("UMENG_CHANNEL");
        return channel;
    }

    public static String getSSNurl(SSNItemBean gb) {
        return "http://" + "/hmsg/"
                + gb.getId();
    }

    public static String getOfficialDetailUrl(OfficialGoodItemBean gb) {
        return "http://" + "/hmsg/"
                + gb.getId();
    }

    public static String getOSVerison(Context activity) {
        return Build.VERSION.RELEASE;
    }

    public static String getAppVersionString(Context context) {
        if (context == null) {
            return "";
        }
        String str = null;
        try {
            str = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e1) {
            T.e(e1);
        }
        return str;
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    public static String getDeviceID(Context context) {
        if (context == null) {
            return "";
        }
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getDeviceId();
    }

    public static Bitmap toOvalBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getHeight(),
                bitmap.getWidth(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return output;
    }

    public static String getQiniuToken() {
        Mac mac = new Mac(com.qiniu.upload.tool.Config.ACCESS_KEY,
                com.qiniu.upload.tool.Config.SECRET_KEY);
        // 请确保该bucket已经存在
        PutPolicy putPolicy = new PutPolicy(
                com.qiniu.upload.tool.Config.bucketName);
        String uptoken = null;
        try {
            uptoken = putPolicy.token(mac);
        } catch (AuthException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uptoken;
    }

    public static int getSampleSize(BitmapFactory.Options options) {
        return computeSampleSize(options, 1500, 1500 * 1500);
    }

    /**
     * 图片压缩算法
     *
     * @param options        Bitmap.Options
     * @param minSideLength  最小显示区
     * @param maxNumOfPixels 你想要的宽度 * 你想要的高度
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options,
                                               int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 得到图片角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            T.e(e);
        }
        return degree;
    }

    public static String getThumblePic(String url, int size) {
        return url + "?imageView2/1/w/" + size + "/h/" + size;
    }

    public static String getThumblePic(String url, int w, int h) {
        if (w == 0) {
            return url + "?imageView2/1/h/" + h;
        } else if (h == 0) {
            return url + "?imageView2/1/w/" + w;
        } else {
            return url + "?imageView2/1/w/" + w + "/h/" + h;
        }
    }

    public static String getUrlRegex() {
        String regex = "(https?|ftp|mms)://([A-z0-9]+[_-]?[A-z0-9]+.)*[A-z0-9]+-?[A-z0-9]+.[A-z]{2,}(/.*)*/?";
        return regex;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将action转换为String
     */
    public static String actionToString(int action) {
//		switch (action) {
//		case Platform.ACTION_AUTHORIZING:
//			return "认证中";
//		case Platform.ACTION_GETTING_FRIEND_LIST:
//			return "得到朋友列表";
//		case Platform.ACTION_FOLLOWING_USER:
//			return "取粉丝朋友";
//		case Platform.ACTION_SENDING_DIRECT_MESSAGE:
//			return "发送消息";
//		case Platform.ACTION_TIMELINE:
//			return "时间轴";
//		case Platform.ACTION_USER_INFOR:
//			return "用户信息";
//		case Platform.ACTION_SHARE:
//			return "分享";
//		default: {
//			return "未知";
//		}
//		}
        return "";

    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}

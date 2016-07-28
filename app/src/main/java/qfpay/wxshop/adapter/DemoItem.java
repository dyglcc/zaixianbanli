package qfpay.wxshop.adapter;

import android.database.Cursor;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2015/2/25  17:21.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2015/2/25        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class DemoItem {
    private long time;
    private String city;
    private int count;

    public DemoItem() {
    }

    public DemoItem(long time,String city,int count) {
        this.time = time;
        this.city = city;
        this.count = count;
    }

    public static DemoItem fromCursor(Cursor cursor) {
        DemoItem demoItem = new DemoItem();
//        demoItem.id = cursor.getInt(cursor.getColumnIndex(ItemsDataHelper.ItemsDBInfo.ID));
//        demoItem.title = cursor.getString(cursor.getColumnIndex(ItemsDataHelper.ItemsDBInfo.TITLE));
        return demoItem;
    }
}

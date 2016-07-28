package qfpay.wxshop.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jiafen.jinniu.com.R;


/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2015/2/25  18:21.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2015/2/25        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class HistoryAdapter extends BaseAbstractRecycleCursorAdapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;

    public HistoryAdapter(Context context) {
        super(context, null);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        DemoItem item = DemoItem.fromCursor(cursor);
//        ((NormalTextViewHolder) holder).mTextView.setText(item.title);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.list_item_history, parent, false));
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        TextView time;
        TextView count;


        public NormalTextViewHolder(View itemView) {
            super(itemView);
            city = (TextView) itemView.findViewById(R.id.city);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            count = (TextView) itemView.findViewById(R.id.count);
        }
    }

    public DemoItem getItem(){
        DemoItem demoItem = new DemoItem();
//        demoItem.id = cursor.getInt(cursor.getColumnIndex(ItemsDataHelper.ItemsDBInfo.ID));
//        demoItem.title = cursor.getString(cursor.getColumnIndex(ItemsDataHelper.ItemsDBInfo.TITLE));
        return demoItem;
    }

}

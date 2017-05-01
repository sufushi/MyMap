package com.rdc.mymap.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.FriendsListItem;
import com.rdc.mymap.model.UserObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by wsoyz on 2017/3/8.
 */

public class TicketListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor cursor;
    private DataBaseHelper dataBaseHelper;

    public TicketListAdapter(Context context,Cursor cursor) {
        mInflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_ticketlist, null);
        }
        if(cursor.moveToPosition(position)){
            TextView name = (TextView) convertView.findViewById(R.id.tv_context);
            TextView money = (TextView) convertView.findViewById(R.id.tv_money);
            TextView time = (TextView) convertView.findViewById(R.id.tv_time);
            TextView number = (TextView) convertView.findViewById(R.id.tv_number);
            ImageView used = (ImageView) convertView.findViewById(R.id.iv_used);
            name.setText(cursor.getString(cursor.getColumnIndex("busName")));
            money.setText(cursor.getInt(cursor.getColumnIndex("fare"))+"");
            SimpleDateFormat formatWithDate=new SimpleDateFormat("MM-dd HH:mm");
            SimpleDateFormat formatWithOutDate=new SimpleDateFormat("HH:mm");
            Date date = new Date(cursor.getLong(cursor.getColumnIndex("purchaseDate")));
            Date tem = new Date(System.currentTimeMillis());
            tem.setHours(0);
            tem.setMinutes(0);
            if(date.compareTo(tem) > 0){
                time.setText(formatWithDate.format(date));
            }else{
                time.setText(formatWithOutDate.format(date));
            }
            number.setText("X3429"+String.format("%04d",cursor.getInt(cursor.getColumnIndex("busTicketId"))));
            if(cursor.getLong(cursor.getColumnIndex("purchaseDate"))== 0)used.setVisibility(View.INVISIBLE);
            used.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

}

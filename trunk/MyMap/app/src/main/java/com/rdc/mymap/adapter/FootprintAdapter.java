package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by wsoyz on 2017/3/8.
 */

public class FootprintAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> list;

    public FootprintAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context);
        if(list == null) this.list = new ArrayList<String>();
        else this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_footprint, null);
        }
        String name = list.get(position);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_context);
        nameTextView.setText(name);
        return convertView;
    }

}

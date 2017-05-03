package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdc.mymap.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyKeyboardGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Map<String, String>> mMapList = new ArrayList<Map<String, String>>();

    public MyKeyboardGridViewAdapter(Context context, List<Map<String, String>> mapList) {
        this.mContext = context;
        this.mMapList = mapList;
    }

    @Override
    public int getCount() {
        return mMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_keyboard_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.keyButton = (TextView) convertView.findViewById(R.id.tv_key);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.keyButton.setText(mMapList.get(position).get("name"));
        if(position == 9) {
            viewHolder.keyButton.setBackgroundResource(R.drawable.selector_key_delete);
            viewHolder.keyButton.setEnabled(false);
        }
        if(position == 11) {
            viewHolder.keyButton.setBackgroundResource(R.drawable.selector_key_delete);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView keyButton;
    }
}

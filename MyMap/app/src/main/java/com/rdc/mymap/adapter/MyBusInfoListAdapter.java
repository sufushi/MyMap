package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.BusInfo;
import com.rdc.mymap.model.NearbyInfo;

import java.util.List;

public class MyBusInfoListAdapter extends BaseAdapter {

    private List<BusInfo> mBusInfoList;
    private Context mContext;

    public MyBusInfoListAdapter(List<BusInfo> busInfoList, Context context) {
        this.mBusInfoList = busInfoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mBusInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBusInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bus_line_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tv_price);
            viewHolder.direction1 = (TextView) convertView.findViewById(R.id.tv_direction1);
            viewHolder.direction2 = (TextView) convertView.findViewById(R.id.tv_direction2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mBusInfoList.get(position).getBusName());
        viewHolder.price.setText("票价：" + mBusInfoList.get(position).getBusPrice() + "元");
        viewHolder.direction1.setText(mBusInfoList.get(position).getDirection1());
        viewHolder.direction2.setText(mBusInfoList.get(position).getDirection2());
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView price;
        TextView direction1;
        TextView direction2;
    }
}

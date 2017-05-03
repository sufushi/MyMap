package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.BusInfo;
import com.rdc.mymap.model.BusStationInfo;

import java.util.List;

public class MyBusStationListAdapter extends BaseAdapter {

    private List<String> mBusStationList;
    private Context mContext;
    private Boolean isFirst = true;
    private Boolean isLast = true;

    public MyBusStationListAdapter(List<String> busStationList, Context context) {
        this.mBusStationList = busStationList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mBusStationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBusStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bus_stations_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.topLine = (View) convertView.findViewById(R.id.v_top_line);
            viewHolder.bottomLine = (View) convertView.findViewById(R.id.v_bottom_line);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_station_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position == 0 && isFirst) {
            viewHolder.topLine.setVisibility(View.INVISIBLE);
            isFirst = false;
        }
        if(position == mBusStationList.size() - 1 && isLast) {
            viewHolder.bottomLine.setVisibility(View.INVISIBLE);
            isLast = false;
        }
        viewHolder.name.setText(mBusStationList.get(position));
        return convertView;
    }

    class ViewHolder {
        View topLine;
        View bottomLine;
        TextView name;
    }
}

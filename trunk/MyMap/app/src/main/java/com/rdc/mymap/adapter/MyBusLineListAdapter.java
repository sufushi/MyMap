package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.rdc.mymap.R;
import com.rdc.mymap.model.BusLineInfo;
import com.rdc.mymap.model.NearbyInfo;

import java.util.ArrayList;
import java.util.List;

public class MyBusLineListAdapter extends BaseAdapter {

    private List<BusLineInfo> mBusLineInfoList = new ArrayList<BusLineInfo>();
    private Context mContext;

    public MyBusLineListAdapter(List<BusLineInfo> busLineInfoList, Context context) {
        this.mBusLineInfoList = busLineInfoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mBusLineInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBusLineInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_buslines_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.busLines = (TextView) convertView.findViewById(R.id.tv_lines);
            viewHolder.busLineInfo = (TextView) convertView.findViewById(R.id.tv_lines_info);
            viewHolder.station = (TextView) convertView.findViewById(R.id.tv_station);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BusLineInfo busLineInfo = mBusLineInfoList.get(position);
        String busLines = "";
        for(int i = 0; i < busLineInfo.getBusLineList().size(); i ++) {
            if(i == busLineInfo.getBusLineList().size() - 1) {
                busLines += busLineInfo.getBusLineList().get(i);
            } else {
                busLines += busLineInfo.getBusLineList().get(i) + " → ";
            }
        }
        viewHolder.busLines.setText(busLines);
        int hour = busLineInfo.getDuration() / 3600;
        int minute = (busLineInfo.getDuration() - hour * 3600) / 60;
        String hourStr = hour == 0 ? "" : hour + "小时";
        String minuteStr = minute == 0 ? "" : minute + "分";
        viewHolder.busLineInfo.setText(hourStr + minuteStr + "   " + busLineInfo.getStopNum() + "站" + "   " + busLineInfo.getDistance() + "米");
        viewHolder.station.setText(busLineInfo.getDepartureStation());
        return convertView;
    }

    class ViewHolder {
        TextView busLines;
        TextView busLineInfo;
        TextView station;
    }
}

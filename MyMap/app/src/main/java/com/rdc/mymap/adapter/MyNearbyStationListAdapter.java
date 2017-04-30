package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.NearbyInfo;

import java.util.List;

public class MyNearbyStationListAdapter extends BaseAdapter {

    private List<NearbyInfo> mNearbyStationList;
    private Context mContext;

    public MyNearbyStationListAdapter(List<NearbyInfo> nearbyInfoList, Context context) {
        this.mNearbyStationList = nearbyInfoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mNearbyStationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNearbyStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nearby_station_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.stationName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.tv_distance);
            viewHolder.details = (TextView) convertView.findViewById(R.id.tv_details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.stationName.setText(mNearbyStationList.get(position).getName());
        viewHolder.details.setText(mNearbyStationList.get(position).getAddress());
        return convertView;
    }

    class ViewHolder {
        TextView stationName;
        TextView distance;
        TextView details;
    }
}

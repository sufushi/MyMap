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

public class MyNearbyListAdapter extends BaseAdapter {

    private List<NearbyInfo> mFoodInfoList;
    private Context mContext;

    public MyNearbyListAdapter(List<NearbyInfo> nearbyInfoList, Context context) {
        this.mFoodInfoList = nearbyInfoList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mFoodInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFoodInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nearby_info_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.pictureImageView = (ImageView) convertView.findViewById(R.id.iv_picture);
            viewHolder.rankTextView = (TextView) convertView.findViewById(R.id.tv_rank);
            viewHolder.restaurantTextView = (TextView) convertView.findViewById(R.id.tv_restaurant);
            viewHolder.costTextView = (TextView) convertView.findViewById(R.id.tv_cost);
            viewHolder.kindTextView = (TextView) convertView.findViewById(R.id.tv_show_time);
            viewHolder.telephoneTextView = (TextView) convertView.findViewById(R.id.tv_telephone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.pictureImageView.setImageBitmap(mFoodInfoList.get(position).getPicture());
        viewHolder.pictureImageView.setImageResource(R.drawable.pikaqiu);
        viewHolder.rankTextView.setText(mFoodInfoList.get(position).getRank());
        viewHolder.restaurantTextView.setText(mFoodInfoList.get(position).getName());
        viewHolder.costTextView.setText(mFoodInfoList.get(position).getCost() + "");
        viewHolder.kindTextView.setText(mFoodInfoList.get(position).getKind());
        viewHolder.telephoneTextView.setText(mFoodInfoList.get(position).getTelephone());
        return convertView;
    }

    class ViewHolder {
        ImageView pictureImageView;
        TextView rankTextView;
        TextView restaurantTextView;
        TextView costTextView;
        TextView kindTextView;
        TextView telephoneTextView;
    }
}

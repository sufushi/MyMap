package com.rdc.mymap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.MyGridViewItem;

import java.util.List;


public class MyGridViewAdapter extends ArrayAdapter<MyGridViewItem> {

    private List<MyGridViewItem> mList;
    private int mId;
    private Context mContext;

    public MyGridViewAdapter(@NonNull Context context, List<MyGridViewItem> list, int id) {
        super(context, id, list);
        this.mContext = context;
        this.mList = list;
        this.mId = id;
    }

    @Override
    public int getCount() {
        if(mList != null) {
            return mList.size();
        } else {
            return 0;
        }

    }

    @Nullable
    @Override
    public MyGridViewItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyGridViewItem myGridViewItem = getItem(position);
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_iv);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(myGridViewItem.getmImageId());
        viewHolder.textView.setText(myGridViewItem.getmName());
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}

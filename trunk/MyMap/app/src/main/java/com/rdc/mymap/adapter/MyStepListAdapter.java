package com.rdc.mymap.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdc.mymap.R;

import java.util.ArrayList;
import java.util.List;

public class MyStepListAdapter extends BaseAdapter {

    private List<String> mStepList = new ArrayList<String>();
    private Context mContext;

    public MyStepListAdapter(List<String> stepList, Context context) {
        this.mStepList = stepList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mStepList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_walking_steps_listview, null);
            viewHolder.step = (TextView) convertView.findViewById(R.id.tv_step);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.step.setText(mStepList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView step;
    }
}

package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;

import java.util.List;

public class MySearchSuggestionAdapter extends BaseAdapter {

    private List<String> mSearchSuggestionList;
    private Context mContext;

    public MySearchSuggestionAdapter(List<String> searchSuggestionList, Context context) {
        this.mSearchSuggestionList = searchSuggestionList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchSuggestionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchSuggestionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_suggestion_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_target);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mSearchSuggestionList.get(position));
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}

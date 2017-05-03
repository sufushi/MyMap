package com.rdc.mymap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.UserObject;

import java.util.List;


/**
 * Created by wsoyz on 2017/3/8.
 */

public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<UserObject> list;
    private DataBaseHelper dataBaseHelper;

    public MessageListAdapter(Context context, List<UserObject> list) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
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
            convertView = mInflater.inflate(R.layout.item_messagelist, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        ImageView image = (ImageView) convertView.findViewById(R.id.iv_icon);
        TextView unread = (TextView) convertView.findViewById(R.id.tv_unread);

        dataBaseHelper = new DataBaseHelper(context, "Data.db", 1);
        UserObject userObject = list.get(position);
        Bitmap bitmap = dataBaseHelper.getUserPhotoToBitmap(userObject.getUserId());
        if (bitmap != null) image.setImageBitmap(bitmap);
        name.setText(userObject.getUsername());
        int i = dataBaseHelper.getUnReadNumber(userObject.getUserId());
        if (i > 0) {
            unread.setText(i + "");
        } else unread.setVisibility(View.INVISIBLE);

        return convertView;
    }

}

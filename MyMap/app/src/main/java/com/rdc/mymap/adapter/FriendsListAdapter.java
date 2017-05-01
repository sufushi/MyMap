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
import com.rdc.mymap.model.FriendsListItem;
import com.rdc.mymap.model.UserObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


/**
 * Created by wsoyz on 2017/3/8.
 */

public class FriendsListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<FriendsListItem> list;
    private DataBaseHelper dataBaseHelper;

    public FriendsListAdapter(Context context, List<FriendsListItem> list) {
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
            convertView = mInflater.inflate(R.layout.item_friendslist, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView character = (TextView) convertView.findViewById(R.id.tv_character);
        ImageView image = (ImageView) convertView.findViewById(R.id.iv_icon);
        FriendsListItem friendsListItem = list.get(position);
        UserObject userObject = friendsListItem.getUserObject();
        dataBaseHelper = new DataBaseHelper(context,"Data.db",1);
        Bitmap bitmap = dataBaseHelper.getPhotoToBitmap(userObject.getUserId());
        if(bitmap != null)image.setImageBitmap(bitmap);
        else image.setImageResource(R.drawable.pikaqiu);
        if (position == 0) {
            //第一个数据要显示字母和姓名
            character.setVisibility(View.VISIBLE);
            character.setText(friendsListItem.getFc());
            name.setText(userObject.getUsername());
        } else {
            //其他数据判断是否为同个字母，这里使用Ascii码比较大小
            if (friendsListItem.getFc().equals(list.get(position -1 ).getFc())) {
                //后面字母的值等于前面字母的值，不显示字母
                character.setVisibility(View.GONE);
                name.setText(userObject.getUsername());
            } else {
                //后面字母的值大于前面字母的值，需要显示字母
                character.setVisibility(View.VISIBLE);
                character.setText(friendsListItem.getFc());
                name.setText(userObject.getUsername());
            }
        }
        return convertView;
    }

    /**
     * 通过字符查找位置
     *
     * @param s
     * @return
     */
    public int getSelectPosition(String s) {
        for (int i = 0; i < getCount(); i++) {
            String firChar = list.get(i).getFc();
            if (firChar.equals(s)) {
                return i;
            }
        }
        return -1;
    }


}

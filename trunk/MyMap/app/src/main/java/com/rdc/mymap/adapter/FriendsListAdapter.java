package com.rdc.mymap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.model.UserObject;

import java.util.List;


/**
 * Created by wsoyz on 2017/3/8.
 */

public class FriendsListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<UserObject> list;
    public int FRIENDS = 1;
    public int LIST = 2;
    private int i;

    public FriendsListAdapter(Context context, List<UserObject> list, int i) {
        this.i = i;
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
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.list_friend, null);
//        }
//        TextView name = (TextView) convertView.findViewById(R.id.tv_list_friendslist_name);
//        TextView character = (TextView) convertView.findViewById(R.id.tv_list_friendslist_character);
//        ImageView image = (ImageView) convertView.findViewById(R.id.iv_list_friendslist);
//
//        people = list.get(position);
//        if (i == FRIENDS) {
//            if (position == 0) {
//                //第一个数据要显示字母和姓名
//                character.setVisibility(View.VISIBLE);
//                character.setText(people.getCharacter());
//                name.setText(people.getName());
//            } else {
//                //其他数据判断是否为同个字母，这里使用Ascii码比较大小
//                if (people.getCharacter().equals(list.get(position - 1).getCharacter())) {
//                    //后面字母的值等于前面字母的值，不显示字母
//                    character.setVisibility(View.GONE);
//                    name.setText(people.getName());
//                } else {
//                    //后面字母的值大于前面字母的值，需要显示字母
//                    character.setVisibility(View.VISIBLE);
//                    character.setText(people.getCharacter());
//                    name.setText(people.getName());
//                }
//            }
//        } else {
//            character.setVisibility(View.GONE);
//            name.setText(people.getName());
//        }
//        return convertView;
        return null;
    }

    /**
     * 通过字符查找位置
     *
     * @param s
     * @return
     */
    public int getSelectPosition(String s) {
//        for (int i = 0; i < getCount(); i++) {
//            String firChar = list.get(i).getCharacter();
//            if (firChar.equals(s)) {
//                return i;
//            }
//        }
        return -1;
    }
}

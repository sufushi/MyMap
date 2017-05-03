package com.rdc.mymap.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;

import java.util.ArrayList;

/**
 * Created by wsoyz on 2017/5/3.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.BaseViewHolder> {
    private ArrayList<Integer> dataList = new ArrayList<>();
    private DataBaseHelper dataBaseHelper;
    public void replaceAll(Context context,ArrayList<Integer> list) {
        dataBaseHelper = new DataBaseHelper(context,"Data.db",1);
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public CollectionAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OneViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection, parent, false));
    }
    @Override
    public void onBindViewHolder(CollectionAdapter.BaseViewHolder holder, int position) {
        holder.setData(dataList.get(position));
    }
    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }
    public class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
        void setData(Object data) {
        }
    }
    private class OneViewHolder extends BaseViewHolder {
        private ImageView ivImage;
        public OneViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv);
            int width = ((Activity) ivImage.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams params = ivImage.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = width/3;
            params.height =  (int) (200 + Math.random() * 400) ;
            ivImage.setLayoutParams(params);
        }
        @Override
        void setData(Object data) {
            if (data != null) {
                Integer id = (Integer) data;
                ivImage.setImageBitmap(dataBaseHelper.getCollectionPhotoToBitmap(id));
            }
        }
    }
}

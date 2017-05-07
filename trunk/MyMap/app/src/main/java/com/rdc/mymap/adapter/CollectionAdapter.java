package com.rdc.mymap.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private final static String TAG = "CollectionAdapter";
    private ArrayList<String> dataList = new ArrayList<>();
    private MyItemClickListener mItemClickListener;
    public void replaceAll(ArrayList<String> list) {
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
    private class OneViewHolder extends BaseViewHolder implements View.OnClickListener {
        private ImageView ivImage;
        public OneViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv);
            ivImage.setOnClickListener(this);
            int screenwidth = ((Activity) ivImage.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams params = ivImage.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = screenwidth/3;
            params.height =  screenwidth/3;
            ivImage.setLayoutParams(params);
        }
        @Override
        void setData(Object data) {
            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                String path = (String) data;
                Bitmap bitmap = BitmapFactory.decodeFile(path,options);
                if(bitmap != null) {
                    ivImage.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG," click!");
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
     *
     * @param myItemClickListener
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}

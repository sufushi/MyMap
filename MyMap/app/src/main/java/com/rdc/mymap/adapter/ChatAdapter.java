package com.rdc.mymap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.ChatModel;
import com.rdc.mymap.model.ItemModel;
import com.rdc.mymap.view.CircleImageView;

import java.util.ArrayList;

/**
 * Created by WangChang on 2016/4/28.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseAdapter> {

    private ArrayList<ItemModel> dataList = new ArrayList<>();
    private DataBaseHelper dataBaseHelper;
    private MyItemClickListener mItemClickListener;
    public ChatAdapter(Context context){
        dataBaseHelper = new DataBaseHelper(context,"Data.db",1);
    }
    public void replaceAll(ArrayList<ItemModel> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ItemModel> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
            notifyItemRangeInserted(dataList.size(),list.size());
//            notifyItemRangeChanged(dataList.size(),list.size());
        }

    }
    @Override
    public ChatAdapter.BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemModel.CHAT_A:
                return new ChatAViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_a, parent, false));
            case ItemModel.CHAT_B:
                return new ChatBViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_b, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.BaseAdapter holder, int position) {
        holder.setData(dataList.get(position).object);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(Object object) {

        }
    }

    private class ChatAViewHolder extends BaseAdapter implements View.OnClickListener {
        private CircleImageView ic_user;
        private TextView tv;

        public ChatAViewHolder(View view) {
            super(view);
            ic_user = (CircleImageView) itemView.findViewById(R.id.ic_user);
            ic_user.setOnClickListener(this);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
            Bitmap bitmap = dataBaseHelper.getUserPhotoToBitmap(model.getId());
            if(bitmap != null) ic_user.setImageBitmap(bitmap);
            else ic_user.setImageResource(R.drawable.pikaqiu);
            tv.setText(model.getContent());
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    private class ChatBViewHolder extends BaseAdapter  {
        private CircleImageView ic_user;
        private TextView tv;

        public ChatBViewHolder(View view) {
            super(view);
            ic_user = (CircleImageView) itemView.findViewById(R.id.ic_user);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
            Bitmap bitmap = dataBaseHelper.getUserPhotoToBitmap(model.getId());
            if(bitmap != null) ic_user.setImageBitmap(bitmap);
            else ic_user.setImageResource(R.drawable.pikaqiu);
            tv.setText(model.getContent());
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
    public void setItemClickListener(ChatAdapter.MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}

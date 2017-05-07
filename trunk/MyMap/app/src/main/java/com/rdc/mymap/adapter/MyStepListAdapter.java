package com.rdc.mymap.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.model.StepInfo;

import java.util.ArrayList;
import java.util.List;

public class MyStepListAdapter extends BaseAdapter {

    private List<StepInfo> mStepList = new ArrayList<StepInfo>();
    private Context mContext;

    public MyStepListAdapter(List<StepInfo> stepList, Context context) {
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
            viewHolder.direction = (ImageView) convertView.findViewById(R.id.iv_arrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.step.setText(mStepList.get(position).getInstructions());
        Bitmap bitmap;
        int degree = 0;
        if(mStepList.get(position).getDirection() % 90 == 0) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow_up);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.locate);
            degree = -45;
        }
        Matrix matrix = new Matrix();
        int newWidth = 40;
        int newHeight = 40;
        float scaleWidth = ((float)newWidth) / bitmap.getWidth();
        float scaleHeight = ((float)newHeight) / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(mStepList.get(position).getDirection() + degree);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        viewHolder.direction.setImageDrawable(new BitmapDrawable(rotateBitmap));
        return convertView;
    }

    class ViewHolder {
        ImageView direction;
        TextView step;
    }
}

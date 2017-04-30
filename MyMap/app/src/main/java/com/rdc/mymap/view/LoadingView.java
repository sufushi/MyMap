package com.rdc.mymap.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rdc.mymap.R;

public class LoadingView extends RelativeLayout {

    private Context mContext;
    private ImageView mLoadingImageView;
    private TextView mLoadingTextView;

    public LoadingView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_view, null);
        mLoadingImageView = (ImageView) view.findViewById(R.id.iv_loading);
        mLoadingTextView = (TextView) view.findViewById(R.id.tv_loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingImageView.getBackground();
        if(animationDrawable != null) {
            animationDrawable.start();
        }
        addView(view);
    }

}

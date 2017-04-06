package com.rdc.mymap.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends Activity implements View.OnClickListener{

    private ViewPager mViewPager;
    private TextView mBusTextView;
    private TextView mCarTextView;
    private TextView mBicycleTextView;
    private TextView mWalkTextView;
    private TextView mTaxiTextView;
    private List<View> mViewList;
    private int mPreIndex = 0;
    private int mCurIndex = 0;
    private View mBusView;
    private View mCarView;
    private View mBicycleView;
    private View mWalkView;
    private View mTaxiView;
    private ImageView mBackImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route);
        init();
    }

    private void init() {
        initTextView();
        initViewPager();
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewList = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater();
        mBusView = layoutInflater.inflate(R.layout.layout_route_bus, null);
        mCarView = layoutInflater.inflate(R.layout.layout_route_car, null);
        mBicycleView = layoutInflater.inflate(R.layout.layout_route_bicycle, null);
        mWalkView = layoutInflater.inflate(R.layout.layout_route_walk, null);
        mTaxiView = layoutInflater.inflate(R.layout.layout_route_taxi, null);
        mViewList.add(mBusView);
        mViewList.add(mCarView);
        mViewList.add(mBicycleView);
        mViewList.add(mWalkView);
        mViewList.add(mTaxiView);
        mViewPager.setAdapter(new MyViewPagerAdapter(mViewList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initTextView() {
        mBusTextView = (TextView) findViewById(R.id.tv_bus);
        mCarTextView = (TextView) findViewById(R.id.tv_car);
        mBicycleTextView = (TextView) findViewById(R.id.tv_bicycle);
        mWalkTextView = (TextView) findViewById(R.id.tv_walk);
        mTaxiTextView = (TextView) findViewById(R.id.taxi);

        mBusTextView.setOnClickListener(new MyOnClickListener(0));
        mCarTextView.setOnClickListener(new MyOnClickListener(1));
        mBicycleTextView.setOnClickListener(new MyOnClickListener(2));
        mWalkTextView.setOnClickListener(new MyOnClickListener(3));
        mTaxiTextView.setOnClickListener(new MyOnClickListener(4));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            default:
                break;
        }
    }


    private class MyOnClickListener implements View.OnClickListener {

        private int mIndex;

        public MyOnClickListener(int index) {
            this.mIndex = index;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(mIndex);
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mCurIndex = mViewPager.getCurrentItem();
            Log.e("error", mCurIndex + "");
            if(mPreIndex == mCurIndex) {
                return;
            }
            switch (mCurIndex) {
                case 0 :
                    mBusTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 1 :
                    mCarTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 2 :
                    mBicycleTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 3 :
                    mWalkTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 4 :
                    mTaxiTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                default:
                    break;
            }
            switch (mPreIndex) {
                case 0 :
                    mBusTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 1 :
                    mCarTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 2 :
                    mBicycleTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 3 :
                    mWalkTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 4 :
                    mTaxiTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                default:
                    break;
            }
            mPreIndex = mCurIndex;
        }
    }
}

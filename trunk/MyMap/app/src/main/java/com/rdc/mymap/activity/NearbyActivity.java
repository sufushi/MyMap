package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyGridViewAdapter;
import com.rdc.mymap.model.MyGridViewItem;

import java.util.ArrayList;
import java.util.List;

public class NearbyActivity extends Activity implements View.OnClickListener{

    private GridView mGridView;
    private List<MyGridViewItem> mList = new ArrayList<>();

    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nearby);
        init();
    }

    private void init() {
        initData();
        initView();
    }

    private void initData() {

        mList.add(new MyGridViewItem(R.drawable.food, "美食"));
        mList.add(new MyGridViewItem(R.drawable.interest, "景点"));
        mList.add(new MyGridViewItem(R.drawable.hotel, "旅馆"));
        mList.add(new MyGridViewItem(R.drawable.entertainment, "娱乐"));
        mList.add(new MyGridViewItem(R.drawable.movie, "电影"));
        mList.add(new MyGridViewItem(R.drawable.market, "超市"));
        mList.add(new MyGridViewItem(R.drawable.wc, "厕所"));
        mList.add(new MyGridViewItem(R.drawable.bus_station, "公交站"));

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.gv);
        mGridView.setAdapter(new MyGridViewAdapter(this, mList, R.layout.item_gridview));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
}

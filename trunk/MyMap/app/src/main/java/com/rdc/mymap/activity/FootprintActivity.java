package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.ChatAdapter;
import com.rdc.mymap.adapter.FootprintAdapter;
import com.rdc.mymap.database.DataBaseHelper;

import java.util.List;

/**
 * Created by wsoyz on 2017/5/1.
 */

public class FootprintActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "FootprintActivity";
    private ImageView mBackImageView;
    private ListView mListView;
    private List<String> list;
    private FootprintAdapter adapter;
    private DataBaseHelper dataBaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friends_details);
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }
    private void init(){
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_main);
        list = dataBaseHelper.getFootprint();
        if(list != null) {
            adapter = new FootprintAdapter(this,list);
            mListView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}

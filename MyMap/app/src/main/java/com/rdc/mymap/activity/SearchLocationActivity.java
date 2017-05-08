package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MySearchHistoryAdapter;
import com.rdc.mymap.adapter.MySearchSuggestionAdapter;
import com.rdc.mymap.utils.SuggestionSearchUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchLocationActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private ImageView mDeleteImageView;
    private EditText mLocationEditText;
    private ListView mLocationListView;

    private SuggestionSearchUtil mSuggestionSearchUtil;
    private List<String> mSuggestionList = new ArrayList<String>();
    private List<LatLng> mLatLngList = new ArrayList<LatLng>();
    private MySearchSuggestionAdapter mMyaSearchSuggestionAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mLocationListView.setAdapter(mMyaSearchSuggestionAdapter);
                    mMyaSearchSuggestionAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_location);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearchUtil.destroy();
    }

    private void init() {
        mSuggestionSearchUtil = new SuggestionSearchUtil();
        mMyaSearchSuggestionAdapter = new MySearchSuggestionAdapter(mSuggestionList, this);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mDeleteImageView = (ImageView) findViewById(R.id.iv_delete);
        mLocationEditText = (EditText) findViewById(R.id.et_location);
        mLocationListView = (ListView) findViewById(R.id.lv_location);
        mBackImageView.setOnClickListener(this);
        mDeleteImageView.setOnClickListener(this);
        mLocationEditText.addTextChangedListener(new MyTextChangedListener());
        mLocationListView.setAdapter(mMyaSearchSuggestionAdapter);
        mLocationListView.setOnItemClickListener(new MyOnItemClickListener());
    }

    private void onSuggestionSearch() {
        for(SuggestionResult.SuggestionInfo suggestionInfo : mSuggestionSearchUtil.getSuggestionInfoList()) {
            mSuggestionList.add(suggestionInfo.key);
            mLatLngList.add(suggestionInfo.pt);
            Log.e("error", suggestionInfo.key);
        }
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.iv_delete :
                mLocationEditText.setText("");
                break;
            default:
                break;
        }
    }

    private class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mSuggestionSearchUtil.getSuggestionInfoList().clear();
            mSuggestionList.clear();
            mLatLngList.clear();
            mSuggestionSearchUtil.searchSuggestion("广州", s.toString());
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        if(mSuggestionSearchUtil.getSuggestionInfoList().size() > 0) {
                            break;
                        }
                    }
                    onSuggestionSearch();
                }
            }.start();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("Location", mSuggestionList.get(position));
            intent.putExtra("latitude", mLatLngList.get(position).latitude);
            intent.putExtra("longitude", mLatLngList.get(position).longitude);
            SearchLocationActivity.this.setResult(0, intent);
            finish();
        }
    }

}

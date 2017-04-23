package com.rdc.mymap.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.ChatAdapter;
import com.rdc.mymap.model.ChatModel;
import com.rdc.mymap.model.ItemModel;

import java.util.ArrayList;

/**
 * Created by wsoyz on 2017/4/23.
 */

public class ChatActivity extends Activity implements View.OnClickListener {
    private ImageView mBackImageView;
    private TextView mTitleTextView;
    private EditText mMessageEditText;
    private RecyclerView mRecyclerView;
    private TextView mSendTextView;
    private ChatAdapter adapter;
    private String content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        init();
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    private void init(){
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mMessageEditText = (EditText) findViewById(R.id.et_message);
        mSendTextView = (TextView) findViewById(R.id.tv_send);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter = new ChatAdapter());
        initData();
    }
    private void initData() {
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content = s.toString().trim();
            }
        });

        mSendTextView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                ArrayList<ItemModel> data = new ArrayList<>();
                ChatModel model = new ChatModel();
                model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_6667.jpg");
                model.setContent(content);
                data.add(new ItemModel(ItemModel.CHAT_B, model));
                adapter.addAll(data);
                mMessageEditText.setText("");
                hideKeyBorad(mMessageEditText);
            }
        });

    }

    private void hideKeyBorad(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}

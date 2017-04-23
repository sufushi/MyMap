package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.view.SideBar;

/**
 * Created by wsoyz on 2017/4/23.
 */

public class FriendsListActivity extends Activity {
    private TextView mTitleTextView;
    private TextView mTipsTextView;
    private ListView listView;
    private SideBar sb;
    private ImageView mBackIamgeView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendslist);
        init();
    }
    private void init(){
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}

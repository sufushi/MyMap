package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.rdc.mymap.R;
import com.rdc.mymap.view.MyMenu;

public class BicycleActivity extends Activity implements View.OnClickListener{

    private MyMenu mMyMenu;
    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bicycle);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mMyMenu = (MyMenu) findViewById(R.id.my_menu);
        mMyMenu.setOnMenuItemClickListener(new MyMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mMyMenu.setMainMenuBackground(position);
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

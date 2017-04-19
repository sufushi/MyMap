package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.rdc.mymap.R;

/**
 * Created by wsoyz on 2017/4/19.
 */

public class WalletActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
    private void init(){

    }
}

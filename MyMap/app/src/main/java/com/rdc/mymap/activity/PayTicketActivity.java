package com.rdc.mymap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.rdc.mymap.R;
import com.rdc.mymap.presenter.OnPassWordInputFinish;
import com.rdc.mymap.view.PasswordView;

public class PayTicketActivity extends Activity {

    private PasswordView mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_ticket);
        init();
    }

    private void init() {
        mPasswordView = (PasswordView) findViewById(R.id.pv);
        mPasswordView.setOnInputFinish(new OnPassWordInputFinish() {
            @Override
            public void onInputFinish() {
                Log.e("error", mPasswordView.getPassword());
                if(mPasswordView.getPassword().equals("123456")) {
                    PayTicketActivity.this.finish();
                }
            }
        });
    }
}

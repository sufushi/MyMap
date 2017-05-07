package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.presenter.OnPassWordInputFinish;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.PasswordView;
import com.rdc.mymap.view.PayResultAnimation;

public class PayTicketActivity extends Activity {

    private PasswordView mPasswordView;
    private PayResultAnimation mPayResultAnimation;
    private float mPrice;
    private String mBusName;
    private String mResult;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    showResultAnimation();
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
        setContentView(R.layout.activity_pay_ticket);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        mPrice = intent.getFloatExtra("price", 0f);
        mBusName = intent.getStringExtra("bus_name");
        mPasswordView = (PasswordView) findViewById(R.id.pv);
        mPasswordView.setOnInputFinish(new OnPassWordInputFinish() {
            @Override
            public void onInputFinish() {
                Log.e("error", mPasswordView.getPassword());
                if(mPasswordView.getPassword().equals("123456")) {
                    new Thread() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                            String cookie = sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, "");
                            mResult = HttpUtil.buyTicket(mBusName, (int) mPrice * 100, cookie);
                            onPayTicket();
                        }
                    }.start();
                }
            }
        });
    }

    private void onPayTicket() {
        mHandler.sendEmptyMessage(0);
    }

    private void showResultAnimation() {
        if(mResult.equals("")) {
            mPayResultAnimation = (PayResultAnimation) mPasswordView.findViewById(R.id.pra);
            mPayResultAnimation.setResultType(PayResultAnimation.RESULT_WRONG);
            mPayResultAnimation.setVisibility(View.VISIBLE);
            mPayResultAnimation.startAnimator();
            Log.e("error", "network error");
        } else {
            mPayResultAnimation = (PayResultAnimation) mPasswordView.findViewById(R.id.pra);
            mPayResultAnimation.setResultType(PayResultAnimation.RESULT_RIGHT);
            mPayResultAnimation.setVisibility(View.VISIBLE);
            mPayResultAnimation.startAnimator();
            Log.e("error", mResult);
        }
        try {
            Thread.sleep(1000);
            Intent ticketIntent = new Intent(PayTicketActivity.this, TicketListActivity.class);
            startActivity(ticketIntent);
            PayTicketActivity.this.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

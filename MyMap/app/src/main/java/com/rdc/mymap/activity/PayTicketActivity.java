package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Window;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.presenter.OnPassWordInputFinish;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.LoadingDialog;
import com.rdc.mymap.view.PasswordView;
import com.rdc.mymap.view.PayResultAnimation;

public class PayTicketActivity extends Activity {

    private PasswordView mPasswordView;
    private PayResultAnimation mPayResultAnimation;
    private float mPrice;
    private String mBusName;
    private String mResult;
    private LoadingDialog mLoadingDialog;
    private Boolean isPass = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    showResultAnimation();
                    break;
                case 1 :
                    Intent ticketIntent = new Intent(PayTicketActivity.this, TicketListActivity.class);
                    startActivity(ticketIntent);
                    PayTicketActivity.this.finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(mLoadingDialog != null) {
            if(mLoadingDialog.isShowing()) {
                mLoadingDialog.hide();
            }
        }
        if (isPass) {
            mLoadingDialog.setMessage("正在支付").show();
            mHandler.sendEmptyMessageDelayed(1, 2000);
        }
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
                    isPass = true;
                    new Thread() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                            String cookie = sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, "");
                            mResult = HttpUtil.buyTicket(mBusName, (int) mPrice * 100, cookie);
                            onPayTicket();
                        }
                    }.start();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PayTicketActivity.this)
                            .setTitle("提醒")//
                            .setIcon(R.mipmap.ic_launcher)//
                            .setMessage("密码错误")//
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    builder.show();
                }
            }
        });
        mLoadingDialog = new LoadingDialog(this);
    }

    private void onPayTicket() {
        mHandler.sendEmptyMessage(0);
    }

    private void showResultAnimation() {
        Log.e("error", mResult);
        if(mResult.equals("")) {
//            mPayResultAnimation = (PayResultAnimation) mPasswordView.findViewById(R.id.pra);
//            mPayResultAnimation.setResultType(PayResultAnimation.RESULT_WRONG);
//            mPayResultAnimation.setVisibility(View.VISIBLE);
//            mPayResultAnimation.startAnimator();
//            Log.e("error", "network error");

        } else if(mResult.contains("余额不足")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(PayTicketActivity.this)
                    .setTitle("提醒")//
                    .setIcon(R.mipmap.ic_launcher)//
                    .setMessage("余额不足,请充值")//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PayTicketActivity.this, WalletActivity.class);
                            startActivity(intent);
                        }
                    })//
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        } else {
//            mPayResultAnimation = (PayResultAnimation) mPasswordView.findViewById(R.id.pra);
//            mPayResultAnimation.setResultType(PayResultAnimation.RESULT_RIGHT);
//            mPayResultAnimation.setVisibility(View.VISIBLE);
//            mPayResultAnimation.startAnimator();
//            Log.e("error", mResult);
            mLoadingDialog.setMessage("正在支付").show();
            mHandler.sendEmptyMessageDelayed(1, 2000);
        }

//        try {
//            Thread.sleep(1000);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}

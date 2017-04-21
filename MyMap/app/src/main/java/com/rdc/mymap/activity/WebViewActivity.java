package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;

public class WebViewActivity extends Activity {

    private WebView mWebView;
    private TextView mTitle;
    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        init();
    }

    private void init() {
        mWebView =(WebView) findViewById(R.id.wb);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        mTitle.setText(intent.getStringExtra("title"));
        Log.e("error", intent.getStringExtra("detailUrl"));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(intent.getStringExtra("detailUrl"));
        //mWebView.loadUrl("http://www.baidu.com");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    //Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    //startActivity(intent1);
                    return false;
                }
            }
        });
    }
}

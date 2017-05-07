package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.utils.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wsoyz on 2017/4/22.
 */

public class AboutActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "AboutActivity";

    private ImageView mBackImageView;
    private WebView mainWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mainWebView = (WebView) findViewById(R.id.wb_main);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.getSettings().setSupportMultipleWindows(true);
        mainWebView.loadUrl("http://www.walmt.cn/pipixia/html/about.html");
//        Log.d(TAG,mainWebView.getUrl());
        mBackImageView.setOnClickListener(this);
    }
//    {
//        //声明WebSettings子类
//        WebSettings webSettings = mainWebView.getSettings();
//
//        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        webSettings.setJavaScriptEnabled(true);
//
//        //支持插件
//        webSettings.setPluginsEnabled(true);
//
//        //设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//
//        //缩放操作
//        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//
//        //其他细节操作
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
//        webSettings.setAllowFileAccess(true); //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//    }
    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}

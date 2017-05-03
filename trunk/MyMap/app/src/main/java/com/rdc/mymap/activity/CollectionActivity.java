package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;

import java.io.File;

/**
 * Created by wsoyz on 2017/5/3.
 */

public class CollectionActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "CollectionActivity";
    private static final String IMAGE_FILE_NAME = "share.jpg";

    /** 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private ImageView mBackImageView;
    private RecyclerView mainRecyclerView;
    private TextView mShareTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collection);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void init(){
        mShareTextView = (TextView) findViewById(R.id.tv_share);
        mShareTextView.setOnClickListener(this);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mainRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,  StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_share:

                Intent intentFromCapture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment
                        .getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(path, IMAGE_FILE_NAME);
                    if (file.exists()) {
                        file.delete();
                    }
                    intentFromCapture.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(file));
                }

                startActivityForResult(intentFromCapture,
                        CAMERA_REQUEST_CODE);
                break;
            default:
                break;
        }
    }
}

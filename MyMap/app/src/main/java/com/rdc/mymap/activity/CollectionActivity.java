package com.rdc.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.CollectionAdapter;
import com.rdc.mymap.database.DataBaseHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wsoyz on 2017/5/3.
 */

public class CollectionActivity extends Activity implements View.OnClickListener, CollectionAdapter.MyItemClickListener {
    private final static String TAG = "CollectionActivity";
    private static final String IMAGE_FILE_NAME = "share.jpg";

    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;


    private ImageView mBackImageView;
    private RecyclerView mainRecyclerView;
    private TextView mShareTextView;
    private DataBaseHelper dataBaseHelper;
    private CollectionAdapter adapter;
    private ArrayList<String> list;
    private String filePath = null;

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

    private void init() {
        dataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        mShareTextView = (TextView) findViewById(R.id.tv_share);
        mShareTextView.setOnClickListener(this);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mainRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        list = dataBaseHelper.getCollectionPathList();
        adapter = new CollectionAdapter();
        adapter.replaceAll(list);
        adapter.setItemClickListener(this);
        mainRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_share:
                if (requireCamera() && requireStore()) {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                        File destDir = new File(Environment.getExternalStorageDirectory()+"/CollectionPhoto");
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        filePath = new File(destDir, System.currentTimeMillis() + IMAGE_FILE_NAME).getPath();
                        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
                        startActivityForResult(getImageByCamera, CAMERA_REQUEST_CODE);
                    } else {
                        Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && requestCode != RESULT_CANCELED) {
            Log.i(TAG, "已经保存");
            dataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
            dataBaseHelper.saveCollectionPhoto(new File(filePath));
        } else {
            //canceled
        }
        this.refresh();
    }

    private void refresh() {
        list = dataBaseHelper.getCollectionPathList();
        adapter.replaceAll(list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, " chlick!");
        startPhotoActivity(list.get(position));
    }

    private void startPhotoActivity(String path) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("type", PhotoActivity.TYPE_COLLECTION_PHOTO);
        intent.putExtra("path", path);
        startActivity(intent);
    }

    private boolean requireCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                return true;

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                return false;

            }
        } else {
            return true;
        }
    }

    private boolean requireStore() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                return true;
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return false;


            }
        } else {
            return true;
        }
    }
}

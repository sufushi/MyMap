package com.rdc.mymap.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.lbsapi.model.BaiduPanoData;
import com.baidu.lbsapi.panoramaview.PanoramaRequest;
import com.baidu.lbsapi.tools.Point;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.platform.comapi.map.K;
import com.bumptech.glide.Glide;
import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.service.TraceService;
import com.rdc.mymap.utils.PermissionUtil;
import com.rdc.mymap.utils.PoiSearchUtil;
import com.rdc.mymap.view.LoadingDialog;
import com.rdc.mymap.view.MyMenu;
import com.rdc.mymap.view.SatMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements SatMenu.OnSatMenuClickListener, View.OnClickListener, MyMenu.OnMenuItemClickListener {

    private static final String IMAGE_FILE_NAME = "share.jpg";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    private MyMenu mMymenu;
    //private SatMenu mSatMenu;
    private LoadingDialog mLoadingDialog;
    private ImageView mUserImageView;
    private LinearLayout mBottomLinearLayout;
    private ImageView mTrafficModeImageView;
    private ImageView mOverviewModeImageView;
    private ImageView mSettingModeImageView;
    private ImageView mLocateImageView;
    private ImageView mTraceImageView;
    private View mPanoramaView;
    private ImageView mPanoramaImageView;
    private ImageView mScanImageView;
    private EditText mSearchEditText;

    private Boolean isTrafficMode = false;
    private Boolean isSettingMode = false;
    private Boolean isTraceMode = false;
    private Boolean isOverviewMode = false;
    private Boolean isBikeMapMode = false;
    private int mX;
    private int mY;
    private LatLng mPanoramaLatLng;
    private LatLng mResultLatlng;
    private final String mBaseUrl = "http://pcsv1.map.bdimg.com/scape/?qt=pdata&pos=0_0&z=0&sid=";
    private static final int REQUEST_SCAN = 3;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng mCurLatLng;
    private BitmapDescriptor mCurMarker;
    private MapStatusUpdate mMapStatusUpdate;
    private LocationClient mLocationClient;
    private LocationClientOption mLocationClientOption;
    private BDLocationListener mBDLocationListener = new MyLocationListener();
    private PoiSearchUtil mPoiSearchUtil;
    private List<PoiInfo> mPoiInfoList = new ArrayList<PoiInfo>();
    private PopupWindow mPopupWindow;
    private MyOrientationListener mMyOrientationListener = new MyOrientationListener(this);
    private float mCurrentX;
    private String mFilePath;
    private DataBaseHelper mDataBaseHelper;
    private List<LatLng> mLatLngList = new ArrayList<LatLng>();
    private LatLng mBikesLatlng;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    startRouteActivity();
                    break;
                case 1 :
                    showInfoWindow(msg);
                    break;
                case 2 :
                    break;
                case 3 :
                    break;
                default:
                    break;
            }
        }
    };

    private void showInfoWindow(Message msg) {
        mLoadingDialog.hide();
        String url = (String) msg.obj;
        Glide.with(MainActivity.this).load(url).into(mPanoramaImageView);
        LatLng sourceLatLng = new LatLng(mResultLatlng.latitude, mResultLatlng.longitude - 0.0002);
        CoordinateConverter coordinateConverter = new CoordinateConverter();
        coordinateConverter.coord(sourceLatLng);
        coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
        LatLng resultLatLng = coordinateConverter.convert();
        InfoWindow infoWindow = new InfoWindow(mPanoramaView, resultLatLng, -57);
        mBaiduMap.showInfoWindow(infoWindow);
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(resultLatLng, 17.0f);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
        mResultLatlng = resultLatLng;
        Log.e("error", "mResultLatlng=" + mResultLatlng.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= 23) {
            showContacts(mMapView);
        } else {
            init();
        }
    }

    private void showContacts(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermissions(view);
        } else {
            init();
        }
    }

    private void requestContactsPermissions(View view) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    private void init() {

        mMymenu = (MyMenu) findViewById(R.id.my_menu);
        mMymenu.setOnMenuItemClickListener(this);
        mLoadingDialog = new LoadingDialog(this);
//        mSatMenu = (SatMenu) findViewById(R.id.sm);
//        mSatMenu.setOnSatMenuClickListener(this);
        mUserImageView = (ImageView) findViewById(R.id.iv_user);
        mUserImageView.setOnClickListener(this);
        mBottomLinearLayout = (LinearLayout) findViewById(R.id.ll_bottomBar);
        mBottomLinearLayout.setOnClickListener(this);
        mTrafficModeImageView = (ImageView) findViewById(R.id.iv_traffic_mode);
        mTrafficModeImageView.setOnClickListener(this);
        mOverviewModeImageView = (ImageView) findViewById(R.id.iv_overview_mode);
        mOverviewModeImageView.setOnClickListener(this);
        mSettingModeImageView = (ImageView) findViewById(R.id.iv_setting_mode);
        mSettingModeImageView.setOnClickListener(this);
        mLocateImageView = (ImageView) findViewById(R.id.iv_current_location);
        mLocateImageView.setOnClickListener(this);
        mTraceImageView = (ImageView) findViewById(R.id.iv_trace);
        mTraceImageView.setOnClickListener(this);
        mPanoramaView = LayoutInflater.from(this).inflate(R.layout.layout_panorama_overlay, null);
        mPanoramaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPanoramaActivity();
            }
        });
        mPanoramaImageView = (ImageView) mPanoramaView.findViewById(R.id.iv_panorama);
        mScanImageView =(ImageView) findViewById(R.id.iv_scan);
        mScanImageView.setOnClickListener(this);
        mSearchEditText = (EditText) findViewById(R.id.et_search);
        mSearchEditText.setOnClickListener(this);

        mMapView = (MapView) findViewById(R.id.mv);
        int count = mMapView.getChildCount();
        for(int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView || child instanceof K) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mMapView.showScaleControl(false);
        mMapView.getChildAt(2).setVisibility(View.GONE);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mBaiduMap.setOnMapClickListener(new MyOnMapClickListener());

        mPoiSearchUtil = new PoiSearchUtil();

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        initLocationClientOption();
        mLocationClient.start();

        //mMyOrientationListener = new MyOrientationListener(this);
        mMyOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void OnOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initLocationClientOption() {
        mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClientOption.setCoorType("gcj02");
        //mLocationClientOption.setScanSpan(10000);
        mLocationClientOption.setIsNeedAddress(true);
        mLocationClientOption.setOpenGps(true);
        mLocationClientOption.setLocationNotify(true);
        mLocationClientOption.setIsNeedLocationDescribe(true);
        mLocationClientOption.setIsNeedLocationPoiList(true);
        //mLocationClientOption.setIgnoreKillProcess(false);

        mLocationClient.setLocOption(mLocationClientOption);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMyOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMyOrientationListener.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPoiSearchUtil != null) {
            mPoiSearchUtil.destroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1) {
            if(PermissionUtil.verifyPermissions(grantResults)) {
                init();
            } else {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSatMenuClick(View view) {
        switch (view.getId()) {
            case R.id.sm_route :
                mHandler.sendEmptyMessageDelayed(0, 200);
                break;
            case R.id.sm_foot:
//                Intent footIntent = new Intent(this, TraceActivity.class);
//                startActivity(footIntent);
                Intent traceHisory = new Intent(this, TraceHistoryActivity.class);
                startActivity(traceHisory);
                break;
            case R.id.sm_bicycle :
//                Intent bicycleIntent = new Intent(this, BicycleActivity.class);
//                startActivity(bicycleIntent);
                launchCameraActivity();
                break;
            case R.id.sm_weather :
                break;
            default:
                break;
        }
    }

    private void launchCameraActivity() {
        if (requireCamera() && requireStore()) {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                File destDir = new File(Environment.getExternalStorageDirectory()+"/CollectionPhoto");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                mFilePath = new File(destDir, System.currentTimeMillis() + IMAGE_FILE_NAME).getPath();
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mFilePath)));
                startActivityForResult(getImageByCamera, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean requireCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean requireStore() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return false;
            }
        } else {
            return true;
        }
    }

    private void startRouteActivity() {
        Intent intent = new Intent(MainActivity.this, RouteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
//        if(mSatMenu.isExpend()) {
//            mSatMenu.change();
//        }
        if(mMymenu.isExpand()) {
            mMymenu.change();
        }
        switch (v.getId()) {
            case R.id.iv_user :
                Intent personCenterIntent = new Intent(MainActivity.this, PersonCenterActivity.class);
                startActivity(personCenterIntent);
                break;
            case R.id.ll_bottomBar :
                Intent nearbyIntent = new Intent(MainActivity.this, NearbyActivity.class);
                startActivity(nearbyIntent);
            case R.id.iv_traffic_mode :
                isTrafficMode = !isTrafficMode;
                mBaiduMap.setTrafficEnabled(isTrafficMode);
                break;
            case R.id.iv_overview_mode :
                if(!isOverviewMode) {
                    mLoadingDialog.setMessage("正在搜索...").show();
                    searchStreetPanorama();
                    isOverviewMode = true;
                } else {
                    hideInfoWindow();
                    isOverviewMode = false;
                }
                break;
            case R.id.iv_setting_mode :
                showSettingWindow();
                break;
            case R.id.iv_current_location :
                locate();
                break;
            case R.id.iv_trace :

                break;
            case R.id.iv_normal_map :
                if(isSettingMode) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    mBaiduMap.clear();
                    mLatLngList.clear();
                    mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mBikesLatlng, 18.0f);
                    mBaiduMap.animateMapStatus(mMapStatusUpdate);
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.iv_compass_map :
//                if(isSettingMode) {
//                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
//                    mPopupWindow.dismiss();
//                }
                if(isSettingMode) {
                    showBicycleMap();
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.iv_satellite_map :
                if(isSettingMode) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    mBaiduMap.clear();
                    mLatLngList.clear();
                    mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mBikesLatlng, 18.0f);
                    mBaiduMap.animateMapStatus(mMapStatusUpdate);
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.v_close :
                if(isSettingMode) {
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.iv_scan :
                Intent scanIntent = new Intent(this, CaptureActivity.class);
                startActivityForResult(scanIntent, REQUEST_SCAN);
                break;
            case R.id.et_search :
                mHandler.sendEmptyMessageDelayed(0, 100);
                break;
            default:
                break;
        }
    }

    private void hideInfoWindow() {
        mBaiduMap.clear();
    }

    private void showBicycleMap() {
        mLatLngList.clear();
        Random random = new Random();
        double latitude = mBikesLatlng.latitude;
        double longitude = mBikesLatlng.longitude;
        double randomLatitude;
        double randomLongitude;
        int type;
        OverlayOptions overlayOptions;
        BitmapDescriptor bitmapDescriptor;
        LatLng latLng;
        for(int i = 0; i < 20; i ++) {
            randomLatitude = random.nextDouble() / 4000;
            randomLongitude = random.nextDouble() / 4000;
            Log.e("error", "randomLatitude=" + randomLatitude + "randomLongitude=" + randomLongitude);
            switch (random.nextInt(4)) {
                case 0 :
                    latLng = new LatLng(latitude + randomLatitude, longitude + randomLongitude);
                    break;
                case 1 :
                    latLng = new LatLng(latitude - randomLatitude, longitude + randomLongitude);
                    break;
                case 2 :
                    latLng = new LatLng(latitude + randomLatitude, longitude - randomLongitude);
                    break;
                case 3 :
                    latLng = new LatLng(latitude - randomLatitude, longitude - randomLongitude);
                    break;
                default:
                    latLng = new LatLng(latitude + randomLatitude, longitude + randomLongitude);
                    break;
            }
            mLatLngList.add(latLng);
        }
        mBaiduMap.clear();
        for(LatLng ll : mLatLngList) {
            type = random.nextInt(4);
            Log.e("error", "type=" + type);
            switch (type) {
                case 0 :
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ofo_overlay);
                    break;
                case 1 :
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.bluegogo_overlay);
                    break;
                case 2 :
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mobike_overlay);
                    break;
                case 3 :
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ofo_overlay);
                    break;
                default:
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ofo_overlay);
                    break;
            }
            overlayOptions = new MarkerOptions().position(ll).icon(bitmapDescriptor).zIndex(9).draggable(false);
            mBaiduMap.addOverlay(overlayOptions);
        }
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(mBikesLatlng, 22.0f);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SCAN) {
            if(resultCode == RESULT_OK) {
                Intent bicycleIntent = new Intent(this, BicycleActivity.class);
                bicycleIntent.putExtra("dataUrl", data.getStringExtra("dataUrl"));
                startActivity(bicycleIntent);
                //Toast.makeText(this, data.getStringExtra("dataUrl"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && requestCode != RESULT_CANCELED) {

            mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
            mDataBaseHelper.saveCollectionPhoto(new File(mFilePath));
        } else {

        }

    }

    private void showSettingWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_setting, null);
        mPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(contentView);
        ImageView normalMap = (ImageView) contentView.findViewById(R.id.iv_normal_map);
        normalMap.setOnClickListener(this);
        ImageView compassMap = (ImageView) contentView.findViewById(R.id.iv_compass_map);
        compassMap.setOnClickListener(this);
        ImageView satelliteMap = (ImageView) contentView.findViewById(R.id.iv_satellite_map);
        satelliteMap.setOnClickListener(this);
        View closeView = (View) contentView.findViewById(R.id.v_close);
        closeView.setOnClickListener(this);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_walking_route_plan, null);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        isSettingMode = true;
    }

    private void locate() {
        CoordinateConverter coordinateConverter = new CoordinateConverter();
        coordinateConverter.coord(mCurLatLng);
        coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
        LatLng latLng = coordinateConverter.convert();
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    private void searchStreetPanorama() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //mPoiSearchUtil.searchNearby("道路", mCurLatLng, 500);
                mPoiSearchUtil.searchBound("道路", mCurLatLng);
                while(true) {
                    if((mPoiInfoList = mPoiSearchUtil.getPoiInfoList()).size() > 0) {
                        Log.e("error", "size:" + mPoiSearchUtil.getPoiInfoList().size());
                        break;
                    }
                }
                for(PoiInfo poiInfo : mPoiInfoList) {
                    //LatLng latLng = poiInfo.location;
                    mResultLatlng = poiInfo.location;
                    Point sourcePoint = new Point(poiInfo.location.latitude, poiInfo.location.longitude);
                    Point resultPoint = com.baidu.lbsapi.tools.CoordinateConverter.converter(com.baidu.lbsapi.tools.CoordinateConverter.COOR_TYPE.COOR_TYPE_GCJ02, sourcePoint);
                    mPanoramaLatLng = new LatLng(resultPoint.x, resultPoint.y);
                    PanoramaRequest panoramaRequest = PanoramaRequest.getInstance(MainActivity.this);
                    BaiduPanoData baiduPanoData = panoramaRequest.getPanoramaInfoByLatLon(mPanoramaLatLng.longitude, mPanoramaLatLng.latitude);
                    if(baiduPanoData.hasStreetPano()) {
                        String url = mBaseUrl + baiduPanoData.getPid();
                        mX = baiduPanoData.getX();
                        mY = baiduPanoData.getY();
                        //Point point = com.baidu.lbsapi.tools.CoordinateConverter.MCConverter2LL(mX, mY);
                        //mPanoramaLatLng = new LatLng(point.x, point.y);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = url;
                        mHandler.sendMessage(message);
                        Log.e("error", baiduPanoData.getName());
                        break;
                    } else {
                        Log.e("error", "No street");
                    }
                }

            }
        }).start();
    }

    private void startPanoramaActivity() {
        Intent panoramaIntent = new Intent(MainActivity.this, PanoramaActivity.class);
        panoramaIntent.putExtra("x", mX);
        panoramaIntent.putExtra("y", mY);
        panoramaIntent.putExtra("latitude", mResultLatlng.latitude);
        panoramaIntent.putExtra("longitude", mResultLatlng.longitude);
        startActivity(panoramaIntent);
        Log.e("error", mX + "   " +  mY);
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.ll_item1 :
                mHandler.sendEmptyMessageDelayed(0, 200);
                break;
            case R.id.ll_item2 :
                launchCameraActivity();
                break;
            case R.id.ll_item3 :
//                Intent traceHistory = new Intent(this, TraceHistoryActivity.class);
//                startActivity(traceHistory);
                Intent traceIntent = new Intent(this, TraceService.class);
                traceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(isTraceMode == false) {
                    startService(traceIntent);
                    isTraceMode = true;
                    mTraceImageView.setVisibility(View.VISIBLE);
                } else  {
                    stopService(traceIntent);
                    isTraceMode = false;
                    mTraceImageView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation == null) {
                Log.e("error", "nullLocation");
                return;
            }
            mBaiduMap.clear();

            mCurLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude() - 0.0002);
            CoordinateConverter coordinateConverter = new CoordinateConverter();
            coordinateConverter.coord(mCurLatLng);
            coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
            LatLng latLng = coordinateConverter.convert();
            mBikesLatlng = latLng;
            SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
            posSharedPreferences.edit().putFloat("longitude", (float) latLng.longitude)
            .putFloat("latitude", (float) latLng.latitude).commit();
            /*OverlayOptions overlayOptions = new MarkerOptions().position(latLng)//
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))//
                .zIndex(4)//
                .draggable(false);
            mBaiduMap.addOverlay(overlayOptions);*/
            MyLocationData myLocationData = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .direction(mCurrentX)//
                    //.direction(bdLocation.getDirection())//
                    .latitude(latLng.latitude)
                    .longitude(latLng.longitude).build();
            mBaiduMap.setMyLocationData(myLocationData);
            mCurMarker = BitmapDescriptorFactory.fromResource(R.drawable.locate);

            MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, mCurMarker);
            mBaiduMap.setMyLocationConfigeration(myLocationConfiguration);

            mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

        }
    }

    private class MyOnMapClickListener implements BaiduMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng latLng) {
//            if(mSatMenu.isExpend()) {
//                mSatMenu.change();
//            }
            if (mMymenu.isExpand()) {
                mMymenu.change();
            }
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    }

    public static class MyOrientationListener implements SensorEventListener {

        private SensorManager mSensorManager;
        private Sensor mSensor;
        private Context mContext;
        private float mLastX;
        private OnOrientationListener mOnOrientationListener;

        public MyOrientationListener(Context context) {
            this.mContext = context;
        }

        public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
            this.mOnOrientationListener = onOrientationListener;
        }

        public void start() {
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            if(mSensorManager != null) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            }
            if(mSensor != null) {
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }

        public void stop() {
            mSensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float x = event.values[SensorManager.DATA_X];
                if(Math.abs(x - mLastX) > 1.0) {
                    mOnOrientationListener.OnOrientationChanged(x);
                }
                mLastX = x;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public interface OnOrientationListener {
            void OnOrientationChanged(float x);
        }
    }
 }

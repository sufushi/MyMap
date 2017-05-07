package com.rdc.mymap.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.AroundSearchResponse;
import com.baidu.trace.api.entity.BoundSearchResponse;
import com.baidu.trace.api.entity.DeleteEntityResponse;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.rdc.mymap.R;
import com.rdc.mymap.model.CurrentLocation;
import com.rdc.mymap.utils.NetUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceActivity extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus mMapStatus;
    private MapStatusUpdate mMapStatusUpdate;
    private BitmapDescriptor mBitmapDescriptor;
    private Overlay mOverlay;
    private Marker mMoveMarker;
    private LatLng mLastPoint;
    private List<LatLng> mPointList = new ArrayList<LatLng>();
    private PolylineOptions mPolylineOptions;
    private int mScreenWidth;
    private int mScreenHeight;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private Trace mTrace;
    private LBSTraceClient mLBSTraceClient;
    private long mServiceId = 139946;
    private String mEntityName;
    private LocRequest mLocRequest;
    private boolean mIsNeedObjectStorage = false;
    private int mGatherInteral = 3;
    private int mPackInterval = 10;
    private int mLocationInterval = 10;
    private int mTraceType = 2;
    private OnEntityListener mOnEntityListener;
    private OnTraceListener mOnTraceListener;
    private OnTrackListener mOnTrackListener;

    private RealTimeHandler mRealTimeHandler = new RealTimeHandler();
    private RealTimeLocRunnable mRealTimeLocRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getScreenSize();
        setContentView(R.layout.activity_trace);
        init();
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        Log.e("error", "screenWidth=" + mScreenWidth + "screenHeight=" + mScreenHeight);
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.mv);
        for(int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mBaiduMap = mMapView.getMap();
        mEntityName = ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        mLocRequest = new LocRequest(mServiceId);
        mTrace = new Trace(mServiceId, mEntityName, mIsNeedObjectStorage);
        mLBSTraceClient = new LBSTraceClient(getApplicationContext());
        mLBSTraceClient.setInterval(mGatherInteral, mPackInterval);
        initListener();
        startRealTimeLoc(mLocationInterval);
        mLBSTraceClient.startTrace(mTrace, mOnTraceListener);
        mLBSTraceClient.startGather(mOnTraceListener);
    }

    private void startRealTimeLoc(int locationInterval) {
        mRealTimeLocRunnable = new RealTimeLocRunnable(locationInterval);
        mRealTimeHandler.post(mRealTimeLocRunnable);
    }

    private void stopRealTimeLoc(int interval) {
        if(mRealTimeLocRunnable != null && mRealTimeHandler != null) {
            mRealTimeHandler.removeCallbacks(mRealTimeLocRunnable);
            mRealTimeLocRunnable = null;
        }
    }

    private void initListener() {
        mOnEntityListener = new OnEntityListener() {
            @Override
            public void onAddEntityCallback(AddEntityResponse addEntityResponse) {
                Log.e("error", "onAddEntityCallback");
                super.onAddEntityCallback(addEntityResponse);
            }

            @Override
            public void onUpdateEntityCallback(UpdateEntityResponse updateEntityResponse) {
                super.onUpdateEntityCallback(updateEntityResponse);
                Log.e("error", "onUpdateEntityCallback");
            }

            @Override
            public void onDeleteEntityCallback(DeleteEntityResponse deleteEntityResponse) {
                super.onDeleteEntityCallback(deleteEntityResponse);
                Log.e("error", "onDeleteEntityCallback");
            }

            @Override
            public void onEntityListCallback(EntityListResponse entityListResponse) {
                super.onEntityListCallback(entityListResponse);
                Log.e("error", "onEntityListCallback");
            }

            @Override
            public void onSearchEntityCallback(SearchResponse searchResponse) {
                super.onSearchEntityCallback(searchResponse);
                Log.e("error", "onSearchEntityCallback");
            }

            @Override
            public void onBoundSearchCallback(BoundSearchResponse boundSearchResponse) {
                super.onBoundSearchCallback(boundSearchResponse);
                Log.e("error", "onBoundSearchCallback");
            }

            @Override
            public void onAroundSearchCallback(AroundSearchResponse aroundSearchResponse) {
                super.onAroundSearchCallback(aroundSearchResponse);
                Log.e("error", "onAroundSearchCallback");
            }

            @Override
            public void onReceiveLocation(TraceLocation traceLocation) {
                Log.e("error", "onReceiveLocation");
                Log.e("error", "traceLocation=" + traceLocation);
                if(StatusCodes.SUCCESS != traceLocation.getStatus() || isZeroPoint(traceLocation.getLatitude(), traceLocation.getLongitude())) {
                    return;
                }
                LatLng currentLatlng = convertTraceLocation2Map(traceLocation);
                if(currentLatlng == null) {
                    return;
                }
                CurrentLocation.locTime = toTimeStamp(traceLocation.getTime());
                CurrentLocation.latitude = currentLatlng.latitude;
                CurrentLocation.longitude = currentLatlng.longitude;
                updateStatus(currentLatlng, true);
            }
        };
        mOnTraceListener = new OnTraceListener() {
            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.e("error", "onStartTraceCallback");
            }

            @Override
            public void onStopTraceCallback(int i, String s) {
                Log.e("error", "onStopTraceCallback");
            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Log.e("error", "onStartGatherCallback");
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                Log.e("error", "onStopGatherCallback");
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {
                Log.e("error", "onPushCallback");
            }
        };
        mOnTrackListener = new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                super.onHistoryTrackCallback(historyTrackResponse);
            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                super.onDistanceCallback(distanceResponse);
            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                Log.e("error", "onLatestPointCallback");
                if(StatusCodes.SUCCESS != latestPointResponse.getStatus()) {
                    Log.e("error", "StatusCodes Failed");
                    return;
                }
                LatestPoint latestPoint = latestPointResponse.getLatestPoint();
                if(latestPoint == null || isZeroPoint(latestPoint.getLocation().getLatitude(), latestPoint.getLocation().getLongitude())) {
                    Log.e("error", "latestPoint is null");
                    return;
                }
                LatLng currentLatlng = convertTrace2Map(latestPoint.getLocation());
                if (currentLatlng == null) {
                    Log.e("error", "currentLatlng is null");
                    return;
                }
                CurrentLocation.locTime = latestPoint.getLocTime();
                CurrentLocation.latitude = currentLatlng.latitude;
                CurrentLocation.longitude = currentLatlng.longitude;
                updateStatus(currentLatlng, true);
                Log.e("error", currentLatlng.toString());
                mPointList.add(currentLatlng);
                drawRealTimePoint(currentLatlng);
                Toast.makeText(TraceActivity.this, currentLatlng.toString(), Toast.LENGTH_LONG).show();
            }


            @Override
            public void onQueryCacheTrackCallback(QueryCacheTrackResponse queryCacheTrackResponse) {
                super.onQueryCacheTrackCallback(queryCacheTrackResponse);
            }

            @Override
            public void onClearCacheTrackCallback(ClearCacheTrackResponse clearCacheTrackResponse) {
                super.onClearCacheTrackCallback(clearCacheTrackResponse);
            }
        };
    }

    private void drawRealTimePoint(LatLng currentLatlng) {
        if(mPointList.size() >= 2 && mPointList.size() <= 1000) {
            mPolylineOptions = new PolylineOptions().width(10).color(Color.RED).points(mPointList);
            mBaiduMap.addOverlay(mPolylineOptions);
        }
    }

    private long toTimeStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime() / 1000;
    }

    private void updateStatus(LatLng currentLatlng, boolean showMarker) {
        if(mBaiduMap == null || currentLatlng == null) {
            return;
        }
        if(mBaiduMap.getProjection() != null) {
            Point screenPoint = mBaiduMap.getProjection().toScreenLocation(currentLatlng);
            if(screenPoint.y < 200 || screenPoint.y > mScreenHeight - 500 || screenPoint.x < 200
                    || screenPoint.x > mScreenWidth - 200 || mMapStatus == null) {
                animateMapStatus(currentLatlng, 15.0f);
            }
        } else if(mMapStatus == null) {
            setMapStatus(currentLatlng, 15.0f);
        }
        if(showMarker) {
            addMarker(currentLatlng);
        }
    }

    private void addMarker(LatLng currentLatlng) {
        if(mMoveMarker == null) {
            mMoveMarker = addOverlay(currentLatlng, BitmapDescriptorFactory.fromResource(R.drawable.locate), null);
            return;
        }
        if(mLastPoint != null) {
            moveLooper(currentLatlng);
        } else {
            mLastPoint = currentLatlng;
            mMoveMarker.setPosition(currentLatlng);
        }
    }

    private void moveLooper(LatLng endLatlng) {
        mMoveMarker.setPosition(endLatlng);
        mMoveMarker.setRotate((float) getAngle(mLastPoint, endLatlng));
        double slope = getSlope(mLastPoint, endLatlng);
        boolean isReverse = (mLastPoint.latitude > endLatlng.latitude);
        double intercept = getInterception(slope, mLastPoint);
        double xMoveDistance = isReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);
        for (double latitude = mLastPoint.latitude; latitude > endLatlng.latitude == isReverse; latitude =
                latitude - xMoveDistance) {
            LatLng latLng;
            if (slope != Double.MAX_VALUE) {
                latLng = new LatLng(latitude, (latitude - intercept) / slope);
            } else {
                latLng = new LatLng(latitude, mLastPoint.longitude);
            }
            mMoveMarker.setPosition(latLng);
        }
    }

    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return 0.001;
        }
        return Math.abs((0.001 * slope) / Math.sqrt(1 + slope * slope));
    }

    private double getInterception(double slope, LatLng lastPoint) {
        return lastPoint.latitude - slope * lastPoint.longitude;
    }

    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        return 180 * (radio / Math.PI) + deltAngle - 90;
    }

    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        return (toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude);
    }

    private Marker addOverlay(LatLng currentLatlng, BitmapDescriptor bitmapDescriptor, Bundle bundle) {
        OverlayOptions overlayOptions = new MarkerOptions().position(currentLatlng).icon(bitmapDescriptor).zIndex(9).draggable(true);
        Marker marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
        if(bundle != null) {
            marker.setExtraInfo(bundle);
        }
        return marker;
    }

    private void setMapStatus(LatLng currentLatlng, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mMapStatus = builder.target(currentLatlng).zoom(zoom).build();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }

    private void animateMapStatus(LatLng currentLatlng, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mMapStatus = builder.target(currentLatlng).zoom(zoom).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }

    private LatLng convertTrace2Map(com.baidu.trace.model.LatLng location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    private LatLng convertTraceLocation2Map(TraceLocation location) {
        if(location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            return null;
        }
        LatLng currentLatLng = new LatLng(latitude, longitude);
        if (CoordType.wgs84 == location.getCoordType()) {
            LatLng sourceLatLng = currentLatLng;
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            currentLatLng = converter.convert();
        }
        return currentLatLng;
    }

    private boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    private boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    private void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        if (NetUtil.isNetworkAvailable(getApplicationContext())) {
            LatestPointRequest request = new LatestPointRequest(mSequenceGenerator.incrementAndGet(), mServiceId, mEntityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mLBSTraceClient.queryLatestPoint(request, trackListener);
            Log.e("error", "mLBSTraceClient queryLatestPoint");
        } else {
            mLBSTraceClient.queryRealTimeLoc(mLocRequest, entityListener);
            Log.e("error", "mLBSTraceClient queryRealTimeLoc");
        }
    }

    private class RealTimeLocRunnable implements Runnable {

        private int mInterval = 0;

        public RealTimeLocRunnable(int interval) {
            this.mInterval = interval;
        }

        @Override
        public void run() {
            getCurrentLocation(mOnEntityListener, mOnTrackListener);
            mRealTimeHandler.postDelayed(this, mInterval * 1000);
        }

    }

    private class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

}

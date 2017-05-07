package com.rdc.mymap.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.api.track.SortType;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.rdc.mymap.R;
import com.rdc.mymap.application.PanoramaDemoApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TraceHistoryActivity extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus mMapStatus;
    private List<LatLng> mPointList = new ArrayList<LatLng>();
    private Overlay mOverlay;
    private SortType mSortType = SortType.asc;

    private static final long mServiceId = 139946;
    private int mTag;
    private String mEntityName;
    private long mStartTime;
    private long mEndTime;
    private HistoryTrackRequest mHistoryTrackRequest;
    private LBSTraceClient mLBSTraceClient;
    private OnTrackListener mOnTrackListener;
    private PanoramaDemoApplication mPanoramaDemoApplication;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trace_history);
        init();
    }

    private void init() {
        mPanoramaDemoApplication = (PanoramaDemoApplication) getApplicationContext();
        //mLBSTraceClient = new LBSTraceClient(getApplicationContext());
        mLBSTraceClient = mPanoramaDemoApplication.lbsTraceClient;
        mOnTrackListener = new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                int total = historyTrackResponse.getTotal();
                if (StatusCodes.SUCCESS != historyTrackResponse.getStatus()) {
                    Log.e("error", historyTrackResponse.getMessage());
                } else if (total == 0) {
                    Log.e("error", "total is 0");
                } else {
                    Log.e("error", "entityName=" + historyTrackResponse.getEntityName() +
                            "distance=" + historyTrackResponse.getDistance());
                    List<TrackPoint> trackPointList = historyTrackResponse.getTrackPoints();
                    if (trackPointList != null) {
                        for (TrackPoint trackPoint : trackPointList) {
                            if(!isZeroPoint(trackPoint.getLocation().getLatitude(), trackPoint.getLocation().getLongitude())) {
                                mPointList.add(convertTrace2Map(trackPoint.getLocation()));
                                Log.e("error", convertTrace2Map(trackPoint.getLocation()).toString());
                            }
                        }
                    }
                }
                drawHistoryTrack(mPointList, mSortType);

            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                super.onDistanceCallback(distanceResponse);
            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                super.onLatestPointCallback(latestPointResponse);
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
        mMapView = (MapView) findViewById(R.id.mv);
        for(int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mBaiduMap = mMapView.getMap();

        mTag = mSequenceGenerator.incrementAndGet();
        mEntityName = ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        mStartTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;
        mEndTime = System.currentTimeMillis() / 1000;
        mHistoryTrackRequest = new HistoryTrackRequest(mTag, mServiceId, mEntityName);
        mHistoryTrackRequest.setStartTime(mStartTime);
        mHistoryTrackRequest.setEndTime(mEndTime);
        mHistoryTrackRequest.setProcessed(true);
        ProcessOption processOption = new ProcessOption();
        processOption.setNeedDenoise(true);
        processOption.setNeedMapMatch(true);
        processOption.setNeedVacuate(true);
        processOption.setRadiusThreshold(100);
        processOption.setTransportMode(TransportMode.walking);
        mHistoryTrackRequest.setProcessOption(processOption);
        mLBSTraceClient.queryHistoryTrack(mHistoryTrackRequest, mOnTrackListener);
    }

    private void drawHistoryTrack(List<LatLng> pointList, SortType sortType) {
        mBaiduMap.clear();
        if (pointList == null || pointList.size() == 0) {
            if(mOverlay != null) {
                mOverlay.remove();
                mOverlay = null;
            }
            return;
        }
        if(pointList.size() == 1) {
            OverlayOptions startOptions = new MarkerOptions().position(pointList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)).zIndex(9).draggable(true);
            mBaiduMap.addOverlay(startOptions);
            animateMapStatus(pointList.get(0), 18.0f);
            return;
        }

        LatLng startPoint = pointList.get(0);
        LatLng endPoint = pointList.get(pointList.size() - 1);
        OverlayOptions startOptions = new MarkerOptions().position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_point)).zIndex(9).draggable(true);
        OverlayOptions endOptions = new MarkerOptions().position(endPoint).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point)).zIndex(9).draggable(true);
        OverlayOptions polylineOptions = new PolylineOptions().width(10).color(Color.BLUE).points(pointList);
        mBaiduMap.addOverlay(startOptions);
        mBaiduMap.addOverlay(endOptions);
        mOverlay = mBaiduMap.addOverlay(polylineOptions);
        animateMapStatus(pointList);
    }

    private void animateMapStatus(List<LatLng> pointList) {
        if(pointList == null || pointList.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng point : pointList) {
            builder.include(point);
        }
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }

    private void animateMapStatus(LatLng latLng, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mMapStatus = builder.target(latLng).zoom(zoom).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
    }

    private boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    private boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    private LatLng convertTrace2Map(com.baidu.trace.model.LatLng location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

}

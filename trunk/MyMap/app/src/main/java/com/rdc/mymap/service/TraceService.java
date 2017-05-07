package com.rdc.mymap.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
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
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.rdc.mymap.model.CurrentLocation;
import com.rdc.mymap.utils.NetUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class TraceService extends Service {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private LocRequest mLocRequest;

    private static final long mServiceId = 139946;
    private String mEntityName;
    private boolean mIsNeedObjectStorage = false;
    private int mGatherInterval = 3;
    private int mPackInterval = 10;
    private int mLocationInterval = 10;

    private Trace mTrace;
    private LBSTraceClient mLBSTraceClient;
    private OnEntityListener mOnEntityListener;
    private OnTraceListener mOnTraceListener;
    private OnTrackListener mOnTrackListener;

    private RealTimeHandler mRealTimeHandler = new RealTimeHandler();
    private RealTimeLocRunnable mRealTimeLocRunnable;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("error", "TraceService onCreate");
        mLocRequest = new LocRequest(mServiceId);
        mEntityName = ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        mTrace = new Trace(mServiceId, mEntityName, mIsNeedObjectStorage);
        mLBSTraceClient = new LBSTraceClient(getApplicationContext());
        mLBSTraceClient.setInterval(mGatherInterval, mPackInterval);
        initListener();
        mLBSTraceClient.startTrace(mTrace, mOnTraceListener);
        mLBSTraceClient.startGather(mOnTraceListener);
        startRealTimeLoc(mLocationInterval);
    }

    private void initListener() {
        mOnEntityListener = new OnEntityListener() {
            @Override
            public void onAddEntityCallback(AddEntityResponse addEntityResponse) {
                super.onAddEntityCallback(addEntityResponse);
            }

            @Override
            public void onUpdateEntityCallback(UpdateEntityResponse updateEntityResponse) {
                super.onUpdateEntityCallback(updateEntityResponse);
            }

            @Override
            public void onDeleteEntityCallback(DeleteEntityResponse deleteEntityResponse) {
                super.onDeleteEntityCallback(deleteEntityResponse);
            }

            @Override
            public void onEntityListCallback(EntityListResponse entityListResponse) {
                super.onEntityListCallback(entityListResponse);
            }

            @Override
            public void onSearchEntityCallback(SearchResponse searchResponse) {
                super.onSearchEntityCallback(searchResponse);
            }

            @Override
            public void onBoundSearchCallback(BoundSearchResponse boundSearchResponse) {
                super.onBoundSearchCallback(boundSearchResponse);
            }

            @Override
            public void onAroundSearchCallback(AroundSearchResponse aroundSearchResponse) {
                super.onAroundSearchCallback(aroundSearchResponse);
            }

            @Override
            public void onReceiveLocation(TraceLocation traceLocation) {
                super.onReceiveLocation(traceLocation);
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
                    Log.e("error", "latestPoint=" + latestPoint.getLocation().toString());
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
                Log.e("error", currentLatlng.toString());
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

    private void startRealTimeLoc(int locationInterval) {
        mRealTimeLocRunnable = new RealTimeLocRunnable(locationInterval);
        mRealTimeHandler.post(mRealTimeLocRunnable);
    }

    private void stopRealTimeLoc() {
        if(mRealTimeLocRunnable != null && mRealTimeHandler != null) {
            mRealTimeHandler.removeCallbacks(mRealTimeLocRunnable);
            mRealTimeLocRunnable = null;
        }
    }

    public TraceService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("error", "TraceService onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("error", "TraceService onDestroy");
        stopRealTimeLoc();
        mLBSTraceClient.stopTrace(mTrace, mOnTraceListener);
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

    private boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    private boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    private LatLng convertTrace2Map(com.baidu.trace.model.LatLng location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
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

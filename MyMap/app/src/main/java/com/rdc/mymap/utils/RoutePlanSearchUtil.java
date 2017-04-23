package com.rdc.mymap.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.rdc.mymap.model.BusLineInfo;
import com.rdc.mymap.model.Node;

import java.util.ArrayList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.*;

public class RoutePlanSearchUtil {

    private RoutePlanSearch mRoutePlanSearch;
    private MyGetRoutePlanResultListener mMyGetRoutePlanResultListener;
    private PlanNode mStartNode;
    private PlanNode mEndNode;

    private List<MassTransitRouteLine> mMassTransitRouteLineList = new ArrayList<MassTransitRouteLine>();
    private List<List<MassTransitRouteLine.TransitStep>> mTransitStepsList = new ArrayList<List<MassTransitRouteLine.TransitStep>>();
    private List<BusLineInfo> mBusLineInfoList = new ArrayList<BusLineInfo>();
    private List<WalkingRouteLine> mWalkingRouteLineList = new ArrayList<WalkingRouteLine>();
    private List<DrivingRouteLine> mDrivingRouteLineList = new ArrayList<DrivingRouteLine>();
    private List<BikingRouteLine> mBikingRouteLineList = new ArrayList<BikingRouteLine>();

    public RoutePlanSearchUtil() {
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mMyGetRoutePlanResultListener = new MyGetRoutePlanResultListener();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(mMyGetRoutePlanResultListener);
    }

    public List<MassTransitRouteLine> getMassTransitRouteLineList() {
        return mMassTransitRouteLineList;
    }

    public void setMassTransitRouteLineList(List<MassTransitRouteLine> massTransitRouteLineList) {
        this.mMassTransitRouteLineList = massTransitRouteLineList;
    }

    public List<BusLineInfo> getBusLineInfoList() {
        return mBusLineInfoList;
    }

    public void setBusLineInfoList(List<BusLineInfo> busLineInfoList) {
        this.mBusLineInfoList = busLineInfoList;
    }

    public List<WalkingRouteLine> getWalkingRouteLineList() {
        return mWalkingRouteLineList;
    }

    public void setWalkingRouteLineList(List<WalkingRouteLine> walkingRouteLineList) {
        this.mWalkingRouteLineList = walkingRouteLineList;
    }

    public List<DrivingRouteLine> getDrivingRouteLineList() {
        return mDrivingRouteLineList;
    }

    public void setDrivingRouteLineList(List<DrivingRouteLine> drivingRouteLineList) {
        this.mDrivingRouteLineList = drivingRouteLineList;
    }

    public List<BikingRouteLine> getBikingRouteLineList() {
        return mBikingRouteLineList;
    }

    public void setBikingRouteLineList(List<BikingRouteLine> bikingRouteLineList) {
        this.mBikingRouteLineList = bikingRouteLineList;
    }

    public void search(Node startNode, Node endNode, int way) {
        mStartNode = PlanNode.withCityNameAndPlaceName(startNode.getCity(), startNode.getPlace());
        mEndNode = PlanNode.withCityNameAndPlaceName(endNode.getCity(), endNode.getPlace());
        switch (way) {
            case WALKING :
                mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(mStartNode).to(mEndNode));
                break;
            case TRANSIT :
                mRoutePlanSearch.masstransitSearch(new MassTransitRoutePlanOption().from(mStartNode).to(mEndNode));
                break;
            case DRIVING :
                mRoutePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(mStartNode).to(mEndNode));
                break;
            case BIKING :
                mRoutePlanSearch.bikingSearch(new BikingRoutePlanOption().from(mStartNode).to(mEndNode));
                break;
            default:
                break;
        }
    }

    public void destroy() {
        mRoutePlanSearch.destroy();
    }

    private class MyGetRoutePlanResultListener implements OnGetRoutePlanResultListener {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            if(walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mWalkingRouteLineList = walkingRouteResult.getRouteLines();
//            for(WalkingRouteLine walkingRouteLine : mWalkingRouteLineList) {
//                Log.e("error", "duration=" + walkingRouteLine.getDuration() + "distance=" + walkingRouteLine.getDistance());
//                Log.e("error", "start=" + walkingRouteLine.getStarting().getTitle() + "end=" + walkingRouteLine.getTerminal().getTitle());
//                for(WalkingRouteLine.WalkingStep walkingStep : walkingRouteLine.getAllStep()) {
//                    Log.e("error", walkingStep.getInstructions());
//                }
//                for(RouteStep routeStep : walkingRouteLine.getAllStep()) {
//                    for(LatLng latLng : routeStep.getWayPoints()) {
//                        Log.e("error", latLng.toString());
//                    }
//                }
//            }

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            if(transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            if(massTransitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            Log.e("error", "success");
            mMassTransitRouteLineList = massTransitRouteResult.getRouteLines();
            for(MassTransitRouteLine massTransitRouteLine : mMassTransitRouteLineList) {
                mTransitStepsList = massTransitRouteLine.getNewSteps();
                //int j = 0;
                List<String> busLineInfoList = new ArrayList<String>();
                int duration = 0;
                int stopNum = 0;
                int distance = 0;
                String departureStation = "";
                Boolean isFirstStation = false;
                for(List<MassTransitRouteLine.TransitStep> transitSteps : mTransitStepsList) {
                    //Log.e("error", j + "");
                    for(MassTransitRouteLine.TransitStep transitStep : transitSteps) {
                        if(transitStep.getBusInfo() != null) {
                            stopNum += transitStep.getBusInfo().getStopNum();
                            busLineInfoList.add(transitStep.getBusInfo().getName());
                            if(!isFirstStation) {
                                departureStation = transitStep.getBusInfo().getDepartureStation();
                                isFirstStation = true;
                            }
                            /*Log.e("error",  "DepartureStation:" + transitStep.getBusInfo().getDepartureStation() +
                                    "stopNum:" + transitStep.getBusInfo().getStopNum() + "ArriveTime:" + transitStep.getBusInfo().getArriveTime()
                             + "name:" +  transitStep.getBusInfo().getName() + "ArriveStation:" + transitStep.getBusInfo().getArriveStation());*/
                        } else {
                            distance += transitStep.getDistance();
                        }
                        duration += transitStep.getDuration();
                        //Log.e("error", "instructions:" + transitStep.getInstructions() + "Duration:" + transitStep.getDuration() + transitStep.getDistance());
                    }
                    //j ++;
                }
                Log.e("error", "duration:" + duration + "stopNum:" + stopNum + "distance:" + distance + "departureStation:" + departureStation);
                for(String string : busLineInfoList) {
                    Log.e("error", string);
                }
                BusLineInfo busLineInfo = new BusLineInfo(busLineInfoList, duration, stopNum, distance, departureStation);
                mBusLineInfoList.add(busLineInfo);
            }
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if(drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mDrivingRouteLineList = drivingRouteResult.getRouteLines();
            for(DrivingRouteLine drivingRouteLine : mDrivingRouteLineList) {
                Log.e("error", "lightNum=" + drivingRouteLine.getLightNum());
                Log.e("error", "distance=" + drivingRouteLine.getDistance() + "duration=" + drivingRouteLine.getDuration());
                for(DrivingRouteLine.DrivingStep drivingStep: drivingRouteLine.getAllStep()) {
                    Log.e("error", "Instructions=" + drivingStep.getInstructions() + "EntranceInstructions=" + drivingStep.getEntranceInstructions());
                }
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            if(indoorRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            if(bikingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mBikingRouteLineList = bikingRouteResult.getRouteLines();
            for(BikingRouteLine bikingRouteLine : mBikingRouteLineList) {
                Log.e("error", "distance=" + bikingRouteLine.getDistance() + "duration=" + bikingRouteLine.getDuration());
                for(BikingRouteLine.BikingStep bikingStep : bikingRouteLine.getAllStep()) {
                    Log.e("error", "Instructions=" + bikingStep.getInstructions() + "direction=" + bikingStep.getDirection());
                }
            }
        }
    }
}

package com.rdc.mymap.utils;

import android.util.Log;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
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
        }
    }
}

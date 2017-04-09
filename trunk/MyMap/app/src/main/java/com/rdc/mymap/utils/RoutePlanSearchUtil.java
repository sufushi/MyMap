package com.rdc.mymap.utils;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.rdc.mymap.model.Node;

import static com.rdc.mymap.config.WayConfig.*;

public class RoutePlanSearchUtil {

    private RoutePlanSearch mRoutePlanSearch;
    private MyGetRoutePlanResultListener mMyGetRoutePlanResultListener;
    private PlanNode mStartNode;
    private PlanNode mEndNode;

    public RoutePlanSearchUtil() {
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mMyGetRoutePlanResultListener = new MyGetRoutePlanResultListener();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(mMyGetRoutePlanResultListener);
    }

    private void search(Node startNode, Node endNode, int way) {
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

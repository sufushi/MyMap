package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
import com.rdc.mymap.R;

import java.util.ArrayList;
import java.util.List;

import static com.rdc.mymap.config.MsgStateConfig.MSG_HIDE;
import static com.rdc.mymap.config.MsgStateConfig.MSG_RESET_NODE;
import static com.rdc.mymap.config.MsgStateConfig.MSG_SHOW;

public class GuideActivity extends Activity {

    private BNRoutePlanNode mBNRoutePlanNode;
    private BaiduNaviCommonModule mBaiduNaviCommonModule;
    private interface RouteGuideModuleConstants {
        final static int METHOD_TYPE_ON_KEY_DOWN = 0x01;
        final static String KEY_TYPE_KEYCODE = "keyCode";
        final static String KEY_TYPE_EVENT = "event";
    }
    private final static String RET_COMMON_MODULE = "module.ret";

    private Boolean isUseCommonInterface = true;

    private Handler mHandler;

    private BNRouteGuideManager.OnNavigationListener mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {
        @Override
        public void onNaviGuideEnd() {
            finish();
        }

        @Override
        public void notifyOtherAction(int i, int i1, int i2, Object o) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WalkRoutePlanActivity.activityList.add(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStart();
            }
        } else {
            BNRouteGuideManager.getInstance().onStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onPause();
            }
        } else {
            BNRouteGuideManager.getInstance().onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStop();
            }
        } else {
            BNRouteGuideManager.getInstance().onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onResume();
            }
        } else {
            BNRouteGuideManager.getInstance().onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isUseCommonInterface) {
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onDestroy();
            }
        } else {
            BNRouteGuideManager.getInstance().onDestroy();
        }
        WalkRoutePlanActivity.activityList.remove(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onBackPressed(false);
            }
        } else {
            BNRouteGuideManager.getInstance().onBackPressed(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
            }
        } else {
            BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isUseCommonInterface) {
            if(mBaiduNaviCommonModule != null) {
                Bundle bundle = new Bundle();
                bundle.putInt(RouteGuideModuleConstants.KEY_TYPE_KEYCODE, keyCode);
                bundle.putParcelable(RouteGuideModuleConstants.KEY_TYPE_EVENT, event);
                mBaiduNaviCommonModule.setModuleParams(RouteGuideModuleConstants.METHOD_TYPE_ON_KEY_DOWN, bundle);
                try {
                    Boolean ret = (Boolean) bundle.get(RET_COMMON_MODULE);
                    if(ret) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        createHandler();
        View view = null;
        if(isUseCommonInterface) {
            mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
                    BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
            if(mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onCreate();
                view = mBaiduNaviCommonModule.getView();
            }
        } else {
            view = BNRouteGuideManager.getInstance().onCreate(this, mOnNavigationListener);
        }
        if(view != null) {
            setContentView(view);
        }
        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable("route_plan_node");
            }
        }
        if(mHandler != null) {
            mHandler.sendEmptyMessageAtTime(MSG_SHOW, 2000);
        }
    }

    private void createHandler() {
        if(mHandler == null) {
            mHandler = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_SHOW :
                            addCustomizedLayerItems();
                            break;
                        case MSG_HIDE :
                            BNRouteGuideManager.getInstance().showCustomizedLayer(false);
                            break;
                        case MSG_RESET_NODE :

                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    private void addCustomizedLayerItems() {
        List<BNRouteGuideManager.CustomizedLayerItem> customizedLayerItemList = new ArrayList<BNRouteGuideManager.CustomizedLayerItem>();
        BNRouteGuideManager.CustomizedLayerItem customizedLayerItem;
        if(mBNRoutePlanNode != null) {
            customizedLayerItem = new BNRouteGuideManager.CustomizedLayerItem(mBNRoutePlanNode.getLongitude(), mBNRoutePlanNode.getLatitude(), mBNRoutePlanNode.getCoordinateType(),
                    getResources().getDrawable(R.drawable.locate), BNRouteGuideManager.CustomizedLayerItem.ALIGN_CENTER);
            customizedLayerItemList.add(customizedLayerItem);
            BNRouteGuideManager.getInstance().setCustomizedLayerItems(customizedLayerItemList);
        }
        BNRouteGuideManager.getInstance().showCustomizedLayer(true);
    }
}

package com.task.system;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.Gravity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.task.system.services.LocationService;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiConfig;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

public class FixApplication extends MultiDexApplication {

    public LocationService locationService;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.gold_text, R.color.ball_red);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
//        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
//            }
//        });
    }


    @Override
    public void onCreate() {
        super.onCreate();


        locationService = new LocationService(getApplicationContext());
        Utils.init(this);
        MultiDex.install(this);
        ToastUtils.setBgResource(R.drawable.normal_toast_black);
        ToastUtils.setGravity(Gravity.CENTER,0,0);
        //侧滑初始化
        BGASwipeBackHelper.init(this, null);
        ApiConfig.init(this,Constans.IS_DEBUG?Constans.BASE_URL_TEST:Constans.BASE_URL_ONLINE);
        ApiConfig.setSuccessCode(1);
        ApiConfig.addCommonParams(TUtils.getParams());
        ApiConfig.setLogFilter("dyc");
        ApiConfig.setReadTimeout(5000);
        ApiConfig.setConnectTimeout(5000);
        ApiConfig.setWriteTimeout(5000);
    }
}

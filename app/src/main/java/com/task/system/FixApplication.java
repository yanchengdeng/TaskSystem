package com.task.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.task.system.activity.LoginActivity;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.VerisonInfo;
import com.task.system.event.ShowVersionUpdateEvent;
import com.task.system.event.TokenTimeOut;
import com.task.system.services.LocationService;
import com.task.system.services.UpdateService;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import retrofit2.Call;

public class FixApplication extends MultiDexApplication {

    public LocationService locationService;

    private MaterialDialog dialog;

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof TokenTimeOut) {
            if (ApiConfig.context != null && ApiConfig.context instanceof Activity) {
                onShowExpire();
            }
        }else if (event instanceof ShowVersionUpdateEvent){
            checkVersion();
        }
    }


    private MaterialDialog materialDialog;


    @Override
    public void onCreate() {
        super.onCreate();


        locationService = new LocationService(getApplicationContext());
        Utils.init(this);
        MultiDex.install(this);
        ToastUtils.setBgResource(R.drawable.normal_toast_black);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        //侧滑初始化
        BGASwipeBackHelper.init(this, null);
        ApiConfig.init(this, Constans.IS_DEBUG ? Constans.BASE_URL_TEST : Constans.BASE_URL_ONLINE);
        ApiConfig.setSuccessCode(1);
        ApiConfig.addCommonParams(TUtils.getParams());
        ApiConfig.setLogFilter("dyc");
        ApiConfig.setReadTimeout(5000);
        ApiConfig.setConnectTimeout(5000);
        ApiConfig.setWriteTimeout(5000);
        EventBus.getDefault().register(this);
    }

    public void onShowExpire() {
        if (materialDialog != null) {
            return;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .title("温馨提示")
                .content("您的账号已过期，请重新登陆")
                .positiveColor(getResources().getColor(R.color.color_blue)).positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        SPUtils.getInstance().put(Constans.USER_INFO, "");
                        Intent intent = new Intent(ApiConfig.context, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .build();


        materialDialog.setCancelable(false);
        materialDialog.setCanceledOnTouchOutside(false);
        if (materialDialog.isShowing()) {
            materialDialog.dismiss();
        } else {
            materialDialog.show();
        }
    }

    //检查版本更新
    private void checkVersion(){
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).checkVersion(TUtils.getParams());
        API.getObject(call, VerisonInfo.class, new ApiCallBack<VerisonInfo>() {

            @Override
            public void onSuccess(int msgCode, String msg, VerisonInfo data) {


                showUploadDialog(data);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }


    //版本升级对话框
    private void showUploadDialog(final VerisonInfo verisonInfo) {
        if (verisonInfo.update_level.equals("0")){
            return;
        }

        if (dialog!=null && dialog.isShowing()){
            return;
        }


        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context);
        View view = LayoutInflater.from(ApiConfig.context).inflate(R.layout.dialog_verison_view,null,false);

        TextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText(""+verisonInfo.update_content);

        builder.customView(view,false);
        dialog = builder.build();

        view.findViewById(R.id.tv_continu_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_continu_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUpdateVersion(verisonInfo);
                dialog.dismiss();
            }
        });

        if (verisonInfo.update_level.equals("2")){
            view.findViewById(R.id.tv_continu_left).setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();

    }

    private void goUpdateVersion(VerisonInfo verisonInfo) {
        Intent intent = new Intent(ApiConfig.context, UpdateService.class);
        intent.putExtra("apkUrl", verisonInfo.app_url);
        startService(intent);
    }

}

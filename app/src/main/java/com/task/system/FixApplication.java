package com.task.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.request.RequestOptions;
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
import com.task.system.views.photoview.NineGridView;
import com.tencent.bugly.Bugly;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.GlideApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.task.system.utils.TUtils.clearUserInfo;

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
//        CrashReport.initCrashReport(getApplicationContext(), "1da049eb86", false);
        Bugly.init(getApplicationContext(), "1da049eb86", false);

        //侧滑初始化
        BGASwipeBackHelper.init(this, null);
        ApiConfig.init(this,TUtils.getApiHost(),!Constans.IS_DEBUG);
        ApiConfig.setSuccessCode(1);
        ApiConfig.addCommonParams(TUtils.getParams());
        ApiConfig.setLogFilter("dyc");
        ApiConfig.setReadTimeout(5000);
        ApiConfig.setConnectTimeout(5000);
        ApiConfig.setWriteTimeout(5000);
        EventBus.getDefault().register(this);

        NineGridView.setImageLoader(new NineGridView.ImageLoader() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {

                GlideApp.with(ApiConfig.context)
                        .load(url)
                        .transition(withCrossFade())
                        .apply(new RequestOptions().placeholder(R.drawable.ic_default_color).error(R.mipmap.load_err))
                        .into(imageView);

            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        });


        //机关推送
        //TODO  这里一定要是true 才会收到推送，也就是只有测试环境才可以  目前不知道原因 强制更新到 setDebugMode = true
        JPushInterface.setDebugMode(Constans.IS_DEBUG ); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

    }

    public void onShowExpire() {
        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(ApiConfig.context)
                    .title("温馨提示")
                    .content("您的账号已过期，请重新登陆")
                    .positiveColor(getResources().getColor(R.color.color_blue)).positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            materialDialog = null;

                            clearUserInfo();
                            Intent intent = new Intent(ApiConfig.context, LoginActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .build();


            materialDialog.setCancelable(false);
            materialDialog.setCanceledOnTouchOutside(false);
        }
        if (materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog.show();
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


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}

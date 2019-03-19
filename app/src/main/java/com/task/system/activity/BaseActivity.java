package com.task.system.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.VerisonInfo;
import com.task.system.services.UpdateService;
import com.task.system.utils.AppManager;
import com.yc.lib.api.ApiConfig;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate, View.OnClickListener {
    protected BGASwipeBackHelper mSwipeBackHelper;
    public View viewRoot, contentView;
    public View tittleUi;
    private View tvTtittleLine;
    private View refresh;
    //    private MaterialDialog loadDialog;
    public ImageView ivBack, ivRightFunction;
    public TextView tvTittle, tvRightFunction;
    public TextView tvErrorTips;
    private KProgressHUD hud;
    private MaterialDialog materialDialog;
    public Activity mContext;


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
//        // 在 super.onCreate(savedInstanceState) 之前调用该方法
////        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//
//        super.onCreate(savedInstanceState);
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hud = KProgressHUD.create(ApiConfig.context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("加载中...");
    }

    @Override
    public void setContentView(int layoutResID) {
        initSwipeBackFinish();
        this.mContext = this;
//        StatusBarUtil.setLightMode(this);
//        StatusBarUtil.hideFakeStatusBarView(this);

        AppManager.getAppManager().addActivity(this);
        viewRoot = getLayoutInflater().inflate(R.layout.activity_base, null);
//        viewRoot =   LayoutInflater.from(BaseActivity.this).inflate(R.layout.activity_base,null,false);
        contentView = LayoutInflater.from(BaseActivity.this).inflate(layoutResID, null, false);
        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.getRootView().setLayoutParams(params);
        RelativeLayout mContainer = (RelativeLayout) viewRoot.getRootView().findViewById(R.id.container);
        mContainer.addView(contentView.getRootView());
        getWindow().setContentView(viewRoot.getRootView());

        contentView.getRootView().setVisibility(View.GONE);


        ivBack = findViewById(R.id.iv_back);
        ivRightFunction = findViewById(R.id.iv_right_function);
        tvTittle = findViewById(R.id.tv_title);
        tvRightFunction = findViewById(R.id.tv_right_function);
        refresh = getView(R.id.ll_error_refresh);
        tvErrorTips = getView(R.id.tv_error_tips);
        tittleUi = getView(R.id.rl_tittle_ui);
        tvTtittleLine = getView(R.id.tv_tittle_ui_line);

        setToolBar();
    }



    //版本升级对话框
    private void showUploadDialog(final VerisonInfo verisonInfo) {
        if (verisonInfo.update_level.equals("0")) {
            return;
        }


        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_verison_view, null, false);

        TextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText("" + verisonInfo.update_content);

        builder.customView(view, false);
        final MaterialDialog dialog = builder.build();
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

        if (verisonInfo.update_level.equals("2")) {
            view.findViewById(R.id.tv_continu_left).setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
        Constans.HAS_VESRSION_TIPS = true;

    }

    private void goUpdateVersion(VerisonInfo verisonInfo) {
        Intent intent = new Intent(mContext, UpdateService.class);
        intent.putExtra("apkUrl", verisonInfo.app_url);
        startService(intent);
    }




    //影藏标题
    public void hideTittle() {
        tittleUi.setVisibility(View.GONE);
        tvTtittleLine.setVisibility(View.GONE);
    }


    /**
     * 设置titlebar
     */
    protected void setToolBar() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void setTitle(CharSequence text) {
        tvTittle.setText(text);
    }

//    protected void showLoading() {
//        if (loadDialog == null) {
//            loadDialog = new MaterialDialog.Builder(this).show();
//        } else {
//            loadDialog.show();
//        }
//
//        if (contentView.getVisibility() != View.GONE) {
//            contentView.setVisibility(View.GONE);
//        }
//        if (refresh.getVisibility() != View.GONE) {
//            refresh.setVisibility(View.GONE);
//        }
//    }



    /**
     * 失败后点击刷新
     */
    protected void onRefresh() {

    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(false);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    /**
     * 查找View
     *
     * @param id 控件的id
     * @return
     */
    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void showLoadingBar() {
        if (hud!=null){
            if (!hud.isShowing()){
                hud.show();
            }
        }
    }

    public void showLoadingBar(String msg) {
        if (hud!=null){
            if (!hud.isShowing()){
                hud.show();
                hud.setLabel(msg);
            }
        }
    }

    public void showLoadingBar(String msg, boolean cancelable) {
        if (hud!=null){
            if (!hud.isShowing()){
                hud.show();
                hud.setLabel(msg);
                hud.setCancellable(cancelable);
            }
        }
    }

    public void dismissLoadingBar() {
        if (hud!=null){
            hud.dismiss();
        }
    }


}

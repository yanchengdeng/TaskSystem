package com.task.system.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.task.system.R;
import com.task.system.utils.AppManager;
import com.task.system.views.LoadDialog;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

public abstract class BaseSimpleActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate {
    protected BGASwipeBackHelper mSwipeBackHelper;
    private LoadDialog loadDialog;
    public Activity mContext;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwipeBackFinish();
        this.mContext = this;
        AppManager.getAppManager().addActivity(this);

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
        if (loadDialog != null) {
            loadDialog.hide();
            loadDialog = null;
        }
        loadDialog = new LoadDialog(this, getString(R.string.loading));
        loadDialog.setCancelable(true);
        loadDialog.setCanceledOnTouchOutside(true);
        loadDialog.show();
    }

    public void showLoadingBar(String msg) {
        if (loadDialog != null) {
            loadDialog.hide();
            loadDialog = null;
        }
        loadDialog = new LoadDialog(this, msg);
        loadDialog.setCancelable(true);
        loadDialog.setCanceledOnTouchOutside(true);
        loadDialog.show();
    }

    public void showLoadingBar(String msg, boolean cancelable) {
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
        loadDialog = new LoadDialog(this, msg);
        loadDialog.setCancelable(cancelable);
        loadDialog.setCanceledOnTouchOutside(true);
        loadDialog.show();
    }

    public void dismissLoadingBar() {
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog.stopAnimal();
            loadDialog = null;
        }
    }


}
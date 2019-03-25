package com.task.system.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.fragments.HomeFragment;
import com.task.system.fragments.PercenterFragment;
import com.task.system.fragments.TaskFragment;
import com.task.system.utils.AppManager;
import com.task.system.utils.TUtils;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.task.system.views.NoScrollViewPager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import retrofit2.Call;

public class MainActivity extends BaseSimpleActivity {

    private NoScrollViewPager viewPager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        ImmersionBar.with(this).statusBarDarkFont(false,0.2f).init();
        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.trans_black).init();

//        StatusBarUtil.setTranslucentForImageViewInFragment(this, null);
//        StatusBarUtil.setTranslucent(this,30);

        viewPager  = findViewById(R.id.viewpage);
        viewPager.setScroll(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final FragmentPagerItems pages = getPagerItems(new FragmentPagerItems(this));
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red).init();
                        break;
                    case 1:
                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red).init();
                        break;
                    case 2:
                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red_mine_status).init();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
//                    checkVersion();
                })
                .onDenied(permissions -> {
                    ToastUtils.showShort("请打开存储权限");
                })
                .start();

        getCustom();

    }

    private void getCustom() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getCustomeSerice(TUtils.getParams());

       API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
           @Override
           public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
               SPUtils.getInstance().put(Constans.KEFU,data.link);
           }

           @Override
           public void onFaild(int msgCode, String msg) {

           }
       });
    }


    private FragmentPagerItems getPagerItems(FragmentPagerItems pages) {
//        pages.add(FragmentPagerItem.of("消息", IMFragmentWithError.class));
        pages.add(FragmentPagerItem.of("任务大厅", HomeFragment.class));
        pages.add(FragmentPagerItem.of("我的任务", TaskFragment.class));
        pages.add(FragmentPagerItem.of("个人中心", PercenterFragment.class));
        return pages;
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        doubleClickExist();
    }

    private long mExitTime;

    /****
     * 连续两次点击退出
     */
    private boolean doubleClickExist() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.double_click_exit);
            mExitTime = System.currentTimeMillis();
            return true;
        } else {
            AppManager.getAppManager().AppExit(this);
            finish();
        }
        return false;
    }


}

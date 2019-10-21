package com.task.system.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoIgnoreBody;
import com.task.system.api.TaskService;
import com.task.system.bean.AdInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.fragments.HomeFragment;
import com.task.system.fragments.PercenterFragment;
import com.task.system.fragments.SortFragment;
import com.task.system.fragments.TaskFragment;
import com.task.system.recieves.MyReceiver;
import com.task.system.utils.AppManager;
import com.task.system.utils.TUtils;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.task.system.views.NoScrollViewPager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;

public class MainActivity extends BaseSimpleActivity {

    public NoScrollViewPager viewPager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    break;
//                    return true;
                case R.id.navigation_sort:
                    viewPager.setCurrentItem(1);
                    break;
//                    return  true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(2);
                    break;
//                    return true;
                case R.id.navigation_notifications:
                    if (TUtils.isMemberType()) {
                        viewPager.setCurrentItem(3);
                    } else {
                        viewPager.setCurrentItem(2);
                    }
//                    return true;
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ImmersionBar.with(this).statusBarDarkFont(false,0.2f).init();
//        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.trans_black).init();

//        StatusBarUtil.setTranslucentForImageViewInFragment(this, null);
//        StatusBarUtil.setTranslucent(this,30);

        viewPager = findViewById(R.id.viewpage);
        viewPager.setScroll(false);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final FragmentPagerItems pages = getPagerItems(new FragmentPagerItems(this));
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

        if (pages.size() == 3) {
            navigation.getMenu().removeItem(R.id.navigation_dashboard);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
//                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red).init();
                        break;
                    case 1:
//                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red).init();
                        break;
                    case 2:
//                        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.red_mine_status).init();
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

        getAds();


        SysUtils.log("jpush---register_id: "+JPushInterface.getRegistrationID(this));

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.JPUSH_REGIEST_ID))) {
            SysUtils.log("jpush---register_id 真正的 ：: "+JPushInterface.getRegistrationID(this));
        }

        if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(this))) {
            registerJPushRegisterId();
        }


        //注册激光推送
//        registerMessageReceiver();
    }

    private void registerJPushRegisterId() {

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("registration_id", JPushInterface.getRegistrationID(this));




        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).userBindPushId(TUtils.getParams(hashMap));

        API.getObjectIgnoreBody(call,  new ApiCallBack() {

            @Override
            public void onSuccess(int msgCode, String msg, Object data) {
                SysUtils.log("绑定register_id 成功");

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                SysUtils.log("绑定register_id 失败"+msg);
            }
        });


    }

    private MyReceiver messageReceiver;
    private void registerMessageReceiver() {
        messageReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }




    private void getAds() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        maps.put("position", "2");
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getAdList(TUtils.getParams(maps));
        API.getList(call, AdInfo.class, new ApiCallBackList<AdInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AdInfo> data) {

                if (data!=null && data.size()>0){
                    SPUtils.getInstance().put(Constans.SAVE_SPLASH_AD,new Gson().toJson(data.get(0)));
                }


            }

            @Override
            public void onFaild(int msgCode, String msg) {
//                ToastUtils.showShort(msg);
            }
        });
    }



    private void getCustom() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getCustomeSerice(TUtils.getParams());

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                SPUtils.getInstance().put(Constans.KEFU, data.link);
            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });
    }


    private FragmentPagerItems getPagerItems(FragmentPagerItems pages) {
        pages.add(FragmentPagerItem.of("首页", HomeFragment.class));
        pages.add(FragmentPagerItem.of("分类大厅", SortFragment.class));
        if (TUtils.isMemberType()) {
            pages.add(FragmentPagerItem.of("我的任务", TaskFragment.class));
        }
        pages.add(FragmentPagerItem.of("个人中心", PercenterFragment.class));


        return pages;
    }


    @Override
    protected void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
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
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
            AppManager.getAppManager().finishAllActivity();
//            AppManager.getAppManager().AppExit(this);
            finish();
        }
        return false;
    }




}

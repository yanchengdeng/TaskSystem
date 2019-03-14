package com.task.system.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.fragments.HomeFragment;
import com.task.system.fragments.PercenterFragment;
import com.task.system.fragments.TaskFragment;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.task.system.views.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseSimpleActivity {

    private NoScrollViewPager viewPager;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

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
        if (Build.VERSION.SDK_INT >= 23) {
            requestExternalStoragePermission();
        }

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

//        if (isMyAppLauncherDefault()){
//            resetPreferredLauncherAndOpenChooser(this);
//        }

    }



    /**
     * method checks to see if app is currently set as default launcher
     * @return boolean true means currently set as default, otherwise false
     */
    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, com.task.system.activity.MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    private FragmentPagerItems getPagerItems(FragmentPagerItems pages) {
//        pages.add(FragmentPagerItem.of("消息", IMFragmentWithError.class));
        pages.add(FragmentPagerItem.of("任务大厅", HomeFragment.class));
        pages.add(FragmentPagerItem.of("我的任务", TaskFragment.class));
        pages.add(FragmentPagerItem.of("个人中心", PercenterFragment.class));
        return pages;
    }


    /**
     * 如果本地补丁放在了外部存储卡中, 6.0以上需要申请读外部存储卡权限才能够使用. 应用内部存储则不受影响
     */

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"local external storage patch is invalid as not read external storage permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}

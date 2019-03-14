package com.task.system.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.FixApplication;
import com.task.system.R;
import com.task.system.activity.CityListActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.adapters.HomeAdapter;
import com.task.system.adapters.MenuAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.CityInfo;
import com.task.system.bean.HomeMenu;
import com.task.system.bean.TaskInfoList;
import com.task.system.bean.UserInfo;
import com.task.system.services.LocationService;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.task.system.views.BubblePopupDouble;
import com.task.system.views.BubblePopupSingle;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

public class HomeFragment extends Fragment {

    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_message_num)
    TextView tvMessageNum;
    //        @BindView(R.id.toolbar)
//        Toolbar toolbar;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    Unbinder unbinder;
    @BindView(R.id.tv_all_sort)
    TextView tvAllSort;
    @BindView(R.id.tv_smart_sort)
    TextView tvSmartSort;
    @BindView(R.id.ll_sort_ui)
    LinearLayout llSortUi;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.rl_search_ui)
    RelativeLayout rlSearchUi;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.view_temp)
    View viewTemp;

    private List<CityInfo> mAllCities = new ArrayList<>();
    private String loctionCity;


    //全部  智能
    private BubblePopupSingle quickPopupSmart;
    private BubblePopupDouble quickPopupAll;
    private MenuAdapter menuAdapter;
    private HomeAdapter homeAdapter;
    private int sortUiHight;
    //避免多次添加margin  导致页面闪动
    private boolean hasAddYesMargin = false, hasAddNoMargin = true;
    private int page = 1;
    private String region_id;
    private String category_id;
    private String sort_id;
    private String tags_id;
    private String keywords;

    private LocationService locationService;
    private static final int REQUEST_PERMISSON_CODE = 100;
    private boolean mPermission = false;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);

        menuAdapter = new MenuAdapter(R.layout.adapter_drop_menu_item);
        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);

        List<HomeMenu> homeMenus = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            HomeMenu menu = new HomeMenu();
            menu.title = "菜单" + i;
            homeMenus.add(menu);
            homeMenus.add(menu);
        }

        menuAdapter.setNewData(homeMenus);


        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recycle.setAdapter(homeAdapter);


        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        String[] images = new String[]{
                "http://img.zcool.cn/community/0166c756e1427432f875520f7cc838.jpg",
                "http://img.zcool.cn/community/018fdb56e1428632f875520f7b67cb.jpg",
                "http://img.zcool.cn/community/01c8dc56e1428e6ac72531cbaa5f2c.jpg",
                "http://img.zcool.cn/community/01fda356640b706ac725b2c8b99b08.jpg",
                "http://img.zcool.cn/community/01fd2756e142716ac72531cbf8bbbf.jpg",
                "http://img.zcool.cn/community/0114a856640b6d32f87545731c076a.jpg"};

        banner.setImages(Arrays.asList(images));
        //banner设置方法全部调用完毕时最后调用
        banner.start();


        //搜索框加一个statusbar 高度
        RelativeLayout.LayoutParams searchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int marginTop = ConvertUtils.dp2px(24);
        if (getActivity() != null) {
            marginTop = ImmersionBar.getStatusBarHeight(getActivity());
        }
        searchParam.topMargin = marginTop;
        rlSearchUi.setLayoutParams(searchParam);


        LinearLayout.LayoutParams menuLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, marginTop);

        //菜单增加一个startbar高度

        viewTemp.setLayoutParams(menuLayout);



        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                LogUtils.w("dyc--", i + "----" + ConvertUtils.dp2px(180));
                if (Math.abs(i) + ImmersionBar.getStatusBarHeight(getActivity()) + 10 > ConvertUtils.dp2px(180)) {
                    if (hasAddNoMargin) {
                        ImmersionBar.with(getActivity()).statusBarDarkFont(false, 0.2f).statusBarColor(R.color.red).init();
                        hasAddNoMargin = false;
                        hasAddYesMargin = true;
                        LogUtils.w("dyc", "----添加padding-");
                    }
                } else {
                    if (hasAddYesMargin) {
                        ImmersionBar.with(getActivity()).statusBarDarkFont(false, 0.2f).statusBarColor(R.color.trans_black).init();
                        hasAddYesMargin = false;
                        hasAddNoMargin = true;
                        LogUtils.w("dyc", "----减少----padding-");
                    }
                }
            }
        });


        getAllSort();
        getSmartSort();
        getTaskList();
        getCityList();

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getTaskList();
            }
        });

        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTaskList();
            }
        }, recycle);


        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, homeAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });

        mPermission = checkLocationPermission();
        if (mPermission) {
            startLocation();
        } else {
            ToastUtils.showShort("请打开定位权限");
        }


        return view;
    }

    //全部分类
    private void getAllSort() {
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCatergoryList(TUtils.getParams());
        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    //智能分类
    private void getSmartSort() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskList(TUtils.getParams());

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    //任务列表

    /**
     * region_id
     * * category_id
     * * sort_id
     * * tags_id
     * * page
     * * keywords
     */
    private void getTaskList() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        maps.put("page_size", Constans.PAGE_SIZE);
        if (!TextUtils.isEmpty(region_id)) {
            maps.put("region_id", region_id);
        }
        if (!TextUtils.isEmpty(category_id)) {
            maps.put("category_id", category_id);
        }
        if (!TextUtils.isEmpty(sort_id)) {
            maps.put("sort_id", sort_id);
        }
        if (!TextUtils.isEmpty(tags_id)) {
            maps.put("tags_id", tags_id);
        }
        if (!TextUtils.isEmpty(keywords)) {
            maps.put("keywords", keywords);
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskList(TUtils.getParams(maps));

        API.getObject(call, TaskInfoList.class, new ApiCallBack<TaskInfoList>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoList data) {
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page);
                smartRefresh.finishRefresh();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle);
                smartRefresh.finishRefresh();
            }
        });
    }


    private void getCityList() {
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCityList(TUtils.getParams());

        API.getList(call, CityInfo.class, new ApiCallBackList<CityInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CityInfo> data) {
                if (data != null && data.size() > 0) {
                    Collections.sort(data, new Comparator<CityInfo>() {
                        @Override
                        public int compare(CityInfo o1, CityInfo o2) {
                            return o1.pinyin.compareTo(o2.pinyin);
                        }
                    });
                    TUtils.setAllCitys(data);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
            }
        });

    }


    private void showSmartSort() {

        if (quickPopupSmart == null) {
            quickPopupSmart = new BubblePopupSingle(ApiConfig.context);
            RecyclerView recyclerView = quickPopupSmart.getContentView();
            recyclerView.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
            recyclerView.setAdapter(menuAdapter);
            quickPopupSmart.getPopupWindow().setOutsideTouchable(false);
        }

//
        if (quickPopupSmart.isShowing()) {
            quickPopupSmart.dismiss();
        } else {
            quickPopupSmart.showPopupWindow(llSortUi);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_location, R.id.tv_message_num, R.id.tv_all_sort, R.id.tv_smart_sort})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                HomeFragment.this.startActivityForResult(new Intent(getContext(), CityListActivity.class), 200);
                break;
            case R.id.tv_message_num:
                break;
            case R.id.tv_all_sort:
                break;
            case R.id.tv_smart_sort:
                showSmartSort();
                break;
        }
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            //Glide 加载图片简单用法
            Glide.with(ApiConfig.context).load(path).into(imageView);

        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            ImageView simpleDraweeView = new ImageView(context);
            simpleDraweeView.setScaleType(ImageView.ScaleType.FIT_XY);
            simpleDraweeView.setAdjustViewBounds(true);
            return simpleDraweeView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ApiConfig.context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ApiConfig.context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
           /* if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }*/
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions((Activity) ApiConfig.context,
                        permissions.toArray(new String[0]),
                        REQUEST_PERMISSON_CODE);
                return false;
            }
        }

        return true;
    }


    private void startLocation() {
        if (getActivity() == null) {

            return;
        }
        locationService = ((FixApplication) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {


        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                ((Activity) ApiConfig.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(bdLocation.getCity())) {
                            loctionCity = bdLocation.getCity();
                            tvLocation.setText("" + loctionCity);
                            if (locationService != null) {
                                locationService.unregisterListener(mListener); //注销掉监听
                                locationService.stop(); //停止定位服务
                            }
                        }
                    }
                });
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSON_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mPermission = true;
                if (mPermission) {
                    startLocation();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null && data.getExtras() != null && data.getExtras().getSerializable(Constans.PASS_OBJECT) != null) {
                    CityInfo cityInfo = (CityInfo) data.getExtras().getSerializable(Constans.PASS_OBJECT);
                    tvLocation.setText("" + cityInfo.region_name);
                    region_id = cityInfo.getRegion_id();
                    page = 1;
                    getTaskList();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
    }
}

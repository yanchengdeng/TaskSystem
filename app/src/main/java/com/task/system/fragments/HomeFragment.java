package com.task.system.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
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
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.FixApplication;
import com.task.system.R;
import com.task.system.activity.CityListActivity;
import com.task.system.activity.MainActivity;
import com.task.system.activity.MessageListActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.activity.TaskListActivity;
import com.task.system.adapters.CategoryAdapter;
import com.task.system.adapters.HomeAdapter;
import com.task.system.adapters.MenuAdapter;
import com.task.system.adapters.TagAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AdInfo;
import com.task.system.bean.CatergoryInfo;
import com.task.system.bean.CityInfo;
import com.task.system.bean.HomeMenu;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.SortTags;
import com.task.system.bean.TaskInfoList;
import com.task.system.common.GlideImageLoader;
import com.task.system.services.LocationService;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.task.system.views.BubblePopupDouble;
import com.task.system.views.BubblePopupSingle;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import razerdp.basepopup.BasePopupWindow;
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
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.iv_all_sort)
    ImageView ivAllSort;
    @BindView(R.id.iv_smart_sort)
    ImageView ivSmartSort;
    @BindView(R.id.ll_all_sort)
    LinearLayout llAllSort;
    @BindView(R.id.ll_smart_sort)
    LinearLayout llSmartSort;

    private List<CityInfo> mAllCities = new ArrayList<>();
    private String loctionCity;


    //全部  智能
    private BubblePopupSingle quickPopupSmart;
    private BubblePopupDouble quickPopupAll;
    private MenuAdapter menuAdapter;
    private CategoryAdapter meneLeft, menuRight;
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
    private TagAdapter tagAdapter;

    //声明AMapLocationClient类对象
//   public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
//   public AMapLocationListener mLocationListener ;


    private int menuSecomdIndex ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);

        menuAdapter = new MenuAdapter(R.layout.adapter_drop_menu_item);
        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);
        meneLeft = new CategoryAdapter(R.layout.adapter_drop_menu_item);
        menuRight = new CategoryAdapter(R.layout.adapter_drop_menu_item);
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));

        tagAdapter = new TagAdapter(R.layout.adapter_tag);
        RecyclerView tagRecycle = new RecyclerView(ApiConfig.context);
        tagRecycle.setBackgroundColor(getResources().getColor(R.color.list_divider_color));
        LinearLayoutManager tagLayoutManage = new LinearLayoutManager(ApiConfig.context);
        tagLayoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        tagRecycle.setLayoutManager(tagLayoutManage);
        tagRecycle.setNestedScrollingEnabled(false);
        tagRecycle.setAdapter(tagAdapter);
        homeAdapter.addHeaderView(tagRecycle);
        homeAdapter.setHeaderAndEmpty(true);
        recycle.setAdapter(homeAdapter);

        tagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (HomeMenu item : tagAdapter.getData()) {
                    item.isSelected = false;
                }
                tagAdapter.getData().get(position).isSelected = true;
                tagAdapter.notifyDataSetChanged();
                tags_id = tagAdapter.getItem(position).id;
                page = 1;
                getTaskList();
            }
        });


        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());


        //搜索框加一个statusbar 高度
        RelativeLayout.LayoutParams searchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int marginTop = ConvertUtils.dp2px(24);
        if (getActivity() != null) {
            marginTop = ImmersionBar.getStatusBarHeight(getActivity());
        }
        searchParam.topMargin = marginTop;
        LinearLayout.LayoutParams menuLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, marginTop);

        //菜单增加一个startbar高度
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i) + ImmersionBar.getStatusBarHeight(getActivity()) + 10 > ConvertUtils.dp2px(180)) {
                    if (hasAddNoMargin) {
//                        ImmersionBar.with(getActivity()).statusBarDarkFont(false, 0.2f).statusBarColor(R.color.red).init();
                        hasAddNoMargin = false;
                        hasAddYesMargin = true;
                    }
                } else {
                    if (hasAddYesMargin) {
//                        ImmersionBar.with(getActivity()).statusBarDarkFont(false, 0.2f).statusBarColor(R.color.trans_black).init();
                        hasAddYesMargin = false;
                        hasAddNoMargin = true;
                    }
                }
            }
        });


        region_id = SPUtils.getInstance().getString(Constans.LOCATON_CITY_id);

        getAds();
        getAllSort();
        getSmartSort();
        getTaskList();
        getCityList();
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getAds();
                getTaskList();
                getUnreadNum();
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
                bundle.putString(Constans.PASS_STRING, homeAdapter.getData().get(position).id);
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });


        AndPermission.with(ApiConfig.context).runtime().permission(Permission.Group.LOCATION).onGranted(
                permissions -> {
                    startLocation();
                }
        ).onDenied(
                permissions -> {
                    ToastUtils.showShort("请打开定位权限");
                }
        ).start();


//        //初始化定位
//        mLocationListener = new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                LogUtils.w("dyc",aMapLocation);
//
//            }
//        };
//        mLocationClient = new AMapLocationClient(ApiConfig.context);
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mLocationListener);
//
//        mLocationClient.startLocation();


        return view;
    }


    private void getUnreadNum() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getMessageCount(TUtils.getParams());
        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                if (data.sum > 0) {
                    tvMessageNum.setText(String.valueOf(data.sum > 99 ? 99 : data.sum));
                    tvMessageNum.setVisibility(View.VISIBLE);
                } else {
                    tvMessageNum.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //更新statusbar
            getUnreadNum();

        }
    }

    private void getAds() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getAdList(TUtils.getParams());
        API.getList(call, AdInfo.class, new ApiCallBackList<AdInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AdInfo> data) {

                List<String> images = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (AdInfo item : data) {
                        images.add(item.cover);
                    }
                }
                banner.setImages(images);
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constans.PASS_NAME, data.get(position).title);
                        bundle.putString(Constans.PASS_STRING, data.get(position).link_url);
                        ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);

                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    //全部分类
    private void getAllSort() {
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCatergoryList(TUtils.getParams());
        API.getList(call, CatergoryInfo.class, new ApiCallBackList<CatergoryInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CatergoryInfo> data) {

                if (data != null) {
                    meneLeft.getData().clear();
                    CatergoryInfo all = new CatergoryInfo();
                    all.id = "";
                    all.isSelected = true;
                    all.title = "全部分类";
                    data.add(0, all);
                }

                meneLeft.setNewData(addExtras(data));

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    private List<CatergoryInfo> addExtras(List<CatergoryInfo> data) {
        for (CatergoryInfo item:data){
            if (item._child!=null && item._child.size()>0){
                CatergoryInfo all = new CatergoryInfo();
                all.id = item.id;
                all.title = "全部";
                item._child.add(0,all);
            }
        }

        return data;
    }

    //智能分类
    private void getSmartSort() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getSortTagsList(TUtils.getParams());

        API.getObject(call, SortTags.class, new ApiCallBack<SortTags>() {
            @Override
            public void onSuccess(int msgCode, String msg, SortTags data) {
                if (data != null) {
                    if (data.sort != null) {
                        menuAdapter.setNewData(data.sort);
                    }
                    if (data.tags != null) {
                        tagAdapter.setNewData(data.tags);
                    }
                }
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
        if (page == 1) {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).showLoadingBar();
            }
        }
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
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page, smartRefresh);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle, smartRefresh);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }

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
                    mAllCities = data;
                    getReginId();


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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight() * 0.5));
            recyclerView.setLayoutParams(layoutParams);
            recyclerView.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
            recyclerView.setAdapter(menuAdapter);

            quickPopupSmart.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivSmartSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });

            menuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    for (HomeMenu item : menuAdapter.getData()) {
                        item.isSelected = false;
                    }
                    menuAdapter.getData().get(position).isSelected = true;
                    menuAdapter.notifyDataSetChanged();
                    sort_id = menuAdapter.getItem(position).id;
                    page = 1;
                    getTaskList();
                    quickPopupSmart.dismiss();
                    tvSmartSort.setText("" + menuAdapter.getData().get(position).title);
                    tvSmartSort.setTextColor(getResources().getColor(R.color.red));
                    ivSmartSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });
        }

//
        if (quickPopupSmart.isShowing()) {
            quickPopupSmart.dismiss();
            ivSmartSort.setImageResource(R.mipmap.icon_arrow_down);
        } else {
            quickPopupSmart.showPopupWindow(llSortUi);
            ivSmartSort.setImageResource(R.mipmap.arrwo_up_red);
        }
    }


    private void showShowDouble() {

        if (quickPopupAll == null) {
            quickPopupAll = new BubblePopupDouble(ApiConfig.context);
            View view = quickPopupAll.getContentView();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight() * 0.5));
            view.setLayoutParams(layoutParams);
            RecyclerView recyclerViewLeft = view.findViewById(R.id.rcv_content);
            RecyclerView recyclerViewRight = view.findViewById(R.id.rcv_content_right);
            recyclerViewLeft.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerViewLeft.setAdapter(meneLeft);

            recyclerViewRight.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerViewRight.setAdapter(menuRight);

            quickPopupAll.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivAllSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });


            meneLeft.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    for (CatergoryInfo item : meneLeft.getData()) {
                        item.isSelected = false;
                    }
                    meneLeft.getData().get(position).isSelected = true;
                    meneLeft.notifyDataSetChanged();
                    if (meneLeft.getData().get(position)._child == null) {
                        category_id = meneLeft.getItem(position).id;
                        page = 1;
                        getTaskList();
                        menuRight.setNewData(new ArrayList<>());
                        quickPopupAll.dismiss();
                        tvAllSort.setText("" + meneLeft.getData().get(position).title);
                        tvAllSort.setTextColor(getResources().getColor(R.color.red));
                        ivAllSort.setImageResource(R.mipmap.icon_arrow_down);
                    } else {
                        menuSecomdIndex = position;
                        menuRight.setNewData(meneLeft.getData().get(position)._child);
                    }

                }
            });

            menuRight.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    for (CatergoryInfo item :meneLeft.getData().get(menuSecomdIndex)._child) {
                        item.isSelected = false;
                    }
                    meneLeft.getData().get(menuSecomdIndex)._child.get(position).isSelected = true;
                    menuRight.setNewData(meneLeft.getData().get(menuSecomdIndex)._child);
                    category_id = menuRight.getItem(position).id;
                    page = 1;
                    getTaskList();
                    quickPopupAll.dismiss();
                    tvAllSort.setText("" + menuRight.getData().get(position).title);
                    tvAllSort.setTextColor(getResources().getColor(R.color.red));
                    ivAllSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });
        }

//
        if (quickPopupAll.isShowing()) {
            quickPopupAll.dismiss();
            ivAllSort.setImageResource(R.mipmap.icon_arrow_down);
        } else {
            quickPopupAll.showPopupWindow(llSortUi);
            ivAllSort.setImageResource(R.mipmap.arrwo_up_red);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_location, R.id.ll_search, R.id.tv_message_num, R.id.iv_message, R.id.ll_all_sort, R.id.ll_smart_sort})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                HomeFragment.this.startActivityForResult(new Intent(getContext(), CityListActivity.class), 200);
                break;
            case R.id.tv_message_num:
            case R.id.iv_message:
                ActivityUtils.startActivity(MessageListActivity
                        .class);
                break;
            case R.id.ll_search:
                ActivityUtils.startActivity(TaskListActivity.class);
                break;
            case R.id.ll_all_sort:
                showShowDouble();
                break;
            case R.id.ll_smart_sort:
                showSmartSort();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = bdLocation.getLocType();

            LogUtils.w("dyc", "===" + errorCode);

            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {


                ((Activity) ApiConfig.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(bdLocation.getCity())) {
                            loctionCity = bdLocation.getCity();
                            setLocationContent();
                        } else {
                            tvLocation.setText("定位失败");
                        }
                    }
                });
            } else {
                tvLocation.setText("定位失败");
            }

            if (locationService != null) {
                locationService.unregisterListener(mListener); //注销掉监听
                locationService.stop(); //停止定位服务
            }
        }
    };

    private void setLocationContent() {
        tvLocation.setText("" + loctionCity);
        if (!TextUtils.isEmpty(loctionCity)) {
            if (loctionCity.length() < 4) {
                tvLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                tvLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null && data.getExtras() != null && data.getExtras().getSerializable(Constans.PASS_OBJECT) != null) {
                    CityInfo cityInfo = (CityInfo) data.getExtras().getSerializable(Constans.PASS_OBJECT);
                    loctionCity = cityInfo.region_name;
                    region_id = cityInfo.getRegion_id();
                    setLocationContent();
                    page = 1;
                    getTaskList();
                }
//                else if (data != null && data.getExtras() != null && !TextUtils.isEmpty(data.getExtras().getString(Constans.PASS_STRING))) {
//                    String cityName = data.getExtras().getString(Constans.PASS_STRING);
//                    if (!TextUtils.isEmpty(cityName)) {
//                        loctionCity = cityName;
//                        getReginId();
//                        setLocationContent();
//                        if (!TextUtils.isEmpty(region_id)) {
//                            page = 1;
//                            getTaskList();
//                        }
//                    }
//                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getReginId() {
        for (CityInfo cityInfo : mAllCities) {
            if (cityInfo.region_name.equals(loctionCity)) {
                region_id = cityInfo.getRegion_id();
                SPUtils.getInstance().put(Constans.LOCATON_CITY_id, region_id);
            }
        }
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

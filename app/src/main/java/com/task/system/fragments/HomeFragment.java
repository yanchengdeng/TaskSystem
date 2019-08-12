package com.task.system.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.imagepicker.view.GridSpacingItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.FixApplication;
import com.task.system.R;
import com.task.system.activity.MainActivity;
import com.task.system.activity.MessageListActivity;
import com.task.system.activity.NewUserTaskActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.SignActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.activity.TaskListActivity;
import com.task.system.adapters.HomeAdapter;
import com.task.system.adapters.TagAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AdInfo;
import com.task.system.bean.AppInitInfo;
import com.task.system.bean.AreaBean;
import com.task.system.bean.CatergoryInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.TaskInfoList;
import com.task.system.common.GlideImageLoader;
import com.task.system.enums.UserType;
import com.task.system.services.LocationService;
import com.task.system.utils.TUtils;
import com.task.system.views.AddressPickerView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.ImageLoaderUtil;
import com.yc.lib.api.utils.SysUtils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
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
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;


    private List<AreaBean> mAllCities;
    private String loctionArea;
    private HomeAdapter homeAdapter;
    private int page = 1;
    private String region_id;

    private LocationService locationService;

    private View headerView;
    private Banner banner;
    private RecyclerView tagRecycle;
    private TagAdapter tagAdapter;
    private CountDownTimer desposidDown;
    private int despositPostion = 0;//获奖的位置


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);
        headerView = inflater.inflate(R.layout.layout_header_home, null, false);
        banner = headerView.findViewById(R.id.banner);


        //签到
        headerView.findViewById(R.id.iv_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(SignActivity.class);
            }
        });

        //新人任务
        headerView.findViewById(R.id.iv_new_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(NewUserTaskActivity.class);
            }
        });
        tagRecycle = headerView.findViewById(R.id.recycle_sort);
        tagAdapter = new TagAdapter(R.layout.adapter_tag);
        tagRecycle.setLayoutManager(new GridLayoutManager(ApiConfig.context, 4));
        tagRecycle.addItemDecoration(new GridSpacingItemDecoration(4, 10, true));
        tagRecycle.setAdapter(tagAdapter);


        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);
//        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));

        homeAdapter.addHeaderView(headerView);
        homeAdapter.setHeaderAndEmpty(true);
        recycle.setAdapter(homeAdapter);


//        tagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constans.PASS_OBJECT,tagAdapter.getItem(position));
//                ActivityUtils.startActivity(bundle,TaskListActivity.class);
//            }
//        });


        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());


        region_id = SPUtils.getInstance().getString(Constans.SAVE_LOCATION_REGION_ID);


//        getAds();
//        getUserCatogry();
        getAppInit();
        getTaskList();
        mAllCities = TUtils.getAllCitys();
        if (mAllCities == null) {
            getCityList();
        }

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
//                getAds();
                getAppInit();
                getTaskList();
//                getUnreadNum();
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

    /**
     * 首页信息   广告、获奖信息、分类、新消息数
     */
    private void getAppInit() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).appInit(TUtils.getParams());
        API.getObject(call, AppInitInfo.class, new ApiCallBack<AppInitInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, AppInitInfo data) {

                if (data != null) {
                    //广告
                    if (data.ad != null && data.ad.size() > 0) {
                        List<String> images = new ArrayList<>();
                        for (AdInfo item : data.ad) {
                            images.add(item.cover);
                        }
                        banner.setImages(images);
                        banner.setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constans.PASS_NAME, data.ad.get(position).title);
                                bundle.putString(Constans.PASS_STRING, data.ad.get(position).link_url);
                                ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);

                            }
                        });
                        //banner设置方法全部调用完毕时最后调用
                        banner.start();
                    }

                    //栏目
                    if (data.categorys != null && data.categorys.size() > 0) {
                        tagAdapter.setNewData(data.categorys);
                    }

                    //奖品
                    if (data.deposit != null && data.deposit.size() > 0) {

                        desposidDown = new CountDownTimer(24 * 60 * 60 * 1000, 5 * 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (despositPostion < data.deposit.size() - 1) {
                                    despositPostion++;
                                } else {
                                    despositPostion = 0;
                                }


                                ImageLoaderUtil.loadCircle(data.deposit.get(despositPostion).avatar, headerView.findViewById(R.id.iv_user_icon), R.mipmap.defalut_header);

                                String despositContent = "<div>" + data.deposit.get(despositPostion).nickname_txt + "提现 <span style=\"color:red\">" + data.deposit.get(despositPostion).cash_txt + " 元</span></div>";
                                ((TextView) headerView.findViewById(R.id.tv_user_name)).setText(Html.fromHtml(despositContent));

                            }

                            @Override
                            public void onFinish() {


                            }
                        }.start();
                    }

                    if (!TextUtils.isEmpty(data.new_message_sum)) {
                        if (Integer.parseInt(data.new_message_sum) > 0) {
                            tvMessageNum.setText(String.valueOf(Integer.parseInt(data.new_message_sum) > 99 ? 99 : Integer.parseInt(data.new_message_sum)));
                            tvMessageNum.setVisibility(View.VISIBLE);
                        } else {
                            tvMessageNum.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                SysUtils.log("" + msg);

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
        maps.put("position", "1");
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getAdList(TUtils.getParams(maps));
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

    //用户全部分类
    private void getUserCatogry() {
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getUserCatergorylist(TUtils.getParams());
        API.getList(call, CatergoryInfo.class, new ApiCallBackList<CatergoryInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CatergoryInfo> data) {
                tagAdapter.setNewData(data);
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

        if (TUtils.getUserInfo().user_type.equals(UserType.USER_TYPE_AREA.getType())) {
            maps.put("role", "publish");
        }
//        if (!TextUtils.isEmpty(category_id)) {
//            maps.put("category_id", category_id);
//        }
//        if (!TextUtils.isEmpty(sort_id)) {
//            maps.put("sort_id", sort_id);
//        }
//        if (!TextUtils.isEmpty(tags_id)) {
//            maps.put("tags_id", tags_id);
//        }


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

        API.getList(call, AreaBean.class, new ApiCallBackList<AreaBean>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AreaBean> data) {
                if (data != null && data.size() > 0) {
                    mAllCities = data;
                    TUtils.setAllCitys(data);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_location, R.id.ll_search, R.id.tv_message_num, R.id.iv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                if (mAllCities == null) {
                    getCityList();
                } else {
                    showAddressDialog();
                }
                break;
            case R.id.tv_message_num:
            case R.id.iv_message:
                ActivityUtils.startActivity(MessageListActivity
                        .class);
                break;
            case R.id.ll_search:
                ActivityUtils.startActivity(TaskListActivity.class);
                break;

        }
    }


    /**
     * 显示地址选择，点击事件
     */
    private void showAddressDialog() {
        AddressPickerView addressView = new AddressPickerView(ApiConfig.context);
        AlertDialog addressDialog =
                new AlertDialog.Builder(ApiConfig.context, R.style.Dialog_FS)
                        .setView(addressView)
                        .setCancelable(true)
                        .create();
        addressDialog.show();
        addressView.initData(mAllCities);
        if (addressDialog.getWindow() != null) {
            addressDialog.getWindow().setGravity(Gravity.BOTTOM);
            addressDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        addressView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String address, String provinceCode, String cityCode, String districtCode) {
                addressDialog.dismiss();
                tvLocation.setText(address);
                region_id = districtCode;
                SPUtils.getInstance().put(Constans.SAVE_LOCATION_REGION_NAME, address);
                SPUtils.getInstance().put(Constans.SAVE_LOCATION_REGION_ID, districtCode);
                getAppInit();
                getTaskList();
            }
        });
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
                        if (!TextUtils.isEmpty(bdLocation.getDistrict())) {
                            loctionArea = bdLocation.getDistrict();
                            region_id = bdLocation.getAdCode();
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
        tvLocation.setText("" + loctionArea);
        SPUtils.getInstance().put(Constans.SAVE_LOCATION_REGION_NAME, loctionArea);
        SPUtils.getInstance().put(Constans.SAVE_LOCATION_REGION_ID, region_id);
        page = 1;
        getTaskList();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
//            if (resultCode == getActivity().RESULT_OK) {
//                if (data != null && data.getExtras() != null && data.getExtras().getSerializable(Constans.PASS_OBJECT) != null) {
//                    CityInfo cityInfo = (CityInfo) data.getExtras().getSerializable(Constans.PASS_OBJECT);
//                    loctionArea = cityInfo.region_name;
//                    region_id = cityInfo.getRegion_id();
//
//                    setLocationContent();
//                    page = 1;
//                    getTaskList();
//                }
//                else if (data != null && data.getExtras() != null && !TextUtils.isEmpty(data.getExtras().getString(Constans.PASS_STRING))) {
//                    String cityName = data.getExtras().getString(Constans.PASS_STRING);
//                    if (!TextUtils.isEmpty(cityName)) {
//                        loctionArea = cityName;
//                        getReginId();
//                        setLocationContent();
//                        if (!TextUtils.isEmpty(region_id)) {
//                            page = 1;
//                            getTaskList();
//                        }
//                    }
//                }
//            }
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

        if (desposidDown != null) {
            desposidDown.cancel();
        }
    }
}

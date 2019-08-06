package com.task.system.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.FixApplication;
import com.task.system.R;
import com.task.system.activity.CityListActivity;
import com.task.system.activity.MessageListActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.TaskListActivity;
import com.task.system.adapters.FatherTagAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AdInfo;
import com.task.system.bean.CatergoryInfo;
import com.task.system.bean.CityInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.SortTags;
import com.task.system.common.GlideImageLoader;
import com.task.system.services.LocationService;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
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
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: SortFragment.java
 * Author: dengyancheng
 * Date: 2019-07-31 23:10
 * Description: 分类大厅
 * History:
 */
public class SortFragment extends Fragment {

    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_message_num)
    TextView tvMessageNum;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    Unbinder unbinder;

    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;


    private List<CityInfo> mAllCities = new ArrayList<>();
    private String loctionCity;
    private String region_id;
    private LocationService locationService;
    private FatherTagAdapter fatherTagAdapter;
    private Banner banner;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_fragment, container, false);

        unbinder = ButterKnife.bind(this, view);
        banner = new Banner(ApiConfig.context);
        ((ImageView)banner.findViewById(R.id.bannerDefaultImage)).setImageResource(R.mipmap.banner1);
        banner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(180)));
        banner.setDelayTime(5000);

        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        fatherTagAdapter = new FatherTagAdapter(R.layout.adapter_tag_father);
        fatherTagAdapter.addHeaderView(banner);
        fatherTagAdapter.setHeaderAndEmpty(true);
        recycle.setAdapter(fatherTagAdapter);

//        fatherTagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                ActivityUtils.startActivity(TaskListActivity.class);
//            }
//        });



        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        region_id = SPUtils.getInstance().getString(Constans.LOCATON_CITY_id);

        getAds();
        getCityList();
        getAllSort();
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getAds();
                getUnreadNum();
                getAllSort();
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

    //全部分类
    private void getAllSort() {
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCatergoryList(TUtils.getParams());
        API.getList(call, CatergoryInfo.class, new ApiCallBackList<CatergoryInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CatergoryInfo> data) {
                if (data != null && data.size() > 0) {
                    fatherTagAdapter.setNewData(data);
                }
                smartRefresh.finishRefresh();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                smartRefresh.finishRefresh();
                fatherTagAdapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recycle));
            }
        });
    }



    //智能分类
    private void getSmartSort() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getSortTagsList(TUtils.getParams());

        API.getObject(call, SortTags.class, new ApiCallBack<SortTags>() {
            @Override
            public void onSuccess(int msgCode, String msg, SortTags data) {

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_location, R.id.ll_search, R.id.tv_message_num, R.id.iv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                SortFragment.this.startActivityForResult(new Intent(getContext(), CityListActivity.class), 200);
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
                }
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

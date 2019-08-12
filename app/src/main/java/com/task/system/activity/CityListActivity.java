package com.task.system.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.FixApplication;
import com.task.system.R;
import com.task.system.adapters.CityListAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.CityInfo;
import com.task.system.bean.LocateState;
import com.task.system.services.LocationService;
import com.task.system.utils.TUtils;
import com.task.system.views.SideLetterBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class CityListActivity extends BaseActivity {

    @BindView(R.id.et_input_text)
    EditText etInputText;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.ll_key_ui)
    LinearLayout llKeyUi;
    @BindView(R.id.listview_all_city)
    ListView listviewAllCity;
    @BindView(R.id.tv_letter_overlay)
    TextView tvLetterOverlay;
    @BindView(R.id.side_letter_bar)
    SideLetterBar sideLetterBar;
    @BindView(R.id.rl_selece_city_ui)
    RelativeLayout rlSeleceCityUi;
    @BindView(R.id.img_err)
    ImageView imgErr;
    @BindView(R.id.tv_error_tips)
    TextView tvErrorTips;
    @BindView(R.id.ll_error_refresh)
    LinearLayout llErrorRefresh;

    private CityListAdapter adapter;
    private List<CityInfo> mAllCities = new ArrayList<>();
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);

        tvTittle.setText("定位选择");

        adapter = new CityListAdapter(mContext, mAllCities);

        getCityList();


        AndPermission.with(ApiConfig.context).runtime().permission(Permission.Group.LOCATION).onGranted(
                permissions -> {
                    startLocation();
                }
        ).onDenied(
                permissions -> {
                    ToastUtils.showShort("请打开定位权限");
                }
        ).start();


    }

    private void startLocation() {

        locationService = ((FixApplication) getApplication()).locationService;
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
                            SPUtils.getInstance().put(Constans.SAVE_LOCATION_REGION_NAME, bdLocation.getCity());
                            adapter.updateLocateState(LocateState.SUCCESS, bdLocation.getCity());
                        } else {
                            adapter.updateLocateState(LocateState.FAILED, "定位失败");
                        }
                    }
                });
            } else {
                adapter.updateLocateState(LocateState.FAILED, "定位失败");
            }

            if (locationService != null) {
                locationService.unregisterListener(mListener); //注销掉监听
                locationService.stop(); //停止定位服务
            }
        }
    };


    private void getCityList() {
        showLoadingBar();
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCityList(TUtils.getParams());

        API.getList(call, CityInfo.class, new ApiCallBackList<CityInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CityInfo> data) {
                dismissLoadingBar();
                if (data != null && data.size() > 0) {
                    mAllCities = data;
                    Collections.sort(mAllCities, new Comparator<CityInfo>() {
                        @Override
                        public int compare(CityInfo o1, CityInfo o2) {
                            return o1.pinyin.compareTo(o2.pinyin);
                        }
                    });


                    initCityList();
                } else {
                    ToastUtils.showShort("" + msg);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort("" + msg);
            }
        });

    }

    private List<CityInfo> subAllCities(String keys) {
        List<CityInfo> subCitys = new ArrayList<>();
        List<CityInfo> allCitys = null;
//        List<CityInfo> allCitys = TUtils.getAllCitys();
        for (CityInfo item : allCitys) {
            if (item.region_name.contains(keys)) {
                subCitys.add(item);
            }
        }
        return subCitys;
    }

    private void initCityList() {

//        if (cityInfos != null) {
//            for (CityInfo item : cityInfos) {
//                if (item.getName().startsWith("重庆")) {
//                    item.setPinyin("chongqing");
//                } else if (item.getName().startsWith("长沙")) {
//                    item.setPinyin("changsha");
//                } else if (item.getName().startsWith("厦门")) {
//                    item.setPinyin("xiamen");
//                } else if (item.getName().startsWith("长治")) {
//                    item.setPinyin("changzhi");
//                } else if (item.getName().startsWith("赤峰")) {
//                    item.setPinyin("chifeng");
//                } else if (item.getName().startsWith("朝阳")) {
//                    item.setPinyin("chaoyang");
//                } else if (item.getName().startsWith("长春")) {
//                    item.setPinyin("changchun");
//                } else {
//                    item.setPinyin(PinyinUtils.getPingYin(item.getName()));
//                }
//                mAllCities.add(item);
//            }
//        }


        adapter.setNewData(mAllCities);
        listviewAllCity.setAdapter(adapter);
        adapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {

            @Override
            public void onCityClick(CityInfo cityInfo) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, cityInfo);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCityNameClick(String name) {
//                back(name, true);

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                CityInfo cityInfo = new CityInfo(name, "");
                cityInfo.setName(name);
                cityInfo.region_name = name;
                cityInfo.region_id = getReginId(name);
                bundle.putSerializable(Constans.PASS_OBJECT, cityInfo);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLocateClick() {
                adapter.updateLocateState(LocateState.LOCATING, null);
                startLocation();


            }
        });

//        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.LOCATON_CITY_NAME))) {
//            adapter.updateLocateState(LocateState.SUCCESS, SPUtils.getInstance().getString(Constans.LOCATON_CITY_NAME));
//        }


        //关键字
        etInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mAllCities = null;
                //TODO 修改
//                mAllCities = TUtils.getAllCitys();
                if (mAllCities != null && mAllCities.size() > 0) {
                    if (subAllCities(etInputText.getEditableText().toString()).size() > 0) {
                        adapter.setNewData(subAllCities(etInputText.getEditableText().toString()));
                        rlSeleceCityUi.setVisibility(View.VISIBLE);
                        llErrorRefresh.setVisibility(View.GONE);
                    } else {
                        rlSeleceCityUi.setVisibility(View.GONE);
                        llErrorRefresh.setVisibility(View.VISIBLE);
                    }
//                        if (!TextUtils.isEmpty()) {
//                            adapter.updateLocateState(LocateState.SUCCESS, loactionCity);
//                        }
                }
            }
        });


        sideLetterBar.setOverlay(tvLetterOverlay);
        sideLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = adapter.getLetterPosition(letter);
                listviewAllCity.setSelection(position);
                tvLetterOverlay.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getReginId(String name) {
        for (CityInfo cityInfo : mAllCities) {
            if (cityInfo.region_name.equals(name)) {
                return cityInfo.region_id;
            }
        }
        return SPUtils.getInstance().getString(Constans.SAVE_LOCATION_REGION_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
    }
}

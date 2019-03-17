package com.task.system.activity;

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

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.CityListAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.CityInfo;
import com.task.system.bean.LocateState;
import com.task.system.utils.TUtils;
import com.task.system.views.SideLetterBar;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);

        tvTittle.setText("定位选择");

        mAllCities = TUtils.getAllCitys();

        initCityList();
    }

    private void getCityList1() {
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCityList(TUtils.getParams());

        API.getList(call, CityInfo.class, new ApiCallBackList<CityInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<CityInfo> data) {
                if (data != null && data.size() > 0) {
                    TUtils.setAllCitys(data);
                    initCityList();
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                LogUtils.w("dyc", msg);
            }
        });

    }

    private List<CityInfo> subAllCities(String keys) {
        List<CityInfo> subCitys = new ArrayList<>();
        List<CityInfo> allCitys = TUtils.getAllCitys();
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


        adapter = new CityListAdapter(mContext, mAllCities);
        listviewAllCity.setAdapter(adapter);
        adapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {

            @Override
            public void onCityClick(CityInfo cityInfo) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT,cityInfo);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onCityNameClick(String name) {
//                back(name, true);

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_STRING,name);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onLocateClick() {
                adapter.updateLocateState(LocateState.LOCATING, null);
//                    locationService.start();// 定位SDK


            }
        });

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.LOCATON_CITY_NAME))) {
            adapter.updateLocateState(LocateState.SUCCESS,SPUtils.getInstance().getString(Constans.LOCATON_CITY_NAME));
        }


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

                mAllCities = TUtils.getAllCitys();
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


}

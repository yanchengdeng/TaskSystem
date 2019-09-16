package com.task.system.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.bean.UserExt;
import com.task.system.fragments.ApplyEnterpriseFragment;
import com.task.system.fragments.ApplyPersonFragment;
import com.task.system.utils.TUtils;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.yc.lib.api.ApiConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: dengyancheng
 * Date: 2019-09-12 20:32
 * Description: 我的申请  支持个人  企业
 * History:
 */
public class MyApplyNewActivity extends BaseActivity {

    @BindView(R.id.tv_title_top)
    TextView tvTitleTop;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.et_keys)
    EditText etKeys;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.tv_smart_sort)
    TextView tvSmartSort;
    @BindView(R.id.iv_smart_sort)
    ImageView ivSmartSort;
    @BindView(R.id.ll_smart_sort_ui)
    LinearLayout llSmartSortUi;
    @BindView(R.id.ll_smart_sort)
    LinearLayout llSmartSort;
    @BindView(R.id.viewpage)
    ViewPager viewpage;

    private Unbinder unbinder;

    private UserExt.BussinessInfo bussinessInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_fragment);
        unbinder =   ButterKnife.bind(this);

        if (getIntent() != null && getIntent().getSerializableExtra(Constans.PASS_OBJECT) != null) {
            bussinessInfo = (UserExt.BussinessInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
            if (bussinessInfo != null) {

            }
        }else{
            finish();
            return;
        }
        setTitle("我的申请");
        tvTitleTop.setVisibility(View.GONE);
        FragmentPagerItems pages = getPagerItems(new FragmentPagerItems(ApiConfig.context));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        viewpage.setAdapter(adapter);
        viewpage.setOffscreenPageLimit(TUtils.getTaskType().size());

        tabLayout.setupWithViewPager(viewpage);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        llSmartSort.setVisibility(View.GONE);

    }

    private FragmentPagerItems getPagerItems(FragmentPagerItems fragmentPagerItems) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constans.PASS_OBJECT, bussinessInfo);
        fragmentPagerItems.add(FragmentPagerItem.of("个人申请", ApplyPersonFragment.class, bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putSerializable(Constans.PASS_OBJECT, bussinessInfo);
        fragmentPagerItems.add(FragmentPagerItem.of("企业申请", ApplyEnterpriseFragment.class, bundle1));
        return fragmentPagerItems;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder!=null){
            unbinder.unbind();
        }

    }
}

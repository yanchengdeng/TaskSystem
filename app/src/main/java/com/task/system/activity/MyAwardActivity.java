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
import com.task.system.fragments.AwardListFragment;
import com.task.system.utils.TUtils;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.yc.lib.api.ApiConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAwardActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_fragment);
        setTitle("我的奖品");
        unbinder =   ButterKnife.bind(this);
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
        bundle.putInt(Constans.PASS_STRING, 0);
        fragmentPagerItems.add(FragmentPagerItem.of("未兑换", AwardListFragment.class, bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putInt(Constans.PASS_STRING, 1);
        fragmentPagerItems.add(FragmentPagerItem.of("已兑换", AwardListFragment.class, bundle1));
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

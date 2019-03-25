package com.task.system.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.adapters.MenuAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.HomeMenu;
import com.task.system.bean.SortTags;
import com.task.system.bean.TaskType;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.task.system.views.BubblePopupSingle;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import razerdp.basepopup.BasePopupWindow;
import retrofit2.Call;

public class TaskFragment extends Fragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpage)
    ViewPager viewpage;
    Unbinder unbinder;
    @BindView(R.id.tv_smart_sort)
    TextView tvSmartSort;
    @BindView(R.id.iv_smart_sort)
    ImageView ivSmartSort;
    @BindView(R.id.ll_smart_sort)
    LinearLayout llSmartSort;
    @BindView(R.id.status_view)
    View statusView;


    private BubblePopupSingle quickPopupSmart;
    private MenuAdapter menuAdapter;
    private String sort_id;

    /**
     * 1: "待工作",
     * 2: "待提交",
     * 3: "待审核",
     * 4: "已通过",
     * 5: "未通过"
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ImmersionBar.getStatusBarHeight(getActivity()));
            statusView.setLayoutParams(params);
        }
        for (TaskType item : TUtils.getTaskType()) {
            tabLayout.addTab(tabLayout.newTab().setText(item.name));
        }

        FragmentPagerItems pages = getPagerItems(new FragmentPagerItems(ApiConfig.context));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewpage.setAdapter(adapter);
        viewpage.setOffscreenPageLimit(TUtils.getTaskType().size());

        tabLayout.setupWithViewPager(viewpage);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        menuAdapter = new MenuAdapter(R.layout.adapter_drop_menu_item);

        getSmartSort();

        llSmartSort.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showSmartSort();
            }
        });


        return view;
    }


    private FragmentPagerItems getPagerItems(FragmentPagerItems pages) {
        for (TaskType item : TUtils.getTaskType()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constans.PASS_STRING, item.type);
            pages.add(FragmentPagerItem.of(item.name, TaskListFragment.class, bundle));
        }

        return pages;
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
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
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
            quickPopupSmart.showPopupWindow(llSmartSort);
            ivSmartSort.setImageResource(R.mipmap.arrwo_up_red);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

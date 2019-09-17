package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.HomeAdapter;
import com.task.system.adapters.MenuAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.CatergoryInfo;
import com.task.system.bean.HomeMenu;
import com.task.system.bean.SortTags;
import com.task.system.bean.TaskInfoList;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.task.system.views.BubblePopupSingle;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import razerdp.basepopup.BasePopupWindow;
import retrofit2.Call;

//任务搜索
public class TaskListActivity extends BaseActivity {

    @BindView(R.id.et_input_text)
    EditText etInputText;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.ll_seach_ui)
    LinearLayout llSeachUi;
    @BindView(R.id.tv_sort)
    TextView tvSort;
    @BindView(R.id.iv_sort)
    ImageView ivSort;
    @BindView(R.id.ll_all_sort)
    LinearLayout llAllSort;
    @BindView(R.id.tv_smart_tag)
    TextView tvSmartTag;
    @BindView(R.id.iv_smart_tag)
    ImageView ivSmartTag;
    @BindView(R.id.ll_smart_tag)
    LinearLayout llSmartTag;
    @BindView(R.id.ll_sort_ui)
    LinearLayout llSortUi;
    @BindView(R.id.recycle_tag)
    RecyclerView recycleTag;

    private HomeAdapter homeAdapter;
    private int page = 1;
    private String region_id;
    private String category_id;
    private String sort_id;
    private String tags_id;
    private String keywords;

    //  分类   标签
    private BubblePopupSingle quickPopupSort;
    private BubblePopupSingle quickPopupTag;
    private MenuAdapter menuSortAdapter, menuTagAdapter;

    private CatergoryInfo catergoryInfo;

    private  BaseQuickAdapter tagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);

        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recycle.setAdapter(homeAdapter);

        menuSortAdapter = new MenuAdapter(R.layout.adapter_drop_menu_item);
        menuTagAdapter = new MenuAdapter(R.layout.adapter_drop_menu_item);

        getSmartSort();

        tagsAdapter =new BaseQuickAdapter<HomeMenu,BaseViewHolder>(R.layout.tag_menu,new ArrayList()) {


            @Override
            protected void convert(BaseViewHolder helper, HomeMenu item) {
                ((TextView)helper.getView(R.id.tv_tab_name)).setText(item.title);
                if (item.isSelected){
                    helper.getView(R.id.tv_tab_name).setBackgroundColor(getResources().getColor(R.color.cp_gray));
                    ((TextView) helper.getView(R.id.tv_tab_name)).setTextColor(getResources().getColor(R.color.white));
                }else{
                    helper.getView(R.id.tv_tab_name).setBackgroundColor(getResources().getColor(R.color.white));
                    ((TextView) helper.getView(R.id.tv_tab_name)).setTextColor(getResources().getColor(R.color.color_info));
                }
            }
        };

        tagsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i =0;i<tagsAdapter.getData().size();i++){
                    ((HomeMenu)tagsAdapter.getItem(i)).isSelected = false;
                }

                ((HomeMenu)tagsAdapter.getItem(position)).isSelected = true;
                tagsAdapter.notifyDataSetChanged();
                tags_id = ((HomeMenu)tagsAdapter.getItem(position)).id;
                page=1;
                getTaskList();
            }
        });


        recycleTag.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
       recycleTag.setAdapter(tagsAdapter);

        catergoryInfo = (CatergoryInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
        if (catergoryInfo != null) {
            tvTittle.setText("" + catergoryInfo.title);
            llSortUi.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_seach_ui).setVisibility(View.GONE);
            category_id = catergoryInfo.id;
            recycleTag.setVisibility(View.VISIBLE);
        } else {
            tvTittle.setText("搜索任务");
            llSortUi.setVisibility(View.GONE);
            findViewById(R.id.ll_seach_ui).setVisibility(View.VISIBLE);
        }

        region_id = SPUtils.getInstance().getString(Constans.SAVE_LOCATION_REGION_ID);

        getTaskList();

        getAllSort();

        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTaskList();

            }
        }, recycle);


        etInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(etInputText.getEditableText().toString())) {
                        ToastUtils.showShort("请输入关键字");
                        return true;
                    } else {
                        KeyboardUtils.hideSoftInput(mContext);
                    }
                    keywords = etInputText.getEditableText().toString();
                    page = 1;
                    getTaskList();
                    return true;

                }
                return false;
            }
        });


        homeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constans.PASS_STRING, homeAdapter.getData().get(position).id);
            ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
        });


        llAllSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmartSort();
            }
        });

        llSmartTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShowTags();
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

                    menuTagAdapter.setNewData( parseToMenu(data));

                    CatergoryInfo item = null;
                    for (CatergoryInfo catergoryInfo:data){
                        if (catergoryInfo.id.equals(category_id)){
                            item = catergoryInfo;
                        }
                    }

                    if (item!=null){
                        if (!TextUtils.isEmpty(item.title)){
                            tvSmartTag.setText("" +item.title);
                        }
                    }
                }
//                smartRefresh.finishRefresh();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
//                smartRefresh.finishRefresh();
            }
        });
    }

    private  List<HomeMenu> parseToMenu(List<CatergoryInfo> data) {
        List<HomeMenu> menus = new ArrayList<>();
        for (CatergoryInfo item:data){
            if (item!=null && item._child!=null && item._child.size()>0){
                for (CatergoryInfo child:item._child){
                    HomeMenu menu = new HomeMenu();
                    menu.id = child.id;
                    menu.title = child.title;
                    menus.add(menu);
                }
            }

        }
        return menus;
    }

    //智能分类
    private void getSmartSort() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getSortTagsList(TUtils.getParams());

        API.getObject(call, SortTags.class, new ApiCallBack<SortTags>() {
            @Override
            public void onSuccess(int msgCode, String msg, SortTags data) {
                if (data != null) {
                    if (data.sort != null) {
                        menuSortAdapter.setNewData(data.sort);
                        tagsAdapter.setNewData(data.tags);
//                        if (data.tags!=null && data.tags.size()>0) {
//                            tvSmartTag.setText("" + data.tags.get(0).title);
//                        }
//
//                        if (data.sort!=null && data.sort.size()>0) {
//                            tvSort.setText("" + data.sort.get(0).title);
//                        }
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
            maps.put("tags_id",tags_id);
        }
        if (!TextUtils.isEmpty(keywords)) {
            maps.put("keywords", keywords);
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskList(TUtils.getParams(maps));

        API.getObject(call, TaskInfoList.class, new ApiCallBack<TaskInfoList>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoList data) {
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page, smartRefresh);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle, smartRefresh);

            }
        });
    }

    @OnClick(R.id.iv_clear)
    public void onViewClicked() {
        keywords = "";
        etInputText.setText("");
        etInputText.setHint("请输入差事");
        page = 1;
        getTaskList();
    }


    private void showSmartSort() {

        if (quickPopupSort == null) {
            quickPopupSort = new BubblePopupSingle(ApiConfig.context);
            RecyclerView recyclerView = quickPopupSort.getContentView();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight() * 0.5));
            recyclerView.setLayoutParams(layoutParams);
            recyclerView.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
            recyclerView.setAdapter(menuSortAdapter);

            quickPopupSort.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });

            menuSortAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    for (HomeMenu item : menuSortAdapter.getData()) {
                        item.isSelected = false;
                    }
                    menuSortAdapter.getData().get(position).isSelected = true;
                    menuSortAdapter.notifyDataSetChanged();
                    sort_id = menuSortAdapter.getItem(position).id;
                    page = 1;
                    getTaskList();
                    quickPopupSort.dismiss();
                    tvSort.setText("" + menuSortAdapter.getData().get(position).title);
                    tvSort.setTextColor(getResources().getColor(R.color.red));
                    ivSort.setImageResource(R.mipmap.icon_arrow_down);
                }
            });
        }

//
        if (quickPopupSort.isShowing()) {
            quickPopupSort.dismiss();
            ivSort.setImageResource(R.mipmap.icon_arrow_down);
        } else {
            quickPopupSort.showPopupWindow(llSortUi);
            ivSort.setImageResource(R.mipmap.arrwo_up_red);
        }
    }


    //下拉  左侧
    private void showShowTags() {

        if (quickPopupTag == null) {
            quickPopupTag = new BubblePopupSingle(ApiConfig.context);
            RecyclerView recyclerView = quickPopupTag.getContentView();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight() * 0.5));
            recyclerView.setLayoutParams(layoutParams);
            recyclerView.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
            recyclerView.setAdapter(menuTagAdapter);

            quickPopupTag.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivSmartTag.setImageResource(R.mipmap.icon_arrow_down);
                }
            });

            menuTagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    for (HomeMenu item : menuTagAdapter.getData()) {
                        item.isSelected = false;
                    }
                    menuTagAdapter.getData().get(position).isSelected = true;
                    menuTagAdapter.notifyDataSetChanged();
                    category_id = catergoryInfo.id;
                    page = 1;
                    getTaskList();
                    quickPopupTag.dismiss();
                    tvSmartTag.setText("" + menuTagAdapter.getData().get(position).title);
                    tvSmartTag.setTextColor(getResources().getColor(R.color.red));
                    ivSmartTag.setImageResource(R.mipmap.icon_arrow_down);
                    setTitle("" + menuTagAdapter.getData().get(position).title);
                }
            });
        }

//
        if (quickPopupTag.isShowing()) {
            quickPopupTag.dismiss();
            ivSmartTag.setImageResource(R.mipmap.icon_arrow_down);
        } else {
            quickPopupTag.showPopupWindow(llSortUi);
            ivSmartTag.setImageResource(R.mipmap.arrwo_up_red);
        }
    }
}

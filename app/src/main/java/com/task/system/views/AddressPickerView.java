package com.task.system.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.AreaBean;
import com.task.system.utils.PerfectClickListener;

import java.util.ArrayList;
import java.util.List;

public class AddressPickerView extends RelativeLayout {
    // recyclerView 选中Item 的颜色
    private int defaultSelectedColor = Color.parseColor("#FF2500");
    // recyclerView 未选中Item 的颜色
    private int defaultUnSelectedColor = Color.parseColor("#333333");
    // 确定字体不可以点击时候的颜色
    private int defaultSureUnClickColor = Color.parseColor("#5cFF2500");
    // 确定字体可以点击时候的颜色
    private int defaultSureCanClickColor = Color.parseColor("#FF2500");

    private Context mContext;
    private int defaultTabCount = 3; //tab 的数量
    private TabLayout mTabLayout; // tabLayout
    private ViewPager mViewPager;
    private String defaultProvince = "省份"; //显示在上面tab中的省份
    private String defaultCity = "城市"; //显示在上面tab中的城市
    private String defaultDistrict = "区域"; //显示在上面tab中的区县

    private List<AreaBean> mRvProvince = new ArrayList<>();
    private List<AreaBean> mRvCity = new ArrayList<>();
    private List<AreaBean> mRvDistrict = new ArrayList<>();// 用来在recyclerview显示的数据

    private ProvinceAdapter mProvinceAdapter;   // recyclerview 的 adapter
    private CityAdapter mCityAdapter;  //
    private DicAreaAdapter mDistrictAdapter;

    private AreaBean mSelectProvince; //选中 省份 bean
    private AreaBean mSelectCity;//选中 城市  bean
    private AreaBean mSelectDistrict;//选中 区县  bean
    private int mSelectProvicePosition = 0; //选中 省份 位置
    private int mSelectCityPosition = 0;//选中 城市  位置
    private int mSelectDistrictPosition = 0;//选中 区县  位置

    private int tabSelectPosition = 0;

    private OnAddressPickerSureListener mOnAddressPickerSureListener;
    private TextView mTvSure; //确定

    public AddressPickerView(Context context) {
        super(context);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        // UI
        View rootView = inflate(mContext, R.layout.layout_address_picker, this);
        // 确定
        mTvSure = rootView.findViewById(R.id.tv_sure);
        mTvSure.setTextColor(defaultSureUnClickColor);
        // tablayout初始化
        initTablayout(rootView);
        // recyclerview adapter的绑定
        // 初始化默认的本地数据  也提供了方法接收外面数据
        initViewPager(rootView, context);
        mViewPager.setCurrentItem(0, false);
        mTvSure.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                sure();
            }
        });
    }

    private void initViewPager(View rootView, Context context) {
        mProvinceAdapter = new ProvinceAdapter(R.layout.item_address, new ArrayList());
        mCityAdapter = new CityAdapter(R.layout.item_address, new ArrayList());
        mDistrictAdapter = new DicAreaAdapter(R.layout.item_address, new ArrayList());
        mViewPager = rootView.findViewById(R.id.viewpager);
        LayoutInflater inflater = LayoutInflater.from(context);

        List<View> viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        viewList.add(inflater.inflate(R.layout.layout_recyclerview, null));
        viewList.add(inflater.inflate(R.layout.layout_recyclerview, null));
        viewList.add(inflater.inflate(R.layout.layout_recyclerview, null));

        mViewPager.setAdapter(new MyPagerAdapter(viewList));
    }

    private void initTablayout(View rootView) {
        mTabLayout = rootView.findViewById(R.id.tablayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultProvince));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultCity));
        mTabLayout.addTab(mTabLayout.newTab().setText(defaultDistrict));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelectPosition = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        mProvinceAdapter.setNewData(mRvProvince);
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        // 点到城市的时候要判断有没有选择省份
                        if (mSelectProvince != null) {
                            mRvCity.clear();
                            mCityAdapter.setNewData(mRvProvince.get(mSelectProvicePosition).get_child());
                            mViewPager.setCurrentItem(1, false);
                        } else {
                            Toast.makeText(mContext, "请您先选择省份", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        // 点到区的时候要判断有没有选择省份与城市
                        if (mSelectProvince != null && mSelectCity != null) {
                            mRvDistrict.clear();
                            mDistrictAdapter.setNewData(mRvProvince.get(mSelectProvicePosition).get_child().get(mSelectCityPosition).get_child());
                            mViewPager.setCurrentItem(2, false);
                        } else {
                            Toast.makeText(mContext, "请您先选择省份与城市", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    /**
     * 开放给外部传入数据
     * 暂时就用这个Bean模型，如果数据不一致就需要各自根据数据来生成这个bean了
     */
    public void initData(List<AreaBean> bean) {
        if (bean != null) {
            mSelectDistrict = null;
            mSelectCity = null;
            mSelectProvince = null;
            mTabLayout.getTabAt(0).select();

            mRvProvince.clear();
            mRvProvince.addAll(bean);
            mRvCity.clear();
            mRvDistrict.clear();
            mProvinceAdapter.setNewData(mRvProvince);
        }
    }

    //点确定
    private void sure() {
        if (mSelectProvince != null &&
                mSelectCity != null &&
                mSelectDistrict != null) {
            //   回调接口
            if (mOnAddressPickerSureListener != null) {
                mOnAddressPickerSureListener.onSureClick(  mSelectDistrict.getRegion_name(),
                        mSelectProvince.getId() + "", mSelectCity.getId() + "", mSelectDistrict.getId() + "");
            }
        } else {
            Toast.makeText(mContext, "地址还没有选完整", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRvProvince = null;
    }


    /**
     * 下面显示数据的adapter
     */
    class ProvinceAdapter extends BaseQuickAdapter<AreaBean, BaseViewHolder> {

        public ProvinceAdapter(int layoutResId, List<AreaBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final AreaBean item) {
            final TextView tvAddress = helper.getView(R.id.tv_address);
            tvAddress.setText(item.getRegion_name());
            tvAddress.setTextColor(defaultUnSelectedColor);

            // 设置选中效果的颜色
            if (mSelectProvince != null &&
                    item.getRegion_name().equals(mSelectProvince.getRegion_name())) {
                tvAddress.setTextColor(defaultSelectedColor);
            }
            // 设置点击之后的事件
            tvAddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击 分类别
                    tvAddress.setTextColor(defaultSelectedColor);
                    mSelectProvince = item;
                    mSelectProvicePosition = helper.getLayoutPosition();

                    // 清空后面两个的数据
                    mSelectCity = null;
                    mSelectDistrict = null;
                    mSelectCityPosition = 0;
                    mSelectDistrictPosition = 0;
                    mTabLayout.getTabAt(1).setText(defaultCity);
                    mTabLayout.getTabAt(2).setText(defaultDistrict);
                    // 设置这个对应的标题
                    mTabLayout.getTabAt(0).setText(mSelectProvince.getRegion_name());
                    // 跳到下一个选择
                    mTabLayout.getTabAt(1).select();
                    // 灰掉确定按钮
                    mTvSure.setTextColor(defaultSureUnClickColor);
                }
            });
        }
    }

    class CityAdapter extends BaseQuickAdapter<AreaBean, BaseViewHolder> {

        public CityAdapter(int layoutResId, List<AreaBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final AreaBean item) {
            final TextView tvAddress = helper.getView(R.id.tv_address);
            tvAddress.setText(item.getRegion_name());
            tvAddress.setTextColor(defaultUnSelectedColor);

            // 设置选中效果的颜色
            if (mSelectCity != null &&
                    item.getRegion_name().equals(mSelectCity.getRegion_name())) {
                tvAddress.setTextColor(defaultSelectedColor);
            }
            // 设置点击之后的事件
            tvAddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击 分类别
                    tvAddress.setTextColor(defaultSelectedColor);
                    mSelectCity = item;
                    mSelectCityPosition = helper.getLayoutPosition();

                    // 清空后面一个的数据
                    mSelectDistrict = null;
                    mSelectDistrictPosition = 0;
                    mTabLayout.getTabAt(2).setText(defaultDistrict);
                    // 设置这个对应的标题
                    mTabLayout.getTabAt(1).setText(mSelectCity.getRegion_name());
                    // 跳到下一个选择
                    mTabLayout.getTabAt(2).select();
                    // 灰掉确定按钮
                    mTvSure.setTextColor(defaultSureUnClickColor);
                }
            });
        }
    }

    class DicAreaAdapter extends BaseQuickAdapter<AreaBean, BaseViewHolder> {

        public DicAreaAdapter(int layoutResId, List<AreaBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final AreaBean item) {
            final TextView tvAddress = helper.getView(R.id.tv_address);
            tvAddress.setText(item.getRegion_name());
            tvAddress.setTextColor(defaultUnSelectedColor);

            // 设置选中效果的颜色
            if (mSelectDistrict != null &&
                    item.getRegion_name().equals(mSelectDistrict.getRegion_name())) {
                tvAddress.setTextColor(defaultSelectedColor);
            }
            // 设置点击之后的事件
            tvAddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击 分类别
                    tvAddress.setTextColor(defaultSelectedColor);
                    mSelectDistrict = item;
                    mSelectDistrictPosition = helper.getLayoutPosition();

                    // 没了，选完了，这个时候可以点确定了
                    mTabLayout.getTabAt(2).setText(mSelectDistrict.getRegion_name());
                    notifyDataSetChanged();

                    // 确定按钮变亮
                    mTvSure.setTextColor(defaultSureCanClickColor);
                }
            });
        }
    }


    /**
     * 点确定回调这个接口
     */
    public interface OnAddressPickerSureListener {
        void onSureClick(String address, String provinceCode, String cityCode, String districtCode);
    }

    public void setOnAddressPickerSure(OnAddressPickerSureListener listener) {
        this.mOnAddressPickerSureListener = listener;
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> views;

        public MyPagerAdapter(List<View> views) {
            this.views = views;
            for (int i = 0; i < 3; i++) {
                RecyclerView rv = views.get(i).findViewById(R.id.recycler_view);
                rv.setLayoutManager(new LinearLayoutManager(mContext));
                switch (i) {
                    case 0:
                        rv.setAdapter(mProvinceAdapter);
                        break;
                    case 1:
                        rv.setAdapter(mCityAdapter);
                        break;
                    case 2:
                        rv.setAdapter(mDistrictAdapter);
                        break;
                }
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // return super.instantiateItem(container, position);
            View view = views.get(position);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView(views.get(position));
        }
    }

}

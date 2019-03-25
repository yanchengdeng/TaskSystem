package com.task.system.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;


//懒加载

public abstract class BaseFragment extends Fragment {
    /**
     * 父控件布局
     */
    private View mParentView;
    /**
     * 是否使用懒加载
     */
    private boolean isLazyLoad = true;
    /**
     * 是否完成加载
     */
    private boolean isLoadComplete = false;
    /**
     * 是否首次启动
     */
    private boolean isFirstCreate = true;
    /**
     * 是否首次启动Resume
     */
    private boolean isFirstResume = true;
    /**
     * 当前的fragment是否已经暂停
     */
    private boolean isAlreadyPause = false;
    /**
     * 是否从OnPause离开
     */
    private boolean isOnPauseOut = false;


    Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(getAbsLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @LayoutRes
    protected abstract int getAbsLayoutId();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isLazyLoad = configIsLazyLoad();
        if (isVisibleToUser && isLazyLoad && !isFirstCreate && !isLoadComplete) {// fragment可见 && 启用懒加载 && 不是第一次启动 && 未加载完成
            init(mParentView, null);
            isLoadComplete = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoadComplete = false;// fragment被回收时重置加载状态
        mParentView = null;
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentView = view;
        if (!isLazyLoad || getUserVisibleHint()) {// 不使用懒加载 || fragment可见
            init(view, savedInstanceState);
            isLoadComplete = true;
            onFragmentResume();
        }
        isFirstCreate = false;
    }

    private void init(View view, @Nullable Bundle savedInstanceState) {
        beforeCreate();
        beforeFindViews(view);
        initView(view, savedInstanceState);
        initData(view);
        initViewListener(view);
    }

    protected void beforeCreate() {
    }

    protected void beforeFindViews(View view) {
    }

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected void initData(View view) {
    }

    protected void initViewListener(View view) {
    }

    /**
     * 配置是否使用懒加载（默认使用，可重写）
     */
    protected boolean configIsLazyLoad() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint() && isLoadComplete) {//自己显示 && 已经加载
            if (getParentFragment() != null && !getParentFragment().getUserVisibleHint()) {// 父类不显示
                return;
            }
            if (isOnPauseOut) {//自己是从OnPause回来的
                onFragmentResume();
                isOnPauseOut = false;
            }
        }
    }

    /**
     * FragmentResume时调用，与activity生命周期保持一致
     */
    protected void onFragmentResume() {
        isAlreadyPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint() && isLoadComplete && !isAlreadyPause) {// 自己显示 && 已加载完成 && 未调用过pause方法
            isOnPauseOut = true;
        }
    }
}

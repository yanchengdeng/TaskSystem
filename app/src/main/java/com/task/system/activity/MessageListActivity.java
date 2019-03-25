package com.task.system.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.fragments.FragmentMessage;
import com.task.system.views.FragmentPagerItem;
import com.task.system.views.FragmentPagerItems;
import com.yc.lib.api.ApiConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2019/3/24  23:39
 * Email: yanchengdeng@gmail.com
 * Describle: 消息列表
 */
public class MessageListActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpage)
    ViewPager viewpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        setTitle("消息");

        tablayout.addTab(tablayout.newTab().setText("系统消息"));
        tablayout.addTab(tablayout.newTab().setText("个人消息"));

        FragmentPagerItems pages = getPagerItems(new FragmentPagerItems(ApiConfig.context));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);
        viewpage.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpage);
    }

    //0-系统消息,1-个人消息
    private FragmentPagerItems getPagerItems(FragmentPagerItems pages) {
        Bundle bundleSys = new Bundle();
        bundleSys.putString(Constans.PASS_STRING, "0");
        pages.add(FragmentPagerItem.of("系统消息", FragmentMessage.class, bundleSys));

        Bundle bundlePerson = new Bundle();
        bundlePerson.putString(Constans.PASS_STRING, "1");
        pages.add(FragmentPagerItem.of("个人消息", FragmentMessage.class, bundleSys));

        return pages;
    }
}

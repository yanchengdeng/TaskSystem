package com.task.system.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.utils.TUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_icon)
    TextView tvIcon;
    @BindView(R.id.tv_society_deal)
    TextView tvSocietyDeal;
    @BindView(R.id.tv_private_statament)
    TextView tvPrivateStatament;
    @BindView(R.id.tv_link_custome)
    TextView tvLinkCustome;
    @BindView(R.id.tv_service_deal)
    TextView tvServiceDeal;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setTitle("关于");

        tvIcon.setText(AppUtils.getAppName()+" "+AppUtils.getAppVersionName());


    }

    @OnClick({R.id.tv_society_deal, R.id.tv_private_statament, R.id.tv_link_custome, R.id.tv_service_deal, R.id.tv_about_us})
    public void onViewClicked(View view) {
        Bundle about = new Bundle();
        switch (view.getId()) {
            case R.id.tv_society_deal:

                TUtils.openAbout("社会公约",Constans.SOCITY_ORDER);
//                about.putString(Constans.PASS_NAME, "社会公约");
//                about.putString(Constans.ARTICAL_TYPE, Constans.SOCITY_ORDER);
//                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
            case R.id.tv_private_statament:
                TUtils.openAbout("隐私申明",Constans.PRIVATE_SASTAMENT);
//                about.putString(Constans.PASS_NAME, "隐私申明");
//                about.putString(Constans.ARTICAL_TYPE, Constans.PRIVATE_SASTAMENT);
//                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
            case R.id.tv_link_custome:
                TUtils.openAbout("联系客服",Constans.LINK_CUSTOM);
//                about.putString(Constans.PASS_NAME, "联系客服");
//                about.putString(Constans.ARTICAL_TYPE, Constans.LINK_CUSTOM);
//                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
            case R.id.tv_service_deal:
                TUtils.openAbout("服务条款",Constans.SERVER_ITEM);
//                about.putString(Constans.PASS_NAME, "服务条款");
//                about.putString(Constans.ARTICAL_TYPE, Constans.SERVER_ITEM);
//                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
            case R.id.tv_about_us:
                TUtils.openAbout("关于我们",Constans.ABOUT_US);
//                about.putString(Constans.PASS_NAME, "关于我们");
//                about.putString(Constans.ARTICAL_TYPE, Constans.ABOUT_US);
//                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
        }
    }
}

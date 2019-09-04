package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.imagepicker.bean.ImageItem;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.IndicatorAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.TaskIndicator;
import com.task.system.bean.TaskInfoItem;
import com.task.system.bean.UploadTaskImageItem;
import com.task.system.fragments.TaskStepFragment;
import com.task.system.utils.TUtils;
import com.task.system.utils.Util;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

//任务第一步
public class DoTaskStepActivity extends BaseActivity {

//    @BindView(R.id.rich_step)
//    RichTextView richStep;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.viewpage)
    ViewPager viewPager;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;
    @BindView(R.id.recycle_indicator)
    RecyclerView recyleIndicator;
    private TaskInfoItem taskInfoItem;


    private List<TaskStepFragment> fragments = new ArrayList<>();
    private MyAdapter myAdapter;
    private int currentPosition = 0;

    private IndicatorAdapter indicatorAdapter;

    private HashMap<Integer, UploadTaskImageItem> uploadHash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_task_step);
        ButterKnife.bind(this);

        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);

        if (!TextUtils.isEmpty(taskInfoItem.title)) {
            setTitle(taskInfoItem.title);
        }


        if (taskInfoItem.task_step!=null && taskInfoItem.task_step.size()>1){
            //显示步骤引导
            indicatorAdapter = new IndicatorAdapter(R.layout.adapter_step_index,parseIndicatorData(taskInfoItem.task_step));
            recyleIndicator.setVisibility(View.VISIBLE);
            recyleIndicator.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            recyleIndicator.setAdapter(indicatorAdapter);
        }


        if (taskInfoItem.task_step != null && taskInfoItem.task_step.size() > 0) {
            getPagerItems();
            myAdapter = new MyAdapter(getSupportFragmentManager());
            viewPager.setAdapter(myAdapter);
            viewPager.setOffscreenPageLimit(taskInfoItem.task_step.size());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    currentPosition = i;
                    if (i == fragments.size() - 1) {
                        tvNextStep.setText("提交");
                    } else {
                        tvNextStep.setText("下一步");
                    }

                    updateIndicators(i);

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            if (fragments.size() ==1) {
                tvNextStep.setText("提交");
            }

        }


//        richStep.setOnImageClickListener(new RichTextView.ImageClickListener() {
//            @Override
//            public void onImageClick(String imageUrl, String[] imageUrls, int position) {
//
//                TUtils.openImageViews(imageUrls, position);
//
//            }
//        });


    }

    //更新指引数据
    private void updateIndicators(int index) {
        if (indicatorAdapter!=null && indicatorAdapter.getData()!=null && indicatorAdapter.getData().size()>0) {
            for (int i = 0;i<indicatorAdapter.getData().size();i++) {
//                indicatorAdapter.getData().get(i).isSelect = false;
            }

            indicatorAdapter.getData().get(index).isSelect = true;

            indicatorAdapter.notifyDataSetChanged();

        }

    }

    //初始话引导数据
    private ArrayList<TaskIndicator> parseIndicatorData(List<TaskInfoItem.TaskStep> task_step) {
        ArrayList<TaskIndicator> taskIndicators = new ArrayList<>();
        for (int i = 0;i<task_step.size();i++){
            TaskIndicator taskIndicator = new TaskIndicator();
            if (i ==0){
                taskIndicator.isSelect = true;
                taskIndicator.isShowLineLeft = false;
                taskIndicator.num =i+1;
            }else if (i == task_step.size()-1){
                taskIndicator.isSelect = false;
                taskIndicator.isShowLineLeft = true;
                taskIndicator.num =i+1;
            }else{
                taskIndicator.isSelect = false;
                taskIndicator.isShowLineLeft = true;
                taskIndicator.num =i+1;
            }
            taskIndicators.add(taskIndicator);
        }
        return taskIndicators;
    }

    private void getPagerItems() {
        for (int i = 0; i < taskInfoItem.task_step.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constans.PASS_OBJECT, taskInfoItem.task_step.get(i));
            TaskStepFragment fragment = new TaskStepFragment();
            fragment.setArguments(bundle);
            fragments.add(fragment);
            UploadTaskImageItem uploadTaskImageItem = new UploadTaskImageItem();
            uploadTaskImageItem.imageItems = new ArrayList<>();
            uploadTaskImageItem.photoPaths = new ArrayList<>();
            uploadHash.put(i, uploadTaskImageItem);
        }
    }

    @OnClick({R.id.tv_custome, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_next_step:
                if (taskInfoItem.task_step==null || taskInfoItem.task_step.size()==0){
                    ToastUtils.showShort("不存在任务步骤");
                    return;
                }
                if (tvNextStep.getText().toString().equals("下一步")) {

                    doLoadAction(false);
                } else {
                    //提交数

                    doLoadAction(true);
                }
                break;
        }
    }

    private void doLoadAction(boolean isLastStep) {

        if (fragments.get(viewPager.getCurrentItem()).selImageList.size()==0 && TextUtils.isEmpty(fragments.get(viewPager.getCurrentItem()).content)) {
           ToastUtils.showShort("请至少完成一项提交");
            return;
        }


        uploadHash.get(viewPager.getCurrentItem()).imageItems = fragments.get(viewPager.getCurrentItem()).selImageList;
        uploadHash.get(viewPager.getCurrentItem()).content = fragments.get(viewPager.getCurrentItem()).content;
        if (fragments.get(viewPager.getCurrentItem()).selImageList.size() > 0) {
            uploadPictures(fragments.get(viewPager.getCurrentItem()).selImageList, uploadHash.get(viewPager.getCurrentItem()).photoPaths);
        } else {
            if (viewPager.getCurrentItem()==fragments.size()-1){
                showLoadingBar("提交中...");
                doUploadTask();
            }else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    }

    /**
     * 上传图片
     *
     * @param selImageList
     * @param photoPaths
     */
    private void uploadPictures(ArrayList<ImageItem> selImageList, List<String> photoPaths) {
        if (viewPager.getCurrentItem()==fragments.size()-1) {
            showLoadingBar("提交中...");
        } else {
            showLoadingBar("上传中...");
        }

        for (ImageItem item : selImageList) {
            updateImageByBase64(new File(item.path), photoPaths);
        }
    }

    //上传头像
    private void updateImageByBase64(File file, List<String> photoPaths) {

        LogUtils.w("dyc---原始文件大小", file.length());
        Luban.with(this)
                .load(file)
                .ignoreBy(Constans.LUBAN_SIZE)
                .setTargetDir(Util.getPath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File lubanFile) {

                        LogUtils.w("dyc---压缩后大小", lubanFile.length());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(lubanFile.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))), photoPaths);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Glide.with(ApiConfig.context)
//                                .load(file)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                                .transition(withCrossFade())
//                                .apply(new RequestOptions().circleCrop().error(R.mipmap.defalut_header))
//                                .into(ivHeader);


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))), photoPaths);
                    }
                }).launch();
    }


    private void doUploadImage(String base64Encode, List<String> photoPaths) {

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("image", "data:image/png;base64," + base64Encode);
        hashMap.put("image_type", "task");
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).uploadImage(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {

            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                ToastUtils.showShort("" + msg);
                photoPaths.add(data.path);
                if (photoPaths.size() == uploadHash.get(viewPager.getCurrentItem()).imageItems.size()) {
                    if (viewPager.getCurrentItem() == fragments.size()-1) {
                        doUploadTask();
                    } else {
                        viewPager.setCurrentItem(currentPosition + 1);
                        dismissLoadingBar();
                    }
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });

    }

    /**
     * 最终上传任务
     */
    private void doUploadTask() {

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("order_id", taskInfoItem.order_id);
        hashMap.put("content", parseHashData());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).uploadContent(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                uploadHash.clear();
                dismissLoadingBar();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort("" + msg);
            }
        });
    }

    private String parseHashData() {

        List<List<String>> allDatas = new ArrayList<>();

        for (Integer key : uploadHash.keySet()) {
            List<String> data = new ArrayList<>();
            data.add(parsePhotoUrls(uploadHash.get(key).photoPaths));
            data.add(uploadHash.get(key).content);
            allDatas.add(data);
        }
        return GsonUtils.toJson(allDatas);
    }

    private String parsePhotoUrls(List<String> photoPaths) {

        if (photoPaths.size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < photoPaths.size(); i++) {
                if (i == photoPaths.size() - 1) {
                    sb.append(photoPaths.get(i));
                } else {
                    sb.append(photoPaths.get(i)).append("|");
                }
                return sb.toString();
            }

        }
        return "";
    }

    public byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
        return outputStream.toByteArray();
    }


    @Override
    public void onBackPressed() {
        if (uploadHash.size()>0) {
            showConfirmDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showConfirmDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content("退出本次编辑？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                }).negativeText("取消").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //定义属于自己的适配器
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        //获得碎片的所有
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //返回碎片的长度
        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}

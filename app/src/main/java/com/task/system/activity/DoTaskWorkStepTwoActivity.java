package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ImagePickerAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.TaskInfoItem;
import com.task.system.common.GlideLoadFileLoader;
import com.task.system.common.RichTextView;
import com.task.system.utils.TUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
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

/**
 * Author: 邓言诚  Create at : 2019/3/23  11:39
 * Email: yanchengdeng@gmail.com
 * Describle:任务工作第一步
 */
public class DoTaskWorkStepTwoActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_pre_styep)
    TextView tvPreStyep;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;
    @BindView(R.id.rich_step_two)
    RichTextView richStepTwo;
    private TaskInfoItem taskInfoItem;
    private ImagePickerAdapter imageAdapter;

    public static final int IMAGE_ITEM_ADD = -1;

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private int maxImgCount = 9;
    private int totalPhoto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_work_step_two);
        ButterKnife.bind(this);
        tvOne.setBackground(getResources().getDrawable(R.drawable.view_unread_gray_bg));
        tvTwo.setBackground(getResources().getDrawable(R.drawable.view_unread_red_bg));

        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
        if (!TextUtils.isEmpty(taskInfoItem.title)) {
            setTitle(taskInfoItem.title);
        }
        imageAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        imageAdapter.setOnItemClickListener(this);
        recycle.setLayoutManager(new GridLayoutManager(this, 3));
        recycle.setHasFixedSize(true);
        recycle.setAdapter(imageAdapter);
        recycle.setNestedScrollingEnabled(false);

//        if (!TextUtils.isEmpty(taskInfoItem.step_2)){
//            richStepTwo.setHtml(taskInfoItem.step_2);
//        }

        richStepTwo.setOnImageClickListener(new RichTextView.ImageClickListener() {
            @Override
            public void onImageClick(String imageUrl, String[] imageUrls, int position) {

                TUtils.openImageViews(imageUrls,position);

            }
        });
        initImagePicker();

    }

    //图片选择
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideLoadFileLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);                      //选中数量限制
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }


    @OnClick({R.id.btn_upload, R.id.tv_custome, R.id.tv_pre_styep, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_upload:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        .onGranted(permissions -> {
                            ImagePicker.getInstance().setSelectedImages(selImageList);
                            Intent intent = new Intent(mContext, ImageGridActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_SELECT);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.showShort("请打开相机权限");
                        })
                        .start();
                break;
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_pre_styep:
                onBackPressed();
                break;
            case R.id.tv_next_step:
                if (selImageList != null && selImageList.size() > 0) {
                    showLoadingBar("上传中...");
                    parseImageToBase64();
//                    ActivityUtils.startActivity(getIntent().getExtras(),DoTaskWordStepThreeActivity.class);
                } else {
//                    ToastUtils.showShort("请上传图片");
                    showJumpTips();
                }
                break;
        }
    }

    private void showJumpTips() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content("是否确定不提交截图？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        ActivityUtils.startActivityForResult(getIntent().getExtras(), DoTaskWorkStepTwoActivity.this, DoTaskWordStepThreeActivity.class, 300);
                    }
                }).negativeText("我点错了").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    //校验上传图片个数有效
    private List<String> base64Images;

    //将图片合并
    private void parseImageToBase64() {
        base64Images = new ArrayList<>();

        for (ImageItem item : selImageList) {
            updateImageByBase64(new File(item.path));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && (requestCode == REQUEST_CODE_SELECT)) {
                selImageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (selImageList != null) {
                    imageAdapter.setImages(selImageList);
                }
            } else if (data != null && requestCode == ImagePicker.REQUEST_CODE_PREVIEW) {
                selImageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (selImageList != null) {
                    imageAdapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                selImageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (selImageList != null) {
                    imageAdapter.setImages(selImageList);
                }
            }
        } else if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    //上传头像
    private void updateImageByBase64(File file) {

        LogUtils.w("dyc---原始文件大小", file.length());
        Luban.with(this)
                .load(file)
                .ignoreBy(30)
//                .setTargetDir(getPath())
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


//                        Glide.with(ApiConfig.context)
//                                .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                                .transition(withCrossFade())
//                                .apply(new RequestOptions().circleCrop().error(R.mipmap.defalut_header))
//                                .into(ivHeader);


                        LogUtils.w("dyc---原始文件大小", lubanFile.length());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(lubanFile.getPath(), options);
                        base64Images.add(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))));

                        finishBase64();
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
                        base64Images.add(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))));
                        finishBase64();
                    }
                }).launch();
    }

    private void finishBase64() {
        if (base64Images.size() == selImageList.size()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < base64Images.size(); i++) {
                if (i < base64Images.size() - 1) {
                    stringBuilder.append("data:image/png;base64,").append(base64Images.get(i)).append("|");
                } else {
                    stringBuilder.append("data:image/png;base64,").append(base64Images.get(i));
                }
            }
            doUploadImage(stringBuilder.toString());
        }
    }

    private void doUploadImage(String base64Encode) {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("task_id", taskInfoItem.id);
        hashMap.put("order_id", taskInfoItem.order_id);
        hashMap.put("images", base64Encode);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).uploadIamges(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                ActivityUtils.startActivityForResult(getIntent().getExtras(), DoTaskWorkStepTwoActivity.this, DoTaskWordStepThreeActivity.class, 300);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort("" + msg);
            }
        });

    }

    public byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


    @Override
    public void onBackPressed() {
        if ((selImageList != null && selImageList.size() > 0)) {
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
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
//                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(DoTaskWorkStepTwoActivity.this, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imageAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, ImagePicker.REQUEST_CODE_PREVIEW);
                break;
        }
    }


}

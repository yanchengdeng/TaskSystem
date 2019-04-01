package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.common.GlideLoadFileLoader;
import com.task.system.utils.TUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.ImageLoaderUtil;

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

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PersonSettingActivity extends BaseActivity {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.iv_header_arrow)
    ImageView ivHeaderArrow;
    @BindView(R.id.rl_header_ui)
    RelativeLayout rlHeaderUi;
    @BindView(R.id.iv_user_name_arrow)
    ImageView ivUserNameArrow;
    @BindView(R.id.rl_name_ui)
    RelativeLayout rlNameUi;
    @BindView(R.id.iv_phone_arrow)
    ImageView ivPhoneArrow;
    @BindView(R.id.rl_phone_ui)
    RelativeLayout rlPhoneUi;
    @BindView(R.id.iv_system_id_arrow)
    ImageView ivSystemIdArrow;
    @BindView(R.id.rl_sysyte_id_ui)
    RelativeLayout rlSysyteIdUi;
    @BindView(R.id.tv_modify_password_ui)
    TextView tvModifyPasswordUi;
    @BindView(R.id.tv_login_out)
    TextView tvLoginOut;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 10;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_uid)
    TextView tvUid;
    private boolean  isUplaodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
        ButterKnife.bind(this);
        setTitle("个人设置");
        initImagePicker();
        UserInfo userInfo = TUtils.getUserInfo();
        initData(userInfo);

    }

    private void initData(UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.avatar)) {
            ImageLoaderUtil.loadCircle(userInfo.avatar, ivHeader, R.mipmap.defalut_header);
        }

        if (!TextUtils.isEmpty(userInfo.username)) {
            tvUserName.setText(userInfo.username);
        }

        if (!TextUtils.isEmpty(userInfo.mobile)) {
            tvPhone.setText(TUtils.getHidePhone());
        }

        if (!TextUtils.isEmpty(userInfo.uid)) {
            tvUid.setText(userInfo.uid);
        }
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


    @OnClick({R.id.rl_header_ui, R.id.rl_name_ui, R.id.rl_phone_ui, R.id.rl_sysyte_id_ui, R.id.tv_modify_password_ui, R.id.tv_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_header_ui:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        .onGranted(permissions -> {
                            ImagePicker.getInstance().setSelectLimit(1);
                            Intent intent = new Intent(mContext, ImageGridActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_SELECT);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.showShort("请打开相机权限");
                        })
                        .start();
                break;
            case R.id.rl_name_ui:
                ActivityUtils.startActivity(ModifyUserNameActivity.class);
                break;
            case R.id.rl_phone_ui:
                ActivityUtils.startActivity(ModifyPhoneActivity.class);
                break;
//            case R.id.rl_sysyte_id_ui:
//                break;
            case R.id.tv_modify_password_ui:
                ActivityUtils.startActivity(ModifyPasswordActivity.class);
                break;
            case R.id.tv_login_out:
                showExitDialog();
                break;
        }
    }


    //退出登陆
    private void showExitDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context)
                .title("温馨提示")
                .content("确定退出登录？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        doLoginOutAction();

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

    private void doLoginOutAction() {
        if (TextUtils.isEmpty(TUtils.getUserId())) {
            loginOutAciton();
            return;
        }
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).doLoginOut(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                dismissLoadingBar();
                loginOutAciton();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                loginOutAciton();

            }
        });
    }

    private void loginOutAciton() {
        TUtils.clearUserInfo();
        ToastUtils.showShort("退出登录");
        ActivityUtils.startActivity(LoginActivity.class);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                List<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    File file = new File(images.get(0).path);
                    updateImageByBase64(file);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                List<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    File file = new File(images.get(0).path);
                    updateImageByBase64(file);
                }
            }
        }
    }

    //上传头像
    private void updateImageByBase64(File file) {

        LogUtils.w("dyc---原始文件大小", file.length());
        showLoadingBar("上传中...");
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



                        Glide.with(ApiConfig.context)
                                .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                                .transition(withCrossFade())
                                .apply(new RequestOptions().circleCrop().error(R.mipmap.defalut_header))
                                .into(ivHeader);



                        LogUtils.w("dyc---原始文件大小", lubanFile.length());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(lubanFile.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Glide.with(ApiConfig.context)
                                .load(file)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                                .transition(withCrossFade())
                                .apply(new RequestOptions().circleCrop().error(R.mipmap.defalut_header))
                                .into(ivHeader);



                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(getBytesByBitmap(bitmap))));
                    }
                }).launch();
    }

    private void doUploadImage(String base64Encode) {




        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("avatar", "data:image/png;base64," + base64Encode);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).setUserAvatar(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                isUplaodImage = true;
//                EventBus.getDefault().post(new UpdateUserInfoEvent());
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                isUplaodImage = false;
                dismissLoadingBar();
            }
        });

    }

    public byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


    /**
     * 获取采样率
     *
     * @param options
     * @param reqWidth  目标view的宽
     * @param reqHeight 目标view的高
     * @return 采样率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        int inSampleSize = 1;
        if (originalHeight > reqHeight || originalWidth > reqHeight) {
            int halfHeight = originalHeight / 2;
            int halfWidth = originalWidth / 2;
            //压缩后的尺寸与所需的尺寸进行比较
            while ((halfWidth / inSampleSize) >= reqHeight && (halfHeight / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = TUtils.getUserInfo();
        if (!TextUtils.isEmpty(userInfo.username)){
            tvUserName.setText(userInfo.username);
        }
        tvPhone.setText(TUtils.getHidePhone());
    }

    @Override
    public void onBackPressed() {
        if (isUplaodImage){
            setResult(RESULT_OK);
            finish();
        }else {
            super.onBackPressed();
        }
    }
}

package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.UserExt;
import com.task.system.common.GlideLoadFileLoader;
import com.task.system.utils.TUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Email: dengyc@dadaodata.com
 * FileName: AddIdCardActivity.java
 * Author: dengyancheng
 * Date: 2019-08-14 00:32
 * Description: 上传身份信息
 * History:
 */
public class AddIdCardActivity extends BaseActivity {


    @BindView(R.id.tv_name_tips)
    TextView tvNameTips;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_id_tips)
    TextView tvIdTips;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.iv_id_up)
    TextView ivIdUp;
    @BindView(R.id.iv_id_down)
    TextView ivIdDown;
    @BindView(R.id.rl_car_up)
    LinearLayout rlCarUp;
    @BindView(R.id.tv_id_up)
    TextView tvIdUp;
    @BindView(R.id.iv_id_hold)
    TextView ivIdHold;
    @BindView(R.id.rl_card_hold)
    LinearLayout rlCardHold;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.iv_ad_up_real)
    ImageView ivAdUpReal;
    @BindView(R.id.iv_ad_down_real)
    ImageView ivAdDownReal;
    @BindView(R.id.iv_id_hold_real)
    ImageView ivIdHoldReal;
    private int selectedPicsPosition = 0;//被选中的图片

    private Map<Integer, String> carsdImages = new HashMap<>();

    public static final int REQUEST_CODE_SELECT = 105;
    public static final int REQUEST_CODE_PREVIEW = 106;


    private UserExt.IdCardInfo userExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_id_card);
        ButterKnife.bind(this);
        setTitle("上传身份信息");
        initImagePicker();
        if (getIntent() != null && getIntent().getSerializableExtra(Constans.PASS_OBJECT) != null) {
            userExt = (UserExt.IdCardInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
            if (userExt != null) {
                setTitle("查看身份信息");
                ivAdDownReal.setClickable(false);
                ivAdUpReal.setClickable(false);
                ivIdHoldReal.setClickable(false);
                btnLogin.setVisibility(View.GONE);
                etId.setText(userExt.idcard);
                etName.setText(userExt.idcard_name);


                Glide.with(ApiConfig.context)
                        .load(userExt.idcard_front)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .transition(withCrossFade())
                        .apply(new RequestOptions())
                        .into(ivAdUpReal);

                Glide.with(ApiConfig.context)
                        .load(userExt.idcard_back)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .transition(withCrossFade())
                        .apply(new RequestOptions())
                        .into(ivAdDownReal);

                Glide.with(ApiConfig.context)
                        .load(userExt.idcard_hand)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .transition(withCrossFade())
                        .apply(new RequestOptions())
                        .into(ivIdHoldReal);
            }
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
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @OnClick({R.id.iv_id_up, R.id.iv_id_down, R.id.iv_id_hold, R.id.btn_login})
    public void onViewClicked(View view) {
        if (userExt!=null){
            return;
        }
        switch (view.getId()) {
            case R.id.iv_id_up:
                selectPicture(0);
                break;
            case R.id.iv_id_down:
                selectPicture(1);
                break;
            case R.id.iv_id_hold:
                selectPicture(2);
                break;
            case R.id.btn_login:
                upLoadCardInfo();
                break;
        }
    }

    private void upLoadCardInfo() {
        if (TextUtils.isEmpty(etName.getEditableText().toString())) {
            SysUtils.showToast("请输入姓名");
            return;
        }

        if (TextUtils.isEmpty(etId.getEditableText().toString())) {
            SysUtils.showToast("请输入身份证号");
            return;
        }

        if (carsdImages.size() != 3) {
            SysUtils.showToast("请上传完成身份证图片");
            return;
        }

        showLoadingBar("上传中...");

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("idcard_front", carsdImages.get(0));
        hashMap.put("idcard_back", carsdImages.get(1));
        hashMap.put("idcard_hand", carsdImages.get(2));
        hashMap.put("idcard_name", etName.getEditableText().toString());
        hashMap.put("idcard", etId.getEditableText().toString());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).addIdCards(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {

            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                ToastUtils.showShort("" + msg);
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

    private void selectPicture(int type) {
        selectedPicsPosition = type;
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
    }


    //上传头像
    private void updateImageByBase64(File file) {

        LogUtils.w("dyc---原始文件大小", file.length());
        showLoadingBar("上传中...");
        Luban.with(this)
                .load(file)
                .ignoreBy(Constans.LUBAN_SIZE)
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


                        LogUtils.w("dyc---原始文件大小", lubanFile.length());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(lubanFile.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(TUtils.getBytesByBitmap(bitmap))));
                    }

                    @Override
                    public void onError(Throwable e) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                        doUploadImage(new String(EncodeUtils.base64Encode(TUtils.getBytesByBitmap(bitmap))));
                    }
                }).launch();
    }


    private void initCards(String lubanFile) {
        if (selectedPicsPosition == 0) {
            //身份证上
            Glide.with(ApiConfig.context)
                    .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .transition(withCrossFade())
                    .apply(new RequestOptions())
                    .into(ivAdUpReal);

        } else if (selectedPicsPosition == 1) {
            //身份下
            Glide.with(ApiConfig.context)
                    .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .transition(withCrossFade())
                    .apply(new RequestOptions())
                    .into(ivAdDownReal);

        } else {
            //手持身份证
            Glide.with(ApiConfig.context)
                    .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .transition(withCrossFade())
                    .apply(new RequestOptions())
                    .into(ivIdHoldReal);

        }
    }

    private void doUploadImage(String base64Encode) {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("image", "data:image/png;base64," + base64Encode);
        hashMap.put("image_type", "certificate");
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).uploadImage(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                dismissLoadingBar();
                if (!TextUtils.isEmpty(data.path) && !TextUtils.isEmpty(data.url)) {
                    ToastUtils.showShort("" + msg);
                    carsdImages.put(selectedPicsPosition, data.path);
                    initCards(data.url);
                } else {
                    ToastUtils.showShort("请重新上传");
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort("" + msg);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}

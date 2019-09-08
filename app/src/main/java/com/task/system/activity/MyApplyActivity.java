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
import com.task.system.api.TaskInfoIgnoreBody;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Author: dengyancheng
 * Date: 2019-09-08 09:20
 * Description: 我的申请
 * History:
 */
public class MyApplyActivity extends BaseActivity {


    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_band_num)
    EditText etBandNum;
    @BindView(R.id.et_open_brand)
    EditText etOpenBrand;
    @BindView(R.id.et_link_person)
    EditText etLinkPerson;
    @BindView(R.id.et_link_phone)
    EditText etLinkPhone;
    @BindView(R.id.iv_ad_up_real)
    ImageView ivAdUpReal;
    @BindView(R.id.iv_id_up)
    TextView ivIdUp;
    @BindView(R.id.iv_ad_down_real)
    ImageView ivAdDownReal;
    @BindView(R.id.iv_id_down)
    TextView ivIdDown;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private UserExt.BussinessInfo bussinessInfo;

    private int selectedPicsPosition = 0;//被选中的图片

    private String businessImage;//营业执照
    private String businessOpenPermit;//开户许可

//    private Map<Integer, String> carsdImages = new HashMap<>();

    public static final int REQUEST_CODE_SELECT = 105;
    public static final int REQUEST_CODE_PREVIEW = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_area);
        ButterKnife.bind(this);
        setTitle("我的申请");


        initImagePicker();
        if (getIntent() != null && getIntent().getSerializableExtra(Constans.PASS_OBJECT) != null) {
            bussinessInfo = (UserExt.BussinessInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
            if (bussinessInfo != null) {
                setTitle("查看申请信息");
//                ivAdDownReal.setClickable(false);
//                ivAdUpReal.setClickable(false);
//                ivIdHoldReal.setClickable(false);
//                btnLogin.setVisibility(View.GONE);
//                etId.setText(userExt.idcard);
                etName.setText(bussinessInfo.business_name);
                etAddress.setText(bussinessInfo.business_address);
                etBandNum.setText(bussinessInfo.business_bank_account);
                etLinkPerson.setText(bussinessInfo.business_contact);
                etOpenBrand.setText(bussinessInfo.business_open_bank);
                etLinkPhone.setText(bussinessInfo.business_contact_mobile);


                Glide.with(ApiConfig.context)
                        .load(bussinessInfo.business_image)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .transition(withCrossFade())
                        .apply(new RequestOptions())
                        .into(ivAdUpReal);

                Glide.with(ApiConfig.context)
                        .load(bussinessInfo.business_open_permit)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .transition(withCrossFade())
                        .apply(new RequestOptions())
                        .into(ivAdDownReal);

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


    @OnClick({R.id.iv_ad_up_real, R.id.iv_ad_down_real, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_ad_up_real:
                selectPicture(0);
                break;
            case R.id.iv_ad_down_real:
                selectPicture(1);
                break;
            case R.id.btn_login:
                if (bussinessInfo == null) {
                    upLoadCardInfo();
                } else {
                    updateCardInfo();
                }
                break;
        }
    }

    //上传认证
    private void upLoadCardInfo() {
        if (TextUtils.isEmpty(etName.getEditableText().toString())) {
            SysUtils.showToast("请企业名称");
            return;
        }

        if (TextUtils.isEmpty(etAddress.getEditableText().toString())) {
            SysUtils.showToast("请输入联系地址");
            return;
        }

        if (TextUtils.isEmpty(etBandNum.getEditableText().toString())) {
            SysUtils.showToast("请输入银行卡号");
            return;
        }

        if (TextUtils.isEmpty(etOpenBrand.getEditableText().toString())) {
            SysUtils.showToast("请输入开户行");
            return;
        }
        if (TextUtils.isEmpty(etLinkPerson.getEditableText().toString())) {
            SysUtils.showToast("请输入联系人");
            return;
        }
        if (TextUtils.isEmpty(etLinkPhone.getEditableText().toString())) {
            SysUtils.showToast("请输入联系人号码");
            return;
        }


        if (TextUtils.isEmpty(businessImage) || TextUtils.isEmpty(businessOpenPermit)) {
            SysUtils.showToast("请完成图片上传");
            return;
        }


        showLoadingBar("上传中...");

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("business_image", businessImage);
        hashMap.put("business_open_permit", businessOpenPermit);
        hashMap.put("business_name", etName.getEditableText().toString());
        hashMap.put("business_address", etAddress.getEditableText().toString());
        hashMap.put("business_bank_account", etBandNum.getEditableText().toString());
        hashMap.put("business_open_bank", etOpenBrand.getEditableText().toString());
        hashMap.put("business_contact", etLinkPerson.getEditableText().toString());
        hashMap.put("business_contact_mobile", etLinkPhone.getEditableText().toString());


        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).userAddBuisness(TUtils.getParams(hashMap));

        API.getObjectIgnoreBody(call,  new ApiCallBack() {

            @Override
            public void onSuccess(int msgCode, String msg, Object data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(""+msg);
            }
        });


    }

    //修改认证
    private void updateCardInfo() {
        if (TextUtils.isEmpty(etName.getEditableText().toString())) {
            SysUtils.showToast("请企业名称");
            return;
        }

        if (TextUtils.isEmpty(etAddress.getEditableText().toString())) {
            SysUtils.showToast("请输入联系地址");
            return;
        }

        if (TextUtils.isEmpty(etBandNum.getEditableText().toString())) {
            SysUtils.showToast("请输入银行卡号");
            return;
        }

        if (TextUtils.isEmpty(etOpenBrand.getEditableText().toString())) {
            SysUtils.showToast("请输入开户行");
            return;
        }
        if (TextUtils.isEmpty(etLinkPerson.getEditableText().toString())) {
            SysUtils.showToast("请输入联系人");
            return;
        }
        if (TextUtils.isEmpty(etLinkPhone.getEditableText().toString())) {
            SysUtils.showToast("请输入联系人号码");
            return;
        }


        showLoadingBar("上传中...");

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        if (!TextUtils.isEmpty(businessImage)) {
            hashMap.put("business_image", businessImage);
        }

        if (!TextUtils.isEmpty(businessOpenPermit)) {
            hashMap.put("business_open_permit", businessOpenPermit);
        }
        hashMap.put("business_name", etName.getEditableText().toString());
        hashMap.put("business_address", etAddress.getEditableText().toString());
        hashMap.put("business_bank_account", etBandNum.getEditableText().toString());
        hashMap.put("business_open_bank", etOpenBrand.getEditableText().toString());
        hashMap.put("business_contact", etLinkPerson.getEditableText().toString());
        hashMap.put("business_contact_mobile", etLinkPhone.getEditableText().toString());


        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).userSetBusiness(TUtils.getParams(hashMap));

        API.getObjectIgnoreBody(call,  new ApiCallBack() {

            @Override
            public void onSuccess(int msgCode, String msg, Object data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                SysUtils.showToast(""+msg);
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


    private void initCards(SimpleBeanInfo simpleBeanInfo) {
        if (selectedPicsPosition == 0) {
            businessImage = simpleBeanInfo.path;
            Glide.with(ApiConfig.context)
                    .load(simpleBeanInfo.url)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .transition(withCrossFade())
                    .apply(new RequestOptions())
                    .into(ivAdUpReal);

        } else if (selectedPicsPosition == 1) {

            businessOpenPermit = simpleBeanInfo.path;
            Glide.with(ApiConfig.context)
                    .load(simpleBeanInfo.url)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .transition(withCrossFade())
                    .apply(new RequestOptions())
                    .into(ivAdDownReal);

        }

//        else {
//            //手持身份证
//            Glide.with(ApiConfig.context)
//                    .load(lubanFile)     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
//                    .transition(withCrossFade())
//                    .apply(new RequestOptions())
//                    .into(ivIdHoldReal);
//
//        }
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
//                    carsdImages.put(selectedPicsPosition, data.path);

                    initCards(data);
                } else {
                    SysUtils.showToast("请重新上传");
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                SysUtils.showToast("" + msg);
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

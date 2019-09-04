package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ImagePickerAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.common.GlideLoadFileLoader;
import com.task.system.event.RefreshUnreadCountEvent;
import com.task.system.utils.TUtils;
import com.task.system.utils.Util;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;

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
 * Author: dengyancheng
 * Date: 2019-09-02 01:03
 * Description: 提交订单 或回复订单
 * History:
 */
public class ApplyDisputeOrReplyActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.edit_dispute)
    EditText editDispute;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;
    private OrderInfo orderInfo;


    private ImagePickerAdapter imageAdapter;

    public static final int IMAGE_ITEM_ADD = -1;

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private int maxImgCount = 5;

    public String content;

    public List<String> uploadHash = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_dispute_or_reply);
        ButterKnife.bind(this);

        orderInfo = (OrderInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);

        setTitle("" + orderInfo.title);

        imageAdapter = new ImagePickerAdapter(ApiConfig.context, selImageList, maxImgCount);
        imageAdapter.setOnItemClickListener(this);
        recycle.setLayoutManager(new GridLayoutManager(ApiConfig.context, 3));
        recycle.setHasFixedSize(true);
        recycle.setAdapter(imageAdapter);
        recycle.setNestedScrollingEnabled(false);
        initImagePicker();

        editDispute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content = s.toString();

            }
        });


    }

    //图片选择
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideLoadFileLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);                      //选中数量限制
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
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
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
//                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(ApiConfig.context, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(ApiConfig.context, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imageAdapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, ImagePicker.REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @OnClick({R.id.tv_custome, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_next_step:
                if (TextUtils.isEmpty(editDispute.getEditableText().toString())){
                    SysUtils.showToast("请填写争议你内容");
                    return;
                }

                if (selImageList!=null && selImageList.size()>0) {
                    uploadPictures(selImageList, uploadHash);
                }else{
                    SysUtils.showToast("请上传图片");
                }
                break;
        }
    }


    /**
     * 上传图片
     *
     * @param selImageList
     * @param photoPaths
     */
    private void uploadPictures(ArrayList<ImageItem> selImageList, List<String> photoPaths) {
            showLoadingBar("提交中...");
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
                if (photoPaths.size()==selImageList.size()){
                    //上传完毕  提交争议
                    showLoadingBar("提交争议");
                    doSubmitDispute();
                }

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });

    }

    /**
     * 提交争议
     */
    private void doSubmitDispute() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("order_id", orderInfo.order_id);
        hashMap.put("dispute_id", "0");
        hashMap.put("images", new Gson().toJson(uploadHash));
        hashMap.put("content", editDispute.getEditableText().toString());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).disputeOrder(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {

            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                ToastUtils.showShort("提交成功");
                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                setResult(RESULT_OK);
                finish();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                SysUtils.showToast(msg+"");
            }
        });
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

}

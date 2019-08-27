package com.task.system.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ImagePickerAdapter;
import com.task.system.bean.TaskInfoItem;
import com.task.system.common.GlideLoadFileLoader;
import com.task.system.common.RichTextView;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: TaskStepFragment
 * Author: dengyc
 * Date: 2019-08-27 06:54
 * Description:
 * History:
 */
public class TaskStepFragment extends BaseFragment implements ImagePickerAdapter.OnRecyclerViewItemClickListener{
    @BindView(R.id.rich_step)
    RichTextView richStep;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    Unbinder unbinder;

    private ImagePickerAdapter imageAdapter;

    public static final int IMAGE_ITEM_ADD = -1;

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private int maxImgCount = 5;

    public String content;

    private TaskInfoItem.TaskStep taskStep;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.fragment_do_step;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        if (getArguments()!=null) {
          taskStep = (TaskInfoItem.TaskStep) getArguments().getSerializable(Constans.PASS_OBJECT);
        }

        richStep.setHtml(taskStep.step);
        imageAdapter = new ImagePickerAdapter(ApiConfig.context, selImageList, maxImgCount);
        imageAdapter.setOnItemClickListener(this);
        recycle.setLayoutManager(new GridLayoutManager(ApiConfig.context, 3));
        recycle.setHasFixedSize(true);
        recycle.setAdapter(imageAdapter);
        recycle.setNestedScrollingEnabled(false);
        initImagePicker();

        etContent.addTextChangedListener(new TextWatcher() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments()!=null) {
            taskStep = (TaskInfoItem.TaskStep) getArguments().getSerializable(Constans.PASS_OBJECT);
        }
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
}

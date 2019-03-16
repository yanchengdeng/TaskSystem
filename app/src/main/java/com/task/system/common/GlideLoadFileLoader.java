package com.task.system.common;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

public class GlideLoadFileLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {

        if (imageView != null && activity != null && !activity.isDestroyed())
            Glide.with(activity)
                    .load(Uri.fromFile(new File(path)))     //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        if (imageView != null && activity != null && !activity.isDestroyed())
            Glide.with(activity)
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
package com.task.system.views.photoview.preview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.task.system.R;
import com.task.system.views.photoview.ImageInfo;
import com.task.system.views.photoview.NineGridView;
import com.yc.lib.api.ApiConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePreviewAdapter extends PagerAdapter implements PhotoViewAttacher.OnPhotoTapListener {

    private List<ImageInfo> imageInfo;
    private Context context;
    private View currentView;

    public ImagePreviewAdapter(Context context, @NonNull List<ImageInfo> imageInfo) {
        super();
        this.imageInfo = imageInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageInfo.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
    }

    public View getPrimaryItem() {
        return currentView;
    }

    public ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.pv);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photoview, container, false);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
        final PhotoView imageView = (PhotoView) view.findViewById(R.id.pv);


        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImage(imageInfo.get(position).bigImageUrl);
                return false;
            }
        });

        ImageInfo info = this.imageInfo.get(position);
        imageView.setOnPhotoTapListener(this);
        showExcessPic(info, imageView);
        NineGridView.getImageLoader().onDisplayImage(view.getContext(), imageView, info.bigImageUrl);
        container.addView(view);
        return view;
    }


    private void saveImage(String bigImageUrl) {


        RequestManager mRequestManager = Glide.with(ApiConfig.context);

        RequestBuilder<File> mRequestBuilder = mRequestManager.downloadOnly();
        mRequestBuilder.load(bigImageUrl);
        mRequestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    FileInputStream fis = new FileInputStream(resource);
                    Bitmap bmp = BitmapFactory.decodeStream(fis);


                    saveFile(bmp,"闲推"+System.currentTimeMillis()+".jpg","/闲推");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        mRequestBuilder.preload();


    }

    public static void saveFile(Bitmap bm, String fileName, String path) throws IOException {
       String SAVE_PIC_PATH= Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
       String SAVE_REAL_PATH = SAVE_PIC_PATH;//保存的确切位置
        String subForder = SAVE_REAL_PATH + path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        ToastUtils.showShort("保存成功");

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        ApiConfig.context.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！

    }

    /**
     * 展示过度图片
     */
    private void showExcessPic(ImageInfo imageInfo, PhotoView imageView) {
        //先获取大图的缓存图片
        Bitmap cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.bigImageUrl);
        //如果大图的缓存不存在,在获取小图的缓存
        if (cacheImage == null)
            cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.thumbnailUrl);
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
            imageView.setImageResource(R.drawable.ic_default_color);
        } else {
            imageView.setImageBitmap(cacheImage);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 单击屏幕关闭
     */
    @Override
    public void onPhotoTap(View view, float x, float y) {
        ((ImagePreviewActivity) context).finishActivityAnim();
    }
}
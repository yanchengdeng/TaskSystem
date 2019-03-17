package com.task.system.common;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.task.system.utils.GlideImageGetter;


public class RichTextView extends AppCompatTextView {
  private int mWidth;
  private Context mContext;
  private ImageClickListener mImageClickListener;

  private ImageSpan mImageSpans[];
  private SpannableStringBuilder stringBuilder;
  private String[] mImageUrls;

  public RichTextView(Context context) {
    this(context, null);
  }

  public RichTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mWidth = MeasureSpec.getSize(widthMeasureSpec);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mWidth = w;
  }
  /**
   * 设置富文本
   *
   */
  public void setHtml(final String source) {
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
        stringBuilder = (SpannableStringBuilder) Html.fromHtml(source,
            new GlideImageGetter(mContext, RichTextView.this), null);

        setImageClickable(stringBuilder);
        setText(stringBuilder);
      }
    });
  }

  public void setOnImageClickListener(ImageClickListener imageClickListener) {
    mImageClickListener = imageClickListener;
  }

  private void setImageClickable(SpannableStringBuilder stringBuilder) {
    mImageSpans = stringBuilder.getSpans(0, stringBuilder.length(), ImageSpan.class);

    mImageUrls = new String[mImageSpans.length];
    for (int i = 0; i < mImageSpans.length; i++) {
      mImageUrls[i] = mImageSpans[i].getSource();
    }
    for (int i = 0; i < mImageSpans.length; i++) {

      int start = stringBuilder.getSpanStart(mImageSpans[i]);
      int end = stringBuilder.getSpanEnd(mImageSpans[i]);
      final int finalI = i;
      stringBuilder.setSpan(new ClickableSpan() {
        @Override public void onClick(View widget) {
          if (mImageClickListener != null) {
            mImageClickListener.onImageClick(mImageUrls[finalI], mImageUrls, finalI);
          }
        }
      }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    setMovementMethod(LinkMovementMethod.getInstance());
  }

  public interface ImageClickListener {
    void onImageClick(String imageUrl, String[] imageUrls, int position);
  }
}
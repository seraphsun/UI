package com.design.code.view.theme;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Ignacey 2016/1/15.
 */
public class Window8ImageView extends ImageView {

    private static final int SCALE_REDUCE_INIT = 10001;
    private static final int SCALING = 10002;
    private static final int SCALE_ADD_INIT = 6;

    /**
     * 控件的宽1/2
     */
    private int mCenterWidth;
    /**
     * 控件的高 1/2
     */
    private int mCenterHeight;
    /**
     * 缩放是否结束
     */
    private boolean isFinish = true;

    private OnViewClickListener mOnViewClickListener;

    // 设置一个缩放的常量
    float mMinScale = 0.85f;

    /**
     * 控制缩放的Handler
     */
    private Handler mScaleHandler = new Handler() {

        Matrix matrix = new Matrix();
        int count = 0;
        float s;
        /**
         * 是否已经调用了点击事件
         */
        boolean isClicked;

        @Override
        public void handleMessage(android.os.Message msg) {
            matrix.set(getImageMatrix());
            switch (msg.what) {
                case SCALE_REDUCE_INIT:
                    if (!isFinish) {
                        mScaleHandler.sendEmptyMessage(SCALE_REDUCE_INIT);
                    } else {
                        isFinish = false;
                        count = 0;
                        s = (float) Math.sqrt(Math.sqrt(mMinScale));
                        beginScale(matrix, s);
                        mScaleHandler.sendEmptyMessage(SCALING);
                    }
                    break;
                case SCALING:
                    beginScale(matrix, s);
                    if (count < 2) {
                        mScaleHandler.sendEmptyMessage(SCALING);
                    } else {
                        isFinish = true;
                        if (mOnViewClickListener != null && !isClicked) {
                            isClicked = true;
                            mOnViewClickListener.onViewClick(Window8ImageView.this);
                        } else {
                            isClicked = false;
                        }
                    }
                    count++;

                    break;
                case 6:
                    if (!isFinish) {
                        mScaleHandler.sendEmptyMessage(SCALE_ADD_INIT);
                    } else {
                        isFinish = false;
                        count = 0;
                        s = (float) Math.sqrt(Math.sqrt(1.0f / mMinScale));
                        beginScale(matrix, s);
                        mScaleHandler.sendEmptyMessage(SCALING);
                    }
                    break;
            }
        }
    };

    public Window8ImageView(Context context) {
        this(context, null);
    }

    public Window8ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Window8ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 必要的初始化
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            int mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int mHeight = getHeight() - getPaddingTop() - getPaddingBottom();

            mCenterWidth = mWidth / 2;
            mCenterHeight = mHeight / 2;

            Drawable drawable = getDrawable();
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bd.setAntiAlias(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // float X = event.getX();
                // float Y = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                mScaleHandler.sendEmptyMessage(SCALE_REDUCE_INIT);
                break;
            case MotionEvent.ACTION_UP:
                mScaleHandler.sendEmptyMessage(SCALE_ADD_INIT);
                break;
        }
        return true;
    }

    /**
     * 缩放
     */
    private synchronized void beginScale(Matrix matrix, float scale) {
        matrix.postScale(scale, scale, mCenterWidth, mCenterHeight);
        setImageMatrix(matrix);
    }

//    protected void sleep(int time) {
//        try {
//            Thread.sleep(time);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public void setOnClickIntent(OnViewClickListener onViewClickListener) {
        this.mOnViewClickListener = onViewClickListener;
    }

    /**
     * 回调接口
     */
    public interface OnViewClickListener {
        void onViewClick(Window8ImageView view);
    }
}

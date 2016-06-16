package com.design.code.view.refresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.design.code.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ignacey 2016/1/14.
 */
public class SwipeRefresh extends ViewGroup {

    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;
    public static final int RESTORE_ANIMATION_DURATION = 2350;

    private static final int DRAG_MAX_DISTANCE = 120;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private View mTarget;
    private ImageView mRefreshView;
    private HeadViewFlight mFlight;
    private Interpolator mDecelerateInterpolator;
    private OnRefreshListener mListener;

    private int mTouchSlop;
    private int mTotalDragDistance;

    private int mCurrentOffsetTop;
    private float mCurrentDragPercent;

    private int mActivePointerId;
    private float mInitialMotionY;

    private int mFrom;
    private float mFromDragPercent;

    private boolean mRefreshing;
    private boolean mIsBeingDragged;
    private boolean mNotify;

    private int mTargetPaddingTop, mTargetPaddingBottom, mTargetPaddingRight, mTargetPaddingLeft;

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            int targetTop;
            int endTarget = mTotalDragDistance;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();

            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
            mFlight.setPercent(mCurrentDragPercent);

            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private final Animation mAnimateToEndPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
            moveToEnd(interpolatedTime);
        }
    };

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mFlight.stop();
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    public SwipeRefresh(Context context) {
        this(context, null);
    }

    public SwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        float density = context.getResources().getDisplayMetrics().density;
        mTotalDragDistance = Math.round((float) DRAG_MAX_DISTANCE * density);

        mRefreshView = new ImageView(context);
        mFlight = new HeadViewFlight(getContext(), this);
        mRefreshView.setImageDrawable(mFlight);

        addView(mRefreshView);
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureTarget();
        if (mTarget == null)
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mRefreshView) {
                    mTarget = child;
                    mTargetPaddingBottom = mTarget.getPaddingBottom();
                    mTargetPaddingLeft = mTarget.getPaddingLeft();
                    mTargetPaddingRight = mTarget.getPaddingRight();
                    mTargetPaddingTop = mTarget.getPaddingTop();
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureTarget();
        if (mTarget == null)
            return;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
        mRefreshView.layout(left, top, left + width - right, top + height - bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp() || mRefreshing) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTop(0, true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }
        return mIsBeingDragged;
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        mFlight.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                final float scrollTop = yDiff * DRAG_RATE;
                mCurrentDragPercent = scrollTop / mTotalDragDistance;
                if (mCurrentDragPercent < 0) {
                    return false;
                }
                float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mTotalDragDistance;
                float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent / 2;
                int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

                mFlight.setPercent(mCurrentDragPercent);
                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overScrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    animateOffsetToPosition(mAnimateToStartPosition);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }
        return true;
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                mFlight.setPercent(1f);
                animateOffsetToCorrectPosition();
            } else {
                mFlight.setEndOfRefreshing(true);
                animateOffsetToPosition(mAnimateToEndPosition);
            }
        }
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(RESTORE_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);

        if (mRefreshing) {
            mFlight.start();
            if (mNotify) {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        } else {
            mFlight.stop();
            animateOffsetToPosition(mAnimateToStartPosition);
        }
        mCurrentOffsetTop = mTarget.getTop();
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalDragDistance);
    }

    private void animateOffsetToPosition(Animation animation) {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;
        long animationDuration = (long) Math.abs(MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent);

        animation.reset();
        animation.setDuration(animationDuration);
        animation.setInterpolator(mDecelerateInterpolator);
        animation.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(animation);
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        mFlight.setPercent(mCurrentDragPercent);
        setTargetOffsetTop(offset, false);
    }

    private void moveToEnd(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f + interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        mFlight.setPercent(mCurrentDragPercent);
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
        setTargetOffsetTop(offset, false);
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 头部视图
     */
    private class HeadViewFlight extends Drawable implements Drawable.Callback, Animatable {

        private static final int ANIMATION_DURATION = 1000;
        private static final float SCALE_START_PERCENT = 0.5f;

        private static final float SIDE_CLOUDS_INITIAL_SCALE = 1.05f;
        private static final float SIDE_CLOUDS_FINAL_SCALE = 1.55f;

        private static final float CENTER_CLOUDS_INITIAL_SCALE = 0.8f;
        private static final float CENTER_CLOUDS_FINAL_SCALE = 1.30f;

        private final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

        // Multiply with this animation interpolator time
        private static final int LOADING_ANIMATION_COEFFICIENT = 80;
        private static final int SLOW_DOWN_ANIMATION_COEFFICIENT = 6;
        // Amount of lines when is going lading animation
        private static final int WIND_SET_AMOUNT = 10;
        private static final int Y_SIDE_CLOUDS_SLOW_DOWN_COF = 4;
        private static final int X_SIDE_CLOUDS_SLOW_DOWN_COF = 2;
        private static final int MIN_WIND_LINE_WIDTH = 50;
        private static final int MAX_WIND_LINE_WIDTH = 300;
        private static final int MIN_WIND_X_OFFSET = 1000;
        private static final int MAX_WIND_X_OFFSET = 2000;
        private static final int RANDOM_Y_COEFFICIENT = 5;

        private Context mContext;
        private SwipeRefresh mParent;
        private Matrix mMatrix, mAdditionalMatrix;
        private Animation mAnimation;

        private int mTop;
        private int mScreenWidth;
        private boolean mInverseDirection;

        // KEY: Y position, Value: X offset of wind
        private Map<Float, Float> mWinds;
        private Paint mWindPaint;
        private float mWindLineWidth;
        private boolean mNewWindSet;

        private float mJetTopOffset;
        private int mJetWidthCenter, mJetHeightCenter;
        private int mFrontCloudHeightCenter, mFrontCloudWidthCenter, mRightCloudsWidthCenter, mRightCloudsHeightCenter, mLeftCloudsWidthCenter, mLeftCloudsHeightCenter;

        private float mPercent = 0.0f;

        private Bitmap mJet, mFrontClouds, mLeftClouds, mRightClouds;

        private boolean isRefreshing = false;
        private float mLoadingAnimationTime, mLastAnimationTime;

        private Random mRandom;
        private boolean mEndOfRefreshing;

        public HeadViewFlight(Context context, SwipeRefresh parent) {
            mContext = context;
            mParent = parent;
            mMatrix = new Matrix();
            mAdditionalMatrix = new Matrix();
            mWinds = new HashMap<>();
            mRandom = new Random();

            mWindPaint = new Paint();
            mWindPaint.setColor(mContext.getResources().getColor(android.R.color.white));
            mWindPaint.setStrokeWidth(3);
            mWindPaint.setAlpha(50);

            initiateDimens();
            createBitmaps();
            setupAnimations();
        }

        private void initiateDimens() {
            mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            mJetTopOffset = mParent.getTotalDragDistance() * 0.5f;
            mTop = -mParent.getTotalDragDistance();
        }

        private void createBitmaps() {
            mLeftClouds = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.refresh_view_clouds_left);
            mRightClouds = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.refresh_view_clouds_right);
            mFrontClouds = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.refresh_view_clouds_center);
            mJet = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.refresh_view_airplane);

            mJetWidthCenter = mJet.getWidth() / 2;
            mJetHeightCenter = mJet.getHeight() / 2;
            mFrontCloudWidthCenter = mFrontClouds.getWidth() / 2;
            mFrontCloudHeightCenter = mFrontClouds.getHeight() / 2;

            mRightCloudsWidthCenter = mRightClouds.getWidth() / 2;
            mRightCloudsHeightCenter = mRightClouds.getHeight() / 2;
            mLeftCloudsWidthCenter = mLeftClouds.getWidth() / 2;
            mLeftCloudsHeightCenter = mLeftClouds.getHeight() / 2;
        }

        private void setupAnimations() {
            mAnimation = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
                    setLoadingAnimationTime(interpolatedTime);
                }
            };
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
            mAnimation.setDuration(ANIMATION_DURATION);
        }

        private void setLoadingAnimationTime(float loadingAnimationTime) {
            /** SLOW DOWN ANIMATION IN {@link #SLOW_DOWN_ANIMATION_COEFFICIENT} time */
            mLoadingAnimationTime = LOADING_ANIMATION_COEFFICIENT * (loadingAnimationTime / SLOW_DOWN_ANIMATION_COEFFICIENT);
            invalidateSelf();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (mScreenWidth <= 0)
                return;

            final int saveCount = canvas.save();

            // DRAW BACKGROUND.
            canvas.drawColor(mContext.getResources().getColor(R.color.color_228FC1));

            if (isRefreshing) {
                // Set up new set of wind
                while (mWinds.size() < WIND_SET_AMOUNT) {
                    float y = (float) (mParent.getTotalDragDistance() / (Math.random() * RANDOM_Y_COEFFICIENT));
                    float x = random(MIN_WIND_X_OFFSET, MAX_WIND_X_OFFSET);

                    // Magic with checking interval between winds
                    if (mWinds.size() > 1) {
                        y = 0;
                        while (y == 0) {
                            float tmp = (float) (mParent.getTotalDragDistance() / (Math.random() * RANDOM_Y_COEFFICIENT));

                            for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                                // We want that interval will be greater than fifth part of draggable distance
                                if (Math.abs(wind.getKey() - tmp) > mParent.getTotalDragDistance() / RANDOM_Y_COEFFICIENT) {
                                    y = tmp;
                                } else {
                                    y = 0;
                                    break;
                                }
                            }
                        }
                    }
                    mWinds.put(y, x);
                    drawWind(canvas, y, x);
                }

                // Draw current set of wind
                if (mWinds.size() >= WIND_SET_AMOUNT) {
                    for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                        drawWind(canvas, wind.getKey(), wind.getValue());
                    }
                }

                // We should to create new set of winds
                if (mInverseDirection && mNewWindSet) {
                    mWinds.clear();
                    mNewWindSet = false;
                    mWindLineWidth = random(MIN_WIND_LINE_WIDTH, MAX_WIND_LINE_WIDTH);
                }

                // needed for checking direction
                mLastAnimationTime = mLoadingAnimationTime;
            }

            // 隐藏标头
            canvas.translate(0, mTop);
            canvas.clipRect(0, -mTop, mScreenWidth, mParent.getTotalDragDistance());

            drawJet(canvas);
            drawSideClouds(canvas);
            drawCenterClouds(canvas);

            canvas.restoreToCount(saveCount);
        }

        public float random(int min, int max) {
            // nextInt is normally exclusive of the top value, so add 1 to make it inclusive
            return mRandom.nextInt((max - min) + 1) + min;
        }

        /**
         * Draw wind on loading animation
         *
         * @param canvas  - area where we will draw
         * @param y       - y position fot one of lines
         * @param xOffset - x offset for on of lines
         */
        private void drawWind(Canvas canvas, float y, float xOffset) {
            /**
             * We should multiply current animation time with this coefficient for taking all screen width in time Removing slowing of animation with dividing on {@LINK #SLOW_DOWN_ANIMATION_COEFFICIENT} And we should don't forget about distance that should "fly" line that depend on screen of device and x offset
             */
            float cof = (mScreenWidth + xOffset) / (LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT);
            float time = mLoadingAnimationTime;

            // HORRIBLE HACK FOR REVERS ANIMATION THAT SHOULD WORK LIKE RESTART ANIMATION
            if (mLastAnimationTime - mLoadingAnimationTime > 0) {
                mInverseDirection = true;
                // take time from 0 to end of animation time
                time = (LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT) - mLoadingAnimationTime;
            } else {
                mNewWindSet = true;
                mInverseDirection = false;
            }

            // Taking current x position of drawing wind For fully disappearing of line we should subtract wind line width
            float x = (mScreenWidth - (time * cof)) + xOffset - mWindLineWidth;
            float xEnd = x + mWindLineWidth;

            canvas.drawLine(x, y, xEnd, y, mWindPaint);
        }

        private void drawJet(Canvas canvas) {
            Matrix matrix = mMatrix;
            matrix.reset();

            float dragPercent = mPercent;
            float rotateAngle = 0;

            // Check overdrag
            if (dragPercent > 1.0f && !mEndOfRefreshing) {
                rotateAngle = (dragPercent % 1) * 10;
                dragPercent = 1.0f;
            }

            float offsetX = ((mScreenWidth * dragPercent) / 2) - mJetWidthCenter;

            float offsetY = mJetTopOffset + (mParent.getTotalDragDistance() / 2) * (1.0f - dragPercent) - mJetHeightCenter;

            if (isRefreshing) {
                if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                    offsetY -= getAnimationPartValue(AnimationPart.FIRST);
                } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                    offsetY -= getAnimationPartValue(AnimationPart.SECOND);
                } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                    offsetY += getAnimationPartValue(AnimationPart.THIRD);
                } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                    offsetY += getAnimationPartValue(AnimationPart.FOURTH);
                }
            }
            matrix.setTranslate(offsetX, offsetY);

            if (dragPercent == 1.0f) {
                matrix.preRotate(rotateAngle, mJetWidthCenter, mJetHeightCenter);
            }
            canvas.drawBitmap(mJet, matrix, null);
        }

        /**
         * On drawing we should check current part of animation
         *
         * @param part - needed part of animation
         * @return - return true if current part
         */
        private boolean checkCurrentAnimationPart(AnimationPart part) {
            switch (part) {
                case FIRST: {
                    return mLoadingAnimationTime < getAnimationTimePart(AnimationPart.FOURTH);
                }
                case SECOND:
                case THIRD: {
                    return mLoadingAnimationTime < getAnimationTimePart(part);
                }
                case FOURTH: {
                    return mLoadingAnimationTime > getAnimationTimePart(AnimationPart.THIRD);
                }
                default:
                    return false;
            }
        }

        /**
         * Get part of animation duration
         *
         * @param part - needed part of time
         * @return - interval of time
         */
        private int getAnimationTimePart(AnimationPart part) {
            switch (part) {
                case SECOND: {
                    return LOADING_ANIMATION_COEFFICIENT / 2;
                }
                case THIRD: {
                    return getAnimationTimePart(AnimationPart.FOURTH) * 3;
                }
                case FOURTH: {
                    return LOADING_ANIMATION_COEFFICIENT / 4;
                }
                default:
                    return 0;
            }
        }

        /**
         * We need a special value for different part of animation
         *
         * @param part - needed part
         * @return - value for needed part
         */
        private float getAnimationPartValue(AnimationPart part) {
            switch (part) {
                case FIRST: {
                    return mLoadingAnimationTime;
                }
                case SECOND: {
                    return getAnimationTimePart(AnimationPart.FOURTH) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
                }
                case THIRD: {
                    return mLoadingAnimationTime - getAnimationTimePart(AnimationPart.SECOND);
                }
                case FOURTH: {
                    return getAnimationTimePart(AnimationPart.THIRD) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
                }
                default:
                    return 0;
            }
        }

        private void drawSideClouds(Canvas canvas) {
            Matrix matrixLeftClouds = mMatrix;
            Matrix matrixRightClouds = mAdditionalMatrix;
            matrixLeftClouds.reset();
            matrixRightClouds.reset();

            // Drag percent will newer get more then 1 here
            float dragPercent = Math.min(1f, Math.abs(mPercent));

            boolean overdrag = false;

            // But we check here for overdrag
            if (mPercent > 1.0f) {
                overdrag = true;
            }

            float scale;
            float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
            if (scalePercentDelta > 0) {
                float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
                scale = SIDE_CLOUDS_INITIAL_SCALE + (SIDE_CLOUDS_FINAL_SCALE - SIDE_CLOUDS_INITIAL_SCALE) * scalePercent;
            } else {
                scale = SIDE_CLOUDS_INITIAL_SCALE;
            }

            // Current y position of clouds
            float dragYOffset = mParent.getTotalDragDistance() * (1.0f - dragPercent);

            // Position where clouds fully visible on screen and we should drag them with content of listView
            int cloudsVisiblePosition = mParent.getTotalDragDistance() / 2 - mLeftCloudsHeightCenter;

            boolean needMoveCloudsWithContent = false;
            if (dragYOffset < cloudsVisiblePosition) {
                needMoveCloudsWithContent = true;
            }

            float offsetRightX = mScreenWidth - mRightClouds.getWidth();
            float offsetRightY = (needMoveCloudsWithContent ? mParent.getTotalDragDistance() * dragPercent - mLeftClouds.getHeight() : dragYOffset) + (overdrag ? mTop : 0);

            float offsetLeftX = 0;
            float offsetLeftY = (needMoveCloudsWithContent ? mParent.getTotalDragDistance() * dragPercent - mLeftClouds.getHeight() : dragYOffset) + (overdrag ? mTop : 0);

            // Magic with animation on loading process
            if (isRefreshing) {
                if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                    offsetLeftY += getAnimationPartValue(AnimationPart.FIRST) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                    offsetRightX -= getAnimationPartValue(AnimationPart.FIRST) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                    offsetLeftY += getAnimationPartValue(AnimationPart.SECOND) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                    offsetRightX -= getAnimationPartValue(AnimationPart.SECOND) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                    offsetLeftY -= getAnimationPartValue(AnimationPart.THIRD) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                    offsetRightX += getAnimationPartValue(AnimationPart.THIRD) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                    offsetLeftY -= getAnimationPartValue(AnimationPart.FOURTH) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                    offsetRightX += getAnimationPartValue(AnimationPart.FOURTH) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                }
            }

            matrixRightClouds.postScale(scale, scale, mRightCloudsWidthCenter, mRightCloudsHeightCenter);
            matrixRightClouds.postTranslate(offsetRightX, offsetRightY);

            matrixLeftClouds.postScale(scale, scale, mLeftCloudsWidthCenter, mLeftCloudsHeightCenter);
            matrixLeftClouds.postTranslate(offsetLeftX, offsetLeftY);

            canvas.drawBitmap(mLeftClouds, matrixLeftClouds, null);
            canvas.drawBitmap(mRightClouds, matrixRightClouds, null);
        }

        private void drawCenterClouds(Canvas canvas) {
            Matrix matrix = mMatrix;
            matrix.reset();
            float dragPercent = Math.min(1f, Math.abs(mPercent));

            float scale;
            float overdragPercent = 0;
            boolean overdrag = false;

            if (mPercent > 1.0f) {
                overdrag = true;
                // Here we want know about how mach percent of over drag we done
                overdragPercent = Math.abs(1.0f - mPercent);
            }

            float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
            if (scalePercentDelta > 0) {
                float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
                scale = CENTER_CLOUDS_INITIAL_SCALE + (CENTER_CLOUDS_FINAL_SCALE - CENTER_CLOUDS_INITIAL_SCALE) * scalePercent;
            } else {
                scale = CENTER_CLOUDS_INITIAL_SCALE;
            }

            float parallaxPercent = 0;
            boolean parallax = false;
            // Current y position of clouds
            float dragYOffset = mParent.getTotalDragDistance() * dragPercent;
            // Position when should start parallax scrolling
            int startParallaxHeight = mParent.getTotalDragDistance() - mFrontCloudHeightCenter;

            if (dragYOffset > startParallaxHeight) {
                parallax = true;
                parallaxPercent = dragYOffset - startParallaxHeight;
            }

            float offsetX = (mScreenWidth / 2) - mFrontCloudWidthCenter;
            float offsetY = dragYOffset - (parallax ? mFrontCloudHeightCenter + parallaxPercent : mFrontCloudHeightCenter) + (overdrag ? mTop : 0);

            float sx = overdrag ? scale + overdragPercent / 4 : scale;
            float sy = overdrag ? scale + overdragPercent / 2 : scale;

            if (isRefreshing && !overdrag) {
                if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                    sx = scale - (getAnimationPartValue(AnimationPart.FIRST) / LOADING_ANIMATION_COEFFICIENT) / 8;
                } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                    sx = scale - (getAnimationPartValue(AnimationPart.SECOND) / LOADING_ANIMATION_COEFFICIENT) / 8;
                } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                    sx = scale + (getAnimationPartValue(AnimationPart.THIRD) / LOADING_ANIMATION_COEFFICIENT) / 6;
                } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                    sx = scale + (getAnimationPartValue(AnimationPart.FOURTH) / LOADING_ANIMATION_COEFFICIENT) / 6;
                }
                sy = sx;
            }

            matrix.postScale(sx, sy, mFrontCloudWidthCenter, mFrontCloudHeightCenter);
            matrix.postTranslate(offsetX, offsetY);

            canvas.drawBitmap(mFrontClouds, matrix, null);
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(this);
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.scheduleDrawable(this, what, when);
            }
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            final Callback callback = getCallback();
            if (callback != null) {
                callback.unscheduleDrawable(this, what);
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        protected void onBoundsChange(@NonNull Rect bounds) {
            super.onBoundsChange(bounds);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void start() {
            mAnimation.reset();
            isRefreshing = true;
            mParent.startAnimation(mAnimation);
            mLastAnimationTime = 0;
            mWinds.clear();
            mWindLineWidth = random(MIN_WIND_LINE_WIDTH, MAX_WIND_LINE_WIDTH);
        }

        @Override
        public void stop() {
            mParent.clearAnimation();
            isRefreshing = false;
            mEndOfRefreshing = false;
            resetOriginals();
        }

        private void resetOriginals() {
            setPercent(0);
        }

        private void setPercent(float percent) {
            mPercent = percent;
        }

        public void offsetTopAndBottom(int offset) {
            mTop += offset;
            invalidateSelf();
        }

        /**
         * Our animation depend on type of current work of refreshing. We should to do different things when it's end of refreshing
         *
         * @param endOfRefreshing - we will check current state of refresh with this
         */
        public void setEndOfRefreshing(boolean endOfRefreshing) {
            mEndOfRefreshing = endOfRefreshing;
        }
    }

    private enum AnimationPart {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }
}

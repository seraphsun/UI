package com.design.code.view.refresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
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
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.design.code.R;
import com.design.code.util.UtilDensity;

/**
 * Created by Ignacey 2016/1/14.
 */
public class PullToRefresh extends ViewGroup {

    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

    private static final int DRAG_MAX_DISTANCE = 120;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private View mTarget;
    private ImageView mRefreshView;
    private HeadViewSunrise mSunrise;
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
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTotalDragDistance;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();

            mCurrentDragPercent = mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime;
            mSunrise.setPercent(mCurrentDragPercent, false);

            setTargetOffsetTop(offset, false);
        }
    };

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
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
            mSunrise.stop();
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    public PullToRefresh(Context context) {
        this(context, null);
    }

    public PullToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTotalDragDistance = UtilDensity.dp2px(context, DRAG_MAX_DISTANCE);

        mRefreshView = new ImageView(context);
        mSunrise = new HeadViewSunrise(this);
        mRefreshView.setImageDrawable(mSunrise);
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
        mSunrise.offsetTopAndBottom(offset);
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

                mSunrise.setPercent(mCurrentDragPercent, true);
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
                    animateOffsetToStartPosition();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }
        return true;
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                mSunrise.setPercent(1f, true);
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
            }
        }
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);

        if (mRefreshing) {
            mSunrise.start();
            if (mNotify) {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        } else {
            mSunrise.stop();
            animateOffsetToStartPosition();
        }
        mCurrentOffsetTop = mTarget.getTop();
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalDragDistance);
    }

    private void animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;
        long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent));

        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(animationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        mCurrentDragPercent = targetPercent;
        mSunrise.setPercent(mCurrentDragPercent, true);
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
        setTargetOffsetTop(offset, false);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }

    // /**
    // * This method sets padding for the refresh (progress) view.
    // */
    // public void setRefreshViewPadding(int left, int top, int right, int bottom) {
    // mRefreshView.setPadding(left, top, right, bottom);
    // }

    /**
     * 头部视图
     */
    private class HeadViewSunrise extends Drawable implements Drawable.Callback, Animatable {

        private static final int ANIMATION_DURATION = 1000;
        private static final float SCALE_START_PERCENT = 0.5f;

        private final static float SKY_RATIO = 0.65f;
        private static final float SKY_INITIAL_SCALE = 1.05f;

        private final static float TOWN_RATIO = 0.22f;
        private static final float TOWN_INITIAL_SCALE = 1.20f;
        private static final float TOWN_FINAL_SCALE = 1.30f;

        private static final float SUN_FINAL_SCALE = 0.75f;
        private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
        private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

        private final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
        private PullToRefresh mParent;
        private Matrix mMatrix;
        private Animation mAnimation;

        private int mTop;
        private int mScreenWidth;

        private int mSkyHeight;
        private float mSkyTopOffset;
        private float mSkyMoveOffset;

        private int mTownHeight;
        private float mTownInitialTopOffset;
        private float mTownFinalTopOffset;
        private float mTownMoveOffset;

        private int mSunSize = 100;
        private float mSunLeftOffset;
        private float mSunTopOffset;

        private float mPercent = 0.0f;
        private float mRotate = 0.0f;

        private Bitmap mSky, mSun, mTown;

        private boolean isRefreshing = false;
        // private boolean mEndOfRefreshing;

        public HeadViewSunrise(final PullToRefresh parent) {
            mParent = parent;
            mMatrix = new Matrix();

            setupAnimations();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    initiateDimens(parent.getWidth());
                }
            });
        }

        private void setupAnimations() {
            mAnimation = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    setRotate(interpolatedTime);
                }
            };
            mAnimation.setRepeatCount(Animation.INFINITE);
            mAnimation.setRepeatMode(Animation.RESTART);
            mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
            mAnimation.setDuration(ANIMATION_DURATION);
        }

        private void setRotate(float rotate) {
            mRotate = rotate;
            invalidateSelf();
        }

        public void initiateDimens(int viewWidth) {
            if (viewWidth <= 0 || viewWidth == mScreenWidth) return;

            mScreenWidth = viewWidth;
            mSkyHeight = (int) (SKY_RATIO * mScreenWidth);
            mSkyTopOffset = (mSkyHeight * 0.38f);
            mSkyMoveOffset = UtilDensity.dp2px(getContext(), 15);

            mTownHeight = (int) (TOWN_RATIO * mScreenWidth);
            mTownInitialTopOffset = (mParent.getTotalDragDistance() - mTownHeight * TOWN_INITIAL_SCALE);
            mTownFinalTopOffset = (mParent.getTotalDragDistance() - mTownHeight * TOWN_FINAL_SCALE);
            mTownMoveOffset = UtilDensity.dp2px(getContext(), 10);

            mSunLeftOffset = 0.3f * (float) mScreenWidth;
            mSunTopOffset = (mParent.getTotalDragDistance() * 0.1f);

            mTop = -mParent.getTotalDragDistance();

            createBitmaps();
        }

        private Context getContext() {
            return mParent != null ? mParent.getContext() : null;
        }

        private void createBitmaps() {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            mSky = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_view_sky, options);
            mSky = Bitmap.createScaledBitmap(mSky, mScreenWidth, mSkyHeight, true);
            mTown = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_view_buildings, options);
            mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (int) (mScreenWidth * TOWN_RATIO), true);
            mSun = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.refresh_view_sun, options);
            mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);
        }

        @Override
        public void draw(Canvas canvas) {
            if (mScreenWidth <= 0)
                return;

            final int saveCount = canvas.save();

            canvas.translate(0, mTop);
            canvas.clipRect(0, -mTop, mScreenWidth, mParent.getTotalDragDistance());

            drawSky(canvas);
            drawSun(canvas);
            drawTown(canvas);

            canvas.restoreToCount(saveCount);
        }

        private void drawSky(Canvas canvas) {
            Matrix matrix = mMatrix;
            matrix.reset();

            float dragPercent = Math.min(1f, Math.abs(mPercent));

            float skyScale;
            float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
            if (scalePercentDelta > 0) {
                /** Change skyScale between {@link #SKY_INITIAL_SCALE} and 1.0f depending on {@link #mPercent} */
                float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
                skyScale = SKY_INITIAL_SCALE - (SKY_INITIAL_SCALE - 1.0f) * scalePercent;
            } else {
                skyScale = SKY_INITIAL_SCALE;
            }

            float offsetX = -(mScreenWidth * skyScale - mScreenWidth) / 2.0f;
            float offsetY = (1.0f - dragPercent) * mParent.getTotalDragDistance() - mSkyTopOffset // Offset canvas moving
                    - mSkyHeight * (skyScale - 1.0f) / 2 // Offset sky scaling
                    + mSkyMoveOffset * dragPercent; // Give it a little move top -> bottom

            matrix.postScale(skyScale, skyScale);
            matrix.postTranslate(offsetX, offsetY);
            canvas.drawBitmap(mSky, matrix, null);
        }

        private void drawSun(Canvas canvas) {
            Matrix matrix = mMatrix;
            matrix.reset();

            float dragPercent = mPercent;
            if (dragPercent > 1.0f) { // Slow down if pulling over set height
                dragPercent = (dragPercent + 9.0f) / 10;
            }

            float sunRadius = (float) mSunSize / 2.0f;
            float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;

            float offsetX = mSunLeftOffset;
            float offsetY = mSunTopOffset
                    + (mParent.getTotalDragDistance() / 2) * (1.0f - dragPercent) // Move the sun up
                    - mTop; // Depending on Canvas position

            float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
            if (scalePercentDelta > 0) {
                float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
                float sunScale = 1.0f - (1.0f - SUN_FINAL_SCALE) * scalePercent;
                sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;

                matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale));
                matrix.preScale(sunScale, sunScale);

                offsetX += sunRadius;
                offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale;
            } else {
                matrix.postTranslate(offsetX, offsetY);

                offsetX += sunRadius;
                offsetY += sunRadius;
            }

            matrix.postRotate((isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth), offsetX, offsetY);

            canvas.drawBitmap(mSun, matrix, null);
        }

        private void drawTown(Canvas canvas) {
            Matrix matrix = mMatrix;
            matrix.reset();

            float dragPercent = Math.min(1f, Math.abs(mPercent));

            float townScale;
            float townTopOffset;
            float townMoveOffset;
            float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
            if (scalePercentDelta > 0) {
                /**
                 * Change townScale between {@link #TOWN_INITIAL_SCALE} and {@link #TOWN_FINAL_SCALE} depending on {@link #mPercent}
                 * Change townTopOffset between {@link #mTownInitialTopOffset} and {@link #mTownFinalTopOffset} depending on {@link #mPercent}
                 */
                float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
                townScale = TOWN_INITIAL_SCALE + (TOWN_FINAL_SCALE - TOWN_INITIAL_SCALE) * scalePercent;
                townTopOffset = mTownInitialTopOffset - (mTownFinalTopOffset - mTownInitialTopOffset) * scalePercent;
                townMoveOffset = mTownMoveOffset * (1.0f - scalePercent);
            } else {
                float scalePercent = dragPercent / SCALE_START_PERCENT;
                townScale = TOWN_INITIAL_SCALE;
                townTopOffset = mTownInitialTopOffset;
                townMoveOffset = mTownMoveOffset * scalePercent;
            }

            float offsetX = -(mScreenWidth * townScale - mScreenWidth) / 2.0f;
            float offsetY = (1.0f - dragPercent) * mParent.getTotalDragDistance() // Offset canvas moving
                    + townTopOffset
                    - mTownHeight * (townScale - 1.0f) / 2 // Offset town scaling
                    + townMoveOffset; // Give it a little move

            matrix.postScale(townScale, townScale);
            matrix.postTranslate(offsetX, offsetY);

            canvas.drawBitmap(mTown, matrix, null);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, mSkyHeight + top);
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
        }

        @Override
        public void stop() {
            mParent.clearAnimation();
            isRefreshing = false;
            resetOriginals();
        }

        private void resetOriginals() {
            setPercent(0);
            setRotate(0);
        }

        private void setPercent(float percent) {
            mPercent = percent;
        }

        private void setPercent(float percent, boolean invalidate) {
            setPercent(percent);
            if (invalidate) setRotate(percent);
        }

        public void offsetTopAndBottom(int offset) {
            mTop += offset;
            invalidateSelf();
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
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        // /**
        // * Our animation depend on type of current work of refreshing. We should to do different things when it's end of refreshing
        // *
        // * @param endOfRefreshing - we will check current state of refresh with this
        // */
        // public void setEndOfRefreshing(boolean endOfRefreshing) {
        // mEndOfRefreshing = endOfRefreshing;
        // }
    }
}

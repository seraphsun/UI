package com.design.code.view.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.design.code.R;
import com.design.code.view.slider.anim.AnimBase;
import com.design.code.view.slider.base.PagerBase;
import com.design.code.view.slider.base.PagerIndicator;
import com.design.code.view.slider.base.PagerInfinite;
import com.design.code.view.slider.base.PagerInfiniteView;
import com.design.code.view.slider.transformer.TransformerBase;
import com.design.code.view.slider.transformer.TransformerCubeIn;
import com.design.code.view.slider.transformer.TransformerDefault;
import com.design.code.view.slider.transformer.TransformerDepthPage;
import com.design.code.view.slider.transformer.TransformerFade;
import com.design.code.view.slider.transformer.TransformerFlipHorizontal;
import com.design.code.view.slider.transformer.TransformerFlipPageView;
import com.design.code.view.slider.transformer.TransformerFold;
import com.design.code.view.slider.transformer.TransformerRotateDown;
import com.design.code.view.slider.transformer.TransformerRotateUp;
import com.design.code.view.slider.transformer.TransformerStack;
import com.design.code.view.slider.transformer.TransformerTablet;
import com.design.code.view.slider.transformer.TransformerToBackground;
import com.design.code.view.slider.transformer.TransformerToForeground;
import com.design.code.view.slider.transformer.TransformerZoomIn;
import com.design.code.view.slider.transformer.TransformerZoomOut;
import com.design.code.view.slider.transformer.TransformerZoomOutSlide;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这是一个将viewpager和viewpagerIndicator合并的布局
 * <p/>
 * There is some properties you can set in XML:
 * <p/>
 * indicator_visibility
 * visible
 * invisible
 * <p/>
 * indicator_shape
 * oval
 * rect
 * <p/>
 * indicator_selected_color
 * <p/>
 * indicator_unselected_color
 * <p/>
 * indicator_selected_drawable
 * <p/>
 * indicator_unselected_drawable
 * <p/>
 * pager_animation
 * Default
 * Accordion
 * Background2Foreground
 * CubeIn
 * DepthPage
 * Fade
 * FlipHorizontal
 * FlipPage
 * Foreground2Background
 * RotateDown
 * RotateUp
 * Stack
 * Tablet
 * ZoomIn
 * ZoomOutSlide
 * ZoomOut
 * <p/>
 * pager_animation_span
 * <p/>
 * Created by Ignacey 2016/1/13.
 */
public class SliderRelativeLayout extends RelativeLayout {

    /**
     * {@link PagerBase} transformer time span.
     */
    public int mTransformerSpan = 1100;
    public int mTransformerId;

    private boolean mAutoCycle;
    /**
     * If {@link PagerBase} is Cycling
     */
    private boolean mCycling;

    /**
     * preset transformers and their names
     */
    public enum Transformer {
        Default("Default"),
        Accordion("Accordion"),
        Background2Foreground("Background2Foreground"),
        CubeIn("CubeIn"),
        DepthPage("DepthPage"),
        Fade("Fade"),
        FlipHorizontal("FlipHorizontal"),
        FlipPage("FlipPage"),
        Foreground2Background("Foreground2Background"),
        RotateDown("RotateDown"),
        RotateUp("RotateUp"),
        Stack("Stack"),
        Tablet("Tablet"),
        ZoomIn("ZoomIn"),
        ZoomOutSlide("ZoomOutSlide"),
        ZoomOut("ZoomOut");

        private final String name;

        Transformer(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }

        public boolean equals(String other) {
            return (other != null) && name.equals(other);
        }
    }

    /**
     * Visibility of {@link PagerIndicator}
     */
    private PagerIndicator.IndicatorVisibility mIndicatorVisibility = PagerIndicator.IndicatorVisibility.Visible;
    /**
     * PagerInfinite adapter.
     */
    private SliderAdapter mSliderAdapter;
    /**
     * PagerInfinite is extended from PagerBase. As the name says, it can scroll without bounder.
     */
    private PagerInfinite mViewPager;
    /**
     * {@link PagerBase} indicator.
     */
    private PagerIndicator mIndicator;
    /**
     * Determine if auto recover after user touch the {@link PagerBase}
     */
    private boolean mAutoRecover = true;
    /**
     * For resuming the cycle, after user touch or click the {@link PagerBase}.
     */
    private Timer mResumingTimer;
    private TimerTask mResumingTask;

    /**
     * the duration between animation.
     */
    private long mSliderDuration = 4000;
    /**
     * A timer and a TimerTask using to cycle the {@link PagerBase}.
     */
    private Timer mCycleTimer;
    private TimerTask mCycleTask;
    private android.os.Handler mh = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moveNextPosition(true);
        }
    };

    public enum PresetIndicators {
        Center_Bottom("Center_Bottom", R.id.default_center_bottom_indicator),
        Right_Bottom("Right_Bottom", R.id.default_bottom_right_indicator),
        Left_Bottom("Left_Bottom", R.id.default_bottom_left_indicator),
        Center_Top("Center_Top", R.id.default_center_top_indicator),
        Right_Top("Right_Top", R.id.default_center_top_right_indicator),
        Left_Top("Left_Top", R.id.default_center_top_left_indicator);

        private final String name;
        private final int id;

        PresetIndicators(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String toString() {
            return name;
        }

        public int getResourceId() {
            return id;
        }
    }

    private TransformerBase mViewPagerTransformer;
    private AnimBase mCustomAnimation;

    /**
     * 构造函数
     */
    public SliderRelativeLayout(Context context) {
        this(context, null);
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SliderStyle);
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.fragment_1_slider_pager_layout, this, true);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SliderRelativeLayout, defStyle, 0);
        mTransformerSpan = attributes.getInteger(R.styleable.SliderRelativeLayout_pager_animation_span, 1100);
        mTransformerId = attributes.getInt(R.styleable.SliderRelativeLayout_pager_animation, Transformer.Default.ordinal());
        mAutoCycle = attributes.getBoolean(R.styleable.SliderRelativeLayout_auto_cycle, true);
        int visibility = attributes.getInt(R.styleable.SliderRelativeLayout_indicator_visibility, 0);
        for (PagerIndicator.IndicatorVisibility v : PagerIndicator.IndicatorVisibility.values()) {
            if (v.ordinal() == visibility) {
                mIndicatorVisibility = v;
                break;
            }
        }
        mSliderAdapter = new SliderAdapter();
        PagerAdapter wrappedAdapter = new PagerInfinite.InfinitePagerAdapter(mSliderAdapter);

        mViewPager = (PagerInfinite) findViewById(R.id.daimajia_slider_viewpager);
        mViewPager.setAdapter(wrappedAdapter);

        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });

        attributes.recycle();
        setPresetIndicator(PresetIndicators.Center_Bottom);
        setPresetTransformer(mTransformerId);
        setSliderTransformDuration(mTransformerSpan, null);
        setIndicatorVisibility(mIndicatorVisibility);
        if (mAutoCycle) {
            startAutoCycle();
        }
    }

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle() {
        if (!mAutoRecover || !mAutoCycle) {
            return;
        }

        if (!mCycling) {
            if (mResumingTask != null && mResumingTimer != null) {
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startAutoCycle();
                }
            };
            mResumingTimer.schedule(mResumingTask, 6000);
        }
    }

    public void startAutoCycle() {
        startAutoCycle(mSliderDuration, mSliderDuration, mAutoRecover);
    }

    /**
     * start auto cycle.
     *
     * @param delay       delay time
     * @param duration    animation duration time.
     * @param autoRecover if recover after user touches the slider.
     */
    public void startAutoCycle(long delay, long duration, boolean autoRecover) {
        if (mCycleTimer != null) mCycleTimer.cancel();
        if (mCycleTask != null) mCycleTask.cancel();
        if (mResumingTask != null) mResumingTask.cancel();
        if (mResumingTimer != null) mResumingTimer.cancel();
        mSliderDuration = duration;
        mCycleTimer = new Timer();
        mAutoRecover = autoRecover;
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask, delay, mSliderDuration);
        mCycling = true;
        mAutoCycle = true;
    }

    /**
     * move to next slide.
     */
    public void moveNextPosition(boolean smooth) {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, smooth);
    }

    private SliderAdapter getRealAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return ((PagerInfinite.InfinitePagerAdapter) adapter).getRealAdapter();
        }
        return null;
    }

    public void moveNextPosition() {
        moveNextPosition(true);
    }

    public void setPresetIndicator(PresetIndicators presetIndicator) {
        PagerIndicator pagerIndicator = (PagerIndicator) findViewById(presetIndicator.getResourceId());
        setCustomIndicator(pagerIndicator);
    }

    public void setCustomIndicator(PagerIndicator indicator) {
        if (mIndicator != null) {
            mIndicator.destroySelf();
        }
        mIndicator = indicator;
        mIndicator.setIndicatorVisibility(mIndicatorVisibility);
        mIndicator.setViewPager(mViewPager);
        mIndicator.redraw();
    }

    /**
     * set a preset viewpager transformer by id.
     */
    public void setPresetTransformer(int transformerId) {
        for (Transformer t : Transformer.values()) {
            if (t.ordinal() == transformerId) {
                setPresetTransformer(t);
                break;
            }
        }
    }

    /**
     * set preset PagerTransformer via the name of transforemer.
     */
    public void setPresetTransformer(String transformerName) {
        for (Transformer t : Transformer.values()) {
            if (t.equals(transformerName)) {
                setPresetTransformer(t);
                return;
            }
        }
    }

    /**
     * pretty much right? enjoy it.
     */
    public void setPresetTransformer(Transformer ts) {
        TransformerBase t = null;
        switch (ts) {
            case Default:
                t = new TransformerDefault();
                break;
            case Accordion:
                t = new TransformerFold();
                break;
            case Background2Foreground:
                t = new TransformerToForeground();
                break;
            case CubeIn:
                t = new TransformerCubeIn();
                break;
            case DepthPage:
                t = new TransformerDepthPage();
                break;
            case Fade:
                t = new TransformerFade();
                break;
            case FlipHorizontal:
                t = new TransformerFlipHorizontal();
                break;
            case FlipPage:
                t = new TransformerFlipPageView();
                break;
            case Foreground2Background:
                t = new TransformerToBackground();
                break;
            case RotateDown:
                t = new TransformerRotateDown();
                break;
            case RotateUp:
                t = new TransformerRotateUp();
                break;
            case Stack:
                t = new TransformerStack();
                break;
            case Tablet:
                t = new TransformerTablet();
                break;
            case ZoomIn:
                t = new TransformerZoomIn();
                break;
            case ZoomOutSlide:
                t = new TransformerZoomOutSlide();
                break;
            case ZoomOut:
                t = new TransformerZoomOut();
                break;
        }
        setPagerTransformer(true, t);
    }

    /**
     * set ViewPager transformer.
     */
    public void setPagerTransformer(boolean reverseDrawingOrder, TransformerBase transformer) {
        mViewPagerTransformer = transformer;
        mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        mViewPager.setPageTransformer(reverseDrawingOrder, mViewPagerTransformer);
    }

    /**
     * set the duration between two slider changes.
     */
    public void setSliderTransformDuration(int period, Interpolator interpolator) {
        try {
            Field mScroller = PagerBase.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), interpolator, period);
            mScroller.set(mViewPager, scroller);
        } catch (Exception ignored) {
        }
    }

    /**
     * Set the visibility of the indicators.
     */
    public void setIndicatorVisibility(PagerIndicator.IndicatorVisibility visibility) {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setIndicatorVisibility(visibility);
    }

    public PagerIndicator.IndicatorVisibility getIndicatorVisibility() {
        if (mIndicator == null) {
            return mIndicator.getIndicatorVisibility();
        }
        return PagerIndicator.IndicatorVisibility.Invisible;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
        }
        return false;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle() {
        if (mCycling) {
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycling = false;
        } else {
            if (mResumingTimer != null && mResumingTask != null) {
                recoverCycle();
            }
        }
    }

    /**
     * stop the auto circle
     */
    public void stopAutoCycle() {
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }
        mAutoCycle = false;
        mCycling = false;
    }

    /**
     * set the duration between two slider changes. the duration value must >= 500
     */
    public void setDuration(long duration) {
        if (duration >= 500) {
            mSliderDuration = duration;
            if (mAutoCycle && mCycling) {
                startAutoCycle();
            }
        }
    }

    /**
     * Inject your custom animation into PageTransformer
     */
    public void setCustomAnimation(AnimBase animation) {
        mCustomAnimation = animation;
        if (mViewPagerTransformer != null) {
            mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        }
    }

    public void setCurrentPosition(int position) {
        setCurrentPosition(position, true);
    }

    /**
     * set current slider
     */
    public void setCurrentPosition(int position, boolean smooth) {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");
        if (position >= getRealAdapter().getCount()) {
            throw new IllegalStateException("Item position is not exist");
        }
        int p = mViewPager.getCurrentItem() % getRealAdapter().getCount();
        int n = (position - p) + mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(n, smooth);
    }

    /**
     * get the current item position
     */
    public int getCurrentPosition() {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        return mViewPager.getCurrentItem() % getRealAdapter().getCount();
    }

    public PagerIndicator getPagerIndicator() {
        return mIndicator;
    }

    private PagerInfinite.InfinitePagerAdapter getWrapperAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return (PagerInfinite.InfinitePagerAdapter) adapter;
        } else {
            return null;
        }
    }

    public PagerInfiniteView getCurrentSlider() {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        int count = getRealAdapter().getCount();
        int realCount = mViewPager.getCurrentItem() % count;
        return getRealAdapter().getSliderView(realCount);
    }

    public void movePrevPosition() {
        movePrevPosition(true);
    }

    /**
     * move to prev slide.
     */
    public void movePrevPosition(boolean smooth) {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a slider adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, smooth);
    }

    /**
     * remove  the slider at the position. Notice: It's a not perfect method, a very small bug still exists.
     */
    public void removeSliderAt(int position) {
        if (getRealAdapter() != null) {
            getRealAdapter().removeSliderAt(position);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem(), false);
        }
    }

    /**
     * remove all the sliders. Notice: It's a not perfect method, a very small bug still exists.
     */
    public void removeAllSliders() {
        if (getRealAdapter() != null) {
            int count = getRealAdapter().getCount();
            getRealAdapter().removeAllSliders();
            //a small bug, but fixed by this trick.
            //bug: when remove adapter's all the sliders.some caching slider still alive.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + count, false);
        }
    }

    public <T extends PagerInfiniteView> void addSlider(T imageContent) {
        mSliderAdapter.addSlider(imageContent);
    }

    public void addOnPageChangeListener(PagerBase.OnPageChangeListener onPageChangeListener) {
        if (onPageChangeListener != null) {
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        }
    }

    public void removeOnPageChangeListener(PagerBase.OnPageChangeListener onPageChangeListener) {
        mViewPager.removeOnPageChangeListener(onPageChangeListener);
    }


    /**
     * 滑动布局的适配器
     */
    public static class SliderAdapter extends PagerAdapter implements PagerInfiniteView.ImageLoadListener {

        private ArrayList<PagerInfiniteView> mImageContents;

        public SliderAdapter() {
            mImageContents = new ArrayList<>();
        }

        public <T extends PagerInfiniteView> void addSlider(T slider) {
            slider.setOnImageLoadListener(this);
            mImageContents.add(slider);
            notifyDataSetChanged();
        }

        public PagerInfiniteView getSliderView(int position) {
            if (position < 0 || position >= mImageContents.size()) {
                return null;
            } else {
                return mImageContents.get(position);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public <T extends PagerInfiniteView> void removeSlider(T slider) {
            if (mImageContents.contains(slider)) {
                mImageContents.remove(slider);
                notifyDataSetChanged();
            }
        }

        public void removeSliderAt(int position) {
            if (mImageContents.size() > position) {
                mImageContents.remove(position);
                notifyDataSetChanged();
            }
        }

        public void removeAllSliders() {
            mImageContents.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImageContents.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PagerInfiniteView b = mImageContents.get(position);
            View v = b.getView();
            container.addView(v);
            return v;
        }

        @Override
        public void onStart(PagerInfiniteView target) {

        }

        /**
         * When image download error, then remove.
         */
        @Override
        public void onEnd(boolean result, PagerInfiniteView target) {
            if (!target.isErrorDisappear() || result) {
                return;
            }
            for (PagerInfiniteView slider : mImageContents) {
                if (slider.equals(target)) {
                    removeSlider(target);
                    break;
                }
            }
        }
    }

    /**
     * 固定速度的滚动器
     */
    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int period) {
            this(context, interpolator);
            mDuration = period;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}

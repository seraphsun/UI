package com.design.code;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.design.code.util.UtilDensity;
import com.design.code.view.guide.ViewBooks;
import com.design.code.view.guide.ViewDays;
import com.design.code.view.guide.ViewGalaxy;

import java.util.HashMap;

/**
 * Created by Ignacey 2016/1/6.
 */
public class GuideActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private LinearLayout mIndicatorLayout;
    private TextView mIndicatorView[];
//    private Drawable mPagerBackground;

    private ImageView mCenterBox, mCamcordImage, mClockImage, mGraphImage, mAudioImage, mQuoteImage, mMapImage, mWordPressImage;
//    private TextView mTitleText, mDescText;

    private AnimatorSet mAnimatorSet;

    private boolean mSecondPageSelected;
    private HashMap<ImageView, Float> mOriginalXValuesMap = new HashMap<>();
    private int mSelectedPosition = -1;

    // Second screen
    private ViewDays mAnimationView;
    private float mPreviousPositionOffset;
    private boolean mViewPagerScrollingLeft;
    private int mPreviousPosition;
    private ViewBooks mViewBooks;


    // Third screen
    private boolean mShouldSpheresRotate = true;
    private ViewGalaxy mRoundView;
//    private boolean mThirdPageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        setUpViews();
    }

    private void setUpViews() {
        mPager = (ViewPager) findViewById(R.id.vp_guide);
//        mPagerBackground = mPager.getBackground();
        mIndicatorLayout = (LinearLayout) findViewById(R.id.ll_guide_indicator);

        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        setIndicatorLayout();
        mPager.addOnPageChangeListener(new MyOnPageChangeListener());
        mPager.bringToFront();
        // mPagerBackground.setAlpha(0);

        // mPager.setOffscreenPageLimit(2);
    }

    private void setIndicatorLayout() {
        int dotsCount = NUM_PAGES;
        mIndicatorView = new TextView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            mIndicatorView[i] = new TextView(this);
            mIndicatorView[i].setWidth(UtilDensity.dp2px(GuideActivity.this, 12));
            mIndicatorView[i].setHeight(UtilDensity.dp2px(GuideActivity.this, 12));
            mIndicatorView[i].setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, (UtilDensity.dp2px(GuideActivity.this, 15)), 0);
            mIndicatorView[i].setLayoutParams(params);
            mIndicatorView[i].setBackgroundResource(R.drawable.guide_inset_indicator_gray);
            mIndicatorLayout.addView(mIndicatorView[i]);
        }

        // mIndicatorView[0].setWidth(20);
        // mIndicatorView[0].setHeight(20);
        mIndicatorView[0].setBackgroundResource(R.drawable.guide_inset_indicator_red);
        mIndicatorView[0].setGravity(Gravity.CENTER);
    }

    /**
     * 初始化第一个页面
     */
    private void initFirstScreenViews(View rootView, final Bundle savedInstanceState) {
        mCenterBox = (ImageView) rootView.findViewById(R.id.center_box);
        mCamcordImage = (ImageView) rootView.findViewById(R.id.imageView);
        mClockImage = (ImageView) rootView.findViewById(R.id.imageView6);
        mGraphImage = (ImageView) rootView.findViewById(R.id.imageView3);
        mAudioImage = (ImageView) rootView.findViewById(R.id.imageView4);
        mQuoteImage = (ImageView) rootView.findViewById(R.id.imageView5);
        mMapImage = (ImageView) rootView.findViewById(R.id.imageView2);
        mWordPressImage = (ImageView) rootView.findViewById(R.id.imageView7);

        initializeAlpha();

        rootView.post(new Runnable() {
            @Override
            public void run() {
                getOriginalXValues(savedInstanceState);
            }
        });

        if (savedInstanceState == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doFadeAnimation();
                }
            }, 700);
        }
    }

    private void initializeAlpha() {
        mCamcordImage.setAlpha(0f);
        mClockImage.setAlpha(0f);
        mGraphImage.setAlpha(0f);
        mAudioImage.setAlpha(0f);
        mQuoteImage.setAlpha(0f);
        mMapImage.setAlpha(0f);
        mWordPressImage.setAlpha(0f);
    }

    private void getOriginalXValues(Bundle savedInstanceState) {
        mOriginalXValuesMap.put(mCenterBox, mCenterBox.getX());
        mOriginalXValuesMap.put(mCamcordImage, mCamcordImage.getX());
        mOriginalXValuesMap.put(mClockImage, mClockImage.getX());
        mOriginalXValuesMap.put(mGraphImage, mGraphImage.getX());
        mOriginalXValuesMap.put(mAudioImage, mAudioImage.getX());
        mOriginalXValuesMap.put(mQuoteImage, mQuoteImage.getX());
        mOriginalXValuesMap.put(mMapImage, mMapImage.getX());
        mOriginalXValuesMap.put(mWordPressImage, mWordPressImage.getX());

        if (savedInstanceState == null) {
            mPager.setPageTransformer(true, new MyTransformer());
        }
    }

    private void doFadeAnimation() {
        ObjectAnimator fadeCamcord = ObjectAnimator.ofFloat(mCamcordImage, "alpha", 0f, 1f);
        fadeCamcord.setDuration(700);

        ObjectAnimator fadeClock = ObjectAnimator.ofFloat(mClockImage, "alpha", 0f, 1f);
        fadeClock.setDuration(700);

        ObjectAnimator fadeGraph = ObjectAnimator.ofFloat(mGraphImage, "alpha", 0f, 1f);
        fadeGraph.setDuration(700);

        ObjectAnimator fadeAudio = ObjectAnimator.ofFloat(mAudioImage, "alpha", 0f, 1f);
        fadeAudio.setDuration(700);

        ObjectAnimator fadeQuote = ObjectAnimator.ofFloat(mQuoteImage, "alpha", 0f, 1f);
        fadeQuote.setDuration(700);

        ObjectAnimator fadeMap = ObjectAnimator.ofFloat(mMapImage, "alpha", 0f, 1f);
        fadeMap.setDuration(700);

        ObjectAnimator fadeWordpress = ObjectAnimator.ofFloat(mWordPressImage, "alpha", 0f, 1f);
        fadeWordpress.setDuration(700);

        // 1 5    3 2  7 6  4
        mAnimatorSet = new AnimatorSet();
        fadeAudio.setStartDelay(50);
        fadeGraph.setStartDelay(200);
        fadeWordpress.setStartDelay(500);
        fadeClock.setStartDelay(700);
        fadeMap.setStartDelay(900);
        fadeQuote.setStartDelay(1100);

        mAnimatorSet.play(fadeCamcord).with(fadeAudio).with(fadeGraph).with(fadeWordpress).with(fadeClock).with(fadeMap).with(fadeQuote);
        mAnimatorSet.start();
    }

    /**
     * 初始化第二个页面
     */
    private void initSecondScreenViews(View rootView) {
//        final RelativeLayout secondScreenRoot = (RelativeLayout) rootView.findViewById(R.id.root);

        // final ImageView centerBox=(ImageView)rootView.findViewById(R.id.center_box_second);
        mViewBooks = (ViewBooks) rootView.findViewById(R.id.center_box_second);
        mAnimationView = (ViewDays) rootView.findViewById(R.id.animation_view);
    }

    /**
     * 初始化第三个页面
     */
    private void initThirdScreenViews(View rootView) {
        mRoundView = (ViewGalaxy) rootView.findViewById(R.id.round_view);
        Button mLetsGoButton = (Button) rootView.findViewById(R.id.letsgo);

        mLetsGoButton.setOnClickListener(clickListener);
        mRoundView.setContext(this);
    }

    /**
     * 跳转MainActivity
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.letsgo:
                    mRoundView.startNextScreen();
                    break;
            }
        }
    };

    private void setViewsInOriginalPosition() {
        mCenterBox.setX(mOriginalXValuesMap.get(mCenterBox));
        mCamcordImage.setX(mOriginalXValuesMap.get(mCamcordImage));
        mClockImage.setX(mOriginalXValuesMap.get(mClockImage));
        mGraphImage.setX(mOriginalXValuesMap.get(mGraphImage));
        mAudioImage.setX(mOriginalXValuesMap.get(mAudioImage));
        mQuoteImage.setX(mOriginalXValuesMap.get(mQuoteImage));
        mMapImage.setX(mOriginalXValuesMap.get(mMapImage));
        mWordPressImage.setX(mOriginalXValuesMap.get(mWordPressImage));

        initializeAlpha();
    }

    private void animateBookView() {
        mViewBooks.fadeInTheLines();
    }

    private void animateSecondScreen(float position, int direction) {
        if (direction == 0) {
            mAnimationView.animateSecondScreenClock(position);
        } else {
            mAnimationView.animateSecondScreenAntiClock(position);
        }
    }

    private void moveTheSpheres(float position, int pageWidth) {
        float camcordPos = (float) ((1 - position) * 0.15 * pageWidth);
        if (camcordPos > (-1 * mOriginalXValuesMap.get(mCamcordImage))) {
            mCamcordImage.setTranslationX(camcordPos);
        }

        float clockPos = (float) ((1 - position) * 0.50 * pageWidth);
        if (clockPos > (-1 * mOriginalXValuesMap.get(mClockImage))) {
            mClockImage.setTranslationX(clockPos);
        }

        float graphPos = (float) ((1 - position) * 0.50 * pageWidth);
        if (graphPos > (-1 * mOriginalXValuesMap.get(mGraphImage))) {
            mGraphImage.setTranslationX(graphPos);
        }

        float audioPos = (float) ((1 - position) * 0.30 * pageWidth);
        if (audioPos > (-1 * mOriginalXValuesMap.get(mAudioImage))) {
            mAudioImage.setTranslationX(audioPos);
        }

        float quotePos = (float) (-(1 - position) * 0.37 * pageWidth);
        if (quotePos > (-1 * mOriginalXValuesMap.get(mQuoteImage))) {
            mQuoteImage.setTranslationX(quotePos);
        }

        float mapPos = (float) (-(1 - position) * 1.1 * pageWidth);
        if (mapPos > (-1 * mOriginalXValuesMap.get(mMapImage))) {
            mMapImage.setTranslationX(mapPos);
        }

        float wordpressPos = (float) (-(1 - position) * 0.37 * pageWidth);
        if (wordpressPos > (-1 * mOriginalXValuesMap.get(mWordPressImage))) {
            mWordPressImage.setTranslationX(wordpressPos);
        }
    }

    /**
     * ViewPager的页面状态适配器
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlideFragment fragment = new ScreenSlideFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /**
     * ViewPager的视图容器
     */
    public class ScreenSlideFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int position = args.getInt("position");
            int layoutId = getLayoutId(position);

            ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);
            if (position == 0) {
                initFirstScreenViews(rootView, savedInstanceState);
            }
            if (position == 1) {
                initSecondScreenViews(rootView);
            }
            if (position == 2) {
                initThirdScreenViews(rootView);
            }
            return rootView;
        }

        private int getLayoutId(int position) {
            int id = 0;

            if (position == 0) {
                id = R.layout.guide_screen_first;
            } else if (position == 1) {
                id = R.layout.guide_screen_second;
            } else if (position == 2) {
                id = R.layout.guide_screen_third;
            }
            return id;
        }
    }

    /**
     * pager滚动监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Scrolling left or right
            if ((positionOffset > mPreviousPositionOffset && position == mPreviousPosition) || (positionOffset < mPreviousPositionOffset && position > mPreviousPosition)) {
                mViewPagerScrollingLeft = true;
            } else if (positionOffset < mPreviousPositionOffset) {
                mViewPagerScrollingLeft = false;
            }
            mPreviousPositionOffset = positionOffset;
            mPreviousPosition = position;

            // FADE the indicator layout
            if (position == 1 && mViewPagerScrollingLeft) {
                mIndicatorLayout.setAlpha(1 - positionOffset);
            } else if (position == 1 && !mViewPagerScrollingLeft) {
                mIndicatorLayout.setAlpha(1 - positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                mSelectedPosition = 1;
                mSecondPageSelected = true;
                setViewsInOriginalPosition();
                // initializeAlpha();
                if (mAnimatorSet != null) {
                    mAnimatorSet.cancel();
                }
                animateBookView();
            }

            if (position == 0) {
                mSelectedPosition = 0;
                doFadeAnimation();
            }

            for (TextView aMIndicatorView : mIndicatorView) {
                aMIndicatorView.setBackgroundResource(R.drawable.guide_inset_indicator_gray);
            }
            mIndicatorView[position].setBackgroundResource(R.drawable.guide_inset_indicator_red);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                mShouldSpheresRotate = false;
            } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                mShouldSpheresRotate = true;
            }
            if (mRoundView != null) {
                mRoundView.setRotatingPermission(mShouldSpheresRotate);
            }

            if (mSelectedPosition == 0 && state == ViewPager.SCROLL_STATE_IDLE) {
                mSecondPageSelected = false;
            }
        }
    }

    /**
     * pager变速器
     */
    private class MyTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            if ((mViewPagerScrollingLeft && page.findViewById(R.id.center_box) != null)) {
                animateSecondScreen(position, 0);
            }

            if (!mViewPagerScrollingLeft && page.findViewById(R.id.center_box_second) != null) {
                animateSecondScreen(position, 1);
            }

            if (position <= 1) {
                if (!mSecondPageSelected && page.findViewById(R.id.center_box_second) != null) {
                    moveTheSpheres(position, pageWidth);
                }

                if (!mShouldSpheresRotate && page.findViewById(R.id.center_box_third) != null) {
                    mRoundView.translateTheSpheres(position);
                }
            }
        }
    }
}

package com.design.code.view.slider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.design.code.R;
import com.design.code.base.transforms.TransformerAccordion;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class BannerPager extends FrameLayout {

    private Context context;
    private ViewPager viewPager;
    private ViewPagerItemListener mListener;
//    private ImageLoader mImageLoader;
    private Picasso mPicasso;

    private List<ImageView> imageViews;

    private LinearLayout ll_dot;
    private List<ImageView> iv_dots;

    private int delayTime;
    private int currentItem;
    private int count;

    private boolean isAutoPlay;

    private Handler handler = new Handler();

    public BannerPager(Context context) {
        this(context, null);
    }

    public BannerPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initImageLoader(context);
        initData();
    }

    public void initImageLoader(Context context) {
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .writeDebugLogs()
//                .build();
//        ImageLoader.getInstance().init(config);
//        mImageLoader = ImageLoader.getInstance();
        mPicasso = Picasso.with(context);
    }

    private void initData() {
        imageViews = new ArrayList<>();
        iv_dots = new ArrayList<>();
        delayTime = 3000;
    }

    public void setImagesRes(int[] imagesRes) {
        initLayout();
        initImgFromRes(imagesRes);
        showTime();
    }

    public void setImagesUrl(String[] imagesUrl, ViewPagerItemListener listener) {
        this.mListener = listener;
        initLayout();
        initImgFromNet(imagesUrl);
        showTime();
    }

    private void initLayout() {
        imageViews.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.banner_pager_layout, this, true);
        viewPager = (ViewPager) view.findViewById(R.id.vp);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_dot);
        ll_dot.removeAllViews();
    }

    private void initImgFromRes(int[] imagesRes) {
        count = imagesRes.length;
        for (int i = 0; i < count; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            iv_dot.setImageResource(R.mipmap.dot_blur);
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }
        iv_dots.get(0).setImageResource(R.mipmap.dot_focus);

        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setBackgroundResource(R.mipmap.wang_jun);
            if (i == 0) {
                iv.setImageResource(imagesRes[count - 1]);
            } else if (i == count + 1) {
                iv.setImageResource(imagesRes[0]);
            } else {
                iv.setImageResource(imagesRes[i - 1]);
            }
            imageViews.add(iv);
        }
    }

    private void initImgFromNet(String[] imagesUrl) {
        count = imagesUrl.length;
        for (int i = 0; i < count; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }
        iv_dots.get(0).setImageResource(R.mipmap.dot_focus);

        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setBackgroundResource(R.mipmap.wang_jun);
            if (i == 0) {
                mPicasso.load(imagesUrl[count - 1]).into(iv);
            } else if (i == count + 1) {
                mPicasso.load(imagesUrl[0]).into(iv);
            } else {
                mPicasso.load(imagesUrl[i - 1]).into(iv);
            }
            imageViews.add(iv);
        }
    }

    private void showTime() {
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setPageTransformer(true, new TransformerAccordion());
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(viewPager.getContext(), sInterpolator);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        }

        viewPager.setFocusable(true);
        viewPager.setCurrentItem(1);
        currentItem = 1;
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.postDelayed(task, 2000);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    viewPager.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    viewPager.setCurrentItem(currentItem);
                    handler.postDelayed(task, 3000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    public class FixedScroller extends Scroller {
        private int mDuration = 500;

        public FixedScroller(Context context) {
            super(context);
        }

        public FixedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
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

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(imageViews.get(position));
            if (mListener != null) {
                imageViews.get(position).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onImageClick(position);
                    }
                });
            }

            return imageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(count, false);
                    } else if (viewPager.getCurrentItem() == count + 1) {
                        viewPager.setCurrentItem(1, false);
                    }
                    currentItem = viewPager.getCurrentItem();
                    isAutoPlay = true;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < iv_dots.size(); i++) {
                if (i == arg0 - 1) {
                    iv_dots.get(i).setImageResource(R.mipmap.dot_focus);
                } else {
                    iv_dots.get(i).setImageResource(R.mipmap.dot_blur);
                }
            }
        }
    }

    /**
     * 轮播控件的监听事件
     *
     * @author hanf
     */
    public interface ViewPagerItemListener {
        /**
         * 单击图片事件
         */
        void onImageClick(int position);
    }
}

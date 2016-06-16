package com.design.code.view.slider.base;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.design.code.view.slider.SliderRelativeLayout;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around effect. Should be used with an {@link InfinitePagerAdapter}.
 * Created by Ignacey 2016/1/12.
 */
public class PagerInfinite extends PagerBase {

    public PagerInfinite(Context context) {
        super(context);
    }

    public PagerInfinite(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * A PagerAdapter that wraps around another PagerAdapter to handle paging wrap-around.
     */
    public static class InfinitePagerAdapter extends PagerAdapter {

        private SliderRelativeLayout.SliderAdapter adapter;

        public InfinitePagerAdapter(SliderRelativeLayout.SliderAdapter adapter) {
            this.adapter = adapter;
        }

        public SliderRelativeLayout.SliderAdapter getRealAdapter() {
            return this.adapter;
        }

        @Override
        public int getCount() {
            // warning: scrolling to very high values (1,000,000+) results in strange drawing behaviour
            return Integer.MAX_VALUE;
        }

        /**
         * @return the {@link #getCount()} result of the wrapped adapter
         */
        public int getRealCount() {
            return adapter.getCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (getRealCount() == 0) {
                return null;
            }
            int virtualPosition = position % getRealCount();

            // only expose virtual position to the inner adapter
            return adapter.instantiateItem(container, virtualPosition);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (getRealCount() == 0) {
                return;
            }
            int virtualPosition = position % getRealCount();

            // only expose virtual position to the inner adapter
            adapter.destroyItem(container, virtualPosition, object);
        }

        /**
         * Delegate rest of methods directly to the inner adapter.
         */
        @Override
        public void finishUpdate(ViewGroup container) {
            adapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            adapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            adapter.startUpdate(container);
        }
    }
}

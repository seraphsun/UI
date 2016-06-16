package com.design.code.view.slider.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.design.code.R;

import java.util.ArrayList;

/**
 * Created by Ignacey 2016/1/12.
 */
public class PagerIndicator extends LinearLayout implements PagerBase.OnPageChangeListener {

    private Context mContext;
    /**
     * bind this Indicator with {@link PagerBase}
     */
    private PagerBase mPager;

    public enum IndicatorVisibility {
        Visible,
        Invisible
    }

    private IndicatorVisibility mVisibility = IndicatorVisibility.Visible;

    public enum Shape {
        Oval,
//        Rectangle
    }

    /**
     * Custom unselected indicator style resource id.
     */
    private int mUserSetSelectedIndicatorResId;
    /**
     * Custom selected indicator style resource id.
     */
    private int mUserSetUnSelectedIndicatorResId;

    private GradientDrawable mSelectedGradientDrawable, mUnSelectedGradientDrawable;

    private float mSelectedPadding_Left, mSelectedPadding_Right, mSelectedPadding_Top, mSelectedPadding_Bottom;
    private float mUnSelectedPadding_Left, mUnSelectedPadding_Right, mUnSelectedPadding_Top, mUnSelectedPadding_Bottom;

    private LayerDrawable mSelectedLayerDrawable, mUnSelectedLayerDrawable;

    private Drawable mSelectedDrawable, mUnselectedDrawable;

    public enum Unit {
        DP,
        Px
    }

    /**
     * Put all the indicators into a ArrayList, so we can remove them easily.
     */
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    /**
     * Variable to remember the previous selected indicator.
     */
    private ImageView mPreviousSelectedIndicator;

    /**
     * This value is from {@link com.design.code.view.slider.SliderRelativeLayout.SliderAdapter} getRealCount() the indicator count that we should draw.
     */
    private int mItemCount = 0;
    /**
     * Previous selected indicator position.
     */
    private int mPreviousSelectedPosition;

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator, 0, 0);

        for (IndicatorVisibility visibility : IndicatorVisibility.values()) {
            if (visibility.ordinal() == attributes.getInt(R.styleable.PagerIndicator_visibility, IndicatorVisibility.Visible.ordinal())) {
                mVisibility = visibility;
                break;
            }
        }

        Shape mIndicatorShape = Shape.Oval;
        for (Shape shape : Shape.values()) {
            if (shape.ordinal() == attributes.getInt(R.styleable.PagerIndicator_slider_shape, Shape.Oval.ordinal())) {
                mIndicatorShape = shape;
                break;
            }
        }

        mUserSetSelectedIndicatorResId = attributes.getResourceId(R.styleable.PagerIndicator_selected_drawable, 0);
        mUserSetUnSelectedIndicatorResId = attributes.getResourceId(R.styleable.PagerIndicator_unselected_drawable, 0);

        int mDefaultSelectedColor = attributes.getColor(R.styleable.PagerIndicator_selected_color, Color.rgb(255, 255, 255));
        int mDefaultUnSelectedColor = attributes.getColor(R.styleable.PagerIndicator_unselected_color, Color.argb(33, 255, 255, 255));

        float mDefaultSelectedWidth = attributes.getDimension(R.styleable.PagerIndicator_selected_width, (int) pxFromDp(6));
        float mDefaultSelectedHeight = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_selected_height, (int) pxFromDp(6));

        float mDefaultUnSelectedWidth = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_width, (int) pxFromDp(6));
        float mDefaultUnSelectedHeight = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_height, (int) pxFromDp(6));

        mSelectedGradientDrawable = new GradientDrawable();
        mUnSelectedGradientDrawable = new GradientDrawable();

        float mPadding_left = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_padding_left, (int) pxFromDp(3));
        float mPadding_right = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_padding_right, (int) pxFromDp(3));
        float mPadding_top = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_padding_top, (int) pxFromDp(0));
        float mPadding_bottom = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_padding_bottom, (int) pxFromDp(0));

        mSelectedPadding_Left = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_selected_padding_left, (int) mPadding_left);
        mSelectedPadding_Right = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_selected_padding_right, (int) mPadding_right);
        mSelectedPadding_Top = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_selected_padding_top, (int) mPadding_top);
        mSelectedPadding_Bottom = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_selected_padding_bottom, (int) mPadding_bottom);

        mUnSelectedPadding_Left = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_padding_left, (int) mPadding_left);
        mUnSelectedPadding_Right = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_padding_right, (int) mPadding_right);
        mUnSelectedPadding_Top = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_padding_top, (int) mPadding_top);
        mUnSelectedPadding_Bottom = attributes.getDimensionPixelSize(R.styleable.PagerIndicator_unselected_padding_bottom, (int) mPadding_bottom);

        mSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mSelectedGradientDrawable});
        mUnSelectedLayerDrawable = new LayerDrawable(new Drawable[]{mUnSelectedGradientDrawable});

        setIndicatorStyleResource(mUserSetSelectedIndicatorResId, mUserSetUnSelectedIndicatorResId);
        setDefaultIndicatorShape(mIndicatorShape);
        setDefaultSelectedIndicatorSize(mDefaultSelectedWidth, mDefaultSelectedHeight, Unit.Px);
        setDefaultUnselectedIndicatorSize(mDefaultUnSelectedWidth, mDefaultUnSelectedHeight, Unit.Px);
        setDefaultIndicatorColor(mDefaultSelectedColor, mDefaultUnSelectedColor);
        setIndicatorVisibility(mVisibility);
        attributes.recycle();
    }

    /**
     * Set Indicator style.
     *
     * @param selected   page selected drawable
     * @param unselected page unselected drawable
     */
    public void setIndicatorStyleResource(int selected, int unselected) {
        mUserSetSelectedIndicatorResId = selected;
        mUserSetUnSelectedIndicatorResId = unselected;
        if (selected == 0) {
            mSelectedDrawable = mSelectedLayerDrawable;
        } else {
            mSelectedDrawable = mContext.getResources().getDrawable(mUserSetSelectedIndicatorResId);
        }
        if (unselected == 0) {
            mUnselectedDrawable = mUnSelectedLayerDrawable;
        } else {
            mUnselectedDrawable = mContext.getResources().getDrawable(mUserSetUnSelectedIndicatorResId);
        }

        resetDrawable();
    }

    private void resetDrawable() {
        for (View imageView : mIndicators) {
            if (mPreviousSelectedIndicator != null && mPreviousSelectedIndicator.equals(imageView)) {
                ((ImageView) imageView).setImageDrawable(mSelectedDrawable);
            } else {
                ((ImageView) imageView).setImageDrawable(mUnselectedDrawable);
            }
        }
    }

    /**
     * if you are using the default indicator, this method will help you to set the shape of indicator,
     * there are two kind of shapes you  can set, oval and rect.
     *
     * @param shape shape
     */
    public void setDefaultIndicatorShape(Shape shape) {
        if (mUserSetSelectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            } else {
                mSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
            }
        }
        if (mUserSetUnSelectedIndicatorResId == 0) {
            if (shape == Shape.Oval) {
                mUnSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
            } else {
                mUnSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
            }
        }
        resetDrawable();
    }

    public void setDefaultSelectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetSelectedIndicatorResId == 0) {
            float w = width;
            float h = height;
            if (unit == Unit.DP) {
                w = pxFromDp(width);
                h = pxFromDp(height);
            }
            mSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    public void setDefaultUnselectedIndicatorSize(float width, float height, Unit unit) {
        if (mUserSetUnSelectedIndicatorResId == 0) {
            float w = width;
            float h = height;
            if (unit == Unit.DP) {
                w = pxFromDp(width);
                h = pxFromDp(height);
            }
            mUnSelectedGradientDrawable.setSize((int) w, (int) h);
            resetDrawable();
        }
    }

    private float pxFromDp(float dp) {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }

//    private float dpFromPx(float px) {
//        return px / this.getContext().getResources().getDisplayMetrics().density;
//    }

    /**
     * if you are using the default indicator ,
     * this method will help you to set the selected status and the unselected status color.
     *
     * @param selectedColor   selected
     * @param unselectedColor unselected
     */
    public void setDefaultIndicatorColor(int selectedColor, int unselectedColor) {
        if (mUserSetSelectedIndicatorResId == 0) {
            mSelectedGradientDrawable.setColor(selectedColor);
        }
        if (mUserSetUnSelectedIndicatorResId == 0) {
            mUnSelectedGradientDrawable.setColor(unselectedColor);
        }
        resetDrawable();
    }

    /**
     * set the visibility of indicator.
     *
     * @param visibility visibility
     */
    public void setIndicatorVisibility(IndicatorVisibility visibility) {
        if (visibility == IndicatorVisibility.Visible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.INVISIBLE);
        }
        resetDrawable();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mItemCount == 0) {
            return;
        }
        setItemAsSelected(position - 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void setItemAsSelected(int position) {
        if (mPreviousSelectedIndicator != null) {
            mPreviousSelectedIndicator.setImageDrawable(mUnselectedDrawable);
            mPreviousSelectedIndicator.setPadding(
                    (int) mUnSelectedPadding_Left,
                    (int) mUnSelectedPadding_Top,
                    (int) mUnSelectedPadding_Right,
                    (int) mUnSelectedPadding_Bottom
            );
        }

        ImageView currentSelected = (ImageView) getChildAt(position + 1);
        if (currentSelected != null) {
            currentSelected.setImageDrawable(mSelectedDrawable);
            currentSelected.setPadding(
                    (int) mSelectedPadding_Left,
                    (int) mSelectedPadding_Top,
                    (int) mSelectedPadding_Right,
                    (int) mSelectedPadding_Bottom
            );
            mPreviousSelectedIndicator = currentSelected;
        }
        mPreviousSelectedPosition = position;
    }

    /**
     * redraw the indicators.
     */
    public void redraw() {
        mItemCount = getShouldDrawCount();
        mPreviousSelectedIndicator = null;
        for (View i : mIndicators) {
            removeView(i);
        }

        for (int i = 0; i < mItemCount; i++) {
            ImageView indicator = new ImageView(mContext);
            indicator.setImageDrawable(mUnselectedDrawable);
            indicator.setPadding((int) mUnSelectedPadding_Left,
                    (int) mUnSelectedPadding_Top,
                    (int) mUnSelectedPadding_Right,
                    (int) mUnSelectedPadding_Bottom);
            addView(indicator);
            mIndicators.add(indicator);
        }
        setItemAsSelected(mPreviousSelectedPosition);
    }

    /**
     * since we used a adapter wrapper, so we can't getCount directly from wrapper.
     *
     * @return int
     */
    private int getShouldDrawCount() {
        if (mPager.getAdapter() instanceof PagerInfinite.InfinitePagerAdapter) {
            return ((PagerInfinite.InfinitePagerAdapter) mPager.getAdapter()).getRealCount();
        } else {
            return mPager.getAdapter().getCount();
        }
    }

    /**
     * bind indicator with viewpagerEx.
     *
     * @param pager pager
     */
    public void setViewPager(PagerBase pager) {
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("Viewpager does not have adapter instance");
        }
        mPager = pager;
        mPager.addOnPageChangeListener(this);
        ((PagerInfinite.InfinitePagerAdapter) mPager.getAdapter()).getRealAdapter().registerDataSetObserver(dataChangeObserver);
    }

    public void setDefaultIndicatorSize(float width, float height, Unit unit) {
        setDefaultSelectedIndicatorSize(width, height, unit);
        setDefaultUnselectedIndicatorSize(width, height, unit);
    }

    /**
     * clear self means unregister the dataset observer and remove all the child views(indicators).
     */
    public void destroySelf() {
        if (mPager == null || mPager.getAdapter() == null) {
            return;
        }
        PagerInfinite.InfinitePagerAdapter wrapper = (PagerInfinite.InfinitePagerAdapter) mPager.getAdapter();
        PagerAdapter adapter = wrapper.getRealAdapter();
        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataChangeObserver);
        }
        removeAllViews();
    }

    /**
     * 数据填充观察者
     */
    private DataSetObserver dataChangeObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            PagerAdapter adapter = mPager.getAdapter();
            int count;
            if (adapter instanceof PagerInfinite.InfinitePagerAdapter) {
                count = ((PagerInfinite.InfinitePagerAdapter) adapter).getRealCount();
            } else {
                count = adapter.getCount();
            }
            if (count > mItemCount) {
                for (int i = 0; i < count - mItemCount; i++) {
                    ImageView indicator = new ImageView(mContext);
                    indicator.setImageDrawable(mUnselectedDrawable);
                    indicator.setPadding((int) mUnSelectedPadding_Left,
                            (int) mUnSelectedPadding_Top,
                            (int) mUnSelectedPadding_Right,
                            (int) mUnSelectedPadding_Bottom);
                    addView(indicator);
                    mIndicators.add(indicator);
                }
            } else if (count < mItemCount) {
                for (int i = 0; i < mItemCount - count; i++) {
                    removeView(mIndicators.get(0));
                    mIndicators.remove(0);
                }
            }
            mItemCount = count;
            mPager.setCurrentItem(mItemCount * 20 + mPager.getCurrentItem());
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            redraw();
        }
    };

    public IndicatorVisibility getIndicatorVisibility() {
        return mVisibility;
    }

    public int getSelectedIndicatorResId() {
        return mUserSetSelectedIndicatorResId;
    }

    public int getUnSelectedIndicatorResId() {
        return mUserSetUnSelectedIndicatorResId;
    }
}

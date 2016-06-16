package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;
import com.design.code.view.slider.anim.AnimBase;
import com.design.code.view.slider.base.PagerBase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is all transformers father.
 *
 * TransformerBase implement {@link PagerBase.PageTransformer} which is just same as {@link android.support.v4.view.ViewPager.PageTransformer}.
 *
 * After you call setPageTransformer(), transformPage() will be called by {@link PagerBase} when your slider are animating.
 *
 * In onPreTransform() function, that will make {@link AnimBase}work.
 *
 * if you want to make an acceptable transformer, please do not forget to extend from this class.
 */
public abstract class TransformerBase implements PagerBase.PageTransformer {

    private AnimBase mCustomAnimationInterface;

    /**
     * Called each {@link #transformPage(View, float)}.
     */
    protected abstract void onTransform(View view, float position);

    private HashMap<View,ArrayList<Float>> h = new HashMap<View, ArrayList<Float>>();

    @Override
    public void transformPage(View view, float position) {
        onPreTransform(view, position);
        onTransform(view, position);
        onPostTransform(view, position);
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the visibility of the fragment to {@link View#GONE}.
     * Returning false will force the fragment to {@link View#VISIBLE}.
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     */
    protected boolean isPagingEnabled() {
        return false;
    }

    /**
     * Called each {@link #transformPage(View, float)} before {{@link #onTransform(View, float)} is called.
     */
    protected void onPreTransform(View view, float position) {
        final float width = view.getWidth();

        AnimHelper.setRotationX(view, 0);
        AnimHelper.setRotationY(view, 0);
        AnimHelper.setRotation(view, 0);
        AnimHelper.setScaleX(view, 1);
        AnimHelper.setScaleY(view, 1);
        AnimHelper.setPivotX(view, 0);
        AnimHelper.setPivotY(view, 0);
        AnimHelper.setTranslationY(view, 0);
        AnimHelper.setTranslationX(view, isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            AnimHelper.setAlpha(view, position <= -1f || position >= 1f ? 0f : 1f);
        } else {
            AnimHelper.setAlpha(view, 1f);
        }
        if (mCustomAnimationInterface != null) {
            if (!h.containsKey(view) || h.get(view).size() == 1) {
                if (position > -1 && position < 1) {
                    if (h.get(view) == null) {
                        h.put(view, new ArrayList<Float>());
                    }
                    h.get(view).add(position);
                    if (h.get(view).size() == 2) {
                        float zero = h.get(view).get(0);
                        float cha = h.get(view).get(1) - h.get(view).get(0);
                        if (zero > 0) {
                            if (cha > -1 && cha < 0) {
                                // in
                                mCustomAnimationInterface.onPrepareNextItemShowInScreen(view);
                            } else {
                                // out
                                mCustomAnimationInterface.onPrepareCurrentItemLeaveScreen(view);
                            }
                        } else {
                            if (cha > -1 && cha < 0) {
                                // out
                                mCustomAnimationInterface.onPrepareCurrentItemLeaveScreen(view);
                            } else {
                                // in
                                mCustomAnimationInterface.onPrepareNextItemShowInScreen(view);
                            }
                        }
                    }
                }
            }
        }
    }
    boolean isApp,isDis;

    /**
     * Called each {@link #transformPage(View, float)} call after {@link #onTransform(View, float)} is finished.
     */
    protected void onPostTransform(View view, float position) {
        if (mCustomAnimationInterface != null) {
            if (position == -1 || position == 1) {
                mCustomAnimationInterface.onCurrentItemDisappear(view);
                isApp = true;
            } else if (position == 0) {
                mCustomAnimationInterface.onNextItemAppear(view);
                isDis = true;
            }
            if (isApp && isDis) {
                h.clear();
                isApp = false;
                isDis = false;
            }
        }
    }

    public void setCustomAnimationInterface(AnimBase animationInterface) {
        mCustomAnimationInterface = animationInterface;
    }

}
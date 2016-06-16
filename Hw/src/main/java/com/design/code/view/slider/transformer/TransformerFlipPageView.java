package com.design.code.view.slider.transformer;

import android.os.Build;
import android.view.View;

import com.design.code.util.anim.AnimHelper;
import com.design.code.view.slider.base.PagerBase;

public class TransformerFlipPageView extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        float percentage = 1 - Math.abs(position);
        if (Build.VERSION.SDK_INT >= 13) {
            view.setCameraDistance(12000);
        }
        setVisibility(view, position);
        setTranslation(view);
        setSize(view, position, percentage);
        setRotation(view, position, percentage);
    }

    private void setVisibility(View page, float position) {
        if (position < 0.5 && position > -0.5) {
            page.setVisibility(View.VISIBLE);
        } else {
            page.setVisibility(View.INVISIBLE);
        }
    }

    private void setTranslation(View view) {
        PagerBase viewPager = (PagerBase) view.getParent();
        int scroll = viewPager.getScrollX() - view.getLeft();
        AnimHelper.setTranslationX(view, scroll);
    }

    private void setSize(View view, float position, float percentage) {
        AnimHelper.setScaleX(view, (position != 0 && position != 1) ? percentage : 1);
        AnimHelper.setScaleY(view, (position != 0 && position != 1) ? percentage : 1);
    }

    private void setRotation(View view, float position, float percentage) {
        if (position > 0) {
            AnimHelper.setRotationY(view, -180 * (percentage + 1));
        } else {
            AnimHelper.setRotationY(view, 180 * (percentage + 1));
        }
    }
}
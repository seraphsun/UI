package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;

public class TransformerDepthPage extends TransformerBase {

    private static final float MIN_SCALE = 0.75f;

    @Override
    protected void onTransform(View view, float position) {
        if (position <= 0f) {
            AnimHelper.setTranslationX(view, 0f);
            AnimHelper.setScaleX(view, 1f);
            AnimHelper.setScaleY(view, 1f);
        } else if (position <= 1f) {
            final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            AnimHelper.setAlpha(view, 1 - position);
            AnimHelper.setPivotY(view, 0.5f * view.getHeight());
            AnimHelper.setTranslationX(view, view.getWidth() * -position);
            AnimHelper.setScaleX(view, scaleFactor);
            AnimHelper.setScaleY(view, scaleFactor);
        }
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}

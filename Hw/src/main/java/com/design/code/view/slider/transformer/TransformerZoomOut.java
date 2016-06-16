package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;


public class TransformerZoomOut extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        final float scale = 1f + Math.abs(position);
        AnimHelper.setScaleX(view, scale);
        AnimHelper.setScaleY(view, scale);
        AnimHelper.setPivotX(view, view.getWidth() * 0.5f);
        AnimHelper.setPivotY(view, view.getWidth() * 0.5f);
        AnimHelper.setAlpha(view, position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
        if (position < -0.9) {
            //-0.9 to prevent a small bug
            AnimHelper.setTranslationX(view, view.getWidth() * position);
        }
    }

}
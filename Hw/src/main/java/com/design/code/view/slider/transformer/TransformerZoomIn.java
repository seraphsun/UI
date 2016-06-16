package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;


public class TransformerZoomIn extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        final float scale = position < 0 ? position + 1f : Math.abs(1f - position);
        AnimHelper.setScaleX(view, scale);
        AnimHelper.setScaleY(view, scale);
        AnimHelper.setPivotX(view, view.getWidth() * 0.5f);
        AnimHelper.setPivotY(view, view.getHeight() * 0.5f);
        AnimHelper.setAlpha(view, position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
    }

}

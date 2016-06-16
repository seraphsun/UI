package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;

public class TransformerToForeground extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        final float height = view.getHeight();
        final float width = view.getWidth();
        final float scale = min(position < 0 ? 1f : Math.abs(1f - position), 0.5f);

        AnimHelper.setScaleX(view, scale);
        AnimHelper.setScaleY(view, scale);
        AnimHelper.setPivotX(view, width * 0.5f);
        AnimHelper.setPivotY(view, height * 0.5f);
        AnimHelper.setTranslationX(view, position < 0 ? width * position : -width * position * 0.25f);
    }

    private static float min(float val, float min) {
        return val < min ? min : val;
    }

}

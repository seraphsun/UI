package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;

public class TransformerFold extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        AnimHelper.setPivotX(view, position < 0 ? 0 : view.getWidth());
        AnimHelper.setScaleX(view, position < 0 ? 1f + position : 1f - position);
    }

}
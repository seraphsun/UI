package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;


public class TransformerStack extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        AnimHelper.setTranslationX(view, position < 0 ? 0f : -view.getWidth() * position);
    }

}

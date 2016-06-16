package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;


public class TransformerRotateUp extends TransformerBase {

    private static final float ROT_MOD = -15f;

    @Override
    protected void onTransform(View view, float position) {
        final float width = view.getWidth();
        final float rotation = ROT_MOD * position;

        AnimHelper.setPivotX(view, width * 0.5f);
        AnimHelper.setPivotY(view, 0f);
        AnimHelper.setTranslationX(view, 0f);
        AnimHelper.setRotation(view, rotation);
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}

package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;


public class TransformerRotateDown extends TransformerBase {

    private static final float ROT_MOD = -15f;

    @Override
    protected void onTransform(View view, float position) {
        final float width = view.getWidth();
        final float height = view.getHeight();
        final float rotation = ROT_MOD * position * -1.25f;

        AnimHelper.setPivotX(view, width * 0.5f);
        AnimHelper.setPivotY(view, height);
        AnimHelper.setRotation(view, rotation);
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}

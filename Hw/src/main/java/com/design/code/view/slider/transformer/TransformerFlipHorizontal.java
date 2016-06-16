package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;

public class TransformerFlipHorizontal extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        final float rotation = 180f * position;
        AnimHelper.setAlpha(view, rotation > 90f || rotation < -90f ? 0 : 1);
        AnimHelper.setPivotY(view, view.getHeight() * 0.5f);
        AnimHelper.setPivotX(view, view.getWidth() * 0.5f);
        AnimHelper.setRotationY(view, rotation);
    }

}

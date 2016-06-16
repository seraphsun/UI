package com.design.code.view.slider.transformer;

import android.view.View;

import com.design.code.util.anim.AnimHelper;

public class TransformerCubeIn extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
        // Rotate the fragment on the left or right edge
        AnimHelper.setPivotX(view, position > 0 ? 0 : view.getWidth());
        AnimHelper.setPivotY(view, 0);
        AnimHelper.setRotation(view, -90f * position);
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}

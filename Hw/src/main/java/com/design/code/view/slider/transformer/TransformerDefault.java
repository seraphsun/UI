package com.design.code.view.slider.transformer;

import android.view.View;

public class TransformerDefault extends TransformerBase {

    @Override
    protected void onTransform(View view, float position) {
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}

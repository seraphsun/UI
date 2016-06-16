package com.design.code.base.transforms;

import android.view.View;

public class TransformerStack extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
    }

}

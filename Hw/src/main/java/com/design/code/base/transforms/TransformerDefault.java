package com.design.code.base.transforms;

import android.view.View;

public class TransformerDefault extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}

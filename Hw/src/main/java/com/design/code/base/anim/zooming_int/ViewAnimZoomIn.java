package com.design.code.base.anim.zooming_int;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;

public class ViewAnimZoomIn extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 0.45f, 1),
                ObjectAnimator.ofFloat(target, "scaleY", 0.45f, 1),
                ObjectAnimator.ofFloat(target, "alpha", 0, 1)
        );
    }
}

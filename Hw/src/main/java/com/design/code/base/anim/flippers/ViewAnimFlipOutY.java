package com.design.code.base.anim.flippers;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;


public class ViewAnimFlipOutY extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "rotationY", 0, 90),
                ObjectAnimator.ofFloat(target, "alpha", 1, 0)
        );
    }
}

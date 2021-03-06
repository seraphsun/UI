package com.design.code.base.anim.fading_out;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;

public class ViewAnimFadeOutLeft extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1, 0),
                ObjectAnimator.ofFloat(target, "translationX", 0, -target.getWidth() / 4)
        );
    }
}

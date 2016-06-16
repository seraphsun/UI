package com.design.code.base.anim.rotating_in;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;

public class ViewAnimRotateIn extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "rotation", -200, 0),
                ObjectAnimator.ofFloat(target, "alpha", 0, 1)
        );
    }
}

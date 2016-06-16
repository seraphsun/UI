package com.design.code.base.anim.attention;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;


public class ViewAnimFlash extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(ObjectAnimator.ofFloat(target, "alpha", 1, 0, 1, 0, 1));
    }
}

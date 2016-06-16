package com.design.code.base.anim.zooming_out;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.design.code.base.anim.BaseViewAnimator;


public class ViewAnimZoomOutRight extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        ViewGroup parent = (ViewGroup) target.getParent();
        int distance = parent.getWidth() - parent.getLeft();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1, 1, 0),
                ObjectAnimator.ofFloat(target, "scaleX", 1, 0.475f, 0.1f),
                ObjectAnimator.ofFloat(target, "scaleY", 1, 0.475f, 0.1f),
                ObjectAnimator.ofFloat(target, "translationX", 0, -42, distance)
        );
    }
}

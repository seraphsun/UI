package com.design.code.base.anim.specials;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;
import com.design.code.base.interp.Glider;
import com.design.code.base.interp.Skill;

public class ViewAnimHinge extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        float x = target.getPaddingLeft();
        float y = target.getPaddingTop();
        getAnimatorAgent().playTogether(
                Glider.glide(Skill.SineEaseInOut, 1300, ObjectAnimator.ofFloat(target, "rotation", 0, 80, 60, 80, 60, 60)),
                ObjectAnimator.ofFloat(target, "translationY", 0, 0, 0, 0, 0, 700),
                ObjectAnimator.ofFloat(target, "alpha", 1, 1, 1, 1, 1, 0),
                ObjectAnimator.ofFloat(target, "pivotX", x, x, x, x, x, x),
                ObjectAnimator.ofFloat(target, "pivotY", y, y, y, y, y, y)
        );

        setDuration(1300);
    }
}


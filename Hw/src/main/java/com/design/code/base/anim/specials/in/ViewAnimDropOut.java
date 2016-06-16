package com.design.code.base.anim.specials.in;

import android.animation.ObjectAnimator;
import android.view.View;

import com.design.code.base.anim.BaseViewAnimator;
import com.design.code.base.interp.Glider;
import com.design.code.base.interp.Skill;

public class ViewAnimDropOut extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        int distance = target.getTop() + target.getHeight();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                Glider.glide(Skill.BounceEaseOut, getDuration(), ObjectAnimator.ofFloat(target, "translationY", -distance, 0))
        );

    }
}

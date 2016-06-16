package com.design.code.base.interp;


import android.animation.ValueAnimator;

public class Glider {

//    public static ValueAnimator glide(Skill skill, float duration, ValueAnimator animator) {
//        return Glider.glide(skill, duration, animator, null);
//    }

    public static ValueAnimator glide(Skill skill, float duration, ValueAnimator animator, BaseInterpolator.EasingListener... listeners) {
        BaseInterpolator t = skill.getMethod(duration);

        if (listeners != null)
            t.addEasingListeners(listeners);

        animator.setEvaluator(t);
        return animator;
    }

//    public static PropertyValuesHolder glide(Skill skill, float duration, PropertyValuesHolder propertyValuesHolder) {
//        propertyValuesHolder.setEvaluator(skill.getMethod(duration));
//        return propertyValuesHolder;
//    }
}

package com.design.code.base.anim;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.design.code.util.anim.AnimHelper;

public abstract class BaseViewAnimator {

    public static final long DURATION = 1000;

    private AnimatorSet mAnimatorSet;
    private long mDuration = DURATION;

    {
        mAnimatorSet = new AnimatorSet();
    }


    protected abstract void prepare(View target);

    public BaseViewAnimator setTarget(View target) {
        reset(target);
        prepare(target);
        return this;
    }

    public void animate() {
        start();
    }

    /**
     * reset the view to default status
     */
    public void reset(View target) {
        AnimHelper.setAlpha(target, 1);
        AnimHelper.setScaleX(target, 1);
        AnimHelper.setScaleY(target, 1);
        AnimHelper.setTranslationX(target, 0);
        AnimHelper.setTranslationY(target, 0);
        AnimHelper.setRotation(target, 0);
        AnimHelper.setRotationY(target, 0);
        AnimHelper.setRotationX(target, 0);
        AnimHelper.setPivotX(target, target.getMeasuredWidth() / 2.0f);
        AnimHelper.setPivotY(target, target.getMeasuredHeight() / 2.0f);
    }

    /**
     * start to animate
     */
    public void start() {
        mAnimatorSet.setDuration(mDuration);
        mAnimatorSet.start();
    }

    public BaseViewAnimator setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public BaseViewAnimator setStartDelay(long delay) {
        getAnimatorAgent().setStartDelay(delay);
        return this;
    }

    public long getStartDelay() {
        return mAnimatorSet.getStartDelay();
    }

    public BaseViewAnimator addAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.addListener(l);
        return this;
    }

    public void cancel(){
        mAnimatorSet.cancel();
    }

    public boolean isRunning(){
        return mAnimatorSet.isRunning();
    }

    public boolean isStarted(){
        return mAnimatorSet.isStarted();
    }

    public void removeAnimatorListener(Animator.AnimatorListener l) {
        mAnimatorSet.removeListener(l);
    }

    public void removeAllListener() {
        mAnimatorSet.removeAllListeners();
    }

    public BaseViewAnimator setInterpolator(Interpolator interpolator) {
        mAnimatorSet.setInterpolator(interpolator);
        return this;
    }

    public long getDuration() {
        return mDuration;
    }

    public AnimatorSet getAnimatorAgent() {
        return mAnimatorSet;
    }

}

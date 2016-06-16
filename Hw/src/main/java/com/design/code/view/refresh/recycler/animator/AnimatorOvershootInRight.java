package com.design.code.view.refresh.recycler.animator;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.OvershootInterpolator;

public class AnimatorOvershootInRight extends AnimatorBase {

    private final float mTension;

    public AnimatorOvershootInRight() {
        mTension = 2.0f;
    }

    public AnimatorOvershootInRight(float mTension) {
        this.mTension = mTension;
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(holder.itemView.getRootView().getWidth())
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, holder.itemView.getRootView().getWidth());
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .setDuration(getAddDuration())
                .setInterpolator(new OvershootInterpolator(mTension))
                .setListener(new DefaultAddVpaListener(holder))
                .start();
    }
}

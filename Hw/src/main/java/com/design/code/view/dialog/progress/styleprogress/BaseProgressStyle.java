package com.design.code.view.dialog.progress.styleprogress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.nineoldandroids.animation.Animator;

import java.util.List;

public abstract class BaseProgressStyle {

    private View mTarget;

    private List<Animator> mAnimators;

    public enum AnimStatus {
        START, END, CANCEL
    }

    public void setTarget(View target) {
        this.mTarget = target;
    }

    public View getTarget() {
        return mTarget;
    }


    public int getWidth() {
        return mTarget.getWidth();
    }

    public int getHeight() {
        return mTarget.getHeight();
    }

    public void postInvalidate() {
        mTarget.postInvalidate();
    }

    /**
     * draw indicator
     */
    public abstract void draw(Canvas canvas, Paint paint);

    /**
     * create animation or animations
     */
    public abstract List<Animator> createAnimation();

    public void initAnimation() {
        mAnimators = createAnimation();
    }

    /**
     * make animation to start or end when target view was be Visible or Gone or Invisible. make animation to cancel when target view be onDetachedFromWindow.
     */
    public void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
            }
        }
    }
}

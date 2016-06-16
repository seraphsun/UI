package com.design.code.view.dialog.progress.styleprogress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;


public class ProgressStyle_18 extends BaseProgressStyle {

    float scale = 1;
    int alpha = 255;

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float circleSpacing = 4;
        paint.setAlpha(alpha);
        canvas.scale(scale, scale, getWidth() / 2, getHeight() / 2);
        paint.setAlpha(alpha);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - circleSpacing, paint);
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList<>();
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(0, 1);
        scaleAnim.setInterpolator(new LinearInterpolator());
        scaleAnim.setDuration(1000);
        scaleAnim.setRepeatCount(-1);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        scaleAnim.start();

        ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 0);
        alphaAnim.setInterpolator(new LinearInterpolator());
        alphaAnim.setDuration(1000);
        alphaAnim.setRepeatCount(-1);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        alphaAnim.start();
        animators.add(scaleAnim);
        animators.add(alphaAnim);
        return animators;
    }


}

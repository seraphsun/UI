package com.design.code.view.theme;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Design 2016/3/23.
 */
public class ExplosionView extends View {

    private List<ExplosionAnimator> mExplosions = new ArrayList<>();
    private int[] mExpandInset = new int[2];

    public ExplosionView(Context context) {
        super(context);
        init();
    }

    public ExplosionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExplosionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Arrays.fill(mExpandInset, ExplosionUtils.dp2Px(32));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator explosion : mExplosions) {
            explosion.draw(canvas);
        }
    }

    public void expandExplosionBound(int dx, int dy) {
        mExpandInset[0] = dx;
        mExpandInset[1] = dy;
    }

    public void explode(final View view) {
        Rect r = new Rect();
        view.getGlobalVisibleRect(r);
        int[] location = new int[2];
        getLocationOnScreen(location);
        r.offset(-location[0], -location[1]);
        r.inset(-mExpandInset[0], -mExpandInset[1]);
        int startDelay = 100;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            Random random = new Random();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((random.nextFloat() - 0.5f) * view.getWidth() * 0.05f);
                view.setTranslationY((random.nextFloat() - 0.5f) * view.getHeight() * 0.05f);

            }
        });
        animator.start();
        view.animate().setDuration(150).setStartDelay(startDelay).scaleX(0f).scaleY(0f).alpha(0f).start();
        explode(ExplosionUtils.createBitmapFromView(view), r, startDelay, ExplosionAnimator.DEFAULT_DURATION);
    }

    private void explode(Bitmap bitmap, Rect bound, long startDelay, long duration) {
        final ExplosionAnimator explosion = new ExplosionAnimator(this, bitmap, bound);
        explosion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExplosions.remove(animation);
            }
        });
        explosion.setStartDelay(startDelay);
        explosion.setDuration(duration);
        mExplosions.add(explosion);
        explosion.start();
    }

    public void clear() {
        mExplosions.clear();
        invalidate();
    }

    public static ExplosionView attach2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ExplosionView explosionView = new ExplosionView(activity);
        rootView.addView(explosionView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return explosionView;
    }

    /**
     * 动画效果
     */
    public static class ExplosionAnimator extends ValueAnimator {

        static long DEFAULT_DURATION = 0x400;
        private static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator(0.6f);
        private static final float END_VALUE = 1.4f;
        private static final float X = ExplosionUtils.dp2Px(5);
        private static final float Y = ExplosionUtils.dp2Px(20);
        private static final float V = ExplosionUtils.dp2Px(2);
        private static final float W = ExplosionUtils.dp2Px(1);
        private Paint mPaint;
        private Particle[] mParticles;
        private Rect mBound;
        private View mContainer;

        public ExplosionAnimator(View container, Bitmap bitmap, Rect bound) {
            mPaint = new Paint();
            mBound = new Rect(bound);
            int partLen = 15;
            mParticles = new Particle[partLen * partLen];
            Random random = new Random(System.currentTimeMillis());
            int w = bitmap.getWidth() / (partLen + 2);
            int h = bitmap.getHeight() / (partLen + 2);
            for (int i = 0; i < partLen; i++) {
                for (int j = 0; j < partLen; j++) {
                    mParticles[(i * partLen) + j] = generateParticle(bitmap.getPixel((j + 1) * w, (i + 1) * h), random);
                }
            }
            mContainer = container;
            setFloatValues(0f, END_VALUE);
            setInterpolator(DEFAULT_INTERPOLATOR);
            setDuration(DEFAULT_DURATION);
        }

        private Particle generateParticle(int color, Random random) {
            Particle particle = new Particle();
            particle.color = color;
            particle.radius = V;
            if (random.nextFloat() < 0.2f) {
                particle.baseRadius = V + ((X - V) * random.nextFloat());
            } else {
                particle.baseRadius = W + ((V - W) * random.nextFloat());
            }
            float nextFloat = random.nextFloat();
            particle.top = mBound.height() * ((0.18f * random.nextFloat()) + 0.2f);
            particle.top = nextFloat < 0.2f ? particle.top : particle.top + ((particle.top * 0.2f) * random.nextFloat());
            particle.bottom = (mBound.height() * (random.nextFloat() - 0.5f)) * 1.8f;
            float f = nextFloat < 0.2f ? particle.bottom : nextFloat < 0.8f ? particle.bottom * 0.6f : particle.bottom * 0.3f;
            particle.bottom = f;
            particle.mag = 4.0f * particle.top / particle.bottom;
            particle.neg = (-particle.mag) / particle.bottom;
            f = mBound.centerX() + (Y * (random.nextFloat() - 0.5f));
            particle.baseCx = f;
            particle.cx = f;
            f = mBound.centerY() + (Y * (random.nextFloat() - 0.5f));
            particle.baseCy = f;
            particle.cy = f;
            particle.life = END_VALUE / 10 * random.nextFloat();
            particle.overflow = 0.4f * random.nextFloat();
            particle.alpha = 1f;
            return particle;
        }

        public boolean draw(Canvas canvas) {
            if (!isStarted()) {
                return false;
            }
            for (Particle particle : mParticles) {
                particle.advance((float) getAnimatedValue());
                if (particle.alpha > 0f) {
                    mPaint.setColor(particle.color);
                    mPaint.setAlpha((int) (Color.alpha(particle.color) * particle.alpha));
                    canvas.drawCircle(particle.cx, particle.cy, particle.radius, mPaint);
                }
            }
            mContainer.invalidate();
            return true;
        }

        @Override
        public void start() {
            super.start();
            mContainer.invalidate(mBound);
        }

        private class Particle {

            float alpha;
            int color;
            float cx;
            float cy;
            float radius;
            float baseCx;
            float baseCy;
            float baseRadius;
            float top;
            float bottom;
            float mag;
            float neg;
            float life;
            float overflow;

            public void advance(float factor) {
                float f = 0f;
                float normalization = factor / END_VALUE;
                if (normalization < life || normalization > 1f - overflow) {
                    alpha = 0f;
                    return;
                }
                normalization = (normalization - life) / (1f - life - overflow);
                float f2 = normalization * END_VALUE;
                if (normalization >= 0.7f) {
                    f = (normalization - 0.7f) / 0.3f;
                }
                alpha = 1f - f;
                f = bottom * f2;
                cx = baseCx + f;
                cy = (float) (baseCy - this.neg * Math.pow(f, 2.0)) - f * mag;
                radius = V + (baseRadius - V) * f2;
            }
        }
    }

    /**
     * 位图处理
     */
    public static class ExplosionUtils {

        private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
        private static final Canvas sCanvas = new Canvas();

        public static int dp2Px(int dp) {
            return Math.round(dp * DENSITY);
        }

        public static Bitmap createBitmapFromView(View view) {
            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();
                if (drawable != null && drawable instanceof BitmapDrawable) {
                    return ((BitmapDrawable) drawable).getBitmap();
                }
            }
            view.clearFocus();
            Bitmap bitmap = createBitmapSafely(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888, 1);
            if (bitmap != null) {
                synchronized (sCanvas) {
                    Canvas canvas = sCanvas;
                    canvas.setBitmap(bitmap);
                    view.draw(canvas);
                    canvas.setBitmap(null);
                }
            }
            return bitmap;
        }

        public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
            try {
                return Bitmap.createBitmap(width, height, config);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if (retryCount > 0) {
                    System.gc();
                    return createBitmapSafely(width, height, config, retryCount - 1);
                }
                return null;
            }
        }
    }
}

package com.design.code.view.dialog.alert;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.design.code.R;
import com.design.code.util.UtilDensity;
import com.design.code.util.anim.AnimHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by Ignacey 2016/3/3.
 */
public class LoadingToast extends View {

    private Context context;
    private String mText = "";

    private Paint textPaint = new Paint();
    private Paint backPaint = new Paint();
    private Paint iconBackPaint = new Paint();
    private Paint loaderPaint = new Paint();
    private Paint successPaint = new Paint();
    private Paint errorPaint = new Paint();

    private Rect iconBounds;
    private Rect mTextBounds = new Rect();
    private RectF spinnerRect = new RectF();

    private int MAX_TEXT_WIDTH = 100; // in DP
    private int BASE_TEXT_SIZE = 20;
    private int IMAGE_WIDTH = 40;
    private int TOAST_HEIGHT = 48;
    private int MARQUE_STEP = 1;
    private float WIDTH_SCALE = 0f;

    private long prevUpdate = 0;

    private Drawable complete_icon;
    private Drawable failed_icon;
    private ValueAnimator va, cmp;

    private boolean success = true;
    private boolean outOfBounds = false;

    private Path toastPath = new Path();
    private AccelerateDecelerateInterpolator easeInterpol = new AccelerateDecelerateInterpolator();

    public LoadingToast(Context context) {
        super(context);
        this.context = context;

        initPaint();
    }

    private void initPaint() {
        textPaint.setTextSize(15);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);

        backPaint.setColor(Color.WHITE);
        backPaint.setAntiAlias(true);

        iconBackPaint.setColor(Color.BLUE);
        iconBackPaint.setAntiAlias(true);

        loaderPaint.setStrokeWidth(UtilDensity.dp2px(context, 4));
        loaderPaint.setAntiAlias(true);
        loaderPaint.setColor(fetchPrimaryColor());
        loaderPaint.setStyle(Paint.Style.STROKE);

        successPaint.setColor(getResources().getColor(R.color.color_3ccd88));
        errorPaint.setColor(getResources().getColor(R.color.color_f44336));
        successPaint.setAntiAlias(true);
        errorPaint.setAntiAlias(true);

        MAX_TEXT_WIDTH = UtilDensity.dp2px(context, MAX_TEXT_WIDTH);
        BASE_TEXT_SIZE = UtilDensity.dp2px(context, BASE_TEXT_SIZE);
        IMAGE_WIDTH = UtilDensity.dp2px(context, IMAGE_WIDTH);
        TOAST_HEIGHT = UtilDensity.dp2px(context, TOAST_HEIGHT);
        MARQUE_STEP = UtilDensity.dp2px(context, MARQUE_STEP);

        int padding = (TOAST_HEIGHT - IMAGE_WIDTH) / 2;
        iconBounds = new Rect(TOAST_HEIGHT + MAX_TEXT_WIDTH - padding, padding, TOAST_HEIGHT + MAX_TEXT_WIDTH - padding + IMAGE_WIDTH, IMAGE_WIDTH + padding);
        complete_icon = getResources().getDrawable(R.mipmap.loading_toast_check);
        complete_icon.setBounds(iconBounds);
        failed_icon = getResources().getDrawable(R.mipmap.loading_toast_error);
        failed_icon.setBounds(iconBounds);

        va = ValueAnimator.ofFloat(0, 1);
        va.setDuration(6000);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                postInvalidate();
            }
        });
        va.setRepeatMode(ValueAnimator.INFINITE);
        va.setRepeatCount(9999999);
        va.setInterpolator(new LinearInterpolator());
        va.start();

        calculateBounds();
    }

    private int fetchPrimaryColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();

            TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorAccent});
            int color = a.getColor(0, 0);

            a.recycle();

            return color;
        }
        return Color.rgb(155, 155, 155);
    }

    private void calculateBounds() {
        outOfBounds = false;
        prevUpdate = 0;

        textPaint.setTextSize(BASE_TEXT_SIZE);
        textPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
        if (mTextBounds.width() > MAX_TEXT_WIDTH) {
            int textSize = BASE_TEXT_SIZE;
            while (textSize > UtilDensity.dp2px(context, 13) && mTextBounds.width() > MAX_TEXT_WIDTH) {
                textSize--;
                textPaint.setTextSize(textSize);
                textPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
            }
            if (mTextBounds.width() > MAX_TEXT_WIDTH) {
                outOfBounds = true;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = IMAGE_WIDTH + MAX_TEXT_WIDTH + TOAST_HEIGHT;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = TOAST_HEIGHT;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        float ws = Math.max(1f - WIDTH_SCALE, 0f);

        if (mText.length() == 0) ws = 0;

        float translateLoad = (1f - ws) * (IMAGE_WIDTH + MAX_TEXT_WIDTH);
        float leftMargin = translateLoad / 2;
        float textOpactity = Math.max(0, ws * 10f - 9f);
        textPaint.setAlpha((int) (textOpactity * 255));
        spinnerRect.set(iconBounds.left + UtilDensity.dp2px(context, 4) - translateLoad / 2, iconBounds.top + UtilDensity.dp2px(context, 4), iconBounds.right - UtilDensity.dp2px(context, 4) - translateLoad / 2, iconBounds.bottom - UtilDensity.dp2px(context, 4));

        int circleOffset = (int) (TOAST_HEIGHT * 2 * (Math.sqrt(2) - 1) / 3);
        int th = TOAST_HEIGHT;
        int pd = (TOAST_HEIGHT - IMAGE_WIDTH) / 2;
        int iconOffset = (int) (IMAGE_WIDTH * 2 * (Math.sqrt(2) - 1) / 3);
        int iw = IMAGE_WIDTH;

        toastPath.reset();
        toastPath.moveTo(leftMargin + th / 2, 0);
        toastPath.rLineTo(ws * (IMAGE_WIDTH + MAX_TEXT_WIDTH), 0);
        toastPath.rCubicTo(circleOffset, 0, th / 2, th / 2 - circleOffset, th / 2, th / 2);

        toastPath.rLineTo(-pd, 0);
        toastPath.rCubicTo(0, -iconOffset, -iw / 2 + iconOffset, -iw / 2, -iw / 2, -iw / 2);
        toastPath.rCubicTo(-iconOffset, 0, -iw / 2, iw / 2 - iconOffset, -iw / 2, iw / 2);
        toastPath.rCubicTo(0, iconOffset, iw / 2 - iconOffset, iw / 2, iw / 2, iw / 2);
        toastPath.rCubicTo(iconOffset, 0, iw / 2, -iw / 2 + iconOffset, iw / 2, -iw / 2);
        toastPath.rLineTo(pd, 0);

        toastPath.rCubicTo(0, circleOffset, circleOffset - th / 2, th / 2, -th / 2, th / 2);
        toastPath.rLineTo(ws * (-IMAGE_WIDTH - MAX_TEXT_WIDTH), 0);
        toastPath.rCubicTo(-circleOffset, 0, -th / 2, -th / 2 + circleOffset, -th / 2, -th / 2);
        toastPath.rCubicTo(0, -circleOffset, -circleOffset + th / 2, -th / 2, th / 2, -th / 2);

        c.drawCircle(spinnerRect.centerX(), spinnerRect.centerY(), iconBounds.height() / 1.9f, backPaint);
        c.drawPath(toastPath, backPaint);

        float prog = va.getAnimatedFraction() * 6.0f;
        float progrot = prog % 2.0f;
        float proglength = easeInterpol.getInterpolation(prog % 3f / 3f) * 3f - .75f;
        if (proglength > .75f) {
            proglength = .75f - (prog % 3f - 1.5f);
            progrot += (prog % 3f - 1.5f) / 1.5f * 2f;
        }

        toastPath.reset();

        if (mText.length() == 0) {
            ws = Math.max(1f - WIDTH_SCALE, 0f);
        }

        toastPath.arcTo(spinnerRect, 180 * progrot, Math.min((200 / .75f) * proglength + 1 + 560 * (1f - ws), 359.9999f));
        loaderPaint.setAlpha((int) (255 * ws));
        c.drawPath(toastPath, loaderPaint);

        if (WIDTH_SCALE > 1f) {
            Drawable icon = (success) ? complete_icon : failed_icon;
            float circleProg = WIDTH_SCALE - 1f;
            textPaint.setAlpha((int) (128 * circleProg + 127));
            int paddingIcon = (int) ((1f - (.25f + (.75f * circleProg))) * TOAST_HEIGHT / 2);
            int completeOff = (int) ((1f - circleProg) * TOAST_HEIGHT / 8);
            icon.setBounds((int) spinnerRect.left + paddingIcon, (int) spinnerRect.top + paddingIcon + completeOff, (int) spinnerRect.right - paddingIcon, (int) spinnerRect.bottom - paddingIcon + completeOff);
            c.drawCircle(leftMargin + TOAST_HEIGHT / 2, (1f - circleProg) * TOAST_HEIGHT / 8 + TOAST_HEIGHT / 2, (.25f + (.75f * circleProg)) * TOAST_HEIGHT / 2, (success) ? successPaint : errorPaint);
            c.save();
            c.rotate(90 * (1f - circleProg), leftMargin + TOAST_HEIGHT / 2, TOAST_HEIGHT / 2);
            icon.draw(c);
            c.restore();

            prevUpdate = 0;
            return;
        }

        int yPos = (int) ((th / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        if (outOfBounds) {
            float shift = 0;
            if (prevUpdate == 0) {
                prevUpdate = System.currentTimeMillis();
            } else {
                shift = ((float) (System.currentTimeMillis() - prevUpdate) / 16f) * MARQUE_STEP;

                if (shift - MAX_TEXT_WIDTH > mTextBounds.width()) {
                    prevUpdate = 0;
                }
            }
            c.clipRect(th / 2, 0, th / 2 + MAX_TEXT_WIDTH, TOAST_HEIGHT);
            c.drawText(mText, th / 2 - shift + MAX_TEXT_WIDTH, yPos, textPaint);
        } else {
            c.drawText(mText, 0, mText.length(), th / 2 + (MAX_TEXT_WIDTH - mTextBounds.width()) / 2, yPos, textPaint);
        }
    }

    public void show() {
        WIDTH_SCALE = 0f;
        if (cmp != null) cmp.removeAllUpdateListeners();
    }

    public void success() {
        success = true;
        done();
    }

    public void error() {
        success = false;
        done();
    }

    private void done() {
        cmp = ValueAnimator.ofFloat(0, 1);
        cmp.setDuration(600);
        cmp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                WIDTH_SCALE = 2f * (valueAnimator.getAnimatedFraction());
                postInvalidate();
            }
        });
        cmp.setInterpolator(new DecelerateInterpolator());
        cmp.start();
    }

    public void setText(String text) {
        mText = text;
        calculateBounds();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    public void setProgressColor(int color) {
        loaderPaint.setColor(color);
    }

    public void setBackgroundColor(int color) {
        backPaint.setColor(color);
        iconBackPaint.setColor(color);
    }

    /**
     * Toast工具
     */
    public static class LoadToast {
        private LoadingToast mView;
        private ViewGroup mParentView;
        private int mTranslationY = 0;
        private boolean mShowCalled, mToastCanceled, mInflated, mVisible;

        public LoadToast(Context context) {
            mView = new LoadingToast(context);
            mParentView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            AnimHelper.setAlpha(mView, 0);
            mParentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimHelper.setTranslationX(mView, (mParentView.getWidth() - mView.getWidth()) / 2);
                    AnimHelper.setTranslationY(mView, -mView.getHeight() + mTranslationY);
                    mInflated = true;
                    if (!mToastCanceled && mShowCalled) show();
                }
            }, 1);

            mParentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    checkZPosition();
                }
            });
        }

        private void checkZPosition() {
            // If the toast isn't visible, no point in updating all the views
            if (!mVisible) return;

            int pos = mParentView.indexOfChild(mView);
            int count = mParentView.getChildCount();
            if (pos != count - 1) {
                ((ViewGroup) mView.getParent()).removeView(mView);
                mParentView.requestLayout();
                mParentView.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        public LoadToast show() {
            if (!mInflated) {
                mShowCalled = true;
                return this;
            }
            mView.show();
            AnimHelper.setTranslationX(mView, (mParentView.getWidth() - mView.getWidth()) / 2);
            AnimHelper.setAlpha(mView, 0f);
            AnimHelper.setTranslationY(mView, -mView.getHeight() + mTranslationY);
            ViewPropertyAnimator.animate(mView).alpha(1f).translationY(25 + mTranslationY).setInterpolator(new DecelerateInterpolator()).setDuration(300).setStartDelay(0).start();

            mVisible = true;
            checkZPosition();

            return this;
        }

        public void success() {
            if (!mInflated) {
                mToastCanceled = true;
                return;
            }
            mView.success();
            slideUp();
        }

        public void error() {
            if (!mInflated) {
                mToastCanceled = true;
                return;
            }
            mView.error();
            slideUp();
        }

        private void slideUp() {
            ViewPropertyAnimator
                    .animate(mView)
                    .setStartDelay(1000)
                    .alpha(0f)
                    .translationY(-mView.getHeight() + mTranslationY)
                    .setInterpolator(new AccelerateInterpolator())
                    .setDuration(300)
                    .start();

            mVisible = false;
        }

        public LoadToast setText(String message) {
            mView.setText(message);
            return this;
        }

        public LoadToast setTextColor(int color) {
            mView.setTextColor(color);
            return this;
        }

        public LoadToast setProgressColor(int color) {
            mView.setProgressColor(color);
            return this;
        }

        public LoadToast setBackgroundColor(int color) {
            mView.setBackgroundColor(color);
            return this;
        }

        public LoadToast setTranslationY(int pixels) {
            mTranslationY = pixels;
            return this;
        }
    }
}

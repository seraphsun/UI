package com.design.code.view.dialog.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.design.code.R;
import com.design.code.util.UtilDensity;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_2;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_3;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_4;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_5;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_6;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_7;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_8;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_9;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_10;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_18;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_20;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_18_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_20_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_21;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_12;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_19_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_19;
import com.design.code.view.dialog.progress.styleprogress.BaseProgressStyle;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_11;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_22;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_17;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_22_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_22_2;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_21_1;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_16;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_13;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_14;
import com.design.code.view.dialog.progress.styleprogress.ProgressStyle_15;


public class ShapeProgressDialog extends View {

    // style
    public static final int BallPulse = 0;
    public static final int BallGridPulse = 1;
    public static final int BallClipRotate = 2;
    public static final int BallClipRotatePulse = 3;
    public static final int SquareSpin = 4;
    public static final int BallClipRotateMultiple = 5;
    public static final int BallPulseRise = 6;
    public static final int BallRotate = 7;
    public static final int CubeTransition = 8;
    public static final int BallZigZag = 9;
    public static final int BallZigZagDeflect = 10;
    public static final int BallTrianglePath = 11;
    public static final int BallScale = 12;
    public static final int LineScale = 13;
    public static final int LineScaleParty = 14;
    public static final int BallScaleMultiple = 15;
    public static final int BallPulseSync = 16;
    public static final int BallBeat = 17;
    public static final int LineScalePulseOut = 18;
    public static final int LineScalePulseOutRapid = 19;
    public static final int BallScaleRipple = 20;
    public static final int BallScaleRippleMultiple = 21;
    public static final int BallSpinFadeLoader = 22;
    public static final int LineSpinFadeLoader = 23;
    public static final int TriangleSkewSpin = 24;
    public static final int Pacman = 25;
    public static final int BallGridBeat = 26;
    public static final int SemiCircleSpin = 27;

    @IntDef(flag = true, value = {
            BallPulse,
            BallGridPulse,
            BallClipRotate,
            BallClipRotatePulse,
            SquareSpin,
            BallClipRotateMultiple,
            BallPulseRise,
            BallRotate,
            CubeTransition,
            BallZigZag,
            BallZigZagDeflect,
            BallTrianglePath,
            BallScale,
            LineScale,
            LineScaleParty,
            BallScaleMultiple,
            BallPulseSync,
            BallBeat,
            LineScalePulseOut,
            LineScalePulseOutRapid,
            BallScaleRipple,
            BallScaleRippleMultiple,
            BallSpinFadeLoader,
            LineSpinFadeLoader,
            TriangleSkewSpin,
            Pacman,
            BallGridBeat,
            SemiCircleSpin
    })
    public @interface Indicator {
    }

    // Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 45;

    // attrs
    int mIndicatorId;
    int mIndicatorColor;

    Paint mPaint;

    BaseProgressStyle mIndicatorController;
    private boolean mHasAnimation;

    public ShapeProgressDialog(Context context) {
        super(context);
        init(null, 0);
    }

    public ShapeProgressDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ShapeProgressDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShapeProgressDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShapeProgressDialog);
        mIndicatorId = a.getInt(R.styleable.ShapeProgressDialog_styles, BallPulse);
        mIndicatorColor = a.getColor(R.styleable.ShapeProgressDialog_styles_color, Color.WHITE);
        a.recycle();
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator() {
        switch (mIndicatorId) {
            case BallPulse:
                mIndicatorController = new ProgressStyle_7();
                break;
            case BallGridPulse:
                mIndicatorController = new ProgressStyle_6();
                break;
            case BallClipRotate:
                mIndicatorController = new ProgressStyle_2();
                break;
            case BallClipRotatePulse:
                mIndicatorController = new ProgressStyle_4();
                break;
            case SquareSpin:
                mIndicatorController = new ProgressStyle_14();
                break;
            case BallClipRotateMultiple:
                mIndicatorController = new ProgressStyle_3();
                break;
            case BallPulseRise:
                mIndicatorController = new ProgressStyle_8();
                break;
            case BallRotate:
                mIndicatorController = new ProgressStyle_10();
                break;
            case CubeTransition:
                mIndicatorController = new ProgressStyle_11();
                break;
            case BallZigZag:
                mIndicatorController = new ProgressStyle_19();
                break;
            case BallZigZagDeflect:
                mIndicatorController = new ProgressStyle_19_1();
                break;
            case BallTrianglePath:
                mIndicatorController = new ProgressStyle_12();
                break;
            case BallScale:
                mIndicatorController = new ProgressStyle_18();
                break;
            case LineScale:
                mIndicatorController = new ProgressStyle_22();
                break;
            case LineScaleParty:
                mIndicatorController = new ProgressStyle_17();
                break;
            case BallScaleMultiple:
                mIndicatorController = new ProgressStyle_20();
                break;
            case BallPulseSync:
                mIndicatorController = new ProgressStyle_9();
                break;
            case BallBeat:
                mIndicatorController = new ProgressStyle_1();
                break;
            case LineScalePulseOut:
                mIndicatorController = new ProgressStyle_22_1();
                break;
            case LineScalePulseOutRapid:
                mIndicatorController = new ProgressStyle_22_2();
                break;
            case BallScaleRipple:
                mIndicatorController = new ProgressStyle_18_1();
                break;
            case BallScaleRippleMultiple:
                mIndicatorController = new ProgressStyle_20_1();
                break;
            case BallSpinFadeLoader:
                mIndicatorController = new ProgressStyle_21();
                break;
            case LineSpinFadeLoader:
                mIndicatorController = new ProgressStyle_21_1();
                break;
            case TriangleSkewSpin:
                mIndicatorController = new ProgressStyle_15();
                break;
            case Pacman:
                mIndicatorController = new ProgressStyle_16();
                break;
            case BallGridBeat:
                mIndicatorController = new ProgressStyle_5();
                break;
            case SemiCircleSpin:
                mIndicatorController = new ProgressStyle_13();
                break;
        }
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(UtilDensity.dp2px(getContext(), DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(UtilDensity.dp2px(getContext(), DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            applyAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                mIndicatorController.setAnimationStatus(BaseProgressStyle.AnimStatus.END);
            } else {
                mIndicatorController.setAnimationStatus(BaseProgressStyle.AnimStatus.START);
            }
        }
    }

    void drawIndicator(Canvas canvas) {
        mIndicatorController.draw(canvas, mPaint);
    }

    void applyAnimation() {
        mIndicatorController.initAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(BaseProgressStyle.AnimStatus.CANCEL);
    }
}

package com.design.code.view.theme;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.design.code.R;
import com.design.code.view.theme.effects.TextAnvil;
import com.design.code.view.theme.effects.TextEvaporate;
import com.design.code.view.theme.effects.TextFall;
import com.design.code.view.theme.effects.TextLine;
import com.design.code.view.theme.effects.TextRainBow;
import com.design.code.view.theme.effects.TextScale;
import com.design.code.view.theme.effects.TextSparkle;
import com.design.code.view.theme.effects.TextTyper;

/**
 * Created by Design 2016/3/23.
 */
public class EffectsTextView extends TextView {

    private IEffects mIEffects = new TextScale();
    private AttributeSet attrs;
    private int defStyle;

    public enum EffectsType {
        SCALE, EVAPORATE, FALL, ANVIL, SPARKLE, LINE, TYPER, RAINBOW
    }

    public EffectsTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public EffectsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EffectsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        this.attrs = attrs;
        this.defStyle = defStyle;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextEffects);
        int animateType = typedArray.getInt(R.styleable.TextEffects_animateType, 0);
        switch (animateType) {
            case 0:
                mIEffects = new TextScale();
                break;
            case 1:
                mIEffects = new TextEvaporate();
                break;
            case 2:
                mIEffects = new TextFall();
                break;
            case 3:
                mIEffects = new TextSparkle();
                break;
            case 4:
                mIEffects = new TextAnvil();
                break;
            case 5:
                mIEffects = new TextLine();
                break;
            case 6:
                mIEffects = new TextTyper();
                break;
            case 7:
                mIEffects = new TextRainBow();
                break;
        }
        typedArray.recycle();
        initEffectsText(attrs, defStyle);
    }

    private void initEffectsText(AttributeSet attrs, int defStyle) {
        mIEffects.init(this, attrs, defStyle);
    }

    public void animateText(CharSequence text) {
        mIEffects.animateText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mIEffects.onDraw(canvas);
    }

    public void reset(CharSequence text) {
        mIEffects.reset(text);
    }

    public void setAnimateType(EffectsType type) {
        switch (type) {
            case SCALE:
                mIEffects = new TextScale();
                break;
            case EVAPORATE:
                mIEffects = new TextEvaporate();
                break;
            case FALL:
                mIEffects = new TextFall();
                break;
            case ANVIL:
                mIEffects = new TextAnvil();
                break;
            case SPARKLE:
                mIEffects = new TextSparkle();
                break;
            case LINE:
                mIEffects = new TextLine();
                break;
            case TYPER:
                mIEffects = new TextTyper();
                break;
            case RAINBOW:
                mIEffects = new TextRainBow();
                break;
        }
        initEffectsText(attrs, defStyle);
    }

    public interface IEffects {
        void init(EffectsTextView effectsTextView, AttributeSet attrs, int defStyle);

        void animateText(CharSequence text);

        void onDraw(Canvas canvas);

        void reset(CharSequence text);
    }
}

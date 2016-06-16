package com.design.code.base.interp;

import android.animation.TypeEvaluator;

import java.util.ArrayList;
import java.util.Collections;

public abstract class BaseInterpolator implements TypeEvaluator<Number> {
    protected float mDuration;

    private ArrayList<EasingListener> mListeners = new ArrayList<>();

    public interface EasingListener {
        void on(float time, float value, float start, float end, float duration);
    }

    public void addEasingListener(EasingListener l) {
        mListeners.add(l);
    }

    public void addEasingListeners(EasingListener... ls) {
        Collections.addAll(mListeners, ls);
    }

    public void removeEasingListener(EasingListener l) {
        mListeners.remove(l);
    }

    public void clearEasingListeners() {
        mListeners.clear();
    }

    public BaseInterpolator(float duration) {
        mDuration = duration;
    }

    public void setDuration(float duration) {
        mDuration = duration;
    }


    @Override
    public final Float evaluate(float fraction, Number startValue, Number endValue) {
        float t = mDuration * fraction;
        float b = startValue.floatValue();
        float c = endValue.floatValue() - startValue.floatValue();
        float d = mDuration;
        float result = calculate(t, b, c, d);
        for (EasingListener l : mListeners) {
            l.on(t, result, b, c, d);
        }
        return result;
    }

    public abstract Float calculate(float t, float b, float c, float d);

}

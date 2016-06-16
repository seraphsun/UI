package com.design.code.base.interp.back;

import com.design.code.base.interp.BaseInterpolator;

public class InterpBackEaseOut extends BaseInterpolator {

    private float s = 1.70158f;

    public InterpBackEaseOut(float duration) {
        super(duration);
    }

    public InterpBackEaseOut(float duration, float back) {
        this(duration);
        s = back;
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    }
}

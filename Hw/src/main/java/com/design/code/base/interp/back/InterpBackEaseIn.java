package com.design.code.base.interp.back;

import com.design.code.base.interp.BaseInterpolator;

public class InterpBackEaseIn extends BaseInterpolator {

    private float s = 1.70158f;

    public InterpBackEaseIn(float duration) {
        super(duration);
    }

    public InterpBackEaseIn(float duration, float back) {
        this(duration);
        s = back;
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (t /= d) * t * ((s + 1) * t - s) + b;
    }

}

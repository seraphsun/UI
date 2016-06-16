package com.design.code.base.interp.cubic;

import com.design.code.base.interp.BaseInterpolator;

public class InterpCubicEaseIn extends BaseInterpolator {

    public InterpCubicEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (t /= d) * t * t + b;
    }
}

package com.design.code.base.interp.quint;

import com.design.code.base.interp.BaseInterpolator;

public class InterpQuintEaseIn extends BaseInterpolator {
    public InterpQuintEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (t /= d) * t * t * t * t + b;
    }
}

package com.design.code.base.interp.linear;

import com.design.code.base.interp.BaseInterpolator;

public class InterpLinear extends BaseInterpolator {
    public InterpLinear(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * t / d + b;
    }
}

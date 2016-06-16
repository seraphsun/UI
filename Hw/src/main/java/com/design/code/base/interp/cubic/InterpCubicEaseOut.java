package com.design.code.base.interp.cubic;


import com.design.code.base.interp.BaseInterpolator;

public class InterpCubicEaseOut extends BaseInterpolator {

    public InterpCubicEaseOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * t + 1) + b;
    }
}

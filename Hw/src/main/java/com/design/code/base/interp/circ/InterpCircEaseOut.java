package com.design.code.base.interp.circ;

import com.design.code.base.interp.BaseInterpolator;

public class InterpCircEaseOut extends BaseInterpolator {

    public InterpCircEaseOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
    }
}

package com.design.code.base.interp.circ;

import com.design.code.base.interp.BaseInterpolator;

public class InterpCircEaseIn extends BaseInterpolator {

    public InterpCircEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return -c * ((float) Math.sqrt(1 - (t /= d) * t) - 1) + b;
    }
}

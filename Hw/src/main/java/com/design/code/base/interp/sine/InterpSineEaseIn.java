package com.design.code.base.interp.sine;

import com.design.code.base.interp.BaseInterpolator;

public class InterpSineEaseIn extends BaseInterpolator {

    public InterpSineEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return -c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;
    }
}

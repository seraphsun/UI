package com.design.code.base.interp.sine;

import com.design.code.base.interp.BaseInterpolator;

public class InterpSineEaseOut extends BaseInterpolator {
    public InterpSineEaseOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
    }
}

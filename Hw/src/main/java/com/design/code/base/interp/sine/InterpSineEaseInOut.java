package com.design.code.base.interp.sine;

import com.design.code.base.interp.BaseInterpolator;

public class InterpSineEaseInOut extends BaseInterpolator {
    public InterpSineEaseInOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return -c / 2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;
    }
}

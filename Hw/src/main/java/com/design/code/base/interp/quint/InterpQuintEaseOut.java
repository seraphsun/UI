package com.design.code.base.interp.quint;


import com.design.code.base.interp.BaseInterpolator;

public class InterpQuintEaseOut extends BaseInterpolator {
    public InterpQuintEaseOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
    }
}

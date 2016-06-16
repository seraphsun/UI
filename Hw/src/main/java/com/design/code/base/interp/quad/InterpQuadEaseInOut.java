package com.design.code.base.interp.quad;


import com.design.code.base.interp.BaseInterpolator;

public class InterpQuadEaseInOut extends BaseInterpolator {
    public InterpQuadEaseInOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }
}

package com.design.code.base.interp.quad;

import com.design.code.base.interp.BaseInterpolator;

public class InterpQuadEaseIn extends BaseInterpolator {
    public InterpQuadEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c * (t /= d) * t + b;
    }
}

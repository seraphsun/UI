package com.design.code.base.interp.quad;

import com.design.code.base.interp.BaseInterpolator;

public class InterpQuadEaseOut extends BaseInterpolator {
    public InterpQuadEaseOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return -c * (t /= d) * (t - 2) + b;
    }
}

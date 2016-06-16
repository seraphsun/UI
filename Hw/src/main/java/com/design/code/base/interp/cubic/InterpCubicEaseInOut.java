package com.design.code.base.interp.cubic;


import com.design.code.base.interp.BaseInterpolator;

public class InterpCubicEaseInOut extends BaseInterpolator {

    public InterpCubicEaseInOut(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        if ((t /= d / 2) < 1)
            return c / 2 * t * t * t + b;

        return c / 2 * ((t -= 2) * t * t + 2) + b;
    }
}

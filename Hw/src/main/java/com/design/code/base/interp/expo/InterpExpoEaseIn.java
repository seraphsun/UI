package com.design.code.base.interp.expo;


import com.design.code.base.interp.BaseInterpolator;

public class InterpExpoEaseIn extends BaseInterpolator {
    public InterpExpoEaseIn(float duration) {
        super(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;
    }
}

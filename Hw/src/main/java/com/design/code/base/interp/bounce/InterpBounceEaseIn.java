package com.design.code.base.interp.bounce;

import com.design.code.base.interp.BaseInterpolator;

public class InterpBounceEaseIn extends BaseInterpolator {

    private InterpBounceEaseOut mInterpBounceEaseOut;

    public InterpBounceEaseIn(float duration) {
        super(duration);
        mInterpBounceEaseOut = new InterpBounceEaseOut(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        return c - mInterpBounceEaseOut.calculate(d - t, 0, c, d) + b;
    }
}

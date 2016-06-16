package com.design.code.base.interp.bounce;

import com.design.code.base.interp.BaseInterpolator;

public class InterpBounceEaseInOut extends BaseInterpolator {

    private InterpBounceEaseOut mInterpBounceEaseOut;
    private InterpBounceEaseIn mInterpBounceEaseIn;

    public InterpBounceEaseInOut(float duration) {
        super(duration);
        mInterpBounceEaseIn = new InterpBounceEaseIn(duration);
        mInterpBounceEaseOut = new InterpBounceEaseOut(duration);
    }

    @Override
    public Float calculate(float t, float b, float c, float d) {
        if (t < d / 2)
            return mInterpBounceEaseIn.calculate(t * 2, 0, c, d) * .5f + b;
        else
            return mInterpBounceEaseOut.calculate(t * 2 - d, 0, c, d) * .5f + c * .5f + b;
    }
}

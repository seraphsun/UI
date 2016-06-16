package com.design.code.base.interp;

import com.design.code.base.interp.back.*;
import com.design.code.base.interp.bounce.*;
import com.design.code.base.interp.circ.*;
import com.design.code.base.interp.cubic.*;
import com.design.code.base.interp.elastic.*;
import com.design.code.base.interp.expo.*;
import com.design.code.base.interp.linear.*;
import com.design.code.base.interp.quad.*;
import com.design.code.base.interp.quint.*;
import com.design.code.base.interp.sine.*;

public enum Skill {

    BackEaseIn(InterpBackEaseIn.class),
    BackEaseOut(InterpBackEaseOut.class),
    BackEaseInOut(InterpBackEaseInOut.class),

    BounceEaseIn(InterpBounceEaseIn.class),
    BounceEaseOut(InterpBounceEaseOut.class),
    BounceEaseInOut(InterpBounceEaseInOut.class),

    CircEaseIn(InterpCircEaseIn.class),
    CircEaseOut(InterpCircEaseOut.class),
    CircEaseInOut(InterpCircEaseInOut.class),

    CubicEaseIn(InterpCubicEaseIn.class),
    CubicEaseOut(InterpCubicEaseOut.class),
    CubicEaseInOut(InterpCubicEaseInOut.class),

    ElasticEaseIn(InterpElasticEaseIn.class),
    ElasticEaseOut(InterpElasticEaseOut.class),

    ExpoEaseIn(InterpExpoEaseIn.class),
    ExpoEaseOut(InterpExpoEaseOut.class),
    ExpoEaseInOut(InterpExpoEaseInOut.class),

    QuadEaseIn(InterpQuadEaseIn.class),
    QuadEaseOut(InterpQuadEaseOut.class),
    QuadEaseInOut(InterpQuadEaseInOut.class),

    QuintEaseIn(InterpQuintEaseIn.class),
    QuintEaseOut(InterpQuintEaseOut.class),
    QuintEaseInOut(InterpQuintEaseInOut.class),

    SineEaseIn(InterpSineEaseIn.class),
    SineEaseOut(InterpSineEaseOut.class),
    SineEaseInOut(InterpSineEaseInOut.class),

    Linear(InterpLinear.class);


    private Class easingMethod;

    Skill(Class clazz) {
        easingMethod = clazz;
    }

    public BaseInterpolator getMethod(float duration) {
        try {
            return (BaseInterpolator) easingMethod.getConstructor(float.class).newInstance(duration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Can not init easingMethod instance");
        }
    }
}

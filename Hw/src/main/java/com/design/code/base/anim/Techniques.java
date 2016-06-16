package com.design.code.base.anim;

import com.design.code.base.anim.attention.ViewAnimBounce;
import com.design.code.base.anim.attention.ViewAnimFlash;
import com.design.code.base.anim.attention.ViewAnimPulse;
import com.design.code.base.anim.attention.ViewAnimRubberBand;
import com.design.code.base.anim.attention.ViewAnimShake;
import com.design.code.base.anim.attention.ViewAnimStandUp;
import com.design.code.base.anim.attention.ViewAnimSwing;
import com.design.code.base.anim.attention.ViewAnimTada;
import com.design.code.base.anim.attention.ViewAnimWave;
import com.design.code.base.anim.attention.ViewAnimWobble;
import com.design.code.base.anim.bouncing.ViewAnimBounceIn;
import com.design.code.base.anim.bouncing.ViewAnimBounceInDown;
import com.design.code.base.anim.bouncing.ViewAnimBounceInLeft;
import com.design.code.base.anim.bouncing.ViewAnimBounceInRight;
import com.design.code.base.anim.bouncing.ViewAnimBounceInUp;
import com.design.code.base.anim.fading_in.ViewAnimFadeIn;
import com.design.code.base.anim.fading_in.ViewAnimFadeInDown;
import com.design.code.base.anim.fading_in.ViewAnimFadeInLeft;
import com.design.code.base.anim.fading_in.ViewAnimFadeInRight;
import com.design.code.base.anim.fading_in.ViewAnimFadeInUp;
import com.design.code.base.anim.fading_out.ViewAnimFadeOut;
import com.design.code.base.anim.fading_out.ViewAnimFadeOutDown;
import com.design.code.base.anim.fading_out.ViewAnimFadeOutLeft;
import com.design.code.base.anim.fading_out.ViewAnimFadeOutRight;
import com.design.code.base.anim.fading_out.ViewAnimFadeOutUp;
import com.design.code.base.anim.flippers.ViewAnimFlipInX;
import com.design.code.base.anim.flippers.ViewAnimFlipInY;
import com.design.code.base.anim.flippers.ViewAnimFlipOutX;
import com.design.code.base.anim.flippers.ViewAnimFlipOutY;
import com.design.code.base.anim.rotating_in.ViewAnimRotateIn;
import com.design.code.base.anim.rotating_in.ViewAnimRotateInDownLeft;
import com.design.code.base.anim.rotating_in.ViewAnimRotateInDownRight;
import com.design.code.base.anim.rotating_in.ViewAnimRotateInUpLeft;
import com.design.code.base.anim.rotating_in.ViewAnimRotateInUpRight;
import com.design.code.base.anim.rotating_out.ViewAnimRotateOut;
import com.design.code.base.anim.rotating_out.ViewAnimRotateOutDownLeft;
import com.design.code.base.anim.rotating_out.ViewAnimRotateOutDownRight;
import com.design.code.base.anim.rotating_out.ViewAnimRotateOutUpLeft;
import com.design.code.base.anim.rotating_out.ViewAnimRotateOutUpRight;
import com.design.code.base.anim.sliders.ViewAnimSlideInDown;
import com.design.code.base.anim.sliders.ViewAnimSlideInLeft;
import com.design.code.base.anim.sliders.ViewAnimSlideInRight;
import com.design.code.base.anim.sliders.ViewAnimSlideInUp;
import com.design.code.base.anim.sliders.ViewAnimSlideOutDown;
import com.design.code.base.anim.sliders.ViewAnimSlideOutLeft;
import com.design.code.base.anim.sliders.ViewAnimSlideOutRight;
import com.design.code.base.anim.sliders.ViewAnimSlideOutUp;
import com.design.code.base.anim.specials.ViewAnimHinge;
import com.design.code.base.anim.specials.ViewAnimRollIn;
import com.design.code.base.anim.specials.ViewAnimRollOut;
import com.design.code.base.anim.specials.in.ViewAnimDropOut;
import com.design.code.base.anim.specials.in.ViewAnimLanding;
import com.design.code.base.anim.specials.out.ViewAnimTakingOff;
import com.design.code.base.anim.zooming_int.ViewAnimZoomIn;
import com.design.code.base.anim.zooming_int.ViewAnimZoomInDown;
import com.design.code.base.anim.zooming_int.ViewAnimZoomInLeft;
import com.design.code.base.anim.zooming_int.ViewAnimZoomInRight;
import com.design.code.base.anim.zooming_int.ViewAnimZoomInUp;
import com.design.code.base.anim.zooming_out.ViewAnimZoomOut;
import com.design.code.base.anim.zooming_out.ViewAnimZoomOutDown;
import com.design.code.base.anim.zooming_out.ViewAnimZoomOutLeft;
import com.design.code.base.anim.zooming_out.ViewAnimZoomOutRight;
import com.design.code.base.anim.zooming_out.ViewAnimZoomOutUp;

public enum Techniques {

    DropOut(ViewAnimDropOut.class),
    Landing(ViewAnimLanding.class),
    TakingOff(ViewAnimTakingOff.class),

    Flash(ViewAnimFlash.class),
    Pulse(ViewAnimPulse.class),
    RubberBand(ViewAnimRubberBand.class),
    Shake(ViewAnimShake.class),
    Swing(ViewAnimSwing.class),
    Wobble(ViewAnimWobble.class),
    Bounce(ViewAnimBounce.class),
    Tada(ViewAnimTada.class),
    StandUp(ViewAnimStandUp.class),
    Wave(ViewAnimWave.class),

    Hinge(ViewAnimHinge.class),
    RollIn(ViewAnimRollIn.class),
    RollOut(ViewAnimRollOut.class),

    BounceIn(ViewAnimBounceIn.class),
    BounceInDown(ViewAnimBounceInDown.class),
    BounceInLeft(ViewAnimBounceInLeft.class),
    BounceInRight(ViewAnimBounceInRight.class),
    BounceInUp(ViewAnimBounceInUp.class),

    FadeIn(ViewAnimFadeIn.class),
    FadeInUp(ViewAnimFadeInUp.class),
    FadeInDown(ViewAnimFadeInDown.class),
    FadeInLeft(ViewAnimFadeInLeft.class),
    FadeInRight(ViewAnimFadeInRight.class),

    FadeOut(ViewAnimFadeOut.class),
    FadeOutDown(ViewAnimFadeOutDown.class),
    FadeOutLeft(ViewAnimFadeOutLeft.class),
    FadeOutRight(ViewAnimFadeOutRight.class),
    FadeOutUp(ViewAnimFadeOutUp.class),

    FlipInX(ViewAnimFlipInX.class),
    FlipOutX(ViewAnimFlipOutX.class),
    FlipInY(ViewAnimFlipInY.class),
    FlipOutY(ViewAnimFlipOutY.class),
    RotateIn(ViewAnimRotateIn.class),
    RotateInDownLeft(ViewAnimRotateInDownLeft.class),
    RotateInDownRight(ViewAnimRotateInDownRight.class),
    RotateInUpLeft(ViewAnimRotateInUpLeft.class),
    RotateInUpRight(ViewAnimRotateInUpRight.class),

    RotateOut(ViewAnimRotateOut.class),
    RotateOutDownLeft(ViewAnimRotateOutDownLeft.class),
    RotateOutDownRight(ViewAnimRotateOutDownRight.class),
    RotateOutUpLeft(ViewAnimRotateOutUpLeft.class),
    RotateOutUpRight(ViewAnimRotateOutUpRight.class),

    SlideInLeft(ViewAnimSlideInLeft.class),
    SlideInRight(ViewAnimSlideInRight.class),
    SlideInUp(ViewAnimSlideInUp.class),
    SlideInDown(ViewAnimSlideInDown.class),

    SlideOutLeft(ViewAnimSlideOutLeft.class),
    SlideOutRight(ViewAnimSlideOutRight.class),
    SlideOutUp(ViewAnimSlideOutUp.class),
    SlideOutDown(ViewAnimSlideOutDown.class),

    ZoomIn(ViewAnimZoomIn.class),
    ZoomInDown(ViewAnimZoomInDown.class),
    ZoomInLeft(ViewAnimZoomInLeft.class),
    ZoomInRight(ViewAnimZoomInRight.class),
    ZoomInUp(ViewAnimZoomInUp.class),

    ZoomOut(ViewAnimZoomOut.class),
    ZoomOutDown(ViewAnimZoomOutDown.class),
    ZoomOutLeft(ViewAnimZoomOutLeft.class),
    ZoomOutRight(ViewAnimZoomOutRight.class),
    ZoomOutUp(ViewAnimZoomOutUp.class);


    private Class animatorClazz;

    Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}

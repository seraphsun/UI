package com.design.code.view.slider.anim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.design.code.R;
import com.design.code.util.anim.AnimHelper;
import com.design.code.view.slider.base.PagerBase;

/**
 * A demo class to show how to use {@link AnimBase}
 * to make your custom animation in {@link PagerBase.PageTransformer} action.
 */
public class AnimDescription implements AnimBase {

    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
        View descriptionLayout = current.findViewById(R.id.description_layout);
        if(descriptionLayout!=null){
            current.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * When next item is coming to show, let's hide the description layout.
     */
    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        View descriptionLayout = next.findViewById(R.id.description_layout);
        if(descriptionLayout!=null){
            next.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCurrentItemDisappear(View view) {

    }

    /**
     * When next item show in PagerBase, let's make an animation to show the description layout.
     */
    @Override
    public void onNextItemAppear(View view) {
        View descriptionLayout = view.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            float layoutY = AnimHelper.getY(descriptionLayout);
            view.findViewById(R.id.description_layout).setVisibility(View.VISIBLE);
            ValueAnimator animator = ObjectAnimator.ofFloat(descriptionLayout, "y", layoutY + descriptionLayout.getHeight(), layoutY).setDuration(500);
            animator.start();
        }

    }
}

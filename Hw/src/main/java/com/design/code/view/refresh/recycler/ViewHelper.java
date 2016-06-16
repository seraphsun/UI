package com.design.code.view.refresh.recycler;

import android.support.v4.view.ViewCompat;
import android.view.View;

public final class ViewHelper {

    public static void clear(View view) {
        ViewCompat.setAlpha(view, 1);
        ViewCompat.setScaleY(view, 1);
        ViewCompat.setScaleX(view, 1);
        ViewCompat.setTranslationY(view, 0);
        ViewCompat.setTranslationX(view, 0);
        ViewCompat.setRotation(view, 0);
        ViewCompat.setRotationY(view, 0);
        ViewCompat.setRotationX(view, 0);

        view.setPivotY(view.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(view, view.getMeasuredWidth() / 2);
        ViewCompat.animate(view).setInterpolator(null);
    }

}

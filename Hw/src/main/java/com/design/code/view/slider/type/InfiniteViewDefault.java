package com.design.code.view.slider.type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.design.code.R;
import com.design.code.view.slider.base.PagerInfiniteView;


/**
 * a simple slider view, which just show an image. If you want to make your own slider view,
 *
 * just extend PagerInfiniteView, and implement getView() method.
 */
public class InfiniteViewDefault extends PagerInfiniteView {

    public InfiniteViewDefault(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_1_slider_render_default, null);
        ImageView target = (ImageView) v.findViewById(R.id.daimajia_slider_image);
        bindEventAndShow(v, target);
        return v;
    }
}

package com.design.code.view.slider.type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.design.code.R;
import com.design.code.view.slider.base.PagerInfiniteView;


/**
 * This is a slider with a description TextView.
 */
public class InfiniteViewText extends PagerInfiniteView {

    public InfiniteViewText(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_1_slider_render_text, null);
        ImageView target = (ImageView) v.findViewById(R.id.daimajia_slider_image);
        TextView description = (TextView) v.findViewById(R.id.description);
        description.setText(getDescription());
        bindEventAndShow(v, target);
        return v;
    }
}

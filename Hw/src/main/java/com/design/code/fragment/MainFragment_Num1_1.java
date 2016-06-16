package com.design.code.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.design.code.R;
import com.design.code.view.slider.BannerPager;

/**
 * Created by Design 2016/3/23.
 */
public class MainFragment_Num1_1 extends Fragment {

//    private BannerPager pager;
//    private int[] imagesId = {R.mipmap.fragment_1_img_bigbang, R.mipmap.fragment_1_img_hannibal, R.mipmap.fragment_1_img_house, R.mipmap.fragment_1_img_thrones};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_num_1_1, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
//        pager = (BannerPager) getActivity().findViewById(R.id.banner);
//        pager.setImagesRes(imagesId);


    }
}

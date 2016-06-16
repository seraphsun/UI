package com.design.code.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.design.code.R;
import com.design.code.view.theme.ExplosionView;
import com.design.code.view.theme.Window8ImageView;

/**
 * Created by Ignacey 2016/1/11.
 */
public class MainFragment_Num3 extends Fragment implements Window8ImageView.OnViewClickListener {

    private AppCompatActivity mActivity;
    private ExplosionView mExplosionView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_num_3, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mExplosionView = ExplosionView.attach2Window(getActivity());
        addListener(getActivity().findViewById(R.id.theme_root));
        setListener();
    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExplosionView.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }
    }

//    private void reset(View root) {
//        if (root instanceof ViewGroup) {
//            ViewGroup parent = (ViewGroup) root;
//            for (int i = 0; i < parent.getChildCount(); i++) {
//                reset(parent.getChildAt(i));
//            }
//        } else {
//            root.setScaleX(1);
//            root.setScaleY(1);
//            root.setAlpha(1);
//        }
//    }

    private void setListener() {
    }

    @Override
    public void onViewClick(Window8ImageView view) {
    }

    @Override
    public void onStop() {
        super.onStop();
        new Handler().removeCallbacksAndMessages(null);
    }
}

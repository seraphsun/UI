package com.design.code.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.design.code.R;
import com.design.code.view.theme.EffectsTextView;

/**
 * Created by Ignacey 2016/1/11.
 */
public class MainFragment_Num4 extends Fragment {

    String[] sentences = new String[]{
            "What is design?",
            "Design",
            "Design is not just",
            "what it looks like",
            "and feels like.",
            "Design",
            "is how it works.",
            "- Steve Jobs",
            "Older people",
            "sit down and ask,",
            "'What is it?'",
            "but the boy asks,",
            "'What can I do with it?'.",
            "- Steve Jobs",
            "Swift",
            "Objective-C",
            "iPhone",
            "iPad",
            "Mac Mini",
            "MacBook Pro",
            "Mac Pro",
            "爱老婆",
            "老婆和女儿"
    };

    private int mCounter = 10;
    private EffectsTextView effectsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_num_4, null);

        initView(view);

        return view;
    }

    private void initView(View view) {
        effectsTextView = (EffectsTextView) view.findViewById(R.id.effects);
        effectsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCounter();
            }
        });
    }

    private void updateCounter() {
        if (mCounter == sentences.length - 1) {
            effectsTextView.setAnimateType(mEffectsTypes[type]);
            if (type < mEffectsTypes.length - 1) {
                type++;
            } else {
                type = 0;
            }
        }
        mCounter = mCounter >= sentences.length - 1 ? 0 : mCounter + 1;
        effectsTextView.animateText(sentences[mCounter]);
    }

    private int type;
    EffectsTextView.EffectsType[] mEffectsTypes = {
            EffectsTextView.EffectsType.SCALE,
            EffectsTextView.EffectsType.EVAPORATE,
            EffectsTextView.EffectsType.FALL,
            EffectsTextView.EffectsType.SPARKLE,
            EffectsTextView.EffectsType.ANVIL,
            EffectsTextView.EffectsType.LINE,
            EffectsTextView.EffectsType.TYPER,
            EffectsTextView.EffectsType.RAINBOW
    };
}

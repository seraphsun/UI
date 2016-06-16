package com.design.code.view.theme.effects;

import android.graphics.Canvas;

/**
 * 打字机效果
 */
public class TextTyper extends BaseText {
    private int currentLength;

    @Override
    protected void initVariables() {
    }

    @Override
    protected void animateStart(CharSequence text) {
        currentLength = 0;
        mEffectsTextView.invalidate();
    }

    @Override
    protected void animatePrepare(CharSequence text) {
    }

    @Override
    protected void drawFrame(Canvas canvas) {
        canvas.drawText(mText, 0, currentLength, startX, startY, mPaint);
        if (currentLength < mText.length()) {
            currentLength++;
            mEffectsTextView.postInvalidateDelayed(100);
        }
    }
}

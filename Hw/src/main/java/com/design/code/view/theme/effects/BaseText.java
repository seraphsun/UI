package com.design.code.view.theme.effects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.design.code.view.theme.EffectsTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseText implements EffectsTextView.IEffects {

    protected Paint mPaint, mOldPaint;

    /**
     * the gap between characters
     */
    protected float[] gaps = new float[100];
    protected float[] oldGaps = new float[100];

    /**
     * current text size
     */
    protected float mTextSize;

    protected CharSequence mText;
    protected CharSequence mOldText;

    protected List<CharacterDiffResult> differentList = new ArrayList<>();

    /**
     * 原来的字符串开始画的x位置
     */
    protected float oldStartX = 0;
    /**
     * 新的字符串开始画的x位置
     */
    protected float startX = 0;
    /**
     * 字符串开始画的y, baseline
     */
    protected float startY = 0;

    protected EffectsTextView mEffectsTextView;

    @Override
    public void init(EffectsTextView hTextView, AttributeSet attrs, int defStyle) {
        mEffectsTextView = hTextView;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mEffectsTextView.getCurrentTextColor());
        mPaint.setStyle(Paint.Style.FILL);

        mOldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOldPaint.setColor(mEffectsTextView.getCurrentTextColor());
        mOldPaint.setStyle(Paint.Style.FILL);

        mText = mEffectsTextView.getText();
        mOldText = mEffectsTextView.getText();

        mTextSize = mEffectsTextView.getTextSize();

        initVariables();
        mEffectsTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareAnimate();
            }
        }, 50);
    }

    @Override
    public void animateText(CharSequence text) {
        mEffectsTextView.setText(text);
        mOldText = mText;
        mText = text;
        prepareAnimate();
        animatePrepare(text);
        animateStart(text);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawFrame(canvas);
    }

    private void prepareAnimate() {
        mTextSize = mEffectsTextView.getTextSize();

        mPaint.setTextSize(mTextSize);
        for (int i = 0; i < mText.length(); i++) {
            gaps[i] = mPaint.measureText(mText.charAt(i) + "");
        }

        mOldPaint.setTextSize(mTextSize);
        for (int i = 0; i < mOldText.length(); i++) {
            oldGaps[i] = mOldPaint.measureText(mOldText.charAt(i) + "");
        }

        oldStartX = (mEffectsTextView.getMeasuredWidth() - mEffectsTextView.getCompoundPaddingLeft() - mEffectsTextView.getPaddingLeft() - mOldPaint.measureText(mOldText.toString())) / 2f;
        startX = (mEffectsTextView.getMeasuredWidth() - mEffectsTextView.getCompoundPaddingLeft() - mEffectsTextView.getPaddingLeft() - mPaint.measureText(mText.toString())) / 2f;
        startY = mEffectsTextView.getBaseline();

        differentList.clear();
        differentList.addAll(CharacterUtils.diff(mOldText, mText));
    }

    public void reset(CharSequence text) {
        animatePrepare(text);
        mEffectsTextView.invalidate();
    }

    /**
     * 类被实例化时初始化
     */
    protected abstract void initVariables();

    /**
     * 具体实现动画
     */
    protected abstract void animateStart(CharSequence text);

    /**
     * 每次动画前初始化调用
     */
    protected abstract void animatePrepare(CharSequence text);

    /**
     * 动画每次刷新界面时调用
     */
    protected abstract void drawFrame(Canvas canvas);

    public static class CharacterDiffResult {
        public char c;
        public int fromIndex;
        public int moveIndex;
    }

    /**
     * 字符处理工具类
     */
    public static class CharacterUtils {

        /**
         * 对比新的字符串和旧的,返回需要保留的字符,以及移动的位置
         *
         * @param oldText 原来的字符串
         * @param newText 新出现的字符串
         * @return 保留的字符, 以及移动的位置
         */
        public static List<CharacterDiffResult> diff(CharSequence oldText, CharSequence newText) {
            List<CharacterDiffResult> differentList = new ArrayList<>();
            Set<Integer> skip = new HashSet<>();

            for (int i = 0; i < oldText.length(); i++) {
                char c = oldText.charAt(i);
                for (int j = 0; j < newText.length(); j++) {
                    if (!skip.contains(j) && c == newText.charAt(j)) {
                        skip.add(j);
                        CharacterDiffResult different = new CharacterDiffResult();
                        different.c = c;
                        different.fromIndex = i;
                        different.moveIndex = j;
                        differentList.add(different);
                        break;
                    }
                }
            }
            return differentList;
        }

        public static int needMove(int index, List<CharacterDiffResult> differentList) {
            for (CharacterDiffResult different : differentList) {
                if (different.fromIndex == index) {
                    return different.moveIndex;
                }
            }
            return -1;
        }

        public static boolean stayHere(int index, List<CharacterDiffResult> differentList) {
            for (CharacterDiffResult different : differentList) {
                if (different.moveIndex == index) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 返回从原来的字符串的from下标移动到新的字符串move下标在进度为progress的x坐标
         *
         * @param from      原来的字符串的from下标
         * @param move      新的字符串move下标
         * @param progress  移动的进度 0~1
         * @param startX    新字符串位移初始值
         * @param oldStartX 原来字符串位移初始值
         * @param gaps      原来字符串每个字符的间距
         * @param oldGaps   新字符串每个字符的间距
         */
        public static float getOffset(int from, int move, float progress, float startX, float oldStartX, float[] gaps, float[] oldGaps) {

            // 计算目标点
            float dist = startX;
            for (int i = 0; i < move; i++) {
                dist += gaps[i];
            }

            // 计算当前点
            float cur = oldStartX;
            for (int i = 0; i < from; i++) {
                cur += oldGaps[i];
            }
            return cur + (dist - cur) * progress;
        }
    }


}

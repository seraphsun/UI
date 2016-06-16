package com.design.code.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * 字母索引
 * Created by Ignacey 2016/1/21.
 */
public class IndexListView extends ListView {

    private boolean mIsFastScrollEnabled = false;
    private IndexScroller mScroller = null;
    private GestureDetector mGestureDetector = null;

    public IndexListView(Context context) {
        super(context);
    }

    public IndexListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFastScrollEnabled() {
        return mIsFastScrollEnabled;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
        if (mIsFastScrollEnabled) {
            if (mScroller == null)
                mScroller = new IndexScroller(getContext(), this);
        } else {
            if (mScroller != null) {
                mScroller.hide();
                mScroller = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Overlay index bar
        if (mScroller != null)
            mScroller.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Intercept ListView's touch event
        if (mScroller != null && mScroller.onTouchEvent(ev))
            return true;

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    // If fling happens, index bar shows
                    mScroller.show();
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

            });
        }
        mGestureDetector.onTouchEvent(ev);

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null)
            mScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null)
            mScroller.onSizeChanged(w, h, oldw, oldh);
    }

    public class IndexScroller {

        private float mIndexbarWidth, mIndexbarMargin, mPreviewPadding, mDensity, mScaledDensity, mAlphaRate;
        private int mState = STATE_HIDDEN;
        private int mListViewWidth, mListViewHeight;
        private int mCurrentSection = -1;

        private boolean mIsIndexing;
        private ListView mListView;
        private SectionIndexer mIndexer;
        private String[] mSections;
        private RectF mIndexbarRect;

        private static final int STATE_HIDDEN = 0;
        private static final int STATE_SHOWING = 1;
        private static final int STATE_SHOWN = 2;
        private static final int STATE_HIDING = 3;

        private Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (mState) {
                    case STATE_SHOWING:
                        // Fade in effect
                        mAlphaRate += (1 - mAlphaRate) * 0.2;
                        if (mAlphaRate > 0.9) {
                            mAlphaRate = 1;
                            setState(STATE_SHOWN);
                        }

                        mListView.invalidate();
                        fade(10);
                        break;
                    case STATE_SHOWN:
                        // If no action, hide automatically
                        setState(STATE_HIDING);
                        break;
                    case STATE_HIDING:
                        // Fade out effect
                        mAlphaRate -= mAlphaRate * 0.2;
                        if (mAlphaRate < 0.1) {
                            mAlphaRate = 0;
                            setState(STATE_HIDDEN);
                        }

                        mListView.invalidate();
                        fade(10);
                        break;
                }
            }
        };

        public IndexScroller(Context context, ListView lv) {
            mDensity = context.getResources().getDisplayMetrics().density;
            mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
            mListView = lv;
            setAdapter(mListView.getAdapter());

            mIndexbarWidth = 20 * mDensity;
            mIndexbarMargin = 10 * mDensity;
            mPreviewPadding = 5 * mDensity;
        }

        public void draw(Canvas canvas) {
            if (mState == STATE_HIDDEN)
                return;

            // mAlphaRate determines the rate of opacity
            Paint indexbarPaint = new Paint();
            indexbarPaint.setColor(Color.BLACK);
            indexbarPaint.setAlpha((int) (64 * mAlphaRate));
            indexbarPaint.setAntiAlias(true);
            canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, indexbarPaint);

            if (mSections != null && mSections.length > 0) {
                // Preview is shown when mCurrentSection is set
                if (mCurrentSection >= 0) {
                    Paint previewPaint = new Paint();
                    previewPaint.setColor(Color.BLACK);
                    previewPaint.setAlpha(96);
                    previewPaint.setAntiAlias(true);
                    previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

                    Paint previewTextPaint = new Paint();
                    previewTextPaint.setColor(Color.WHITE);
                    previewTextPaint.setAntiAlias(true);
                    previewTextPaint.setTextSize(50 * mScaledDensity);

                    float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                    float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                    RectF previewRect = new RectF((mListViewWidth - previewSize) / 2, (mListViewHeight - previewSize) / 2, (mListViewWidth - previewSize) / 2 + previewSize, (mListViewHeight - previewSize) / 2 + previewSize);

                    canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                    canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1, previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
                }

                Paint indexPaint = new Paint();
                indexPaint.setColor(Color.WHITE);
                indexPaint.setAlpha((int) (255 * mAlphaRate));
                indexPaint.setAntiAlias(true);
                indexPaint.setTextSize(12 * mScaledDensity);

                float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
                float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
                for (int i = 0; i < mSections.length; i++) {
                    float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
                    canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft, mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // If down event occurs inside index bar region, start indexing
                    if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY())) {
                        setState(STATE_SHOWN);

                        // It demonstrates that the motion event started from index bar
                        mIsIndexing = true;
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mIsIndexing) {
                        // If this event moves inside index bar
                        if (contains(ev.getX(), ev.getY())) {
                            // Determine which section the point is in, and move the list to that section
                            mCurrentSection = getSectionByPoint(ev.getY());
                            mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mIsIndexing) {
                        mIsIndexing = false;
                        mCurrentSection = -1;
                    }
                    if (mState == STATE_SHOWN)
                        setState(STATE_HIDING);
                    break;
            }
            return false;
        }

        private boolean contains(float x, float y) {
            // Determine if the point is in index bar region, which includes the right margin of the bar
            return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
        }

        private void setState(int state) {
            if (state < STATE_HIDDEN || state > STATE_HIDING)
                return;

            mState = state;
            switch (mState) {
                case STATE_HIDDEN:
                    // Cancel any fade effect
                    mHandler.removeMessages(0);
                    break;
                case STATE_SHOWING:
                    // Start to fade in
                    mAlphaRate = 0;
                    fade(0);
                    break;
                case STATE_SHOWN:
                    // Cancel any fade effect
                    mHandler.removeMessages(0);
                    break;
                case STATE_HIDING:
                    // Start to fade out after three seconds
                    mAlphaRate = 1;
                    fade(3000);
                    break;
            }
        }

        private void fade(long delay) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
        }

        private int getSectionByPoint(float y) {
            if (mSections == null || mSections.length == 0)
                return 0;
            if (y < mIndexbarRect.top + mIndexbarMargin)
                return 0;
            if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
                return mSections.length - 1;
            return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
        }

        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            mListViewWidth = w;
            mListViewHeight = h;
            mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth, mIndexbarMargin, w - mIndexbarMargin, h - mIndexbarMargin);
        }

        public void show() {
            if (mState == STATE_HIDDEN)
                setState(STATE_SHOWING);
            else if (mState == STATE_HIDING)
                setState(STATE_HIDING);
        }

        public void hide() {
            if (mState == STATE_SHOWN)
                setState(STATE_HIDING);
        }

        public void setAdapter(Adapter adapter) {
            if (adapter instanceof SectionIndexer) {
                mIndexer = (SectionIndexer) adapter;
                mSections = (String[]) mIndexer.getSections();
            }
        }
    }

    public static class StringMatcher {

        private final static char KOREAN_UNICODE_START = '가';
        private final static char KOREAN_UNICODE_END = '힣';
        private final static char KOREAN_UNIT = '까' - '가';
        private final static char[] KOREAN_INITIAL = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

        public static boolean match(String value, String keyword) {
            if (value == null || keyword == null)
                return false;
            if (keyword.length() > value.length())
                return false;

            int i = 0, j = 0;
            do {
                if (isKorean(value.charAt(i)) && isInitialSound(keyword.charAt(j))) {
                    if (keyword.charAt(j) == getInitialSound(value.charAt(i))) {
                        i++;
                        j++;
                    } else if (j > 0)
                        break;
                    else
                        i++;
                } else {
                    if (keyword.charAt(j) == value.charAt(i)) {
                        i++;
                        j++;
                    } else if (j > 0)
                        break;
                    else
                        i++;
                }
            } while (i < value.length() && j < keyword.length());

            return (j == keyword.length());
        }

        private static boolean isKorean(char c) {
            return c >= KOREAN_UNICODE_START && c <= KOREAN_UNICODE_END;
        }

        private static boolean isInitialSound(char c) {
            for (char i : KOREAN_INITIAL) {
                if (c == i)
                    return true;
            }
            return false;
        }

        private static char getInitialSound(char c) {
            return KOREAN_INITIAL[(c - KOREAN_UNICODE_START) / KOREAN_UNIT];
        }
    }
}

package com.design.code.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.design.code.R;
import com.design.code.util.UtilDensity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 滑动删除
 * Created by Ignacey 2016/1/21.
 */
public class SwipeFrameLayout extends FrameLayout {

    public static final int EMPTY_LAYOUT = -1;
    private static final int DRAG_LEFT = 1;
    private static final int DRAG_RIGHT = 2;
    private static final int DRAG_TOP = 4;
    private static final int DRAG_BOTTOM = 8;
    private static final DragEdge DefaultDragEdge = DragEdge.Right;

    private int mTouchSlop;

    private DragEdge mCurrentDragEdge = DefaultDragEdge;
    private ViewDragHelper mDragHelper;

    private int mDragDistance = 0;
    private LinkedHashMap<DragEdge, View> mDragEdges = new LinkedHashMap<>();
    private ShowMode mShowMode;

    private float[] mEdgeSwipesOffset = new float[4];

    private List<SwipeListener> mSwipeListeners = new ArrayList<>();
    private List<SwipeDenier> mSwipeDeniers = new ArrayList<>();
    private Map<View, ArrayList<OnRevealListener>> mRevealListeners = new HashMap<>();
    private Map<View, Boolean> mShowEntirely = new HashMap<>();
    private Map<View, Rect> mViewBoundCache = new HashMap<>();// save all children's bound, restore in onLayout

    private DoubleClickListener mDoubleClickListener;

    private boolean mSwipeEnabled = true;
    private boolean[] mSwipesEnabled = new boolean[]{true, true, true, true};
    private boolean mClickToClose = false;

    private int mEventCounter = 0;

    private Rect hitSurfaceRect;

    private boolean mIsBeingDragged;
    private float sX = -1, sY = -1;

    public enum DragEdge {
        Left,
        Top,
        Right,
        Bottom
    }

    public enum ShowMode {
        LayDown,
        PullOut
    }

    public enum Status {
        Middle,
        Open,
        Close
    }

    public SwipeFrameLayout(Context context) {
        this(context, null);
    }

    public SwipeFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (child == getSurfaceView()) {
                    switch (mCurrentDragEdge) {
                        case Top:
                        case Bottom:
                            return getPaddingLeft();
                        case Left:
                            if (left < getPaddingLeft()) return getPaddingLeft();
                            if (left > getPaddingLeft() + mDragDistance)
                                return getPaddingLeft() + mDragDistance;
                            break;
                        case Right:
                            if (left > getPaddingLeft()) return getPaddingLeft();
                            if (left < getPaddingLeft() - mDragDistance)
                                return getPaddingLeft() - mDragDistance;
                            break;
                    }
                } else if (getCurrentBottomView() == child) {

                    switch (mCurrentDragEdge) {
                        case Top:
                        case Bottom:
                            return getPaddingLeft();
                        case Left:
                            if (mShowMode == ShowMode.PullOut) {
                                if (left > getPaddingLeft()) return getPaddingLeft();
                            }
                            break;
                        case Right:
                            if (mShowMode == ShowMode.PullOut) {
                                if (left < getMeasuredWidth() - mDragDistance) {
                                    return getMeasuredWidth() - mDragDistance;
                                }
                            }
                            break;
                    }
                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                if (child == getSurfaceView()) {
                    switch (mCurrentDragEdge) {
                        case Left:
                        case Right:
                            return getPaddingTop();
                        case Top:
                            if (top < getPaddingTop()) return getPaddingTop();
                            if (top > getPaddingTop() + mDragDistance)
                                return getPaddingTop() + mDragDistance;
                            break;
                        case Bottom:
                            if (top < getPaddingTop() - mDragDistance) {
                                return getPaddingTop() - mDragDistance;
                            }
                            if (top > getPaddingTop()) {
                                return getPaddingTop();
                            }
                    }
                } else {
                    View surfaceView = getSurfaceView();
                    int surfaceViewTop = surfaceView == null ? 0 : surfaceView.getTop();
                    switch (mCurrentDragEdge) {
                        case Left:
                        case Right:
                            return getPaddingTop();
                        case Top:
                            if (mShowMode == ShowMode.PullOut) {
                                if (top > getPaddingTop()) return getPaddingTop();
                            } else {
                                if (surfaceViewTop + dy < getPaddingTop())
                                    return getPaddingTop();
                                if (surfaceViewTop + dy > getPaddingTop() + mDragDistance)
                                    return getPaddingTop() + mDragDistance;
                            }
                            break;
                        case Bottom:
                            if (mShowMode == ShowMode.PullOut) {
                                if (top < getMeasuredHeight() - mDragDistance)
                                    return getMeasuredHeight() - mDragDistance;
                            } else {
                                if (surfaceViewTop + dy >= getPaddingTop())
                                    return getPaddingTop();
                                if (surfaceViewTop + dy <= getPaddingTop() - mDragDistance)
                                    return getPaddingTop() - mDragDistance;
                            }
                    }
                }
                return top;
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                boolean result = child == getSurfaceView() || getBottomViews().contains(child);
                if (result) {
                    isCloseBeforeDrag = getOpenStatus() == Status.Close;
                }
                return result;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return mDragDistance;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return mDragDistance;
            }

            boolean isCloseBeforeDrag = true;

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                processHandRelease(xvel, yvel, isCloseBeforeDrag);
                for (SwipeListener l : mSwipeListeners) {
                    l.onHandRelease(SwipeFrameLayout.this, xvel, yvel);
                }

                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                View surfaceView = getSurfaceView();
                if (surfaceView == null) return;
                View currentBottomView = getCurrentBottomView();
                int evLeft = surfaceView.getLeft(), evRight = surfaceView.getRight(), evTop = surfaceView.getTop(), evBottom = surfaceView.getBottom();
                if (changedView == surfaceView) {
                    if (mShowMode == ShowMode.PullOut && currentBottomView != null) {
                        if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                            currentBottomView.offsetLeftAndRight(dx);
                        } else {
                            currentBottomView.offsetTopAndBottom(dy);
                        }
                    }
                } else if (getBottomViews().contains(changedView)) {

                    if (mShowMode == ShowMode.PullOut) {
                        surfaceView.offsetLeftAndRight(dx);
                        surfaceView.offsetTopAndBottom(dy);
                    } else {
                        Rect rect = computeBottomLayDown(mCurrentDragEdge);
                        if (currentBottomView != null) {
                            currentBottomView.layout(rect.left, rect.top, rect.right, rect.bottom);
                        }
                        int newLeft = surfaceView.getLeft() + dx, newTop = surfaceView.getTop() + dy;
                        if (mCurrentDragEdge == DragEdge.Left && newLeft < getPaddingLeft()) {
                            newLeft = getPaddingLeft();
                        } else if (mCurrentDragEdge == DragEdge.Right && newLeft > getPaddingLeft()) {
                            newLeft = getPaddingLeft();
                        } else if (mCurrentDragEdge == DragEdge.Top && newTop < getPaddingTop()) {
                            newTop = getPaddingTop();
                        } else if (mCurrentDragEdge == DragEdge.Bottom && newTop > getPaddingTop()) {
                            newTop = getPaddingTop();
                        }
                        surfaceView.layout(newLeft, newTop, newLeft + getMeasuredWidth(), newTop + getMeasuredHeight());
                    }
                }
                dispatchRevealEvent(evLeft, evTop, evRight, evBottom);
                dispatchSwipeEvent(evLeft, evTop, dx, dy);
                invalidate();
                captureChildrenBound();
            }
        };
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeFrameLayout);
        int dragEdgeChoices = a.getInt(R.styleable.SwipeFrameLayout_drag_edge, DRAG_RIGHT);
        mEdgeSwipesOffset[DragEdge.Left.ordinal()] = a.getDimension(R.styleable.SwipeFrameLayout_leftEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Right.ordinal()] = a.getDimension(R.styleable.SwipeFrameLayout_rightEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Top.ordinal()] = a.getDimension(R.styleable.SwipeFrameLayout_topEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Bottom.ordinal()] = a.getDimension(R.styleable.SwipeFrameLayout_bottomEdgeSwipeOffset, 0);
        setClickToClose(a.getBoolean(R.styleable.SwipeFrameLayout_clickToClose, mClickToClose));

        if ((dragEdgeChoices & DRAG_LEFT) == DRAG_LEFT) {
            mDragEdges.put(DragEdge.Left, null);
        }
        if ((dragEdgeChoices & DRAG_TOP) == DRAG_TOP) {
            mDragEdges.put(DragEdge.Top, null);
        }
        if ((dragEdgeChoices & DRAG_RIGHT) == DRAG_RIGHT) {
            mDragEdges.put(DragEdge.Right, null);
        }
        if ((dragEdgeChoices & DRAG_BOTTOM) == DRAG_BOTTOM) {
            mDragEdges.put(DragEdge.Bottom, null);
        }
        int ordinal = a.getInt(R.styleable.SwipeFrameLayout_show_mode, ShowMode.PullOut.ordinal());
        mShowMode = ShowMode.values()[ordinal];
        a.recycle();
    }

    public void setClickToClose(boolean mClickToClose) {
        this.mClickToClose = mClickToClose;
    }

    /**
     * return null if there is no surface view(no children)
     */
    public View getSurfaceView() {
        if (getChildCount() == 0) return null;
        return getChildAt(getChildCount() - 1);
    }

    /**
     * return null if there is no bottom view
     */
    public View getCurrentBottomView() {
        List<View> bottoms = getBottomViews();
        if (mCurrentDragEdge.ordinal() < bottoms.size()) {
            return bottoms.get(mCurrentDragEdge.ordinal());
        }
        return null;
    }

    /**
     * @return all bottomViews: left, top, right, bottom (may null if the edge is not set)
     */
    public List<View> getBottomViews() {
        ArrayList<View> bottoms = new ArrayList<>();
        for (DragEdge dragEdge : DragEdge.values()) {
            bottoms.add(mDragEdges.get(dragEdge));
        }
        return bottoms;
    }

    /**
     * get the open status.
     *
     * @return {@link Status} Open , Close or Middle.
     */
    public Status getOpenStatus() {
        View surfaceView = getSurfaceView();
        if (surfaceView == null) {
            return Status.Close;
        }
        int surfaceLeft = surfaceView.getLeft();
        int surfaceTop = surfaceView.getTop();
        if (surfaceLeft == getPaddingLeft() && surfaceTop == getPaddingTop()) return Status.Close;

        if (surfaceLeft == (getPaddingLeft() - mDragDistance) || surfaceLeft == (getPaddingLeft() + mDragDistance) || surfaceTop == (getPaddingTop() - mDragDistance) || surfaceTop == (getPaddingTop() + mDragDistance))
            return Status.Open;

        return Status.Middle;
    }

    /**
     * Process the surface release event.
     *
     * @param xvel                 xVelocity
     * @param yvel                 yVelocity
     * @param isCloseBeforeDragged the open state before drag
     */
    protected void processHandRelease(float xvel, float yvel, boolean isCloseBeforeDragged) {
        float minVelocity = mDragHelper.getMinVelocity();
        View surfaceView = getSurfaceView();
        DragEdge currentDragEdge = mCurrentDragEdge;
        if (currentDragEdge == null || surfaceView == null) {
            return;
        }
        float willOpenPercent = (isCloseBeforeDragged ? .25f : .75f);
        if (currentDragEdge == DragEdge.Left) {
            if (xvel > minVelocity) open();
            else if (xvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getLeft() / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Right) {
            if (xvel > minVelocity) close();
            else if (xvel < -minVelocity) open();
            else {
                float openPercent = 1f * (-getSurfaceView().getLeft()) / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Top) {
            if (yvel > minVelocity) open();
            else if (yvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getTop() / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Bottom) {
            if (yvel > minVelocity) close();
            else if (yvel < -minVelocity) open();
            else {
                float openPercent = 1f * (-getSurfaceView().getTop()) / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        }
    }

    /**
     * smoothly open surface.
     */
    public void open() {
        open(true, true);
    }

    public void open(boolean smooth) {
        open(smooth, true);
    }

    public void open(boolean smooth, boolean notify) {
        View surface = getSurfaceView(), bottom = getCurrentBottomView();
        if (surface == null) {
            return;
        }
        int dx, dy;
        Rect rect = computeSurfaceLayoutArea(true);
        if (smooth) {
            mDragHelper.smoothSlideViewTo(surface, rect.left, rect.top);
        } else {
            dx = rect.left - surface.getLeft();
            dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (getShowMode() == ShowMode.PullOut) {
                Rect bRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect);
                if (bottom != null) {
                    bottom.layout(bRect.left, bRect.top, bRect.right, bRect.bottom);
                }
            }
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    /**
     * a helper function to compute the Rect area that surface will hold in.
     *
     * @param open open status or close status.
     */
    private Rect computeSurfaceLayoutArea(boolean open) {
        int l = getPaddingLeft(), t = getPaddingTop();
        if (open) {
            if (mCurrentDragEdge == DragEdge.Left) {
                l = getPaddingLeft() + mDragDistance;
            } else if (mCurrentDragEdge == DragEdge.Right) {
                l = getPaddingLeft() - mDragDistance;
            } else if (mCurrentDragEdge == DragEdge.Top) {
                t = getPaddingTop() + mDragDistance;
            } else {
                t = getPaddingTop() - mDragDistance;
            }
        }
        return new Rect(l, t, l + getMeasuredWidth(), t + getMeasuredHeight());
    }

    public ShowMode getShowMode() {
        return mShowMode;
    }

    private Rect computeBottomLayoutAreaViaSurface(ShowMode mode, Rect surfaceArea) {
        View bottomView = getCurrentBottomView();

        int bl = surfaceArea.left, bt = surfaceArea.top, br = surfaceArea.right, bb = surfaceArea.bottom;
        if (mode == ShowMode.PullOut) {
            if (mCurrentDragEdge == DragEdge.Left) {
                bl = surfaceArea.left - mDragDistance;
            } else if (mCurrentDragEdge == DragEdge.Right) {
                bl = surfaceArea.right;
            } else if (mCurrentDragEdge == DragEdge.Top) {
                bt = surfaceArea.top - mDragDistance;
            } else {
                bt = surfaceArea.bottom;
            }

            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                bb = surfaceArea.bottom;
                br = bl + (bottomView == null ? 0 : bottomView.getMeasuredWidth());
            } else {
                bb = bt + (bottomView == null ? 0 : bottomView.getMeasuredHeight());
                br = surfaceArea.right;
            }
        } else if (mode == ShowMode.LayDown) {
            if (mCurrentDragEdge == DragEdge.Left) {
                br = bl + mDragDistance;
            } else if (mCurrentDragEdge == DragEdge.Right) {
                bl = br - mDragDistance;
            } else if (mCurrentDragEdge == DragEdge.Top) {
                bb = bt + mDragDistance;
            } else {
                bt = surfaceArea.bottom;
            }

        }
        return new Rect(bl, bt, br, bb);
    }

    protected void dispatchRevealEvent(final int surfaceLeft, final int surfaceTop, final int surfaceRight, final int surfaceBottom) {
        if (mRevealListeners.isEmpty()) return;
        for (Map.Entry<View, ArrayList<OnRevealListener>> entry : mRevealListeners.entrySet()) {
            View child = entry.getKey();
            Rect rect = getRelativePosition(child);
            if (isViewShowing(child, rect, mCurrentDragEdge, surfaceLeft, surfaceTop, surfaceRight, surfaceBottom)) {
                mShowEntirely.put(child, false);
                int distance = 0;
                float fraction = 0f;
                if (getShowMode() == ShowMode.LayDown) {
                    switch (mCurrentDragEdge) {
                        case Left:
                            distance = rect.left - surfaceLeft;
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Right:
                            distance = rect.right - surfaceRight;
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Top:
                            distance = rect.top - surfaceTop;
                            fraction = distance / (float) child.getHeight();
                            break;
                        case Bottom:
                            distance = rect.bottom - surfaceBottom;
                            fraction = distance / (float) child.getHeight();
                            break;
                    }
                } else if (getShowMode() == ShowMode.PullOut) {
                    switch (mCurrentDragEdge) {
                        case Left:
                            distance = rect.right - getPaddingLeft();
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Right:
                            distance = rect.left - getWidth();
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Top:
                            distance = rect.bottom - getPaddingTop();
                            fraction = distance / (float) child.getHeight();
                            break;
                        case Bottom:
                            distance = rect.top - getHeight();
                            fraction = distance / (float) child.getHeight();
                            break;
                    }
                }

                for (OnRevealListener l : entry.getValue()) {
                    l.onReveal(child, mCurrentDragEdge, Math.abs(fraction), distance);
                    if (Math.abs(fraction) == 1) {
                        mShowEntirely.put(child, true);
                    }
                }
            }

            if (isViewTotallyFirstShowed(child, rect, mCurrentDragEdge, surfaceLeft, surfaceTop, surfaceRight, surfaceBottom)) {
                mShowEntirely.put(child, true);
                for (OnRevealListener l : entry.getValue()) {
                    if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                        l.onReveal(child, mCurrentDragEdge, 1, child.getWidth());
                    } else {
                        l.onReveal(child, mCurrentDragEdge, 1, child.getHeight());
                    }

                }
            }
        }
    }

    protected Rect getRelativePosition(View child) {
        View t = child;
        Rect r = new Rect(t.getLeft(), t.getTop(), 0, 0);
        while (t.getParent() != null && t != getRootView()) {
            t = (View) t.getParent();
            if (t == this) break;
            r.left += t.getLeft();
            r.top += t.getTop();
        }
        r.right = r.left + child.getMeasuredWidth();
        r.bottom = r.top + child.getMeasuredHeight();
        return r;
    }

    protected boolean isViewShowing(View child, Rect relativePosition, DragEdge availableEdge, int surfaceLeft, int surfaceTop, int surfaceRight, int surfaceBottom) {
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        if (getShowMode() == ShowMode.LayDown) {
            switch (availableEdge) {
                case Right:
                    if (surfaceRight > childLeft && surfaceRight <= childRight) {
                        return true;
                    }
                    break;
                case Left:
                    if (surfaceLeft < childRight && surfaceLeft >= childLeft) {
                        return true;
                    }
                    break;
                case Top:
                    if (surfaceTop >= childTop && surfaceTop < childBottom) {
                        return true;
                    }
                    break;
                case Bottom:
                    if (surfaceBottom > childTop && surfaceBottom <= childBottom) {
                        return true;
                    }
                    break;
            }
        } else if (getShowMode() == ShowMode.PullOut) {
            switch (availableEdge) {
                case Right:
                    if (childLeft <= getWidth() && childRight > getWidth()) return true;
                    break;
                case Left:
                    if (childRight >= getPaddingLeft() && childLeft < getPaddingLeft()) return true;
                    break;
                case Top:
                    if (childTop < getPaddingTop() && childBottom >= getPaddingTop()) return true;
                    break;
                case Bottom:
                    if (childTop < getHeight() && childTop >= getPaddingTop()) return true;
                    break;
            }
        }
        return false;
    }

    /**
     * the dispatchRevealEvent method may not always get accurate position, it makes the view may not always get the event when the view is totally show( fraction = 1), so , we need to calculate every time.
     */
    protected boolean isViewTotallyFirstShowed(View child, Rect relativePosition, DragEdge edge, int surfaceLeft, int surfaceTop, int surfaceRight, int surfaceBottom) {
        if (mShowEntirely.get(child)) return false;
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        boolean r = false;
        if (getShowMode() == ShowMode.LayDown) {
            if ((edge == DragEdge.Right && surfaceRight <= childLeft) || (edge == DragEdge.Left && surfaceLeft >= childRight) || (edge == DragEdge.Top && surfaceTop >= childBottom) || (edge == DragEdge.Bottom && surfaceBottom <= childTop))
                r = true;
        } else if (getShowMode() == ShowMode.PullOut) {
            if ((edge == DragEdge.Right && childRight <= getWidth()) || (edge == DragEdge.Left && childLeft >= getPaddingLeft()) || (edge == DragEdge.Top && childTop >= getPaddingTop()) || (edge == DragEdge.Bottom && childBottom <= getHeight()))
                r = true;
        }
        return r;
    }

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, int dx, int dy) {
        DragEdge edge = getDragEdge();
        boolean open = true;
        if (edge == DragEdge.Left) {
            if (dx < 0) open = false;
        } else if (edge == DragEdge.Right) {
            if (dx > 0) open = false;
        } else if (edge == DragEdge.Top) {
            if (dy < 0) open = false;
        } else if (edge == DragEdge.Bottom) {
            if (dy > 0) open = false;
        }
        dispatchSwipeEvent(surfaceLeft, surfaceTop, open);
    }

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, boolean open) {
        safeBottomView();
        Status status = getOpenStatus();

        if (!mSwipeListeners.isEmpty()) {
            mEventCounter++;
            for (SwipeListener l : mSwipeListeners) {
                if (mEventCounter == 1) {
                    if (open) {
                        l.onStartOpen(this);
                    } else {
                        l.onStartClose(this);
                    }
                }
                l.onUpdate(SwipeFrameLayout.this, surfaceLeft - getPaddingLeft(), surfaceTop - getPaddingTop());
            }

            if (status == Status.Close) {
                for (SwipeListener l : mSwipeListeners) {
                    l.onClose(SwipeFrameLayout.this);
                }
                mEventCounter = 0;
            }

            if (status == Status.Open) {
                View currentBottomView = getCurrentBottomView();
                if (currentBottomView != null) {
                    currentBottomView.setEnabled(true);
                }
                for (SwipeListener l : mSwipeListeners) {
                    l.onOpen(SwipeFrameLayout.this);
                }
                mEventCounter = 0;
            }
        }
    }

    public DragEdge getDragEdge() {
        return mCurrentDragEdge;
    }

    /**
     * prevent bottom view get any touch event. Especially in LayDown mode.
     */
    private void safeBottomView() {
        Status status = getOpenStatus();
        List<View> bottoms = getBottomViews();

        if (status == Status.Close) {
            for (View bottom : bottoms) {
                if (bottom != null && bottom.getVisibility() != INVISIBLE) {
                    bottom.setVisibility(INVISIBLE);
                }
            }
        } else {
            View currentBottomView = getCurrentBottomView();
            if (currentBottomView != null && currentBottomView.getVisibility() != VISIBLE) {
                currentBottomView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * smoothly close surface.
     */
    public void close() {
        close(true, true);
    }

    public void close(boolean smooth) {
        close(smooth, true);
    }

    /**
     * close surface
     *
     * @param smooth smoothly or not.
     * @param notify if notify all the listeners.
     */
    public void close(boolean smooth, boolean notify) {
        View surface = getSurfaceView();
        if (surface == null) {
            return;
        }
        int dx, dy;
        if (smooth)
            mDragHelper.smoothSlideViewTo(getSurfaceView(), getPaddingLeft(), getPaddingTop());
        else {
            Rect rect = computeSurfaceLayoutArea(false);
            dx = rect.left - surface.getLeft();
            dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    private Rect computeBottomLayDown(DragEdge dragEdge) {
        int bl = getPaddingLeft(), bt = getPaddingTop();
        int br, bb;
        if (dragEdge == DragEdge.Right) {
            bl = getMeasuredWidth() - mDragDistance;
        } else if (dragEdge == DragEdge.Bottom) {
            bt = getMeasuredHeight() - mDragDistance;
        }
        if (dragEdge == DragEdge.Left || dragEdge == DragEdge.Right) {
            br = bl + mDragDistance;
            bb = bt + getMeasuredHeight();
        } else {
            br = bl + getMeasuredWidth();
            bb = bt + mDragDistance;
        }
        return new Rect(bl, bt, br, bb);
    }

    /**
     * save children's bounds, so they can restore the bound in {@link #onLayout(boolean, int, int, int, int)}
     */
    private void captureChildrenBound() {
        View currentBottomView = getCurrentBottomView();
        if (getOpenStatus() == Status.Close) {
            mViewBoundCache.remove(currentBottomView);
            return;
        }

        View[] views = new View[]{getSurfaceView(), currentBottomView};
        for (View child : views) {
            Rect rect = mViewBoundCache.get(child);
            if (rect == null) {
                rect = new Rect();
                mViewBoundCache.put(child, rect);
            }
            rect.left = child.getLeft();
            rect.top = child.getTop();
            rect.right = child.getRight();
            rect.bottom = child.getBottom();
        }
    }

    private boolean isTouchOnSurface(MotionEvent ev) {
        View surfaceView = getSurfaceView();
        if (surfaceView == null) {
            return false;
        }
        if (hitSurfaceRect == null) {
            hitSurfaceRect = new Rect();
        }
        surfaceView.getHitRect(hitSurfaceRect);
        return hitSurfaceRect.contains((int) ev.getX(), (int) ev.getY());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (insideAdapterView()) {
            if (clickListener == null) {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performAdapterViewItemClick();
                    }
                });
            }
            if (longClickListener == null) {
                setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        performAdapterViewItemLongClick();
                        return true;
                    }
                });
            }
        }
    }

    private boolean insideAdapterView() {
        return getAdapterView() != null;
    }

    private AdapterView getAdapterView() {
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            return (AdapterView) t;
        }
        return null;
    }

    private void performAdapterViewItemClick() {
        if (getOpenStatus() != Status.Close) return;
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            AdapterView view = (AdapterView) t;
            int p = view.getPositionForView(SwipeFrameLayout.this);
            if (p != AdapterView.INVALID_POSITION) {
                view.performItemClick(view.getChildAt(p - view.getFirstVisiblePosition()), p, view.getAdapter().getItemId(p));
            }
        }
    }

    private boolean performAdapterViewItemLongClick() {
        if (getOpenStatus() != Status.Close) return false;
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            AdapterView view = (AdapterView) t;
            int p = view.getPositionForView(SwipeFrameLayout.this);
            if (p == AdapterView.INVALID_POSITION) return false;
            long vId = view.getItemIdAtPosition(p);
            boolean handled = false;
            try {
                Method m = AbsListView.class.getDeclaredMethod("performLongPress", View.class, int.class, long.class);
                m.setAccessible(true);
                handled = (boolean) m.invoke(view, SwipeFrameLayout.this, p, vId);

            } catch (Exception e) {
                e.printStackTrace();

                if (view.getOnItemLongClickListener() != null) {
                    handled = view.getOnItemLongClickListener().onItemLongClick(view, SwipeFrameLayout.this, p, vId);
                }
                if (handled) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
            }
            return handled;
        }
        return false;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == null) return;
        int gravity = Gravity.NO_GRAVITY;
        try {
            gravity = (Integer) params.getClass().getField("gravity").get(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gravity > 0) {
            gravity = GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));

            if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                mDragEdges.put(DragEdge.Left, child);
            }
            if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                mDragEdges.put(DragEdge.Right, child);
            }
            if ((gravity & Gravity.TOP) == Gravity.TOP) {
                mDragEdges.put(DragEdge.Top, child);
            }
            if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                mDragEdges.put(DragEdge.Bottom, child);
            }
        } else {
            for (Map.Entry<DragEdge, View> entry : mDragEdges.entrySet()) {
                if (entry.getValue() == null) {
                    // means used the drag_edge attr, the no gravity child should be use set
                    mDragEdges.put(entry.getKey(), child);
                    break;
                }
            }
        }
        if (child.getParent() == this) {
            return;
        }
        super.addView(child, index, params);
    }

    private List<OnLayout> mOnLayoutListeners;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        updateBottomViews();

        if (mOnLayoutListeners != null) {
            for (int i = 0; i < mOnLayoutListeners.size(); i++) {
                mOnLayoutListeners.get(i).onLayout(this);
            }
        }
    }

    private void updateBottomViews() {
        View currentBottomView = getCurrentBottomView();
        if (currentBottomView != null) {
            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                mDragDistance = currentBottomView.getMeasuredWidth() - UtilDensity.dp2px(getContext(), getCurrentOffset());
            } else {
                mDragDistance = currentBottomView.getMeasuredHeight() - UtilDensity.dp2px(getContext(), getCurrentOffset());
            }
        }

        if (mShowMode == ShowMode.PullOut) {
            layoutPullOut();
        } else if (mShowMode == ShowMode.LayDown) {
            layoutLayDown();
        }

        safeBottomView();
    }

    private float getCurrentOffset() {
        if (mCurrentDragEdge == null) return 0;
        return mEdgeSwipesOffset[mCurrentDragEdge.ordinal()];
    }

    void layoutPullOut() {
        View surfaceView = getSurfaceView();
        Rect surfaceRect = mViewBoundCache.get(surfaceView);
        if (surfaceRect == null) surfaceRect = computeSurfaceLayoutArea(false);
        if (surfaceView != null) {
            surfaceView.layout(surfaceRect.left, surfaceRect.top, surfaceRect.right, surfaceRect.bottom);
            bringChildToFront(surfaceView);
        }
        View currentBottomView = getCurrentBottomView();
        Rect bottomViewRect = mViewBoundCache.get(currentBottomView);
        if (bottomViewRect == null)
            bottomViewRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, surfaceRect);
        if (currentBottomView != null) {
            currentBottomView.layout(bottomViewRect.left, bottomViewRect.top, bottomViewRect.right, bottomViewRect.bottom);
        }
    }

    void layoutLayDown() {
        View surfaceView = getSurfaceView();
        Rect surfaceRect = mViewBoundCache.get(surfaceView);
        if (surfaceRect == null) surfaceRect = computeSurfaceLayoutArea(false);
        if (surfaceView != null) {
            surfaceView.layout(surfaceRect.left, surfaceRect.top, surfaceRect.right, surfaceRect.bottom);
            bringChildToFront(surfaceView);
        }
        View currentBottomView = getCurrentBottomView();
        Rect bottomViewRect = mViewBoundCache.get(currentBottomView);
        if (bottomViewRect == null)
            bottomViewRect = computeBottomLayoutAreaViaSurface(ShowMode.LayDown, surfaceRect);
        if (currentBottomView != null) {
            currentBottomView.layout(bottomViewRect.left, bottomViewRect.top, bottomViewRect.right, bottomViewRect.bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeEnabled()) {
            return false;
        }
        if (mClickToClose && getOpenStatus() == Status.Open && isTouchOnSurface(ev)) {
            return true;
        }
        for (SwipeDenier denier : mSwipeDeniers) {
            if (denier != null && denier.shouldDenySwipe(ev)) {
                return false;
            }
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDragHelper.processTouchEvent(ev);
                mIsBeingDragged = false;
                sX = ev.getRawX();
                sY = ev.getRawY();
                // if the swipe is in middle state(scrolling), should intercept the touch
                if (getOpenStatus() == Status.Middle) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                boolean beforeCheck = mIsBeingDragged;
                checkCanDrag(ev);
                if (mIsBeingDragged) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (!beforeCheck && mIsBeingDragged) {
                    // let children has one chance to catch the touch, and request the swipe not intercept useful when swipeLayout wrap a swipeLayout or other gestural layout
                    return false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mDragHelper.processTouchEvent(ev);
                break;
            default:
                // handle other action, such as ACTION_POINTER_DOWN/UP
                mDragHelper.processTouchEvent(ev);
        }
        return mIsBeingDragged;
    }

    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    private void checkCanDrag(MotionEvent ev) {
        if (mIsBeingDragged) return;
        if (getOpenStatus() == Status.Middle) {
            mIsBeingDragged = true;
            return;
        }
        Status status = getOpenStatus();
        float distanceX = ev.getRawX() - sX;
        float distanceY = ev.getRawY() - sY;
        float angle = Math.abs(distanceY / distanceX);
        angle = (float) Math.toDegrees(Math.atan(angle));
        if (getOpenStatus() == Status.Close) {
            DragEdge dragEdge;
            if (angle < 45) {
                if (distanceX > 0 && isLeftSwipeEnabled()) {
                    dragEdge = DragEdge.Left;
                } else if (distanceX < 0 && isRightSwipeEnabled()) {
                    dragEdge = DragEdge.Right;
                } else return;

            } else {
                if (distanceY > 0 && isTopSwipeEnabled()) {
                    dragEdge = DragEdge.Top;
                } else if (distanceY < 0 && isBottomSwipeEnabled()) {
                    dragEdge = DragEdge.Bottom;
                } else return;
            }
            setCurrentDragEdge(dragEdge);
        }

        boolean doNothing = false;
        if (mCurrentDragEdge == DragEdge.Right) {
            boolean suitable = (status == Status.Open && distanceX > mTouchSlop) || (status == Status.Close && distanceX < -mTouchSlop);
            suitable = suitable || (status == Status.Middle);

            if (angle > 30 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Left) {
            boolean suitable = (status == Status.Open && distanceX < -mTouchSlop) || (status == Status.Close && distanceX > mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle > 30 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Top) {
            boolean suitable = (status == Status.Open && distanceY < -mTouchSlop) || (status == Status.Close && distanceY > mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle < 60 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Bottom) {
            boolean suitable = (status == Status.Open && distanceY > mTouchSlop) || (status == Status.Close && distanceY < -mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle < 60 || !suitable) {
                doNothing = true;
            }
        }
        mIsBeingDragged = !doNothing;
    }

    public boolean isLeftSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Left);
        return bottomView != null && bottomView.getParent() == this && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Left.ordinal()];
    }

    public boolean isRightSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Right);
        return bottomView != null && bottomView.getParent() == this && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Right.ordinal()];
    }

    public boolean isTopSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Top);
        return bottomView != null && bottomView.getParent() == this && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Top.ordinal()];
    }

    public boolean isBottomSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Bottom);
        return bottomView != null && bottomView.getParent() == this && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Bottom.ordinal()];
    }

    private void setCurrentDragEdge(DragEdge dragEdge) {
        mCurrentDragEdge = dragEdge;
        updateBottomViews();
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new SwipeDetector());

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSwipeEnabled()) return super.onTouchEvent(event);

        int action = event.getActionMasked();
        gestureDetector.onTouchEvent(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDragHelper.processTouchEvent(event);
                sX = event.getRawX();
                sY = event.getRawY();

            case MotionEvent.ACTION_MOVE: {
                // the drag state and the direction are already judged at onInterceptTouchEvent
                checkCanDrag(event);
                if (mIsBeingDragged) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mDragHelper.processTouchEvent(event);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mDragHelper.processTouchEvent(event);
                break;

            default:
                // handle other action, such as ACTION_POINTER_DOWN/UP
                mDragHelper.processTouchEvent(event);
        }
        return super.onTouchEvent(event) || mIsBeingDragged || action == MotionEvent.ACTION_DOWN;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    OnClickListener clickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        clickListener = l;
    }

    OnLongClickListener longClickListener;

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        longClickListener = l;
    }

    public void addSwipeListener(SwipeListener l) {
        mSwipeListeners.add(l);
    }

    public void removeSwipeListener(SwipeListener l) {
        mSwipeListeners.remove(l);
    }

    public void removeAllSwipeListener() {
        mSwipeListeners.clear();
    }


    public void addSwipeDenier(SwipeDenier denier) {
        mSwipeDeniers.add(denier);
    }

    public void removeSwipeDenier(SwipeDenier denier) {
        mSwipeDeniers.remove(denier);
    }

    public void removeAllSwipeDeniers() {
        mSwipeDeniers.clear();
    }

    /**
     * bind a view with a specific
     * {@link OnRevealListener}
     *
     * @param childId the view id.
     * @param l       the target{@link OnRevealListener}
     */
    public void addRevealListener(int childId, OnRevealListener l) {
        View child = findViewById(childId);
        if (child == null) {
            throw new IllegalArgumentException("Child does not belong to SwipeListener.");
        }

        if (!mShowEntirely.containsKey(child)) {
            mShowEntirely.put(child, false);
        }
        if (mRevealListeners.get(child) == null)
            mRevealListeners.put(child, new ArrayList<OnRevealListener>());

        mRevealListeners.get(child).add(l);
    }

    /**
     * bind multiple views with an
     * {@link OnRevealListener}.
     *
     * @param childIds the view id.
     * @param l        the {@link OnRevealListener}
     */
    public void addRevealListener(int[] childIds, OnRevealListener l) {
        for (int i : childIds)
            addRevealListener(i, l);
    }

    public void removeRevealListener(int childId, OnRevealListener l) {
        View child = findViewById(childId);

        if (child == null) return;

        mShowEntirely.remove(child);
        if (mRevealListeners.containsKey(child)) mRevealListeners.get(child).remove(l);
    }

    public void removeAllRevealListeners(int childId) {
        View child = findViewById(childId);
        if (child != null) {
            mRevealListeners.remove(child);
            mShowEntirely.remove(child);
        }
    }

    public void addOnLayoutListener(OnLayout l) {
        if (mOnLayoutListeners == null) mOnLayoutListeners = new ArrayList<>();
        mOnLayoutListeners.add(l);
    }

    public void removeOnLayoutListener(OnLayout l) {
        if (mOnLayoutListeners != null) mOnLayoutListeners.remove(l);
    }

    public void clearDragEdge() {
        mDragEdges.clear();
    }

    public void setDrag(DragEdge dragEdge, int childId) {
        clearDragEdge();
        addDrag(dragEdge, childId);
    }

    public void setDrag(DragEdge dragEdge, View child) {
        clearDragEdge();
        addDrag(dragEdge, child);
    }

    public void addDrag(DragEdge dragEdge, int childId) {
        addDrag(dragEdge, findViewById(childId), null);
    }

    public void addDrag(DragEdge dragEdge, View child) {
        addDrag(dragEdge, child, null);
    }

    public void addDrag(DragEdge dragEdge, View child, ViewGroup.LayoutParams params) {
        if (child == null) return;

        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        int gravity = -1;
        switch (dragEdge) {
            case Left:
                gravity = Gravity.LEFT;
                break;
            case Right:
                gravity = Gravity.RIGHT;
                break;
            case Top:
                gravity = Gravity.TOP;
                break;
            case Bottom:
                gravity = Gravity.BOTTOM;
                break;
        }
        if (params instanceof LayoutParams) {
            ((LayoutParams) params).gravity = gravity;
        }
        addView(child, 0, params);
    }

    public boolean isClickToClose() {
        return mClickToClose;
    }

    public void setSwipeEnabled(boolean enabled) {
        mSwipeEnabled = enabled;
    }

    public void setLeftSwipeEnabled(boolean leftSwipeEnabled) {
        this.mSwipesEnabled[DragEdge.Left.ordinal()] = leftSwipeEnabled;
    }

    public void setRightSwipeEnabled(boolean rightSwipeEnabled) {
        this.mSwipesEnabled[DragEdge.Right.ordinal()] = rightSwipeEnabled;
    }

    public void setTopSwipeEnabled(boolean topSwipeEnabled) {
        this.mSwipesEnabled[DragEdge.Top.ordinal()] = topSwipeEnabled;
    }

    public void setBottomSwipeEnabled(boolean bottomSwipeEnabled) {
        this.mSwipesEnabled[DragEdge.Bottom.ordinal()] = bottomSwipeEnabled;
    }

    /**
     * set the drag distance, it will force set the bottom view's width or height via this value.
     *
     * @param max max distance in dp unit
     */
    public void setDragDistance(int max) {
        if (max < 0) max = 0;
        mDragDistance = UtilDensity.dp2px(getContext(), max);
        requestLayout();
    }

    /**
     * There are 2 diffirent show mode.
     * {@link ShowMode}.PullOut and
     * {@link ShowMode}.LayDown.
     */
    public void setShowMode(ShowMode mode) {
        mShowMode = mode;
        requestLayout();
    }

    public int getDragDistance() {
        return mDragDistance;
    }

    public void open(DragEdge edge) {
        setCurrentDragEdge(edge);
        open(true, true);
    }

    public void open(boolean smooth, DragEdge edge) {
        setCurrentDragEdge(edge);
        open(smooth, true);
    }

    public void open(boolean smooth, boolean notify, DragEdge edge) {
        setCurrentDragEdge(edge);
        open(smooth, notify);
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean smooth) {
        if (getOpenStatus() == Status.Open) {
            close(smooth);
        } else if (getOpenStatus() == Status.Close) {
            open(smooth);
        }
    }

    public void setOnDoubleClickListener(DoubleClickListener doubleClickListener) {
        mDoubleClickListener = doubleClickListener;
    }

    public void onViewRemoved(View child) {
        for (Map.Entry<DragEdge, View> entry : new HashMap<>(mDragEdges).entrySet()) {
            if (entry.getValue() == child) {
                mDragEdges.remove(entry.getKey());
            }
        }
    }

    public Map<DragEdge, View> getDragEdgeMap() {
        return mDragEdges;
    }

    /**
     * Deprecated, use {@link #setDrag(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdge(DragEdge dragEdge) {
        clearDragEdge();
        if (getChildCount() >= 2) {
            mDragEdges.put(dragEdge, getChildAt(getChildCount() - 2));
        }
        setCurrentDragEdge(dragEdge);
    }

    /**
     * Deprecated, use {@link #getDragEdgeMap()}
     */
    @Deprecated
    public List<DragEdge> getDragEdges() {
        return new ArrayList<>(mDragEdges.keySet());
    }

    /**
     * Deprecated, use {@link #setDrag(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdges(List<DragEdge> dragEdges) {
        clearDragEdge();
        for (int i = 0, size = Math.min(dragEdges.size(), getChildCount() - 1); i < size; i++) {
            DragEdge dragEdge = dragEdges.get(i);
            mDragEdges.put(dragEdge, getChildAt(i));
        }
        if (dragEdges.size() == 0 || dragEdges.contains(DefaultDragEdge)) {
            setCurrentDragEdge(DefaultDragEdge);
        } else {
            setCurrentDragEdge(dragEdges.get(0));
        }
    }

    /**
     * Deprecated, use {@link #addDrag(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdges(DragEdge... mDragEdges) {
        clearDragEdge();
        setDragEdges(Arrays.asList(mDragEdges));
    }

    /**
     * Deprecated, use {@link #addDrag(DragEdge, View)} When using multiple drag edges it's a good idea to pass the ids of the views that you're using for the left, right, top bottom views (-1 if you're not using a particular view)
     */
    @Deprecated
    public void setBottomViewIds(int leftId, int rightId, int topId, int bottomId) {
        addDrag(DragEdge.Left, findViewById(leftId));
        addDrag(DragEdge.Right, findViewById(rightId));
        addDrag(DragEdge.Top, findViewById(topId));
        addDrag(DragEdge.Bottom, findViewById(bottomId));
    }

    /**
     * 手势滑动触发器
     */
    class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mClickToClose && isTouchOnSurface(e)) {
                close();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mDoubleClickListener != null) {
                View target;
                View bottom = getCurrentBottomView();
                View surface = getSurfaceView();
                if (bottom != null && e.getX() > bottom.getLeft() && e.getX() < bottom.getRight() && e.getY() > bottom.getTop() && e.getY() < bottom.getBottom()) {
                    target = bottom;
                } else {
                    target = surface;
                }
                mDoubleClickListener.onDoubleClick(SwipeFrameLayout.this, target == surface);
            }
            return true;
        }
    }

    public interface SwipeListener {
        void onStartOpen(SwipeFrameLayout layout);

        void onOpen(SwipeFrameLayout layout);

        void onStartClose(SwipeFrameLayout layout);

        void onClose(SwipeFrameLayout layout);

        void onUpdate(SwipeFrameLayout layout, int leftOffset, int topOffset);

        void onHandRelease(SwipeFrameLayout layout, float xvel, float yvel);
    }

    public interface SwipeDenier {
        /*
         * Called in onInterceptTouchEvent Determines if this swipe event should be denied Implement this interface if you are using views with swipe gestures As a child of SwipeFrameLayout
         *
         * @return true deny false allow
         */
        boolean shouldDenySwipe(MotionEvent ev);
    }

    public interface OnRevealListener {
        void onReveal(View child, DragEdge edge, float fraction, int distance);
    }

    /**
     * {@link OnLayoutChangeListener} added in API 11. I need to support it from API 8.
     */
    public interface OnLayout {
        void onLayout(SwipeFrameLayout v);
    }

    public interface DoubleClickListener {
        void onDoubleClick(SwipeFrameLayout layout, boolean surface);
    }

    public interface SwipeItemMangerInterface {

        void openItem(int position);

        void closeItem(int position);

        void closeAllExcept(SwipeFrameLayout layout);

        void closeAllItems();

        List<Integer> getOpenItems();

        List<SwipeFrameLayout> getOpenLayouts();

        void removeShownLayouts(SwipeFrameLayout layout);

        boolean isOpen(int position);

        Attributes.Mode getMode();

        void setMode(Attributes.Mode mode);
    }

    public interface SwipeAdapterInterface {

        int getSwipeLayoutResourceId(int position);

        void notifyDatasetChanged();
    }

    public static class Attributes {

        public enum Mode {
            Single, Multiple
        }
    }

    /**
     * adapter
     */
    public static abstract class BaseSwipeAdapter extends BaseAdapter implements SwipeItemMangerInterface, SwipeAdapterInterface {

        protected SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

        /**
         * return the {@link SwipeFrameLayout} resource id, int the view item.
         */
        public abstract int getSwipeLayoutResourceId(int position);

        /**
         * generate a new view item. Never bind SwipeListener or fill values here, every item has a chance to fill value or bind listeners in fillValues. to fill it in {@code fillValues} method.
         */
        public abstract View generateView(int position, ViewGroup parent);

        /**
         * fill values or bind listeners to the view.
         */
        public abstract void fillValues(int position, View convertView);

        @Override
        public void notifyDatasetChanged() {
            super.notifyDataSetChanged();
        }


        @Override
        public final View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = generateView(position, parent);
            }
            mItemManger.bind(v, position);
            fillValues(position, v);
            return v;
        }

        @Override
        public void openItem(int position) {
            mItemManger.openItem(position);
        }

        @Override
        public void closeItem(int position) {
            mItemManger.closeItem(position);
        }

        @Override
        public void closeAllExcept(SwipeFrameLayout layout) {
            mItemManger.closeAllExcept(layout);
        }

        @Override
        public void closeAllItems() {
            mItemManger.closeAllItems();
        }

        @Override
        public List<Integer> getOpenItems() {
            return mItemManger.getOpenItems();
        }

        @Override
        public List<SwipeFrameLayout> getOpenLayouts() {
            return mItemManger.getOpenLayouts();
        }

        @Override
        public void removeShownLayouts(SwipeFrameLayout layout) {
            mItemManger.removeShownLayouts(layout);
        }

        @Override
        public boolean isOpen(int position) {
            return mItemManger.isOpen(position);
        }

        @Override
        public Attributes.Mode getMode() {
            return mItemManger.getMode();
        }

        @Override
        public void setMode(Attributes.Mode mode) {
            mItemManger.setMode(mode);
        }
    }

    /**
     * adapter
     */
    public static abstract class RecyclerSwipeAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements SwipeItemMangerInterface, SwipeAdapterInterface {

        public SwipeItemMangerImpl mItemManger = new SwipeItemMangerImpl(this);

        @Override
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        @Override
        public abstract void onBindViewHolder(VH viewHolder, final int position);

        @Override
        public void notifyDatasetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public void openItem(int position) {
            mItemManger.openItem(position);
        }

        @Override
        public void closeItem(int position) {
            mItemManger.closeItem(position);
        }

        @Override
        public void closeAllExcept(SwipeFrameLayout layout) {
            mItemManger.closeAllExcept(layout);
        }

        @Override
        public void closeAllItems() {
            mItemManger.closeAllItems();
        }

        @Override
        public List<Integer> getOpenItems() {
            return mItemManger.getOpenItems();
        }

        @Override
        public List<SwipeFrameLayout> getOpenLayouts() {
            return mItemManger.getOpenLayouts();
        }

        @Override
        public void removeShownLayouts(SwipeFrameLayout layout) {
            mItemManger.removeShownLayouts(layout);
        }

        @Override
        public boolean isOpen(int position) {
            return mItemManger.isOpen(position);
        }

        @Override
        public Attributes.Mode getMode() {
            return mItemManger.getMode();
        }

        @Override
        public void setMode(Attributes.Mode mode) {
            mItemManger.setMode(mode);
        }
    }


    /**
     * SwipeItemMangerImpl is a helper class to help all the adapters to maintain open status.
     */
    public static class SwipeItemMangerImpl implements SwipeItemMangerInterface {

        private Attributes.Mode mode = Attributes.Mode.Single;
        public final int INVALID_POSITION = -1;

        protected int mOpenPosition = INVALID_POSITION;

        protected Set<Integer> mOpenPositions = new HashSet<>();
        protected Set<SwipeFrameLayout> mShownLayouts = new HashSet<>();

        protected SwipeAdapterInterface swipeAdapterInterface;

        public SwipeItemMangerImpl(SwipeAdapterInterface swipeAdapterInterface) {
            if (swipeAdapterInterface == null)
                throw new IllegalArgumentException("SwipeAdapterInterface can not be null");

            this.swipeAdapterInterface = swipeAdapterInterface;
        }

        public Attributes.Mode getMode() {
            return mode;
        }

        public void setMode(Attributes.Mode mode) {
            this.mode = mode;
            mOpenPositions.clear();
            mShownLayouts.clear();
            mOpenPosition = INVALID_POSITION;
        }

        public void bind(View view, int position) {
            int resId = swipeAdapterInterface.getSwipeLayoutResourceId(position);
            SwipeFrameLayout swipeLayout = (SwipeFrameLayout) view.findViewById(resId);
            if (swipeLayout == null)
                throw new IllegalStateException("can not find SwipeLayout in target view");

            if (swipeLayout.getTag(resId) == null) {
                OnLayoutListener onLayoutListener = new OnLayoutListener(position);
                SwipeMemory swipeMemory = new SwipeMemory(position);
                swipeLayout.addSwipeListener(swipeMemory);
                swipeLayout.addOnLayoutListener(onLayoutListener);
                swipeLayout.setTag(resId, new ValueBox(position, swipeMemory, onLayoutListener));
                mShownLayouts.add(swipeLayout);
            } else {
                ValueBox valueBox = (ValueBox) swipeLayout.getTag(resId);
                valueBox.swipeMemory.setPosition(position);
                valueBox.onLayoutListener.setPosition(position);
                valueBox.position = position;
            }
        }

        @Override
        public void openItem(int position) {
            if (mode == Attributes.Mode.Multiple) {
                if (!mOpenPositions.contains(position))
                    mOpenPositions.add(position);
            } else {
                mOpenPosition = position;
            }
            swipeAdapterInterface.notifyDatasetChanged();
        }

        @Override
        public void closeItem(int position) {
            if (mode == Attributes.Mode.Multiple) {
                mOpenPositions.remove(position);
            } else {
                if (mOpenPosition == position)
                    mOpenPosition = INVALID_POSITION;
            }
            swipeAdapterInterface.notifyDatasetChanged();
        }

        @Override
        public void closeAllExcept(SwipeFrameLayout layout) {
            for (SwipeFrameLayout s : mShownLayouts) {
                if (s != layout)
                    s.close();
            }
        }

        @Override
        public void closeAllItems() {
            if (mode == Attributes.Mode.Multiple) {
                mOpenPositions.clear();
            } else {
                mOpenPosition = INVALID_POSITION;
            }
            for (SwipeFrameLayout s : mShownLayouts) {
                s.close();
            }
        }

        @Override
        public void removeShownLayouts(SwipeFrameLayout layout) {
            mShownLayouts.remove(layout);
        }

        @Override
        public List<Integer> getOpenItems() {
            if (mode == Attributes.Mode.Multiple) {
                return new ArrayList<>(mOpenPositions);
            } else {
                return Collections.singletonList(mOpenPosition);
            }
        }

        @Override
        public List<SwipeFrameLayout> getOpenLayouts() {
            return new ArrayList<>(mShownLayouts);
        }

        @Override
        public boolean isOpen(int position) {
            if (mode == Attributes.Mode.Multiple) {
                return mOpenPositions.contains(position);
            } else {
                return mOpenPosition == position;
            }
        }

        class ValueBox {
            OnLayoutListener onLayoutListener;
            SwipeMemory swipeMemory;
            int position;

            ValueBox(int position, SwipeMemory swipeMemory, OnLayoutListener onLayoutListener) {
                this.swipeMemory = swipeMemory;
                this.onLayoutListener = onLayoutListener;
                this.position = position;
            }
        }

        class OnLayoutListener implements SwipeFrameLayout.OnLayout {

            private int position;

            OnLayoutListener(int position) {
                this.position = position;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            @Override
            public void onLayout(SwipeFrameLayout v) {
                if (isOpen(position)) {
                    v.open(false, false);
                } else {
                    v.close(false, false);
                }
            }
        }

        class SwipeMemory extends SimpleSwipeListener {

            private int position;

            SwipeMemory(int position) {
                this.position = position;
            }

            @Override
            public void onClose(SwipeFrameLayout layout) {
                if (mode == Attributes.Mode.Multiple) {
                    mOpenPositions.remove(position);
                } else {
                    mOpenPosition = INVALID_POSITION;
                }
            }

            @Override
            public void onStartOpen(SwipeFrameLayout layout) {
                if (mode == Attributes.Mode.Single) {
                    closeAllExcept(layout);
                }
            }

            @Override
            public void onOpen(SwipeFrameLayout layout) {
                if (mode == Attributes.Mode.Multiple)
                    mOpenPositions.add(position);
                else {
                    closeAllExcept(layout);
                    mOpenPosition = position;
                }
            }

            public void setPosition(int position) {
                this.position = position;
            }
        }
    }

    /**
     * SwipeListener
     */
    public static class SimpleSwipeListener implements SwipeFrameLayout.SwipeListener {

        @Override
        public void onStartOpen(SwipeFrameLayout layout) {
        }

        @Override
        public void onOpen(SwipeFrameLayout layout) {
        }

        @Override
        public void onStartClose(SwipeFrameLayout layout) {
        }

        @Override
        public void onClose(SwipeFrameLayout layout) {
        }

        @Override
        public void onUpdate(SwipeFrameLayout layout, int leftOffset, int topOffset) {
        }

        @Override
        public void onHandRelease(SwipeFrameLayout layout, float xvel, float yvel) {
        }
    }
}

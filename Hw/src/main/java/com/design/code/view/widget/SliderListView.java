package com.design.code.view.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.design.code.util.UtilDensity;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动删除
 * Created by Ignacey 2016/1/22.
 */
public class SliderListView extends ListView {

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;

    private float mDownX, mDownY;
    private int mTouchState, mTouchPosition;
    private SliderRoot mTouchView;
    private OnSliderListener mOnSliderListener;

    private SliderCreator mMenuCreator;
    private OnSliderItemClickListener mOnSliderItemClickListener;
    private Interpolator mOpenInterpolator;
    private Interpolator mCloseInterpolator;

    public SliderListView(Context context) {
        super(context);
        init();
    }

    public SliderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SliderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        MAX_X = UtilDensity.dp2px(getContext(), MAX_X);
        MAX_Y = UtilDensity.dp2px(getContext(), MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SliderListAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SliderMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onMenuItemClick(SliderChildView view, SliderMenu menu,
                                        int index) {
                if (mOnSliderItemClickListener != null) {
                    mOnSliderItemClickListener.onItemClick(
                            view.getPosition(), menu, index);
                }
                if (mTouchView != null) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null && mTouchView.isOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipe(ev);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    return super.onTouchEvent(ev);
                }
                if (view instanceof SliderRoot) {
                    mTouchView = (SliderRoot) view;
                }
                if (mTouchView != null) {
                    mTouchView.onSwipe(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSliderListener != null) {
                            mOnSliderListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                        if (!mTouchView.isOpen()) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSliderListener != null) {
                        mOnSliderListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SliderRoot) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SliderRoot) view;
                mTouchView.smoothOpenMenu();
            }
        }
    }

    public void setMenuCreator(SliderCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(
            OnSliderItemClickListener onSliderItemClickListener) {
        this.mOnSliderItemClickListener = onSliderItemClickListener;
    }

    public void setOnSwipeListener(OnSliderListener onSliderListener) {
        this.mOnSliderListener = onSliderListener;
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    public interface SliderCreator {
        void create(SliderMenu menu);
    }

    public interface OnSliderItemClickListener {
        void onItemClick(int position, SliderMenu menu, int index);
    }

    public interface OnSliderListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }

    public class SliderListAdapter implements WrapperListAdapter, SliderChildView.OnSliderMenuItemClickListener {

        private ListAdapter mAdapter;
        private Context mContext;
        private OnSliderItemClickListener onSliderItemClickListener;

        public SliderListAdapter(Context context, ListAdapter adapter) {
            mAdapter = adapter;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return mAdapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SliderRoot layout;
            if (convertView == null) {
                View contentView = mAdapter.getView(position, convertView, parent);
                SliderMenu menu = new SliderMenu(mContext);
                menu.setViewType(mAdapter.getItemViewType(position));
                createMenu(menu);
                SliderChildView menuView = new SliderChildView(menu, (SliderListView) parent);
                menuView.setOnSwipeItemClickListener(this);
                SliderListView listView = (SliderListView) parent;
                layout = new SliderRoot(contentView, menuView, listView.getCloseInterpolator(), listView.getOpenInterpolator());
                layout.setPosition(position);
            } else {
                layout = (SliderRoot) convertView;
                layout.closeMenu();
                layout.setPosition(position);
                View view = mAdapter.getView(position, layout.getContentView(), parent);
            }
            return layout;
        }

        public void createMenu(SliderMenu menu) {
            // Test Code
            SliderMenuItem item = new SliderMenuItem(mContext);
            item.setTitle("Item 1");
            item.setBackground(new ColorDrawable(Color.GRAY));
            item.setWidth(300);
            menu.addMenuItem(item);

            item = new SliderMenuItem(mContext);
            item.setTitle("Item 2");
            item.setBackground(new ColorDrawable(Color.RED));
            item.setWidth(300);
            menu.addMenuItem(item);
        }

        @Override
        public void onMenuItemClick(SliderChildView view, SliderMenu menu, int index) {
            if (onSliderItemClickListener != null) {
                onSliderItemClickListener.onItemClick(view.getPosition(), menu, index);
            }
        }

        public void setOnSliderItemClickListener(
                OnSliderItemClickListener onSliderItemClickListener) {
            this.onSliderItemClickListener = onSliderItemClickListener;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return mAdapter.areAllItemsEnabled();
        }

        @Override
        public boolean isEnabled(int position) {
            return mAdapter.isEnabled(position);
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        @Override
        public int getItemViewType(int position) {
            return mAdapter.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return mAdapter.getViewTypeCount();
        }

        @Override
        public boolean isEmpty() {
            return mAdapter.isEmpty();
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }
    }

    public static class SliderMenu {

        private Context mContext;
        private List<SliderMenuItem> mItems;
        private int mViewType;

        public SliderMenu(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        public Context getContext() {
            return mContext;
        }

        public void addMenuItem(SliderMenuItem item) {
            mItems.add(item);
        }

        public void removeMenuItem(SliderMenuItem item) {
            mItems.remove(item);
        }

        public List<SliderMenuItem> getMenuItems() {
            return mItems;
        }

        public SliderMenuItem getMenuItem(int index) {
            return mItems.get(index);
        }

        public int getViewType() {
            return mViewType;
        }

        public void setViewType(int viewType) {
            this.mViewType = viewType;
        }
    }

    public static class SliderMenuItem {

        private int id;
        private Context mContext;
        private String title;
        private Drawable icon;
        private Drawable background;
        private int width;
        private int titleColor;
        private int titleSize;

        public SliderMenuItem(Context context) {
            mContext = context;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTitleColor() {
            return titleColor;
        }

        public int getTitleSize() {
            return titleSize;
        }

        public void setTitleSize(int titleSize) {
            this.titleSize = titleSize;
        }

        public void setTitleColor(int titleColor) {
            this.titleColor = titleColor;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setTitle(int resId) {
            setTitle(mContext.getString(resId));
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public void setIcon(int resId) {
            this.icon = mContext.getResources().getDrawable(resId);
        }

        public Drawable getBackground() {
            return background;
        }

        public void setBackground(Drawable background) {
            this.background = background;
        }

        public void setBackground(int resId) {
            this.background = mContext.getResources().getDrawable(resId);
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }

    public class SliderRoot extends FrameLayout {

        private static final int CONTENT_VIEW_ID = 1;
        private static final int MENU_VIEW_ID = 2;

        private static final int STATE_CLOSE = 0;
        private static final int STATE_OPEN = 1;

        private View mContentView;
        private SliderChildView mMenuView;
        private int mDownX;
        private int state = STATE_CLOSE;
        private GestureDetectorCompat mGestureDetector;
        private GestureDetector.OnGestureListener mGestureListener;
        private boolean isFling;
        private int MIN_FLING = UtilDensity.dp2px(getContext(), 15);
        private int MAX_VELOCITYX = -(UtilDensity.dp2px(getContext(), 500));
        private ScrollerCompat mOpenScroller;
        private ScrollerCompat mCloseScroller;
        private int mBaseX;
        private int position;
        private Interpolator mCloseInterpolator;
        private Interpolator mOpenInterpolator;

        public SliderRoot(View contentView, SliderChildView menuView) {
            this(contentView, menuView, null, null);
        }

        public SliderRoot(View contentView, SliderChildView menuView, Interpolator closeInterpolator, Interpolator openInterpolator) {
            super(contentView.getContext());
            mCloseInterpolator = closeInterpolator;
            mOpenInterpolator = openInterpolator;
            mContentView = contentView;
            mMenuView = menuView;
            mMenuView.setLayout(this);
            init();
        }

        private SliderRoot(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private SliderRoot(Context context) {
            super(context);
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
            mMenuView.setPosition(position);
        }

        private void init() {
            setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            mGestureListener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    isFling = false;
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if ((e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
                        isFling = true;
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            };
            mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);

            if (mCloseInterpolator != null) {
                mCloseScroller = ScrollerCompat.create(getContext(), mCloseInterpolator);
            } else {
                mCloseScroller = ScrollerCompat.create(getContext());
            }
            if (mOpenInterpolator != null) {
                mOpenScroller = ScrollerCompat.create(getContext(), mOpenInterpolator);
            } else {
                mOpenScroller = ScrollerCompat.create(getContext());
            }

            LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            mContentView.setLayoutParams(contentParams);
            if (mContentView.getId() < 1) {
                mContentView.setId(CONTENT_VIEW_ID);
            }

            mMenuView.setId(MENU_VIEW_ID);
            mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            addView(mContentView);
            addView(mMenuView);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        public boolean onSwipe(MotionEvent event) {
            requestDisallowInterceptTouchEvent(true);

            mGestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) event.getX();
                    isFling = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dis = (int) (mDownX - event.getX());
                    if (state == STATE_OPEN) {
                        dis += mMenuView.getWidth();
                    }
                    swipe(dis);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isFling || (mDownX - event.getX()) > (mMenuView.getWidth() / 2)) {
                        // open
                        smoothOpenMenu();
                    } else {
                        // close
                        smoothCloseMenu();
                        return false;
                    }
                    break;
            }
            return true;
        }

        public boolean isOpen() {
            return state == STATE_OPEN;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        private void swipe(int dis) {
            if (dis > mMenuView.getWidth()) {
                dis = mMenuView.getWidth();
            }
            if (dis < 0) {
                dis = 0;
            }
            mContentView.layout(-dis, mContentView.getTop(), mContentView.getWidth() - dis, getMeasuredHeight());
            mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(), mContentView.getWidth() + mMenuView.getWidth() - dis, mMenuView.getBottom());
        }

        @Override
        public void computeScroll() {
            if (state == STATE_OPEN) {
                if (mOpenScroller.computeScrollOffset()) {
                    swipe(mOpenScroller.getCurrX());
                    postInvalidate();
                }
            } else {
                if (mCloseScroller.computeScrollOffset()) {
                    swipe(mBaseX - mCloseScroller.getCurrX());
                    postInvalidate();
                }
            }
        }

        public void smoothCloseMenu() {
            state = STATE_CLOSE;
            mBaseX = -mContentView.getLeft();
            mCloseScroller.startScroll(0, 0, mBaseX, 0, 350);
            postInvalidate();
        }

        public void smoothOpenMenu() {
            state = STATE_OPEN;
            mOpenScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
            postInvalidate();
        }

        public void closeMenu() {
            if (mCloseScroller.computeScrollOffset()) {
                mCloseScroller.abortAnimation();
            }
            if (state == STATE_OPEN) {
                state = STATE_CLOSE;
                swipe(0);
            }
        }

        public void openMenu() {
            if (state == STATE_CLOSE) {
                state = STATE_OPEN;
                swipe(mMenuView.getWidth());
            }
        }

        public View getContentView() {
            return mContentView;
        }

        public SliderChildView getMenuView() {
            return mMenuView;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            mContentView.layout(0, 0, getMeasuredWidth(), mContentView.getMeasuredHeight());
            mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        }

        public void setMenuHeight(int measuredHeight) {
            LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
            if (params.height != measuredHeight) {
                params.height = measuredHeight;
                mMenuView.setLayoutParams(mMenuView.getLayoutParams());
            }
        }
    }

    public static class SliderChildView extends LinearLayout implements OnClickListener {

        private SliderListView mListView;
        private SliderRoot mLayout;
        private SliderMenu mMenu;
        private OnSliderMenuItemClickListener onItemClickListener;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public SliderChildView(SliderMenu menu, SliderListView listView) {
            super(menu.getContext());
            mListView = listView;
            mMenu = menu;
            List<SliderMenuItem> items = menu.getMenuItems();
            int id = 0;
            for (SliderMenuItem item : items) {
                addItem(item, id++);
            }
        }

        private void addItem(SliderMenuItem item, int id) {
            LayoutParams params = new LayoutParams(item.getWidth(), LayoutParams.MATCH_PARENT);
            LinearLayout parent = new LinearLayout(getContext());
            parent.setId(id);
            parent.setGravity(Gravity.CENTER);
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setLayoutParams(params);
            parent.setBackgroundDrawable(item.getBackground());
            parent.setOnClickListener(this);
            addView(parent);

            if (item.getIcon() != null) {
                parent.addView(createIcon(item));
            }
            if (!TextUtils.isEmpty(item.getTitle())) {
                parent.addView(createTitle(item));
            }

        }

        private ImageView createIcon(SliderMenuItem item) {
            ImageView iv = new ImageView(getContext());
            iv.setImageDrawable(item.getIcon());
            return iv;
        }

        private TextView createTitle(SliderMenuItem item) {
            TextView tv = new TextView(getContext());
            tv.setText(item.getTitle());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(item.getTitleSize());
            tv.setTextColor(item.getTitleColor());
            return tv;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && mLayout.isOpen()) {
                onItemClickListener.onMenuItemClick(this, mMenu, v.getId());
            }
        }

        public OnSliderMenuItemClickListener getOnSwipeItemClickListener() {
            return onItemClickListener;
        }

        public void setOnSwipeItemClickListener(OnSliderMenuItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setLayout(SliderRoot mLayout) {
            this.mLayout = mLayout;
        }

        public interface OnSliderMenuItemClickListener {
            void onMenuItemClick(SliderChildView view, SliderMenu menu, int index);
        }
    }
}

package com.design.code.view.dialog.alert;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.design.code.R;
import com.design.code.util.UtilDensity;

import java.util.List;

/**
 * Created by Design 2016/3/17.
 */
public class DropDownLayout extends LinearLayout {

    // 顶部菜单布局
    private LinearLayout tabMenuView;
    // 底部容器，包含popupMenuViews，maskView
    private FrameLayout containerView;
    // 弹出菜单父布局
    private FrameLayout popupMenuViews;
    // 遮罩半透明View，点击可关闭DropDownMenu
    private View maskView;
    // tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    // 分割线颜色
    private int dividerColor = 0xffcccccc;
    // tab选中颜色
    private int textSelectedColor = 0xff890c85;
    // tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    // 遮罩颜色
    private int maskColor = 0x88888888;

    // tab字体大小
    private int menuTextSize = 14;
    // tab选中图标
    private int menuSelectedIcon;
    // tab未选中图标
    private int menuUnselectedIcon;


    public DropDownLayout(Context context) {
        super(context, null);
    }

    public DropDownLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        // 为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownLayout);
        underlineColor = a.getColor(R.styleable.DropDownLayout_DropLineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropDownLayout_DropDividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropDownLayout_DropTextSelectedColor, textSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownLayout_DropTextUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownLayout_DropBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownLayout_DropMaskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownLayout_DropMenuTextSize, menuTextSize);
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownLayout_DropMenuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownLayout_DropMenuUnselectedIcon, menuUnselectedIcon);
        a.recycle();

        // 初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        // 为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UtilDensity.dp2px(getContext(), 1.0f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        // 初始化containerView并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);
    }

    /**
     * 初始化DropDownLayout
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @NonNull View contentView) {
        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException(tabTexts.size()+"ad"+popupViews.size()+"params not match, tabTexts.size() should be equal popupViews.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        containerView.addView(contentView, 0);

        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        containerView.addView(maskView, 1);
        maskView.setVisibility(GONE);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 2);

        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }
    }

    private void addTab(@NonNull List<String> tabTexts, int i) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tab.setTextColor(textUnselectedColor);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
        tab.setText(tabTexts.get(i));
        tab.setPadding(UtilDensity.dp2px(getContext(), 5), UtilDensity.dp2px(getContext(), 12), UtilDensity.dp2px(getContext(), 5), UtilDensity.dp2px(getContext(), 12));
        // 添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
        // 添加分割线
        if (i < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(UtilDensity.dp2px(getContext(), 0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }

    /**
     * 切换菜单
     */
    private void switchMenu(View target) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_list_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_list_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_mask_out));
            current_tab_position = -1;
        }
    }

    /**
     * 改变tab文字
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
        }
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }
}

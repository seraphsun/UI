package com.design.code;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.design.code.fragment.MainFragment_Num1_1;
import com.design.code.fragment.MainFragment_Num2_2;
import com.design.code.fragment.MainFragment_Num3;
import com.design.code.fragment.MainFragment_Num4;
import com.design.code.view.MainTabHost;
import com.design.code.view.dialog.alert.IosDialogAlert;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String[] mTitle = {"首页", "刷新", "主题", "个人中心"};

    private int[] mIconNormal = {
            R.mipmap.main_icon_tabhost_1_normal,
            R.mipmap.main_icon_tabhost_2_normal,
            R.mipmap.main_icon_tabhost_3_normal,
            R.mipmap.main_icon_tabhost_4_normal,
    };

    private int[] mIconSelect = {
            R.mipmap.main_icon_tabhost_1_selected,
            R.mipmap.main_icon_tabhost_2_selected,
            R.mipmap.main_icon_tabhost_3_selected,
            R.mipmap.main_icon_tabhost_4_selected,
    };

    private Map<Integer, Fragment> mFragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.id_view_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        MainTabHost tabHost = (MainTabHost) findViewById(R.id.id_tab);
        tabHost.setViewPager(viewPager);
    }

    private Fragment getFragment(int position) {
        Fragment fragment = mFragmentMap.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new MainFragment_Num1_1();
                    break;
                case 1:
                    fragment = new MainFragment_Num2_2();
                    break;
                case 2:
                    fragment = new MainFragment_Num3();
                    break;
                case 3:
                    fragment = new MainFragment_Num4();
                    break;
            }
            mFragmentMap.put(position, fragment);
        }
        return fragment;
    }

    /**
     * 需要实现OnItemIconTextSelectListener接口
     */
    class PageAdapter extends FragmentPagerAdapter implements MainTabHost.OnItemIconTextSelectListener {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int[] onIconSelect(int position) {
            int icon[] = new int[2];
            icon[0] = mIconSelect[position];
            icon[1] = mIconNormal[position];
            return icon;
        }

        @Override
        public String onTextSelect(int position) {
            return mTitle[position];
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            new IosDialogAlert(this).builder()
                    .setTitle("警告")
                    .setMsg("What are you 弄啥类 ？")
                    .setPositiveButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .show();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}


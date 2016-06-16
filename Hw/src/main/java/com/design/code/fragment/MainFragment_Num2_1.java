package com.design.code.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.design.code.R;
import com.design.code.view.dialog.progress.LoadingDialog;
import com.design.code.view.dialog.progress.NumProgressBar;




/**
 * Created by Ignacey 2016/3/3.
 */
public class MainFragment_Num2_1 extends Fragment implements NumProgressBar.OnProgressBarListener {

//    private Timer timer;
    private NumProgressBar bnp;
//
//    private DropDownLayout mDropDownLayout;
//    private String headers[] = {"城市", "年龄", "性别", "星座"};
//
//    private List<View> popupViews = new ArrayList<>();
//
//    private DropDownGirdAdapter cityAdapter;
//    private DropDownListAdapter ageAdapter;
//    private DropDownListAdapter sexAdapter;
//    private DropDownCustomAdapter dropDownCustomAdapter;
//
//    private String cities[] = {"不限", "河北", "邯郸", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
//    private String ages[] = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
//    private String sexes[] = {"不限", "男", "女"};
//    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
//    private int constellationPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_num_2_1, null);

        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void init() {
//        bnp = (NumProgressBar) getActivity().findViewById(R.id.num_progress_bar);
//        bnp.setOnProgressBarListener(this);
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        bnp.incrementProgressBy(1);
//                    }
//                });
//            }
//        }, 5000, 100);

//        LoadingDialog dialog = new LoadingDialog(getActivity());
//        dialog.setLoadingText("加载中...");
//        dialog.show();
    }

    private void initView(View view) {
//        mDropDownLayout = (DropDownLayout) view.findViewById(R.id.dropDownLayout);
//
//        // init city menu
//        final ListView cityView = new ListView(getActivity());
//        cityAdapter = new DropDownGirdAdapter(getActivity(), Arrays.asList(cities));
//        cityView.setDividerHeight(0);
//        cityView.setAdapter(cityAdapter);
//
//        // init age menu
//        final ListView ageView = new ListView(getActivity());
//        ageView.setDividerHeight(0);
//        ageAdapter = new DropDownListAdapter(getActivity(), Arrays.asList(ages));
//        ageView.setAdapter(ageAdapter);
//
//        // init sex menu
//        final ListView sexView = new ListView(getActivity());
//        sexView.setDividerHeight(0);
//        sexAdapter = new DropDownListAdapter(getActivity(), Arrays.asList(sexes));
//        sexView.setAdapter(sexAdapter);
//
//        // init constellation
//        final View constellationView = getActivity().getLayoutInflater().inflate(R.layout.fragment_2_custom_layout, null);
//        GridView constellation = ButterKnife.findById(constellationView, R.id.constellation);
//        dropDownCustomAdapter = new DropDownCustomAdapter(getActivity(), Arrays.asList(constellations));
//        constellation.setAdapter(dropDownCustomAdapter);
//        TextView ok = ButterKnife.findById(constellationView, R.id.ok);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDropDownLayout.setTabText(constellationPosition == 0 ? headers[3] : constellations[constellationPosition]);
//                mDropDownLayout.closeMenu();
//            }
//        });
//
//        // init popupViews
//        popupViews.add(cityView);
//        popupViews.add(ageView);
//        popupViews.add(sexView);
//        popupViews.add(constellationView);
//
//        //add item click event
//        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                cityAdapter.setCheckItem(position);
//                mDropDownLayout.setTabText(position == 0 ? headers[0] : cities[position]);
//                mDropDownLayout.closeMenu();
//            }
//        });
//
//        ageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ageAdapter.setCheckItem(position);
//                mDropDownLayout.setTabText(position == 0 ? headers[1] : ages[position]);
//                mDropDownLayout.closeMenu();
//            }
//        });
//
//        sexView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                sexAdapter.setCheckItem(position);
//                mDropDownLayout.setTabText(position == 0 ? headers[2] : sexes[position]);
//                mDropDownLayout.closeMenu();
//            }
//        });
//
//        constellation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dropDownCustomAdapter.setCheckItem(position);
//                constellationPosition = position;
//            }
//        });
//
//        // 内容显示区域
//        TextView contentView = new TextView(getActivity());
//        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        contentView.setText("内容显示区域");
//        contentView.setTextColor(getResources().getColor(R.color.color_ff0000));
//        contentView.setGravity(Gravity.CENTER);
//        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//
//        // init DropDownLayout
//        List<String> strings = Arrays.asList(headers);
//        mDropDownLayout.setDropDownMenu(Arrays.asList(headers), popupViews, contentView);
    }

    @Override
    public void onProgressChange(int current, int max) {
        if (current == max) {
            bnp.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 相当于Fragment的onResume
            LoadingDialog dialog = new LoadingDialog(getActivity());
            dialog.setLoadingText("加载中...");
            dialog.show();
//        } else {
//            // 相当于Fragment的onPause
//            if (mDropDownLayout != null && mDropDownLayout.isShowing()) {
//                mDropDownLayout.closeMenu();
//            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

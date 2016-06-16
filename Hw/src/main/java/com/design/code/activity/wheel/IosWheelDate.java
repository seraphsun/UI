package com.design.code.activity.wheel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.design.code.R;
import com.design.code.adapter.wheel.WheelAdapterTextNumeric;
import com.design.code.util.UtilScreen;
import com.design.code.view.dialog.alert.LoadingToast;
import com.design.code.view.dialog.alert.WheelDate;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ignacey 2016/2/22.
 */
public class IosWheelDate extends Activity {

    private LayoutInflater mInflater;
    private LinearLayout mDateContent;
    private TextView age, constellation;

    private WheelDate year, month, day, time, min, sec;
    private int mYear = 1996;
    private int mMonth = 0;
    private int mDay = 1;

    private View view;
    boolean isMonthSetting = false, isDaySetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ios_dialog_wheel_date);
        mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mDateContent = (LinearLayout) findViewById(R.id.ll);
        mDateContent.addView(getDataPick());
        age = (TextView) findViewById(R.id.tv1);// 年龄
        constellation = (TextView) findViewById(R.id.tv2);// 星座
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
                // execute the task
                showToast();
//            }
//        }, 1000);
    }

    private LoadingToast.LoadToast toast;

    private void showToast() {
        toast = new LoadingToast.LoadToast(this).setText("正在加载");
        toast.setTranslationY(UtilScreen.getScreenHeight(this) / 2 - 100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        }, 2000);
    }

    private View getDataPick() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
//        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
//        int curDate = c.get(Calendar.DATE);

        int curYear = mYear;
        int curMonth = mMonth + 1;
        int curDate = mDay;

        view = mInflater.inflate(R.layout.ios_dialog_wheel_layout, null);

        year = (WheelDate) view.findViewById(R.id.year);
        WheelAdapterTextNumeric numericWheelAdapter1 = new WheelAdapterTextNumeric(this, 1950, norYear);
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);

        month = (WheelDate) view.findViewById(R.id.month);
        WheelAdapterTextNumeric numericWheelAdapter2 = new WheelAdapterTextNumeric(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        day = (WheelDate) view.findViewById(R.id.day);
        initDay(curYear, curMonth);
        day.setCyclic(true);

        min = (WheelDate) view.findViewById(R.id.min);
        WheelAdapterTextNumeric numericWheelAdapter3 = new WheelAdapterTextNumeric(this, 1, 23, "%02d");
        numericWheelAdapter3.setLabel("时");
        min.setViewAdapter(numericWheelAdapter3);
        min.setCyclic(true);
        min.addScrollingListener(scrollListener);

        sec = (WheelDate) view.findViewById(R.id.sec);
        WheelAdapterTextNumeric numericWheelAdapter4 = new WheelAdapterTextNumeric(this, 1, 59, "%02d");
        numericWheelAdapter4.setLabel("分");
        sec.setViewAdapter(numericWheelAdapter4);
        sec.setCyclic(true);
        sec.addScrollingListener(scrollListener);


        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);
        min.setVisibleItems(7);
        sec.setVisibleItems(7);

        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);

        return view;
    }

    private void initDay(int arg1, int arg2) {
        WheelAdapterTextNumeric numericWheelAdapter = new WheelAdapterTextNumeric(this, 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /**
     * 根据日期计算年龄
     */
    public static String calculateDatePoor(String birthday) {
        try {
            if (TextUtils.isEmpty(birthday))
                return "0";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdayDate = sdf.parse(birthday);
            String currTimeStr = sdf.format(new Date());
            Date currDate = sdf.parse(currTimeStr);
            if (birthdayDate.getTime() > currDate.getTime()) {
                return "0";
            }
            long age = (currDate.getTime() - birthdayDate.getTime()) / (24 * 60 * 60 * 1000) + 1;
            String year = new DecimalFormat("0.00").format(age / 365f);
            if (TextUtils.isEmpty(year))
                return "0";
            return String.valueOf(new Double(year).intValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * 根据月日计算星座
     */
    public String getConstellation(int month, int day) {
        String[] constellations = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
        int[] arr = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};// 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < arr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return constellations[index];
    }

    WheelDate.OnWheelScrollListener scrollListener = new WheelDate.OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelDate wheel) {
            toast.success();
        }

        @Override
        public void onScrollingFinished(WheelDate wheel) {
            int n_year = year.getCurrentItem() + 1950;//年
            int n_month = month.getCurrentItem() + 1;//月

            initDay(n_year, n_month);

            String birthday = String.valueOf((year.getCurrentItem() + 1950)) + "-" + ((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)) + "-" + (((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1));
            age.setText("年龄             " + calculateDatePoor(birthday) + "岁");
            constellation.setText("星座             " + getConstellation(month.getCurrentItem() + 1, day.getCurrentItem() + 1));
        }
    };
}

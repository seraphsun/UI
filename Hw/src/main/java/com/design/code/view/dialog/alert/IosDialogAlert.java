package com.design.code.view.dialog.alert;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.design.code.R;

/**
 * Created by Ignacey 2016/2/22.
 */
public class IosDialogAlert {

    private Context context;
    private Display display;

    private Dialog dialog;
    private LinearLayout mRootLayout;

    private TextView txt_title, txt_msg;
    private Button btn_neg, btn_pos;
    private ImageView img_line;
    private boolean showTitle, showMsg, showPosBtn, showNegBtn;

    public IosDialogAlert(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IosDialogAlert builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.ios_dialog_alert, null);

        // 获取自定义Dialog布局中的控件
        mRootLayout = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        img_line = (ImageView) view.findViewById(R.id.img_line);

        txt_title.setVisibility(View.GONE);
        txt_msg.setVisibility(View.GONE);
        btn_pos.setVisibility(View.GONE);
        btn_neg.setVisibility(View.GONE);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.DialogAlertStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        mRootLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public IosDialogAlert setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public IosDialogAlert setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public IosDialogAlert setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public IosDialogAlert setPositiveButton(String text, final View.OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public IosDialogAlert setNegativeButton(String text, final View.OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ios_alert_selector_single);
            btn_pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ios_alert_selector_right);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.ios_alert_selector_left);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ios_alert_selector_single);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.ios_alert_selector_single);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }
}

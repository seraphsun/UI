package com.design.code.view.dialog.progress;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.design.code.R;
import com.design.code.view.dialog.progress.styleloading.ViewShaping;

/**
 * Created by Design 2016/3/17.
 */
public class LoadingDialog {

    private Context mContext;
    private Dialog mDialog;
    private ViewShaping mViewShaping;
    private View mDialogContentView;

    public LoadingDialog(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mDialog = new Dialog(mContext, R.style.LoadingDialog_Default);
        mDialogContentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_shape, null);

        mViewShaping = (ViewShaping) mDialogContentView.findViewById(R.id.loadView);
        mDialog.setContentView(mDialogContentView);
    }

    public void setBackground(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) mDialogContentView.getBackground();
        gradientDrawable.setColor(color);
    }

    public void setLoadingText(CharSequence charSequence) {
        mViewShaping.setLoadingText(charSequence);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}

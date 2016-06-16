package com.design.code.view.dialog.alert;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.design.code.R;
import com.design.code.base.SuccessTickView;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Design 2016/3/15.
 */
public class SweetDialog extends Dialog implements View.OnClickListener {

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;

    private ProgressHelper mProgressHelper;
    private View mDialogView;

    private int mAlertType;
    private Animation mErrorInAnim, mSuccessBowAnim, mOverlayOutAnim;
    private AnimationSet mErrorXInAnim, mSuccessLayoutAnimSet, mModalInAnim, mModalOutAnim;

    private TextView mTitleTextView, mContentTextView;
    private String mTitleText, mContentText;
    private String mCancelText, mConfirmText;
    private boolean mShowCancel, mShowContent;

    private View mSuccessLeftMask, mSuccessRightMask;
    private ImageView mErrorX, mCustomImage;
    private Drawable mCustomImgDrawable;
    private SuccessTickView mSuccessTick;
    private Button mConfirmButton, mCancelButton;

    private FrameLayout mErrorFrame, mSuccessFrame, mProgressFrame, mWarningFrame;
    private OnSweetClickListener mCancelClickListener, mConfirmClickListener;

    private boolean mCloseFromCancel;

    public SweetDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public SweetDialog(Context context, int alertType) {
        super(context, R.style.SweetDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mProgressHelper = new ProgressHelper(context);
        mAlertType = alertType;
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_error_x_in);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animation> childAnims = mErrorXInAnim.getAnimations();
            int idx = 0;
            for (; idx < childAnims.size(); idx++) {
                if (childAnims.get(idx) instanceof AlphaAnimation) {
                    break;
                }
            }
            if (idx < childAnims.size()) {
                childAnims.remove(idx);
            }
        }
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_success_mask_layout);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.sweet_modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            SweetDialog.super.cancel();
                        } else {
                            SweetDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sweet);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
        mErrorX = (ImageView) mErrorFrame.findViewById(R.id.error_x);
        mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
        mProgressFrame = (FrameLayout) findViewById(R.id.progress_dialog);
        mSuccessTick = (SuccessTickView) mSuccessFrame.findViewById(R.id.success_tick);
        mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
        mCustomImage = (ImageView) findViewById(R.id.custom_image);
        mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mProgressHelper.setProgressWheel((ProgressWheel) findViewById(R.id.progressWheel));
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);
    }

    public SweetDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public SweetDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public SweetDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public SweetDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);
    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                    mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    break;
                case WARNING_TYPE:
                    mConfirmButton.setBackgroundResource(R.drawable.sweet_dialog_btn_bg_red);
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE_TYPE:
                    setCustomImage(mCustomImgDrawable);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    break;
            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);

        mConfirmButton.setBackgroundResource(R.drawable.sweet_dialog_btn_bg_blue);
        mErrorFrame.clearAnimation();
        mErrorX.clearAnimation();
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    private void playAnimation() {
        if (mAlertType == ERROR_TYPE) {
            mErrorFrame.startAnimation(mErrorInAnim);
            mErrorX.startAnimation(mErrorXInAnim);
        } else if (mAlertType == SUCCESS_TYPE) {
            mSuccessTick.startTickAnim();
            mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    public SweetDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public SweetDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public SweetDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public SweetDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(SweetDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(SweetDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mConfirmButton.startAnimation(mOverlayOutAnim);
        mDialogView.startAnimation(mModalOutAnim);
    }

    public SweetDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public SweetDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    @Override
    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    public int getAlertType() {
        return mAlertType;
    }

    public String getTitleText() {
        return mTitleText;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    public ProgressHelper getProgressHelper() {
        return mProgressHelper;
    }

    public static class ProgressHelper {

        private ProgressWheel mProgressWheel;
        private int mBarWidth, mBarColor, mRimWidth, mRimColor;
        private boolean mToSpin;
        private float mSpinSpeed;
        private boolean mIsInstantProgress;
        private float mProgressVal;
        private int mCircleRadius;

        public ProgressHelper(Context ctx) {
            mToSpin = true;
            mSpinSpeed = 0.75f;
            mBarWidth = ctx.getResources().getDimensionPixelSize(R.dimen.dimen_3) + 1;
            mBarColor = ctx.getResources().getColor(R.color.color_a5dc86);
            mRimWidth = 0;
            mRimColor = 0x00000000;
            mIsInstantProgress = false;
            mProgressVal = -1;
            mCircleRadius = ctx.getResources().getDimensionPixelOffset(R.dimen.dimen_34);
        }

        public ProgressWheel getProgressWheel() {
            return mProgressWheel;
        }

        public void setProgressWheel(ProgressWheel progressWheel) {
            mProgressWheel = progressWheel;
            updatePropsIfNeed();
        }

        private void updatePropsIfNeed() {
            if (mProgressWheel != null) {
                if (!mToSpin && mProgressWheel.isSpinning()) {
                    mProgressWheel.stopSpinning();
                } else if (mToSpin && !mProgressWheel.isSpinning()) {
                    mProgressWheel.spin();
                }
                if (mSpinSpeed != mProgressWheel.getSpinSpeed()) {
                    mProgressWheel.setSpinSpeed(mSpinSpeed);
                }
                if (mBarWidth != mProgressWheel.getBarWidth()) {
                    mProgressWheel.setBarWidth(mBarWidth);
                }
                if (mBarColor != mProgressWheel.getBarColor()) {
                    mProgressWheel.setBarColor(mBarColor);
                }
                if (mRimWidth != mProgressWheel.getRimWidth()) {
                    mProgressWheel.setRimWidth(mRimWidth);
                }
                if (mRimColor != mProgressWheel.getRimColor()) {
                    mProgressWheel.setRimColor(mRimColor);
                }
                if (mProgressVal != mProgressWheel.getProgress()) {
                    if (mIsInstantProgress) {
                        mProgressWheel.setInstantProgress(mProgressVal);
                    } else {
                        mProgressWheel.setProgress(mProgressVal);
                    }
                }
                if (mCircleRadius != mProgressWheel.getCircleRadius()) {
                    mProgressWheel.setCircleRadius(mCircleRadius);
                }
            }
        }

        public void resetCount() {
            if (mProgressWheel != null) {
                mProgressWheel.resetCount();
            }
        }

        public boolean isSpinning() {
            return mToSpin;
        }

        public void spin() {
            mToSpin = true;
            updatePropsIfNeed();
        }

        public void stopSpinning() {
            mToSpin = false;
            updatePropsIfNeed();
        }

        public float getProgress() {
            return mProgressVal;
        }

        public void setProgress(float progress) {
            mIsInstantProgress = false;
            mProgressVal = progress;
            updatePropsIfNeed();
        }

        public void setInstantProgress(float progress) {
            mProgressVal = progress;
            mIsInstantProgress = true;
            updatePropsIfNeed();
        }

        public int getCircleRadius() {
            return mCircleRadius;
        }

        /**
         * @param circleRadius units using pixel
         */
        public void setCircleRadius(int circleRadius) {
            mCircleRadius = circleRadius;
            updatePropsIfNeed();
        }

        public int getBarWidth() {
            return mBarWidth;
        }

        public void setBarWidth(int barWidth) {
            mBarWidth = barWidth;
            updatePropsIfNeed();
        }

        public int getBarColor() {
            return mBarColor;
        }

        public void setBarColor(int barColor) {
            mBarColor = barColor;
            updatePropsIfNeed();
        }

        public int getRimWidth() {
            return mRimWidth;
        }

        public void setRimWidth(int rimWidth) {
            mRimWidth = rimWidth;
            updatePropsIfNeed();
        }

        public int getRimColor() {
            return mRimColor;
        }

        public void setRimColor(int rimColor) {
            mRimColor = rimColor;
            updatePropsIfNeed();
        }

        public float getSpinSpeed() {
            return mSpinSpeed;
        }

        public void setSpinSpeed(float spinSpeed) {
            mSpinSpeed = spinSpeed;
            updatePropsIfNeed();
        }
    }

    public static class OptAnimationLoader {

        public static Animation loadAnimation(Context context, int id) throws Resources.NotFoundException {

            XmlResourceParser parser = null;
            try {
                parser = context.getResources().getAnimation(id);
                return createAnimationFromXml(context, parser);
            } catch (XmlPullParserException ex) {
                Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(ex);
                throw rnf;
            } catch (IOException ex) {
                Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(ex);
                throw rnf;
            } finally {
                if (parser != null) parser.close();
            }
        }

        private static Animation createAnimationFromXml(Context c, XmlPullParser parser) throws XmlPullParserException, IOException {
            return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
        }

        private static Animation createAnimationFromXml(Context c, XmlPullParser parser, AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {
            Animation anim = null;

            // Make sure we are on a start tag.
            int type;
            int depth = parser.getDepth();

            while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {

                if (type != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();

                if (name.equals("set")) {
                    anim = new AnimationSet(c, attrs);
                    createAnimationFromXml(c, parser, (AnimationSet) anim, attrs);
                } else if (name.equals("alpha")) {
                    anim = new AlphaAnimation(c, attrs);
                } else if (name.equals("scale")) {
                    anim = new ScaleAnimation(c, attrs);
                } else if (name.equals("rotate")) {
                    anim = new RotateAnimation(c, attrs);
                } else if (name.equals("translate")) {
                    anim = new TranslateAnimation(c, attrs);
                } else {
                    try {
                        anim = (Animation) Class.forName(name).getConstructor(Context.class, AttributeSet.class).newInstance(c, attrs);
                    } catch (Exception te) {
                        throw new RuntimeException("Unknown animation name: " + parser.getName() + " error:" + te.getMessage());
                    }
                }

                if (parent != null) {
                    parent.addAnimation(anim);
                }
            }
            return anim;
        }
    }

    public interface OnSweetClickListener {
        void onClick(SweetDialog sweetDialog);
    }

}

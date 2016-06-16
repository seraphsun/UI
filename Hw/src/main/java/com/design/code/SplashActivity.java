package com.design.code;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.design.code.util.UtilBitmap;
import com.design.code.util.UtilData;
import com.design.code.view.theme.ShimmerFrameLayout;

/**
 * Created by Ignacey 2016/1/6.
 */
public class SplashActivity extends Activity {

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash_img_bg);
//        Bitmap blurBitmap = new UtilBitmap(this).blurBitmap(bitmap);
//        findViewById(R.id.fl_splash_root).setBackground(UtilBitmap.bitmapToDrawableByBD(blurBitmap));

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);

        selectPreset(4);

        mShimmerViewContainer.startShimmerAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mShimmerViewContainer.setVisibility(View.GONE);
                Intent intent = new Intent();
                boolean is2Guide = (boolean) UtilData.get(SplashActivity.this, "is2Guide", true);

                if (is2Guide) {
                    intent.setClass(SplashActivity.this, GuideActivity.class);
                    UtilData.put(SplashActivity.this, "is2Guide", false);
                } else {
                    intent.setClass(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 5500);
    }

    /**
     * Select one of the shimmer animation presets.
     *
     * @param preset    index of the shimmer animation preset
     */
    private void selectPreset(int preset) {

        // Save the state of the animation
        boolean isPlaying = mShimmerViewContainer.isAnimationStarted();

        // Reset all parameters of the shimmer animation
        mShimmerViewContainer.useDefaults();

        // If a toast is already showing, hide it

        switch (preset) {
            default:
            case 0:
                // Default
                break;
            case 1:
                // Slow and reverse
                mShimmerViewContainer.setDuration(2400);
                mShimmerViewContainer.setRepeatMode(ObjectAnimator.RESTART);
                break;
            case 2:
                // Thin, straight and transparent
                mShimmerViewContainer.setBaseAlpha(0.1f);
                mShimmerViewContainer.setDropoff(0.1f);
                mShimmerViewContainer.setTilt(0);
                break;
            case 3:
                // Sweep angle 90
                mShimmerViewContainer.setAngle(ShimmerFrameLayout.MaskAngle.CW_90);
                break;
            case 4:
                // Spotlight
                mShimmerViewContainer.setBaseAlpha(0);
                mShimmerViewContainer.setDuration(2000);
                mShimmerViewContainer.setDropoff(0.1f);
                mShimmerViewContainer.setIntensity(0.35f);
                mShimmerViewContainer.setMaskShape(ShimmerFrameLayout.MaskShape.RADIAL);
                break;
        }

        // Show toast describing the chosen preset, if necessary

        // Setting a value on the shimmer layout stops the animation. Restart it, if necessary.
        if (isPlaying) {
            mShimmerViewContainer.startShimmerAnimation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShimmerViewContainer.stopShimmerAnimation();
        new Handler().removeCallbacksAndMessages(null);
    }
}

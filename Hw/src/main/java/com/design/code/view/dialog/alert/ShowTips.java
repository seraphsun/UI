package com.design.code.view.dialog.alert;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.design.code.R;

/**
 * Created by Ignacey 2016/1/25.
 */
public class ShowTips extends RelativeLayout {

    private ShowTipsBuilder.StoreUtils showTipsStore;
    private ShowTipsBuilder.ShowTipsInterface callback;

    private Paint paint, bitmapPaint, circleLine, transparentPaint;
    private PorterDuffXfermode porterDuffXfermode;

    private int screenY;
    private Bitmap bitmap;
    private Canvas temp;

    private Point showHintPoints;
    private int radius = 0;
    private int titleColor, descriptionColor, backgroundColor, circleColor, buttonColor, buttonTextColor;
    private int backgroundAlpha = 220;
    private String title, description, buttonText;
    private Drawable closeButtonDrawableBG;

    private View targetView;
    private boolean isMeasured;
    private boolean custom, displayOneTime;

    private int displayOneTimeID = 0;
    private int delay = 0;

    public ShowTips(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ShowTips(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowTips(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setVisibility(View.GONE);
        this.setBackgroundColor(Color.TRANSPARENT);

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // DO NOTHING
                // HACK TO BLOCK CLICKS

            }
        });

        showTipsStore = new ShowTipsBuilder.StoreUtils(getContext());

        paint = new Paint();
        bitmapPaint = new Paint();
        circleLine = new Paint();
        transparentPaint = new Paint();
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        // Get screen dimensions
        screenY = h;
    }

    /**
     * Draw circle and transparency background
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		/*
         * Since bitmap needs the canva's size, it wont be load at init()To prevent the DrawAllocation issue on low memory devices, the bitmap will be instantiate only when its null
		 */
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            temp = new Canvas(bitmap);
        }

        if (backgroundColor != 0) {
            paint.setColor(backgroundColor);
        } else {
            paint.setColor(Color.parseColor("#7f000000"));
        }

//        paint.setAlpha(backgroundAlpha);
        temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), paint);

        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(porterDuffXfermode);

        int x = showHintPoints.x;
        int y = showHintPoints.y;
        temp.drawCircle(x, y, radius, transparentPaint);

        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);

        circleLine.setStyle(Paint.Style.STROKE);

        if (circleColor != 0) {
            circleLine.setColor(circleColor);
        } else {
            circleLine.setColor(Color.RED);
        }

        circleLine.setAntiAlias(true);
        circleLine.setStrokeWidth(3);
        canvas.drawCircle(x, y, radius, circleLine);
    }

    public void show(final Activity activity) {
        if (isDisplayOneTime() && showTipsStore.hasShown(getDisplayOneTimeID())) {
            setVisibility(View.GONE);
            ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTips.this);
            return;
        } else {
            if (isDisplayOneTime()) {
                showTipsStore.storeShownId(getDisplayOneTimeID());
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTips.this);

                ShowTips.this.setVisibility(View.VISIBLE);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.show_tips_set_fade_in);
                ShowTips.this.startAnimation(fadeInAnimation);

                final ViewTreeObserver observer = targetView.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (isMeasured) {
                            return;
                        }

                        if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
                            isMeasured = true;
                        }

                        if (!custom) {
                            int[] location = new int[2];
                            targetView.getLocationInWindow(location);
                            int x = location[0] + targetView.getWidth() / 2;
                            int y = location[1] + targetView.getHeight() / 2;

                            showHintPoints = new Point(x, y);
                            radius = targetView.getWidth() / 2;
                        } else {
                            int[] location = new int[2];
                            targetView.getLocationInWindow(location);
                            int x = location[0] + showHintPoints.x;
                            int y = location[1] + showHintPoints.y;

                            showHintPoints = new Point(x, y);
                        }
                        invalidate();

                        createViews();
                    }
                });
            }
        }, getDelay());
    }

    /**
     * Create text views and close button
     */
    private void createViews() {
        this.removeAllViews();
        RelativeLayout texts_layout = new RelativeLayout(getContext());

        /*
         * Title
         */
        TextView textTitle = new TextView(getContext());
        textTitle.setText(getTitle());
        if (getTitleColor() != 0) {
            textTitle.setTextColor(getTitleColor());
        } else {
            textTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
        }
        textTitle.setId(123);
        textTitle.setTextSize(26);

        // Add title to this view
        texts_layout.addView(textTitle);

        /*
         * Description
         */
        TextView text = new TextView(getContext());
        text.setText(getDescription());
        if (getDescriptionColor() != 0) {
            text.setTextColor(getDescriptionColor());
        } else {
            text.setTextColor(Color.WHITE);
        }
        text.setTextSize(17);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, 123);
        text.setLayoutParams(params);

        texts_layout.addView(text);

        LayoutParams paramsTexts = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if (screenY / 2 > showHintPoints.y) {
            // textBlock under the highlight circle
            paramsTexts.height = (showHintPoints.y + radius) - screenY;
            paramsTexts.topMargin = (showHintPoints.y + radius);
            texts_layout.setGravity(Gravity.START | Gravity.TOP);
            texts_layout.setPadding(50, 50, 50, 50);
        } else {
            // textBlock above the highlight circle
            paramsTexts.height = showHintPoints.y - radius;
            texts_layout.setGravity(Gravity.START | Gravity.BOTTOM);
            texts_layout.setPadding(50, 100, 50, 50);
        }

        texts_layout.setLayoutParams(paramsTexts);
        this.addView(texts_layout);

		/*
         * Close button
		 */
        Button btn_close = new Button(getContext());
        btn_close.setId(4375);
        btn_close.setText(getButtonText());
        btn_close.setTextColor(buttonTextColor == 0 ? Color.WHITE : buttonTextColor);

        if (closeButtonDrawableBG != null) {
            btn_close.setBackgroundDrawable(closeButtonDrawableBG);
        }

        if (buttonColor != 0) {
            btn_close.getBackground().setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
        }

        btn_close.setTextSize(17);
        btn_close.setGravity(Gravity.CENTER);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = 50;
        params.bottomMargin = 100;

        btn_close.setLayoutParams(params);
        btn_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getCallback() != null) {
                    getCallback().gotItClicked();
                }

                setVisibility(View.GONE);
                ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTips.this);

            }
        });
        this.addView(btn_close);
    }

    private boolean isDisplayOneTime() {
        return displayOneTime;
    }

    private void setDisplayOneTime(boolean displayOneTime) {
        this.displayOneTime = displayOneTime;
    }

    private int getDisplayOneTimeID() {
        return displayOneTimeID;
    }

    private void setDisplayOneTimeID(int displayOneTimeID) {
        this.displayOneTimeID = displayOneTimeID;
    }

    private int getDelay() {
        return delay;
    }

    private void setDelay(int delay) {
        this.delay = delay;
    }

    private String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private int getTitleColor() {
        return titleColor;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private String getDescription() {
        return description;
    }

    private void setDescriptionColor(int descriptionColor) {
        this.descriptionColor = descriptionColor;
    }

    private int getDescriptionColor() {
        return descriptionColor;
    }

    private String getButtonText() {
        if (buttonText == null || buttonText.equals("")) {
            return "Got it";
        }

        return buttonText;
    }

    private void setButtonText(String text) {
        this.buttonText = text;
    }

    private ShowTipsBuilder.ShowTipsInterface getCallback() {
        return callback;
    }

    private void setCallback(ShowTipsBuilder.ShowTipsInterface callback) {
        this.callback = callback;
    }

    private void setTarget(View v) {
        targetView = v;
    }

    private void setTarget(View v, int x, int y, int radius) {
        custom = true;
        targetView = v;
        showHintPoints = new Point(x, y);
        this.radius = radius;
    }

    private int getBackgroundColors() {
        return backgroundColor;
    }

    private void setBackgroundColors(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private int getCircleColor() {
        return circleColor;
    }

    private void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    private int getButtonColor() {
        return buttonColor;
    }

    private void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    private int getButtonTextColor() {
        return buttonTextColor;
    }

    private void setButtonTextColor(int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    private Drawable getCloseButtonDrawableBG() {
        return closeButtonDrawableBG;
    }

    private void setCloseButtonDrawableBG(Drawable closeButtonDrawableBG) {
        this.closeButtonDrawableBG = closeButtonDrawableBG;
    }

    private int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    private void setBackgroundAlpha(int backgroundAlpha) {
        if (backgroundAlpha > 255) {
            this.backgroundAlpha = 255;
        } else if (backgroundAlpha < 0) {
            this.backgroundAlpha = 0;
        } else {
            this.backgroundAlpha = backgroundAlpha;
        }
    }

    /**
     * 构建器
     */
    public static class ShowTipsBuilder {
        ShowTips showTips;

        public ShowTipsBuilder(Activity activity) {
            this.showTips = new ShowTips(activity);
        }

        /**
         * Set highlight view. All view will be highlighted
         *
         * @param v Target view
         * @return ShowTipsBuilder
         */
        public ShowTipsBuilder setTarget(View v) {
            this.showTips.setTarget(v);
            return this;
        }

        /**
         * Set highlighted view with custom center and radius
         *
         * @param v Target View
         * @param x circle center x according target
         * @param y circle center y according target
         */
        public ShowTipsBuilder setTarget(View v, int x, int y, int radius) {
            showTips.setTarget(v, x, y, radius);

            return this;
        }

        public ShowTips build() {
            return showTips;
        }

        public ShowTipsBuilder setTitle(String text) {
            this.showTips.setTitle(text);
            return this;
        }

        public ShowTipsBuilder setDescription(String text) {
            this.showTips.setDescription(text);
            return this;
        }

        public ShowTipsBuilder displayOneTime(int showtipId) {
            this.showTips.setDisplayOneTime(true);
            this.showTips.setDisplayOneTimeID(showtipId);
            return this;
        }

        public ShowTipsBuilder setCallback(ShowTipsInterface callback) {
            this.showTips.setCallback(callback);
            return this;
        }

        public ShowTipsBuilder setDelay(int delay) {
            this.showTips.setDelay(delay);
            return this;
        }

        public ShowTipsBuilder setTitleColor(int color) {
            this.showTips.setTitleColor(color);
            return this;
        }

        public ShowTipsBuilder setDescriptionColor(int color) {
            this.showTips.setDescriptionColor(color);
            return this;
        }

        public ShowTipsBuilder setBackgroundColor(int color) {
            this.showTips.setBackgroundColors(color);
            return this;
        }

        public ShowTipsBuilder setCircleColor(int color) {
            this.showTips.setCircleColor(color);
            return this;
        }

        public ShowTipsBuilder setButtonText(String text) {
            this.showTips.setButtonText(text);
            return this;
        }

        public ShowTipsBuilder setCloseButtonColor(int color) {
            this.showTips.setButtonColor(color);
            return this;
        }

        public ShowTipsBuilder setCloseButtonTextColor(int color) {
            this.showTips.setButtonTextColor(color);
            return this;
        }

        public ShowTipsBuilder setButtonBackground(Drawable drawable) {
            this.showTips.setCloseButtonDrawableBG(drawable);
            return this;
        }

        /**
         * Set transparecy for background layer. 0-255 range
         *
         * @return ShowTipsBuilder
         */
        public ShowTipsBuilder setBackgroundAlpha(int alpha) {
            this.showTips.setBackgroundAlpha(alpha);
            return this;
        }

        /**
         * 数据缓存
         */
        private static class StoreUtils {

            private Context context;

            public StoreUtils(Context context) {
                this.context = context;
            }

            boolean hasShown(int value) {
                return context.getSharedPreferences("show tips", Context.MODE_PRIVATE).getBoolean("key" + value, false);
            }

            void storeShownId(int value) {
                SharedPreferences internal = context.getSharedPreferences("show tips", Context.MODE_PRIVATE);
                internal.edit().putBoolean("key" + value, true).apply();
            }
        }

        public interface ShowTipsInterface {
            void gotItClicked();
        }
    }
}

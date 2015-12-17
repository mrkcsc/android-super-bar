package com.miguelgaeta.super_bar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * A value bar with style.
 *
 * Created by Miguel Gaeta on 6/11/15.
 */
public class SuperBar extends View implements ValueAnimator.AnimatorUpdateListener {

    public interface ColorFormatter {

        /**
         * Given the current state of the bar, return the
         * desired color integer.
         *
         * @param value Current value of the bar.
         *
         * @param maxValue Maximum value the bar can display.
         * @param minValue Minimum value the bar can display.
         *
         * @return Color integer for current values.
         */
        int getColor(float value, float maxValue, float minValue);

        /**
         * Default color formatter that just returns
         * a single color.
         */
        class Solid implements ColorFormatter {

            private int color;

            public Solid(int color) {

                this.color = color;
            }

            @Override
            public int getColor(float value, float maxVal, float minVal) {

                return color;
            }
        }
    }

    public interface OnSelectionChanged {

        /**
         * Called when the user releases his finger from the ValueBar.
         *
         * @param value Current value of the bar.
         *
         * @param maxValue Maximum value the bar can display.
         * @param minValue Minimum value the bar can display.
         *
         *
         * @param superBar Associated instance.
         */
        void onSelectionChanged(float value, float maxValue, float minValue, SuperBar superBar);
    }

    public interface OnSelectionMoved {

        /**
         * Called every time the user moves the finger on the ValueBar.
         *
         * @param value Current value of the bar.
         *
         * @param maxValue Maximum value the bar can display.
         * @param minValue Minimum value the bar can display.
         *
         * @param superBar Associated instance.
         */
        void onSelectionMoved(float value, float maxValue, float minValue, SuperBar superBar);
    }

    private final Config config = new Config(this);

    @SuppressWarnings("unused")
    public Config getConfig() {

        return config;
    }

    private RectF mBar = new RectF();
    private RectF mBarBackground = new RectF();
    private RectF mBarOverlay = new RectF();

    private Paint mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mOverlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mDrawValueText = false;

    public SuperBar(Context context) {
        super(context);
        init();
    }

    public SuperBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuperBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //@Setter @Getter TODO
    private int controlShadowSize = 12;

    //@Setter @Getter @ColorInt TODO
    private int controlShadowColor = Color.argb(127, 0, 0, 0);

    //@Setter @Getter @ColorRes TODO
    private int controlColor = Color.WHITE;

    //@Setter @Getter TODO
    private int barMargin = 12;

    //@Setter @Getter @ColorInt TODO
    private int backgroundBarColor = Color.GREEN;

    //@Setter @Getter @ColorInt TODO
    private int overlayBarColor = Color.parseColor("#CCFF0000");

    //@Getter TODO
    private float overlayBarValue = 90f;

    public void setOverlayBarValue(float overlayBarValue) {

        this.overlayBarValue = overlayBarValue;

        invalidate();
    }

    /**
     * Do all preparations.
     */
    private void init() {

        // Standard fill style.
        paint.setStyle(Paint.Style.FILL);

        // Needed to render shadow.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBarPaint.setStyle(Paint.Style.FILL);

        mOverlayPaint.setStyle(Paint.Style.FILL);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getParent().requestDisallowInterceptTouchEvent(true);

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_UP:

                        view.getParent().requestDisallowInterceptTouchEvent(false);

                        break;
                }

                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float shadowRadius = controlShadowSize / 2f;

        final float halfMargin = barMargin / 2f;

        final float controlRadius = (getHeight() / 2f);

        if (!isInEditMode()) {

            mOverlayPaint.setShadowLayer(shadowRadius, 0f, 0f, controlShadowColor);
            mOverlayPaint.setColor(controlColor);
        }

        float length = ((getWidth() - (controlRadius * 2)) / (config.maxBarValue - config.minBarValue)) * (config.barValue - config.minBarValue);

        mBar.set(controlRadius, shadowRadius + halfMargin, length + controlRadius, getHeight() - shadowRadius - halfMargin);

        mBarPaint.setColor(config.colorFormatter.getColor(config.barValue, config.maxBarValue, config.minBarValue));

        drawBackgroundBar(canvas, shadowRadius, halfMargin, controlRadius);

        // draw the value-bar
        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, mBarPaint);

        drawOverlayBar(canvas, shadowRadius, halfMargin, controlRadius);

        // Dragging control.
        canvas.drawCircle(mBar.right, (getHeight() / 2f), controlRadius - shadowRadius, mOverlayPaint);
    }

    private void drawBackgroundBar(Canvas canvas, float halfShadow, float halfMargin, float controlRadius) {

        mBarBackground.set(controlRadius, halfShadow + halfMargin, getWidth() - controlRadius, getHeight() - halfShadow - halfMargin);

        paint.setColor(backgroundBarColor);

        canvas.drawRoundRect(mBarBackground, mBarBackground.height() / 2f, mBarBackground.height() / 2f, paint);
    }

    private void drawOverlayBar(Canvas canvas, float halfShadow, float halfMargin, float controlRadius) {

        float length = ((getWidth() - (controlRadius * 2)) / (config.maxBarValue - config.minBarValue)) * (overlayBarValue - config.minBarValue);

        mBarOverlay.set(length + controlRadius, halfShadow + halfMargin, getWidth() - controlRadius, getHeight() - halfShadow - halfMargin);

        paint.setColor(overlayBarColor);

        canvas.drawRoundRect(mBarOverlay, mBarBackground.height() / 2f, mBarBackground.height() / 2f, paint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        invalidate();
    }



    /**
     * Set this to true to enable drawing the actual value that is currently
     * displayed onto the bar.
     *
     * @param enabled Test
     */
    public void setDrawValueText(boolean enabled) {
        mDrawValueText = enabled;
    }

    /**
     * Returns true if drawing the text that describes the actual value is
     * enabled.
     *
     * @return Test
     */
    public boolean isDrawValueTextEnabled() {
        return mDrawValueText;
    }

    /**
     * Returns the corresponding value for a pixel-position on the horizontal
     * axis.
     *
     * @param xPos test
     */
    public float getValueForPosition(int xPos) {

        float factor = xPos / getWidth();

        return config.maxBarValue * factor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (!config.touchEnabled) {

            return super.onTouchEvent(motionEvent);
        }

        if (config.gestureDetector != null &&
            config.gestureDetector.onTouchEvent(motionEvent)) {

            return true;
        }

        float x = motionEvent.getX();

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                updateValue(x);
                invalidate();
            case MotionEvent.ACTION_MOVE:
                updateValue(x);
                invalidate();

                if (config.onSelectionMoved != null) {
                    config.onSelectionMoved.onSelectionMoved(config.barValue, config.maxBarValue, config.minBarValue, this);
                }

                break;
            case MotionEvent.ACTION_UP:
                updateValue(x);
                invalidate();

                if (config.onSelectionChanged != null) {
                    config.onSelectionChanged.onSelectionChanged(config.barValue, config.maxBarValue, config.minBarValue, this);
                }

                break;
        }

        return true;
    }

    /**
     * Updates the value on the ValueBar depending on the touch position.
     */
    private void updateValue(float x) {

        float newVal;

        if (x <= 0)
            newVal = config.minBarValue;
        else if (x > getWidth())
            newVal = config.maxBarValue;
        else {
            float factor = x / getWidth();

            newVal = (config.maxBarValue - config.minBarValue) * factor + config.minBarValue;
        }

        if (config.barInterval > 0f) {

            float remainder = newVal % config.barInterval;

            // check if the new value is closer to the next, or the previous
            if (remainder <= config.barInterval / 2f) {

                newVal = newVal - remainder;
            } else {
                newVal = newVal - remainder + config.barInterval;
            }
        }

        config.barValue = newVal;
    }

    public static class Config {

        private final SuperBar superBar;

        private Config(SuperBar superBar) {

            this.superBar = superBar;
        }

        private OnSelectionChanged onSelectionChanged;
        private OnSelectionMoved onSelectionMoved;

        private float barValue = 40f;

        private float minBarValue = 0f;
        private float maxBarValue = 100f;

        private float barInterval = 1f;

        private ColorFormatter colorFormatter = new ColorFormatter.Solid(Color.BLUE);

        private GestureDetector gestureDetector;

        private boolean touchEnabled = true;

        /**
         * Set a callback to be fired when the current bar selection
         * value is changed by the user.
         *
         * @param onSelectionChanged Selection changed callback.
         */
        @SuppressWarnings("unused")
        public void setSelectedChanged(OnSelectionChanged onSelectionChanged) {

            this.onSelectionChanged = onSelectionChanged;
        }

        /***
         * Set a callback to be fired when the current bar selection
         * value if moved by the user.
         *
         * @param onSelectionMoved Selection moved callback.
         */
        @SuppressWarnings("unused")
        public void setOnSelectionMoved(OnSelectionMoved onSelectionMoved) {

            this.onSelectionMoved = onSelectionMoved;
        }

        /**
         * Get the current value of the bar.
         *
         * @return Current value of the bar.
         */
        @SuppressWarnings("unused")
        public float getBarValue() {

            return barValue;
        }

        /**
         * Set bar value from it's current position to another
         * value within it's bounds.
         *
         * @param durationMillis Duration in milliseconds - if null will not animate.
         *
         * @param barValue Target bar value.
         */
        @SuppressWarnings("unused")
        public void setBarValue(Integer durationMillis, float barValue) {

            setBarValue(durationMillis, barValue, this.barValue);
        }

        /**
         * Set bar value from any value within it's bounds to another
         * value within it's bounds.
         *
         * @param durationMillis Duration in milliseconds - if null will not animate.
         *
         * @param barValue Target bar value.
         * @param barValueFrom Starting bar value.
         */
        public void setBarValue(Integer durationMillis, float barValue, float barValueFrom) {

            if (barValueFrom < minBarValue) {
                barValueFrom = minBarValue;
            }

            if (barValueFrom > maxBarValue) {
                barValueFrom = maxBarValue;
            }

            if (barValue < minBarValue) {
                barValue = minBarValue;
            }

            if (barValue > maxBarValue) {
                barValue = maxBarValue;
            }

            if (durationMillis == null) {

                this.barValue = barValue;

                superBar.invalidate();

            } else {

                final ObjectAnimator animator = ObjectAnimator.ofFloat(this, "barValue", barValueFrom, barValue);

                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(durationMillis);
                animator.addUpdateListener(superBar);
                animator.start();
            }
        }

        /**
         * Sets the minimum and maximum value the bar can display.
         *
         * @param minBarValue Minimum value.
         * @param maxBarValue Maximum value.
         */
        @SuppressWarnings("unused")
        public void setBarValueBounds(float minBarValue, float maxBarValue) {

            this.maxBarValue = maxBarValue;
            this.minBarValue = minBarValue;
        }

        /**
         * Returns the maximum value the bar can display.
         *
         * @return Maximum value.
         */
        @SuppressWarnings("unused")
        public float getMaxBarValue() {

            return maxBarValue;
        }

        /**
         * Returns the minimum value the bar can display.
         *
         * @return Minimum value.
         */
        @SuppressWarnings("unused")
        public float getMinBarValue() {

            return minBarValue;
        }

        /**
         * Sets the interval in which the values can be chosen and displayed
         * from the bar slider.
         *
         * If interval less than 0, there is no interval.
         *
         * @param barInterval Bar value interval.
         */
        @SuppressWarnings("unused")
        public void setBarInterval(float barInterval) {

            this.barInterval = barInterval;
        }

        /**
         * Returns bar interval.
         *
         * @return Bar interval.
         */
        @SuppressWarnings("unused")
        public float getBarInterval() {

            return barInterval;
        }

        /**
         * Sets a custom color formatter for the bar.
         *
         * @param colorFormatter Color formatter.
         */
        public void setColor(ColorFormatter colorFormatter) {

            if (colorFormatter == null) {

                return;
            }

            this.colorFormatter = colorFormatter;
        }

        /**
         * Set a solid color for the bar.
         *
         * @param color Color.
         */
        @SuppressWarnings("unused")
        public void setColor(int color) {

            setColor(new ColorFormatter.Solid(color));
        }

        /**
         * Set a gesture detector for consumers that wish
         * to add custom handling of touch events.
         *
         * @param gestureDetector Detector that returns true if event is consumed.
         */
        @SuppressWarnings("unused")
        public void setGestureDetector(GestureDetector gestureDetector) {

            this.gestureDetector = gestureDetector;
        }

        /**
         * Set this to true to enable touch gestures on the bar control.
         *
         * @param touchEnabled Is touch enabled.
         */
        @SuppressWarnings("unused")
        public void setTouchEnabled(boolean touchEnabled) {

            this.touchEnabled = touchEnabled;
        }
    }
}

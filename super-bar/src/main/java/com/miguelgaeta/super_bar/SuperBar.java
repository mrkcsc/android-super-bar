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

    private final Painter paint = new Painter(this);

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

    //@Setter @Getter TODO
    private int controlShadowSize = 12;

    //@Setter @Getter @ColorInt TODO
    private int controlShadowColor = Color.argb(127, 0, 0, 0);

    //@Setter @Getter @ColorRes TODO
    private int controlColor = Color.YELLOW;

    //@Setter @Getter TODO
    private int barMargin = 12;

    /**
     * Do all preparations.
     */
    private void init() {

        // Needed to render shadow.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

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

        float barTop = shadowRadius + halfMargin;
        float barBot = getHeight() - shadowRadius - halfMargin;

        drawBackgroundBar(canvas, barTop, barBot, controlRadius);

        drawBar(canvas, barTop, barBot, controlRadius, config.barValue);

        final float controlX = mBar.right;

        drawOverlayBar(canvas, barTop, barBot, controlRadius, config.overlayBarValue);

        paint.setColor(controlColor, shadowRadius, controlShadowColor);

        // Dragging control.
        canvas.drawCircle(controlX, (getHeight() / 2f), controlRadius - shadowRadius, paint);
    }

    private void drawBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((getWidth() - (controlRadius * 2)) / (config.maxBarValue - config.minBarValue)) * (barValue - config.minBarValue);

        mBar.set(controlRadius, barTop, length + controlRadius, barBot);

        paint.setColor(config.color.getColor(config.barValue, config.maxBarValue, config.minBarValue));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    private void drawBackgroundBar(Canvas canvas, float barTop, float barBot, float controlRadius) {

        mBar.set(controlRadius, barTop, getWidth() - controlRadius, barBot);

        paint.setColor(config.backgroundColor);

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    private void drawOverlayBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((getWidth() - (controlRadius * 2)) / (config.maxBarValue - config.minBarValue)) * (barValue - config.minBarValue);

        mBar.set(length + controlRadius, barTop, getWidth() - controlRadius, barBot);

        paint.setColor(config.overlayBarColor.getColor(config.overlayBarValue, config.maxBarValue, config.minBarValue));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        invalidate();
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

    private static class Painter extends Paint {

        private final View view;

        /**
         * Paint instance with a view subclass for
         * simple configuration.
         *
         * @param view Associated view to paint.
         */
        private Painter(View view) {
            super();

            this.view = view;

            setFlags(Paint.ANTI_ALIAS_FLAG);
            setStyle(Paint.Style.FILL);
        }

        /**
         * Set color of instance.
         *
         * @param color Target color.
         */
        @Override
        public void setColor(int color) {

            setColor(color, null, 0);
        }

        /**
         * Set color of instance and optionally a shadow layer
         * if not in edit mode.
         *
         * @param color Target color.
         * @param shadowRadius Shadow radius, null for no shadow.
         * @param shadowColor Shadow color.
         */
        private void setColor(int color, Float shadowRadius, int shadowColor) {

            if (!view.isInEditMode() && shadowRadius != null) {

                setShadowLayer(shadowRadius, 0f, 0f, shadowColor);

            } else {

                clearShadowLayer();
            }

            super.setColor(color);
        }
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

        private int backgroundColor = Color.GREEN;

        private ColorFormatter color = new ColorFormatter.Solid(Color.BLUE);

        private GestureDetector gestureDetector;

        private boolean touchEnabled = true;

        private float overlayBarValue = 80f;

        private ColorFormatter overlayBarColor = new ColorFormatter.Solid(Color.RED);

        /**
         * Set overlay bar value.

         * @param overlayBarValue Target bar value.
         */
        @SuppressWarnings("unused")
        public void setOverlayBarValue(float overlayBarValue) {

            this.overlayBarValue = overlayBarValue;

            superBar.invalidate();
        }

        /**
         * Get the current value of the overlay bar.
         *
         * @return Current value of the overlay bar.
         */
        @SuppressWarnings("unused")
        public float getOverlayBarValue() {

            return overlayBarValue;
        }

        /**
         * Sets a custom color formatter for the overlay bar.
         *
         * @param colorFormatter Color formatter.
         */
        public void setOverlayBarColor(ColorFormatter colorFormatter) {

            if (colorFormatter == null) {

                return;
            }

            this.overlayBarColor = colorFormatter;
        }

        /**
         * Set a solid color for the overlay bar.
         *
         * @param color Color.
         */
        @SuppressWarnings("unused")
        public void setOverlayBarColor(int color) {

            setOverlayBarColor(new ColorFormatter.Solid(color));
        }

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

            this.color = colorFormatter;
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

        /**
         * Set background color for the bar.
         *
         * @param backgroundColor Target background color.
         */
        @SuppressWarnings("unused")
        public void setBackgroundColor(int backgroundColor) {

            this.backgroundColor = backgroundColor;
        }
    }
}

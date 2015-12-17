package com.miguelgaeta.super_bar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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

    private final SuperBarAttributes attrs = new SuperBarAttributes(this);

    private final SuperBarPainter paint = new SuperBarPainter(this);

    private final SuperBarConfig config = new SuperBarConfig(this);

    @SuppressWarnings("unused")
    public SuperBarConfig getConfig() {

        return config;
    }

    private RectF mBar = new RectF();

    public SuperBar(Context context) {
        super(context);

        init();
    }

    public SuperBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.attrs.parse(attrs, 0);

        init();
    }

    public SuperBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.attrs.parse(attrs, defStyleAttr);

        init();
    }
    /**
     * Do all preparations.
     */
    private void init() {

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

        final float shadowRadius = config.getControlShadowSize() / 2f;

        final float halfMargin = config.getBarMargin() / 2f;

        final float controlRadius = (getHeight() / 2f);

        float barTop = shadowRadius + halfMargin;
        float barBot = getHeight() - shadowRadius - halfMargin;

        drawBackgroundBar(canvas, barTop, barBot, controlRadius);

        drawBar(canvas, barTop, barBot, controlRadius, config.getBarValue());

        final float controlX = mBar.right;

        drawOverlayBar(canvas, barTop, barBot, controlRadius, config.getOverlayBarValue());

        paint.setColor(config.getControlColor(), shadowRadius, config.getControlShadowColor());

        // Dragging control.
        canvas.drawCircle(controlX, (getHeight() / 2f), controlRadius - shadowRadius, paint);
    }

    private void drawBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((getWidth() - (controlRadius * 2)) / (config.getMaxBarValue() - config.getMinBarValue())) * (barValue - config.getMinBarValue());

        mBar.set(controlRadius, barTop, length + controlRadius, barBot);

        paint.setColor(config.getColor().getColor(config.getBarValue(), config.getMaxBarValue(), config.getMinBarValue()));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    private void drawBackgroundBar(Canvas canvas, float barTop, float barBot, float controlRadius) {

        mBar.set(controlRadius, barTop, getWidth() - controlRadius, barBot);

        paint.setColor(config.getBackgroundColor());

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    private void drawOverlayBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((getWidth() - (controlRadius * 2)) / (config.getMaxBarValue() - config.getMinBarValue())) * (barValue - config.getMinBarValue());

        mBar.set(length + controlRadius, barTop, getWidth() - controlRadius, barBot);

        paint.setColor(config.getOverlayBarColor().getColor(config.getOverlayBarValue(), config.getMaxBarValue(), config.getMinBarValue()));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, paint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (!config.isTouchEnabled()) {

            return super.onTouchEvent(motionEvent);
        }

        if (config.getGestureDetector() != null &&
            config.getGestureDetector().onTouchEvent(motionEvent)) {

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

                if (config.getOnSelectionMoved() != null) {
                    config.getOnSelectionMoved().onSelectionMoved(config.getBarValue(), config.getMaxBarValue(), config.getMinBarValue(), this);
                }

                break;
            case MotionEvent.ACTION_UP:
                updateValue(x);
                invalidate();

                if (config.getOnSelectionChanged() != null) {
                    config.getOnSelectionChanged().onSelectionChanged(config.getBarValue(), config.getMaxBarValue(), config.getMinBarValue(), this);
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
            newVal = config.getMinBarValue();
        else if (x > getWidth())
            newVal = config.getMaxBarValue();
        else {
            float factor = x / getWidth();

            newVal = (config.getMaxBarValue() - config.getMinBarValue()) * factor + config.getMinBarValue();
        }

        if (config.getBarInterval() > 0f) {

            float remainder = newVal % config.getBarInterval();

            // check if the new value is closer to the next, or the previous
            if (remainder <= config.getBarInterval() / 2f) {

                newVal = newVal - remainder;
            } else {
                newVal = newVal - remainder + config.getBarInterval();
            }
        }

        config.setBarValue(null, newVal);
    }
}

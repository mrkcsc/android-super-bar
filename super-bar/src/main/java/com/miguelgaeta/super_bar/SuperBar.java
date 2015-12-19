package com.miguelgaeta.super_bar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
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

    final SuperBarAttributes attrs = new SuperBarAttributes(this);

    final SuperBarPainter paint = new SuperBarPainter(this);

    final SuperBarDrawer drawer = new SuperBarDrawer(this);

    final SuperBarConfig config = new SuperBarConfig(this);

    @SuppressWarnings("unused")
    public SuperBarConfig getConfig() {

        return config;
    }

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

        drawer.draw(canvas);
    }

    /**
     * When an animation ticks, invalidate the view
     * so on draw is called again.
     *
     * @param valueAnimator Value animator.
     */
    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {

        invalidate();
    }

    /**
     * Intercept touch events for this view and apply
     * custom draw code to update the bar control
     * position and dispatch events.
     *
     * @param motionEvent Motion event.
     *
     * @return True if event is consumed.
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (!config.isTouchEnabled()) {

            return super.onTouchEvent(motionEvent);
        }

        return drawer.onTouchEvent(motionEvent);
    }
}

package com.miguelgaeta.super_bar;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Configurable properties of the super view.
 *
 * Created by Miguel Gaeta on 12/17/15.
 */
public class SuperBarConfig {

    private final SuperBar superBar;

    SuperBarConfig(SuperBar superBar) {

        this.superBar = superBar;
    }

    private SuperBar.OnSelectionChanged onSelectionChanged;
    private SuperBar.OnSelectionMoved onSelectionMoved;

    private float barHeight = 0f;
    private float barValue = 40f;

    private float minBarValue = 0f;
    private float maxBarValue = 100f;

    private float barInterval = 1f;

    private int backgroundColor = Color.GREEN;

    private SuperBar.ColorFormatter color = new SuperBar.ColorFormatter.Solid(Color.BLUE);

    private GestureDetector gestureDetector;

    private boolean touchEnabled = true;

    private float overlayBarValue = 80f;

    private SuperBar.ColorFormatter overlayBarColor = new SuperBar.ColorFormatter.Solid(Color.RED);

    private int controlShadowRadius = 6;
    private int controlShadowColor = Color.argb(127, 0, 0, 0);
    private int controlColor = Color.YELLOW;
    private int controlRadius = 0;

    public int getControlRadius() {

        return (int)(controlRadius > 0 && controlRadius < superBar.getHeight() ? controlRadius : superBar.getHeight() / 2f);
    }

    public void setControlRadius(int controlRadius) {

        this.controlRadius = controlRadius;
    }

    /**
     * Get the height of the bar but enforce that it
     * cannot be larger than view bounds.
     *
     * @return Bar height.
     */
    public float getBarHeight() {

        return barHeight > 0 && barHeight < superBar.getHeight() ? barHeight : superBar.getHeight();
    }

    /**
     * Set the bar height.
     *
     * @param barHeight Bar height.
     */
    public void setBarHeight(float barHeight) {

        this.barHeight = barHeight;
    }

    /**
     * Set control shadow size in pixels.
     *
     * @param controlShadowRadius Control shadow radius in pixels.
     */
    public void setControlShadowRadius(int controlShadowRadius) {

        this.controlShadowRadius = controlShadowRadius;
    }

    /**
     * Get the shadow size of the control knob.
     *
     * @return Shadow size of the control knob.
     */
    public int getControlShadowRadius() {

        return this.controlShadowRadius;
    }

    /**
     * Set control shadow color.
     *
     * @param controlShadowColor Control shadow color.
     */
    public void setControlShadowColor(int controlShadowColor) {

        this.controlShadowColor = controlShadowColor;
    }

    /**
     * Get control shadow color.
     *
     * @return Control shadow color.
     */
    public int getControlShadowColor() {

        return this.controlShadowColor;
    }

    /**
     * Set control color.
     *
     * @param controlColor Control color.
     */
    public void setControlColor(int controlColor) {

        this.controlColor = controlColor;
    }

    /**
     * Get control color.
     *
     * @return Control color.
     */
    public int getControlColor() {

        return this.controlColor;
    }

    /**
     * Set overlay bar value.

     * @param overlayBarValue Target bar value.
     */
    public void setOverlayBarValue(float overlayBarValue) {

        this.overlayBarValue = overlayBarValue;

        superBar.invalidate();
    }

    /**
     * Get the current value of the overlay bar.
     *
     * @return Current value of the overlay bar.
     */
    public float getOverlayBarValue() {

        return overlayBarValue;
    }

    /**
     * Sets a custom color formatter for the overlay bar.
     *
     * @param colorFormatter Color formatter.
     */
    public void setOverlayBarColor(SuperBar.ColorFormatter colorFormatter) {

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
    public void setOverlayBarColor(int color) {

        setOverlayBarColor(new SuperBar.ColorFormatter.Solid(color));
    }

    /**
     * Get overlay bar color.
     *
     * @return Overlay bar color.
     */
    public SuperBar.ColorFormatter getOverlayBarColor() {

        return this.overlayBarColor;
    }

    /**
     * Set a callback to be fired when the current bar selection
     * value is changed by the user.
     *
     * @param onSelectionChanged Selection changed callback.
     */
    @SuppressWarnings("unused")
    public void setOnSelectedChanged(SuperBar.OnSelectionChanged onSelectionChanged) {

        this.onSelectionChanged = onSelectionChanged;
    }

    /**
     * Get on selection changed.
     *
     * @return Selection changed.
     */
    public SuperBar.OnSelectionChanged getOnSelectionChanged() {

        return this.onSelectionChanged;
    }

    /***
     * Set a callback to be fired when the current bar selection
     * value if moved by the user.
     *
     * @param onSelectionMoved Selection moved callback.
     */
    @SuppressWarnings("unused")
    public void setOnSelectionMoved(SuperBar.OnSelectionMoved onSelectionMoved) {

        this.onSelectionMoved = onSelectionMoved;
    }

    /**
     * Get on selection moved.
     *
     * @return On selection moved.
     */
    public SuperBar.OnSelectionMoved getOnSelectionMoved() {

        return this.onSelectionMoved;
    }

    /**
     * Get the current value of the bar.
     *
     * @return Current value of the bar.
     */
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
    public void setBarValueBounds(float minBarValue, float maxBarValue) {

        this.maxBarValue = maxBarValue;
        this.minBarValue = minBarValue;
    }

    /**
     * Returns the maximum value the bar can display.
     *
     * @return Maximum value.
     */
    public float getMaxBarValue() {

        return maxBarValue;
    }

    /**
     * Returns the minimum value the bar can display.
     *
     * @return Minimum value.
     */
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
    public void setBarInterval(float barInterval) {

        this.barInterval = barInterval;
    }

    /**
     * Returns bar interval.
     *
     * @return Bar interval.
     */
    public float getBarInterval() {

        return barInterval;
    }

    /**
     * Sets a custom color formatter for the bar.
     *
     * @param colorFormatter Color formatter.
     */
    public void setColor(SuperBar.ColorFormatter colorFormatter) {

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
    public void setColor(int color) {

        setColor(new SuperBar.ColorFormatter.Solid(color));
    }

    /**
     * Get bar color formatter.
     *
     * @return Color formatter.
     */
    public SuperBar.ColorFormatter getColor() {

        return this.color;
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
     * Get gesture detector.
     *
     * @return Get gesture detector.
     */
    public GestureDetector getGestureDetector() {

        return this.gestureDetector;
    }

    /**
     * Set this to true to enable touch gestures on the bar control.
     *
     * @param touchEnabled Is touch enabled.
     */
    public void setTouchEnabled(boolean touchEnabled) {

        this.touchEnabled = touchEnabled;
    }

    /**
     * Is touch enabled.
     *
     * @return Touch enabled.
     */
    public boolean isTouchEnabled() {

        return this.touchEnabled;
    }

    /**
     * Set background color for the bar.
     *
     * @param backgroundColor Target background color.
     */
    public void setBackgroundColor(int backgroundColor) {

        this.backgroundColor = backgroundColor;
    }

    /**
     * Get background color for the bar.
     *
     * @return Background color for the bar.
     */
    public int getBackgroundColor() {

        return this.backgroundColor;
    }
}

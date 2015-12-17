package com.miguelgaeta.super_bar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
    }

    public interface SelectionChanged {

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
        void onValueChanged(float value, float maxValue, float minValue, SuperBar superBar);
    }

    public interface SelectionMoved {

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

    /** minimum value the bar can display */
    private float mMinVal = 0f;

    /** maximum value the bar can display */
    private float mMaxVal = 100f;

    /** the interval in which values can be chosen and displayed */
    private float mInterval = 1f;

    private RectF mBar;
    private RectF mBarBackground;
    private RectF mBarOverlay;

    private Paint mBarPaint;
    private Paint mBorderPaint;
    private Paint mOverlayPaint;

    private ObjectAnimator mAnimator;

    private boolean mDrawBorder = false;
    private boolean mDrawValueText = false;
    private boolean mTouchEnabled = true;

    private ColorFormatter mColorFormatter;

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
    private SelectionChanged mSelectionChanged;

    //@Setter @Getter TODO
    private SelectionMoved mSelectionMoved;

    //@Setter @Getter TODO
    private int controlShadowSize = 12;

    //@Setter @Getter @ColorInt TODO
    private int controlShadowColor = Color.argb(127, 0, 0, 0);

    //@Setter @Getter @ColorRes TODO
    private int controlColor = android.R.color.white;

    //@Setter @Getter TODO
    private int barMargin = 12;

    //@Setter @Getter @ColorInt TODO
    private int backgroundBarColor = Color.GREEN;

    //@Setter @Getter @ColorInt TODO
    private int overlayBarColor = Color.TRANSPARENT;

    //@Getter TODO
    private float overlayBarValue = 50f;

    //@Getter TODO
    private float barValue = 75f;

    public void setBarValue(float barValue) {

        this.barValue = barValue;

        invalidate();
    }

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

        mBar = new RectF();
        mBarBackground = new RectF();
        mBarOverlay = new RectF();

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(4);

        mOverlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOverlayPaint.setStyle(Paint.Style.FILL);

        mColorFormatter = new DefaultColorFormatterValue(Color.rgb(39, 140, 230));

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
            //mOverlayPaint.setColor(ContextCompat.getColor(getContext(), controlColor)); TODO
        }

        float length = ((getWidth() - (controlRadius * 2)) / (mMaxVal - mMinVal)) * (barValue - mMinVal);

        mBar.set(controlRadius, shadowRadius + halfMargin, length + controlRadius, getHeight() - shadowRadius - halfMargin);

        mBarPaint.setColor(mColorFormatter.getColor(barValue, mMaxVal, mMinVal));

        drawBackgroundBar(canvas, shadowRadius, halfMargin, controlRadius);

        // draw the value-bar
        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, mBarPaint);

        drawOverlayBar(canvas, shadowRadius, halfMargin, controlRadius);

        // draw the border
        if (mDrawBorder)
            canvas.drawRect(0, 0, getWidth(), getHeight(),
                mBorderPaint);

        // Dragging control.
        canvas.drawCircle(mBar.right, (getHeight() / 2f), controlRadius - shadowRadius, mOverlayPaint);
    }

    private void drawBackgroundBar(Canvas canvas, float halfShadow, float halfMargin, float controlRadius) {

        mBarBackground.set(controlRadius, halfShadow + halfMargin, getWidth() - controlRadius, getHeight() - halfShadow - halfMargin);

        paint.setColor(backgroundBarColor);

        canvas.drawRoundRect(mBarBackground, mBarBackground.height() / 2f, mBarBackground.height() / 2f, paint);
    }

    private void drawOverlayBar(Canvas canvas, float halfShadow, float halfMargin, float controlRadius) {

        float length = ((getWidth() - (controlRadius * 2)) / (mMaxVal - mMinVal)) * (overlayBarValue - mMinVal);

        mBarOverlay.set(length + controlRadius, halfShadow + halfMargin, getWidth(), getHeight() - halfShadow - halfMargin);

        paint.setColor(overlayBarColor);

        // mBarOverlay.height() / 2f, mBarOverlay.height() / 2f

        canvas.drawRect(mBarOverlay, paint);
    }

    /**
     * Sets the minimum and maximum value the bar can display.
     *
     * @param min Minimum value.
     * @param max Maximum value.
     */
    public void setMinMax(float min, float max) {
        mMaxVal = max;
        mMinVal = min;
    }

    /**
     * Returns the maximum value the bar can display.
     *
     * @return Maximum value.
     */
    public float getMax() {
        return mMaxVal;
    }

    /**
     * Returns the minimum value the bar can display.
     *
     * @return Minimum value.
     */
    public float getMin() {
        return mMinVal;
    }

    /**
     * Sets the interval in which the values can be chosen and dismayed from
     * on the ValueBar. If interval less than 0, there is no interval.
     *
     * @param interval Value interval.
     */
    public void setInterval(float interval) {
        mInterval = interval;
    }

    /**
     * Returns the interval in which values can be chosen and displayed.
     *
     * @return Value interval.
     */
    public float getInterval() {
        return mInterval;
    }

    /**
     * Returns the bar that represents the value.
     *
     * @return Bar rectangle.
     */
    public RectF getBar() {
        return mBar;
    }

    /**
     * Animates the bar from a specific value to a specific value.
     *
     * @param from Test
     * @param to Test
     * @param durationMillis Test
     */
    public void animate(float from, float to, int durationMillis) {

        if (from < mMinVal)
            from = mMinVal;
        if (from > mMaxVal)
            from = mMaxVal;

        if (to < mMinVal)
            to = mMinVal;
        if (to > mMaxVal)
            to = mMaxVal;

        barValue = from;
        mAnimator = ObjectAnimator.ofFloat(this, "barValue", from, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    /**
     * Animates the bar up from it's minimum value to the specified value.
     *
     * @param to Test
     * @param durationMillis Test
     */
    public void animateUp(float to, int durationMillis) {

        if (to > mMaxVal)
            to = mMaxVal;

        mAnimator = ObjectAnimator.ofFloat(this, "barValue", barValue, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    /**
     * Animates the bar down from it's current value to the specified value.
     *
     * @param to Test
     * @param durationMillis Test
     */
    public void animateDown(float to, int durationMillis) {

        if (to < mMinVal)
            to = mMinVal;

        mAnimator = ObjectAnimator.ofFloat(this, "barValue", barValue, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator va) {

        invalidate();
    }

    /**
     * Set this to true to enable drawing the border around the bar, or false to
     * disable it.
     *
     * @param enabled Test
     */
    public void setDrawBorder(boolean enabled) {
        mDrawBorder = enabled;
    }

    /**
     * Sets the width of the border around the bar (if drawn).
     *
     * @param width Test
     */
    public void setBorderWidth(float width) {
        mBorderPaint.setStrokeWidth(width);
    }

    /**
     * Sets the color of the border around the bar (if drawn).
     *
     * @param color Test
     */
    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
    }

    /**
     * Sets a custom BarColorFormatter for the ValueBar. Implement the
     * BarColorFormatter interface in your own formatter class and return
     * whatever color you like from the getColor(...) method. You can for
     * example make the color depend on the current value of the bar. Provide
     * null to reset all changes.
     *
     * @param formatter Test
     */
    public void setColorFormatter(ColorFormatter formatter) {

        if (formatter == null)
            formatter = new DefaultColorFormatterValue(Color.rgb(39, 140, 230));
        mColorFormatter = formatter;
    }

    /**
     * Sets the color the ValueBar should have.
     *
     * @param color Color
     */
    public void setColor(int color) {
        mColorFormatter = new DefaultColorFormatterValue(color);
    }

    /**
     * Returns the paint object that is used for drawing the bar.
     *
     * @return Test
     */
    public Paint getBarPaint() {
        return mBarPaint;
    }

    /**
     * Set this to true to enable touch gestures on the ValueBar.
     *
     * @param enabled Test
     */
    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
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
        return mMaxVal * factor;
    }

    /**
     * Sets a GestureDetector for the ValueBar to receive callbacks on gestures.
     *
     * @param gd Test
     */
    public void setGestureDetector(GestureDetector gd) {
        mGestureDetector = gd;
    }

    /** gesturedetector for recognizing single-taps */
    private GestureDetector mGestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) { // TODO
        if (mTouchEnabled) {

            // if the detector recognized a gesture, consume it
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(e))
                return true;

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    updateValue(x, y);
                    invalidate();
                case MotionEvent.ACTION_MOVE:
                    updateValue(x, y);
                    invalidate();

                    if (mSelectionMoved != null) {
                        mSelectionMoved.onSelectionMoved(barValue, mMaxVal, mMinVal, this);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    updateValue(x, y);
                    invalidate();

                    if (mSelectionChanged != null) {
                        mSelectionChanged.onValueChanged(barValue, mMaxVal, mMinVal, this);
                    }

                    break;
            }

            return true;
        }
        else
            return super.onTouchEvent(e);
    }

    /**
     * Updates the value on the ValueBar depending on the touch position.
     */
    private void updateValue(float x, float y) {

        float newVal;

        if (x <= 0)
            newVal = mMinVal;
        else if (x > getWidth())
            newVal = mMaxVal;
        else {
            float factor = x / getWidth();

            newVal = (mMaxVal - mMinVal) * factor + mMinVal;
        }

        if (mInterval > 0f) {

            float remainder = newVal % mInterval;

            // check if the new value is closer to the next, or the previous
            if (remainder <= mInterval / 2f) {

                newVal = newVal - remainder;
            } else {
                newVal = newVal - remainder + mInterval;
            }
        }

        barValue = newVal;
    }

    /**
     * Default BarColorFormatter class that supports a single color.
     */
    private class DefaultColorFormatterValue implements ColorFormatter {

        private int mColor;

        public DefaultColorFormatterValue(int color) {
            mColor = color;
        }

        @Override
        public int getColor(float value, float maxVal, float minVal) {
            return mColor;
        }
    }
}

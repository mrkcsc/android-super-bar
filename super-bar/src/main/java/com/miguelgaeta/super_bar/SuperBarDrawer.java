package com.miguelgaeta.super_bar;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Handles drawing and update operations for the super bar.
 *
 * Created by Miguel Gaeta on 12/17/15.
 */
class SuperBarDrawer {

    private final SuperBar sb;

    SuperBarDrawer(SuperBar superBar) {

        this.sb = superBar;
    }

    private RectF mBar = new RectF();

    void draw(Canvas canvas) {

        final float controlRadius = (sb.getHeight() / 2f);

        float barTop = controlRadius - sb.config.getBarHeight() / 2f;
        float barBot = controlRadius + sb.config.getBarHeight() / 2f;

        drawBackgroundBar(canvas, barTop, barBot, controlRadius);

        drawBar(canvas, barTop, barBot, controlRadius, sb.config.getBarValue());

        final float controlX = mBar.right;

        drawOverlayBar(canvas, barTop, barBot, controlRadius, sb.config.getOverlayBarValue());

        drawControl(canvas, controlX, controlRadius);
    }

    private void drawBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((sb.getWidth() - (controlRadius * 2)) / (sb.config.getMaxBarValue() - sb.config.getMinBarValue())) * (barValue - sb.config.getMinBarValue());

        mBar.set(controlRadius, barTop, length + controlRadius, barBot);

        sb.paint.setColor(sb.config.getColor().getColor(sb.config.getBarValue(), sb.config.getMaxBarValue(), sb.config.getMinBarValue()));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, sb.paint);
    }

    private void drawBackgroundBar(Canvas canvas, float barTop, float barBot, float controlRadius) {

        mBar.set(controlRadius, barTop, sb.getWidth() - controlRadius, barBot);

        sb.paint.setColor(sb.config.getBackgroundColor());

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, sb.paint);
    }

    private void drawOverlayBar(Canvas canvas, float barTop, float barBot, float controlRadius, float barValue) {

        float length = ((sb.getWidth() - (controlRadius * 2)) / (sb.config.getMaxBarValue() - sb.config.getMinBarValue())) * (barValue - sb.config.getMinBarValue());

        mBar.set(length + controlRadius, barTop, sb.getWidth() - controlRadius, barBot);

        sb.paint.setColor(sb.config.getOverlayBarColor().getColor(
            sb.config.getOverlayBarValue(),
            sb.config.getMaxBarValue(),
            sb.config.getMinBarValue()));

        canvas.drawRoundRect(mBar, mBar.height() / 2f, mBar.height() / 2f, sb.paint);
    }

    private void drawControl(Canvas canvas, float controlX, float controlRadius) {

        final float shadowRadius = sb.config.getControlShadowSize() / 2f;

        sb.paint.setColor(sb.config.getControlColor(), shadowRadius, sb.config.getControlShadowColor());

        // Dragging control.
        canvas.drawCircle(controlX, (sb.getHeight() / 2f), controlRadius - shadowRadius, sb.paint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (!sb.config.isTouchEnabled()) {

            return sb.onTouchEvent(motionEvent);
        }

        if (sb.config.getGestureDetector() != null &&
            sb.config.getGestureDetector().onTouchEvent(motionEvent)) {

            return true;
        }

        float x = motionEvent.getX();

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:

                updateValue(sb.config, x);

            case MotionEvent.ACTION_MOVE:

                updateValue(sb.config, x);

                if (sb.config.getOnSelectionMoved() != null) {
                    sb.config.getOnSelectionMoved().onSelectionMoved(
                        sb.config.getBarValue(),
                        sb.config.getMaxBarValue(),
                        sb.config.getMinBarValue(), sb);
                }

                break;

            case MotionEvent.ACTION_UP:

                updateValue(sb.config, x);

                if (sb.config.getOnSelectionChanged() != null) {
                    sb.config.getOnSelectionChanged().onSelectionChanged(
                        sb.config.getBarValue(),
                        sb.config.getMaxBarValue(),
                        sb.config.getMinBarValue(), sb);
                }

                break;
        }

        return true;
    }

    private void updateValue(SuperBarConfig config, float x) {

        float newVal;

        if (x <= 0) {
            newVal = config.getMinBarValue();

        } else if (x > sb.getWidth()) {

            newVal = config.getMaxBarValue();

        } else {

            float factor = x / sb.getWidth();

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

        sb.invalidate();
    }
}

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

    private RectF rect = new RectF();

    SuperBarDrawer(SuperBar superBar) {

        this.sb = superBar;
    }

    void draw(Canvas canvas) {

        float barTop = (sb.getHeight() / 2f) - sb.config.getBarHeight() / 2f;
        float barBot = (sb.getHeight() / 2f) + sb.config.getBarHeight() / 2f;

        drawBackgroundBar(canvas, barTop, barBot);

        drawBar(canvas, barTop, barBot, sb.config.getBarValue());

        drawOverlayBar(canvas, barTop, barBot, sb.config.getOverlayBarValue());

        drawControl(canvas, sb.config.getBarValue());
    }

    private void drawBar(Canvas canvas, float barTop, float barBot, float barValue) {

        float length = ((sb.getWidth() - (sb.config.getControlShadowRadius() * 2)) / (sb.config.getMaxBarValue() - sb.config.getMinBarValue())) * (barValue - sb.config.getMinBarValue());

        rect.set(sb.config.getControlShadowRadius(), barTop, length + sb.config.getControlShadowRadius(), barBot);

        sb.paint.setColor(sb.config.getColor().getColor(sb.config.getBarValue(), sb.config.getMaxBarValue(), sb.config.getMinBarValue()));

        canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, sb.paint);
    }

    private void drawBackgroundBar(Canvas canvas, float barTop, float barBot) {

        rect.set(sb.config.getControlShadowRadius(), barTop, sb.getWidth() - sb.config.getControlShadowRadius(), barBot);

        sb.paint.setColor(sb.config.getBackgroundColor());

        canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, sb.paint);
    }

    private void drawOverlayBar(Canvas canvas, float barTop, float barBot, float barValue) {

        float length = ((sb.getWidth() - (sb.config.getControlShadowRadius() * 2)) / (sb.config.getMaxBarValue() - sb.config.getMinBarValue())) * (barValue - sb.config.getMinBarValue());

        rect.set(length + sb.config.getControlShadowRadius(), barTop, sb.getWidth() - sb.config.getControlShadowRadius(), barBot);

        sb.paint.setColor(sb.config.getOverlayBarColor().getColor(
            sb.config.getOverlayBarValue(),
            sb.config.getMaxBarValue(),
            sb.config.getMinBarValue()));

        canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, sb.paint);
    }

    private void drawControl(Canvas canvas, float barValue) {

        float controlX = ((sb.getWidth() -
            (sb.config.getControlShadowRadius() * 2 + sb.config.getControlRadius() * 2)) /
            (sb.config.getMaxBarValue() - sb.config.getMinBarValue())) * (barValue - sb.config.getMinBarValue());

        controlX += sb.config.getControlRadius() + sb.config.getControlShadowRadius();

        sb.paint.setColor(
            sb.config.getControlColor(),
            sb.config.getControlShadowRadius(),
            sb.config.getControlShadowColor());

        canvas.drawCircle(controlX,
            sb.getHeight() / 2f,
            sb.config.getControlRadius(),
            sb.paint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (!sb.config.isTouchEnabled()) {

            return sb.onTouchEvent(motionEvent);
        }

        if (sb.config.getGestureDetector() != null &&
            sb.config.getGestureDetector().onTouchEvent(motionEvent)) {

            return true;
        }

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_MOVE:

                if (sb.config.getOnSelectionMoved() != null) {
                    sb.config.getOnSelectionMoved().onSelectionMoved(
                        sb.config.getBarValue(),
                        sb.config.getMaxBarValue(),
                        sb.config.getMinBarValue(), sb);
                }

                break;

            case MotionEvent.ACTION_UP:

                if (sb.config.getOnSelectionChanged() != null) {
                    sb.config.getOnSelectionChanged().onSelectionChanged(
                        sb.config.getBarValue(),
                        sb.config.getMaxBarValue(),
                        sb.config.getMinBarValue(), sb);
                }

                break;
        }

        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:

                float barValue;

                if (motionEvent.getX() <= 0) {

                    barValue = sb.config.getMinBarValue();

                } else if (motionEvent.getX() > sb.getWidth()) {

                    barValue = sb.config.getMaxBarValue();

                } else {

                    float factor = motionEvent.getX() / sb.getWidth();

                    barValue = (sb.config.getMaxBarValue() - sb.config.getMinBarValue()) * factor + sb.config.getMinBarValue();
                }

                if (sb.config.getBarInterval() > 0f) {

                    float remainder = barValue % sb.config.getBarInterval();

                    if (remainder <= sb.config.getBarInterval() / 2f) {

                        barValue = barValue - remainder;
                    } else {
                        barValue = barValue - remainder + sb.config.getBarInterval();
                    }
                }

                sb.config.setBarValue(null, barValue);
                sb.invalidate();

                break;
        }

        return true;
    }
}

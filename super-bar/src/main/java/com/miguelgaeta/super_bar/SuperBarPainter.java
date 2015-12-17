package com.miguelgaeta.super_bar;

import android.graphics.Paint;
import android.view.View;

/**
 * Simple utility class for a paint object.
 *
 * Created by Miguel Gaeta on 12/17/15.
 */
class SuperBarPainter extends Paint {

    private final View view;

    /**
     * Paint instance with a view subclass for
     * simple configuration.
     *
     * @param view Associated view to paint.
     */
    SuperBarPainter(View view) {
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
    void setColor(int color, Float shadowRadius, int shadowColor) {

        if (!view.isInEditMode() && shadowRadius != null) {

            setShadowLayer(shadowRadius, 0f, 0f, shadowColor);

        } else {

            clearShadowLayer();
        }

        super.setColor(color);
    }
}

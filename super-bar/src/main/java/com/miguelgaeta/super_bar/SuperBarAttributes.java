package com.miguelgaeta.super_bar;

import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Handles parsing all xml attributes of the super bar.
 *
 * Created by Miguel Gaeta on 12/17/15.
 */
class SuperBarAttributes {

    private final SuperBar sb;

    /**
     * Initialize with instance of a super bar.
     *
     * @param superBar The super bar.
     */
    SuperBarAttributes(SuperBar superBar) {

        this.sb = superBar;
    }

    void parse(AttributeSet attrs, int defStyleAttr) {

        final TypedArray array = sb.getContext().obtainStyledAttributes(attrs, R.styleable.SuperBar, defStyleAttr, 0);

        try {

            final SuperBarConfig config = sb.getConfig();

            config.setBarValueBounds(
                array.getFloat(R.styleable.SuperBar_sb_barValueMin, config.getMinBarValue()),
                array.getFloat(R.styleable.SuperBar_sb_barValueMax, config.getMaxBarValue()));

            config.setBarHeight(
                array.getDimensionPixelSize(R.styleable.SuperBar_sb_barHeight, 0));

            config.setBarInterval(array.getFloat(R.styleable.SuperBar_sb_barInterval,
                config.getBarInterval()));

            config.setColor(array.getColor(R.styleable.SuperBar_sb_barColor,
                config.getColor().getColor(
                    config.getBarValue(),
                    config.getMaxBarValue(),
                    config.getMinBarValue())));

            config.setBackgroundColor(array.getColor(R.styleable.SuperBar_sb_barBackgroundColor,
                config.getBackgroundColor()));

            config.setTouchEnabled(array.getBoolean(R.styleable.SuperBar_sb_barTouchEnabled, config.isTouchEnabled()));

            config.setOverlayBarValue(array.getFloat(R.styleable.SuperBar_sb_barOverlayValue, config.getOverlayBarValue()));
            config.setOverlayBarColor(array.getColor(R.styleable.SuperBar_sb_barOverlayColor,
                config.getOverlayBarColor().getColor(
                    config.getOverlayBarValue(),
                    config.getMaxBarValue(),
                    config.getMinBarValue())));

            config.setControlRadius(array.getDimensionPixelSize(R.styleable.SuperBar_sb_barControlRadius, 0));

            config.setControlColor(array.getColor(R.styleable.SuperBar_sb_barControlColor,
                config.getControlColor()));

            config.setControlShadowColor(array.getColor(R.styleable.SuperBar_sb_barControlShadowColor,
                config.getControlShadowColor()));

            config.setControlShadowRadius(array.getDimensionPixelSize(R.styleable.SuperBar_sb_barControlShadowRadius,
                config.getControlShadowRadius()));

            config.setBarValue(null, array.getFloat(R.styleable.SuperBar_sb_barValue,
                config.getBarValue()));

        } finally {

            array.recycle();
        }
    }
}

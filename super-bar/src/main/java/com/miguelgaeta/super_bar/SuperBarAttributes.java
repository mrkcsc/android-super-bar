package com.miguelgaeta.super_bar;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Handles parsing all xml attributes of the super bar.
 *
 * Created by Miguel Gaeta on 12/17/15.
 */
class SuperBarAttributes {

    private final SuperBar superBar;

    /**
     * Initialize with instance of a super bar.
     *
     * @param superBar The super bar.
     */
    SuperBarAttributes(SuperBar superBar) {

        this.superBar = superBar;
    }

    void parse(AttributeSet attrs, int defStyleAttr) {

        final TypedArray array = superBar.getContext().obtainStyledAttributes(attrs, R.styleable.SuperBar, defStyleAttr, 0);

        try {

            superBar.getConfig().setBarValueBounds(
                array.getFloat(R.styleable.SuperBar_sb_barValueMin, 0f),
                array.getFloat(R.styleable.SuperBar_sb_barValueMax, 100f));

            superBar.getConfig().setBarMargin(array.getDimensionPixelSize(R.styleable.SuperBar_sb_barMargin, 12));
            superBar.getConfig().setBarInterval(array.getFloat(R.styleable.SuperBar_sb_barInterval, 1f));

            superBar.getConfig().setColor(array.getColor(R.styleable.SuperBar_sb_color, Color.BLUE));
            superBar.getConfig().setBackgroundColor(array.getColor(R.styleable.SuperBar_sb_backgroundColor, Color.GREEN));

            superBar.getConfig().setTouchEnabled(array.getBoolean(R.styleable.SuperBar_sb_barTouchEnabled, true));

            superBar.getConfig().setOverlayBarValue(array.getFloat(R.styleable.SuperBar_sb_barOverlayValue, 80f));
            superBar.getConfig().setOverlayBarColor(array.getColor(R.styleable.SuperBar_sb_barOverlayColor, Color.RED));

            superBar.getConfig().setBarValue(null, array.getFloat(R.styleable.SuperBar_sb_barValue, 10f));

        } finally {

            array.recycle();
        }
    }
}

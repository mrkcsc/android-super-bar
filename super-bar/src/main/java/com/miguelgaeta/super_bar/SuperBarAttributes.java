package com.miguelgaeta.super_bar;

import android.content.res.TypedArray;
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

            superBar.getConfig().setBarValue(null, array.getFloat(R.styleable.SuperBar_sb_barValue, 10f));

        } finally {

            array.recycle();
        }
    }
}

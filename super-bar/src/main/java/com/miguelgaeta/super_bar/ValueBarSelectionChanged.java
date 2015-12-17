package com.miguelgaeta.super_bar;

/**
 * Created by Miguel Gaeta on 6/11/15.
 */
public interface ValueBarSelectionChanged {

    /**
     * Called when the user releases his finger from the ValueBar.
     *
     * @param val
     * @param maxval
     * @param minval
     * @param bar
     */
    void onValueChanged(float val, float maxval, float minval, ValueBar bar);
}

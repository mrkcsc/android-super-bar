package com.miguelgaeta.super_bar;

/**
 * Created by Miguel Gaeta on 6/12/15.
 */
public interface ValueBarSelectionMoved {

    /**
     * Called every time the user moves the finger on the ValueBar.
     *
     * @param val
     * @param maxval
     * @param minval
     * @param bar
     */
    void onSelectionMoved(float val, float maxval, float minval, ValueBar bar);
}

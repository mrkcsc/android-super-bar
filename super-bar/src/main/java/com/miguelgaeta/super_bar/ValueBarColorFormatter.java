package com.miguelgaeta.super_bar;

/**
 * Created by Miguel Gaeta on 6/11/15.
 */
public interface ValueBarColorFormatter {

    /**
     * Use this method to return whatever color you like the ValueBar to have.
     * You can also make use of the current value the bar has.
     *
     * @param value
     * @param maxVal the maximum value the bar can display
     * @param minVal the minimum value the bar can display
     * @return
     */
    int getColor(float value, float maxVal, float minVal);
}

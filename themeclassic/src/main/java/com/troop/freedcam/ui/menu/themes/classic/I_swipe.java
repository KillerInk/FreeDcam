package com.troop.freedcam.ui.menu.themes.classic;

/**
 * Created by troop on 29.09.2014.
 */
public interface I_swipe
{
    /*
    Gets called when a horizontal swipe is detected
     */
    void doHorizontalSwipe();

    /**
     * Gets called on vertical swipe detected
     */
    void doVerticalSwipe();

    /**
     * Gets called when a click is detected
     * @param x the x axis
     * @param y the y axis
     */
    void onClick(int x, int y);
}

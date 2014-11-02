package com.troop.freedcam.ui.menu;


/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner extends TouchHandler
{
    I_swipe swipehandler;

    public SwipeMenuListner(I_swipe swipehandler)
    {
        this.swipehandler = swipehandler;
    }

    protected void doHorizontalSwipe()
    {
        if (swipehandler != null)
            swipehandler.doHorizontalSwipe();
    }

    protected void doVerticalSwipe()
    {
        if (swipehandler!= null)
            swipehandler.doVerticalSwipe();
    }

}

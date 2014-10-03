package com.troop.freecamv2.ui.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.troop.freecam.R;


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

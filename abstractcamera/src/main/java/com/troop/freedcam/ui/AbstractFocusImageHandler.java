package com.troop.freedcam.ui;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_Focus;

/**
 * Created by troop on 09.06.2015.
 */
public abstract class AbstractFocusImageHandler implements I_Focus
{
    protected ImageView focusImageView;
    /**
     * Holds a referenz to the activity to get the size of the preview etc
     */
    protected I_Activity activity;
    /**
     * Holds toplayerd fragment that has created this
     */
    protected Fragment fragment;

    /**
     *
     * @param view the view that contains the focus imageviews
     * @param fragment the toplayerd fragment wich create this
     * @param activity Holds a referenz to the activity to get the size of the preview etc
     */
    public AbstractFocusImageHandler(View view, Fragment fragment, I_Activity activity)
    {
        this.activity = activity;
        this.fragment = fragment;
    }


}

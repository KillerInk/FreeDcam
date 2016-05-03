package com.troop.freedcam.ui;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.interfaces.I_Focus;

/**
 * Created by troop on 09.06.2015.
 */
public abstract class AbstractFocusImageHandler implements I_Focus
{
    protected ImageView focusImageView;
    /**
     * Holds toplayerd fragment that has created this
     */
    protected Fragment fragment;

    /**
     *
     * @param view the view that contains the focus imageviews
     * @param fragment the toplayerd fragment wich create this
     */
    protected AbstractFocusImageHandler(View view, Fragment fragment)
    {
        this.fragment = fragment;
    }


}

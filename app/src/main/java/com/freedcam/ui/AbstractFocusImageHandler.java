/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.freedcam.apis.basecamera.interfaces.I_Focus;

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

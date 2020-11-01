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

package com.troop.freedcam.cameraui.service;


import android.view.MotionEvent;

/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner extends TouchHandler
{
    private final I_swipe swipehandler;

    public SwipeMenuListner(I_swipe swipehandler)
    {
        this.swipehandler = swipehandler;
    }

    protected void doLeftToRightSwipe()
    {
        if (swipehandler != null)
            swipehandler.doLeftToRightSwipe();
    }

    protected void doRightToLeftSwipe()
    {
        if (swipehandler != null)
            swipehandler.doRightToLeftSwipe();
    }

    protected void doTopToBottomSwipe()
    {
        if (swipehandler != null)
            swipehandler.doTopToBottomSwipe();
    }

    protected void doBottomToTopSwipe()
    {
        if (swipehandler != null)
            swipehandler.doBottomToTopSwipe();
    }

    @Override
    protected void OnClick(int x, int y) {
        swipehandler.onClick(x,y);
    }

    @Override
    protected void OnMotionEvent(MotionEvent event) {
        if (swipehandler != null)
            swipehandler.onMotionEvent(event);
    }

    public void LeftToRightSwipe()
    {
        if (swipehandler != null)
            swipehandler.doLeftToRightSwipe();
    }

    public void RightToLeftSwipe()
    {
        if (swipehandler != null)
            swipehandler.doRightToLeftSwipe();
    }

    public void TopToBottomSwipe()
    {
        if (swipehandler != null)
            swipehandler.doTopToBottomSwipe();
    }

    public void BottomToTopSwipe()
    {
        if (swipehandler != null)
            swipehandler.doBottomToTopSwipe();
    }
}

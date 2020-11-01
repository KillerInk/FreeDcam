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
 * Created by troop on 29.09.2014.
 */
public interface I_swipe
{

    void doLeftToRightSwipe();

    void doRightToLeftSwipe();

    void doTopToBottomSwipe();

    void doBottomToTopSwipe();


    /**
     * Gets called when a click is detected
     * @param x the x axis
     * @param y the y axis
     */
    void onClick(int x, int y);
    void onMotionEvent(MotionEvent event);
}

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

package com.freedcam.apis.basecamera.interfaces;

import android.view.MotionEvent;

import com.freedcam.apis.basecamera.FocusRect;

/**
 * Created by troop on 02.09.2014.
 */
public interface FocusHandlerInterface
{
    void FocusStarted(FocusRect rect);
    void FocusFinished(boolean success);
    void FocusLocked(boolean locked);
    void TouchToFocusSupported(boolean isSupported);
    void AEMeteringSupported(boolean isSupported);
    boolean onTouchEvent(MotionEvent event);
}
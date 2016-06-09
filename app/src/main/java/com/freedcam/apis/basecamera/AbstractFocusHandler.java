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

package com.freedcam.apis.basecamera;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.freedcam.apis.basecamera.interfaces.I_Focus;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    private final String TAG = AbstractFocusHandler.class.getSimpleName();
    public void StartFocus(){}
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height){}
    public void SetMeteringAreas(FocusRect meteringRect, int width, int height){}
    public I_Focus focusEvent;
    public abstract boolean isAeMeteringSupported();
    public abstract void SetMotionEvent(MotionEvent event);

    protected void logFocusRect(FocusRect rect)
    {
        Logger.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }

    protected void logRect(Rect rect)
    {
        Logger.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }
}

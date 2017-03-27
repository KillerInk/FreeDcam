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

package freed.cam.apis.basecamera;

import android.graphics.Rect;
import freed.utils.Log;
import android.view.MotionEvent;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    private final String TAG = AbstractFocusHandler.class.getSimpleName();
    protected CameraWrapperInterface cameraUiWrapper;

    public AbstractFocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public abstract void StartFocus();
    public abstract void StartTouchToFocus(int x, int y,int width, int height);
    public abstract void SetMeteringAreas(int x, int y, int width, int height);
    public FocusHandlerInterface focusEvent;
    public abstract boolean isAeMeteringSupported();
    public abstract void SetMotionEvent(MotionEvent event);


    protected void logFocusRect(Rect rect)
    {
        Log.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }

    protected void logRect(Rect rect)
    {
        Log.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }
}

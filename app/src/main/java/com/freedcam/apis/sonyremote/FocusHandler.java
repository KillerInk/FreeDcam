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

package com.freedcam.apis.sonyremote;

import android.view.MotionEvent;

import com.freedcam.apis.basecamera.AbstractFocusHandler;
import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.CameraFocusEvent;
import com.freedcam.apis.basecamera.modules.I_Callbacks.AutoFocusCallback;
import com.freedcam.apis.sonyremote.parameters.ParameterHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 31.01.2015.
 */
public class FocusHandler extends AbstractFocusHandler implements AutoFocusCallback
{
    private I_CameraUiWrapper cameraUiWrapper;
    private CameraHolder cameraHolder;
    private ParameterHandler parametersHandler;
    private final String TAG = FocusHandler.class.getSimpleName();
    private boolean isFocusing = false;

    public FocusHandler(I_CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraHolder = (CameraHolder) cameraUiWrapper.GetCameraHolder();
        parametersHandler = (ParameterHandler)cameraUiWrapper.GetParameterHandler();
    }

    @Override
    public void StartFocus() {
        super.StartFocus();
    }

    @Override
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height)
    {
        if (parametersHandler == null)
            return;
        if (isFocusing)
        {
            cameraHolder.CancelFocus();
            Logger.d(TAG, "Canceld Focus");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }

        double x = rect.left + (rect.right - rect.left)/2  ;
        double y = rect.top + (rect.bottom - rect.top )  /2;
        double xproz = x / (double)width * 100;
        double yproz = y / (double)height *100;
        Logger.d(TAG, "set focus to: x: " + xproz + " y: " +yproz);
        cameraHolder.StartFocus(this);
        cameraHolder.SetTouchFocus(xproz, yproz);
        isFocusing = true;
        if (focusEvent != null)
            focusEvent.FocusStarted(rect);
    }

    @Override
    public boolean isAeMeteringSupported() {
        return false;
    }

    @Override
    public void SetMotionEvent(MotionEvent event) {
        ((SonyCameraFragment)cameraUiWrapper).getSurfaceView().onTouchEvent(event);
    }


    @Override
    public void onAutoFocus(CameraFocusEvent event)
    {
        isFocusing = false;
        if (focusEvent != null) {
            focusEvent.FocusFinished(event.success);
            focusEvent.FocusLocked(cameraHolder.canCancelFocus());
        }

    }

    @Override
    public void onFocusLock(boolean locked) {
        if (focusEvent != null) {
            focusEvent.FocusLocked(locked);
        }
    }


}



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

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import com.freedcam.apis.basecamera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.modules.I_Callbacks.AutoFocusCallback;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 * holds the instance for the camera to work with
 */
public abstract class AbstractCameraHolder implements I_CameraHolder
{
    protected boolean isRdy = false;
    //holds the parameters for the camera
    private AbstractParameterHandler ParameterHandler;
    //handel focus realted stuff
    public AbstractFocusHandler Focus;
    //the listner to for camera state changes
    protected I_CameraChangedListner cameraChangedListner;
    //handler wich runs in mainthread
    protected Handler UIHandler;
    //holds the appsettings
    public AppSettingsManager appSettingsManager;

    /**
     *
     * @param cameraChangedListner to listen on camera state changes
     * @param appSettingsManager
     */
    protected AbstractCameraHolder(I_CameraChangedListner cameraChangedListner, AppSettingsManager appSettingsManager)
    {
        this.cameraChangedListner = cameraChangedListner;
        this.appSettingsManager = appSettingsManager;
        UIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Send message to UI
     * @param msg to send
     */
    public void SendUIMessage(String msg)
    {
        if (cameraChangedListner != null)
            cameraChangedListner.onCameraError(msg);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        return false;
    }

    @Override
    public void CloseCamera() {

    }

    /**
     *
     * @return the count of avail cameras
     */
    @Override
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
        return false;
    }

    @Override
    public boolean SetSurface(SurfaceHolder texture) {
        return false;
    }

    @Override
    public void StartPreview()
    {

    }

    @Override
    public void StopPreview()
    {
    }


    public void StartFocus(AutoFocusCallback autoFocusCallback){}
    public void CancelFocus(){}

    public abstract void SetLocation(Location loc);


    public void SetParameterHandler(AbstractParameterHandler parametersHandler)
    {
        ParameterHandler = parametersHandler;
    }

    public AbstractParameterHandler GetParameterHandler()
    {
        return ParameterHandler;
    }
}

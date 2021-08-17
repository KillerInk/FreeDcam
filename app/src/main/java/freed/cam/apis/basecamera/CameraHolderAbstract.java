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

import android.location.Location;

import freed.cam.event.camera.CameraHolderEventHandler;

/**
 * Created by troop on 12.12.2014.
 * holds the instance for the camera to work with
 */
public abstract class CameraHolderAbstract implements CameraHolderInterface
{
    protected CameraWrapperInterface cameraUiWrapper;
    private CameraHolderEventHandler cameraHolderEventHandler;

    /**
     *
     * @param cameraUiWrapper to listen on camera state changes
     */
    protected CameraHolderAbstract(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public abstract boolean OpenCamera(int camera);

    @Override
    public abstract void CloseCamera();

    public abstract void StartFocus(FocusEvents autoFocusCallback);
    public abstract void CancelFocus();

    public abstract void SetLocation(Location loc);

    @Override
    public void addEventListner(CameraHolderEventHandler event)
    {
        cameraHolderEventHandler = event;
    }

    @Override
    public CameraHolderEventHandler getCameraHolderEventHandler() {
        return cameraHolderEventHandler;
    }

    public void fireCameraOpen()
    {
        if (cameraHolderEventHandler != null)
            cameraHolderEventHandler.fireOnCameraOpen();
    }

    public void fireCameraOpenFinished()
    {
        if (cameraHolderEventHandler != null)
            cameraHolderEventHandler.fireOnCameraOpenFinished();
    }

    public void fireCameraClose()
    {
        if (cameraHolderEventHandler != null)
            cameraHolderEventHandler.fireOnCameraClose();
    }

    public void fireOnCameraChangedAspectRatioEvent(Size sie)
    {
        if (cameraHolderEventHandler != null)
            cameraHolderEventHandler.fireOnCameraChangedAspectRatioEvent(sie);
    }


    public void fireOCameraError(String error)
    {
        if (cameraHolderEventHandler != null)
            cameraHolderEventHandler.fireOnCameraError(error);
    }
}

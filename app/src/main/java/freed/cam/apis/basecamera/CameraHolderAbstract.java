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
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

/**
 * Created by troop on 12.12.2014.
 * holds the instance for the camera to work with
 */
public abstract class CameraHolderAbstract implements CameraHolderInterface
{
    protected boolean isRdy;
    //handler wich runs in mainthread
    protected Handler UIHandler;

    protected CameraWrapperInterface cameraUiWrapper;

    //protected ModuleHandlerAbstract moduleHandler;

    /**
     *
     * @param cameraUiWrapper to listen on camera state changes
     */
    protected CameraHolderAbstract(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        //this.moduleHandler = cameraUiWrapper.getModuleHandler();
        UIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Send message to UI
     * @param msg to send
     */
    public void SendUIMessage(String msg)
    {
        if (cameraUiWrapper != null)
            cameraUiWrapper.onCameraError(msg);
    }

    @Override
    public abstract boolean OpenCamera(int camera);

    @Override
    public abstract void CloseCamera();

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
        return isRdy;
    }

    @Override
    public boolean SetSurface(SurfaceHolder texture) {
        return false;
    }

    @Override
    public abstract void StartPreview();


    @Override
    public abstract void StopPreview();



    public abstract void StartFocus(FocusEvents autoFocusCallback);
    public abstract void CancelFocus();

    public abstract void SetLocation(Location loc);

}

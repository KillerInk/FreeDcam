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

package com.troop.freedcam.camera.camera1.cameraholder;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.CameraHolder;
import com.troop.freedcam.camera.camera1.lge.hardware.LGCameraRef;
import com.troop.freedcam.camera.events.CameraStateEvents;
import com.troop.freedcam.settings.Frameworks;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 08.06.2016.
 */
public class CameraHolderLG extends CameraHolder
{
    private LGCameraRef lgCamera;
    public CameraHolderLG(CameraControllerInterface cameraUiWrapper, Frameworks frameworks) {
        super(cameraUiWrapper,frameworks);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        boolean isRdy = false;
        try {
            if (SettingsManager.get(SettingKeys.openCamera1Legacy).get()) {
                lgCamera = new LGCameraRef(camera, 256);
                Log.d(CameraHolderLG.class.getSimpleName(), "open LG camera legacy");
            }
            else {
                lgCamera = new LGCameraRef(camera);
                Log.d(CameraHolderLG.class.getSimpleName(), "open LG camera");
            }
            mCamera = lgCamera.getCamera();
            isRdy = true;
        }
        catch (RuntimeException  | NoClassDefFoundError ex)
        {
            if (mCamera != null)
                mCamera.release();
            Log.WriteEx(ex);
            try {
                super.OpenCamera(camera);
            }
            catch (RuntimeException  | NoClassDefFoundError e) {
                CameraStateEvents.fireCameraErrorEvent("Fail to connect to camera service");
                isRdy = false;
                Log.WriteEx(e);
            }
        }

        CameraStateEvents.fireCameraOpenEvent();
        return isRdy;
    }

    @Override
    public void CloseCamera() {
        super.CloseCamera();
        lgCamera = null;
    }
}

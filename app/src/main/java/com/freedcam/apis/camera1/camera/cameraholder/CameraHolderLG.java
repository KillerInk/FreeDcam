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

package com.freedcam.apis.camera1.camera.cameraholder;

import android.os.Handler;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.lge.hardware.LGCamera;

/**
 * Created by troop on 08.06.2016.
 */
public class CameraHolderLG extends CameraHolder
{
    public CameraHolderLG(I_CameraChangedListner cameraChangedListner, AppSettingsManager appSettingsManager, Frameworks frameworks) {
        super(cameraChangedListner, appSettingsManager, frameworks);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        LGCamera lgCamera;
        try {
            if (appSettingsManager.getDevice() == DeviceUtils.Devices.LG_G4)
                lgCamera = new LGCamera(camera, 256);
            else
                lgCamera = new LGCamera(camera);
            mCamera = lgCamera.getCamera();
            isRdy = true;
        }
        catch (RuntimeException ex)
        {
            cameraChangedListner.onCameraError("Fail to connect to camera service");
            isRdy = false;
        }

        cameraChangedListner.onCameraOpen("");
        super.OpenCamera(0);
        return isRdy;
    }
}

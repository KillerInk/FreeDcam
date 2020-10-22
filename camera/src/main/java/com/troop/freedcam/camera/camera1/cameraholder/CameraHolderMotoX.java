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

import android.hardware.Camera;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.settings.Frameworks;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 17.06.2016.
 */
public class CameraHolderMotoX extends CameraHolderLegacy {
    public CameraHolderMotoX(CameraControllerInterface cameraUiWrapper, Frameworks frameworks) {
        super(cameraUiWrapper, frameworks);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        boolean isRdy;
        Log.d(CameraHolderLG.class.getSimpleName(), "open Motox camera");
        isRdy = super.OpenCamera(camera);
        try {
            Camera.Parameters paras = mCamera.getParameters();
            paras.set("mot-app", "true");
            mCamera.setParameters(paras);
            isRdy = true;
        }
        catch (RuntimeException ex)
        {
            CameraStateEvents.fireCameraErrorEvent("Fail to connect to camera service");
            isRdy = false;
        }
        return isRdy;
    }
}

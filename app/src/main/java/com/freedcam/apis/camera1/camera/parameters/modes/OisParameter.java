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

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 26.05.2015.
 */
public class OisParameter extends BaseModeParameter {
    /**
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     * @param values
     */
    public OisParameter(Handler uihandler, Camera.Parameters parameters, CameraHolder cameraHolder, String values) {
        super(parameters, cameraHolder, "", "");
    }

    @Override
    public boolean IsSupported() {
        return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G2pro) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || DeviceUtils.IS(DeviceUtils.Devices.p8lite) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            parameters.set("ois-ctrl", valueToSet);
        else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5))
            parameters.set("ois", valueToSet);
        else
            parameters.set("hw_ois_enable", valueToSet);
        cameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public String GetValue() {
        return parameters.get("ois-ctrl");
    }

    @Override
    public String[] GetValues() {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G2pro)  || DeviceUtils.IS(DeviceUtils.Devices.LG_G4) )
        return new String[] {
                "preview-capture","capture","video","centering-only","centering-off"
        };
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5))
        {
            return new String[] {
                    "enable,disable"
            };
        }
        else
            return new String[] {
                    "on,off"
            };
    }
}

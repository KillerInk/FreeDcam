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

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by GeorgeKiarie on 9/24/2015.
 */
public class VirtualLensFilter extends  BaseModeParameter {

    private CameraHolder cameraHolder;
    private CameraUiWrapper cameraUiWrapper;

    private final int[] asT = new int[]{0, 1, 2, 3, 4, 5, 6};
    private final String[] asU = new String[]{"", "100 0 0 0 100 0 0 0 100 0 0 80", "100 0 0 0 100 0 0 0 100 12 50 100",
            "100 0 0 0 100 0 0 0 100 0 100 100", "100 0 0 0 100 0 0 0 100 0 85 0", "100 0 0 0 100 0 0 0 100 80 80 0"
            , "100 0 0 0 100 0 0 0 100 80 0 0", "100 0 0 0 100 0 0 0 50 115 20 70", "100 0 0 0 100 0 0 0 40 -60 -60 -60"
            , "100 0 0 0 100 0 0 0 40 -60 -60 -60", "100 0 0 0 100 0 0 0 40 -60 -60 -60", "100 0 0 0 100 0 0 0 40 -60 -60 -60"};
    public VirtualLensFilter(Camera.Parameters parameters, CameraHolder parameterChanged, String values, CameraUiWrapper cameraUiWrapper)
    {
        super(parameters, parameterChanged, "", "");

        if (cameraHolder.appSettingsManager.getDevice() ==(DeviceUtils.Devices.ZTE_ADV))
            this.isSupported = true;
        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues()
    {
        return new String[]{"Off","Red","Orange","Yellow","Green","Cyan","Blue","Purple","Grad Left","Grad Right","Grad Top","Grad Bottom"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        switch (valueToSet)
        {
            case "Off":
                parameters.set("color-filter-type", ""+asT[0]);
                break;
            case "Red":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[1]);
                break;

            case "Orange":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[2]);
                break;
            case "Yellow":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[3]);
                break;
            case "Green":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[4]);
                break;
            case "Cyan":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[5]);
                break;
            case "Blue":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[6]);
                break;
            case "Purple":
                parameters.set("color-filter-type", ""+asT[1]);
                parameters.set("color-filter-param", asU[7]);
                break;
            case "Grad Left":
                parameters.set("color-filter-type", ""+asT[4]);
                parameters.set("color-filter-param", asU[8]);
                break;
            case "Grad Right":
                parameters.set("color-filter-type", ""+asT[3]);
                parameters.set("color-filter-param", asU[9]);
                break;
            case "Grad Top":
                parameters.set("color-filter-type", ""+asT[5]);
                parameters.set("color-filter-param", asU[10]);
                break;
            case "Grad Bottom":
                parameters.set("color-filter-type", ""+asT[6]);
                parameters.set("color-filter-param", asU[11]);
                break;


        }
        cameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public String GetValue()
    {
        return "Off";
    }
}
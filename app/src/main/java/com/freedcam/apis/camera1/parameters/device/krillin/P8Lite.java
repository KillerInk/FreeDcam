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

package com.freedcam.apis.camera1.parameters.device.krillin;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.FocusManualHuawei;
import com.freedcam.apis.camera1.parameters.manual.ShutterManualKrillin;
import com.troop.androiddng.DngProfile;

import java.util.ArrayList;

/**
 * Created by troop on 01.06.2016.
 */
public class P8Lite extends AbstractDevice {


    public P8Lite(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualKrillin(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualHuawei(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        return null;
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
        ArrayList<Camera.Area> ar = new ArrayList<>();
        ar.add(a);
        parameters.setFocusAreas(ar);
        ((ParametersHandler)parametersHandler).SetParametersToCamera(parameters);
    }

    @Override
    public AbstractManualParameter getManualBrightness() {
        return  new BaseManualParameter(parameters, "brightness", "max-brightness", "min-brightness", cameraUiWrapper, 50);
    }

    @Override
    public AbstractManualParameter getManualContrast() {
        return new BaseManualParameter(parameters,"contrast", "max-contrast", "min-contrast",cameraUiWrapper,25);
    }
}

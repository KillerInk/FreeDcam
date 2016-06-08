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

package com.freedcam.apis.camera1.parameters.device;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.CameraUiWrapper;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.modes.VideoProfilesParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 31.05.2016.
 */
public abstract class AbstractDevice
{
    protected Parameters parameters;
    protected CameraHolder cameraHolder;
    protected CameraUiWrapper cameraUiWrapper;
    protected ParametersHandler parametersHandler;
    protected MatrixChooserParameter matrixChooserParameter;

    public AbstractDevice(Parameters parameters, CameraUiWrapper cameraUiWrapper)
    {
        this.parameters = parameters;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraHolder = cameraUiWrapper.cameraHolder;
        parametersHandler = (ParametersHandler) cameraUiWrapper.parametersHandler;
        if (IsDngSupported())
        {
            matrixChooserParameter = new MatrixChooserParameter();
            parametersHandler.matrixChooser = matrixChooserParameter;
        }
    }

    public abstract boolean IsDngSupported();

    public abstract AbstractManualParameter getExposureTimeParameter();

    public abstract AbstractManualParameter getIsoParameter();

    public abstract AbstractManualParameter getManualFocusParameter();

    public abstract AbstractManualParameter getCCTParameter();

    public AbstractManualParameter getSkintoneParameter() {
        return null;
    }

    public AbstractManualParameter getManualSaturation()
    {
        BaseManualParameter ManualSaturation = null;
        //p920 hack
        if (parameters.get(KEYS.MAX_SATURATION)!= null && parameters.get(KEYS.SATURATION_MAX)!= null) {
            parameters.set(KEYS.MAX_SATURATION, 100);
            parameters.set(KEYS.MIN_SATURATION, 0);
        }
        //check first max after evo 3d has both but max infront is empty
        if (parameters.get(KEYS.SATURATION_MAX)!= null)
            ManualSaturation = new BaseManualParameter(parameters, KEYS.SATURATION, KEYS.SATURATION_MAX, KEYS.SATURATION_MIN, parametersHandler,1);
        else if (parameters.get(KEYS.MAX_SATURATION)!= null)
            ManualSaturation = new BaseManualParameter(parameters, KEYS.SATURATION, KEYS.MAX_SATURATION, KEYS.MIN_SATURATION, parametersHandler,1);
        if (ManualSaturation != null ) {
            parametersHandler.PictureFormat.addEventListner(ManualSaturation.GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(ManualSaturation.GetModuleListner());
        }
        return ManualSaturation;
    }
    public abstract DngProfile getDngProfile(int filesize);

    public AbstractModeParameter getVideoProfileMode()
    {
        return new VideoProfilesParameter(parameters,cameraHolder, "", cameraUiWrapper);
    }

    public AbstractModeParameter getNonZslManualMode()
    {
        return null;
    }

    public AbstractModeParameter getOpCodeParameter()
    {
        return null;
    }

    public abstract AbstractModeParameter getDenoiseParameter();
}

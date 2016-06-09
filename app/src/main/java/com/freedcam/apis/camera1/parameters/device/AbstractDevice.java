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
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.CameraHolder;
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
    protected I_CameraUiWrapper cameraUiWrapper;
    protected ParametersHandler parametersHandler;
    protected MatrixChooserParameter matrixChooserParameter;

    public AbstractDevice(Parameters parameters, I_CameraUiWrapper cameraUiWrapper)
    {
        this.parameters = parameters;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();
        parametersHandler = (ParametersHandler) cameraUiWrapper.GetParameterHandler();
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
        return ManualSaturation;
    }

    public AbstractManualParameter getManualSharpness()
    {

        if (parameters.get("max-sharpness")!= null && parameters.get("sharpness-max")!= null) {
            parameters.set("max-sharpness", "100");
            parameters.set("min-sharpness", "0");
        }
        int step = 1;
        if (parameters.get("sharpness-step")!= null)
            step = Integer.parseInt(parameters.get("sharpness-step"));

        if (parameters.get("sharpness-max")!= null)
        {
            return new BaseManualParameter(parameters, "sharpness", "sharpness-max", "sharpness-min", parametersHandler,step);
        }
        else if (parameters.get("max-sharpness")!= null)
        {
            return new BaseManualParameter(parameters, "sharpness", "max-sharpness", "min-sharpness", parametersHandler,step);
        }
        return null;
    }

    public AbstractManualParameter getManualBrightness()
    {

        //p920hack
        if (parameters.get("max-brightness")!= null && parameters.get("brightness-max")!= null)
        {
            parameters.set("max-brightness", "100");
            parameters.set("min-brightness", "0");
        }
        if (parameters.get("brightness-max")!= null)
        {
            return new BaseManualParameter(parameters, "brightness", "brightness-max", "brightness-min", parametersHandler, 1);
        }
        else if (parameters.get("max-brightness")!= null)
            return new BaseManualParameter(parameters, "brightness", "max-brightness", "min-brightness", parametersHandler, 1);
        else if (parameters.get("luma-adaptation")!= null)
             return   new BaseManualParameter(parameters,"luma-adaptation","max-brightness","min-brightness",parametersHandler,1);
        return null;
    }

    public AbstractManualParameter getManualContrast()
    {
        //p920 hack
        if (parameters.get("max-contrast")!= null && parameters.get("contrast-max")!= null) {
            parameters.set("max-contrast", "100");
            parameters.set("min-contrast", "0");
        }
        if (parameters.get("contrast-max")!= null)
             return  new BaseManualParameter(parameters,"contrast", "contrast-max", "contrast-min",parametersHandler,1);
        else if (parameters.get("max-contrast")!= null)
             return new BaseManualParameter(parameters,"contrast", "max-contrast", "min-contrast",parametersHandler,1);
        return null;
    }
    public abstract DngProfile getDngProfile(int filesize);

    public AbstractModeParameter getVideoProfileMode()
    {
        return new VideoProfilesParameter(parameters,cameraUiWrapper);
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

    public AbstractModeParameter getLensFilter()
    {
        return null;
    }

    public float GetFnumber()
    {
        if (parameters.get("f-number")!= null) {
            String fnum = parameters.get("f-number");
            return Float.parseFloat(fnum);
        }
        else
            return 0;
    }

    public float GetFocal()
    {
        if (parameters.get("focal-length")!= null) {
            String focal = parameters.get("focal-length");
            return Float.parseFloat(focal);
        }
        else
            return 0;
    }
}

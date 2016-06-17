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

package freed.cam.apis.camera1.parameters.device;

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.dng.DngProfile;

/**
 * Created by troop on 31.05.2016.
 */
public abstract class AbstractDevice implements I_Device {
    protected Parameters parameters;
    protected CameraHolder cameraHolder;
    protected CameraWrapperInterface cameraUiWrapper;
    protected ParametersHandler parametersHandler;
    protected MatrixChooserParameter matrixChooserParameter;

    public AbstractDevice(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
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



    @Override
    public abstract boolean IsDngSupported();

    @Override
    public abstract ManualParameterInterface getExposureTimeParameter();

    @Override
    public abstract ManualParameterInterface getIsoParameter();

    @Override
    public abstract ManualParameterInterface getManualFocusParameter();

    @Override
    public abstract ManualParameterInterface getCCTParameter();

    @Override
    public ManualParameterInterface getSkintoneParameter() {
        return null;
    }

    @Override
    public boolean isLegacy(){return false;};

    @Override
    public ManualParameterInterface getManualSaturation()
    {
        BaseManualParameter ManualSaturation = null;
        //p920 hack
        if (parameters.get(KEYS.MAX_SATURATION)!= null && parameters.get(KEYS.SATURATION_MAX)!= null) {
            parameters.set(KEYS.MAX_SATURATION, 100);
            parameters.set(KEYS.MIN_SATURATION, 0);
        }
        //check first max after evo 3d has both but max infront is empty
        if (parameters.get(KEYS.SATURATION_MAX)!= null)
            ManualSaturation = new BaseManualParameter(parameters, KEYS.SATURATION, KEYS.SATURATION_MAX, KEYS.SATURATION_MIN, cameraUiWrapper,1);
        else if (parameters.get(KEYS.MAX_SATURATION)!= null)
            ManualSaturation = new BaseManualParameter(parameters, KEYS.SATURATION, KEYS.MAX_SATURATION, KEYS.MIN_SATURATION, cameraUiWrapper,1);
        return ManualSaturation;
    }

    @Override
    public ManualParameterInterface getManualSharpness()
    {
        if (parameters.get(KEYS.MAX_SHARPNESS)!= null && parameters.get(KEYS.SHARPNESS_MAX)!= null) {
            parameters.set(KEYS.MAX_SHARPNESS, KEYS.MAGIC_NUM100);
            parameters.set(KEYS.MIN_SHARPNESS, KEYS.MAGIC_NUM0);
        }
        int step = 1;
        if (parameters.get(KEYS.SHARPNESS_STEP)!= null)
            step = Integer.parseInt(parameters.get(KEYS.SHARPNESS_STEP));

        if (parameters.get(KEYS.SHARPNESS_MAX)!= null)
        {
            return new BaseManualParameter(parameters, KEYS.SHARPNESS, KEYS.SHARPNESS_MAX, KEYS.SHARPNESS_MIN, cameraUiWrapper,step);
        }
        else if (parameters.get(KEYS.MAX_SHARPNESS)!= null)
        {
            return new BaseManualParameter(parameters, KEYS.SHARPNESS, KEYS.MAX_SHARPNESS, KEYS.MIN_SHARPNESS, cameraUiWrapper,step);
        }
        return null;
    }

    @Override
    public ManualParameterInterface getManualBrightness()
    {
        //p920hack
        if (parameters.get("max-brightness")!= null && parameters.get("brightness-max")!= null)
        {
            parameters.set("max-brightness", "100");
            parameters.set("min-brightness", "0");
        }
        if (parameters.get("brightness-max")!= null)
        {
            return new BaseManualParameter(parameters, "brightness", "brightness-max", "brightness-min", cameraUiWrapper, 1);
        }
        else if (parameters.get("max-brightness")!= null)
            return new BaseManualParameter(parameters, "brightness", "max-brightness", "min-brightness", cameraUiWrapper, 1);
        else if (parameters.get("luma-adaptation")!= null)
             return   new BaseManualParameter(parameters,"luma-adaptation","max-brightness","min-brightness", cameraUiWrapper,1);
        return null;
    }

    @Override
    public ManualParameterInterface getManualContrast()
    {
        //p920 hack
        if (parameters.get("max-contrast")!= null && parameters.get("contrast-max")!= null) {
            parameters.set("max-contrast", "100");
            parameters.set("min-contrast", "0");
        }
        if (parameters.get("contrast-max")!= null)
             return  new BaseManualParameter(parameters,"contrast", "contrast-max", "contrast-min", cameraUiWrapper,1);
        else if (parameters.get("max-contrast")!= null)
             return new BaseManualParameter(parameters,"contrast", "max-contrast", "min-contrast", cameraUiWrapper,1);
        return null;
    }

    @Override
    public abstract DngProfile getDngProfile(int filesize);

    @Override
    public ModeParameterInterface getVideoProfileMode()
    {
        return new VideoProfilesParameter(parameters, cameraUiWrapper);
    }
    @Override
    public ModeParameterInterface getNonZslManualMode()
    {
        return null;
    }

    @Override
    public ModeParameterInterface getOpCodeParameter()
    {
        return null;
    }

    @Override
    public abstract ModeParameterInterface getDenoiseParameter();

    @Override
    public ModeParameterInterface getLensFilter()
    {
        return null;
    }

    @Override
    public ModeParameterInterface getNightMode()
    {
        return null;
    }

    @Override
    public float GetFnumber()
    {
        if (parameters.get("f-number")!= null) {
            String fnum = parameters.get("f-number");
            return Float.parseFloat(fnum);
        }
        else
            return 0;
    }
    @Override
    public float GetFocal()
    {
        if (parameters.get("focal-length")!= null) {
            String focal = parameters.get("focal-length");
            return Float.parseFloat(focal);
        }
        else
            return 0;
    }
    @Override
    public float getCurrentExposuretime()
    {
        return 0;
    }
    @Override
    public int getCurrentIso()
    {
        return 0;
    }

    @Override
    public void Set_RAWFNAME(String filename) {

    }

    @Override
    public void UniqueOverride()
    {

    }
}

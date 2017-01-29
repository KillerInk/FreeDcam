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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.modes.HDRModeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoStabilizationParameter;

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
        if (cameraUiWrapper !=  null) {
            this.cameraUiWrapper = cameraUiWrapper;
            cameraHolder = (CameraHolder) cameraUiWrapper.GetCameraHolder();
            parametersHandler = (ParametersHandler) cameraUiWrapper.GetParameterHandler();
        }
    }

    @Override
    public abstract ManualParameterInterface getCCTParameter();

    @Override
    public ModeParameterInterface getOpCodeParameter()
    {
        return null;
    }

    @Override
    public ModeParameterInterface getNightMode()
    {
        return null;
    }

    @Override
    public ModeParameterInterface getHDRMode() {
        return new HDRModeParameter(parameters, cameraUiWrapper);
    }

    @Override
    public ModeParameterInterface getVideoStabilisation() {
        return new VideoStabilizationParameter(parameters,cameraUiWrapper);
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
    public long getCurrentExposuretime()
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
}

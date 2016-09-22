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

package freed.cam.apis.camera1.parameters.device.qualcomm.LG;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.device.AbstractDevice;
import freed.cam.apis.camera1.parameters.manual.lg.FocusManualParameterLG;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesG3Parameter;
import freed.dng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G2 extends AbstractDevice
{


    public LG_G2(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        if (cameraHolder.DeviceFrameWork == Frameworks.LG)
            parameters.set("lge-camera","1");
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getManualFocusParameter() {
        return new FocusManualParameterLG(parameters, cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getCCTParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getSkintoneParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 16224256:
                return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }



    @Override
    public ModeParameterInterface getVideoProfileMode()
    {
        if (cameraHolder.DeviceFrameWork == Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
            return new VideoProfilesG3Parameter(parameters, cameraUiWrapper);
        else
            return super.getVideoProfileMode();
    }

    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        if (focusAreas != null) {
            Camera.Area a = new Camera.Area(new Rect(focusAreas.left, focusAreas.top, focusAreas.right, focusAreas.bottom), 1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            parameters.setFocusAreas(ar);
        }
        else
            parameters.setFocusAreas(null);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}

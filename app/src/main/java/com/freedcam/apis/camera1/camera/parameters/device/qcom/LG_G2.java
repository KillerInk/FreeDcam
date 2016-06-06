package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterLG;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G2 extends AbstractDevice
{
    public LG_G2(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        if (cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.LG)
            parameters.set("lge-camera","1");
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualParameterLG(parameters,cameraHolder, camParametersHandler);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 16224256:
                return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }



    @Override
    public AbstractModeParameter getVideoProfileMode()
    {
        if (cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
            return new VideoProfilesG3Parameter(parameters,cameraHolder, "", cameraUiWrapper);
        else
            return super.getVideoProfileMode();
    }
}

package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class OnePlusOne extends AbstractDevice {
    public OnePlusOne(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
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
        return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 16473600: //oneplus
                return new DngProfile(16,4224,3120,DngProfile.Mipi,DngProfile.BGGR,5280, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 17522688:
                return new DngProfile(64, 4212, 3082, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}

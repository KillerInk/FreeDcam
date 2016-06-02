package com.freedcam.apis.camera1.camera.parameters.device.mtk;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.BaseMTKDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_FloatToSixty;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class ForwardArt_MTK extends BaseMTKDevice {
    public ForwardArt_MTK(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManual_ExposureTime_FloatToSixty(parameters,camParametersHandler,null);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize) {
            case 9830400: //NGM Forward Art
                return new DngProfile(16, 2560, 1920, DngProfile.Plain, DngProfile.BGGR, 0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}

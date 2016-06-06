package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.BaseQcomNew;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseISOManual;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManual_ExposureTime_Micro;
import com.troop.androiddng.DngProfile;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class Blackberry_Priv extends BaseQcomNew
{


    public Blackberry_Priv(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
    super(uihandler, parameters, cameraUiWrapper);
}

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManual_ExposureTime_Micro(parameters, parametersHandler,null,"exposure-time", "max-exposure-time", "min-exposure-time");
    }

    @Override
    public AbstractManualParameter getIsoParameter() {

        return new BaseISOManual(parameters,"continuous-iso",parameters.getInt("min-iso"),parameters.getInt("max-iso"), parametersHandler,1);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        if (filesize < 23472640 && filesize > 22472640) //qcom
            return new DngProfile(0, 4896, 3672, DngProfile.Qcom, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        return null;
    }
}
package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualHtc;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterHTC;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManualParameterHTC;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class HTC_M8 extends AbstractDevice {
    public HTC_M8(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualParameterHTC(parameters,"","",camParametersHandler);
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualParameterHTC(parameters, "","", cameraHolder,camParametersHandler);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new CCTManualHtc(parameters,camParametersHandler);
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        if (filesize < 6000000 && filesize > 5382641) //qcom
            return new DngProfile(0, 2688, 1520, DngProfile.Qcom, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
            return new DngProfile(0, 2688, 1520, DngProfile.Mipi16, DngProfile.GRBG, DngProfile.HTCM8_rowSize,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        return null;
    }
}

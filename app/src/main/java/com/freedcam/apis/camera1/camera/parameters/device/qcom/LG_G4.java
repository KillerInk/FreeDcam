package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_LGG4;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualG4;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterLG;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G4 extends LG_G2 {
    public LG_G4(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
        new AE_Handler_LGG4(parameters,cameraHolder,camParametersHandler);
        parameters.set("lge-camera","1");
    }

    //set due Ae handler
    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }
    //set due Ae handler
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
        return new CCTManualG4(parameters,camParametersHandler);
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 19976192: //g4 bayer mipi camera1
                return new DngProfile(64, 5312,2988,DngProfile.Mipi16, DngProfile.BGGR,0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
        }
        return null;
    }
}

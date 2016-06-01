package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_LGG4;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualG4;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterLG;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G4 extends AbstractDevice {
    public LG_G4(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
        new AE_Handler_LGG4(parameters,cameraHolder,camParametersHandler);
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
        return null;
    }
}

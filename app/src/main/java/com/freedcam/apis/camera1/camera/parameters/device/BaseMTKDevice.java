package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualMTK;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class BaseMTKDevice extends AbstractDevice
{
    public BaseMTKDevice(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler) {
        super(uihandler, parameters, cameraHolder, camParametersHandler);
        new AE_Handler_MTK(parameters,cameraHolder,camParametersHandler);
    }
    //set by aehandler to camparametershandler direct
    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }
    //set by aehandler to camparametershandler direct
    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualMTK(parameters,camParametersHandler);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }
}

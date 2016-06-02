package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualMTK;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class BaseMTKDevice extends AbstractDevice
{
    public BaseMTKDevice(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
        new AE_Handler_MTK(parameters,cameraHolder,camParametersHandler);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
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
    public AbstractManualParameter getManualFocusParameter()
    {
        if(parameters.get("afeng-max-focus-step")!=null)
            return new FocusManualMTK(parameters,camParametersHandler);
       /* else  if(parameters.get("focus-fs-fi-max") != null)
            return new FocusManualMTK(parameters,"focus-fs-fi","focus-fs-fi-max","focus-fs-fi-min", camParametersHandler,10,0);*/
        else
            return null;
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

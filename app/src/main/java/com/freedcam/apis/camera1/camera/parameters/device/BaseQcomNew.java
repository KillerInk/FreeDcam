package com.freedcam.apis.camera1.camera.parameters.device;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.KEYS;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManual_SonyM4;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomNew extends AbstractDevice
{
    protected AE_Handler_QcomM aeHandlerQcomM;

    public BaseQcomNew(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
        aeHandlerQcomM = new AE_Handler_QcomM(uihandler, parameters, cameraHolder, camParametersHandler);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    //set by aehandler
    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }

    //set by aehandler
    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0, 100,KEYS.KEY_FOCUS_MODE_MANUAL,camParametersHandler,1,2);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new CCTManual_SonyM4(parameters, 8000,2000,camParametersHandler,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }
}

package com.freedcam.apis.camera1.camera.parameters.device.krillin;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualHuawei;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterManualKrillin;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class P8Lite extends AbstractDevice {
    public P8Lite(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualKrillin(parameters,cameraHolder,camParametersHandler);
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualHuawei(parameters, "hw-vcm-end-value","hw-vcm-start-value", KEYS.KEY_FOCUS_MODE_MANUAL,camParametersHandler,10,0);
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

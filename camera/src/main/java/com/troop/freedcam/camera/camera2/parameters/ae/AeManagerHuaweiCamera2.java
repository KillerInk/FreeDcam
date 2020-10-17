package com.troop.freedcam.camera.camera2.parameters.ae;

import android.annotation.TargetApi;
import android.os.Build;

import camera2_hidden_keys.huawei.CaptureRequestHuawei;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ae.AeStates;
import com.troop.freedcam.camera.basecamera.parameters.manual.AbstractManualShutter;

/**
 * Created by KillerInk on 29.12.2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeManagerHuaweiCamera2 extends AeManagerCamera2 {

    private boolean expotimeIsActive = false;
    private boolean isoIsActive = false;

    public AeManagerHuaweiCamera2(CameraControllerInterface cameraControllerInterface) {
        super(cameraControllerInterface);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Visible);
    }

    @Override
    public boolean isExposureTimeWriteable() {
        return activeAeState == AeStates.shutter_priority || activeAeState == AeStates.manual || activeAeState == AeStates.auto;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_PROFESSIONAL_MODE, CaptureRequestHuawei.HUAWEI_PROFESSIONAL_MODE_ENABLED,setToCamera);
        if (valueToSet > 0) {
            int val = (int) AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet]);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_EXPOSURE_TIME, val,setToCamera);
            manualExposureTime.fireIntValueChanged(valueToSet);
            expotimeIsActive = true;

        }
        else
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_EXPOSURE_TIME, 0,setToCamera);
            expotimeIsActive = false;
        }
        applyAeMode();
    }

    private void applyAeMode()
    {
        if (expotimeIsActive && isoIsActive)
            setAeMode(AeStates.manual);
        else if (!expotimeIsActive && isoIsActive)
            setAeMode(AeStates.iso_priority);
        else if (expotimeIsActive && !isoIsActive)
            setAeMode(AeStates.shutter_priority);
        else if (!expotimeIsActive && !isoIsActive)
            setAeMode(AeStates.auto);
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_PROFESSIONAL_MODE, CaptureRequestHuawei.HUAWEI_PROFESSIONAL_MODE_ENABLED,setToCamera);
        if (valueToSet == 0)
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_ISO_VALUE, 0,setToCamera);
            isoIsActive = false;
        }
        else
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_ISO_VALUE, Integer.parseInt(manualIso.getStringValues()[valueToSet]),setToCamera);
            isoIsActive =true;
        }
        applyAeMode();
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        float t = Float.parseFloat(exposureCompensation.getStringValues()[valueToSet].replace(",","."));
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_EXPOSURE_COMP_VALUE, t,setToCamera);
    }

    @Override
    public void setAeMode(AeStates aeState) {
        if (activeAeState == aeState)
            return;
        activeAeState = aeState;
        switch (aeState)
        {
            case manual:
                exposureCompensation.setViewState(AbstractParameter.ViewState.Disabled);
                break;
            default:
                exposureCompensation.setViewState(AbstractParameter.ViewState.Enabled);
        }
    }
}

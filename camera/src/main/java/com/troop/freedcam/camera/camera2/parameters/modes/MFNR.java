package com.troop.freedcam.camera.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.camera2.camera2_hidden_keys.qcom.CaptureRequestQcom;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera2.Camera2Fragment;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

public class MFNR extends BaseModeApi2 {
    public MFNR(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.MFNR);
        if (SettingsManager.get(SettingKeys.MFNR).isSupported())
            setViewState(ViewState.Visible);
    }


    @Override
    public String GetStringValue() {
        return String.valueOf(SettingsManager.get(SettingKeys.MFNR).get());
    }

    @Override
    public String[] getStringValues() {
        return new String[]{"false","true"};
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        SettingsManager.get(SettingKeys.MFNR).set(Boolean.parseBoolean(valueToSet));

        if (SettingsManager.get(SettingKeys.MFNR).get()) {
            ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY, setToCamera);
            ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 1, setToCamera);
        }
        else {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).SetValue(cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).GetStringValue(),true);
            ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 0, setToCamera);
        }

        fireStringValueChanged(valueToSet);
    }
}
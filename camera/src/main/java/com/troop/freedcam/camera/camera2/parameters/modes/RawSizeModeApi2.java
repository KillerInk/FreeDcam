package com.troop.freedcam.camera.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera2.Camera2Controller;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

public class RawSizeModeApi2 extends BaseModeApi2 {
    public RawSizeModeApi2(Camera2Controller cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    public RawSizeModeApi2(Camera2Controller cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        SettingsManager.get(SettingKeys.RawSize).set(valueToSet);
        if (setToCamera)
        {
            cameraUiWrapper.stopPreviewAsync();
            cameraUiWrapper.startPreviewAsync();
        }
    }

    @Override
    public String GetStringValue()
    {
        return  SettingsManager.get(SettingKeys.RawSize).get();
    }

    @Override
    public String[] getStringValues()
    {
        return SettingsManager.get(SettingKeys.RawSize).getValues();
    }
}

package com.troop.freedcam.camera.camera2.parameters.manual;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.camera2.Camera2Fragment;
import com.troop.freedcam.camera.camera2.camera2_hidden_keys.qcom.CaptureRequestQcom;
import com.troop.freedcam.settings.SettingKeys;

public class ManualSaturationQcomApi2 extends AbstractParameter {

    public ManualSaturationQcomApi2(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_Saturation);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet, boolean setToCamera) {
        super.setValue(valueToSet,setToCamera);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.saturation, currentInt,setToCamera);
    }


    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }
}

package com.troop.freedcam.camera.camera2.parameters.manual;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.camera2.Camera2Controller;
import com.troop.freedcam.camera.camera2.camera2_hidden_keys.qcom.CaptureRequestQcom;
import com.troop.freedcam.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ManualSharpnessQcomApi2 extends AbstractParameter<Camera2Controller> {

    public ManualSharpnessQcomApi2(Camera2Controller cameraUiWrapper) {
        super(cameraUiWrapper,SettingKeys.M_Sharpness);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.sharpness, currentInt,setToCamera);
    }


    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }
}

package com.troop.freedcam.camera.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.text.TextUtils;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera2.Camera2Fragment;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

import java.util.HashMap;

/**
 * Created by Ingo on 03.10.2016.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JpegQualityModeApi2 extends BaseModeApi2 {
    public JpegQualityModeApi2(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.JpegQuality);
        parameterValues =new HashMap<>();
        for (int i= 10; i <= 100; i+=10)
        {
            parameterValues.put(i+"", i);
        }
        setViewState(ViewState.Visible);
    }



    @Override
    public String GetStringValue()
    {
        if(TextUtils.isEmpty(SettingsManager.get(SettingKeys.JpegQuality).get()))
            return "100";
        else
            return SettingsManager.get(SettingKeys.JpegQuality).get();
    }

    @Override
    public String[] getStringValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        SettingsManager.get(SettingKeys.JpegQuality).set(valueToSet);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_QUALITY, (byte)Integer.parseInt(valueToSet),setToCamera);
        fireStringValueChanged(valueToSet);
    }
}

package com.troop.freedcam.camera.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.os.Build;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.camera2.Camera2Controller;
import com.troop.freedcam.camera.camera2.camera2_hidden_keys.huawei.CaptureRequestHuawei;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualWbCtApi2Hw  extends AbstractParameter<Camera2Controller>
{
    private boolean isSupported;

    private final String TAG = ManualWbCtApi2Hw.class.getSimpleName();

    public ManualWbCtApi2Hw(Camera2Controller cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_Whitebalance);
        stringvalues = SettingsManager.get(SettingKeys.M_Whitebalance).getValues();
        currentInt = 0;
        setViewState(ViewState.Visible);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public String GetStringValue()
    {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        super.setValue(valueToSet, setToCamera);
        int toset;
        currentInt =valueToSet;
        if (valueToSet == 0) // = auto
            toset = 0;
        else
            toset = Integer.parseInt(stringvalues[currentInt]);
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestHuawei.HUAWEI_SENSOR_WB_VALUE, toset,setToCamera);

    }

}

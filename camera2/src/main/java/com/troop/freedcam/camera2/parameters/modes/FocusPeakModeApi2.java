package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 10.09.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FocusPeakModeApi2 extends BaseModeApi2 {
    public FocusPeakModeApi2(Handler handler, BaseCameraHolderApi2 baseCameraHolderApi2)
    {
        super(handler, baseCameraHolderApi2);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(StringUtils.ON))
        {
            cameraHolder.FocusPeakEnable(true);

        }
        else
            cameraHolder.FocusPeakEnable(false);
    }

    @Override
    public String GetValue() {
        if (cameraHolder.isFocuspeakEnable())
            return StringUtils.ON;
        else
            return StringUtils.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public void addEventListner(I_ModeParameterEvent eventListner) {

    }

    @Override
    public void removeEventListner(I_ModeParameterEvent parameterEvent) {

    }

    @Override
    public void BackgroundValueHasChanged(String value)
    {
        if (value.equals("true"))
            super.BackgroundValueHasChanged(StringUtils.ON);
        else if (value.equals("false"))
            super.BackgroundValueHasChanged(StringUtils.OFF);
    }

    @Override
    public void BackgroundValuesHasChanged(String[] value) {

    }

    @Override
    public void BackgroundIsSupportedChanged(boolean value) {

    }

    @Override
    public void BackgroundSetIsSupportedHasChanged(boolean value) {

    }
}

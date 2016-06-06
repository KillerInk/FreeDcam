package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.os.Build;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 10.09.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FocusPeakModeApi2 extends BaseModeApi2 {
    public FocusPeakModeApi2(CameraHolderApi2 cameraHolderApi2)
    {
        super(cameraHolderApi2);
    }


    @Override
    public boolean IsSupported()
    {
        return true;//cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(StringUtils.ON))
        {
            cameraHolder.FocusPeakEnable(true);
            this.BackgroundValueHasChanged("true");
        }
        else {
            cameraHolder.FocusPeakEnable(false);
            this.BackgroundValueHasChanged("false");
        }

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

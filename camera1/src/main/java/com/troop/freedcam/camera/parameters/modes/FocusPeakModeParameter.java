package com.troop.freedcam.camera.parameters.modes;

import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.PreviewHandler;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 27.08.2015.
 */
public class FocusPeakModeParameter extends BaseModeParameter {

    PreviewHandler previewHandler;
    public FocusPeakModeParameter(Handler uihandler, BaseCameraHolder cameraHolder, PreviewHandler previewHandler)
    {
        super(uihandler, null, cameraHolder, null, null);
        this.previewHandler = previewHandler;
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= 18;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(StringUtils.ON))
        {
            previewHandler.Enable(true);
        }
        else
            previewHandler.Enable(false);
    }

    @Override
    public String GetValue()
    {
        if (previewHandler.isEnable())
            return StringUtils.ON;
        else
            return StringUtils.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public void addEventListner(I_ModeParameterEvent eventListner)
    {
        super.addEventListner(eventListner);
    }

    @Override
    public void removeEventListner(I_ModeParameterEvent parameterEvent) {
        super.removeEventListner(parameterEvent);
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

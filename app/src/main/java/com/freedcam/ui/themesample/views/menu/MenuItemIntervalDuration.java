package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemIntervalDuration extends MenuItem
{
    private AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemIntervalDuration(Context context) {
        super(context);
    }

    public MenuItemIntervalDuration(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue,AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, settingvalue,appSettingsManager);
        //onValueChanged(appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION));
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        super.SetParameter(cameraUiWrapper.camParametersHandler.IntervalDuration);
    }

    @Override
    public String[] GetValues() {

        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return parameter.GetValues();
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL_DURATION, value);
        onValueChanged(value);
        parameter.SetValue(value,true);
    }
}
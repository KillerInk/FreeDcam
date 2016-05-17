package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemInterval extends MenuItem
{
    public MenuItemInterval(Context context) {
        super(context);
    }

    public MenuItemInterval(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        super.SetParameter(cameraUiWrapper.camParametersHandler.IntervalShutterSleep);
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue,AppSettingsManager appSettingsManager) {
        super.SetStuff(i_activity, settingvalue,appSettingsManager);
    }

    @Override
    public String[] GetValues() {
       return parameter.GetValues();
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL,  value);
        onValueChanged(value);
        parameter.SetValue(value,true);
    }
}
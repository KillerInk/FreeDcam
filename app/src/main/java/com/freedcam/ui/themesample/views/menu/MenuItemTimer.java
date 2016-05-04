package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AppSettingsManager;
import com.freedcam.ui.I_Activity;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemTimer extends MenuItem
{
    private AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemTimer(Context context) {
        super(context);
    }

    public MenuItemTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper instanceof CameraUiWrapper)
            this.setVisibility(View.VISIBLE);
        else
            this.setVisibility(View.GONE);


    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue) {
        super.SetStuff(i_activity, settingvalue);
        onValueChanged(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_TIMER));
    }

    @Override
    public String[] GetValues() {
        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return new String[]{"0 sec","5 sec","10 sec","15 sec","20 sec"};
    }

    @Override
    public void SetValue(String value)
    {
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_TIMER, value);
        onValueChanged(value);
    }
}
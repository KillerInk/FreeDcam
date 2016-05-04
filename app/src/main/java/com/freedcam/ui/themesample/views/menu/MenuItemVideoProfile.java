package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;

/**
 * Created by troop on 22.07.2015.
 */
public class MenuItemVideoProfile extends MenuItem
{
    public MenuItemVideoProfile(Context context) {
        super(context);
    }

    public MenuItemVideoProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetValue(String value) {
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_VIDEPROFILE, value);
        onValueChanged(value);
        parameter.SetValue(value, true);
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue) {
        super.SetStuff(i_activity, settingvalue);
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}

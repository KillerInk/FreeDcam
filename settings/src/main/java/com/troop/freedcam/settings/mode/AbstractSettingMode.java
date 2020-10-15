package com.troop.freedcam.settings.mode;

import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 04.01.2018.
 */

public abstract class AbstractSettingMode implements SettingInterface
{
    protected SettingsManagerInterface settingsManagerInterface;
    //key to identify this settings
    protected String camera1ParameterKEY_value;
    protected SettingKeys.Key settingKey;

    public AbstractSettingMode(SettingKeys.Key settingkey)
    {
        this.settingKey = settingkey;
    }

    public String getCamera1ParameterKEY()
    {
        return camera1ParameterKEY_value;
    }

    public void setCamera1ParameterKEY(String KEY)
    {
        this.camera1ParameterKEY_value = KEY;
    }

}

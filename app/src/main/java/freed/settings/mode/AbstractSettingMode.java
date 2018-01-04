package freed.settings.mode;

import freed.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 04.01.2018.
 */

public abstract class AbstractSettingMode implements SettingInterface
{
    protected SettingsManagerInterface settingsManagerInterface;
    //String to get the value from the cameraparameters
    protected String KEY_value;

    public AbstractSettingMode(SettingsManagerInterface settingsManagerInterface, String key)
    {
        this.settingsManagerInterface = settingsManagerInterface;
        this.KEY_value = key;
    }
}

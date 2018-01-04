package freed.settings.mode;

import freed.settings.SettingsManagerInterface;
import freed.settings.mode.AbstractSettingMode;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class GlobalBooleanSettingMode extends AbstractSettingMode {
    public GlobalBooleanSettingMode(SettingsManagerInterface settingsManagerInterface, String key) {
        super(settingsManagerInterface, key);
    }

    public boolean get()
    {
        return settingsManagerInterface.getBoolean(KEY_value,false);
    }

    public void set(boolean enable)
    {
        settingsManagerInterface.setBoolean(KEY_value,enable);
    }
}

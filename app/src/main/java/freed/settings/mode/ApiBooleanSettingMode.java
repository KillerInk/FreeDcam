package freed.settings.mode;

import freed.settings.SettingsManagerInterface;

/**
 * Created by KillerInk on 04.01.2018.
 */

public class ApiBooleanSettingMode extends GlobalBooleanSettingMode {
    private final String presetKey;

    public ApiBooleanSettingMode(SettingsManagerInterface settingsManagerInterface, String key) {
        super(settingsManagerInterface, key);
        this.presetKey = key + "preset";
    }

    public boolean get()
    {
        return settingsManagerInterface.getApiBoolean(KEY_value,false);
    }

    public void set(boolean enable)
    {
        settingsManagerInterface.setApiBoolean(KEY_value,enable);
    }

    public boolean isPresetted()
    {
        return settingsManagerInterface.getApiBoolean(presetKey,false);
    }

    public void setIsPresetted(boolean preset)
    {
        settingsManagerInterface.setApiBoolean(presetKey, preset);
    }
}

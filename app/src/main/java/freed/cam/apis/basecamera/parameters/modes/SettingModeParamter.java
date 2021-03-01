package freed.cam.apis.basecamera.parameters.modes;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.mode.SettingMode;

public class SettingModeParamter extends AbstractParameter {
    public SettingModeParamter(SettingKeys.Key key) {
        super(key);
    }


    public SettingModeParamter(SettingMode settingMode)
    {
        super(settingMode);
    }
}

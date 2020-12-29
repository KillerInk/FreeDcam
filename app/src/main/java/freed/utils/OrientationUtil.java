package freed.utils;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class OrientationUtil {
    public static int getOrientation(int rotation)
    {
        if (!SettingsManager.get(SettingKeys.orientationHack).get().equals("0"))
            return  (360 + rotation+Integer.parseInt(SettingsManager.get(SettingKeys.orientationHack).get()))%360;
        else return rotation;
    }
}

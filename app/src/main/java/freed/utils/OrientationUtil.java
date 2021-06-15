package freed.utils;

import freed.FreedApplication;
import freed.settings.SettingKeys;

public class OrientationUtil {
    public static int getOrientation(int rotation)
    {
        return  (360 + rotation+Integer.parseInt(FreedApplication.settingsManager().get(SettingKeys.orientationHack).get()))%360;
    }
}

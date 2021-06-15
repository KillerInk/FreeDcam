package freed.gl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

import freed.FreedApplication;

public class GlVersion {

    public static int getGlesVersion()
    {
        final ActivityManager activityManager = (ActivityManager) FreedApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        if (configurationInfo.reqGlEsVersion >= 0x20000 && configurationInfo.reqGlEsVersion <= 0x30000)
            return 2;
        if (configurationInfo.reqGlEsVersion >= 0x30000)
            return 3;
        else
            return 1;
    }

    public static boolean isMinGlVersion()
    {
        return getGlesVersion() >1;
    }
}

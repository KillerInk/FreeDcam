package freed;

import android.app.Application;

import freed.settings.AppSettingsManager;

/**
 * Created by troop on 04.04.2017.
 */

public class SettingsApplication extends Application {

    private AppSettingsManager appSettingsManager;

    public void setAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    public AppSettingsManager getAppSettingsManager()
    {
        return AppSettingsManager.getInstance();
    }
}

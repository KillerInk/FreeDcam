package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingsManager;
import freed.utils.LocationManager;

@Module
@InstallIn(ActivityComponent.class)
public class LocationManagerModule
{
    @Provides
    @ActivityScoped
    public LocationManager locationManager(@ActivityContext Context context, SettingsManager settingsManager, UserMessageHandler userMessageHandler)
    {
        return new LocationManager(context,settingsManager,userMessageHandler);
    }
}

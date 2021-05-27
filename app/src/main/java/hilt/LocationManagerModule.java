package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import freed.settings.SettingsManager;
import freed.utils.LocationManager;

@Module
@InstallIn(ActivityComponent.class)
public class LocationManagerModule
{
    @Provides
    public LocationManager locationManager(@ActivityContext Context context, SettingsManager settingsManager)
    {
        return new LocationManager(context,settingsManager);
    }
}

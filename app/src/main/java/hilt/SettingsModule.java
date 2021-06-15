package hilt;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import freed.settings.SettingsManager;

@Module
@InstallIn(SingletonComponent.class)
public class SettingsModule {

    @Provides
    @Singleton
    public static SettingsManager settingsManager()
    {
        return new SettingsManager();
    }
}

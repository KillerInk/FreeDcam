package hilt;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.components.SingletonComponent;
import freed.cam.apis.CameraFragmentManager;
import freed.settings.SettingsManager;

@Module
@InstallIn(SingletonComponent.class)
public class CameraFragmentManagerModule {

    @Provides
    @Singleton
    public static CameraFragmentManager cameraFragmentManager(SettingsManager settingsManager)
    {
        return new CameraFragmentManager(settingsManager);
    }
}

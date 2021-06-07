package hilt;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.components.SingletonComponent;
import freed.cam.apis.CameraFragmentManager;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingsManager;

@Module
@InstallIn(ActivityComponent.class)
public class CameraFragmentManagerModule {

    @Provides
    @ActivityScoped
    public static CameraFragmentManager cameraFragmentManager(SettingsManager settingsManager,PreviewController previewController)
    {
        return new CameraFragmentManager(settingsManager, previewController);
    }
}

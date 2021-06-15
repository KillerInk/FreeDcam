package hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.apis.CameraApiManager;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingsManager;

@Module
@InstallIn(ActivityComponent.class)
public class CameraApiManagerModule {

    @Provides
    @ActivityScoped
    public static CameraApiManager cameraApiManager(SettingsManager settingsManager, PreviewController previewController)
    {
        return new CameraApiManager(settingsManager, previewController);
    }
}

package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.apis.CameraApiManager;
import freed.cam.ui.ThemeManager;


@Module
@InstallIn(ActivityComponent.class)
public class ThemeManagerModule {
    @ActivityScoped
    @Provides
    public ThemeManager themeManager(CameraApiManager cameraApiManager)
    {
        return new ThemeManager(cameraApiManager);
    }
}

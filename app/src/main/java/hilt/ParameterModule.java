package hilt;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.ui.themesample.settings.SettingsMenuItemFactory;
import freed.settings.SettingsManager;

@Module
@InstallIn(ActivityComponent.class)
public class ParameterModule {

    public ApiParameter apiParameter(SettingsManager settingsManager, CameraApiManager cameraApiManager)
    {
        return new ApiParameter(settingsManager, cameraApiManager);
    }

    public SettingsMenuItemFactory settingsMenuItemFactory(ApiParameter apiParameter)
    {
        return new SettingsMenuItemFactory(apiParameter);
    }
}

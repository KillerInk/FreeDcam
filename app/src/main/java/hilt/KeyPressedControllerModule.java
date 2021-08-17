package hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.apis.CameraApiManager;
import freed.cam.ui.KeyPressedController;
import freed.cam.ui.themesample.handler.UserMessageHandler;

@Module
@InstallIn(ActivityComponent.class)
public class KeyPressedControllerModule {
    @ActivityScoped
    @Provides
    public KeyPressedController keyPressedController(CameraApiManager cameraApiManager, UserMessageHandler userMessageHandler)
    {
        return new KeyPressedController(cameraApiManager,userMessageHandler);
    }
}

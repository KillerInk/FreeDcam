package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.ui.themesample.handler.UserMessageHandler;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface UserMessageHandlerEntryPoint {
    UserMessageHandler userMessageHandler();
}

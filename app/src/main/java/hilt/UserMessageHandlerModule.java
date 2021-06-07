package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.ui.themesample.handler.UserMessageHandler;

@Module
@InstallIn(ActivityComponent.class)
public class UserMessageHandlerModule {
    @ActivityScoped
    @Provides
    public UserMessageHandler userMessageHandler(@ActivityContext Context context)
    {
        return new UserMessageHandler(context);
    }
}

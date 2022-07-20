package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.utils.SoundPlayer;

@Module
@InstallIn(ActivityComponent.class)
public class SoundPlayerModule {

    @ActivityScoped
    @Provides
    public SoundPlayer soundPlayer(@ActivityContext Context context)
    {
        return new SoundPlayer(context);
    }
}

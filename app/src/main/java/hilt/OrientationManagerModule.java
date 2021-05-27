package hilt;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import freed.utils.OrientationManager;

@Module
@InstallIn(ActivityComponent.class)
public class OrientationManagerModule {

    @Provides
    public OrientationManager orientationManager(@ActivityContext Context context)
    {
        return new OrientationManager(context);
    }
}

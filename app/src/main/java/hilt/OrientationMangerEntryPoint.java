package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.utils.OrientationManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface OrientationMangerEntryPoint {
    OrientationManager orientationManager();
}

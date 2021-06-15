package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.utils.LocationManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface LocationManagerEntryPoint {
    LocationManager locationManager();
}

package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.histogram.HistogramController;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface HistogramControllerEntryPoint {
    HistogramController histogramcontroller();
}

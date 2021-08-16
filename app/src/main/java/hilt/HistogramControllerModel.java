package hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import freed.cam.histogram.HistogramController;
import freed.settings.SettingsManager;

@Module
@InstallIn(ActivityComponent.class)
public class HistogramControllerModel {

    @ActivityScoped
    @Provides
    public HistogramController histogramController(SettingsManager settingsManager)
    {
        return new HistogramController(settingsManager);
    }
}

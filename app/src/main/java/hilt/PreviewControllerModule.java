package hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.FragmentScoped;
import freed.cam.previewpostprocessing.PreviewController;

@Module
@InstallIn(ActivityComponent.class)
public class PreviewControllerModule {

    @ActivityScoped
    @Provides
    public PreviewController previewController()
    {
        return new PreviewController();
    }


}

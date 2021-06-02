package hilt;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;
import freed.cam.previewpostprocessing.PreviewController;

@Module
@InstallIn(FragmentComponent.class)
public class PreviewControllerModule {

    @FragmentScoped
    @Provides
    public PreviewController previewController()
    {
        return new PreviewController();
    }


}

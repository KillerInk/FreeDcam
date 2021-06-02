package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;
import freed.cam.previewpostprocessing.PreviewController;

@EntryPoint
@InstallIn(FragmentComponent.class)
public interface PreviewControllerEntryPoint {
    PreviewController previewController();
}

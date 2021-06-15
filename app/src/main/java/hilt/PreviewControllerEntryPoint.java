package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.previewpostprocessing.PreviewController;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface PreviewControllerEntryPoint {
    PreviewController previewController();
}

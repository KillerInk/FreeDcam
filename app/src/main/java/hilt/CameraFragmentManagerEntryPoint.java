package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.apis.CameraFragmentManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface CameraFragmentManagerEntryPoint {
    CameraFragmentManager cameraFragmentManager();
}

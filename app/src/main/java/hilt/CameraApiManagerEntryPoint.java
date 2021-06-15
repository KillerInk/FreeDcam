package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import freed.cam.apis.CameraApiManager;

@EntryPoint
@InstallIn(ActivityComponent.class)
public interface CameraApiManagerEntryPoint {
    CameraApiManager cameraApiManager();
}

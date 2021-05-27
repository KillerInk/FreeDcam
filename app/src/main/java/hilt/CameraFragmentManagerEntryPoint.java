package hilt;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import freed.cam.apis.CameraFragmentManager;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface CameraFragmentManagerEntryPoint {
    CameraFragmentManager cameraFragmentManager();
}

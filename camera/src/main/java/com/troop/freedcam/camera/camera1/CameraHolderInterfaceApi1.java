package com.troop.freedcam.camera.camera1;

import android.view.Surface;

import com.troop.freedcam.camera.basecamera.CameraHolderInterface;

public interface CameraHolderInterfaceApi1 extends CameraHolderInterface {
    boolean setSurface(Surface texture);
    void resetPreviewCallback();
    void StartPreview();
    void StopPreview();
}

package com.troop.freedcam.camera.camera1;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.view.Surface;
import android.view.TextureView;

import com.troop.freedcam.camera.basecamera.cameraholder.CameraHolderInterface;
import com.troop.freedcam.camera.basecamera.focus.FocusEvents;

public interface CameraHolderInterfaceApi1 extends CameraHolderInterface {
    boolean setSurface(Surface texture);
    void resetPreviewCallback();
    void StartPreview();
    void StopPreview();
    void SetLocation(Location loc);
    void SetCameraParameters(Camera.Parameters parameters);
    void setTextureView(SurfaceTexture texturView);
    Surface getSurfaceHolder();
    Camera.Parameters GetCameraParameters();
    void TakePicture(Camera.PictureCallback picture);
    void StartFocus(final FocusEvents autoFocusCallback);
    void CancelFocus();
    void SetMeteringAreas(Rect meteringRect);
    void SetCameraRotation(int rotation);
    Camera GetCamera();
}

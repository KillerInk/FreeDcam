package com.troop.freedcam.camera.sonyremote;

import com.troop.freedcam.camera.basecamera.focus.FocusEvents;

public interface CameraHolderSonyInterface {
    void StartPreview();
    void StopPreview();
    void CancelFocus();
    void StartFocus(FocusEvents autoFocusCallback);
    void SetTouchFocus(double x, double y);
}

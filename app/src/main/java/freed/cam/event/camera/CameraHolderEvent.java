package freed.cam.event.camera;


import freed.cam.apis.basecamera.Size;
import freed.cam.event.MyEvent;

public interface CameraHolderEvent extends MyEvent {
    void onCameraOpen();
    void onCameraOpenFinished();
    void onCameraClose();
    void onCameraError(String error);
    void onCameraChangedAspectRatioEvent(Size size);
}

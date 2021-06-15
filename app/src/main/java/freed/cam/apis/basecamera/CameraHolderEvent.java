package freed.cam.apis.basecamera;


public interface CameraHolderEvent {
    void onCameraOpen();
    void onCameraOpenFinished();
    void onCameraClose();
    void onCameraError(String error);
    void onCameraChangedAspectRatioEvent(Size size);
}

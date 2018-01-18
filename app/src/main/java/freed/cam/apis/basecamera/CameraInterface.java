package freed.cam.apis.basecamera;

/**
 * Created by KillerInk on 18.01.2018.
 */

public interface CameraInterface {
    void createCamera();
    void initCamera();
    void startCamera();
    void stopCamera();
    void restartCamera();
    void startPreview();
    void stopPreview();
}

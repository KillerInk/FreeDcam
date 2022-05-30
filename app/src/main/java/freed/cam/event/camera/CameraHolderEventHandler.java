package freed.cam.event.camera;

import freed.cam.apis.basecamera.Size;
import freed.cam.event.BaseEventHandler;
import freed.utils.Log;

public class CameraHolderEventHandler extends BaseEventHandler<CameraHolderEvent> {

    private static final String TAG = CameraHolderEventHandler.class.getSimpleName();

    public void fireOnCameraOpen()
    {
        Log.d(TAG, "fireOnCameraOpen");
        for (CameraHolderEvent event : eventListners)
            event.onCameraOpen();
    }

    public void fireOnCameraOpenFinished() {
        Log.d(TAG, "fireOnCameraOpenFinished");
        for (CameraHolderEvent event : eventListners)
            event.onCameraOpenFinished();
    }

    public synchronized void fireOnCameraClose() {
        Log.d(TAG, "fireOnCameraClose");
        for (CameraHolderEvent event : eventListners)
            event.onCameraClose();
    }

    public void fireOnCameraError(String error) {
        Log.d(TAG, "fireOnCameraError");
        for (CameraHolderEvent event : eventListners)
            event.onCameraError(error);
    }

    public void fireOnCameraChangedAspectRatioEvent(Size size) {
        Log.d(TAG, "fireOnCameraChangedAspectRatioEvent");
        for (CameraHolderEvent event : eventListners)
            event.onCameraChangedAspectRatioEvent(size);
    }
}

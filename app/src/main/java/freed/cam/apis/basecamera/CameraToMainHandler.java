package freed.cam.apis.basecamera;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class CameraToMainHandler extends Handler implements CameraStateEvents
{

    public static final int MSG_ON_CAMERA_OPEN = 0;
    public static final int MSG_ON_CAMERA_ERROR = 1;
    public static final int MSG_ON_CAMERA_CLOSE = 3;
    public static final int MSG_ON_PREVIEW_OPEN= 4;
    public static final int MSG_ON_PREVIEW_CLOSE= 5;
    public static final int MSG_ON_CAMERA_OPEN_FINISHED= 6;
    public static final int MSG_SET_CAMERASTAUSLISTNER = 7;
    public static final int MSG_SET_ASPECTRATIO = 1337;

    public interface MainMessageEvent{
        void handelMainMessage(Message msg);
    }

    private WeakReference<MainMessageEvent> mainMessageEventWeakReference;

    public CameraToMainHandler(MainMessageEvent event)
    {
        super(Looper.getMainLooper());
        mainMessageEventWeakReference = new WeakReference<MainMessageEvent>(event);
    }

    @Override
    public void handleMessage(Message msg) {
        MainMessageEvent mainMessageEvent = mainMessageEventWeakReference.get();
        if (mainMessageEvent != null)
            mainMessageEvent.handelMainMessage(msg);
        else
            super.handleMessage(msg);
    }

    public void setCameraStateChangedListner(final CameraStateEvents cameraChangedListner)
    {
        obtainMessage(CameraToMainHandler.MSG_SET_CAMERASTAUSLISTNER,cameraChangedListner).sendToTarget();
    }

    @Override
    public void onCameraOpen()
    {
        obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_OPEN).sendToTarget();
    }

    @Override
    public void onCameraOpenFinish() {
        obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_OPEN_FINISHED).sendToTarget();
    }

    @Override
    public void onCameraClose(String message) {
        obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_CLOSE, message).sendToTarget();
    }

    @Override
    public void onPreviewOpen(String message) {
        obtainMessage(CameraToMainHandler.MSG_ON_PREVIEW_OPEN, message).sendToTarget();
    }

    @Override
    public void onPreviewClose(String message) {
        obtainMessage(CameraToMainHandler.MSG_ON_PREVIEW_CLOSE, message).sendToTarget();
    }

    @Override
    public void onCameraError(final String error) {
        obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_ERROR,error).sendToTarget();
    }
    
}

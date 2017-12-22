package freed.cam.apis.basecamera;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class MainToCameraHandler extends Handler {


    public interface CameraMessageEvent
    {
        void handelCameraMessage(Message msg);
    }

    public static final int MSG_START_CAMERA = 10;
    public static final int MSG_STOP_CAMERA = 11;
    public static final int MSG_RESTART_CAMERA = 12;
    public static final int MSG_START_PREVIEW = 13;
    public static final int MSG_STOP_PREVIEW = 14;
    public static final int MSG_INIT_CAMERA = 15;
    public static final int MSG_CREATE_CAMERA = 16;
    public static final int MSG_SET_ASPECTRATIO = 1337;

    private WeakReference<CameraMessageEvent> messageHandlerWeakReference;

    public void createCamera()
    {
        this.obtainMessage(MSG_CREATE_CAMERA).sendToTarget();
    }

    public void initCamera()
    {
        this.obtainMessage(MSG_INIT_CAMERA).sendToTarget();
    }

    public void startCamera()
    {
        this.obtainMessage(MSG_START_CAMERA).sendToTarget();
    }

    public void stopCamera()
    {
        this.obtainMessage(MSG_STOP_CAMERA).sendToTarget();
    }

    public void restartCamera()
    {
        this.obtainMessage(MSG_RESTART_CAMERA).sendToTarget();
    }

    public void startPreview()
    {
        this.obtainMessage(MSG_START_PREVIEW).sendToTarget();
    }

    public void stopPreview()
    {
        this.obtainMessage(MSG_STOP_PREVIEW).sendToTarget();
    }


    public MainToCameraHandler(Looper looper, CameraMessageEvent cameraMessageEvent)
    {
        super(looper);
        messageHandlerWeakReference = new WeakReference<CameraMessageEvent>(cameraMessageEvent);
    }

    @Override
    public void handleMessage(Message msg) {
        CameraMessageEvent cameraMessageEvent = messageHandlerWeakReference.get();
        if (cameraMessageEvent != null)
            cameraMessageEvent.handelCameraMessage(msg);
    }
}

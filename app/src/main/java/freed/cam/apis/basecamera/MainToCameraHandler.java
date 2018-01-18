package freed.cam.apis.basecamera;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class MainToCameraHandler extends Handler {

    public static final int MSG_START_CAMERA = 10;
    public static final int MSG_STOP_CAMERA = 11;
    public static final int MSG_RESTART_CAMERA = 12;
    public static final int MSG_START_PREVIEW = 13;
    public static final int MSG_STOP_PREVIEW = 14;
    public static final int MSG_INIT_CAMERA = 15;
    public static final int MSG_CREATE_CAMERA = 16;
    public static final int MSG_SET_ASPECTRATIO = 1337;

    private WeakReference<CameraInterface> messageHandlerWeakReference;

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


    public MainToCameraHandler(Looper looper, CameraInterface cameraMessageEvent)
    {
        super(looper);
        messageHandlerWeakReference = new WeakReference<CameraInterface>(cameraMessageEvent);
    }

    @Override
    public void handleMessage(Message msg) {
        CameraInterface cameraMessageEvent = messageHandlerWeakReference.get();
        if (cameraMessageEvent != null) {
            switch (msg.what)
            {
                case MainToCameraHandler.MSG_START_CAMERA:
                    cameraMessageEvent.startCamera();
                    break;
                case MainToCameraHandler.MSG_STOP_CAMERA:
                    cameraMessageEvent.stopCamera();
                    break;
                case MainToCameraHandler.MSG_RESTART_CAMERA:
                    cameraMessageEvent.restartCamera();
                    break;
                case MainToCameraHandler.MSG_START_PREVIEW:
                    cameraMessageEvent.startPreview();
                    break;
                case MainToCameraHandler.MSG_STOP_PREVIEW:
                    cameraMessageEvent.stopPreview();
                    break;
                case MainToCameraHandler.MSG_INIT_CAMERA:
                    cameraMessageEvent.initCamera();
                    break;
                case MainToCameraHandler.MSG_CREATE_CAMERA:

                    cameraMessageEvent.createCamera();
                    break;
            }
        }
    }
}

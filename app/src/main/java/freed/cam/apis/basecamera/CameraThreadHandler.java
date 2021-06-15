package freed.cam.apis.basecamera;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import freed.utils.Log;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class CameraThreadHandler extends Handler {

    public static final int MSG_START_CAMERA = 10;
    public static final int MSG_STOP_CAMERA = 11;
    public static final int MSG_RESTART_CAMERA = 12;
    public static final int MSG_START_PREVIEW = 13;
    public static final int MSG_STOP_PREVIEW = 14;
    public static final int MSG_INIT_CAMERA = 15;
    public static final int MSG_RESTART_PREVIEW = 17;
    public static final int MSG_SET_ASPECTRATIO = 1337;

    private static CameraThreadHandler cameraThreadHandler;

    public static void initCameraAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.initCamera();
    }

    public static void startCameraAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.startCamera();
    }

    public static void startCameraAsync(int delay)
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.startCamera();
    }

    public static void stopCameraAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.stopCamera();
    }

    public static void restartCameraAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.restartCamera();
    }

    public static void startPreviewAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.startPreview();
    }

    public static void stopPreviewAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.stopPreview();
    }

    public static void restartPreviewAsync()
    {
        if (cameraThreadHandler != null)
            cameraThreadHandler.restartPreview();
    }

    public static void close()
    {
        cameraThreadHandler = null;
    }

    public static void setCameraInterface(CameraInterface cameraInterface)
    {
        if (cameraThreadHandler != null)
        {
            cameraThreadHandler.messageHandlerWeakReference = new WeakReference<>(cameraInterface);
        }
    }

    private WeakReference<CameraInterface> messageHandlerWeakReference;


    public void initCamera()
    {
        this.obtainMessage(MSG_INIT_CAMERA).sendToTarget();
    }

    public void startCamera()
    {
        this.obtainMessage(MSG_START_CAMERA).sendToTarget();
    }

    public void startCamera(int delay)
    {
        this.sendMessageDelayed(this.obtainMessage(MSG_START_CAMERA),delay);
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

    public void restartPreview()
    {
        this.obtainMessage(MSG_RESTART_PREVIEW).sendToTarget();
    }


    public CameraThreadHandler(Looper looper)
    {
        super(looper);
        cameraThreadHandler = this;
    }

    public Looper getCameraLooper()
    {
        return getLooper();
    }

    @Override
    public void handleMessage(Message msg) {
        if (messageHandlerWeakReference == null)
            return;
        CameraInterface cameraMessageEvent = messageHandlerWeakReference.get();
        if (cameraMessageEvent != null) {
            try {
                switch (msg.what) {
                    case CameraThreadHandler.MSG_START_CAMERA:
                        cameraMessageEvent.startCamera();
                        break;
                    case CameraThreadHandler.MSG_STOP_CAMERA:
                        cameraMessageEvent.stopCamera();
                        break;
                    case CameraThreadHandler.MSG_RESTART_CAMERA:
                        cameraMessageEvent.restartCamera();
                        break;
                    case CameraThreadHandler.MSG_START_PREVIEW:
                        cameraMessageEvent.startPreview();
                        break;
                    case CameraThreadHandler.MSG_STOP_PREVIEW:
                        cameraMessageEvent.stopPreview();
                        break;
                    case CameraThreadHandler.MSG_INIT_CAMERA:
                        cameraMessageEvent.initCamera();
                        break;
                    case CameraThreadHandler.MSG_RESTART_PREVIEW:
                        cameraMessageEvent.stopPreview();
                        cameraMessageEvent.startPreview();
                    break;
                }
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }
        }
    }
}

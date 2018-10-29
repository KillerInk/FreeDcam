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

    public CameraToMainHandler()
    {
        super(Looper.getMainLooper());
    }

    public void setMainMessageEventWeakReference(MainMessageEvent eventWeakReference)
    {
        mainMessageEventWeakReference = new WeakReference<>(eventWeakReference);
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
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_SET_CAMERASTAUSLISTNER;
        msg.obj = cameraChangedListner;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_SET_CAMERASTAUSLISTNER,cameraChangedListner).sendToTarget();
    }

    @Override
    public void onCameraOpen()
    {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_CAMERA_OPEN;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_OPEN).sendToTarget();
    }

    @Override
    public void onCameraOpenFinish() {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_CAMERA_OPEN_FINISHED;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_OPEN_FINISHED).sendToTarget();
    }

    @Override
    public void onCameraClose(String message) {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_CAMERA_CLOSE;
        msg.obj = message;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_CLOSE, message).sendToTarget();
    }

    @Override
    public void onPreviewOpen(String message) {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_PREVIEW_OPEN;
        msg.obj = message;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_PREVIEW_OPEN, message).sendToTarget();
    }

    @Override
    public void onPreviewClose(String message) {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_PREVIEW_CLOSE;
        msg.obj = message;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_PREVIEW_CLOSE, message).sendToTarget();
    }

    @Override
    public void onCameraError(final String error) {
        Message msg = new Message();
        msg.what = CameraToMainHandler.MSG_ON_CAMERA_ERROR;
        msg.obj = error;
        sendMessage(msg);
        //obtainMessage(CameraToMainHandler.MSG_ON_CAMERA_ERROR,error).sendToTarget();
    }
    
}

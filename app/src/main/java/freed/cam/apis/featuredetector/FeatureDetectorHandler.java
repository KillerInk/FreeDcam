package freed.cam.apis.featuredetector;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 28.12.2017.
 */

public class FeatureDetectorHandler extends Handler
{
    public interface FdHandlerInterface
    {
        void startFreedcam();
        void sendLog(String msg);
    }
    private WeakReference<FdHandlerInterface> fdHandlerInterfaceWeakReference;

    public FeatureDetectorHandler(FdHandlerInterface fdHandlerInterface)
    {
        fdHandlerInterfaceWeakReference = new WeakReference<FdHandlerInterface>(fdHandlerInterface);
    }

    public final static int MSG_STARTFREEDCAM = 0;
    public final static int MSG_SENDLOG = 1;
    @Override
    public void handleMessage(Message msg) {
        FdHandlerInterface fdHandlerInterface = fdHandlerInterfaceWeakReference.get();
        if (fdHandlerInterface == null)
            return;
        switch (msg.what) {
            case MSG_STARTFREEDCAM:
                fdHandlerInterface.startFreedcam();
                break;
            case MSG_SENDLOG:
                fdHandlerInterface.sendLog((String)msg.obj);
                break;
            default:
                super.handleMessage(msg);
        }
    }
}

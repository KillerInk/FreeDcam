package freed.cam.ui.themesample.cameraui;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class ManualButtonHandler extends Handler
{
    public interface ManualMessageEvent
    {
        void handelMainMessage(Message msg);
    }

    public static final int ONISSUPPRTEDCHANGED = 0;
    public static final int ON_IS_SET_SUPPORTED_CHANGED = 1;
    public static final int ON_STRING_VALUE_CHANGED = 2;
    public static final int ON_INT_VALUE_CHANGED = 3;
    public static final int ON_UPDATE_SETTING = 4;

    private WeakReference<ManualMessageEvent> manualMessagEventWeakReference;

    public ManualButtonHandler(ManualMessageEvent msgevent)
    {
        super(Looper.getMainLooper());
        manualMessagEventWeakReference = new WeakReference<ManualMessageEvent>(msgevent);
    }

    public void setON_IS_SUPPORTED_CHANGED(boolean val)
    {
        this.obtainMessage(ONISSUPPRTEDCHANGED,val).sendToTarget();
    }

    public void setON_IS_SET_SUPPORTED_CHANGED(boolean val)
    {
        this.obtainMessage(ON_IS_SET_SUPPORTED_CHANGED,val).sendToTarget();
    }

    public void setON_STRING_VALUE_CHANGED(String val)
    {
        this.obtainMessage(ON_STRING_VALUE_CHANGED,val).sendToTarget();
    }

    public void setON_INT_VALUE_CHANGED(int val)
    {
        this.obtainMessage(ON_INT_VALUE_CHANGED,val).sendToTarget();
    }

    public void setON_UPDATE_SETTING(int val)
    {
        this.obtainMessage(ON_UPDATE_SETTING,val).sendToTarget();
    }

    @Override
    public void handleMessage(Message msg) {
        ManualMessageEvent event = manualMessagEventWeakReference.get();
        if (event != null)
            event.handelMainMessage(msg);
    }
}

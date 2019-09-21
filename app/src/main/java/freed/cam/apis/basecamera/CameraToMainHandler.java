package freed.cam.apis.basecamera;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KillerInk on 22.12.2017.
 */

public class CameraToMainHandler extends Handler
{
    public CameraToMainHandler()
    {
        super(Looper.getMainLooper());
    }


    
}

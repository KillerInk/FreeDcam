package com.troop.freedcam.camera.basecamera;


import android.os.Handler;
import android.os.Looper;

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

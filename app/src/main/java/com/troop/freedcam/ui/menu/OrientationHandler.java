package com.troop.freedcam.ui.menu;

import android.app.Activity;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationHandler
{
    private int currentOrientation = 0;
    OrientationEventListener orientationEventListener;
    boolean parametersLoaded = false;
    I_orientation orientationListner;
    Activity activity;

    public OrientationHandler(Activity activity, final I_orientation orientationListner)
    {
        this.orientationListner = orientationListner;
        this.activity = activity;

        orientationEventListener = new OrientationEventListener(activity, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {
                if (currentOrientation != calcCurrentOrientation(orientation))
                {
                    currentOrientation = calcCurrentOrientation(orientation);
                    if (orientationListner != null)
                        orientationListner.OrientationChanged(currentOrientation);
                }
            }
        };
    }

    public void Start()
    {
        orientationEventListener.enable();
    }
    public void Stop()
    {
        orientationEventListener.disable();
    }


    private int calcCurrentOrientation(int orientation)
    {
        int orientationToRet = 0;
        if (orientation >= 315 || orientation < 45)
            orientationToRet = 90;
        else if (orientation < 135 && orientation > 45)
            orientationToRet = 180;
        else if (orientation >= 135 && orientation < 230)
            orientationToRet = 270;
        return orientationToRet;
    }

}

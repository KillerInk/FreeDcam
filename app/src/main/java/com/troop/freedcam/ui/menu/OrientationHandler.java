package com.troop.freedcam.ui.menu;

import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.view.OrientationEventListener;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationHandler implements AbstractModuleHandler.I_worker
{
    private int currentOrientation = 0;
    OrientationEventListener orientationEventListener;
    boolean parametersLoaded = false;
    I_orientation orientationListner;
    FragmentActivity activity;

    public OrientationHandler(FragmentActivity activity, final I_orientation orientationListner)
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
                    if (orientationListner != null) {
                        try {
                            orientationListner.OrientationChanged(currentOrientation);
                        }
                        catch (NullPointerException ex)
                        {

                        }
                    }
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

    @Override
    public void onWorkStarted() {
        Stop();
    }

    @Override
    public void onWorkFinished(boolean finished) {
        Start();
    }
}

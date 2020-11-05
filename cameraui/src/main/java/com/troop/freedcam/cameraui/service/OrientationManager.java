package com.troop.freedcam.cameraui.service;

import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import androidx.fragment.app.FragmentActivity;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.DeviceOrientationChanged;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationManager
{
    private int currentOrientation;
    private final OrientationEventListener orientationEventListener;

    public OrientationManager()
    {
        orientationEventListener = new OrientationEventListener(ContextApplication.getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {
                int newOr = calcCurrentOrientation(orientation);
                if (currentOrientation != newOr)
                {
                    currentOrientation = newOr;
                    EventBusHelper.post(new DeviceOrientationChanged(currentOrientation));
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

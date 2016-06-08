package com.freedcam.ui.handler;

import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.view.OrientationEventListener;

import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.I_worker;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationHandler implements I_worker
{
    private int currentOrientation = 0;
    private OrientationEventListener orientationEventListener;
    private I_orientation orientationListner;

    public OrientationHandler(FragmentActivity activity, final I_orientation orientationListner)
    {
        this.orientationListner = orientationListner;

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
                            Logger.exception(ex);
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
    public void onCaptureStateChanged(CaptureModes modes)
    {
        switch (modes)
        {
            case video_recording_stop:
                Start();
                break;
            case video_recording_start:
                Stop();
                break;
            case image_capture_stop:
                Start();
                break;
            case image_capture_start:
                Stop();
                break;
            case continouse_capture_start:
                Stop();
                break;
            case continouse_capture_stop:
                Start();
                break;
        }
    }

}

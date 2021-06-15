package freed.utils;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationManager implements LifecycleObserver
{
    private int currentOrientation;
    private final OrientationEventListener orientationEventListener;
    private OrientationEvent orientationListner;

    public OrientationManager(Context activity)
    {
        orientationEventListener = new OrientationEventListener(activity, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {
                int newOr = calcCurrentOrientation(orientation);
                if (currentOrientation != newOr)
                {
                    currentOrientation = newOr;
                    if (orientationListner != null) {
                        try {
                            orientationListner.onOrientationChanged(currentOrientation);
                        }
                        catch (NullPointerException ex)
                        {
                            Log.WriteEx(ex);
                        }
                    }
                }
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void Start()
    {
        orientationEventListener.enable();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void Stop()
    {
        orientationEventListener.disable();
    }

    public void setOrientationEventListener(OrientationEvent listener)
    {
        this.orientationListner = listener;
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

    public int getCurrentOrientation()
    {
        return currentOrientation;
    }

}

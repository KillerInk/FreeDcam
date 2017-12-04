package freed.utils;

import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.view.OrientationEventListener;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationManager
{
    private int currentOrientation;
    private final OrientationEventListener orientationEventListener;

    public OrientationManager(FragmentActivity activity, final OrientationEvent orientationListner)
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

package troop.com.themesample.handler;

import android.media.Ringtone;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.ui.AppSettingsManager;


import troop.com.themesample.subfragments.CameraUiFragment;

/**
 * Created by Ingo on 04.10.2015.
 */
public class IntervalHandler
{
    AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;
    long Countduration = 0;

    final String TAG = IntervalHandler.class.getSimpleName();
    //intervalmeter start ///////////////

    static int counter = 0;
    int intervalDuration = 0;
    int shutterDelay = 0;
    Handler handler;
    Time startTime;
    Time endTime;

    public IntervalHandler(AppSettingsManager appSettingsManager, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraUiWrapper = cameraUiWrapper;
        handler = new Handler();
    }

    public void StartInterval()
    {
        Log.d(TAG, "Start Start Interval");
        startTime = new Time();
        startTime.setToNow();
        String interval = appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL).replace(" sec", "");
        intervalDuration = Integer.parseInt(interval)*1000;
        String timeToRun = appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION);
        if(!timeToRun.equals("Bulb"))
        {
            int min = Integer.parseInt(timeToRun.replace(" min", ""));
            endTime = new Time();
            endTime.set(startTime.second,startTime.minute + min, startTime.hour,startTime.monthDay,startTime.month,startTime.year);
        }
        String shutterdelay = appSettingsManager.getString(AppSettingsManager.SETTING_TIMER);
        if (!shutterdelay.equals("off"))
            shutterDelay = Integer.parseInt(shutterdelay.replace(" sec", "")) *1000;
        else
            shutterDelay = 0;
        startShutterDelay();
    }

    public void DoNextInterval()
    {
        Log.d(TAG, "Start StartNext Interval");
        handler.postDelayed(intervalDelayRunner, intervalDuration);
    }

    private Runnable intervalDelayRunner =new Runnable() {
        @Override
        public void run()
        {

            startShutterDelay();
        }
    };

    private void startShutterDelay()
    {
        Log.d(TAG, "Start ShutterDelay");
        handler.postDelayed(shutterDelayRunner, shutterDelay);
    }

    private Runnable shutterDelayRunner =new Runnable() {
        @Override
        public void run() {
            cameraUiWrapper.DoWork();
        }
    };
}

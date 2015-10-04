package troop.com.themesample.handler;

import android.media.Ringtone;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.ui.AppSettingsManager;


import java.util.Date;

import troop.com.themesample.subfragments.CameraUiFragment;

/**
 * Created by Ingo on 04.10.2015.
 */
public class IntervalHandler
{
    AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;

    final String TAG = IntervalHandler.class.getSimpleName();

    int intervalDuration = 0;
    int shutterDelay = 0;
    int intervalToEndDuration = 0;
    Handler handler;
    long startTime = 0;

    public IntervalHandler(AppSettingsManager appSettingsManager, AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraUiWrapper = cameraUiWrapper;
        handler = new Handler();
    }

    public void StartInterval()
    {
        Log.d(TAG, "Start Start Interval");
        this.startTime = new Date().getTime();
        String interval = appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL).replace(" sec", "");
        this.intervalDuration = Integer.parseInt(interval)*1000;
        String shutterdelay = appSettingsManager.getString(AppSettingsManager.SETTING_TIMER);
        if (!shutterdelay.equals("0 sec"))
            shutterDelay = Integer.parseInt(shutterdelay.replace(" sec", "")) *1000;
        else
            shutterDelay = 0;
        String endDuration = appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION).replace(" min","");
        this.intervalToEndDuration = Integer.parseInt(endDuration);
        startShutterDelay();
    }

    public void DoNextInterval()
    {

        long dif = new Date().getTime() - IntervalHandler.this.startTime;
        double min = (double)(dif /1000) / 60;
        if (min >= IntervalHandler.this.intervalToEndDuration)
        {
            Log.d(TAG, "Finished Interval");
            return;
        }
        Log.d(TAG, "Start StartNext Interval in" + IntervalHandler.this.intervalDuration + " " + min + " " + IntervalHandler.this.intervalToEndDuration);
        handler.postDelayed(intervalDelayRunner, IntervalHandler.this.intervalDuration);
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
        Log.d(TAG, "Start ShutterDelay in " + IntervalHandler.this.shutterDelay);
        handler.postDelayed(shutterDelayRunner, IntervalHandler.this.shutterDelay);
    }

    private Runnable shutterDelayRunner =new Runnable() {
        @Override
        public void run() {
            cameraUiWrapper.DoWork();
        }
    };
}

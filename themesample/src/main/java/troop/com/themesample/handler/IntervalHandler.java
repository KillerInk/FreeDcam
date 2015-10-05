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
    UserMessageHandler messageHandler;

    public IntervalHandler(AppSettingsManager appSettingsManager, AbstractCameraUiWrapper cameraUiWrapper, UserMessageHandler messageHandler)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraUiWrapper = cameraUiWrapper;
        handler = new Handler();
        this.messageHandler = messageHandler;
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

    private void sendMsg()
    {

        String t = "Time:"+String.format("%.2f ",((double)((new Date().getTime() - IntervalHandler.this.startTime)) /1000) / 60);
        t+=("/"+intervalToEndDuration+ " NextIn:" + shuttercounter +"/" + intervalDuration/1000);
        messageHandler.SetUserMessage(t);

    }

    int shuttercounter = 0;
    public void DoNextInterval()
    {

        long dif = new Date().getTime() - IntervalHandler.this.startTime;
        double min = (double)(dif /1000) / 60;
        if (min >= IntervalHandler.this.intervalToEndDuration)
        {
            Log.d(TAG, "Finished Interval");
            cameraUiWrapper.camParametersHandler.IntervalCaptureFocusSet = false;
            cameraUiWrapper.camParametersHandler.IntervalCapture = false;
            return;
        }
        Log.d(TAG, "Start StartNext Interval in" + IntervalHandler.this.intervalDuration + " " + min + " " + IntervalHandler.this.intervalToEndDuration);
        intervalDelayCounter = 0;
        handler.post(intervalDelayRunner);
    }

    int intervalDelayCounter;
    private Runnable intervalDelayRunner =new Runnable() {
        @Override
        public void run()
        {
            if (intervalDelayCounter < IntervalHandler.this.intervalDuration /1000) {
                handler.postDelayed(intervalDelayRunner, 1000);
                sendMsg();
                intervalDelayCounter++;
                shuttercounter++;
            }
            else {
                handler.postDelayed(shutterDelayRunner, 1000);
                shuttercounter = 0;
            }
        }
    };

    private Runnable shutterDelayRunner =new Runnable() {
        @Override
        public void run()
        {

            startShutterDelay();
        }
    };


    int shutterWaitCounter =0;
    private void startShutterDelay()
    {
        Log.d(TAG, "Start ShutterDelay in " + IntervalHandler.this.shutterDelay);
        if (shutterWaitCounter <  IntervalHandler.this.shutterDelay / 1000)
        {
            handler.postDelayed(shutterDelayRunner, 1000);
            sendMsg();
            shutterWaitCounter++;
        }
        else
        {
            handler.postDelayed(doWorkDelayRunner, 1000);
            shutterWaitCounter = 0;
        }
    }

    private Runnable doWorkDelayRunner =new Runnable() {
        @Override
        public void run() {
            cameraUiWrapper.DoWork();
        }
    };
}

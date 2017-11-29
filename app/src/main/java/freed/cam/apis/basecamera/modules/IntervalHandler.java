/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.basecamera.modules;

import android.os.Handler;
import android.text.TextUtils;

import java.util.Date;

import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by Ingo on 04.10.2015.
 */
public class IntervalHandler
{
    private final ModuleAbstract picmodule;

    private final String TAG = IntervalHandler.class.getSimpleName();

    //the sleeptime how long the cam waits for the next capture in ms
    private int sleepTimeBetweenCaptures;
    private int shutterDelay;
    //how long the interval should run, 0 = infinity, maybe its need a check if sd is full^^
    private int fullIntervalCaptureDuration;
    private final Handler handler;
    private long startTime;
    private boolean working;

    public boolean IsWorking() {return working;}

    public IntervalHandler(ModuleAbstract picmodule)
    {
        this.picmodule = picmodule;
        handler = new Handler();
    }

    public void StartInterval()
    {
        Log.d(TAG, "Start Interval");
        working = true;
        startTime = new Date().getTime();
        String sleep = picmodule.cameraUiWrapper.getParameterHandler().IntervalShutterSleep.GetStringValue();
        if (sleep.contains(" sec"))
            sleepTimeBetweenCaptures = Integer.parseInt(sleep.replace(" sec",""))*1000;
        if (sleep.contains(" min"))
            sleepTimeBetweenCaptures = Integer.parseInt(sleep.replace(" min",""))*60*1000;

        String duration = picmodule.cameraUiWrapper.getParameterHandler().IntervalDuration.GetStringValue();
        if (duration.equals("∞"))
            fullIntervalCaptureDuration = 0;
        else if (duration.contains(" min"))
            fullIntervalCaptureDuration = Integer.parseInt(duration.replace(" min",""));
        else if (duration.contains(" h"))
            fullIntervalCaptureDuration = Integer.parseInt(duration.replace(" h",""))*60;

        startShutterDelay();
    }

    public void CancelInterval()
    {
        Log.d(TAG, "Cancel Interval");
        handler.removeCallbacks(intervalDelayRunner);
        handler.removeCallbacks(shutterDelayRunner);
        timeGoneTillNextCapture = 0;
        sleepTimeBetweenCaptures = 0;
        fullIntervalCaptureDuration = 0;
        intervalDelayCounter = 0;
        timeGoneTillNextCapture = 0;
        shutterWaitCounter = 0;
        working = false;
    }

    private void sendMsg()
    {

        String t = "Time:"+String.format("%.2f ", (double) (new Date().getTime() - startTime) /1000 / 60);
        t+= "/"+ fullIntervalCaptureDuration + " NextIn:" + timeGoneTillNextCapture +"/" + sleepTimeBetweenCaptures /1000;
        picmodule.cameraUiWrapper.getCameraHolder().SendUIMessage(t);

    }

    //holds the time that is gone bevor next capture happens in Sec
    private int timeGoneTillNextCapture;
    public void DoNextInterval()
    {
        long dif = new Date().getTime() - startTime;
        double min = (double)(dif /1000) / 60;
        if (min >= fullIntervalCaptureDuration && fullIntervalCaptureDuration > 0)
        {
            Log.d(TAG, "Finished Interval");
            picmodule.cameraUiWrapper.getParameterHandler().IntervalCaptureFocusSet = false;
            picmodule.cameraUiWrapper.getParameterHandler().IntervalCapture = false;
            working = false;
            return;
        }
        Log.d(TAG, "Start StartNext Interval in" + sleepTimeBetweenCaptures + " " + min + " " + fullIntervalCaptureDuration);
        intervalDelayCounter = 0;
        handler.post(intervalDelayRunner);
    }

    private int intervalDelayCounter;
    private final Runnable intervalDelayRunner =new Runnable() {
        @Override
        public void run()
        {
            if (intervalDelayCounter < sleepTimeBetweenCaptures /1000) {
                handler.postDelayed(intervalDelayRunner, 1000);
                sendMsg();
                intervalDelayCounter++;
                timeGoneTillNextCapture++;
            }
            else {
                picmodule.DoWork();
                timeGoneTillNextCapture = 0;
            }
        }
    };

    private final Runnable shutterDelayRunner =new Runnable() {
        @Override
        public void run()
        {

            startShutterDelay();
        }
    };

    private void msg()
    {
        picmodule.cameraUiWrapper.getCameraHolder().SendUIMessage(shutterWaitCounter +"");
    }

    private int shutterWaitCounter;
    private void startShutterDelay()
    {
        Log.d(TAG, "Start ShutterDelay in " + shutterDelay);
        if (shutterWaitCounter < shutterDelay / 1000)
        {
            handler.postDelayed(shutterDelayRunner, 1000);
            msg();
            shutterWaitCounter++;
        }
        else
        {
            picmodule.DoWork();
            shutterWaitCounter = 0;
        }
    }

    public void StartShutterTime()
    {
        String shutterdelay = AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_TIMER);
        try {
            if (TextUtils.isEmpty(shutterdelay))
                shutterdelay = "0 sec";
            if (!shutterdelay.equals("0 sec"))
                shutterDelay = Integer.parseInt(shutterdelay.replace(" sec", "")) * 1000;
            else
                shutterDelay = 0;
            handler.postDelayed(shutterDelayRunner, shutterDelay);
        }
        catch (Exception ex)
        {
            Log.d("Freedcam",ex.getMessage());
        }
    }


}

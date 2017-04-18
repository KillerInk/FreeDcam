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

import java.util.Date;

import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by Ingo on 04.10.2015.
 */
public class IntervalHandler
{
    private final ModuleAbstract picmodule;

    private final String TAG = IntervalHandler.class.getSimpleName();

    private int intervalDuration;
    private int shutterDelay;
    private int intervalToEndDuration;
    private final Handler handler;
    private long startTime;
    private boolean working;
    private final AppSettingsManager appSettingsManager;

    public boolean IsWorking() {return working;}

    public IntervalHandler(ModuleAbstract picmodule, AppSettingsManager appSettingsManager)
    {
        this.picmodule = picmodule;
        handler = new Handler();
        this.appSettingsManager = appSettingsManager;
    }

    public void StartInterval()
    {
        Log.d(TAG, "Start Interval");
        working = true;
        startTime = new Date().getTime();
        String sleep = picmodule.cameraUiWrapper.getParameterHandler().IntervalShutterSleep.GetValue();
        if (sleep.contains(" sec"))
            intervalDuration = Integer.parseInt(sleep.replace(" sec",""))*1000;
        if (sleep.contains(" min"))
            intervalDuration = Integer.parseInt(sleep.replace(" min",""))*60*1000;

        String duration = picmodule.cameraUiWrapper.getParameterHandler().IntervalDuration.GetValue();
        if (duration.contains(" min"))
            intervalToEndDuration = Integer.parseInt(duration.replace(" min",""));
        else if (duration.contains(" h"))
            intervalToEndDuration = Integer.parseInt(duration.replace(" h",""))*60;

        startShutterDelay();
    }

    public void CancelInterval()
    {
        Log.d(TAG, "Cancel Interval");
        handler.removeCallbacks(intervalDelayRunner);
        handler.removeCallbacks(shutterDelayRunner);
        shuttercounter = 0;
        intervalDuration = 0;
        intervalToEndDuration = 0;
        intervalDelayCounter = 0;
        shuttercounter = 0;
        shutterWaitCounter = 0;
        working = false;
    }

    private void sendMsg()
    {

        String t = "Time:"+String.format("%.2f ", (double) (new Date().getTime() - startTime) /1000 / 60);
        t+= "/"+ intervalToEndDuration + " NextIn:" + shuttercounter +"/" + intervalDuration /1000;
        picmodule.cameraUiWrapper.getCameraHolder().SendUIMessage(t);

    }

    private int shuttercounter;
    public void DoNextInterval()
    {
        long dif = new Date().getTime() - startTime;
        double min = (double)(dif /1000) / 60;
        if (min >= intervalToEndDuration)
        {
            Log.d(TAG, "Finished Interval");
            picmodule.cameraUiWrapper.getParameterHandler().IntervalCaptureFocusSet = false;
            picmodule.cameraUiWrapper.getParameterHandler().IntervalCapture = false;
            working = false;
            return;
        }
        Log.d(TAG, "Start StartNext Interval in" + intervalDuration + " " + min + " " + intervalToEndDuration);
        intervalDelayCounter = 0;
        handler.post(intervalDelayRunner);
    }

    private int intervalDelayCounter;
    private final Runnable intervalDelayRunner =new Runnable() {
        @Override
        public void run()
        {
            if (intervalDelayCounter < intervalDuration /1000) {
                handler.postDelayed(intervalDelayRunner, 1000);
                sendMsg();
                intervalDelayCounter++;
                shuttercounter++;
            }
            else {
                picmodule.DoWork();
                shuttercounter = 0;
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
        String shutterdelay = appSettingsManager.getApiString(AppSettingsManager.SETTING_TIMER);
        try {
            if (shutterdelay.equals(""))
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

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

package com.freedcam.apis.basecamera.camera.modules;

import android.os.Handler;

import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.util.Date;

/**
 * Created by Ingo on 04.10.2015.
 */
class IntervalHandler
{
    private AbstractModule picmodule;

    private final String TAG = IntervalHandler.class.getSimpleName();

    private int intervalDuration = 0;
    private int shutterDelay = 0;
    private int intervalToEndDuration = 0;
    private Handler handler;
    private long startTime = 0;
    private boolean working = false;
    private AppSettingsManager appSettingsManager;

    public boolean IsWorking() {return  this.working;}

    public IntervalHandler(AbstractModule picmodule, AppSettingsManager appSettingsManager)
    {
        this.picmodule = picmodule;
        handler = new Handler();
        this.appSettingsManager = appSettingsManager;
    }

    public void StartInterval()
    {
        Logger.d(TAG, "Start Interval");
        this.working = true;
        this.startTime = new Date().getTime();
        String interval = picmodule.ParameterHandler.IntervalShutterSleep.GetValue().replace(" sec", "");
        this.intervalDuration = Integer.parseInt(interval)*1000;

        String endDuration = picmodule.ParameterHandler.IntervalDuration.GetValue().replace(" min","");
        this.intervalToEndDuration = Integer.parseInt(endDuration);
        startShutterDelay();
    }

    public void CancelInterval()
    {
        Logger.d(TAG, "Cancel Interval");
        handler.removeCallbacks(intervalDelayRunner);
        handler.removeCallbacks(shutterDelayRunner);
        shuttercounter = 0;
        intervalDuration = 0;
        intervalToEndDuration = 0;
        intervalDelayCounter = 0;
        shuttercounter = 0;
        shutterWaitCounter = 0;
        this.working = false;
    }

    private void sendMsg()
    {

        String t = "Time:"+String.format("%.2f ",((double)((new Date().getTime() - IntervalHandler.this.startTime)) /1000) / 60);
        t+=("/"+intervalToEndDuration+ " NextIn:" + shuttercounter +"/" + intervalDuration/1000);
        picmodule.cameraHolder.SendUIMessage(t);

    }

    private int shuttercounter = 0;
    public void DoNextInterval()
    {
        long dif = new Date().getTime() - IntervalHandler.this.startTime;
        double min = (double)(dif /1000) / 60;
        if (min >= IntervalHandler.this.intervalToEndDuration)
        {
            Logger.d(TAG, "Finished Interval");
            picmodule.ParameterHandler.IntervalCaptureFocusSet = false;
            picmodule.ParameterHandler.IntervalCapture = false;
            working = false;
            return;
        }
        Logger.d(TAG, "Start StartNext Interval in" + IntervalHandler.this.intervalDuration + " " + min + " " + IntervalHandler.this.intervalToEndDuration);
        intervalDelayCounter = 0;
        handler.post(intervalDelayRunner);
    }

    private int intervalDelayCounter;
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
                picmodule.DoWork();
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

    private void msg()
    {
        picmodule.cameraHolder.SendUIMessage(shutterWaitCounter+"");
    }

    private int shutterWaitCounter =0;
    private void startShutterDelay()
    {
        Logger.d(TAG, "Start ShutterDelay in " + IntervalHandler.this.shutterDelay);
        if (shutterWaitCounter <  IntervalHandler.this.shutterDelay / 1000)
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
        String shutterdelay = appSettingsManager.getString(AppSettingsManager.SETTING_TIMER);
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
            Logger.d("Freedcam",ex.getMessage());
        }
    }


}

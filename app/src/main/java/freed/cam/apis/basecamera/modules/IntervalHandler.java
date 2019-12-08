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
import android.os.Looper;

import java.util.Date;

import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by Ingo on 04.10.2015.
 */
public class IntervalHandler
{
    private final SuperDoWork picmodule;

    private final String TAG = IntervalHandler.class.getSimpleName();

    //the sleeptime how long the cam waits for the next capture in ms
    private int sleepTimeBetweenCaptures;
    private int shutterDelay;
    //how long the interval should run, 0 = infinity, maybe its need a check if sd is full^^
    private int fullIntervalCaptureDuration;
    private long startTime;
    private boolean working;
    //holds the time that is gone bevor next capture happens in Sec
    private int timeGoneTillNextCapture;
    private boolean extThread;
    private Handler handler;

    public interface SuperDoWork
    {
        void SuperDoTheWork();
        boolean isWorking();

    }


    public boolean IsWorking() {return working;}

    public IntervalHandler(SuperDoWork picmodule)
    {
        this.picmodule = picmodule;
        handler = new Handler(Looper.getMainLooper());

    }

    public void Init()
    {

    }

    public void Destroy()
    {
        if (working)
            CancelInterval();
    }

    public void StartInterval()
    {
        if (working)
            return;
        Log.d(TAG, "Start Interval" + " " + Thread.currentThread().getName());
        working = true;
        startTime = new Date().getTime();
        String sleep = SettingsManager.get(SettingKeys.INTERVAL_SHUTTER_SLEEP).get();
        if (sleep.contains(" sec"))
            sleepTimeBetweenCaptures = Integer.parseInt(sleep.replace(" sec",""))*1000;
        if (sleep.contains(" min"))
            sleepTimeBetweenCaptures = Integer.parseInt(sleep.replace(" min",""))*60*1000;

        String duration = SettingsManager.get(SettingKeys.INTERVAL_DURATION).get();
        if (duration.equals("∞"))
            fullIntervalCaptureDuration = 0;
        else if (duration.contains(" min"))
            fullIntervalCaptureDuration = Integer.parseInt(duration.replace(" min",""));
        else if (duration.contains(" h"))
            fullIntervalCaptureDuration = Integer.parseInt(duration.replace(" h",""))*60;

        //startShutterDelay();
        startInterval();
    }

    private Thread intervalBackgroundThread;

    private Object captureWaitLock = new Object();
    private boolean waitForCaptureEnd = false;

    private void startInterval()
    {
        Log.d(TAG, "Start IntervalThread" + " " + Thread.currentThread().getName());
        intervalBackgroundThread = new Thread(()->
        {
            Log.d(TAG, "Started IntervalThread" + " " + Thread.currentThread().getName());
            working = true;
            picmodule.SuperDoTheWork();
            boolean captureTimeOver=isIntervalCaptureTimeOver();
            while (!Thread.currentThread().isInterrupted() && !captureTimeOver && working)
            {

                if (timeGoneTillNextCapture < sleepTimeBetweenCaptures /1000) {

                    timeGoneTillNextCapture++;

                }
                else {
                   /* if (extThread && picmodule.isWorking()) {
                        synchronized (captureWaitLock) {
                            try {
                                Log.d(TAG, "wait for capture end:");
                                captureWaitLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }*/
                    //EventBus.getDefault().post(new StartWorkEvent());
                    picmodule.SuperDoTheWork();

                    timeGoneTillNextCapture = 0;
                }
                Log.d(TAG,"IntervalDelayCounter:" + timeGoneTillNextCapture );
                sendMsg();

                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                captureTimeOver=isIntervalCaptureTimeOver();
                Log.d(TAG,"CaptureTime is Over: " + captureTimeOver);
            }
            working = false;
            Log.d(TAG, "Stopped IntervalThread" + " " + Thread.currentThread().getName());
        });
        intervalBackgroundThread.setName("intervalBackgroundThread");
        intervalBackgroundThread.start();
    }

    public void CancelInterval()
    {
        Log.d(TAG, "Cancel Interval");
        working = false;
        intervalBackgroundThread.interrupt();

        /*while (!intervalBackgroundThread.isInterrupted() || working) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        intervalBackgroundThread = null;
        timeGoneTillNextCapture = 0;
        sleepTimeBetweenCaptures = 0;
    }

    private void sendMsg()
    {

        String t = "Time:"+String.format("%.2f ", (double) (new Date().getTime() - startTime) /1000 / 60);
        t+= "/"+ fullIntervalCaptureDuration + " NextIn:" + ((sleepTimeBetweenCaptures /1000) - timeGoneTillNextCapture);
        UserMessageHandler.sendMSG(t,false);

    }


    public void DoNextInterval()
    {

        extThread = Thread.currentThread() != intervalBackgroundThread;
        Log.d(TAG, "isextThread:" + extThread);
        /*if (extThread) {
            synchronized (captureWaitLock) {
                waitForCaptureEnd = false;
                captureWaitLock.notify();
            }
        }*/
        Log.d(TAG, "Start StartNext Interval in" + sleepTimeBetweenCaptures + " " + getTimeGoneSinceStart() + " " + fullIntervalCaptureDuration);
    }

    private boolean isIntervalCaptureTimeOver()
    {
        return getTimeGoneSinceStart() >= fullIntervalCaptureDuration && fullIntervalCaptureDuration > 0;
    }

    private double getTimeGoneSinceStart()
    {
        long dif = new Date().getTime() - startTime;
        return  (double)(dif /1000) / 60;
    }
}

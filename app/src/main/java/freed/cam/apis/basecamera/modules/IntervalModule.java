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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.Logger;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalModule extends ModuleAbstract
{
    private final ModuleAbstract picModule;
    private final IntervalHandler intervalHandler;
    private  final String TAG  = IntervalModule.class.getSimpleName();
    protected CaptureStateReciever captureStateReciever = new CaptureStateReciever();

    public IntervalModule(ModuleAbstract picModule, CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper, mBackgroundHandler);
        this.picModule = picModule;

        intervalHandler = new IntervalHandler(picModule, appSettingsManager);
        name = KEYS.MODULE_INTERVAL;
    }

    @Override
    public String ShortName() {
        return "Int";
    }

    @Override
    public String LongName() {
        return "Interval";
    }

    @Override
    public boolean DoWork()
    {
        if (!intervalHandler.IsWorking())
        {
            Logger.d(TAG, "StartInterval");
            isWorking = true;
            intervalHandler.StartInterval();
            sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_START);
            return true;
        } else {
            Logger.d(TAG, "Stop Interval");
            isWorking = false;
            intervalHandler.CancelInterval();
            if (picModule.isWorking)
            {
                Logger.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_WORKING);
            }
            else {
                Logger.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_NOTWORKING);
            }
            return false;
        }
    }

    @Override
    public void InitModule() {
        IntentFilter intentFilter = new IntentFilter(
                "troop.com.freedcam.capturestateIntent");
        cameraUiWrapper.getActivityInterface().RegisterLocalReciever(captureStateReciever,intentFilter);
        picModule.InitModule();

    }

   /* @Override
    public void SetCaptureStateChangedListner(CaptureStateChanged captureStateChangedListner) {
        super.SetCaptureStateChangedListner(captureStateChangedListner);
        picModule.SetCaptureStateChangedListner(this);
    }*/

    class CaptureStateReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int state = intent.getIntExtra("CaptureState",2);
            onCaptureStateChanged(state);
        }
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        cameraUiWrapper.getActivityInterface().UnregisterLocalReciever(captureStateReciever);
        picModule.DestroyModule();
    }

    public void onCaptureStateChanged(int captureStates)
    {
        Logger.d(TAG, "onCaptureStateChanged from picModule " + captureStates);
        switch (captureStates)
        {
            case CaptureStates.IMAGE_CAPTURE_STOP:
                if (isWorking)
                {
                    Logger.d(TAG, "image_capture_stop Work Finished, Start nex Capture");
                    sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP);
                    intervalHandler.DoNextInterval();

                }
                else
                {
                    if (picModule.isWorking) {
                        Logger.d(TAG, "changework to "+ CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP + " picmodule is working"+picModule.isWorking);
                        sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_WORK_STOP);
                    }
                    else {
                        sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_NOTWORKING);
                        Logger.d(TAG, "changework to "+ CaptureStates.CONTINOUSE_CAPTURE_STOP_WHILE_NOTWORKING + " picmodule is working"+picModule.isWorking);
                    }
                }
                break;
            case CaptureStates.IMAGE_CAPTURE_START:
                sendCaptureStateChangedBroadCast(CaptureStates.CONTINOUSE_CAPTURE_WORK_START);
                break;

        }
    }

    @Override
    public boolean IsWorking()
    {
        return intervalHandler.IsWorking();
    }
}

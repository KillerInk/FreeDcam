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

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.I_worker;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalModule extends AbstractModule implements I_worker
{
    private AbstractModule picModule;
    private IntervalHandler intervalHandler;
    private  final String TAG  = IntervalModule.class.getSimpleName();

    public IntervalModule(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, AbstractModule picModule, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        this.picModule = picModule;

        intervalHandler = new IntervalHandler(picModule,appSettingsManager);
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
            changeWorkState(CaptureModes.continouse_capture_start);
            return true;
        } else {
            Logger.d(TAG, "Stop Interval");
            isWorking = false;
            intervalHandler.CancelInterval();
            if (picModule.isWorking)
            {
                Logger.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                changeWorkState(CaptureModes.cont_capture_stop_while_working);
            }
            else {
                Logger.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                changeWorkState(CaptureModes.cont_capture_stop_while_notworking);
            }
            return false;
        }
    }

    @Override
    public void InitModule() {
        picModule.SetWorkerListner(this);
    }

    @Override
    public void DestroyModule() {

    }

    @Override
    public void onCaptureStateChanged(CaptureModes captureModes)
    {
        Logger.d(TAG, "onCaptureStateChanged from picModule " +captureModes);
        switch (captureModes)
        {
            case image_capture_stop:
                Logger.d(TAG, "Work Finished");
                if (isWorking)
                {
                    intervalHandler.DoNextInterval();
                    changeWorkState(CaptureModes.continouse_capture_work_stop);
                }
                else
                {
                    Logger.d(TAG, "changework to "+ CaptureModes.continouse_capture_stop);
                    if (picModule.isWorking)
                        changeWorkState(CaptureModes.continouse_capture_stop);
                    else
                        changeWorkState(CaptureModes.cont_capture_stop_while_notworking);
                }
                break;

        }
    }

    @Override
    public boolean IsWorking()
    {
        return intervalHandler.IsWorking();
    }
}

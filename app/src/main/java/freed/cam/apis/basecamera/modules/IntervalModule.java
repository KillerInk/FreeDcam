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

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.utils.Log;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalModule extends ModuleAbstract implements IntervalHandler.SuperDoWork
{
    private final ModuleAbstract picModule;
    protected final IntervalHandler intervalHandler;
    protected   final String TAG  = IntervalModule.class.getSimpleName();
    protected List<File> filesSaved;

    public IntervalModule(ModuleAbstract picModule, CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler,mainHandler);
        this.picModule = picModule;


        intervalHandler = new IntervalHandler(this);
        name = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_interval);
        filesSaved = new ArrayList<>();
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
    public void DoWork()
    {
        if (!isWorking)
        {
            Log.d(TAG, "StartInterval");
            isWorking = true;
            intervalHandler.StartInterval();
            changeCaptureState(CaptureStates.continouse_capture_start);
        } else {
            Log.d(TAG, "Stop Interval");

            intervalHandler.CancelInterval();
            if (picModule.isWorking)
            {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                changeCaptureState(CaptureStates.cont_capture_stop_while_working);
            }
            else {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                changeCaptureState(CaptureStates.cont_capture_stop_while_notworking);
            }
            isWorking = false;
        }
    }

    @Override
    public void InitModule() {
        super.InitModule();
        Log.d(TAG, "Init");
        intervalHandler.Init();
        picModule.InitModule();
        //picModule.SetCaptureStateChangedListner(this);
        picModule.setOverrideWorkFinishListner(this);
        changeCaptureState(CaptureStates.continouse_capture_stop);
    }

    /*@Override
    public void SetCaptureStateChangedListner(CaptureStateChanged captureStateChangedListner) {
        super.SetCaptureStateChangedListner(captureStateChangedListner);
        picModule.SetCaptureStateChangedListner(this);
    }*/

    @Override
    public void DestroyModule() {
        Log.d(TAG, "Destroy");
        intervalHandler.Destroy();
        //picModule.SetCaptureStateChangedListner(null);
        picModule.setOverrideWorkFinishListner(null);
        picModule.DestroyModule();
    }

    /*@Override
    public void onCaptureStateChanged(CaptureStates captureStates)
    {
        Log.d(TAG, "onCaptureStateChanged from picModule " + captureStates);
        switch (captureStates)
        {
            case image_capture_stop:
                if (isWorking)
                {
                    Log.d(TAG, "image_capture_stop Work Finished, Start nex Capture");
                    changeCaptureState(CaptureStates.continouse_capture_work_stop);
                }
                else
                {
                    if (picModule.isWorking) {
                        Log.d(TAG, "changework to "+ CaptureStates.continouse_capture_work_stop + " picmodule is working"+picModule.isWorking);
                        changeCaptureState(CaptureStates.continouse_capture_work_stop);
                    }
                    else {
                        changeCaptureState(CaptureStates.cont_capture_stop_while_notworking);
                        Log.d(TAG, "changework to "+ CaptureStates.cont_capture_stop_while_notworking + " picmodule is working"+picModule.isWorking);
                    }
                }
                break;
            case image_capture_start:
                changeCaptureState(CaptureStates.continouse_capture_work_start);
                break;

        }
    }*/

    @Override
    public boolean IsWorking()
    {
        return intervalHandler.IsWorking();
    }

    @Override
    public void internalFireOnWorkDone(File file) {
        Log.d(TAG,"internalFireOnWorkDone");
       /* filesSaved.add(file);
        if (!isWorking) {
            super.fireOnWorkFinish(filesSaved.toArray(new File[filesSaved.size()]));
            filesSaved.clear();
        }
        else*/
            intervalHandler.DoNextInterval();

    }

    @Override
    public void SuperDoTheWork() {
        picModule.DoWork();
    }

    @Override
    public boolean isWorking() {
        return picModule.isWorking;
    }
}

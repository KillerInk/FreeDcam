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

package freed.cam.apis.camera2.modules;

import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalHandler;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.utils.Log;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends PictureModuleApi2 implements I_PreviewWrapper, IntervalHandler.SuperDoWork
{

    protected final IntervalHandler intervalHandler;
    protected   final String TAG  = IntervalApi2.class.getSimpleName();

    private boolean module_isWorking;

    public IntervalApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_interval);
        intervalHandler = new IntervalHandler(this);
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
    public void InitModule() {
        super.InitModule();
        Log.d(TAG, "Init");
        module_isWorking = false;
        intervalHandler.Init();
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_stop);
    }
    @Override
    public void DestroyModule() {
        super.DestroyModule();
        Log.d(TAG, "Destroy");
        intervalHandler.Destroy();
    }

    @Override
    public void DoWork()
    {
        if (!module_isWorking)
        {
            Log.d(TAG, "StartInterval");
            module_isWorking = true;
            intervalHandler.StartInterval();
            changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_start);
        } else {
            Log.d(TAG, "Stop Interval");

            intervalHandler.CancelInterval();
            if (module_isWorking)
            {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_working);
            }
            else {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_notworking);
            }
            module_isWorking = false;
        }
    }

    /*@Override
    public void onCaptureStateChanged(ModuleHandlerAbstract.CaptureStates captureStates)
    {
        if (captureStates == null)
            return;
        Log.d(TAG, "onCaptureStateChanged from picModule " + captureStates);
        switch (captureStates)
        {
            case image_capture_stop:
                if (module_isWorking)
                {
                    Log.d(TAG, "image_capture_stop Work Finished, Start nex Capture");
                    changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_work_stop);
                }
                else
                {
                    if (module_isWorking) {
                        Log.d(TAG, "changework to "+ ModuleHandlerAbstract.CaptureStates.continouse_capture_work_stop + " picmodule is working"+isWorking);
                        changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_work_stop);
                    }
                    else {
                        changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_notworking);
                        Log.d(TAG, "changework to "+ ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_notworking + " picmodule is working"+isWorking);
                    }
                }
                break;
            case image_capture_start:
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_work_start);
                break;

        }
        if (captureStates == ModuleHandlerAbstract.CaptureStates.image_capture_stop)
            intervalHandler.DoNextInterval();
    }*/

   /* @Override
    public void SetCaptureStateChangedListner(ModuleHandlerAbstract.CaptureStateChanged captureStateChangedListner) {
        super.SetCaptureStateChangedListner(this);
        this.acitvecaptureStateChangedListner = captureStateChangedListner;
    }*/

  /*  @Override
    public void changeCaptureState(ModuleHandlerAbstract.CaptureStates captureStates) {
        if (acitvecaptureStateChangedListner != null)
            acitvecaptureStateChangedListner.onCaptureStateChanged(captureStates);

    }*/

    @Override
    public void SuperDoTheWork() {
        super.DoWork();
    }

    @Override
    public boolean isWorking() {
        return super.isWorking;
    }
}

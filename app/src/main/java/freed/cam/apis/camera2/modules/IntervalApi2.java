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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.IntervalModule;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.utils.Log;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends IntervalModule implements I_PreviewWrapper
{
    private final PictureModuleApi2 picModule;
    public IntervalApi2(ModuleAbstract picModule, CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(picModule,cameraUiWrapper,mBackgroundHandler,mainHandler);
        this.picModule = (PictureModuleApi2)picModule;
    }


    @Override
    public void startPreview() {
        picModule.startPreview();
    }

    @Override
    public void stopPreview() {
        picModule.stopPreview();
    }

    @Override
    public void InitModule()
    {
        picModule.InitModule();
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_stop);
    }

    @Override
    public void DestroyModule() {
        picModule.DestroyModule();
    }

    @Override
    public void DoWork()
    {
        if (!intervalHandler.IsWorking())
        {
            Log.d(TAG, "StartInterval");
            isWorking = true;
            intervalHandler.StartInterval();
            changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_start);
            return;
        } else {
            Log.d(TAG, "Stop Interval");
            isWorking = false;
            intervalHandler.CancelInterval();
            if (picModule.isWorking)
            {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_working");
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_working);
            }
            else {
                Log.d(TAG, "changeWorkstate to cont_capture_stop_while_notworking");
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.cont_capture_stop_while_notworking);
            }
            return;
        }
    }
}

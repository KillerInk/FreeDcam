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

package freed.cam.apis.sonyremote.modules;

import android.os.Handler;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.sonyremote.CameraHolderSony;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.file.holder.BaseHolder;

/**
 * Created by troop on 08.06.2015.
 */
public class VideoModuleSony extends ModuleAbstract implements I_CameraStatusChanged
{
    private final String TAG = VideoModuleSony.class.getSimpleName();
    private final CameraHolderSony cameraHolder;

    public VideoModuleSony(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = FreedApplication.getStringFromRessources(R.string.module_video);
        cameraHolder = (CameraHolderSony)cameraUiWrapper.getCameraHolder();

    }

    @Override
    public String LongName() {
        return "Movie";
    }

    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public void DoWork()
    {
        if (!isWorking)
        {
            cameraHolder.StartRecording();
        }
        else cameraHolder.StopRecording();
    }

    @Override
    public void InitModule() {
        ((ParameterHandler)cameraUiWrapper.getParameterHandler()).CameraStatusListner = this;
        changeCaptureState(CaptureStates.video_recording_stop);
        onCameraStatusChanged(((ParameterHandler)cameraUiWrapper.getParameterHandler()).GetCameraStatus());
    }

    @Override
    public void DestroyModule() {

    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        if (status.equals("IDLE") && isWorking)
        {
            isWorking = false;
            changeCaptureState(CaptureStates.video_recording_stop);
        }
        else if (status.equals("MovieWaitRecStart") && !isWorking) {
            isWorking = true;
            changeCaptureState(CaptureStates.video_recording_start);
        }

    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }
}

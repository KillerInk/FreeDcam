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

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.CaptureStates;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.sonyremote.CameraHolderSony;

/**
 * Created by troop on 08.06.2015.
 */
public class VideoModuleSony extends ModuleAbstract implements I_CameraStatusChanged
{
    private final String TAG = VideoModuleSony.class.getSimpleName();
    private final CameraHolderSony cameraHolder;

    public VideoModuleSony(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
        name = KEYS.MODULE_VIDEO;
        cameraHolder = (CameraHolderSony)cameraUiWrapper.GetCameraHolder();

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
    public boolean DoWork()
    {
        if (!isWorking)
        {
            cameraHolder.StartRecording();
        }
        else cameraHolder.StopRecording();
        return true;
    }

    @Override
    public void InitModule() {
        cameraHolder.CameraStatusListner = this;
        onCameraStatusChanged(cameraHolder.GetCameraStatus());
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
            changeCaptureState(CaptureStates.RECORDING_STOP);
        }
        else if (status.equals("MovieWaitRecStart") && !isWorking) {
            isWorking = true;
            changeCaptureState(CaptureStates.RECORDING_START);
        }

    }
}

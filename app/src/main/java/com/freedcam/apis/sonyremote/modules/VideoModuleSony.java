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

package com.freedcam.apis.sonyremote.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureStates;
import com.freedcam.apis.sonyremote.CameraHolder;

/**
 * Created by troop on 08.06.2015.
 */
public class VideoModuleSony extends AbstractModule implements I_CameraStatusChanged
{
    private static String TAG = VideoModuleSony.class.getSimpleName();
    private CameraHolder cameraHolder;

    public VideoModuleSony(I_CameraUiWrapper cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_VIDEO;
        cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();

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
            changeCaptureState(CaptureStates.video_recording_stop);
        }
        else if (status.equals("MovieWaitRecStart") && !isWorking) {
            isWorking = true;
            changeCaptureState(CaptureStates.video_recording_start);
        }

    }
}

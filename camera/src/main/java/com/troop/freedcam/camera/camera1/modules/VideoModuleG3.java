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

package com.troop.freedcam.camera.camera1.modules;

import android.location.Location;
import android.media.MediaRecorder.VideoSource;
import android.os.Handler;

import com.lge.media.MediaRecorderExRef;
import com.troop.freedcam.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.camera.basecamera.record.VideoRecorder;
import com.troop.freedcam.camera.camera1.CameraHolder;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.camera.camera1.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.file.holder.BaseHolder;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.VideoMediaProfile;
import com.troop.freedcam.utils.VideoMediaProfile.VideoMode;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraControllerInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }

    protected void initRecorder()
    {
        recorder = new VideoRecorder(cameraUiWrapper ,new MediaRecorderExRef().getMediaRecorder());
        if (currentProfile == null)
        {
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);

        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_))){
            Location location = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
            if (location != null)
                recorder.setLocation(location);
        }
        recorder.setVideoSource(VideoSource.CAMERA);

    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        if (((ParametersHandler)cameraUiWrapper.getParameterHandler()).getParameters().get("preview-fps-range") != null) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters().set("preview-fps-range", "30000,30000");
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters());
        }
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("2160p"))
        {
            ParameterInterface mce = cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement);
            if(mce != null && mce.getViewState() == AbstractParameter.ViewState.Visible)
                mce.SetValue(ContextApplication.getStringFromRessources(R.string.disable_),false);
            ParameterInterface dis = cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization);
            if (dis!= null && dis.getViewState() == AbstractParameter.ViewState.Visible)
                dis.SetValue(ContextApplication.getStringFromRessources(R.string.disable_), false);
            ParameterInterface denoise = cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise);
            if (denoise != null && denoise.getViewState() == AbstractParameter.ViewState.Visible)
                denoise.SetValue("denoise-off", false);
            if(!SettingsManager.getInstance().hasCamera2Features())
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("nv12-venus",false);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                ParameterInterface hfr = cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate);
                if (hfr != null && hfr.getViewState() == AbstractParameter.ViewState.Visible)
                {
                    hfr.SetValue(currentProfile.videoFrameRate+"", false);
                }
            }
        }
        else
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("yuv420sp", false);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).SetValue(size,false);
        cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoSize).SetValue(size,true);
        /*cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();*/
    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }
}

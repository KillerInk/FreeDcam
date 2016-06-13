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

package com.freedcam.apis.camera1.modules;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.modes.VideoProfilesG3Parameter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils.Devices;
import com.lge.media.MediaRecorderEx;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private MediaRecorderEx recorder;
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    protected MediaRecorder initRecorder()
    {

        try {
            recorder = new MediaRecorderEx();
            recorder.reset();
            recorder.setCamera(((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera());
            recorder.setVideoSource(VideoSource.CAMERA);
            switch (currentProfile.Mode)
            {

                case Normal:
                case Highspeed:
                    if (currentProfile.isAudioActive)
                        recorder.setAudioSource(AudioSource.CAMCORDER);
                    break;
                case Timelapse:
                    break;
            }

            recorder.setOutputFormat(OutputFormat.MPEG_4);
            recorder.setVideoFrameRate(currentProfile.videoFrameRate);
            recorder.setVideoSize(currentProfile.videoFrameWidth, currentProfile.videoFrameHeight);
            recorder.setVideoEncodingBitRate(currentProfile.videoBitRate);
            recorder.setVideoEncoder(currentProfile.videoCodec);

            switch (currentProfile.Mode)
            {
                case Normal:
                case Highspeed:
                    if (currentProfile.isAudioActive)
                        setAudioStuff(currentProfile);
                    break;
                case Timelapse:
                    float frame = 30;
                    if (!appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                        frame = Float.parseFloat(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                    else
                        appSettingsManager.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, "" + frame);
                    recorder.setCaptureRate(frame);
                    break;
            }
            if (appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD")) {

                recorder.setMaxFileSize(3037822976L);
                recorder.setMaxDuration(7200000);
            }
        }
        catch (IllegalStateException ex)
        {
            recorder.reset();
        }
        return recorder;
    }

    private void setAudioStuff(VideoMediaProfile prof) {
        recorder.setAudioSamplingRate(prof.audioSampleRate);
        recorder.setAudioEncodingBitRate(prof.audioBitRate);
        recorder.setAudioChannels(prof.audioChannels);
        recorder.setAudioEncoder(prof.audioCodec);
    }

    @Override
    public void InitModule()
    {
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {

    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter) cameraUiWrapper.GetParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("4kUHD"))
        {
            cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.SetValue("disable",true);
            cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue("disable", true);
            cameraUiWrapper.GetParameterHandler().Denoise.SetValue("denoise-off", true);
            if(appSettingsManager.getDevice() != Devices.LG_G4)
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("nv12-venus",true);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported())
                {
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", true);
                }
            }
        }
        else
        {
            cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp", true);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.GetParameterHandler().PreviewSize.SetValue(size,true);
        cameraUiWrapper.GetParameterHandler().VideoSize.SetValue(size,true);
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StartPreview();
    }
}

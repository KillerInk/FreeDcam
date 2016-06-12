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
import android.os.Environment;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.modes.VideoProfilesParameter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.Logger;


/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private static String TAG = VideoModule.class.getSimpleName();
    private VideoMediaProfile currentProfile;

    public VideoModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }



    protected MediaRecorder initRecorder()
    {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCamera());

        recorder.setVideoSource(VideoSource.CAMERA);

        switch (currentProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if(currentProfile.isAudioActive)
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
                if(currentProfile.isAudioActive)
                {
                    recorder.setAudioSamplingRate(currentProfile.audioSampleRate);
                    recorder.setAudioEncodingBitRate(currentProfile.audioBitRate);
                    recorder.setAudioChannels(currentProfile.audioChannels);
                    recorder.setAudioEncoder(currentProfile.audioCodec);
                }
                break;
            case Timelapse:
                float frame = 60;
                if(!appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                    frame = Float.parseFloat(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                else
                    appSettingsManager.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, ""+frame);
                recorder.setCaptureRate(frame);
                break;
        }
        return recorder;
    }



    @Override
    public void InitModule()
    {

        if (cameraUiWrapper.GetParameterHandler().VideoHDR != null)
            if(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && cameraUiWrapper.GetParameterHandler().VideoHDR.IsSupported())
                cameraUiWrapper.GetParameterHandler().VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (cameraUiWrapper.GetParameterHandler().VideoHDR != null && cameraUiWrapper.GetParameterHandler().VideoHDR.IsSupported())
            cameraUiWrapper.GetParameterHandler().VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)cameraUiWrapper.GetParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            if(currentProfile.ProfileName.equals("1080pHFR")
                    && appSettingsManager.getDevice() == Devices.XiaomiMI3W
                    || appSettingsManager.getDevice() == Devices.ZTE_ADV)
                cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("60",true);
            if(currentProfile.ProfileName.equals("720pHFR") && appSettingsManager.getDevice() == Devices.ZTE_ADV)
                cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("120", true);

            if(currentProfile.ProfileName.equals("720pHFR")
                    && appSettingsManager.getDevice() == Devices.XiaomiMI3W
                    || appSettingsManager.getDevice() == Devices.ZTE_ADV
                    || appSettingsManager.getDevice() == Devices.ZTEADV234
                    ||appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
            {
                cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("120",true);
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("nv12-venus", true);
            }

            if (cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement != null && cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.IsSupported())
                cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.SetValue("disable", true);
            if (cameraUiWrapper.GetParameterHandler().DigitalImageStabilization != null && cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.IsSupported())
                cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue("disable", true);
            if (cameraUiWrapper.GetParameterHandler().VideoStabilization != null && cameraUiWrapper.GetParameterHandler().VideoStabilization.IsSupported())
                cameraUiWrapper.GetParameterHandler().VideoStabilization.SetValue("false", true);
            if (cameraUiWrapper.GetParameterHandler().Denoise != null && cameraUiWrapper.GetParameterHandler().Denoise.IsSupported())
                cameraUiWrapper.GetParameterHandler().Denoise.SetValue("denoise-off", true);
            cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp", true);
            if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported())
            {
                cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", true);
            }
        }
        else
        {
            if (currentProfile.ProfileName.contains(VideoProfilesParameter._4kUHD))
            {
                if (cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement != null && cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.IsSupported())
                    cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.SetValue("disable", true);
                if (cameraUiWrapper.GetParameterHandler().DigitalImageStabilization != null && cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue("disable", true);
                if (cameraUiWrapper.GetParameterHandler().VideoStabilization != null && cameraUiWrapper.GetParameterHandler().VideoStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().VideoStabilization.SetValue("false", true);
                if (cameraUiWrapper.GetParameterHandler().Denoise != null && cameraUiWrapper.GetParameterHandler().Denoise.IsSupported())
                    cameraUiWrapper.GetParameterHandler().Denoise.SetValue("denoise-off", true);
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("nv12-venus",true);
            }
            else
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp", true);
            if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported())
            {
                cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("off", true);
            }
        }

        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.GetParameterHandler().PreviewSize.SetValue(size,true);
        cameraUiWrapper.GetParameterHandler().VideoSize.SetValue(size, true);
        //cameraUiWrapper.GetParameterHandler().SetParametersToCamera(cameraUiWrapper.GetParameterHandler().getParameters());
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StartPreview();

    }

    private void videoTime(int VB, int AB)
    {
        int i = VB / 2;

        long l2 = (i + AB >> 3) / 1000;
        // long l3 = Environment.getExternalStorageDirectory().getUsableSpace() / l2;
        Logger.d("VideoCamera Remaing", getTimeString(Environment.getExternalStorageDirectory().getUsableSpace() / l2)) ;

    }

    private String getTimeString(long paramLong)
    {
        long l1 = paramLong / 1000L;
        long l2 = l1 / 60L;
        long l3 = l2 / 60L;
        long l4 = l2 - 60L * l3;
        String str1 = Long.toString(l1 - 60L * l2);
        if (str1.length() < 2) {
            str1 = "0" + str1;
        }
        String str2 = Long.toString(l4);
        if (str2.length() < 2) {
            str2 = "0" + str2;
        }
        String str3 = str2 + ":" + str1;
        if (l3 > 0L)
        {
            String str4 = Long.toString(l3);
            if (str4.length() < 2) {
                str4 = "0" + str4;
            }
            str3 = str4 + ":" + str3;
        }
        return str3;
    }
}
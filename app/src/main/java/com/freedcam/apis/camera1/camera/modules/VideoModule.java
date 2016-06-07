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

package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesParameter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;


/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private static String TAG = VideoModule.class.getSimpleName();
    private VideoMediaProfile currentProfile;

    public VideoModule(CameraHolder cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context, appSettingsManager);
    }



    protected MediaRecorder initRecorder()
    {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(cameraHolder.GetCamera());

        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        switch (currentProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if(currentProfile.isAudioActive)
                    recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                break;
            case Timelapse:
                break;
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
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

        if (ParameterHandler.VideoHDR != null)
            if(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && ParameterHandler.VideoHDR.IsSupported())
                ParameterHandler.VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported())
            ParameterHandler.VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMediaProfile.VideoMode.Highspeed)
        {
            if(currentProfile.ProfileName.equals("1080pHFR")
                    && appSettingsManager.getDevice() ==DeviceUtils.Devices.XiaomiMI3W
                    || appSettingsManager.getDevice() ==(DeviceUtils.Devices.ZTE_ADV))
                ParameterHandler.VideoHighFramerateVideo.SetValue("60",true);
            if(currentProfile.ProfileName.equals("720pHFR") && appSettingsManager.getDevice() == DeviceUtils.Devices.ZTE_ADV)
                ParameterHandler.VideoHighFramerateVideo.SetValue("120", true);

            if(currentProfile.ProfileName.equals("720pHFR")
                    && (appSettingsManager.getDevice() == DeviceUtils.Devices.XiaomiMI3W)
                    || appSettingsManager.getDevice() == DeviceUtils.Devices.ZTE_ADV
                    || appSettingsManager.getDevice() ==DeviceUtils.Devices.ZTEADV234
                    ||appSettingsManager.getDevice() == DeviceUtils.Devices.ZTEADVIMX214)
            {
                ParameterHandler.VideoHighFramerateVideo.SetValue("120",true);
                ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);
            }

            if (ParameterHandler.MemoryColorEnhancement != null && ParameterHandler.MemoryColorEnhancement.IsSupported())
                ParameterHandler.MemoryColorEnhancement.SetValue("disable", true);
            if (ParameterHandler.DigitalImageStabilization != null && ParameterHandler.DigitalImageStabilization.IsSupported())
                ParameterHandler.DigitalImageStabilization.SetValue("disable", true);
            if (ParameterHandler.VideoStabilization != null && ParameterHandler.VideoStabilization.IsSupported())
                ParameterHandler.VideoStabilization.SetValue("false", true);
            if (ParameterHandler.Denoise != null && ParameterHandler.Denoise.IsSupported())
                ParameterHandler.Denoise.SetValue("denoise-off", true);
            ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
            if (ParameterHandler.VideoHighFramerateVideo != null && ParameterHandler.VideoHighFramerateVideo.IsSupported())
            {
                ParameterHandler.VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", true);
            }
        }
        else
        {
            if (currentProfile.ProfileName.contains(VideoProfilesParameter._4kUHD))
            {
                if (ParameterHandler.MemoryColorEnhancement != null && ParameterHandler.MemoryColorEnhancement.IsSupported())
                    ParameterHandler.MemoryColorEnhancement.SetValue("disable", true);
                if (ParameterHandler.DigitalImageStabilization != null && ParameterHandler.DigitalImageStabilization.IsSupported())
                    ParameterHandler.DigitalImageStabilization.SetValue("disable", true);
                if (ParameterHandler.VideoStabilization != null && ParameterHandler.VideoStabilization.IsSupported())
                    ParameterHandler.VideoStabilization.SetValue("false", true);
                if (ParameterHandler.Denoise != null && ParameterHandler.Denoise.IsSupported())
                    ParameterHandler.Denoise.SetValue("denoise-off", true);
                ParameterHandler.PreviewFormat.SetValue("nv12-venus",true);
            }
            else
                ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
            if (ParameterHandler.VideoHighFramerateVideo != null && ParameterHandler.VideoHighFramerateVideo.IsSupported())
            {
                ParameterHandler.VideoHighFramerateVideo.SetValue("off", true);
            }
        }

        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        ParameterHandler.PreviewSize.SetValue(size,true);
        ParameterHandler.VideoSize.SetValue(size, true);
        //ParameterHandler.SetParametersToCamera(ParameterHandler.getParameters());
        cameraHolder.StopPreview();
        cameraHolder.StartPreview();

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
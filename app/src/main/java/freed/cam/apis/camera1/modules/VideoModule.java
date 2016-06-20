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

package freed.cam.apis.camera1.modules;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;


/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private final String TAG = VideoModule.class.getSimpleName();
    private VideoMediaProfile currentProfile;

    public VideoModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }



    protected MediaRecorder initRecorder()
    {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera());

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
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.GetParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            if(appSettingsManager.getDevice() == Devices.Htc_M8 ||appSettingsManager.getDevice() == Devices.Htc_M9||appSettingsManager.getDevice() == Devices.HTC_OneA9||appSettingsManager.getDevice() == Devices.HTC_OneE8 ) {
                if (currentProfile.ProfileName.equals("1080pHFR"))
                {
                    cameraUiWrapper.GetParameterHandler().HTCVideoMode.SetValue("2",true);
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("off", true);
                }
                else if (currentProfile.ProfileName.equals("720pHFR"))
                {
                    if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
                    {
                        cameraUiWrapper.GetParameterHandler().HTCVideoMode.SetValue("2",true);
                        cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("off", true);
                    }
                    else {
                        cameraUiWrapper.GetParameterHandler().HTCVideoMode.SetValue("1",true);
                        cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("120", true);}
                }

            }

            else if (((CameraHolder)cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == CameraHolder.Frameworks.MTK)
            {
                if(cameraUiWrapper.GetParameterHandler().PreviewFPS.GetValues().toString().contains(currentProfile.videoFrameRate+""))
                {
                    cameraUiWrapper.GetParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate+"",true);

                    if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported()) {
                        cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", true);
                    }

                }
            }

            else
            {
            if (currentProfile.ProfileName.equals("1080pHFR")
                        && appSettingsManager.getDevice() == Devices.XiaomiMI3W
                        || appSettingsManager.getDevice() == Devices.ZTE_ADV)
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("60", true);
                if (currentProfile.ProfileName.equals("720pHFR") && appSettingsManager.getDevice() == Devices.ZTE_ADV)
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue("120", true);


                if (cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement != null && cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.IsSupported())
                    cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.SetValue("disable", true);
                if (cameraUiWrapper.GetParameterHandler().DigitalImageStabilization != null && cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue("disable", true);
                if (cameraUiWrapper.GetParameterHandler().VideoStabilization != null && cameraUiWrapper.GetParameterHandler().VideoStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().VideoStabilization.SetValue("false", true);
                if (cameraUiWrapper.GetParameterHandler().Denoise != null && cameraUiWrapper.GetParameterHandler().Denoise.IsSupported())
                    cameraUiWrapper.GetParameterHandler().Denoise.SetValue("denoise-off", true);
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp", true);
                if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported()) {
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", true);
                }
            }
        }
        else
        {
            if (currentProfile.ProfileName.contains(VideoProfilesParameter._4kUHD) ||currentProfile.ProfileName.contains(VideoProfilesParameter._4kDCI))
            {

                if (cameraUiWrapper.GetParameterHandler().DigitalImageStabilization != null && cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue("disable", true);
                if (cameraUiWrapper.GetParameterHandler().VideoStabilization != null && cameraUiWrapper.GetParameterHandler().VideoStabilization.IsSupported())
                    cameraUiWrapper.GetParameterHandler().VideoStabilization.SetValue("false", true);

                if (((CameraHolder)cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != CameraHolder.Frameworks.MTK)
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


}
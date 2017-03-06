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

import android.location.Location;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;
import android.os.Handler;

import com.lge.media.MediaRecorderEx;
import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.utils.AppSettingsManager;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private MediaRecorderEx recorder;
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
    }

    protected MediaRecorder initRecorder()
    {
        try {
            recorder = new MediaRecorderEx();
            recorder.reset();
            recorder.setCamera(((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera());
            if (cameraUiWrapper.GetAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_))){
                Location location = cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation();
                if (location != null)
                    recorder.setLocation((float) location.getLatitude(), (float) location.getLongitude());
            }
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
            try {
                recorder.setVideoEncoder(currentProfile.videoCodec);
            }
            catch (IllegalArgumentException ex)
            {
                recorder.reset();
                cameraUiWrapper.GetCameraHolder().SendUIMessage("VideoCodec not Supported");
            }


            switch (currentProfile.Mode)
            {
                case Normal:
                case Highspeed:
                    if (currentProfile.isAudioActive)
                        setAudioStuff(currentProfile);
                    break;
                case Timelapse:
                    float frame = 30;
                    if (!appSettingsManager.getApiString(AppSettingsManager.TIMELAPSEFRAME).equals(""))
                        frame = Float.parseFloat(appSettingsManager.getApiString(AppSettingsManager.TIMELAPSEFRAME).replace(",", "."));
                    else
                        appSettingsManager.setApiString(AppSettingsManager.TIMELAPSEFRAME, "" + frame);
                    recorder.setCaptureRate(frame);
                    break;
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
        try {
            recorder.setAudioEncoder(prof.audioCodec);
        }
        catch (IllegalArgumentException ex)
        {
            recorder.reset();
            cameraUiWrapper.GetCameraHolder().SendUIMessage("AudioCodec not Supported");
        }

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
        super.DestroyModule();
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.GetParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.videoProfile.get());
        if (((ParametersHandler)cameraUiWrapper.GetParameterHandler()).getParameters().get("preview-fps-range") != null) {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).getParameters().set("preview-fps-range", "30000,30000");
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(((ParametersHandler) cameraUiWrapper.GetParameterHandler()).getParameters());
        }
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("2160p"))
        {
            cameraUiWrapper.GetParameterHandler().MemoryColorEnhancement.SetValue(appSettingsManager.getResString(R.string.disable_),false);
            cameraUiWrapper.GetParameterHandler().DigitalImageStabilization.SetValue(appSettingsManager.getResString(R.string.disable_), false);
            cameraUiWrapper.GetParameterHandler().Denoise.SetValue("denoise-off", false);
            if(!appSettingsManager.IsCamera2FullSupported())
                cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("nv12-venus",false);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                if (cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.IsSupported())
                {
                    cameraUiWrapper.GetParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", false);
                }
            }
        }
        else
        {
            cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp", false);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.GetParameterHandler().PreviewSize.SetValue(size,false);
        cameraUiWrapper.GetParameterHandler().VideoSize.SetValue(size,true);
        /*cameraUiWrapper.StopPreview();
        cameraUiWrapper.StartPreview();*/
    }
}

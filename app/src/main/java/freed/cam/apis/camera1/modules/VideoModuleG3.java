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
import android.text.TextUtils;

import com.lge.media.MediaRecorderExRef;
import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.settings.AppSettingsManager;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private MediaRecorder recorder;
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }

    protected MediaRecorder initRecorder()
    {
        try {
            recorder = new MediaRecorderExRef().getMediaRecorder();
            recorder.reset();
            recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
            if (AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_))){
                Location location = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
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
                cameraUiWrapper.getCameraHolder().SendUIMessage("VideoCodec not Supported");
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
                    if (!TextUtils.isEmpty(AppSettingsManager.getInstance().getApiString(AppSettingsManager.TIMELAPSEFRAME)))
                        frame = Float.parseFloat(AppSettingsManager.getInstance().getApiString(AppSettingsManager.TIMELAPSEFRAME).replace(",", "."));
                    else
                        AppSettingsManager.getInstance().setApiString(AppSettingsManager.TIMELAPSEFRAME, "" + frame);
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
            cameraUiWrapper.getCameraHolder().SendUIMessage("AudioCodec not Supported");
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
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(AppSettingsManager.getInstance().videoProfile.get());
        if (((ParametersHandler)cameraUiWrapper.getParameterHandler()).getParameters().get("preview-fps-range") != null) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters().set("preview-fps-range", "30000,30000");
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters());
        }
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("2160p"))
        {
            if(cameraUiWrapper.getParameterHandler().MemoryColorEnhancement != null && cameraUiWrapper.getParameterHandler().MemoryColorEnhancement.IsSupported())
                cameraUiWrapper.getParameterHandler().MemoryColorEnhancement.SetValue(AppSettingsManager.getInstance().getResString(R.string.disable_),false);
            if (cameraUiWrapper.getParameterHandler().DigitalImageStabilization != null && cameraUiWrapper.getParameterHandler().DigitalImageStabilization.IsSupported())
                cameraUiWrapper.getParameterHandler().DigitalImageStabilization.SetValue(AppSettingsManager.getInstance().getResString(R.string.disable_), false);
            if (cameraUiWrapper.getParameterHandler().Denoise != null && cameraUiWrapper.getParameterHandler().Denoise.IsSupported())
                cameraUiWrapper.getParameterHandler().Denoise.SetValue("denoise-off", false);
            if(!AppSettingsManager.getInstance().hasCamera2Features())
                cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("nv12-venus",false);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                if (cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.IsSupported())
                {
                    cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", false);
                }
            }
        }
        else
        {
            cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("yuv420sp", false);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.getParameterHandler().PreviewSize.SetValue(size,false);
        cameraUiWrapper.getParameterHandler().VideoSize.SetValue(size,true);
        /*cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();*/
    }

    @Override
    public void internalFireOnWorkDone(File file) {

    }
}

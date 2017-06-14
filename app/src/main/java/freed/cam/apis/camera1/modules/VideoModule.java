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

import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;


/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private final String TAG = VideoModule.class.getSimpleName();
    private VideoMediaProfile currentProfile;

    public VideoModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }



    protected MediaRecorder initRecorder()
    {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (cameraUiWrapper.getAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_))){
            Location location = cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation();
            if (location != null)
                recorder.setLocation((float) location.getLatitude(), (float) location.getLongitude());
        }

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
        int maXFPS = 30;
        recorder.setVideoFrameRate(maXFPS);
        recorder.setVideoSize(currentProfile.videoFrameWidth, currentProfile.videoFrameHeight);
        recorder.setVideoEncodingBitRate(currentProfile.videoBitRate);
        try {
            recorder.setVideoEncoder(currentProfile.videoCodec);
        }
        catch (IllegalArgumentException ex)
        {
            recorder.reset();
            cameraUiWrapper.getCameraHolder().SendUIMessage("VideoCodec not Supported");
            Log.WriteEx(ex);
        }

        switch (currentProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if(currentProfile.isAudioActive)
                {
                    recorder.setAudioSamplingRate(currentProfile.audioSampleRate);
                    recorder.setAudioEncodingBitRate(currentProfile.audioBitRate);
                    recorder.setAudioChannels(currentProfile.audioChannels);
                    try {
                        recorder.setAudioEncoder(currentProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        recorder.reset();
                        cameraUiWrapper.getCameraHolder().SendUIMessage("AudioCodec not Supported");
                        Log.WriteEx(ex);
                    }
                }
                break;
            case Timelapse:
                float frame = 60;
                if(!appSettingsManager.getApiString(AppSettingsManager.TIMELAPSEFRAME).equals(""))
                    frame = Float.parseFloat(appSettingsManager.getApiString(AppSettingsManager.TIMELAPSEFRAME).replace(",", "."));
                else
                    appSettingsManager.setApiString(AppSettingsManager.TIMELAPSEFRAME, ""+frame);
                recorder.setCaptureRate(frame);
                break;
        }
        return recorder;
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (appSettingsManager.videoHDR.isSupported())
            if(appSettingsManager.videoHDR.equals("on"))
                cameraUiWrapper.getParameterHandler().VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (appSettingsManager.videoHDR.isSupported())
            cameraUiWrapper.getParameterHandler().VideoHDR.SetValue("off", true);
        super.DestroyModule();
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.videoProfile.get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            if(cameraUiWrapper.getParameterHandler().HTCVideoMode != null) {
                loadHtcHighspeed();
            }
            else if (((CameraHolder)cameraUiWrapper.getCameraHolder()).DeviceFrameWork == CameraHolder.Frameworks.MTK)
            {
                loadMtkHighspeed();
            }
            else
            {
                loadDefaultHighspeed();
            }
        }
        else
        {
            if (appSettingsManager.previewFps.isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : appSettingsManager.previewFps.getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            if (appSettingsManager.previewFpsRange.isSupported()) {

                if (currentProfile.videoFrameRate <= 30) {
                    cameraUiWrapper.getParameterHandler().PreviewFpsRange.SetValue(String.valueOf(currentProfile.videoFrameRate * 1000) + "," + String.valueOf(currentProfile.videoFrameRate * 1000),true);
                }
            }
            if (currentProfile.videoFrameHeight >=2160)
            {
                disable_mce_dis_vs_denoise();
                if (!appSettingsManager.IsCamera2FullSupported() && StringUtils.arrayContainsString(appSettingsManager.previewFormat.getValues(), "nv12-venus"))
                    cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("nv12-venus",true);
            }
            else
                cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("yuv420sp", true);

            if (appSettingsManager.videoHFR.isSupported())
            {
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(appSettingsManager.getResString(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.stopPreview();
        if (appSettingsManager.previewSize.isSupported())
            cameraUiWrapper.getParameterHandler().PreviewSize.SetValue(size,false);
        //video size applies the parameters to the camera
        if (appSettingsManager.videoSize.isSupported())
            cameraUiWrapper.getParameterHandler().VideoSize.SetValue(size, true);

        cameraUiWrapper.startPreview();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "Load default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!appSettingsManager.IsCamera2FullSupported() && StringUtils.arrayContainsString(appSettingsManager.previewFormat.getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("nv12-venus", false);


        cameraUiWrapper.stopPreview();
        //set the profile defined frames per seconds
        if (appSettingsManager.videoHFR.isSupported()) {
            cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        cameraUiWrapper.startPreview();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        if (cameraUiWrapper.getParameterHandler().MemoryColorEnhancement != null && appSettingsManager.memoryColorEnhancement.isSupported())
            cameraUiWrapper.getParameterHandler().MemoryColorEnhancement.SetValue("disable", false);
        if (cameraUiWrapper.getParameterHandler().DigitalImageStabilization != null && appSettingsManager.digitalImageStabilisationMode.isSupported())
            cameraUiWrapper.getParameterHandler().DigitalImageStabilization.SetValue("disable", false);
        if (cameraUiWrapper.getParameterHandler().VideoStabilization != null && appSettingsManager.videoStabilisation.isSupported())
            cameraUiWrapper.getParameterHandler().VideoStabilization.SetValue("false", false);
        if (cameraUiWrapper.getParameterHandler().Denoise != null && cameraUiWrapper.getParameterHandler().Denoise.IsSupported())
            cameraUiWrapper.getParameterHandler().Denoise.SetValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        if(cameraUiWrapper.getParameterHandler().PreviewFPS.GetValues().toString().contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.IsSupported()) {
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        if (currentProfile.videoFrameHeight == 1080 && currentProfile.Mode == VideoMode.Highspeed)
        {
            cameraUiWrapper.getParameterHandler().HTCVideoMode.SetValue("2",true);
            cameraUiWrapper.getParameterHandler().HTCVideoModeHSR.SetValue("60",true);
            cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue("60", true);
        }
        else if (currentProfile.videoFrameHeight == 720 && currentProfile.Mode == VideoMode.Highspeed)
        {
            if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
            {
                cameraUiWrapper.getParameterHandler().HTCVideoMode.SetValue("2",false);
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue("off", false);
            }
            else {
                cameraUiWrapper.getParameterHandler().HTCVideoMode.SetValue("1",true);
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue("120", true);}
        }
    }


    @Override
    public void internalFireOnWorkDone(File file) {

    }
}
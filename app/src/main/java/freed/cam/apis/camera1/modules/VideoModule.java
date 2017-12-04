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

import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;


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
                if(!TextUtils.isEmpty(AppSettingsManager.getInstance().getApiString(AppSettingsManager.TIMELAPSEFRAME)))
                    frame = Float.parseFloat(AppSettingsManager.getInstance().getApiString(AppSettingsManager.TIMELAPSEFRAME).replace(",", "."));
                else
                    AppSettingsManager.getInstance().setApiString(AppSettingsManager.TIMELAPSEFRAME, ""+frame);
                recorder.setCaptureRate(frame);
                break;
        }
        return recorder;
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (AppSettingsManager.getInstance().videoHDR.isSupported())
            if(AppSettingsManager.getInstance().videoHDR.equals("on"))
                cameraUiWrapper.getParameterHandler().VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (AppSettingsManager.getInstance().videoHDR.isSupported())
            cameraUiWrapper.getParameterHandler().VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(AppSettingsManager.getInstance().videoProfile.get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            Log.d(TAG, "prepareHighspeed");
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
            if (AppSettingsManager.getInstance().previewFps.isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : AppSettingsManager.getInstance().previewFps.getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            if (AppSettingsManager.getInstance().previewFpsRange.isSupported()) {

                if (currentProfile.videoFrameRate <= 30) {
                    cameraUiWrapper.getParameterHandler().PreviewFpsRange.SetValue(String.valueOf(currentProfile.videoFrameRate * 1000) + "," + String.valueOf(currentProfile.videoFrameRate * 1000),true);
                }
            }
            if (currentProfile.videoFrameHeight >=2160)
            {
                Log.d(TAG,"prepare 4k video");
                disable_mce_dis_vs_denoise();
                if (!AppSettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(AppSettingsManager.getInstance().previewFormat.getValues(), "nv12-venus")) {
                    Log.d(TAG,"Set Preview format to nv12-venus");
                    cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("nv12-venus", true);
                    Log.d(TAG,"Set Preview format to nv12-venus done");
                }
            }
            else {
                Log.d(TAG,"Set Preview format to yuv420sp");
                cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("yuv420sp", true);
                Log.d(TAG,"Set Preview format to yuv420sp done");
            }

            if (AppSettingsManager.getInstance().videoHFR.isSupported())
            {
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(AppSettingsManager.getInstance().getResString(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.stopPreview();
        if (AppSettingsManager.getInstance().previewSize.isSupported()) {
            Log.d(TAG,"Set previewSize to:" + size);
            cameraUiWrapper.getParameterHandler().PreviewSize.SetValue(size, true);
            Log.d(TAG,"Set previewSize done");
        }
        //video size applies the parameters to the camera
        if (AppSettingsManager.getInstance().videoSize.isSupported()) {
            Log.d(TAG,"Set videoSize to:" + size);
            cameraUiWrapper.getParameterHandler().VideoSize.SetValue(size, true);
            Log.d(TAG,"Set videoSize done");
        }

        cameraUiWrapper.startPreview();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "prepare default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!AppSettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(AppSettingsManager.getInstance().previewFormat.getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("nv12-venus", false);


        cameraUiWrapper.stopPreview();
        //set the profile defined frames per seconds
        if (AppSettingsManager.getInstance().videoHFR.isSupported()) {
            cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        cameraUiWrapper.startPreview();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        Log.d(TAG, "disable_mce");
        if (cameraUiWrapper.getParameterHandler().MemoryColorEnhancement != null && AppSettingsManager.getInstance().memoryColorEnhancement.isSupported())
            cameraUiWrapper.getParameterHandler().MemoryColorEnhancement.SetValue("disable", false);
        Log.d(TAG, "disable_dis");
        if (cameraUiWrapper.getParameterHandler().DigitalImageStabilization != null && AppSettingsManager.getInstance().digitalImageStabilisationMode.isSupported())
            cameraUiWrapper.getParameterHandler().DigitalImageStabilization.SetValue("disable", false);
        Log.d(TAG, "disable_vs");
        if (cameraUiWrapper.getParameterHandler().VideoStabilization != null && AppSettingsManager.getInstance().videoStabilisation.isSupported())
            cameraUiWrapper.getParameterHandler().VideoStabilization.SetValue("false", false);
        Log.d(TAG, "disable_denoise");
        if (cameraUiWrapper.getParameterHandler().Denoise != null && cameraUiWrapper.getParameterHandler().Denoise.IsSupported())
            cameraUiWrapper.getParameterHandler().Denoise.SetValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        Log.d(TAG, "prepare mtk highspeed");
        if(cameraUiWrapper.getParameterHandler().PreviewFPS.getStringValues().toString().contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo != null && cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.IsSupported()) {
                cameraUiWrapper.getParameterHandler().VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().PreviewFPS.SetValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        Log.d(TAG, "prepare HTC Highpseed");
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
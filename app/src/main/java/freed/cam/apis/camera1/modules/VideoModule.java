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
import freed.settings.Settings;
import freed.settings.SettingsManager;
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
        if (SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_))){
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
                if(!TextUtils.isEmpty(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME)))
                    frame = Float.parseFloat(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME).replace(",", "."));
                else
                    SettingsManager.getInstance().setApiString(SettingsManager.TIMELAPSEFRAME, ""+frame);
                recorder.setCaptureRate(frame);
                break;
        }
        return recorder;
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (SettingsManager.get(Settings.VideoHDR).isSupported())
            if(SettingsManager.get(Settings.VideoHDR).get().equals("on"))
                cameraUiWrapper.getParameterHandler().get(Settings.VideoHDR).SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (SettingsManager.get(Settings.VideoHDR).isSupported())
            cameraUiWrapper.getParameterHandler().get(Settings.VideoHDR).SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(Settings.VideoProfiles);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(Settings.VideoProfiles).get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            Log.d(TAG, "prepareHighspeed");
            if(cameraUiWrapper.getParameterHandler().get(Settings.HTCVideoMode) != null) {
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
            if (SettingsManager.get(Settings.PreviewFPS).isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : SettingsManager.get(Settings.PreviewFPS).getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().get(Settings.PreviewFPS).SetValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            if (SettingsManager.getInstance().get(Settings.PreviewFpsRange).isSupported()) {

                if (currentProfile.videoFrameRate <= 30) {
                    cameraUiWrapper.getParameterHandler().get(Settings.PreviewFpsRange).SetValue(String.valueOf(currentProfile.videoFrameRate * 1000) + "," + String.valueOf(currentProfile.videoFrameRate * 1000),true);
                }
            }
            if (currentProfile.videoFrameHeight >=2160)
            {
                Log.d(TAG,"prepare 4k video");
                disable_mce_dis_vs_denoise();
                if (!SettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(SettingsManager.get(Settings.PreviewFormat).getValues(), "nv12-venus")) {
                    Log.d(TAG,"Set Preview format to nv12-venus");
                    cameraUiWrapper.getParameterHandler().get(Settings.PreviewFormat).SetValue("nv12-venus", true);
                    Log.d(TAG,"Set Preview format to nv12-venus done");
                }
            }
            else {
                Log.d(TAG,"Set Preview format to yuv420sp");
                cameraUiWrapper.getParameterHandler().get(Settings.PreviewFormat).SetValue("yuv420sp", true);
                Log.d(TAG,"Set Preview format to yuv420sp done");
            }

            if (SettingsManager.get(Settings.VideoHighFramerate).isSupported())
            {
                cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue(SettingsManager.getInstance().getResString(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.stopPreview();
        if (SettingsManager.get(Settings.PreviewSize).isSupported()) {
            Log.d(TAG,"Set previewSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(Settings.PreviewSize).SetValue(size, true);
            Log.d(TAG,"Set previewSize done");
        }
        //video size applies the parameters to the camera
        if (SettingsManager.get(Settings.VideoSize).isSupported()) {
            Log.d(TAG,"Set videoSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(Settings.VideoSize).SetValue(size, true);
            Log.d(TAG,"Set videoSize done");
        }

        cameraUiWrapper.startPreview();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "prepare default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!SettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(SettingsManager.get(Settings.PreviewFormat).getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().get(Settings.PreviewFormat).SetValue("nv12-venus", false);


        cameraUiWrapper.stopPreview();
        //set the profile defined frames per seconds
        if (SettingsManager.getInstance().get(Settings.VideoHighFramerate).isSupported()) {
            cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        cameraUiWrapper.startPreview();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        Log.d(TAG, "disable_mce");
        if (cameraUiWrapper.getParameterHandler().get(Settings.MemoryColorEnhancement) != null && SettingsManager.get(Settings.MemoryColorEnhancement).isSupported())
            cameraUiWrapper.getParameterHandler().get(Settings.MemoryColorEnhancement).SetValue("disable", false);
        Log.d(TAG, "disable_dis");
        if (cameraUiWrapper.getParameterHandler().get(Settings.DigitalImageStabilization) != null && SettingsManager.get(Settings.DigitalImageStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(Settings.DigitalImageStabilization).SetValue("disable", false);
        Log.d(TAG, "disable_vs");
        if (cameraUiWrapper.getParameterHandler().get(Settings.VideoStabilization) != null && SettingsManager.get(Settings.VideoStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(Settings.VideoStabilization).SetValue("false", false);
        Log.d(TAG, "disable_denoise");
        if (cameraUiWrapper.getParameterHandler().get(Settings.Denoise) != null && cameraUiWrapper.getParameterHandler().get(Settings.Denoise).IsSupported())
            cameraUiWrapper.getParameterHandler().get(Settings.Denoise).SetValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        Log.d(TAG, "prepare mtk highspeed");
        if(cameraUiWrapper.getParameterHandler().get(Settings.PreviewFPS).getStringValues().toString().contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().get(Settings.PreviewFPS).SetValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate) != null && cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).IsSupported()) {
                cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().get(Settings.PreviewFPS).SetValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        Log.d(TAG, "prepare HTC Highpseed");
        if (currentProfile.videoFrameHeight == 1080 && currentProfile.Mode == VideoMode.Highspeed)
        {
            cameraUiWrapper.getParameterHandler().get(Settings.HTCVideoMode).SetValue("2",true);
            cameraUiWrapper.getParameterHandler().get(Settings.HTCVideoModeHSR).SetValue("60",true);
            cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue("60", true);
        }
        else if (currentProfile.videoFrameHeight == 720 && currentProfile.Mode == VideoMode.Highspeed)
        {
            if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
            {
                cameraUiWrapper.getParameterHandler().get(Settings.HTCVideoMode).SetValue("2",false);
                cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue("off", false);
            }
            else {
                cameraUiWrapper.getParameterHandler().get(Settings.HTCVideoMode).SetValue("1",true);
                cameraUiWrapper.getParameterHandler().get(Settings.VideoHighFramerate).SetValue("120", true);}
        }
    }


    @Override
    public void internalFireOnWorkDone(File file) {

    }
}
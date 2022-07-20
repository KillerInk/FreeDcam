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
import android.media.MediaRecorder.VideoSource;
import android.os.Handler;

import com.troop.freedcam.R;

import java.util.Arrays;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.file.holder.BaseHolder;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
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

    public VideoModule(Camera1 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }



    protected void initRecorder()
    {
        recorder = new VideoRecorder(cameraUiWrapper,new MediaRecorder());
        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (currentProfile == null)
        {
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_PROFILES);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VIDEO_PROFILES).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);
        recorder.setVideoSource(VideoSource.CAMERA);
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (settingsManager.get(SettingKeys.VIDEO_HDR).isSupported())
            if(settingsManager.get(SettingKeys.VIDEO_HDR).get().equals("on"))
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HDR).setStringValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (settingsManager.get(SettingKeys.VIDEO_HDR).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HDR).setStringValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_PROFILES);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VIDEO_PROFILES).get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            Log.d(TAG, "prepareHighspeed");
            if(cameraUiWrapper.getParameterHandler().get(SettingKeys.HTC_VIDEO_MODE) != null) {
                loadHtcHighspeed();
            }
            else if (settingsManager.getFrameWork() == Frameworks.MTK)
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
            if (settingsManager.get(SettingKeys.PREVIEW_FPS).isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : settingsManager.get(SettingKeys.PREVIEW_FPS).getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FPS).setStringValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            try {
                if (settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).isSupported()) {

                    if (currentProfile.videoFrameRate <= 30) {
                        cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FPS_RANGE).setStringValue(currentProfile.videoFrameRate * 1000 + "," + currentProfile.videoFrameRate * 1000,true);
                    }
                }
            }
            catch (NullPointerException ex)
            {
                Log.d(TAG, "Preview Fps throws NullPointer");
                Log.WriteEx(ex);
            }

            if (currentProfile.videoFrameHeight >=2160)
            {
                Log.d(TAG,"prepare 4k video");
                disable_mce_dis_vs_denoise();
                if (!settingsManager.hasCamera2Features() && StringUtils.arrayContainsString(settingsManager.get(SettingKeys.PREVIEW_FORMAT).getValues(), "nv12-venus")) {
                    Log.d(TAG,"Set Preview format to nv12-venus");
                    cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FORMAT).setStringValue("nv12-venus", true);
                    Log.d(TAG,"Set Preview format to nv12-venus done");
                }
            }
            else {
                Log.d(TAG,"Set Preview format to yuv420sp");
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FORMAT).setStringValue("yuv420sp", true);
                Log.d(TAG,"Set Preview format to yuv420sp done");
            }

            if (settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).isSupported())
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue(FreedApplication.getStringFromRessources(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        CameraThreadHandler.stopPreviewAsync();
        if (settingsManager.get(SettingKeys.PREVIEW_SIZE).isSupported()) {
            Log.d(TAG,"Set previewSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_SIZE).setStringValue(size, false);
            Log.d(TAG,"Set previewSize done");
        }
        //video size applies the parameters to the camera
        if (settingsManager.get(SettingKeys.VIDEO_SIZE).isSupported()) {
            Log.d(TAG,"Set videoSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_SIZE).setStringValue(size, true);
            Log.d(TAG,"Set videoSize done");
        }

        CameraThreadHandler.startPreviewAsync();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "prepare default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!settingsManager.hasCamera2Features() && StringUtils.arrayContainsString(settingsManager.get(SettingKeys.PREVIEW_FORMAT).getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FORMAT).setStringValue("nv12-venus", false);


        CameraThreadHandler.stopPreviewAsync();
        //set the profile defined frames per seconds
        if (settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).isSupported()) {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        CameraThreadHandler.startPreviewAsync();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        Log.d(TAG, "disable_mce");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.MEMORY_COLOR_ENHANCEMENT) != null && settingsManager.get(SettingKeys.MEMORY_COLOR_ENHANCEMENT).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.MEMORY_COLOR_ENHANCEMENT).setStringValue("disable", false);
        Log.d(TAG, "disable_dis");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.DIGITAL_IMAGE_STABILIZATION) != null && settingsManager.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.DIGITAL_IMAGE_STABILIZATION).setStringValue("disable", false);
        Log.d(TAG, "disable_vs");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_STABILIZATION) != null && settingsManager.get(SettingKeys.VIDEO_STABILIZATION).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_STABILIZATION).setStringValue("false", false);
        Log.d(TAG, "disable_denoise");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.DENOISE) != null
                && cameraUiWrapper.getParameterHandler().get(SettingKeys.DENOISE).getViewState() == AbstractParameter.ViewState.Visible)
            cameraUiWrapper.getParameterHandler().get(SettingKeys.DENOISE).setStringValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        Log.d(TAG, "prepare mtk highspeed");
        if(Arrays.toString(cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FPS).getStringValues()).contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FPS).setStringValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE) != null
                    && cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).getViewState() == AbstractParameter.ViewState.Visible) {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_FPS).setStringValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        Log.d(TAG, "prepare HTC Highpseed");
        if (currentProfile.videoFrameHeight == 1080 && currentProfile.Mode == VideoMode.Highspeed)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTC_VIDEO_MODE).setStringValue("2",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTC_VIDEO_MODE_HSR).setStringValue("60",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue("60", true);
        }
        else if (currentProfile.videoFrameHeight == 720 && currentProfile.Mode == VideoMode.Highspeed)
        {
            if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTC_VIDEO_MODE).setStringValue("2",false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue("off", false);
            }
            else {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTC_VIDEO_MODE).setStringValue("1",true);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VIDEO_HIGH_FRAMERATE).setStringValue("120", true);}
        }
    }


    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }
}
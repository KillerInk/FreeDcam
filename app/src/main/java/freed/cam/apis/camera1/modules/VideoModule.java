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
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VideoProfiles).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);
        recorder.setVideoSource(VideoSource.CAMERA);
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (settingsManager.get(SettingKeys.VideoHDR).isSupported())
            if(settingsManager.get(SettingKeys.VideoHDR).get().equals("on"))
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR).setStringValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (settingsManager.get(SettingKeys.VideoHDR).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR).setStringValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VideoProfiles).get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            Log.d(TAG, "prepareHighspeed");
            if(cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode) != null) {
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
            if (settingsManager.get(SettingKeys.PreviewFPS).isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : settingsManager.get(SettingKeys.PreviewFPS).getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).setStringValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            try {
                if (settingsManager.get(SettingKeys.PreviewFpsRange).isSupported()) {

                    if (currentProfile.videoFrameRate <= 30) {
                        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFpsRange).setStringValue(String.valueOf(currentProfile.videoFrameRate * 1000) + "," + String.valueOf(currentProfile.videoFrameRate * 1000),true);
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
                if (!settingsManager.hasCamera2Features() && StringUtils.arrayContainsString(settingsManager.get(SettingKeys.PreviewFormat).getValues(), "nv12-venus")) {
                    Log.d(TAG,"Set Preview format to nv12-venus");
                    cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).setStringValue("nv12-venus", true);
                    Log.d(TAG,"Set Preview format to nv12-venus done");
                }
            }
            else {
                Log.d(TAG,"Set Preview format to yuv420sp");
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).setStringValue("yuv420sp", true);
                Log.d(TAG,"Set Preview format to yuv420sp done");
            }

            if (settingsManager.get(SettingKeys.VideoHighFramerate).isSupported())
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue(FreedApplication.getStringFromRessources(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        CameraThreadHandler.stopPreviewAsync();
        if (settingsManager.get(SettingKeys.PreviewSize).isSupported()) {
            Log.d(TAG,"Set previewSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).setStringValue(size, false);
            Log.d(TAG,"Set previewSize done");
        }
        //video size applies the parameters to the camera
        if (settingsManager.get(SettingKeys.VideoSize).isSupported()) {
            Log.d(TAG,"Set videoSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoSize).setStringValue(size, true);
            Log.d(TAG,"Set videoSize done");
        }

        CameraThreadHandler.startPreviewAsync();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "prepare default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!settingsManager.hasCamera2Features() && StringUtils.arrayContainsString(settingsManager.get(SettingKeys.PreviewFormat).getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).setStringValue("nv12-venus", false);


        CameraThreadHandler.stopPreviewAsync();
        //set the profile defined frames per seconds
        if (settingsManager.get(SettingKeys.VideoHighFramerate).isSupported()) {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        CameraThreadHandler.startPreviewAsync();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        Log.d(TAG, "disable_mce");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement) != null && settingsManager.get(SettingKeys.MemoryColorEnhancement).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement).setStringValue("disable", false);
        Log.d(TAG, "disable_dis");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization) != null && settingsManager.get(SettingKeys.DigitalImageStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization).setStringValue("disable", false);
        Log.d(TAG, "disable_vs");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoStabilization) != null && settingsManager.get(SettingKeys.VideoStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoStabilization).setStringValue("false", false);
        Log.d(TAG, "disable_denoise");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise) != null
                && cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).getViewState() == AbstractParameter.ViewState.Visible)
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).setStringValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        Log.d(TAG, "prepare mtk highspeed");
        if(Arrays.toString(cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).getStringValues()).contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).setStringValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate) != null
                    && cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).getViewState() == AbstractParameter.ViewState.Visible) {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).setStringValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        Log.d(TAG, "prepare HTC Highpseed");
        if (currentProfile.videoFrameHeight == 1080 && currentProfile.Mode == VideoMode.Highspeed)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).setStringValue("2",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoModeHSR).setStringValue("60",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue("60", true);
        }
        else if (currentProfile.videoFrameHeight == 720 && currentProfile.Mode == VideoMode.Highspeed)
        {
            if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).setStringValue("2",false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue("off", false);
            }
            else {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).setStringValue("1",true);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).setStringValue("120", true);}
        }
    }


    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }
}
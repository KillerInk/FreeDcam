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

import java.io.File;
import java.util.Arrays;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
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



    protected void initRecorder()
    {
        recorder = new VideoRecorder(cameraUiWrapper,new MediaRecorder());
        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (currentProfile == null)
        {
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);
        recorder.setVideoSource(VideoSource.CAMERA);
    }



    @Override
    public void InitModule()
    {
        super.InitModule();
        if (SettingsManager.get(SettingKeys.VideoHDR).isSupported())
            if(SettingsManager.get(SettingKeys.VideoHDR).get().equals("on"))
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR).SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void DestroyModule() {
        if (isWorking)
            stopRecording();
        if (SettingsManager.get(SettingKeys.VideoHDR).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR).SetValue("off", true);
        super.DestroyModule();
    }

    private void loadProfileSpecificParameters()
    {

        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        Log.d(TAG,"LoadProfile: " + currentProfile.ProfileName + " Size: " + currentProfile.videoFrameWidth+"/"+currentProfile.videoFrameHeight);
        if (currentProfile.Mode == VideoMode.Highspeed)
        {
            Log.d(TAG, "prepareHighspeed");
            if(cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode) != null) {
                loadHtcHighspeed();
            }
            else if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
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
            if (SettingsManager.get(SettingKeys.PreviewFPS).isSupported())
            {
                if (currentProfile.videoFrameRate <=24) {
                    for (String fpz : SettingsManager.get(SettingKeys.PreviewFPS).getValues()) {
                        if (Integer.parseInt(fpz) == currentProfile.videoFrameRate) {
                            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).SetValue(currentProfile.videoFrameRate + "", false);
                        }
                    }
                }
            }

            try {
                if (SettingsManager.get(SettingKeys.PreviewFpsRange).isSupported()) {

                    if (currentProfile.videoFrameRate <= 30) {
                        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFpsRange).SetValue(String.valueOf(currentProfile.videoFrameRate * 1000) + "," + String.valueOf(currentProfile.videoFrameRate * 1000),true);
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
                if (!SettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(SettingsManager.get(SettingKeys.PreviewFormat).getValues(), "nv12-venus")) {
                    Log.d(TAG,"Set Preview format to nv12-venus");
                    cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("nv12-venus", true);
                    Log.d(TAG,"Set Preview format to nv12-venus done");
                }
            }
            else {
                Log.d(TAG,"Set Preview format to yuv420sp");
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("yuv420sp", true);
                Log.d(TAG,"Set Preview format to yuv420sp done");
            }

            if (SettingsManager.get(SettingKeys.VideoHighFramerate).isSupported())
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue(SettingsManager.getInstance().getResString(R.string.off_), true);
            }
        }


        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.stopPreviewAsync();
        if (SettingsManager.get(SettingKeys.PreviewSize).isSupported()) {
            Log.d(TAG,"Set previewSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).SetValue(size, true);
            Log.d(TAG,"Set previewSize done");
        }
        //video size applies the parameters to the camera
        if (SettingsManager.get(SettingKeys.VideoSize).isSupported()) {
            Log.d(TAG,"Set videoSize to:" + size);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoSize).SetValue(size, true);
            Log.d(TAG,"Set videoSize done");
        }

        cameraUiWrapper.startPreviewAsync();
    }

    private void loadDefaultHighspeed() {
        Log.d(TAG, "prepare default higspeed");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        disable_mce_dis_vs_denoise();
        //full camera2 devices dont use hardware preview format so set it only for legacy devices
        if (!SettingsManager.getInstance().hasCamera2Features() && StringUtils.arrayContainsString(SettingsManager.get(SettingKeys.PreviewFormat).getValues(), "nv12-venus"))
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("nv12-venus", false);


        cameraUiWrapper.stopPreviewAsync();
        //set the profile defined frames per seconds
        if (SettingsManager.getInstance().get(SettingKeys.VideoHighFramerate).isSupported()) {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue(currentProfile.videoFrameRate + "", false);
        }
        Log.d(TAG, "Load default highspeed done");
        cameraUiWrapper.startPreviewAsync();
    }

    private void disable_mce_dis_vs_denoise()
    {
        Log.d(TAG, "disable_mce_dis_vs_denoise");
        //turn off all blocking/postprocessing parameters wich avoid highframes
        Log.d(TAG, "disable_mce");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement) != null && SettingsManager.get(SettingKeys.MemoryColorEnhancement).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement).SetValue("disable", false);
        Log.d(TAG, "disable_dis");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization) != null && SettingsManager.get(SettingKeys.DigitalImageStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization).SetValue("disable", false);
        Log.d(TAG, "disable_vs");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoStabilization) != null && SettingsManager.get(SettingKeys.VideoStabilization).isSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoStabilization).SetValue("false", false);
        Log.d(TAG, "disable_denoise");
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise) != null
                && cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).getViewState() == AbstractParameter.ViewState.Visible)
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).SetValue("denoise-off", false);
        Log.d(TAG, "disable_mce_dis_vs_denoise done");
    }

    private void loadMtkHighspeed() {
        Log.d(TAG, "prepare mtk highspeed");
        if(Arrays.toString(cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).getStringValues()).contains(currentProfile.videoFrameRate+""))
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).SetValue(currentProfile.videoFrameRate+"",false);

            if (cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate) != null
                    && cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).getViewState() == AbstractParameter.ViewState.Visible) {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue(currentProfile.videoFrameRate + "", false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFPS).SetValue(currentProfile.videoFrameRate+"",true);
            }

        }
    }

    private void loadHtcHighspeed() {
        Log.d(TAG, "prepare HTC Highpseed");
        if (currentProfile.videoFrameHeight == 1080 && currentProfile.Mode == VideoMode.Highspeed)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).SetValue("2",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoModeHSR).SetValue("60",true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue("60", true);
        }
        else if (currentProfile.videoFrameHeight == 720 && currentProfile.Mode == VideoMode.Highspeed)
        {
            if (currentProfile.videoFrameRate < 120 && currentProfile.videoFrameRate >30 )
            {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).SetValue("2",false);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue("off", false);
            }
            else {
                cameraUiWrapper.getParameterHandler().get(SettingKeys.HTCVideoMode).SetValue("1",true);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate).SetValue("120", true);}
        }
    }


    @Override
    public void internalFireOnWorkDone(File file) {

    }
}
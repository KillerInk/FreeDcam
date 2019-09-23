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
import android.media.MediaRecorder.VideoSource;
import android.os.Handler;

import com.lge.media.MediaRecorderExRef;
import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }

    protected void initRecorder()
    {
        recorder = new VideoRecorder(cameraUiWrapper ,new MediaRecorderExRef().getMediaRecorder());
        if (currentProfile == null)
        {
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);

        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_))){
            Location location = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
            if (location != null)
                recorder.setLocation(location);
        }
        recorder.setVideoSource(VideoSource.CAMERA);

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
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        if (((ParametersHandler)cameraUiWrapper.getParameterHandler()).getParameters().get("preview-fps-range") != null) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters().set("preview-fps-range", "30000,30000");
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters());
        }
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("2160p"))
        {
            ParameterInterface mce = cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement);
            if(mce != null && mce.getViewState() == AbstractParameter.ViewState.Visible)
                mce.SetValue(SettingsManager.getInstance().getResString(R.string.disable_),false);
            ParameterInterface dis = cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization);
            if (dis!= null && dis.getViewState() == AbstractParameter.ViewState.Visible)
                dis.SetValue(SettingsManager.getInstance().getResString(R.string.disable_), false);
            ParameterInterface denoise = cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise);
            if (denoise != null && denoise.getViewState() == AbstractParameter.ViewState.Visible)
                denoise.SetValue("denoise-off", false);
            if(!SettingsManager.getInstance().hasCamera2Features())
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("nv12-venus",false);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                ParameterInterface hfr = cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate);
                if (hfr != null && hfr.getViewState() == AbstractParameter.ViewState.Visible)
                {
                    hfr.SetValue(currentProfile.videoFrameRate+"", false);
                }
            }
        }
        else
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("yuv420sp", false);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).SetValue(size,false);
        cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoSize).SetValue(size,true);
        /*cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();*/
    }

    @Override
    public void internalFireOnWorkDone(File file) {

    }
}

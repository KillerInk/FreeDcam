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

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.file.holder.BaseHolder;
import freed.settings.SettingKeys;
import freed.utils.VideoMediaProfile;
import freed.utils.VideoMediaProfile.VideoMode;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private VideoMediaProfile currentProfile;

    private final String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(Camera1 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
    }

    protected void initRecorder()
    {
        recorder = new VideoRecorder(cameraUiWrapper ,new MediaRecorderExRef().getMediaRecorder());
        if (currentProfile == null)
        {
            VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoProfiles);
            currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VideoProfiles).get());
        }
        recorder.setCurrentVideoProfile(currentProfile);

        recorder.setCamera(((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera());
        if (settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_))){
            Location location = locationManager.getCurrentLocation();
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
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(settingsManager.get(SettingKeys.VideoProfiles).get());
        if (((ParametersHandler)cameraUiWrapper.getParameterHandler()).getParameters().get("preview-fps-range") != null) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters().set("preview-fps-range", "30000,30000");
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(((ParametersHandler) cameraUiWrapper.getParameterHandler()).getParameters());
        }
        if (currentProfile.Mode == VideoMode.Highspeed || currentProfile.ProfileName.contains("2160p"))
        {
            ParameterInterface mce = cameraUiWrapper.getParameterHandler().get(SettingKeys.MemoryColorEnhancement);
            if(mce != null && mce.getViewState() == AbstractParameter.ViewState.Visible)
                mce.setStringValue(FreedApplication.getStringFromRessources(R.string.disable_),false);
            ParameterInterface dis = cameraUiWrapper.getParameterHandler().get(SettingKeys.DigitalImageStabilization);
            if (dis!= null && dis.getViewState() == AbstractParameter.ViewState.Visible)
                dis.setStringValue(FreedApplication.getStringFromRessources(R.string.disable_), false);
            ParameterInterface denoise = cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise);
            if (denoise != null && denoise.getViewState() == AbstractParameter.ViewState.Visible)
                denoise.setStringValue("denoise-off", false);
            if(!settingsManager.hasCamera2Features())
                cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).setStringValue("nv12-venus",false);
            if (currentProfile.Mode == VideoMode.Highspeed)
            {
                ParameterInterface hfr = cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHighFramerate);
                if (hfr != null && hfr.getViewState() == AbstractParameter.ViewState.Visible)
                {
                    hfr.setStringValue(currentProfile.videoFrameRate+"", false);
                }
            }
        }
        else
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).setStringValue("yuv420sp", false);
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).setStringValue(size,false);
        cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoSize).setStringValue(size,true);
        /*cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();*/
    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }
}

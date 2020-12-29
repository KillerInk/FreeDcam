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

package freed.cam.apis.camera1.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoProfilesParameter extends AbstractParameter
{
    private final String TAG = VideoProfilesParameter.class.getSimpleName();
    protected HashMap<String, VideoMediaProfile> supportedProfiles;
    protected String profile;
    protected boolean isSupported;


    public VideoProfilesParameter(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper,SettingKeys.VideoProfiles);
        isSupported =true;
        try {
            supportedProfiles = SettingsManager.getInstance().getMediaProfiles();
        }
        catch (NullPointerException ex)
        {
            Log.e(TAG, "Failed to load MediaProfiles");
        }

        profile = SettingsManager.get(SettingKeys.VideoProfiles).get();
        if (profile == null && supportedProfiles.size() > 0)
        {
            List<String> keys = new ArrayList<>(supportedProfiles.keySet());
            profile = keys.get(0);
            SettingsManager.get(SettingKeys.VideoProfiles).set(profile);
        }
        else if (supportedProfiles == null || supportedProfiles.size() == 0)
            fireViewStateChanged(ViewState.Hidden);

    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        profile = valueToSet;
        if (cameraUiWrapper.getModuleHandler().getCurrentModule() != null
                && cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video)))
            cameraUiWrapper.getModuleHandler().getCurrentModule().InitModule();
    }

    @Override
    public String GetStringValue()
    {
        if ((profile == null || TextUtils.isEmpty(profile)) && supportedProfiles != null)
        {
            List<String> keys = new ArrayList<>(supportedProfiles.keySet());
            try {
                profile = keys.get(0);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                profile = "null";
                isSupported = false;
                setViewState(ViewState.Hidden);
            }
        }
        return profile;
    }

    @Override
    public String[] getStringValues()
    {
        List<String> keys = new ArrayList<>(supportedProfiles.keySet());
        Collections.sort(keys);
        return keys.toArray(new String[keys.size()]);
    }

    public VideoMediaProfile GetCameraProfile(String profile)
    {
        if (supportedProfiles == null)
            supportedProfiles = SettingsManager.getInstance().getMediaProfiles();
        if (profile == null || TextUtils.isEmpty(profile))
        {
            String[] t = supportedProfiles.keySet().toArray(new String[supportedProfiles.keySet().size()]);
            return supportedProfiles.get(t[0]);
        }
        return supportedProfiles.get(profile);
    }

}

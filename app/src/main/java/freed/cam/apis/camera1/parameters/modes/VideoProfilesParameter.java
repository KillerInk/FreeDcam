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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.Settings;
import freed.settings.SettingsManager;
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
        super(cameraUiWrapper);
        isSupported =true;
        supportedProfiles = SettingsManager.getInstance().getMediaProfiles();
        profile = SettingsManager.get(Settings.VideoProfiles).get();
        if (profile == null)
        {
            List<String> keys = new ArrayList<>(supportedProfiles.keySet());
            profile = keys.get(0);
            SettingsManager.get(Settings.VideoProfiles).set(profile);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet,setToCam);
        profile = valueToSet;
        if (cameraUiWrapper.getModuleHandler().getCurrentModule() != null
                && cameraUiWrapper.getModuleHandler().getCurrentModuleName().equals(cameraUiWrapper.getResString(R.string.module_video)))
            cameraUiWrapper.getModuleHandler().getCurrentModule().InitModule();
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String GetStringValue()
    {
        if ((profile == null || TextUtils.isEmpty(profile)) && supportedProfiles != null)
        {
            List<String> keys = new ArrayList<>(supportedProfiles.keySet());
            profile = keys.get(0);
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
        if (profile == null || TextUtils.isEmpty(profile))
        {
            String[] t = supportedProfiles.keySet().toArray(new String[supportedProfiles.keySet().size()]);
            return supportedProfiles.get(t[0]);
        }
        return supportedProfiles.get(profile);
    }

}

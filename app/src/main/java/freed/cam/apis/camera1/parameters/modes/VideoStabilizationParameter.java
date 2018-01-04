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

import android.hardware.Camera.Parameters;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;

/**
 * Created by Ar4eR on 10.12.15.
 */
public class VideoStabilizationParameter extends  BaseModeParameter {
    private final String[] vs_values = {cameraUiWrapper.getResString(R.string.true_), cameraUiWrapper.getResString(R.string.false_)};
    public VideoStabilizationParameter(Parameters parameters, CameraWrapperInterface parameterChanged)
    {
        super(parameters, parameterChanged, SettingKeys.VideoStabilization);
        if (parameters.get(cameraUiWrapper.getResString(R.string.video_stabilization_supported)).equals(cameraUiWrapper.getResString(R.string.true_)))
        {
            isSupported = true;
            key_value = cameraUiWrapper.getResString(R.string.video_stabilization);
        }
        else
            isSupported = false;

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] getStringValues() {
        return vs_values;
    }

    @Override
    public String GetStringValue()
    {
        String vs = parameters.get(cameraUiWrapper.getResString(R.string.video_stabilization));
        if (vs != null && !TextUtils.isEmpty(vs))
            return vs;
        else
            return "error";
    }

}
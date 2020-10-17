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

package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera.Parameters;
import android.text.TextUtils;

import com.troop.freedcam.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.settings.SettingKeys;

/**
 * Created by Ar4eR on 10.12.15.
 */
public class VideoStabilizationParameter extends  BaseModeParameter {
    private final String[] vs_values = {ContextApplication.getStringFromRessources(R.string.true_), ContextApplication.getStringFromRessources(R.string.false_)};
    public VideoStabilizationParameter(Parameters parameters, CameraControllerInterface parameterChanged)
    {
        super(parameters, parameterChanged, SettingKeys.VideoStabilization);
        if (parameters.get(ContextApplication.getStringFromRessources(R.string.video_stabilization_supported)) != null
                && parameters.get(ContextApplication.getStringFromRessources(R.string.video_stabilization_supported)).equals(ContextApplication.getStringFromRessources(R.string.true_)))
        {
            setViewState(ViewState.Visible);
            key_value = ContextApplication.getStringFromRessources(R.string.video_stabilization);
        }

    }

    @Override
    public String[] getStringValues() {
        return vs_values;
    }

    @Override
    public String GetStringValue()
    {
        String vs = parameters.get(ContextApplication.getStringFromRessources(R.string.video_stabilization));
        if (vs != null && !TextUtils.isEmpty(vs))
            return vs;
        else
            return "error";
    }

}
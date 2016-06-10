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

package com.freedcam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by troop on 27.04.2015.
 */
public class SceneModeParameter extends BaseModeParameter {
    public SceneModeParameter(Parameters parameters, I_CameraUiWrapper cameraHolder, String value, String values) {
        super(parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        super.SetValue(valueToSet, setToCam);
        //cameraHolder.StopPreview();
        //cameraHolder.StartPreview();
    }

    @Override
    public String[] GetValues()
    {
        List<String> Trimmed = new ArrayList<>(Arrays.asList(parameters.get(KEYS.SCENE_MODE_VALUES).split(",")));

        if(Trimmed.contains(KEYS.SCENE_MODE_VALUES_HDR)) {
            Trimmed.remove(KEYS.SCENE_MODE_VALUES_HDR);
            return Trimmed.toArray(new String[Trimmed.size()]);
        }

        return Trimmed.toArray(new String[Trimmed.size()]);



    }
}

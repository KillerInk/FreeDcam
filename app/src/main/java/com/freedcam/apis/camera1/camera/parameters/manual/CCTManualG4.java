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

package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualG4 extends BaseCCTManual {
    public CCTManualG4(Parameters parameters, String value, int max, int min, ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, parametersHandler, step, wbmode);
    }

    public CCTManualG4(Parameters parameters,ParametersHandler parametersHandler) {
        super(parameters, KEYS.LG_WB, KEYS.LG_WB_SUPPORTED_MAX, KEYS.LG_WB_SUPPORTED_MIN, parametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.set(key_value, "0");
    }
}

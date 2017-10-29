/*
 *
 *     Copyright (C) 2015 George Kiarie
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

package freed.cam.apis.camera1.parameters.manual.qcom;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;

/**
 * Created by troop on 12.04.2015.
 */
public class SkintoneManualPrameter extends BaseManualParameter {
    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public SkintoneManualPrameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        super(parameters, "", "", "", cameraUiWrapper,1);
        try
        {
            isSupported = true;
            if (isSupported)
            {
                stringvalues = createStringArray(-100,100,1);
            }
        }
        catch (Exception ex)
        {
            isSupported = false;

        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void setValue(int valueToSet) {
        cameraUiWrapper.getParameterHandler().SceneMode.SetValue("portrait", true);
        parameters.set("skinToneEnhancement",valueToSet + "");
        if (valueToSet == 0)
            cameraUiWrapper.getParameterHandler().SceneMode.SetValue("auto", true);
    }
}

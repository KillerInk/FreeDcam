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

package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.Logger;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    private final String TAG = BaseFocusManual.class.getSimpleName();
    protected String manualFocusModeString;
    private final int manualFocusType;

    /**
     * checks if the key_value maxvalue and minvalues are contained in the cameraparameters
     * and creates depending on it the stringarray
     * NOTE:if super fails the parameter is unsupported
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param step
     */
    public BaseFocusManual(Parameters parameters, String value, String maxValue, String MinValue, String manualFocusModeString, CameraWrapperInterface cameraUiWrapper, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, cameraUiWrapper, step);
        this.manualFocusModeString = manualFocusModeString;
        this.manualFocusType = manualFocusType;
    }

    /**
     * this allows to hardcode devices wich support manual focus but the parameters are messed up.
     * @param parameters
     * @param value
     * @param min
     * @param max
     * @param manualFocusModeString
     * @param cameraUiWrapper
     * @param step
     * @param manualFocusType
     */
    public BaseFocusManual(Parameters parameters, String value, int min, int max, String manualFocusModeString, CameraWrapperInterface cameraUiWrapper, float step, int manualFocusType) {
        super(parameters, value, "", "", cameraUiWrapper, step);
        isSupported = true;
        isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        stringvalues = createStringArray(min,max,step);
        this.manualFocusType = manualFocusType;
    }


    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.AUTO);
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(KEYS.AUTO, true);
            Logger.d(TAG, "Set Focus to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !cameraUiWrapper.GetParameterHandler().FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(manualFocusModeString, false);
            parameters.set(KEYS.KEY_MANUAL_FOCUS_TYPE, manualFocusType +"");

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
    }

}

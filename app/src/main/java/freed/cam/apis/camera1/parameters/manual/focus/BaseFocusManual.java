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

package freed.cam.apis.camera1.parameters.manual.focus;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    private final String TAG = BaseFocusManual.class.getSimpleName();
    protected String manualFocusModeString;
    private final int manualFocusType;
    AppSettingsManager.SettingMode settingMode;

    public BaseFocusManual(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AppSettingsManager.TypeSettingsMode settingMode)
    {
        super(parameters,cameraUiWrapper);
        this.settingMode = settingMode;
        manualFocusType = settingMode.getType();
        manualFocusModeString = settingMode.getMode();
        stringvalues = settingMode.getValues();
        key_value = settingMode.getKEY();
        isSupported =true;
        isVisible = isSupported;
    }


    public BaseFocusManual(Parameters parameters, String value, String maxValue, String MinValue, String manualFocusModeString, CameraWrapperInterface cameraUiWrapper, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, cameraUiWrapper, step);
        this.manualFocusModeString = manualFocusModeString;
        this.manualFocusType = manualFocusType;
    }


    /*public BaseFocusManual(Parameters parameters, String value, int min, int max, String manualFocusModeString, CameraWrapperInterface cameraUiWrapper, float step, int manualFocusType) {
        super(parameters, value, "", "", cameraUiWrapper, step);

        isSupported = true;
        isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        if (cameraUiWrapper.getAppSettingsManager().manualFocus.getValues().length == 0) {
            stringvalues = createStringArray(min, max, step);
            cameraUiWrapper.getAppSettingsManager().manualFocus.setValues(stringvalues);
        }
        else
            stringvalues = cameraUiWrapper.getAppSettingsManager().manualFocus.getValues();
        this.manualFocusType = manualFocusType;
    }*/


   /* @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(cameraUiWrapper.getResString(R.string.auto_));
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }*/

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            cameraUiWrapper.getParameterHandler().FocusMode.SetValue(cameraUiWrapper.getResString(R.string.auto_), true);
            Log.d(TAG, "Set Focus to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !cameraUiWrapper.getParameterHandler().FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                cameraUiWrapper.getParameterHandler().FocusMode.SetValue(manualFocusModeString, false);
            if (manualFocusType > -1)
                parameters.set(cameraUiWrapper.getResString(R.string.manual_focus_pos_type), manualFocusType +"");

            parameters.set(key_value, stringvalues[currentInt]);
            Log.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
    }

}

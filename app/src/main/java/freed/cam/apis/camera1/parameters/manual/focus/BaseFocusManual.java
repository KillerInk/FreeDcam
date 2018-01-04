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
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.TypedSettingMode;
import freed.utils.Log;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    private final String TAG = BaseFocusManual.class.getSimpleName();
    protected String manualFocusModeString;
    private final int manualFocusType;
    SettingKeys.Key settingMode;

    public BaseFocusManual(Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode)
    {
        super(parameters,cameraUiWrapper,settingMode);
        this.settingMode = settingMode;
        TypedSettingMode settingMode1 =  (TypedSettingMode) SettingsManager.get(key);
        manualFocusType = settingMode1.getType();
        Log.d(TAG,"mf type:" +manualFocusType);
        manualFocusModeString = settingMode1.getMode();
        Log.d(TAG,"mf focus mode:" +manualFocusModeString);
        stringvalues = settingMode1.getValues();
        key_value = settingMode1.getKEY();
        Log.d(TAG,"mf key:" +key_value);
        isSupported =true;
        isVisible = isSupported;
    }


    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(cameraUiWrapper.getResString(R.string.auto_), true);
            Log.d(TAG, "Set Focus to : auto");
        }
        else
        {
            if ((!TextUtils.isEmpty(manualFocusModeString) || manualFocusModeString == null)
                    && !cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).GetStringValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(manualFocusModeString, false);
            if (manualFocusType > -1)
                parameters.set(cameraUiWrapper.getResString(R.string.manual_focus_pos_type), manualFocusType +"");

            ((TypedSettingMode) SettingsManager.get(key)).set(stringvalues[currentInt]);
            parameters.set(key_value, stringvalues[currentInt]);
            Log.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
    }

}

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


package freed.cam.apis.camera1.parameters.manual.mtk;

import android.hardware.Camera.Parameters;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.settings.SettingKeys;
import freed.settings.mode.SettingMode;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 28.03.2016.
 */
public class FocusManualMTK extends BaseFocusManual {

    private final String TAG = FocusManualMTK.class.getSimpleName();
    public FocusManualMTK(Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key  settingMode) {
        super(parameters, cameraUiWrapper, settingMode);
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(cameraUiWrapper.getResString(R.string.auto_), true);
            ((SettingMode)SettingsManager.get(key)).set(cameraUiWrapper.getResString(R.string.auto_));
        }
        else
        {
            if ((!TextUtils.isEmpty(manualFocusModeString) || manualFocusModeString == null)
                    && !cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).GetStringValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(manualFocusModeString, false);

            if (currentInt > stringvalues.length -1)
                currentInt = stringvalues.length -1;

            parameters.set(key_value, stringvalues[currentInt]);
            Log.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            ((SettingMode)SettingsManager.get(key)).set(stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
    }
}

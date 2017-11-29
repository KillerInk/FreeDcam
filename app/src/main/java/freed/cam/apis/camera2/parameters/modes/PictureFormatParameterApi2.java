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

package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    private String format;

    public PictureFormatParameterApi2(CameraWrapperInterface cameraUiWrapper, AppSettingsManager.SettingMode settingMode, CaptureRequest.Key<Integer> parameterKey)
    {
        super(cameraUiWrapper,settingMode,parameterKey);
        format = settingMode.get();
    }

    @Override
    public boolean IsSupported() {
        return AppSettingsManager.getInstance().pictureFormat.isSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        format = valueToSet;
        if (setToCamera)
        {
            cameraUiWrapper.stopPreview();
            cameraUiWrapper.startPreview();
        }
    }

    @Override
    public String GetStringValue() {
        return format;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] getStringValues()
    {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }
}

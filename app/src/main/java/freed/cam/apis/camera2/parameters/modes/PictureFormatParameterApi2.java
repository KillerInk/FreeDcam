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
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{

    public PictureFormatParameterApi2(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey)
    {
        super(cameraUiWrapper,key,parameterKey);
        if (SettingsManager.get(SettingKeys.PictureFormat).isSupported()) {
            setViewState(ViewState.Visible);
            currentString = SettingsManager.get(SettingKeys.PictureFormat).get();
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        super.setValue(valueToSet,setToCamera);
        if (setToCamera)
        {
            cameraUiWrapper.restartPreviewAsync();
            /*cameraUiWrapper.stopPreviewAsync();
            cameraUiWrapper.startPreviewAsync();*/
        }
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] getStringValues()
    {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }

    @Override
    public String GetStringValue() {
        return SettingsManager.get(SettingKeys.PictureFormat).get();
    }
}

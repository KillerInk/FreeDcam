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
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import java.util.HashMap;
import java.util.Map;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.AppSettingsManager;
import freed.utils.StringUtils;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class BaseModeApi2 extends AbstractModeParameter
{
    private final String TAG = BaseModeApi2.class.getSimpleName();
    protected CameraWrapperInterface cameraUiWrapper;
    protected HashMap<String, Integer> parameterValues;
    protected AppSettingsManager.SettingMode settingMode;
    protected Key<Integer> parameterKey;
    boolean isSupported;

    public BaseModeApi2(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper =cameraUiWrapper;
    }

    public BaseModeApi2(CameraWrapperInterface cameraUiWrapper, AppSettingsManager.SettingMode settingMode, Key<Integer> parameterKey)
    {
        this(cameraUiWrapper);
        this.settingMode = settingMode;
        this.parameterKey = parameterKey;
        isSupported = settingMode.isSupported();
        if (isSupported)
            parameterValues = StringUtils.StringArrayToIntHashmap(settingMode.getValues());
        else settingMode = null;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        int toset = parameterValues.get(valueToSet);
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).captureSessionHandler.SetParameterRepeating(parameterKey, toset);
        onValueHasChanged(valueToSet);

    }

    @Override
    public String GetValue()
    {
        int i = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).captureSessionHandler.get(parameterKey);
        for (Map.Entry s : parameterValues.entrySet())
            if (s.getValue().equals(i))
                return s.getKey().toString();
        return "";
    }

    @Override
    public String[] GetValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }
}

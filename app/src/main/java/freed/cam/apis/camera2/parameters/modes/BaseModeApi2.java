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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class BaseModeApi2 extends AbstractParameter
{
    private final String TAG = BaseModeApi2.class.getSimpleName();
    protected HashMap<String, Integer> parameterValues;
    protected Key<Integer> parameterKey;
    protected CaptureSessionHandler captureSessionHandler;

    public BaseModeApi2(CameraWrapperInterface cameraUiWrapper,SettingKeys.Key settingMode)
    {
        super(cameraUiWrapper,settingMode);
        this.captureSessionHandler = ((Camera2Fragment) cameraUiWrapper).captureSessionHandler;
    }

    public BaseModeApi2(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key key, Key<Integer> parameterKey) {
        this(cameraUiWrapper,key);
        this.parameterKey = parameterKey;

        try {
            if (isSupported) {
                String values[] = settingMode.getValues();
                if (values == null || values.length == 0) {
                    Log.d(TAG, "Values are null set to unsupported");
                    parameterValues = null;
                    isSupported = false;
                    return;
                }
                Log.d(TAG, "array:" + Arrays.toString(values));
                parameterValues = StringUtils.StringArrayToIntHashmap(values);
                if (parameterValues == null) {
                    isSupported = false;
                    return;
                }
                stringvalues = new String[parameterValues.size()];
                parameterValues.keySet().toArray(stringvalues);
            } else isSupported = false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            isSupported = false;
            Log.WriteEx(ex);
        }
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        if (parameterValues == null || parameterValues.size() == 0)
            return;
        super.setValue(valueToSet, setToCamera);
        try {
            int toset = parameterValues.get(valueToSet);
            captureSessionHandler.SetParameterRepeating(parameterKey, toset,setToCamera);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public String GetStringValue()
    {
        if (parameterValues == null)
            return "";
        int i = captureSessionHandler.getPreviewParameter(parameterKey);
        for (Map.Entry s : parameterValues.entrySet())
            if (s.getValue().equals(i))
                return s.getKey().toString();
        return "";
    }

}

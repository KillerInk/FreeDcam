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

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class BaseModeApi2 extends AbstractParameter<Camera2>
{
    private final String TAG = BaseModeApi2.class.getSimpleName();
    protected HashMap<String, Integer> parameterValues;
    protected Key<Integer> parameterKey;
    protected CaptureSessionHandler captureSessionHandler;

    public BaseModeApi2(Camera2 cameraUiWrapper,SettingKeys.Key settingMode)
    {
        super(cameraUiWrapper,settingMode);
        this.captureSessionHandler = cameraUiWrapper.captureSessionHandler;
    }

    public BaseModeApi2(Camera2 cameraUiWrapper, SettingKeys.Key key, Key<Integer> parameterKey) {
        this(cameraUiWrapper,key);
        this.parameterKey = parameterKey;

        try {
            if (settingMode.isSupported()) {
                String[] values = settingMode.getValues();
                if (values == null || values.length == 0) {
                    Log.d(TAG, "Values are null set to unsupported");
                    parameterValues = null;
                    setViewState(ViewState.Hidden);
                    return;
                }
                Log.d(TAG, key.toString() + " array:" + Arrays.toString(values));
                parameterValues = StringUtils.StringArrayToIntHashmap(values);
                if (parameterValues == null) {
                    Log.d(TAG, "Parametervalues are null hide mode");
                    setViewState(ViewState.Hidden);
                    return;
                }
                stringvalues = new String[parameterValues.size()];
                parameterValues.keySet().toArray(stringvalues);
                setViewState(ViewState.Visible);
            }
            else {
                setViewState(ViewState.Hidden);
                Log.d(TAG, key.toString() + " not supported");
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            setViewState(ViewState.Hidden);
            Log.WriteEx(ex);
        }
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        // if parameterValues are empty, the ui dont need to get notifyed
        if (parameterValues == null || parameterValues.size() == 0)
            return;
        //notfiy the ui that the value has changed
        super.setValue(valueToSet, setToCamera);
        //if the key is null dont apply it to the capturesession, thats the case for the PictureFormatParameterApi2
        if (parameterKey == null)
            return;
        try {
            int toset = parameterValues.get(valueToSet);
            captureSessionHandler.SetParameterRepeating(parameterKey, toset,setToCamera);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }
}

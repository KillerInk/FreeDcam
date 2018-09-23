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

package freed.cam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringFloatArray;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractParameter
{
    private final String TAG = ManualFocus.class.getSimpleName();
    protected StringFloatArray focusvalues;

    public ManualFocus(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper,SettingKeys.M_Focus);
        if (SettingsManager.get(SettingKeys.M_Focus).isSupported())
        {
            setViewState(ViewState.Visible);
            String[] arr = SettingsManager.get(SettingKeys.M_Focus).getValues();
            if (arr == null || arr.length == 0) {
                setViewState(ViewState.Hidden);
                Log.d(TAG, "No mf values from Appsettings");
            }
            else
                focusvalues = new StringFloatArray(arr);
            currentInt = 0;
        }

    }

    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    public String GetStringValue()
    {
        return focusvalues.getKey(currentInt);
    }


    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        //set to auto
        if(valueToSet == 0)
        {
            //apply last used focuse mode
            cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).SetValue(SettingsManager.get(SettingKeys.FocusMode).get(), setToCamera);
            ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE,setToCamera);
        }
        else // set to manual
        {
            //if focusmode is in any other mode, turn af off
            if (!cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).GetStringValue().equals(cameraUiWrapper.getContext().getString(R.string.off)))
            {
                //apply turn off direct to the capturesession, else it get stored in settings.
                cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).fireStringValueChanged(cameraUiWrapper.getContext().getString(R.string.off));
                ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL,setToCamera);
                ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF,setToCamera);
            }
            if (currentInt > focusvalues.getSize())
                currentInt = focusvalues.getSize() -1;
            float valtoset= focusvalues.getValue(currentInt);
            Log.d(TAG, "Set MF TO: " + valtoset+ " ValueTOSET: " + valueToSet);
            ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, valtoset,setToCamera);
        }
    }

    @Override
    public String[] getStringValues() {
        return focusvalues.getKeys();
    }

    public float getFloatValue(int index)
    {
        return focusvalues.getValue(index);
    }

    public String getStringValue(int index)
    {
        return focusvalues.getKey(index);
    }
}

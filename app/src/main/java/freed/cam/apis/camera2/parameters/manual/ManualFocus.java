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
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.Log;
import freed.utils.StringFloatArray;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractParameter
{
    private final String TAG = ManualFocus.class.getSimpleName();
    private StringFloatArray focusvalues;

    public ManualFocus(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        if (cameraUiWrapper.getAppSettingsManager().manualFocus.isSupported())
        {
            isSupported = true;
            focusvalues = new StringFloatArray(cameraUiWrapper.getAppSettingsManager().manualFocus.getValues());
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
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet == 0)
        {
            cameraUiWrapper.getParameterHandler().FocusMode.SetValue(cameraUiWrapper.getContext().getString(R.string.auto), true);
        }
        else
        {
            if (!cameraUiWrapper.getParameterHandler().FocusMode.GetStringValue().equals(cameraUiWrapper.getContext().getString(R.string.off)))
            {
                ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                cameraUiWrapper.getParameterHandler().FocusMode.SetValue(cameraUiWrapper.getContext().getString(R.string.off), true);
            }
            float valtoset= focusvalues.getValue(currentInt);
            Log.d(TAG, "Set MF TO: " + valtoset+ " ValueTOSET: " + valueToSet);
            ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).captureSessionHandler.SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, valtoset);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {

    }


    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public String[] getStringValues() {
        return focusvalues.getKeys();
    }
}

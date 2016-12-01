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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.DeviceUtils;
import android.util.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractManualParameter
{
    private final String TAG = ManualFocus.class.getSimpleName();
    public ManualFocus(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        try {
            float m = ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == DeviceUtils.Devices.LG_G4)
                m = 14;
            Log.d(TAG,"MINIMUM Focus DISTANCE :" + m);
            int max = (int)(m*10);
            stringvalues = createStringArray(-1, max,1);
            currentInt = -1;
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public int GetValue() {
        return (int)(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.LENS_FOCUS_DISTANCE)* 10);
    }

    @Override
    public String GetStringValue()
    {
        if (currentInt == -1)
            return KEYS.AUTO;
        else {
            if (isSupported)
                return StringUtils.TrimmFloatString4Places(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.LENS_FOCUS_DISTANCE) + "");
        }
        return "";
    }


    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet == 0)
        {
            cameraUiWrapper.GetParameterHandler().FocusMode.SetValue("auto", true);
        }
        else
        {
            if (!cameraUiWrapper.GetParameterHandler().FocusMode.GetValue().equals("off"))
            {
                ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
                cameraUiWrapper.GetParameterHandler().FocusMode.SetValue("off", true);
            }
            float valtoset= (float) valueToSet / 10;
            Log.d(TAG, "Set MF TO: " + valtoset+ " ValueTOSET: " + valueToSet);
            ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, valtoset);
        }
    }


    @Override
    public boolean IsSupported()
    {
        int[] af = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        isSupported = false;
        for (int i : af)
        {
            if (i == CameraCharacteristics.CONTROL_AF_MODE_OFF)
                isSupported = true;
        }
        try {
            Log.d(TAG, "LensFocusDistance" + ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.LENS_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){ex.printStackTrace();}
        try {
            Log.d(TAG, "LensMinFocusDistance" + ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){ex.printStackTrace();}


        if (((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.LENS_FOCUS_DISTANCE) == null
                || ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) == 0)
            isSupported = false;
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



}
